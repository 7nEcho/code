package pox.com.piteagents.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Agent 配置实体类
 * <p>
 * 存储 Agent 的运行参数配置，如模型选择、温度参数等。
 * 与 Agent 是一对一关系。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("agent_config")
public class AgentConfigPO implements Serializable {

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
     * 关联的 Agent（业务字段，不存在于数据库）
     */
    @TableField(exist = false)
    private AgentPO agent;

    /**
     * 模型代码
     * <p>
     * 如：glm-4.5, glm-4.6
     * </p>
     */
    @TableField("model")
    @Builder.Default
    private String model = "glm-4.6";

    /**
     * 温度参数
     * <p>
     * 控制输出的随机性，取值范围：0.00-1.00
     * </p>
     */
    @TableField("temperature")
    @Builder.Default
    private Double temperature = 0.70;

    /**
     * 最大 Token 数
     * <p>
     * 控制生成内容的最大长度
     * </p>
     */
    @TableField("max_tokens")
    @Builder.Default
    private Integer maxTokens = 2000;

    /**
     * Top P 参数
     * <p>
     * 核采样参数，取值范围：0.00-1.00
     * </p>
     */
    @TableField("top_p")
    @Builder.Default
    private Double topP = 0.95;

    /**
     * 其他参数
     * <p>
     * JSON 格式存储额外的配置参数
     * </p>
     */
    @TableField("extra_params")
    private String extraParams;

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
}
