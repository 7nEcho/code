package pox.com.piteagents.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ai.z.openapi.ZhipuAiClient;

/**
 * 智谱AI配置类
 * <p>
 * 负责读取配置文件中的智谱AI相关配置，并初始化智谱AI客户端。
 * 使用HTTP Bearer认证方式，符合智谱AI官方API规范。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "zhipu")
public class ZhipuConfig {

    /**
     * API Key
     * <p>
     * 从智谱AI开放平台获取的API密钥。
     * 用于身份认证，采用HTTP Bearer方式。
     * </p>
     */
    private String apiKey;

    /**
     * API Base URL
     * <p>
     * 智谱AI API的基础URL。
     * 默认值：https://open.bigmodel.cn/api/paas/v4
     * </p>
     */
    private String baseUrl;

    /**
     * 默认模型
     * <p>
     * 当请求中未指定模型时，使用此默认模型。
     * 推荐值：glm-4.6
     * </p>
     */
    private String defaultModel;

    /**
     * 请求超时时间（秒）
     * <p>
     * API调用的最大等待时间。
     * 默认值：60秒
     * </p>
     */
    private Integer timeout;

    /**
     * 默认温度参数
     * <p>
     * 控制输出随机性的参数，取值范围0.0-1.0。
     * 默认值：0.7
     * </p>
     */
    private Double defaultTemperature;

    /**
     * 最大Token数
     * <p>
     * 单次请求生成内容的最大token数量。
     * 默认值：2000
     * </p>
     */
    private Integer maxTokens;

    /**
     * 创建智谱AI客户端Bean
     * <p>
     * 初始化智谱AI SDK客户端，使用配置文件中的API Key进行认证。
     * 该客户端实例将被注入到Service层使用。
     * 参考官方文档: https://docs.bigmodel.cn/cn/guide/models/text/glm-4.5#java
     * </p>
     *
     * @return 智谱AI客户端实例
     * @throws IllegalStateException 如果API Key未配置
     */
    @Bean
    public ZhipuAiClient zhipuAiClient() {
        // 验证API Key是否已配置
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("智谱AI API Key未配置，请在application.yml中配置zhipu.api-key");
            throw new IllegalStateException("智谱AI API Key未配置");
        }

        // 记录初始化日志（隐藏部分API Key信息）
        String maskedApiKey = maskApiKey(apiKey);
        log.info("正在初始化智谱AI客户端...");
        log.info("API Key: {}", maskedApiKey);
        log.info("Base URL: {}", baseUrl);
        log.info("默认模型: {}", defaultModel);
        log.info("超时时间: {}秒", timeout);

        // 创建并返回智谱AI客户端实例
        // 使用Builder模式创建客户端，符合官方SDK规范
        ZhipuAiClient client = ZhipuAiClient.builder()
                .apiKey(apiKey)
                .build();

        log.info("智谱AI客户端初始化成功");
        return client;
    }

    /**
     * 隐藏API Key的敏感信息
     * <p>
     * 用于日志输出时保护API Key安全。
     * 只显示前6位和后4位，中间部分用星号替换。
     * </p>
     *
     * @param apiKey 原始API Key
     * @return 脱敏后的API Key
     */
    private String maskApiKey(String apiKey) {
        if (apiKey == null || apiKey.length() < 10) {
            return "****";
        }
        // 显示前6位和后4位，中间用星号替换
        String prefix = apiKey.substring(0, 6);
        String suffix = apiKey.substring(apiKey.length() - 4);
        return prefix + "****" + suffix;
    }
}

