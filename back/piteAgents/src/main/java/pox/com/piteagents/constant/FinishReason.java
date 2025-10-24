package pox.com.piteagents.constant;

/**
 * 完成原因常量
 * <p>
 * 定义智谱AI对话生成完成的原因类型。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public final class FinishReason {

    /**
     * 正常结束
     * <p>
     * 内容生成自然结束
     * </p>
     */
    public static final String STOP = "stop";

    /**
     * 长度限制
     * <p>
     * 达到最大token数限制
     * </p>
     */
    public static final String LENGTH = "length";

    /**
     * 内容过滤
     * <p>
     * 生成内容被安全过滤器拦截
     * </p>
     */
    public static final String CONTENT_FILTER = "content_filter";

    /**
     * 私有构造函数，防止实例化
     */
    private FinishReason() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}

