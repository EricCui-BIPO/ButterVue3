<template>
  <BaseChart :option="chartOption" ref="baseChartRef" />
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import BaseChart from '../BaseChart/index.vue';
import type { EChartsOption } from 'echarts';

export interface BarSeriesData {
  name: string;
  data: number[];
  stack?: string;
  barWidth?: string | number;
  label?: {
    show?: boolean;
    position?: 'top' | 'inside' | 'outside';
    formatter?: string | ((params: any) => string);
  };
}

interface Props {
  xAxisData: string[];
  series: BarSeriesData[];
}

const props = withDefaults(defineProps<Props>(), {});

const baseChartRef = ref();

const chartOption = computed<EChartsOption>(() => {
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: props.series.map(item => item.name),
      top: 'bottom'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: props.xAxisData,
      nameLocation: 'middle',
      nameGap: 30,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      nameLocation: 'middle',
      nameGap: 30
    },
    series: props.series.map(item => ({
      name: item.name,
      type: 'bar',
      data: item.data,
      barGap: '30%',
      itemStyle: {
        borderRadius: 50
      },
      emphasis: {
        focus: 'series'
      }
    }))
  };
});

defineExpose({
  getChartInstance: () => baseChartRef.value?.getChartInstance(),
  resize: () => baseChartRef.value?.resize(),
  updateChart: () => baseChartRef.value?.updateChart()
});
</script>
