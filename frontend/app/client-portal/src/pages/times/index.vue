<template>
  <div class="page-times">
    <div class="page-times__header">
      <div class="page-times__title">
        <h1>Times</h1>
        <p>时间管理，工时记录、考勤统计、时间追踪</p>
      </div>
    </div>

    <div class="page-times__placeholder">
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
.page-times {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-times__header {
  margin-bottom: var(--spacing-xl);
}

.page-times__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-times__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-times__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Times
meta:
  title: 'Times'
  layout: 'main'
</route>