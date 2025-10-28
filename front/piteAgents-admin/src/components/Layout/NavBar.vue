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
        <router-link to="/tools" class="nav-item" active-class="active">
          <WrenchScrewdriverIcon class="nav-icon" />
          <span>工具管理</span>
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
  WrenchScrewdriverIcon,
  UserCircleIcon,
} from '@heroicons/vue/24/outline'
</script>

<style scoped>
/* 导航栏样式 - 扁平化设计 */

:root {
  --primary-color: #1976d2;
  --text-primary: #333;
  --text-secondary: #666;
  --text-hint: #999;
  --border-color: #e0e0e0;
  --bg-white: #fff;
  --bg-light: #f5f5f5;
}

.navbar {
  position: sticky;
  top: 0;
  z-index: 100;
  background: var(--bg-white);
  border-bottom: 1px solid var(--border-color);
}

.navbar-container {
  max-width: 1920px;
  margin: 0 auto;
  padding: 0 24px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
}

/* 品牌区域 */
.navbar-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  flex-shrink: 0;
}

.navbar-logo {
  width: 36px;
  height: 36px;
  background: var(--primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-icon {
  width: 22px;
  height: 22px;
  color: white;
}

.navbar-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--primary-color);
}

/* 导航菜单 */
.navbar-menu {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  justify-content: center;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 20px;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  text-decoration: none;
  position: relative;
  background: transparent;
  border: none;
}

.nav-item:hover {
  color: var(--text-primary);
}

.nav-item.active {
  color: var(--primary-color);
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--primary-color);
}

.nav-icon {
  width: 18px;
  height: 18px;
}

/* 右侧区域 */
.navbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  padding: 6px;
  cursor: pointer;
}

.user-info:hover {
  opacity: 0.7;
}

.user-icon {
  width: 24px;
  height: 24px;
  color: var(--text-secondary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .navbar-container {
    padding: 0 16px;
    height: 56px;
  }

  .navbar-title {
    display: none;
  }

  .navbar-menu {
    gap: 4px;
  }

  .nav-item {
    padding: 8px;
  }

  .nav-item span {
    display: none;
  }

  .nav-icon {
    width: 22px;
    height: 22px;
  }
}
</style>
