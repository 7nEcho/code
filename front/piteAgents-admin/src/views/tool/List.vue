<template>
  <div class="tool-page">
    <div class="tool-card">
      <div class="tool-header">
        <div class="tool-header-left">
          <h1 class="tool-title">
            <WrenchScrewdriverIcon class="icon-lg" />
            工具管理
          </h1>
          <p class="tool-subtitle">统一管理大模型可用的外部工具，支持创建、更新、启用及绑定 Agent。</p>
        </div>
        <div class="tool-actions">
          <div class="tool-search-bar">
            <label class="tool-search-input">
              <MagnifyingGlassIcon class="icon-sm" />
              <input
                v-model="searchKeyword"
                type="text"
                placeholder="搜索工具名称或描述..."
              />
            </label>
            <select v-model="searchParams.isActive" class="tool-filter-select" @change="handleFilterChange">
              <option value="">全部状态</option>
              <option value="true">已启用</option>
              <option value="false">未启用</option>
            </select>
          </div>
          <router-link to="/tools/create" class="create-tool-button">
            <PlusIcon class="icon-sm" />
            新建工具
          </router-link>
        </div>
      </div>

      <div v-if="loading" class="tool-empty-state">
        <div class="loading-spinner"></div>
        <p>工具列表加载中...</p>
      </div>
      <template v-else>
        <table v-if="displayedTools.length > 0" class="tool-table">
          <thead>
            <tr>
              <th>工具信息</th>
              <th>请求配置</th>
              <th>状态</th>
              <th>最后更新时间</th>
              <th style="width: 220px">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="tool in displayedTools" :key="tool.id">
              <td>
                <div class="tool-name">
                  {{ tool.name }}
                  <span class="tool-type-badge" :class="(tool.toolType || 'HTTP').toLowerCase()">
                    {{ tool.toolType || 'HTTP' }}
                  </span>
                </div>
                <div class="tool-desc">{{ tool.description || '暂无描述' }}</div>
                <div v-if="tool.toolType === 'LOCAL'" class="tool-implementation">
                  {{ tool.implementationClass }}::{{ tool.methodName }}
                </div>
              </td>
              <td>
                <div class="tool-method">
                  <BoltIcon class="icon-sm" />
                  {{ tool.method }}
                </div>
                <div class="tool-endpoint">{{ tool.endpoint }}</div>
              </td>
              <td>
                <span
                  class="tool-status"
                  :class="tool.isActive ? 'active' : 'inactive'"
                >
                  <component :is="tool.isActive ? CheckCircleIcon : XCircleIcon" class="icon-sm" />
                  {{ tool.isActive ? '启用' : '停用' }}
                </span>
              </td>
              <td>
                <div>{{ formatDateTime(tool.updatedAt) }}</div>
                <div class="tool-hint">timeout: {{ tool.timeout }}ms · retry: {{ tool.retryCount }}</div>
              </td>
              <td>
                <div class="tool-actions-cell">
                  <button class="tool-action-button edit" @click="handleEdit(tool.id)">
                    <PencilIcon class="icon-sm" />
                    编辑
                  </button>
                  <button class="tool-action-button delete" @click="handleDelete(tool)">
                    <TrashIcon class="icon-sm" />
                    删除
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-else class="tool-empty-state">
          <PuzzlePieceIcon class="empty-icon" />
          <h3>暂无工具数据</h3>
          <p>点击右上角「新建工具」开始创建第一个工具。</p>
        </div>

        <div v-if="totalPages > 1" class="tool-pagination">
          <button
            :disabled="page <= 1"
            @click="changePage(page - 1)"
          >
            <ChevronLeftIcon class="icon-sm" />
          </button>
          <span>第 {{ page }} / {{ totalPages }} 页，共 {{ totalElements }} 条</span>
          <button
            :disabled="page >= totalPages"
            @click="changePage(page + 1)"
          >
            <ChevronRightIcon class="icon-sm" />
          </button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  WrenchScrewdriverIcon,
  MagnifyingGlassIcon,
  PlusIcon,
  BoltIcon,
  CheckCircleIcon,
  XCircleIcon,
  PencilIcon,
  TrashIcon,
  PuzzlePieceIcon,
  ChevronLeftIcon,
  ChevronRightIcon,
} from '@heroicons/vue/24/outline'
import { getToolList, deleteTool } from '@/api/tool'
import './styles.css'

const router = useRouter()
const loading = ref(false)
const tools = ref([])
const totalPages = ref(0)
const totalElements = ref(0)
const page = ref(1) // MyBatis-Plus 分页从1开始
const size = ref(10)
const searchKeyword = ref('')

const searchParams = reactive({
  isActive: '',
  sortField: '',
  sortDirection: '',
})

const displayedTools = computed(() => {
  if (!searchKeyword.value) {
    return tools.value
  }
  const keyword = searchKeyword.value.toLowerCase()
  return tools.value.filter(
    (tool) =>
      tool.name.toLowerCase().includes(keyword) ||
      (tool.description && tool.description.toLowerCase().includes(keyword))
  )
})

const fetchTools = async () => {
  try {
    loading.value = true
    const params = {
      page: page.value,
      size: size.value,
      sortField: searchParams.sortField || undefined,
      sortDirection: searchParams.sortDirection || undefined,
    }
    if (searchParams.isActive !== '') {
      params.isActive = searchParams.isActive === 'true'
    }
    const response = await getToolList(params)
    
    // MyBatis-Plus 分页响应格式
    const pageData = response.data
    tools.value = pageData.records || []
    totalPages.value = pageData.pages || 0
    totalElements.value = pageData.total || 0
    page.value = pageData.current || 1
  } catch (error) {
    console.error('加载工具列表失败:', error)
    alert(error.message || '加载工具列表失败，请稍后再试')
  } finally {
    loading.value = false
  }
}

const changePage = (newPage) => {
  if (newPage < 1 || newPage > totalPages.value) return
  page.value = newPage
  fetchTools()
}

const handleFilterChange = () => {
  page.value = 1 // 重置到第一页
  fetchTools()
}

const handleEdit = (id) => {
  router.push(`/tools/${id}/edit`)
}

const handleDelete = async (tool) => {
  if (!confirm(`确认删除工具「${tool.name}」吗？该操作无法撤回。`)) {
    return
  }
  try {
    await deleteTool(tool.id)
    alert('删除成功')
    fetchTools()
  } catch (error) {
    console.error('删除工具失败:', error)
    alert(error.message || '删除工具失败，请稍后再试')
  }
}

const formatDateTime = (value) => {
  if (!value) return '-'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

onMounted(() => {
  fetchTools()
})
</script>
