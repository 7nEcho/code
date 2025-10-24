package pox.com.piteagents.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import pox.com.piteagents.entity.po.KnowledgeBasePO;

import java.util.List;

/**
 * 知识库 Mapper 接口
 * <p>
 * 提供 KnowledgeBase 实体的数据访问功能。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
public interface KnowledgeBaseMapper extends BaseMapper<KnowledgeBasePO> {

    /**
     * 查询知识库列表（不包含 content，使用 XML 配置）
     *
     * @param contentType 内容类型
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 知识库列表
     */
    List<KnowledgeBasePO> selectList(
            @Param("contentType") String contentType,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit
    );

    /**
     * 查询知识库详情（包含 content，使用 XML 配置）
     *
     * @param id 知识库 ID
     * @return 知识库对象
     */
    KnowledgeBasePO selectByIdWithContent(@Param("id") Long id);

    /**
     * 根据 Agent ID 查询知识库（使用 XML 配置）
     *
     * @param agentId Agent ID
     * @return 知识库列表
     */
    List<KnowledgeBasePO> selectKnowledgeByAgentId(@Param("agentId") Long agentId);

    /**
     * 根据 Agent ID 查询知识库内容（用于构建上下文，使用 XML 配置）
     *
     * @param agentId Agent ID
     * @return 内容列表
     */
    List<String> selectKnowledgeContentByAgentId(@Param("agentId") Long agentId);

    /**
     * 全文搜索知识库（使用 XML 配置）
     *
     * @param keyword 关键词
     * @param limit 限制数量
     * @return 知识库列表
     */
    List<KnowledgeBasePO> fullTextSearch(@Param("keyword") String keyword, @Param("limit") Integer limit);
}

