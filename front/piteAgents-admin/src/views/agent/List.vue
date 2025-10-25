<template>
  <div class="agent-list-page">
    <!-- 顶部搜索栏 -->
    <div class="search-bar">
      <div class="search-filters">
        <!-- 关键词搜索 -->
        <div class="search-input-wrapper">
          <MagnifyingGlassIcon class="search-icon" />
          <input
            v-model="searchParams.keyword"
            type="text"
            placeholder="搜索 Agent 名称或描述..."
            class="search-input"
            @input="handleSearch"
          />
        </div>

        <!-- 分类筛选 -->
        <select v-model="searchParams.category" class="filter-select" @change="handleSearch">
          <option value="">全部分类</option>
          <option value="通用">通用</option>
          <option value="编程">编程</option>
          <option value="写作">写作</option>
          <option value="翻译">翻译</option>
        </select>

        <!-- 状态筛选 -->
        <select v-model="searchParams.status" class="filter-select" @change="handleSearch">
          <option value="">全部状态</option>
          <option value="ACTIVE">激活</option>
          <option value="INACTIVE">未激活</option>
          <option value="ARCHIVED">已归档</option>
        </select>
      </div>

      <!-- 新建按钮 -->
      <router-link to="/agents/create" class="create-button">
        <PlusIcon class="icon" />
        <span>创建 Agent</span>
      </router-link>
    </div>

    <!-- Agent 列表 -->
    <div class="agent-list-container">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <!-- 空状态 -->
      <div v-else-if="agents.length === 0" class="empty-state">
        <UserGroupIcon class="empty-icon" />
        <h3>暂无 Agent</h3>
        <p>点击右上角创建您的第一个 Agent</p>
      </div>

      <!-- Agent 卡片列表 -->
      <div v-else class="agent-grid">
        <div v-for="agent in agents" :key="agent.id" class="agent-card">
          <!-- Agent 头部 -->
          <div class="agent-card-header">
            <div class="agent-avatar">
              <img v-if="agent.avatar" :src="agent.avatar" :alt="agent.name" />
              <CpuChipIcon v-else class="avatar-icon" />
            </div>
            <div class="agent-status" :class="agent.status.toLowerCase()">
              {{ getStatusText(agent.status) }}
            </div>
          </div>

          <!-- Agent 信息 -->
          <div class="agent-card-body">
            <h3 class="agent-name">{{ agent.name }}</h3>
            <p class="agent-description">
              {{ agent.description || '暂无描述' }}
            </p>
            <div class="agent-meta">
              <span class="agent-category">
                <TagIcon class="meta-icon" />
                {{ agent.category || '未分类' }}
              </span>
              <span class="agent-model">
                <CpuChipIcon class="meta-icon" />
                {{ agent.config?.model || 'glm-4.6' }}
              </span>
            </div>
          </div>

          <!-- Agent 操作 -->
          <div class="agent-card-footer">
            <router-link :to="`/agents/${agent.id}`" class="action-button view">
              <EyeIcon class="icon-sm" />
              <span>查看</span>
            </router-link>
            <router-link :to="`/agents/${agent.id}/edit`" class="action-button edit">
              <PencilIcon class="icon-sm" />
              <span>编辑</span>
            </router-link>
            <button @click="handleDelete(agent)" class="action-button delete">
              <TrashIcon class="icon-sm" />
              <span>删除</span>
            </button>
          </div>
        </div>
      </div>

      <!-- 分页器 -->
      <div v-if="totalPages > 1" class="pagination">
        <button
          @click="handlePageChange(currentPage - 1)"
          :disabled="currentPage === 0"
          class="pagination-button"
        >
          <ChevronLeftIcon class="icon-sm" />
        </button>
        <span class="pagination-info">
          第 {{ currentPage + 1 }} / {{ totalPages }} 页
        </span>
        <button
          @click="handlePageChange(currentPage + 1)"
          :disabled="currentPage >= totalPages - 1"
          class="pagination-button"
        >
          <ChevronRightIcon class="icon-sm" />
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  MagnifyingGlassIcon,
  PlusIcon,
  UserGroupIcon,
  CpuChipIcon,
  TagIcon,
  EyeIcon,
  PencilIcon,
  TrashIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
} from '@heroicons/vue/24/outline'
import { getAgentList, deleteAgent } from '@/api/agent'

const router = useRouter()

// 状态管理
const loading = ref(false)
const agents = ref([])
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)

// 搜索参数
const searchParams = reactive({
  keyword: '',
  category: '',
  status: '',
  page: 0,
  size: 12,
})

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    ACTIVE: '激活',
    INACTIVE: '未激活',
    ARCHIVED: '已归档',
  }
  return statusMap[status] || status
}

// 加载 Agent 列表
const loadAgents = async () => {
  try {
    loading.value = true
    const response = await getAgentList(searchParams)

    agents.value = response.data.content || []
    totalPages.value = response.data.totalPages || 0
    totalElements.value = response.data.totalElements || 0
    currentPage.value = response.data.number || 0
  } catch (error) {
    console.error('加载 Agent 列表失败:', error)
    alert('加载 Agent 列表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 搜索处理（防抖）
let searchTimeout = null
const handleSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    searchParams.page = 0
    loadAgents()
  }, 500)
}

// 翻页处理
const handlePageChange = (page) => {
  searchParams.page = page
  loadAgents()
}

// 删除 Agent
const handleDelete = async (agent) => {
  if (!confirm(`确定要删除 Agent "${agent.name}" 吗？此操作不可恢复。`)) {
    return
  }

  try {
    await deleteAgent(agent.id)
    alert('删除成功')
    loadAgents()
  } catch (error) {
    console.error('删除 Agent 失败:', error)
    alert('删除失败，请稍后重试')
  }
}

// 初始化
onMounted(() => {
  loadAgents()
})
</script>

<style scoped>
/* 页面容器 */
.agent-list-page {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f0fe 50%, #f0e8ff 100%);
}

/* 搜索栏 */
.search-bar {
  padding: clamp(1.5rem, 3vh, 2rem);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--gray-200);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
}

.search-filters {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex: 1;
  flex-wrap: wrap;
}

.search-input-wrapper {
  position: relative;
  flex: 1;
  min-width: 200px;
  max-width: 400px;
}

.search-icon {
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  color: var(--gray-400);
  pointer-events: none;
}

.search-input {
  width: 100%;
  padding: 0.75rem 1rem 0.75rem 2.75rem;
  border: 2px solid var(--gray-200);
  border-radius: var(--radius-xl);
  font-size: 0.9375rem;
  outline: none;
  transition: all var(--transition-base);
}

.search-input:focus {
  border-color: var(--primary-500);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.filter-select {
  padding: 0.75rem 2.5rem 0.75rem 1rem;
  border: 2px solid var(--gray-200);
  border-radius: var(--radius-xl);
  font-size: 0.9375rem;
  background: white;
  cursor: pointer;
  outline: none;
  transition: all var(--transition-base);
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%236b7280'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M19 9l-7 7-7-7'%3E%3C/path%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.75rem center;
  background-size: 1rem;
}

.filter-select:hover {
  border-color: var(--gray-300);
}

.filter-select:focus {
  border-color: var(--primary-500);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.create-button {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--secondary-600) 100%);
  color: white;
  border-radius: var(--radius-xl);
  font-weight: 600;
  font-size: 0.9375rem;
  text-decoration: none;
  transition: all var(--transition-base);
  box-shadow: var(--shadow-md);
}

.create-button:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.icon {
  width: 20px;
  height: 20px;
}

/* Agent 列表容器 */
.agent-list-container {
  flex: 1;
  overflow-y: auto;
  padding: clamp(1.5rem, 3vh, 2rem);
}

/* 加载状态 */
.loading-state {
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

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-align: center;
  color: var(--gray-500);
}

.empty-icon {
  width: 64px;
  height: 64px;
  color: var(--gray-300);
  margin-bottom: var(--spacing-lg);
}

.empty-state h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--gray-700);
  margin-bottom: var(--spacing-sm);
}

/* Agent 网格 */
.agent-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: clamp(1.25rem, 2vw, 1.75rem);
  max-width: 1400px;
  margin: 0 auto;
}

/* Agent 卡片 */
.agent-card {
  background: white;
  border-radius: var(--radius-2xl);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
  border: 1px solid var(--gray-200);
}

.agent-card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-4px);
  border-color: var(--primary-200);
}

.agent-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.agent-avatar {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-xl);
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--secondary-600) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.agent-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-icon {
  width: 32px;
  height: 32px;
  color: white;
}

.agent-status {
  padding: 0.375rem 0.75rem;
  border-radius: var(--radius-lg);
  font-size: 0.75rem;
  font-weight: 600;
}

.agent-status.active {
  background: rgba(16, 185, 129, 0.1);
  color: #059669;
}

.agent-status.inactive {
  background: rgba(156, 163, 175, 0.1);
  color: #6b7280;
}

.agent-status.archived {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

.agent-card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.agent-name {
  font-size: 1.125rem;
  font-weight: 700;
  color: var(--gray-900);
  margin: 0;
}

.agent-description {
  font-size: 0.875rem;
  color: var(--gray-600);
  line-height: 1.5;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.agent-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex-wrap: wrap;
  margin-top: var(--spacing-sm);
}

.agent-meta span {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.8125rem;
  color: var(--gray-500);
}

.meta-icon {
  width: 16px;
  height: 16px;
}

.agent-card-footer {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--gray-100);
}

.action-button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.375rem;
  padding: 0.625rem;
  border-radius: var(--radius-lg);
  font-size: 0.8125rem;
  font-weight: 500;
  text-decoration: none;
  border: none;
  cursor: pointer;
  transition: all var(--transition-base);
}

.action-button.view {
  background: var(--gray-50);
  color: var(--gray-700);
}

.action-button.view:hover {
  background: var(--gray-100);
}

.action-button.edit {
  background: rgba(59, 130, 246, 0.1);
  color: var(--primary-600);
}

.action-button.edit:hover {
  background: rgba(59, 130, 246, 0.2);
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

/* 分页器 */
.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-lg);
  margin-top: var(--spacing-2xl);
  padding: var(--spacing-lg);
}

.pagination-button {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid var(--gray-200);
  border-radius: var(--radius-lg);
  background: white;
  cursor: pointer;
  transition: all var(--transition-base);
}

.pagination-button:hover:not(:disabled) {
  border-color: var(--primary-500);
  background: rgba(59, 130, 246, 0.05);
}

.pagination-button:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.pagination-info {
  font-size: 0.9375rem;
  font-weight: 500;
  color: var(--gray-700);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .agent-grid {
    grid-template-columns: 1fr;
  }

  .search-bar {
    padding: 1rem;
  }

  .search-input-wrapper {
    max-width: 100%;
  }

  .action-button span {
    display: none;
  }
}
</style>

