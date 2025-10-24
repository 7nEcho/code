package pox.com.piteagents.enums;

import lombok.Getter;

/**
 * 内容类型枚举
 * <p>
 * 定义知识库支持的内容类型。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Getter
public enum ContentTypeEnum {

    /**
     * 纯文本
     */
    TEXT("TEXT", "纯文本"),

    /**
     * Markdown 格式
     */
    MARKDOWN("MARKDOWN", "Markdown"),

    /**
     * JSON 格式
     */
    JSON("JSON", "JSON"),

    /**
     * HTML 格式
     */
    HTML("HTML", "HTML");

    /**
     * 类型代码
     */
    private final String code;

    /**
     * 类型名称
     */
    private final String name;

    /**
     * 构造函数
     *
     * @param code 类型代码
     * @param name 类型名称
     */
    ContentTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举实例
     *
     * @param code 类型代码
     * @return 对应的枚举实例，如果不存在则返回 null
     */
    public static ContentTypeEnum fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (ContentTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 检查类型代码是否有效
     *
     * @param code 类型代码
     * @return true 表示有效，false 表示无效
     */
    public static boolean isValidCode(String code) {
        return fromCode(code) != null;
    }
}

