package pox.com.piteagents.entity.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 使用统计
 * <p>
 * 记录 AI 对话中的 Token 消耗情况，用于成本计算和用量统计。
 * 该类可在多个响应对象中复用。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenUsage {

    /**
     * 提示词 Token 数
     * <p>
     * 输入消息（包括系统提示、用户消息、历史消息等）消耗的 Token 数量
     * </p>
     */
    private Integer promptTokens;

    /**
     * 生成 Token 数
     * <p>
     * AI 生成的回复内容消耗的 Token 数量
     * </p>
     */
    private Integer completionTokens;

    /**
     * 总 Token 数
     * <p>
     * promptTokens + completionTokens 的总和
     * </p>
     */
    private Integer totalTokens;

    /**
     * 创建 TokenUsage 对象
     *
     * @param promptTokens     提示词 Token 数
     * @param completionTokens 生成 Token 数
     * @param totalTokens      总 Token 数
     * @return TokenUsage 对象
     */
    public static TokenUsage of(Integer promptTokens, Integer completionTokens, Integer totalTokens) {
        return TokenUsage.builder()
                .promptTokens(promptTokens)
                .completionTokens(completionTokens)
                .totalTokens(totalTokens)
                .build();
    }

    /**
     * 创建 TokenUsage 对象（自动计算总数）
     *
     * @param promptTokens     提示词 Token 数
     * @param completionTokens 生成 Token 数
     * @return TokenUsage 对象
     */
    public static TokenUsage of(Integer promptTokens, Integer completionTokens) {
        int total = (promptTokens != null ? promptTokens : 0) + (completionTokens != null ? completionTokens : 0);
        return TokenUsage.builder()
                .promptTokens(promptTokens)
                .completionTokens(completionTokens)
                .totalTokens(total)
                .build();
    }
}

