package pox.com.piteagents.common.enums;

import lombok.Getter;

/**
 * HTTP 方法枚举
 * <p>
 * 定义工具调用支持的 HTTP 方法。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Getter
public enum HttpMethodEnum {

    /**
     * GET 请求
     */
    GET("GET", "GET 请求"),

    /**
     * POST 请求
     */
    POST("POST", "POST 请求"),

    /**
     * PUT 请求
     */
    PUT("PUT", "PUT 请求"),

    /**
     * DELETE 请求
     */
    DELETE("DELETE", "DELETE 请求"),

    /**
     * PATCH 请求
     */
    PATCH("PATCH", "PATCH 请求");

    /**
     * 方法代码
     */
    private final String code;

    /**
     * 方法名称
     */
    private final String name;

    /**
     * 构造函数
     *
     * @param code 方法代码
     * @param name 方法名称
     */
    HttpMethodEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举实例
     *
     * @param code 方法代码
     * @return 对应的枚举实例，如果不存在则返回 null
     */
    public static HttpMethodEnum fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (HttpMethodEnum method : values()) {
            if (method.code.equalsIgnoreCase(code)) {
                return method;
            }
        }
        return null;
    }

    /**
     * 检查方法代码是否有效
     *
     * @param code 方法代码
     * @return true 表示有效，false 表示无效
     */
    public static boolean isValidCode(String code) {
        return fromCode(code) != null;
    }
}

