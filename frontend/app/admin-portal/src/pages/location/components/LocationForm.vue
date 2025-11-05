<template>
  <el-form
    ref="formRef"
    :model="localFormData"
    :rules="props.formRules"
    label-position="top"
  >
    <el-form-item label="Location Name" prop="name">      
      <el-input
        v-model="localFormData.name"
        placeholder="Enter location name"
        :disabled="props.submitting"
      />
    </el-form-item>

    <el-form-item label="ISO Code" prop="isoCode">
      <el-input
        v-model="localFormData.isoCode"
        placeholder="Enter ISO code (optional, e.g., CN or CN-44-GZ)"
        :disabled="props.submitting"
        maxlength="10"
        pattern="[A-Za-z0-9-]+"
        show-word-limit
      />
    </el-form-item>

    <el-form-item label="Location Type" prop="locationType">
      <el-select
        v-model="localFormData.locationType"
        placeholder="Select location type"
        :disabled="props.submitting || props.mode === 'edit'"
        style="width: 100%"
      >
        <el-option
          v-for="type in locationTypes"
          :key="type.value"
          :label="type.label"
          :value="type.value"
        />
      </el-select>
    </el-form-item>

    <el-form-item label="Parent Location" prop="parentId">
      <el-select
        v-model="localFormData.parentId"
        placeholder="Select parent location (optional)"
        :disabled="props.submitting"
        clearable
        filterable
        style="width: 100%"
      >
        <el-option
          v-for="location in parentLocationOptions"
          :key="location.id"
          :label="location.name"
          :value="location.id"
        />
      </el-select>
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { CreateLocationInput, LocationOutput } from '@I0/shared/types'
import { LocationType } from '@I0/shared/types'
import { useLocationManagement } from '../composables/useLocationManagement'

interface Props {
  formData: CreateLocationInput
  formRules: FormRules
  mode: 'create' | 'edit'
  submitting: boolean
}

const props = defineProps<Props>()

const emit = defineEmits<{
  submit: [data: Props['formData']]
  'update:formData': [data: CreateLocationInput]
}>()

// 使用 hooks 获取数据和方法
const { locationTypes, getLocationsByType } = useLocationManagement()

// Form reference
const formRef = ref<FormInstance>()

// 创建本地响应式副本以避免直接修改props
const localFormData = reactive<CreateLocationInput>({ ...props.formData })

// 上级地理位置选项
const parentLocationOptions = ref<LocationOutput[]>([])

// 监听props变化并同步到本地副本
watch(() => props.formData, (newData) => {
  Object.assign(localFormData, newData)
}, { deep: true })

// 监听本地副本变化并向上传递
watch(localFormData, (newData) => {
  emit('update:formData', { ...newData })
}, { deep: true })

// 监听地理位置类型变化，加载对应的上级选项
watch(() => localFormData.locationType, async (newType) => {
  if (newType) {
    await loadParentLocationOptions(newType)
  } else {
    // 清空上级选项和选中值
    parentLocationOptions.value = []
    localFormData.parentId = undefined
  }
})

// 加载上级地理位置选项
const loadParentLocationOptions = async (locationType: LocationType) => {
  try {
    // 根据当前类型确定上级类型
    const parentType = getParentLocationType(locationType)
    if (parentType) {
      const locations = await getLocationsByType(parentType)
      parentLocationOptions.value = locations
      
      // 如果当前选中的上级不在新的选项列表中，清空选择
      if (localFormData.parentId && !locations.some(loc => loc.id === localFormData.parentId)) {
        localFormData.parentId = undefined
      }
    } else {
      parentLocationOptions.value = []
      localFormData.parentId = undefined
    }
  } catch (error) {
    console.error('Failed to load parent location options:', error)
    parentLocationOptions.value = []
    localFormData.parentId = undefined
  }
}

// 获取上级地理位置类型
const getParentLocationType = (locationType: LocationType): LocationType | null => {
  switch (locationType) {
    case LocationType.COUNTRY:
      return LocationType.CONTINENT
    case LocationType.PROVINCE:
      return LocationType.COUNTRY
    case LocationType.CITY:
      return LocationType.PROVINCE
    default:
      return null
  }
}

// Methods
const validateAndSubmit = async () => {
  if (!formRef.value) return false

  try {
    await formRef.value.validate()
    emit('submit', { ...localFormData })
    return true
  } catch (error) {
    console.error('Form validation failed:', error)
    return false
  }
}

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 组件挂载时加载初始数据
onMounted(() => {
  if (localFormData.locationType) {
    loadParentLocationOptions(localFormData.locationType)
  }
})

// Expose methods
defineExpose({
  validateAndSubmit,
  resetForm
})
</script>