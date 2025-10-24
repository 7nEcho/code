package pox.com.piteagents.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 对话响应对象
 * <p>
 * 扩展原有的 ChatResponse，增加会话 ID 等信息。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentChatResponse {

    /**
     * 响应ID
     */
    private String id;

    /**
     * 会话ID
     */
    private Long sessionId;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * 生成的回复内容
     */
    private String content;

    /**
     * 完成原因
     */
    private String finishReason;

    /**
     * Token使用情况
     */
    private ChatResponse.TokenUsage usage;
}

