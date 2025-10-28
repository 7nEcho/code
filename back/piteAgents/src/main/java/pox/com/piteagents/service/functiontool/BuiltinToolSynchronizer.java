package pox.com.piteagents.service.functiontool;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import pox.com.piteagents.common.utils.JsonUtils;
import pox.com.piteagents.entity.po.FunctionToolDefinitionPO;
import pox.com.piteagents.mapper.FunctionToolDefinitionMapper;

import java.util.Map;

/**
 * 内置工具同步器
 * <p>
 * 在应用启动时自动将代码中定义的内置工具同步到数据库。
 * 如果数据库中已存在同名的内置工具，则跳过插入。
 * </p>
 *
 * @author piteAgents
 * @since 2.0.0
 */
@Slf4j
@Component
@DependsOn("functionToolRegistry")
@RequiredArgsConstructor
public class BuiltinToolSynchronizer {

    private final FunctionToolRegistry functionToolRegistry;
    private final FunctionToolDefinitionMapper toolDefinitionMapper;
    private final JsonUtils jsonUtils;

    /**
     * 应用启动后同步内置工具到数据库
     * <p>
     * 该方法会在 FunctionToolRegistry 初始化完成后执行。
     * 遍历所有已注册的内置工具，如果数据库中不存在，则插入新记录。
     * </p>
     */
    @PostConstruct
    public void synchronizeBuiltinTools() {
        log.info("开始同步内置工具到数据库...");

        try {
            // 1. 获取所有 BUILTIN 类型的工具
            Map<String, GLMUnifiedTool> builtinTools = 
                    functionToolRegistry.getToolsByType(GLMUnifiedTool.ToolType.BUILTIN);

            if (builtinTools.isEmpty()) {
                log.info("未发现内置工具，跳过同步");
                return;
            }

            log.info("发现 {} 个内置工具，开始检查数据库...", builtinTools.size());

            int syncCount = 0;
            int skipCount = 0;

            // 2. 遍历每个内置工具
            for (GLMUnifiedTool tool : builtinTools.values()) {
                try {
                    String toolName = tool.getName();

                    // 3. 检查数据库是否存在同名工具
                    LambdaQueryWrapper<FunctionToolDefinitionPO> query = 
                            Wrappers.<FunctionToolDefinitionPO>lambdaQuery()
                                    .eq(FunctionToolDefinitionPO::getName, toolName)
                                    .eq(FunctionToolDefinitionPO::getToolType, "BUILTIN");

                    Long count = toolDefinitionMapper.selectCount(query);

                    // 4. 如果不存在，插入数据库
                    if (count == null || count == 0) {
                        FunctionToolDefinitionPO toolPO = FunctionToolDefinitionPO.builder()
                                .name(toolName)
                                .toolType("BUILTIN")
                                .description(tool.getDescription())
                                .parameters(jsonUtils.toJson(tool.getParametersSchema()))
                                .endpoint(null)  // 内置工具不需要 endpoint
                                .method(null)    // 内置工具不需要 method
                                .timeout(30000)
                                .retryCount(3)
                                .isActive(true)
                                .build();

                        toolDefinitionMapper.insert(toolPO);
                        syncCount++;
                        log.info("✓ 内置工具已同步到数据库: {} (ID: {})", toolName, toolPO.getId());
                    } else {
                        skipCount++;
                        log.debug("✓ 内置工具已存在于数据库，跳过: {}", toolName);
                    }

                } catch (Exception e) {
                    log.error("同步内置工具失败: {}, 错误: {}", tool.getName(), e.getMessage(), e);
                }
            }

            log.info("内置工具同步完成，新增: {}, 跳过: {}", syncCount, skipCount);

        } catch (Exception e) {
            log.error("内置工具同步过程发生异常", e);
        }
    }
}

