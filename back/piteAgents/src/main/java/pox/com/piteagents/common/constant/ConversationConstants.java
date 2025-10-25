package pox.com.piteagents.common.constant;

/**
 * 对话相关常量
 * <p>
 * 定义对话模块使用的常量值。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public final class ConversationConstants {

    /**
     * 默认会话标题
     */
    public static final String DEFAULT_SESSION_TITLE = "新对话";

    /**
     * 会话标题最大长度
     */
    public static final int MAX_TITLE_LENGTH = 200;

    /**
     * 生成会话标题时的最大字符数
     */
    public static final int TITLE_GENERATION_MAX_CHARS = 30;

    /**
     * 默认加载历史消息数量
     */
    public static final int DEFAULT_HISTORY_LIMIT = 10;

    /**
     * 最大加载历史消息数量
     */
    public static final int MAX_HISTORY_LIMIT = 100;

    /**
     * 私有构造函数，防止实例化
     */
    private ConversationConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}

