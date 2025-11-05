# I0Table 组件文档

## 概述

I0Table 是基于 Vue 3 和 Element Plus 的高级表格组件，提供数据展示、分页、排序和自定义格式化功能。组件设计简洁易用，通过列配置自动渲染表格。

### 核心特性

- 🔧 **零配置开箱即用** - 基于 `tableColumn` 配置自动渲染
- 📊 **智能数据格式化** - 内置 string、number、date、dateTime、boolean、currency 类型
- 📄 **内置分页系统** - 完整的分页控制和状态管理
- 🎯 **灵活插槽系统** - 支持列内容和操作按钮自定义
- 🔄 **事件驱动** - 完整的排序、分页、行点击事件支持

## 快速开始

### 基础导入

```typescript
// 组件导入
import { I0Table } from '@I0/shared/components'

// 类型导入（推荐）
import type {
  I0TableProps,
  TableColumn,
  PaginationConfig,
  SortChangeEvent,
  RowClickEvent
} from '@I0/shared/components'
```

### 基础使用

```vue
<template>
  <I0Table
    :table-data="tableData"
    :table-column="tableColumn"
    :loading="loading"
    :pagination="pagination"
    @sort-change="handleSortChange"
    @pagination-change="handlePaginationChange"
    @row-click="handleRowClick"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { TableColumn, PaginationConfig } from '@I0/shared/components'

const tableData = ref([])
const loading = ref(false)
const pagination = ref<PaginationConfig>({
  total: 100,
  currentPage: 1,
  pageSize: 20
})

const tableColumn: TableColumn[] = [
  { name: '姓名', prop: 'name', type: 'string' },
  { name: '年龄', prop: 'age', type: 'number', align: 'center' }
]

// 事件处理
const handleSortChange = (event: SortChangeEvent) => {
  console.log('排序:', event)
}

const handlePaginationChange = (event) => {
  console.log('分页:', event.page, event.pageSize)
}

const handleRowClick = (event: RowClickEvent) => {
  console.log('行点击:', event.row.name)
}
</script>
```

## Props 参考

### I0TableProps 接口

直接引用组件 types.ts 中的接口定义：

```typescript
interface I0TableProps {
  /** 表格数据 - 要显示的对象数组 */
  tableData: Record<string, any>[]

  /** 列配置 - 定义表格结构 */
  tableColumn: TableColumn[]

  /** 加载状态 */
  loading?: boolean

  /** 分页配置 */
  pagination?: PaginationConfig

  /** 表格样式选项 */
  stripe?: boolean
  border?: boolean
  size?: 'small' | 'default' | 'large'
  highlightCurrentRow?: boolean
  emptyText?: string
}
```

### 默认值

```typescript
const defaultProps = {
  loading: false,
  stripe: false,
  border: false,
  size: 'small',
  highlightCurrentRow: true,
  emptyText: 'No data available'
}
```

## 列配置

### TableColumn 接口

```typescript
interface TableColumn {
  /** 列显示名称 */
  name: string

  /** 数据字段映射 */
  prop: string

  /** 数据类型用于格式化 */
  type: 'string' | 'number' | 'date' | 'dateTime' | 'boolean' | 'currency'

  /** 自定义格式化函数 */
  formatter?: (value: any, row: Record<string, any>, column: TableColumn) => string

  /** 列宽度 */
  width?: string | number

  /** 最小列宽度 */
  minWidth?: string | number

  /** 固定位置 */
  fixed?: 'left' | 'right'

  /** 可排序配置 */
  sortable?: boolean

  /** 列对齐 */
  align?: 'left' | 'center' | 'right'

  /** 显示溢出工具提示 */
  showOverflowTooltip?: boolean

  /** 自定义类名 */
  className?: string

  /** 动态列槽配置 */
  slot?: string | boolean
}
```

### 数据类型格式化

#### 内置格式化器

1. **string** - 字符串类型
   ```typescript
   { name: '姓名', prop: 'name', type: 'string' }
   ```

2. **number** - 数字类型（本地化格式）
   ```typescript
   { name: '数量', prop: 'quantity', type: 'number', align: 'right' }
   ```

3. **date** - 日期类型
   ```typescript
   { name: '出生日期', prop: 'birthDate', type: 'date' }
   ```

4. **dateTime** - 日期时间类型
   ```typescript
   { name: '创建时间', prop: 'createdAt', type: 'dateTime' }
   ```

5. **boolean** - 布尔类型（✓/✗ 符号）
   ```typescript
   { name: '激活状态', prop: 'isActive', type: 'boolean', align: 'center' }
   ```

6. **currency** - 货币类型
   ```typescript
   { name: '价格', prop: 'price', type: 'currency', align: 'right' }
   ```

#### 自定义格式化

```typescript
const tableColumn: TableColumn[] = [
  {
    name: '状态',
    prop: 'status',
    type: 'string',
    formatter: (value) => {
      const statusMap = {
        'pending': '待处理',
        'processing': '处理中',
        'completed': '已完成'
      }
      return statusMap[value] || value
    }
  }
]
```
- 建议抽象为方法，以便复用


## 分页系统

### PaginationConfig 接口

```typescript
interface PaginationConfig {
  /** 项目总数 */
  total: number

  /** 当前页码（1-based，默认为 1） */
  currentPage?: number

  /** 当前每页大小（默认为 20） */
  pageSize?: number

  /** 页面大小选项 */
  pageSizes?: number[]

  /** 布局配置 */
  layout?: string

  /** 是否显示小分页 */
  small?: boolean
}
```

### 分页使用

```typescript
const pagination = ref<PaginationConfig>({
  total: 100,
  currentPage: 1,
  pageSize: 20,
  pageSizes: [10, 20, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper',
  small: true
})

// 分页事件处理
const handlePaginationChange = (event) => {
  const { page, pageSize } = event
  // 重新加载数据
  loadData(page, pageSize)
}
```

## 事件系统

### 事件列表

| 事件名 | 参数类型 | 触发时机 |
|--------|----------|----------|
| `sort-change` | `SortChangeEvent` | 排序变更时 |
| `pagination-change` | `PaginationChangeEvent` | 分页变更时 |
| `row-click` | `RowClickEvent` | 行点击时 |
| `cell-click` | `CellClickEvent` | 单元格点击时 |
| `refresh` | - | 刷新时 |
| `error` | `Error` | 错误时 |

### 事件参数接口

```typescript
// 排序变更事件
interface SortChangeEvent {
  column: TableColumn
  order: 'ascending' | 'descending' | null
  prop: string
}

// 分页变更事件
interface PaginationChangeEvent {
  page: number
  pageSize: number
}

// 行点击事件
interface RowClickEvent {
  row: Record<string, any>
  index: number
  event: Event
}

// 单元格点击事件
interface CellClickEvent {
  row: Record<string, any>
  column: TableColumn
  value: any
}
```

## 插槽系统

### 可用插槽

| 插槽名 | 作用域参数 | 说明 |
|--------|------------|------|
| `default` | - | 表格顶部内容 |
| `empty` | - | 空状态自定义内容 |
| `actions` | `{ row, column, index }` | 操作列插槽 |
| `column-{prop}` | `{ row, column, value, formattedValue, index }` | 动态列插槽 |

### 插槽使用示例

#### 1. 操作列插槽

```vue
<template>
  <I0Table :table-data="tableData" :table-column="tableColumn">
    <template #actions="{ row }">
      <el-button size="small" type="primary" @click="handleEdit(row)">
        编辑
      </el-button>
      <el-button size="small" type="danger" @click="handleDelete(row)">
        删除
      </el-button>
    </template>
  </I0Table>
</template>

<script setup lang="ts">
const tableColumn = [
  // 需要有一列 prop 为 'actions'
  { name: '操作', prop: 'actions', type: 'string', width: 160 }
]
</script>
```

#### 2. 动态列插槽

```vue
<template>
  <I0Table :table-data="tableData" :table-column="tableColumn">
    <!-- 自动生成的插槽：column-{prop} -->
    <template #column-status="{ row, value }">
      <el-tag :type="value === 'active' ? 'success' : 'danger'">
        {{ value === 'active' ? '激活' : '禁用' }}
      </el-tag>
    </template>

    <!-- 使用 slot 配置指定插槽名 -->
    <template #user-avatar="{ row, value }">
      <el-avatar :src="value">{{ row.name?.charAt(0) }}</el-avatar>
    </template>
  </I0Table>
</template>

<script setup lang="ts">
const tableColumn = [
  { name: '状态', prop: 'status', type: 'string', slot: true }, // 自动生成 column-status
  { name: '头像', prop: 'avatar', type: 'string', slot: 'user-avatar' } // 指定插槽名
]
</script>
```

## 公共方法

### 实例方法

通过 ref 调用组件暴露的方法：

```vue
<template>
  <I0Table ref="tableRef" :table-data="tableData" :table-column="tableColumn" />

  <el-button @click="refreshTable">刷新</el-button>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { I0TableInstance } from '@I0/shared/components'

const tableRef = ref<I0TableInstance>()

const refreshTable = () => {
  tableRef.value?.refresh()
}

const getTableState = () => {
  const sortState = tableRef.value?.getSortState()
  const paginationState = tableRef.value?.getPaginationState()
  console.log('排序状态:', sortState)
  console.log('分页状态:', paginationState)
}
</script>
```

### 方法列表

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| `refresh()` | - | `void` | 刷新表格数据 |
| `getSortState()` | - | `{ prop: string; order: string \| null }` | 获取当前排序状态 |
| `getPaginationState()` | - | `PaginationConfig` | 获取当前分页状态 |
| `setLoading(loading)` | `boolean` | `void` | 设置加载状态 |

## 实际案例

### 来自 Entity 页面的使用示例

```vue
<template>
  <I0Table
    :table-data="tableData"
    :table-column="tableColumn"
    :loading="loading"
    :pagination="pagination"
    @sort-change="handleSortChange"
    @pagination-change="handlePageChange"
    @row-click="handleRowClick"
  >
    <!-- 实体名称列插槽 -->
    <template #column-name="{ row }">
      <div class="entity-name">
        <span class="name">{{ row.name }}</span>
        <el-tag v-if="!row.active" type="info" size="small">已停用</el-tag>
      </div>
    </template>

    <!-- 实体类型列插槽 -->
    <template #column-entityType="{ row }">
      <el-tag :type="getEntityTypeTagType(row.entityType)">
        {{ getEntityTypeDisplayName(row.entityType) }}
      </el-tag>
    </template>

    <!-- 操作列插槽 -->
    <template #actions="{ row }">
      <el-button type="primary" link @click="handleEdit(row)">
        编辑
      </el-button>
      <el-button v-if="row.active" type="warning" link @click="handleDeactivate(row)">
        停用
      </el-button>
      <el-button v-else type="success" link @click="handleActivate(row)">
        激活
      </el-button>
      <el-button type="danger" link @click="handleDelete(row)">
        删除
      </el-button>
    </template>
  </I0Table>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { TableColumn, SortChangeEvent } from '@I0/shared/components'

// 实际列配置（来自 entity 页面）
const tableColumn = computed<TableColumn[]>(() => [
  {
    name: '实体名称',
    prop: 'name',
    type: 'string',
    sortable: true,
    minWidth: 150,
    slot: true // 启用插槽
  },
  {
    name: '实体类型',
    prop: 'entityType',
    type: 'string',
    width: 120,
    slot: true // 启用插槽
  },
  {
    name: '描述',
    prop: 'description',
    type: 'string',
    minWidth: 200,
    showOverflowTooltip: true
  },
  {
    name: '创建时间',
    prop: 'createdAt',
    type: 'dateTime',
    width: 180,
    sortable: true
  },
  {
    name: '操作',
    prop: 'actions',
    type: 'string',
    width: 200,
    fixed: 'right'
  }
])

// 实际事件处理（来自 entity 页面）
const handleSortChange = (event: SortChangeEvent) => {
  if (event.order) {
    // 更新搜索参数中的排序字段
    searchEntities({
      ...searchForm,
      sortBy: event.prop,
      sortOrder: event.order === 'ascending' ? 'asc' : 'desc'
    })
  } else {
    // 默认按创建时间排序
    searchEntities({
      ...searchForm,
      sortBy: 'createdAt',
      sortOrder: 'desc'
    })
  }
}

const handlePageChange = (event: any) => {
  changePage(event.page, event.pageSize)
}

const handleRowClick = (event: any) => {
  console.log('行点击:', event.row)
}
</script>
```

## 组件类型

### 完整类型导出

```typescript
// 主要类型
export type {
  I0TableProps,
  TableColumn,
  PaginationConfig,
  SortChangeEvent,
  PaginationChangeEvent,
  RowClickEvent,
  CellClickEvent,
  I0TableInstance
} from '@I0/shared/components'

// 组件实例类型
interface I0TableInstance {
  refresh: () => void
  getSortState: () => { prop: string; order: string | null }
  getPaginationState: () => PaginationConfig
  setLoading: (loading: boolean) => void
}
```

## 版本信息

- **当前版本**: 1.0.0
- **兼容性**: Vue 3.3+, TypeScript 5.3+, Element Plus 2.11+