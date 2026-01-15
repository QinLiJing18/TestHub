# 🚀 TestHub IoT - 智能硬件测试管理平台

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2022.0.4-blue.svg)](https://spring.io/projects/spring-cloud)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## 📌 项目简介

TestHub IoT 是一个面向智能硬件的自动化测试管理平台，专注于IoT设备（扫地机器人、智能音箱、空气净化器等）的测试场景。

### 核心特性

- 🤖 **IoT设备管理** - 支持MQTT协议的设备接入与控制
- 📝 **测试用例库** - 提供冒烟测试、专项测试等标准模板
- ⚡ **自动化执行** - MQTT指令下发，实时日志采集
- 📊 **智能报告** - 基于EasyExcel的多维度测试报告
- 🔐 **权限管理** - 基于JWT的细粒度权限控制
- 🎯 **微服务架构** - Spring Cloud Alibaba生态

### 技术亮点

- **结合实际经验**：项目源于Anker扫地机测试实习经验
- **技术栈精简**：仅使用成熟稳定的技术组件
- **业务场景真实**：针对硬件测试的实际痛点设计

---

## 🏗️ 系统架构

### 整体架构图

```
┌─────────────────────────────────────────────────┐
│         API网关层 (Gateway 8080)                 │
│    路由转发 · JWT鉴权 · 限流熔断 · 日志追踪      │
└─────────────────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────┐
│              微服务层 (Spring Cloud)              │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐        │
│  │ 认证服务  │ │ 项目服务  │ │ 测试服务  │        │
│  │  8081    │ │  8082    │ │  8083    │        │
│  └──────────┘ └──────────┘ └──────────┘        │
└─────────────────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────┐
│            基础设施层 (Infrastructure)            │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐          │
│  │Nacos │ │MySQL │ │Redis │ │ MQTT │          │
│  │ 8848 │ │ 3306 │ │ 6379 │ │ 1883 │          │
│  └──────┘ └──────┘ └──────┘ └──────┘          │
└─────────────────────────────────────────────────┘
```

### 微服务职责

| 服务名称 | 端口 | 职责 | 核心技术 |
|---------|------|------|----------|
| testhub-gateway | 8080 | API网关、路由、鉴权 | Spring Cloud Gateway |
| testhub-auth | 8081 | 用户认证、权限管理 | Spring Security + JWT |
| testhub-project | 8082 | 项目管理、设备管理 | MyBatis Plus + Redis |
| testhub-testcase | 8083 | 测试用例、执行、报告 | MQTT + WebSocket + EasyExcel |

---

## 🛠️ 技术栈

### 后端技术

- **核心框架**：Spring Boot 3.1.5、Spring Cloud 2022.0.4
- **微服务治理**：Nacos 2.2.0（注册+配置中心）
- **API网关**：Spring Cloud Gateway
- **数据库**：MySQL 8.0 + MyBatis Plus 3.5.4
- **缓存**：Redis 7.0
- **认证**：JWT (JJWT 0.12.3)
- **IoT通信**：Eclipse Paho MQTT
- **实时通信**：Spring WebSocket
- **文档导出**：EasyExcel 3.3.3
- **工具库**：Hutool、Lombok

### 基础设施

- **容器化**：Docker + Docker Compose
- **注册中心**：Nacos
- **消息代理**：EMQX (MQTT Broker)
- **数据库**：MySQL 8.0
- **缓存**：Redis 7.0

---

## 📦 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- Docker & Docker Compose
- MySQL 8.0
- Redis 7.0

### 1️⃣ 克隆项目

```bash
git clone https://github.com/yourusername/testhub-iot.git
cd TestHub
```

### 2️⃣ 启动基础设施

```bash
# 启动MySQL、Redis、Nacos、MQTT
docker-compose up -d

# 检查容器状态
docker ps
```

### 3️⃣ 初始化数据库

```bash
# 导入数据库脚本
mysql -h127.0.0.1 -uroot -proot123456 < sql/testhub-init.sql
```

### 4️⃣ 启动微服务

**方式一：IDEA启动（推荐开发环境）**

1. 导入项目到IDEA
2. 等待Maven依赖下载完成
3. 依次启动以下Application类：
   - `GatewayApplication` (8080)
   - `AuthApplication` (8081)
   - `ProjectApplication` (8082)
   - `TestcaseApplication` (8083)

**方式二：命令行启动**

```bash
# 编译整个项目
mvn clean install -DskipTests

# 启动网关
cd testhub-gateway
mvn spring-boot:run &

# 启动认证服务
cd ../testhub-auth
mvn spring-boot:run &

# 启动项目服务
cd ../testhub-project
mvn spring-boot:run &

# 启动测试服务
cd ../testhub-testcase
mvn spring-boot:run &
```

### 5️⃣ 验证服务

访问以下地址确认服务启动成功：

- **Nacos控制台**：http://localhost:8848/nacos (nacos/nacos)
- **API网关**：http://localhost:8080/actuator/health
- **API文档**：http://localhost:8080/doc.html

---

## 🎯 核心功能演示

### 1. 用户登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "username": "admin",
    "roles": ["ADMIN"]
  }
}
```

### 2. 创建IoT设备

```bash
curl -X POST http://localhost:8080/api/project/devices \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "deviceName": "扫地机器人-T1000",
    "deviceType": "ROBOT_VACUUM",
    "mqttClientId": "robot_t1000",
    "projectId": 1
  }'
```

### 3. 创建测试用例

```bash
curl -X POST http://localhost:8080/api/testcase/cases \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "扫地机器人-启动测试",
    "deviceId": 1,
    "testType": "SMOKE",
    "steps": [
      {"action": "POWER_ON", "expected": "设备启动成功"},
      {"action": "START_CLEAN", "expected": "开始清扫"}
    ]
  }'
```

### 4. 执行测试

```bash
curl -X POST http://localhost:8080/api/testcase/execute/{caseId} \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 📂 项目结构

```
TestHub/
├── pom.xml                          # Maven父工程配置
├── README.md                        # 项目说明
├── docker-compose.yml              # Docker编排文件
│
├── testhub-common/                  # 公共模块
│   ├── pom.xml
│   └── src/main/java/com/testhub/common/
│       ├── entity/                 # 基础实体类
│       ├── dto/                    # 通用DTO
│       ├── utils/                  # 工具类
│       └── constants/              # 常量定义
│
├── testhub-gateway/                 # API网关 (8080)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/testhub/gateway/
│       │   ├── GatewayApplication.java
│       │   ├── filter/            # 过滤器
│       │   └── config/            # 配置类
│       └── resources/
│           └── application.yml
│
├── testhub-auth/                    # 认证服务 (8081)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/testhub/auth/
│       │   ├── AuthApplication.java
│       │   ├── controller/        # 登录/注册接口
│       │   ├── service/           # 认证逻辑
│       │   ├── security/          # Spring Security配置
│       │   └── entity/            # 用户实体
│       └── resources/
│           └── application.yml
│
├── testhub-project/                 # 项目服务 (8082)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/testhub/project/
│       │   ├── ProjectApplication.java
│       │   ├── controller/        # 项目/设备接口
│       │   ├── service/
│       │   ├── mapper/
│       │   └── entity/
│       └── resources/
│           └── application.yml
│
├── testhub-testcase/                # 测试服务 (8083)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/testhub/testcase/
│       │   ├── TestcaseApplication.java
│       │   ├── controller/        # 测试用例接口
│       │   ├── service/           # 测试执行逻辑
│       │   ├── mqtt/              # MQTT客户端
│       │   ├── websocket/         # 实时推送
│       │   └── entity/
│       └── resources/
│           └── application.yml
│
└── sql/                             # 数据库脚本
    └── testhub-init.sql            # 初始化SQL
```

---

## 🗄️ 数据库设计

### 核心表结构

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| tb_user | 用户表 | id, username, password, roles |
| tb_project | 项目表 | id, project_name, description |
| tb_device | 设备表 | id, device_name, device_type, mqtt_client_id |
| tb_testcase | 测试用例表 | id, title, device_id, test_type, steps |
| tb_test_execution | 执行记录表 | id, case_id, status, start_time, result |
| tb_test_report | 测试报告表 | id, execution_id, pass_rate, report_file |

---

## 🔐 安全说明

### JWT认证流程

1. 用户登录 → 验证用户名密码
2. 生成JWT Token（有效期2小时）
3. 客户端携带Token访问API
4. Gateway网关验证Token
5. 提取用户信息并转发到后端服务

### 权限角色

- **ADMIN**：系统管理员，拥有所有权限
- **TEST_MANAGER**：测试负责人，管理项目和用例
- **TESTER**：测试工程师，执行测试任务
- **VIEWER**：访客，仅查看权限

---

## 📊 监控与运维

### 健康检查

```bash
# 检查所有服务状态
curl http://localhost:8080/actuator/health
```

### 查看Nacos注册信息

访问：http://localhost:8848/nacos

### Docker容器管理

```bash
# 查看容器状态
docker-compose ps

# 查看日志
docker-compose logs -f [service_name]

# 重启服务
docker-compose restart [service_name]

# 停止所有服务
docker-compose down
```

---

## 🧪 测试指南

### 单元测试

```bash
mvn test
```

### 接口测试

推荐使用Postman或Apifox导入以下集合：
- 导入文件：`docs/TestHub-API.postman_collection.json`

### 压力测试

使用JMeter测试并发场景：
- 测试计划：`docs/TestHub-LoadTest.jmx`

---

## 📝 开发指南

### 代码规范

- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一使用RestControllerAdvice处理异常
- 所有接口返回统一响应格式

### 新增微服务步骤

1. 创建新模块：`mvn archetype:generate`
2. 配置Nacos注册：`spring.cloud.nacos.discovery.server-addr`
3. 添加Gateway路由配置
4. 实现业务逻辑
5. 编写单元测试

---

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

1. Fork本仓库
2. 创建特性分支：`git checkout -b feature/AmazingFeature`
3. 提交更改：`git commit -m 'Add some AmazingFeature'`
4. 推送分支：`git push origin feature/AmazingFeature`
5. 提交Pull Request

---

## 📄 License

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 👨‍💻 作者信息

- **姓名**：[Your Name]
- **邮箱**：your.email@example.com
- **GitHub**：https://github.com/yourusername

---

## 🙏 致谢

- Spring Boot Team
- Spring Cloud Alibaba Team
- 感谢Anker实习经历提供的业务场景灵感

---

## 📞 联系方式

如有问题或建议，请通过以下方式联系：

- 提交Issue：https://github.com/yourusername/testhub-iot/issues
- 邮箱：support@testhub.com

---

**⭐ 如果这个项目对你有帮助，请给个Star支持一下！**
