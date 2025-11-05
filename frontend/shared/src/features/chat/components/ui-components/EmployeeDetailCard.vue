<template>
  <div>
    <el-descriptions :column="2" border direction="vertical">
      <el-descriptions-item label="Employee ID">
        {{ data.employeeId || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Employee Number">
        {{ data.employeeNumber || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Name">
        {{ data.name || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Email">
        {{ data.email || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Department">
        {{ data.department || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Position">
        {{ data.position || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Status">
        <el-tag :type="getStatusType(data.status)" round size="small">
          {{ getStatusText(data.status) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="Active">
        <el-tag :type="data.active ? 'success' : 'info'" round size="small">
          {{ data.active ? 'Yes' : 'No' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="Join Date">
        {{ formatDate(data.joinDate) }}
      </el-descriptions-item>
      <el-descriptions-item label="Leave Date">
        {{ formatDate(data.leaveDate) }}
      </el-descriptions-item>
      <el-descriptions-item label="Data Location">
        {{ data.dataLocation || '-' }}
      </el-descriptions-item>

      <!-- 工作地点信息 -->
      <template v-if="data.workLocationName || data.workLocationId">
        <el-descriptions-item label="Work Location">
          {{ data.workLocationName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="Work Location ID">
          {{ data.workLocationId || '-' }}
        </el-descriptions-item>
      </template>

      <!-- 国籍信息 -->
      <template v-if="data.nationalityName || data.nationalityId">
        <el-descriptions-item label="Nationality">
          {{ data.nationalityName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="Nationality ID">
          {{ data.nationalityId || '-' }}
        </el-descriptions-item>
      </template>

      <!-- 额外的工作地点类型信息 -->
      <el-descriptions-item v-if="data.workLocationType" label="Work Location Type">
        <el-tag type="info" round size="small">
          {{ data.workLocationType }}
        </el-tag>
      </el-descriptions-item>

      <!-- 国籍ISO代码 -->
      <el-descriptions-item v-if="data.nationalityIsoCode" label="Nationality ISO Code">
        <el-tag type="info" round size="small">
          {{ data.nationalityIsoCode }}
        </el-tag>
      </el-descriptions-item>
    </el-descriptions>
  </div>
</template>

<script setup lang="ts">
// 类型定义
interface EmployeeDetailData {
  employeeId?: string;
  employeeNumber?: string;
  name?: string;
  email?: string;
  department?: string;
  position?: string;
  status?: string;
  joinDate?: string;
  leaveDate?: string;
  dataLocation?: string;
  active?: boolean;
  workLocationId?: string;
  workLocationName?: string;
  workLocationType?: string;
  nationalityId?: string;
  nationalityName?: string;
  nationalityIsoCode?: string;
}

interface Props {
  data: EmployeeDetailData;
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

// 格式化日期
const formatDate = (date?: string): string => {
  if (!date) return '-';

  try {
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    });
  } catch {
    return date;
  }
};
</script>