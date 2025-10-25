import { createRouter, createWebHistory } from 'vue-router'

// 导入页面组件
const ChatView = () => import('@/views/chat/Index.vue')
const AgentList = () => import('@/views/agent/List.vue')
const AgentDetail = () => import('@/views/agent/Detail.vue')
const AgentForm = () => import('@/views/agent/Form.vue')

/**
 * 路由配置
 */
const routes = [
  {
    path: '/',
    redirect: '/chat',
  },
  {
    path: '/chat',
    name: 'Chat',
    component: ChatView,
    meta: {
      title: '聊天测试',
    },
  },
  {
    path: '/agents',
    name: 'AgentList',
    component: AgentList,
    meta: {
      title: 'Agent 管理',
    },
  },
  {
    path: '/agents/create',
    name: 'AgentCreate',
    component: AgentForm,
    meta: {
      title: '创建 Agent',
    },
  },
  {
    path: '/agents/:id',
    name: 'AgentDetail',
    component: AgentDetail,
    meta: {
      title: 'Agent 详情',
    },
  },
  {
    path: '/agents/:id/edit',
    name: 'AgentEdit',
    component: AgentForm,
    meta: {
      title: '编辑 Agent',
    },
  },
]

/**
 * 创建路由实例
 * 使用 History 模式
 */
const router = createRouter({
  history: createWebHistory(),
  routes,
})

/**
 * 路由守卫 - 设置页面标题
 */
router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - 智谱AI对话助手`
  }
  next()
})

export default router

