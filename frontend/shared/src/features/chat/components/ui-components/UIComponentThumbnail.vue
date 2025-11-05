<template>
  <component
    v-if="thumbnailComponent"
    class="ui-component-thumbnail"
    :is="thumbnailComponent"
    :data="data.componentData.toolData"
  />
  <div v-else class="unknown-component">
    {{ `未知组件类型: ${data.componentType}` }}
  </div>
</template>

<script setup lang="ts">
import type { UIComponentData } from '@I0/shared/types';
import { computed } from 'vue';
import EmployeeInfoThumbnail from './EmployeeInfoThumbnail.vue';
import EntityInfoThumbnail from './EntityInfoThumbnail.vue';

interface Props {
  data: UIComponentData;
}

const props = defineProps<Props>();

// 根据组件类型返回对应的缩略图组件
const thumbnailComponent = computed(() => {
  switch (props.data.componentType) {
    case 'employee-info':
    case 'employee-detail':
      return EmployeeInfoThumbnail;
    case 'entity-info':
    case 'entity-detail':
      return EntityInfoThumbnail;
    default:
      return null;
  }
});
</script>

<style scoped lang="scss">
.ui-component-thumbnail {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border: 1px solid var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  width: 160px;

  &:hover {
    transform: translateY(-1px);
  }
}
</style>
