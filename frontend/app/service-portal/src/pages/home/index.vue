<template>
  <div class="page-home">
    <div class="page-home__header">
      <div class="page-home__title">
        <h1>Home</h1>
        <p>服务仪表板，待处理服务请求、工作负载、服务质量指标</p>
      </div>
    </div>

    <div class="page-home__placeholder">
      <el-empty description="页面功能待实现">
        <el-button type="primary">敬请期待</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
// No props needed - domain pages use static content
// No computed properties needed for simplified structure
</script>

<style scoped>
.page-home {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-home__header {
  margin-bottom: var(--spacing-xl);
}

.page-home__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-home__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-home__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Home
meta:
  title: 'Home'
  layout: 'main'
</route>