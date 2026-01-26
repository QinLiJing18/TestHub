import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authService } from '../services/api'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const token = ref(localStorage.getItem('token'))
  const loading = ref(false)

  const login = async (username, password) => {
    loading.value = true
    try {
      const response = await authService.login(username, password)
      token.value = response.token
      user.value = {
        username: response.username,
        roles: response.roles || []
      }
      localStorage.setItem('token', response.token)
      return true
    } catch (error) {
      console.error('登录失败:', error)
      return false
    } finally {
      loading.value = false
    }
  }

  const logout = () => {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  const isLoggedIn = () => {
    return !!token.value
  }

  const hasRole = (role) => {
    return user.value?.roles?.includes(role)
  }

  return {
    user,
    token,
    loading,
    login,
    logout,
    isLoggedIn,
    hasRole
  }
})
