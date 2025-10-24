package pox.com.piteagents.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pox.com.piteagents.entity.dto.common.ApiResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * <p>
 * 统一处理应用中的各类异常，返回标准化的错误响应。
 * 包括参数校验异常、业务异常、系统异常等。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     * <p>
     * 当请求参数校验失败时（如@Valid注解校验失败），会触发此异常处理。
     * 返回详细的字段错误信息，帮助客户端定位问题。
     * </p>
     *
     * @param ex 参数校验异常
     * @return 错误响应，包含具体的字段错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 收集所有字段的验证错误
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // 记录日志
        log.warn("参数校验失败: {}", errors);

        // 返回错误响应
        return ApiResponse.<Map<String, String>>builder()
                .code(400)
                .message("参数校验失败")
                .data(errors)
                .build();
    }

    /**
     * 处理智谱API调用异常
     * <p>
     * 当调用智谱AI接口出现问题时触发。
     * 包括网络错误、API返回错误等情况。
     * </p>
     *
     * @param ex 智谱API异常
     * @return 错误响应
     */
    @ExceptionHandler(ZhipuApiException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleZhipuApiException(ZhipuApiException ex) {
        // 记录详细的错误日志
        log.error("智谱API调用失败: code={}, message={}", ex.getCode(), ex.getMessage(), ex);

        // 返回错误响应
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 处理IllegalArgumentException异常
     * <p>
     * 当业务逻辑中参数不合法时触发。
     * </p>
     *
     * @param ex 非法参数异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleIllegalArgumentException(IllegalArgumentException ex) {
        // 记录警告日志
        log.warn("非法参数: {}", ex.getMessage());

        // 返回错误响应
        return ApiResponse.error(400, ex.getMessage());
    }

    /**
     * 处理通用异常
     * <p>
     * 捕获所有未被特定处理器处理的异常。
     * 这是最后的异常兜底处理，避免异常信息直接暴露给客户端。
     * </p>
     *
     * @param ex 异常对象
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleGeneralException(Exception ex) {
        // 记录完整的异常堆栈
        log.error("系统异常: ", ex);

        // 返回通用错误响应，不暴露具体异常信息
        return ApiResponse.error(500, "系统内部错误，请稍后重试");
    }
}

