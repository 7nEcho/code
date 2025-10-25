<template>
  <div class="main-layout">
    <!-- 顶部导航栏 -->
    <NavBar />

    <!-- 主内容区域 -->
    <div class="main-content-wrapper">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script setup>
import NavBar from './NavBar.vue'
</script>

<style scoped>
/* CSS 变量定义 */
:root {
  /* 过渡 */
  --transition-base: 200ms cubic-bezier(0.4, 0, 0.2, 1);
}

.main-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
}

.main-content-wrapper {
  flex: 1;
  overflow: hidden;
  position: relative;
}

/* 页面切换动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity var(--transition-base);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

