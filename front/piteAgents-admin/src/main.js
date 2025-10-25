import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/styles/markdown.css'

const app = createApp(App)

// 使用路由
app.use(router)

app.mount('#app')
