const STORAGE_KEY = 'chat_conversations'

/**
 * 保存对话历史到 localStorage
 * @param {Array} conversations - 对话列表
 */
export function saveConversations(conversations) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(conversations))
  } catch (error) {
    console.error('保存对话历史失败:', error)
  }
}

/**
 * 从 localStorage 读取对话历史
 * @returns {Array} 对话列表
 */
export function loadConversations() {
  try {
    const data = localStorage.getItem(STORAGE_KEY)
    return data ? JSON.parse(data) : []
  } catch (error) {
    console.error('读取对话历史失败:', error)
    return []
  }
}

/**
 * 清空对话历史
 */
export function clearConversations() {
  try {
    localStorage.removeItem(STORAGE_KEY)
  } catch (error) {
    console.error('清空对话历史失败:', error)
  }
}

