<template>
  <div class="client-page">
    <!-- 操作区域 -->
    <div class="operation-section">
      <el-form :model="searchForm" inline>
        <el-form-item>
          <el-input
            v-model="searchForm.q"
            placeholder="Enter client name, code or alias"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.locationId"
            placeholder="Select location"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="location in locations"
              :key="location.id"
              :label="location.name"
              :value="location.id"
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
      <el-button type="primary" round @click="openCreateDialog" :icon="Plus">
        Create Client
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
          <div class="client-name">
            <span class="name">{{ row.name }}</span>
          </div>
        </template>

        <template #column-code="{ row }">
          {{ row.code }}
        </template>

        <template #column-location="{ row }">
          <LocationDisplay :location="row.location" />
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

    <!-- <pre>{{ tableData }}</pre> -->

    <!-- ClientDialog 组件 -->
    <ClientDialog ref="clientDialogRef" @close="handleDialogClose" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { Plus, Search, Refresh, Edit, Delete, Lock, Unlock } from '@element-plus/icons-vue';
// components
import { I0Table, LocationDisplay, ActionDropdown } from '@I0/shared/components';
import ClientDialog from './components/ClientDialog.vue';
// types
import type { TableColumn, SortChangeEvent, RowClickEvent } from '@I0/shared/components';
import type { Client, ClientPageParams } from '@I0/shared/types';
import { DEFAULT_CLIENT_SEARCH_PARAMS } from '@I0/shared/types';
// hooks
import { useClientManagement } from './composables/useClientManagement';

const clientDialogRef = ref<any>();

// 使用 hooks 管理状态
const {
  loading,
  clients: tableData,
  locations,
  statusOptions,
  pagination,
  fetchLocations,
  fetchClients,
  deleteClientById,
  activateClientById,
  deactivateClientById,
  searchClients,
  resetSearch,
  changePage
} = useClientManagement();

// 搜索表单
const searchForm = reactive<ClientPageParams>({
  ...DEFAULT_CLIENT_SEARCH_PARAMS
});

// I0Table 列配置
const I0TableColumns = computed<TableColumn[]>(() => [
  {
    name: 'Client Name',
    prop: 'name',
    type: 'string',
    sortable: true,
    minWidth: 150,
    slot: true
  },
  {
    name: 'Client Code',
    prop: 'code',
    type: 'string',
    width: 120,
    slot: true
  },
  {
    name: 'Location',
    prop: 'location',
    type: 'string',
    width: 150,
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
  searchClients(searchForm);
};

// 重置搜索
const handleReset = () => {
  // 重置搜索表单
  Object.assign(searchForm, {
    ...DEFAULT_CLIENT_SEARCH_PARAMS,
    q: undefined,
    locationId: undefined,
    activeOnly: undefined
  });
  // 调用后端重置方法
  resetSearch();
};

const handleI0SortChange = (event: SortChangeEvent) => {
  if (event.order) {
    searchClients({
      ...searchForm,
      sortBy: event.prop,
      sortOrder: event.order === 'ascending' ? 'asc' : 'desc'
    });
  } else {
    searchClients({
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

// 创建客户
const openCreateDialog = () => {
  clientDialogRef.value?.open('create');
};

// 编辑客户
const openEditDialog = (client: Client) => {
  clientDialogRef.value?.open('edit', client);
};

// 激活客户
const handleActivate = async (client: Client) => {
  await activateClientById(client.id);
};

// 停用客户
const handleDeactivate = async (client: Client) => {
  await deactivateClientById(client.id, client.name);
};

// 删除客户
const handleDelete = async (client: Client) => {
  await deleteClientById(client.id, client.name);
};

// 处理 ClientDialog 关闭事件
const handleDialogClose = () => {
  // 可以在这里做一些清理工作
};

// 页面初始化
onMounted(() => {
  fetchLocations();
  fetchClients();
});
</script>

<style scoped lang="scss">
</style>

<route lang="yaml">
name: Clients
meta:
  title: 'Clients'
  layout: 'main'
</route>
