<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="600px"
    @close="handleClose"
  >
    <LocationForm
      ref="formRef"
      :form-data="formData"
      :form-rules="formRules"
      :mode="mode"
      :submitting="submitting"
      @submit="handleSubmit"
    />

    <template #footer>
      <el-button round @click="handleCancel">Cancel</el-button>
      <el-button round type="primary" :loading="submitting" @click="handleFormSubmit">
        {{ mode === 'edit' ? 'Update' : 'Create' }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import type { LocationOutput, CreateLocationInput } from '@I0/shared/types'
import { LocationType } from '@I0/shared/types'
import LocationForm from './LocationForm.vue'
import { useLocationManagement } from '../composables/useLocationManagement'

const emit = defineEmits<{
  close: []
}>()

// 使用 hooks 管理状态
const {
  submitting,
  createLocation,
  updateLocation,
  getLocationsTree
} = useLocationManagement()

// Dialog state
const dialogVisible = ref(false)
const mode = ref<'create' | 'edit'>('create')
const currentEditLocation = ref<LocationOutput | null>(null)

// 创建默认表单数据的函数
const createDefaultFormData = (): CreateLocationInput => ({
  name: '',
  locationType: '' as LocationType,
  parentId: undefined,
  isoCode: undefined,
  description: undefined,
  sortOrder: undefined
})

// Form data
const formData = ref<CreateLocationInput>(createDefaultFormData())

// Form reference
const formRef = ref<InstanceType<typeof LocationForm>>()

// Form validation rules
const formRules = {
  name: [
    { required: true, message: 'Please enter location name', trigger: 'blur' },
    { min: 2, max: 100, message: 'Location name length should be between 2 and 100 characters', trigger: 'blur' }
  ],
  locationType: [{ required: true, message: 'Please select location type', trigger: 'change' }],
  isoCode: [
    { max: 10, message: 'ISO code length cannot exceed 10 characters', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9-]*$/, message: 'ISO code can only contain letters, numbers and hyphens', trigger: 'blur' }
  ]
}

// Computed
const dialogTitle = computed(() => (mode.value === 'edit' ? 'Edit Location' : 'Create Location'))

// Methods
const open = (openMode: 'create' | 'edit', location?: LocationOutput) => {
  mode.value = openMode
  currentEditLocation.value = location || null
  dialogVisible.value = true

  // Reset form data
  if (openMode === 'create') {
    formData.value = createDefaultFormData()
  } else if (openMode === 'edit' && location) {
    formData.value = {
      name: location.name,
      locationType: location.locationType,
      parentId: location.parentId || undefined,
      isoCode: location.isoCode || undefined,
      description: undefined,
      sortOrder: undefined
    }
  }

  formRef.value?.resetForm()
}

const close = () => {
  dialogVisible.value = false
}

const handleClose = () => {
  close()
  emit('close')
}

// 点击提交 > 校验表单
const handleFormSubmit = async () => {
  const success = await formRef.value?.validateAndSubmit()
  if (!success) return
}

// 校验表单: 通过 -> 提交数据
const handleSubmit = async (form: CreateLocationInput) => {
  try {
    if (mode.value === 'edit') {
      await updateLocation(
        currentEditLocation.value!.id,
        {
          name: form.name,
          locationType: form.locationType,
          parentId: form.parentId,
          description: form.description,
          sortOrder: form.sortOrder
        }
      )
    } else {
      await createLocation({
        name: form.name,
        locationType: form.locationType,
        parentId: form.parentId,
        isoCode: form.isoCode,
        description: form.description,
        sortOrder: form.sortOrder
      })
    }

    // 提交成功后关闭对话框
    close()
    getLocationsTree()
  } catch (error) {
    // 错误已在 hooks 中处理，这里可以留空
  }
}

const handleCancel = () => {
  close()
}

// Expose methods
defineExpose({
  open,
  close
})
</script>