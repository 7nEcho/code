package pox.com.piteagents.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话数据传输对象
 * <p>
 * 用于返回会话的详细信息。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationSessionDTO {

    /**
     * 会话ID
     */
    private Long id;

    /**
     * Agent ID
     */
    private Long agentId;

    /**
     * Agent 名称
     */
    private String agentName;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 会话摘要
     */
    private String summary;

    /**
     * 消息数量
     */
    private Integer messageCount;

    /**
     * 总 Token 消耗
     */
    private Integer totalTokens;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

