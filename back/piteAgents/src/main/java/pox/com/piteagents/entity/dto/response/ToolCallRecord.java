package pox.com.piteagents.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工具调用记录
 * <p>
 * 用于记录和返回工具调用的详细信息。
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolCallRecord {

    /**
     * 工具调用 ID
     */
    private String id;

    /**
     * 工具名称
     */
    private String name;

    /**
     * 工具参数（JSON 格式）
     */
    private String arguments;

    /**
     * 工具执行结果
     */
    private String result;

    /**
     * 是否执行成功
     */
    private Boolean success;

    /**
     * 错误信息（如果失败）
     */
    private String errorMessage;
}

