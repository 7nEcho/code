package pox.com.piteagents.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import pox.com.piteagents.entity.dto.request.AgentToolBindRequest;
import pox.com.piteagents.entity.dto.request.FunctionToolCreateRequest;
import pox.com.piteagents.entity.dto.request.FunctionToolUpdateRequest;
import pox.com.piteagents.entity.dto.response.AgentToolDTO;
import pox.com.piteagents.entity.dto.response.FunctionToolDefinitionDTO;
import pox.com.piteagents.entity.po.AgentPO;
import pox.com.piteagents.entity.po.AgentToolPO;
import pox.com.piteagents.entity.po.FunctionToolDefinitionPO;
import pox.com.piteagents.exception.ZhipuApiException;
import pox.com.piteagents.mapper.AgentMapper;
import pox.com.piteagents.mapper.AgentToolMapper;
import pox.com.piteagents.mapper.FunctionToolDefinitionMapper;
import pox.com.piteagents.service.IFunctionToolService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 工具管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FunctionToolServiceImpl implements IFunctionToolService {

    private final FunctionToolDefinitionMapper functionToolDefinitionMapper;
    private final AgentToolMapper agentToolMapper;
    private final AgentMapper agentMapper;
    private final ObjectMapper objectMapper;

    private static final int DEFAULT_TIMEOUT = 30000;
    private static final int DEFAULT_RETRY = 3;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FunctionToolDefinitionDTO createTool(FunctionToolCreateRequest request) {
        log.info("创建工具定义: {}", request.getName());

        // 名称唯一校验
        Long count = functionToolDefinitionMapper.selectCount(
                Wrappers.<FunctionToolDefinitionPO>lambdaQuery().eq(FunctionToolDefinitionPO::getName, request.getName())
        );
        if (count != null && count > 0) {
            throw new IllegalArgumentException("工具名称已存在: " + request.getName());
        }

        // 验证工具类型
        String toolType = request.getToolType();
        if (toolType == null || toolType.trim().isEmpty()) {
            toolType = "HTTP";
        }
        if (!"HTTP".equals(toolType) && !"BUILTIN".equals(toolType)) {
            throw new IllegalArgumentException("工具类型必须是 HTTP 或 BUILTIN");
        }

        // HTTP 工具必须有 endpoint
        if ("HTTP".equals(toolType) && !StringUtils.hasText(request.getEndpoint())) {
            throw new IllegalArgumentException("HTTP 工具必须指定 endpoint");
        }

        FunctionToolDefinitionPO tool = FunctionToolDefinitionPO.builder()
                .name(request.getName())
                .toolType(toolType)
                .description(request.getDescription())
                .endpoint(request.getEndpoint())
                .method(request.getMethod() != null ? request.getMethod().toUpperCase(Locale.ROOT) : "POST")
                .parameters(toJson(request.getParameters()))
                .headers(toJson(request.getHeaders()))
                .timeout(request.getTimeout() != null ? request.getTimeout() : DEFAULT_TIMEOUT)
                .retryCount(request.getRetryCount() != null ? request.getRetryCount() : DEFAULT_RETRY)
                .isActive(request.getIsActive() != null ? request.getIsActive() : Boolean.TRUE)
                .build();

        functionToolDefinitionMapper.insert(tool);
        log.info("工具创建成功，ID: {}", tool.getId());
        return convertToDTO(tool);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FunctionToolDefinitionDTO updateTool(Long id, FunctionToolUpdateRequest request) {
        log.info("更新工具定义，ID: {}", id);

        FunctionToolDefinitionPO tool = functionToolDefinitionMapper.selectById(id);
        if (tool == null) {
            throw new ZhipuApiException(404, "工具不存在，ID: " + id);
        }

        if (StringUtils.hasText(request.getName()) && !request.getName().equals(tool.getName())) {
            Long count = functionToolDefinitionMapper.selectCount(
                    Wrappers.<FunctionToolDefinitionPO>lambdaQuery()
                            .eq(FunctionToolDefinitionPO::getName, request.getName())
                            .ne(FunctionToolDefinitionPO::getId, id)
            );
            if (count != null && count > 0) {
                throw new IllegalArgumentException("工具名称已存在: " + request.getName());
            }
            tool.setName(request.getName());
        }

        if (request.getDescription() != null) {
            tool.setDescription(request.getDescription());
        }
        if (request.getEndpoint() != null) {
            tool.setEndpoint(request.getEndpoint());
        }
        if (request.getMethod() != null) {
            tool.setMethod(request.getMethod().toUpperCase(Locale.ROOT));
        }
        if (request.getParameters() != null) {
            tool.setParameters(toJson(request.getParameters()));
        }
        if (request.getHeaders() != null) {
            tool.setHeaders(toJson(request.getHeaders()));
        }
        if (request.getTimeout() != null) {
            tool.setTimeout(request.getTimeout());
        }
        if (request.getRetryCount() != null) {
            tool.setRetryCount(request.getRetryCount());
        }
        if (request.getIsActive() != null) {
            tool.setIsActive(request.getIsActive());
        }

        functionToolDefinitionMapper.updateById(tool);
        log.info("工具更新成功");
        return convertToDTO(tool);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTool(Long id) {
        log.info("删除工具定义，ID: {}", id);

        FunctionToolDefinitionPO tool = functionToolDefinitionMapper.selectById(id);
        if (tool == null) {
            throw new ZhipuApiException(404, "工具不存在，ID: " + id);
        }

        // 删除关联关系
        LambdaQueryWrapper<AgentToolPO> queryWrapper = Wrappers.<AgentToolPO>lambdaQuery()
                .eq(AgentToolPO::getToolId, id);
        agentToolMapper.delete(queryWrapper);

        functionToolDefinitionMapper.deleteById(id);
        log.info("工具删除成功");
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<FunctionToolDefinitionDTO> listTools(Boolean isActive, IPage<FunctionToolDefinitionDTO> page) {
        log.info("分页查询工具列表，isActive: {}, page: {}", isActive, page);

        // 构建查询条件
        LambdaQueryWrapper<FunctionToolDefinitionPO> queryWrapper = Wrappers.lambdaQuery();
        if (isActive != null) {
            queryWrapper.eq(FunctionToolDefinitionPO::getIsActive, isActive);
        }

        // 创建 MyBatis-Plus 分页对象
        Page<FunctionToolDefinitionPO> mpPage = new Page<>(page.getCurrent(), page.getSize());
        
        // 复制排序信息
        if (page.orders() != null && !page.orders().isEmpty()) {
            mpPage.addOrder(page.orders());
        }
        
        // 分页查询
        IPage<FunctionToolDefinitionPO> pageResult = functionToolDefinitionMapper.selectPage(mpPage, queryWrapper);

        // 转换为 DTO
        Page<FunctionToolDefinitionDTO> resultPage = new Page<>(pageResult.getCurrent(), pageResult.getSize(), pageResult.getTotal());
        resultPage.setRecords(pageResult.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
        
        // 复制排序信息到结果
        if (pageResult.orders() != null && !pageResult.orders().isEmpty()) {
            resultPage.addOrder(pageResult.orders());
        }
        
        return resultPage;
    }

    @Override
    @Transactional(readOnly = true)
    public FunctionToolDefinitionDTO getTool(Long id) {
        FunctionToolDefinitionPO tool = functionToolDefinitionMapper.selectById(id);
        if (tool == null) {
            throw new ZhipuApiException(404, "工具不存在，ID: " + id);
        }
        return convertToDTO(tool);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindAgentTools(Long agentId, AgentToolBindRequest request) {
        log.info("绑定 Agent 工具，agentId: {}, tools: {}", agentId, request.getToolIds());

        AgentPO agent = agentMapper.selectById(agentId);
        if (agent == null) {
            throw new ZhipuApiException(404, "Agent 不存在，ID: " + agentId);
        }

        if (CollectionUtils.isEmpty(request.getToolIds())) {
            throw new IllegalArgumentException("工具列表不能为空");
        }

        // 校验工具是否存在
        List<FunctionToolDefinitionPO> tools = functionToolDefinitionMapper.selectBatchIds(request.getToolIds());
        if (tools.size() != request.getToolIds().size()) {
            throw new IllegalArgumentException("存在无效的工具 ID");
        }

        // 现有关联
        List<AgentToolPO> existing = agentToolMapper.selectList(
                Wrappers.<AgentToolPO>lambdaQuery().eq(AgentToolPO::getAgentId, agentId)
        );
        Map<Long, AgentToolPO> existingMap = existing.stream()
                .collect(Collectors.toMap(AgentToolPO::getToolId, item -> item));

        // 删除未包含的关联
        agentToolMapper.delete(
                Wrappers.<AgentToolPO>lambdaQuery()
                        .eq(AgentToolPO::getAgentId, agentId)
                        .notIn(AgentToolPO::getToolId, request.getToolIds())
        );

        Map<String, AgentToolBindRequest.ToolBindingConfig> configs =
                request.getConfigs() != null ? request.getConfigs() : Collections.emptyMap();

        for (int index = 0; index < request.getToolIds().size(); index++) {
            Long toolId = request.getToolIds().get(index);
            AgentToolBindRequest.ToolBindingConfig config = configs.get(String.valueOf(toolId));

            Integer sortOrder = config != null && config.getSortOrder() != null
                    ? config.getSortOrder()
                    : index;
            Boolean enabled = config != null && config.getEnabled() != null
                    ? config.getEnabled()
                    : Boolean.TRUE;
            String toolConfigJson = config != null && config.getToolConfig() != null
                    ? toJson(config.getToolConfig())
                    : null;

            AgentToolPO existingRelation = existingMap.get(toolId);
            if (existingRelation != null) {
                existingRelation.setSortOrder(sortOrder);
                existingRelation.setIsEnabled(enabled);
                existingRelation.setToolConfig(toolConfigJson);

                LambdaUpdateWrapper<AgentToolPO> updateWrapper = Wrappers.<AgentToolPO>lambdaUpdate()
                        .eq(AgentToolPO::getAgentId, agentId)
                        .eq(AgentToolPO::getToolId, toolId);
                agentToolMapper.update(existingRelation, updateWrapper);
            } else {
                AgentToolPO relation = AgentToolPO.builder()
                        .agentId(agentId)
                        .toolId(toolId)
                        .sortOrder(sortOrder)
                        .isEnabled(enabled)
                        .toolConfig(toolConfigJson)
                        .build();
                agentToolMapper.insert(relation);
            }
        }

        log.info("Agent 工具绑定完成");
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentToolDTO> listAgentTools(Long agentId) {
        log.info("查询 Agent 工具列表，agentId: {}", agentId);

        // 确保 Agent 存在
        AgentPO agent = agentMapper.selectById(agentId);
        if (agent == null) {
            throw new ZhipuApiException(404, "Agent 不存在，ID: " + agentId);
        }

        List<AgentToolPO> relations = agentToolMapper.selectList(
                Wrappers.<AgentToolPO>lambdaQuery()
                        .eq(AgentToolPO::getAgentId, agentId)
                        .orderByAsc(AgentToolPO::getSortOrder)
        );
        if (relations.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> toolIds = relations.stream()
                .map(AgentToolPO::getToolId)
                .collect(Collectors.toSet());

        List<FunctionToolDefinitionPO> tools = functionToolDefinitionMapper.selectBatchIds(toolIds);
        Map<Long, FunctionToolDefinitionPO> toolMap = tools.stream()
                .collect(Collectors.toMap(FunctionToolDefinitionPO::getId, item -> item));

        return relations.stream()
                .map(relation -> {
                    FunctionToolDefinitionPO tool = toolMap.get(relation.getToolId());
                    if (tool == null) {
                        return null;
                    }
                    return AgentToolDTO.builder()
                            .toolId(tool.getId())
                            .name(tool.getName())
                            .description(tool.getDescription())
                            .toolType(tool.getToolType())
                            .endpoint(tool.getEndpoint())
                            .method(tool.getMethod())
                            .parameters(fromJsonToObjectMap(tool.getParameters()))
                            .headers(fromJsonToStringMap(tool.getHeaders()))
                            .sortOrder(relation.getSortOrder())
                            .enabled(relation.getIsEnabled())
                            .toolConfig(fromJsonToObjectMap(relation.getToolConfig()))
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private FunctionToolDefinitionDTO convertToDTO(FunctionToolDefinitionPO tool) {
        return FunctionToolDefinitionDTO.builder()
                .id(tool.getId())
                .name(tool.getName())
                .toolType(tool.getToolType())
                .description(tool.getDescription())
                .endpoint(tool.getEndpoint())
                .method(tool.getMethod())
                .parameters(fromJsonToObjectMap(tool.getParameters()))
                .headers(fromJsonToStringMap(tool.getHeaders()))
                .timeout(tool.getTimeout())
                .retryCount(tool.getRetryCount())
                .isActive(tool.getIsActive())
                .createdAt(tool.getCreatedAt())
                .updatedAt(tool.getUpdatedAt())
                .build();
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON 序列化失败: " + e.getMessage(), e);
        }
    }

    private Map<String, Object> fromJsonToObjectMap(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.warn("JSON 反序列化失败: {}", e.getMessage());
            return null;
        }
    }

    private Map<String, String> fromJsonToStringMap(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            log.warn("JSON 反序列化失败: {}", e.getMessage());
            return null;
        }
    }
}
