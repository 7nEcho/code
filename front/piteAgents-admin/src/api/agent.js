import request from './request'

/**
 * 获取 Agent 列表
 * @param {Object} params - 查询参数
 * @param {string} params.category - 分类（可选）
 * @param {string} params.status - 状态（可选）
 * @param {string} params.keyword - 关键词（可选）
 * @param {number} params.page - 页码（默认0）
 * @param {number} params.size - 每页大小（默认10）
 * @returns {Promise}
 */
export function getAgentList(params = {}) {
  return request({
    url: '/api/agents',
    method: 'get',
    params,
  })
}

/**
 * 获取 Agent 详情
 * @param {number|string} id - Agent ID
 * @returns {Promise}
 */
export function getAgentDetail(id) {
  return request({
    url: `/api/agents/${id}`,
    method: 'get',
  })
}

/**
 * 创建 Agent
 * @param {Object} data - Agent 数据
 * @param {string} data.name - Agent 名称（必填）
 * @param {string} data.description - Agent 描述
 * @param {string} data.avatar - Agent 头像URL
 * @param {string} data.category - Agent 分类
 * @param {string} data.systemPrompt - 系统提示词
 * @param {string} data.rolePrompt - 角色提示词
 * @param {Object} data.config - Agent 配置
 * @returns {Promise}
 */
export function createAgent(data) {
  return request({
    url: '/api/agents',
    method: 'post',
    data,
  })
}

/**
 * 更新 Agent
 * @param {number|string} id - Agent ID
 * @param {Object} data - 更新的数据
 * @returns {Promise}
 */
export function updateAgent(id, data) {
  return request({
    url: `/api/agents/${id}`,
    method: 'put',
    data,
  })
}

/**
 * 删除 Agent
 * @param {number|string} id - Agent ID
 * @returns {Promise}
 */
export function deleteAgent(id) {
  return request({
    url: `/api/agents/${id}`,
    method: 'delete',
  })
}

/**
 * 更新 Agent 配置
 * @param {number|string} id - Agent ID
 * @param {Object} config - 配置数据
 * @param {string} config.model - 模型代码
 * @param {number} config.temperature - 温度参数
 * @param {number} config.maxTokens - 最大 Token 数
 * @param {number} config.topP - Top P 参数
 * @param {string} config.extraParams - 其他参数（JSON 字符串）
 * @returns {Promise}
 */
export function updateAgentConfig(id, config) {
  return request({
    url: `/api/agents/${id}/config`,
    method: 'put',
    data: config,
  })
}

/**
 * 更新 Agent 状态
 * @param {number|string} id - Agent ID
 * @param {string} status - 新状态（ACTIVE/INACTIVE/ARCHIVED）
 * @returns {Promise}
 */
export function updateAgentStatus(id, status) {
  return request({
    url: `/api/agents/${id}/status`,
    method: 'put',
    data: { status },
  })
}

