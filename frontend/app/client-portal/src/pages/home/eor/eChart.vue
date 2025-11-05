<template>
  <Charts :items="chartData" />
</template>

<script setup lang="ts">
import Charts from '../components/charts.vue'
import type { ChartItem } from '../components/charts.vue'

const chartData: ChartItem[] = [
  {
    type: 'bar',
    chartName: 'Active Employee Amount by Country',
    xAxisData: [
      'China',
      'India',
      'USA',
      'Indonesia',
      'Pakistan',
      'Brazil',
      'Nigeria',
      'Bangladesh',
      'Russia',
      'Mexico',
      'Japan',
      'Philippines',
      'Vietnam',
      'Turkey',
      'Iran',
      'Germany',
      'Thailand',
      'UK',
      'France',
      'Italy',
    ],
    series: [{ name: 'Employees', data: [850, 720, 680, 590, 540, 480, 460, 420, 380, 360, 340, 320, 310, 290, 270, 250, 240, 220, 210, 200] }],
  },
  {
    type: 'line',
    chartName: 'New Hires & Departures in the Last 12 Months',
    xAxisData: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],
    series: [
      { name: 'Onboarding', data: [120, 135, 148, 162, 155, 170, 185, 178, 165, 190, 205, 220] },
      { name: 'Offboarding', data: [80, 95, 108, 100, 80, 90, 50, 70, 44, 30, 20, 10] },
    ],
  },
  {
    type: 'pie',
    chartName: 'Service Processing',
    data: [
      { name: 'Onboarding', value: 15 },
      { name: 'Information Change', value: 13 },
      { name: 'Visa Renew', value: 1 },
      { name: 'Contract Renew', value: 5 },
      { name: 'Offboarding', value: 2 },
    ],
  },
]
</script>