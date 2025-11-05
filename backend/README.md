# Backend Project - Java 11 环境

## 项目概述

这是一个基于 Java 11 和 Spring Boot 的后端项目，使用 Gradle 作为构建工具。

## 环境要求

- **Java**: OpenJDK 11 或更高版本
- **Gradle**: 8.5 (通过 Gradle Wrapper 管理)
- **操作系统**: macOS, Linux, Windows

## 环境安装

### Java 11 安装 (macOS)

1. 使用 Homebrew 安装 OpenJDK 11:
   ```bash
   brew install openjdk@11
   ```

2. 创建系统级别的符号链接:
   ```bash
   sudo ln -sfn /opt/homebrew/opt/openjdk@11/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-11.jdk
   ```

3. 将 Java 11 添加到 PATH (添加到 ~/.zshrc 或 ~/.bash_profile):
   ```bash
   export PATH="/opt/homebrew/opt/openjdk@11/bin:$PATH"
   ```

4. 重新加载配置文件:
   ```bash
   source ~/.zshrc
   ```

5. 验证安装:
   ```bash
   java -version
   ```
   应该显示类似以下内容:
   ```
   openjdk version "11.0.28" 2025-07-15
   OpenJDK Runtime Environment Homebrew (build 11.0.28+0)
   OpenJDK 64-Bit Server VM Homebrew (build 11.0.28+0, mixed mode)
   ```

## 项目结构

```
backend/
├── build.gradle          # 主构建配置文件
├── settings.gradle       # 项目设置文件
├── gradle.properties     # Gradle 属性配置
├── gradlew              # Gradle Wrapper 脚本 (Unix/Linux/macOS)
├── gradlew.bat          # Gradle Wrapper 脚本 (Windows)
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar        # Gradle Wrapper JAR
│       └── gradle-wrapper.properties # Wrapper 配置
├── modules/             # 子模块目录
├── JAVA_TESTING_GUIDE.md # Java 测试指南
└── README.md           # 项目说明文档
```

## 构建和运行
```bash
./gradlew :modules:app:bootRun
```
### 构建项目

```bash
# 构建整个项目
./gradlew build

# 清理构建产物
./gradlew clean

# 运行测试
./gradlew test

# 生成测试覆盖率报告
./gradlew jacocoTestReport
```

### 查看可用任务

```bash
./gradlew tasks
```

## 技术栈

- **Java**: OpenJDK 11
- **Spring Boot**: 2.7.18
- **构建工具**: Gradle 8.5
- **测试框架**: JUnit Platform
- **代码覆盖率**: JaCoCo
- **日志框架**: SLF4J

## 开发配置

### Gradle 配置

项目使用以下 Gradle 配置优化:

- **并行构建**: `org.gradle.parallel=true`
- **构建缓存**: `org.gradle.caching=true`
- **按需配置**: `org.gradle.configureondemand=true`
- **JVM 参数**: `-Xmx2048m -Dfile.encoding=UTF-8`

### 测试配置

- **并行测试**: 启用
- **最大并行分支**: 4
- **最小覆盖率**: 80%

## 常用命令

```bash
# 检查 Java 版本
java -version

# 检查 Gradle 版本
./gradlew --version

# 构建项目
./gradlew build

# 运行测试
./gradlew test

# 生成覆盖率报告
./gradlew jacocoTestReport

# 查看依赖
./gradlew dependencies

# 清理项目
./gradlew clean
```

## 故障排除

### 常见问题

1. **Java 版本不匹配**
   - 确保使用 Java 11
   - 检查 `JAVA_HOME` 环境变量

2. **Gradle Wrapper 问题**
   - 确保 `gradle-wrapper.jar` 存在
   - 检查 `gradlew` 脚本的执行权限

3. **构建失败**
   - 运行 `./gradlew clean build` 重新构建
   - 检查网络连接（依赖下载）

### 环境变量

如果需要设置编译器相关的环境变量:

```bash
export CPPFLAGS="-I/opt/homebrew/opt/openjdk@11/include"
```

## 贡献指南

1. 确保代码符合项目的编码规范
2. 运行测试确保所有测试通过
3. 保持测试覆盖率不低于 80%
4. 提交前运行 `./gradlew build` 确保构建成功

## 许可证

本项目使用 [许可证名称] 许可证。详情请参阅 LICENSE 文件。