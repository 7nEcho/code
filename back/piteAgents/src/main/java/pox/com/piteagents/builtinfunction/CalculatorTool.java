package pox.com.piteagents.builtinfunction;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pox.com.piteagents.service.functiontool.GLMUnifiedTool;
import pox.com.piteagents.service.functiontool.ParameterSchemaBuilderFofGLM;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计算器工具（内置工具示例）
 * <p>
 * 执行基本的数学运算。
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
@Slf4j
@Component
public class CalculatorTool implements GLMUnifiedTool {

    private static final String TOOL_NAME = "calculator";

    @Override
    public String getName() {
        return TOOL_NAME;
    }

    @Override
    public String getDescription() {
        return "执行基本的数学运算，支持加减乘除四则运算。";
    }

    @Override
    public Map<String, Object> getParametersSchema() {
        return ParameterSchemaBuilderFofGLM.create()
                .addNumberProperty("a", "第一个数字", true, null, null)
                .addNumberProperty("b", "第二个数字", true, null, null)
                .addStringProperty("operation", "运算符", true, 
                        List.of("add", "subtract", "multiply", "divide"))
                .build();
    }

    @Override
    public String execute(Map<String, Object> parameters) {
        try {
            // 获取参数
            double a = getDoubleParameter(parameters, "a");
            double b = getDoubleParameter(parameters, "b");
            String operation = (String) parameters.get("operation");

            if (operation == null || operation.trim().isEmpty()) {
                return "{\"error\": \"运算符不能为空\"}";
            }

            // 执行计算
            double result;
            switch (operation) {
                case "add":
                    result = a + b;
                    break;
                case "subtract":
                    result = a - b;
                    break;
                case "multiply":
                    result = a * b;
                    break;
                case "divide":
                    if (b == 0) {
                        return "{\"error\": \"除数不能为零\"}";
                    }
                    result = a / b;
                    break;
                default:
                    return "{\"error\": \"不支持的运算符: " + operation + "\"}";
            }

            // 构建返回结果
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("a", a);
            resultMap.put("b", b);
            resultMap.put("operation", operation);
            resultMap.put("result", result);

            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(resultMap);

        } catch (Exception e) {
            log.error("计算失败: {}", e.getMessage(), e);
            return "{\"error\": \"计算失败: " + e.getMessage() + "\"}";
        }
    }

    @Override
    public ToolType getToolType() {
        return ToolType.BUILTIN;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    /**
     * 从参数中获取 double 值
     */
    private double getDoubleParameter(Map<String, Object> parameters, String key) {
        Object value = parameters.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            return Double.parseDouble((String) value);
        }
        throw new IllegalArgumentException("参数 " + key + " 必须是数字");
    }
}

