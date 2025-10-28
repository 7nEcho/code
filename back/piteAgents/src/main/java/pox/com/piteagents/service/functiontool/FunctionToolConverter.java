package pox.com.piteagents.service.functiontool;

import ai.z.openapi.service.model.ChatFunction;
import ai.z.openapi.service.model.ChatFunctionParameters;
import ai.z.openapi.service.model.ChatTool;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pox.com.piteagents.entity.dto.response.AgentToolDTO;
import pox.com.piteagents.entity.po.FunctionToolDefinitionPO;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工具转换器
 * <p>
 * 将工具定义转换为 GLM Function Calling 需要的 ChatTool 格式。
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FunctionToolConverter {

    private final ObjectMapper objectMapper;
    private final ParameterSchemaValidatorForGLM schemaValidator;

    /**
     * 将 FunctionToolDefinitionPO 转换为 ChatTool
     *
     * @param tool 工具定义
     * @return ChatTool 对象
     */
    public ChatTool convertToChatTool(FunctionToolDefinitionPO tool) {
        try {
            // 解析参数 schema
            Map<String, Object> parametersSchema = parseParametersSchema(tool.getParameters());

            // 验证 schema（仅记录警告，不阻塞）
            validateSchema(tool.getName(), parametersSchema);

            // 构建 ChatTool
            return ChatTool.builder()
                    .type("function")
                    .function(ChatFunction.builder()
                            .name(tool.getName())
                            .description(tool.getDescription())
                            .parameters(buildChatFunctionParameters(parametersSchema))
                            .build())
                    .build();

        } catch (Exception e) {
            log.error("转换工具定义失败，工具: {}, 错误: {}", tool.getName(), e.getMessage(), e);
            throw new RuntimeException("工具定义转换失败: " + tool.getName(), e);
        }
    }

    /**
     * 将 GLMUnifiedTool 转换为 ChatTool
     *
     * @param tool 统一工具接口
     * @return ChatTool 对象
     */
    public ChatTool convertToChatTool(GLMUnifiedTool tool) {
        try {
            Map<String, Object> parametersSchema = tool.getParametersSchema();

            // 验证 schema（仅记录警告，不阻塞）
            validateSchema(tool.getName(), parametersSchema);

            return ChatTool.builder()
                    .type("function")
                    .function(ChatFunction.builder()
                            .name(tool.getName())
                            .description(tool.getDescription())
                            .parameters(buildChatFunctionParameters(parametersSchema))
                            .build())
                    .build();

        } catch (Exception e) {
            log.error("转换内置工具失败，工具: {}, 错误: {}", tool.getName(), e.getMessage(), e);
            throw new RuntimeException("内置工具转换失败: " + tool.getName(), e);
        }
    }

    /**
     * 将 AgentToolDTO 列表转换为 ChatTool 列表
     *
     * @param agentTools Agent 的工具列表
     * @return ChatTool 列表
     */
    public List<ChatTool> convertAgentTools(List<AgentToolDTO> agentTools) {
        return agentTools.stream()
                .filter(tool -> tool.getEnabled() != null && tool.getEnabled())
                .map(this::convertAgentToolToChatTool)
                .collect(Collectors.toList());
    }

    /**
     * 将单个 AgentToolDTO 转换为 ChatTool
     */
    private ChatTool convertAgentToolToChatTool(AgentToolDTO agentTool) {
        try {
            // 从 parameters Map 构建 schema
            Map<String, Object> parametersSchema = agentTool.getParameters();
            if (parametersSchema == null) {
                parametersSchema = ParameterSchemaBuilderFofGLM.buildEmpty();
            }

            // 验证 schema
            validateSchema(agentTool.getName(), parametersSchema);

            return ChatTool.builder()
                    .type("function")
                    .function(ChatFunction.builder()
                            .name(agentTool.getName())
                            .description(agentTool.getDescription())
                            .parameters(buildChatFunctionParameters(parametersSchema))
                            .build())
                    .build();

        } catch (Exception e) {
            log.error("转换 Agent 工具失败，工具: {}, 错误: {}", agentTool.getName(), e.getMessage(), e);
            throw new RuntimeException("Agent 工具转换失败: " + agentTool.getName(), e);
        }
    }

    /**
     * 解析参数 schema（从 JSON 字符串）
     */
    private Map<String, Object> parseParametersSchema(String parametersJson) {
        if (!StringUtils.hasText(parametersJson)) {
            return ParameterSchemaBuilderFofGLM.buildEmpty();
        }

        try {
            return objectMapper.readValue(parametersJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            log.warn("解析参数 schema 失败，使用空 schema: {}", e.getMessage());
            return ParameterSchemaBuilderFofGLM.buildEmpty();
        }
    }

    /**
     * 构建 ChatFunctionParameters
     * <p>
     * 构建符合 GLM Function Calling 规范的参数定义。
     * 使用 JSON 序列化和反序列化的方式来绕过类型限制。
     * </p>
     */
    @SuppressWarnings("unchecked")
    private ChatFunctionParameters buildChatFunctionParameters(Map<String, Object> parametersSchema) {
        if (parametersSchema == null || parametersSchema.isEmpty()) {
            // 返回 null，让 SDK 使用默认行为
            return null;
        }
        
        try {
            // 方案：使用 ObjectMapper 进行类型转换
            // 将 Map<String, Object> 先序列化为 JSON，再反序列化为 ChatFunctionParameters
            String jsonString = objectMapper.writeValueAsString(parametersSchema);
            ChatFunctionParameters params = objectMapper.readValue(jsonString, ChatFunctionParameters.class);
            return params;
        } catch (Exception e) {
            log.warn("通过 JSON 转换构建参数失败: {}, 使用简化版本", e.getMessage());
            
            // 降级方案：只设置 type 和 required
            ChatFunctionParameters.ChatFunctionParametersBuilder builder = 
                    ChatFunctionParameters.builder()
                            .type("object");

            Object required = parametersSchema.get("required");
            if (required instanceof List) {
                builder.required((List<String>) required);
            }

            return builder.build();
        }
    }

    /**
     * 验证 schema（仅记录警告）
     */
    private void validateSchema(String toolName, Map<String, Object> schema) {
        ParameterSchemaValidatorForGLM.ValidationResult result = schemaValidator.validate(schema);
        if (!result.isValid()) {
            log.warn("工具 {} 的参数 schema 验证失败: {}", toolName, result.getErrorMessage());
        }
    }
}

