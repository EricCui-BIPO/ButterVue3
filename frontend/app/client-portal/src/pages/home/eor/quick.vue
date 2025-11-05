<template>
  <Quick :items="eorQuickItems" @action="handleEorQuickAction" />
</template>

<script setup lang="ts">
import Quick from '../components/quick.vue'
import type { QuickActionItem } from '../components/quick.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const eorQuickItems: QuickActionItem[] = [
  {
    type: 'success',
    title: 'Onboarding',
    description:
      'New employee onboarding, including EOR employees. Suitable for companies without a local legal entity.',
    processing: 15,
    actionLabel: 'Onboard Now',
  },
  {
    type: 'warning',
    title: 'Information Change',
    description: 'Update employee employment information, position changes, and salary adjustments.',
    processing: 13,
    actionLabel: 'Update Now',
  },
  {
    type: 'warning',
    title: 'Work Visa Renew',
    description: "Renew foreign employee’s work visa under an Employer of Record (EOR) arrangement.",
    processing: 1,
    actionLabel: 'Renew Now',
  },
  {
    type: 'warning',
    title: 'Contract Renew',
    description:
      "Renew employee’s existing employment agreement for a new term under the same or updated conditions.",
    processing: 5,
    actionLabel: 'Renew Now',
  },
  {
    type: 'danger',
    title: 'Offboarding',
    description:
      'Employee offboarding process, including work handover, asset return, and exit procedures.',
    processing: 2,
    actionLabel: 'Offboard Now',
  },
]

function handleEorQuickAction(payload: { item: QuickActionItem; index: number }) {
  const { item } = payload
  if (item.title === 'Onboarding' || item.actionLabel === 'Onboard Now') {
    router.push('/home/eor/createEorEmployee')
    return
  }
  // 其他动作暂时仅记录日志
  console.log('EOR Quick Action click', payload)
}
</script>
