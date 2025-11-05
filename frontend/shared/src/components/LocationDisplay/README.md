# LocationDisplay 组件文档

## 概述

LocationDisplay 是一个简单的地理位置展示组件，用于显示国家旗帜和地理位置名称，主要在表格、用户资料等场景中使用。

### 解决什么问题

- 统一地理位置信息的展示格式
- 自动处理国家旗帜的显示和错误处理
- 为地理位置数据提供一致的视觉效果

### 核心特性

- 🏳️ **国家旗帜** - 自动根据 ISO 代码显示对应国家旗帜
- 🌍 **位置名称** - 显示地理位置名称
- 🎯 **错误处理** - 内置旗帜加载失败处理
- 🔧 **灵活配置** - 支持隐藏旗帜和自定义回退文本

## 基础使用

### 基础导入

```typescript
import { LocationDisplay } from '@I0/shared/components'
```

### 基础用法

```vue
<template>
  <LocationDisplay
    :location="location"
    :show-flag="true"
    fallback-text="位置未知"
  />
</template>

<script setup lang="ts">
const location = ref({
  isoCode: 'US',
  name: 'United States'
})
</script>
```

### 表格中使用

```vue
<template>
  <el-table :data="tableData">
    <el-table-column prop="name" label="姓名" />
    <el-table-column label="位置" width="200">
      <template #default="{ row }">
        <LocationDisplay
          :location="row.location"
          fallback-text="未设置"
        />
      </template>
    </el-table-column>
  </el-table>
</template>
```

## Props

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `location` | `Location` | - | 位置信息对象 |
| `showFlag` | `boolean` | `true` | 是否显示国家旗帜 |
| `fallbackText` | `string` | `'-'` | 无位置信息时的回退文本 |

### Location 接口

```typescript
interface Location {
  isoCode?: string  // ISO 国家代码
  name?: string    // 位置名称
  [key: string]: any
}
```

## 组件类型

```typescript
// 主要类型
export type {
  LocationDisplayProps,
  Location
} from '@I0/shared/components'
```

## 版本信息

- **当前版本**: 1.0.0
- **兼容性**: Vue 3.3+, TypeScript 5.3+
- **依赖**: @I0/shared/utils/country-flag