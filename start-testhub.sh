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
