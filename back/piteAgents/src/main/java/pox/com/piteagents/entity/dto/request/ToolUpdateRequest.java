package pox.com.piteagents.entity.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.Map;

/**
 * 更新工具请求对象
 */
@Data
public class ToolUpdateRequest {

    private String name;

    private String description;

    private String endpoint;

    private String method;

    private Map<String, Object> parameters;

    private Map<String, String> headers;

    @Min(value = 1000, message = "超时时间不能低于 1000 ms")
    @Max(value = 300000, message = "超时时间不能超过 300000 ms")
    private Integer timeout;

    @Min(value = 0, message = "重试次数不能小于 0")
    @Max(value = 10, message = "重试次数不能超过 10")
    private Integer retryCount;

    private Boolean isActive;
}
