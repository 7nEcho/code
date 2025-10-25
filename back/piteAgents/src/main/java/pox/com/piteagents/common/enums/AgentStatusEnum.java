package pox.com.piteagents.common.enums;

import lombok.Getter;

/**
 * Agent 状态枚举
 * <p>
 * 定义 Agent 的生命周期状态。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Getter
public enum AgentStatusEnum {

    /**
     * 激活状态
     * <p>
     * Agent 可以正常使用
     * </p>
     */
    ACTIVE("ACTIVE", "激活"),

    /**
     * 未激活状态
     * <p>
     * Agent 暂时不可用
     * </p>
     */
    INACTIVE("INACTIVE", "未激活"),

    /**
     * 已归档状态
     * <p>
     * Agent 已归档，不再使用
     * </p>
     */
    ARCHIVED("ARCHIVED", "已归档");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态名称
     */
    private final String name;

    /**
     * 构造函数
     *
     * @param code 状态代码
     * @param name 状态名称
     */
    AgentStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举实例
     *
     * @param code 状态代码
     * @return 对应的枚举实例，如果不存在则返回 null
     */
    public static AgentStatusEnum fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (AgentStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 检查状态代码是否有效
     *
     * @param code 状态代码
     * @return true 表示有效，false 表示无效
     */
    public static boolean isValidCode(String code) {
        return fromCode(code) != null;
    }
}

