<template>
  <div class="quick-actions-container" :style="containerStyle">
    <el-card
      v-for="(item, index) in items"
      :key="item.key ?? index"
      shadow="never"
      :class="[`${item.type}-plain-card`]"
      class="quick-card"
    >
      <h3>{{ item.title }}</h3>
      <p>{{ item.description }}</p>
      <div class="action-buttons">
        <el-button type="primary" link>
          {{ item.processing ?? 0 }} Processing
        </el-button>
        <el-button :type="buttonType(item.type)" plain @click="handleAction(item, index)">
          {{ item.actionLabel }}
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

type ButtonType = 'primary' | 'success' | 'warning' | 'danger' | 'info'

export interface QuickActionItem {
  key?: string | number
  type: ButtonType
  title: string
  description: string
  processing?: number
  actionLabel: string
}

const props = defineProps<{
  items: QuickActionItem[]
  columns?: number // 可选：手动设置列数（不传则按 items.length）
  gap?: number // 每项之间的间距，单位 px，默认 10
}>()

const emit = defineEmits<{
  (e: 'action', payload: { item: QuickActionItem; index: number }): void
}>()

const containerStyle = computed(() => {
  const count = props.columns ?? (props.items?.length || 1)
  const gapPx = (props.gap ?? 10) + 'px'
  return {
    display: 'grid',
    gridTemplateColumns: `repeat(${count}, 1fr)`,
    gap: gapPx,
  } as Record<string, string>
})

function buttonType(type: ButtonType): ButtonType {
  return type || 'primary'
}

function handleAction(item: QuickActionItem, index: number) {
  emit('action', { item, index })
}
</script>

<style scoped lang="scss">
.quick-actions-container {
  // 使用 CSS Grid，根据列数动态布局（由 inline style 控制列数与间距）
}

.quick-card {
  width: 100%;
}

h3 {
  font-size: 16px;
  margin: 0 0 10px 0;
}

p {
  min-height: 70px;
  font-size: 12px;
  margin: 0 0 10px 0;
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
  word-break: break-word;

  .el-button {
    margin-left: 0;
  }
}
</style>