<template>
  <div class="entity-page">
    <!-- 操作区域 -->
    <div class="operation-section">
      <el-form :model="searchForm" inline>
        <el-form-item>
          <el-input
            v-model="searchForm.nameKeyword"
            placeholder="Enter entity name"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.entityType"
            placeholder="Select entity type"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="type in entityTypes"
              :key="type"
              :label="getEntityTypeDisplayName(type)"
              :value="type"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.activeOnly"
            placeholder="Select status"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="option in statusOptions"
              :key="option.label"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" round @click="handleSearch" :icon="Search"> Search </el-button>
          <el-button round @click="handleReset" :icon="Refresh"> Reset </el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" round @click="openCreateDialog" :icon="CirclePlusFilled">
        Create Entity
      </el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <I0Table
        :table-data="tableData"
        :table-column="I0TableColumns"
        :loading="loading"
        :pagination="pagination"
        @sort-change="handleI0SortChange"
        @pagination-change="handleI0PageChange"
        @row-click="handleI0RowClick"
      >
        <template #column-name="{ row }">
          <div class="entity-name">
            <span class="name">{{ row.name }}</span>
          </div>
        </template>

        <template #column-entityType="{ row }">
          <el-tag round :type="getEntityTypeTagType(row.entityType)">
            {{ getEntityTypeDisplayName(row.entityType) }}
          </el-tag>
        </template>

        <template #column-active="{ row }">
          <el-tag round :type="row.active ? 'success' : 'info'">
            {{ row.active ? 'Active' : 'Inactive' }}
          </el-tag>
        </template>

        <template #actions="{ row }">
          <ActionDropdown trigger="hover" placement="bottom">
            <template #dropdown>
              <el-dropdown-item @click="openEditDialog(row)">
                <el-icon><Edit /></el-icon>
                <span>Edit</span>
              </el-dropdown-item>
              <el-dropdown-item 
                v-if="row.active"
                @click="handleDeactivate(row)"
              >
                <el-icon><Lock /></el-icon>
                <span>Deactivate</span>
              </el-dropdown-item>
              <el-dropdown-item 
                v-else
                @click="handleActivate(row)"
              >
                <el-icon><Unlock /></el-icon>
                <span>Activate</span>
              </el-dropdown-item>
              <el-dropdown-item 
                @click="handleDelete(row)"
                divided
              >
                <el-icon class="danger-action"><Delete /></el-icon>
                <span class="danger-action">Delete</span>
              </el-dropdown-item>
            </template>
          </ActionDropdown>
        </template>
      </I0Table>
    </div>

    <!-- EntityDialog 组件 -->
    <EntityDialog ref="entityDialogRef" @close="handleDialogClose" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { CirclePlusFilled, Search, Refresh, Edit, Delete, Lock, Unlock } from '@element-plus/icons-vue';
// components
import { I0Table, ActionDropdown } from '@I0/shared/components';
import EntityDialog from './components/EntityDialog.vue';
// types
import type { TableColumn, SortChangeEvent, RowClickEvent } from '@I0/shared/components';
import type { Entity, EntityPageParams } from '@I0/shared/types';
// hooks
import { useEntityManagement } from './composables/useEntityManagement';

const entityDialogRef = ref<any>();

// 使用 hooks 管理状态
const {
  loading,
  entities: tableData,
  entityTypes,
  statusOptions,
  pagination,
  fetchEntityTypes,
  fetchEntities,
  deleteEntityById,
  activateEntityById,
  deactivateEntityById,
  searchEntities,
  resetSearch,
  changePage,
  getEntityTypeDisplayName,
  getEntityTypeTagType
} = useEntityManagement();

// 搜索表单
const searchForm = reactive<EntityPageParams>({
  page: 0,
  size: 20,
  nameKeyword: '',
  entityType: undefined,
  activeOnly: undefined,
  sortBy: 'createdAt',
  sortOrder: 'desc'
});

// I0Table 列配置
const I0TableColumns = computed<TableColumn[]>(() => [
  {
    name: 'Entity Name',
    prop: 'name',
    type: 'string',
    sortable: true,
    minWidth: 150,
    slot: true
  },
  {
    name: 'Entity Type',
    prop: 'entityType',
    type: 'string',
    width: 120,
    slot: true
  },
  {
    name: 'Status',
    prop: 'active',
    type: 'string',
    width: 100,
    slot: true
  },
  {
    name: 'Description',
    prop: 'description',
    type: 'string',
    minWidth: 200,
    showOverflowTooltip: true
  },
  {
    name: 'Created At',
    prop: 'createdAt',
    type: 'dateTime',
    width: 180,
    sortable: true
  },
  {
    name: 'Updated At',
    prop: 'updatedAt',
    type: 'dateTime',
    width: 180,
    sortable: true
  },
  {
    name: 'Actions',
    prop: 'actions',
    type: 'string',
    width: 100,
    fixed: 'right'
  }
]);

// 搜索处理
const handleSearch = () => {
  searchEntities(searchForm);
};

// 重置搜索
const handleReset = () => {
  // 重置搜索表单
  Object.assign(searchForm, {
    page: 0,
    size: 20,
    nameKeyword: '',
    entityType: undefined,
    activeOnly: undefined,
    sortBy: 'createdAt',
    sortOrder: 'desc'
  });
  // 调用后端重置方法
  resetSearch();
};

const handleI0SortChange = (event: SortChangeEvent) => {
  if (event.order) {
    searchEntities({
      ...searchForm,
      sortBy: event.prop,
      sortOrder: event.order === 'ascending' ? 'asc' : 'desc'
    });
  } else {
    searchEntities({
      ...searchForm,
      sortBy: 'createdAt',
      sortOrder: 'desc'
    });
  }
};

const handleI0PageChange = (event: any) => {
  changePage(event.page, event.pageSize);
};

const handleI0RowClick = (event: RowClickEvent) => {
  console.log('I0Table 行点击:', event.row);
};

// 创建实体
const openCreateDialog = () => {
  entityDialogRef.value?.open('create');
};

// 编辑实体
const openEditDialog = (entity: Entity) => {
  entityDialogRef.value?.open('edit', entity);
};

// 激活实体
const handleActivate = async (entity: Entity) => {
  await activateEntityById(entity.id);
};

// 停用实体
const handleDeactivate = async (entity: Entity) => {
  await deactivateEntityById(entity.id, entity.name);
};

// 删除实体
const handleDelete = async (entity: Entity) => {
  await deleteEntityById(entity.id, entity.name);
};

// 处理 EntityDialog 关闭事件
const handleDialogClose = () => {
  // 可以在这里做一些清理工作
};

// 页面初始化
onMounted(() => {
  fetchEntityTypes();
  fetchEntities();
});
</script>

<style scoped lang="scss"></style>

<route lang="yaml">
name: Entity
meta:
  title: 'Entity'
  layout: 'main'
</route>
