<template>
  <el-dropdown 
    :trigger="trigger" 
    :placement="placement"
    :popper-class="popperClass"
    @visible-change="handleVisibleChange"
  >
    <span class="action-dropdown-trigger">
      <slot name="trigger">
        <el-icon :size="triggerSize"><MoreFilled /></el-icon>
      </slot>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <slot name="dropdown"></slot>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { MoreFilled } from '@element-plus/icons-vue';

interface Props {
  trigger?: 'hover' | 'click' | 'contextmenu';
  placement?: 'top' | 'top-start' | 'top-end' | 'bottom' | 'bottom-start' | 'bottom-end' | 'left' | 'left-start' | 'left-end' | 'right' | 'right-start' | 'right-end';
  popperClass?: string;
  triggerSize?: number;
}

const props = withDefaults(defineProps<Props>(), {
  trigger: 'hover',
  placement: 'bottom',
  popperClass: '',
  triggerSize: 16
});

interface Emits {
  (e: 'visible-change', visible: boolean): void;
}

const emit = defineEmits<Emits>();

const handleVisibleChange = (visible: boolean) => {
  emit('visible-change', visible);
};
</script>

<style scoped lang="scss">
.action-dropdown-trigger {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  cursor: pointer;
  border-radius: 5px;
  background: none;
  border: 1px solid transparent;
  outline: none;
  padding: 0;
  margin: 0;
  transition: all 0.3s ease;
  
  &:hover {
    background-color: #fff;
    border: 1px solid #d9d9d9;
  }
  
  &:focus {
    outline: none;
  }
}
</style>