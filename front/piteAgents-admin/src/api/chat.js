import request from './request'

/**
 * 同步对话
 * @param {Object} data - 对话请求参数
 * @returns {Promise}
 */
export function chat(data) {
  return request({
    url: '/api/chat',
    method: 'post',
    data,
  })
}

/**
 * 流式对话
 * @param {Object} data - 对话请求参数
 * @param {Function} onMessage - 接收消息回调
 * @param {Function} onError - 错误回调
 * @param {Function} onComplete - 完成回调
 */
export async function streamChat(data, onMessage, onError, onComplete) {
  try {
    console.log('[StreamChat] 开始流式对话请求', data)
    
    const response = await fetch('http://localhost:8080/api/chat/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data),
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    console.log('[StreamChat] 连接成功，开始接收流式数据')
    
    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      
      if (done) {
        console.log('[StreamChat] 流式数据接收完成')
        if (onComplete) onComplete()
        break
      }

      // 解码新的数据块
      const chunk = decoder.decode(value, { stream: true })
      console.log('[StreamChat] 接收到数据块:', chunk)
      
      buffer += chunk
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        const trimmedLine = line.trim()
        if (!trimmedLine) continue
        
        console.log('[StreamChat] 处理行:', trimmedLine)
        
        if (trimmedLine.startsWith('data:')) {
          const jsonStr = trimmedLine.substring(5).trim()
          if (jsonStr) {
            try {
              const parsedData = JSON.parse(jsonStr)
              console.log('[StreamChat] 解析数据:', parsedData)
              
              if (onMessage) onMessage(parsedData)
              
              // 如果是最后一条消息
              if (parsedData.done) {
                console.log('[StreamChat] 收到结束标记')
                if (onComplete) onComplete()
                return
              }
            } catch (e) {
              console.error('[StreamChat] 解析 SSE 数据失败:', e, jsonStr)
            }
          }
        }
      }
    }
  } catch (error) {
    console.error('[StreamChat] 流式对话错误:', error)
    if (onError) onError(error)
  }
}

/**
 * 获取模型列表
 * @returns {Promise}
 */
export function getModels() {
  return request({
    url: '/api/models',
    method: 'get',
  })
}

