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
                <span class="config-value">{{ agent.config?.temperature ?? 0.7 }}</span>
              </div>
              <div class="config-item">
                <span class="config-label">最大 Token</span>
                <span class="config-value">{{ agent.config?.maxTokens ?? 2000 }}</span>
              </div>
              <div class="config-item">
                <span class="config-label">Top P</span>
                <span class="config-value">{{ agent.config?.topP ?? 0.95 }}</span>
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

        <!-- 工具绑定卡片 -->
        <div class="info-card">
          <div class="card-section tool-manage-header">
            <div class="section-title">
              <WrenchScrewdriverIcon class="section-icon" />
              工具绑定
            </div>
            <button class="action-button edit" type="button" @click="openToolModal">
              <WrenchIcon class="icon-sm" />
              管理工具
            </button>
          </div>

          <div v-if="toolsLoading" class="tool-loading-state">
            <div class="loading-spinner"></div>
            <p>工具列表加载中...</p>
          </div>
          <div v-else-if="agentTools.length === 0" class="tool-empty-state">
            <PuzzlePieceIcon class="empty-icon" />
            <h3>尚未绑定工具</h3>
            <p>点击「管理工具」按钮，为 Agent 绑定可调用的工具。</p>
          </div>
          <div v-else class="tool-table-wrapper">
            <table class="agent-tool-table">
              <thead>
                <tr>
                  <th>工具信息</th>
                  <th>Endpoint</th>
                  <th>排序</th>
                  <th>启用状态</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="tool in agentTools" :key="tool.toolId">
                  <td>
                    <div class="tool-name">{{ tool.name }}</div>
                    <div class="tool-desc">{{ tool.description || '暂无描述' }}</div>
                  </td>
                  <td>
                    <div class="tool-endpoint">{{ tool.endpoint }}</div>
                    <div class="tool-method-tag">{{ tool.method }}</div>
                  </td>
                  <td class="tool-sort">{{ tool.sortOrder ?? 0 }}</td>
                  <td>
                    <span class="tool-status" :class="tool.enabled ? 'active' : 'inactive'">
                      {{ tool.enabled ? '启用' : '停用' }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
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

  <div v-if="toolModalVisible" class="tool-modal-overlay">
    <div class="tool-modal">
      <div class="tool-modal-header">
        <div>
          <h3>管理工具</h3>
          <p>选择要绑定到该 Agent 的工具，并设置启用状态、排序及个性化配置。</p>
        </div>
        <button class="tool-modal-close" type="button" @click="closeToolModal">
          <XMarkIcon class="icon-sm" />
        </button>
      </div>

      <div class="tool-modal-body">
        <div class="tool-modal-column">
          <div class="tool-modal-section-title">
            <MagnifyingGlassIcon class="icon-sm" />
            可用工具
          </div>
          <div class="tool-modal-search">
            <input
              v-model="toolSearchKeyword"
              type="text"
              placeholder="搜索工具名称或描述..."
            />
          </div>
          <div class="tool-modal-list">
            <div v-if="toolModalLoading" class="tool-modal-loading">
              <div class="loading-spinner"></div>
              <p>加载工具列表...</p>
            </div>
            <div v-else-if="filteredToolOptions.length === 0" class="tool-modal-empty">
              <PuzzlePieceIcon class="empty-icon" />
              <p>暂无符合条件的工具</p>
            </div>
            <label
              v-else
              v-for="option in filteredToolOptions"
              :key="option.id"
              class="tool-option-item"
            >
              <input
                type="checkbox"
                :checked="option.selected"
                @change="toggleToolSelection(option)"
              />
              <div class="tool-option-content">
                <div class="tool-option-title">
                  <span>{{ option.name }}</span>
                  <span class="tool-option-status" :class="option.isActive ? 'active' : 'inactive'">
                    {{ option.isActive ? '启用' : '停用' }}
                  </span>
                </div>
                <p>{{ option.description || '暂无描述' }}</p>
              </div>
            </label>
          </div>
        </div>

        <div class="tool-modal-column selected">
          <div class="tool-modal-section-title">
            <Squares2X2Icon class="icon-sm" />
            已选择工具 ({{ selectedToolOptions.length }})
          </div>
          <div v-if="selectedToolOptions.length === 0" class="tool-modal-empty">
            <p>请选择至少一个工具绑定。</p>
          </div>
          <div
            v-else
            v-for="option in selectedToolOptions"
            :key="`selected-${option.id}`"
            class="selected-tool-item"
          >
            <div class="selected-tool-header">
              <div>
                <h4>{{ option.name }}</h4>
                <p>{{ option.description || '暂无描述' }}</p>
              </div>
              <button class="remove-tool-button" type="button" @click="removeTool(option)">
                <TrashIcon class="icon-sm" />
              </button>
            </div>

            <div class="selected-tool-grid">
              <label>
                排序权重
                <input
                  type="number"
                  class="sort-input"
                  :value="option.sortOrder"
                  @input="updateSortOrder(option, $event.target.value)"
                />
              </label>
              <label class="tool-switch">
                <input type="checkbox" v-model="option.enabled" />
                <span class="tool-slider"></span>
                <span class="switch-text">{{ option.enabled ? '启用' : '停用' }}</span>
              </label>
            </div>

            <div class="tool-config-editor">
              <button
                class="config-toggle"
                type="button"
                @click="option.showConfig = !option.showConfig"
              >
                <Cog6ToothIcon class="icon-sm" />
                {{ option.showConfig ? '收起个性化配置' : '个性化配置 (可选)' }}
              </button>
              <transition name="fade">
                <div v-if="option.showConfig" class="config-textarea-wrapper">
                  <textarea
                    v-model="option.configText"
                    class="tool-textarea"
                    rows="4"
                    placeholder='{ "customKey": "value" }'
                  ></textarea>
                  <span v-if="option.configError" class="json-error">{{ option.configError }}</span>
                  <span v-else class="tool-hint">留空表示使用工具默认配置。</span>
                </div>
              </transition>
            </div>
          </div>
        </div>
      </div>

      <div class="tool-modal-footer">
        <button class="tool-button cancel" type="button" @click="closeToolModal">取消</button>
        <button class="tool-button primary" type="button" :disabled="bindingSubmitting" @click="submitToolBindings">
          <span v-if="bindingSubmitting">保存中...</span>
          <span v-else>保存绑定</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
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
  WrenchScrewdriverIcon,
  WrenchIcon,
  PuzzlePieceIcon,
  MagnifyingGlassIcon,
  Squares2X2Icon,
  XMarkIcon,
} from '@heroicons/vue/24/outline'
import { getAgentDetail, deleteAgent, updateAgentStatus } from '@/api/agent'
import { getAgentTools, getToolList, bindAgentTools } from '@/api/tool'
import './styles.css'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const agent = ref(null)

const agentTools = ref([])
const toolsLoading = ref(false)

const toolModalVisible = ref(false)
const toolModalLoading = ref(false)
const toolOptions = ref([])
const toolSearchKeyword = ref('')
const bindingSubmitting = ref(false)

const getStatusText = (status) => {
  const statusMap = {
    ACTIVE: '激活',
    INACTIVE: '未激活',
    ARCHIVED: '已归档',
  }
  return statusMap[status] || status
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return '-'
  const date = new Date(dateTime)
  if (Number.isNaN(date.getTime())) {
    return dateTime
  }
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  })
}

const loadAgentTools = async () => {
  try {
    toolsLoading.value = true
    const response = await getAgentTools(route.params.id)
    agentTools.value = response.data || []
  } catch (error) {
    console.error('加载 Agent 工具失败:', error)
    agentTools.value = []
  } finally {
    toolsLoading.value = false
  }
}

const loadAgent = async () => {
  try {
    loading.value = true
    const response = await getAgentDetail(route.params.id)
    agent.value = response.data
    await loadAgentTools()
  } catch (error) {
    console.error('加载 Agent 详情失败:', error)
    agent.value = null
  } finally {
    loading.value = false
  }
}

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
    await loadAgent()
  } catch (error) {
    console.error('更新状态失败:', error)
    alert(`${action}失败，请稍后重试`)
  }
}

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

const filteredToolOptions = computed(() => {
  if (!toolSearchKeyword.value) {
    return toolOptions.value
  }
  const keyword = toolSearchKeyword.value.toLowerCase()
  return toolOptions.value.filter(
    (item) =>
      item.name.toLowerCase().includes(keyword) ||
      (item.description && item.description.toLowerCase().includes(keyword))
  )
})

const selectedToolOptions = computed(() =>
  toolOptions.value
    .filter((item) => item.selected)
    .sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0))
)

const openToolModal = async () => {
  toolModalVisible.value = true
  toolModalLoading.value = true
  toolSearchKeyword.value = ''

  try {
    const response = await getToolList({ page: 0, size: 100 })
    const list = response.data?.content || []

    const existingMap = new Map()
    agentTools.value.forEach((tool, index) => {
      existingMap.set(tool.toolId, {
        sortOrder: tool.sortOrder ?? index,
        enabled: tool.enabled ?? true,
        configText: tool.toolConfig ? JSON.stringify(tool.toolConfig, null, 2) : '',
      })
    })

    const optionEntries = new Map()
    toolOptions.value = list.map((tool, index) => {
      const preset = existingMap.get(tool.id)
      const option = {
        id: tool.id,
        name: tool.name,
        description: tool.description,
        isActive: tool.isActive,
        selected: Boolean(preset),
        sortOrder: preset ? preset.sortOrder : index,
        enabled: preset ? preset.enabled : true,
        configText: preset ? preset.configText : '',
        configError: '',
        showConfig: Boolean(preset && preset.configText),
      }
      optionEntries.set(tool.id, option)
      return option
    })

    agentTools.value.forEach((tool, index) => {
      if (!optionEntries.has(tool.toolId)) {
        toolOptions.value.push({
          id: tool.toolId,
          name: tool.name,
          description: tool.description,
          isActive: tool.enabled,
          selected: true,
          sortOrder: tool.sortOrder ?? (toolOptions.value.length + index),
          enabled: tool.enabled ?? true,
          configText: tool.toolConfig ? JSON.stringify(tool.toolConfig, null, 2) : '',
          configError: '',
          showConfig: Boolean(tool.toolConfig),
        })
      }
    })
  } catch (error) {
    console.error('加载工具列表失败:', error)
    alert('加载工具列表失败，请稍后重试')
    toolModalVisible.value = false
  } finally {
    toolModalLoading.value = false
  }
}

const closeToolModal = () => {
  toolModalVisible.value = false
  toolOptions.value = []
  toolSearchKeyword.value = ''
}

const toggleToolSelection = (option) => {
  option.selected = !option.selected
  if (option.selected) {
    option.sortOrder =
      typeof option.sortOrder === 'number' && !Number.isNaN(option.sortOrder)
        ? option.sortOrder
        : selectedToolOptions.value.length
    option.enabled = option.enabled ?? true
  }
}

const removeTool = (option) => {
  option.selected = false
}

const updateSortOrder = (option, value) => {
  const numeric = Number(value)
  option.sortOrder = Number.isFinite(numeric) ? numeric : 0
}

const normalizeSortOrders = (options) => {
  const sorted = [...options].sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0))
  sorted.forEach((item, index) => {
    item.sortOrder = index
  })
}

const submitToolBindings = async () => {
  const selected = selectedToolOptions.value
  normalizeSortOrders(selected)

  const payload = {
    toolIds: selected.map((item) => item.id),
    configs: {},
  }

  let hasError = false

  selected.forEach((item, index) => {
    let configPayload = null
    if (item.configText && item.configText.trim()) {
      try {
        configPayload = JSON.parse(item.configText)
        item.configError = ''
      } catch (error) {
        item.configError = error.message
        hasError = true
      }
    } else {
      item.configError = ''
    }

    payload.configs[String(item.id)] = {
      sortOrder: item.sortOrder ?? index,
      enabled: item.enabled ?? true,
      toolConfig: configPayload,
    }
  })

  if (hasError) {
    alert('请修复工具个性化配置中的 JSON 错误后再提交。')
    return
  }

  try {
    bindingSubmitting.value = true
    await bindAgentTools(agent.value.id, payload)
    alert('工具绑定保存成功')
    closeToolModal()
    await loadAgentTools()
  } catch (error) {
    console.error('绑定工具失败:', error)
    alert(error.message || '保存工具绑定失败，请稍后再试')
  } finally {
    bindingSubmitting.value = false
  }
}

onMounted(() => {
  loadAgent()
})
</script>
