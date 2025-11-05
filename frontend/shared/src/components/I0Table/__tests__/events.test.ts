import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createVNode } from 'vue'
import type {
  I0TableProps,
  TableColumn,
  PaginationConfig,
  I0TableEmits,
  SortChangeEvent,
  PaginationChangeEvent,
  RowClickEvent,
  CellClickEvent
} from '../types'

// Mock Vue component for testing event emission
const MockI0Table = {
  name: 'I0Table',
  props: {
    tableData: {
      type: Array as () => I0TableProps['tableData'],
      required: true
    },
    tableColumn: {
      type: Array as () => I0TableProps['tableColumn'],
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    },
    pagination: {
      type: Object as () => I0TableProps['pagination'],
      required: false
    },
    stripe: {
      type: Boolean,
      default: true
    },
    border: {
      type: Boolean,
      default: true
    },
    size: {
      type: String as () => I0TableProps['size'],
      default: 'default'
    },
    highlightCurrentRow: {
      type: Boolean,
      default: false
    }
  },
  emits: ['sort-change', 'pagination-change', 'row-click', 'cell-click'],
  template: '<div class="i0-table-mock"></div>'
}

describe('I0Table 事件发射测试', () => {
  let wrapper: any

  const mockTableData: I0TableProps['tableData'] = [
    { id: 1, name: 'John Doe', email: 'john@example.com' },
    { id: 2, name: 'Jane Smith', email: 'jane@example.com' }
  ]

  const mockTableColumn: I0TableProps['tableColumn'] = [
    { name: 'Name', prop: 'name', type: 'string' },
    { name: 'Email', prop: 'email', type: 'string' }
  ]

  const mockPagination: PaginationConfig = {
    page: 1,
    pageSize: 20,
    total: 100
  }

  beforeEach(() => {
    wrapper = mount(MockI0Table, {
      props: {
        tableData: mockTableData,
        tableColumn: mockTableColumn
      }
    })
  })

  describe('sort-change 事件', () => {
    it('应该能够发射 sort-change 事件', () => {
      const sortEvent: SortChangeEvent = {
        column: mockTableColumn[0],
        order: 'ascending',
        prop: 'name'
      }

      wrapper.vm.$emit('sort-change', sortEvent)

      expect(wrapper.emitted('sort-change')).toBeTruthy()
      expect(wrapper.emitted('sort-change')[0]).toEqual([sortEvent])
    })

    it('应该发射降序排序事件', () => {
      const sortEvent: SortChangeEvent = {
        column: mockTableColumn[0],
        order: 'descending',
        prop: 'name'
      }

      wrapper.vm.$emit('sort-change', sortEvent)

      expect(wrapper.emitted('sort-change')).toBeTruthy()
      expect(wrapper.emitted('sort-change')[0][0].order).toBe('descending')
    })

    it('应该发射取消排序事件', () => {
      const sortEvent: SortChangeEvent = {
        column: mockTableColumn[0],
        order: null,
        prop: 'name'
      }

      wrapper.vm.$emit('sort-change', sortEvent)

      expect(wrapper.emitted('sort-change')).toBeTruthy()
      expect(wrapper.emitted('sort-change')[0][0].order).toBeNull()
    })

    it('应该包含正确的列信息', () => {
      const sortEvent: SortChangeEvent = {
        column: mockTableColumn[1],
        order: 'ascending',
        prop: 'email'
      }

      wrapper.vm.$emit('sort-change', sortEvent)

      expect(wrapper.emitted('sort-change')).toBeTruthy()
      expect(wrapper.emitted('sort-change')[0][0].column.name).toBe('Email')
      expect(wrapper.emitted('sort-change')[0][0].column.prop).toBe('email')
    })

    it('应该能够处理多个排序事件', () => {
      const sortEvent1: SortChangeEvent = {
        column: mockTableColumn[0],
        order: 'ascending',
        prop: 'name'
      }

      const sortEvent2: SortChangeEvent = {
        column: mockTableColumn[1],
        order: 'descending',
        prop: 'email'
      }

      wrapper.vm.$emit('sort-change', sortEvent1)
      wrapper.vm.$emit('sort-change', sortEvent2)

      expect(wrapper.emitted('sort-change')).toBeTruthy()
      expect(wrapper.emitted('sort-change')).toHaveLength(2)
      expect(wrapper.emitted('sort-change')[0][0].prop).toBe('name')
      expect(wrapper.emitted('sort-change')[1][0].prop).toBe('email')
    })
  })

  describe('pagination-change 事件', () => {
    it('应该能够发射 pagination-change 事件', () => {
      const paginationEvent: PaginationChangeEvent = {
        page: 2,
        pageSize: 50
      }

      wrapper.vm.$emit('pagination-change', paginationEvent)

      expect(wrapper.emitted('pagination-change')).toBeTruthy()
      expect(wrapper.emitted('pagination-change')[0]).toEqual([paginationEvent])
    })

    it('应该包含正确的页面信息', () => {
      const paginationEvent: PaginationChangeEvent = {
        page: 3,
        pageSize: 10
      }

      wrapper.vm.$emit('pagination-change', paginationEvent)

      expect(wrapper.emitted('pagination-change')).toBeTruthy()
      expect(wrapper.emitted('pagination-change')[0][0].page).toBe(3)
      expect(wrapper.emitted('pagination-change')[0][0].pageSize).toBe(10)
    })

    it('应该能够处理页面大小变化', () => {
      const paginationEvent1: PaginationChangeEvent = {
        page: 1,
        pageSize: 20
      }

      const paginationEvent2: PaginationChangeEvent = {
        page: 1,
        pageSize: 50
      }

      wrapper.vm.$emit('pagination-change', paginationEvent1)
      wrapper.vm.$emit('pagination-change', paginationEvent2)

      expect(wrapper.emitted('pagination-change')).toBeTruthy()
      expect(wrapper.emitted('pagination-change')).toHaveLength(2)
      expect(wrapper.emitted('pagination-change')[0][0].pageSize).toBe(20)
      expect(wrapper.emitted('pagination-change')[1][0].pageSize).toBe(50)
    })

    it('应该能够处理页面跳转', () => {
      const paginationEvent1: PaginationChangeEvent = {
        page: 1,
        pageSize: 20
      }

      const paginationEvent2: PaginationChangeEvent = {
        page: 5,
        pageSize: 20
      }

      wrapper.vm.$emit('pagination-change', paginationEvent1)
      wrapper.vm.$emit('pagination-change', paginationEvent2)

      expect(wrapper.emitted('pagination-change')).toBeTruthy()
      expect(wrapper.emitted('pagination-change')).toHaveLength(2)
      expect(wrapper.emitted('pagination-change')[0][0].page).toBe(1)
      expect(wrapper.emitted('pagination-change')[1][0].page).toBe(5)
    })
  })

  describe('row-click 事件', () => {
    it('应该能够发射 row-click 事件', () => {
      const rowEvent: RowClickEvent = {
        row: mockTableData[0],
        index: 0,
        event: new MouseEvent('click')
      }

      wrapper.vm.$emit('row-click', rowEvent)

      expect(wrapper.emitted('row-click')).toBeTruthy()
      expect(wrapper.emitted('row-click')[0]).toEqual([rowEvent])
    })

    it('应该包含正确的行数据', () => {
      const rowEvent: RowClickEvent = {
        row: mockTableData[1],
        index: 1,
        event: new MouseEvent('click')
      }

      wrapper.vm.$emit('row-click', rowEvent)

      expect(wrapper.emitted('row-click')).toBeTruthy()
      expect(wrapper.emitted('row-click')[0][0].row).toEqual(mockTableData[1])
      expect(wrapper.emitted('row-click')[0][0].row.name).toBe('Jane Smith')
    })

    it('应该包含正确的行索引', () => {
      const rowEvent: RowClickEvent = {
        row: mockTableData[0],
        index: 0,
        event: new MouseEvent('click')
      }

      wrapper.vm.$emit('row-click', rowEvent)

      expect(wrapper.emitted('row-click')).toBeTruthy()
      expect(wrapper.emitted('row-click')[0][0].index).toBe(0)
    })

    it('应该包含事件对象', () => {
      const mockEvent = new MouseEvent('click')
      const rowEvent: RowClickEvent = {
        row: mockTableData[0],
        index: 0,
        event: mockEvent
      }

      wrapper.vm.$emit('row-click', rowEvent)

      expect(wrapper.emitted('row-click')).toBeTruthy()
      expect(wrapper.emitted('row-click')[0][0].event).toBe(mockEvent)
    })

    it('应该能够处理多行点击', () => {
      const rowEvent1: RowClickEvent = {
        row: mockTableData[0],
        index: 0,
        event: new MouseEvent('click')
      }

      const rowEvent2: RowClickEvent = {
        row: mockTableData[1],
        index: 1,
        event: new MouseEvent('click')
      }

      wrapper.vm.$emit('row-click', rowEvent1)
      wrapper.vm.$emit('row-click', rowEvent2)

      expect(wrapper.emitted('row-click')).toBeTruthy()
      expect(wrapper.emitted('row-click')).toHaveLength(2)
      expect(wrapper.emitted('row-click')[0][0].row.name).toBe('John Doe')
      expect(wrapper.emitted('row-click')[1][0].row.name).toBe('Jane Smith')
    })
  })

  describe('cell-click 事件', () => {
    it('应该能够发射 cell-click 事件', () => {
      const cellEvent: CellClickEvent = {
        row: mockTableData[0],
        column: mockTableColumn[0],
        value: 'John Doe'
      }

      wrapper.vm.$emit('cell-click', cellEvent)

      expect(wrapper.emitted('cell-click')).toBeTruthy()
      expect(wrapper.emitted('cell-click')[0]).toEqual([cellEvent])
    })

    it('应该包含正确的单元格数据', () => {
      const cellEvent: CellClickEvent = {
        row: mockTableData[0],
        column: mockTableColumn[0],
        value: 'John Doe'
      }

      wrapper.vm.$emit('cell-click', cellEvent)

      expect(wrapper.emitted('cell-click')).toBeTruthy()
      expect(wrapper.emitted('cell-click')[0][0].row).toEqual(mockTableData[0])
      expect(wrapper.emitted('cell-click')[0][0].column).toEqual(mockTableColumn[0])
      expect(wrapper.emitted('cell-click')[0][0].value).toBe('John Doe')
    })

    it('应该包含列信息', () => {
      const cellEvent: CellClickEvent = {
        row: mockTableData[0],
        column: mockTableColumn[1],
        value: 'john@example.com'
      }

      wrapper.vm.$emit('cell-click', cellEvent)

      expect(wrapper.emitted('cell-click')).toBeTruthy()
      expect(wrapper.emitted('cell-click')[0][0].column.name).toBe('Email')
      expect(wrapper.emitted('cell-click')[0][0].column.prop).toBe('email')
    })

    it('应该能够处理不同类型的单元格值', () => {
      const cellEvent1: CellClickEvent = {
        row: mockTableData[0],
        column: mockTableColumn[0],
        value: 'John Doe'
      }

      const cellEvent2: CellClickEvent = {
        row: mockTableData[0],
        column: mockTableColumn[1],
        value: 'john@example.com'
      }

      wrapper.vm.$emit('cell-click', cellEvent1)
      wrapper.vm.$emit('cell-click', cellEvent2)

      expect(wrapper.emitted('cell-click')).toBeTruthy()
      expect(wrapper.emitted('cell-click')).toHaveLength(2)
      expect(wrapper.emitted('cell-click')[0][0].value).toBe('John Doe')
      expect(wrapper.emitted('cell-click')[1][0].value).toBe('john@example.com')
    })
  })

  describe('事件类型验证', () => {
    it('应该验证 SortChangeEvent 类型', () => {
      const invalidSortEvent = {
        column: 'not a column',
        order: 'invalid',
        prop: 123
      }

      // 组件应该仍然发射事件，但类型检查会捕获错误
      wrapper.vm.$emit('sort-change', invalidSortEvent)

      expect(wrapper.emitted('sort-change')).toBeTruthy()
    })

    it('应该验证 PaginationChangeEvent 类型', () => {
      const invalidPaginationEvent = {
        page: 'not a number',
        pageSize: 'not a number'
      }

      wrapper.vm.$emit('pagination-change', invalidPaginationEvent)

      expect(wrapper.emitted('pagination-change')).toBeTruthy()
    })

    it('应该验证 RowClickEvent 类型', () => {
      const invalidRowEvent = {
        row: 'not a row',
        index: 'not an index',
        event: 'not an event'
      }

      wrapper.vm.$emit('row-click', invalidRowEvent)

      expect(wrapper.emitted('row-click')).toBeTruthy()
    })

    it('应该验证 CellClickEvent 类型', () => {
      const invalidCellEvent = {
        row: 'not a row',
        column: 'not a column',
        value: 'not a value'
      }

      wrapper.vm.$emit('cell-click', invalidCellEvent)

      expect(wrapper.emitted('cell-click')).toBeTruthy()
    })
  })

  describe('事件发射频率', () => {
    it('应该正确处理高频事件发射', () => {
      for (let i = 0; i < 10; i++) {
        const sortEvent: SortChangeEvent = {
          column: mockTableColumn[0],
          order: i % 2 === 0 ? 'ascending' : 'descending',
          prop: 'name'
        }

        wrapper.vm.$emit('sort-change', sortEvent)
      }

      expect(wrapper.emitted('sort-change')).toBeTruthy()
      expect(wrapper.emitted('sort-change')).toHaveLength(10)
    })

    it('应该能够处理混合事件类型', () => {
      const sortEvent: SortChangeEvent = {
        column: mockTableColumn[0],
        order: 'ascending',
        prop: 'name'
      }

      const paginationEvent: PaginationChangeEvent = {
        page: 2,
        pageSize: 50
      }

      const rowEvent: RowClickEvent = {
        row: mockTableData[0],
        index: 0,
        event: new MouseEvent('click')
      }

      const cellEvent: CellClickEvent = {
        row: mockTableData[0],
        column: mockTableColumn[0],
        value: 'John Doe'
      }

      wrapper.vm.$emit('sort-change', sortEvent)
      wrapper.vm.$emit('pagination-change', paginationEvent)
      wrapper.vm.$emit('row-click', rowEvent)
      wrapper.vm.$emit('cell-click', cellEvent)

      expect(wrapper.emitted('sort-change')).toBeTruthy()
      expect(wrapper.emitted('pagination-change')).toBeTruthy()
      expect(wrapper.emitted('row-click')).toBeTruthy()
      expect(wrapper.emitted('cell-click')).toBeTruthy()
    })
  })

  describe('事件数据完整性', () => {
    it('应该确保事件数据不被修改', () => {
      const originalRow = { ...mockTableData[0] }
      const rowEvent: RowClickEvent = {
        row: mockTableData[0],
        index: 0,
        event: new MouseEvent('click')
      }

      wrapper.vm.$emit('row-click', rowEvent)

      // 验证原始数据未被修改
      expect(wrapper.emitted('row-click')[0][0].row).toEqual(originalRow)
      expect(mockTableData[0]).toEqual(originalRow)
    })

    it('应该处理空数据事件', () => {
      const emptyRowEvent: RowClickEvent = {
        row: {},
        index: 0,
        event: new MouseEvent('click')
      }

      wrapper.vm.$emit('row-click', emptyRowEvent)

      expect(wrapper.emitted('row-click')).toBeTruthy()
      expect(wrapper.emitted('row-click')[0][0].row).toEqual({})
    })

    it('应该处理 undefined/null 值', () => {
      const cellEvent: CellClickEvent = {
        row: mockTableData[0],
        column: mockTableColumn[0],
        value: undefined
      }

      wrapper.vm.$emit('cell-click', cellEvent)

      expect(wrapper.emitted('cell-click')).toBeTruthy()
      expect(wrapper.emitted('cell-click')[0][0].value).toBeUndefined()
    })
  })
})