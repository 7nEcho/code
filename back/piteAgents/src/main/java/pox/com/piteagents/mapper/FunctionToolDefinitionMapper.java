package pox.com.piteagents.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import pox.com.piteagents.entity.po.ToolDefinitionPO;

import java.util.List;

/**
 * 工具定义 Mapper 接口
 * <p>
 * 提供 ToolDefinition 实体的数据访问功能。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public interface ToolDefinitionMapper extends BaseMapper<ToolDefinitionPO> {

    /**
     * 查询所有启用的工具（使用 XML 配置）
     *
     * @return 工具列表
     */
    List<ToolDefinitionPO> selectActiveTools();

    /**
     * 根据 Agent ID 查询关联的工具（使用 XML 配置）
     *
     * @param agentId Agent ID
     * @return 工具列表
     */
    List<ToolDefinitionPO> selectToolsByAgentId(@Param("agentId") Long agentId);
}

