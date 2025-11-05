<template>
  <div class="thumbnail-container">
    <!-- 实体图标 -->
    <div class="thumbnail-icon">
      <el-avatar :size="32" class="entity-avatar">
        <el-icon><School /></el-icon>
      </el-avatar>
    </div>

    <!-- 右侧信息 -->
    <div class="thumbnail-right">
      <div class="thumbnail-name" :title="data.name || '-'">
        {{ data.name || '-' }}
      </div>
      <div class="thumbnail-description" :title="data.code || data.type || '-'">
        {{ data.code || data.type || '-' }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { School } from '@element-plus/icons-vue';

interface Props {
  data: {
    name?: string;
    code?: string;
    type?: string;
    [key: string]: any;
  };
}

defineProps<Props>();
</script>

<style scoped lang="scss">
.thumbnail-container {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.thumbnail-icon {
  flex-shrink: 0;

  .entity-avatar {
    background: var(--el-color-primary);
    border: 2px solid #e9ecef;
  }
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
