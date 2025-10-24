package pox.com.piteagents.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pox.com.piteagents.entity.po.AgentKnowledgePO;

/**
 * Agent 知识库关联 Mapper 接口
 * <p>
 * 提供 AgentKnowledge 实体的数据访问功能。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public interface AgentKnowledgeMapper extends BaseMapper<AgentKnowledgePO> {
    // MyBatis-Plus 提供的基础 CRUD 方法已满足需求
    // 复杂查询可在 KnowledgeBaseMapper 中定义
}

