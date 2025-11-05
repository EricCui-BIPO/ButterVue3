<template>
  <div class="talent-page">
    <!-- 操作区域 -->
    <div class="operation-section">
      <el-form :model="searchForm" inline>
        <el-form-item>
          <el-input
            v-model="searchForm.keyword"
            placeholder="搜索员工姓名、工号、邮箱"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.department"
            placeholder="选择部门"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="dept in departments"
              :key="dept.id"
              :label="dept.name"
              :value="dept.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.workLocation"
            placeholder="选择工作地点"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="location in workLocations"
              :key="location.id"
              :label="location.name"
              :value="location.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.nationality"
            placeholder="选择国籍"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="country in countries"
              :key="country.id"
              :label="country.name"
              :value="country.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.status"
            placeholder="选择状态"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="option in statusOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.dataLocation"
            placeholder="数据位置"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="option in dataLocationOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" round @click="handleSearch" :icon="Search"> 搜索 </el-button>
          <el-button round @click="handleReset" :icon="Refresh"> 重置 </el-button>
        </el-form-item>
      </el-form>
      <el-button type="primary" round @click="openCreateDialog" :icon="Plus"> 创建员工 </el-button>
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


        <template #column-workLocation="{ row }">
          <LocationDisplay
            :location="row.workLocation"
          />
        </template>

        <template #column-nationality="{ row }">
          <div class="nationality-display">
            <LocationDisplay
              :location="row.nationality"
            />
          </div>
        </template>

        <template #column-active="{ row }">
          <el-tag round :type="row.active ? 'success' : 'info'">
            {{ row.active ? '在职' : '离职' }}
          </el-tag>
        </template>

        <template #column-dataLocation="{ row }">
          {{ getDataLocationDisplayName(row.dataLocation) }}
        </template>

        <template #actions="{ row }">
          <ActionDropdown trigger="hover" placement="bottom">
            <template #dropdown>
              <el-dropdown-item @click="openEditDialog(row)">
                <el-icon><Edit /></el-icon>
                <span>编辑</span>
              </el-dropdown-item>
              <el-dropdown-item v-if="!row.active" @click="handleActivate(row)">
                <el-icon><Unlock /></el-icon>
                <span>激活</span>
              </el-dropdown-item>
              <el-dropdown-item v-else @click="handleDeactivate(row)">
                <el-icon><Lock /></el-icon>
                <span>停用</span>
              </el-dropdown-item>
              <el-dropdown-item @click="handleDelete(row)" divided>
                <el-icon class="danger-action"><Delete /></el-icon>
                <span class="danger-action">删除</span>
              </el-dropdown-item>
            </template>
          </ActionDropdown>
        </template>
      </I0Table>
    </div>

    <!-- TalentDialog 组件 -->
    <TalentDialog ref="talentDialogRef" @success="handleDialogSuccess" @close="handleDialogClose" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import { Plus, Search, Refresh, Edit, Delete, Lock, Unlock } from '@element-plus/icons-vue';
// components
import { I0Table, LocationDisplay, ActionDropdown } from '@I0/shared/components';
import TalentDialog from './components/TalentDialog.vue';
// types
import type { TableColumn, SortChangeEvent, RowClickEvent } from '@I0/shared/components';
import type { Employee, EmployeePageParams } from '@I0/shared/types';
import { DEFAULT_EMPLOYEE_SEARCH_PARAMS, DATA_LOCATION_OPTIONS } from '@I0/shared/types';
// hooks
import { useTalentManagement } from './composables/useTalentManagement';

const talentDialogRef = ref<any>();

// 使用 hooks 管理状态
const {
  loading,
  employees: tableData,
  workLocations,
  countries,
  departments,
  statusOptions,
  pagination,
  fetchEmployees,
  fetchWorkLocations,
  fetchCountries,
  fetchClients,
  deleteEmployeeById,
  activateEmployeeById,
  deactivateEmployeeById,
  searchTalentEmployees,
  resetSearch,
  changePage
} = useTalentManagement();

// 搜索表单
const searchForm = reactive<EmployeePageParams>({
  ...DEFAULT_EMPLOYEE_SEARCH_PARAMS
});

// 数据位置选项
const dataLocationOptions = ref(DATA_LOCATION_OPTIONS);

// I0Table 列配置
const I0TableColumns = computed<TableColumn[]>(() => [
  {
    name: '员工名称',
    prop: 'name',
    type: 'string',
    sortable: true,
    minWidth: 180,
  },
  {
    name: '员工编号',
    prop: 'employeeNumber',
    type: 'string',
    sortable: true,
    minWidth: 180,
  },
  {
    name: '邮箱',
    prop: 'email',
    type: 'string',
    minWidth: 200,
    showOverflowTooltip: true
  },
  {
    name: '部门',
    prop: 'department',
    type: 'string',
    width: 120
  },
  {
    name: '职位',
    prop: 'position',
    type: 'string',
    width: 150
  },
  {
    name: '工作地点',
    prop: 'workLocation',
    type: 'string',
    width: 150,
    slot: true
  },
  {
    name: '国籍',
    prop: 'nationality',
    type: 'string',
    width: 120,
    slot: true
  },
  {
    name: '状态',
    prop: 'active',
    type: 'string',
    width: 100,
    slot: true
  },
  {
    name: '数据位置',
    prop: 'dataLocation',
    type: 'string',
    width: 120,
    slot: true
  },
  {
    name: '入职日期',
    prop: 'joinDate',
    type: 'dateTime',
    width: 120,
    sortable: true
  },
  {
    name: '创建时间',
    prop: 'createdAt',
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

// 获取数据位置显示名称
const getDataLocationDisplayName = (dataLocation?: string) => {
  const option = DATA_LOCATION_OPTIONS.find(opt => opt.value === dataLocation);
  return option ? option.label : '-';
};

// 获取数据位置标签类型
const getDataLocationTagType = (dataLocation?: string) => {
  switch (dataLocation) {
    case 'NINGXIA':
      return 'primary';
    case 'SINGAPORE':
      return 'success';
    case 'GERMANY':
      return 'warning';
    default:
      return 'info';
  }
};

// 搜索处理
const handleSearch = () => {
  searchTalentEmployees(searchForm);
};

// 重置搜索
const handleReset = () => {
  // 重置搜索表单
  Object.assign(searchForm, {
    ...DEFAULT_EMPLOYEE_SEARCH_PARAMS,
    keyword: undefined,
    department: undefined,
    workLocation: undefined,
    nationality: undefined,
    status: undefined,
    dataLocation: undefined
  });
  // 调用后端重置方法
  resetSearch();
};

const handleI0SortChange = (event: SortChangeEvent) => {
  if (event.order) {
    searchTalentEmployees({
      ...searchForm,
      sortBy: event.prop,
      sortOrder: event.order === 'ascending' ? 'asc' : 'desc'
    });
  } else {
    searchTalentEmployees({
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

// 创建员工
const openCreateDialog = () => {
  talentDialogRef.value?.open('create');
};

// 编辑员工
const openEditDialog = (employee: Employee) => {
  talentDialogRef.value?.open('edit', employee);
};

// 激活员工
const handleActivate = async (employee: Employee) => {
  await activateEmployeeById(employee.id);
};

// 停用员工
const handleDeactivate = async (employee: Employee) => {
  await deactivateEmployeeById(employee.id, employee.name);
};

// 删除员工
const handleDelete = async (employee: Employee) => {
  await deleteEmployeeById(employee.id, employee.name);
};

// 处理对话框成功事件
const handleDialogSuccess = () => {
  // 弹窗已经在子组件中关闭，这里只需要刷新数据
  fetchEmployees();
};

// 处理对话框关闭事件
const handleDialogClose = () => {
  // 可以在这里做一些清理工作
};

// 页面初始化
onMounted(() => {
  fetchWorkLocations();
  fetchCountries();
  fetchClients();
  fetchEmployees();
});
</script>

<style scoped lang="scss">
.employee-info {
  display: flex;
  gap: 12px;
  align-items: center;

  .name-column {
    font-weight: 500;
    color: var(--text-primary);
    min-width: 100px;
  }

  .number-column {
    font-size: 12px;
    color: var(--text-secondary);
    background: var(--bg-color-page);
    padding: 2px 6px;
    border-radius: 4px;
  }
}

.nationality-display {
  display: flex;
  flex-direction: column;
  gap: 2px;

  .iso-code {
    font-size: 11px;
    color: var(--text-secondary);
    opacity: 0.8;
  }
}
</style>

<route lang="yaml">
name: Talents
meta:
  title: 'Talents'
  layout: 'main'
</route>
