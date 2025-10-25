package pox.com.piteagents.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * JSON 工具类
 * <p>
 * 提供统一的 JSON 序列化和反序列化功能，
 * 封装 Jackson ObjectMapper 的常用操作。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JsonUtils {

    private final ObjectMapper objectMapper;

    /**
     * 将对象转换为 JSON 字符串
     * <p>
     * 对 null 值进行安全处理，序列化失败时抛出 IllegalArgumentException。
     * </p>
     *
     * @param value 待转换的对象
     * @return JSON 字符串，如果输入为 null 则返回 null
     * @throws IllegalArgumentException 当序列化失败时抛出
     */
    public String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("JSON 序列化失败: {}", e.getMessage(), e);
            throw new IllegalArgumentException("JSON 序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将 JSON 字符串转换为 Map&lt;String, Object&gt;
     * <p>
     * 用于解析通用的 JSON 对象，支持嵌套结构。
     * 对空字符串和反序列化错误进行安全处理。
     * </p>
     *
     * @param json JSON 字符串
     * @return Map 对象，如果输入为空或解析失败则返回 null
     */
    public Map<String, Object> fromJsonToObjectMap(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.warn("JSON 反序列化为 ObjectMap 失败: {}, JSON: {}", e.getMessage(), json);
            return null;
        }
    }

    /**
     * 将 JSON 字符串转换为 Map&lt;String, String&gt;
     * <p>
     * 用于解析简单的键值对 JSON 对象，所有值都转换为字符串。
     * 对空字符串和反序列化错误进行安全处理。
     * </p>
     *
     * @param json JSON 字符串
     * @return Map 对象，如果输入为空或解析失败则返回 null
     */
    public Map<String, String> fromJsonToStringMap(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            log.warn("JSON 反序列化为 StringMap 失败: {}, JSON: {}", e.getMessage(), json);
            return null;
        }
    }

    /**
     * 将 JSON 字符串转换为指定类型的对象
     * <p>
     * 通用的类型转换方法，支持任意 Java 对象类型。
     * </p>
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型的 Class 对象
     * @param <T>   目标类型
     * @return 转换后的对象，如果输入为空或解析失败则返回 null
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.warn("JSON 反序列化为 {} 失败: {}, JSON: {}", clazz.getSimpleName(), e.getMessage(), json);
            return null;
        }
    }

    /**
     * 将 JSON 字符串转换为指定类型的对象（支持泛型）
     * <p>
     * 使用 TypeReference 支持复杂的泛型类型，如 List&lt;T&gt;, Map&lt;K,V&gt; 等。
     * </p>
     *
     * @param json          JSON 字符串
     * @param typeReference 类型引用
     * @param <T>           目标类型
     * @return 转换后的对象，如果输入为空或解析失败则返回 null
     */
    public <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            log.warn("JSON 反序列化失败: {}, JSON: {}", e.getMessage(), json);
            return null;
        }
    }

    /**
     * 将对象转换为格式化的 JSON 字符串（美化输出）
     * <p>
     * 适用于日志记录或调试输出。
     * </p>
     *
     * @param value 待转换的对象
     * @return 格式化的 JSON 字符串，如果输入为 null 则返回 null
     * @throws IllegalArgumentException 当序列化失败时抛出
     */
    public String toPrettyJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("JSON 格式化序列化失败: {}", e.getMessage(), e);
            throw new IllegalArgumentException("JSON 格式化序列化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查字符串是否为有效的 JSON 格式
     * <p>
     * 通过尝试解析来验证 JSON 格式的有效性。
     * </p>
     *
     * @param json 待检查的字符串
     * @return true 表示有效的 JSON，false 表示无效
     */
    public boolean isValidJson(String json) {
        if (!StringUtils.hasText(json)) {
            return false;
        }
        try {
            objectMapper.readTree(json);
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }
}

