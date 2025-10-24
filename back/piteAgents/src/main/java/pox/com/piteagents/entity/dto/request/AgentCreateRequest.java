package pox.com.piteagents.entity.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pox.com.piteagents.entity.dto.response.AgentConfigDTO;

/**
 * 创建 Agent 请求对象
 * <p>
 * 用于接收创建 Agent 的请求参数。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentCreateRequest {

    /**
     * Agent 名称
     */
    @NotBlank(message = "Agent 名称不能为空")
    @Size(max = 100, message = "Agent 名称长度不能超过100个字符")
    private String name;

    /**
     * Agent 描述
     */
    @Size(max = 5000, message = "Agent 描述长度不能超过5000个字符")
    private String description;

    /**
     * Agent 头像URL
     */
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatar;

    /**
     * Agent 分类
     */
    @Size(max = 50, message = "分类长度不能超过50个字符")
    private String category;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 角色提示词
     */
    private String rolePrompt;

    /**
     * Agent 配置
     */
    @Valid
    private AgentConfigDTO config;
}

