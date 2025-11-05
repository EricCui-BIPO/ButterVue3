import type { ApiResponse, PaginationParams, PaginationData } from '../request/types'

/**
 * 服务类型枚举
 * 对应后端 ServiceType enum
 */
export enum ServiceType {
  EOR = 'EOR',
  GPO = 'GPO', 
  CONTRACTOR = 'CONTRACTOR',
  SELF = 'SELF'
}

/**
 * 服务类型显示名称映射
 */
export const ServiceTypeDisplayNames: Record<ServiceType, string> = {
  [ServiceType.EOR]: 'EOR',
  [ServiceType.GPO]: 'GPO',
  [ServiceType.CONTRACTOR]: 'Contractor',
  [ServiceType.SELF]: 'Self'
}

/**
 * 服务类型标签类型映射（用于UI展示）
 */
export const ServiceTypeTagTypes: Record<ServiceType, string> = {
  [ServiceType.EOR]: 'primary',
  [ServiceType.GPO]: 'success',
  [ServiceType.CONTRACTOR]: 'warning',
  [ServiceType.SELF]: 'info'
}

/**
 * 服务类型代码映射
 */
export const ServiceTypeCodes: Record<ServiceType, string> = {
  [ServiceType.EOR]: 'EOR',
  [ServiceType.GPO]: 'GPO',
  [ServiceType.CONTRACTOR]: 'CONTRACTOR',
  [ServiceType.SELF]: 'SELF'
}

/**
 * 服务类型描述映射
 */
export const ServiceTypeDescriptions: Record<ServiceType, string> = {
  [ServiceType.EOR]: 'Employer of Record',
  [ServiceType.GPO]: 'Global Payroll Outsourcing',
  [ServiceType.CONTRACTOR]: 'Independent Contractor',
  [ServiceType.SELF]: 'Self Employment'
}

/**
 * 服务类型输出接口
 * 对应后端 ServiceTypeOutput
 */
export interface ServiceTypeOutput {
  id: string
  name: string
  code: string
  displayName: string
  description?: string
  active: boolean
  outsourcingService: boolean
  managementService: boolean
  createdAt: string
  updatedAt: string
  version: number
}

/**
 * 服务类型页面输出接口
 * 对应后端 ServiceTypePageOutput（不包含version字段）
 */
export interface ServiceTypePageOutput {
  id: string
  name: string
  code: string
  displayName: string
  description?: string
  active: boolean
  outsourcingService: boolean
  managementService: boolean
  createdAt: string
  updatedAt: string
}

/**
 * 创建服务类型输入接口
 * 对应后端 CreateServiceTypeInput
 */
export interface CreateServiceTypeInput {
  name: string
  serviceType: ServiceType
  description?: string
  active?: boolean
}

/**
 * 更新服务类型输入接口
 * 对应后端 UpdateServiceTypeInput
 */
export interface UpdateServiceTypeInput {
  name?: string
  serviceType?: ServiceType
  description?: string
  active?: boolean
}

/**
 * 服务类型分页查询参数接口
 * 对应后端 ServiceTypePageInput
 */
export interface ServiceTypePageParams extends PaginationParams {
  serviceType?: ServiceType
  nameKeyword?: string
  activeOnly?: boolean
  page?: number        // 页码，从0开始
  size?: number        // 每页大小
  sortBy?: string      // 排序字段
  sortOrder?: 'asc' | 'desc'  // 排序方向
}

/**
 * 服务类型选项接口（用于下拉选择等UI组件）
 */
export interface ServiceTypeOption {
  value: ServiceType
  label: string
  code: string
  description?: string
  disabled?: boolean
}

/**
 * 服务类型工具函数类型
 */
export interface ServiceTypeUtils {
  isEOR: (serviceType: ServiceType) => boolean
  isOutsourcingService: (serviceType: ServiceType) => boolean
  isManagementService: (serviceType: ServiceType) => boolean
  getDisplayName: (serviceType: ServiceType) => string
  getDescription: (serviceType: ServiceType) => string
  getTagType: (serviceType: ServiceType) => string
}

// API响应类型别名
export type ServiceTypeResponse = ApiResponse<ServiceTypeOutput>
export type ServiceTypeListResponse = ApiResponse<ServiceTypeOutput[]>
export type ServiceTypePageResponse = ApiResponse<PaginationData<ServiceTypePageOutput>>
export type ServiceTypesResponse = ApiResponse<ServiceType[]>
export type ActiveServiceTypesResponse = ApiResponse<ServiceTypeOutput[]>