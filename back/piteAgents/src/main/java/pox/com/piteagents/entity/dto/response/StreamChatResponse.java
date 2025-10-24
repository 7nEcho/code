package pox.com.piteagents.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流式对话响应对象
 * <p>
 * 用于封装SSE（Server-Sent Events）流式响应的数据结构。
 * 在流式模式下，每次返回一个增量数据片段。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamChatResponse {

    /**
     * 响应ID
     * <p>
     * 智谱AI返回的唯一请求标识
     * </p>
     */
    private String id;

    /**
     * 使用的模型
     * <p>
     * 实际执行对话的模型代码
     * </p>
     */
    private String model;

    /**
     * 增量内容
     * <p>
     * 本次流式返回的文本片段
     * 客户端需要累积所有delta组成完整的回复内容
     * </p>
     */
    private String delta;

    /**
     * 完成原因
     * <p>
     * 只在最后一个数据块中包含，表示生成结束的原因
     * 可能的值：
     * - stop: 正常结束
     * - length: 达到最大长度限制
     * - content_filter: 内容被过滤
     * 非最后一个块时为null
     * </p>
     */
    private String finishReason;

    /**
     * 是否为最后一个数据块
     * <p>
     * true: 表示流式输出结束
     * false: 后续还有更多数据
     * </p>
     */
    private Boolean done;

    /**
     * 创建数据块
     *
     * @param id    响应ID
     * @param model 模型代码
     * @param delta 增量内容
     * @return 流式响应对象
     */
    public static StreamChatResponse chunk(String id, String model, String delta) {
        return StreamChatResponse.builder()
                .id(id)
                .model(model)
                .delta(delta)
                .done(false)
                .build();
    }

    /**
     * 创建结束块
     *
     * @param id           响应ID
     * @param model        模型代码
     * @param finishReason 完成原因
     * @return 流式响应对象
     */
    public static StreamChatResponse end(String id, String model, String finishReason) {
        return StreamChatResponse.builder()
                .id(id)
                .model(model)
                .finishReason(finishReason)
                .done(true)
                .build();
    }
}

