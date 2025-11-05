<template>
  <BaseChart
    :option="chartOption"
    ref="baseChartRef"
  />
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import BaseChart from '../BaseChart/index.vue';
import type { EChartsOption } from 'echarts';

export interface LineSeriesData {
  name: string;
  data: number[];
}

interface Props {
  xAxisData: string[];
  series: LineSeriesData[];
}

const props = defineProps<Props>();

const baseChartRef = ref();

const chartOption = computed<EChartsOption>(() => {
  return {
    // 内置的默认标题策略：不显示标题，由具体页面决定是否需要额外描述
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: props.series.map(item => item.name),
      top: 'bottom'
    },
    grid: {
      left: '2%',
      right: '2%',
      bottom: '20%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: props.xAxisData,
      nameLocation: 'middle',
      nameGap: 30,
      axisLabel: { rotate: 45 }
    },
    yAxis: {
      type: 'value',
      nameLocation: 'middle',
      nameGap: 50
    },
    // 默认不启用 dataZoom，如需启用请在具体图表组件中实现
    series: props.series.map(item => ({
      name: item.name,
      type: 'line',
      smooth: true,
      data: item.data,
      lineStyle: { width: 3 }
    }))
  };
});

defineExpose({
  getChartInstance: () => baseChartRef.value?.getChartInstance(),
  resize: () => baseChartRef.value?.resize(),
  updateChart: () => baseChartRef.value?.updateChart()
});
</script>
