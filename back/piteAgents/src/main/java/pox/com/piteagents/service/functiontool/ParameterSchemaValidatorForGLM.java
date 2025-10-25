package pox.com.piteagents.service.functiontool;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 参数 Schema 验证器
 * <p>
 * 验证工具的参数 schema 是否符合 GLM Function Calling 规范。
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
@Slf4j
@Component
public class ParameterSchemaValidatorForGLM {

    private static final List<String> VALID_TYPES = List.of("string", "integer", "number", "boolean", "array", "object");

    /**
     * 验证 Schema 是否有效
     *
     * @param schema 参数 schema
     * @return 验证结果
     */
    public ValidationResult validate(Map<String, Object> schema) {
        List<String> errors = new ArrayList<>();

        if (schema == null || schema.isEmpty()) {
            // 空 schema 是合法的（表示无参数）
            return ValidationResult.success();
        }

        // 1. 验证顶层必须有 type 字段
        Object type = schema.get("type");
        if (type == null) {
            errors.add("Schema 缺少 'type' 字段");
            return ValidationResult.failure(errors);
        }

        // 2. 验证 type 必须是 "object"
        if (!"object".equals(type)) {
            errors.add("Schema 顶层 type 必须为 'object'，当前为: " + type);
        }

        // 3. 验证 properties 字段
        Object properties = schema.get("properties");
        if (properties != null) {
            if (!(properties instanceof Map)) {
                errors.add("'properties' 字段必须是对象");
            } else {
                @SuppressWarnings("unchecked")
                Map<String, Object> propsMap = (Map<String, Object>) properties;
                validateProperties(propsMap, errors);
            }
        }

        // 4. 验证 required 字段（可选）
        Object required = schema.get("required");
        if (required != null && !(required instanceof List)) {
            errors.add("'required' 字段必须是数组");
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.failure(errors);
    }

    /**
     * 验证 JSON 字符串格式的 Schema
     *
     * @param schemaJson Schema JSON 字符串
     * @return 验证结果
     */
    @SuppressWarnings("unchecked")
    public ValidationResult validateJson(String schemaJson) {
        if (schemaJson == null || schemaJson.trim().isEmpty()) {
            return ValidationResult.success();
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> schema = mapper.readValue(schemaJson, Map.class);
            return validate(schema);
        } catch (Exception e) {
            log.error("解析 Schema JSON 失败: {}", e.getMessage());
            return ValidationResult.failure(List.of("Schema JSON 格式错误: " + e.getMessage()));
        }
    }

    /**
     * 验证 properties 中的每个属性
     */
    private void validateProperties(Map<String, Object> properties, List<String> errors) {
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            String propName = entry.getKey();
            Object propValue = entry.getValue();

            if (!(propValue instanceof Map)) {
                errors.add("属性 '" + propName + "' 的定义必须是对象");
                continue;
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> propDef = (Map<String, Object>) propValue;

            // 验证属性的 type 字段
            Object propType = propDef.get("type");
            if (propType == null) {
                errors.add("属性 '" + propName + "' 缺少 'type' 字段");
            } else if (!VALID_TYPES.contains(propType.toString())) {
                errors.add("属性 '" + propName + "' 的 type 无效: " + propType);
            }

            // 验证 array 类型必须有 items 字段
            if ("array".equals(propType) && !propDef.containsKey("items")) {
                errors.add("属性 '" + propName + "' 是 array 类型但缺少 'items' 字段");
            }
        }
    }

    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        private ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors != null ? errors : List.of();
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult failure(List<String> errors) {
            return new ValidationResult(false, errors);
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            return String.join("; ", errors);
        }
    }
}

