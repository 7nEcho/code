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
 * Agent 工具关联实体类
 * <p>
 * 表示 Agent 和工具的多对多关联关系。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("agent_tool")
public class AgentToolPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Agent ID（联合主键）
     */
    @TableField("agent_id")
    private Long agentId;

    /**
     * 工具ID（联合主键）
     */
    @TableField("tool_id")
    private Long toolId;

    /**
     * 排序顺序
     * <p>
     * 数字越小优先级越高
     * </p>
     */
    @TableField("sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    /**
     * 工具个性化配置
     * <p>
     * JSON 格式，可以覆盖工具的默认配置
     * </p>
     */
    @TableField("tool_config")
    private String toolConfig;

    /**
     * 是否启用此工具
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
