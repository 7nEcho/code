package pox.com.piteagents.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pox.com.piteagents.entity.dto.request.AgentToolBindRequest;
import pox.com.piteagents.entity.dto.request.ToolCreateRequest;
import pox.com.piteagents.entity.dto.request.ToolUpdateRequest;
import pox.com.piteagents.entity.dto.response.AgentToolDTO;
import pox.com.piteagents.entity.dto.response.ToolDefinitionDTO;

import java.util.List;

/**
 * 工具管理服务接口
 */
public interface IToolService {

    /**
     * 创建工具定义
     *
     * @param request 请求参数
     * @return 工具定义
     */
    ToolDefinitionDTO createTool(ToolCreateRequest request);

    /**
     * 更新工具定义
     *
     * @param id 工具 ID
     * @param request 请求参数
     * @return 工具定义
     */
    ToolDefinitionDTO updateTool(Long id, ToolUpdateRequest request);

    /**
     * 删除工具
     *
     * @param id 工具 ID
     */
    void deleteTool(Long id);

    /**
     * 分页查询工具
     *
     * @param isActive 是否启用
     * @param pageable 分页参数
     * @return 工具定义分页
     */
    Page<ToolDefinitionDTO> listTools(Boolean isActive, Pageable pageable);

    /**
     * 查询单个工具
     *
     * @param id 工具 ID
     * @return 工具定义
     */
    ToolDefinitionDTO getTool(Long id);

    /**
     * 批量绑定 Agent 的工具列表
     *
     * @param agentId Agent ID
     * @param request 请求参数
     */
    void bindAgentTools(Long agentId, AgentToolBindRequest request);

    /**
     * 查询 Agent 的工具列表
     *
     * @param agentId Agent ID
     * @return 工具列表
     */
    List<AgentToolDTO> listAgentTools(Long agentId);
}
