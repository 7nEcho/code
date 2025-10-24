package pox.com.piteagents.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import pox.com.piteagents.entity.po.ConversationMessagePO;

import java.util.List;

/**
 * 消息记录 Mapper 接口
 * <p>
 * 提供 ConversationMessage 实体的数据访问功能。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public interface ConversationMessageMapper extends BaseMapper<ConversationMessagePO> {

    /**
     * 根据会话 ID 查询消息列表（使用 XML 配置）
     *
     * @param sessionId 会话 ID
     * @return 消息列表（按创建时间升序）
     */
    List<ConversationMessagePO> selectBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 查询会话的最近 N 条消息（使用 XML 配置）
     *
     * @param sessionId 会话 ID
     * @param limit 限制数量
     * @return 消息列表
     */
    List<ConversationMessagePO> selectRecentMessages(
            @Param("sessionId") Long sessionId,
            @Param("limit") Integer limit
    );

    /**
     * 统计会话的 Token 使用量（使用 XML 配置）
     *
     * @param sessionId 会话 ID
     * @return Token 总量
     */
    Long sumTokensBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 批量插入消息（使用 XML 配置）
     *
     * @param messages 消息列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<ConversationMessagePO> messages);
}

