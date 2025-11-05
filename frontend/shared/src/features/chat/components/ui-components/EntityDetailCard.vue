<template>
  <div>
    <el-descriptions :column="2" border direction="vertical">
      <el-descriptions-item label="Entity ID">
        {{ data.id || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Entity Name">
        {{ data.name || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Entity Type">
        <el-tag type="primary" round size="small">
          {{ getEntityTypeDisplayName(data.entityType) }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="Entity Code">
        {{ data.code || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Description" :span="2">
        {{ data.description || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="Status">
        <el-tag :type="data.active ? 'success' : 'info'" round size="small">
          {{ data.active ? 'Active' : 'Inactive' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="Created At">
        {{ formatDateTime(data.createdAt) }}
      </el-descriptions-item>
      <el-descriptions-item label="Updated At">
        {{ formatDateTime(data.updatedAt) }}
      </el-descriptions-item>

      <!-- 实体类型详细信息 -->
      <template v-if="data.entityTypeDisplayName">
        <el-descriptions-item label="Type Display Name">
          {{ data.entityTypeDisplayName }}
        </el-descriptions-item>
        <el-descriptions-item label="Type Description">
          {{ data.entityTypeDescription || '-' }}
        </el-descriptions-item>
      </template>

      <template v-if="data.isBipoEntity !== undefined">
        <el-descriptions-item label="Is BIPO Entity">
          <el-tag :type="data.isBipoEntity ? 'success' : 'info'" round size="small">
            {{ data.isBipoEntity ? 'Yes' : 'No' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="Is External Entity">
          <el-tag :type="data.isExternalEntity ? 'warning' : 'info'" round size="small">
            {{ data.isExternalEntity ? 'Yes' : 'No' }}
          </el-tag>
        </el-descriptions-item>
      </template>
    </el-descriptions>
  </div>
</template>

<script setup lang="ts">
// 类型定义
interface EntityDetailData {
  id?: string;
  name?: string;
  entityType?: string;
  code?: string;
  description?: string;
  active?: boolean;
  createdAt?: string;
  updatedAt?: string;
  entityTypeDisplayName?: string;
  entityTypeDescription?: string;
  isBipoEntity?: boolean;
  isExternalEntity?: boolean;
}

interface Props {
  data: EntityDetailData;
}

withDefaults(defineProps<Props>(), {
  data: () => ({})
});

// 获取实体类型显示名称
const getEntityTypeDisplayName = (entityType?: string): string => {
  if (!entityType) return 'Unknown';

  // 简化的实体类型映射，可以根据需要扩展
  const typeMap: Record<string, string> = {
    'BIPO_ENTITY': 'BIPO Entity',
    'CLIENT_ENTITY': 'Client Entity',
    'VENDOR_ENTITY': 'Vendor Entity'
  };

  return typeMap[entityType] || entityType;
};

// 格式化日期时间
const formatDateTime = (dateTime?: string): string => {
  if (!dateTime) return '-';

  try {
    return new Date(dateTime).toLocaleString('en-US', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch {
    return dateTime;
  }
};
</script>