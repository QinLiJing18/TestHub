# TestHub IoT - Docker部署问题修复报告

## 📋 执行摘要

**问题状态**: ✅ 已完全解决
**执行时间**: 2026-01-15
**修复结果**: 所有4个Docker容器(MySQL、Redis、Nacos、EMQX)已成功启动并达到healthy状态

---

## 🔍 问题根因分析

### 1. 核心问题：端口冲突

**现象**:
- 执行 `docker-compose up -d` 时容器无法启动
- MySQL端口3306被占用
- Redis端口6379被占用

**根本原因**:
系统中已有testai项目的MySQL和Redis容器在运行，占用了TestHub项目所需的端口:
```bash
testai-mysql (PID: f41603ce7d39) - 占用3306端口
testai-redis (PID: 6f73e6ff99da) - 占用6379端口
```

### 2. Docker Compose版本问题

**现象**:
- 使用 `docker-compose` 命令提示未找到
- docker-compose.yml中使用了过时的 `version: '3.8'` 配置

**根本原因**:
- 系统安装的是Docker Compose V2 (v5.0.1)
- 应使用 `docker compose` (空格) 而不是 `docker-compose` (连字符)
- Docker Compose V2不再需要version字段

### 3. Nacos数据库表缺失

**现象**:
- Nacos容器启动后不断重启
- 日志显示: "No DataSource set" 错误

**根本原因**:
- nacos_config数据库虽然创建，但缺少必需的表结构
- Nacos需要12张表才能正常运行(config_info, users, roles等)

---

## 🛠️ 完整修复方案

### 步骤1: 停止冲突容器

```bash
# 检查端口占用
docker ps -a | grep testai

# 停止testai项目的容器
docker stop testai-mysql testai-redis

# 验证端口已释放
ss -tulpn | grep -E ':(3306|6379) '
```

### 步骤2: 修复docker-compose.yml配置

**修改内容**:
```yaml
# 移除过时的version字段
# 修改前:
version: '3.8'

# 修改后:
# (直接删除version行)
```

**修改后的文件位置**: `/home/sutai/TestHub/docker-compose.yml`

### 步骤3: 启动基础设施容器

```bash
# 进入项目目录
cd /home/sutai/TestHub

# 先启动MySQL和Redis
docker compose up -d mysql redis

# 等待30秒让容器完全启动
sleep 30

# 验证容器状态
docker ps --filter "name=testhub"
```

**预期输出**:
```
testhub-mysql    Up 30 seconds (healthy)
testhub-redis    Up 30 seconds (healthy)
```

### 步骤4: 创建并初始化Nacos数据库

```bash
# 1. 创建nacos_config数据库
docker exec testhub-mysql mysql -uroot -proot123456 -e \
  "CREATE DATABASE IF NOT EXISTS nacos_config CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;"

# 2. 下载Nacos官方SQL初始化脚本
curl -s https://raw.githubusercontent.com/alibaba/nacos/2.2.0/distribution/conf/mysql-schema.sql \
  -o /tmp/nacos-mysql-schema.sql

# 3. 执行SQL脚本初始化表结构
docker exec -i testhub-mysql mysql -uroot -proot123456 nacos_config < /tmp/nacos-mysql-schema.sql

# 4. 验证表创建成功(应该有12张表)
docker exec testhub-mysql mysql -uroot -proot123456 -e "SHOW TABLES FROM nacos_config;"
```

**预期输出**:
```
Tables_in_nacos_config
config_info
config_info_aggr
config_info_beta
config_info_tag
config_tags_relation
group_capacity
his_config_info
permissions
roles
tenant_capacity
tenant_info
users
```

### 步骤5: 启动Nacos和EMQX

```bash
# 启动Nacos和EMQX容器
docker compose up -d nacos emqx

# 或者手动启动
docker start testhub-nacos
docker start testhub-emqx

# 等待60秒让Nacos完全初始化
sleep 60

# 检查所有容器状态
docker ps --filter "name=testhub"
```

**预期输出**:
```
testhub-mysql    Up X minutes (healthy)
testhub-redis    Up X minutes (healthy)
testhub-nacos    Up X seconds (healthy)
testhub-emqx     Up X seconds (healthy)
```

---

## ✅ 验证步骤

### 1. 验证容器健康状态

```bash
# 查看所有容器状态
docker ps --filter "name=testhub" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

**预期结果**: 所有容器状态都应该是 `Up ... (healthy)`

### 2. 验证MySQL连接和数据初始化

```bash
# 检查MySQL连接
docker exec testhub-mysql mysql -uroot -proot123456 -e "SELECT 1"

# 检查testhub数据库和表
docker exec testhub-mysql mysql -uroot -proot123456 -e "USE testhub; SHOW TABLES;"

# 应该看到7张表
# tb_user, tb_project, tb_project_member, tb_device,
# tb_testcase, tb_test_execution, tb_test_report
```

### 3. 验证Redis连接

```bash
# 测试Redis连接
docker exec testhub-redis redis-cli ping

# 预期输出: PONG
```

### 4. 验证Nacos服务

```bash
# 检查Nacos健康状态
curl http://localhost:8848/nacos/actuator/health

# 预期输出: {"status":"UP","groups":["liveness","readiness"]}

# 检查Nacos版本和模式
curl -u nacos:nacos "http://localhost:8848/nacos/v1/console/server/state"

# 预期输出: {"version":"2.2.0","standalone_mode":"standalone",...}

# 访问Nacos控制台
# 浏览器打开: http://localhost:8848/nacos
# 用户名: nacos
# 密码: nacos
```

### 5. 验证EMQX服务

```bash
# 检查EMQX Dashboard可访问性
curl -I http://localhost:18083

# 预期输出: HTTP/1.1 200 OK

# 访问EMQX Dashboard
# 浏览器打开: http://localhost:18083
# 用户名: admin
# 密码: public
```

---

## 🚀 完整启动脚本

将以下内容保存为 `start-testhub.sh`:

```bash
#!/bin/bash

set -e  # 遇到错误立即退出

echo "========================================="
echo "TestHub IoT - 自动化启动脚本"
echo "========================================="

# 1. 停止冲突容器
echo "步骤1: 停止可能冲突的容器..."
docker stop testai-mysql testai-redis 2>/dev/null || true

# 2. 进入项目目录
cd /home/sutai/TestHub

# 3. 启动MySQL和Redis
echo "步骤2: 启动MySQL和Redis..."
docker compose up -d mysql redis

# 4. 等待MySQL完全启动
echo "步骤3: 等待MySQL启动(30秒)..."
sleep 30

# 5. 创建Nacos数据库
echo "步骤4: 创建Nacos数据库..."
docker exec testhub-mysql mysql -uroot -proot123456 -e \
  "CREATE DATABASE IF NOT EXISTS nacos_config CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;" \
  2>/dev/null

# 6. 初始化Nacos表结构
echo "步骤5: 初始化Nacos表结构..."
if [ ! -f /tmp/nacos-mysql-schema.sql ]; then
  curl -s https://raw.githubusercontent.com/alibaba/nacos/2.2.0/distribution/conf/mysql-schema.sql \
    -o /tmp/nacos-mysql-schema.sql
fi
docker exec -i testhub-mysql mysql -uroot -proot123456 nacos_config < /tmp/nacos-mysql-schema.sql 2>/dev/null || true

# 7. 启动Nacos和EMQX
echo "步骤6: 启动Nacos和EMQX..."
docker compose up -d nacos emqx

# 8. 等待所有服务启动
echo "步骤7: 等待所有服务完全启动(60秒)..."
sleep 60

# 9. 验证服务状态
echo ""
echo "========================================="
echo "验证服务状态"
echo "========================================="

docker ps --filter "name=testhub" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo ""
echo "========================================="
echo "服务访问地址"
echo "========================================="
echo "MySQL:        localhost:3306 (root/root123456)"
echo "Redis:        localhost:6379"
echo "Nacos:        http://localhost:8848/nacos (nacos/nacos)"
echo "EMQX:         http://localhost:18083 (admin/public)"
echo "MQTT:         mqtt://localhost:1883"
echo ""
echo "✅ 启动完成！"
echo "========================================="
```

**使用方法**:
```bash
chmod +x start-testhub.sh
./start-testhub.sh
```

---

## 🔄 停止和重启命令

### 停止所有服务

```bash
cd /home/sutai/TestHub
docker compose down
```

### 停止并清理数据卷(慎用)

```bash
cd /home/sutai/TestHub
docker compose down -v
```

### 重启单个服务

```bash
# 重启MySQL
docker restart testhub-mysql

# 重启Nacos
docker restart testhub-nacos
```

### 查看服务日志

```bash
# 查看Nacos日志
docker logs -f testhub-nacos

# 查看MySQL日志
docker logs -f testhub-mysql

# 查看所有容器日志
docker compose logs -f
```

---

## 🐛 常见问题和解决方案

### 问题1: 端口被占用

**症状**: 容器启动失败，提示端口已被使用

**解决方法**:
```bash
# 查找占用端口的进程
lsof -i :3306
ss -tulpn | grep 3306

# 停止占用端口的Docker容器
docker ps -a | grep 3306
docker stop <容器名>
```

### 问题2: Nacos启动失败

**症状**: Nacos容器不断重启，日志显示 "No DataSource set"

**解决方法**:
```bash
# 1. 检查nacos_config数据库的表
docker exec testhub-mysql mysql -uroot -proot123456 -e "SHOW TABLES FROM nacos_config;"

# 2. 如果表为空，重新初始化
curl -s https://raw.githubusercontent.com/alibaba/nacos/2.2.0/distribution/conf/mysql-schema.sql \
  -o /tmp/nacos-mysql-schema.sql
docker exec -i testhub-mysql mysql -uroot -proot123456 nacos_config < /tmp/nacos-mysql-schema.sql

# 3. 重启Nacos
docker restart testhub-nacos
```

### 问题3: MySQL初始化脚本未执行

**症状**: testhub数据库中没有表

**解决方法**:
```bash
# 检查SQL文件是否存在
ls -la /home/sutai/TestHub/sql/testhub-init.sql

# 手动执行初始化脚本
docker exec -i testhub-mysql mysql -uroot -proot123456 < /home/sutai/TestHub/sql/testhub-init.sql

# 验证表是否创建
docker exec testhub-mysql mysql -uroot -proot123456 -e "USE testhub; SHOW TABLES;"
```

### 问题4: Docker Compose命令不存在

**症状**: `docker-compose: command not found`

**解决方法**:
```bash
# 检查Docker Compose版本
docker compose version

# 如果显示版本号，使用 docker compose (空格)
# 如果未安装，执行以下命令安装
sudo apt install docker-compose
# 或
sudo snap install docker
```

### 问题5: 容器健康检查失败

**症状**: 容器状态显示 (unhealthy)

**解决方法**:
```bash
# 查看容器详细信息
docker inspect testhub-mysql

# 查看健康检查日志
docker inspect testhub-mysql | jq '.[0].State.Health'

# 手动测试健康检查命令
docker exec testhub-mysql mysqladmin ping -h localhost
```

---

## 📊 最终验证结果

### 容器状态

```
NAME              STATUS                   PORTS
testhub-mysql     Up X minutes (healthy)   0.0.0.0:3306->3306/tcp
testhub-redis     Up X minutes (healthy)   0.0.0.0:6379->6379/tcp
testhub-nacos     Up X minutes (healthy)   0.0.0.0:8848->8848/tcp, 0.0.0.0:9848->9848/tcp
testhub-emqx      Up X minutes (healthy)   0.0.0.0:1883->1883/tcp, 0.0.0.0:18083->18083/tcp
```

### 数据库验证

- **testhub数据库**: ✅ 已创建，包含7张表
- **nacos_config数据库**: ✅ 已创建，包含12张表
- **MySQL连接**: ✅ 正常 (root/root123456)

### 服务验证

- **Redis**: ✅ PONG响应正常
- **Nacos**: ✅ 健康检查通过，版本v2.2.0，standalone模式
- **EMQX**: ✅ Dashboard可访问(HTTP 200)
- **MySQL**: ✅ 连接正常，数据初始化完成

### 控制台访问

- **Nacos控制台**: http://localhost:8848/nacos ✅ 可访问
- **EMQX Dashboard**: http://localhost:18083 ✅ 可访问

---

## 🎯 后续步骤

### 1. 启动微服务

按照QUICK_START.md文档继续执行:

```bash
# 方式A: 在IDEA中启动
1. 打开TestHub项目
2. 运行 GatewayApplication.java (8080)
3. 运行 AuthApplication.java (8081)

# 方式B: 命令行启动
cd /home/sutai/TestHub
mvn clean install -DskipTests

# 启动网关
cd testhub-gateway
mvn spring-boot:run

# 启动认证服务(新终端)
cd testhub-auth
mvn spring-boot:run
```

### 2. 测试API

```bash
# 登录获取Token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 使用Token访问受保护接口
TOKEN="<返回的token>"
curl -X GET http://localhost:8080/api/auth/userinfo \
  -H "Authorization: Bearer $TOKEN"
```

### 3. 查看服务注册

访问Nacos控制台 http://localhost:8848/nacos，应该能看到:
- testhub-gateway
- testhub-auth

---

## 📝 修复总结

### 修复的问题

1. ✅ 解决了端口冲突问题(testai项目容器占用)
2. ✅ 修复了docker-compose.yml配置(移除过时的version字段)
3. ✅ 解决了Nacos数据库表缺失问题
4. ✅ 所有4个容器成功启动并达到healthy状态
5. ✅ 数据库初始化完成(testhub + nacos_config)

### 核心修改

1. **docker-compose.yml**: 删除 `version: '3.8'` 行
2. **Nacos数据库**: 执行官方SQL脚本创建12张表
3. **启动方式**: 改用 `docker compose` (空格) 命令

### 验证通过项

- [x] MySQL容器healthy状态
- [x] Redis容器healthy状态
- [x] Nacos容器healthy状态
- [x] EMQX容器healthy状态
- [x] MySQL数据库连接正常
- [x] Redis连接正常(PONG)
- [x] Nacos控制台可访问
- [x] EMQX Dashboard可访问
- [x] testhub数据库7张表已创建
- [x] nacos_config数据库12张表已创建

---

## 📞 技术支持

### 相关文档

- QUICK_START.md - 快速启动指南
- README.md - 项目介绍
- PROJECT_SUMMARY.md - 项目架构说明

### 日志查看

```bash
# 实时查看所有容器日志
docker compose logs -f

# 查看特定容器日志
docker logs -f testhub-nacos
docker logs -f testhub-mysql --tail 100
```

### 健康检查

```bash
# 批量检查所有服务
curl http://localhost:8848/nacos/actuator/health
curl -I http://localhost:18083
docker exec testhub-redis redis-cli ping
docker exec testhub-mysql mysqladmin ping -h localhost
```

---

**修复完成时间**: 2026-01-15
**文档版本**: v1.0
**状态**: ✅ 所有问题已解决
