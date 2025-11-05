<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="600px"
    @close="handleClose"
  >
    <EntityForm
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
import {
  type Entity,
  type CreateEntityRequest,
  type EntityType
} from '@I0/shared/types'
import EntityForm from './EntityForm.vue'
import { useEntityManagement } from '../composables/useEntityManagement'


const emit = defineEmits<{
  close: []
}>()

// 使用 hooks 管理状态
const {
  submitting,
  createNewEntity,
  updateExistingEntity,
  fetchEntities
} = useEntityManagement()

// Dialog state
const dialogVisible = ref(false)
const mode = ref<'create' | 'edit'>('create')
const currentEditEntity = ref<Entity | null>(null)

// 创建默认表单数据的函数
const createDefaultFormData = () => ({
  name: '',
  entityType: '' as EntityType,
  description: '',
  active: true
})

// Form data
const formData = ref<CreateEntityRequest>(createDefaultFormData())

// Form reference
const formRef = ref<InstanceType<typeof EntityForm>>()

// Form validation rules
const formRules = {
  name: [
    { required: true, message: 'Please enter entity name', trigger: 'blur' },
    { min: 2, max: 50, message: 'Entity name length should be between 2 and 50 characters', trigger: 'blur' }
  ],
  entityType: [{ required: true, message: 'Please select entity type', trigger: 'change' }],
  description: [{ max: 500, message: 'Description cannot exceed 500 characters', trigger: 'blur' }]
}

// Computed
const dialogTitle = computed(() => (mode.value === 'edit' ? 'Edit Entity' : 'Create Entity'))

// Methods
const open = (openMode: 'create' | 'edit', entity?: Entity) => {
  mode.value = openMode
  currentEditEntity.value = entity || null
  dialogVisible.value = true

  // Reset form data
  if (openMode === 'create') {
    formData.value = createDefaultFormData()
  } else if (openMode === 'edit' && entity) {
    formData.value = {
      name: entity.name,
      entityType: entity.entityType,
      description: entity.description || '',
      active: entity.active
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
  if (!success) return;
}

// 校验表单: 通过 -> 提交数据
const handleSubmit = async (form: CreateEntityRequest ) => {
  try {
    if (mode.value === 'edit') {
      await updateExistingEntity(
        currentEditEntity.value!.id,
        {
          name: form.name,
          entityType: form.entityType,
          description: form.description || undefined,
          active: form.active!
        }
      )
    } else {
      await createNewEntity({
        name: form.name,
        entityType: form.entityType,
        description: form.description || undefined,
        active: form.active!
      })
    }

    // 提交成功后关闭对话框
    close()
    fetchEntities()
    // emit('submit', form, mode.value, currentEditEntity.value?.id, form.active)
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