/**
 * 全系统通用的 Element Plus 选择器
 * 这些选择器在所有 Portal 中保持一致
 */
export const COMMON_SELECTORS = {
  // 表格相关
  table: '.i0-table',
  elTable: '.el-table',
  tableRows: 'tbody tr',
  tableEmpty: '.el-table__empty-block',
  
  // 对话框相关
  dialog: '.el-dialog',
  dialogTitle: '.el-dialog__title',
  dialogClose: '.el-dialog__close',
  
  // 下拉菜单相关
  dropdown: '.el-dropdown-menu',
  dropdownItem: '.el-dropdown-menu__item',
  dropdownTrigger: '.action-dropdown-trigger',
  
  // 表单相关
  form: '.el-form',
  formItem: '.el-form-item',
  formError: '.el-form-item__error',
  
  // 按钮相关
  button: '.el-button',
  
  // 选择器相关
  select: '.el-select',
  selectDropdown: '.el-select-dropdown',
  selectItem: '.el-select-dropdown__item',
  
  // 输入框相关
  input: '.el-input__inner',
  textarea: '.el-textarea__inner',
  
  // 标签相关
  tag: '.el-tag',
  
  // 分页相关
  pagination: '.el-pagination',
  paginationNext: '.el-pagination .btn-next',
  paginationPrev: '.el-pagination .btn-prev',
  paginationNumbers: '.el-pagination .number',
  
  // 消息相关
  message: '.el-message',
  messageError: '.el-message--error',
  messageSuccess: '.el-message--success',
  
  // 确认对话框相关
  messageBox: '.el-message-box',
  
  // 空状态相关
  empty: '.el-empty',
  emptyDescription: '.el-empty__description',
  
  // 菜单相关
  menu: '.el-menu',
  menuItem: '.el-menu-item',
  
  // 加载相关
  loading: '.el-loading-mask',
  
  // 工具提示相关
  tooltip: '.el-tooltip',
  
  // 弹窗相关
  popover: '.el-popover',
  popper: '.el-popper'
} as const;

/**
 * Portal 特定的选择器
 * 不同 Portal 可能有不同的选择器
 */
export const PORTAL_SELECTORS = {
  admin: {
    // Admin Portal 特定选择器
    entityTable: '.i0-table',
    serviceTypeTable: '.i0-table'
  },
  client: {
    // Client Portal 特定选择器（未来）
  },
  service: {
    // Service Portal 特定选择器（未来）
  },
  talent: {
    // Talent Portal 特定选择器（未来）
  }
} as const;

/**
 * 获取 Portal 特定的选择器
 */
export function getPortalSelectors(portal: keyof typeof PORTAL_SELECTORS) {
  return PORTAL_SELECTORS[portal];
}

/**
 * 选择器类型定义
 */
export type CommonSelector = typeof COMMON_SELECTORS[keyof typeof COMMON_SELECTORS];
export type PortalSelector = typeof PORTAL_SELECTORS[keyof typeof PORTAL_SELECTORS];
