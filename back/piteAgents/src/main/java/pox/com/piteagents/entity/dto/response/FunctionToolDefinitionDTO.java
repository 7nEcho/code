package pox.com.piteagents.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 工具定义响应 DTO
 * <p>
 * 返回工具的元数据信息及配置，用于工具管理接口。
 * </p>
 *
 * @author
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolDefinitionDTO {

    /**
     * 工具 ID
     */
    private Long id;

    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具类型
     * <p>
     * HTTP: 外部API调用, BUILTIN: 内置工具
     * </p>
     */
    private String toolType;

    /**
     * 工具描述
     */
    private String description;

    /**
     * 工具调用的 HTTP 端点
     */
    private String endpoint;

    /**
     * HTTP 方法
     */
    private String method;

    /**
     * 参数定义（JSON Schema）
     */
    private Map<String, Object> parameters;

    /**
     * 请求头配置
     */
    private Map<String, String> headers;

    /**
     * 超时时间（毫秒）
     */
    private Integer timeout;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 是否启用
     */
    private Boolean isActive;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
