package pox.com.piteagents.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import pox.com.piteagents.entity.po.ConversationSessionPO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 会话 Mapper 接口
 * <p>
 * 提供 ConversationSession 实体的数据访问功能。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public interface ConversationSessionMapper extends BaseMapper<ConversationSessionPO> {

    /**
     * 查询会话及 Agent 名称（使用 XML 配置）
     *
     * @param sessionId 会话 ID
     * @return 会话对象（包含 Agent 名称）
     */
    ConversationSessionPO selectSessionWithAgent(@Param("sessionId") Long sessionId);

    /**
     * 分页查询会话列表（带 Agent 名称，使用 XML 配置）
     *
     * @param agentId Agent ID（可选）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 会话列表
     */
    List<ConversationSessionPO> selectSessionsWithAgentName(
            @Param("agentId") Long agentId,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );

    /**
     * 统计会话的 Token 使用总量（使用 XML 配置）
     *
     * @param agentId Agent ID
     * @return Token 总量
     */
    Long sumTotalTokensByAgentId(@Param("agentId") Long agentId);

    /**
     * 软删除会话（使用 XML 配置）
     *
     * @param id 会话 ID
     * @return 影响行数
     */
    int softDelete(@Param("id") Long id);

    /**
     * 清理过期会话（使用 XML 配置）
     *
     * @param expireDate 过期时间
     * @return 影响行数
     */
    int cleanExpiredSessions(@Param("expireDate") LocalDateTime expireDate);
}

