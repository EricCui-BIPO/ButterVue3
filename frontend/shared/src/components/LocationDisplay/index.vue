<template>
  <div class="location-display">
    <img
      v-if="showFlag && location?.isoCode"
      :src="getFlagUrl(location.isoCode)"
      :alt="`${location.isoCode} flag`"
      class="country-flag"
      @error="handleFlagError"
    />
    <span>{{ location?.name || fallbackText }}</span>
  </div>
</template>

<script setup lang="ts">
import { getFlagUrl, handleFlagError } from '@I0/shared/utils/country-flag'

interface Location {
  isoCode?: string
  name?: string
  [key: string]: any
}

interface Props {
  location: Location
  showFlag?: boolean
  fallbackText?: string
}

withDefaults(defineProps<Props>(), {
  showFlag: true,
  fallbackText: '-'
})
</script>

<style scoped lang="scss">
.location-display {
  display: flex;
  align-items: center;
  gap: 8px;

  .country-flag {
    width: 20px;
    height: 15px;
    object-fit: cover;
    border-radius: 2px;
    box-shadow: 1px solid #e4e7ed;
  }
}
</style>