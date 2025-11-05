<template>
  <Charts :items="chartData" />
</template>

<script setup lang="ts">
import Charts from '../components/charts.vue'
import type { ChartItem } from '../components/charts.vue'

const chartData: ChartItem[] = [
  {
    type: 'bar',
    chartName: 'Monthly Total Payroll by Country',
    xAxisData: ['United States', 'Germany', 'China', 'United Kingdom', 'India', 'Japan', 'Canada', 'France', 'Australia', 'Brazil'],
    series: [{ name: 'Total Payroll (USD)', data: [550000, 380000, 320000, 320000, 280000, 260000, 240000, 220000, 200000, 180000] }],
  },
  {
    type: 'line',
    chartName: 'Employee Payroll Trend in Last 12 Months',
    xAxisData: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
    series: [
      { name: 'New Payroll Employees', data: [68, 73, 82, 87, 95, 102, 108, 115, 122, 128, 135, 142] },
      { name: 'Stopped Payroll Employees', data: [85, 88, 82, 78, 73, 68, 63, 58, 53, 48, 43, 38] },
    ],
  },
  {
    type: 'pie',
    chartName: 'Current Month Payroll Status Distribution',
    data: [
      { name: 'Paid', value: 75 },
      { name: 'Processing', value: 13 },
      { name: 'Pending Review', value: 8 },
      { name: 'Failed/Error', value: 4 },
    ],
  },
]
</script>