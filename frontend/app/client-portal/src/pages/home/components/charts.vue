<template>
  <div ref="containerRef" class="charts-grid" :style="containerStyle">
    <template v-if="canRender">
      <div v-for="(chart, index) in items" :key="chart.key ?? index" class="chart-container">
        <div class="chart-title">{{ chart.chartName || 'Chart' }}</div>
        <BarChart v-if="chart.type === 'bar'" :x-axis-data="chart.xAxisData || []" :series="chart.series || []" />
        <LineChart v-else-if="chart.type === 'line'" :x-axis-data="chart.xAxisData || []" :series="chart.series || []" />
        <PieChart v-else-if="chart.type === 'pie'" :data="chart.data || []" />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, onBeforeUnmount } from 'vue'
import { LineChart, BarChart, PieChart } from '@I0/shared/components'

export type ChartType = 'bar' | 'line' | 'pie'

export interface SeriesItem {
  name: string
  data: number[]
}

export interface PieDatum {
  name: string
  value: number
}

export interface ChartItem {
  key?: string | number
  type: ChartType
  chartName?: string
  xAxisData?: string[]
  series?: SeriesItem[]
  data?: PieDatum[]
}

const props = defineProps<{
  items: ChartItem[]
  columns?: number // 可选：手动设置列数（不传则按 items.length）
  gap?: number // 每项之间的间距，单位 px，默认 10
}>()

// 监听容器宽度，避免初次在不可见状态下渲染导致图表宽度为 0
const containerRef = ref<HTMLElement | null>(null)
const containerWidth = ref(0)
let ro: ResizeObserver | null = null

onMounted(() => {
  if (containerRef.value) {
    ro = new ResizeObserver((entries) => {
      if (!entries.length) return
      const w = entries[0].contentRect.width
      containerWidth.value = w
    })
    ro.observe(containerRef.value)
  }
})

onBeforeUnmount(() => {
  if (ro && containerRef.value) {
    ro.unobserve(containerRef.value)
  }
  ro = null
})

// 只有当容器宽度可用（>0）时才渲染图表
const canRender = computed(() => containerWidth.value > 0)

const containerStyle = computed(() => {
  const count = props.columns ?? (props.items?.length || 1)
  const gapPx = (props.gap ?? 10) + 'px'
  return {
    display: 'grid',
    gridTemplateColumns: `repeat(${count}, 1fr)`,
    gap: gapPx,
  } as Record<string, string>
})
</script>

<style scoped>
.charts-grid {
  /* 使用 CSS Grid，根据列数动态布局（由 inline style 控制列数与间距） */
}

.chart-container {
  width: 100%;
}

.chart-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 8px 0;
}
</style>