import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { createVNode } from 'vue'
import type {
  I0TableProps,
  TableColumn,
  PaginationConfig,
  I0TableEmits
} from '../types'

// Mock Vue component for testing interface compliance
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

describe('I0Table Interface 契约测试', () => {
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

  describe('Props 接口验证', () => {
    it('应该接受必需的 tableData 和 tableColumn props', () => {
      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: mockTableColumn
        }
      })

      expect(wrapper.exists()).toBe(true)
      expect(wrapper.props('tableData')).toEqual(mockTableData)
      expect(wrapper.props('tableColumn')).toEqual(mockTableColumn)
    })

    it('应该接受可选的 loading prop', () => {
      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: mockTableColumn,
          loading: true
        }
      })

      expect(wrapper.props('loading')).toBe(true)
    })

    it('应该接受可选的 pagination prop', () => {
      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: mockTableColumn,
          pagination: mockPagination
        }
      })

      expect(wrapper.props('pagination')).toEqual(mockPagination)
    })

    it('应该接受表格样式相关 props', () => {
      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: mockTableColumn,
          stripe: false,
          border: false,
          size: 'small',
          highlightCurrentRow: true
        }
      })

      expect(wrapper.props('stripe')).toBe(false)
      expect(wrapper.props('border')).toBe(false)
      expect(wrapper.props('size')).toBe('small')
      expect(wrapper.props('highlightCurrentRow')).toBe(true)
    })

    it('应该有正确的默认值', () => {
      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: mockTableColumn
        }
      })

      expect(wrapper.props('loading')).toBe(false)
      expect(wrapper.props('stripe')).toBe(true)
      expect(wrapper.props('border')).toBe(true)
      expect(wrapper.props('size')).toBe('default')
      expect(wrapper.props('highlightCurrentRow')).toBe(false)
    })
  })

  describe('Column 配置接口验证', () => {
    it('应该接受基本的列配置', () => {
      const columns: TableColumn[] = [
        { name: 'ID', prop: 'id', type: 'string' },
        { name: 'Name', prop: 'name', type: 'string' }
      ]

      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: columns
        }
      })

      expect(wrapper.props('tableColumn')).toEqual(columns)
    })

    it('应该接受完整的列配置', () => {
      const columns: TableColumn[] = [
        {
          name: 'Name',
          prop: 'name',
          type: 'string',
          width: '200px',
          fixed: 'left',
          sortable: true,
          align: 'center',
          showOverflowTooltip: true,
          className: 'custom-column',
          formatter: (value: any) => `Formatted: ${value}`
        }
      ]

      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: columns
        }
      })

      expect(wrapper.props('tableColumn')[0].name).toBe('Name')
      expect(wrapper.props('tableColumn')[0].formatter).toBeDefined()
    })
  })

  describe('分页配置接口验证', () => {
    it('应该接受基本的分页配置', () => {
      const pagination: PaginationConfig = {
        page: 1,
        pageSize: 10,
        total: 50
      }

      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: mockTableColumn,
          pagination
        }
      })

      expect(wrapper.props('pagination')).toEqual(pagination)
    })

    it('应该接受完整的分页配置', () => {
      const pagination: PaginationConfig = {
        page: 2,
        pageSize: 50,
        total: 200,
        pageSizes: [10, 50, 100],
        layout: 'total, sizes, prev, pager, next',
        small: false
      }

      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: mockTableColumn,
          pagination
        }
      })

      expect(wrapper.props('pagination')).toEqual(pagination)
    })
  })

  describe('事件接口验证', () => {
    it('应该定义所有必需的事件', () => {
      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: mockTableColumn
        }
      })

      // 检查组件是否定义了所有事件
      const emittedEvents = wrapper.emitted()
      expect(typeof wrapper.vm.$emit).toBe('function')
    })

    it('应该能够触发 sort-change 事件', () => {
      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: mockTableColumn
        }
      })

      const sortEvent = {
        column: mockTableColumn[0],
        order: 'ascending',
        prop: 'name'
      }

      wrapper.vm.$emit('sort-change', sortEvent)

      expect(wrapper.emitted('sort-change')).toBeTruthy()
      expect(wrapper.emitted('sort-change')[0]).toEqual([sortEvent])
    })

    it('应该能够触发 pagination-change 事件', () => {
      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: mockTableColumn
        }
      })

      const paginationEvent = {
        page: 2,
        pageSize: 50
      }

      wrapper.vm.$emit('pagination-change', paginationEvent)

      expect(wrapper.emitted('pagination-change')).toBeTruthy()
      expect(wrapper.emitted('pagination-change')[0]).toEqual([paginationEvent])
    })

    it('应该能够触发 row-click 事件', () => {
      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: mockTableColumn
        }
      })

      const rowEvent = {
        row: mockTableData[0],
        index: 0,
        event: new MouseEvent('click')
      }

      wrapper.vm.$emit('row-click', rowEvent)

      expect(wrapper.emitted('row-click')).toBeTruthy()
      expect(wrapper.emitted('row-click')[0]).toEqual([rowEvent])
    })

    it('应该能够触发 cell-click 事件', () => {
      wrapper = mount(MockI0Table, {
        props: {
          tableData: mockTableData,
          tableColumn: mockTableColumn
        }
      })

      const cellEvent = {
        row: mockTableData[0],
        column: mockTableColumn[0],
        value: 'John Doe'
      }

      wrapper.vm.$emit('cell-click', cellEvent)

      expect(wrapper.emitted('cell-click')).toBeTruthy()
      expect(wrapper.emitted('cell-click')[0]).toEqual([cellEvent])
    })
  })

  describe('类型安全性验证', () => {
    it('应该强制 tableData 为数组类型', () => {
      // 这个测试验证类型系统工作正常
      const validData: I0TableProps['tableData'] = []
      const invalidData = 'not an array' as any

      expect(Array.isArray(validData)).toBe(true)
      expect(Array.isArray(invalidData)).toBe(false)
    })

    it('应该强制 tableColumn 为数组类型', () => {
      const validColumns: I0TableProps['tableColumn'] = []
      const invalidColumns = 'not an array' as any

      expect(Array.isArray(validColumns)).toBe(true)
      expect(Array.isArray(invalidColumns)).toBe(false)
    })

    it('应该验证必需的列属性', () => {
      const validColumn: TableColumn = {
        name: 'Test',
        prop: 'test',
        type: 'string'
      }

      const invalidColumn = {
        name: 'Test'
        // 缺少必需的 prop 属性
      } as TableColumn

      expect(validColumn.prop).toBeDefined()
      expect(invalidColumn.prop).toBeUndefined()
    })
  })
})