/**
 * 人才管理相关常量定义
 */

import {
  EMPLOYEE_STATUS_OPTIONS,
  DATA_LOCATION_OPTIONS,
  MOCK_DEPARTMENTS
} from './types'

export {
  EMPLOYEE_STATUS_OPTIONS,
  DATA_LOCATION_OPTIONS,
  MOCK_DEPARTMENTS
}

// 人才管理模块常量
export const TALENT_MODULE_CONSTANTS = {
  // 员工状态显示名称映射
  EmployeeStatusDisplayNames: {
    ACTIVE: '在职',
    INACTIVE: '离职',
    ON_LEAVE: '休假'
  },

  // 数据位置显示名称映射
  DataLocationDisplayNames: {
    NINGXIA: '中国宁夏',
    SINGAPORE: '新加坡',
    GERMANY: '德国'
  },

  // 默认分页大小
  DefaultPageSize: 20,
  MaxPageSize: 100,

  // 搜索防抖延迟（毫秒）
  SearchDebounceDelay: 300,

  // 表单验证规则
  ValidationRules: {
    NameMinLength: 1,
    NameMaxLength: 100,
    EmployeeNumberMaxLength: 50,
    EmailMaxLength: 100,
    PositionMaxLength: 100
  },

  // 文件上传限制
  FileUpload: {
    MaxFileSize: 5 * 1024 * 1024, // 5MB
    AllowedTypes: ['jpg', 'jpeg', 'png', 'pdf', 'doc', 'docx']
  }
}