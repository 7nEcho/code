# piteAgents 快速开始指南

## 1. 前置条件

- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+
- IDE（推荐 IntelliJ IDEA 或 VS Code）

## 2. 环境准备

### 2.1 安装 MySQL

确保 MySQL 已安装并启动：

```bash
# macOS (使用 Homebrew)
brew install mysql
brew services start mysql

# 或使用 Docker
docker run --name mysql-piteagents -e MYSQL_ROOT_PASSWORD=root@1234 -p 3306:3306 -d mysql:8.0
```

### 2.2 创建数据库

```sql
CREATE DATABASE pite_agents CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 3. 项目配置

### 3.1 克隆项目（如果是首次）

```bash
cd /Users/pox/Desktop/pite/code/back/piteAgents
```

### 3.2 配置数据库连接

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pite_agents?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root@1234  # 修改为你的密码
```

### 3.3 配置智谱 AI API Key

```yaml
zhipu:
  api-key: your-api-key-here  # 替换为你的实际 API Key
```

## 4. 启动项目

### 4.1 使用 Maven 启动

```bash
cd /Users/pox/Desktop/pite/code/back/piteAgents
./mvnw clean spring-boot:run
```

### 4.2 使用 IDE 启动

1. 在 IDEA 中打开项目
2. 找到 `PiteAgentsApplication.java`
3. 右键 -> Run 'PiteAgentsApplication'

### 4.3 验证启动

看到以下日志说明启动成功：

```
Tomcat started on port(s): 8080 (http)
Started PiteAgentsApplication in X.XXX seconds
```

Flyway 会自动执行数据库迁移，创建所有表并插入示例数据。

## 5. API 测试

### 5.1 测试 Agent 列表查询

```bash
curl http://localhost:8080/api/agents
```

**预期响应：**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "通用助手",
        "description": "一个通用的 AI 助手，可以回答各种问题",
        "category": "通用",
        "status": "ACTIVE",
        "isActive": true,
        "config": {
          "model": "glm-4.6",
          "temperature": 0.7,
          "maxTokens": 2000,
          "topP": 0.95
        }
      }
    ]
  }
}
```

### 5.2 测试创建 Agent

```bash
curl -X POST http://localhost:8080/api/agents \
  -H "Content-Type: application/json" \
  -d '{
    "name": "测试助手",
    "description": "这是一个测试用的 AI 助手",
    "category": "测试",
    "systemPrompt": "你是一个专业的测试助手",
    "rolePrompt": "请详细回答用户的问题",
    "config": {
      "model": "glm-4.6",
      "temperature": 0.7,
      "maxTokens": 2000
    }
  }'
```

### 5.3 测试获取 Agent 详情

```bash
curl http://localhost:8080/api/agents/1
```

### 5.4 测试会话列表查询

```bash
curl http://localhost:8080/api/sessions
```

### 5.5 测试现有的对话接口

```bash
curl -X POST http://localhost:8080/api/chat \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [
      {
        "role": "user",
        "content": "你好，请介绍一下你自己"
      }
    ],
    "model": "glm-4.6",
    "temperature": 0.7
  }'
```

## 6. 使用 Postman 测试

### 6.1 导入 Postman Collection

创建以下请求集合：

**Collection: piteAgents API**

1. **查询 Agent 列表**
   - Method: GET
   - URL: `http://localhost:8080/api/agents`

2. **创建 Agent**
   - Method: POST
   - URL: `http://localhost:8080/api/agents`
   - Body (JSON):
   ```json
   {
     "name": "编程专家",
     "description": "专业的编程助手",
     "category": "编程",
     "systemPrompt": "你是一个经验丰富的编程专家",
     "config": {
       "model": "glm-4.6",
       "temperature": 0.3
     }
   }
   ```

3. **获取 Agent 详情**
   - Method: GET
   - URL: `http://localhost:8080/api/agents/:id`

4. **更新 Agent**
   - Method: PUT
   - URL: `http://localhost:8080/api/agents/:id`
   - Body (JSON):
   ```json
   {
     "name": "高级编程专家",
     "description": "更专业的编程助手"
   }
   ```

5. **删除 Agent**
   - Method: DELETE
   - URL: `http://localhost:8080/api/agents/:id`

6. **查询会话列表**
   - Method: GET
   - URL: `http://localhost:8080/api/sessions?agentId=1`

7. **查询会话消息**
   - Method: GET
   - URL: `http://localhost:8080/api/sessions/:id/messages`

## 7. 数据库查看

### 7.1 使用 MySQL 命令行

```bash
mysql -u root -p
use pite_agents;

-- 查看所有表
SHOW TABLES;

-- 查看 Agent 列表
SELECT * FROM agent;

-- 查看 Agent 配置
SELECT * FROM agent_config;

-- 查看会话列表
SELECT * FROM conversation_session;

-- 查看消息记录
SELECT * FROM conversation_message;
```

### 7.2 使用数据库 GUI 工具

推荐使用以下工具之一：
- **MySQL Workbench**
- **DBeaver**
- **DataGrip** (IntelliJ IDEA 内置)
- **Sequel Pro** (macOS)

连接信息：
- Host: localhost
- Port: 3306
- Database: pite_agents
- Username: root
- Password: root@1234

## 8. 日志查看

### 8.1 日志级别

默认配置：
- Root Level: INFO
- Application Level: DEBUG
- SQL Level: DEBUG

### 8.2 查看日志

```bash
# 实时查看日志
tail -f logs/piteAgents.log

# 或在 IDE 控制台查看
```

## 9. 常见问题

### Q1: Flyway 迁移失败

**错误**: `FlywayException: Found non-empty schema(s)`

**解决**:
```sql
-- 清空数据库重新开始
DROP DATABASE pite_agents;
CREATE DATABASE pite_agents CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Q2: 数据库连接超时

**错误**: `Communications link failure`

**检查**:
1. MySQL 是否启动
2. 端口 3306 是否开放
3. 用户名密码是否正确

### Q3: JPA 懒加载异常

**错误**: `LazyInitializationException`

**解决**: 已在配置中设置 `spring.jpa.open-in-view=false`，确保在 Service 层完成数据加载。

### Q4: Agent 创建失败

**错误**: `Agent 名称已存在`

**说明**: Agent 名称必须唯一，请使用不同的名称。

## 10. 下一步

### 10.1 探索示例数据

系统已自动创建 4 个示例 Agent：
1. 通用助手
2. 编程助手
3. 写作助手
4. 翻译助手

可以直接使用这些 Agent 进行测试。

### 10.2 集成到前端

参考 API 文档，将 Agent 管理功能集成到你的前端应用中。

### 10.3 开发新功能

- 实现工具管理接口
- 实现知识库管理接口
- 集成 Agent 到对话流程
- 添加更多业务逻辑

## 11. 开发工作流

### 11.1 创建新功能的步骤

1. **设计数据库表**（如果需要）
   - 创建 Flyway 迁移脚本

2. **创建实体类**
   - 在 `entity/` 包下创建

3. **创建 Repository**
   - 在 `repository/` 包下创建

4. **创建 DTO**
   - 在 `dto/` 包下创建请求和响应对象

5. **创建 Service**
   - 在 `service/` 包下实现业务逻辑

6. **创建 Controller**
   - 在 `controller/` 包下创建 REST API

7. **编写测试**
   - 在 `src/test/` 目录下创建测试类

8. **更新文档**
   - 更新 API 文档和架构设计文档

## 12. 相关文档

- [数据库设计文档](./database-design.md)
- [API 设计文档](./api-design.md)
- [架构设计文档](./architecture-design.md)

---

**祝你使用愉快！**

如有问题，请查看架构设计文档或提issue。

