<template>
  <nav class="navbar">
    <div class="navbar-container">
      <!-- 左侧：Logo 和名称 -->
      <div class="navbar-brand">
        <div class="navbar-logo">
          <CpuChipIcon class="logo-icon" />
        </div>
        <span class="navbar-title">piteAgents</span>
      </div>

      <!-- 中间：导航菜单 -->
      <div class="navbar-menu">
        <router-link to="/chat" class="nav-item" active-class="active">
          <ChatBubbleLeftRightIcon class="nav-icon" />
          <span>聊天测试</span>
        </router-link>
        <router-link to="/agents" class="nav-item" active-class="active">
          <UserGroupIcon class="nav-icon" />
          <span>Agent 管理</span>
        </router-link>
      </div>

      <!-- 右侧：预留用户信息区域 -->
      <div class="navbar-right">
        <div class="user-info">
          <UserCircleIcon class="user-icon" />
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import {
  CpuChipIcon,
  ChatBubbleLeftRightIcon,
  UserGroupIcon,
  UserCircleIcon,
} from '@heroicons/vue/24/outline'
</script>

<style scoped>
.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--gray-200);
  box-shadow: var(--shadow-sm);
}

.navbar-container {
  max-width: 1920px;
  margin: 0 auto;
  padding: 0 clamp(1rem, 3vw, 2rem);
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-xl);
}

/* 品牌区域 */
.navbar-brand {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  cursor: pointer;
  flex-shrink: 0;
}

.navbar-logo {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-lg);
  background: linear-gradient(135deg, var(--primary-500) 0%, var(--secondary-600) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-md);
}

.logo-icon {
  width: 24px;
  height: 24px;
  color: white;
}

.navbar-title {
  font-size: 1.25rem;
  font-weight: 700;
  background: linear-gradient(135deg, var(--primary-600) 0%, var(--secondary-600) 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 导航菜单 */
.navbar-menu {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  flex: 1;
  justify-content: center;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm) var(--spacing-lg);
  border-radius: var(--radius-lg);
  font-size: 0.9375rem;
  font-weight: 500;
  color: var(--gray-600);
  text-decoration: none;
  transition: all var(--transition-base);
  position: relative;
}

.nav-item:hover {
  color: var(--primary-600);
  background: var(--gray-50);
}

.nav-item.active {
  color: var(--primary-600);
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.1) 0%, rgba(99, 102, 241, 0.1) 100%);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 50%;
  transform: translateX(-50%);
  width: 40%;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-500) 0%, var(--secondary-600) 100%);
  border-radius: 2px 2px 0 0;
}

.nav-icon {
  width: 20px;
  height: 20px;
}

/* 右侧区域 */
.navbar-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  padding: var(--spacing-sm);
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-base);
}

.user-info:hover {
  background: var(--gray-50);
}

.user-icon {
  width: 28px;
  height: 28px;
  color: var(--gray-500);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-container {
    padding: 0 1rem;
    height: 56px;
  }

  .navbar-title {
    display: none;
  }

  .navbar-menu {
    gap: 0.25rem;
  }

  .nav-item {
    padding: var(--spacing-sm);
  }

  .nav-item span {
    display: none;
  }

  .nav-icon {
    width: 24px;
    height: 24px;
  }
}
</style>

