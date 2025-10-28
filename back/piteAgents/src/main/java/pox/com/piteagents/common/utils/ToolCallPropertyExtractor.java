package pox.com.piteagents.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 工具调用属性提取器
 * <p>
 * 使用反射从 GLM SDK 的 ToolCall 对象中提取属性，
 * 避免硬编码依赖 SDK 的具体类型，提高兼容性。
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
@Slf4j
@Component
public class ToolCallPropertyExtractor {

    /**
     * 从工具调用对象中获取属性
     * <p>
     * 使用反射方式获取属性，兼容不同版本的 GLM SDK。
     * </p>
     *
     * @param toolCall     工具调用对象（来自 GLM SDK）
     * @param propertyName 属性名称（id、name、arguments）
     * @return 属性值，如果不存在或发生错误则返回 null
     */
    public String getProperty(Object toolCall, String propertyName) {
        if (toolCall == null || propertyName == null) {
            return null;
        }

        try {
            // 尝试通过 getFunction() 获取 function 对象
            Object function = toolCall.getClass().getMethod("getFunction").invoke(toolCall);

            if ("name".equals(propertyName) || "arguments".equals(propertyName)) {
                // name 和 arguments 在 function 对象中
                if (function != null) {
                    String methodName = "get" + capitalize(propertyName);
                    Object value = function.getClass().getMethod(methodName).invoke(function);
                    return value != null ? value.toString() : null;
                }
            } else if ("id".equals(propertyName)) {
                // id 在 toolCall 对象本身
                Object value = toolCall.getClass().getMethod("getId").invoke(toolCall);
                return value != null ? value.toString() : null;
            }

            return null;

        } catch (Exception e) {
            log.warn("提取工具调用属性失败: property={}, error={}", propertyName, e.getMessage());
            return null;
        }
    }

    /**
     * 将字符串首字母大写
     *
     * @param str 输入字符串
     * @return 首字母大写的字符串
     */
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}

