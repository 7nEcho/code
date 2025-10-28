<template>
  <div class="chat-container">
    <!-- 左侧历史对话侧边栏 -->
    <aside :class="['sidebar', sidebarOpen ? 'open' : 'closed']">
      <div class="sidebar-header">
        <h2 class="sidebar-title">对话历史</h2>
        <button @click="createNewConversation" class="icon-button primary" title="新建对话">
          <PlusIcon class="icon" />
        </button>
      </div>

      <div class="sidebar-content">
        <div
          v-for="conv in conversations"
          :key="conv.id"
          @click="switchConversation(conv.id)"
          :class="['conversation-item', currentConversationId === conv.id ? 'active' : '']"
        >
          <div class="conversation-info">
            <p class="conversation-title">{{ conv.title || '新对话' }}</p>
            <p class="conversation-time">{{ formatTime(conv.updatedAt) }}</p>
          </div>
          <button @click.stop="deleteConversation(conv.id)" class="delete-button" title="删除对话">
            <TrashIcon class="icon-sm" />
          </button>
        </div>

        <div v-if="conversations.length === 0" class="empty-state">
          <ChatBubbleLeftRightIcon class="icon-xl" style="margin: 0 auto 1rem" />
          <p>暂无对话历史</p>
          <p style="font-size: 0.75rem; margin-top: 0.5rem">点击右上角创建新对话</p>
        </div>
      </div>
    </aside>

    <!-- 右侧主对话区域 -->
    <main class="main-content">
      <!-- 顶部工具栏 -->
      <header class="header">
        <div class="header-left">
          <button @click="sidebarOpen = !sidebarOpen" class="icon-button">
            <Bars3Icon class="icon-lg" />
          </button>
          <div v-if="selectedAgent" class="agent-info">
            <div class="agent-avatar-sm">
              <img v-if="selectedAgent.avatar" :src="selectedAgent.avatar" :alt="selectedAgent.name" />
              <CpuChipIcon v-else class="avatar-icon-sm" />
            </div>
            <span class="agent-name-sm">{{ selectedAgent.name }}</span>
          </div>
        </div>

        <div class="header-right">
          <!-- Agent 选择 -->
          <select v-model="selectedAgentId" class="agent-select" @change="handleAgentChange">
            <option :value="null">无 Agent（直接对话）</option>
            <option v-for="agent in agents" :key="agent.id" :value="agent.id">
              {{ agent.name }} - {{ agent.category || '通用' }}
            </option>
          </select>

          <!-- 模型选择 -->
          <select v-model="selectedModel" class="model-select" :disabled="selectedAgentId !== null">
            <option v-for="model in models" :key="model.code" :value="model.code">
              {{ model.name }}
            </option>
          </select>

          <!-- 对话模式切换 -->
          <div class="mode-toggle">
            <button
              @click="isStreamMode = false"
              :class="['mode-button', !isStreamMode ? 'active' : '']"
            >
              同步
            </button>
            <button
              @click="isStreamMode = true"
              :class="['mode-button', isStreamMode ? 'active' : '']"
            >
              流式
            </button>
          </div>
          
          <!-- 工具调用状态提示 -->
          <div v-if="selectedAgentId" class="tool-status-badge" title="已启用工具调用功能">
            <WrenchIcon class="icon-sm" />
            <span>工具已启用</span>
          </div>
        </div>
      </header>

      <!-- 消息列表 -->
      <div ref="messageContainer" class="message-container">
        <!-- 欢迎页面 -->
        <div v-if="currentMessages.length === 0" class="welcome-screen">
          <div class="welcome-icon">
            <ChatBubbleLeftRightIcon class="icon-xl" style="color: white" />
          </div>
          <h2 class="welcome-title">开始新的对话</h2>
          <p class="welcome-subtitle">输入您的问题，我会尽力帮助您</p>
          <div class="welcome-tags">
            <span class="welcome-tag">✨ 智能问答</span>
            <span class="welcome-tag">📝 内容创作</span>
            <span class="welcome-tag">💻 代码助手</span>
          </div>
        </div>

        <!-- 消息列表 -->
        <div v-else class="messages-wrapper">
          <div
            v-for="(message, index) in currentMessages"
            :key="`msg-${index}-${message.content?.length || 0}`"
            class="message-row"
          >
            <!-- 工具调用消息 -->
            <div v-if="message.role === 'tool-call'" class="tool-call-message">
              <div class="tool-call-header">
                <WrenchIcon class="icon-sm" />
                <span>调用工具</span>
              </div>
              <div v-for="(call, idx) in message.toolCalls" :key="idx" class="tool-call-item">
                <div class="tool-call-name">🔧 {{ call.name }}</div>
                <div class="tool-call-info">
                  <div class="tool-call-row">
                    <span class="tool-call-label">参数:</span>
                    <code class="tool-call-value">{{ formatJsonField(call.arguments) }}</code>
                  </div>
                  <div class="tool-call-row">
                    <span class="tool-call-label">结果:</span>
                    <code class="tool-call-value">{{ formatJsonField(call.result) }}</code>
                  </div>
                </div>
                <details class="tool-call-details">
                  <summary>查看完整详情</summary>
                  <pre>{{ formatToolCallDetails(call) }}</pre>
                </details>
              </div>
            </div>
            
            <!-- 普通消息 -->
            <div v-else :class="['message-bubble', message.role]">
              <div class="message-content-wrapper">
                <div class="message-avatar">
                  <UserIcon v-if="message.role === 'user'" class="icon" style="color: white" />
                  <CpuChipIcon v-else class="icon" style="color: white" />
                </div>
                <div class="message-text">
                  <!-- 用户消息 -->
                  <div v-if="message.role === 'user'">{{ message.content }}</div>
                  <!-- AI 消息 - Markdown 渲染 -->
                  <div v-else class="markdown-body" v-html="renderMarkdown(message.content)"></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 加载中指示器 -->
          <div v-if="isLoading" class="loading-indicator">
            <div class="loading-bubble">
              <div class="loading-content">
                <div class="message-avatar loading-avatar">
                  <CpuChipIcon class="icon" style="color: white" />
                </div>
                <div style="display: flex; align-items: center; gap: 0.75rem">
                  <div class="loading-dots">
                    <span class="loading-dot"></span>
                    <span class="loading-dot"></span>
                    <span class="loading-dot"></span>
                  </div>
                  <span class="loading-text">AI 正在思考...</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部输入区域 -->
      <div class="input-area">
        <div class="input-wrapper">
          <div class="input-field-wrapper">
            <textarea
              v-model="inputText"
              @keydown.enter.exact.prevent="sendMessage"
              @keydown.enter.shift.exact="inputText += '\n'"
              placeholder="输入消息... (Enter 发送, Shift+Enter 换行)"
              rows="1"
              class="input-field"
              :disabled="isLoading"
            ></textarea>
          </div>
          <button
            @click="sendMessage"
            :disabled="!inputText.trim() || isLoading"
            class="send-button"
          >
            <PaperAirplaneIcon class="icon" />
            <span>发送</span>
          </button>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import {
  Bars3Icon,
  PlusIcon,
  TrashIcon,
  PaperAirplaneIcon,
  ChatBubbleLeftRightIcon,
  UserIcon,
  CpuChipIcon,
  WrenchIcon,
} from '@heroicons/vue/24/outline'
import { chat, streamChat, getModels, chatWithTools } from '@/api/chat'
import { getAgentList } from '@/api/agent'
import { saveConversations, loadConversations } from '@/utils/storage'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import './styles.css'

// 初始化 Markdown 渲染器
const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return `<pre class="hljs"><code>${hljs.highlight(str, { language: lang, ignoreIllegals: true }).value}</code></pre>`
      } catch (__) {}
    }
    return `<pre class="hljs"><code>${md.utils.escapeHtml(str)}</code></pre>`
  }
})

// 渲染 Markdown
const renderMarkdown = (content) => {
  if (!content) return ''
  return md.render(content)
}

// 格式化单个 JSON 字段（用于简洁显示）
const formatJsonField = (field) => {
  if (!field) return '-'
  
  try {
    // 如果是字符串，尝试解析为 JSON
    if (typeof field === 'string') {
      const parsed = JSON.parse(field)
      return JSON.stringify(parsed, null, 2)
    }
    // 如果已经是对象，直接格式化
    return JSON.stringify(field, null, 2)
  } catch (e) {
    // 解析失败，返回原始字符串（去除多余的引号）
    return typeof field === 'string' ? field : String(field)
  }
}

// 格式化工具调用详情（用于详细展开）
const formatToolCallDetails = (call) => {
  try {
    const formatted = {
      工具名称: call.name,
      工具ID: call.id,
      调用参数: null,
      执行结果: null,
      执行状态: call.success ? '✅ 成功' : '❌ 失败'
    }
    
    // 尝试解析 arguments（如果是 JSON 字符串）
    if (typeof call.arguments === 'string') {
      try {
        formatted.调用参数 = JSON.parse(call.arguments)
      } catch (e) {
        formatted.调用参数 = call.arguments
      }
    } else {
      formatted.调用参数 = call.arguments || {}
    }
    
    // 尝试解析 result（如果是 JSON 字符串）
    if (typeof call.result === 'string') {
      try {
        formatted.执行结果 = JSON.parse(call.result)
      } catch (e) {
        formatted.执行结果 = call.result
      }
    } else {
      formatted.执行结果 = call.result || {}
    }
    
    // 如果有错误信息，添加进去
    if (call.errorMessage) {
      formatted.错误信息 = call.errorMessage
    }
    
    return JSON.stringify(formatted, null, 2)
  } catch (error) {
    return JSON.stringify(call, null, 2)
  }
}

// 状态管理
const sidebarOpen = ref(true)
const conversations = ref([])
const currentConversationId = ref(null)
const currentMessages = ref([])
const inputText = ref('')
const isLoading = ref(false)
const isStreamMode = ref(true)
const models = ref([])
const selectedModel = ref('glm-4.5') // 默认使用 GLM-4.5
const messageContainer = ref(null)

// Agent 相关状态
const agents = ref([])
const selectedAgentId = ref(null)
const selectedAgent = ref(null)

// 生成唯一 ID
const generateId = () => {
  return Date.now().toString(36) + Math.random().toString(36).substr(2)
}

// 格式化时间
const formatTime = (timestamp) => {
  const date = new Date(timestamp)
  const now = new Date()
  const diff = now - date

  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`

  return date.toLocaleDateString('zh-CN')
}

// 滚动到底部
const scrollToBottom = () => {
  nextTick(() => {
    if (messageContainer.value) {
      messageContainer.value.scrollTop = messageContainer.value.scrollHeight
    }
  })
}

// 保存当前对话
const saveCurrentConversation = () => {
  if (!currentConversationId.value) return

  const index = conversations.value.findIndex((c) => c.id === currentConversationId.value)
  if (index !== -1) {
    conversations.value[index].messages = [...currentMessages.value]
    conversations.value[index].updatedAt = Date.now()

    // 生成标题（使用第一条用户消息的前20个字符）
    const firstUserMessage = currentMessages.value.find((m) => m.role === 'user')
    if (firstUserMessage && !conversations.value[index].title) {
      conversations.value[index].title = firstUserMessage.content.substring(0, 20)
    }

    saveConversations([...conversations.value])
  }
}

// 创建新对话
const createNewConversation = () => {
  const newConv = {
    id: generateId(),
    title: '',
    messages: [],
    createdAt: Date.now(),
    updatedAt: Date.now(),
  }

  conversations.value.unshift(newConv)
  currentConversationId.value = newConv.id
  currentMessages.value = []
  saveConversations([...conversations.value])
}

// 切换对话
const switchConversation = (id) => {
  const conv = conversations.value.find((c) => c.id === id)
  if (conv) {
    currentConversationId.value = id
    currentMessages.value = [...conv.messages]
    scrollToBottom()
  }
}

// 删除对话
const deleteConversation = (id) => {
  if (confirm('确定要删除这个对话吗？')) {
    conversations.value = conversations.value.filter((c) => c.id !== id)
    saveConversations([...conversations.value])

    if (currentConversationId.value === id) {
      if (conversations.value.length > 0) {
        switchConversation(conversations.value[0].id)
      } else {
        createNewConversation()
      }
    }
  }
}

// 发送消息
const sendMessage = async () => {
  const message = inputText.value.trim()
  if (!message || isLoading.value) return

  // 如果没有当前对话，创建新对话
  if (!currentConversationId.value) {
    createNewConversation()
  }

  // 添加用户消息
  const userMessage = {
    role: 'user',
    content: message,
  }
  currentMessages.value.push(userMessage)
  inputText.value = ''
  isLoading.value = true
  scrollToBottom()

  try {
    if (isStreamMode.value) {
      // 流式模式
      console.log('[ChatView] 开始流式对话')
      
      // 判断是否需要工具调用（选择了 Agent）
      const useTools = selectedAgentId.value !== null
      
      if (useTools) {
        // 流式模式 + 工具调用：使用同步工具调用 + 流式展示结果
        console.log('[ChatView] 流式模式下使用工具调用（混合模式）')
        
        // 第一步：同步调用获取工具调用结果
        const requestData = {
          messages: buildMessages(),
          model: selectedModel.value,
          temperature: selectedAgent.value?.config?.temperature,
          maxTokens: selectedAgent.value?.config?.maxTokens,
          topP: selectedAgent.value?.config?.topP,
          agentId: selectedAgentId.value,
        }
        
        const response = await chatWithTools(requestData)
        
        console.log('[ChatView] 工具调用完成，收到响应:', response.data)
        
        // 显示工具调用记录
        if (response.data.toolCalls && response.data.toolCalls.length > 0) {
          console.log('[ChatView] 显示工具调用消息')
          const toolCallMessage = {
            role: 'tool-call',
            toolCalls: response.data.toolCalls,
          }
          currentMessages.value.push(toolCallMessage)
          scrollToBottom()
        }
        
        // 显示 AI 最终回答
        const assistantMessage = {
          role: 'assistant',
          content: response.data.content,
        }
        currentMessages.value.push(assistantMessage)
        scrollToBottom()
        saveCurrentConversation()
        isLoading.value = false
      } else {
        // 普通流式对话（无工具）
        console.log('[ChatView] 普通流式对话')
        
        // 创建 AI 消息对象并添加到列表
        const assistantMessage = reactive({
          role: 'assistant',
          content: ''
        })
        currentMessages.value.push(assistantMessage)
        
        // 强制滚动到底部
        scrollToBottom()

        await streamChat(
          {
            messages: buildMessages().slice(0, -1),
            model: selectedModel.value,
            stream: true,
            temperature: selectedAgent.value?.config?.temperature,
            maxTokens: selectedAgent.value?.config?.maxTokens,
            topP: selectedAgent.value?.config?.topP,
          },
          (data) => {
            // 接收增量数据
            console.log('[ChatView] 收到流式数据:', data)
            if (data.delta) {
              console.log('[ChatView] 添加增量内容:', data.delta)
              // 直接修改响应式对象的 content 属性
              assistantMessage.content += data.delta
              // 强制触发视图更新并滚动
              nextTick(() => {
                scrollToBottom()
              })
            }
          },
          (error) => {
            console.error('[ChatView] 流式对话错误:', error)
            assistantMessage.content = '抱歉，发生了错误，请稍后再试。'
            isLoading.value = false
          },
          () => {
            // 完成
            console.log('[ChatView] 流式对话完成，最终内容长度:', assistantMessage.content.length)
            isLoading.value = false
            saveCurrentConversation()
          }
        )
      }
    } else {
      // 同步模式
      console.log('[ChatView] 开始同步对话')
      
      // 判断是否使用工具调用接口
      const useTools = selectedAgentId.value !== null
      const apiFunction = useTools ? chatWithTools : chat
      
      const requestData = {
        messages: buildMessages(),
        model: selectedModel.value,
        temperature: selectedAgent.value?.config?.temperature,
        maxTokens: selectedAgent.value?.config?.maxTokens,
        topP: selectedAgent.value?.config?.topP,
      }
      
      // 如果使用工具，添加 agentId
      if (useTools) {
        requestData.agentId = selectedAgentId.value
      }
      
      const response = await apiFunction(requestData)
      
      console.log('[ChatView] 收到响应:', response.data)
      console.log('[ChatView] 工具调用记录:', response.data.toolCalls)
      console.log('[ChatView] AI 回答内容:', response.data.content)

      // 如果响应包含工具调用信息，先显示工具调用
      if (response.data.toolCalls && response.data.toolCalls.length > 0) {
        console.log('[ChatView] 显示工具调用消息')
        const toolCallMessage = {
          role: 'tool-call',
          toolCalls: response.data.toolCalls,
        }
        currentMessages.value.push(toolCallMessage)
        scrollToBottom()
      }

      // 显示 AI 的最终回答
      console.log('[ChatView] 显示 AI 回答')
      const assistantMessage = {
        role: 'assistant',
        content: response.data.content,
      }
      currentMessages.value.push(assistantMessage)
      scrollToBottom()
      saveCurrentConversation()
      isLoading.value = false
    }
  } catch (error) {
    console.error('[ChatView] 发送消息失败:', error)
    const errorMessage = {
      role: 'assistant',
      content: '抱歉，发生了错误，请稍后再试。错误信息：' + error.message,
    }
    currentMessages.value.push(errorMessage)
    isLoading.value = false
  }
}

// 获取模型列表
const fetchModels = async () => {
  try {
    const response = await getModels()
    models.value = response.data || []
    console.log('[ChatView] 获取到模型列表:', models.value)
  } catch (error) {
    console.error('[ChatView] 获取模型列表失败:', error)
    // 使用默认模型
    models.value = [
      { code: 'glm-4.5', name: 'GLM-4.5', description: '高性能模型' },
      { code: 'glm-4.6', name: 'GLM-4.6', description: '最新版本' },
    ]
  }
}

// 获取 Agent 列表
const fetchAgents = async () => {
  try {
    const response = await getAgentList({ status: 'ACTIVE', page: 1, size: 100 })
    // MyBatis-Plus 分页响应格式
    agents.value = response.data.records || []
    console.log('[ChatView] 获取到 Agent 列表:', agents.value.length)
  } catch (error) {
    console.error('[ChatView] 获取 Agent 列表失败:', error)
    agents.value = []
  }
}

// 处理 Agent 选择变更
const handleAgentChange = () => {
  if (selectedAgentId.value) {
    // 查找选中的 Agent
    selectedAgent.value = agents.value.find(a => a.id === selectedAgentId.value)
    
    if (selectedAgent.value && selectedAgent.value.config) {
      // 使用 Agent 的模型配置
      selectedModel.value = selectedAgent.value.config.model || 'glm-4.6'
    }
    
    console.log('[ChatView] 选择 Agent:', selectedAgent.value?.name)
  } else {
    selectedAgent.value = null
    console.log('[ChatView] 取消选择 Agent')
  }
}

// 构建消息列表（如果选择了 Agent，添加系统提示词）
const buildMessages = () => {
  const messages = []
  
  // 如果选择了 Agent，添加系统提示词
  if (selectedAgent.value && selectedAgent.value.systemPrompt) {
    messages.push({
      role: 'system',
      content: selectedAgent.value.systemPrompt
    })
    
    // 如果有角色提示词，也添加进去
    if (selectedAgent.value.rolePrompt) {
      messages.push({
        role: 'system',
        content: selectedAgent.value.rolePrompt
      })
    }
  }
  
  // 添加对话历史
  messages.push(...currentMessages.value.map(m => ({
    role: m.role,
    content: m.content
  })))
  
  return messages
}

// 初始化
onMounted(() => {
  console.log('[ChatView] 组件挂载，开始初始化')
  
  // 加载对话历史
  const savedConversations = loadConversations()
  conversations.value = savedConversations
  console.log('[ChatView] 加载历史对话数量:', savedConversations.length)

  // 如果有历史对话，加载最近的一个
  if (conversations.value.length > 0) {
    const lastConv = conversations.value[0]
    currentConversationId.value = lastConv.id
    currentMessages.value = [...lastConv.messages]
    console.log('[ChatView] 加载最近对话，消息数:', currentMessages.value.length)
  } else {
    // 创建新对话
    createNewConversation()
    console.log('[ChatView] 创建新对话')
  }

  // 获取模型列表和 Agent 列表
  fetchModels()
  fetchAgents()

  scrollToBottom()
})

// 监听消息变化，自动滚动
watch(() => currentMessages.value.length, () => {
  scrollToBottom()
})
</script>
