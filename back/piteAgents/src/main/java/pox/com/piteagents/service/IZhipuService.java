package pox.com.piteagents.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pox.com.piteagents.entity.dto.request.ChatRequest;
import pox.com.piteagents.entity.dto.response.ChatResponse;

/**
 * 智谱 AI 服务接口
 * <p>
 * 定义智谱 AI 对话调用的业务接口。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public interface IZhipuService {

    /**
     * 同步对话接口
     *
     * @param request 对话请求对象
     * @return 对话响应对象
     */
    ChatResponse chat(ChatRequest request);

    /**
     * 流式对话接口
     *
     * @param request 对话请求对象
     * @return SSE发射器，用于发送流式数据
     */
    SseEmitter streamChat(ChatRequest request);
}

