<template>
  <Charts :items="chartData" />
</template>

<script setup lang="ts">
import Charts from '../components/charts.vue'
import type { ChartItem } from '../components/charts.vue'

const chartData: ChartItem[] = [
  {
    type: 'bar',
    chartName: 'Active Direct Employee Amount by Country',
    xAxisData: ['China', 'USA', 'India', 'Germany', 'UK', 'Japan'],
    series: [{ name: 'Direct Employees', data: [420, 380, 350, 280, 220, 180] }],
  },
  {
    type: 'line',
    chartName: 'New Hires & Departures in the Last 12 Months',
    xAxisData: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
    series: [
      { name: 'New Direct Employee', data: [45, 120, 30, 150, 70, 110, 20, 160, 90, 130, 60, 140] },
      { name: 'Termination', data: [90, 40, 130, 50, 110, 30, 140, 60, 120, 35, 125, 45] },
    ],
  },
  {
    type: 'pie',
    chartName: 'Service Processing',
    data: [
      { name: 'New Direct Employee', value: 23 },
      { name: 'Information Change', value: 3 },
      { name: 'Contract Renew', value: 13 },
      { name: 'Termination', value: 5 },
    ],
  },
]
</script>