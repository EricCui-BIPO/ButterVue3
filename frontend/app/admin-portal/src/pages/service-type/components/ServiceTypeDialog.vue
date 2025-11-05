<template>
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" @close="handleClose">
    <ServiceTypeForm
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
import { ref, computed } from 'vue';
import {
  type ServiceTypePageOutput,
  type CreateServiceTypeInput,
  type ServiceType
} from '@I0/shared/types';
import ServiceTypeForm from './ServiceTypeForm.vue';
import { useServiceTypeManagement } from '../composables/useServiceTypeManagement';

const emit = defineEmits<{
  close: [];
}>();

// 使用 hooks 管理状态
const { submitting, createNewServiceType, updateExistingServiceType, fetchServiceTypes } =
  useServiceTypeManagement();

// Dialog state
const dialogVisible = ref(false);
const mode = ref<'create' | 'edit'>('create');
const currentEditServiceType = ref<ServiceTypePageOutput | null>(null);

// 创建默认表单数据的函数
const createDefaultFormData = () => ({
  name: '',
  serviceType: '' as ServiceType,
  description: ''
});

// Form data
const formData = ref<CreateServiceTypeInput>(createDefaultFormData());

// Form reference
const formRef = ref<InstanceType<typeof ServiceTypeForm>>();

// Form validation rules
const formRules = {
  name: [
    { required: true, message: 'Please enter service type name', trigger: 'blur' },
    {
      min: 2,
      max: 50,
      message: 'Service type name length should be between 2 and 50 characters',
      trigger: 'blur'
    }
  ],
  serviceType: [{ required: true, message: 'Please select service type', trigger: 'change' }],
  description: [{ max: 500, message: 'Description cannot exceed 500 characters', trigger: 'blur' }]
};

// Computed
const dialogTitle = computed(() =>
  mode.value === 'edit' ? 'Edit Service Type' : 'Create Service Type'
);

// Methods
const open = (openMode: 'create' | 'edit', serviceType?: ServiceTypePageOutput) => {
  mode.value = openMode;
  currentEditServiceType.value = serviceType || null;
  dialogVisible.value = true;

  // Reset form data
  if (openMode === 'create') {
    formData.value = createDefaultFormData();
  } else if (openMode === 'edit' && serviceType) {
    formData.value = {
      name: serviceType.name,
      serviceType: serviceType.code as ServiceType,
      description: serviceType.description || ''
    };
  }

  formRef.value?.resetForm();
};

const close = () => {
  dialogVisible.value = false;
};

const handleClose = () => {
  close();
  emit('close');
};

// 点击提交 > 校验表单
const handleFormSubmit = async () => {
  const success = await formRef.value?.validateAndSubmit();
  if (!success) return;
};

// 校验表单: 通过 -> 提交数据
const handleSubmit = async (form: CreateServiceTypeInput) => {
  try {
    if (mode.value === 'edit') {
      await updateExistingServiceType(currentEditServiceType.value!.id, {
        name: form.name,
        description: form.description || undefined
      });
    } else {
      await createNewServiceType({
        name: form.name,
        serviceType: form.serviceType,
        description: form.description || undefined
      });
    }

    close();
    fetchServiceTypes();
  } catch (error: any) {
    console.log('错误信息:', error);
  }
};

const handleCancel = () => {
  close();
};

// Expose methods
defineExpose({
  open,
  close
});
</script>
