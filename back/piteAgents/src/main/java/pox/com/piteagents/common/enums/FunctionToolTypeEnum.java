package pox.com.piteagents.common.enums;

import lombok.Getter;

/**
 * 工具类型枚举（简化版）
 * <p>
 * 定义系统支持的工具类型，仅支持HTTP和内置两种类型。
 * 移除了复杂的LOCAL类型，简化工具管理。
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
@Getter
public enum FunctionToolTypeEnum {

    /**
     * HTTP 工具
     * <p>
     * 调用外部的HTTP API服务
     * 需要配置：endpoint, method, headers
     * 适用场景：天气查询、搜索引擎、外部数据API等
     * </p>
     */
    HTTP("HTTP", "外部API调用"),

    /**
     * 内置工具
     * <p>
     * 系统预定义的内置工具
     * 通过@Tool注解自动注册
     * 适用场景：数学计算、时间处理、系统信息等
     * </p>
     */
    BUILTIN("BUILTIN", "内置工具");

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
    FunctionToolTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举实例
     *
     * @param code 类型代码
     * @return 对应的枚举实例，如果不存在则返回null
     */
    public static FunctionToolTypeEnum fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (FunctionToolTypeEnum type : values()) {
            if (type.code.equals(code.toUpperCase())) {
                return type;
            }
        }
        return null;
    }

    /**
     * 检查类型代码是否有效
     *
     * @param code 类型代码
     * @return true表示有效，false表示无效
     */
    public static boolean isValidCode(String code) {
        return fromCode(code) != null;
    }

    /**
     * 检查是否为HTTP工具类型
     *
     * @param code 类型代码
     * @return 是否为HTTP类型
     */
    public static boolean isHttpType(String code) {
        return HTTP.code.equals(code);
    }


    /**
     * 检查是否为内置工具类型
     *
     * @param code 类型代码
     * @return 是否为内置类型
     */
    public static boolean isBuiltinType(String code) {
        return BUILTIN.code.equals(code);
    }
}
