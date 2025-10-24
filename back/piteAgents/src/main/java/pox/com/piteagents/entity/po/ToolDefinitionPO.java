package pox.com.piteagents.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 工具定义实体类
 * <p>
 * 定义可供 Agent 调用的外部工具或 API。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("tool_definition")
public class ToolDefinitionPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 工具名称
     */
    @TableField("name")
    private String name;

    /**
     * 工具描述
     */
    @TableField("description")
    private String description;

    /**
     * API 端点 URL
     */
    @TableField("endpoint")
    private String endpoint;

    /**
     * HTTP 方法
     * <p>
     * 可选值：GET、POST、PUT、DELETE、PATCH
     * </p>
     */
    @TableField("method")
    @Builder.Default
    private String method = "POST";

    /**
     * 参数定义
     * <p>
     * JSON Schema 格式，定义工具的输入参数
     * </p>
     */
    @TableField("parameters")
    private String parameters;

    /**
     * 请求头配置
     * <p>
     * JSON 格式，定义 HTTP 请求头
     * </p>
     */
    @TableField("headers")
    private String headers;

    /**
     * 超时时间（毫秒）
     */
    @TableField("timeout")
    @Builder.Default
    private Integer timeout = 30000;

    /**
     * 重试次数
     */
    @TableField("retry_count")
    @Builder.Default
    private Integer retryCount = 3;

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
}
