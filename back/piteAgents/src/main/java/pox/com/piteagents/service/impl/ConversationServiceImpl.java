package pox.com.piteagents.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pox.com.piteagents.entity.dto.common.Message;
import pox.com.piteagents.entity.dto.response.ConversationMessageDTO;
import pox.com.piteagents.entity.dto.response.ConversationSessionDTO;
import pox.com.piteagents.entity.po.AgentPO;
import pox.com.piteagents.entity.po.ConversationMessagePO;
import pox.com.piteagents.entity.po.ConversationSessionPO;
import pox.com.piteagents.exception.ZhipuApiException;
import pox.com.piteagents.mapper.AgentMapper;
import pox.com.piteagents.mapper.ConversationMessageMapper;
import pox.com.piteagents.mapper.ConversationSessionMapper;
import pox.com.piteagents.service.IConversationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 对话服务实现类
 * <p>
 * 提供会话和消息管理功能的具体实现。
 * 使用 MyBatis-Plus 进行数据访问。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements IConversationService {

    private final ConversationSessionMapper sessionMapper;
    private final ConversationMessageMapper messageMapper;
    private final AgentMapper agentMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConversationSessionDTO createSession(Long agentId, String title) {
        log.info("创建新会话，Agent ID: {}, 标题: {}", agentId, title);

        // 验证 Agent 是否存在
        AgentPO agent = agentMapper.selectById(agentId);
        if (agent == null) {
            throw new ZhipuApiException(404, "Agent 不存在，ID: " + agentId);
        }

        // 创建会话
        ConversationSessionPO session = ConversationSessionPO.builder()
                .agentId(agentId)
                .title(title != null ? title : "新对话")
                .build();

        sessionMapper.insert(session);
        log.info("会话创建成功，ID: {}", session.getId());

        return convertSessionToDTO(session, agent.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ConversationMessageDTO saveMessage(Long sessionId, String role, String content,
                                              Integer promptTokens, Integer completionTokens,
                                              Integer totalTokens) {
        log.debug("保存消息到会话，会话ID: {}, 角色: {}", sessionId, role);

        // 查找会话
        ConversationSessionPO session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new ZhipuApiException(404, "会话不存在，ID: " + sessionId);
        }

        // 创建消息
        ConversationMessagePO message = ConversationMessagePO.builder()
                .sessionId(sessionId)
                .role(role)
                .content(content)
                .promptTokens(promptTokens != null ? promptTokens : 0)
                .completionTokens(completionTokens != null ? completionTokens : 0)
                .totalTokens(totalTokens != null ? totalTokens : 0)
                .build();

        messageMapper.insert(message);
        log.debug("消息保存成功，ID: {}", message.getId());

        return convertMessageToDTO(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getSessionHistory(Long sessionId, Integer limit) {
        log.debug("获取会话历史，会话ID: {}, 限制数量: {}", sessionId, limit);

        // 验证会话是否存在
        Long count = sessionMapper.selectCount(
                Wrappers.<ConversationSessionPO>lambdaQuery().eq(ConversationSessionPO::getId, sessionId)
        );
        if (count == 0) {
            throw new ZhipuApiException(404, "会话不存在，ID: " + sessionId);
        }

        List<ConversationMessagePO> messages;
        if (limit != null && limit > 0) {
            // 获取最近的 N 条消息
            messages = messageMapper.selectRecentMessages(sessionId, limit);
            // 反转顺序，按时间升序
            messages = messages.stream()
                    .sorted((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()))
                    .collect(Collectors.toList());
        } else {
            // 获取全部消息
            messages = messageMapper.selectBySessionId(sessionId);
        }

        return messages.stream()
                .map(m -> Message.builder()
                        .role(m.getRole())
                        .content(m.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<ConversationSessionDTO> listSessions(Long agentId, IPage<ConversationSessionDTO> page) {
        log.info("查询会话列表，Agent ID: {}", agentId);

        // 构建查询条件
        LambdaQueryWrapper<ConversationSessionPO> queryWrapper = Wrappers.lambdaQuery();
        if (agentId != null) {
            queryWrapper.eq(ConversationSessionPO::getAgentId, agentId);
        }

        // 创建 MyBatis-Plus 分页对象
        Page<ConversationSessionPO> mpPage = new Page<>(page.getCurrent(), page.getSize());
        
        // 复制排序信息
        if (page.orders() != null && !page.orders().isEmpty()) {
            mpPage.addOrder(page.orders());
        }
        
        // 分页查询
        IPage<ConversationSessionPO> sessionPage = sessionMapper.selectPage(mpPage, queryWrapper);

        // 查询所有涉及的 Agent ID 并批量加载 Agent 名称
        List<Long> agentIds = sessionPage.getRecords().stream()
                .map(ConversationSessionPO::getAgentId)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, String> agentNameMap = new HashMap<>();
        if (!agentIds.isEmpty()) {
            List<AgentPO> agents = agentMapper.selectBatchIds(agentIds);
            agentNameMap = agents.stream()
                    .collect(Collectors.toMap(AgentPO::getId, AgentPO::getName));
        }
        
        // 转换为 DTO
        Map<Long, String> finalAgentNameMap = agentNameMap;
        Page<ConversationSessionDTO> resultPage = new Page<>(sessionPage.getCurrent(), sessionPage.getSize(), sessionPage.getTotal());
        resultPage.setRecords(sessionPage.getRecords().stream()
                .map(session -> convertSessionToDTO(session, finalAgentNameMap.get(session.getAgentId())))
                .collect(Collectors.toList()));
        
        // 复制排序信息到结果
        if (sessionPage.orders() != null && !sessionPage.orders().isEmpty()) {
            resultPage.addOrder(sessionPage.orders());
        }
        
        return resultPage;
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationSessionDTO getSession(Long sessionId) {
        log.info("查询会话详情，会话ID: {}", sessionId);

        // 使用自定义方法查询会话及 Agent 名称
        ConversationSessionPO session = sessionMapper.selectSessionWithAgent(sessionId);
        if (session == null) {
            throw new ZhipuApiException(404, "会话不存在，ID: " + sessionId);
        }

        return convertSessionToDTO(session, session.getAgentName());
    }

    @Override
    @Transactional(readOnly = true)
    public IPage<ConversationMessageDTO> getSessionMessages(Long sessionId, IPage<ConversationMessageDTO> page) {
        log.info("查询会话消息，会话ID: {}", sessionId);

        // 验证会话是否存在
        Long count = sessionMapper.selectCount(
                Wrappers.<ConversationSessionPO>lambdaQuery().eq(ConversationSessionPO::getId, sessionId)
        );
        if (count == 0) {
            throw new ZhipuApiException(404, "会话不存在，ID: " + sessionId);
        }

        // 构建查询条件
        LambdaQueryWrapper<ConversationMessagePO> queryWrapper = Wrappers.<ConversationMessagePO>lambdaQuery()
                .eq(ConversationMessagePO::getSessionId, sessionId);

        // 创建 MyBatis-Plus 分页对象
        Page<ConversationMessagePO> mpPage = new Page<>(page.getCurrent(), page.getSize());
        
        // 复制排序信息
        if (page.orders() != null && !page.orders().isEmpty()) {
            mpPage.addOrder(page.orders());
        }
        
        // 分页查询消息
        IPage<ConversationMessagePO> messagePage = messageMapper.selectPage(mpPage, queryWrapper);

        // 转换为 DTO
        Page<ConversationMessageDTO> resultPage = new Page<>(messagePage.getCurrent(), messagePage.getSize(), messagePage.getTotal());
        resultPage.setRecords(messagePage.getRecords().stream()
                .map(this::convertMessageToDTO)
                .collect(Collectors.toList()));
        
        // 复制排序信息到结果
        if (messagePage.orders() != null && !messagePage.orders().isEmpty()) {
            resultPage.addOrder(messagePage.orders());
        }
        
        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSession(Long sessionId) {
        log.info("删除会话，会话ID: {}", sessionId);

        ConversationSessionPO session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new ZhipuApiException(404, "会话不存在，ID: " + sessionId);
        }

        // MyBatis-Plus 会自动执行逻辑删除
        sessionMapper.deleteById(sessionId);
        log.info("会话删除成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSessionTitle(Long sessionId, String title) {
        log.info("更新会话标题，会话ID: {}, 新标题: {}", sessionId, title);

        ConversationSessionPO session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new ZhipuApiException(404, "会话不存在，ID: " + sessionId);
        }

        session.setTitle(title);
        sessionMapper.updateById(session);
        log.info("会话标题更新成功");
    }

    @Override
    public String generateSessionTitle(String content) {
        if (content == null || content.trim().isEmpty()) {
            return "新对话";
        }

        // 取前 30 个字符作为标题
        String title = content.trim();
        if (title.length() > 30) {
            title = title.substring(0, 30) + "...";
        }

        return title;
    }

    /**
     * 将会话实体转换为 DTO
     *
     * @param session 会话实体
     * @param agentName Agent 名称
     * @return 会话 DTO
     */
    private ConversationSessionDTO convertSessionToDTO(ConversationSessionPO session, String agentName) {
        return ConversationSessionDTO.builder()
                .id(session.getId())
                .agentId(session.getAgentId())
                .agentName(agentName)
                .title(session.getTitle())
                .summary(session.getSummary())
                .messageCount(session.getMessageCount())
                .totalTokens(session.getTotalTokens())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    /**
     * 将消息实体转换为 DTO
     *
     * @param message 消息实体
     * @return 消息 DTO
     */
    private ConversationMessageDTO convertMessageToDTO(ConversationMessagePO message) {
        return ConversationMessageDTO.builder()
                .id(message.getId())
                .sessionId(message.getSessionId())
                .role(message.getRole())
                .content(message.getContent())
                .promptTokens(message.getPromptTokens())
                .completionTokens(message.getCompletionTokens())
                .totalTokens(message.getTotalTokens())
                .createdAt(message.getCreatedAt())
                .build();
    }

}
