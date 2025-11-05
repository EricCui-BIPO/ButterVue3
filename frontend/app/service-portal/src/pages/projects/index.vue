<template>
  <div class="page-projects">
    <div class="page-projects__header">
      <div class="page-projects__title">
        <h1>Projects</h1>
        <p>项目管理，项目创建、项目跟踪、项目统计</p>
      </div>
    </div>

    <div class="page-projects__placeholder">
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
.page-projects {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-projects__header {
  margin-bottom: var(--spacing-xl);
}

.page-projects__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-projects__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-projects__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Projects
meta:
  title: 'Projects'
  layout: 'main'
</route>