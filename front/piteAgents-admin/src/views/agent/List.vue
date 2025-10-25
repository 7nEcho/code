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
import './styles.css'

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

