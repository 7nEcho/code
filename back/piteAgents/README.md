# piteAgents - AI Agent 管理系统

一个基于 Spring Boot 和智谱 AI 的完整 Agent 管理系统，提供 Agent 创建、配置、对话历史管理等核心功能。

## ✨ 特性

- 🤖 **Agent 管理**: 创建和管理多个 AI Agent，每个 Agent 有独立的配置和提示词
- 💬 **对话历史**: 完整的会话和消息记录，支持历史查询和上下文加载
- ⚙️ **灵活配置**: 支持模型选择、温度参数、Token 限制等细粒度配置
- 🔄 **流式输出**: 支持 SSE 流式返回，实时展示 AI 生成过程
- 📊 **数据持久化**: 使用 MySQL 持久化所有数据，Flyway 管理数据库版本
- 🚀 **RESTful API**: 标准化的 REST API 接口，易于集成

## 🏗️ 技术栈

- **后端框架**: Spring Boot 3.5.6
- **Java 版本**: Java 17
- **数据库**: MySQL 8.0+
- **ORM**: Spring Data JPA + Hibernate
- **数据库迁移**: Flyway
- **AI SDK**: 智谱 AI SDK 0.0.6
- **构建工具**: Maven

## 📋 系统要求

- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+
- 智谱 AI API Key

## 🚀 快速开始

### 1. 克隆项目

```bash
cd /Users/pox/Desktop/pite/code/back/piteAgents
```

### 2. 配置数据库

创建 MySQL 数据库：

```sql
CREATE DATABASE pite_agents CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pite_agents
    username: root
    password: your-password  # 修改为你的密码

zhipu:
  api-key: your-api-key  # 替换为你的智谱 AI API Key
```

### 3. 启动应用

```bash
./mvnw spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

### 4. 测试 API

```bash
# 查询 Agent 列表
curl http://localhost:8080/api/agents

# 创建 Agent
curl -X POST http://localhost:8080/api/agents \
  -H "Content-Type: application/json" \
  -d '{
    "name": "编程助手",
    "description": "专业的编程辅导 AI",
    "category": "编程",
    "systemPrompt": "你是一个专业的编程助手",
    "config": {
      "model": "glm-4.6",
      "temperature": 0.7,
      "maxTokens": 2000
    }
  }'
```

## 📚 核心功能

### Agent 管理

- ✅ 创建、编辑、删除 Agent
- ✅ 配置系统提示词和角色提示词
- ✅ 设置模型参数（温度、Token 限制等）
- ✅ Agent 状态管理（启用/禁用）
- ✅ 按分类、状态查询和搜索

### 对话历史

- ✅ 会话管理（创建、查询、删除）
- ✅ 消息记录（完整的对话历史）
- ✅ Token 使用统计
- ✅ 历史消息加载和上下文管理

### 智谱 AI 集成

- ✅ 同步对话接口
- ✅ 流式对话接口（SSE）
- ✅ 多模型支持（GLM-4.5、GLM-4.6）
- ✅ 灵活的参数配置

### 进阶功能（待实现）

- 🚧 工具调用管理
- 🚧 知识库集成
- 🚧 Agent 与对话的完整集成

## 📖 API 文档

### Agent 管理接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/agents` | POST | 创建 Agent |
| `/api/agents` | GET | 查询 Agent 列表 |
| `/api/agents/{id}` | GET | 获取 Agent 详情 |
| `/api/agents/{id}` | PUT | 更新 Agent |
| `/api/agents/{id}` | DELETE | 删除 Agent |
| `/api/agents/{id}/config` | PUT | 更新 Agent 配置 |
| `/api/agents/{id}/status` | PUT | 更新 Agent 状态 |

### 对话历史接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/sessions` | GET | 获取会话列表 |
| `/api/sessions/{id}` | GET | 获取会话详情 |
| `/api/sessions/{id}/messages` | GET | 获取会话消息 |
| `/api/sessions/{id}` | DELETE | 删除会话 |
| `/api/sessions/{id}/title` | PUT | 更新会话标题 |

### 对话接口

| 接口 | 方法 | 描述 |
|------|------|------|
| `/api/chat` | POST | 同步对话 |
| `/api/chat/stream` | POST | 流式对话 |
| `/api/models` | GET | 获取支持的模型列表 |

详细 API 文档请查看：[docs/api-design.md](./docs/api-design.md)

## 🗄️ 数据库设计

系统使用 8 个核心数据表：

- `agent` - Agent 基础信息
- `agent_config` - Agent 参数配置
- `conversation_session` - 对话会话
- `conversation_message` - 消息记录
- `tool_definition` - 工具定义
- `agent_tool` - Agent-工具关联
- `knowledge_base` - 知识库
- `agent_knowledge` - Agent-知识库关联

详细数据库设计请查看：[docs/database-design.md](./docs/database-design.md)

## 📁 项目结构

```
src/main/java/pox/com/piteagents/
├── config/           # 配置类
├── constant/         # 常量定义
├── controller/       # REST API 控制器
├── dto/              # 数据传输对象
├── entity/           # JPA 实体类
├── enums/            # 枚举类型
├── exception/        # 异常处理
├── repository/       # 数据访问层
└── service/          # 业务逻辑层

src/main/resources/
├── application.yml   # 应用配置
└── db/migration/     # Flyway 数据库迁移脚本

docs/
├── database-design.md      # 数据库设计文档
├── api-design.md           # API 设计文档
├── architecture-design.md  # 架构设计文档
└── quick-start.md          # 快速开始指南
```

## 🧪 测试

```bash
# 运行所有测试
./mvnw test

# 运行特定测试
./mvnw test -Dtest=ChatControllerTest
```

## 📝 开发文档

- [快速开始指南](./docs/quick-start.md) - 详细的安装和使用指南
- [数据库设计文档](./docs/database-design.md) - 完整的数据库设计说明
- [API 设计文档](./docs/api-design.md) - RESTful API 详细说明
- [架构设计文档](./docs/architecture-design.md) - 系统架构和设计思想

## 🛠️ 开发

### 添加新功能

1. 设计数据库表（如果需要）
2. 创建 Flyway 迁移脚本
3. 创建实体类 (Entity)
4. 创建 Repository 接口
5. 创建 DTO 类
6. 实现 Service 业务逻辑
7. 创建 Controller 接口
8. 编写测试
9. 更新文档

### 代码规范

- 遵循 Java 代码规范
- 使用 Lombok 减少样板代码
- 所有类和方法添加详细注释
- 使用中文注释和文档
- Repository 方法遵循 Spring Data JPA 命名规范

## 📊 当前进度

### 已完成 ✅

- [x] 数据库设计和迁移脚本
- [x] 实体类和 Repository 层
- [x] Agent CRUD 功能
- [x] 对话历史管理
- [x] 智谱 AI 基础集成
- [x] API 接口实现
- [x] 完整文档

### 进行中 🚧

- [ ] Agent 与对话的完整集成
- [ ] 单元测试和集成测试
- [ ] 工具管理功能
- [ ] 知识库管理功能

### 计划中 📋

- [ ] API 文档自动生成（SpringDoc OpenAPI）
- [ ] 性能优化和缓存
- [ ] 前端集成示例
- [ ] Docker 部署支持

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目采用 MIT 许可证。

## 👥 作者

piteAgents Team

## 🙏 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Flyway](https://flywaydb.org/)
- [智谱 AI](https://www.bigmodel.cn/)

---

**快速链接**

- [快速开始](./docs/quick-start.md)
- [API 文档](./docs/api-design.md)
- [架构设计](./docs/architecture-design.md)
- [数据库设计](./docs/database-design.md)

