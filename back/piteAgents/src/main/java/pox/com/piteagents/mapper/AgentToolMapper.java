package pox.com.piteagents.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pox.com.piteagents.entity.po.AgentToolPO;

/**
 * Agent 工具关联 Mapper 接口
 * <p>
 * 提供 AgentTool 实体的数据访问功能。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public interface AgentToolMapper extends BaseMapper<AgentToolPO> {
    // MyBatis-Plus 提供的基础 CRUD 方法已满足需求
    // 复杂查询可在 ToolDefinitionMapper 中定义
}

