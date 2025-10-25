package pox.com.piteagents.common.enums;

import lombok.Getter;

/**
 * 对话模式枚举
 * <p>
 * 定义智谱AI对话的调用模式，包括同步调用和流式输出两种方式。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Getter
public enum ChatModeEnum {

    /**
     * 同步模式
     * <p>
     * 特点：一次性返回完整响应
     * stream参数：false
     * 适用场景：对实时性要求不高、需要完整响应的场景
     * </p>
     */
    SYNC("sync", "同步调用", false),

    /**
     * 流式模式
     * <p>
     * 特点：实时流式返回响应内容，使用Server-Sent Events (SSE)
     * stream参数：true
     * 适用场景：需要实时展示生成过程、提升用户体验的场景
     * </p>
     */
    STREAM("stream", "流式输出", true);

    /**
     * 模式代码
     */
    private final String code;

    /**
     * 模式名称
     */
    private final String name;

    /**
     * 是否为流式模式（对应API中的stream参数）
     */
    private final boolean stream;

    /**
     * 构造函数
     *
     * @param code   模式代码
     * @param name   模式名称
     * @param stream 是否为流式模式
     */
    ChatModeEnum(String code, String name, boolean stream) {
        this.code = code;
        this.name = name;
        this.stream = stream;
    }

    /**
     * 根据代码获取枚举实例
     *
     * @param code 模式代码
     * @return 对应的枚举实例，如果不存在则返回null
     */
    public static ChatModeEnum fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (ChatModeEnum mode : values()) {
            if (mode.code.equalsIgnoreCase(code)) {
                return mode;
            }
        }
        return null;
    }
}

