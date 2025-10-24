package pox.com.piteagents.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Agent 实体类
 * <p>
 * 表示一个 AI Agent，包含基础信息、提示词配置等。
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
@TableName("agent")
public class AgentPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Agent 名称
     */
    @TableField("name")
    private String name;

    /**
     * Agent 描述
     */
    @TableField("description")
    private String description;

    /**
     * Agent 头像URL
     */
    @TableField("avatar")
    private String avatar;

    /**
     * Agent 分类
     * <p>
     * 如：编程、写作、翻译、通用等
     * </p>
     */
    @TableField("category")
    private String category;

    /**
     * 状态
     * <p>
     * 可选值：ACTIVE（激活）、INACTIVE（未激活）、ARCHIVED（已归档）
     * </p>
     */
    @TableField("status")
    @Builder.Default
    private String status = "ACTIVE";

    /**
     * 系统提示词
     * <p>
     * 用于设定 AI 的行为和角色定位
     * </p>
     */
    @TableField("system_prompt")
    private String systemPrompt;

    /**
     * 角色提示词
     * <p>
     * 用于细化 AI 的回复风格和规范
     * </p>
     */
    @TableField("role_prompt")
    private String rolePrompt;

    /**
     * 是否启用
     */
    @TableField("is_active")
    @Builder.Default
    private Boolean isActive = true;

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
     * Agent 配置（一对一关联）
     * 非数据库字段，用于业务关联
     */
    @TableField(exist = false)
    private AgentConfigPO config;
}

