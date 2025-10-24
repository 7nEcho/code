-- ====================================================
-- Agent 管理系统 - 对话历史相关表
-- 版本: V1.0.1
-- 描述: 创建会话表和消息记录表
-- 作者: piteAgents Team
-- 日期: 2025-10-24
-- ====================================================

-- ===========================
-- 1. 会话表
-- ===========================
CREATE TABLE IF NOT EXISTS `conversation_session` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `agent_id` BIGINT NOT NULL COMMENT '关联的 Agent ID',
    `title` VARCHAR(200) NOT NULL COMMENT '会话标题',
    `summary` TEXT COMMENT '会话摘要',
    `message_count` INT NOT NULL DEFAULT 0 COMMENT '消息数量',
    `total_tokens` INT NOT NULL DEFAULT 0 COMMENT '总 Token 消耗',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` DATETIME COMMENT '软删除时间',
    PRIMARY KEY (`id`),
    INDEX `idx_agent_id` (`agent_id`),
    INDEX `idx_created_at` (`created_at` DESC),
    INDEX `idx_deleted_at` (`deleted_at`),
    CONSTRAINT `fk_session_agent` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话表';

-- ===========================
-- 2. 消息记录表
-- ===========================
CREATE TABLE IF NOT EXISTS `conversation_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id` BIGINT NOT NULL COMMENT '关联的会话 ID',
    `role` VARCHAR(20) NOT NULL COMMENT '角色（system/user/assistant）',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `prompt_tokens` INT DEFAULT 0 COMMENT '提示词 Token 数',
    `completion_tokens` INT DEFAULT 0 COMMENT '生成内容 Token 数',
    `total_tokens` INT DEFAULT 0 COMMENT '总 Token 数',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_session_id_created_at` (`session_id`, `created_at`),
    INDEX `idx_role` (`role`),
    CONSTRAINT `fk_message_session` FOREIGN KEY (`session_id`) REFERENCES `conversation_session` (`id`) ON DELETE CASCADE,
    CONSTRAINT `chk_role` CHECK (`role` IN ('system', 'user', 'assistant'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息记录表';

-- ===========================
-- 3. 创建触发器：自动更新会话统计信息
-- ===========================
DELIMITER $$

CREATE TRIGGER `trg_message_insert_update_session`
AFTER INSERT ON `conversation_message`
FOR EACH ROW
BEGIN
    UPDATE `conversation_session`
    SET 
        `message_count` = `message_count` + 1,
        `total_tokens` = `total_tokens` + COALESCE(NEW.total_tokens, 0),
        `updated_at` = CURRENT_TIMESTAMP
    WHERE `id` = NEW.session_id;
END$$

DELIMITER ;

