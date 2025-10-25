package pox.com.piteagents.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;
import pox.com.piteagents.entity.dto.common.ApiResponse;
import pox.com.piteagents.entity.dto.response.ConversationMessageDTO;
import pox.com.piteagents.entity.dto.response.ConversationSessionDTO;
import pox.com.piteagents.service.IConversationService;
import pox.com.piteagents.common.utils.PaginationUtils;

/**
 * 对话历史控制器
 * <p>
 * 提供会话和消息历史查询的 REST API 接口。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class ConversationController {

    private final IConversationService conversationService;
    private final PaginationUtils paginationUtils;

    /**
     * 获取会话列表
     *
     * @param agentId Agent ID（可选）
     * @param page 页码（默认1）
     * @param size 每页大小（使用配置的默认值）
     * @param sortField 排序字段（可选）
     * @param sortDirection 排序方向（可选）
     * @return 会话分页列表
     */
    @GetMapping
    public ApiResponse<IPage<ConversationSessionDTO>> listSessions(
            @RequestParam(required = false) Long agentId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection) {
        
        log.info("查询会话列表，agentId: {}, page: {}, size: {}, sortField: {}, sortDirection: {}", 
                agentId, page, size, sortField, sortDirection);
        
        // 使用分页工具类创建分页对象，默认按更新时间降序排列
        String defaultSortField = sortField != null ? sortField : "updatedAt";
        IPage<ConversationSessionDTO> pageParam = paginationUtils.createPage(page, size, defaultSortField, sortDirection);
        IPage<ConversationSessionDTO> sessions = conversationService.listSessions(agentId, pageParam);
        
        paginationUtils.logPaginationInfo(sessions, "查询会话列表");
        
        return ApiResponse.success(sessions);
    }

    /**
     * 获取会话详情
     *
     * @param id 会话 ID
     * @return 会话详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ConversationSessionDTO> getSession(@PathVariable Long id) {
        log.info("查询会话详情，ID: {}", id);
        ConversationSessionDTO session = conversationService.getSession(id);
        return ApiResponse.success(session);
    }

    /**
     * 获取会话消息历史
     *
     * @param id 会话 ID
     * @param page 页码（默认1）
     * @param size 每页大小（使用配置的默认值）
     * @param sortField 排序字段（可选）
     * @param sortDirection 排序方向（可选）
     * @return 消息分页列表
     */
    @GetMapping("/{id}/messages")
    public ApiResponse<IPage<ConversationMessageDTO>> getSessionMessages(
            @PathVariable Long id,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection) {
        
        log.info("查询会话消息，会话ID: {}, page: {}, size: {}, sortField: {}, sortDirection: {}", 
                id, page, size, sortField, sortDirection);
        
        // 使用分页工具类创建分页对象，默认按创建时间升序排列（消息的时间顺序）
        String defaultSortField = sortField != null ? sortField : "createdAt";
        String defaultSortDirection = sortDirection != null ? sortDirection : "ASC";
        IPage<ConversationMessageDTO> pageParam = paginationUtils.createPage(page, size, defaultSortField, defaultSortDirection);
        IPage<ConversationMessageDTO> messages = conversationService.getSessionMessages(id, pageParam);
        
        paginationUtils.logPaginationInfo(messages, "查询会话消息");
        
        return ApiResponse.success(messages);
    }

    /**
     * 删除会话
     *
     * @param id 会话 ID
     * @return 成功消息
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSession(@PathVariable Long id) {
        log.info("删除会话，ID: {}", id);
        conversationService.deleteSession(id);
        return ApiResponse.success("会话删除成功");
    }

    /**
     * 更新会话标题
     *
     * @param id 会话 ID
     * @param titleRequest 标题请求
     * @return 成功消息
     */
    @PutMapping("/{id}/title")
    public ApiResponse<Void> updateSessionTitle(@PathVariable Long id, 
                                                @RequestBody TitleRequest titleRequest) {
        log.info("更新会话标题，ID: {}, 新标题: {}", id, titleRequest.getTitle());
        conversationService.updateSessionTitle(id, titleRequest.getTitle());
        return ApiResponse.success("标题更新成功");
    }

    /**
     * 标题更新请求对象
     */
    @lombok.Data
    public static class TitleRequest {
        private String title;
    }
}

