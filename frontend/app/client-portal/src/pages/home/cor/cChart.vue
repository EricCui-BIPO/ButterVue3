<template>
  <Charts :items="chartData" />
</template>

<script setup lang="ts">
import Charts from '../components/charts.vue'
import type { ChartItem } from '../components/charts.vue'

const chartData: ChartItem[] = [
  {
    type: 'bar',
    chartName: 'Active Contractor Amount by Country',
    xAxisData: ['China', 'India', 'USA', 'Germany', 'UK'],
    series: [{ name: 'Contractors', data: [320, 280, 250, 180, 150] }],
  },
  {
    type: 'line',
    chartName: 'New Hires & Departures in the Last 12 Months',
    xAxisData: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
    series: [
      { name: 'New Contractor', data: [85, 88, 92, 95, 90, 88, 92, 95, 90, 88, 92, 95] },
      { name: 'Termination', data: [68, 73, 82, 87, 78, 83, 63, 58, 53, 48, 38, 33] },
    ],
  },
  {
    type: 'pie',
    chartName: 'Service Processing',
    data: [
      { name: 'New Contractor', value: 15 },
      { name: 'Information Change', value: 13 },
      { name: 'Contract Renew', value: 5 },
      { name: 'Termination', value: 2 },
    ],
  },
]
</script>