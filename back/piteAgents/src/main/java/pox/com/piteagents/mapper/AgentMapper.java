package pox.com.piteagents.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import pox.com.piteagents.entity.po.AgentPO;

import java.util.List;

/**
 * Agent Mapper 接口
 * <p>
 * 提供 Agent 实体的数据访问功能。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public interface AgentMapper extends BaseMapper<AgentPO> {

    /**
     * 查询 Agent 及其配置（使用 XML 配置）
     *
     * @param id Agent ID
     * @return Agent 对象（包含配置信息）
     */
    AgentPO selectAgentWithConfig(@Param("id") Long id);

    /**
     * 复杂搜索查询（使用 XML 配置的动态 SQL）
     *
     * @param keyword 关键词
     * @param category 分类
     * @param status 状态
     * @param isActive 是否启用
     * @param offset 偏移量
     * @param limit 限制数量
     * @return Agent 列表
     */
    List<AgentPO> complexSearch(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("status") String status,
            @Param("isActive") Boolean isActive,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );

    /**
     * 统计查询（使用 XML 配置）
     *
     * @param category 分类
     * @param status 状态
     * @return 数量
     */
    Long countByConditions(@Param("category") String category, @Param("status") String status);

    /**
     * 批量插入（使用 XML 配置）
     *
     * @param agents Agent 列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<AgentPO> agents);

    /**
     * 软删除（使用 XML 配置）
     *
     * @param id Agent ID
     * @return 影响行数
     */
    int softDelete(@Param("id") Long id);

    /**
     * 批量软删除（使用 XML 配置）
     *
     * @param ids Agent ID 列表
     * @return 影响行数
     */
    int batchSoftDelete(@Param("ids") List<Long> ids);
}

