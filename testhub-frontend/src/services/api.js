import axios from 'axios'

// 创建 axios 实例
const api = axios.create({
  baseURL: 'http://localhost:8001/api',
  timeout: 5000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加 JWT Token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    // 后端返回 Result 格式：{ code, message, data, timestamp }
    const result = response.data
    if (result.code === 200) {
      return result.data
    } else if (result.code === 401) {
      // 未认证，清除 token 并跳转登录
      localStorage.removeItem('token')
      window.location.href = '/login'
      return Promise.reject(new Error(result.message))
    } else {
      return Promise.reject(new Error(result.message))
    }
  },
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

// ==================== 认证服务 ====================
export const authService = {
  /**
   * 用户登录
   * POST /api/auth/login
   */
  login: (username, password) => {
    return api.post('/auth/login', { username, password })
  },

  /**
   * 用户注册
   * POST /api/auth/register
   */
  register: (username, password, email) => {
    return api.post('/auth/register', { username, password, email })
  },

  /**
   * 获取当前用户信息
   * GET /api/auth/user
   */
  getCurrentUser: () => {
    return api.get('/auth/user')
  },

  /**
   * 用户登出
   * POST /api/auth/logout
   */
  logout: () => {
    return api.post('/api/auth/logout')
  }
}

// ==================== 项目服务 ====================
export const projectService = {
  /**
   * 获取项目列表
   * GET /api/project/projects
   */
  getProjects: (params = {}) => {
    return api.get('/project/projects', { params })
  },

  /**
   * 创建项目
   * POST /api/project/projects
   */
  createProject: (data) => {
    return api.post('/project/projects', data)
  },

  /**
   * 更新项目
   * PUT /api/project/projects/{id}
   */
  updateProject: (id, data) => {
    return api.put(`/project/projects/${id}`, data)
  },

  /**
   * 删除项目
   * DELETE /api/project/projects/{id}
   */
  deleteProject: (id) => {
    return api.delete(`/project/projects/${id}`)
  },

  /**
   * 获取设备列表
   * GET /api/project/devices
   */
  getDevices: (params = {}) => {
    return api.get('/project/devices', { params })
  },

  /**
   * 创建设备
   * POST /api/project/devices
   * @param data: { deviceName, deviceType, mqttClientId, projectId }
   */
  createDevice: (data) => {
    return api.post('/project/devices', data)
  },

  /**
   * 更新设备
   * PUT /api/project/devices/{id}
   */
  updateDevice: (id, data) => {
    return api.put(`/project/devices/${id}`, data)
  },

  /**
   * 删除设备
   * DELETE /api/project/devices/{id}
   */
  deleteDevice: (id) => {
    return api.delete(`/project/devices/${id}`)
  },

  /**
   * 获取设备在线状态
   * GET /api/project/devices/{id}/status
   */
  getDeviceStatus: (id) => {
    return api.get(`/project/devices/${id}/status`)
  }
}

// ==================== 测试用例服务 ====================
export const testcaseService = {
  /**
   * 获取测试用例列表
   * GET /api/testcase/cases
   */
  getCases: (params = {}) => {
    return api.get('/testcase/cases', { params })
  },

  /**
   * 创建测试用例
   * POST /api/testcase/cases
   * @param data: { title, deviceId, testType, steps, description }
   */
  createCase: (data) => {
    return api.post('/testcase/cases', data)
  },

  /**
   * 更新测试用例
   * PUT /api/testcase/cases/{id}
   */
  updateCase: (id, data) => {
    return api.put(`/testcase/cases/${id}`, data)
  },

  /**
   * 删除测试用例
   * DELETE /api/testcase/cases/{id}
   */
  deleteCase: (id) => {
    return api.delete(`/testcase/cases/${id}`)
  },

  /**
   * 执行测试用例
   * POST /api/testcase/execute/{caseId}
   */
  executeCase: (caseId) => {
    return api.post(`/testcase/execute/${caseId}`)
  },

  /**
   * 获取测试执行记录
   * GET /api/testcase/executions
   */
  getExecutions: (params = {}) => {
    return api.get('/testcase/executions', { params })
  },

  /**
   * 获取测试报告
   * GET /api/testcase/reports/{executionId}
   */
  getReport: (executionId) => {
    return api.get(`/testcase/reports/${executionId}`)
  },

  /**
   * 获取实时执行日志（WebSocket）
   */
  getExecutionLogs: (executionId) => {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    return new WebSocket(`${protocol}//localhost:8083/api/testcase/logs/${executionId}`)
  }
}

export default api
