import { ref, computed } from 'vue';
import { createSharedComposable } from '@vueuse/core';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useAsyncAction } from '@I0/shared/composables';
import { entityAPI } from '@I0/shared/api';
import {
  type Entity,
  type EntityPageParams,
  EntityType,
  EntityTypeDisplayNames,
  EntityTagTypes,
  type CreateEntityRequest,
  type UpdateEntityRequest
} from '@I0/shared/types';

export const useEntityManagement = createSharedComposable(() => {
  // 状态管理
  const entities = ref<Entity[]>([]);
  const entityTypes = ref<EntityType[]>([]);

  // 状态选项数据源
  const statusOptions = ref([
    { label: 'All', value: undefined },
    { label: 'Active', value: true },
    { label: 'Inactive', value: false }
  ]);

  const currentParams = ref<EntityPageParams>({
    page: 0,
    size: 20,
    nameKeyword: '',
    entityType: undefined,
    activeOnly: undefined,
    sortBy: 'createdAt',
    sortOrder: 'desc'
  });

  // 分页状态
  const pagination = ref({
    total: 0,
    currentPage: 1,
    pageSize: 20
  });

  // 使用 useAsyncAction 管理各种操作
  // 获取实体类型列表
  const [fetchEntityTypes] = useAsyncAction(async () => {
    const { data } = await entityAPI.getEntityTypes();
    entityTypes.value = data || [];
  });

  // 获取实体列表
  const [fetchEntities, isLoadingEntities] = useAsyncAction(
    async (params?: Partial<EntityPageParams>) => {
      const finalParams = { ...currentParams.value, ...params };
      currentParams.value = finalParams;

      const { data } = await entityAPI.getEntities({
        ...finalParams,
        page: pagination.value.currentPage - 1,
        size: pagination.value.pageSize
      });

      entities.value = data?.content || [];
      pagination.value.total = data?.pagination?.total || 0;
    }
  );

  // 创建新实体
  const [createNewEntity, isSubmitting] = useAsyncAction(async (data: CreateEntityRequest) => {
    await entityAPI.createEntity(data);
    ElMessage.success('Entity created successfully');
    await fetchEntities();
  });

  // 更新实体
  const [updateExistingEntity] = useAsyncAction(async (id: string, data: UpdateEntityRequest) => {
    await entityAPI.updateEntity(id, data);

    ElMessage.success('Entity updated successfully');

    await fetchEntities();
  });

  // 删除实体
  const [deleteEntityById] = useAsyncAction(async (id: string, name: string) => {
    // 1. 确认对话框
    await ElMessageBox.confirm(
      `Are you sure you want to delete entity "${name}"? This action cannot be undone!`,
      'Confirm Delete',
      {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'error'
      }
    );

    // 2. 调用删除接口
    await entityAPI.deleteEntity(id);

    ElMessage.success('Entity deleted successfully');

    // 3. 刷新列表
    await fetchEntities();
  });

  // 激活实体
  const [activateEntityById] = useAsyncAction(async (id: string) => {
    await entityAPI.activateEntity(id);
    ElMessage.success('Entity activated successfully');
    await fetchEntities();
  });

  // 停用实体
  const [deactivateEntityById] = useAsyncAction(async (id: string, name?: string) => {
    if (name) {
      await ElMessageBox.confirm(
        `Are you sure you want to deactivate entity "${name}"?`,
        'Confirm Deactivate',
        {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }
      );
    }

    await entityAPI.deactivateEntity(id);
    await fetchEntities();
    ElMessage.success('Entity deactivated successfully');
  });

  // 计算属性 - 合并所有 loading 状态
  const isLoading = computed(() => isLoadingEntities.value);

  // 搜索和分页方法
  const searchEntities = (params: Partial<EntityPageParams>) => {
    pagination.value.currentPage = 1; // 重置到第一页
    fetchEntities(params);
  };

  const resetSearch = () => {
    currentParams.value = {
      page: 0,
      size: 20,
      nameKeyword: '',
      entityType: undefined,
      activeOnly: undefined,
      sortBy: 'createdAt',
      sortOrder: 'desc'
    };
    pagination.value.currentPage = 1;
    fetchEntities();
  };

  const changePage = (page: number, pageSize: number) => {
    pagination.value.currentPage = page;
    pagination.value.pageSize = pageSize;
    fetchEntities();
  };

  // 用于列表 entity type 渲染
  const getEntityTypeDisplayName = (type: EntityType): string => {
    return EntityTypeDisplayNames[type] || type;
  };

  const getEntityTypeTagType = (type: EntityType): string => {
    return EntityTagTypes[type] || 'default';
  };

  return {
    // 状态
    loading: isLoading,
    submitting: isSubmitting,
    entities,
    entityTypes,
    statusOptions,
    pagination,
    currentParams,

    // 主要方法
    fetchEntityTypes,
    fetchEntities,
    createNewEntity,
    updateExistingEntity,
    deleteEntityById,
    activateEntityById,
    deactivateEntityById,

    // 搜索和分页
    searchEntities,
    resetSearch,
    changePage,

    // 实用方法
    getEntityTypeDisplayName,
    getEntityTypeTagType
  };
});
