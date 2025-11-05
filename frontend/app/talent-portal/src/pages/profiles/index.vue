<template>
  <div class="page-profiles">
    <div class="page-profiles__header">
      <div class="page-profiles__title">
        <h1>Profiles</h1>
        <p>档案管理，个人档案、档案更新、档案维护</p>
      </div>
    </div>

    <div class="page-profiles__placeholder">
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
.page-profiles {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-profiles__header {
  margin-bottom: var(--spacing-xl);
}

.page-profiles__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-profiles__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-profiles__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Profiles
meta:
  title: 'Profiles'
  layout: 'main'
</route>