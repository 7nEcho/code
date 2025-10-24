package pox.com.piteagents.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import pox.com.piteagents.entity.dto.common.ApiResponse;
import pox.com.piteagents.entity.dto.request.AgentCreateRequest;
import pox.com.piteagents.entity.dto.request.AgentUpdateRequest;
import pox.com.piteagents.entity.dto.response.AgentConfigDTO;
import pox.com.piteagents.entity.dto.response.AgentDTO;
import pox.com.piteagents.service.IAgentService;

/**
 * Agent 控制器
 * <p>
 * 提供 Agent 管理相关的 REST API 接口。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/agents")
@RequiredArgsConstructor
public class AgentController {

    private final IAgentService agentService;

    /**
     * 创建 Agent
     *
     * @param request 创建请求
     * @return Agent 详情
     */
    @PostMapping
    public ApiResponse<AgentDTO> createAgent(@Valid @RequestBody AgentCreateRequest request) {
        log.info("收到创建 Agent 请求: {}", request.getName());
        AgentDTO agent = agentService.createAgent(request);
        return ApiResponse.success(agent);
    }

    /**
     * 查询 Agent 列表
     *
     * @param category 分类（可选）
     * @param status 状态（可选）
     * @param keyword 关键词（可选）
     * @param page 页码（默认0）
     * @param size 每页大小（默认10）
     * @return Agent 分页列表
     */
    @GetMapping
    public ApiResponse<Page<AgentDTO>> listAgents(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        log.info("查询 Agent 列表，category: {}, status: {}, keyword: {}, page: {}, size: {}", 
                 category, status, keyword, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AgentDTO> agents = agentService.listAgents(category, status, keyword, pageable);
        
        return ApiResponse.success(agents);
    }

    /**
     * 获取 Agent 详情
     *
     * @param id Agent ID
     * @return Agent 详情
     */
    @GetMapping("/{id}")
    public ApiResponse<AgentDTO> getAgent(@PathVariable Long id) {
        log.info("查询 Agent 详情，ID: {}", id);
        AgentDTO agent = agentService.getAgent(id);
        return ApiResponse.success(agent);
    }

    /**
     * 更新 Agent
     *
     * @param id Agent ID
     * @param request 更新请求
     * @return Agent 详情
     */
    @PutMapping("/{id}")
    public ApiResponse<AgentDTO> updateAgent(@PathVariable Long id, 
                                             @Valid @RequestBody AgentUpdateRequest request) {
        log.info("更新 Agent，ID: {}", id);
        AgentDTO agent = agentService.updateAgent(id, request);
        return ApiResponse.success(agent);
    }

    /**
     * 删除 Agent
     *
     * @param id Agent ID
     * @return 成功消息
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAgent(@PathVariable Long id) {
        log.info("删除 Agent，ID: {}", id);
        agentService.deleteAgent(id);
        return ApiResponse.success("Agent 删除成功");
    }

    /**
     * 更新 Agent 配置
     *
     * @param id Agent ID
     * @param configDTO 配置信息
     * @return Agent 配置
     */
    @PutMapping("/{id}/config")
    public ApiResponse<AgentConfigDTO> updateAgentConfig(@PathVariable Long id, 
                                                          @Valid @RequestBody AgentConfigDTO configDTO) {
        log.info("更新 Agent 配置，ID: {}", id);
        AgentConfigDTO config = agentService.updateAgentConfig(id, configDTO);
        return ApiResponse.success(config);
    }

    /**
     * 更新 Agent 状态
     *
     * @param id Agent ID
     * @param statusRequest 状态请求
     * @return 成功消息
     */
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateAgentStatus(@PathVariable Long id, 
                                               @RequestBody StatusRequest statusRequest) {
        log.info("更新 Agent 状态，ID: {}, 新状态: {}", id, statusRequest.getStatus());
        agentService.updateAgentStatus(id, statusRequest.getStatus());
        return ApiResponse.success("状态更新成功");
    }

    /**
     * 状态更新请求对象
     */
    @lombok.Data
    public static class StatusRequest {
        private String status;
    }
}

