package pox.com.piteagents.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import pox.com.piteagents.entity.dto.common.ApiResponse;
import pox.com.piteagents.entity.dto.response.ConversationMessageDTO;
import pox.com.piteagents.entity.dto.response.ConversationSessionDTO;
import pox.com.piteagents.service.IConversationService;

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

    /**
     * 获取会话列表
     *
     * @param agentId Agent ID（可选）
     * @param page 页码（默认0）
     * @param size 每页大小（默认20）
     * @return 会话分页列表
     */
    @GetMapping
    public ApiResponse<Page<ConversationSessionDTO>> listSessions(
            @RequestParam(required = false) Long agentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("查询会话列表，agentId: {}, page: {}, size: {}", agentId, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<ConversationSessionDTO> sessions = conversationService.listSessions(agentId, pageable);
        
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
     * @param page 页码（默认0）
     * @param size 每页大小（默认50）
     * @return 消息分页列表
     */
    @GetMapping("/{id}/messages")
    public ApiResponse<Page<ConversationMessageDTO>> getSessionMessages(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        log.info("查询会话消息，会话ID: {}, page: {}, size: {}", id, page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<ConversationMessageDTO> messages = conversationService.getSessionMessages(id, pageable);
        
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

