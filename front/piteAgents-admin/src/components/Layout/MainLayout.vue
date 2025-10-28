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
/* 主布局样式 - 扁平化设计 */

.main-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
  background: #fff;
}

.main-content-wrapper {
  flex: 1;
  overflow: hidden;
  position: relative;
}

/* 页面切换动画 - 简化 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 200ms ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

