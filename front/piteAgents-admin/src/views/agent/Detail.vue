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

<style scoped>
.agent-detail-page {
  height: 100%;
  overflow-y: auto;
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f0fe 50%, #f0e8ff 100%);
}

/* 加载和错误状态 */
.loading-container,
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  gap: var(--spacing-lg);
}

.loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid var(--gray-200);
  border-top-color: var(--primary-500);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-icon {
  width: 64px;
  height: 64px;
  color: var(--error);
}

.error-state h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--gray-700);
}

.error-state p {
  color: var(--gray-500);
}

/* 详情容器 */
.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: clamp(1.5rem, 3vh, 2rem);
}

/* 头部 */
.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--spacing-xl);
  gap: var(--spacing-lg);
  flex-wrap: wrap;
}

.back-button {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: 0.75rem 1.25rem;
  background: white;
  border: 2px solid var(--gray-200);
  border-radius: var(--radius-xl);
  font-size: 0.9375rem;
  font-weight: 500;
  color: var(--gray-700);
  cursor: pointer;
  transition: all var(--transition-base);
}

.back-button:hover {
  border-color: var(--gray-300);
  background: var(--gray-50);
}

.icon {
  width: 20px;
  height: 20px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.action-button {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.75rem 1.25rem;
  border-radius: var(--radius-xl);
  font-size: 0.9375rem;
  font-weight: 500;
  text-decoration: none;
  border: none;
  cursor: pointer;
  transition: all var(--transition-base);
}

.action-button.edit {
  background: rgba(59, 130, 246, 0.1);
  color: var(--primary-600);
}

.action-button.edit:hover {
  background: rgba(59, 130, 246, 0.2);
}

.action-button.active {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.action-button.active:hover {
  background: rgba(16, 185, 129, 0.2);
}

.action-button.inactive {
  background: rgba(156, 163, 175, 0.1);
  color: #6b7280;
}

.action-button.inactive:hover {
  background: rgba(156, 163, 175, 0.2);
}

.action-button.delete {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

.action-button.delete:hover {
  background: rgba(239, 68, 68, 0.2);
}

.icon-sm {
  width: 16px;
  height: 16px;
}

/* 详情内容 */
.detail-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

/* 信息卡片 */
.info-card {
  background: white;
  border-radius: var(--radius-2xl);
  padding: clamp(1.5rem, 3vw, 2rem);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--gray-200);
}

.card-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-xl);
  padding-bottom: var(--spacing-xl);
  border-bottom: 1px solid var(--gray-100);
  margin-bottom: var(--spacing-xl);
}

.agent-avatar-large {
  width: 96px;
  height: 96px;
  border-radius: var(--radius-2xl);
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--secondary-600) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  box-shadow: var(--shadow-lg);
  flex-shrink: 0;
}

.agent-avatar-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-icon {
  width: 48px;
  height: 48px;
  color: white;
}

.agent-title-group {
  flex: 1;
}

.agent-title {
  font-size: clamp(1.5rem, 3vw, 2rem);
  font-weight: 700;
  color: var(--gray-900);
  margin: 0 0 var(--spacing-md) 0;
}

.agent-badges {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.badge {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  padding: 0.5rem 1rem;
  border-radius: var(--radius-lg);
  font-size: 0.875rem;
  font-weight: 600;
}

.badge.category {
  background: var(--gray-100);
  color: var(--gray-700);
}

.badge.status.active {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.badge.status.inactive {
  background: rgba(156, 163, 175, 0.1);
  color: #6b7280;
}

.badge.status.archived {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

.badge-icon {
  width: 16px;
  height: 16px;
}

/* 卡片区块 */
.card-section {
  margin-bottom: var(--spacing-xl);
}

.card-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: var(--gray-900);
  margin: 0 0 var(--spacing-lg) 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.section-icon {
  width: 24px;
  height: 24px;
  color: var(--primary-500);
}

.section-content {
  font-size: 1rem;
  color: var(--gray-700);
  line-height: 1.75;
  margin: 0;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: var(--spacing-lg);
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.info-label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--gray-500);
}

.info-value {
  font-size: 1rem;
  font-weight: 600;
  color: var(--gray-900);
}

/* 配置网格 */
.config-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: var(--spacing-lg);
}

.config-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: var(--spacing-lg);
  background: var(--gray-50);
  border-radius: var(--radius-xl);
  border: 1px solid var(--gray-100);
}

.config-label {
  font-size: 0.8125rem;
  font-weight: 500;
  color: var(--gray-500);
}

.config-value {
  font-size: 1.125rem;
  font-weight: 700;
  color: var(--gray-900);
}

.config-value.primary {
  color: var(--primary-600);
}

/* 提示词内容 */
.prompt-content {
  padding: var(--spacing-lg);
  background: var(--gray-50);
  border-radius: var(--radius-xl);
  border: 1px solid var(--gray-200);
  font-size: 0.9375rem;
  color: var(--gray-700);
  line-height: 1.75;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    text-align: center;
  }

  .agent-avatar-large {
    width: 80px;
    height: 80px;
  }

  .agent-title {
    font-size: 1.5rem;
  }

  .header-actions {
    width: 100%;
    justify-content: stretch;
  }

  .action-button {
    flex: 1;
  }

  .action-button span {
    display: none;
  }

  .info-grid,
  .config-grid {
    grid-template-columns: 1fr;
  }
}
</style>

