import request from '../../core/request';
import type { ApiResponse, PaginationData } from '@I0/shared/types';
import type {
  Entity,
  CreateEntityRequest,
  UpdateEntityRequest,
  EntityPageParams,
  EntityType
} from '@I0/shared/types';

// 实体 API 类
class EntityAPI {
  private readonly basePath = '/v1/entities';

  // 分页查询实体 - 使用标准化分页方法
  async getEntities(params: EntityPageParams = {}) {
    return request.get<PaginationData<Entity>>(this.basePath, params);
  }

  // 获取所有实体
  async getAllEntities(): Promise<ApiResponse<Entity[]>> {
    return request.get<Entity[]>(`${this.basePath}`);
  }

  // 根据ID获取实体
  async getEntityById(id: string): Promise<ApiResponse<Entity>> {
    return request.get<Entity>(`${this.basePath}/${id}`);
  }

  // 创建实体
  async createEntity(data: CreateEntityRequest): Promise<ApiResponse<Entity>> {
    return request.post<Entity>(`${this.basePath}`, data);
  }

  // 更新实体
  async updateEntity(id: string, data: UpdateEntityRequest): Promise<ApiResponse<Entity>> {
    return request.put<Entity>(`${this.basePath}/${id}`, data);
  }

  // 激活实体
  async activateEntity(id: string): Promise<ApiResponse<Entity>> {
    return request.put<Entity>(`${this.basePath}/${id}/activate`);
  }

  // 停用实体
  async deactivateEntity(id: string): Promise<ApiResponse<Entity>> {
    return request.put<Entity>(`${this.basePath}/${id}/deactivate`);
  }

  // 删除实体
  async deleteEntity(id: string): Promise<ApiResponse<void>> {
    return request.delete<void>(`${this.basePath}/${id}`);
  }

  // 获取激活的实体
  async getActiveEntities(entityType?: EntityType): Promise<ApiResponse<Entity[]>> {
    const url = entityType
      ? `${this.basePath}/active?entityType=${entityType}`
      : `${this.basePath}/active`;
    return request.get<Entity[]>(url);
  }

  // 获取所有实体类型
  async getEntityTypes(): Promise<ApiResponse<EntityType[]>> {
    return request.get<EntityType[]>(`${this.basePath}/types`);
  }
}

// 创建 API 实例
export const entityAPI = new EntityAPI();
