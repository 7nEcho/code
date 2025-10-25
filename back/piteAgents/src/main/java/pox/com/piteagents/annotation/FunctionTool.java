package pox.com.piteagents.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 工具注解
 * <p>
 * 用于标识工具类，支持自动扫描和注册。
 * 所有使用此注解的类都会被自动注册到工具注册器中。
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface FunctionTool {

    /**
     * 工具名称
     * <p>
     * 如果未指定，将使用类名的camelCase形式作为工具名称。
     * 例如：WeatherTool -> weatherTool
     * </p>
     *
     * @return 工具名称
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

    /**
     * 工具描述
     * <p>
     * 可选的工具描述，如果未指定，将使用类中的getDescription()方法返回值。
     * </p>
     *
     * @return 工具描述
     */
    String description() default "";

    /**
     * 是否启用
     * <p>
     * 控制工具是否启用，默认启用。
     * </p>
     *
     * @return 是否启用
     */
    boolean enabled() default true;
}
