package pox.com.piteagents.exception;

import lombok.Getter;

/**
 * 工具执行异常
 * <p>
 * 当调用外部工具API时发生错误时抛出此异常。
 * 包含工具名称、错误原因等详细信息。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Getter
public class FunctionToolExecutionException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 工具名称
     */
    private final String toolName;

    /**
     * 构造函数
     *
     * @param toolName 工具名称
     * @param message  错误消息
     */
    public FunctionToolExecutionException(String toolName, String message) {
        super(message);
        this.code = 500;
        this.toolName = toolName;
    }

    /**
     * 构造函数（包含原始异常）
     *
     * @param toolName 工具名称
     * @param message  错误消息
     * @param cause    原始异常
     */
    public FunctionToolExecutionException(String toolName, String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
        this.toolName = toolName;
    }

    /**
     * 构造函数（包含错误码）
     *
     * @param code     错误码
     * @param toolName 工具名称
     * @param message  错误消息
     */
    public FunctionToolExecutionException(Integer code, String toolName, String message) {
        super(message);
        this.code = code;
        this.toolName = toolName;
    }

    /**
     * 构造函数（完整参数）
     *
     * @param code     错误码
     * @param toolName 工具名称
     * @param message  错误消息
     * @param cause    原始异常
     */
    public FunctionToolExecutionException(Integer code, String toolName, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.toolName = toolName;
    }

    @Override
    public String getMessage() {
        return String.format("工具执行失败 [%s]: %s", toolName, super.getMessage());
    }
}
