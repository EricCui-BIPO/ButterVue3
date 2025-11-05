<template>
  <!-- 右侧UI组件区域 - 支持多个组件 -->
  <transition name="panel-slide">
    <div v-if="list.length > 0" class="ui-components-panel">
      <transition-group name="ui-component" tag="div" class="ui-components-list">
        <div
          v-for="(componentData, index) in list"
          :key="componentData.id"
          class="ui-components-container"
          :style="{ 'z-index': 10 + index }"
        >
          <UIComponent :componentData="componentData" @close="$emit('close', index)" />
        </div>
      </transition-group>
    </div>
  </transition>
</template>

<script setup lang="ts">
import type { UIComponentData } from '@I0/shared/types';
import UIComponent from './ui-components/UIComponent.vue';

// Props
interface Props {
  list: UIComponentData[];
}

defineProps<Props>();

// Emits
interface Emits {
  (e: 'close', index: number): void;
}

defineEmits<Emits>();
</script>

<style scoped lang="scss">
// UI组件面板 - 支持多个组件
.ui-components-panel {
  flex: 1;
  height: 100%;
  overflow-y: auto;
  overflow-x: hidden;
}

// 面板整体滑入滑出动画
.panel-slide-enter-active,
.panel-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.panel-slide-enter-from {
  width: 0;
  opacity: 0;
  transform: translateX(100%);
}

.panel-slide-leave-to {
  width: 0;
  opacity: 0;
  transform: translateX(100%);
}

.ui-components-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

// 多个UI组件的动画效果
.ui-component-enter-active {
  transition: all 0.3s ease;
}

.ui-component-leave-active {
  transition: all 0.3s ease;
  position: absolute;
  width: 100%;
}

.ui-component-move {
  transition: all 0.3s ease;
}

.ui-component-enter-from {
  transform: translateX(100%) scale(0.3);
  opacity: 0;
}

.ui-component-leave-to {
  transform: translateX(100%) scale(0.3);
  opacity: 0;
}
</style>
