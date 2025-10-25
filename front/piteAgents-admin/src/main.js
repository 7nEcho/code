import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/styles/global.css'
import './assets/styles/chat.css'
import './assets/styles/markdown.css'
import './assets/styles/agent.css'

const app = createApp(App)

// 使用路由
app.use(router)

app.mount('#app')
