package pox.com.piteagents.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 会话实体类
 * <p>
 * 表示一次完整的对话会话，包含多轮消息记录。
 * 支持软删除功能。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation_session")
public class ConversationSessionPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联的 Agent ID
     */
    @TableField("agent_id")
    private Long agentId;

    /**
     * 会话标题
     * <p>
     * 可由第一条用户消息自动生成或用户手动设置
     * </p>
     */
    @TableField("title")
    private String title;

    /**
     * 会话摘要
     * <p>
     * 对会话内容的简要概括
     * </p>
     */
    @TableField("summary")
    private String summary;

    /**
     * 消息数量
     * <p>
     * 通过触发器自动维护
     * </p>
     */
    @TableField("message_count")
    @Builder.Default
    private Integer messageCount = 0;

    /**
     * 总 Token 消耗
     * <p>
     * 通过触发器自动维护
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

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    /**
     * 删除时间（软删除）
     */
    @TableField("deleted_at")
    @TableLogic
    private LocalDateTime deletedAt;

    /**
     * 会话消息列表（业务字段，不存在于数据库）
     */
    @TableField(exist = false)
    @Builder.Default
    private List<ConversationMessagePO> messages = new ArrayList<>();

    /**
     * Agent 名称（业务字段，用于查询结果）
     */
    @TableField(exist = false)
    private String agentName;
}
