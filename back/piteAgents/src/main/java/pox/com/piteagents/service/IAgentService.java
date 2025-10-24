package pox.com.piteagents.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pox.com.piteagents.entity.dto.request.AgentCreateRequest;
import pox.com.piteagents.entity.dto.request.AgentUpdateRequest;
import pox.com.piteagents.entity.dto.response.AgentConfigDTO;
import pox.com.piteagents.entity.dto.response.AgentDTO;
import pox.com.piteagents.entity.po.AgentConfigPO;

/**
 * Agent 服务接口
 * <p>
 * 定义 Agent 管理的业务接口。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public interface IAgentService {

    /**
     * 创建 Agent
     *
     * @param request 创建请求
     * @return Agent DTO
     */
    AgentDTO createAgent(AgentCreateRequest request);

    /**
     * 更新 Agent
     *
     * @param id Agent ID
     * @param request 更新请求
     * @return Agent DTO
     */
    AgentDTO updateAgent(Long id, AgentUpdateRequest request);

    /**
     * 删除 Agent（软删除）
     *
     * @param id Agent ID
     */
    void deleteAgent(Long id);

    /**
     * 获取 Agent 详情
     *
     * @param id Agent ID
     * @return Agent DTO
     */
    AgentDTO getAgent(Long id);

    /**
     * 查询 Agent 列表
     *
     * @param category 分类（可选）
     * @param status 状态（可选）
     * @param keyword 关键词（可选）
     * @param pageable 分页参数
     * @return Agent 分页列表
     */
    Page<AgentDTO> listAgents(String category, String status, String keyword, Pageable pageable);

    /**
     * 更新 Agent 配置
     *
     * @param agentId Agent ID
     * @param configDTO 配置 DTO
     * @return Agent 配置 DTO
     */
    AgentConfigDTO updateAgentConfig(Long agentId, AgentConfigDTO configDTO);

    /**
     * 切换 Agent 状态
     *
     * @param id Agent ID
     * @param status 新状态
     */
    void updateAgentStatus(Long id, String status);

    /**
     * 获取 Agent 配置
     *
     * @param agentId Agent ID
     * @return Agent 配置
     */
    AgentConfigPO getAgentConfig(Long agentId);
}

