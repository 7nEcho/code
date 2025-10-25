package pox.com.piteagents.entity.dto.common;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pox.com.piteagents.common.constant.MessageRole;

/**
 * 消息对象
 * <p>
 * 表示对话中的一条消息，包含角色和内容。
 * 符合智谱AI API的消息格式要求。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    /**
     * 消息角色
     * <p>
     * 可选值：
     * - system: 系统消息，用于设定AI的行为和角色
     * - user: 用户消息，表示用户的输入
     * - assistant: 助手消息，表示AI的回复
     * </p>
     */
    @NotBlank(message = "消息角色不能为空")
    private String role;

    /**
     * 消息内容
     * <p>
     * 实际的消息文本内容
     * </p>
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 创建系统消息
     *
     * @param content 消息内容
     * @return 系统消息对象
     */
    public static Message system(String content) {
        return Message.builder()
                .role(MessageRole.SYSTEM)
                .content(content)
                .build();
    }

    /**
     * 创建用户消息
     *
     * @param content 消息内容
     * @return 用户消息对象
     */
    public static Message user(String content) {
        return Message.builder()
                .role(MessageRole.USER)
                .content(content)
                .build();
    }

    /**
     * 创建助手消息
     *
     * @param content 消息内容
     * @return 助手消息对象
     */
    public static Message assistant(String content) {
        return Message.builder()
                .role(MessageRole.ASSISTANT)
                .content(content)
                .build();
    }
}

