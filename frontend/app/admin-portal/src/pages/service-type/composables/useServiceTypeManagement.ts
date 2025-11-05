import { ref, computed } from 'vue';
import { createSharedComposable } from '@vueuse/core';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useAsyncAction } from '@I0/shared/composables';
import {
  type ServiceTypePageOutput,
  type ServiceTypePageParams,
  type CreateServiceTypeInput,
  type UpdateServiceTypeInput
} from '@I0/shared/types';
import { ServiceType, ServiceTypeDisplayNames, ServiceTypeTagTypes } from '@I0/shared/types';
import { serviceTypeAPI } from '@I0/shared/api';

export const useServiceTypeManagement = createSharedComposable(() => {
  // 状态管理
  const serviceTypes = ref<ServiceTypePageOutput[]>([]);
  const allServiceTypes = ref<ServiceType[]>(Object.values(ServiceType));

  // 状态选项数据源
  const statusOptions = ref([
    { label: 'All', value: undefined },
    { label: 'Active', value: true },
    { label: 'Inactive', value: false }
  ]);

  const currentParams = ref<ServiceTypePageParams>({
    page: 0,
    size: 20,
    nameKeyword: '',
    serviceType: undefined,
    activeOnly: undefined
  });

  // 分页状态
  const pagination = ref({
    total: 0,
    currentPage: 1,
    pageSize: 20
  });

  // 使用 useAsyncAction 管理各种操作
  // 获取服务类型列表（分页）
  const [fetchServiceTypes, isLoadingServiceTypes] = useAsyncAction(
    async (params?: Partial<ServiceTypePageParams>) => {
      const finalParams = { ...currentParams.value, ...params };
      currentParams.value = finalParams;

      const { data } = await serviceTypeAPI.getServiceTypePage({
        ...finalParams,
        page: pagination.value.currentPage - 1,
        size: pagination.value.pageSize
      });

      serviceTypes.value = data?.content || [];
      pagination.value.total = data?.pagination?.total || 0;
    }
  );

  // 获取单个服务类型
  const [getServiceTypeById] = useAsyncAction(async (id: string) => {
    const { data } = await serviceTypeAPI.getServiceTypeById(id);
    return data;
  });

  // 创建新服务类型
  const [createNewServiceType, isSubmitting] = useAsyncAction(
    async (data: CreateServiceTypeInput) => {
      await serviceTypeAPI.createServiceType(data);
      ElMessage.success('Service type created successfully');
      await fetchServiceTypes();
    }
  );

  // 更新服务类型
  const [updateExistingServiceType] = useAsyncAction(
    async (id: string, data: UpdateServiceTypeInput) => {
      await serviceTypeAPI.updateServiceType(id, data);
      ElMessage.success('Service type updated successfully');
      await fetchServiceTypes();
    }
  );

  // 删除操作确认函数
  const confirmDelete = async (name?: string) => {
    return await ElMessageBox.confirm(
      `Are you sure you want to delete ${name ? `service type "${name}"` : 'this service type'}? This action cannot be undone!`,
      'Confirm Delete',
      {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'error'
      }
    );
  };

  // 删除服务类型
  const [deleteServiceTypeById] = useAsyncAction(async (id: string, name?: string) => {
    await confirmDelete(name);
    await serviceTypeAPI.deleteServiceType(id);
    ElMessage.success('Service type deleted successfully');
    await fetchServiceTypes();
  });

  // 获取活跃的服务类型列表
  const [fetchActiveServiceTypes] = useAsyncAction(async () => {
    const { data } = await serviceTypeAPI.getActiveServiceTypes(0, 1000); // 获取所有活跃的服务类型
    return data?.content || [];
  });

  // 激活服务类型
  const [activateServiceType] = useAsyncAction(async (id: string) => {
    await serviceTypeAPI.activateServiceType(id);
    ElMessage.success('Service type activated successfully');
    await fetchServiceTypes();
  });

  // 停用服务类型
  const [deactivateServiceType] = useAsyncAction(async (id: string) => {
    await serviceTypeAPI.deactivateServiceType(id);
    ElMessage.success('Service type deactivated successfully');
    await fetchServiceTypes();
  });

  // 计算属性 - 合并所有 loading 状态
  const isLoading = computed(() => isLoadingServiceTypes.value);

  // 搜索和分页方法
  const searchServiceTypes = (params: Partial<ServiceTypePageParams>) => {
    pagination.value.currentPage = 1; // 重置到第一页
    fetchServiceTypes(params);
  };

  const resetSearch = () => {
    currentParams.value = {
      page: 0,
      size: 20,
      nameKeyword: '',
      serviceType: undefined,
      activeOnly: undefined
    };
    pagination.value.currentPage = 1;
    fetchServiceTypes();
  };

  const changePage = (page: number, pageSize: number) => {
    pagination.value.currentPage = page;
    pagination.value.pageSize = pageSize;
    fetchServiceTypes();
  };

  // 用于列表 service type 渲染
  const getServiceTypeDisplayName = (type: ServiceType): string => {
    return ServiceTypeDisplayNames[type] || type;
  };

  const getServiceTypeTagType = (type: ServiceType): string => {
    return ServiceTypeTagTypes[type] || 'default';
  };

  return {
    // 状态
    loading: isLoading,
    submitting: isSubmitting,
    serviceTypes,
    allServiceTypes,
    statusOptions,
    pagination,
    currentParams,

    // 主要方法
    fetchServiceTypes,
    getServiceTypeById,
    createNewServiceType,
    updateExistingServiceType,
    deleteServiceTypeById,
    fetchActiveServiceTypes,
    activateServiceType,
    deactivateServiceType,

    // 搜索和分页
    searchServiceTypes,
    resetSearch,
    changePage,

    // 实用方法
    getServiceTypeDisplayName,
    getServiceTypeTagType
  };
});
