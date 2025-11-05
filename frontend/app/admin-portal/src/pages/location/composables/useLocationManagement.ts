import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import type {
  LocationOutput,
  CreateLocationInput,
  UpdateLocationInput,
  LocationPageInput
} from '@I0/shared/types';
import { LocationType } from '@I0/shared/types';
import { locationAPI } from '@I0/shared/api';

// 地理位置类型选项
export const locationTypes = [
  { label: 'Continent', value: LocationType.CONTINENT },
  { label: 'Country', value: LocationType.COUNTRY },
  { label: 'Province', value: LocationType.PROVINCE },
  { label: 'City', value: LocationType.CITY }
];

// 地理位置类型显示名称映射
const locationTypeDisplayNames = {
  [LocationType.CONTINENT]: 'Continent',
  [LocationType.COUNTRY]: 'Country',
  [LocationType.PROVINCE]: 'Province',
  [LocationType.CITY]: 'City'
};

// 地理位置类型标签类型映射
const locationTypeTagTypes = {
  [LocationType.CONTINENT]: 'primary',
  [LocationType.COUNTRY]: 'success',
  [LocationType.PROVINCE]: 'warning',
  [LocationType.CITY]: 'info'
};

export function useLocationManagement() {
  // 状态管理
  const locations = ref<LocationOutput[]>([]);
  const loading = ref(false);
  const submitting = ref(false);

  // 搜索表单
  const searchForm = ref({
    name: '',
    type: '',
    activeOnly: undefined as boolean | undefined
  });

  // 计算属性
  const tableData = computed(() => locations.value as any[]);
  const isLoading = computed(() => loading.value);

  // 判断是否有搜索条件
  const hasSearchConditions = computed(() => {
    return !!(
      searchForm.value.name ||
      searchForm.value.type ||
      searchForm.value.activeOnly !== undefined
    );
  });

  // 获取树形结构的地理位置列表
  const getLocationsTree = async () => {
    loading.value = true;
    try {
      const response = await locationAPI.getLocationsTree();

      if (response.success && response.data) {
        locations.value = response.data;
      } else {
        ElMessage.error(response.errorMessage || 'Failed to get location list');
        locations.value = [];
      }
    } catch (error) {
      console.error('Failed to get location list:', error);
      ElMessage.error('Failed to get location list');

      // 错误时重置数据
      locations.value = [];
    } finally {
      loading.value = false;
    }
  };

  // 获取搜索结果（平铺数据）
  const getSearchResults = async () => {
    loading.value = true;
    try {
      const params: LocationPageInput = {
        page: 0, // 后端从0开始
        size: 1000 // 获取大量数据以显示所有搜索结果
      };

      // 添加搜索条件
      if (searchForm.value.name) {
        params.name = searchForm.value.name;
      }
      if (searchForm.value.type) {
        params.type = searchForm.value.type;
      }
      if (searchForm.value.activeOnly !== undefined) {
        params.activeOnly = searchForm.value.activeOnly;
      }

      const response = await locationAPI.getLocationPage(params);

      if (response.success && response.data) {
        // 从分页响应中提取数据
        locations.value = response.data.content || [];
      } else {
        ElMessage.error(response.errorMessage || 'Failed to search locations');
        locations.value = [];
      }
    } catch (error) {
      console.error('Failed to search locations:', error);
      ElMessage.error('Failed to search locations');

      // 错误时重置数据
      locations.value = [];
    } finally {
      loading.value = false;
    }
  };

  // fetchLocations 方法 - 根据是否有搜索条件选择不同的API
  const fetchLocations = () => {
    if (hasSearchConditions.value) {
      getSearchResults();
    } else {
      getLocationsTree();
    }
  };

  // 根据类型获取地理位置
  const getLocationsByType = async (locationType: LocationType): Promise<LocationOutput[]> => {
    try {
      const response = await locationAPI.getLocationsByType(locationType.toString());
      if (response.success && response.data) {
        return response.data;
      } else {
        ElMessage.error(response.errorMessage || 'Failed to get location options');
        return [];
      }
    } catch (error) {
      console.error('Failed to get locations by type:', error);
      ElMessage.error('Failed to get location options');
      return [];
    }
  };

  // 创建地理位置
  const createLocation = async (data: CreateLocationInput): Promise<boolean> => {
    submitting.value = true;
    try {
      const response = await locationAPI.createLocation(data);
      if (response.success) {
        ElMessage.success('Location created successfully');
        await getLocationsTree();
        return true;
      } else {
        ElMessage.error(response.errorMessage || 'Failed to create location');
        return false;
      }
    } catch (error) {
      console.error('Failed to create location:', error);
      ElMessage.error('Failed to create location');
      return false;
    } finally {
      submitting.value = false;
    }
  };

  // 更新地理位置
  const updateLocation = async (id: string, data: UpdateLocationInput): Promise<boolean> => {
    submitting.value = true;
    try {
      const response = await locationAPI.updateLocation(id, data);
      if (response.success) {
        ElMessage.success('Location updated successfully');
        await getLocationsTree();
        return true;
      } else {
        ElMessage.error(response.errorMessage || 'Failed to update location');
        return false;
      }
    } catch (error) {
      console.error('Failed to update location:', error);
      ElMessage.error('Failed to update location');
      return false;
    } finally {
      submitting.value = false;
    }
  };

  // 删除地理位置
  const deleteLocation = async (id: string): Promise<boolean> => {
    try {
      const response = await locationAPI.deleteLocation(id);
      if (response.success) {
        ElMessage.success('Location deleted successfully');
        await getLocationsTree();
        return true;
      } else {
        ElMessage.error(response.errorMessage || 'Failed to delete location');
        return false;
      }
    } catch (error) {
      console.error('Failed to delete location:', error);
      ElMessage.error('Failed to delete location');
      return false;
    }
  };

  // 激活地理位置
  const activateLocation = async (id: string): Promise<boolean> => {
    try {
      const response = await locationAPI.activateLocation(id);
      if (response.success) {
        ElMessage.success('Location activated successfully');
        await getLocationsTree();
        return true;
      } else {
        ElMessage.error(response.errorMessage || 'Failed to activate location');
        return false;
      }
    } catch (error) {
      console.error('Failed to activate location:', error);
      ElMessage.error('Failed to activate location');
      return false;
    }
  };

  // 禁用地理位置
  const deactivateLocation = async (id: string): Promise<boolean> => {
    try {
      const response = await locationAPI.deactivateLocation(id);
      if (response.success) {
        ElMessage.success('Location disabled successfully');
        await getLocationsTree();
        return true;
      } else {
        ElMessage.error(response.errorMessage || 'Failed to disable location');
        return false;
      }
    } catch (error) {
      console.error('Failed to disable location:', error);
      ElMessage.error('Failed to disable location');
      return false;
    }
  };

  // 搜索处理
  const handleSearch = () => {
    fetchLocations();
  };

  // 重置搜索
  const handleReset = () => {
    searchForm.value = {
      name: '',
      type: '',
      activeOnly: undefined
    };
    fetchLocations();
  };

  // 获取地理位置类型显示名称
  const getLocationTypeDisplayName = (locationType: LocationType): string => {
    return locationTypeDisplayNames[locationType] || locationType;
  };

  // 获取地理位置类型标签类型
  const getLocationTypeTagType = (locationType: LocationType): string => {
    return locationTypeTagTypes[locationType] || 'default';
  };

  return {
    // 状态
    locations,
    loading,
    submitting,
    searchForm,

    // 计算属性
    tableData,
    isLoading,
    hasSearchConditions,

    // 常量
    locationTypes,

    // 方法
    getLocationsTree,
    getSearchResults,
    fetchLocations,
    getLocationsByType,
    createLocation,
    updateLocation,
    deleteLocation,
    activateLocation,
    deactivateLocation,
    handleSearch,
    handleReset,
    getLocationTypeDisplayName,
    getLocationTypeTagType
  };
}
