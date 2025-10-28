package pox.com.piteagents.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pox.com.piteagents.entity.dto.common.TokenUsage;

import java.util.List;

/**
 * 对话响应对象
 * <p>
 * 封装智谱AI的对话响应数据，包含生成的内容、token使用情况等信息。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

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
     * 生成的回复内容
     * <p>
     * AI助手返回的文本内容
     * </p>
     */
    private String content;

    /**
     * 完成原因
     * <p>
     * 可能的值：
     * - stop: 正常结束
     * - length: 达到最大长度限制
     * - content_filter: 内容被过滤
     * </p>
     */
    private String finishReason;

    /**
     * Token使用情况
     * <p>
     * 记录本次请求的token消耗统计
     * </p>
     */
    private TokenUsage usage;

    /**
     * 工具调用记录（如果有）
     * <p>
     * 记录本次对话中调用的工具详情，用于前端展示工具调用过程
     * </p>
     */
    private List<ToolCallRecord> toolCalls;
}

