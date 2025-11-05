# 图表组件

基于 ECharts 的图表组件库，提供简单易用的图表组件。

## 组件列表

- **BaseChart** - 基础图表组件
- **PieChart** - 饼图组件
- **LineChart** - 折线图组件
- **BarChart** - 柱状图组件

## 安装依赖

```bash
yarn add echarts
```

## 基础用法

### 饼图组件

```vue
<template>
  <PieChart
    :data="pieData"
    title="销售分布"
    :width="'600px'"
    :height="'400px'"
    :show-legend="true"
    :radius="['40%', '70%']"
    :rose-type="false"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { PieChart, type PieDataItem } from '@I0/shared/components'

const pieData = ref<PieDataItem[]>([
  { name: '产品A', value: 335 },
  { name: '产品B', value: 310 },
  { name: '产品C', value: 234 },
  { name: '产品D', value: 135 }
])
</script>
```

### 折线图组件

```vue
<template>
  <LineChart
    :x-axis-data="xAxisData"
    :series="lineSeries"
    title="销售趋势"
    :width="'800px'"
    :height="'400px'"
    :smooth="true"
    :data-zoom="true"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { LineChart, type LineSeriesData } from '@I0/shared/components'

const xAxisData = ref(['1月', '2月', '3月', '4月', '5月', '6月'])

const lineSeries = ref<LineSeriesData[]>([
  {
    name: '产品A',
    data: [120, 132, 101, 134, 90, 230],
    smooth: true,
    lineStyle: { color: '#5470c6' }
  },
  {
    name: '产品B',
    data: [220, 182, 191, 234, 290, 330],
    smooth: true,
    lineStyle: { color: '#91cc75' }
  }
])
</script>
```

### 柱状图组件

```vue
<template>
  <BarChart
    :x-axis-data="xAxisData"
    :series="barSeries"
    title="月度销售"
    :width="'800px'"
    :height="'400px'"
    :horizontal="false"
    :data-zoom="true"
  />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { BarChart, type BarSeriesData } from '@I0/shared/components'

const xAxisData = ref(['1月', '2月', '3月', '4月', '5月', '6月'])

const barSeries = ref<BarSeriesData[]>([
  {
    name: '产品A',
    data: [120, 200, 150, 80, 70, 110],
    itemStyle: { color: '#5470c6' }
  },
  {
    name: '产品B',
    data: [60, 100, 80, 40, 30, 50],
    itemStyle: { color: '#91cc75' }
  }
])
</script>
```

## API 文档

### BaseChart

基础图表组件，提供 ECharts 的基础功能。

**Props**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| option | EChartsOption | - | ECharts 配置项 |
| width | string | '100%' | 图表宽度 |
| height | string | '400px' | 图表高度 |
| theme | string | - | 主题 |
| notMerge | boolean | false | 是否不与之前的配置合并 |
| lazyUpdate | boolean | false | 是否在更新后不立即渲染图表 |

**方法**

| 方法名 | 参数 | 返回值 | 说明 |
|--------|------|--------|------|
| getChartInstance | - | ECharts | 获取图表实例 |
| resize | - | void | 调整图表大小 |
| updateChart | - | void | 更新图表 |

### PieChart

饼图组件。

**Props**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| data | PieDataItem[] | - | 数据 |
| width | string | '100%' | 图表宽度 |
| height | string | '400px' | 图表高度 |
| title | string | - | 标题 |
| showLegend | boolean | true | 是否显示图例 |
| radius | string \| string[] | '60%' | 半径 |
| center | string[] | ['50%', '50%'] | 中心位置 |
| showLabel | boolean | true | 是否显示标签 |
| labelPosition | 'outside' \| 'inside' | 'outside' | 标签位置 |
| roseType | boolean \| 'radius' \| 'area' | false | 是否南丁格尔玫瑰图 |
| clockwise | boolean | true | 是否顺时针 |
| startAngle | number | 90 | 起始角度 |
| emphasis | boolean | true | 是否显示高亮效果 |

**类型定义**

```typescript
interface PieDataItem {
  name: string
  value: number
  itemStyle?: {
    color?: string
  }
}
```

### LineChart

折线图组件。

**Props**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| xAxisData | string[] | - | X轴数据 |
| series | LineSeriesData[] | - | 系列数据 |
| width | string | '100%' | 图表宽度 |
| height | string | '400px' | 图表高度 |
| title | string | - | 标题 |
| showLegend | boolean | true | 是否显示图例 |
| showXAxis | boolean | true | 是否显示X轴 |
| showYAxis | boolean | true | 是否显示Y轴 |
| showGrid | boolean | true | 是否显示网格 |
| showTooltip | boolean | true | 是否显示提示框 |
| smooth | boolean | false | 是否平滑曲线 |
| dataZoom | boolean | false | 是否显示数据缩放 |
| xAxisName | string | - | X轴名称 |
| yAxisName | string | - | Y轴名称 |
| xAxisRotate | number | 0 | X轴标签旋转角度 |

**类型定义**

```typescript
interface LineSeriesData {
  name: string
  data: number[]
  smooth?: boolean
  lineStyle?: {
    color?: string
    width?: number
    type?: 'solid' | 'dashed' | 'dotted'
  }
  itemStyle?: {
    color?: string
  }
  areaStyle?: {
    color?: string
    opacity?: number
  }
}
```

### BarChart

柱状图组件。

**Props**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| xAxisData | string[] | - | X轴数据 |
| series | BarSeriesData[] | - | 系列数据 |
| width | string | '100%' | 图表宽度 |
| height | string | '400px' | 图表高度 |
| title | string | - | 标题 |
| showLegend | boolean | true | 是否显示图例 |
| showXAxis | boolean | true | 是否显示X轴 |
| showYAxis | boolean | true | 是否显示Y轴 |
| showGrid | boolean | true | 是否显示网格 |
| showTooltip | boolean | true | 是否显示提示框 |
| horizontal | boolean | false | 是否水平柱状图 |
| xAxisName | string | - | X轴名称 |
| yAxisName | string | - | Y轴名称 |
| xAxisRotate | number | 0 | X轴标签旋转角度 |
| dataZoom | boolean | false | 是否显示数据缩放 |
| barGap | string | '30%' | 柱间距离 |

**类型定义**

```typescript
interface BarSeriesData {
  name: string
  data: number[]
  stack?: string
  barWidth?: string | number
  itemStyle?: {
    color?: string
    borderRadius?: number | number[]
  }
  label?: {
    show?: boolean
    position?: 'top' | 'inside' | 'outside'
    formatter?: string | ((params: any) => string)
  }
}
```

## 注意事项

1. 确保项目中已安装 `echarts` 依赖
2. 图表组件会自动响应容器大小变化
3. 组件内部已处理图表实例的创建和销毁
4. 支持响应式数据更新