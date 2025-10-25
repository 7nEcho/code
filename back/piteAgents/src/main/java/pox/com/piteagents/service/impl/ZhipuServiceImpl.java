package pox.com.piteagents.service.impl;

import ai.z.openapi.ZhipuAiClient;
import ai.z.openapi.service.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pox.com.piteagents.config.ZhipuConfig;
import pox.com.piteagents.common.constant.FinishReason;
import pox.com.piteagents.entity.dto.common.Message;
import pox.com.piteagents.entity.dto.request.ChatRequest;
import pox.com.piteagents.entity.dto.response.ChatResponse;
import pox.com.piteagents.entity.dto.response.StreamChatResponse;
import pox.com.piteagents.common.enums.ZhipuModelEnum;
import pox.com.piteagents.exception.ZhipuApiException;
import pox.com.piteagents.service.HelloPoxToolService;
import pox.com.piteagents.service.IZhipuService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 智谱AI服务实现类
 * <p>
 * 封装智谱AI SDK的调用逻辑，提供同步对话和流式对话功能的具体实现。
 * 支持多模型切换（GLM-4.5、GLM-4.6）和多种调用模式。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ZhipuServiceImpl implements IZhipuService {

    /**
     * 智谱AI客户端
     */
    private final ZhipuAiClient zhipuAiClient;

    /**
     * 智谱AI配置
     */
    private final ZhipuConfig zhipuConfig;

    /**
     * HelloPox 工具服务
     */
    private final HelloPoxToolService helloPoxToolService;

    @Override
    public ChatResponse chat(ChatRequest request) {
        try {
            // 记录请求日志
            log.info("开始同步对话请求，模型: {}, 消息数: {}",
                    request.getModel() != null ? request.getModel() : zhipuConfig.getDefaultModel(),
                    request.getMessages().size());

            // 构建API请求参数
            ChatCompletionCreateParams apiRequest = buildChatRequest(request, false);

            // 调用智谱AI SDK
            ChatCompletionResponse apiResponse = zhipuAiClient.chat().createChatCompletion(apiRequest);

            // 检查响应是否成功
            if (!apiResponse.isSuccess()) {
                throw new ZhipuApiException(500, "智谱AI API调用失败: " + apiResponse.getMsg());
            }

            // 转换响应
            ChatResponse response = convertResponse(apiResponse);

            // 记录响应日志
            log.info("同步对话完成，响应ID: {}, Token使用: {}",
                    response.getId(),
                    response.getUsage().getTotalTokens());

            return response;

        } catch (ZhipuApiException e) {
            // 重新抛出业务异常
            throw e;
        } catch (Exception e) {
            // 记录错误日志
            log.error("同步对话失败: {}", e.getMessage(), e);
            // 抛出业务异常
            throw new ZhipuApiException("智谱AI对话调用失败: " + e.getMessage(), e);
        }
    }

    @Override
    public SseEmitter streamChat(ChatRequest request) {
        // 创建SSE发射器，设置超时时间
        SseEmitter emitter = new SseEmitter(Long.valueOf(zhipuConfig.getTimeout() * 1000));

        // 异步处理流式响应
        new Thread(() -> {
            try {
                // 记录请求日志
                log.info("开始流式对话请求，模型: {}, 消息数: {}",
                        request.getModel() != null ? request.getModel() : zhipuConfig.getDefaultModel(),
                        request.getMessages().size());

                // 构建API请求参数（开启流式模式）
                ChatCompletionCreateParams apiRequest = buildChatRequest(request, true);

                // 调用智谱AI SDK流式接口
                ChatCompletionResponse response = zhipuAiClient.chat().createChatCompletion(apiRequest);

                // 检查响应是否成功
                if (!response.isSuccess()) {
                    throw new ZhipuApiException(500, "智谱AI流式调用失败: " + response.getMsg());
                }

                // 订阅流式数据
                response.getFlowable().subscribe(
                        // 处理流式消息数据
                        data -> {
                            try {
                                String responseId = data.getId();
                                String model = data.getModel();

                                // 获取增量内容
                                if (data.getChoices() != null && !data.getChoices().isEmpty()) {
                                    var choice = data.getChoices().get(0);
                                    var delta = choice.getDelta();

                                    String content = null;
                                    if (delta != null && delta.getContent() != null) {
                                        content = String.valueOf(delta.getContent());
                                    }

                                    if (content != null && !content.isEmpty()) {
                                        // 构建流式响应对象
                                        StreamChatResponse streamResponse = StreamChatResponse.chunk(
                                                responseId,
                                                model,
                                                content
                                        );

                                        // 发送数据块 - 使用正确的 SSE 格式
                                        emitter.send(SseEmitter.event()
                                                .data(streamResponse, MediaType.APPLICATION_JSON)
                                                .build());

                                        log.debug("发送流式数据块: {}", content);
                                    }

                                    // 如果是最后一个块，发送结束标记
                                    if (choice.getFinishReason() != null && FinishReason.STOP.equals(choice.getFinishReason())) {
                                        StreamChatResponse endResponse = StreamChatResponse.end(
                                                responseId,
                                                model,
                                                choice.getFinishReason()
                                        );
                                        emitter.send(SseEmitter.event()
                                                .data(endResponse, MediaType.APPLICATION_JSON)
                                                .build());
                                        log.info("流式对话完成，响应ID: {}", responseId);
                                    }
                                }
                            } catch (IOException e) {
                                log.error("发送SSE数据失败: {}", e.getMessage());
                                emitter.completeWithError(e);
                            }
                        },
                        // 处理流式响应错误
                        error -> {
                            log.error("流式对话发生错误: {}", error.getMessage(), error);
                            emitter.completeWithError(error);
                        },
                        // 处理流式响应完成事件
                        () -> {
                            emitter.complete();
                            log.info("流式对话流完成");
                        }
                );

            } catch (Exception e) {
                log.error("流式对话失败: {}", e.getMessage(), e);
                emitter.completeWithError(new ZhipuApiException("智谱AI流式对话调用失败: " + e.getMessage(), e));
            }
        }).start();

        return emitter;
    }

    /**
     * 构建智谱AI API请求对象
     * <p>
     * 将业务层的请求对象转换为SDK所需的请求格式。
     * 处理默认值填充、参数校验等逻辑。
     * 参考官方文档: https://docs.bigmodel.cn/cn/guide/models/text/glm-4.5#java
     * </p>
     *
     * @param request 业务请求对象
     * @param stream  是否使用流式模式
     * @return SDK请求对象
     */
    private ChatCompletionCreateParams buildChatRequest(ChatRequest request, boolean stream) {
        // 确定使用的模型
        String model = request.getModel();
        if (model == null || model.trim().isEmpty()) {
            model = zhipuConfig.getDefaultModel();
        }

        // 验证模型是否支持
        if (!ZhipuModelEnum.isValidCode(model)) {
            throw new IllegalArgumentException("不支持的模型: " + model);
        }

        // 转换消息列表
        List<ChatMessage> messages = request.getMessages().stream()
                .map(this::convertMessage)
                .collect(Collectors.toList());

        // 构建请求参数
        var builder = ChatCompletionCreateParams.builder()
                .model(model)
                .messages(messages)
                .stream(stream);

        // 设置温度参数
        if (request.getTemperature() != null) {
            builder.temperature(request.getTemperature().floatValue());
        } else {
            builder.temperature(zhipuConfig.getDefaultTemperature().floatValue());
        }

        // 设置最大Token数
        if (request.getMaxTokens() != null) {
            builder.maxTokens(request.getMaxTokens());
        } else {
            builder.maxTokens(zhipuConfig.getMaxTokens());
        }

        // 设置Top P参数
        if (request.getTopP() != null) {
            builder.topP(request.getTopP().floatValue());
        }

        return builder.build();
    }

    /**
     * 带工具调用的对话接口
     * <p>
     * 支持 AI 主动调用工具获取信息后再生成回答。
     * 实现完整的工具调用流程：AI 判断 → 调用工具 → 基于结果回答。
     * </p>
     *
     * @param request 对话请求对象
     * @return 对话响应对象
     */
    @Override
    public ChatResponse chatWithTools(ChatRequest request) {
        try {
            log.info("开始工具调用对话请求，模型: {}", 
                    request.getModel() != null ? request.getModel() : zhipuConfig.getDefaultModel());

            // 1. 定义工具（硬编码用于 POC 测试）
            ChatTool helloPoxTool = ChatTool.builder()
                    .type("function")
                    .function(ChatFunction.builder()
                            .name("hello_pox")
                            .description("返回问候语 hello pox，当用户想要获取问候信息时调用此工具")
                            .parameters(ChatFunctionParameters.builder()
                                    .type("object")
                                    .build())
                            .build())
                    .build();

            log.info("定义工具: hello_pox");

            // 2. 构建带工具的请求
            ChatCompletionCreateParams requestWithTools = buildChatRequestWithTools(
                    request, 
                    List.of(helloPoxTool)
            );

            // 3. 第一次调用 AI（判断是否需要工具）
            log.info("第一次调用 AI - 判断是否需要工具调用");
            ChatCompletionResponse response1 = zhipuAiClient.chat().createChatCompletion(requestWithTools);

            if (!response1.isSuccess()) {
                throw new ZhipuApiException(500, "智谱AI API调用失败: " + response1.getMsg());
            }

            // 4. 检查是否有工具调用
            var choice = response1.getData().getChoices().get(0);
            var message = choice.getMessage();
            var toolCalls = message.getToolCalls();

            if (toolCalls != null && !toolCalls.isEmpty()) {
                log.info("AI 请求调用工具，数量: {}", toolCalls.size());

                // 5. 执行工具调用
                var toolCall = toolCalls.get(0);
                String toolName = toolCall.getFunction().getName();
                String toolCallId = toolCall.getId();
                
                log.info("工具名称: {}, 工具调用ID: {}", toolName, toolCallId);

                // 执行实际工具
                String toolResult;
                if ("hello_pox".equals(toolName)) {
                    toolResult = helloPoxToolService.execute();
                    log.info("工具执行成功，结果: {}", toolResult);
                } else {
                    toolResult = "未知工具: " + toolName;
                    log.warn("未知工具调用: {}", toolName);
                }

                // 6. 构建包含工具结果的消息列表
                List<ChatMessage> messagesWithToolResult = new ArrayList<>();
                
                // 添加原始对话消息
                messagesWithToolResult.addAll(convertMessages(request.getMessages()));
                
                // 添加 AI 的工具调用请求消息
                messagesWithToolResult.add(message);
                
                // 添加工具返回结果消息
                ChatMessage toolResultMessage = ChatMessage.builder()
                        .role("tool")
                        .content(toolResult)
                        .toolCallId(toolCallId)
                        .build();
                messagesWithToolResult.add(toolResultMessage);

                log.info("构建工具结果消息，准备第二次调用 AI");

                // 7. 第二次调用 AI（基于工具结果生成回答）
                ChatCompletionCreateParams request2 = ChatCompletionCreateParams.builder()
                        .model(request.getModel() != null ? request.getModel() : zhipuConfig.getDefaultModel())
                        .messages(messagesWithToolResult)
                        .temperature(request.getTemperature() != null ? 
                                request.getTemperature().floatValue() : 
                                zhipuConfig.getDefaultTemperature().floatValue())
                        .maxTokens(request.getMaxTokens() != null ? 
                                request.getMaxTokens() : 
                                zhipuConfig.getMaxTokens())
                        .build();

                log.info("第二次调用 AI - 基于工具结果生成最终回答");
                ChatCompletionResponse response2 = zhipuAiClient.chat().createChatCompletion(request2);

                if (!response2.isSuccess()) {
                    throw new ZhipuApiException(500, "智谱AI API第二次调用失败: " + response2.getMsg());
                }

                ChatResponse finalResponse = convertResponse(response2);
                log.info("工具调用对话完成，最终响应ID: {}", finalResponse.getId());
                
                return finalResponse;
            } else {
                // 无工具调用，直接返回
                log.info("AI 未请求调用工具，直接返回回答");
                return convertResponse(response1);
            }

        } catch (ZhipuApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("工具调用对话失败: {}", e.getMessage(), e);
            throw new ZhipuApiException("工具调用对话失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建带工具的 API 请求对象
     * <p>
     * 在标准请求基础上添加工具定义列表。
     * </p>
     *
     * @param request 业务请求对象
     * @param tools   工具列表
     * @return SDK 请求对象
     */
    private ChatCompletionCreateParams buildChatRequestWithTools(ChatRequest request, List<ChatTool> tools) {
        String model = request.getModel();
        if (model == null || model.trim().isEmpty()) {
            model = zhipuConfig.getDefaultModel();
        }

        // 验证模型是否支持
        if (!ZhipuModelEnum.isValidCode(model)) {
            throw new IllegalArgumentException("不支持的模型: " + model);
        }

        // 转换消息列表
        List<ChatMessage> messages = convertMessages(request.getMessages());

        // 构建请求参数（添加工具定义）
        var builder = ChatCompletionCreateParams.builder()
                .model(model)
                .messages(messages)
                .tools(tools)  // 关键：添加工具定义
                .stream(false); // 工具调用不使用流式

        // 设置温度参数
        if (request.getTemperature() != null) {
            builder.temperature(request.getTemperature().floatValue());
        } else {
            builder.temperature(zhipuConfig.getDefaultTemperature().floatValue());
        }

        // 设置最大Token数
        if (request.getMaxTokens() != null) {
            builder.maxTokens(request.getMaxTokens());
        } else {
            builder.maxTokens(zhipuConfig.getMaxTokens());
        }

        // 设置Top P参数
        if (request.getTopP() != null) {
            builder.topP(request.getTopP().floatValue());
        }

        return builder.build();
    }

    /**
     * 转换消息列表
     * <p>
     * 将业务层的消息列表转换为 SDK 的消息格式。
     * </p>
     *
     * @param messages 业务消息列表
     * @return SDK 消息列表
     */
    private List<ChatMessage> convertMessages(List<Message> messages) {
        return messages.stream()
                .map(this::convertMessage)
                .collect(Collectors.toList());
    }

    /**
     * 转换消息对象
     * <p>
     * 将业务层的Message对象转换为SDK的ChatMessage对象。
     * 根据官方SDK示例使用ChatMessageRole枚举。
     * </p>
     *
     * @param message 业务消息对象
     * @return SDK消息对象
     */
    private ChatMessage convertMessage(Message message) {
        return ChatMessage.builder()
                .role(message.getRole())
                .content(message.getContent())
                .build();
    }

    /**
     * 转换API响应
     * <p>
     * 将SDK返回的响应对象转换为业务层的响应格式。
     * 提取核心信息：内容、token使用量等。
     * 注意：SDK返回的响应需要通过getData()获取实际数据。
     * </p>
     *
     * @param apiResponse SDK响应对象
     * @return 业务响应对象
     */
    private ChatResponse convertResponse(ChatCompletionResponse apiResponse) {
        // 获取响应数据
        var data = apiResponse.getData();

        // 提取第一个选择项（通常只有一个）
        var choice = data.getChoices().get(0);

        // 构建Token使用统计
        ChatResponse.TokenUsage usage = ChatResponse.TokenUsage.builder()
                .promptTokens(data.getUsage().getPromptTokens())
                .completionTokens(data.getUsage().getCompletionTokens())
                .totalTokens(data.getUsage().getTotalTokens())
                .build();

        // 构建响应对象
        return ChatResponse.builder()
                .id(data.getId())
                .model(data.getModel())
                .content(String.valueOf(choice.getMessage().getContent()))
                .finishReason(choice.getFinishReason())
                .usage(usage)
                .build();
    }
}

