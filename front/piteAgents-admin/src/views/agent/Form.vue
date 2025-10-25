<template>
  <div class="agent-form-page">
    <div class="form-container">
      <!-- 表单头部 -->
      <div class="form-header">
        <button @click="$router.back()" class="back-button">
          <ArrowLeftIcon class="icon" />
          <span>返回</span>
        </button>
        <h1 class="form-title">{{ isEditMode ? '编辑 Agent' : '创建 Agent' }}</h1>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>加载中...</p>
      </div>

      <!-- 表单内容 -->
      <form v-else @submit.prevent="handleSubmit" class="agent-form">
        <!-- 基础信息 -->
        <div class="form-card">
          <h2 class="card-title">
            <InformationCircleIcon class="title-icon" />
            基础信息
          </h2>

          <div class="form-row">
            <div class="form-group required">
              <label class="form-label">Agent 名称</label>
              <input
                v-model="formData.name"
                type="text"
                class="form-input"
                placeholder="请输入 Agent 名称"
                maxlength="100"
                required
              />
              <span class="form-hint">{{ formData.name.length }}/100</span>
            </div>

            <div class="form-group">
              <label class="form-label">分类</label>
              <select v-model="formData.category" class="form-select">
                <option value="">请选择分类</option>
                <option value="通用">通用</option>
                <option value="编程">编程</option>
                <option value="写作">写作</option>
                <option value="翻译">翻译</option>
              </select>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">头像 URL</label>
            <input
              v-model="formData.avatar"
              type="url"
              class="form-input"
              placeholder="请输入头像图片 URL（可选）"
              maxlength="255"
            />
          </div>

          <div class="form-group">
            <label class="form-label">描述</label>
            <textarea
              v-model="formData.description"
              class="form-textarea"
              placeholder="请输入 Agent 描述"
              rows="4"
              maxlength="5000"
            ></textarea>
            <span class="form-hint">{{ formData.description.length }}/5000</span>
          </div>
        </div>

        <!-- 提示词设置 -->
        <div class="form-card">
          <h2 class="card-title">
            <ChatBubbleLeftIcon class="title-icon" />
            提示词设置
          </h2>

          <div class="form-group">
            <label class="form-label">系统提示词</label>
            <textarea
              v-model="formData.systemPrompt"
              class="form-textarea"
              placeholder="定义 AI 的行为和角色定位，例如：你是一个友好、专业的 AI 助手..."
              rows="6"
            ></textarea>
            <span class="form-hint">用于设定 AI 的基本行为和规则</span>
          </div>

          <div class="form-group">
            <label class="form-label">角色提示词</label>
            <textarea
              v-model="formData.rolePrompt"
              class="form-textarea"
              placeholder="细化 AI 的回复风格和规范，例如：请用清晰、易懂的语言回答..."
              rows="6"
            ></textarea>
            <span class="form-hint">用于细化 AI 的回复风格（可选）</span>
          </div>
        </div>

        <!-- 模型配置 -->
        <div class="form-card">
          <h2 class="card-title">
            <Cog6ToothIcon class="title-icon" />
            模型配置
          </h2>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">模型选择</label>
              <select v-model="formData.config.model" class="form-select">
                <option value="glm-4.5">GLM-4.5</option>
                <option value="glm-4.6">GLM-4.6（推荐）</option>
              </select>
              <span class="form-hint">选择使用的 AI 模型</span>
            </div>

            <div class="form-group">
              <label class="form-label">最大 Token 数</label>
              <input
                v-model.number="formData.config.maxTokens"
                type="number"
                class="form-input"
                placeholder="2000"
                min="1"
                max="8000"
              />
              <span class="form-hint">控制生成内容的最大长度</span>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">
              温度参数: {{ formData.config.temperature.toFixed(2) }}
            </label>
            <input
              v-model.number="formData.config.temperature"
              type="range"
              class="form-range"
              min="0"
              max="1"
              step="0.01"
            />
            <div class="range-labels">
              <span>更确定 (0.0)</span>
              <span>更随机 (1.0)</span>
            </div>
            <span class="form-hint">较低值输出更确定，较高值输出更有创意</span>
          </div>

          <div class="form-group">
            <label class="form-label">
              Top P 参数: {{ formData.config.topP.toFixed(2) }}
            </label>
            <input
              v-model.number="formData.config.topP"
              type="range"
              class="form-range"
              min="0"
              max="1"
              step="0.01"
            />
            <div class="range-labels">
              <span>更聚焦 (0.0)</span>
              <span>更多样 (1.0)</span>
            </div>
            <span class="form-hint">核采样参数，控制生成内容的多样性</span>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <button type="button" @click="$router.back()" class="action-button cancel">
            取消
          </button>
          <button type="submit" class="action-button submit" :disabled="submitting">
            <span v-if="submitting">提交中...</span>
            <span v-else>{{ isEditMode ? '保存更改' : '创建 Agent' }}</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowLeftIcon,
  InformationCircleIcon,
  ChatBubbleLeftIcon,
  Cog6ToothIcon,
} from '@heroicons/vue/24/outline'
import { getAgentDetail, createAgent, updateAgent } from '@/api/agent'

const route = useRoute()
const router = useRouter()

// 状态管理
const loading = ref(false)
const submitting = ref(false)
const isEditMode = computed(() => !!route.params.id && route.name === 'AgentEdit')

// 表单数据
const formData = reactive({
  name: '',
  description: '',
  avatar: '',
  category: '',
  systemPrompt: '',
  rolePrompt: '',
  config: {
    model: 'glm-4.6',
    temperature: 0.7,
    maxTokens: 2000,
    topP: 0.95,
  },
})

// 加载 Agent 数据（编辑模式）
const loadAgent = async () => {
  if (!isEditMode.value) return

  try {
    loading.value = true
    const response = await getAgentDetail(route.params.id)
    const agent = response.data

    // 填充表单数据
    formData.name = agent.name || ''
    formData.description = agent.description || ''
    formData.avatar = agent.avatar || ''
    formData.category = agent.category || ''
    formData.systemPrompt = agent.systemPrompt || ''
    formData.rolePrompt = agent.rolePrompt || ''

    if (agent.config) {
      formData.config.model = agent.config.model || 'glm-4.6'
      formData.config.temperature = agent.config.temperature || 0.7
      formData.config.maxTokens = agent.config.maxTokens || 2000
      formData.config.topP = agent.config.topP || 0.95
    }
  } catch (error) {
    console.error('加载 Agent 详情失败:', error)
    alert('加载 Agent 信息失败，请稍后重试')
    router.back()
  } finally {
    loading.value = false
  }
}

// 表单验证
const validateForm = () => {
  if (!formData.name.trim()) {
    alert('请输入 Agent 名称')
    return false
  }

  if (formData.name.length > 100) {
    alert('Agent 名称不能超过 100 个字符')
    return false
  }

  if (formData.description.length > 5000) {
    alert('Agent 描述不能超过 5000 个字符')
    return false
  }

  if (formData.avatar && formData.avatar.length > 255) {
    alert('头像 URL 不能超过 255 个字符')
    return false
  }

  if (formData.config.temperature < 0 || formData.config.temperature > 1) {
    alert('温度参数必须在 0-1 之间')
    return false
  }

  if (formData.config.topP < 0 || formData.config.topP > 1) {
    alert('Top P 参数必须在 0-1 之间')
    return false
  }

  if (formData.config.maxTokens < 1 || formData.config.maxTokens > 8000) {
    alert('最大 Token 数必须在 1-8000 之间')
    return false
  }

  return true
}

// 提交表单
const handleSubmit = async () => {
  if (!validateForm()) return

  try {
    submitting.value = true

    // 准备提交数据
    const submitData = {
      name: formData.name.trim(),
      description: formData.description.trim() || null,
      avatar: formData.avatar.trim() || null,
      category: formData.category || null,
      systemPrompt: formData.systemPrompt.trim() || null,
      rolePrompt: formData.rolePrompt.trim() || null,
      config: {
        model: formData.config.model,
        temperature: formData.config.temperature,
        maxTokens: formData.config.maxTokens,
        topP: formData.config.topP,
      },
    }

    if (isEditMode.value) {
      // 编辑模式
      await updateAgent(route.params.id, submitData)
      alert('更新成功')
      router.push(`/agents/${route.params.id}`)
    } else {
      // 创建模式
      const response = await createAgent(submitData)
      alert('创建成功')
      router.push(`/agents/${response.data.id}`)
    }
  } catch (error) {
    console.error('提交失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '未知错误'
    alert(`操作失败：${errorMsg}`)
  } finally {
    submitting.value = false
  }
}

// 初始化
onMounted(() => {
  if (isEditMode.value) {
    loadAgent()
  }
})
</script>

<style scoped>
.agent-form-page {
  height: 100%;
  overflow-y: auto;
  background: linear-gradient(135deg, #f0f4f8 0%, #e8f0fe 50%, #f0e8ff 100%);
}

/* 表单容器 */
.form-container {
  max-width: 900px;
  margin: 0 auto;
  padding: clamp(1.5rem, 3vh, 2rem);
}

/* 表单头部 */
.form-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-xl);
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

.form-title {
  font-size: clamp(1.5rem, 3vw, 2rem);
  font-weight: 700;
  background: linear-gradient(135deg, var(--primary-600) 0%, var(--secondary-600) 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
}

/* 加载状态 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-2xl);
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

/* 表单 */
.agent-form {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

/* 表单卡片 */
.form-card {
  background: white;
  border-radius: var(--radius-2xl);
  padding: clamp(1.5rem, 3vw, 2rem);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--gray-200);
}

.card-title {
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--gray-900);
  margin: 0 0 var(--spacing-xl) 0;
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.title-icon {
  width: 24px;
  height: 24px;
  color: var(--primary-500);
}

/* 表单行 */
.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: var(--spacing-lg);
}

/* 表单组 */
.form-group {
  margin-bottom: var(--spacing-lg);
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-group.required .form-label::after {
  content: ' *';
  color: var(--error);
}

.form-label {
  display: block;
  font-size: 0.9375rem;
  font-weight: 600;
  color: var(--gray-700);
  margin-bottom: var(--spacing-sm);
}

.form-input,
.form-select,
.form-textarea {
  width: 100%;
  padding: 0.875rem 1.125rem;
  border: 2px solid var(--gray-200);
  border-radius: var(--radius-xl);
  font-size: 0.9375rem;
  font-family: inherit;
  color: var(--gray-900);
  background: white;
  outline: none;
  transition: all var(--transition-base);
}

.form-input:focus,
.form-select:focus,
.form-textarea:focus {
  border-color: var(--primary-500);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.form-input::placeholder,
.form-textarea::placeholder {
  color: var(--gray-400);
}

.form-select {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' fill='none' viewBox='0 0 24 24' stroke='%236b7280'%3E%3Cpath stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='M19 9l-7 7-7-7'%3E%3C/path%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 0.875rem center;
  background-size: 1rem;
  padding-right: 2.5rem;
}

.form-textarea {
  resize: vertical;
  line-height: 1.6;
  min-height: 100px;
}

/* 滑块 */
.form-range {
  width: 100%;
  height: 8px;
  border-radius: 4px;
  background: var(--gray-200);
  outline: none;
  -webkit-appearance: none;
  appearance: none;
  cursor: pointer;
}

.form-range::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--secondary-600) 100%);
  cursor: pointer;
  box-shadow: var(--shadow-md);
}

.form-range::-moz-range-thumb {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--secondary-600) 100%);
  cursor: pointer;
  border: none;
  box-shadow: var(--shadow-md);
}

.range-labels {
  display: flex;
  justify-content: space-between;
  margin-top: var(--spacing-sm);
  font-size: 0.8125rem;
  color: var(--gray-500);
}

.form-hint {
  display: block;
  margin-top: var(--spacing-sm);
  font-size: 0.8125rem;
  color: var(--gray-500);
}

/* 表单操作 */
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  padding-top: var(--spacing-xl);
}

.action-button {
  padding: 1rem 2rem;
  border-radius: var(--radius-xl);
  font-size: 1rem;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all var(--transition-base);
  min-width: 120px;
}

.action-button.cancel {
  background: white;
  color: var(--gray-700);
  border: 2px solid var(--gray-200);
}

.action-button.cancel:hover {
  background: var(--gray-50);
  border-color: var(--gray-300);
}

.action-button.submit {
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--secondary-600) 100%);
  color: white;
  box-shadow: var(--shadow-md);
}

.action-button.submit:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.action-button.submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .action-button {
    width: 100%;
  }
}
</style>

