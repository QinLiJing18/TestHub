# Git 命令学习文档 - 项目管理实战

> 本文档记录了今天（2026年1月19日）所有手动执行的命令，特别是 Git 版本管理相关的操作，用于学习和回顾。

---

## 📚 目录
1. [Git 基础查看命令](#git-基础查看命令)
2. [Git 文件管理命令](#git-文件管理命令)
3. [Git 提交管理命令](#git-提交管理命令)
4. [Git 编码配置命令](#git-编码配置命令)
5. [Maven 编译命令](#maven-编译命令)
6. [命令执行流程示意](#命令执行流程示意)
7. [学习总结](#学习总结)

---

## Git 基础查看命令

### 1️⃣ 查看 Git 状态
```powershell
git status
```

**作用**：
- 查看当前分支
- 查看有多少本地提交领先远程
- 查看修改的文件（已追踪、未追踪）
- 查看未提交的更改

**输出示例**：
```
On branch master
Your branch is ahead of 'origin/master' by 2 commits.
  (use "git push" to publish your local commits)

Untracked files:
  (use "git add <file>..." to include in what will be committed)
        testhub-auth/target/
        testhub-common/target/
        ...
```

**学习点**：
- `ahead of 'origin/master' by 2 commits` = 本地比远程新 2 个提交
- `Untracked files` = 新增文件，还未被 git 追踪
- `Modified` = 已追踪的文件被修改了

---

### 2️⃣ 查看短格式状态
```powershell
git status --short
```

**作用**：
- 以简洁形式显示状态
- 每行一个文件，更容易阅读

**输出示例**：
```
 M pom.xml
 M testhub-auth/pom.xml
 M testhub-gateway/pom.xml
?? testhub-auth/target/
?? testhub-common/target/
```

**符号含义**：
- `M` = Modified（已修改）
- `A` = Added（新增）
- `??` = Untracked（未追踪）
- `D` = Deleted（已删除）

---

### 3️⃣ 列出已追踪的文件
```powershell
git ls-files
```

**作用**：
- 显示 git 正在管理的所有文件
- 用于检查是否有不应该被追踪的文件

**结合查询**（查找 target 目录）：
```powershell
git ls-files | Select-String -Pattern "target/"
```

**输出示例**：
```
testhub-auth/target/classes/application.yml
testhub-common/target/classes/com/testhub/common/...
testhub-gateway/target/classes/com/testhub/gateway/...
```

**学习点**：
- 虽然 `.gitignore` 设置了忽略规则，但历史提交中可能已经包含了 `target/` 目录
- 需要用 `git rm --cached` 从版本控制中移除

---

### 4️⃣ 查看 Git 日志
```powershell
git log --oneline -5
```

**作用**：
- 查看最近 5 个提交
- `--oneline` 每行一个提交（简洁格式）

**输出示例**：
```
20a2023 chore: Java 8 兼容性配置...
abc1234 某个之前的提交
def5678 更早的提交
...
```

---

## Git 文件管理命令

### 1️⃣ 添加单个文件到暂存区
```powershell
git add pom.xml
```

**作用**：
- 将修改过的 `pom.xml` 添加到暂存区（Stage）
- 暂存区是提交前的准备区域

**三个状态理解**：
```
工作目录(Untracked) → git add → 暂存区(Staged) → git commit → 版本库(Committed)
```

---

### 2️⃣ 添加多个文件（批量操作）
```powershell
git add pom.xml
git add testhub-auth/pom.xml
git add testhub-gateway/pom.xml
git add testhub-project/pom.xml
git add testhub-testcase/pom.xml
git add testhub-common/pom.xml
git add testhub-gateway/src/main/java/com/testhub/gateway/GatewayApplication.java
git add testhub-gateway/src/main/resources/application.yml
git add LOCAL_STARTUP_GUIDE.md
git add STARTUP_STATUS.md
```

**执行结果**：
```
warning: LF will be replaced by CRLF in pom.xml
...
```

**学习点**：
- `LF/CRLF` 警告：Windows 和 Unix 的换行符不同
  - LF = Unix/Mac（`\n`）
  - CRLF = Windows（`\r\n`）
- 这只是警告，不影响功能

---

### 3️⃣ 添加所有修改文件
```powershell
git add .
```

**作用**：
- 快速添加当前目录及子目录的所有修改文件
- 不添加未追踪的新文件

---

### 4️⃣ 从版本控制中移除文件（保留本地）
```powershell
git rm --cached testhub-auth/target/
```

**作用**：
- 将 `target/` 目录从 git 追踪中移除
- `--cached` 参数表示只从 git 移除，不删除本地文件

**应用场景**：
- 不小心提交了编译产物（.class、.jar 等）
- 想从版本管理中移除但保留本地文件

**批量操作（全部 target 目录）**：
```powershell
git rm --cached -r testhub-auth/target
git rm --cached -r testhub-common/target
git rm --cached -r testhub-gateway/target
git rm --cached -r testhub-project/target
git rm --cached -r testhub-testcase/target
```

**参数说明**：
- `-r` = recursive（递归）
- `--cached` = 只从 git 移除，保留本地文件

---

### 5️⃣ 查看文件修改内容
```powershell
git diff pom.xml
```

**作用**：
- 显示文件在工作目录中相对于暂存区的更改
- 红色 = 删除行
- 绿色 = 新增行

**查看暂存区的更改**：
```powershell
git diff --cached
```

---

## Git 提交管理命令

### 1️⃣ 提交暂存区的文件
```powershell
git commit -m "chore: Java 8 兼容性配置完成

- 降级 Spring Boot 从 3.1.5 到 2.7.15
- 降级 Spring Cloud 从 2022.0.4 到 2021.0.8
- 修改 MySQL 驱动从 mysql-connector-j 8.2.2 到 mysql-connector-java 5.1.49
- 移除 Sentinel gateway 依赖
- Gateway 添加 DataSourceAutoConfiguration 排除
- Gateway 配置禁用 Nacos，使用直接 HTTP 路由
- 所有模块编译成功"
```

**作用**：
- 将暂存区的所有文件提交到本地版本库
- `-m` 后面跟提交信息

**提交信息最佳实践**：
```
第一行：简短总结（50 字以内）
空行
详细说明（可选）：
- 用 - 符号列出具体改动
- 解释为什么做这个改动
- 解释改动的影响
```

**输出示例**：
```
[master 20a2023] chore: Java 8 兼容性配置完成
 9 files changed, 477 insertions(+), 29 deletions(-)
 create mode 100644 LOCAL_STARTUP_GUIDE.md
 create mode 100644 STARTUP_STATUS.md
```

---

### 2️⃣ 查看提交历史
```powershell
git log --oneline
```

**输出**：
```
20a2023 chore: Java 8 兼容性配置完成
7f8c4a9 之前的某个提交
...
```

---

### 3️⃣ 撤销未暂存的修改
```powershell
git checkout -- pom.xml
```

**作用**：
- 丢弃工作目录中 `pom.xml` 的所有修改
- 恢复到上次提交的版本

⚠️ **警告**：此操作不可逆，请谨慎使用

---

### 4️⃣ 撤销已暂存的文件
```powershell
git reset HEAD pom.xml
```

**作用**：
- 将 `pom.xml` 从暂存区移除
- 文件修改保留在工作目录中（未删除）

---

## Git 编码配置命令

### 1️⃣ 配置 GUI 编码（修复 gitk 中文乱码）
```powershell
git config --global gui.encoding utf-8
```

**作用**：配置 gitk 界面显示为 UTF-8 编码

---

### 2️⃣ 配置提交信息编码
```powershell
git config --global i18n.commitencoding utf-8
```

**作用**：设置提交时使用 UTF-8 编码（保存中文）

---

### 3️⃣ 配置日志输出编码
```powershell
git config --global i18n.logoutputencoding utf-8
```

**作用**：设置查看日志时的输出编码为 UTF-8

**组合执行**（一行搞定）：
```powershell
git config --global gui.encoding utf-8; git config --global i18n.commitencoding utf-8; git config --global i18n.logoutputencoding utf-8
```

**学习点**：
- `--global` 表示全局配置（对所有 git 仓库生效）
- 也可以用 `--local` 配置单个仓库

---

### 4️⃣ 查看已配置的信息
```powershell
git config --global --list
```

**输出示例**：
```
gui.encoding=utf-8
i18n.commitencoding=utf-8
i18n.logoutputencoding=utf-8
...
```

---

## Maven 编译命令

### 1️⃣ 清理编译
```powershell
cd D:\aproject\TestHub
mvn clean
```

**作用**：
- 删除 `target/` 目录
- 清理所有编译产物

---

### 2️⃣ 编译并安装（完整构建）
```powershell
cd D:\aproject\TestHub
mvn clean install -DskipTests
```

**参数说明**：
- `clean` = 清理上一次编译
- `install` = 编译 + 打包 + 安装到本地仓库
- `-DskipTests` = 跳过测试（节省时间）

**执行过程**：
1. 清理 target 目录
2. 编译 Java 源文件
3. 打包（创建 JAR/WAR）
4. 安装到 Maven 本地仓库

**成功标志**：
```
[INFO] BUILD SUCCESS
[INFO] Total time: 16.596 s
[INFO] Finished at: 2026-01-19T...
```

---

### 3️⃣ 只编译（不打包）
```powershell
mvn compile
```

**作用**：
- 编译 Java 源代码
- 生成 .class 文件到 `target/classes/`

---

### 4️⃣ 启动单个服务
```powershell
mvn -pl testhub-gateway spring-boot:run
```

**参数说明**：
- `-pl testhub-gateway` = 只在 testhub-gateway 模块执行
- `spring-boot:run` = 运行 Spring Boot 应用

**启动其他服务**：
```powershell
mvn -pl testhub-auth spring-boot:run
mvn -pl testhub-project spring-boot:run
mvn -pl testhub-testcase spring-boot:run
```

---

### 5️⃣ 调试模式启动
```powershell
mvn -pl testhub-gateway -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005" spring-boot:run
```

**作用**：
- 启动服务并激活 JDWP 调试
- 可以在 VS Code/IDE 中附加调试器

---

## 命令执行流程示意

### 完整的 Git 工作流程：

```
┌─────────────────────────────────────────┐
│ 1. 修改文件 (pom.xml, 源代码等)          │
│    ↓                                     │
│ 2. git status (查看修改)                 │
│    ↓                                     │
│ 3. git add *.pom.xml (添加到暂存区)      │
│    ↓                                     │
│ 4. git status --short (确认暂存)        │
│    ↓                                     │
│ 5. git commit -m "..." (提交)            │
│    ↓                                     │
│ 6. git log --oneline (查看提交历史)      │
│    ↓                                     │
│ 7. git push (推送到远程，如需要)         │
└─────────────────────────────────────────┘
```

### 实际执行顺序（今天的例子）：

```
1️⃣  查看状态
    git status --short
    结果: 9 个文件修改，5 个 target 目录未追踪

2️⃣  添加源代码修改
    git add pom.xml
    git add testhub-auth/pom.xml
    ... (多个 pom.xml 文件)
    git add LOCAL_STARTUP_GUIDE.md
    git add STARTUP_STATUS.md

3️⃣  查看暂存状态
    git status
    结果: 已添加 9 个文件到暂存区

4️⃣  提交到版本库
    git commit -m "chore: Java 8 兼容性配置完成..."
    结果: ✅ [master 20a2023] 提交成功
          9 files changed, 477 insertions(+), 29 deletions(-)

5️⃣  查看提交历史
    git log --oneline
    结果: 20a2023 chore: Java 8 兼容性配置完成
          ... (之前的提交)

6️⃣  编码配置（修复中文）
    git config --global gui.encoding utf-8
    git config --global i18n.commitencoding utf-8
    git config --global i18n.logoutputencoding utf-8
    结果: 配置完成，gitk 现在可以显示中文
```

---

## 学习总结

### 🎯 核心概念

**1. Git 的三个区域**
```
工作目录(Working Directory)
    ↓ git add
暂存区(Staging Area)
    ↓ git commit
版本库(Repository)
    ↓ git push
远程仓库(Remote)
```

**2. 文件状态流转**
```
Untracked (未追踪) 
    ↓ git add
Staged (暂存区)
    ↓ git commit
Committed (已提交)
    ↓ git modify
Modified (已修改)
    ↓ git add
Staged (暂存区)
```

**3. 常用命令速查表**

| 操作 | 命令 | 说明 |
|------|------|------|
| 查看状态 | `git status` | 查看工作区状态 |
| 查看简洁状态 | `git status --short` | 一行一个文件 |
| 添加文件 | `git add <file>` | 添加到暂存区 |
| 添加所有 | `git add .` | 添加所有修改 |
| 移除追踪 | `git rm --cached <file>` | 保留本地文件 |
| 提交 | `git commit -m "msg"` | 提交并说明 |
| 查看日志 | `git log --oneline` | 查看提交历史 |
| 查看差异 | `git diff` | 查看修改内容 |

### 📖 学习要点

✅ **今天学到的知识点**：

1. **git status 和 git status --short**
   - 快速了解项目当前状态
   - 区分已修改、未追踪、未暂存的文件

2. **git add 的正确用法**
   - 单文件 `git add <file>`
   - 多文件 `git add file1 file2 file3`
   - 所有文件 `git add .`

3. **git rm --cached 的妙用**
   - 从版本控制移除编译产物（target/）
   - 不删除本地文件
   - 配合 .gitignore 使用

4. **git commit 的最佳实践**
   - 第一行简短总结（50字以内）
   - 空行分隔
   - 详细描述改动内容
   - 用 - 号列点

5. **编码问题的解决**
   - `git config --global` 全局配置
   - 三个编码配置保证中文显示正常

### 💡 实践建议

1. **每次提交前**：
   ```powershell
   git status          # 确认有哪些改动
   git diff            # 查看具体改动内容
   git add .           # 添加改动
   git commit -m "..."  # 编写清晰的提交信息
   ```

2. **编写提交信息**：
   - ✅ `fix: 修复 auth 服务启动失败`
   - ✅ `feat: 添加 Gateway 路由配置`
   - ❌ `modified pom.xml`（不清楚改了什么）

3. **不要追踪的文件**：
   - 编译产物：`target/`、`*.class`、`*.jar`
   - IDE 配置：`.idea/`、`.vscode/`
   - 依赖缓存：`node_modules/`
   - 环境变量：`.env`

4. **定期检查**：
   ```powershell
   git log --oneline -10  # 查看最近 10 个提交
   git ls-files           # 检查哪些文件被追踪
   ```

### 🔗 相关资源

- Git 官方文档：https://git-scm.com/doc
- 阮一峰 Git 教程：http://www.ruanyifeng.com/blog/2015/12/git-cheat-sheet.html
- Commit Message 规范：https://www.conventionalcommits.org/

---

**⏰ 最后更新**：2026年1月19日

