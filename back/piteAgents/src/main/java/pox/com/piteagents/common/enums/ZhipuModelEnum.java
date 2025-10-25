package pox.com.piteagents.common.enums;

import lombok.Getter;

/**
 * 智谱AI模型枚举
 * <p>
 * 定义支持的智谱AI模型类型，包括模型代码、描述和推荐使用场景。
 * 参考官方文档: https://docs.bigmodel.cn/cn/api/introduction
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Getter
public enum ZhipuModelEnum {

    /**
     * GLM-4.5 模型
     * <p>
     * 特点：高性能、高准确率
     * 适用场景：复杂推理、深度分析、专业内容生成
     * </p>
     */
    GLM_4_5("glm-4.5", "GLM-4.5", "高性能模型，适合复杂任务和深度分析"),

    /**
     * GLM-4.6 模型
     * <p>
     * 特点：最新版本、性能优化、响应速度快
     * 适用场景：通用对话、内容生成、代码编写、知识问答
     * 推荐：作为默认模型使用
     * </p>
     */
    GLM_4_6("glm-4.6", "GLM-4.6", "最新版本，性能优化，推荐使用");

    /**
     * 模型代码（API调用时使用的标识）
     */
    private final String code;

    /**
     * 模型名称（用于展示）
     */
    private final String name;

    /**
     * 模型描述（说明模型特点和适用场景）
     */
    private final String description;

    /**
     * 构造函数
     *
     * @param code        模型代码
     * @param name        模型名称
     * @param description 模型描述
     */
    ZhipuModelEnum(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    /**
     * 根据模型代码获取枚举实例
     *
     * @param code 模型代码
     * @return 对应的枚举实例，如果不存在则返回null
     */
    public static ZhipuModelEnum fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        for (ZhipuModelEnum model : values()) {
            if (model.code.equals(code)) {
                return model;
            }
        }
        return null;
    }

    /**
     * 检查模型代码是否有效
     *
     * @param code 模型代码
     * @return true表示有效，false表示无效
     */
    public static boolean isValidCode(String code) {
        return fromCode(code) != null;
    }
}

