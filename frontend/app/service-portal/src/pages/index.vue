<template>
  <div class="home">
    <!-- 重定向中... -->
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 页面挂载后立即重定向到人才管理页面
onMounted(() => {
  router.replace('/talent')
})
</script>

<style scoped>
.home {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
</style>

<route lang="yaml">
name: "Index"
meta:
  title: ""
</route>