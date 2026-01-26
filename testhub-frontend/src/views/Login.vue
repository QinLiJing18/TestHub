<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>🤖 TestHub IoT</h1>
        <p>智能硬件测试管理平台</p>
      </div>

      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        @keyup.enter="handleLogin"
        label-position="top"
        label-width="auto"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            placeholder="请输入用户名"
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            clearable
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            style="width: 100%"
            @click="handleLogin"
            :loading="loading"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p class="demo-tip">💡 演示账号：admin / admin123</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/authStore'
import { ElMessage, ElForm } from 'element-plus'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: 'admin',
  password: 'admin123'
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不少于6位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  try {
    await formRef.value.validate()
    loading.value = true
    
    const success = await authStore.login(form.username, form.password)
    
    if (success) {
      ElMessage.success('登录成功！')
      router.push('/')
    } else {
      ElMessage.error('登录失败，请检查用户名或密码')
    }
  } catch (error) {
    ElMessage.error('登录失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  background: white;
  border-radius: 8px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);

  .login-header {
    text-align: center;
    margin-bottom: 30px;

    h1 {
      font-size: 28px;
      margin: 0 0 8px 0;
      color: #333;
    }

    p {
      font-size: 14px;
      color: #999;
      margin: 0;
    }
  }

  .login-footer {
    text-align: center;
    margin-top: 20px;

    .demo-tip {
      font-size: 12px;
      color: #999;
      margin: 0;
    }
  }
}

@media (max-width: 480px) {
  .login-box {
    margin: 20px;
    padding: 30px 20px;
  }
}
</style>
