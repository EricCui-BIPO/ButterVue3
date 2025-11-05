import request from '../../core/request';
import type { ApiResponse, PaginationData } from '@I0/shared/types';
import type {
  Employee,
  CreateEmployeeRequest,
  UpdateEmployeeRequest,
  EmployeePageParams
} from '@I0/shared/types';

// 人才 API 类
class TalentAPI {
  private readonly basePath = '/v1/employees';

  // 分页查询员工
  async getEmployees(params?: EmployeePageParams) {
    return request.get<PaginationData<Employee>>(this.basePath, params);
  }

  // 根据ID获取员工详情
  async getEmployeeById(id: string): Promise<ApiResponse<Employee>> {
    return request.get<Employee>(`${this.basePath}/${id}`);
  }

  // 创建员工
  async createEmployee(data: CreateEmployeeRequest): Promise<ApiResponse<Employee>> {
    return request.post<Employee>(this.basePath, data);
  }

  // 更新员工
  async updateEmployee(id: string, data: UpdateEmployeeRequest): Promise<ApiResponse<Employee>> {
    return request.put<Employee>(`${this.basePath}/${id}`, data);
  }

  // 部分更新员工
  async patchEmployee(id: string, data: UpdateEmployeeRequest): Promise<ApiResponse<Employee>> {
    return request.patch<Employee>(`${this.basePath}/${id}`, data);
  }

  // 搜索员工
  async searchEmployees(keyword: string, page = 0, size = 20) {
    return request.get<PaginationData<Employee>>(`${this.basePath}/search`, {
      keyword,
      page,
      size
    });
  }

  // 根据部门查询员工
  async getEmployeesByDepartment(department: string, page = 0, size = 20) {
    return request.get<PaginationData<Employee>>(`${this.basePath}/by-department`, {
      department,
      page,
      size
    });
  }

  // 根据工作地点查询员工
  async getEmployeesByWorkLocation(workLocation: string, page = 0, size = 20) {
    return request.get<PaginationData<Employee>>(`${this.basePath}/by-work-location`, {
      workLocation,
      page,
      size
    });
  }

  // 根据客户ID查询员工
  async getEmployeesByClientId(clientId: string, page = 0, size = 20) {
    return request.get<PaginationData<Employee>>(`${this.basePath}/by-client`, {
      clientId,
      page,
      size
    });
  }

  // 获取激活员工
  async getActiveEmployees(page = 0, size = 20) {
    return request.get<PaginationData<Employee>>(`${this.basePath}/active`, {
      page,
      size
    });
  }

  // 获取员工访问权限描述
  async getEmployeeAccessPermission(
    id: string,
    userId: string,
    userRole: string
  ): Promise<ApiResponse<string>> {
    return request.get<string>(`${this.basePath}/${id}/access-permission`, {
      headers: {
        'X-User-Id': userId,
        'X-User-Role': userRole
      }
    });
  }

  // 获取员工法律法规提示
  async getEmployeeLegalNotice(id: string): Promise<ApiResponse<string>> {
    return request.get<string>(`${this.basePath}/${id}/legal-notice`);
  }

  // 激活员工（如果后端有此接口）
  async activateEmployee(id: string): Promise<ApiResponse<Employee>> {
    return request.put<Employee>(`${this.basePath}/${id}/activate`);
  }

  // 停用员工（如果后端有此接口）
  async deactivateEmployee(id: string): Promise<ApiResponse<Employee>> {
    return request.put<Employee>(`${this.basePath}/${id}/deactivate`);
  }

  // 删除员工（如果后端支持）
  async deleteEmployee(id: string): Promise<ApiResponse<void>> {
    return request.delete<void>(`${this.basePath}/${id}`);
  }

  // 批量操作员工状态（扩展功能）
  async batchUpdateEmployeeStatus(
    ids: string[],
    active: boolean
  ): Promise<ApiResponse<Employee[]>> {
    return request.patch<Employee[]>(`${this.basePath}/batch-status`, {
      ids,
      active
    });
  }

  // 导出员工数据（扩展功能）
  async exportEmployees(params?: EmployeePageParams): Promise<ApiResponse<Blob>> {
    return request.get<Blob>(`${this.basePath}/export`, {
      ...params,
      responseType: 'blob'
    });
  }
}

// 导出人才 API 实例
export const talentAPI = new TalentAPI();
