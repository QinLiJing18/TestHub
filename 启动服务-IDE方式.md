# TestHub 服务启动指南 - IDE方式

## ⚠️ 重要：pom.xml已修复

我已经修复了三个服务的pom.xml文件，添加了正确的spring-boot-maven-plugin配置。
**在启动服务之前，请先在IDE中重新加载Maven项目！**

### VSCode用户
1. 按 `Ctrl+Shift+P`
2. 输入 `Java: Reload Projects`
3. 选择并执行

### IntelliJ IDEA用户
1. 右键点击项目根目录
2. 选择 `Maven` → `Reload Project`
3. 或点击右侧Maven工具窗口的刷新按钮🔄

---

## 🚀 启动步骤（推荐按顺序启动）

### 1. 启动认证服务 (端口9001)

**文件位置：**
```
d:\aproject\TestHub\testhub-auth\src\main\java\com\testhub\auth\AuthApplication.java
```

**启动方法：**
- **VSCode**: 打开文件，点击main方法上方的 `▶ Run` 按钮
- **IDEA**: 右键文件 → `Run 'AuthApplication.main()'`

**验证：**
等待看到控制台输出：
```
TestHub认证服务启动成功！端口: 9001
```

浏览器访问：http://localhost:9001/actuator/health
应该返回：`{"status":"UP"}`

---

### 2. 启动项目服务 (端口8082)

**文件位置：**
```
d:\aproject\TestHub\testhub-project\src\main\java\com\testhub\project\ProjectApplication.java
```

**启动方法：**
- **VSCode**: 打开文件，点击main方法上方的 `▶ Run` 按钮
- **IDEA**: 右键文件 → `Run 'ProjectApplication.main()'`

**验证：**
等待看到控制台输出：
```
TestHub项目管理服务启动成功！端口: 8082
```

---

### 3. 启动测试服务 (端口8083)

**文件位置：**
```
d:\aproject\TestHub\testhub-testcase\src\main\java\com\testhub\testcase\TestCaseApplication.java
```

**启动方法：**
- **VSCode**: 打开文件，点击main方法上方的 `▶ Run` 按钮
- **IDEA**: 右键文件 → `Run 'TestCaseApplication.main()'`

**验证：**
等待看到控制台输出：
```
TestHub测试用例服务启动成功！端口: 8083
```

---

### 4. 启动网关服务 (端口8001)

**文件位置：**
```
d:\aproject\TestHub\testhub-gateway\src\main\java\com\testhub\gateway\GatewayApplication.java
```

**启动方法：**
- **VSCode**: 打开文件，点击main方法上方的 `▶ Run` 按钮
- **IDEA**: 右键文件 → `Run 'GatewayApplication.main()'`

**验证：**
等待看到控制台输出：
```
TestHub API网关启动成功！端口: 8001
```

浏览器访问：http://localhost:8001/actuator/health

---

### 5. 启动前端 (端口5173)

打开新终端，执行：
```bash
cd d:\aproject\TestHub\testhub-frontend
npm run dev
```

浏览器访问：http://localhost:5173

---

## 🧪 测试登录功能

### 使用curl测试
```bash
curl -X POST http://localhost:8001/api/auth/login -H "Content-Type: application/json" -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

### 使用前端测试
1. 访问 http://localhost:5173
2. 输入用户名：`admin`
3. 输入密码：`admin123`
4. 点击登录

**预期结果：**
- 成功跳转到Dashboard页面
- 右上角显示用户名
- 左侧显示菜单

---

## ❌ 常见错误排查

### 错误1: 端口被占用
```
Port 9001 is already in use
```

**解决方法：**
```bash
# Windows查找占用端口的进程
netstat -ano | findstr :9001

# 杀掉进程（管理员权限）
taskkill /PID <进程号> /F
```

---

### 错误2: 数据库初始化失败
```
Table "TB_USER" not found
```

**原因：** H2数据库的schema.sql没有执行

**解决方法：**
1. 检查 `application.yml` 中的配置：
```yaml
spring:
  sql:
    init:
      mode: always  # 确保是always
```

2. 停止服务，删除H2数据库文件（内存模式不需要）
3. 重新启动服务

---

### 错误3: 前端登录失败 ERR_NETWORK
```
AxiosError: Network Error
```

**原因：** 网关服务未启动或端口错误

**解决方法：**
1. 确认网关服务已启动（端口8001）
2. 检查浏览器控制台的Network请求，确认请求地址是 `http://localhost:8001/api/auth/login`
3. 确认前端的 `testhub-frontend/src/services/api.js` 中 baseURL 配置正确：
```javascript
baseURL: 'http://localhost:8001/api'
```

---

## 📊 端口占用检查

启动前检查端口是否可用：
```bash
netstat -ano | findstr ":9001"  # 认证服务
netstat -ano | findstr ":8082"  # 项目服务
netstat -ano | findstr ":8083"  # 测试服务
netstat -ano | findstr ":8001"  # 网关服务
netstat -ano | findstr ":5173"  # 前端
```

---

## ✅ 启动成功标志

当所有服务启动成功后，你应该看到：

1. **4个Java进程在运行**
2. **5个端口被监听**: 9001, 8082, 8083, 8001, 5173
3. **前端可以访问**: http://localhost:5173
4. **网关健康检查通过**: http://localhost:8001/actuator/health

---

## 🎯 快速启动顺序

1. ✅ 重新加载Maven项目（Reload Projects）
2. ✅ 启动 AuthApplication (9001)
3. ✅ 启动 ProjectApplication (8082)
4. ✅ 启动 TestCaseApplication (8083)
5. ✅ 启动 GatewayApplication (8001)
6. ✅ 启动前端 (npm run dev)
7. ✅ 测试登录 (admin/admin123)
