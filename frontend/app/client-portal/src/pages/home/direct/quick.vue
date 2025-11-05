<template>
  <Quick :items="eorQuickItems" @action="handleEorQuickAction" />
</template>

<script setup lang="ts">
import Quick from '../components/quick.vue'
import type { QuickActionItem } from '../components/quick.vue'

const eorQuickItems: QuickActionItem[] = [
  {
    type: 'success',
    title: 'New Direct Employe (Self-Employed)',
    description:
      'Onboard new direct employees and complete their onboarding process. Suitable for scenarios where you need to temporarily hire employees locally while complying with local laws.',
    processing: 15,
    actionLabel: 'Onboard Now',
  },
  {
    type: 'warning',
    title: 'Information Update',
    description: 'Update direct employee information, contact details, bank accounts, and other critical data.',
    processing: 13,
    actionLabel: 'Update Now',
  },
  {
    type: 'warning',
    title: 'Contract Renew',
    description: "Renew direct employee contracts, supporting clause adjustments and renewal periods.",
    processing: 5,
    actionLabel: 'Renew Now',
  },
  {
    type: 'danger',
    title: 'Termination',
    description:
      'Terminate direct employee contracts, including work handover, asset return, and compliance exit procedures.',
    processing: 2,
    actionLabel: 'Terminate Now',
  },
]

function handleEorQuickAction(payload: { item: QuickActionItem; index: number }) {
  console.log('EOR Quick Action click', payload)
}
</script>
