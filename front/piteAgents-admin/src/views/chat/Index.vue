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
        </div>

        <div class="header-right">
          <!-- 模型选择 -->
          <select v-model="selectedModel" class="model-select">
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
            :key="`msg-${index}-${message.content.length}`"
            class="message-row"
          >
            <div :class="['message-bubble', message.role]">
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
                <div class="message-avatar" style="background: linear-gradient(135deg, var(--primary-500) 0%, var(--secondary-600) 100%)">
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
} from '@heroicons/vue/24/outline'
import { chat, streamChat, getModels } from '@/api/chat'
import { saveConversations, loadConversations } from '@/utils/storage'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

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
      // 流式模式 - 关键修复：使用响应式对象
      console.log('[ChatView] 开始流式对话')
      
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
          messages: currentMessages.value.slice(0, -1).map(m => ({
            role: m.role,
            content: m.content
          })),
          model: selectedModel.value,
          stream: true,
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
    } else {
      // 同步模式
      console.log('[ChatView] 开始同步对话')
      const response = await chat({
        messages: currentMessages.value,
        model: selectedModel.value,
      })

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

  // 获取模型列表
  fetchModels()

  scrollToBottom()
})

// 监听消息变化，自动滚动
watch(() => currentMessages.value.length, () => {
  scrollToBottom()
})
</script>

<style scoped>
/* 组件特定样式 */
</style>
