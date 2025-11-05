import type { AxiosRequestConfig } from 'axios'

// API 常量配置
export const API_CONSTANTS = {
  // 版本
  VERSION: 'v1',

  // 基础路径
  BASE_PATH: '/api',

  // 超时设置
  TIMEOUT: {
    DEFAULT: 10000,
    UPLOAD: 30000,
    DOWNLOAD: 60000
  },

  // 重试设置
  RETRY: {
    MAX_RETRIES: 3,
    BASE_DELAY: 1000,
    MAX_DELAY: 10000
  },
}

// HTTP 状态码
export const HTTP_STATUS = {
  OK: 200,
  CREATED: 201,
  NO_CONTENT: 204,
  BAD_REQUEST: 400,
  UNAUTHORIZED: 401,
  FORBIDDEN: 403,
  NOT_FOUND: 404,
  INTERNAL_SERVER_ERROR: 500
}

// 错误类型
export const ERROR_TYPES = {
  NETWORK_ERROR: 'network_error',
  UNAUTHORIZED: 'unauthorized',
  FORBIDDEN: 'forbidden',
  NOT_FOUND: 'not_found',
  SERVER_ERROR: 'server_error',
  BUSINESS_ERROR: 'business_error',
  HTTP_ERROR: 'http_error',
  REQUEST_ERROR: 'request_error',
  VALIDATION_ERROR: 'validation_error'
} as const

// 请求方法
export const HTTP_METHODS = {
  GET: 'GET',
  POST: 'POST',
  PUT: 'PUT',
  DELETE: 'DELETE',
  PATCH: 'PATCH'
} as const

// 基础API响应结构
export interface ApiResponse<T = any> {
  errorCode: string | null     // 错误代码，成功时为null
  errorMessage: string | null  // 错误消息，成功时为null
  errParams: any[] | null      // 错误参数，成功时为null
  data: T                      // 响应数据
  success: boolean             // 请求是否成功
}

// 分页参数（API传参格式）
export interface PaginationParams {
  page?: number        // 页码，从0开始（与后端API保持一致）
  size?: number        // 每页大小，默认20（与后端API保持一致）
  sortBy?: string      // 排序字段
  sortOrder?: 'asc' | 'desc'  // 排序方向
}

// 分页响应数据结构
export interface PaginationData<T> {
  content: T[]         // 当前页数据列表
  pagination?: {       // 分页信息
    page: number
    pageSize: number
    total: number
    totalPages: number
  }
}

// 分页响应类型（组合类型）
export type PaginationResponse<T> = ApiResponse<PaginationData<T>>

// API 错误类型
export interface ApiError {
  type: (typeof ERROR_TYPES)[keyof typeof ERROR_TYPES]
  details: string
  code?: number | string    // 支持 HTTP 状态码和后端错误码字符串
  field?: string
  timestamp?: string
  requestId?: string
}

// 请求配置扩展
export interface RequestConfig extends AxiosRequestConfig {
  loading?: boolean           // 是否显示加载状态
  showError?: boolean        // 是否显示控制台错误日志（默认：true）
  showErrorToast?: boolean   // 是否自动显示错误Toast（默认：true）
  customErrorHandler?: (error: ApiError) => void  // 自定义错误处理器
}