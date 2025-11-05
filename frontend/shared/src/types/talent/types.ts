import type { ApiResponse, PaginationParams, PaginationResponse } from '../request/types'

// 员工信息接口
export interface Employee {
  id: string
  name: string
  employeeNumber: string
  workLocationId?: string
  workLocationName?: string
  workLocationType?: string
  nationalityId?: string
  nationalityName?: string
  nationalityIsoCode?: string
  email: string
  department?: string
  position?: string
  joinDate?: string
  leaveDate?: string
  dataLocation?: string
  status?: string
  clientId?: string
  active: boolean
  createdAt: string
  updatedAt: string
  creator?: string
  updater?: string
}

// 创建员工请求
export interface CreateEmployeeRequest {
  name: string
  employeeNumber: string
  workLocationId: string
  nationalityId: string
  email: string
  department: string
  position: string
  joinDate: string
  dataLocation: string
  clientId?: string
}

// 更新员工请求
export interface UpdateEmployeeRequest {
  id: string
  name?: string
  employeeNumber?: string
  workLocationId?: string
  nationalityId?: string
  email?: string
  department?: string
  position?: string
  joinDate?: string
  leaveDate?: string
  dataLocation?: string
  clientId?: string
  active?: boolean
}

// 员工分页查询参数
export interface EmployeePageParams extends PaginationParams {
  keyword?: string          // 搜索关键词
  department?: string       // 部门筛选
  workLocation?: string     // 工作地点筛选
  nationality?: string      // 国籍筛选
  status?: string          // 员工状态筛选
  dataLocation?: string    // 数据存储位置筛选
  clientId?: string        // 客户ID筛选
  activeOnly?: boolean     // 仅显示激活状态
  page?: number           // 页码，从0开始
  size?: number           // 每页大小
  sortBy?: string         // 排序字段
  sortOrder?: 'asc' | 'desc'  // 排序方向
}

// 员工页面参数类型别名
export type EmployeeSearchParams = EmployeePageParams

// 员工表单数据（用于前端表单）
export interface EmployeeFormData {
  name: string
  employeeNumber: string
  workLocationId: string
  nationalityId: string
  email: string
  department: string
  position: string
  joinDate: string
  dataLocation: string
  clientId?: string
}

// 员工状态选项
export interface EmployeeStatusOption {
  label: string
  value: string
  type: 'success' | 'info' | 'warning' | 'danger'
}

// 数据位置选项
export interface DataLocationOption {
  label: string
  value: string
  description: string
  complianceNotice: string
}

// 部门选项（Mock数据）
export interface DepartmentOption {
  id: string
  name: string
}

// API 响应类型
export type EmployeeResponse = ApiResponse<Employee>
export type EmployeeListResponse = ApiResponse<Employee[]>
export type EmployeePageResponse = PaginationResponse<Employee>

// 员工操作相关常量
export const EMPLOYEE_STATUS_OPTIONS: EmployeeStatusOption[] = [
  { label: '在职', value: 'ACTIVE', type: 'success' },
  { label: '离职', value: 'INACTIVE', type: 'info' },
  { label: '休假', value: 'ON_LEAVE', type: 'warning' }
]

export const DATA_LOCATION_OPTIONS: DataLocationOption[] = [
  {
    label: '中国宁夏',
    value: 'NINGXIA',
    description: '中国宁夏数据中心',
    complianceNotice: '员工数据将存储在中国宁夏数据中心，需符合中国个人信息保护法'
  },
  {
    label: '新加坡',
    value: 'SINGAPORE',
    description: '新加坡数据中心',
    complianceNotice: '员工数据将存储在新加坡数据中心，需符合新加坡个人数据保护法(PDPA)'
  },
  {
    label: '德国',
    value: 'GERMANY',
    description: '德国法兰克福数据中心',
    complianceNotice: '员工数据将存储在德国数据中心，需符合欧盟通用数据保护条例(GDPR)'
  }
]

// Mock 部门数据
export const MOCK_DEPARTMENTS: DepartmentOption[] = [
  { id: 'IT', name: 'IT部' },
  { id: 'HR', name: '人力资源部' },
  { id: 'Finance', name: '财务部' },
  { id: 'Operations', name: '运营部' },
  { id: 'Sales', name: '销售部' },
  { id: 'Marketing', name: '市场部' },
  { id: 'Administration', name: '行政部' },
  { id: 'Legal', name: '法务部' }
]

// 员工表单验证规则常量
export const EMPLOYEE_FORM_RULES = {
  name: [
    { required: true, message: '请输入员工姓名', trigger: 'blur' },
    { min: 1, max: 100, message: '员工姓名长度应在 1 到 100 个字符', trigger: 'blur' }
  ],
  employeeNumber: [
    { required: true, message: '请输入员工工号', trigger: 'blur' },
    { min: 1, max: 50, message: '员工工号长度应在 1 到 50 个字符', trigger: 'blur' },
    { pattern: /^[A-Z0-9_-]+$/, message: '员工工号只能包含大写字母、数字、下划线和连字符', trigger: 'blur' }
  ],
  workLocationId: [
    { required: true, message: '请选择工作地点', trigger: 'change' }
  ],
  nationalityId: [
    { required: true, message: '请选择国籍', trigger: 'change' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址格式', trigger: 'blur' },
    { max: 100, message: '邮箱地址不能超过 100 个字符', trigger: 'blur' }
  ],
  department: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ],
  position: [
    { required: true, message: '请输入职位', trigger: 'blur' },
    { min: 1, max: 100, message: '职位长度应在 1 到 100 个字符', trigger: 'blur' }
  ],
  joinDate: [
    { required: true, message: '请选择入职日期', trigger: 'change' }
  ],
  dataLocation: [
    { required: true, message: '请选择数据存储位置', trigger: 'change' }
  ]
}

// 默认表单数据
export const DEFAULT_EMPLOYEE_FORM_DATA: EmployeeFormData = {
  name: '',
  employeeNumber: '',
  workLocationId: '',
  nationalityId: '',
  email: '',
  department: '',
  position: '',
  joinDate: '',
  dataLocation: 'NINGXIA',
  clientId: undefined
}

// 默认员工搜索参数
export const DEFAULT_EMPLOYEE_SEARCH_PARAMS: EmployeeSearchParams = {
  page: 0,
  size: 20,
  sortBy: 'createdAt',
  sortOrder: 'desc'
}