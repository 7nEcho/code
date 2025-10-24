-- ====================================================
-- piteAgents 数据库初始化脚本
-- 描述: 创建数据库和用户（如果需要）
-- 作者: piteAgents Team
-- 日期: 2025-10-24
-- ====================================================

-- ===========================
-- 1. 创建数据库
-- ===========================
CREATE DATABASE IF NOT EXISTS `pite_agents` 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci
    COMMENT 'piteAgents AI Agent 管理系统数据库';

-- ===========================
-- 2. 选择数据库
-- ===========================
USE `pite_agents`;

-- ===========================
-- 3. （可选）创建专用数据库用户
-- ===========================
-- 如果需要创建专用用户，取消下面的注释

-- CREATE USER IF NOT EXISTS 'piteagents'@'localhost' IDENTIFIED BY 'piteagents123';
-- GRANT ALL PRIVILEGES ON pite_agents.* TO 'piteagents'@'localhost';
-- FLUSH PRIVILEGES;

-- ===========================
-- 说明
-- ===========================
-- 执行此脚本后，Flyway 会自动创建所有表结构和插入示例数据
-- 
-- 使用方法：
-- 1. 确保 MySQL 服务已启动
-- 2. 执行此脚本: mysql -u root -p < database-init.sql
-- 3. 启动 Spring Boot 应用
-- 4. Flyway 会自动执行迁移脚本
-- ====================================================

SELECT '数据库初始化成功！' as message;
SELECT '请启动 Spring Boot 应用，Flyway 会自动创建表结构' as next_step;

