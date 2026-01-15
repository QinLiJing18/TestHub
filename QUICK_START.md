# 🚀 TestHub IoT - 快速开始指南

## 📌 前提条件

### 必需软件

```bash
✅ JDK 17+
✅ Maven 3.8+
✅ Docker & Docker Compose
✅ Git
```

### 检查环境

```bash
# 检查Java版本
java -version

# 检查Maven版本
mvn -v

# 检查Docker版本
docker --version
docker-compose --version
```

---

## 🎯 方式一：本地快速启动（推荐开发环境）

### 步骤1：启动基础设施（2分钟）

```bash
# 进入项目目录
cd TestHub

# 启动MySQL、Redis、Nacos、EMQX
docker-compose up -d

# 等待30秒，让服务完全启动
sleep 30

# 检查容器状态（所有服务应该是healthy）
docker-compose ps
```

**预期输出：**
```
NAME                  STATUS         PORTS
testhub-mysql         Up (healthy)   0.0.0.0:3306->3306/tcp
testhub-redis         Up (healthy)   0.0.0.0:6379->6379/tcp
testhub-nacos         Up (healthy)   0.0.0.0:8848->8848/tcp
testhub-emqx          Up (healthy)   0.0.0.0:1883->1883/tcp, 0.0.0.0:18083->18083/tcp
```

### 步骤2：验证基础设施（1分钟）

```bash
# 验证MySQL（应该看到testhub数据库）
mysql -h127.0.0.1 -uroot -proot123456 -e "SHOW DATABASES;"

# 验证Redis
redis-cli ping  # 应该返回PONG

# 访问Nacos控制台
浏览器打开: http://localhost:8848/nacos
用户名: nacos
密码: nacos

# 访问EMQX Dashboard
浏览器打开: http://localhost:18083
用户名: admin
密码: public
```

### 步骤3：启动微服务（3分钟）

#### 方式A：IDEA启动（推荐）

1. 使用IDEA打开TestHub项目
2. 等待Maven依赖下载完成（首次需要5-10分钟）
3. 依次运行以下Application类：
   - `GatewayApplication.java` (8080)
   - `AuthApplication.java` (8081)

**提示**：其他微服务暂时不运行也可以，网关和认证服务足够测试核心功能。

#### 方式B：命令行启动

```bash
# 编译整个项目
mvn clean install -DskipTests

# 启动网关（新终端）
cd testhub-gateway
mvn spring-boot:run

# 启动认证服务（新终端）
cd testhub-auth
mvn spring-boot:run
```

### 步骤4：验证服务启动（1分钟）

```bash
# 检查网关健康状态
curl http://localhost:8080/actuator/health

# 检查认证服务健康状态
curl http://localhost:8081/actuator/health

# 查看Nacos服务列表
浏览器打开: http://localhost:8848/nacos
点击"服务管理" -> "服务列表"
应该看到: testhub-gateway, testhub-auth
```

---

## 🧪 快速测试API

### 1. 用户登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**预期响应：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "admin",
    "roles": ["ADMIN"]
  },
  "timestamp": "2026-01-14T10:00:00"
}
```

### 2. 使用Token访问受保护接口

```bash
# 复制上面返回的token
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 查询用户信息
curl -X GET http://localhost:8080/api/auth/userinfo \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🎨 使用Postman测试（推荐）

### 导入Postman集合

1. 下载Postman集合文件：`docs/TestHub-API.postman_collection.json`
2. 打开Postman → Import → 选择文件
3. 在集合中运行"登录"请求
4. Token会自动保存到环境变量，后续请求自动携带

### 预置的测试账号

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | ADMIN | 系统管理员 |
| tester1 | admin123 | TESTER | 测试工程师1 |
| tester2 | admin123 | TESTER | 测试工程师2 |

---

## 🐛 常见问题排查

### 问题1：Maven依赖下载慢

**解决方法：**使用阿里云镜像（已在pom.xml中配置）

```bash
# 如果还是慢，清理缓存重新下载
mvn clean install -U
```

### 问题2：Docker容器启动失败

```bash
# 查看容器日志
docker-compose logs mysql
docker-compose logs nacos

# 常见原因：端口被占用
# 检查端口占用
netstat -ano | findstr 3306  # Windows
lsof -i :3306  # Linux/Mac

# 解决方法：修改docker-compose.yml中的端口映射
```

### 问题3：Nacos注册失败

```bash
# 检查Nacos是否启动
curl http://localhost:8848/nacos/actuator/health

# 检查application.yml中的nacos地址是否正确
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848  # 确保这里是正确的地址
```

### 问题4：MySQL连接失败

```bash
# 检查MySQL是否启动
docker exec testhub-mysql mysql -uroot -proot123456 -e "SELECT 1"

# 检查数据库是否已创建
docker exec testhub-mysql mysql -uroot -proot123456 -e "SHOW DATABASES"

# 如果数据库不存在，手动导入
mysql -h127.0.0.1 -uroot -proot123456 < sql/testhub-init.sql
```

### 问题5：JWT Token解析失败

**原因：**Constants.java中的JWT_SECRET与实际不匹配

**解决方法：**
1. 确保testhub-common模块正确编译
2. 重启所有服务
3. 重新登录获取新Token

---

## 📊 监控和管理

### Nacos控制台

- 地址：http://localhost:8848/nacos
- 账号：nacos / nacos
- 功能：
  - 查看服务列表
  - 查看服务实例
  - 动态配置管理

### EMQX Dashboard

- 地址：http://localhost:18083
- 账号：admin / public
- 功能：
  - 查看MQTT客户端连接
  - 监控消息流量
  - 查看主题订阅

### 查看日志

```bash
# 查看网关日志
tail -f testhub-gateway/logs/application.log

# 查看Docker容器日志
docker-compose logs -f mysql
docker-compose logs -f nacos
```

---

## 🔄 停止和清理

### 停止所有服务

```bash
# 停止Docker容器
docker-compose down

# 停止微服务（IDEA中直接点Stop，或Ctrl+C终止命令行进程）
```

### 清理数据（慎用）

```bash
# 删除所有Docker容器和数据卷
docker-compose down -v

# 清理Maven编译产物
mvn clean
```

---

## 🎯 下一步

### 开发新功能

1. 阅读完整的开发指南：`docs/DEVELOPMENT_GUIDE.md`
2. 了解代码结构：`docs/CODE_STRUCTURE.md`
3. 查看API文档：`docs/API_REFERENCE.md`

### 部署到生产环境

1. 修改配置文件中的数据库密码
2. 修改JWT密钥
3. 配置域名和HTTPS
4. 使用Nginx进行反向代理
5. 配置监控和告警

---

## 📞 获取帮助

- 查看完整文档：[README.md](README.md)
- 提交Issue：https://github.com/yourusername/TestHub/issues
- 联系邮箱：support@testhub.com

---

**🎉 恭喜！你已成功启动TestHub IoT测试管理平台！**
