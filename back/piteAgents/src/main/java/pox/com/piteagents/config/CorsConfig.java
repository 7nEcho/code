package pox.com.piteagents.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 * <p>
 * 配置CORS，允许前端应用访问后端API。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Configuration
public class CorsConfig {

    /**
     * 配置CORS过滤器
     * <p>
     * 允许前端开发服务器（http://localhost:5173）访问后端API。
     * </p>
     *
     * @return CORS过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 允许携带凭证
        config.setAllowCredentials(true);
        
        // 允许的源
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://127.0.0.1:5173");
        
        // 允许的请求头
        config.addAllowedHeader("*");
        
        // 允许的请求方法
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        
        // 预检请求的有效期，单位：秒
        config.setMaxAge(3600L);

        // 对所有路径应用此配置
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}

