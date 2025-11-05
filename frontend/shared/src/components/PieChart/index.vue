<template>
  <BaseChart :option="chartOption" ref="baseChartRef" />
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import BaseChart from '../BaseChart/index.vue';
import type { EChartsOption } from 'echarts';

export interface PieDataItem {
  name: string;
  value: number;
  itemStyle?: {
    color?: string;
  };
}

interface Props {
  data: PieDataItem[];
  name?: string;
}

const props = withDefaults(defineProps<Props>(), {});

const baseChartRef = ref();

const chartOption = computed<EChartsOption>(() => {
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
      backgroundColor: 'rgba(0, 0, 0, 0.8)',
      borderColor: 'transparent',
      textStyle: {
        color: '#fff',
        fontSize: 12
      }
    },
    legend: {
      orient: 'horizontal',
      left: 'left',
      bottom: '5%',
      itemGap: 15,
      textStyle: {
        fontSize: 12,
        color: '#666'
      },
      icon: 'circle'
    },
    series: [
      {
        name: props.name || '数据',
        type: 'pie',
        radius: ['60%', '10%'],
        center: ['50%', '40%'],
        padAngle: 4,
        itemStyle: {
          borderRadius: 7
        },
        clockwise: true,
        startAngle: 90,
        data: props.data,
        label: {
          show: false,
          position: 'outside',
          formatter: '{b}\n{d}%',
          fontSize: 10,
          color: '#666',
          lineHeight: 14
        },
        labelLine: {
          show: true,
          length: 30,
          length2: 50,
          smooth: true
        },
        emphasis: {
          itemStyle: {
            shadowBlur: 8,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.3)'
          },
          scaleSize: 5
        },
        animationType: 'scale',
        animationEasing: 'elasticOut'
      }
    ]
  };
});

defineExpose({
  getChartInstance: () => baseChartRef.value?.getChartInstance(),
  resize: () => baseChartRef.value?.resize(),
  updateChart: () => baseChartRef.value?.updateChart()
});
</script>
