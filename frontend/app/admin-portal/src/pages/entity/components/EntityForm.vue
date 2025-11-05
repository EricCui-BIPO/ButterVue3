<template>
  <el-form ref="formRef" :model="localFormData" :rules="formRules" label-position="top">
    <el-form-item label="Entity Name" prop="name">
      <el-input
        v-model="localFormData.name"
        placeholder="Please enter entity name"
        :disabled="submitting"
      />
    </el-form-item>

    <el-form-item label="Entity Type" prop="entityType">
      <el-select
        v-model="localFormData.entityType"
        placeholder="Please select entity type"
        :disabled="submitting || mode === 'edit'"
      >
        <el-option
          v-for="type in entityTypes"
          :key="type"
          :label="getEntityTypeDisplayName(type)"
          :value="type"
        />
      </el-select>
    </el-form-item>

    <el-form-item label="Description" prop="description">
      <el-input
        v-model="localFormData.description"
        type="textarea"
        :rows="3"
        placeholder="Please enter entity description"
        :disabled="submitting"
      />
    </el-form-item>

    <el-form-item label="Status" prop="active">
      <el-switch
        v-model="localFormData.active"
        active-text="Active"
        inactive-text="Inactive"
        :disabled="submitting"
      />
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import type { EntityType, CreateEntityRequest } from '@I0/shared/types';
import { EntityTypeDisplayNames } from '@I0/shared/types';
import { useEntityManagement } from '../composables/useEntityManagement';

interface Props {
  formData: CreateEntityRequest;
  formRules: FormRules;
  mode: 'create' | 'edit';
  submitting: boolean;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  submit: [data: Props['formData']];
  'update:formData': [data: CreateEntityRequest];
}>();

// 使用 hooks 获取 entityTypes
const { entityTypes } = useEntityManagement();

// Form reference
const formRef = ref<FormInstance>();

// 创建本地响应式副本以避免直接修改props
const localFormData = reactive<CreateEntityRequest>({ ...props.formData });

// 监听props变化并同步到本地副本
watch(
  () => props.formData,
  newData => {
    Object.assign(localFormData, newData);
  },
  { deep: true }
);

// 监听本地副本变化并向上传递
watch(
  localFormData,
  newData => {
    emit('update:formData', { ...newData });
  },
  { deep: true }
);

// Methods
const getEntityTypeDisplayName = (type: EntityType): string => {
  return EntityTypeDisplayNames[type] || type;
};

const validateAndSubmit = async () => {
  if (!formRef.value) return false;

  try {
    await formRef.value.validate();
    emit('submit', { ...localFormData });
    return true;
  } catch (error) {
    console.error('Form validation failed:', error);
    return false;
  }
};

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
};

// Expose methods
defineExpose({
  validateAndSubmit,
  resetForm
});
</script>
