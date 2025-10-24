package pox.com.piteagents.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pox.com.piteagents.entity.dto.request.AgentCreateRequest;
import pox.com.piteagents.entity.dto.request.AgentUpdateRequest;
import pox.com.piteagents.entity.dto.response.AgentConfigDTO;
import pox.com.piteagents.entity.dto.response.AgentDTO;
import pox.com.piteagents.entity.po.AgentConfigPO;
import pox.com.piteagents.entity.po.AgentPO;
import pox.com.piteagents.exception.ZhipuApiException;
import pox.com.piteagents.mapper.AgentConfigMapper;
import pox.com.piteagents.mapper.AgentMapper;
import pox.com.piteagents.service.IAgentService;

/**
 * Agent 服务实现类
 * <p>
 * 提供 Agent 的 CRUD 和业务逻辑处理的具体实现。
 * 使用 MyBatis-Plus 进行数据访问。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements IAgentService {

    private final AgentMapper agentMapper;
    private final AgentConfigMapper agentConfigMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AgentDTO createAgent(AgentCreateRequest request) {
        log.info("创建 Agent: {}", request.getName());

        // 检查名称是否已存在
        Long count = agentMapper.selectCount(
                Wrappers.<AgentPO>lambdaQuery().eq(AgentPO::getName, request.getName())
        );
        if (count > 0) {
            throw new IllegalArgumentException("Agent 名称已存在: " + request.getName());
        }

        // 创建 Agent 实体
        AgentPO agent = AgentPO.builder()
                .name(request.getName())
                .description(request.getDescription())
                .avatar(request.getAvatar())
                .category(request.getCategory())
                .systemPrompt(request.getSystemPrompt())
                .rolePrompt(request.getRolePrompt())
                .status("ACTIVE")
                .isActive(true)
                .build();

        // 保存 Agent
        agentMapper.insert(agent);
        log.info("Agent 创建成功，ID: {}", agent.getId());

        // 创建 Agent 配置
        AgentConfigDTO configDTO = request.getConfig();
        if (configDTO != null) {
            AgentConfigPO config = AgentConfigPO.builder()
                    .agentId(agent.getId())
                    .model(configDTO.getModel() != null ? configDTO.getModel() : "glm-4.6")
                    .temperature(configDTO.getTemperature() != null ? configDTO.getTemperature() : 0.7)
                    .maxTokens(configDTO.getMaxTokens() != null ? configDTO.getMaxTokens() : 2000)
                    .topP(configDTO.getTopP())
                    .extraParams(configDTO.getExtraParams())
                    .build();

            agentConfigMapper.insert(config);
            agent.setConfig(config);
            log.info("Agent 配置创建成功");
        }

        return convertToDTO(agent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AgentDTO updateAgent(Long id, AgentUpdateRequest request) {
        log.info("更新 Agent，ID: {}", id);

        // 查找 Agent
        AgentPO agent = agentMapper.selectById(id);
        if (agent == null) {
            throw new ZhipuApiException(404, "Agent 不存在，ID: " + id);
        }

        // 检查名称是否与其他 Agent 重复
        if (request.getName() != null && !request.getName().equals(agent.getName())) {
            Long count = agentMapper.selectCount(
                    Wrappers.<AgentPO>lambdaQuery()
                            .eq(AgentPO::getName, request.getName())
                            .ne(AgentPO::getId, id)
            );
            if (count > 0) {
                throw new IllegalArgumentException("Agent 名称已存在: " + request.getName());
            }
            agent.setName(request.getName());
        }

        // 更新字段
        if (request.getDescription() != null) {
            agent.setDescription(request.getDescription());
        }
        if (request.getAvatar() != null) {
            agent.setAvatar(request.getAvatar());
        }
        if (request.getCategory() != null) {
            agent.setCategory(request.getCategory());
        }
        if (request.getStatus() != null) {
            agent.setStatus(request.getStatus());
        }
        if (request.getSystemPrompt() != null) {
            agent.setSystemPrompt(request.getSystemPrompt());
        }
        if (request.getRolePrompt() != null) {
            agent.setRolePrompt(request.getRolePrompt());
        }

        agentMapper.updateById(agent);
        log.info("Agent 更新成功");

        return convertToDTO(agent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAgent(Long id) {
        log.info("删除 Agent，ID: {}", id);

        AgentPO agent = agentMapper.selectById(id);
        if (agent == null) {
            throw new ZhipuApiException(404, "Agent 不存在，ID: " + id);
        }

        // MyBatis-Plus 会自动执行逻辑删除（设置 deletedAt）
        agentMapper.deleteById(id);
        log.info("Agent 删除成功");
    }

    @Override
    @Transactional(readOnly = true)
    public AgentDTO getAgent(Long id) {
        log.info("查询 Agent 详情，ID: {}", id);

        // 使用自定义方法查询 Agent 及其配置
        AgentPO agent = agentMapper.selectAgentWithConfig(id);
        if (agent == null) {
            throw new ZhipuApiException(404, "Agent 不存在，ID: " + id);
        }

        return convertToDTO(agent);
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<AgentDTO> listAgents(String category, String status, String keyword, org.springframework.data.domain.Pageable pageable) {
        log.info("查询 Agent 列表，category: {}, status: {}, keyword: {}", category, status, keyword);

        // 构建查询条件
        LambdaQueryWrapper<AgentPO> queryWrapper = Wrappers.lambdaQuery();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 关键词搜索
            queryWrapper.and(wrapper -> wrapper
                    .like(AgentPO::getName, keyword)
                    .or()
                    .like(AgentPO::getDescription, keyword)
            );
        }
        
        if (category != null && !category.trim().isEmpty()) {
            queryWrapper.eq(AgentPO::getCategory, category);
        }
        
        if (status != null && !status.trim().isEmpty()) {
            queryWrapper.eq(AgentPO::getStatus, status);
        }
        
        // 排序
        queryWrapper.orderByDesc(AgentPO::getCreatedAt);

        // 分页查询
        Page<AgentPO> mpPage = new Page<>(pageable.getPageNumber() + 1, pageable.getPageSize());
        IPage<AgentPO> agentPage = agentMapper.selectPage(mpPage, queryWrapper);

        // 转换为 Spring Data Page
        return convertToSpringPage(agentPage, pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AgentConfigDTO updateAgentConfig(Long agentId, AgentConfigDTO configDTO) {
        log.info("更新 Agent 配置，Agent ID: {}", agentId);

        // 查找 Agent
        AgentPO agent = agentMapper.selectById(agentId);
        if (agent == null) {
            throw new ZhipuApiException(404, "Agent 不存在，ID: " + agentId);
        }

        // 查找或创建配置
        AgentConfigPO config = agentConfigMapper.selectByAgentId(agentId);
        if (config == null) {
            // 创建新配置
            config = AgentConfigPO.builder()
                    .agentId(agentId)
                    .model(configDTO.getModel() != null ? configDTO.getModel() : "glm-4.6")
                    .temperature(configDTO.getTemperature() != null ? configDTO.getTemperature() : 0.7)
                    .maxTokens(configDTO.getMaxTokens() != null ? configDTO.getMaxTokens() : 2000)
                    .topP(configDTO.getTopP())
                    .extraParams(configDTO.getExtraParams())
                    .build();
            agentConfigMapper.insert(config);
        } else {
            // 更新现有配置
            if (configDTO.getModel() != null) {
                config.setModel(configDTO.getModel());
            }
            if (configDTO.getTemperature() != null) {
                config.setTemperature(configDTO.getTemperature());
            }
            if (configDTO.getMaxTokens() != null) {
                config.setMaxTokens(configDTO.getMaxTokens());
            }
            if (configDTO.getTopP() != null) {
                config.setTopP(configDTO.getTopP());
            }
            if (configDTO.getExtraParams() != null) {
                config.setExtraParams(configDTO.getExtraParams());
            }
            agentConfigMapper.updateById(config);
        }

        log.info("Agent 配置更新成功");
        return convertConfigToDTO(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAgentStatus(Long id, String status) {
        log.info("更新 Agent 状态，ID: {}, 新状态: {}", id, status);

        AgentPO agent = agentMapper.selectById(id);
        if (agent == null) {
            throw new ZhipuApiException(404, "Agent 不存在，ID: " + id);
        }

        agent.setStatus(status);
        agent.setIsActive("ACTIVE".equals(status));
        agentMapper.updateById(agent);

        log.info("Agent 状态更新成功");
    }

    @Override
    @Transactional(readOnly = true)
    public AgentConfigPO getAgentConfig(Long agentId) {
        AgentConfigPO config = agentConfigMapper.selectByAgentId(agentId);
        if (config == null) {
            throw new ZhipuApiException(404, "Agent 配置不存在，Agent ID: " + agentId);
        }
        return config;
    }

    /**
     * 将 Agent 实体转换为 DTO
     *
     * @param agent Agent 实体
     * @return Agent DTO
     */
    private AgentDTO convertToDTO(AgentPO agent) {
        AgentDTO dto = AgentDTO.builder()
                .id(agent.getId())
                .name(agent.getName())
                .description(agent.getDescription())
                .avatar(agent.getAvatar())
                .category(agent.getCategory())
                .status(agent.getStatus())
                .systemPrompt(agent.getSystemPrompt())
                .rolePrompt(agent.getRolePrompt())
                .isActive(agent.getIsActive())
                .createdAt(agent.getCreatedAt())
                .updatedAt(agent.getUpdatedAt())
                .build();

        // 加载配置信息
        if (agent.getConfig() != null) {
            dto.setConfig(convertConfigToDTO(agent.getConfig()));
        } else {
            AgentConfigPO config = agentConfigMapper.selectByAgentId(agent.getId());
            if (config != null) {
                dto.setConfig(convertConfigToDTO(config));
            }
        }

        return dto;
    }

    /**
     * 将 AgentConfig 实体转换为 DTO
     *
     * @param config AgentConfig 实体
     * @return AgentConfigDTO
     */
    private AgentConfigDTO convertConfigToDTO(AgentConfigPO config) {
        return AgentConfigDTO.builder()
                .model(config.getModel())
                .temperature(config.getTemperature())
                .maxTokens(config.getMaxTokens())
                .topP(config.getTopP())
                .extraParams(config.getExtraParams())
                .build();
    }

    /**
     * 将 MyBatis-Plus Page 转换为 Spring Data Page
     *
     * @param mpPage MyBatis-Plus 分页对象
     * @param pageable Spring Data 分页参数
     * @return Spring Data Page 对象
     */
    private org.springframework.data.domain.Page<AgentDTO> convertToSpringPage(
            IPage<AgentPO> mpPage, 
            org.springframework.data.domain.Pageable pageable) {
        
        java.util.List<AgentDTO> content = mpPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(java.util.stream.Collectors.toList());
        
        return new org.springframework.data.domain.PageImpl<>(
                content,
                pageable,
                mpPage.getTotal()
        );
    }
}
