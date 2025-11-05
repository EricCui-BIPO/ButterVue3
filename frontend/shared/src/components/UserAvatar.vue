<template>
  <div class="user-avatar">
    <div class="user-info">
      <div class="avatar-container">
        <img v-if="url" :src="url" :alt="name" class="avatar-image" />
        <div v-else class="avatar-placeholder">
          {{ avatarPlaceholder }}
        </div>
      </div>
      <span v-if="name && !hideName" class="username">{{ name }}</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

interface Props {
  url?: string;
  name?: string;
  size?: number;
  hideName?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  url: '',
  name: '',
  size: 28,
  hideName: false
});

// 计算头像占位符文本
const avatarPlaceholder = computed(() => {
  if (!props.name) return '?';

  // 获取第一个字符作为占位符
  const firstChar = props.name.charAt(0).toUpperCase();
  return firstChar || '?';
});
</script>

<style scoped lang="scss">
.user-avatar {
  .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    border-radius: 6px;
  }

  .avatar-container {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .avatar-image {
    width: v-bind('props.size + "px"');
    height: v-bind('props.size + "px"');
    border-radius: 50%;
    object-fit: cover;
  }

  .avatar-placeholder {
    width: v-bind('props.size + "px"');
    height: v-bind('props.size + "px"');
    border-radius: 50%;
    background: var(--el-color-primary);
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-weight: 600;
    font-size: calc(v-bind('props.size + "px"') / 2.3);
  }

  .username {
    font-size: 14px;
    font-weight: 500;
    color: var(--el-text-color-primary);
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
