# 前端代码模板库

## 概述

本模板库提供了 I0 前端项目中的核心代码模板，涵盖开发流程中的关键文件类型。所有模板都基于项目最佳实践设计。

## 核心模板

### Types 模块模板

#### 基础类型定义

```typescript
// shared/src/types/[domain]/types.ts
export enum EntityType {
  EXAMPLE = 'EXAMPLE',
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE'
}

export interface Entity {
  id: string
  name: string
  type: EntityType
  description?: string
  active: boolean
  createdAt: string
  updatedAt: string
}

export interface CreateEntityRequest {
  name: string
  type: EntityType
  description?: string
}

export interface UpdateEntityRequest extends Partial<CreateEntityRequest> {
  id: string
}

export interface EntityPageParams {
  page?: number
  size?: number
  nameKeyword?: string
  type?: EntityType
  activeOnly?: boolean
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}
```

#### 常量定义

```typescript
// shared/src/types/[domain]/index.ts
export * from './types'

export const DOMAIN_CONSTANTS = {
  EntityTypeDisplayNames: {
    [EntityType.EXAMPLE]: 'Example',
    [EntityType.ACTIVE]: 'Active',
    [EntityType.INACTIVE]: 'Inactive'
  },

  DefaultPageSize: 20,
  MaxPageSize: 100,

  ValidationRules: {
    NameMinLength: 2,
    NameMaxLength: 50,
    DescriptionMaxLength: 500
  }
}
```

### API 模块模板

```typescript
// shared/src/api/domains/[domain]/index.ts
import { request } from '../../core/request';
import type { Entity, CreateEntityRequest, UpdateEntityRequest, EntityPageParams } from '@I0/shared/types';

export class EntityAPI {
  async getEntities(params?: EntityPageParams) {
    return request.get('/entities', { params });
  }

  async getEntityById(id: string) {
    return request.get(`/entities/${id}`);
  }

  async createEntity(data: CreateEntityRequest) {
    return request.post('/entities', data);
  }

  async updateEntity(id: string, data: UpdateEntityRequest) {
    return request.put(`/entities/${id}`, data);
  }

  async deleteEntity(id: string) {
    return request.delete(`/entities/${id}`);
  }
}

export const entityAPI = new EntityAPI();
```

### Composable 模板

```typescript
// app/[portal]/src/pages/[feature]/composables/useFeatureManagement.ts
import { ref, computed, createSharedComposable } from '@vueuse/core';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useAsyncAction } from '@I0/shared/composables';
import { entityAPI } from '@I0/shared/api';
import type { Entity, CreateEntityRequest, EntityPageParams } from '@I0/shared/types';

export const useFeatureManagement = createSharedComposable(() => {
  const entities = ref<Entity[]>([]);
  const total = ref(0);
  const loading = ref(false);

  const searchForm = ref<EntityPageParams>({
    page: 0,
    size: 20,
    nameKeyword: '',
    activeOnly: undefined
  });

  const loadEntities = useAsyncAction(async (params?: EntityPageParams) => {
    const { data } = await entityAPI.getEntities(params || searchForm.value);
    entities.value = data.content;
    total.value = data.pagination.total;
  });

  const createEntity = useAsyncAction(async (data: CreateEntityRequest) => {
    await entityAPI.createEntity(data);
    ElMessage.success('Created successfully');
    await loadEntities();
  });

  const updateEntity = useAsyncAction(async (id: string, data: Partial<CreateEntityRequest>) => {
    await entityAPI.updateEntity(id, data);
    ElMessage.success('Updated successfully');
    await loadEntities();
  });

  const deleteEntity = useAsyncAction(async (id: string, name?: string) => {
    await ElMessageBox.confirm(
      `Are you sure you want to delete ${name ? `"${name}"` : 'this entity'}?`,
      'Confirm Delete',
      { type: 'warning' }
    );
    await entityAPI.deleteEntity(id);
    ElMessage.success('Deleted successfully');
    await loadEntities();
  });

  return {
    entities,
    total,
    loading,
    searchForm,
    loadEntities,
    createEntity,
    updateEntity,
    deleteEntity
  };
});
```

### 页面组件模板

```vue
<!-- app/[portal]/src/pages/[feature]/index.vue -->
<template>
  <div class="page-feature">
    <div class="operation-section">
      <el-form :model="searchForm" inline>
        <el-form-item>
          <el-input
            v-model="searchForm.nameKeyword"
            placeholder="Search..."
            clearable
            @keyup.enter="loadEntities"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadEntities" :loading="loading">
            Search
          </el-button>
        </el-form-item>
      </el-form>

      <el-button type="primary" @click="showCreateDialog">
        Create Entity
      </el-button>
    </div>

    <I0Table
      :table-data="entities"
      :table-column="tableColumns"
      :loading="loading"
      :pagination="pagination"
      @pagination-change="handlePageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { I0Table } from '@I0/shared/components';
import { useFeatureManagement } from './composables/useFeatureManagement';
import type { TableColumn } from '@I0/shared/components';

const {
  entities,
  total,
  loading,
  searchForm,
  loadEntities,
  createEntity,
  updateEntity,
  deleteEntity
} = useFeatureManagement();

const tableColumns = computed<TableColumn[]>(() => [
  { name: 'Name', prop: 'name', type: 'string', sortable: true },
  { name: 'Type', prop: 'type', type: 'string', width: 120 },
  { name: 'Description', prop: 'description', type: 'string', minWidth: 200 },
  { name: 'Created', prop: 'createdAt', type: 'dateTime', width: 180 },
  { name: 'Actions', prop: 'actions', type: 'string', width: 150, fixed: 'right' }
]);

const pagination = computed(() => ({
  currentPage: searchForm.value.page || 0,
  pageSize: searchForm.value.size || 20,
  total: total.value
}));

const handlePageChange = (event: any) => {
  searchForm.value.page = event.page;
  searchForm.value.size = event.size;
  loadEntities();
};

const showCreateDialog = () => {
  // Dialog implementation
};

// Initialize
loadEntities();
</script>

<style scoped lang="scss">
.page-feature {
  @include page-container;

  .operation-section {
    @include operation-section;
  }
}
</style>

<route lang="yaml">
name: FeatureManagement
meta:
  title: "Feature Management"
  layout: "main"
</route>
```

## 页面实现模板

### 标准页面组件模板

**增删改查列表参考页面**：`app/admin-portal/src/pages/clients/index.vue`

```vue
<!-- app/[portal]/src/pages/[feature]/index.vue -->
<template>
  <div class="page-[feature]">
    <!-- 操作区域 -->
    <div class="operation-section">
      <el-form :model="searchForm" inline>
        <!-- 搜索表单 -->
        <el-form-item>
          <el-button type="primary" round @click="handleSearch">搜索</el-button>
          <el-button round @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-button type="primary" round @click="openCreateDialog">
        创建[Feature]
      </el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <I0Table
        :table-data="tableData"
        :table-column="tableColumns"
        :loading="loading"
        :pagination="pagination"
        @sort-change="handleSortChange"
        @pagination-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { I0Table } from '@I0/shared/components';
import { use[Feature]Management } from './composables/use[Feature]Management';

const { tableData, loading, pagination, handleSearch, handleReset } = use[Feature]Management();

const tableColumns = computed(() => [
  // 表格列配置
]);
</script>

<style scoped lang="scss">
</style>

<route lang="yaml">
name: [Feature]Management
meta:
  title: "[Feature] Management"
  layout: "main"
</route>
```

### UI 一致性检查模板

```markdown
## UI 一致性检查清单

### 与参考页面对比 ([Reference Page])
- [ ] 按钮样式：所有按钮包含 round 属性
- [ ] 表单布局：使用 label-position="top"
- [ ] 样式块：使用空的 <style scoped lang="scss"></style>
- [ ] 页面结构：操作区域 + 表格区域
- [ ] 响应式：断点处理与参考页面一致

### 验证步骤
1. 打开参考页面 [Reference Page]
2. 逐项对比视觉表现
3. 验证交互行为一致性
4. 检查响应式布局
```

### API 实现模板

```typescript
// shared/src/api/domains/[domain]/index.ts
import request from '../../core/request';
import type { ApiResponse, PaginationData } from '@I0/shared/types';
import type { [Entity], Create[Entity]Request, Update[Entity]Request } from '@I0/shared/types';

class [Entity]API {
  private readonly basePath = '/[entities]'; // 简洁路径

  async get[Entities](params?: any) {
    return request.get<PaginationData<[Entity]>>(this.basePath, params);
  }

  async create[Entity](data: Create[Entity]Request) {
    return request.post<[Entity]>(this.basePath, data);
  }

  async update[Entity](id: string, data: Update[Entity]Request) {
    return request.put<[Entity]>(`${this.basePath}/${id}`, data);
  }
}

export const [entity]API = new [Entity]API();
```

## 最佳实践

### 开发顺序
```
Types → API → Composables → Components → Pages
```

### UI 一致性原则
1. **严格参考现有页面**：完全复制参考页面的样式和布局
2. **优先使用默认样式**：不添加不必要的自定义样式
3. **保持交互一致**：确保hover效果、动画等与参考页面相同
4. **响应式设计一致**：断点和布局调整与参考页面相同

### 检查清单
1. 参考页面分析
2. UI 一致性检查
3. 代码规范检查
4. 功能验证测试

### 性能优化
- 使用 `shallowRef` 处理大型数据列表
- 使用 `computed` 缓存计算属性
- 使用防抖处理用户输入

### 错误处理
- 友好的用户提示
- 区分不同类型的错误（网络错误、业务错误、验证错误）

## 相关文档

- [架构设计](./fe-architecture.md) - 系统架构说明
- [编码规范](./fe-standards.md) - 编码标准和规范
- [新手指南](./fe-getting-started.md) - 环境搭建和入门