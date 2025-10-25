package pox.com.piteagents.service.functiontool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pox.com.piteagents.entity.po.FunctionToolDefinitionPO;

import java.util.Map;

/**
 * HTTP工具实现类
 * <p>
 * 用于调用外部HTTP API的工具实现。
 * 此类主要用于包装数据库定义的HTTP工具。
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class HttpToolGLM implements GLMUnifiedTool {

    private final FunctionToolDefinitionPO toolDefinition;

    @Override
    public String getName() {
        return toolDefinition.getName();
    }

    @Override
    public String getDescription() {
        return toolDefinition.getDescription();
    }

    @Override
    public Map<String, Object> getParametersSchema() {
        // TODO: 从toolDefinition.getParameters()解析JSON Schema
        // 暂时返回空的schema，待实现
        return Map.of(
            "type", "object",
            "properties", Map.of()
        );
    }

    @Override
    public String execute(Map<String, Object> parameters) {
        // HTTP工具的执行逻辑由SimpleToolExecutor处理
        // 这个类主要用于包装和标识HTTP工具
        throw new UnsupportedOperationException("HTTP工具的执行由SimpleToolExecutor处理");
    }

    @Override
    public boolean isAvailable() {
        return toolDefinition.getIsActive() != null && toolDefinition.getIsActive();
    }

    @Override
    public ToolType getToolType() {
        return ToolType.HTTP;
    }
}
