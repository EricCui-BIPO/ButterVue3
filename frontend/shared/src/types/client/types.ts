import type { ApiResponse, PaginationParams, PaginationResponse } from '../request/types'

// 客户信息接口
export interface Client {
  id: string
  name: string
  code: string
  aliasName?: string
  locationId?: string
  locationName?: string
  description?: string
  active: boolean
  createdAt: string
  updatedAt: string
  version: number
  displayName: string
}

// 创建客户请求
export interface CreateClientRequest {
  name: string
  code: string
  aliasName?: string
  locationId?: string
  description?: string
  active?: boolean
}

// 更新客户请求
export interface UpdateClientRequest {
  name?: string
  code?: string
  aliasName?: string
  locationId?: string
  description?: string
  active?: boolean
}

// 客户分页查询参数
export interface ClientSearchParams extends PaginationParams {
  q?: string              // 关键字搜索（搜索名称、代码、别名）
  locationId?: string      // 位置ID筛选
  activeOnly?: boolean     // 仅显示激活状态
  page?: number           // 页码，从0开始
  size?: number           // 每页大小
  sortBy?: string         // 排序字段
  sortOrder?: 'asc' | 'desc'  // 排序方向
}

// 客户页面参数类型别名
export type ClientPageParams = ClientSearchParams

// 客户表单数据（用于前端表单）
export interface ClientFormData {
  name: string
  code: string
  aliasName: string
  locationId: string
  description: string
}

// 客户状态选项
export interface ClientStatusOption {
  label: string
  value: boolean | undefined
  type: 'success' | 'info'
}

// API 响应类型
export type ClientResponse = ApiResponse<Client>
export type ClientListResponse = ApiResponse<Client[]>
export type ClientPageResponse = PaginationResponse<Client>

// 客户操作相关常量
export const CLIENT_STATUS_OPTIONS: ClientStatusOption[] = [
  { label: 'All', value: undefined, type: 'info' },
  { label: 'Active', value: true, type: 'success' },
  { label: 'Inactive', value: false, type: 'info' }
]

// 客户表单验证规则常量
export const CLIENT_FORM_RULES = {
  name: [
    { required: true, message: '请输入客户名称', trigger: 'blur' },
    { min: 1, max: 100, message: '客户名称长度应在 1 到 100 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入客户代码', trigger: 'blur' },
    { min: 1, max: 50, message: '客户代码长度应在 1 到 50 个字符', trigger: 'blur' },
    { pattern: /^[A-Z0-9_-]+$/, message: '客户代码只能包含大写字母、数字、下划线和连字符', trigger: 'blur' }
  ],
  locationId: [
    { required: true, message: '请选择位置', trigger: 'change' }
  ],
  aliasName: [
    { max: 100, message: '别名长度不能超过 100 个字符', trigger: 'blur' }
  ],
  description: [
    { max: 500, message: '描述长度不能超过 500 个字符', trigger: 'blur' }
  ]
}

// 默认表单数据
export const DEFAULT_CLIENT_FORM_DATA: ClientFormData = {
  name: '',
  code: '',
  aliasName: '',
  locationId: '',
  description: ''
}

// 默认客户搜索参数
export const DEFAULT_CLIENT_SEARCH_PARAMS: ClientSearchParams = {
  page: 0,
  size: 20,
  sortBy: 'createdAt',
  sortOrder: 'desc'
}