<template>
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="800px"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    @close="handleClose"
  >
    <TalentForm
      ref="talentFormRef"
      v-model="formData"
      :submitting="submitting"
      @submit="handleSubmit"
    />

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleFormSubmit">
          {{ isEditMode ? '更新' : '创建' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import TalentForm from './TalentForm.vue';
import { useTalentManagement } from '../composables/useTalentManagement';
import {
  DEFAULT_EMPLOYEE_FORM_DATA,
  type Employee,
  type EmployeeFormData,
  type CreateEmployeeRequest,
  type UpdateEmployeeRequest
} from '@I0/shared/types';

// 对话框状态
const dialogVisible = ref(false);
const dialogMode = ref<'create' | 'edit'>('create');
const editingEmployee = ref<Employee | null>(null);

// 表单数据
const formData = ref<EmployeeFormData>({ ...DEFAULT_EMPLOYEE_FORM_DATA });

// 表单引用
const talentFormRef = ref();

// 使用人才管理 composable
const { createNewEmployee, updateExistingEmployee, submitting } = useTalentManagement();

// 计算属性
const isEditMode = computed(() => dialogMode.value === 'edit');
const dialogTitle = computed(() => {
  return isEditMode.value ? '编辑员工' : '创建员工';
});

// Emits
interface Emits {
  (e: 'close'): void;
  (e: 'success', data: Employee | EmployeeFormData): void;
}

const emit = defineEmits<Emits>();

// 打开对话框
const open = async (mode: 'create' | 'edit', employee?: Employee) => {
  dialogMode.value = mode;
  editingEmployee.value = employee || null;

  if (mode === 'create') {
    // 创建模式：重置表单
    formData.value = { ...DEFAULT_EMPLOYEE_FORM_DATA };
  } else if (mode === 'edit' && employee) {
    // 编辑模式：填充表单数据
    formData.value = {
      name: employee.name || '',
      employeeNumber: employee.employeeNumber || '',
      workLocationId: employee.workLocationId || employee.workLocation?.id || '',
      nationalityId: employee.nationalityId || employee.nationality?.id || '',
      email: employee.email || '',
      department: employee.department || '',
      position: employee.position || '',
      joinDate: employee.joinDate || '',
      dataLocation: employee.dataLocation || 'NINGXIA',
      clientId: employee.clientId || undefined
    };
  }

  dialogVisible.value = true;

  // 等待下一帧确保表单已渲染
  await new Promise(resolve => setTimeout(resolve, 0));
};

// 关闭对话框
const close = () => {
  dialogVisible.value = false;
  editingEmployee.value = null;
  formData.value = { ...DEFAULT_EMPLOYEE_FORM_DATA };
};

// 处理关闭事件
const handleClose = () => {
  close();
  emit('close');
};

// 点击提交 > 触发表单验证和提交
const handleFormSubmit = async () => {
  const success = await talentFormRef.value?.validateAndSubmit();
  if (!success) return;
};

// 校验表单: 通过 -> 提交数据
const handleSubmit = async (form: EmployeeFormData) => {
  try {
    if (isEditMode.value && editingEmployee.value) {
      // 编辑模式
      const updateData: UpdateEmployeeRequest = {
        id: editingEmployee.value.id,
        ...form
      };
      await updateExistingEmployee(editingEmployee.value.id, updateData);
    } else {
      // 创建模式
      const createData: CreateEmployeeRequest = form;
      await createNewEmployee(createData);
    }

    // 提交成功后关闭对话框
    ElMessage.success(isEditMode.value ? '员工更新成功' : '员工创建成功');
    emit('success', editingEmployee.value || form);
    close();
  } catch (error) {
    console.error('操作失败:', error);
    ElMessage.error('操作失败，请重试');
  }
};

// 暴露方法给父组件
defineExpose({
  open,
  close
});
</script>

<style scoped lang="scss">
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
