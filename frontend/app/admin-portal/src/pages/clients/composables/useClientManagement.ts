import { ref, computed } from 'vue';
import { createSharedComposable } from '@vueuse/core';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useAsyncAction } from '@I0/shared/composables';
import { clientAPI } from '@I0/shared/api';
import {
  type Client,
  type ClientSearchParams,
  type CreateClientRequest,
  type UpdateClientRequest,
  type LocationOutput,
  CLIENT_STATUS_OPTIONS,
  DEFAULT_CLIENT_SEARCH_PARAMS
} from '@I0/shared/types';

export const useClientManagement = createSharedComposable(() => {
  // 状态管理
  const clients = ref<Client[]>([]);
  const locations = ref<LocationOutput[]>([]);

  // 状态选项数据源
  const statusOptions = ref(CLIENT_STATUS_OPTIONS);

  const currentParams = ref<ClientSearchParams>({
    ...DEFAULT_CLIENT_SEARCH_PARAMS
  });

  // 分页状态
  const pagination = ref({
    total: 0,
    currentPage: 1,
    pageSize: 20
  });

  // 使用 useAsyncAction 管理各种操作
  // 获取客户列表
  const [fetchClients, isLoadingClients] = useAsyncAction(
    async (params?: Partial<ClientSearchParams>) => {
      const finalParams = { ...currentParams.value, ...params };
      currentParams.value = finalParams;

      const { data } = await clientAPI.getClients({
        ...finalParams,
        page: pagination.value.currentPage - 1,
        size: pagination.value.pageSize
      });

      clients.value = data?.content || [];
      pagination.value.total = data?.pagination?.total || 0;
    }
  );

  // 获取位置列表 - 使用客户端专用接口
  const [fetchLocations] = useAsyncAction(async () => {
    const { data } = await clientAPI.getClientLocations();
    locations.value = data || [];
  });

  // 创建新客户
  const [createNewClient, isSubmitting] = useAsyncAction(async (data: CreateClientRequest) => {
    await clientAPI.createClient(data);
    ElMessage.success('Client created successfully');
    await fetchClients();
  });

  // 更新客户
  const [updateExistingClient] = useAsyncAction(async (id: string, data: UpdateClientRequest) => {
    await clientAPI.updateClient(id, data);
    ElMessage.success('Client updated successfully');
    await fetchClients();
  });

  // 删除客户
  const [deleteClientById] = useAsyncAction(async (id: string, name: string) => {
    // 1. 确认对话框
    await ElMessageBox.confirm(
      `Are you sure you want to delete client "${name}"? This action cannot be undone!`,
      'Confirm Delete',
      {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'error'
      }
    );

    // 2. 调用删除接口
    await clientAPI.deleteClient(id);
    ElMessage.success('Client deleted successfully');

    // 3. 刷新列表
    await fetchClients();
  });

  // 激活客户
  const [activateClientById] = useAsyncAction(async (id: string) => {
    await clientAPI.activateClient(id);
    ElMessage.success('Client activated successfully');
    await fetchClients();
  });

  // 停用客户
  const [deactivateClientById] = useAsyncAction(async (id: string, name?: string) => {
    if (name) {
      await ElMessageBox.confirm(
        `Are you sure you want to deactivate client "${name}"?`,
        'Confirm Deactivate',
        {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }
      );
    }

    await clientAPI.deactivateClient(id);
    await fetchClients();
    ElMessage.success('Client deactivated successfully');
  });

  // 检查客户名称唯一性
  const [checkClientNameUnique] = useAsyncAction(async (name: string, excludeId?: string) => {
    const { data } = await clientAPI.checkClientNameUnique(name, excludeId);
    return data;
  });

  // 检查客户代码唯一性
  const [checkClientCodeUnique] = useAsyncAction(async (code: string, excludeId?: string) => {
    const { data } = await clientAPI.checkClientCodeUnique(code, excludeId);
    return data;
  });

  // 计算属性 - 合并所有 loading 状态
  const isLoading = computed(() => isLoadingClients.value);

  // 搜索和分页方法
  const searchClients = (params: Partial<ClientSearchParams>) => {
    pagination.value.currentPage = 1; // 重置到第一页
    fetchClients(params);
  };

  const resetSearch = () => {
    currentParams.value = {
      ...DEFAULT_CLIENT_SEARCH_PARAMS
    };
    pagination.value.currentPage = 1;
    fetchClients();
  };

  const changePage = (page: number, pageSize: number) => {
    pagination.value.currentPage = page;
    pagination.value.pageSize = pageSize;
    fetchClients();
  };

  // 根据位置搜索客户
  const searchClientsByLocation = (locationId: string) => {
    searchClients({ locationId });
  };

  // 根据名称搜索客户
  const searchClientsByKeyword = (q: string) => {
    searchClients({ q });
  };

  return {
    // 状态
    loading: isLoading,
    submitting: isSubmitting,
    clients,
    locations,
    statusOptions,
    pagination,
    currentParams,

    // 主要方法
    fetchClients,
    fetchLocations,
    createNewClient,
    updateExistingClient,
    deleteClientById,
    activateClientById,
    deactivateClientById,
    checkClientNameUnique,
    checkClientCodeUnique,

    // 搜索和分页
    searchClients,
    resetSearch,
    changePage,
    searchClientsByLocation,
    searchClientsByKeyword
  };
});