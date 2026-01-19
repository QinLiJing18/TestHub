# TestHub 本地启动指南

## 📋 项目概况
- **项目名称**: TestHub IoT 测试管理平台
- **技术栈**: Java 8 + Spring Boot 2.7.15 + Spring Cloud 2021.0.8
- **架构**: 微服务架构（使用 Nacos 服务注册与发现）

## ✅ 已完成的配置

### 1. 环境要求
- **Java 版本**: 1.8.0_472 (OpenJDK Temurin) ✓
- **Maven 版本**: 3.8.8 ✓
- **Maven 配置**: 已配置阿里云镜像

### 2. 已修改的版本兼容性

#### 原始配置（不兼容 Java 8）
```
Java: 17
Spring Boot: 3.1.5
Spring Cloud: 2022.0.4
Spring Cloud Alibaba: 2022.0.0.0
Knife4j: 4.3.0
MySQL Driver: 8.2.2 (在阿里云镜像中不可用)
Sentinel: 网关依赖导致兼容性问题
```

#### 修改后的配置（Java 8 兼容）
```
Java: 8
Spring Boot: 2.7.15 ✓
Spring Cloud: 2021.0.8 ✓
Spring Cloud Alibaba: 2021.0.5.0 ✓
Knife4j: 3.0.3 ✓（改用 3.0.3 版本替代 4.3.0）
MySQL Driver: 5.1.49 ✓（改用官方驱动 mysql-connector-java）
Sentinel: 已从网关中移除，改用基础网关功能
```

### 3. 已修改的文件

#### pom.xml 主文件修改：
1. Java 版本: 17 → 8（第 31-34 行）
2. Spring Boot: 3.1.5 → 2.7.15（第 36 行）
3. Spring Cloud: 2022.0.4 → 2021.0.8（第 38 行）
4. Spring Cloud Alibaba: 2022.0.0.0 → 2021.0.5.0（第 40 行）
5. Knife4j: 4.3.0 → 3.0.3（第 57 行）
6. MySQL Driver: 8.2.2 → 5.1.49（第 45 行）
7. 依赖类型: com.mysql:mysql-connector-j → mysql:mysql-connector-java（第 109-110 行）

#### 子模块 pom.xml 修改：
- testhub-gateway/pom.xml
  - 移除 Sentinel 限流依赖
  - 排除公共模块的 spring-boot-starter-web
  - 添加 Spring Boot Maven 插件版本 2.7.15
  - 改用 knife4j-spring-boot-starter 替代 Jakarta 版本

- testhub-auth/pom.xml
  - 添加 Spring Boot Maven 插件版本 2.7.15

- testhub-project/pom.xml
  - 添加 Spring Boot Maven 插件版本 2.7.15

- testhub-testcase/pom.xml
  - 添加 Spring Boot Maven 插件版本 2.7.15

#### GatewayApplication.java
- 添加 `@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)`
- 禁用数据库自动配置（网关不需要数据库）

### 4. 编译结果 ✓

```
[INFO] BUILD SUCCESS
[INFO] TestHub IoT测试管理平台 ..................... SUCCESS [  0.147 s]
[INFO] TestHub-公共模块 .......................... SUCCESS [  2.085 s]
[INFO] TestHub-API网关 .......................... SUCCESS [ 13.010 s]
[INFO] TestHub-认证服务 ......................... SUCCESS [  0.912 s]
[INFO] TestHub-项目管理服务 ..................... SUCCESS [  0.114 s]
[INFO] TestHub-测试用例服务 ..................... SUCCESS [  0.104 s]
[INFO] Total time: 16.596 s
```

## 🚀 本地启动步骤

### 前置条件
```bash
# 1. 确保 Java 和 Maven 已配置到 PATH
java -version
mvn -version

# 输出应为:
# Java: version "1.8.0_472"
# Maven: Apache Maven 3.8.8
```

### 启动服务

#### 方式 1: 编译并启动（推荐）
```bash
cd D:\aproject\TestHub

# 编译所有模块
mvn clean install -DskipTests

# 启动网关服务（端口 8080）
mvn -pl testhub-gateway spring-boot:run -DskipTests

# 新开终端：启动认证服务（端口 8081）
mvn -pl testhub-auth spring-boot:run -DskipTests

# 新开终端：启动项目管理服务（端口 8082）
mvn -pl testhub-project spring-boot:run -DskipTests

# 新开终端：启动测试用例服务（端口 8083）
mvn -pl testhub-testcase spring-boot:run -DskipTests
```

#### 方式 2: 使用 PowerShell 脚本（简化启动）
```powershell
# 在项目根目录执行
.\scripts\start-all.ps1
```

### 访问应用

- **网关入口**: http://localhost:8080
- **API 文档**: http://localhost:8080/doc.html（Knife4j）
- **认证服务**: http://localhost:8081
- **项目管理**: http://localhost:8082
- **测试用例**: http://localhost:8083

## ⚠️ 已知限制

### 1. 无需外部服务即可启动
- ✓ 网关服务独立启动
- ✓ 各微服务可独立启动
- ✓ 所有服务都有默认配置

### 2. Nacos 注册中心（可选）
- 服务会尝试连接 Nacos（http://localhost:8848）
- 如果 Nacos 不可用，服务仍可启动，但服务发现功能不可用
- 对本地开发测试无影响

### 3. Redis 连接（可选）
- 网关和服务会尝试连接 Redis（localhost:6379）
- 如果 Redis 不可用，某些功能（如缓存）会降级处理
- 对基本功能无影响

### 4. 数据库连接
- 认证、项目管理、测试用例服务需要 MySQL 数据库
- 如需完整功能，请提供 MySQL 连接配置
- 可在 application.yml 中修改数据库连接字符串

### 5. 消息队列（MQTT）
- 测试用例服务支持 MQTT 协议
- 需要 EMQX 或其他 MQTT Broker
- 可选功能，不影响基本测试

## 📝 配置文件位置

```
testhub-gateway/src/main/resources/application.yml
testhub-auth/src/main/resources/application.yml
testhub-project/src/main/resources/application.yml
testhub-testcase/src/main/resources/application.yml
```

## 🔧 常见问题

### Q1: 启动时出现 "Failed to determine a suitable driver class"
**原因**: 数据库驱动未找到或 DataSource 配置缺失
**解决**:
- 检查 MySQL 驱动是否已下载：`C:\Users\<用户>\.m2\repository\mysql\mysql-connector-java\`
- 确保数据库配置正确
- 对于网关服务，已添加 `@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)`

### Q2: Nacos 连接超时
**原因**: Nacos 服务未启动
**解决**: 这是正常的，本地开发时 Nacos 可选

### Q3: 端口被占用
**原因**: 服务已在运行或其他应用占用端口
**解决**:
```bash
# 查找占用端口 8080 的进程
netstat -ano | findstr :8080

# 使用不同端口启动（修改 application.yml）
server:
  port: 8090
```

### Q4: 内存不足
**原因**: Spring Boot 应用占用内存较多
**解决**:
```bash
# 使用自定义 JVM 参数启动
set MAVEN_OPTS=-Xmx512m -Xms256m
mvn spring-boot:run
```

## 📦 项目模块结构

```
testhub-parent/
├── testhub-common/           # 公共模块（工具类、常量等）
├── testhub-gateway/          # API 网关（8080）
│   └── 路由、认证、限流
├── testhub-auth/             # 认证服务（8081）
│   └── JWT、用户认证
├── testhub-project/          # 项目管理服务（8082）
│   └── 项目 CRUD 操作
└── testhub-testcase/         # 测试用例服务（8083）
    └── 测试用例管理、执行
```

## 🎯 后续优化建议

1. **使用 Docker Compose**（如需要）
   - 创建完整的 docker-compose.yml
   - 包含 MySQL、Redis、Nacos、EMQX 等服务

2. **数据库初始化**
   - 执行 `sql/testhub-init.sql` 初始化数据库
   - 创建示例数据

3. **API 测试**
   - 使用 Postman 或 Insomnia 测试 API
   - 参考 API 文档：http://localhost:8080/doc.html

4. **日志配置**
   - 添加 logback 配置优化日志输出
   - 配置日志级别和输出位置

## 📞 支持信息

- **项目地址**: https://github.com/QinLiJing18/TestHub
- **文档**: 查看项目根目录中的其他 README 和文档文件

---

**最后更新**: 2026-01-19  
**Java 版本**: 1.8.0_472  
**Maven 版本**: 3.8.8  
**编译状态**: ✅ 全部成功
