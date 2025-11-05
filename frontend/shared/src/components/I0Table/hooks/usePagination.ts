import { ref, computed } from 'vue'

/**
 * I0Table 专用的分页 Hook
 * 基于 el-pagination 的基础实现
 */
export function usePagination(options: I0TablePaginationOptions = {}) {
  const {
    initialPage = 1,
    initialPageSize = 20,
    pageSizes = [10, 20, 50, 100],
    onLoadData
  } = options

  // 分页状态
  const currentPage = ref(initialPage)
  const pageSize = ref(initialPageSize)
  const total = ref(0)
  const loading = ref(false)

  // I0Table 分页配置
  const i0PaginationConfig = computed(() => ({
    total: total.value,
    initialPage: currentPage.value,
    initialPageSize: pageSize.value,
    pageSizes,
    layout: 'total, sizes, prev, pager, next, jumper',
    small: true
  }))

  // 处理分页变更事件
  const handlePaginationChange = (event: any) => {
    currentPage.value = event.page
    pageSize.value = event.pageSize

    // 如果有数据加载回调，自动调用
    if (onLoadData) {
      onLoadData({ page: currentPage.value, pageSize: pageSize.value })
    }
  }

  // 更新总数据量
  const updateData = (newTotal: number) => {
    total.value = newTotal
  }

  // 重置分页
  const resetPagination = () => {
    currentPage.value = initialPage
    pageSize.value = initialPageSize
  }

  // 跳转到指定页
  const goToPage = (page: number) => {
    currentPage.value = page

    // 如果有数据加载回调，自动调用
    if (onLoadData) {
      onLoadData({ page: currentPage.value, pageSize: pageSize.value })
    }
  }

  // 设置页面大小
  const setPageSize = (size: number) => {
    pageSize.value = size
    currentPage.value = 1 // 重置到第一页

    // 如果有数据加载回调，自动调用
    if (onLoadData) {
      onLoadData({ page: currentPage.value, pageSize: pageSize.value })
    }
  }

  return {
    // 状态
    currentPage,
    pageSize,
    total,
    loading,

    // 配置
    i0PaginationConfig,

    // 方法
    handlePaginationChange,
    updateData,
    resetPagination,
    goToPage,
    setPageSize
  }
}

/**
 * I0Table 分页 Hook 的配置选项
 */
export interface I0TablePaginationOptions {
  /** 初始页码（1-based） */
  initialPage?: number

  /** 初始每页大小 */
  initialPageSize?: number

  /** 页面大小选项 */
  pageSizes?: number[]

  /** 数据加载回调函数 */
  onLoadData?: (params: { page: number; pageSize: number }) => Promise<void> | void
}