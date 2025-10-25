package pox.com.piteagents.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP客户端配置
 * <p>
 * 配置用于工具调用的RestTemplate，设置合理的超时时间和连接池。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Slf4j
@Configuration
public class HttpClientConfig {

    /**
     * 配置RestTemplate Bean
     * <p>
     * 用于工具执行器的HTTP调用，设置默认超时时间。
     * 实际超时时间会被工具定义中的timeout属性覆盖。
     * </p>
     *
     * @return RestTemplate实例
     */
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // 配置请求工厂，设置默认超时时间
        ClientHttpRequestFactory factory = createRequestFactory();
        restTemplate.setRequestFactory(factory);
        
        log.info("RestTemplate配置完成，默认连接超时: 10s, 读取超时: 30s");
        return restTemplate;
    }

    /**
     * 创建请求工厂，配置超时时间
     *
     * @return 请求工厂
     */
    private ClientHttpRequestFactory createRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // 连接超时时间（10秒）
        factory.setConnectTimeout(10000);
        
        // 读取超时时间（30秒）- 会被工具的timeout属性覆盖
        factory.setReadTimeout(30000);
        
        return factory;
    }
}
