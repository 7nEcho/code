package pox.com.piteagents.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import pox.com.piteagents.entity.po.AgentConfigPO;

import java.util.List;

/**
 * Agent 配置 Mapper 接口
 * <p>
 * 提供 AgentConfig 实体的数据访问功能。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public interface AgentConfigMapper extends BaseMapper<AgentConfigPO> {

    /**
     * 根据 Agent ID 查询配置（使用 XML 配置）
     *
     * @param agentId Agent ID
     * @return Agent 配置对象
     */
    AgentConfigPO selectByAgentId(@Param("agentId") Long agentId);

    /**
     * 批量更新配置（使用 XML 配置）
     *
     * @param configs 配置列表
     * @return 影响行数
     */
    int batchUpdateConfig(@Param("list") List<AgentConfigPO> configs);
}

