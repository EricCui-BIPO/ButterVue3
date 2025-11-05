<template>
  <div class="thumbnail-container">
    <!-- 头像 -->
    <div class="thumbnail-icon">
      <UserAvatar :url="data.avatar" :name="data.name || ''" :size="32" :hide-name="true" />
    </div>

    <!-- 右侧信息 -->
    <div class="thumbnail-right">
      <div class="thumbnail-name" :title="data.name || '-'">
        {{ data.name || '-' }}
      </div>
      <div class="thumbnail-description" :title="data.employeeNumber || '-'">
        {{ data.employeeNumber || '-' }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { UserAvatar } from '@I0/shared/components';

interface Props {
  data: {
    name?: string;
    employeeNumber?: string;
    avatar?: string;
    [key: string]: any;
  };
}

defineProps<Props>();
</script>

<style scoped lang="scss">
.thumbnail-container {
  display: flex;
  align-items: center;
  gap: 4px;
  width: 100%;
}

.thumbnail-icon {
  flex-shrink: 0;
}

.thumbnail-right {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;

  .thumbnail-name {
    font-size: 12px;
    font-weight: 500;
    color: #333;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 1.2;
  }

  .thumbnail-description {
    font-size: 10px;
    color: #666;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    line-height: 1.2;
  }
}
</style>
