# I0 Frontend 项目 - Claude 指导文档

## 项目概述

I0 Frontend 是一个基于 Vue 3 的前端 monorepo 项目，使用 Workspace 架构管理多个应用门户。

### 项目结构

```
frontend/
├── app/                    # 应用目录
│   ├── admin-portal/      # 管理员门户
│   ├── client-portal/      # 客户门户
│   ├── service-portal/    # 服务门户
│   └── talent-portal/     # 人才门户
├── shared/                 # 共享代码库
│   ├── src/
│   │   ├── components/     # 通用组件 (有 README.md 索引)
│   │   ├── composables/    # 组合式函数
│   │   ├── api/           # API 接口
│   │   ├── utils/         # 工具函数
│   │   ├── stores/        # Pinia 状态管理
│   │   ├── types/         # TypeScript 类型定义
│   │   └── styles/        # 全局样式
├── configs/               # 配置文件
├── tests/                 # 测试文件
└── package.json           # 根级包配置
```

## 技术栈

- **框架**: Vue 3 + TypeScript
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router
- **包管理**: Yarn Workspaces
- **国际化**: Vue I18n
- **样式**: Sass 支持
- **测试**: Vitest + Playwright + Cucumber

## 需要长期记忆
- **新手指南**:
    - 环境搭建、第一个功能开发、常见问题解答
    - @docs/frontend/fe-getting-started.md
- **架构设计**:
    - 系统架构设计、模块职责划分、依赖关系说明
    - @docs/frontend/fe-architecture.md
- **编码规范**:
    - 代码风格、文件结构、组件设计、性能优化规范
    - @docs/frontend/fe-standards.md
- **模板库**:
    - 标准代码模板、最佳实践、完整开发流程示例
    - @docs/frontend/fe-templates.md
- **测试规范**:
    - 单元测试、集成测试和 E2E 测试规范
    - @docs/frontend/fe-testing-standard.md



## 开发命令

### 启动开发服务器
```bash
# 以下需要在 frontend 目录下执行
# 启动所有应用
yarn dev

# 启动单个应用
yarn dev:admin      # 管理员门户
yarn dev:client     # 客户门户
yarn dev:service    # 服务门户
yarn dev:talent     # 人才门户
```

### 构建
```bash
# 构建所有应用
yarn build

# 构建单个应用
yarn build:admin    # 管理员门户
yarn build:client   # 客户门户
yarn build:service  # 服务门户
yarn build:talent   # 人才门户
```

### 代码质量
```bash
# 运行所有检查
yarn lint           # ESLint 检查
yarn format         # Prettier 格式化
yarn type-check     # TypeScript 类型检查

# 针对特定应用
yarn lint:admin     # 管理员门户
yarn lint:shared    # 共享代码库
```

### 测试
```bash
# 单元测试
yarn test                    # Vitest 单元测试
yarn test:coverage          # 测试覆盖率

# E2E 测试
yarn test:e2e               # Playwright E2E 测试
yarn test:e2e:admin         # 管理员门户 E2E
yarn test:e2e:ui            # Playwright UI 模式

# BDD 测试
yarn test:bdd               # Cucumber BDD 测试
yarn test:bdd:search        # 搜索功能 BDD 测试
```

## 共享组件库

### 组件索引
所有通用组件都在 `shared/src/components/` 目录中，并有详细文档：

- **I0Table** - 高级表格组件 (支持分页、排序、自定义格式化)
- **ActionDropdown** - 下拉菜单触发器 (表格行操作、卡片操作)
- **LocationDisplay** - 地理位置展示 (国家旗帜 + 位置名称)

### 组件使用
```typescript
// 从共享库导入组件
import { I0Table, ActionDropdown, LocationDisplay } from '@I0/shared/components'

// 导入类型
import type { I0TableProps, TableColumn, Location } from '@I0/shared/components'
```

## 工作空间架构

### 包导出规则
- `@I0/shared` - 主导出包
- `@I0/shared/components` - 组件导出
- `@I0/shared/api` - API 导出
- `@I0/shared/composables` - 组合式函数导出
- `@I0/shared/utils/*` - 工具函数导出

### 应用依赖
所有应用都依赖 `@I0/shared` 包，确保代码复用性。

## 开发规范

### 代码风格
- 使用 TypeScript 严格模式
- 遵循 ESLint + Prettier 规范
- 组件使用 PascalCase 命名
- 文件使用 kebab-case 命名

### 组件开发
- 新组件必须在 `shared/src/components/` 下创建
- 每个组件需要 README.md 文档
- 在 `shared/src/components/index.ts` 中导出
- 遵循单一职责原则

### 提交规范
- 代码提交前运行 `yarn lint` 和 `yarn type-check`
- 新功能需要对应的测试用例
- 遵循 Git Flow 分支管理

## 配置文件

### 重要配置
- `tsconfig.json` - TypeScript 配置
- `vite.config.*.ts` - Vite 构建配置
- `playwright.config.ts` - E2E 测试配置
- `.eslintrc.js` - ESLint 配置

### 环境变量
- `.env` - 环境变量配置
- 各应用可有独立的 `.env.*` 文件

## 测试策略

### 测试层次
1. **单元测试** - Vitest 测试组件和工具函数
2. **集成测试** - 组件间交互测试
3. **E2E 测试** - Playwright 完整用户流程测试
4. **BDD 测试** - Cucumber 业务场景测试

### 测试文件位置
- `tests/unit/` - 单元测试
- `tests/e2e/` - E2E 测试
- `tests/e2e/*/features/` - BDD 特性文件

## 构建和部署

### 构建流程
1. 运行 `yarn type-check` 进行类型检查
2. 运行 `yarn lint` 进行代码检查
3. 运行 `yarn test` 进行测试
4. 运行 `yarn build` 构建所有应用

### 输出目录
- `app/*/dist/` - 各应用的构建输出
- `shared/dist/` - 共享库构建输出

## 故障排除

### 常见问题
1. **依赖问题** - 运行 `yarn install` 重新安装
2. **类型错误** - 检查 `tsconfig.json` 和导入路径
3. **构建失败** - 检查 Vite 配置和环境变量
4. **测试失败** - 检查 Playwright 配置和测试环境

### 调试工具
- Vue DevTools - 组件调试
- Vite Dev Server - 热重载开发
- Playwright UI - E2E 测试调试

## 版本信息

- **Node.js**: >= 20.0.0
- **Yarn**: >= 1.22.0
- **Vue**: 3.3+
- **TypeScript**: ~5.3.3
- **Element Plus**: 2.11+

## 联系信息

项目维护者可以通过 Git 提交记录或项目管理工具联系开发团队。