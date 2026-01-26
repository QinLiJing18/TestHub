# TestHub IoT 前端

智能硬件测试管理平台前端应用（Vue 3 + Vite）

## 📦 项目结构

```
testhub-frontend/
├── src/
│   ├── views/              # 页面组件
│   │   ├── Login.vue       # 登录页面
│   │   ├── Layout.vue      # 主布局（含侧边栏和顶部栏）
│   │   ├── Dashboard.vue   # 仪表板
│   │   ├── Projects.vue    # 项目管理
│   │   ├── Devices.vue     # 设备管理
│   │   ├── TestCases.vue   # 测试用例管理
│   │   ├── Executions.vue  # 执行记录
│   │   └── NotFound.vue    # 404 页面
│   ├── services/           # API 服务层
│   │   └── api.js          # 后端接口调用（authService, projectService, testcaseService）
│   ├── stores/             # Pinia 状态管理
│   │   └── authStore.js    # 认证状态管理
│   ├── router/             # Vue Router 路由
│   │   └── index.js        # 路由配置和守卫
│   ├── assets/             # 静态资源
│   ├── App.vue             # 根组件
│   └── main.js             # 应用入口
├── package.json            # 项目配置
├── vite.config.js          # Vite 构建配置
└── index.html              # HTML 模板
```

## 🚀 快速开始

### 1. 安装依赖

```bash
cd testhub-frontend
npm install
```

### 2. 启动开发服务器

```bash
npm run dev
```

访问：http://localhost:5173

### 3. 生产打包

```bash
npm run build
```

## 📋 演示账号

- **用户名**：admin
- **密码**：admin123

## 🔌 API 接口配置

前端默认连接到后端 API Gateway：`http://localhost:8001`

### 支持的后端服务

| 服务 | 端口 | 说明 |
|------|------|------|
| API Gateway | 8001 | 路由转发、认证 |
| Auth 服务 | 9001 | 用户认证（通过网关调用） |
| Project 服务 | 8082 | 项目和设备管理（通过网关调用） |
| TestCase 服务 | 8083 | 测试用例和执行（通过网关调用） |

### API 路由

```
/api/auth/          → Auth 服务（8081）
/api/project/       → Project 服务（8082）
/api/testcase/      → TestCase 服务（8083）
```

## 🔐 认证机制

采用 JWT Token 认证：

1. 用户登录获取 Token
2. 前端存储 Token 到 localStorage
3. 所有请求在 Header 中携带：`Authorization: Bearer {token}`
4. Gateway 验证 Token 有效性
5. Token 过期自动清除并跳转登录

## 📱 功能模块

### 1. 仪表板 (Dashboard)
- 显示项目、设备、用例、执行记录的统计数据
- 最近活动时间线

### 2. 项目管理 (Projects)
- 创建、编辑、删除项目
- 项目列表展示

### 3. 设备管理 (Devices)
- 添加、编辑、删除设备
- 显示设备在线状态
- 支持设备类型：扫地机器人、智能音箱、空气净化器等
- MQTT 客户端 ID 配置

### 4. 测试用例 (TestCases)
- 创建、编辑、删除测试用例
- 关联设备和项目
- 支持多种测试类型：冒烟测试、功能测试、性能测试、兼容性测试
- 一键执行测试用例

### 5. 执行记录 (Executions)
- 查看所有测试执行记录
- 实时显示执行状态（待执行、执行中、已完成、执行失败）
- 查看执行报告和日志
- 刷新功能

## 🛠️ 开发指南

### 添加新页面

1. 在 `src/views/` 创建 `.vue` 文件
2. 在 `src/router/index.js` 中添加路由配置
3. 在 `src/views/Layout.vue` 的菜单中添加导航项

### 调用后端 API

使用 `src/services/api.js` 中的服务：

```javascript
import { authService, projectService, testcaseService } from '@/services/api'

// 登录
const login = await authService.login('admin', 'password')

// 获取项目列表
const projects = await projectService.getProjects()

// 创建设备
const device = await projectService.createDevice({
  deviceName: 'My Device',
  deviceType: 'ROBOT_VACUUM',
  mqttClientId: 'robot_1',
  projectId: 1
})

// 执行测试用例
await testcaseService.executeCase(caseId)
```

### 状态管理

使用 Pinia 进行状态管理，认证信息存储在 `authStore`：

```javascript
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()
console.log(authStore.user)        // 当前用户
console.log(authStore.token)       // JWT Token
console.log(authStore.isLoggedIn()) // 是否已登录
```

## 🎨 UI 框架

使用 Element Plus 作为 UI 框架，提供丰富的组件库

官方文档：https://element-plus.org/

## 📦 依赖列表

- **Vue 3**：渐进式 JavaScript 框架
- **Vue Router 4**：客户端路由
- **Pinia**：Vue 3 状态管理库
- **Axios**：HTTP 客户端
- **Element Plus**：Vue 3 UI 组件库
- **Vite**：现代前端构建工具

## ⚙️ 配置说明

### vite.config.js

配置了代理，将 `/api` 请求代理到后端 Gateway（8001）：

```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8001',
    changeOrigin: true
  }
}
```

### 支持跨域

后端 Gateway 配置了 CORS，允许前端跨域访问

## 🐛 常见问题

### Q: 页面空白，控制台报错 "Failed to resolve import"
**A**: 检查文件路径是否正确，确保 .vue 文件存在

### Q: 登录后跳转 404
**A**: 检查后端服务是否启动，API 地址是否正确

### Q: CORS 错误
**A**: 确保后端 Gateway 启动了（8001 端口）并配置了 CORS

### Q: Token 失效后自动重定向
**A**: 这是正常行为，重新登录即可

## 📞 支持

有任何问题请查看后端 README.md 或联系开发团队

