<template>
  <div class="location-page">
    <!-- 操作区域 -->
    <div class="operation-section">
      <el-form :model="searchForm" inline>
        <el-form-item>
          <el-input
            v-model="searchForm.name"
            placeholder="Enter location name"
            clearable
            @clear="handleSearch"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchForm.type"
            placeholder="Select location type"
            clearable
            @change="handleSearch"
          >
            <el-option
              v-for="type in locationTypes"
              :key="type.value"
              :label="type.label"
              :value="type.value"
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
            <el-option label="All" :value="undefined" />
            <el-option label="Active" :value="true" />
            <el-option label="Inactive" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button round type="primary" @click="handleSearch" :icon="Search"> Search </el-button>
          <el-button round @click="handleReset" :icon="Refresh"> Reset </el-button>
        </el-form-item>
      </el-form>
      <el-button round type="primary" @click="handleCreate" :icon="CirclePlusFilled"> Add Location </el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <el-table
        :data="tableData"
        :loading="isLoading"
        row-key="id"
        :tree-props="
          hasSearchConditions ? undefined : { children: 'children', hasChildren: 'hasChildren' }
        "
        :default-expand-all="hasSearchConditions ? undefined : false"
        :expand-row-keys="hasSearchConditions ? [] : expandedRowKeys"
        @row-click="handleRowClick"
        style="width: 100%"
      >
        <el-table-column prop="isoCode" label="Code" min-width="100">
          <template #default="{ row }">
            <span v-if="row.isoCode">{{ row.isoCode }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>

        <el-table-column prop="name" label="Location Name" min-width="150" sortable>
          <template #default="{ row }">
            <LocationDisplay
              :location="row"
              :show-flag="row.locationType === 'COUNTRY'"
            />
          </template>
        </el-table-column>

        <el-table-column prop="locationType" label="Location Type" min-width="120">
          <template #default="{ row }">
            <el-tag round :type="getLocationTypeTagType(row.locationType)">
              {{ getLocationTypeDisplayName(row.locationType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="parentId" label="Parent ID" min-width="150">
          <template #default="{ row }">
            <span v-if="row.parentId">{{ row.parentId }}</span>
            <span v-else class="text-muted">None</span>
          </template>
        </el-table-column>

        <el-table-column prop="active" label="Status" width="100">
          <template #default="{ row }">
            <el-tag round :type="row.active ? 'success' : 'info'">
              {{ row.active ? 'Active' : 'Inactive' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="level" label="Level" width="80" />

        <el-table-column prop="createdAt" label="Created At" width="180" sortable />

        <el-table-column label="Actions" width="100" fixed="right">
          <template #default="{ row }">
            <ActionDropdown trigger="hover" placement="bottom">
              <template #dropdown>
                <el-dropdown-item @click="handleEdit(row)">
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
        </el-table-column>
      </el-table>
    </div>

    <!-- 地理位置对话框 -->
    <LocationDialog ref="locationDialogRef" @close="handleDialogClose" />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { CirclePlusFilled, Search, Refresh, Edit, Delete, Lock, Unlock } from '@element-plus/icons-vue';
// components
import LocationDialog from './components/LocationDialog.vue';
import { LocationDisplay, ActionDropdown } from '@I0/shared/components';
// types
import type { LocationOutput } from '@I0/shared/types';
// hooks
import { useLocationManagement } from './composables/useLocationManagement';

const locationDialogRef = ref<any>();

// 使用location管理composable
const {
  // 状态
  isLoading,
  locations,
  locationTypes,
  searchForm,
  hasSearchConditions,

  // 方法
  fetchLocations,
  deleteLocation,
  activateLocation,
  deactivateLocation,
  handleSearch,
  handleReset,
  getLocationTypeDisplayName,
  getLocationTypeTagType
} = useLocationManagement();

// 将locations作为tableData传递给el-table
const tableData = computed(() => locations.value as any[]);

// 计算需要展开的行键（只展开第一层）
const expandedRowKeys = computed(() => {
  if (hasSearchConditions.value) return [];

  // 获取第一层的所有节点ID（level为0或1的节点）
  return locations.value.filter(location => location.level === 0).map(location => location.id);
});

// 处理行点击
const handleRowClick = (row: LocationOutput) => {
  console.log('行点击:', row);
};

// 处理新增
const handleCreate = () => {
  locationDialogRef.value?.open('create');
};

// 处理编辑
const handleEdit = (location: LocationOutput) => {
  locationDialogRef.value?.open('edit', location);
};

// 处理删除
const handleDelete = async (location: LocationOutput) => {
  await deleteLocation(location.id);
};

// 处理激活
const handleActivate = async (location: LocationOutput) => {
  await activateLocation(location.id);
};

// 处理禁用
const handleDeactivate = async (location: LocationOutput) => {
  await deactivateLocation(location.id);
};


// 处理 LocationDialog 关闭事件
const handleDialogClose = () => {
  // 可以在这里做一些清理工作
  fetchLocations();
};

// 页面初始化
onMounted(() => {
  fetchLocations();
});
</script>

<style scoped lang="scss"></style>

<route lang="yaml">
name: Location
meta:
  title: 'Location'
  layout: 'main'
</route>
