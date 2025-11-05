/**
 * 全系统通用的常量配置
 * 这些常量在所有 Portal 中保持一致
 */

/**
 * 超时时间配置（毫秒）
 */
export const TIMEOUTS = {
  // 短超时 - 用于快速操作
  short: 1000,
  
  // 中等超时 - 用于一般操作
  medium: 5000,
  
  // 长超时 - 用于网络请求
  long: 10000,
  
  // 网络超时 - 用于网络相关操作
  network: 30000,
  
  // 页面加载超时
  pageLoad: 30000,
  
  // 元素等待超时
  elementWait: 10000,
  
  // 对话框等待超时
  dialogWait: 5000,
  
  // 表格数据加载超时
  tableLoad: 10000,
  
  // 下拉菜单等待超时
  dropdownWait: 5000
} as const;

/**
 * 测试数据相关常量
 */
export const TEST_DATA = {
  // 测试数据前缀
  prefix: 'test_',
  
  // 测试数据后缀长度
  suffixLength: 10,
  
  // 测试数据清理超时
  cleanupTimeout: 15000,
  
  // 批量清理间隔
  batchCleanupInterval: 500
} as const;

/**
 * 页面相关常量
 */
export const PAGE_CONFIG = {
  // 默认页面大小
  defaultPageSize: 10,
  
  // 最大页面大小
  maxPageSize: 100,
  
  // 分页按钮数量
  paginationButtonCount: 7
} as const;

/**
 * 表单验证相关常量
 */
export const VALIDATION = {
  // 必填字段错误消息
  required: {
    entityName: 'Please enter entity name',
    serviceTypeName: 'Please enter service type name',
    entityType: 'Please select entity type',
    serviceType: 'Please select service type'
  },
  
  // 长度限制
  maxLength: {
    name: 100,
    description: 500
  },
  
  // 最小长度
  minLength: {
    name: 1
  }
} as const;

/**
 * 状态相关常量
 */
export const STATUS = {
  // 通用状态
  active: 'Active',
  inactive: 'Inactive',
  
  // 实体类型
  entityTypes: ['Client', 'Vendor', 'Partner'],
  
  // 服务类型
  serviceTypes: ['EOR', 'PEO', 'Payroll']
} as const;

/**
 * 错误消息常量
 */
export const ERROR_MESSAGES = {
  // 网络错误
  networkError: 'Network error occurred',
  
  // 权限错误
  permissionError: 'Permission denied',
  
  // 数据不存在
  notFound: 'Data not found',
  
  // 重复数据
  duplicate: 'Duplicate data exists',
  
  // 验证失败
  validationFailed: 'Validation failed'
} as const;

/**
 * 成功消息常量
 */
export const SUCCESS_MESSAGES = {
  // 创建成功
  created: 'Created successfully',
  
  // 更新成功
  updated: 'Updated successfully',
  
  // 删除成功
  deleted: 'Deleted successfully',
  
  // 激活成功
  activated: 'Activated successfully',
  
  // 停用成功
  deactivated: 'Deactivated successfully'
} as const;

/**
 * Portal 类型定义
 */
export type Portal = 'admin' | 'client' | 'service' | 'talent';

/**
 * 获取 Portal 特定的配置
 */
export function getPortalConfig(portal: Portal) {
  return {
    timeout: TIMEOUTS,
    testData: TEST_DATA,
    validation: VALIDATION,
    status: STATUS
  };
}
