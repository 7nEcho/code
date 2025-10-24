-- ====================================================
-- Agent 管理系统 - 示例数据
-- 版本: V1.0.4
-- 描述: 插入示例 Agent 和配置数据，便于测试
-- 作者: piteAgents Team
-- 日期: 2025-10-24
-- ====================================================

-- ===========================
-- 1. 插入示例 Agent
-- ===========================
INSERT INTO `agent` (`name`, `description`, `avatar`, `category`, `system_prompt`, `role_prompt`, `status`, `is_active`)
VALUES 
    ('通用助手', '一个通用的 AI 助手，可以回答各种问题', NULL, '通用', 
     '你是一个友好、专业的 AI 助手，可以帮助用户解答各种问题。', 
     '请用清晰、易懂的语言回答用户的问题，必要时提供详细的解释。', 
     'ACTIVE', TRUE),
    
    ('编程助手', '专业的编程辅导 AI，擅长解答编程相关问题', NULL, '编程', 
     '你是一个经验丰富的编程专家，精通多种编程语言和技术栈。', 
     '请用专业但易懂的方式解释技术概念，提供清晰的代码示例，并给出最佳实践建议。', 
     'ACTIVE', TRUE),
    
    ('写作助手', '帮助用户进行文案创作、润色和改进', NULL, '写作', 
     '你是一个专业的写作顾问，擅长各类文体的写作和润色。', 
     '请帮助用户改进文章结构、用词和表达，提供建设性的修改建议。', 
     'ACTIVE', TRUE),
    
    ('翻译助手', '提供专业的中英文翻译服务', NULL, '翻译', 
     '你是一个专业的翻译专家，能够准确理解并翻译中英文内容。', 
     '请提供准确、流畅的翻译，保持原文的语气和风格。对于专业术语，请给出对应的英文或中文解释。', 
     'ACTIVE', TRUE);

-- ===========================
-- 2. 为每个 Agent 插入默认配置
-- ===========================
INSERT INTO `agent_config` (`agent_id`, `model`, `temperature`, `max_tokens`, `top_p`)
SELECT 
    id,
    CASE 
        WHEN category = '编程' THEN 'glm-4.6'
        WHEN category = '写作' THEN 'glm-4.6'
        WHEN category = '翻译' THEN 'glm-4.5'
        ELSE 'glm-4.6'
    END as model,
    CASE 
        WHEN category = '编程' THEN 0.3
        WHEN category = '写作' THEN 0.8
        WHEN category = '翻译' THEN 0.3
        ELSE 0.7
    END as temperature,
    2000 as max_tokens,
    0.95 as top_p
FROM `agent`
WHERE `deleted_at` IS NULL;

-- ===========================
-- 3. 插入示例工具定义
-- ===========================
INSERT INTO `tool_definition` (`name`, `description`, `endpoint`, `method`, `parameters`, `is_active`)
VALUES 
    ('网页搜索', '搜索互联网上的相关信息', 
     'https://api.example.com/search', 'POST',
     '{"type":"object","properties":{"query":{"type":"string","description":"搜索关键词"}},"required":["query"]}',
     TRUE),
    
    ('代码执行', '执行 Python 代码并返回结果', 
     'https://api.example.com/execute', 'POST',
     '{"type":"object","properties":{"code":{"type":"string","description":"要执行的代码"},"language":{"type":"string","description":"编程语言","default":"python"}},"required":["code"]}',
     TRUE);

-- ===========================
-- 4. 插入示例知识库
-- ===========================
INSERT INTO `knowledge_base` (`name`, `description`, `content`, `content_type`, `char_count`)
VALUES 
    ('Java 基础知识', 'Java 编程语言的基础知识库',
     '# Java 简介\n\nJava 是一种广泛使用的面向对象编程语言。\n\n## 特点\n1. 跨平台：一次编写，到处运行\n2. 面向对象：封装、继承、多态\n3. 安全性：自动内存管理\n4. 多线程支持',
     'MARKDOWN', 150),
    
    ('Spring Boot 指南', 'Spring Boot 框架的使用指南',
     '# Spring Boot\n\nSpring Boot 是一个用于快速开发 Spring 应用的框架。\n\n## 核心特性\n1. 自动配置\n2. 起步依赖\n3. 内嵌服务器\n4. 生产就绪特性',
     'MARKDOWN', 120);

-- ===========================
-- 5. 关联知识库到编程助手
-- ===========================
INSERT INTO `agent_knowledge` (`agent_id`, `knowledge_id`, `priority`)
SELECT 
    a.id,
    k.id,
    10
FROM `agent` a, `knowledge_base` k
WHERE a.category = '编程' 
  AND a.deleted_at IS NULL
  AND k.name IN ('Java 基础知识', 'Spring Boot 指南');

-- ===========================
-- 6. 关联工具到编程助手
-- ===========================
INSERT INTO `agent_tool` (`agent_id`, `tool_id`, `sort_order`)
SELECT 
    a.id,
    t.id,
    ROW_NUMBER() OVER (PARTITION BY a.id ORDER BY t.id) as sort_order
FROM `agent` a, `tool_definition` t
WHERE a.category = '编程' 
  AND a.deleted_at IS NULL
  AND t.name IN ('网页搜索', '代码执行');

