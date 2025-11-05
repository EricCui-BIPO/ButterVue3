<template>
  <Quick :items="eorQuickItems" @action="handleEorQuickAction" />
</template>

<script setup lang="ts">
import Quick from '../components/quick.vue'
import type { QuickActionItem } from '../components/quick.vue'

const eorQuickItems: QuickActionItem[] = [
  {
    type: 'success',
    title: 'New Project',
    description:
      'Create a new global payroll project and configure multi-country payroll cycles, ensuring compliance with local tax and labor laws.',
    actionLabel: 'Create Now',
  },
  {
    type: 'warning',
    title: 'Ad-hoc Payroll',
    description: 'Run off-cycle payroll for bonuses, corrections, or one-time payments across multiple countries.',
    processing: 13,
    actionLabel: 'Run Now',
  },
  {
    type: 'warning',
    title: 'Payroll Calendar',
    description: 'View and adjust the global payroll calendar, including cut-off dates and payment schedules.',
    actionLabel: 'View Now',
  },
]

function handleEorQuickAction(payload: { item: QuickActionItem; index: number }) {
  console.log('EOR Quick Action click', payload)
}
</script>
