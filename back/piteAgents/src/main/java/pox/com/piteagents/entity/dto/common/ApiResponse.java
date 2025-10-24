package pox.com.piteagents.entity.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一API响应格式
 * <p>
 * 用于封装所有API接口的响应数据，提供统一的响应结构。
 * </p>
 *
 * @param <T> 响应数据的类型
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 响应状态码
     * <p>
     * 200: 成功
     * 400: 请求参数错误
     * 500: 服务器内部错误
     * </p>
     */
    private Integer code;

    /**
     * 响应消息
     * <p>
     * 用于描述响应状态或错误信息
     * </p>
     */
    private String message;

    /**
     * 响应数据
     * <p>
     * 实际的业务数据，类型由泛型指定
     * </p>
     */
    private T data;

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .build();
    }

    /**
     * 创建成功响应（无数据）
     *
     * @param message 响应消息
     * @param <T>     数据类型
     * @return 成功响应对象
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .code(200)
                .message(message)
                .build();
    }

    /**
     * 创建失败响应
     *
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 失败响应对象
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .build();
    }

    /**
     * 创建失败响应（默认500错误码）
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 失败响应对象
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(500, message);
    }
}

