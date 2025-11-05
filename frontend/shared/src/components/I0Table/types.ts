import type { VNode } from 'vue'

/**
 * I0Table 组件属性接口
 */
export interface I0TableProps {
  /** 表格数据 - 要显示的对象数组 */
  tableData: Record<string, any>[]

  /** 列配置 - 定义表格结构 */
  tableColumn: TableColumn[]

  /** 加载状态 */
  loading?: boolean

  /** 分页配置（新接口，内部管理分页状态） */
  pagination?: PaginationConfig

  /** 表格级别选项 */
  stripe?: boolean
  border?: boolean
  size?: 'small' | 'default' | 'large'
  highlightCurrentRow?: boolean
  emptyText?: string
}

/**
 * 列配置接口
 */
export interface TableColumn {
  /** 列显示名称 */
  name: string

  /** 数据字段映射 */
  prop: string

  /** 数据类型用于格式化 */
  type: 'string' | 'number' | 'date' | 'dateTime' | 'boolean' | 'currency'

  /** 自定义格式化函数（覆盖类型基础格式化） */
  formatter?: (value: any, row: Record<string, any>, column: TableColumn) => string

  /** 列宽度 */
  width?: string | number

  /** 最小列宽度 */
  minWidth?: string | number

  /** 固定位置 */
  fixed?: 'left' | 'right'

  /** 可排序配置 */
  sortable?: boolean

  /** 列对齐 */
  align?: 'left' | 'center' | 'right'

  /** 显示溢出工具提示 */
  showOverflowTooltip?: boolean

  /** 自定义类名 */
  className?: string

  /** 动态列槽配置 - true 时自动生成槽名，字符串时指定槽名 */
  slot?: string | boolean
}

/**
 * 分页配置接口（组件内部管理分页状态）
 */
export interface PaginationConfig {
  /** 项目总数 */
  total: number

  /** 当前页码（1-based，默认为 1） */
  currentPage?: number

  /** 当前每页大小（默认为 20） */
  pageSize?: number

  /** 页面大小选项 */
  pageSizes?: number[]

  /** 布局配置 */
  layout?: string

  /** 是否显示小分页 */
  small?: boolean
}

/**
 * 排序变更事件接口
 */
export interface SortChangeEvent {
  /** 被排序的列 */
  column: TableColumn

  /** 排序顺序：'ascending' | 'descending' | null */
  order: 'ascending' | 'descending' | null

  /** 排序属性 */
  prop: string
}

/**
 * 分页变更事件接口
 */
export interface PaginationChangeEvent {
  /** 当前页（1-based） */
  page: number

  /** 每页大小 */
  pageSize: number
}

/**
 * 行点击事件接口
 */
export interface RowClickEvent {
  /** 行数据 */
  row: Record<string, any>

  /** 行索引 */
  index: number

  /** 事件对象 */
  event: Event
}

/**
 * 单元格点击事件接口
 */
export interface CellClickEvent {
  /** 行数据 */
  row: Record<string, any>

  /** 列配置 */
  column: TableColumn

  /** 单元格值 */
  value: any
}

/**
 * 组件事件接口
 */
export interface I0TableEmits {
  /** 排序变更时发出 */
  (e: 'sort-change', event: SortChangeEvent): void

  /** 分页变更时发出 */
  (e: 'pagination-change', event: PaginationChangeEvent): void

  /** 行点击时发出 */
  (e: 'row-click', event: RowClickEvent): void

  /** 单元格点击时发出 */
  (e: 'cell-click', event: CellClickEvent): void

  /** 刷新时发出 */
  (e: 'refresh'): void

  /** 错误时发出 */
  (e: 'error', error: Error): void
}

/**
 * 组件实例类型
 */
export interface I0TableInstance {
  /** 刷新表格数据 */
  refresh: () => void

  /** 获取当前排序状态 */
  getSortState: () => { prop: string; order: string | null }

  /** 获取当前分页状态 */
  getPaginationState: () => PaginationConfig

  /** 设置加载状态 */
  setLoading: (loading: boolean) => void
}

/**
 * 数据类型格式化选项
 */
export type DataFormatter = {
  string: (value: any) => string
  number: (value: any) => string
  date: (value: any) => string
  dateTime: (value: any) => string
  boolean: (value: any) => string
  currency: (value: any) => string
}

/**
 * 列对齐选项
 */
export type ColumnAlignment = 'left' | 'center' | 'right'

/**
 * 表格大小选项
 */
export type TableSize = 'small' | 'default' | 'large'

/**
 * 固定位置选项
 */
export type FixedPosition = 'left' | 'right' | undefined

/**
 * 默认属性值
 */
export const DEFAULT_TABLE_PROPS: Partial<I0TableProps> = {
  loading: false,
  stripe: true,
  border: true,
  size: 'default',
  highlightCurrentRow: false
}

/**
 * 默认列配置
 */
export const DEFAULT_COLUMN: Partial<TableColumn> = {
  type: 'string',
  sortable: false,
  align: 'left',
  showOverflowTooltip: true
}

/**
 * 默认分页配置
 */
export const DEFAULT_PAGINATION: Partial<PaginationConfig> = {
  currentPage: 1,
  pageSize: 20,
  pageSizes: [10, 20, 50, 100],
  layout: 'total, sizes, prev, pager, next, jumper',
  small: true
}

/**
 * 验证规则接口
 */
export interface ValidationRules {
  tableData: 'array'
  tableColumn: 'array:minLength:1'
  column: 'object:requiredProperties:name,prop'
  pagination: 'object:optional'
  paginationPage: 'integer:minimum:1'
  paginationPageSize: 'integer:minimum:1'
  paginationTotal: 'integer:minimum:0'
}

/**
 * 组件状态接口
 */
export interface ComponentState {
  /** 内部加载状态 */
  internalLoading: boolean

  /** 当前排序状态 */
  currentSort: {
    prop: string
    order: 'ascending' | 'descending' | null
  }

  /** 当前分页状态 */
  currentPagination: {
    page: number
    pageSize: number
  }

  /** 列可见性状态 */
  columnVisibility: Record<string, boolean>
}

/**
 * 动态列插槽作用域参数
 */
export interface ColumnSlotScope {
  /** 行数据 */
  row: Record<string, any>
  /** 列配置 */
  column: TableColumn
  /** 单元格原始值 */
  value: any
  /** 格式化后的值 */
  formattedValue: string
  /** 行索引 */
  index: number
}

/**
 * 插槽接口
 */
export interface I0TableSlots {
  /** 默认插槽 - 附加内容 */
  default: () => VNode[]

  /** 空状态插槽 */
  empty: () => VNode[]

  /** 追加插槽 */
  append: () => VNode[]

  /** 操作列插槽 */
  actions: (scope: { row: Record<string, any>; column: TableColumn; index: number }) => VNode[]

  /** 动态列插槽 - 使用列 prop 或自定义名称作为槽名 */
  [slotName: string]: (scope: ColumnSlotScope) => VNode[]
}

/**
 * 内置格式化函数
 */
export const BUILTIN_FORMATTERS: DataFormatter = {
  string: (value: any): string => {
    return value != null ? String(value) : ''
  },

  number: (value: any): string => {
    const num = Number(value)
    return isNaN(num) ? '0' : num.toLocaleString()
  },

  date: (value: any): string => {
    if (!value) return ''
    const date = new Date(value)
    return isNaN(date.getTime()) ? String(value) : date.toLocaleDateString()
  },

  dateTime: (value: any): string => {
    if (!value) return ''
    try {
      // 使用dayjs处理带时区的时间格式，转换为当前系统时区
      const dayjs = require('dayjs')
      const dateTime = dayjs(value)
      if (!dateTime.isValid()) {
        return String(value)
      }
      // 格式化为中文本地化时间格式
      return dateTime.format('YYYY-MM-DD HH:mm:ss')
    } catch (error) {
      console.error('DateTime formatting error:', error)
      return String(value)
    }
  },

  boolean: (value: any): string => {
    return value ? '✓' : '✗'
  },

  currency: (value: any): string => {
    const num = Number(value)
    return isNaN(num) ? '$0.00' : `$${num.toFixed(2)}`
  }
}

/**
 * 组件版本信息
 */
export const COMPONENT_VERSION = '1.0.0'

