package pox.com.piteagents.entity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Agent 数据传输对象
 * <p>
 * 用于返回 Agent 的详细信息。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentDTO {

    /**
     * Agent ID
     */
    private Long id;

    /**
     * Agent 名称
     */
    private String name;

    /**
     * Agent 描述
     */
    private String description;

    /**
     * Agent 头像URL
     */
    private String avatar;

    /**
     * Agent 分类
     */
    private String category;

    /**
     * 状态
     */
    private String status;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 角色提示词
     */
    private String rolePrompt;

    /**
     * 是否启用
     */
    private Boolean isActive;

    /**
     * Agent 配置
     */
    private AgentConfigDTO config;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}

