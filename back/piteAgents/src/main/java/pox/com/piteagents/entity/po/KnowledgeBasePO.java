package pox.com.piteagents.entity.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 知识库实体类
 * <p>
 * 存储知识库内容，可供 Agent 引用作为上下文。
 * </p>
 *
 * @author piteAgents
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("knowledge_base")
public class KnowledgeBasePO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 知识库名称
     */
    @TableField("name")
    private String name;

    /**
     * 知识库描述
     */
    @TableField("description")
    private String description;

    /**
     * 知识库内容
     * <p>
     * 存储实际的知识内容
     * </p>
     */
    @TableField("content")
    private String content;

    /**
     * 内容类型
     * <p>
     * 可选值：TEXT（纯文本）、MARKDOWN、JSON、HTML
     * </p>
     */
    @TableField("content_type")
    @Builder.Default
    private String contentType = "TEXT";

    /**
     * 字符数
     * <p>
     * 内容的字符数量统计
     * </p>
     */
    @TableField("char_count")
    @Builder.Default
    private Integer charCount = 0;

    /**
     * 文件路径
     * <p>
     * 如果是上传的文件，记录文件路径
     * </p>
     */
    @TableField("file_path")
    private String filePath;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
