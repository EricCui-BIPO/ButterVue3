<template>
  <div class="page-calendar">
    <div class="page-calendar__header">
      <div class="page-calendar__title">
        <h1>Calendar</h1>
        <p>日历管理，日程安排、事件提醒、时间规划</p>
      </div>
    </div>

    <div class="page-calendar__placeholder">
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
.page-calendar {
  min-height: 100vh;
  padding: var(--spacing-lg);
}

.page-calendar__header {
  margin-bottom: var(--spacing-xl);
}

.page-calendar__title h1 {
  font-size: var(--font-size-2xl);
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 var(--spacing-sm) 0;
}

.page-calendar__title p {
  font-size: var(--font-size-base);
  color: var(--text-secondary);
  margin: 0;
}

.page-calendar__placeholder {
  text-align: center;
  padding: var(--spacing-2xl) 0;
}
</style>

<route lang="yaml">
name: Calendar
meta:
  title: 'Calendar'
  layout: 'main'
</route>