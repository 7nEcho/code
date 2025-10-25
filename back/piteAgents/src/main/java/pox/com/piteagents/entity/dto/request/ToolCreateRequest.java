package pox.com.piteagents.entity.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Map;

/**
 * 创建工具请求对象
 */
@Data
public class ToolCreateRequest {

    /**
     * 工具名称
     */
    @NotBlank(message = "工具名称不能为空")
    private String name;

    /**
     * 工具描述
     */
    @NotBlank(message = "工具描述不能为空")
    private String description;

    /**
     * 工具调用端点
     */
    @NotBlank(message = "工具 endpoint 不能为空")
    private String endpoint;

    /**
     * HTTP 方法
     */
    @NotBlank(message = "HTTP 方法不能为空")
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
    @Min(value = 1000, message = "超时时间不能低于 1000 ms")
    @Max(value = 300000, message = "超时时间不能超过 300000 ms")
    private Integer timeout;

    /**
     * 重试次数
     */
    @Min(value = 0, message = "重试次数不能小于 0")
    @Max(value = 10, message = "重试次数不能超过 10")
    private Integer retryCount;

    /**
     * 是否启用
     */
    private Boolean isActive;
}
