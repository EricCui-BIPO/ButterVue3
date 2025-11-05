import { 
  ServiceType,
  ServiceTypeDisplayNames, 
  ServiceTypeDescriptions, 
  ServiceTypeTagTypes,
  ServiceTypeCodes
} from './types'
import type { 
  ServiceTypeOutput, 
  ServiceTypeOption,
  ServiceTypeUtils
} from './types'

/**
 * 检查是否为EOR服务类型
 * @param serviceType 服务类型
 * @returns 是否为EOR类型
 */
export const isEOR = (serviceType: ServiceType): boolean => {
  return serviceType === ServiceType.EOR
}

/**
 * 检查是否为外包服务类型
 * @param serviceType 服务类型
 * @returns 是否为外包服务类型
 */
export const isOutsourcingService = (serviceType: ServiceType): boolean => {
  return serviceType === ServiceType.EOR || serviceType === ServiceType.GPO
}

/**
 * 检查是否为管理服务类型
 * @param serviceType 服务类型
 * @returns 是否为管理服务类型
 */
export const isManagementService = (serviceType: ServiceType): boolean => {
  return serviceType === ServiceType.EOR || serviceType === ServiceType.CONTRACTOR
}

/**
 * 获取服务类型的显示名称
 * @param serviceType 服务类型
 * @returns 显示名称
 */
export const getServiceTypeDisplayName = (serviceType: ServiceType): string => {
  return ServiceTypeDisplayNames[serviceType] || serviceType
}

/**
 * 获取服务类型的描述
 * @param serviceType 服务类型
 * @returns 描述信息
 */
export const getServiceTypeDescription = (serviceType: ServiceType): string => {
  return ServiceTypeDescriptions[serviceType] || ''
}

/**
 * 获取服务类型的标签类型（用于UI展示）
 * @param serviceType 服务类型
 * @returns 标签类型
 */
export const getServiceTypeTagType = (serviceType: ServiceType): string => {
  return ServiceTypeTagTypes[serviceType] || 'default'
}

/**
 * 获取服务类型的代码
 * @param serviceType 服务类型
 * @returns 服务类型代码
 */
export const getServiceTypeCode = (serviceType: ServiceType): string => {
  return ServiceTypeCodes[serviceType] || serviceType
}

/**
 * 将ServiceTypeOutput转换为ServiceTypeOption
 * @param serviceTypeOutput 服务类型输出对象
 * @returns 服务类型选项
 */
export const toServiceTypeOption = (serviceTypeOutput: ServiceTypeOutput): ServiceTypeOption => {
  return {
    value: serviceTypeOutput.code as ServiceType,
    label: serviceTypeOutput.displayName,
    code: serviceTypeOutput.code,
    description: serviceTypeOutput.description,
    disabled: !serviceTypeOutput.active
  }
}

/**
 * 将ServiceTypeOutput数组转换为ServiceTypeOption数组
 * @param serviceTypeOutputs 服务类型输出对象数组
 * @returns 服务类型选项数组
 */
export const toServiceTypeOptions = (serviceTypeOutputs: ServiceTypeOutput[]): ServiceTypeOption[] => {
  return serviceTypeOutputs.map(toServiceTypeOption)
}

/**
 * 获取所有服务类型选项
 * @param includeDisabled 是否包含禁用的选项
 * @returns 服务类型选项数组
 */
export const getAllServiceTypeOptions = (includeDisabled: boolean = true): ServiceTypeOption[] => {
  return Object.values(ServiceType).map(serviceType => ({
    value: serviceType,
    label: getServiceTypeDisplayName(serviceType),
    code: getServiceTypeCode(serviceType),
    description: getServiceTypeDescription(serviceType),
    disabled: false
  }))
}

/**
 * 根据服务类型代码查找服务类型枚举
 * @param code 服务类型代码
 * @returns 服务类型枚举或undefined
 */
export const findServiceTypeByCode = (code: string): ServiceType | undefined => {
  return Object.values(ServiceType).find(serviceType => 
    getServiceTypeCode(serviceType) === code
  )
}

/**
 * 根据显示名称查找服务类型枚举
 * @param displayName 显示名称
 * @returns 服务类型枚举或undefined
 */
export const findServiceTypeByDisplayName = (displayName: string): ServiceType | undefined => {
  return Object.values(ServiceType).find(serviceType => 
    getServiceTypeDisplayName(serviceType) === displayName
  )
}

/**
 * 验证服务类型是否有效
 * @param serviceType 服务类型
 * @returns 是否有效
 */
export const isValidServiceType = (serviceType: string): serviceType is ServiceType => {
  return Object.values(ServiceType).includes(serviceType as ServiceType)
}

/**
 * 过滤激活的服务类型选项
 * @param options 服务类型选项数组
 * @returns 激活的服务类型选项数组
 */
export const filterActiveServiceTypeOptions = (options: ServiceTypeOption[]): ServiceTypeOption[] => {
  return options.filter(option => !option.disabled)
}

/**
 * 过滤外包服务类型选项
 * @param options 服务类型选项数组
 * @returns 外包服务类型选项数组
 */
export const filterOutsourcingServiceTypeOptions = (options: ServiceTypeOption[]): ServiceTypeOption[] => {
  return options.filter(option => isOutsourcingService(option.value))
}

/**
 * 过滤管理服务类型选项
 * @param options 服务类型选项数组
 * @returns 管理服务类型选项数组
 */
export const filterManagementServiceTypeOptions = (options: ServiceTypeOption[]): ServiceTypeOption[] => {
  return options.filter(option => isManagementService(option.value))
}

/**
 * 服务类型工具函数集合
 */
export const serviceTypeUtils: ServiceTypeUtils = {
  isEOR,
  isOutsourcingService,
  isManagementService,
  getDisplayName: getServiceTypeDisplayName,
  getDescription: getServiceTypeDescription,
  getTagType: getServiceTypeTagType
}

/**
 * 默认导出所有工具函数
 */
export default {
  isEOR,
  isOutsourcingService,
  isManagementService,
  getServiceTypeDisplayName,
  getServiceTypeDescription,
  getServiceTypeTagType,
  getServiceTypeCode,
  toServiceTypeOption,
  toServiceTypeOptions,
  getAllServiceTypeOptions,
  findServiceTypeByCode,
  findServiceTypeByDisplayName,
  isValidServiceType,
  filterActiveServiceTypeOptions,
  filterOutsourcingServiceTypeOptions,
  filterManagementServiceTypeOptions,
  serviceTypeUtils
}