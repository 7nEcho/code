# 工具调用功能测试报告

## 测试日期
2025-10-25

## 测试环境
- 模型：GLM-4.5
- SDK 版本：zai-sdk 0.0.6
- Spring Boot 版本：3.5.6-SNAPSHOT

## 功能概述

实现了基础的工具调用功能（Function Calling），允许 AI 模型主动调用后端定义的工具函数。

### 实现的核心文件

1. **HelloPoxToolService.java** - 简单的测试工具，返回 "hello pox"
2. **IZhipuService.java** - 添加 `chatWithTools()` 接口方法
3. **ZhipuServiceImpl.java** - 实现完整的工具调用流程
4. **ChatController.java** - 新增 `/api/chat/with-tools` 测试端点

## 测试用例

### ✅ 测试 1：明确指定调用工具

**请求：**
```bash
curl -X POST http://localhost:8080/api/chat/with-tools \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "请调用 hello_pox 工具"}],
    "model": "glm-4.5"
  }'
```

**结果：**
```json
{
  "code": 200,
  "data": {
    "id": "2025102508205226e08ea105c94aef",
    "model": "glm-4.5",
    "content": "成功调用了 hello_pox 工具！工具返回了问候信息：\"hello pox\"。...",
    "finishReason": "stop",
    "usage": {
      "promptTokens": 112,
      "completionTokens": 74,
      "totalTokens": 186
    }
  }
}
```

**✅ 状态：成功** - AI 正确调用了工具并生成回答

---

### ✅ 测试 2：自动判断需要调用工具

**请求：**
```bash
curl -X POST http://localhost:8080/api/chat/with-tools \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "给我一个问候语"}],
    "model": "glm-4.5"
  }'
```

**结果：**
```json
{
  "code": 200,
  "data": {
    "content": "你好！这里是你的问候语：hello pox 😊",
    "usage": {
      "promptTokens": 80,
      "completionTokens": 36,
      "totalTokens": 116
    }
  }
}
```

**✅ 状态：成功** - AI 自动判断需要调用工具，成功执行并基于结果回答

---

### ✅ 测试 3：不需要工具的普通问题

**请求：**
```bash
curl -X POST http://localhost:8080/api/chat/with-tools \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "什么是 Java？"}],
    "model": "glm-4.5"
  }'
```

**结果：**
```json
{
  "code": 200,
  "data": {
    "content": "Java是一种广泛使用的面向对象编程语言...",
    "usage": {
      "promptTokens": 152,
      "completionTokens": 415,
      "totalTokens": 567
    }
  }
}
```

**✅ 状态：成功** - AI 正确判断不需要工具，直接回答问题

---

### ✅ 测试 4：明确要求执行函数

**请求：**
```bash
curl -X POST http://localhost:8080/api/chat/with-tools \
  -H "Content-Type: application/json" \
  -d '{
    "messages": [{"role": "user", "content": "帮我执行 hello_pox 函数并告诉我结果"}],
    "model": "glm-4.5"
  }'
```

**结果：**
```json
{
  "code": 200,
  "data": {
    "content": "hello_pox 函数执行成功，返回结果是：**hello pox**...",
    "usage": {
      "promptTokens": 96,
      "completionTokens": 66,
      "totalTokens": 162
    }
  }
}
```

**✅ 状态：成功** - AI 成功调用工具并格式化返回结果

---

## 工具调用流程

### 完整流程图

```
用户请求
    ↓
[第一次 API 调用]
    ↓
AI 分析是否需要工具？
    ├─ 是 → 返回 tool_calls
    │     ↓
    │   执行工具函数（helloPoxToolService.execute()）
    │     ↓
    │   获得结果："hello pox"
    │     ↓
    │   [第二次 API 调用]
    │     ↓
    │   AI 基于工具结果生成最终回答
    │     ↓
    │   返回给用户
    │
    └─ 否 → 直接生成回答
          ↓
        返回给用户
```

### 关键代码片段

**工具定义：**
```java
ChatTool helloPoxTool = ChatTool.builder()
    .type("function")
    .function(ChatFunction.builder()
        .name("hello_pox")
        .description("返回问候语 hello pox，当用户想要获取问候信息时调用此工具")
        .parameters(ChatFunctionParameters.builder()
            .type("object")
            .build())
        .build())
    .build();
```

**工具调用检测：**
```java
var toolCalls = message.getToolCalls();
if (toolCalls != null && !toolCalls.isEmpty()) {
    // 执行工具
    String toolResult = helloPoxToolService.execute();
    // 构建工具返回消息
    ChatMessage toolResultMessage = ChatMessage.builder()
        .role("tool")
        .content(toolResult)
        .toolCallId(toolCallId)
        .build();
}
```

## 结论

✅ **POC 验证成功！**

- AI 能够智能判断何时需要调用工具
- 工具调用流程完整且稳定
- 不影响原有的普通对话功能
- 为后续模块化开发奠定了基础

## 下一步规划

1. **抽象工具接口** - 定义统一的工具执行接口
2. **工具注册机制** - 支持动态注册多个工具
3. **从数据库加载工具** - 使用 `tool_definition` 表中的配置
4. **支持带参数的工具** - 实现更复杂的工具调用
5. **工具管理 API** - 提供工具的 CRUD 接口
6. **前端工具管理界面** - 可视化管理工具定义

## API 端点

- **测试端点**：`POST /api/chat/with-tools`
- **原有端点**：`POST /api/chat`（不受影响）
- **流式端点**：`POST /api/chat/stream`（不受影响）

## 注意事项

- 工具调用需要两次 API 请求，会消耗更多 Token
- 目前工具定义是硬编码的，后续需要改为从数据库加载
- 仅在 GLM-4.5/4.6 等支持工具调用的模型中可用

