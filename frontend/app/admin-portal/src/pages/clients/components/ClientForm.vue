<template>
  <el-form ref="formRef" :model="localFormData" :rules="formRules" label-position="top">
    <el-form-item label="Client Name" prop="name">
      <el-input
        v-model="localFormData.name"
        placeholder="Please enter client name"
        :disabled="submitting"
      />
    </el-form-item>

    <el-form-item label="Client Code" prop="code">
      <el-input
        v-model="localFormData.code"
        placeholder="Please enter client code"
        :disabled="submitting || props.mode === 'edit'"
      />
    </el-form-item>

    <el-form-item label="Location" prop="locationId">
      <el-select
        v-model="localFormData.locationId"
        placeholder="Please select location"
        :disabled="submitting"
        filterable
        clearable
      >
        <el-option
          v-for="location in locations"
          :key="location.id"
          :label="location.name"
          :value="location.id"
        />
      </el-select>
    </el-form-item>

    <el-form-item label="Description" prop="description">
      <el-input
        v-model="localFormData.description"
        type="textarea"
        :rows="3"
        placeholder="Please enter client description"
        :disabled="submitting"
      />
    </el-form-item>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, watch, onMounted } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import type { CreateClientRequest, LocationOutput } from '@I0/shared/types';
import { clientAPI } from '@I0/shared/api';

interface Props {
  formData: CreateClientRequest;
  formRules: FormRules;
  mode: 'create' | 'edit';
  submitting: boolean;
}

const props = defineProps<Props>();

const emit = defineEmits<{
  submit: [data: Props['formData']];
  'update:formData': [data: CreateClientRequest];
}>();

// Form reference
const formRef = ref<FormInstance>();

// 位置列表
const locations = ref<LocationOutput[]>([]);

// 创建本地响应式副本以避免直接修改props
const localFormData = reactive<CreateClientRequest>({ ...props.formData });

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

// 获取位置列表 - 使用客户端专用接口
const fetchLocations = async () => {
  try {
    const { data } = await clientAPI.getClientLocations();
    locations.value = data || [];
  } catch (error) {
    console.error('Failed to fetch locations:', error);
  }
};

// Methods
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

// 组件挂载时获取位置列表
onMounted(() => {
  fetchLocations();
});

// Expose methods
defineExpose({
  validateAndSubmit,
  resetForm
});
</script>