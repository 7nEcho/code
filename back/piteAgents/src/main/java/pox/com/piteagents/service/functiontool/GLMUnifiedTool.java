package pox.com.piteagents.service.functiontool;

import java.util.Map;

/**
 * GLM统一工具接口
 * <p>
 * 符合GLM Function Calling规范的统一工具接口，支持HTTP和内置两种工具类型。
 * 所有工具都必须实现此接口，提供标准化的工具调用方式。
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
public interface GLMUnifiedTool {

    /**
     * 获取工具名称
     * <p>
     * 返回工具的唯一标识符，必须与GLM Function Calling中的function name一致。
     * </p>
     *
     * @return 工具名称
     */
    String getName();

    /**
     * 获取工具描述
     * <p>
     * 返回工具的详细描述，帮助AI理解工具的功能和使用场景。
     * 此描述将直接用于GLM Function Calling的function description。
     * </p>
     *
     * @return 工具描述
     */
    String getDescription();

    /**
     * 获取参数定义
     * <p>
     * 返回符合JSON Schema格式的参数定义，用于GLM Function Calling。
     * 格式需要严格遵循OpenAPI规范。
     * </p>
     *
     * @return JSON Schema格式的参数定义
     */
    Map<String, Object> getParametersSchema();

    /**
     * 执行工具
     * <p>
     * 根据提供的参数执行工具逻辑，返回执行结果。
     * 参数已经过验证，可以直接使用。
     * </p>
     *
     * @param parameters 工具参数
     * @return 工具执行结果（JSON格式字符串）
     * @throws RuntimeException 当执行过程中出现异常时
     */
    String execute(Map<String, Object> parameters);

    /**
     * 检查工具是否可用
     * <p>
     * 检查工具当前是否可以正常执行，用于健康检查。
     * 默认返回true，子类可以根据需要重写。
     * </p>
     *
     * @return true表示可用，false表示不可用
     */
    default boolean isAvailable() {
        return true;
    }

    /**
     * 获取工具类型
     * <p>
     * 返回工具类型，仅支持HTTP和BUILTIN两种类型。
     * </p>
     *
     * @return 工具类型
     */
    ToolType getToolType();

    /**
     * 工具类型枚举
     */
    enum ToolType {
        /**
         * HTTP工具 - 调用外部API
         */
        HTTP,
        
        /**
         * 内置工具 - 系统内置功能
         */
        BUILTIN
    }
}
