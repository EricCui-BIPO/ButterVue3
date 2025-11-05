<template>
  <div>
    <el-descriptions :column="2" border direction="vertical">
      <el-descriptions-item label="Name">
        {{ data.name || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Employee Number">
        {{ data.employeeNumber || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Email">
        {{ data.email || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Status">
        <el-tag :type="getStatusType(data.status)" round size="small">
          {{ getStatusText(data.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="Work Location">
        {{ data.workLocationName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Nationality">
        {{ data.nationalityName || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Department">
        {{ data.department || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Position">
        {{ data.position || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Employee ID" :span="2">
        {{ data.employeeId || '-' }}
      </el-descriptions-item>
    </el-descriptions>
  </div>
</template>

<script setup lang="ts">
// 类型定义
interface EmployeeData {
  name?: string;
  employeeId?: string;
  employeeNumber?: string;
  email?: string;
  status?: string;
  workLocationName?: string;
  nationalityName?: string;
  position?: string | null;
  department?: string | null;
}

interface Props {
  data: EmployeeData;
}

withDefaults(defineProps<Props>(), {
  data: () => ({})
});

// 获取状态文本
const getStatusText = (status?: string): string => {
  switch (status) {
    case 'ACTIVE':
      return 'Active';
    case 'INACTIVE':
      return 'Inactive';
    case 'PENDING':
      return 'Pending';
    case 'TERMINATED':
      return 'Terminated';
    default:
      return status || 'Unknown';
  }
};

// 获取状态标签类型
const getStatusType = (status?: string): string => {
  switch (status) {
    case 'ACTIVE':
      return 'success';
    case 'INACTIVE':
    case 'TERMINATED':
      return 'danger';
    case 'PENDING':
      return 'warning';
    default:
      return 'info';
  }
};
</script>
