package pox.com.piteagents.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息记录实体类
 * <p>
 * 表示对话会话中的一条消息，可以是用户消息、系统消息或 AI 回复。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation_message")
public class ConversationMessagePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的会话 ID
     */
    @TableField("session_id")
    private Long sessionId;

    /**
     * 关联的会话（业务字段，不存在于数据库）
     */
    @TableField(exist = false)
    private ConversationSessionPO session;

    /**
     * 消息角色
     * <p>
     * 可选值：system（系统）、user（用户）、assistant（AI 助手）
     * </p>
     */
    @TableField("role")
    private String role;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 提示词 Token 数
     * <p>
     * 输入消息消耗的 Token 数量
     * </p>
     */
    @TableField("prompt_tokens")
    @Builder.Default
    private Integer promptTokens = 0;

    /**
     * 生成内容 Token 数
     * <p>
     * AI 生成的回复消耗的 Token 数量
     * </p>
     */
    @TableField("completion_tokens")
    @Builder.Default
    private Integer completionTokens = 0;

    /**
     * 总 Token 数
     * <p>
     * promptTokens + completionTokens
     * </p>
     */
    @TableField("total_tokens")
    @Builder.Default
    private Integer totalTokens = 0;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
