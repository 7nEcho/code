package pox.com.piteagents.common.constant;

/**
 * 消息角色常量
 * <p>
 * 定义智谱AI对话中支持的消息角色类型。
 * 使用常量类代替魔法字符串，提高代码可维护性。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public final class MessageRole {

    /**
     * 系统角色
     * <p>
     * 用于设定AI的行为、角色和规则。
     * 例如："你是一个专业的编程助手"
     * </p>
     */
    public static final String SYSTEM = "system";

    /**
     * 用户角色
     * <p>
     * 表示用户的输入或问题。
     * </p>
     */
    public static final String USER = "user";

    /**
     * 助手角色
     * <p>
     * 表示AI助手的回复。
     * 用于构建多轮对话的上下文。
     * </p>
     */
    public static final String ASSISTANT = "assistant";

    /**
     * 私有构造函数，防止实例化
     */
    private MessageRole() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}

