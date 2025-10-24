package pox.com.piteagents.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agent 配置数据传输对象
 * <p>
 * 用于传输 Agent 的参数配置信息。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentConfigDTO {

    /**
     * 模型代码
     */
    private String model;

    /**
     * 温度参数
     */
    private Double temperature;

    /**
     * 最大 Token 数
     */
    private Integer maxTokens;

    /**
     * Top P 参数
     */
    private Double topP;

    /**
     * 其他参数（JSON 字符串）
     */
    private String extraParams;
}

