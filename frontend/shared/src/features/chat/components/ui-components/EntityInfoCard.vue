<template>
  <div>
    <div class="item">
      <div class="inner">
        <div class="left">Entity ID</div>
        <div class="right">{{ data.id || '-' }}</div>
      </div>
    </div>

    <div class="item">
      <div class="inner">
        <div class="left">Entity Name</div>
        <div class="right">{{ data.name || '-' }}</div>
      </div>
    </div>

    <div class="item">
      <div class="inner">
        <div class="left">Entity Type</div>
        <div class="right">
          <el-tag type="primary" round size="small">
          {{ getEntityTypeDisplayName(data.entityType) }}
        </el-tag>
        </div>
      </div>
    </div>

    <div class="item">
      <div class="inner">
        <div class="left">Entity Code</div>
        <div class="right">
          {{ data.code || '-' }}
        </div>
      </div>
    </div>

    <div class="item">
      <div class="inner">
        <div class="left">Description</div>
        <div class="right">{{ data.description || '-' }}</div>
      </div>
    </div>

    <div class="item">
      <div class="inner">
        <div class="left">Status</div>
        <div class="right">
           <el-tag :type="data.active ? 'success' : 'info'" round size="small">
          {{ data.active ? 'Active' : 'Inactive' }}
        </el-tag>
        </div>
      </div>
    </div>

    <div class="item">
      <div class="inner">
        <div class="left">Created At</div>
        <div class="right">{{ formatDateTime(data.createdAt) }}</div>
      </div>
    </div>
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
    </el-descriptions> 
  </div> 
</template>

<script setup lang="ts">
// 类型定义
interface EntityData {
  id?: string;
  name?: string;
  entityType?: string;
  code?: string;
  description?: string;
  active?: boolean;
  createdAt?: string;
}

interface Props {
  data: EntityData;
}

withDefaults(defineProps<Props>(), {
  data: () => ({})
});

// 获取实体类型显示名称
const getEntityTypeDisplayName = (entityType?: string): string => {
  if (!entityType) return 'Unknown';

  // 简化的实体类型映射，可以根据需要扩展
  const typeMap: Record<string, string> = {
    BIPO_ENTITY: 'BIPO Entity',
    CLIENT_ENTITY: 'Client Entity',
    VENDOR_ENTITY: 'Vendor Entity'
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
<style lang="scss" scoped>
.item {
  display: block;
  border-radius: 12px;
  -webkit-box-align: center;
  align-items: center;
  background-color: rgb(250, 250, 250);
  padding: 12px 16px;
  outline-offset: -1px;
  outline: rgb(242, 242, 242) solid 1px;
  word-break: break-word;
  margin-bottom: 10px;
  font-size: 12px;
  .inner {
    display: flex;
    -webkit-box-pack: start;
    justify-content: flex-start;
    -webkit-box-align: center;
    align-items: center;
    position: relative;
    text-decoration: none;
    width: 100%;
    box-sizing: border-box;
    text-align: left;
    gap: 24px;
    .left {
      margin-top: 0px;
      margin-bottom: 0px;
      gap: 4px;
      min-width: 84px;
      display: flex;
      flex-direction: column;
      flex: 1 1 auto;
    }
    .right {
      display: flex;
      text-align: right;
      -webkit-box-align: center;
      align-items: center;
      right: 0px;
      position: relative;
      top: 0px;
      transform: none;
    }
  }
}
</style>
