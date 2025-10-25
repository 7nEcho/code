package pox.com.piteagents.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pox.com.piteagents.entity.dto.common.ApiResponse;
import pox.com.piteagents.entity.dto.request.ChatRequest;
import pox.com.piteagents.entity.dto.response.ChatResponse;
import pox.com.piteagents.entity.dto.response.ModelInfoResponse;
import pox.com.piteagents.common.enums.ZhipuModelEnum;
import pox.com.piteagents.service.IZhipuService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 对话控制器
 * <p>
 * 提供智谱AI对话相关的REST API接口。
 * 包括同步对话、流式对话和模型查询功能。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    /**
     * 智谱AI服务
     */
    private final IZhipuService zhipuService;

    /**
     * 同步对话接口
     * <p>
     * 发送消息并等待完整响应。
     * 适用于对实时性要求不高的场景。
     * </p>
     *
     * @param request 对话请求，包含消息列表、模型选择等参数
     * @return 统一响应格式，包含AI的完整回复
     */
    @PostMapping("/normal")
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("收到同步对话请求");

        // 调用服务层处理
        ChatResponse response = zhipuService.chat(request);

        // 返回成功响应
        return ApiResponse.success(response);
    }

    /**
     * 流式对话接口
     * <p>
     * 使用SSE（Server-Sent Events）方式实时返回生成内容。
     * 客户端可以实时接收并展示生成过程，提升用户体验。
     * </p>
     * <p>
     * 响应格式：
     * - Content-Type: text/event-stream
     * - 数据格式：每条消息为一个JSON对象
     * - 最后一条消息的done字段为true，表示生成结束
     * </p>
     *
     * @param request 对话请求，包含消息列表、模型选择等参数
     * @return SSE发射器，用于推送流式数据
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamChat(@Valid @RequestBody ChatRequest request) {
        log.info("收到流式对话请求");

        // 强制设置为流式模式
        request.setStream(true);

        // 调用服务层处理并返回SSE发射器
        return zhipuService.streamChat(request);
    }

    /**
     * 获取支持的模型列表
     * <p>
     * 返回当前系统支持的所有智谱AI模型信息。
     * 包括模型代码、名称和描述。
     * </p>
     *
     * @return 模型列表
     */
    @GetMapping("/models")
    public ApiResponse<List<ModelInfoResponse>> getModels() {
        log.info("查询支持的模型列表");

        // 获取所有模型枚举并转换为响应对象
        List<ModelInfoResponse> models = Arrays.stream(ZhipuModelEnum.values())
                .map(model -> ModelInfoResponse.builder()
                        .code(model.getCode())
                        .name(model.getName())
                        .description(model.getDescription())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.success(models);
    }

    /**
     * 带工具调用的智能对话接口
     * <p>
     * 支持Agent动态工具调用的智能对话接口。
     * 根据请求中的agentId自动加载对应Agent的工具列表，
     * AI会智能判断是否需要调用工具来获取信息或执行操作。
     * </p>
     * <p>
     * 兼容性说明：
     * - 如果不指定agentId，则进行普通对话（不启用工具）
     * - 如果指定agentId但该Agent未绑定工具，也会降级为普通对话
     * </p>
     *
     * @param request 对话请求（可包含agentId）
     * @return 对话响应（可能包含工具调用结果）
     */
    @PostMapping("/with-tools")
    public ApiResponse<ChatResponse> chatWithTools(@Valid @RequestBody ChatRequest request) {
        log.info("收到工具调用测试请求");
        ChatResponse response = zhipuService.chatWithTools(request);
        return ApiResponse.success(response);
    }
}

