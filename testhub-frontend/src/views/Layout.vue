<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside width="200px" class="sidebar">
      <div class="logo">
        <span>🤖 TestHub</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        router
        class="menu"
      >
        <el-menu-item index="/" route="/">
          <el-icon><Dashboard /></el-icon>
          <span>仪表板</span>
        </el-menu-item>
        <el-menu-item index="/projects" route="/projects">
          <el-icon><Collection /></el-icon>
          <span>项目管理</span>
        </el-menu-item>
        <el-menu-item index="/devices" route="/devices">
          <el-icon><Monitor /></el-icon>
          <span>设备管理</span>
        </el-menu-item>
        <el-menu-item index="/testcases" route="/testcases">
          <el-icon><DocumentCopy /></el-icon>
          <span>测试用例</span>
        </el-menu-item>
        <el-menu-item index="/executions" route="/executions">
          <el-icon><DataAnalysis /></el-icon>
          <span>执行记录</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-right">
          <span class="username">{{ authStore.user?.username }}</span>
          <el-dropdown @command="handleCommand">
            <el-button link>
              退出登录 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import { ElMessage } from 'element-plus'
import {
  Dashboard,
  Collection,
  Monitor,
  DocumentCopy,
  DataAnalysis,
  ArrowDown
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const activeMenu = computed(() => route.path)

const handleCommand = (command) => {
  if (command === 'logout') {
    authStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100vh;
  background-color: #f5f7fa;
}

.sidebar {
  background-color: #001529;
  border-right: 1px solid #e4e7eb;
  overflow-y: auto;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    padding: 0 20px;
    color: white;
    font-size: 18px;
    font-weight: bold;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }

  .menu {
    border-right: none;
    background-color: #001529;

    :deep(.el-menu-item) {
      color: rgba(255, 255, 255, 0.65);
      
      &:hover {
        color: white !important;
      }

      &.is-active {
        background-color: #1890ff !important;
        color: white;
      }
    }
  }
}

.header {
  background-color: white;
  border-bottom: 1px solid #e4e7eb;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding-right: 20px;

  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;

    .username {
      color: #333;
      font-size: 14px;
    }
  }
}

.main-content {
  background-color: #f5f7fa;
  padding: 20px;
}
</style>
