package pox.com.piteagents.entity.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pox.com.piteagents.entity.dto.common.Message;

import java.util.List;

/**
 * 对话请求对象
 * <p>
 * 用于接收客户端的对话请求，包含消息列表、模型选择、温度参数等配置。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {

    /**
     * 消息列表
     * <p>
     * 对话历史记录，按时间顺序排列。
     * 至少需要包含一条消息。
     * </p>
     */
    @NotEmpty(message = "消息列表不能为空")
    @Valid
    private List<Message> messages;

    /**
     * 模型代码
     * <p>
     * 可选值：glm-4-5, glm-4.6
     * 如果不指定，使用配置文件中的默认模型
     * </p>
     */
    private String model;

    /**
     * 温度参数
     * <p>
     * 控制输出的随机性，取值范围：0.0-1.0
     * - 较低的值（如0.2）：输出更确定、更聚焦
     * - 较高的值（如0.8）：输出更随机、更有创意
     * 默认值：0.7
     * </p>
     */
    @Min(value = 0, message = "温度参数不能小于0")
    @Max(value = 1, message = "温度参数不能大于1")
    private Double temperature;

    /**
     * 最大生成token数
     * <p>
     * 控制生成内容的最大长度
     * 如果不指定，使用配置文件中的默认值
     * </p>
     */
    @Min(value = 1, message = "最大token数不能小于1")
    private Integer maxTokens;

    /**
     * 是否使用流式输出
     * <p>
     * true: 使用SSE流式返回
     * false: 一次性返回完整结果
     * 默认值：false
     * </p>
     */
    private Boolean stream;

    /**
     * Top P 采样参数
     * <p>
     * 核采样参数，取值范围：0.0-1.0
     * 控制模型生成时考虑的token范围
     * 如：0.1表示只考虑概率质量前10%的token
     * </p>
     */
    @Min(value = 0, message = "topP参数不能小于0")
    @Max(value = 1, message = "topP参数不能大于1")
    private Double topP;
}

