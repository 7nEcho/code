-- ====================================================
-- Agent 管理系统 - 知识库管理相关表
-- 版本: V1.0.3
-- 描述: 创建知识库表和 Agent 知识库关联表
-- 作者: piteAgents Team
-- 日期: 2025-10-24
-- ====================================================

-- ===========================
-- 1. 知识库表
-- ===========================
CREATE TABLE IF NOT EXISTS `knowledge_base` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(200) NOT NULL COMMENT '知识库名称',
    `description` TEXT COMMENT '知识库描述',
    `content` LONGTEXT NOT NULL COMMENT '知识库内容',
    `content_type` VARCHAR(50) NOT NULL DEFAULT 'TEXT' COMMENT '内容类型（TEXT/MARKDOWN/JSON/HTML）',
    `char_count` INT NOT NULL DEFAULT 0 COMMENT '字符数',
    `file_path` VARCHAR(500) COMMENT '文件路径（如果是上传文件）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_content_type` (`content_type`),
    FULLTEXT INDEX `ft_content` (`content`) COMMENT '全文搜索索引',
    CONSTRAINT `chk_content_type` CHECK (`content_type` IN ('TEXT', 'MARKDOWN', 'JSON', 'HTML'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库表';

-- ===========================
-- 2. Agent 知识库关联表
-- ===========================
CREATE TABLE IF NOT EXISTS `agent_knowledge` (
    `agent_id` BIGINT NOT NULL COMMENT 'Agent ID',
    `knowledge_id` BIGINT NOT NULL COMMENT '知识库ID',
    `priority` INT NOT NULL DEFAULT 0 COMMENT '优先级（数字越大优先级越高）',
    `is_enabled` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`agent_id`, `knowledge_id`),
    INDEX `idx_agent_id` (`agent_id`),
    INDEX `idx_knowledge_id` (`knowledge_id`),
    INDEX `idx_priority` (`priority` DESC),
    CONSTRAINT `fk_agent_knowledge_agent` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_agent_knowledge_kb` FOREIGN KEY (`knowledge_id`) REFERENCES `knowledge_base` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent 知识库关联表';

