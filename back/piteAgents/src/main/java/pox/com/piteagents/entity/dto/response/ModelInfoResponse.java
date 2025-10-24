package pox.com.piteagents.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模型信息对象
 * <p>
 * 用于返回智谱AI模型的详细信息，包括模型代码、名称和描述。
 * 主要用于模型列表查询接口的响应。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelInfoResponse {

    /**
     * 模型代码
     * <p>
     * API调用时使用的模型标识
     * 例如: glm-4-5, glm-4.6
     * </p>
     */
    private String code;

    /**
     * 模型名称
     * <p>
     * 用于展示的模型名称
     * 例如: GLM-4.5, GLM-4.6
     * </p>
     */
    private String name;

    /**
     * 模型描述
     * <p>
     * 模型的详细说明，包括特点和适用场景
     * </p>
     */
    private String description;
}

