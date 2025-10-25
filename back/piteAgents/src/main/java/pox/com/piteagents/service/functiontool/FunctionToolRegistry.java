package pox.com.piteagents.service.functiontool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 统一工具注册器
 * <p>
 * 负责发现、注册和管理所有的工具实例。
 * 在应用启动时自动扫描所有带@Tool注解的UnifiedTool实现并注册。
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
@Slf4j
@Component
public class FunctionToolRegistry implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    
    /**
     * 工具注册表
     * key: 工具名称，value: 工具实例
     */
    private final Map<String, GLMUnifiedTool> toolRegistry = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 应用启动后初始化工具注册表
     */
    @PostConstruct
    public void initializeRegistry() {
        log.info("开始初始化工具注册表...");
        
        try {
            // 扫描所有UnifiedTool实现
            Map<String, GLMUnifiedTool> toolBeans = applicationContext.getBeansOfType(GLMUnifiedTool.class);
            
            for (GLMUnifiedTool tool : toolBeans.values()) {
                registerTool(tool);
            }
            
            log.info("工具注册表初始化完成，注册工具数量: {}", toolRegistry.size());
            logRegisteredTools();
            
        } catch (Exception e) {
            log.error("工具注册表初始化失败", e);
        }
    }

    /**
     * 注册单个工具
     *
     * @param tool 工具实例
     */
    private void registerTool(GLMUnifiedTool tool) {
        try {
            String toolName = tool.getName();
            
            if (toolName == null || toolName.trim().isEmpty()) {
                log.warn("工具名称为空，跳过注册: {}", tool.getClass().getName());
                return;
            }
            
            // 检查工具名称是否重复
            if (toolRegistry.containsKey(toolName)) {
                log.warn("发现重复的工具名称: {}，将覆盖之前的注册", toolName);
            }
            
            // 检查工具是否可用
            if (!tool.isAvailable()) {
                log.warn("工具不可用，跳过注册: {}", toolName);
                return;
            }
            
            // 注册工具
            toolRegistry.put(toolName, tool);
            
            log.info("注册工具: {} -> {} ({})", 
                    toolName, 
                    tool.getClass().getSimpleName(), 
                    tool.getToolType());
            
        } catch (Exception e) {
            log.error("注册工具失败: {}", tool.getClass().getName(), e);
        }
    }

    /**
     * 根据工具名称获取工具实例
     *
     * @param toolName 工具名称
     * @return 工具实例，如果不存在则返回null
     */
    public GLMUnifiedTool getTool(String toolName) {
        return toolRegistry.get(toolName);
    }

    /**
     * 检查工具是否存在
     *
     * @param toolName 工具名称
     * @return 是否存在
     */
    public boolean hasTool(String toolName) {
        return toolRegistry.containsKey(toolName);
    }

    /**
     * 获取所有注册的工具
     *
     * @return 只读的工具注册表
     */
    public Map<String, GLMUnifiedTool> getAllTools() {
        return new HashMap<>(toolRegistry);
    }

    /**
     * 获取所有注册的工具名称
     *
     * @return 工具名称集合
     */
    public Set<String> getAllToolNames() {
        return toolRegistry.keySet();
    }

    /**
     * 获取注册的工具数量
     *
     * @return 工具数量
     */
    public int getToolCount() {
        return toolRegistry.size();
    }

    /**
     * 获取指定类型的工具
     *
     * @param toolType 工具类型
     * @return 指定类型的工具Map
     */
    public Map<String, GLMUnifiedTool> getToolsByType(GLMUnifiedTool.ToolType toolType) {
        Map<String, GLMUnifiedTool> result = new HashMap<>();
        toolRegistry.forEach((name, tool) -> {
            if (tool.getToolType() == toolType) {
                result.put(name, tool);
            }
        });
        return result;
    }

    /**
     * 输出已注册的工具信息
     */
    private void logRegisteredTools() {
        if (toolRegistry.isEmpty()) {
            log.info("未发现任何工具");
            return;
        }

        log.info("已注册的工具列表:");
        toolRegistry.forEach((name, tool) -> {
            log.info("  - {}: {} ({})", 
                    name, 
                    tool.getDescription(), 
                    tool.getToolType());
        });
    }

    /**
     * 应用关闭时清理工具
     */
    @PreDestroy
    public void cleanup() {
        log.info("清理工具注册表...");
        toolRegistry.clear();
        log.info("工具注册表清理完成");
    }
}
