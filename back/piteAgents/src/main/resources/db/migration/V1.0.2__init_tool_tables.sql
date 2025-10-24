-- ====================================================
-- Agent 管理系统 - 工具管理相关表
-- 版本: V1.0.2
-- 描述: 创建工具定义表和 Agent 工具关联表
-- 作者: piteAgents Team
-- 日期: 2025-10-24
-- ====================================================

-- ===========================
-- 1. 工具定义表
-- ===========================
CREATE TABLE IF NOT EXISTS `tool_definition` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(100) NOT NULL COMMENT '工具名称',
    `description` TEXT NOT NULL COMMENT '工具描述',
    `endpoint` VARCHAR(500) NOT NULL COMMENT 'API 端点 URL',
    `method` VARCHAR(10) NOT NULL DEFAULT 'POST' COMMENT 'HTTP 方法（GET/POST/PUT/DELETE等）',
    `parameters` TEXT COMMENT '参数定义（JSON Schema格式）',
    `headers` TEXT COMMENT '请求头配置（JSON格式）',
    `timeout` INT DEFAULT 30000 COMMENT '超时时间（毫秒）',
    `retry_count` INT DEFAULT 3 COMMENT '重试次数',
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`),
    INDEX `idx_is_active` (`is_active`),
    CONSTRAINT `chk_method` CHECK (`method` IN ('GET', 'POST', 'PUT', 'DELETE', 'PATCH'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工具定义表';

-- ===========================
-- 2. Agent 工具关联表
-- ===========================
CREATE TABLE IF NOT EXISTS `agent_tool` (
    `agent_id` BIGINT NOT NULL COMMENT 'Agent ID',
    `tool_id` BIGINT NOT NULL COMMENT '工具ID',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序（数字越小优先级越高）',
    `tool_config` TEXT COMMENT '工具个性化配置（JSON格式）',
    `is_enabled` BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用此工具',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`agent_id`, `tool_id`),
    INDEX `idx_agent_id` (`agent_id`),
    INDEX `idx_tool_id` (`tool_id`),
    INDEX `idx_sort_order` (`sort_order`),
    CONSTRAINT `fk_agent_tool_agent` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_agent_tool_tool` FOREIGN KEY (`tool_id`) REFERENCES `tool_definition` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent 工具关联表';

