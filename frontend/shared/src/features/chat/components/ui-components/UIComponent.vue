<template>
  <el-card class="ui-component-renderer" shadow="never">
    <!-- 组件标题栏 -->
    <template #header>
      <div class="component-header">
        <h3 class="component-title">{{ getComponentTitle() }}</h3>
        <el-button type="info" :icon="Close" circle size="small" @click="handleClose" />
      </div>
    </template>

    <!-- 根据组件类型动态渲染 -->
    <div class="component-content">
      <component
        v-if="currentComponent"
        :is="currentComponent"
        :key="componentData.componentData?.toolName"
        :data="componentData.componentData?.toolData ?? {}"
      />

      <!-- 未知组件类型的降级显示 -->
      <div v-else class="unknown-component">
        {{ `未知组件类型: ${componentType}` }}
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import type { UIComponentData } from '@I0/shared/types';
import { computed } from 'vue';
import { Close } from '@element-plus/icons-vue';
import { EmployeeInfoCard, EntityInfoCard, EntityDetailCard, EmployeeDetailCard } from '.';

interface Props {
  componentData: UIComponentData;
}

const props = defineProps<Props>();

interface Emits {
  (e: 'close'): void;
}

const emit = defineEmits<Emits>();

// 计算组件类型
const componentType = computed(() => props.componentData.componentType);

// 根据组件类型返回对应的组件
const currentComponent = computed(() => {
  switch (componentType.value) {
    case 'employee-info':
      return EmployeeInfoCard;
    case 'entity-info':
      return EntityInfoCard;
    case 'entity-detail':
      return EntityDetailCard;
    case 'employee-detail':
      return EmployeeDetailCard;
    default:
      return null;
  }
});

// 获取组件标题
const getComponentTitle = () => {
  switch (componentType.value) {
    case 'employee-info':
      return '员工信息';
    case 'entity-info':
      return '实体信息';
    case 'entity-detail':
      return '实体详情';
    case 'employee-detail':
      return '员工详情';
    default:
      return '未知组件';
  }
};

// 处理关闭事件
const handleClose = () => {
  emit('close');
};
</script>

<style scoped lang="scss">
.ui-component-renderer {
  height: 100%;
  border:none;

  :deep(.el-card__header) {
    border-bottom: none;
  }
  :deep(.el-card__body) {
    padding-top: 0;
  }

  // 设置 el-descriptions__cell rowspan="1" 的宽度为 50%
  :deep(.el-descriptions__cell[rowspan='1']) {
    width: 50% !important;
  }

  .component-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .component-title {
      margin: 0;
      font-size: 14px;
      font-weight: 600;
      color: #2c3e50;
    }
  }
}

.unknown-component {
  color: #666;
  font-size: 14px;
  text-align: center;
  padding: 20px;
}
</style>
