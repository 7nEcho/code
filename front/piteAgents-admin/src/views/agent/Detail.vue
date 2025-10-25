<template>
  <div class="agent-detail-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- Agent 详情 -->
    <div v-else-if="agent" class="detail-container">
      <!-- 返回和操作栏 -->
      <div class="detail-header">
        <button @click="$router.back()" class="back-button">
          <ArrowLeftIcon class="icon" />
          <span>返回</span>
        </button>

        <div class="header-actions">
          <router-link :to="`/agents/${agent.id}/edit`" class="action-button edit">
            <PencilIcon class="icon-sm" />
            <span>编辑</span>
          </router-link>
          <button @click="handleToggleStatus" class="action-button" :class="agent.isActive ? 'inactive' : 'active'">
            <span>{{ agent.isActive ? '禁用' : '启用' }}</span>
          </button>
          <button @click="handleDelete" class="action-button delete">
            <TrashIcon class="icon-sm" />
            <span>删除</span>
          </button>
        </div>
      </div>

      <!-- 详情内容 -->
      <div class="detail-content">
        <!-- 基础信息卡片 -->
        <div class="info-card">
          <div class="card-header">
            <div class="agent-avatar-large">
              <img v-if="agent.avatar" :src="agent.avatar" :alt="agent.name" />
              <CpuChipIcon v-else class="avatar-icon" />
            </div>
            <div class="agent-title-group">
              <h1 class="agent-title">{{ agent.name }}</h1>
              <div class="agent-badges">
                <span class="badge category">
                  <TagIcon class="badge-icon" />
                  {{ agent.category || '未分类' }}
                </span>
                <span class="badge status" :class="agent.status.toLowerCase()">
                  {{ getStatusText(agent.status) }}
                </span>
              </div>
            </div>
          </div>

          <div class="card-section">
            <h3 class="section-title">描述</h3>
            <p class="section-content">{{ agent.description || '暂无描述' }}</p>
          </div>

          <div class="card-section">
            <h3 class="section-title">基础信息</h3>
            <div class="info-grid">
              <div class="info-item">
                <span class="info-label">Agent ID</span>
                <span class="info-value">{{ agent.id }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">状态</span>
                <span class="info-value">{{ agent.isActive ? '已启用' : '已禁用' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">创建时间</span>
                <span class="info-value">{{ formatDateTime(agent.createdAt) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">更新时间</span>
                <span class="info-value">{{ formatDateTime(agent.updatedAt) }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 配置信息卡片 -->
        <div class="info-card">
          <div class="card-section">
            <h3 class="section-title">
              <Cog6ToothIcon class="section-icon" />
              模型配置
            </h3>
            <div class="config-grid">
              <div class="config-item">
                <span class="config-label">模型</span>
                <span class="config-value primary">{{ agent.config?.model || 'glm-4.6' }}</span>
              </div>
              <div class="config-item">
                <span class="config-label">温度参数</span>
                <span class="config-value">{{ agent.config?.temperature || 0.7 }}</span>
              </div>
              <div class="config-item">
                <span class="config-label">最大 Token</span>
                <span class="config-value">{{ agent.config?.maxTokens || 2000 }}</span>
              </div>
              <div class="config-item">
                <span class="config-label">Top P</span>
                <span class="config-value">{{ agent.config?.topP || 0.95 }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 提示词卡片 -->
        <div class="info-card">
          <div class="card-section">
            <h3 class="section-title">
              <ChatBubbleLeftIcon class="section-icon" />
              系统提示词
            </h3>
            <div class="prompt-content">
              {{ agent.systemPrompt || '未设置系统提示词' }}
            </div>
          </div>

          <div v-if="agent.rolePrompt" class="card-section">
            <h3 class="section-title">
              <UserIcon class="section-icon" />
              角色提示词
            </h3>
            <div class="prompt-content">
              {{ agent.rolePrompt }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-state">
      <ExclamationCircleIcon class="error-icon" />
      <h3>加载失败</h3>
      <p>无法加载 Agent 信息，请稍后重试</p>
      <button @click="$router.back()" class="back-button">返回列表</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowLeftIcon,
  PencilIcon,
  TrashIcon,
  CpuChipIcon,
  TagIcon,
  Cog6ToothIcon,
  ChatBubbleLeftIcon,
  UserIcon,
  ExclamationCircleIcon,
} from '@heroicons/vue/24/outline'
import { getAgentDetail, deleteAgent, updateAgentStatus } from '@/api/agent'
import './styles.css'

const route = useRoute()
const router = useRouter()

// 状态管理
const loading = ref(true)
const agent = ref(null)

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    ACTIVE: '激活',
    INACTIVE: '未激活',
    ARCHIVED: '已归档',
  }
  return statusMap[status] || status
}

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

// 加载 Agent 详情
const loadAgent = async () => {
  try {
    loading.value = true
    const response = await getAgentDetail(route.params.id)
    agent.value = response.data
  } catch (error) {
    console.error('加载 Agent 详情失败:', error)
    agent.value = null
  } finally {
    loading.value = false
  }
}

// 切换启用状态
const handleToggleStatus = async () => {
  if (!agent.value) return

  const newStatus = agent.value.isActive ? 'INACTIVE' : 'ACTIVE'
  const action = agent.value.isActive ? '禁用' : '启用'

  if (!confirm(`确定要${action} Agent "${agent.value.name}" 吗？`)) {
    return
  }

  try {
    await updateAgentStatus(agent.value.id, newStatus)
    alert(`${action}成功`)
    loadAgent()
  } catch (error) {
    console.error('更新状态失败:', error)
    alert(`${action}失败，请稍后重试`)
  }
}

// 删除 Agent
const handleDelete = async () => {
  if (!agent.value) return

  if (!confirm(`确定要删除 Agent "${agent.value.name}" 吗？此操作不可恢复。`)) {
    return
  }

  try {
    await deleteAgent(agent.value.id)
    alert('删除成功')
    router.push('/agents')
  } catch (error) {
    console.error('删除 Agent 失败:', error)
    alert('删除失败，请稍后重试')
  }
}

// 初始化
onMounted(() => {
  loadAgent()
})
</script>

