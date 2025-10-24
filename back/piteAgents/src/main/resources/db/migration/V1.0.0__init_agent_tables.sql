-- ====================================================
-- Agent 管理系统 - Agent 相关表
-- 版本: V1.0.0
-- 描述: 创建 Agent 基础信息表和配置表
-- 作者: piteAgents Team
-- 日期: 2025-10-24
-- ====================================================

-- ===========================
-- 1. Agent 基础信息表
-- ===========================
CREATE TABLE IF NOT EXISTS `agent` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT 'Agent 名称',
    `description` TEXT COMMENT 'Agent 描述',
    `avatar` VARCHAR(255) COMMENT 'Agent 头像URL',
    `category` VARCHAR(50) COMMENT 'Agent 分类（编程、写作、翻译等）',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE/INACTIVE/ARCHIVED）',
    `system_prompt` TEXT COMMENT '系统提示词',
    `role_prompt` TEXT COMMENT '角色提示词',
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_at` DATETIME COMMENT '软删除时间',
    PRIMARY KEY (`id`),
    INDEX `idx_name` (`name`),
    INDEX `idx_category` (`category`),
    INDEX `idx_status` (`status`),
    INDEX `idx_created_at` (`created_at`),
    INDEX `idx_deleted_at` (`deleted_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent 基础信息表';

-- ===========================
-- 2. Agent 参数配置表
-- ===========================
CREATE TABLE IF NOT EXISTS `agent_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `agent_id` BIGINT NOT NULL COMMENT '关联的 Agent ID',
    `model` VARCHAR(50) NOT NULL DEFAULT 'glm-4.6' COMMENT '模型代码（glm-4.5, glm-4.6等）',
    `temperature` DECIMAL(3,2) NOT NULL DEFAULT 0.70 COMMENT '温度参数（0.00-1.00）',
    `max_tokens` INT NOT NULL DEFAULT 2000 COMMENT '最大 Token 数',
    `top_p` DECIMAL(3,2) DEFAULT 0.95 COMMENT 'Top P 参数（0.00-1.00）',
    `extra_params` TEXT COMMENT '其他参数（JSON 格式）',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_agent_id` (`agent_id`),
    CONSTRAINT `fk_agent_config_agent` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`id`) ON DELETE CASCADE,
    CONSTRAINT `chk_temperature` CHECK (`temperature` >= 0.00 AND `temperature` <= 1.00),
    CONSTRAINT `chk_top_p` CHECK (`top_p` IS NULL OR (`top_p` >= 0.00 AND `top_p` <= 1.00)),
    CONSTRAINT `chk_max_tokens` CHECK (`max_tokens` >= 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent 参数配置表';

