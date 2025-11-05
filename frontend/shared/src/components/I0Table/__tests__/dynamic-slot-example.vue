<template>
  <div class="dynamic-slot-example">
    <h2>I0Table 动态列槽功能示例</h2>

    <I0Table
      :table-data="tableData"
      :table-column="tableColumns"
      :loading="loading"
    >
      <!-- 状态列槽 - 使用自定义槽名 -->
      <template #status-slot="{ value, formattedValue }">
        <el-tag :type="getStatusType(value)">
          {{ formattedValue }}
        </el-tag>
      </template>

      <!-- 用户名列槽 - 使用自动生成的槽名 -->
      <template #column-userName="{ row, value }">
        <div class="user-cell">
          <el-avatar :src="row.avatar" :size="32" />
          <span class="user-name">{{ value }}</span>
        </div>
      </template>

      <!-- 金额列槽 - 使用自动生成的槽名 -->
      <template #column-amount="{ value, formattedValue }">
        <span class="amount" :class="{ 'amount-negative': value < 0 }">
          {{ formattedValue }}
        </span>
      </template>
    </I0Table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import I0Table from '../index.vue'
import type { TableColumn } from '../types'

// 示例数据
const tableData = ref([
  {
    id: 1,
    userName: '张三',
    email: 'zhangsan@example.com',
    status: 'active',
    amount: 1000.50,
    avatar: 'https://example.com/avatar1.jpg',
    lastLogin: '2024-01-15T10:30:00Z'
  },
  {
    id: 2,
    userName: '李四',
    email: 'lisi@example.com',
    status: 'inactive',
    amount: -200.00,
    avatar: 'https://example.com/avatar2.jpg',
    lastLogin: '2024-01-14T15:45:00Z'
  },
  {
    id: 3,
    userName: '王五',
    email: 'wangwu@example.com',
    status: 'pending',
    amount: 3500.75,
    avatar: 'https://example.com/avatar3.jpg',
    lastLogin: '2024-01-13T09:20:00Z'
  }
])

// 列配置 - 演示不同的槽配置方式
const tableColumns: TableColumn[] = [
  {
    name: '用户名',
    prop: 'userName',
    type: 'string',
    slot: true, // 自动生成槽名: column-userName
    width: '150px'
  },
  {
    name: '邮箱',
    prop: 'email',
    type: 'string',
    // 不配置槽，使用默认渲染
    width: '200px'
  },
  {
    name: '状态',
    prop: 'status',
    type: 'string',
    slot: 'status-slot', // 自定义槽名
    width: '100px',
    align: 'center'
  },
  {
    name: '金额',
    prop: 'amount',
    type: 'currency',
    slot: true, // 自动生成槽名: column-amount
    width: '120px',
    align: 'right'
  },
  {
    name: '最后登录',
    prop: 'lastLogin',
    type: 'dateTime',
    // 不配置槽，使用内置的dateTime格式化
    width: '180px'
  }
]

const loading = ref(false)

// 辅助函数
const getStatusType = (status: string) => {
  switch (status) {
    case 'active':
      return 'success'
    case 'inactive':
      return 'danger'
    case 'pending':
      return 'warning'
    default:
      return 'info'
  }
}
</script>

<style scoped lang="scss">
.dynamic-slot-example {
  padding: 20px;

  .user-cell {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .user-name {
    font-weight: 500;
  }

  .amount {
    font-weight: 600;

    &.amount-negative {
      color: #f56c6c;
    }
  }
}
</style>