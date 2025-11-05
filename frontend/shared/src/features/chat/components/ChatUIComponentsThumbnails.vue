<template>
  <!-- UI组件缩略图容器 -->
  <div v-if="components.length > 0" class="ui-components-thumbnails">
    <UIComponentThumbnail
      v-for="(component, index) in components"
      :key="index"
      :data="component"
      @click="handleThumbnailClick(component)"
    />
  </div>
</template>

<script setup lang="ts">
import type { UIComponentData } from '@I0/shared/types';
import UIComponentThumbnail from './ui-components/UIComponentThumbnail.vue';

interface Props {
  components: UIComponentData[];
}

defineProps<Props>();

interface Emits {
  (e: 'thumbnail-click', component: UIComponentData): void;
}

const emit = defineEmits<Emits>();

// 处理缩略图点击
const handleThumbnailClick = (component: UIComponentData) => {
  emit('thumbnail-click', component);
};
</script>

<style scoped lang="scss">
.ui-components-thumbnails {
  width: 100%;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}
</style>
