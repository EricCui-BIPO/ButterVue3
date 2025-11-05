import type { ApiResponse, PaginationData } from '../request/types'

/**
 * 地理位置类型枚举
 */
export enum LocationType {
  CONTINENT = 'CONTINENT',
  COUNTRY = 'COUNTRY', 
  PROVINCE = 'PROVINCE',
  CITY = 'CITY'
}

/**
 * 地理位置类型显示名称映射
 */
export const LocationTypeDisplayNames: Record<LocationType, string> = {
  [LocationType.CONTINENT]: '大洲',
  [LocationType.COUNTRY]: '国家',
  [LocationType.PROVINCE]: '省/州',
  [LocationType.CITY]: '市'
}

/**
 * 地理位置类型标签类型映射
 */
export const LocationTagTypes: Record<LocationType, string> = {
  [LocationType.CONTINENT]: 'success',
  [LocationType.COUNTRY]: 'primary', 
  [LocationType.PROVINCE]: 'warning',
  [LocationType.CITY]: 'info'
}

/**
 * 地理位置类型选项接口
 */
export interface LocationTypeOption {
  value: LocationType
  label: string
  chineseName: string
}

// 地理位置信息
export interface LocationOutput {
  id: string
  name: string
  locationType: LocationType
  parentId?: string
  parentName?: string
  level: number
  active: boolean
  isoCode?: string
  createdAt: string
  updatedAt: string
}

// 创建地理位置输入
export interface CreateLocationInput {
  name: string
  locationType: LocationType
  parentId?: string
  isoCode?: string
  description?: string
  sortOrder?: number
}

// 更新地理位置输入
export interface UpdateLocationInput {
  name?: string
  locationType?: LocationType
  parentId?: string
  active?: boolean
  description?: string
  sortOrder?: number
}

// 地理位置分页查询输入
export interface LocationPageInput {
  page?: number
  size?: number
  name?: string
  type?: string
  parentId?: string
  activeOnly?: boolean
}

// API响应类型别名
export type LocationPageResponse = ApiResponse<PaginationData<LocationOutput>>
export type LocationResponse = ApiResponse<LocationOutput>
export type LocationListResponse = ApiResponse<LocationOutput[]>