<template>
  <div class="not-found">
    <div class="container">
      <div class="error-code">404</div>
      <div class="error-message">页面未找到</div>
      <p class="error-description">抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}
</script>

<style scoped lang="scss">
.not-found {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

  .container {
    text-align: center;
    background: white;
    padding: 60px 40px;
    border-radius: 8px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);

    .error-code {
      font-size: 80px;
      font-weight: bold;
      color: #667eea;
      margin-bottom: 20px;
    }

    .error-message {
      font-size: 24px;
      color: #333;
      margin-bottom: 10px;
    }

    .error-description {
      font-size: 14px;
      color: #999;
      margin-bottom: 30px;
    }
  }
}
</style>
