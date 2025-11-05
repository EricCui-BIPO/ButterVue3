# ActionDropdown 组件文档

## 概述

ActionDropdown 是一个简单的下拉菜单触发器组件，基于 Element Plus 封装，为表格行操作等场景提供统一的操作菜单入口。

### 解决什么问题

- 为表格行操作、卡片操作等场景提供统一的下拉菜单触发器
- 简化 Element Plus Dropdown 的使用，提供默认的触发器样式
- 支持自定义触发器内容和触发方式

### 核心特性

- 🔧 **默认触发器** - 内置三点图标的默认触发器
- 🎨 **可自定义** - 支持通过插槽自定义触发器内容
- 📍 **灵活配置** - 支持多种触发方式和位置配置
- 📱 **响应式** - 适配不同屏幕尺寸

## 基础使用

### 基础导入

```typescript
import { ActionDropdown } from '@I0/shared/components'
```

### 基础用法

```vue
<template>
  <ActionDropdown>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item @click="handleEdit">编辑</el-dropdown-item>
        <el-dropdown-item @click="handleDelete">删除</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </ActionDropdown>
</template>

<script setup lang="ts">
const handleEdit = () => {
  console.log('编辑操作')
}

const handleDelete = () => {
  console.log('删除操作')
}
</script>
```

### 自定义触发器

```vue
<template>
  <ActionDropdown>
    <template #trigger>
      <el-button type="primary" size="small">
        更多操作
        <el-icon><ArrowDown /></el-icon>
      </el-button>
    </template>

    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item>选项1</el-dropdown-item>
        <el-dropdown-item>选项2</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </ActionDropdown>
</template>
```

## Props

| 属性名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `trigger` | `'hover' \| 'click' \| 'contextmenu'` | `'hover'` | 触发方式 |
| `placement` | Element Plus 位置值 | `'bottom'` | 弹出位置 |
| `popperClass` | `string` | `''` | 弹出层自定义类名 |
| `triggerSize` | `number` | `16` | 触发器图标尺寸 |

## 事件

| 事件名 | 参数 | 说明 |
|--------|------|------|
| `visible-change` | `visible: boolean` | 下拉菜单可见性变化时 |

## 插槽

| 插槽名 | 说明 |
|--------|------|
| `trigger` | 自定义触发器内容 |
| `dropdown` | 下拉菜单内容 |

## 组件类型

```typescript
// 主要类型
export type { ActionDropdownProps } from '@I0/shared/components'
```

## 版本信息

- **当前版本**: 1.0.0
- **兼容性**: Vue 3.3+, TypeScript 5.3+, Element Plus 2.11+