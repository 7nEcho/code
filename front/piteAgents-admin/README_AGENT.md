# Agent 管理功能使用指南

## 功能概述

本项目现已集成完整的 Agent 管理功能，包括：

- 🧭 **全局导航栏**：顶部导航栏，支持在"聊天测试"和"Agent 管理"之间切换
- 📋 **Agent 列表**：展示所有 Agent，支持搜索、筛选和分页
- 👁️ **Agent 详情**：查看 Agent 的完整信息，包括配置和提示词
- ✏️ **创建/编辑 Agent**：通过表单创建新 Agent 或编辑现有 Agent
- 🔄 **状态管理**：启用/禁用 Agent
- 🗑️ **删除 Agent**：支持删除不需要的 Agent

## 路由说明

应用使用 Vue Router History 模式，主要路由如下：

- `/` - 默认重定向到聊天页面
- `/chat` - 聊天测试页面
- `/agents` - Agent 列表页面
- `/agents/create` - 创建新 Agent
- `/agents/:id` - Agent 详情页面
- `/agents/:id/edit` - 编辑 Agent

## 使用指南

### 1. 启动应用

```bash
cd front/piteAgents-admin
npm install  # 如果还没安装依赖
npm run dev  # 启动开发服务器
```

访问：http://localhost:5173

### 2. 导航

- 点击顶部导航栏的 **"Agent 管理"** 进入 Agent 管理页面
- 点击 **"聊天测试"** 返回聊天页面

### 3. Agent 列表

在 Agent 列表页面，您可以：

- 使用搜索框搜索 Agent 名称或描述
- 使用下拉菜单按分类或状态筛选
- 点击 **"创建 Agent"** 按钮创建新的 Agent
- 点击 Agent 卡片上的操作按钮：
  - **查看**：查看 Agent 详情
  - **编辑**：编辑 Agent 信息
  - **删除**：删除 Agent（需确认）

### 4. 创建 Agent

点击"创建 Agent"按钮，填写表单：

**基础信息**：
- Agent 名称（必填）
- 分类（通用/编程/写作/翻译）
- 头像 URL（可选）
- 描述

**提示词设置**：
- 系统提示词：定义 AI 的行为和角色
- 角色提示词：细化 AI 的回复风格

**模型配置**：
- 模型选择：GLM-4.5 或 GLM-4.6
- 温度参数：0.0（更确定）- 1.0（更随机）
- 最大 Token 数：1-8000
- Top P 参数：核采样参数

### 5. 编辑 Agent

在 Agent 详情页面或列表页面点击"编辑"按钮，修改 Agent 信息后保存。

### 6. 查看详情

点击"查看"按钮进入详情页面，可以看到：

- Agent 基础信息（ID、状态、创建时间等）
- 模型配置参数
- 完整的系统提示词和角色提示词
- 快捷操作按钮（编辑、启用/禁用、删除）

## 技术实现

### 前端技术栈

- **Vue 3**：使用 Composition API
- **Vue Router 4**：History 模式路由
- **Axios**：HTTP 请求
- **Heroicons**：图标库
- **CSS3**：现代化样式，支持响应式设计

### 目录结构

项目采用模块化、清晰的目录结构，样式文件与对应的视图模块放在一起：

```
front/piteAgents-admin/src/
├── api/                      # API 调用模块
│   ├── agent.js              # Agent API 调用
│   ├── chat.js               # 聊天 API 调用
│   └── request.js            # Axios 实例配置
├── assets/
│   └── styles/               # 全局样式文件
│       ├── global.css        # 全局基础样式
│       └── markdown.css      # Markdown 渲染样式
├── components/
│   └── Layout/               # 全局布局组件
│       ├── MainLayout.vue    # 主布局组件
│       └── NavBar.vue        # 导航栏组件
├── router/
│   └── index.js              # 路由配置
├── utils/
│   └── storage.js            # 本地存储工具
├── views/                    # 页面视图（按模块分类）
│   ├── chat/                 # 聊天模块
│   │   ├── Index.vue         # 聊天主页面
│   │   └── styles.css        # 聊天页面样式
│   └── agent/                # Agent 管理模块
│       ├── List.vue          # Agent 列表页
│       ├── Detail.vue        # Agent 详情页
│       ├── Form.vue          # Agent 表单页（新建/编辑）
│       └── styles.css        # Agent 模块样式
├── App.vue                   # 根组件
└── main.js                   # 应用入口
```

**设计原则**：
- ✅ 模块化：按功能模块组织代码
- ✅ 样式隔离：各模块样式与页面放在同一目录
- ✅ 全局复用：全局组件和样式单独管理
- ✅ 自适应布局：所有页面使用响应式设计，支持多种屏幕尺寸

### API 接口

所有 Agent 相关接口已在后端实现（`AgentController.java`）：

- `GET /api/agents` - 获取 Agent 列表
- `GET /api/agents/{id}` - 获取 Agent 详情
- `POST /api/agents` - 创建 Agent
- `PUT /api/agents/{id}` - 更新 Agent
- `DELETE /api/agents/{id}` - 删除 Agent
- `PUT /api/agents/{id}/config` - 更新 Agent 配置
- `PUT /api/agents/{id}/status` - 更新 Agent 状态

## 响应式设计

所有页面都支持响应式设计，可在不同设备上正常使用：

- **桌面端**：完整功能和布局
- **平板端**：自适应布局
- **移动端**：优化的移动端体验

## 注意事项

1. **History 模式**：应用使用 History 模式路由，开发环境下 Vite 已自动处理。生产环境部署时，需要配置服务器将所有路由回退到 `index.html`。

2. **后端服务**：确保后端服务（Spring Boot）正在运行在 `http://localhost:8080`。

3. **跨域配置**：后端已配置 CORS，允许前端访问。

4. **数据持久化**：所有 Agent 数据存储在 MySQL 数据库中。

## 下一步开发建议

- [ ] 添加 Agent 与对话的集成（在聊天时选择使用哪个 Agent）
- [ ] 添加工具管理功能
- [ ] 添加知识库管理功能
- [ ] 添加批量操作功能
- [ ] 添加 Agent 导入/导出功能
- [ ] 添加 Agent 使用统计

## 反馈与支持

如有问题或建议，请联系开发团队。

