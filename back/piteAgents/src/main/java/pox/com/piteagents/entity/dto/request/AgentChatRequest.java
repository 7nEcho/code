package pox.com.piteagents.entity.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pox.com.piteagents.entity.dto.common.Message;

import java.util.List;

/**
 * 使用 Agent 进行对话的请求对象
 * <p>
 * 扩展原有的 ChatRequest，增加 Agent 相关参数。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentChatRequest {

    /**
     * Agent ID（必填）
     */
    @NotNull(message = "Agent ID 不能为空")
    private Long agentId;

    /**
     * 会话 ID（可选，不传则创建新会话）
     */
    private Long sessionId;

    /**
     * 消息列表
     */
    @NotEmpty(message = "消息列表不能为空")
    @Valid
    private List<Message> messages;

    /**
     * 是否使用流式输出
     */
    private Boolean stream;

    /**
     * 是否加载历史消息作为上下文
     */
    @Builder.Default
    private Boolean loadHistory = true;

    /**
     * 加载历史消息的数量（默认10条）
     */
    @Builder.Default
    private Integer historyLimit = 10;
}

