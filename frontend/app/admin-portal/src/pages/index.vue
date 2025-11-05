<template>
  <div class="home">
    <h1>管理门户首页</h1>
    <p>欢迎使用 {{ appName }} 管理后台</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue"

const appName = computed(() => import.meta.env.VITE_APP_NAME || "I0")
</script>

<style scoped>
.home {
  padding: 2rem;
  text-align: center;
}

h1 {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 1rem;
}

p {
  font-size: 1.2rem;
  color: var(--secondary-color);
}
</style>

<route lang="yaml">
name: "Index"
meta:
  title: ""
</route>