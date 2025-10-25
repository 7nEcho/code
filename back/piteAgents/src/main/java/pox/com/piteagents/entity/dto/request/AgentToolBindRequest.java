package pox.com.piteagents.entity.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Agent 工具批量绑定请求
 */
@Data
public class AgentToolBindRequest {

    /**
     * 需要绑定的工具 ID 列表
     */
    @NotEmpty(message = "工具 ID 列表不能为空")
    private List<Long> toolIds;

    /**
     * 每个工具的个性化配置
     * key 使用字符串以兼容 JSON（会在服务层转换为 Long）
     */
    private Map<String, ToolBindingConfig> configs;

    /**
     * 工具个性化配置
     */
    @Data
    public static class ToolBindingConfig {

        /**
         * 排序顺序
         */
        private Integer sortOrder;

        /**
         * 是否启用
         */
        private Boolean enabled;

        /**
         * 个性化配置（JSON对象）
         */
        private Map<String, Object> toolConfig;
    }
}
