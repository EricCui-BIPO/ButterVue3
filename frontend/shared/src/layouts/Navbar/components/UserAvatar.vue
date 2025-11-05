<template>
  <UserAvatar
    :url="userStore.avatarUrl"
    :name="userStore.displayName"
    :size="28"
  />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { UserAvatar } from '@I0/shared/components'
import { useUserStore } from '@I0/shared/stores/user'

const userStore = useUserStore()

// 组件挂载时获取用户信息
onMounted(() => {
  userStore.fetchUserInfo()
})
</script>