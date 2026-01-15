# 🚀 TestHub IoT - 修复后的快速启动指南

> **重要提示**: 原QUICK_START.md中的docker-compose命令已更新为docker compose (空格)

## 📌 前提条件

### 必需软件

```bash
✅ JDK 17+
✅ Maven 3.8+
✅ Docker & Docker Compose V2
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
docker compose version  # 注意：使用空格，不是连字符

# 预期输出：Docker Compose version v5.0.1 或更高版本
```

---

## 🎯 本地快速启动（已修复）

### 方式A：使用自动化脚本（推荐）

```bash
# 进入项目目录
cd /home/sutai/TestHub

# 运行自动化启动脚本
./start-testhub.sh

# 等待90秒，所有服务将自动启动并完成初始化
```

**脚本会自动完成**:
1. 停止冲突的testai项目容器
2. 启动MySQL和Redis
3. 创建并初始化Nacos数据库
4. 启动Nacos和EMQX
5. 验证所有服务状态

### 方式B：手动启动（逐步执行）

#### 步骤1：启动基础设施（5分钟）

```bash
# 进入项目目录
cd /home/sutai/TestHub

# 先启动MySQL和Redis
docker compose up -d mysql redis

# 等待30秒，让服务完全启动
sleep 30

# 检查容器状态
docker ps --filter "name=testhub"
```

**预期输出：**
```
testhub-mysql         Up 30 seconds (healthy)
testhub-redis         Up 30 seconds (healthy)
```

#### 步骤2：初始化Nacos数据库（2分钟）

```bash
# 创建nacos_config数据库
docker exec testhub-mysql mysql -uroot -proot123456 -e \
  "CREATE DATABASE IF NOT EXISTS nacos_config CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"

# 下载Nacos SQL初始化脚本
curl -s https://raw.githubusercontent.com/alibaba/nacos/2.2.0/distribution/conf/mysql-schema.sql \
  -o /tmp/nacos-mysql-schema.sql

# 执行SQL脚本
docker exec -i testhub-mysql mysql -uroot -proot123456 nacos_config < /tmp/nacos-mysql-schema.sql

# 验证表创建成功（应该有12张表）
docker exec testhub-mysql mysql -uroot -proot123456 -e "SHOW TABLES FROM nacos_config;"
```

#### 步骤3：启动Nacos和EMQX（2分钟）

```bash
# 启动Nacos和EMQX
docker compose up -d nacos emqx

# 等待60秒，让Nacos完全初始化
sleep 60

# 检查所有容器状态
docker ps --filter "name=testhub"
```

**预期输出：**
```
NAME                  STATUS         PORTS
testhub-mysql         Up (healthy)   0.0.0.0:3306->3306/tcp
testhub-redis         Up (healthy)   0.0.0.0:6379->6379/tcp
testhub-nacos         Up (healthy)   0.0.0.0:8848->8848/tcp
testhub-emqx          Up (healthy)   0.0.0.0:1883->1883/tcp, 0.0.0.0:18083->18083/tcp
```

### 步骤4：验证基础设施（1分钟）

```bash
# 验证MySQL（应该看到testhub和nacos_config数据库）
docker exec testhub-mysql mysql -uroot -proot123456 -e "SHOW DATABASES;"

# 验证Redis
docker exec testhub-redis redis-cli ping  # 应该返回PONG

# 验证Nacos
curl http://localhost:8848/nacos/actuator/health
# 应该返回: {"status":"UP",...}

# 访问Nacos控制台
浏览器打开: http://localhost:8848/nacos
用户名: nacos
密码: nacos

# 访问EMQX Dashboard
浏览器打开: http://localhost:18083
用户名: admin
密码: public
```

### 步骤5：启动微服务（3分钟）

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
cd /home/sutai/TestHub
mvn clean install -DskipTests

# 启动网关（新终端）
cd testhub-gateway
mvn spring-boot:run

# 启动认证服务（新终端）
cd testhub-auth
mvn spring-boot:run
```

### 步骤6：验证服务启动（1分钟）

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
  "timestamp": "2026-01-15T10:00:00"
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

## 🐛 常见问题排查（已更新）

### 问题1：docker-compose命令未找到

**原因**：系统安装的是Docker Compose V2

**解决方法**：
```bash
# 使用 docker compose (空格) 而不是 docker-compose (连字符)
docker compose version

# 如果显示版本号，说明已安装正确
```

### 问题2：Docker容器启动失败（端口被占用）

```bash
# 查看是否有冲突的容器
docker ps -a | grep -E "testai|3306|6379"

# 停止冲突容器
docker stop testai-mysql testai-redis

# 或使用自动化脚本，它会自动处理冲突
./start-testhub.sh
```

### 问题3：Nacos容器不断重启

**原因**：nacos_config数据库缺少必需的表

**解决方法**：
```bash
# 下载并执行Nacos SQL脚本
curl -s https://raw.githubusercontent.com/alibaba/nacos/2.2.0/distribution/conf/mysql-schema.sql \
  -o /tmp/nacos-mysql-schema.sql

docker exec -i testhub-mysql mysql -uroot -proot123456 nacos_config < /tmp/nacos-mysql-schema.sql

# 重启Nacos
docker restart testhub-nacos

# 等待60秒后检查状态
sleep 60
docker ps --filter "name=testhub-nacos"
```

### 问题4：Maven依赖下载慢

**解决方法**：使用阿里云镜像（已在pom.xml中配置）

```bash
# 如果还是慢，清理缓存重新下载
mvn clean install -U
```

### 问题5：MySQL连接失败

```bash
# 检查MySQL是否启动
docker ps --filter "name=testhub-mysql"

# 测试连接
docker exec testhub-mysql mysql -uroot -proot123456 -e "SELECT 1"

# 如果数据库不存在，检查初始化脚本
docker exec testhub-mysql mysql -uroot -proot123456 -e "SHOW DATABASES"

# 手动导入（如果需要）
docker exec -i testhub-mysql mysql -uroot -proot123456 < sql/testhub-init.sql
```

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
# 查看Docker容器日志
docker logs -f testhub-mysql
docker logs -f testhub-nacos

# 查看所有容器日志
docker compose logs -f

# 查看微服务日志（如果有配置）
tail -f testhub-gateway/logs/application.log
```

---

## 🔄 停止和清理

### 停止所有服务

```bash
# 停止Docker容器
cd /home/sutai/TestHub
docker compose down

# 停止微服务（IDEA中直接点Stop，或Ctrl+C终止命令行进程）
```

### 清理数据（慎用）

```bash
# 删除所有Docker容器和数据卷
docker compose down -v

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

- 查看修复报告：[DOCKER_FIX_REPORT.md](DOCKER_FIX_REPORT.md)
- 查看完整文档：[README.md](README.md)
- 自动化脚本：[start-testhub.sh](start-testhub.sh)

---

## ✅ 修复说明

### 主要变更

1. **Docker Compose命令**：从 `docker-compose` 更新为 `docker compose` (空格)
2. **Nacos数据库初始化**：添加了自动下载和执行SQL脚本的步骤
3. **端口冲突处理**：添加了自动停止冲突容器的逻辑
4. **自动化脚本**：提供了一键启动脚本 `start-testhub.sh`

### 验证通过

所有4个容器已成功启动并达到healthy状态：
- ✅ testhub-mysql (healthy)
- ✅ testhub-redis (healthy)
- ✅ testhub-nacos (healthy)
- ✅ testhub-emqx (healthy)

---

**🎉 恭喜！TestHub IoT测试管理平台已完全修复并可正常使用！**

**修复完成时间**: 2026-01-15
**修复版本**: v1.0.1-fixed
