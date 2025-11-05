<template>
  <div class="service-type-page">
    <!-- 操作区域 -->
    <div class="operation-section">
      <el-form :model="searchForm" inline>
        <el-form-item>
          <el-input
            v-model="searchForm.nameKeyword"
            placeholder="Enter service type name"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.serviceType"
            placeholder="Select service type"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="type in allServiceTypes"
              :key="type"
              :label="getServiceTypeDisplayName(type)"
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
          <el-button round type="primary" @click="handleSearch" :icon="Search"> Search </el-button>
          <el-button round @click="handleReset" :icon="Refresh"> Reset </el-button>
        </el-form-item>
      </el-form>
      <el-button round type="primary" @click="openCreateDialog" :icon="CirclePlusFilled">
        Create Service Type
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
          <div class="service-type-name">
            <span class="name">{{ row.name }}</span>
          </div>
        </template>

        <template #column-code="{ row }">
          <el-tag round :type="getServiceTypeTagType(row.code)">
            {{ getServiceTypeDisplayName(row.code) }}
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

    <!-- ServiceTypeDialog 组件 -->
    <ServiceTypeDialog ref="serviceTypeDialogRef" @close="handleDialogClose" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { CirclePlusFilled, Search, Refresh, Edit, Delete, Lock, Unlock } from '@element-plus/icons-vue';
// components
import { I0Table, ActionDropdown } from '@I0/shared/components';
import ServiceTypeDialog from './components/ServiceTypeDialog.vue';
// types
import type { TableColumn, SortChangeEvent, RowClickEvent } from '@I0/shared/components';
import type { ServiceTypePageOutput, ServiceTypePageParams } from '@I0/shared/types';
// hooks
import { useServiceTypeManagement } from './composables/useServiceTypeManagement';

const serviceTypeDialogRef = ref<any>();

// 使用 hooks 管理状态
const {
  loading,
  serviceTypes: tableData,
  allServiceTypes,
  statusOptions,
  pagination,
  fetchServiceTypes,
  deleteServiceTypeById,
  searchServiceTypes,
  resetSearch,
  changePage,
  getServiceTypeDisplayName,
  getServiceTypeTagType,
  activateServiceType,
  deactivateServiceType
} = useServiceTypeManagement();

// 搜索表单
const searchForm = reactive<ServiceTypePageParams>({
  page: 0,
  size: 20,
  nameKeyword: '',
  serviceType: undefined,
  activeOnly: undefined
});

// I0Table 列配置
const I0TableColumns = computed<TableColumn[]>(() => [
  {
    name: 'Service Type Name',
    prop: 'name',
    type: 'string',
    sortable: false,
    minWidth: 150,
    slot: true
  },
  {
    name: 'Service Type',
    prop: 'code',
    type: 'string',
    width: 150,
    slot: true
  },
  {
    name: 'Display Name',
    prop: 'displayName',
    type: 'string',
    minWidth: 150
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
    name: 'Actions',
    prop: 'actions',
    type: 'string',
    width: 100,
    fixed: 'right'
  }
]);

// 搜索处理
const handleSearch = () => {
  searchServiceTypes(searchForm);
};

// 重置搜索
const handleReset = () => {
  // 重置搜索表单
  Object.assign(searchForm, {
    page: 0,
    size: 20,
    nameKeyword: '',
    serviceType: undefined,
    activeOnly: undefined
  });
  // 调用后端重置方法
  resetSearch();
};

const handleI0SortChange = (event: SortChangeEvent) => {
  // Service type 暂不支持排序，因为后端API没有排序参数
  console.log('Sort change:', event);
};

const handleI0PageChange = (event: any) => {
  changePage(event.page, event.pageSize);
};

const handleI0RowClick = (event: RowClickEvent) => {
  console.log('I0Table 行点击:', event.row);
};

// 创建服务类型
const openCreateDialog = () => {
  serviceTypeDialogRef.value?.open('create');
};

// 编辑服务类型
const openEditDialog = (serviceType: ServiceTypePageOutput) => {
  serviceTypeDialogRef.value?.open('edit', serviceType);
};

// 删除服务类型
const handleDelete = async (serviceType: ServiceTypePageOutput) => {
  await deleteServiceTypeById(serviceType.id, serviceType.name);
};

// 激活处理
const handleActivate = async (row: any) => {
  await activateServiceType(row.id);
};

// 停用处理
const handleDeactivate = async (row: any) => {
  await deactivateServiceType(row.id);
};

// 处理 ServiceTypeDialog 关闭事件
const handleDialogClose = () => {
  // 可以在这里做一些清理工作
};

// 页面初始化
onMounted(() => {
  fetchServiceTypes();
});
</script>

<style scoped lang="scss"></style>

<route lang="yaml">
name: ServiceType
meta:
  title: 'Service Type'
  layout: 'main'
</route>
