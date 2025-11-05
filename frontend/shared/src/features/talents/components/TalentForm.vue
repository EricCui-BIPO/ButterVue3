<template>
  <el-form
    ref="formRef"
    :model="formData"
    :rules="formRules"
    label-position="top"
    @submit.prevent
  >
    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="员工姓名" prop="name">
          <el-input
            v-model="formData.name"
            placeholder="请输入员工姓名"
            clearable
          />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="员工工号" prop="employeeNumber">
          <el-input
            v-model="formData.employeeNumber"
            placeholder="请输入员工工号"
            clearable
          />
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="邮箱地址" prop="email">
          <el-input
            v-model="formData.email"
            type="email"
            placeholder="请输入邮箱地址"
            clearable
          />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="入职日期" prop="joinDate">
          <el-date-picker
            v-model="formData.joinDate"
            type="date"
            placeholder="选择入职日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="部门" prop="department">
          <el-select
            v-model="formData.department"
            placeholder="请选择部门"
            style="width: 100%"
          >
            <el-option
              v-for="dept in departments"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="职位" prop="position">
          <el-input
            v-model="formData.position"
            placeholder="请输入职位"
            clearable
          />
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="工作地点" prop="workLocationId">
          <el-select
            v-model="formData.workLocationId"
            placeholder="请选择工作地点"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="location in workLocations"
              :key="location.id"
              :label="location.name"
              :value="location.id"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="国籍" prop="nationalityId">
          <el-select
            v-model="formData.nationalityId"
            placeholder="请选择国籍"
            style="width: 100%"
            filterable
          >
            <el-option
              v-for="country in countries"
              :key="country.id"
              :label="country.name"
              :value="country.id"
            />
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-form-item label="所属客户" prop="clientId">
          <el-select
            v-model="formData.clientId"
            placeholder="请选择所属客户（可选）"
            style="width: 100%"
            clearable
            filterable
          >
            <el-option
              v-for="client in clients"
              :key="client.id"
              :label="client.name"
              :value="client.id"
            />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="数据存储位置" prop="dataLocation">
          <el-select
            v-model="formData.dataLocation"
            placeholder="请选择数据存储位置"
            style="width: 100%"
            @change="handleDataLocationChange"
          >
            <el-option
              v-for="option in dataLocationOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            >
              <div class="data-location-option">
                <span class="label">{{ option.label }}</span>
                <span class="description">{{ option.description }}</span>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </el-col>
    </el-row>

    <!-- 数据位置合规提醒 -->
    <el-alert
      v-if="currentDataLocationNotice"
      :title="currentDataLocationNotice"
      type="warning"
      :closable="false"
      show-icon
      class="compliance-notice"
    />
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import type { FormInstance, FormRules } from 'element-plus';
import {
  DEFAULT_EMPLOYEE_FORM_DATA,
  EMPLOYEE_FORM_RULES,
  DATA_LOCATION_OPTIONS,
  type EmployeeFormData
} from '@I0/shared/types';
import { useTalentManagement } from '../composables/useTalentManagement';

// Props 定义
interface Props {
  modelValue?: EmployeeFormData;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: () => ({ ...DEFAULT_EMPLOYEE_FORM_DATA })
});

// Emits 定义
interface Emits {
  (e: 'update:modelValue', value: EmployeeFormData): void;
  (e: 'submit', value: EmployeeFormData): void;
}

const emit = defineEmits<Emits>();

// 使用人才管理 composable 获取数据
const {
  departments,
  workLocations,
  countries,
  clients
} = useTalentManagement();

// 表单引用
const formRef = ref<FormInstance>();

// 表单数据
const formData = reactive<EmployeeFormData>({ ...props.modelValue });

// 表单验证规则
const formRules: FormRules = EMPLOYEE_FORM_RULES;

// 数据位置选项
const dataLocationOptions = ref(DATA_LOCATION_OPTIONS);

// 当前数据位置的合规提醒
const currentDataLocationNotice = computed(() => {
  const option = DATA_LOCATION_OPTIONS.find(opt => opt.value === formData.dataLocation);
  return option?.complianceNotice || '';
});

// 数据位置变更处理
const handleDataLocationChange = () => {
  // 可以在这里添加额外的数据位置变更逻辑
  console.log('数据位置变更为:', formData.dataLocation);
};

// 监听 props.modelValue 变化
watch(
  () => props.modelValue,
  (newValue) => {
    if (newValue) {
      Object.assign(formData, newValue);
    }
  },
  { deep: true, immediate: true }
);

// 监听 formData 变化，触发 update:modelValue
watch(
  formData,
  (newValue) => {
    emit('update:modelValue', { ...newValue });
  },
  { deep: true }
);

// 表单验证方法
const validate = async (): Promise<boolean> => {
  if (!formRef.value) return false;
  try {
    await formRef.value.validate();
    return true;
  } catch (error) {
    console.error('表单验证失败:', error);
    return false;
  }
};

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  Object.assign(formData, DEFAULT_EMPLOYEE_FORM_DATA);
};

// 设置表单数据
const setFormData = (data: Partial<EmployeeFormData>) => {
  Object.assign(formData, data);
};

// 验证并提交表单
const validateAndSubmit = async (): Promise<boolean> => {
  // 1. 验证表单
  const isValid = await validate();
  if (!isValid) {
    return false;
  }

  // 2. 触发提交事件
  emit('submit', { ...formData });
  return true;
};

// 暴露方法给父组件
defineExpose({
  validate,
  validateAndSubmit,
  resetForm,
  setFormData
});
</script>

<style scoped lang="scss">
.data-location-option {
  display: flex;
  flex-direction: column;
  gap: 2px;

  .label {
    font-weight: 500;
    color: var(--text-primary);
  }

  .description {
    font-size: 12px;
    color: var(--text-secondary);
  }
}

.compliance-notice {
  margin-top: 20px;
}
</style>