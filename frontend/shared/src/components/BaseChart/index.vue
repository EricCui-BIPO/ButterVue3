<template>
  <div ref="chartContainer" :style="{ width: width, height: height }" class="base-chart" />
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue';
import * as echarts from 'echarts';
import type { EChartsOption } from 'echarts';

interface Props {
  option: EChartsOption;
  width?: string;
  height?: string;
  theme?: string;
  notMerge?: boolean;
  lazyUpdate?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  width: '100%',
  height: '400px',
  notMerge: false,
  lazyUpdate: false
});

const chartContainer = ref<HTMLElement>();
let chartInstance: echarts.ECharts | null = null;

const initChart = () => {
  if (!chartContainer.value) return;

  chartInstance = echarts.init(chartContainer.value, props.theme);
  chartInstance.setOption(props.option, props.notMerge, props.lazyUpdate);
  
  // Store instance reference for e2e testing
  (chartContainer.value as any)._echarts_instance_ = chartInstance;

  // 添加响应式支持
  window.addEventListener('resize', handleResize);
};

const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize();
  }
};

const updateChart = () => {
  if (chartInstance) {
    chartInstance.setOption(props.option, props.notMerge, props.lazyUpdate);
  }
};

// 监听配置变化
watch(
  () => props.option,
  () => {
    updateChart();
  },
  { deep: true }
);

// 暴露图表实例方法
defineExpose({
  getChartInstance: () => chartInstance,
  resize: handleResize,
  updateChart
});

onMounted(() => {
  nextTick(() => {
    initChart();
    
    // Expose echarts globally for e2e testing
    if (typeof window !== 'undefined') {
      (window as any).echarts = echarts;
    }
  });
});

onBeforeUnmount(() => {
  if (chartInstance) {
    chartInstance.dispose();
    chartInstance = null;
  }
  window.removeEventListener('resize', handleResize);
});
</script>

<style scoped lang="scss">
.base-chart {
  min-height: 200px;
}
</style>
