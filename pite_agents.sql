/*
 Navicat Premium Dump SQL

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : pite_agents

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 28/10/2025 21:19:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for agent
-- ----------------------------
DROP TABLE IF EXISTS `agent`;
CREATE TABLE `agent` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Agent 名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT 'Agent 描述',
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Agent 头像URL',
  `category` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Agent 分类（编程、写作、翻译等）',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE/INACTIVE/ARCHIVED）',
  `system_prompt` text COLLATE utf8mb4_unicode_ci COMMENT '系统提示词',
  `role_prompt` text COLLATE utf8mb4_unicode_ci COMMENT '角色提示词',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_deleted_at` (`deleted_at`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent 基础信息表';

-- ----------------------------
-- Records of agent
-- ----------------------------
BEGIN;
INSERT INTO `agent` (`id`, `name`, `description`, `avatar`, `category`, `status`, `system_prompt`, `role_prompt`, `is_active`, `created_at`, `updated_at`, `deleted_at`) VALUES (1, '通用助手', '一个通用的 AI 助手，可以回答各种问题', NULL, '通用', 'ACTIVE', '你是一个友好、专业的 AI 助手，可以帮助用户解答各种问题。', '请用清晰、易懂的语言回答用户的问题，必要时提供详细的解释。', 1, '2025-10-24 03:29:42', '2025-10-28 15:08:43', '2025-10-28 07:08:43');
INSERT INTO `agent` (`id`, `name`, `description`, `avatar`, `category`, `status`, `system_prompt`, `role_prompt`, `is_active`, `created_at`, `updated_at`, `deleted_at`) VALUES (2, '编程助手', '专业的编程辅导 AI，擅长解答编程相关问题', NULL, '编程', 'ACTIVE', '你是一个经验丰富的编程专家，精通多种编程语言和技术栈。', '请用专业但易懂的方式解释技术概念，提供清晰的代码示例，并给出最佳实践建议。', 1, '2025-10-24 03:29:42', '2025-10-28 15:08:39', '2025-10-28 07:08:39');
INSERT INTO `agent` (`id`, `name`, `description`, `avatar`, `category`, `status`, `system_prompt`, `role_prompt`, `is_active`, `created_at`, `updated_at`, `deleted_at`) VALUES (3, '写作助手', '帮助用户进行文案创作、润色和改进', NULL, '写作', 'ACTIVE', '你是一个专业的写作顾问，擅长各类文体的写作和润色。', '请帮助用户改进文章结构、用词和表达，提供建设性的修改建议。', 1, '2025-10-24 03:29:42', '2025-10-28 15:08:33', '2025-10-28 07:08:32');
INSERT INTO `agent` (`id`, `name`, `description`, `avatar`, `category`, `status`, `system_prompt`, `role_prompt`, `is_active`, `created_at`, `updated_at`, `deleted_at`) VALUES (4, '工具调用测试 1', '在对话的过程中尽可能的调用已有的工具去完善结果', NULL, '通用', 'ACTIVE', '你是一个专业的问题解决专家，在对话的过程中尽可能的调用已有的工具去完善结果（function calling）', '你是一个专业的问题解决专家，在对话的过程中尽可能的调用已有的工具去完善结果（function calling）', 1, '2025-10-24 03:29:42', '2025-10-24 03:29:42', NULL);
COMMIT;

-- ----------------------------
-- Table structure for agent_config
-- ----------------------------
DROP TABLE IF EXISTS `agent_config`;
CREATE TABLE `agent_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `agent_id` bigint NOT NULL COMMENT '关联的 Agent ID',
  `model` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'glm-4.6' COMMENT '模型代码（glm-4.5, glm-4.6等）',
  `temperature` decimal(3,2) NOT NULL DEFAULT '0.70' COMMENT '温度参数（0.00-1.00）',
  `max_tokens` int NOT NULL DEFAULT '2000' COMMENT '最大 Token 数',
  `top_p` decimal(3,2) DEFAULT '0.95' COMMENT 'Top P 参数（0.00-1.00）',
  `extra_params` text COLLATE utf8mb4_unicode_ci COMMENT '其他参数（JSON 格式）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_agent_id` (`agent_id`),
  CONSTRAINT `fk_agent_config_agent` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_max_tokens` CHECK ((`max_tokens` >= 1)),
  CONSTRAINT `chk_temperature` CHECK (((`temperature` >= 0.00) and (`temperature` <= 1.00))),
  CONSTRAINT `chk_top_p` CHECK (((`top_p` is null) or ((`top_p` >= 0.00) and (`top_p` <= 1.00))))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent 参数配置表';

-- ----------------------------
-- Records of agent_config
-- ----------------------------
BEGIN;
INSERT INTO `agent_config` (`id`, `agent_id`, `model`, `temperature`, `max_tokens`, `top_p`, `extra_params`, `created_at`, `updated_at`) VALUES (1, 1, 'glm-4.6', 0.70, 2000, 0.95, NULL, '2025-10-24 03:29:42', '2025-10-24 03:29:42');
INSERT INTO `agent_config` (`id`, `agent_id`, `model`, `temperature`, `max_tokens`, `top_p`, `extra_params`, `created_at`, `updated_at`) VALUES (2, 2, 'glm-4.6', 0.30, 2000, 0.95, NULL, '2025-10-24 03:29:42', '2025-10-24 03:29:42');
INSERT INTO `agent_config` (`id`, `agent_id`, `model`, `temperature`, `max_tokens`, `top_p`, `extra_params`, `created_at`, `updated_at`) VALUES (3, 3, 'glm-4.6', 0.80, 2000, 0.95, NULL, '2025-10-24 03:29:42', '2025-10-24 03:29:42');
INSERT INTO `agent_config` (`id`, `agent_id`, `model`, `temperature`, `max_tokens`, `top_p`, `extra_params`, `created_at`, `updated_at`) VALUES (4, 4, 'glm-4.5', 0.30, 2000, 0.95, NULL, '2025-10-24 03:29:42', '2025-10-24 03:29:42');
COMMIT;

-- ----------------------------
-- Table structure for agent_knowledge
-- ----------------------------
DROP TABLE IF EXISTS `agent_knowledge`;
CREATE TABLE `agent_knowledge` (
  `agent_id` bigint NOT NULL COMMENT 'Agent ID',
  `knowledge_id` bigint NOT NULL COMMENT '知识库ID',
  `priority` int NOT NULL DEFAULT '0' COMMENT '优先级（数字越大优先级越高）',
  `is_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`agent_id`,`knowledge_id`),
  KEY `idx_agent_id` (`agent_id`),
  KEY `idx_knowledge_id` (`knowledge_id`),
  KEY `idx_priority` (`priority` DESC),
  CONSTRAINT `fk_agent_knowledge_agent` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_agent_knowledge_kb` FOREIGN KEY (`knowledge_id`) REFERENCES `knowledge_base` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent 知识库关联表';

-- ----------------------------
-- Records of agent_knowledge
-- ----------------------------
BEGIN;
INSERT INTO `agent_knowledge` (`agent_id`, `knowledge_id`, `priority`, `is_enabled`, `created_at`) VALUES (2, 1, 10, 1, '2025-10-24 03:29:42');
INSERT INTO `agent_knowledge` (`agent_id`, `knowledge_id`, `priority`, `is_enabled`, `created_at`) VALUES (2, 2, 10, 1, '2025-10-24 03:29:42');
COMMIT;

-- ----------------------------
-- Table structure for agent_tool
-- ----------------------------
DROP TABLE IF EXISTS `agent_tool`;
CREATE TABLE `agent_tool` (
  `agent_id` bigint NOT NULL COMMENT 'Agent ID',
  `tool_id` bigint NOT NULL COMMENT '工具ID',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序顺序（数字越小优先级越高）',
  `tool_config` text COLLATE utf8mb4_unicode_ci COMMENT '工具个性化配置（JSON格式）',
  `is_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用此工具',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`agent_id`,`tool_id`),
  KEY `idx_agent_id` (`agent_id`),
  KEY `idx_tool_id` (`tool_id`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Agent 工具关联表';

-- ----------------------------
-- Records of agent_tool
-- ----------------------------
BEGIN;
INSERT INTO `agent_tool` (`agent_id`, `tool_id`, `sort_order`, `tool_config`, `is_enabled`, `created_at`) VALUES (4, 1, 0, NULL, 1, '2025-10-28 14:43:08');
INSERT INTO `agent_tool` (`agent_id`, `tool_id`, `sort_order`, `tool_config`, `is_enabled`, `created_at`) VALUES (4, 2, 1, NULL, 1, '2025-10-28 14:43:08');
COMMIT;

-- ----------------------------
-- Table structure for conversation_message
-- ----------------------------
DROP TABLE IF EXISTS `conversation_message`;
CREATE TABLE `conversation_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `session_id` bigint NOT NULL COMMENT '关联的会话 ID',
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色（system/user/assistant）',
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `prompt_tokens` int DEFAULT '0' COMMENT '提示词 Token 数',
  `completion_tokens` int DEFAULT '0' COMMENT '生成内容 Token 数',
  `total_tokens` int DEFAULT '0' COMMENT '总 Token 数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_session_id_created_at` (`session_id`,`created_at`),
  KEY `idx_role` (`role`),
  CONSTRAINT `fk_message_session` FOREIGN KEY (`session_id`) REFERENCES `conversation_session` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_role` CHECK ((`role` in (_utf8mb4'system',_utf8mb4'user',_utf8mb4'assistant')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息记录表';

-- ----------------------------
-- Records of conversation_message
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for conversation_session
-- ----------------------------
DROP TABLE IF EXISTS `conversation_session`;
CREATE TABLE `conversation_session` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `agent_id` bigint NOT NULL COMMENT '关联的 Agent ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '会话标题',
  `summary` text COLLATE utf8mb4_unicode_ci COMMENT '会话摘要',
  `message_count` int NOT NULL DEFAULT '0' COMMENT '消息数量',
  `total_tokens` int NOT NULL DEFAULT '0' COMMENT '总 Token 消耗',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted_at` datetime DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`),
  KEY `idx_agent_id` (`agent_id`),
  KEY `idx_created_at` (`created_at` DESC),
  KEY `idx_deleted_at` (`deleted_at`),
  CONSTRAINT `fk_session_agent` FOREIGN KEY (`agent_id`) REFERENCES `agent` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话表';

-- ----------------------------
-- Records of conversation_session
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for flyway_schema_history
-- ----------------------------
DROP TABLE IF EXISTS `flyway_schema_history`;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `script` varchar(1000) COLLATE utf8mb4_unicode_ci NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Records of flyway_schema_history
-- ----------------------------
BEGIN;
INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES (1, '1.0.0', 'init agent tables', 'SQL', 'V1.0.0__init_agent_tables.sql', 841763483, 'root', '2025-10-24 03:29:42', 32, 1);
INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES (2, '1.0.1', 'init conversation tables', 'SQL', 'V1.0.1__init_conversation_tables.sql', -145004006, 'root', '2025-10-24 03:29:42', 30, 1);
INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES (3, '1.0.2', 'init tool tables', 'SQL', 'V1.0.2__init_tool_tables.sql', -292413919, 'root', '2025-10-24 03:29:42', 28, 1);
INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES (4, '1.0.3', 'init knowledge tables', 'SQL', 'V1.0.3__init_knowledge_tables.sql', 804675200, 'root', '2025-10-24 03:29:42', 60, 1);
INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES (5, '1.0.4', 'insert sample data', 'SQL', 'V1.0.4__insert_sample_data.sql', 1924139660, 'root', '2025-10-24 03:29:42', 15, 1);
COMMIT;

-- ----------------------------
-- Table structure for knowledge_base
-- ----------------------------
DROP TABLE IF EXISTS `knowledge_base`;
CREATE TABLE `knowledge_base` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '知识库名称',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '知识库描述',
  `content` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '知识库内容',
  `content_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'TEXT' COMMENT '内容类型（TEXT/MARKDOWN/JSON/HTML）',
  `char_count` int NOT NULL DEFAULT '0' COMMENT '字符数',
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件路径（如果是上传文件）',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_content_type` (`content_type`),
  FULLTEXT KEY `ft_content` (`content`) COMMENT '全文搜索索引',
  CONSTRAINT `chk_content_type` CHECK ((`content_type` in (_utf8mb4'TEXT',_utf8mb4'MARKDOWN',_utf8mb4'JSON',_utf8mb4'HTML')))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库表';

-- ----------------------------
-- Records of knowledge_base
-- ----------------------------
BEGIN;
INSERT INTO `knowledge_base` (`id`, `name`, `description`, `content`, `content_type`, `char_count`, `file_path`, `created_at`, `updated_at`) VALUES (1, 'Java 基础知识', 'Java 编程语言的基础知识库', '# Java 简介\n\nJava 是一种广泛使用的面向对象编程语言。\n\n## 特点\n1. 跨平台：一次编写，到处运行\n2. 面向对象：封装、继承、多态\n3. 安全性：自动内存管理\n4. 多线程支持', 'MARKDOWN', 150, NULL, '2025-10-24 03:29:42', '2025-10-24 03:29:42');
INSERT INTO `knowledge_base` (`id`, `name`, `description`, `content`, `content_type`, `char_count`, `file_path`, `created_at`, `updated_at`) VALUES (2, 'Spring Boot 指南', 'Spring Boot 框架的使用指南', '# Spring Boot\n\nSpring Boot 是一个用于快速开发 Spring 应用的框架。\n\n## 核心特性\n1. 自动配置\n2. 起步依赖\n3. 内嵌服务器\n4. 生产就绪特性', 'MARKDOWN', 120, NULL, '2025-10-24 03:29:42', '2025-10-24 03:29:42');
COMMIT;

-- ----------------------------
-- Table structure for tool_definition
-- ----------------------------
DROP TABLE IF EXISTS `tool_definition`;
CREATE TABLE `tool_definition` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工具名称',
  `tool_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'HTTP' COMMENT '工具类型：HTTP(外部API调用) / LOCAL(本地方法调用) / BUILTIN(内置工具)',
  `method_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '本地工具方法名（LOCAL类型使用）',
  `description` text COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '工具描述',
  `endpoint` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'API 端点 URL',
  `method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'POST' COMMENT 'HTTP 方法（GET/POST/PUT/DELETE等）',
  `parameters` text COLLATE utf8mb4_unicode_ci COMMENT '参数定义（JSON Schema格式）',
  `headers` text COLLATE utf8mb4_unicode_ci COMMENT '请求头配置（JSON格式）',
  `timeout` int DEFAULT '30000' COMMENT '超时时间（毫秒）',
  `retry_count` int DEFAULT '3' COMMENT '重试次数',
  `is_active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`),
  KEY `idx_is_active` (`is_active`),
  KEY `idx_tool_type` (`tool_type`),
  CONSTRAINT `chk_http_tool_fields` CHECK (((`tool_type` <> _utf8mb4'HTTP') or ((`tool_type` = _utf8mb4'HTTP') and (`endpoint` is not null)))),
  CONSTRAINT `chk_method` CHECK ((`method` in (_utf8mb4'GET',_utf8mb4'POST',_utf8mb4'PUT',_utf8mb4'DELETE',_utf8mb4'PATCH'))),
  CONSTRAINT `chk_tool_type` CHECK ((`tool_type` in (_utf8mb4'HTTP',_utf8mb4'LOCAL',_utf8mb4'BUILTIN'))),
  CONSTRAINT `chk_tool_type_value` CHECK ((`tool_type` in (_utf8mb4'HTTP',_utf8mb4'BUILTIN')))
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工具定义表';

-- ----------------------------
-- Records of tool_definition
-- ----------------------------
BEGIN;
INSERT INTO `tool_definition` (`id`, `name`, `tool_type`, `method_name`, `description`, `endpoint`, `method`, `parameters`, `headers`, `timeout`, `retry_count`, `is_active`, `created_at`, `updated_at`) VALUES (1, 'calculator', 'BUILTIN', NULL, '执行基本的数学运算，支持加减乘除四则运算。', NULL, 'POST', '{\"type\":\"object\",\"properties\":{\"a\":{\"type\":\"number\",\"description\":\"第一个数字\"},\"b\":{\"type\":\"number\",\"description\":\"第二个数字\"},\"operation\":{\"type\":\"string\",\"description\":\"运算符\",\"enum\":[\"add\",\"subtract\",\"multiply\",\"divide\"]}},\"required\":[\"a\",\"b\",\"operation\"]}', NULL, 30000, 3, 1, '2025-10-28 14:30:29', '2025-10-28 14:30:29');
INSERT INTO `tool_definition` (`id`, `name`, `tool_type`, `method_name`, `description`, `endpoint`, `method`, `parameters`, `headers`, `timeout`, `retry_count`, `is_active`, `created_at`, `updated_at`) VALUES (2, 'get_current_time', 'BUILTIN', NULL, '获取当前时间，可以指定时区。支持的时区格式如: Asia/Shanghai, America/New_York, UTC 等。', NULL, 'POST', '{\"type\":\"object\",\"properties\":{\"timezone\":{\"type\":\"string\",\"description\":\"时区标识（可选，默认为系统时区）\"}}}', NULL, 30000, 3, 1, '2025-10-28 14:30:29', '2025-10-28 14:30:29');
COMMIT;

-- ----------------------------
-- Triggers structure for table conversation_message
-- ----------------------------
DROP TRIGGER IF EXISTS `trg_message_insert_update_session`;
delimiter ;;
CREATE TRIGGER `pite_agents`.`trg_message_insert_update_session` AFTER INSERT ON `conversation_message` FOR EACH ROW BEGIN
    UPDATE `conversation_session`
    SET 
        `message_count` = `message_count` + 1,
        `total_tokens` = `total_tokens` + COALESCE(NEW.total_tokens, 0),
        `updated_at` = CURRENT_TIMESTAMP
    WHERE `id` = NEW.session_id;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
