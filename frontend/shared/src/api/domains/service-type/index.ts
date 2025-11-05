import request from '../../core/request';
import type { ApiResponse, PaginationData } from '../../../types';
import type {
  ServiceTypeOutput,
  ServiceTypePageOutput,
  CreateServiceTypeInput,
  UpdateServiceTypeInput,
  ServiceTypePageParams,
  ServiceType
} from '../../../types';

/**
 * 服务类型 API 类
 * 对应后端 ServiceTypeController 的所有端点
 */
class ServiceTypeAPI {
  private readonly basePath = '/v1/service-types';

  /**
   * 分页查询服务类型
   * GET /service-types
   * @param params 分页查询参数
   * @returns 分页响应数据
   */
  async getServiceTypePage(
    params: ServiceTypePageParams = {}
  ): Promise<ApiResponse<PaginationData<ServiceTypePageOutput>>> {
    return request.get<PaginationData<ServiceTypePageOutput>>(this.basePath, params);
  }

  /**
   * 根据ID获取服务类型
   * GET /service-types/{id}
   * @param id 服务类型ID
   * @returns 服务类型详情
   */
  async getServiceTypeById(id: string): Promise<ApiResponse<ServiceTypeOutput>> {
    return request.get<ServiceTypeOutput>(`${this.basePath}/${id}`);
  }

  /**
   * 创建服务类型
   * POST /service-types
   * @param data 创建服务类型的输入数据
   * @returns 创建的服务类型
   */
  async createServiceType(data: CreateServiceTypeInput): Promise<ApiResponse<ServiceTypeOutput>> {
    return request.post<ServiceTypeOutput>(this.basePath, data);
  }

  /**
   * 更新服务类型
   * PUT /service-types/{id}
   * @param id 服务类型ID
   * @param data 更新服务类型的输入数据
   * @returns 更新后的服务类型
   */
  async updateServiceType(
    id: string,
    data: UpdateServiceTypeInput
  ): Promise<ApiResponse<ServiceTypeOutput>> {
    return request.put<ServiceTypeOutput>(`${this.basePath}/${id}`, data);
  }

  /**
   * 删除服务类型
   * DELETE /service-types/{id}
   * @param id 服务类型ID
   * @returns 删除操作结果
   */
  async deleteServiceType(id: string): Promise<ApiResponse<void>> {
    return request.delete<void>(`${this.basePath}/${id}`);
  }

  /**
   * 获取激活的服务类型列表（分页）
   * GET /service-types/active
   * @param page 页码（从0开始）
   * @param size 页面大小
   * @returns 激活的服务类型分页数据
   */
  async getActiveServiceTypes(
    page: number = 0,
    size: number = 20
  ): Promise<ApiResponse<PaginationData<ServiceTypePageOutput>>> {
    return request.get<PaginationData<ServiceTypePageOutput>>(`${this.basePath}/active`, {
      page,
      size
    });
  }

  /**
   * 激活服务类型
   * PUT /service-types/{id}/activate
   * @param id 服务类型ID
   * @returns 激活后的服务类型信息
   */
  async activateServiceType(id: string): Promise<ApiResponse<ServiceTypeOutput>> {
    return request.put<ServiceTypeOutput>(`${this.basePath}/${id}/activate`);
  }

  /**
   * 停用服务类型
   * PUT /service-types/{id}/deactivate
   * @param id 服务类型ID
   * @returns 停用后的服务类型信息
   */
  async deactivateServiceType(id: string): Promise<ApiResponse<ServiceTypeOutput>> {
    return request.put<ServiceTypeOutput>(`${this.basePath}/${id}/deactivate`);
  }
}

// 创建 API 实例
export const serviceTypeAPI = new ServiceTypeAPI();
