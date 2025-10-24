package pox.com.piteagents.entity.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Agent 知识库关联实体类
 * <p>
 * 表示 Agent 和知识库的多对多关联关系。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("agent_knowledge")
public class AgentKnowledgePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Agent ID（联合主键）
     */
    @TableField("agent_id")
    private Long agentId;

    /**
     * 知识库ID（联合主键）
     */
    @TableField("knowledge_id")
    private Long knowledgeId;

    /**
     * 优先级
     * <p>
     * 数字越大优先级越高，在多个知识库中确定检索优先级
     * </p>
     */
    @TableField("priority")
    @Builder.Default
    private Integer priority = 0;

    /**
     * 是否启用
     */
    @TableField("is_enabled")
    @Builder.Default
    private Boolean isEnabled = true;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
