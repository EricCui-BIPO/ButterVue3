# I0 项目开发文档

## 🚀 快速开始

### 快速启动（推荐）

手动启动服务，可以分别控制前后端：

```bash
# 启动所有服务
# 终端1: 启动后端服务
cd backend && ./gradlew :modules:app:bootRun

# 终端2: 启动前端所有门户
cd frontend && yarn dev
```

### 选择性启动

```bash
# 仅启动后端
cd backend && ./gradlew :modules:app:bootRun

# 仅启动前端
cd frontend && yarn dev

# 启动特定门户
cd frontend && yarn dev:client    # 客户端门户 - 端口 3001
cd frontend && yarn dev:service   # 服务端门户 - 端口 3002
cd frontend && yarn dev:admin     # 管理员门户 - 端口 3003
cd frontend && yarn dev:talent    # 人才门户 - 端口 3004
```

### 访问地址

| 服务 | 端口 | 访问地址 |
|------|------|----------|
| 后端服务 | 8088 | http://localhost:8088 |
| 客户端门户 | 3001 | http://localhost:3001 |
| 服务端门户 | 3002 | http://localhost:3002 |
| 管理员门户 | 3003 | http://localhost:3003 |
| 人才门户 | 3004 | http://localhost:3004 |

### 环境要求

- **Node.js**: >= 20.0.0
- **Yarn**: >= 1.22.0
- **Java**: OpenJDK 11

> 💡 脚本会自动检查环境依赖和端口占用情况

---

## 前端开发

### 环境要求
- **Node.js**: >= 20.0.0
- **Yarn**: >= 1.22.0

### 安装依赖
```bash
# 进入前端项目目录
cd frontend

# 安装依赖
yarn install
```

### 本地启动
```bash
# 进入前端项目目录，确保当前目录是 frontend
# cd frontend

# 启动所有前端应用
yarn dev

# 单独启动特定应用
yarn dev:client      # 客户端门户 - 端口 3001
yarn dev:service     # 服务端门户 - 端口 3002
yarn dev:admin       # 管理员门户 - 端口 3003
yarn dev:talent      # 人才门户 - 端口 3004
```

### 构建和测试
```bash
# 进入前端项目目录，确保当前目录是 frontend
# cd frontend

# 构建所有应用
yarn build

# 构建特定应用
yarn build:client
yarn build:service
yarn build:admin
yarn build:talent

# 运行测试
yarn test

# 生成测试覆盖率报告
yarn test:coverage
```

### 代码质量
```bash
# 进入前端项目目录，确保当前目录是 frontend
# cd frontend

# 代码检查
yarn lint

# 类型检查
yarn type-check

# 代码格式化
yarn format

# 清理构建产物
yarn clean
```

## 后端开发

### 环境要求
- **Java**: OpenJDK 11
- **Gradle**: 8.5 (通过 Gradle Wrapper 管理)

### 启动服务
```bash
# 进入后端项目目录
cd backend

# 启动服务
./gradlew :modules:app:bootRun
```

### 构建和测试
```bash
cd backend

# 构建整个项目
./gradlew build

# 清理构建产物
./gradlew clean

# 运行所有测试
./gradlew test

# 生成测试覆盖率报告
./gradlew jacocoTestReport

# 查看所有可用任务
./gradlew tasks
```

### 开发调试
```bash
cd backend

# 查看项目依赖
./gradlew dependencies

# 查看特定模块依赖
./gradlew :modules:app:dependencies

# 编译但不运行测试
./gradlew compileJava

# 运行特定测试
./gradlew test --tests *YourTestClass*
```

## 工作流程

### 推荐工作流程
1. **启动后端服务**: `cd backend && ./gradlew :modules:app:bootRun`
2. **启动前端应用**: `cd frontend && yarn dev` (或选择特定门户)
3. **开发调试**: 检查各服务运行状态
4. **运行测试**: `cd frontend && yarn test` 和 `cd backend && ./gradlew test`
5. **代码检查**: `cd frontend && yarn lint`
6. **构建项目**: `cd frontend && yarn build` 和 `cd backend && ./gradlew build`

### 传统工作流程（手动启动）
1. 启动后端服务: `cd backend && ./gradlew :modules:app:bootRun`
2. 启动前端应用: `cd frontend && yarn dev` (或 `yarn dev:admin` 等启动特定门户)
3. 开发完成后运行测试: `cd frontend && yarn test` 和 `cd backend && ./gradlew test`
4. 提交前进行代码检查: `cd frontend && yarn lint`
5. 构建项目: `cd frontend && yarn build` 和 `cd backend && ./gradlew build`

## 端口配置
- 后端服务: http://localhost:8088
- 客户端门户: http://localhost:3001
- 服务端门户: http://localhost:3002
- 管理员门户: http://localhost:3003
- 人才门户: http://localhost:3004