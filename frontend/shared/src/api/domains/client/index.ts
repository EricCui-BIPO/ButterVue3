import request from '../../core/request';
import type { ApiResponse, PaginationData } from '@I0/shared/types';
import type {
  Client,
  CreateClientRequest,
  UpdateClientRequest,
  ClientSearchParams,
  LocationOutput
} from '@I0/shared/types';

// 客户 API 类
class ClientAPI {
  private readonly basePath = '/v1/clients';

  // 分页查询客户 - 使用标准化分页方法
  async getClients(params: ClientSearchParams = {}) {
    return request.get<PaginationData<Client>>(this.basePath, params);
  }

  // 获取所有客户
  async getAllClients(): Promise<ApiResponse<Client[]>> {
    return request.get<Client[]>(`${this.basePath}`);
  }

  // 根据ID获取客户
  async getClientById(id: string): Promise<ApiResponse<Client>> {
    return request.get<Client>(`${this.basePath}/${id}`);
  }

  // 创建客户
  async createClient(data: CreateClientRequest): Promise<ApiResponse<Client>> {
    return request.post<Client>(`${this.basePath}`, data);
  }

  // 更新客户
  async updateClient(id: string, data: UpdateClientRequest): Promise<ApiResponse<Client>> {
    return request.put<Client>(`${this.basePath}/${id}`, data);
  }

  // 激活客户
  async activateClient(id: string): Promise<ApiResponse<Client>> {
    return request.put<Client>(`${this.basePath}/${id}/activate`);
  }

  // 停用客户
  async deactivateClient(id: string): Promise<ApiResponse<Client>> {
    return request.put<Client>(`${this.basePath}/${id}/deactivate`);
  }

  // 删除客户
  async deleteClient(id: string): Promise<ApiResponse<void>> {
    return request.delete<void>(`${this.basePath}/${id}`);
  }

  // 获取激活状态的客户
  async getActiveClients(): Promise<ApiResponse<Client[]>> {
    return request.get<Client[]>(`${this.basePath}`, {
      activeOnly: true
    });
  }

  // 根据关键字搜索客户
  async searchClientsByKeyword(q: string): Promise<ApiResponse<Client[]>> {
    return request.get<Client[]>(`${this.basePath}`, {
      q,
      activeOnly: true
    });
  }

  // 根据位置ID获取客户
  async getClientsByLocation(locationId: string): Promise<ApiResponse<Client[]>> {
    return request.get<Client[]>(`${this.basePath}`, {
      locationId,
      activeOnly: true
    });
  }

  // 检查客户名称是否唯一
  async checkClientNameUnique(name: string, excludeId?: string): Promise<ApiResponse<boolean>> {
    const params: any = { name };
    if (excludeId) {
      params.excludeId = excludeId;
    }
    return request.get<boolean>(`${this.basePath}/check-name-unique`, params);
  }

  // 检查客户代码是否唯一
  async checkClientCodeUnique(code: string, excludeId?: string): Promise<ApiResponse<boolean>> {
    const params: any = { code };
    if (excludeId) {
      params.excludeId = excludeId;
    }
    return request.get<boolean>(`${this.basePath}/check-code-unique`, params);
  }

  // 获取客户端专用位置数据
  async getClientLocations(): Promise<ApiResponse<LocationOutput[]>> {
    return request.get<LocationOutput[]>(`${this.basePath}/locations`);
  }
}

// 导出客户 API 实例
export const clientAPI = new ClientAPI();
