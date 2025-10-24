package pox.com.piteagents.exception;

import lombok.Getter;

/**
 * 智谱API调用异常
 * <p>
 * 当调用智谱AI API出现错误时抛出此异常。
 * 包含错误码和详细的错误信息。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Getter
public class ZhipuApiException extends RuntimeException {

    /**
     * 错误码
     */
    private final Integer code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public ZhipuApiException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数（默认错误码500）
     *
     * @param message 错误消息
     */
    public ZhipuApiException(String message) {
        this(500, message);
    }

    /**
     * 构造函数（包含原始异常）
     *
     * @param code    错误码
     * @param message 错误消息
     * @param cause   原始异常
     */
    public ZhipuApiException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数（默认错误码500，包含原始异常）
     *
     * @param message 错误消息
     * @param cause   原始异常
     */
    public ZhipuApiException(String message, Throwable cause) {
        this(500, message, cause);
    }
}

