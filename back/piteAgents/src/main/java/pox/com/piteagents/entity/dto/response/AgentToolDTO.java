package pox.com.piteagents.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Agent 工具关联信息 DTO
 * <p>
 * 展示 Agent 与工具之间的绑定关系及个性化配置。
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentToolDTO {

    /**
     * 工具 ID
     */
    private Long toolId;

    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具描述
     */
    private String description;

    /**
     * 工具调用端点
     */
    private String endpoint;

    /**
     * HTTP 方法
     */
    private String method;

    /**
     * 参数定义
     */
    private Map<String, Object> parameters;

    /**
     * 工具的请求头设置
     */
    private Map<String, String> headers;

    /**
     * 关联的排序顺序
     */
    private Integer sortOrder;

    /**
     * 是否启用该工具
     */
    private Boolean enabled;

    /**
     * Agent 对该工具的个性化配置
     */
    private Map<String, Object> toolConfig;
}
