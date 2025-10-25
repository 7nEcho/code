import request from './request'

/**
 * 获取工具列表
 * @param {Object} params 查询参数
 * @param {number} params.page 页码
 * @param {number} params.size 每页大小
 * @param {boolean} params.isActive 是否启用
 */
export function getToolList(params = {}) {
  return request({
    url: '/api/tools',
    method: 'get',
    params,
  })
}

/**
 * 获取工具详情
 * @param {number|string} id 工具 ID
 */
export function getToolDetail(id) {
  return request({
    url: `/api/tools/${id}`,
    method: 'get',
  })
}

/**
 * 创建工具
 * @param {Object} data 工具信息
 */
export function createTool(data) {
  return request({
    url: '/api/tools',
    method: 'post',
    data,
  })
}

/**
 * 更新工具
 * @param {number|string} id 工具 ID
 * @param {Object} data 工具信息
 */
export function updateTool(id, data) {
  return request({
    url: `/api/tools/${id}`,
    method: 'put',
    data,
  })
}

/**
 * 删除工具
 * @param {number|string} id 工具 ID
 */
export function deleteTool(id) {
  return request({
    url: `/api/tools/${id}`,
    method: 'delete',
  })
}

/**
 * 绑定 Agent 的工具列表
 * @param {number|string} agentId Agent ID
 * @param {Object} data 绑定数据
 */
export function bindAgentTools(agentId, data) {
  return request({
    url: `/api/agents/${agentId}/tools`,
    method: 'post',
    data,
  })
}

/**
 * 获取 Agent 的工具列表
 * @param {number|string} agentId Agent ID
 */
export function getAgentTools(agentId) {
  return request({
    url: `/api/agents/${agentId}/tools`,
    method: 'get',
  })
}
