<template>
  <div class="tool-form-page">
    <div class="tool-form-container">
      <div class="tool-form-header">
        <button class="tool-button cancel" type="button" @click="$router.back()">
          <ChevronLeftIcon class="icon-sm" />
          返回
        </button>
        <h1 class="tool-form-title">{{ isEditMode ? '编辑工具' : '创建工具' }}</h1>
      </div>

      <div v-if="loading" class="tool-empty-state">
        <div class="loading-spinner"></div>
        <p>工具信息加载中...</p>
      </div>

      <form v-else class="tool-form" @submit.prevent="handleSubmit">
        <section class="tool-form-section">
          <h2 class="tool-section-title">
            <WrenchScrewdriverIcon class="icon-sm" />
            基础信息
          </h2>
          <div class="tool-form-grid">
            <div class="tool-form-group">
              <label>工具名称 *</label>
              <input
                v-model.trim="formData.name"
                type="text"
                class="tool-input"
                placeholder="如：hello_pox 或 WeatherQuery"
                required
                maxlength="60"
              />
              <span class="tool-hint">名称将作为大模型调用时的 function.name，请保持唯一。</span>
            </div>
            <div class="tool-form-group">
              <label>HTTP 方法 *</label>
              <select v-model="formData.method" class="tool-select" required>
                <option v-for="method in httpMethods" :key="method" :value="method">
                  {{ method }}
                </option>
              </select>
            </div>
          </div>
          <div class="tool-form-group">
            <label>描述 *</label>
            <textarea
              v-model="formData.description"
              class="tool-textarea"
              placeholder="简要说明工具能力和使用场景，帮助模型决定是否调用。"
              rows="3"
              required
              maxlength="200"
            ></textarea>
          </div>
          <div class="tool-form-group">
            <label>接口 Endpoint *</label>
            <input
              v-model.trim="formData.endpoint"
              type="url"
              class="tool-input"
              placeholder="https://api.example.com/tools/hello-pox"
              required
            />
            <span class="tool-hint">需为可访问的完整 URL，后端会基于该配置调用真实服务。</span>
          </div>
        </section>

        <section class="tool-form-section">
          <h2 class="tool-section-title">
            <CommandLineIcon class="icon-sm" />
            参数与请求头
          </h2>
          <div class="tool-params-grid">
            <div class="tool-params-card">
              <h4>参数定义（JSON Schema）</h4>
              <p>用于描述工具函数的入参结构，传递给大模型帮助其构造调用参数。</p>
              <textarea
                v-model="parametersText"
                class="tool-textarea"
                rows="10"
                placeholder='{ "type": "object", "properties": { "name": { "type": "string" } } }'
              ></textarea>
              <span v-if="parametersError" class="json-error">{{ parametersError }}</span>
            </div>
            <div class="tool-params-card">
              <h4>请求头配置</h4>
              <p>以 JSON 对象形式定义 HTTP 请求头，如认证信息、Content-Type 等。</p>
              <textarea
                v-model="headersText"
                class="tool-textarea"
                rows="10"
                placeholder='{ "Content-Type": "application/json" }'
              ></textarea>
              <span v-if="headersError" class="json-error">{{ headersError }}</span>
            </div>
          </div>
        </section>

        <section class="tool-form-section">
          <h2 class="tool-section-title">
            <AdjustmentsHorizontalIcon class="icon-sm" />
            运行配置
          </h2>
          <div class="tool-form-grid">
            <div class="tool-form-group">
              <label>超时时间（毫秒）</label>
              <input
                v-model.number="formData.timeout"
                type="number"
                class="tool-input"
                min="1000"
                max="300000"
                step="500"
              />
              <span class="tool-hint">调用超时时间，默认 30000。数值过小可能导致调用提前失败。</span>
            </div>
            <div class="tool-form-group">
              <label>重试次数</label>
              <input
                v-model.number="formData.retryCount"
                type="number"
                class="tool-input"
                min="0"
                max="10"
              />
              <span class="tool-hint">调用失败时的最大重试次数，默认 3。</span>
            </div>
          </div>
          <div class="tool-form-group">
            <label>启用状态</label>
            <div class="tool-switch-row">
              <label class="tool-switch">
                <input v-model="formData.isActive" type="checkbox" />
                <span class="tool-slider"></span>
              </label>
              <span class="tool-hint">
                {{ formData.isActive ? '工具已启用，大模型在绑定后即可调用' : '工具停用，绑定的 Agent 也无法使用' }}
              </span>
            </div>
          </div>
        </section>

        <div class="tool-form-actions">
          <button class="tool-button cancel" type="button" @click="$router.back()">取消</button>
          <button class="tool-button primary" type="submit" :disabled="submitting">
            <span v-if="submitting">提交中...</span>
            <span v-else>{{ isEditMode ? '保存修改' : '创建工具' }}</span>
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
  ChevronLeftIcon,
  WrenchScrewdriverIcon,
  CommandLineIcon,
  AdjustmentsHorizontalIcon,
} from '@heroicons/vue/24/outline'
import { createTool, updateTool, getToolDetail } from '@/api/tool'
import './styles.css'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const submitting = ref(false)
const parametersText = ref('')
const headersText = ref('')
const parametersError = ref('')
const headersError = ref('')

const httpMethods = ['GET', 'POST', 'PUT', 'DELETE', 'PATCH']

const formData = reactive({
  name: '',
  description: '',
  endpoint: '',
  method: 'POST',
  timeout: 30000,
  retryCount: 3,
  isActive: true,
})

const isEditMode = computed(() => Boolean(route.params.id))

const parseJsonField = (value, errorRef, fieldName) => {
  if (!value || !value.trim()) {
    errorRef.value = ''
    return null
  }
  try {
    const parsed = JSON.parse(value)
    errorRef.value = ''
    if (typeof parsed !== 'object' || Array.isArray(parsed)) {
      throw new Error(`${fieldName} 必须是 JSON 对象`)
    }
    return parsed
  } catch (error) {
    errorRef.value = error.message
    throw error
  }
}

const loadToolDetail = async () => {
  if (!isEditMode.value) return
  try {
    loading.value = true
    const response = await getToolDetail(route.params.id)
    const tool = response.data
    formData.name = tool.name
    formData.description = tool.description
    formData.endpoint = tool.endpoint
    formData.method = tool.method || 'POST'
    formData.timeout = tool.timeout ?? 30000
    formData.retryCount = tool.retryCount ?? 3
    formData.isActive = tool.isActive ?? true
    parametersText.value = tool.parameters ? JSON.stringify(tool.parameters, null, 2) : ''
    headersText.value = tool.headers ? JSON.stringify(tool.headers, null, 2) : ''
  } catch (error) {
    console.error('加载工具详情失败:', error)
    alert(error.message || '加载工具详情失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!formData.name || !formData.description || !formData.endpoint) {
    alert('请填写必填项')
    return
  }

  let parametersPayload = null
  let headersPayload = null
  try {
    parametersPayload = parseJsonField(parametersText.value, parametersError, '参数定义')
    headersPayload = parseJsonField(headersText.value, headersError, '请求头配置')
  } catch (error) {
    return
  }

  const payload = {
    name: formData.name,
    description: formData.description,
    endpoint: formData.endpoint,
    method: formData.method,
    timeout: formData.timeout,
    retryCount: formData.retryCount,
    isActive: formData.isActive,
    parameters: parametersPayload,
    headers: headersPayload,
  }

  try {
    submitting.value = true
    if (isEditMode.value) {
      await updateTool(route.params.id, payload)
      alert('工具更新成功')
    } else {
      await createTool(payload)
      alert('工具创建成功')
    }
    router.push('/tools')
  } catch (error) {
    console.error('提交工具信息失败:', error)
    alert(error.message || '提交失败，请检查填写内容或稍后再试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadToolDetail()
})
</script>
