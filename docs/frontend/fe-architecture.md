# 前端架构设计指南

## 架构概述

I0 前端项目采用 Vue 3 Monorepo 架构，基于单一职责原则和依赖注入原则设计。系统由四个门户应用和一个共享库组成，确保代码复用和维护效率。

## 整体架构

### 系统组成

```
I0 Frontend Architecture
├── Portal Applications          # 门户应用 (4个)
│   ├── Client Portal (3001)     # 客户端应用
│   ├── Service Portal (3002)   # 服务端应用
│   ├── Admin Portal (3003)     # 管理端应用
│   └── Talent Portal (3004)    # 人才端应用
└── Shared Library (@I0/shared)  # 共享库
    ├── Core Modules           # 核心模块
    ├── Business Features      # 业务功能
    └── Infrastructure         # 基础设施
```

### 架构原则

#### 1. 单一职责原则

- **页面组件**: 只负责布局组合和路由配置
- **业务组件**: 负责特定业务领域的完整功能
- **通用组件**: 负责纯 UI 展示，无业务逻辑
- **组合式函数**: 负责抽象可复用的业务逻辑
- **工具函数**: 负责通用的数据处理和转换

#### 2. 依赖注入原则

- 高层模块不依赖低层模块的具体实现
- 通过接口和类型定义进行依赖抽象
- 使用组合式函数进行依赖注入

#### 3. 开放封闭原则

- 对扩展开放，对修改封闭
- 通过组合和继承扩展功能
- 避免修改现有代码来添加新功能

#### 4. Types 模块原则

- **Types 模块作为类型和常量的唯一来源**
- 按业务领域组织类型定义
- 统一导出，避免重复定义

## 模块详细设计

### 门户应用 (Portal Applications)

#### 应用结构

每个门户应用都是独立的 Vite 应用，但共享相同的配置模式：

```typescript
// 典型的门户应用结构
app/[portal]-portal/
├── src/
│   ├── pages/           # 页面组件 (基于文件的路由)
│   ├── layouts/         # 门户特定布局
│   ├── router/          # 路由配置
│   ├── stores/          # 门户特定状态
│   └── main.ts          # 应用入口
├── package.json
└── vite.config.ts       # Vite 配置
```

#### 应用职责

- **Client Portal**: 客户端功能，用户界面
- **Service Portal**: 服务端功能，服务提供商界面
- **Admin Portal**: 管理端功能，系统管理界面
- **Talent Portal**: 人才端功能，人才管理界面

### 共享库 (@I0/shared)

#### 库结构

```
shared/src/
├── api/                 # API 层 - HTTP 请求处理
│   └── domains/         # 按领域组织的 API 客户端
├── components/          # 通用 UI 组件
├── composables/         # 组合式函数
├── features/            # 业务功能模块
├── layouts/             # 布局组件
├── stores/              # Pinia 状态管理
├── styles/              # 全局样式
├── types/               # 类型和常量定义 (按领域)
├── utils/               # 工具函数
└── i18n/                # 国际化
```

#### 模块依赖关系

```
Types → API → Composables → Features
         ↓
       Stores → Components → Layouts
         ↓
       Styles → Utils
```

#### 依赖层级说明

1. **Types 模块**: 基础模块，无外部依赖，提供类型和常量定义
2. **API 模块**: 依赖 Types 模块，提供 HTTP 请求处理
3. **Composables 模块**: 依赖 Types、API、Stores 模块，提供业务逻辑抽象
4. **Features 模块**: 依赖 Types、Composables、Components 模块，提供业务功能组件
5. **Components 模块**: 依赖 Styles 模块，提供通用 UI 组件
6. **Stores 模块**: 依赖 Types 模块，提供状态管理

### 核心模块设计

#### Types 模块

**职责**: 类型定义和常量的唯一来源
**组织方式**: 按业务领域分组
**依赖**: 无外部依赖 (基础模块)

```typescript
// shared/src/types/[domain]/types.ts
export enum EntityType {
  EXAMPLE = "EXAMPLE",
}

export interface Entity {
  id: string;
  name: string;
  type: EntityType;
}

// shared/src/types/[domain]/index.ts
export * from "./types";
export const CONSTANTS = {
  EntityTypeDisplayNames: {
    [EntityType.EXAMPLE]: "示例",
  },
};
```

#### API 模块

**职责**: HTTP 请求处理，提供类型安全的 API 客户端
**依赖**: Types 模块
**组织方式**: 按业务领域分组

```typescript
// shared/src/api/domains/[domain]/index.ts
import request from "../../core/request";
import type { ApiResponse, PaginationData } from "@I0/shared/types";
import type {
  Entity,
  CreateEntityRequest,
  UpdateEntityRequest,
  EntityPageParams,
  EntityType,
} from "@I0/shared/types";

// 实体 API 类
class EntityAPI {
  private readonly basePath = "/entities";

  // 分页查询实体 - GET 方法
  async getEntities(params: EntityPageParams = {}) {
    return request.get<PaginationData<Entity>>(this.basePath, params);
  }

  // 创建实体 - POST 方法
  async createEntity(data: CreateEntityRequest): Promise<ApiResponse<Entity>> {
    return request.post<Entity>(`${this.basePath}`, data);
  }

  // 更新实体 - PUT 方法
  async updateEntity(
    id: string,
    data: UpdateEntityRequest
  ): Promise<ApiResponse<Entity>> {
    return request.put<Entity>(`${this.basePath}/${id}`, data);
  }

  // 删除实体 - DELETE 方法
  async deleteEntity(id: string): Promise<ApiResponse<void>> {
    return request.delete<void>(`${this.basePath}/${id}`);
  }
}

// 创建 API 实例
export const entityAPI = new EntityAPI();
```

#### Composables 模块

**职责**: 抽象可复用的业务逻辑
**依赖**: Types, API, Stores 模块
**组织方式**: 按功能领域分组

```typescript
// shared/src/composables/[feature]/use[Feature].ts
import { ref, createSharedComposable } from "@vueuse/core";
import { entityAPI } from "@I0/shared/api";

export const useFeatureManagement = createSharedComposable(() => {
  const loading = ref(false);
  const entities = ref([]);

  const loadEntities = async () => {
    loading.value = true;
    try {
      const { data } = await entityAPI.getEntities();
      entities.value = data || [];
    } catch (error) {
      console.error("Failed to load entities:", error);
    } finally {
      loading.value = false;
    }
  };

  return { entities, loading, loadEntities };
});
```

#### Features 模块

**职责**: 业务功能组件，包含完整的功能实现
**依赖**: Types, Composables, Components 模块
**组织方式**: 按业务领域分组

#### Components 模块

**职责**: 通用 UI 组件，纯展示逻辑
**依赖**: Styles 模块
**组织方式**: 按组件类型分组

## 数据流设计

### 状态管理架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Pages         │    │   Components    │    │   Composables   │
│   (View Layer)  │◄──►│   (UI Layer)    │◄──►│ (Logic Layer)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Stores        │    │      API        │    │   Types         │
│ (State Layer)   │◄──►│ (Data Layer)    │◄──►│ (Type Layer)    │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### 数据流向

1. **用户交互** → **页面组件** → **业务组件**
2. **业务组件** → **Composables** → **API 模块**
3. **API 模块** → **后端服务** → **数据返回**
4. **数据返回** → **Stores** → **组件更新**

## 路由架构

### 文件基于路由

使用 `unplugin-vue-router` 实现文件基于的路由系统：

```typescript
// 自动路由生成
src/pages/
├── index.vue              →  /
├── users/
│   ├── index.vue          →  /users
│   ├── [id].vue           →  /users/:id
│   └── create.vue         →  /users/create
└── settings/
    └── index.vue          →  /settings
```

### 路由配置

每个页面通过 `<route lang="yaml">` 块配置路由信息：

```yaml
<route lang="yaml">
name: UserManagement
meta:
  title: '用户管理'
  layout: 'main'
</route>
```

## 构建配置架构

### Vite 工厂模式

使用工厂模式动态生成配置，基于 `VITE_APP_TYPE` 环境变量：

```typescript
// configs/vite.config.factory.ts
export function createViteConfig(
  env: Record<string, string>,
  __dirname: string
) {
  const appType = env.VITE_APP_TYPE || "client";
  const port = env.VITE_APP_PORT || 3001;

  return {
    plugins: [
      VueRouter({
        routeBlockLang: "yaml",
        routesFolder: resolve(__dirname, "src/pages"),
        dts: resolve(__dirname, "src/router/types.d.ts"),
        exclude: ["**/components/**/*"],
      }),
    ],
    resolve: {
      alias: { "@": fileURLToPath(new URL("src", import.meta.url)) },
    },
    server: {
      port: Number(port),
      proxy: {
        "/api": { target: env.VITE_API_BASE_URL || "http://localhost:8080" },
      },
    },
    build: {
      outDir: `dist/${appType}-portal`,
    },
  };
}
```

### 环境配置

每个门户的环境变量：

- `VITE_APP_TYPE` - 门户类型 (client/service/admin/talent)
- `VITE_APP_PORT` - 开发端口
- `VITE_APP_NAME` - 应用名称
- `VITE_API_BASE_URL` - 后端 API 地址

## 性能优化架构

### 代码分割

- **页面级别**: 每个页面自动分割为独立 chunk
- **组件级别**: 动态导入大型组件
- **库级别**: 第三方库单独打包

### 状态管理优化

- **shallowRef**: 大数据列表使用浅引用
- **computed**: 计算属性缓存
- **debounce**: 搜索输入防抖处理

### 样式优化

- **CSS 变量**: 主题变量统一管理
- **will-change**: 动画性能优化
- **contain**: 渲染包含优化

## 扩展性设计

### 新门户添加

1. 创建新的门户应用目录
2. 配置 `VITE_APP_TYPE` 环境变量
3. 添加对应的构建脚本

### 新功能模块添加

1. 在 `shared/src/types/[domain]/` 定义类型
2. 在 `shared/src/api/domains/[domain]/` 创建 API 客户端
3. 在 `shared/src/composables/` 或页面级别创建 composables
4. 在 `shared/src/features/` 创建业务组件
5. 在对应门户的 `pages/` 目录创建页面

## 相关文档

- [编码规范](./fe-standards.md) - 编码标准和规范
- [模板库](./fe-templates.md) - 代码模板和最佳实践
- [新手指南](./fe-getting-started.md) - 环境搭建和入门
- [测试指南](./fe-testing-standard.md) - 测试相关指导
