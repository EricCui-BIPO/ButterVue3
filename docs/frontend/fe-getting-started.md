# 前端开发新手指南

## 概述

本指南为 I0 前端项目的新开发者提供快速上手指导，涵盖环境搭建、第一个功能开发和常见问题解决。


## 第一个功能开发

### 1. 启动开发环境

```bash
# 启动后端服务 (端口 8080)
cd backend && ./gradlew :modules:app:bootRun

# 确保在 frontend 目录下
cd frontend

# 启动所有前端门户
yarn dev

# 或启动特定门户
yarn dev:admin
```

### 2. 创建功能的标准流程

```bash
# 1. 确定功能类型
# 2. 定义 Types 模块
# 3. 创建 API 客户端
# 4. 开发 Composables
# 5. 创建业务组件
# 6. 实现页面组件
```

### 3. 实际示例：创建用户管理功能

#### 步骤 1: 创建页面结构

```bash
mkdir -p app/admin-portal/src/pages/user-management
touch app/admin-portal/src/pages/user-management/index.vue
touch app/admin-portal/src/pages/user-management/composables/useUserManagement.ts

# 如果场景复杂 composables 下可以拆分多个 hooks 来组织代码
```

#### 步骤 2: 创建组件目录

```bash
mkdir -p app/admin-portal/src/pages/user-management/components
touch app/admin-portal/src/pages/user-management/components/UserTable.vue
touch app/admin-portal/src/pages/user-management/components/UserFormDialog.vue
```

#### 步骤 3: 定义 Types (如果需要新类型)

```typescript
// shared/src/types/user/types.ts
export enum UserRole {
  ADMIN = "ADMIN",
  USER = "USER",
}

export interface User {
  id: string;
  username: string;
  email: string;
  role: UserRole;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}
```

#### 步骤 4: 创建 API 客户端

```typescript
// shared/src/api/domains/user/index.ts
import { request } from "../../core/request";
import type { User, CreateUserRequest } from "@I0/shared/types";

export class UserAPI {
  async getUsers(params?: UserPageParams) {
    return request.get("/users", { params });
  }

  async createUser(data: CreateUserRequest) {
    return request.post("/users", data);
  }

  async updateUser(id: string, data: Partial<CreateUserRequest>) {
    return request.put(`/users/${id}`, data);
  }

  async deleteUser(id: string) {
    return request.delete(`/users/${id}`);
  }
}

export const userAPI = new UserAPI();
```

#### 步骤 5: 创建页面级 Composable

```typescript
// app/admin-portal/src/pages/user-management/composables/useUserManagement.ts
import { ref, createSharedComposable } from "@vueuse/core";
import { ElMessage, ElMessageBox } from "element-plus";
import { userAPI } from "@I0/shared/api";
import type { User, CreateUserRequest } from "@I0/shared/types";

export const useUserManagement = createSharedComposable(() => {
  const loading = ref(false);
  const users = ref<User[]>([]);

  const loadUsers = async () => {
    loading.value = true;
    try {
      const { data } = await userAPI.getUsers();
      users.value = data || [];
    } finally {
      loading.value = false;
    }
  };

  const createUser = async (data: CreateUserRequest) => {
    await userAPI.createUser(data);
    ElMessage.success("创建成功");
    await loadUsers();
  };

  return { users, loading, loadUsers, createUser };
});
```

#### 步骤 6: 实现页面组件

```vue
<!-- app/admin-portal/src/pages/user-management/index.vue -->
<template>
  <div class="page-user-management">
    <UserTable :users="users" :loading="loading" @refresh="loadUsers" />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useUserManagement } from "./composables/useUserManagement";
import { UserTable } from "./components";

const { users, loading, loadUsers } = useUserManagement();

onMounted(() => {
  loadUsers();
});
</script>

<style scoped lang="scss">
.page-user-management {
  @include page-container;
}
</style>

<route lang="yaml">
name: UserManagement
meta:
  title: "用户管理"
  layout: "main"
</route>
```

## 常见问题解答

### 开发问题

#### 类型错误

```bash
# 运行类型检查
yarn type-check
```

#### 样式不生效

1. 确保使用 `<style scoped lang="scss">`
2. 检查 SCSS 变量是否正确导入
3. 验证 CSS 选择器优先级

#### 路由不工作

1. 确认文件名是 `index.vue`
2. 检查 `<route lang="yaml">` 块配置
3. 验证路径别名配置

### 调试技巧

#### 浏览器调试

- 使用 Vue DevTools 检查组件状态
- 在 Network 面板检查 API 请求
- 在 Console 面板查看错误信息

#### 控制台调试

```typescript
console.log("调试信息", data);
console.table(users.value); // 表格形式显示数组
console.time("操作耗时");
// ...代码
console.timeEnd("操作耗时");
```

#### 后端调试

```bash
# 启动后端调试模式
cd backend && ./gradlew :modules:app:bootRun --debug

# 查看健康检查
curl http://localhost:8080/actuator/health
```

## 开发资源

### 必读文档

- [架构设计](./fe-architecture.md) - 系统架构说明
- [编码规范](./fe-standards.md) - 编码标准
- [模板库](./fe-templates.md) - 代码模板
- [测试指南](./fe-testing-standard.md) - 测试指导

### 实际参考

- 查看 `admin-portal` 的 `entity` 页面作为常见 crud 完整示例
- 参考 `shared/src/components` 中的通用组件
- 学习 `shared/src/types` 中的类型定义模式

### 工具推荐

- **VS Code** - 推荐扩展：Vue, TypeScript, ESLint, Prettier
- **Vue DevTools** - 浏览器扩展，用于调试 Vue 应用
- **Postman** - API 测试工具

## 快速参考

### 技术栈版本

**主要技术栈**
- **Vue**: 3.3.0
- **TypeScript**: 5.3.3
- **Vite**: 5.0.10
- **Element Plus**: 2.11.2
- **Pinia**: 状态管理

**关键工具库**
- **@vueuse/core**: 13.9.0
- **unplugin-vue-router**: 0.10.8
- **lodash**: 4.17.21
- **dayjs**: 1.11.18

### 开发命令速查

```bash
# 启动开发服务器
yarn dev              # 启动所有门户
yarn dev:client       # 客户端 (3001)
yarn dev:service      # 服务端 (3002)
yarn dev:admin        # 管理端 (3003)
yarn dev:talent       # 人才端 (3004)

# 构建和测试
yarn build           # 构建所有门户
yarn test            # 运行单元测试
yarn test:coverage   # 生成覆盖率报告
yarn test:e2e        # 运行端到端测试
yarn lint            # 代码检查
yarn type-check      # 类型检查
```

### 端口配置

- **Client Portal**: 3001
- **Service Portal**: 3002
- **Admin Portal**: 3003
- **Talent Portal**: 3004
- **Backend API**: 8080

### 常用导入路径

```typescript
// 共享库导入
import { Component } from '@I0/shared/components'
import { useComposable } from '@I0/shared/composables'
import { API } from '@I0/shared/api'
import type { EntityType } from '@I0/shared/types'

// 本地导入
import Component from './components/Component.vue'
import { useComposable } from './composables/useComposable'
```

## 下一步

完成环境搭建和第一个功能后，建议：

1. **深入学习架构** - 阅读 [架构设计](./fe-architecture.md)
2. **掌握编码规范** - 阅读 [编码规范](./fe-standards.md)
3. **学习最佳实践** - 阅读 [模板库](./fe-templates.md)
4. **运行测试** - 执行 `yarn test` 了解测试框架

## 获取帮助

遇到问题时：

1. 检查本文档的常见问题部分
2. 查看相关文档的具体说明
3. 参考现有功能的实现方式
4. 使用调试工具排查问题

---
