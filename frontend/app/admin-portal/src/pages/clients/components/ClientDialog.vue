<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="600px"
    @close="handleClose"
  >
    <ClientForm
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
  type Client,
  type CreateClientRequest,
  CLIENT_FORM_RULES,
  DEFAULT_CLIENT_FORM_DATA
} from '@I0/shared/types';
import ClientForm from './ClientForm.vue';
import { useClientManagement } from '../composables/useClientManagement';

const emit = defineEmits<{
  close: [];
}>();

// 使用 hooks 管理状态
const {
  submitting,
  createNewClient,
  updateExistingClient,
  fetchClients
} = useClientManagement();

// Dialog state
const dialogVisible = ref(false);
const mode = ref<'create' | 'edit'>('create');
const currentEditClient = ref<Client | null>(null);

// 创建默认表单数据的函数
const createDefaultFormData = (): CreateClientRequest => ({
  ...DEFAULT_CLIENT_FORM_DATA
});

// Form data
const formData = ref<CreateClientRequest>(createDefaultFormData());

// Form reference
const formRef = ref<InstanceType<typeof ClientForm>>();

// Form validation rules
const formRules = CLIENT_FORM_RULES;

// Computed
const dialogTitle = computed(() => (mode.value === 'edit' ? 'Edit Client' : 'Create Client'));

// Methods
const open = (openMode: 'create' | 'edit', client?: Client) => {
  mode.value = openMode;
  currentEditClient.value = client || null;
  dialogVisible.value = true;

  // Reset form data
  if (openMode === 'create') {
    formData.value = createDefaultFormData();
  } else if (openMode === 'edit' && client) {
    formData.value = {
      name: client.name,
      code: client.code,
      description: client.description || '',
      locationId: client.locationId || '',
      active: client.active
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
const handleSubmit = async (form: CreateClientRequest) => {
  try {
    if (mode.value === 'edit') {
      await updateExistingClient(
        currentEditClient.value!.id,
        {
          name: form.name,
          code: form.code,
          description: form.description || undefined,
          locationId: form.locationId || undefined,
          active: form.active!
        }
      );
    } else {
      await createNewClient({
        name: form.name,
        code: form.code,
        description: form.description || undefined,
        locationId: form.locationId || undefined,
        active: form.active!
      });
    }

    // 提交成功后关闭对话框
    close();
    fetchClients();
  } catch (error) {
    // 错误已在 hooks 中处理，这里可以留空
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