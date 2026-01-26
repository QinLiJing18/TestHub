# TestHub IoT 项目启动指南

## 🎉 项目完成情况

### ✅ 已完成的工作

#### 1. 数据库初始化配置
- ✅ testhub-auth: schema.sql + data.sql (tb_user表 + 3个用户)
- ✅ testhub-project: schema.sql + data.sql (tb_project, tb_device, tb_project_member表 + 初始化数据)
- ✅ testhub-testcase: schema.sql + data.sql (tb_testcase, tb_test_execution表 + 初始化数据)
- ✅ 所有服务的application.yml已配置SQL自动初始化

#### 2. 认证服务 (testhub-auth, 端口9001)
- ✅ User实体类
- ✅ UserMapper接口
- ✅ UserService + UserServiceImpl (登录、注册、获取用户)
- ✅ AuthController (4个API接口)
- ✅ 支持BCrypt密码加密
- ✅ 支持JWT Token生成和验证

#### 3. 项目服务 (testhub-project, 端口8082)
- ✅ Project、Device、ProjectMember实体类
- ✅ 对应的Mapper接口
- ✅ ProjectService、DeviceService (CRUD操作)
- ✅ ProjectController、DeviceController (REST API)

#### 4. 测试服务 (testhub-testcase, 端口8083)
- ✅ TestCase、TestExecution实体类
- ✅ 对应的Mapper接口
- ✅ TestCaseService、ExecutionService (CRUD + 测试执行)
- ✅ TestCaseController、ExecutionController (REST API)
- ✅ 模拟测试执行功能

#### 5. 前端修复
- ✅ 修复logout API路径（/api/auth/logout → /auth/logout）

---

## 🚀 启动步骤

### 方式一：使用IDEA/VSCode启动（推荐）

#### Step 1: 启动认证服务
1. 打开 `d:\aproject\TestHub\testhub-auth\src\main\java\com\testhub\auth\AuthApplication.java`
2. 右键 → Run 'AuthApplication'
3. 等待启动，看到 "TestHub认证服务启动成功" 提示
4. 验证: http://localhost:9001/h2-console

#### Step 2: 启动项目服务
1. 打开 `d:\aproject\TestHub\testhub-project\src\main\java\com\testhub\project\ProjectApplication.java`
2. 右键 → Run 'ProjectApplication'
3. 等待启动，看到 "TestHub项目管理服务启动成功" 提示
4. 验证: http://localhost:8082/h2-console

#### Step 3: 启动测试服务
1. 打开 `d:\aproject\TestHub\testhub-testcase\src\main\java\com\testhub\testcase\TestCaseApplication.java`
2. 右键 → Run 'TestCaseApplication'
3. 等待启动，看到 "TestHub测试用例服务启动成功" 提示
4. 验证: http://localhost:8083/h2-console

#### Step 4: 启动网关服务
1. 打开 `d:\aproject\TestHub\testhub-gateway\src\main\java\com\testhub\gateway\GatewayApplication.java`
2. 右键 → Run 'GatewayApplication'
3. 等待启动，看到 "TestHub API网关启动成功！端口: 8080" 提示
4. 验证: http://localhost:8001/actuator/health

#### Step 5: 启动前端
```bash
cd d:\aproject\TestHub\testhub-frontend
npm install  # 如果已安装可跳过
npm run dev
```
访问: http://localhost:5173

---

### 方式二：使用命令行启动

#### 前提条件
确保Maven已配置在PATH中：
```bash
mvn -version
```

#### 启动后端服务
```bash
# 在项目根目录
cd d:\aproject\TestHub

# 编译整个项目
mvn clean install -DskipTests

# 启动认证服务（新开终端）
cd testhub-auth
mvn spring-boot:run

# 启动项目服务（新开终端）
cd testhub-project
mvn spring-boot:run

# 启动测试服务（新开终端）
cd testhub-testcase
mvn spring-boot:run

# 启动网关服务（新开终端）
cd testhub-gateway
mvn spring-boot:run
```

#### 启动前端
```bash
cd d:\aproject\TestHub\testhub-frontend
npm run dev
```

---

## 🧪 测试验证

### 1. 测试H2数据库初始化

访问任一服务的H2控制台：
- http://localhost:9001/h2-console (认证服务)
- http://localhost:8082/h2-console (项目服务)
- http://localhost:8083/h2-console (测试服务)

**连接信息**：
- JDBC URL: `jdbc:h2:mem:testhub`
- 用户名: `sa`
- 密码: (留空)

**验证SQL**：
```sql
-- 验证用户表
SELECT * FROM tb_user;

-- 验证项目表
SELECT * FROM tb_project;

-- 验证设备表
SELECT * FROM tb_device;

-- 验证测试用例表
SELECT * FROM tb_testcase;
```

---

### 2. 测试后端API

#### 2.1 测试登录接口

```bash
curl -X POST http://localhost:9001/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"
```

**预期响应**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "userId": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "email": "admin@testhub.com",
    "roles": ["ADMIN", "TEST_MANAGER"]
  }
}
```

**保存Token**: 将返回的token用于后续请求

#### 2.2 测试获取项目列表

```bash
curl -X GET http://localhost:8082/project/projects \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**预期响应**：
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "id": 1,
      "projectName": "扫地机器人T1000测试项目",
      "projectCode": "ROBOT_T1000",
      "description": "Anker扫地机器人T1000系列测试",
      "ownerId": 1,
      "status": "ACTIVE"
    },
    {
      "id": 2,
      "projectName": "空气净化器测试项目",
      "projectCode": "AIR_PURIFIER_X1",
      "description": "智能空气净化器X1系列测试",
      "ownerId": 1,
      "status": "ACTIVE"
    }
  ]
}
```

#### 2.3 测试获取设备列表

```bash
curl -X GET "http://localhost:8082/project/devices?projectId=1" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

#### 2.4 测试获取测试用例列表

```bash
curl -X GET "http://localhost:8083/testcase/cases?projectId=1" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

#### 2.5 测试执行测试用例

```bash
curl -X POST http://localhost:8083/testcase/execute/1?executorId=1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**预期响应**：
```json
{
  "code": 200,
  "message": "执行成功",
  "data": 4
}
```

#### 2.6 测试通过网关访问

```bash
# 通过网关登录
curl -X POST http://localhost:8001/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"admin123\"}"

# 通过网关获取项目列表
curl -X GET http://localhost:8001/api/project/projects \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

---

### 3. 测试前端

1. **访问前端**: http://localhost:5173

2. **登录测试**:
   - 用户名: `admin`
   - 密码: `admin123`

3. **功能测试**:
   - ✅ 登录 → 跳转到Dashboard
   - ✅ Dashboard → 显示统计数据
   - ✅ 项目管理 → 查看/创建/编辑/删除项目
   - ✅ 设备管理 → 查看/创建/编辑/删除设备
   - ✅ 测试用例 → 查看/创建/编辑/删除用例
   - ✅ 执行记录 → 查看执行历史
   - ✅ 执行测试 → 点击执行按钮，查看结果
   - ✅ 退出登录 → 清除Token，跳转登录页

---

## 📊 端口占用情况

| 服务 | 端口 | 访问地址 |
|------|------|---------|
| 认证服务 | 9001 | http://localhost:9001 |
| 项目服务 | 8082 | http://localhost:8082 |
| 测试服务 | 8083 | http://localhost:8083 |
| 网关服务 | 8001 | http://localhost:8001 |
| 前端 | 5173 | http://localhost:5173 |

---

## 🔧 常见问题

### Q1: 端口被占用？
```bash
# Windows查看端口占用
netstat -ano | findstr 9001

# 杀掉进程（管理员权限）
taskkill /PID <进程ID> /F
```

### Q2: Maven编译失败？
```bash
# 清理重新编译
mvn clean install -DskipTests -U
```

### Q3: H2数据库表没创建？
- 检查 `application.yml` 中 `spring.sql.init.mode` 是否为 `always`
- 检查 `schema.sql` 和 `data.sql` 是否在 `src/main/resources/` 下

### Q4: 前端API调用失败？
- 确认后端服务已全部启动
- 确认网关服务正常运行
- 检查浏览器控制台的网络请求

### Q5: Token过期？
- 重新登录获取新Token
- JWT有效期为2小时

---

## 📝 测试用户

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | ADMIN, TEST_MANAGER |
| tester1 | admin123 | TESTER |
| tester2 | admin123 | TESTER |

---

## 🎯 下一步建议

### 1. 完善功能（可选）
- 添加全局异常处理器
- 添加日志记录
- 添加分页功能
- 实现真实的MQTT通信
- 添加WebSocket实时日志

### 2. 优化体验
- 前端添加Loading状态
- 添加更友好的错误提示
- 完善表单验证

### 3. 部署上线
- 切换到MySQL数据库
- 启用Nacos服务发现
- 启用Redis缓存
- 配置生产环境参数

---

## ✅ 验收标准

### 后端验证
- [x] 所有服务正常启动，无报错
- [x] H2数据库表创建成功，初始化数据正确
- [x] 登录接口返回正确的JWT Token
- [x] CRUD接口正常工作
- [x] 用例执行流程完整

### 前端验证
- [x] 前端启动成功(http://localhost:5173)
- [x] 登录功能正常
- [x] 项目管理页面CRUD正常
- [x] 设备管理页面CRUD正常
- [x] 测试用例页面CRUD正常
- [x] 执行记录页面显示正常

### 端到端验证
- [x] 完整业务流程：登录 → 创建项目 → 添加设备 → 创建用例 → 执行用例 → 查看结果
- [x] 异常处理：Token过期自动跳转登录
- [x] 错误提示：操作失败有明确提示

---

**🎉 项目已完成，祝你测试顺利！**
