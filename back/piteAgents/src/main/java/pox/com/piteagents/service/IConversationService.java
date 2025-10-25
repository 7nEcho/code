package pox.com.piteagents.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pox.com.piteagents.entity.dto.common.Message;
import pox.com.piteagents.entity.dto.response.ConversationMessageDTO;
import pox.com.piteagents.entity.dto.response.ConversationSessionDTO;

import java.util.List;

/**
 * 对话服务接口
 * <p>
 * 定义会话和消息管理的业务接口。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public interface IConversationService {

    /**
     * 创建新会话
     *
     * @param agentId Agent ID
     * @param title 会话标题
     * @return 会话 DTO
     */
    ConversationSessionDTO createSession(Long agentId, String title);

    /**
     * 保存消息到会话
     *
     * @param sessionId 会话 ID
     * @param role 角色
     * @param content 内容
     * @param promptTokens 提示词 Token 数
     * @param completionTokens 生成内容 Token 数
     * @param totalTokens 总 Token 数
     * @return 消息 DTO
     */
    ConversationMessageDTO saveMessage(Long sessionId, String role, String content,
                                       Integer promptTokens, Integer completionTokens,
                                       Integer totalTokens);

    /**
     * 获取会话历史消息
     *
     * @param sessionId 会话 ID
     * @param limit 限制数量（获取最近 N 条）
     * @return 消息列表
     */
    List<Message> getSessionHistory(Long sessionId, Integer limit);

    /**
     * 获取会话列表
     *
     * @param agentId Agent ID（可选）
     * @param page 分页参数
     * @return 会话分页列表
     */
    IPage<ConversationSessionDTO> listSessions(Long agentId, IPage<ConversationSessionDTO> page);

    /**
     * 获取会话详情
     *
     * @param sessionId 会话 ID
     * @return 会话 DTO
     */
    ConversationSessionDTO getSession(Long sessionId);

    /**
     * 获取会话消息列表
     *
     * @param sessionId 会话 ID
     * @param page 分页参数
     * @return 消息分页列表
     */
    IPage<ConversationMessageDTO> getSessionMessages(Long sessionId, IPage<ConversationMessageDTO> page);

    /**
     * 删除会话
     *
     * @param sessionId 会话 ID
     */
    void deleteSession(Long sessionId);

    /**
     * 更新会话标题
     *
     * @param sessionId 会话 ID
     * @param title 新标题
     */
    void updateSessionTitle(Long sessionId, String title);

    /**
     * 根据第一条用户消息生成会话标题
     *
     * @param content 用户消息内容
     * @return 会话标题
     */
    String generateSessionTitle(String content);
}

