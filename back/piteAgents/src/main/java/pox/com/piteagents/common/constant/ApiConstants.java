package pox.com.piteagents.constant;

/**
 * API 相关常量
 * <p>
 * 定义 API 接口使用的常量值。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public final class ApiConstants {

    /**
     * API 基础路径
     */
    public static final String API_BASE_PATH = "/api";

    /**
     * API 版本
     */
    public static final String API_VERSION = "v1";

    /**
     * 默认分页大小
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大分页大小
     */
    public static final int MAX_PAGE_SIZE = 100;

    /**
     * 成功响应码
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * 客户端错误响应码
     */
    public static final int CLIENT_ERROR_CODE = 400;

    /**
     * 服务器错误响应码
     */
    public static final int SERVER_ERROR_CODE = 500;

    /**
     * 资源不存在错误码
     */
    public static final int NOT_FOUND_CODE = 404;

    /**
     * 成功消息
     */
    public static final String SUCCESS_MESSAGE = "success";

    /**
     * 私有构造函数，防止实例化
     */
    private ApiConstants() {
        throw new UnsupportedOperationException("This is a constants class and cannot be instantiated");
    }
}

