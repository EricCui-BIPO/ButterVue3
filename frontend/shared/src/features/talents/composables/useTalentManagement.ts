import { ref, computed } from 'vue';
import { createSharedComposable } from '@vueuse/core';
import { ElMessage, ElMessageBox } from 'element-plus';
import { useAsyncAction } from '@I0/shared/composables';
import { talentAPI, locationAPI, clientAPI } from '@I0/shared/api';
import {
  type Employee,
  type EmployeeSearchParams,
  type CreateEmployeeRequest,
  type UpdateEmployeeRequest,
  type LocationOutput,
  EMPLOYEE_STATUS_OPTIONS,
  DEFAULT_EMPLOYEE_SEARCH_PARAMS,
  MOCK_DEPARTMENTS
} from '@I0/shared/types';

export const useTalentManagement = createSharedComposable(() => {
  // 状态管理
  const employees = ref<Employee[]>([]);
  const workLocations = ref<LocationOutput[]>([]);
  const countries = ref<LocationOutput[]>([]);
  const clients = ref<any[]>([]); // 使用 any 类型，复用现有 client 类型

  // 状态选项和 Mock 数据
  const statusOptions = ref(EMPLOYEE_STATUS_OPTIONS);
  const departments = ref(MOCK_DEPARTMENTS);

  const currentParams = ref<EmployeeSearchParams>({
    ...DEFAULT_EMPLOYEE_SEARCH_PARAMS
  });

  // 分页状态
  const pagination = ref({
    total: 0,
    currentPage: 1,
    pageSize: 20
  });

  // 使用 useAsyncAction 管理各种操作
  // 获取员工列表
  const [fetchEmployees, isLoadingEmployees] = useAsyncAction(
    async (params?: Partial<EmployeeSearchParams>) => {
      const finalParams = { ...currentParams.value, ...params };
      currentParams.value = finalParams;

      const { data } = await talentAPI.getEmployees({
        ...finalParams,
        page: pagination.value.currentPage - 1,
        size: pagination.value.pageSize
      });

      employees.value = data?.content || [];
      pagination.value.total = data?.pagination?.total || 0;
    }
  );

  // 获取工作地点列表
  const [fetchWorkLocations] = useAsyncAction(async () => {
    const { data } = await locationAPI.getWorkLocations();
    workLocations.value = data?.content || [];
  });

  // 获取国家列表（用于国籍选择）
  const [fetchCountries] = useAsyncAction(async () => {
    const { data } = await locationAPI.getCountries();
    countries.value = data || [];
  });

  // 获取客户列表
  const [fetchClients] = useAsyncAction(async () => {
    const { data } = await clientAPI.getClients({ activeOnly: true });
    clients.value = data?.content || [];
  });

  // 创建新员工
  const [createNewEmployee, isSubmitting] = useAsyncAction(async (data: CreateEmployeeRequest) => {
    await talentAPI.createEmployee(data);
    ElMessage.success('员工创建成功');
    await fetchEmployees();
  });

  // 更新员工
  const [updateExistingEmployee] = useAsyncAction(async (id: string, data: UpdateEmployeeRequest) => {
    await talentAPI.updateEmployee(id, data);
    ElMessage.success('员工信息更新成功');
    await fetchEmployees();
  });

  // 删除员工
  const [deleteEmployeeById] = useAsyncAction(async (id: string, name: string) => {
    // 1. 确认对话框
    await ElMessageBox.confirm(
      `确定要删除员工 "${name}" 吗？此操作不可撤销！`,
      '确认删除',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'error'
      }
    );

    // 2. 调用删除接口
    await talentAPI.deleteEmployee(id);
    ElMessage.success('员工删除成功');

    // 3. 刷新列表
    await fetchEmployees();
  });

  // 激活员工
  const [activateEmployeeById] = useAsyncAction(async (id: string) => {
    await talentAPI.activateEmployee(id);
    ElMessage.success('员工激活成功');
    await fetchEmployees();
  });

  // 停用员工
  const [deactivateEmployeeById] = useAsyncAction(async (id: string, name?: string) => {
    if (name) {
      await ElMessageBox.confirm(
        `确定要停用员工 "${name}" 吗？`,
        '确认停用',
        {
          confirmButtonText: '确认',
          cancelButtonText: '取消',
          type: 'warning'
        }
      );
    }

    await talentAPI.deactivateEmployee(id);
    ElMessage.success('员工停用成功');
    await fetchEmployees();
  });

  // 搜索员工
  const [searchEmployees] = useAsyncAction(async (params: EmployeeSearchParams) => {
    pagination.value.currentPage = 1; // 重置到第一页
    const { data } = await talentAPI.getEmployees({
      ...params,
      page: 0,
      size: pagination.value.pageSize
    });

    employees.value = data?.content || [];
    pagination.value.total = data?.pagination?.total || 0;
    currentParams.value = params;
  });

  // 根据部门搜索员工
  const [searchEmployeesByDepartment] = useAsyncAction(async (department: string) => {
    const { data } = await talentAPI.getEmployeesByDepartment(
      department,
      0,
      pagination.value.pageSize
    );

    employees.value = data?.content || [];
    pagination.value.total = data?.pagination?.total || 0;
  });

  // 根据工作地点搜索员工
  const [searchEmployeesByWorkLocation] = useAsyncAction(async (workLocation: string) => {
    const { data } = await talentAPI.getEmployeesByWorkLocation(
      workLocation,
      0,
      pagination.value.pageSize
    );

    employees.value = data?.content || [];
    pagination.value.total = data?.pagination?.total || 0;
  });

  // 根据客户搜索员工
  const [searchEmployeesByClient] = useAsyncAction(async (clientId: string) => {
    const { data } = await talentAPI.getEmployeesByClientId(
      clientId,
      0,
      pagination.value.pageSize
    );

    employees.value = data?.content || [];
    pagination.value.total = data?.pagination?.total || 0;
  });

  // 计算属性 - 合并所有 loading 状态
  const isLoading = computed(() => isLoadingEmployees.value);

  // 搜索和分页方法
  const searchTalentEmployees = (params: Partial<EmployeeSearchParams>) => {
    pagination.value.currentPage = 1;
    fetchEmployees(params);
  };

  const resetSearch = () => {
    currentParams.value = {
      ...DEFAULT_EMPLOYEE_SEARCH_PARAMS
    };
    pagination.value.currentPage = 1;
    fetchEmployees();
  };

  const changePage = (page: number, pageSize: number) => {
    pagination.value.currentPage = page;
    pagination.value.pageSize = pageSize;
    fetchEmployees();
  };

  // 初始化所有数据
  const initializeData = async () => {
    await Promise.all([
      fetchWorkLocations(),
      fetchCountries(),
      fetchClients(),
      fetchEmployees()
    ]);
  };

  return {
    // 状态
    loading: isLoading,
    submitting: isSubmitting,
    employees,
    workLocations,
    countries,
    clients,
    departments,
    statusOptions,
    pagination,
    currentParams,

    // 主要方法
    fetchEmployees,
    fetchWorkLocations,
    fetchCountries,
    fetchClients,
    createNewEmployee,
    updateExistingEmployee,
    deleteEmployeeById,
    activateEmployeeById,
    deactivateEmployeeById,

    // 搜索和分页
    searchEmployees,
    searchTalentEmployees,
    resetSearch,
    changePage,
    searchEmployeesByDepartment,
    searchEmployeesByWorkLocation,
    searchEmployeesByClient,

    // 初始化
    initializeData
  };
});