# 前端编码规范

## 代码风格规范

### Vue 组件规范

#### 组件格式
- 使用 `<script setup lang="ts">` 语法
- 使用 `<style scoped lang="scss">` 样式块
- 使用 `<route lang="yaml">` 路由配置块

```vue
<template>
  <div class="component-name">
    <!-- 模板内容 -->
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 组件逻辑
</script>

<style scoped lang="scss">
.component-name {
  // 组件样式
}
</style>

<route lang="yaml">
name: ComponentName
meta:
  title: '组件标题'
  layout: 'main'
</route>
```

#### 命名约定
- **组件文件**: 使用 PascalCase，如 `UserProfile.vue`
- **页面文件**: 使用 kebab-case，如 `user-management/index.vue`
- **组件名**: 使用 PascalCase，在模板中使用 kebab-case
- **Props**: 使用 camelCase，在模板中使用 kebab-case
- **事件**: 使用 kebab-case，如 `@user-updated`

### TypeScript 规范

#### 类型声明
- 明确的类型注解，避免隐式 any
- 使用 interface 定义对象类型
- 使用 type 定义联合类型或工具类型

```typescript
// 好的实践
interface User {
  id: string
  name: string
  email: string
}

type UserRole = 'admin' | 'user' | 'guest'

// 避免隐式 any
const users: User[] = []  // 明确类型
```

#### 导入规范
- **类型导入 (`type`)**: 只用于纯类型（接口、类型别名）
- **值导入**: 用于有运行时值的实体（枚举、类、函数）
- **混合导入**: 根据需要组合使用

```typescript
// ❌ 错误：将枚举作为类型导入
import type { Employee, EmployeeStatus, DataLocation } from '@I0/shared/types';

// ✅ 正确：枚举使用值导入，接口使用类型导入
import type { Employee, EmployeeStatus } from '@I0/shared/types';
import { DataLocation } from '@I0/shared/types';

// ✅ 混合导入示例
import type { User, CreateUserRequest } from '@I0/shared/types';
import { UserRole, userService } from '@I0/shared/types';
```

#### 导入分类说明
1. **纯类型导入** (`import type`):
   - 接口 (`interface`)
   - 类型别名 (`type`)
   - 泛型参数

2. **值导入**:
   - 枚举 (`enum`) - 编译时类型 + 运行时值
   - 类 (`class`)
   - 函数 (`function`)
   - 常量 (`const`)

3. **错误原因**:
   - 枚举被 `type` 导入后，运行时被完全移除
   - 访问 `Enum.VALUE` 时导致 `ReferenceError`

#### UI 一致性规范
- **严格参考现有页面实现**：新页面必须严格参考现有页面的样式和布局
- **优先使用框架默认样式**：不添加不必要的自定义样式
- **按钮样式统一**：所有按钮必须使用 `round` 属性
- **表单布局统一**：所有表单必须使用 `label-position="top"` 布局
- **样式块保持简洁**：页面组件使用空的 `<style scoped lang="scss"></style>` 块

```vue
<!-- ✅ 正确的实现 -->
<template>
  <el-button type="primary" round>提交</el-button>
  <el-form label-position="top">
    <!-- 表单内容 -->
  </el-form>
</template>

<style scoped lang="scss">
</style>
```

#### 响应式数据
- 使用 `ref` 处理基本类型和对象引用
- 使用 `reactive` 处理复杂对象
- 使用 `computed` 处理计算属性
- 使用 `shallowRef` 优化大型数据列表

```typescript
// 基本类型
const count = ref(0)

// 对象引用
const user = ref<User>()

// 复杂对象
const formData = reactive({
  username: '',
  email: '',
  profile: {}
})

// 计算属性
const fullName = computed(() => `${user.value?.firstName} ${user.value?.lastName}`)

// 大型数据优化
const largeList = shallowRef<Item[]>([])
```

### SCSS 样式规范

#### 样式组织
- 使用 BEM 命名约定
- 组件样式必须使用 `scoped`
- 优先使用 SCSS 变量和混入

```scss
// BEM 命名
.user-card {
  // Block
  &__header {
    // Element
  }

  &--active {
    // Modifier
  }

  // 状态
  &.is-loading {
    opacity: 0.6;
  }
}
```

#### 全局样式
- 全局样式只在 `shared/src/styles/` 中定义
- 使用 CSS 变量管理主题
- 避免在组件中定义全局样式

## 类型与常量管理规范

### 类型定义原则

**核心原则**: 所有类型和常量统一在 `@frontend/shared/src/types/` 中维护，按业务领域建文件夹划分

#### 类型文件组织结构

```
shared/src/types/
├── [domain]/           # 按业务领域划分的文件夹
│   ├── types.ts        # 类型定义文件
│   ├── index.ts        # 导出入口文件
│   └── constants.ts    # 常量定义文件（可选）
└── index.ts            # 全局类型导出
```

#### 文件命名规范

- **类型定义文件**: `types.ts` - 包含该领域的所有类型定义
- **导出入口文件**: `index.ts` - 统一导出该领域的类型和常量
- **常量定义文件**: `constants.ts` - 包含该领域的常量定义

#### 类型定义示例

```typescript
// shared/src/types/entity/types.ts
import type { PaginationParams } from '../request/types'

// 实体类型枚举
export enum EntityType {
  BIPO_ENTITY = 'BIPO_ENTITY',
  CLIENT_ENTITY = 'CLIENT_ENTITY',
  VENDOR_ENTITY = 'VENDOR_ENTITY'
}

// 实体信息接口
export interface Entity {
  id: string
  name: string
  entityType: EntityType
  active: boolean
  createdAt: string
  updatedAt: string
}

// 分页查询参数
export interface EntityPageParams extends PaginationParams {
  entityType?: EntityType
  nameKeyword?: string
  activeOnly?: boolean
}
```

#### 常量定义示例

```typescript
// shared/src/types/entity/constants.ts
import { EntityType } from './types'

// 实体类型显示名称映射
export const EntityTypeDisplayNames: Record<EntityType, string> = {
  [EntityType.BIPO_ENTITY]: 'BIPO',
  [EntityType.CLIENT_ENTITY]: 'Client',
  [EntityType.VENDOR_ENTITY]: 'Vendor'
}
```

#### 导出入口规范

```typescript
// shared/src/types/entity/index.ts
/**
 * 实体相关类型定义
 * 包含实体类型枚举、实体接口、请求响应类型等
 */
export * from './types'
export * from './constants'
```

### 类型引用原则

- **唯一来源**: Types 模块是类型和常量的唯一来源
- **按需导入**: 只导入需要的类型，避免全量导入
- **领域隔离**: 不同领域的类型定义相互独立

## 文件结构规范

### 目录组织
```
app/[portal]/src/pages/[feature]/
├── index.vue              # 页面主文件
├── composables/           # 页面级 composables
│   └── use[Feature].ts
└── components/           # 页面级组件
    ├── FeatureTable.vue
    └── FeatureForm.vue
```

### 文件命名
- **页面目录**: 使用 kebab-case，如 `user-management/`
- **Composable**: 使用 `use[Feature].ts` 格式
- **组件文件**: 使用 PascalCase.vue
- **类型文件和常量文件**: 使用 `types.ts`

### 共享库引用规范

**核心原则**: 四大门户应用引用共享库内容时，必须使用 `@I0/shared` 前缀，禁止使用相对路径和别名

#### 引用规则

- **统一前缀**: 所有共享库内容必须使用 `@I0/shared` 前缀
- **禁止相对路径**: 不能使用 `../../../../shared/` 等相对路径
- **禁止别名**: 不能使用 `@/shared` 或其他别名
- **yarn workspace**: 必须遵循 yarn workspace 的管理模式

#### 正确引用示例

```typescript
// 引用共享组件
import { I0Table } from '@I0/shared/components'
import { I0Form } from '@I0/shared/components'

// 引用类型定义
import type { Entity, EntityPageParams } from '@I0/shared/types'
import type { TableColumn } from '@I0/shared/components'

// 引用 API 客户端
import { entityAPI } from '@I0/shared/api'
import { userAPI } from '@I0/shared/api'

// 引用组合函数
import { useTablePagination } from '@I0/shared/composables'
import { useFormValidation } from '@I0/shared/composables'

// 引用业务功能
import { UserManagement } from '@I0/shared/features'
import { EntityForm } from '@I0/shared/features'
```

#### 错误引用示例

```typescript
// 错误：使用相对路径
import { I0Table } from '../../../../shared/components'
import type { Entity } from '../../../../../shared/types/entity'

// 错误：使用别名
import { I0Table } from '@/shared/components'
import type { Entity } from '@/types'

// 错误：直接引用具体文件
import { Entity } from '@I0/shared/types/entity/types'
```

#### 导入顺序规范

```typescript
// 1. Vue 和插件
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'

// 2. 第三方库
import { ElMessage } from 'element-plus'

// 3. 共享库模块
import type { Entity, EntityPageParams } from '@I0/shared/types'
import { I0Table } from '@I0/shared/components'
import { entityAPI } from '@I0/shared/api'
import { useTablePagination } from '@I0/shared/composables'

// 4. 本地模块
import { useUserManagement } from './composables/useUserManagement'
import UserForm from './components/UserForm.vue'
```

## 开发流程规范

### 开发顺序
```
Types 定义 → API 开发 → Composables 实现 → Features 开发 → 页面集成
```

### 代码检查流程
```bash
# 1. 类型检查
yarn fe:type-check

# 2. 代码检查
yarn fe:lint

# 3. 格式化
yarn fe:format

# 4. 运行测试
yarn fe:test
```

### Git 提交规范
```bash
# 提交格式
type(scope): description

# 类型说明
feat: 新功能
fix: 修复问题
docs: 文档更新
style: 代码格式化
refactor: 重构
test: 测试相关
chore: 构建或工具相关

# 示例
feat(user): add user management feature
fix(auth): resolve login validation issue
docs(readme): update installation guide
```

## 组件设计规范

### 组件职责
- **页面组件**: 只负责布局组合和路由配置
- **业务组件**: 负责特定业务领域的完整功能
- **通用组件**: 负责纯 UI 展示，无业务逻辑

### Props 设计
- Props 必须有明确的类型定义
- 为 Props 提供默认值
- 使用 TypeScript 接口定义复杂 Props

### 组件实现参考标准
- **严格参考现有页面**：如果在问答中明确给出了参考目录或文件，那新页面必须完全参考现有页面的实现模式
- **UI一致性检查**：确保按钮样式、表单布局、颜色方案完全一致
- **样式继承原则**：优先使用 Element Plus 默认样式，避免自定义样式
- **布局一致性**：确保页面结构和组件布局与参考页面相同

#### UI 一致性检查清单
- [ ] 按钮使用 round 属性
- [ ] 表单使用 label-position="top"
- [ ] 不包含自定义样式
- [ ] 页面结构与参考页面一致
- [ ] 交互模式与参考页面一致

```typescript
interface Props {
  title: string
  items: Item[]
  loading?: boolean
  onRefresh?: () => void
}

const props = withDefaults(defineProps<Props>(), {
  loading: false
})
```

### 事件设计
- 使用 kebab-case 事件名
- 事件参数应该是单个对象
- 提供清晰的事件类型定义

```typescript
interface Emits {
  (e: 'update', data: UpdateData): void
  (e: 'delete', id: string): void
}

const emit = defineEmits<Emits>()
```

### 插槽设计
- 为插槽提供明确的类型定义
- 使用具名插槽提高可读性
- 提供插槽默认内容

```vue
<template>
  <div class="table-component">
    <slot name="header" :data="tableData">
      <div class="default-header">默认标题</div>
    </slot>
  </div>
</template>

<script setup lang="ts">
interface TableData {
  items: any[]
  total: number
}

const tableData: TableData = {
  items: [],
  total: 0
}

defineSlots<{
  header?: (props: { data: TableData }) => any
}>()
</script>
```

## 性能规范

### 代码分割
- 页面组件自动分割 (通过路由配置)
- 大型组件使用动态导入
- 第三方库按需引入

```typescript
// 动态导入大型组件
const HeavyComponent = defineAsyncComponent(() =>
  import('./HeavyComponent.vue')
)

// 按需引入第三方库
import { debounce } from 'lodash-es'
```

### 状态管理优化
- 大数据列表使用 `shallowRef`
- 计算属性使用 `computed` 缓存
- 搜索功能使用防抖处理

```typescript
// 大数据优化
const largeList = shallowRef<Item[]>([])

// 防抖搜索
const debouncedSearch = useDebounceFn(search, 300)

// 计算属性缓存
const filteredItems = computed(() =>
  items.value.filter(item => item.active)
)
```

### 渲染优化
- 使用 `v-if` 替代 `v-show` 控制显示
- 使用 `key` 属性优化列表渲染
- 避免在模板中使用复杂表达式

## 测试规范

### 测试文件组织
- 组件测试文件与组件文件同级
- 使用 `.spec.ts` 或 `.test.ts` 后缀
- 测试文件放在 `__tests__/` 目录

### 测试覆盖率要求
- **共享库模块**: 90% 以上覆盖率
- **业务组件**: 80% 以上覆盖率
- **工具函数**: 100% 覆盖率
- **页面组件**: 按需测试核心功能

## API 定义规范

### API 设计原则

**核心原则**: 明确各种 HTTP 方法的定义规范，按实际业务需求定义接口，避免过度设计

### API 路径规范

- **简洁路径原则**：使用简洁的资源路径，不包含全局前缀
- **与业务实体一致**：路径名称与业务实体名称保持一致
- **统一格式**：所有 API 类使用相同的路径格式

```typescript
// ✅ 正确的路径格式
class ClientAPI {
  private readonly basePath = '/clients';  // 简洁路径
}
```

### HTTP 方法规范

#### GET 方法 - 查询操作

```typescript
// 查询列表
async getEntities(params: EntityPageParams = {}) {
  return request.get<PaginationData<Entity>>(this.basePath, params);
}

// 根据ID查询单个对象
async getEntityById(id: string): Promise<ApiResponse<Entity>> {
  return request.get<Entity>(`${this.basePath}/${id}`);
}

// 查询激活状态的对象
async getActiveEntities(entityType?: EntityType): Promise<ApiResponse<Entity[]>> {
  const url = entityType
    ? `${this.basePath}/active?entityType=${entityType}`
    : `${this.basePath}/active`;
  return request.get<Entity[]>(url);
}
```

#### POST 方法 - 创建操作

```typescript
// 创建新对象
async createEntity(data: CreateEntityRequest): Promise<ApiResponse<Entity>> {
  return request.post<Entity>(`${this.basePath}`, data);
}
```

#### PUT 方法 - 更新操作

```typescript
// 更新对象
async updateEntity(id: string, data: UpdateEntityRequest): Promise<ApiResponse<Entity>> {
  return request.put<Entity>(`${this.basePath}/${id}`, data);
}

// 激活对象
async activateEntity(id: string): Promise<ApiResponse<Entity>> {
  return request.put<Entity>(`${this.basePath}/${id}/activate`);
}

// 停用对象
async deactivateEntity(id: string): Promise<ApiResponse<Entity>> {
  return request.put<Entity>(`${this.basePath}/${id}/deactivate`);
}
```

#### DELETE 方法 - 删除操作

```typescript
// 删除对象
async deleteEntity(id: string): Promise<ApiResponse<void>> {
  return request.delete<void>(`${this.basePath}/${id}`);
}
```

### API 响应类型规范

```typescript
// 统一使用 ApiResponse 包装响应类型
export type EntityResponse = ApiResponse<Entity>
export type EntityListResponse = ApiResponse<Entity[]>

// 分页响应类型
export interface PaginationData<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  empty: boolean
}
```

### API 类组织规范

```typescript
// API 类定义
class EntityAPI {
  private readonly basePath = '/entities';

  // GET 方法
  async getEntities(params: EntityPageParams = {}) {
    return request.get<PaginationData<Entity>>(this.basePath, params);
  }

  // POST 方法
  async createEntity(data: CreateEntityRequest): Promise<ApiResponse<Entity>> {
    return request.post<Entity>(`${this.basePath}`, data);
  }

  // PUT 方法
  async updateEntity(id: string, data: UpdateEntityRequest): Promise<ApiResponse<Entity>> {
    return request.put<Entity>(`${this.basePath}/${id}`, data);
  }

  // DELETE 方法
  async deleteEntity(id: string): Promise<ApiResponse<void>> {
    return request.delete<void>(`${this.basePath}/${id}`);
  }
}

// 导出 API 实例
export const entityAPI = new EntityAPI();
```

### 接口定义原则

- **按需定义**: 只定义实际业务需要的接口，避免过度设计
- **RESTful 风格**: 遵循 RESTful API 设计原则
- **类型安全**: 所有接口必须有明确的类型定义
- **统一响应**: 使用 `ApiResponse<T>` 统一包装响应类型

## 安全规范

### 数据验证
- 所有用户输入必须验证
- 使用 TypeScript 类型检查
- 实施运行时类型检查

### XSS 防护
- 使用 `v-text` 替代 `{{ }}` 显示用户内容
- 避免使用 `v-html` 处理用户内容
- 使用 DOMPurify 清理 HTML 内容

### API 安全
- 敏感信息不在前端存储
- 使用 HTTPS 传输数据
- 实施适当的 CORS 策略

## 相关文档

- [架构设计](./fe-architecture.md) - 系统架构说明
- [模板库](./fe-templates.md) - 代码模板和最佳实践
- [新手指南](./fe-getting-started.md) - 环境搭建和入门
- [测试指南](./fe-testing-standard.md) - 测试相关指导