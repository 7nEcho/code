package pox.com.piteagents.constant;

/**
 * Agent 相关常量
 * <p>
 * 定义 Agent 模块使用的常量值。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public final class AgentConstants {

    /**
     * 默认分类
     */
    public static final String DEFAULT_CATEGORY = "通用";

    /**
     * 默认状态
     */
    public static final String DEFAULT_STATUS = "ACTIVE";

    /**
     * Agent 名称最大长度
     */
    public static final int MAX_NAME_LENGTH = 100;

    /**
     * Agent 描述最大长度
     */
    public static final int MAX_DESCRIPTION_LENGTH = 5000;

    /**
     * 头像 URL 最大长度
     */
    public static final int MAX_AVATAR_LENGTH = 255;

    /**
     * 分类最大长度
     */
    public static final int MAX_CATEGORY_LENGTH = 50;

    /**
     * 默认系统提示词
     */
    public static final String DEFAULT_SYSTEM_PROMPT = "你是一个友好、专业的 AI 助手。";

    /**
     * 私有构造函数，防止实例化
     */
    private AgentConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}

