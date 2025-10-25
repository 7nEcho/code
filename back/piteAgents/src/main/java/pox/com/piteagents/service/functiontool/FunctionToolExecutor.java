package pox.com.piteagents.service.functiontool;

import ai.z.openapi.service.model.ChatMessage;
import ai.z.openapi.service.model.ChatTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import pox.com.piteagents.entity.dto.response.AgentToolDTO;
import pox.com.piteagents.entity.po.FunctionToolDefinitionPO;
import pox.com.piteagents.exception.FunctionToolExecutionException;
import pox.com.piteagents.service.IFunctionToolService;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 简化工具执行器
 * <p>
 * 基于新的统一工具接口的简化执行器，支持：
 * - 统一的工具调用接口
 * - HTTP工具调用
 * - 内置工具调用
 * - 基本的错误处理和日志
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FunctionToolExecutor {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final FunctionToolRegistry functionToolRegistry;
    private final FunctionToolConverter functionToolConverter;
    private final IFunctionToolService toolService;

    /**
     * 执行工具调用（新的统一接口）
     *
     * @param toolName 工具名称
     * @param argumentsJson 参数JSON
     * @return 工具执行结果
     */
    public String execute(String toolName, String argumentsJson) {
        LocalDateTime startTime = LocalDateTime.now();
        log.info("🔧 开始执行工具: {}, 参数: {}", toolName, truncateForLog(argumentsJson));

        try {
            // 1. 获取工具实例
            GLMUnifiedTool tool = functionToolRegistry.getTool(toolName);
            if (tool == null) {
                throw new FunctionToolExecutionException(toolName, "工具不存在: " + toolName);
            }

            // 2. 解析参数
            Map<String, Object> arguments = parseArguments(argumentsJson);

            // 3. 执行工具
            String result = executeTool(tool, arguments);

            // 4. 记录成功日志
            long durationMs = java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();
            log.info("✅ 工具执行成功: {}, 耗时: {}ms, 结果: {}", 
                    toolName, durationMs, truncateForLog(result));

            return result;

        } catch (FunctionToolExecutionException e) {
            long durationMs = java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();
            log.error("❌ 工具执行失败: {}, 耗时: {}ms, 错误: {}", 
                    toolName, durationMs, e.getMessage());
            throw e;
        } catch (Exception e) {
            long durationMs = java.time.Duration.between(startTime, LocalDateTime.now()).toMillis();
            log.error("❌ 工具执行异常: {}, 耗时: {}ms, 错误: {}", 
                    toolName, durationMs, e.getMessage(), e);
            throw new FunctionToolExecutionException(toolName, "工具执行异常: " + e.getMessage(), e);
        }
    }

    /**
     * 执行工具调用（兼容旧接口）
     *
     * @param tool 工具定义
     * @param argumentsJson 参数JSON
     * @return 工具执行结果
     */
    public String execute(FunctionToolDefinitionPO tool, String argumentsJson) {
        String toolName = tool.getName();
        
        // 检查是否为内置工具
        if ("BUILTIN".equals(tool.getToolType())) {
            return execute(toolName, argumentsJson);
        }
        
        // HTTP工具调用
        return executeHttpTool(tool, argumentsJson);
    }

    /**
     * 执行统一工具
     */
    private String executeTool(GLMUnifiedTool tool, Map<String, Object> arguments) {
        if (tool.getToolType() == GLMUnifiedTool.ToolType.BUILTIN) {
            // 内置工具直接调用
            return tool.execute(arguments);
        } else {
            // HTTP工具需要特殊处理
            return executeHttpToolDirect(tool, arguments);
        }
    }

    /**
     * 执行HTTP工具（直接调用）
     */
    private String executeHttpToolDirect(GLMUnifiedTool tool, Map<String, Object> arguments) {
        // TODO: 从工具或配置中获取HTTP相关信息
        // 暂时抛出异常，后续实现
        throw new FunctionToolExecutionException(tool.getName(), "HTTP工具调用暂未实现");
    }

    /**
     * 执行HTTP工具（通过ToolDefinitionPO）
     */
    private String executeHttpTool(FunctionToolDefinitionPO tool, String argumentsJson) {
        String toolName = tool.getName();
        
        try {
            Map<String, Object> arguments = parseArguments(argumentsJson);
            
            // 构建HTTP请求
            HttpHeaders headers = buildHeaders(tool);
            String method = tool.getMethod().toUpperCase();
            String endpoint = tool.getEndpoint();
            
            HttpEntity<String> entity;
            String finalUrl;
            
            switch (method) {
                case "GET":
                    finalUrl = buildUrlWithParams(endpoint, arguments);
                    entity = new HttpEntity<>(headers);
                    break;
                case "POST":
                case "PUT":
                case "PATCH":
                    finalUrl = endpoint;
                    String requestBody = objectMapper.writeValueAsString(arguments);
                    entity = new HttpEntity<>(requestBody, headers);
                    break;
                case "DELETE":
                    finalUrl = buildUrlWithParams(endpoint, arguments);
                    entity = new HttpEntity<>(headers);
                    break;
                default:
                    throw new FunctionToolExecutionException(toolName, "不支持的HTTP方法: " + method);
            }
            
            log.debug("🌐 HTTP请求: {} {}, 参数: {}", method, finalUrl, arguments);
            
            // 执行HTTP调用
            ResponseEntity<String> response = restTemplate.exchange(
                    URI.create(finalUrl),
                    HttpMethod.valueOf(method),
                    entity,
                    String.class
            );
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new FunctionToolExecutionException(toolName,
                    String.format("HTTP调用失败，状态码: %d, 响应: %s", 
                        response.getStatusCode().value(), response.getBody()));
            }
            
            return response.getBody() != null ? response.getBody() : "";
            
        } catch (JsonProcessingException e) {
            throw new FunctionToolExecutionException(toolName, "参数序列化失败: " + e.getMessage(), e);
        } catch (RestClientException e) {
            throw new FunctionToolExecutionException(toolName, "HTTP调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析参数JSON
     */
    private Map<String, Object> parseArguments(String argumentsJson) {
        try {
            if (argumentsJson == null || argumentsJson.trim().isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(argumentsJson, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("参数JSON格式错误: " + e.getMessage(), e);
        }
    }

    /**
     * 构建HTTP请求头
     */
    private HttpHeaders buildHeaders(FunctionToolDefinitionPO tool) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        if (StringUtils.hasText(tool.getHeaders())) {
            try {
                Map<String, String> customHeaders = objectMapper.readValue(
                        tool.getHeaders(), 
                        new TypeReference<Map<String, String>>() {}
                );
                customHeaders.forEach(headers::add);
            } catch (JsonProcessingException e) {
                log.warn("解析工具请求头失败，工具: {}, 错误: {}", tool.getName(), e.getMessage());
            }
        }
        
        return headers;
    }

    /**
     * 为GET/DELETE请求构建带参数的URL
     */
    private String buildUrlWithParams(String baseUrl, Map<String, Object> arguments) {
        if (arguments.isEmpty()) {
            return baseUrl;
        }

        StringBuilder url = new StringBuilder(baseUrl);
        url.append(baseUrl.contains("?") ? "&" : "?");
        
        arguments.forEach((key, value) -> {
            if (value != null) {
                url.append(key).append("=").append(value).append("&");
            }
        });

        // 移除最后一个多余的&
        if (url.charAt(url.length() - 1) == '&') {
            url.deleteCharAt(url.length() - 1);
        }

        return url.toString();
    }

    /**
     * 截断日志内容，避免日志过长
     */
    private String truncateForLog(String content) {
        if (content == null) {
            return "null";
        }
        if (content.length() <= 200) {
            return content;
        }
        return content.substring(0, 200) + "...[截断]";
    }

    // ==================== 工具加载功能 ====================

    /**
     * 加载 Agent 绑定的所有工具（转换为 GLM ChatTool 格式）
     * <p>
     * 根据 Agent ID 加载绑定的工具列表，包括内置工具和 HTTP 工具。
     * </p>
     *
     * @param agentId Agent ID
     * @return ChatTool 列表
     */
    public List<ChatTool> loadAgentTools(Long agentId) {
        if (agentId == null) {
            log.debug("未指定 Agent ID，加载所有内置工具");
            return loadBuiltinTools();
        }

        try {
            log.info("开始加载 Agent {} 的工具列表", agentId);
            
            // 1. 从数据库查询 Agent 绑定的工具
            List<AgentToolDTO> agentTools = toolService.listAgentTools(agentId);
            
            if (agentTools.isEmpty()) {
                log.info("Agent {} 未绑定任何工具", agentId);
                return List.of();
            }

            // 2. 分离内置工具和 HTTP 工具
            List<ChatTool> chatTools = new ArrayList<>();
            
            for (AgentToolDTO agentTool : agentTools) {
                if (!agentTool.getEnabled()) {
                    log.debug("跳过未启用的工具: {}", agentTool.getName());
                    continue;
                }

                try {
                    if ("BUILTIN".equals(agentTool.getToolType())) {
                        // 内置工具：从 FunctionToolRegistry 获取
                        GLMUnifiedTool builtinTool = functionToolRegistry.getTool(agentTool.getName());
                        if (builtinTool != null && builtinTool.isAvailable()) {
                            chatTools.add(functionToolConverter.convertToChatTool(builtinTool));
                            log.debug("加载内置工具: {}", agentTool.getName());
                        } else {
                            log.warn("内置工具未找到或不可用: {}", agentTool.getName());
                        }
                    } else {
                        // HTTP 工具：直接从数据库定义转换
                        chatTools.add(functionToolConverter.convertAgentTools(List.of(agentTool)).get(0));
                        log.debug("加载 HTTP 工具: {}", agentTool.getName());
                    }
                } catch (Exception e) {
                    log.error("加载工具失败: {}, 错误: {}", agentTool.getName(), e.getMessage());
                }
            }

            log.info("Agent {} 成功加载 {} 个工具", agentId, chatTools.size());
            return chatTools;

        } catch (Exception e) {
            log.error("加载 Agent 工具列表失败，Agent ID: {}, 错误: {}", agentId, e.getMessage(), e);
            return List.of(); // 失败时返回空列表，降级为普通对话
        }
    }

    /**
     * 加载所有内置工具
     *
     * @return ChatTool 列表
     */
    public List<ChatTool> loadBuiltinTools() {
        Map<String, GLMUnifiedTool> builtinTools = functionToolRegistry.getToolsByType(GLMUnifiedTool.ToolType.BUILTIN);

        List<ChatTool> chatTools = builtinTools.values().stream()
                .filter(GLMUnifiedTool::isAvailable)
                .map(functionToolConverter::convertToChatTool)
                .collect(Collectors.toList());

        log.info("加载到 {} 个内置工具", chatTools.size());
        return chatTools;
    }

    // ==================== 批量工具调用功能 ====================

    /**
     * 批量执行工具调用
     * <p>
     * 遍历 AI 返回的 tool_calls 列表，逐个执行工具调用，
     * 并将结果封装为 ChatMessage 格式返回给 AI。
     * </p>
     *
     * @param toolCalls AI 返回的工具调用列表（SDK 的 ToolCall 类型）
     * @param agentId   Agent ID（可为 null）
     * @return 工具调用结果消息列表
     */
    public List<ChatMessage> executeToolCalls(List<?> toolCalls, Long agentId) {
        List<ChatMessage> toolResults = new ArrayList<>();

        log.info("开始批量执行工具调用，数量: {}", toolCalls.size());

        for (Object toolCallObj : toolCalls) {
            // 使用反射获取工具调用信息（兼容不同版本的 SDK）
            try {
                String toolName = getToolCallProperty(toolCallObj, "name");
                String arguments = getToolCallProperty(toolCallObj, "arguments");
                String toolCallId = getToolCallProperty(toolCallObj, "id");

                log.info("执行工具调用: {}, ID: {}, 参数: {}", toolName, toolCallId, truncateForLog(arguments));

                // 执行工具
                String toolResult = executeToolByName(toolName, arguments, agentId);

                // 构建工具结果消息
                ChatMessage resultMessage = buildToolResultMessage(toolCallId, toolResult);
                toolResults.add(resultMessage);

                log.info("工具 {} 执行成功，ID: {}", toolName, toolCallId);

            } catch (Exception e) {
                log.error("工具执行失败，错误: {}", e.getMessage(), e);

                // 即使工具执行失败，也要返回错误信息给 AI
                String errorToolCallId = "unknown";
                try {
                    errorToolCallId = getToolCallProperty(toolCallObj, "id");
                } catch (Exception ignored) {
                }
                
                ChatMessage errorMessage = buildToolResultMessage(
                        errorToolCallId,
                        "工具执行失败: " + e.getMessage()
                );
                toolResults.add(errorMessage);
            }
        }

        log.info("批量工具调用完成，成功: {}", toolResults.size());
        return toolResults;
    }

    /**
     * 根据工具名称执行工具
     *
     * @param toolName  工具名称
     * @param arguments 参数 JSON
     * @param agentId   Agent ID（可为 null）
     * @return 工具执行结果
     */
    private String executeToolByName(String toolName, String arguments, Long agentId) {
        // 1. 优先尝试内置工具
        if (functionToolRegistry.hasTool(toolName)) {
            log.debug("执行内置工具: {}", toolName);
            return execute(toolName, arguments);
        }

        // 2. 尝试从数据库查询 HTTP 工具
        if (agentId != null) {
            List<AgentToolDTO> agentTools = toolService.listAgentTools(agentId);
            AgentToolDTO targetTool = agentTools.stream()
                    .filter(tool -> tool.getName().equals(toolName) && tool.getEnabled())
                    .findFirst()
                    .orElse(null);

            if (targetTool != null && "HTTP".equals(targetTool.getToolType())) {
                log.debug("执行 HTTP 工具: {}", toolName);
                // 构建 FunctionToolDefinitionPO 用于 HTTP 调用
                FunctionToolDefinitionPO toolDef = buildToolDefinitionFromDTO(targetTool);
                return execute(toolDef, arguments);
            }
        }

        // 3. 未找到工具
        throw new FunctionToolExecutionException(toolName, "未知工具: " + toolName);
    }

    /**
     * 构建工具结果消息
     *
     * @param toolCallId 工具调用 ID
     * @param result     工具执行结果
     * @return ChatMessage
     */
    private ChatMessage buildToolResultMessage(String toolCallId, String result) {
        return ChatMessage.builder()
                .role("tool")
                .content(result)
                .toolCallId(toolCallId)
                .build();
    }

    /**
     * 从 AgentToolDTO 构建 FunctionToolDefinitionPO
     */
    private FunctionToolDefinitionPO buildToolDefinitionFromDTO(AgentToolDTO dto) {
        return FunctionToolDefinitionPO.builder()
                .id(dto.getToolId())
                .name(dto.getName())
                .description(dto.getDescription())
                .toolType("HTTP")
                .endpoint(dto.getEndpoint())
                .method(dto.getMethod())
                .parameters(convertMapToJson(dto.getParameters()))
                .headers(convertMapToJson(dto.getHeaders()))
                .timeout(30000)
                .retryCount(3)
                .isActive(true)
                .build();
    }

    /**
     * 将 Map 转换为 JSON 字符串
     */
    private String convertMapToJson(Map<String, ?> map) {
        if (map == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception e) {
            log.warn("Map 转 JSON 失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从工具调用对象中获取属性（兼容不同 SDK 版本）
     * <p>
     * 使用反射方式获取属性，避免直接依赖 SDK 的具体类型。
     * </p>
     */
    private String getToolCallProperty(Object toolCall, String propertyName) throws Exception {
        if (toolCall == null) {
            return null;
        }

        // 尝试通过 getFunction() 获取 function 对象
        Object function = toolCall.getClass().getMethod("getFunction").invoke(toolCall);
        
        if ("name".equals(propertyName) || "arguments".equals(propertyName)) {
            // name 和 arguments 在 function 对象中
            if (function != null) {
                Object value = function.getClass().getMethod("get" + capitalize(propertyName)).invoke(function);
                return value != null ? value.toString() : null;
            }
        } else if ("id".equals(propertyName)) {
            // id 在 toolCall 对象本身
            Object value = toolCall.getClass().getMethod("getId").invoke(toolCall);
            return value != null ? value.toString() : null;
        }
        
        return null;
    }

    /**
     * 首字母大写
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
