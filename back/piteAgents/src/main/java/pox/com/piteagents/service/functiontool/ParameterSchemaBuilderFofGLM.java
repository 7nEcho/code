package pox.com.piteagents.service.functiontool;

import java.util.*;

/**
 * 参数 Schema 构建器
 * <p>
 * 提供流式 API 构建符合 GLM Function Calling 规范的 JSON Schema。
 * 参考文档: https://docs.bigmodel.cn/cn/guide/capabilities/function-calling
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
public class ParameterSchemaBuilderFofGLM {

    private final Map<String, Object> properties;
    private final List<String> required;

    private ParameterSchemaBuilderFofGLM() {
        this.properties = new LinkedHashMap<>();
        this.required = new ArrayList<>();
    }

    /**
     * 创建新的构建器实例
     *
     * @return 构建器实例
     */
    public static ParameterSchemaBuilderFofGLM create() {
        return new ParameterSchemaBuilderFofGLM();
    }

    /**
     * 添加字符串类型属性
     *
     * @param name        属性名称
     * @param description 属性描述
     * @param required    是否必填
     * @return 构建器实例（支持链式调用）
     */
    public ParameterSchemaBuilderFofGLM addStringProperty(String name, String description, boolean required) {
        return addProperty(name, "string", description, required, null);
    }

    /**
     * 添加字符串类型属性（带枚举值）
     *
     * @param name        属性名称
     * @param description 属性描述
     * @param required    是否必填
     * @param enumValues  枚举值列表
     * @return 构建器实例（支持链式调用）
     */
    public ParameterSchemaBuilderFofGLM addStringProperty(String name, String description, boolean required, List<String> enumValues) {
        Map<String, Object> constraints = new HashMap<>();
        if (enumValues != null && !enumValues.isEmpty()) {
            constraints.put("enum", enumValues);
        }
        return addProperty(name, "string", description, required, constraints);
    }

    /**
     * 添加整数类型属性
     *
     * @param name        属性名称
     * @param description 属性描述
     * @param required    是否必填
     * @return 构建器实例（支持链式调用）
     */
    public ParameterSchemaBuilderFofGLM addIntegerProperty(String name, String description, boolean required) {
        return addProperty(name, "integer", description, required, null);
    }

    /**
     * 添加整数类型属性（带范围限制）
     *
     * @param name        属性名称
     * @param description 属性描述
     * @param required    是否必填
     * @param minimum     最小值（可选）
     * @param maximum     最大值（可选）
     * @return 构建器实例（支持链式调用）
     */
    public ParameterSchemaBuilderFofGLM addIntegerProperty(String name, String description, boolean required,
                                                           Integer minimum, Integer maximum) {
        Map<String, Object> constraints = new HashMap<>();
        if (minimum != null) {
            constraints.put("minimum", minimum);
        }
        if (maximum != null) {
            constraints.put("maximum", maximum);
        }
        return addProperty(name, "integer", description, required, constraints);
    }

    /**
     * 添加数字类型属性
     *
     * @param name        属性名称
     * @param description 属性描述
     * @param required    是否必填
     * @return 构建器实例（支持链式调用）
     */
    public ParameterSchemaBuilderFofGLM addNumberProperty(String name, String description, boolean required) {
        return addProperty(name, "number", description, required, null);
    }

    /**
     * 添加数字类型属性（带范围限制）
     *
     * @param name        属性名称
     * @param description 属性描述
     * @param required    是否必填
     * @param minimum     最小值（可选）
     * @param maximum     最大值（可选）
     * @return 构建器实例（支持链式调用）
     */
    public ParameterSchemaBuilderFofGLM addNumberProperty(String name, String description, boolean required,
                                                          Double minimum, Double maximum) {
        Map<String, Object> constraints = new HashMap<>();
        if (minimum != null) {
            constraints.put("minimum", minimum);
        }
        if (maximum != null) {
            constraints.put("maximum", maximum);
        }
        return addProperty(name, "number", description, required, constraints);
    }

    /**
     * 添加布尔类型属性
     *
     * @param name        属性名称
     * @param description 属性描述
     * @param required    是否必填
     * @return 构建器实例（支持链式调用）
     */
    public ParameterSchemaBuilderFofGLM addBooleanProperty(String name, String description, boolean required) {
        return addProperty(name, "boolean", description, required, null);
    }

    /**
     * 添加数组类型属性
     *
     * @param name        属性名称
     * @param description 属性描述
     * @param itemType    数组元素类型（string, integer, number, boolean, object）
     * @param required    是否必填
     * @return 构建器实例（支持链式调用）
     */
    public ParameterSchemaBuilderFofGLM addArrayProperty(String name, String description, String itemType, boolean required) {
        Map<String, Object> items = new HashMap<>();
        items.put("type", itemType);

        Map<String, Object> propertyDef = new LinkedHashMap<>();
        propertyDef.put("type", "array");
        propertyDef.put("description", description);
        propertyDef.put("items", items);

        properties.put(name, propertyDef);

        if (required) {
            this.required.add(name);
        }

        return this;
    }

    /**
     * 添加对象类型属性
     *
     * @param name        属性名称
     * @param description 属性描述
     * @param objectSchema 对象的 schema 定义
     * @param required    是否必填
     * @return 构建器实例（支持链式调用）
     */
    public ParameterSchemaBuilderFofGLM addObjectProperty(String name, String description,
                                                          Map<String, Object> objectSchema, boolean required) {
        Map<String, Object> propertyDef = new LinkedHashMap<>();
        propertyDef.put("type", "object");
        propertyDef.put("description", description);
        propertyDef.putAll(objectSchema);

        properties.put(name, propertyDef);

        if (required) {
            this.required.add(name);
        }

        return this;
    }

    /**
     * 添加通用属性
     *
     * @param name        属性名称
     * @param type        属性类型
     * @param description 属性描述
     * @param required    是否必填
     * @param constraints 额外的约束（如 enum, minimum, maximum 等）
     * @return 构建器实例（支持链式调用）
     */
    private ParameterSchemaBuilderFofGLM addProperty(String name, String type, String description,
                                                     boolean required, Map<String, Object> constraints) {
        Map<String, Object> propertyDef = new LinkedHashMap<>();
        propertyDef.put("type", type);
        propertyDef.put("description", description);

        if (constraints != null && !constraints.isEmpty()) {
            propertyDef.putAll(constraints);
        }

        properties.put(name, propertyDef);

        if (required) {
            this.required.add(name);
        }

        return this;
    }

    /**
     * 构建最终的 Schema 对象
     * <p>
     * 返回符合 GLM Function Calling 规范的 JSON Schema。
     * </p>
     *
     * @return Schema Map
     */
    public Map<String, Object> build() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);

        if (!required.isEmpty()) {
            schema.put("required", required);
        }

        return schema;
    }

    /**
     * 构建空的 Schema（无参数）
     *
     * @return 空 Schema Map
     */
    public static Map<String, Object> buildEmpty() {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", new HashMap<>());
        return schema;
    }
}

