import type { ApiResponse, PaginationParams } from '../request/types'

// 实体类型枚举
export enum EntityType {
  BIPO_ENTITY = 'BIPO_ENTITY',
  CLIENT_ENTITY = 'CLIENT_ENTITY',
  VENDOR_ENTITY = 'VENDOR_ENTITY'
}

// 实体类型显示名称映射
export const EntityTypeDisplayNames: Record<EntityType, string> = {
  [EntityType.BIPO_ENTITY]: 'BIPO',
  [EntityType.CLIENT_ENTITY]: 'Client',
  [EntityType.VENDOR_ENTITY]: 'Vendor'
}

export const EntityTagTypes: Record<EntityType, string> = {
  [EntityType.BIPO_ENTITY]: 'primary',
  [EntityType.CLIENT_ENTITY]: 'success',
  [EntityType.VENDOR_ENTITY]: 'warning'
}

// 实体信息
export interface Entity {
  id: string
  name: string
  entityType: EntityType
  entityTypeDisplayName: string
  code?: string
  description?: string
  active: boolean
  createdAt: string
  updatedAt: string
  version: number
}

// 创建实体请求
export interface CreateEntityRequest {
  name: string
  entityType: EntityType
  code?: string
  description?: string
  active?: boolean
}

// 更新实体请求
export type UpdateEntityRequest = Partial<CreateEntityRequest>;

// 实体分页查询参数
export interface EntityPageParams extends PaginationParams {
  entityType?: EntityType
  nameKeyword?: string
  activeOnly?: boolean
  page?: number        // 页码，从0开始
  size?: number        // 每页大小
  sortBy?: string      // 排序字段
  sortOrder?: 'asc' | 'desc'  // 排序方向
}

// 导出类型别名
export type EntityResponse = ApiResponse<Entity>
export type EntityListResponse = ApiResponse<Entity[]>
export type EntityTypesResponse = ApiResponse<EntityType[]>