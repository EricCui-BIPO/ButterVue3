import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import type { I0TableProps, TableColumn, PaginationConfig } from '../types'

// Mock Vue component for props validation testing
const MockI0Table = {
  name: 'I0Table',
  props: {
    tableData: {
      type: Array,
      required: true,
      validator: (value: any[]) => {
        return Array.isArray(value)
      }
    },
    tableColumn: {
      type: Array,
      required: true,
      validator: (value: TableColumn[]) => {
        return Array.isArray(value) && value.length > 0 &&
               value.every(col => col.name && col.prop)
      }
    },
    loading: {
      type: Boolean,
      default: false
    },
    pagination: {
      type: Object,
      required: false,
      validator: (value: PaginationConfig) => {
        return value &&
               typeof value.page === 'number' && value.page >= 1 &&
               typeof value.pageSize === 'number' && value.pageSize >= 1 &&
               typeof value.total === 'number' && value.total >= 0
      }
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
      type: String,
      default: 'default',
      validator: (value: string) => ['small', 'default', 'large'].includes(value)
    },
    highlightCurrentRow: {
      type: Boolean,
      default: false
    }
  },
  template: '<div class="i0-table-mock"></div>'
}

describe('I0Table Props 验证测试', () => {
  const validTableData: I0TableProps['tableData'] = [
    { id: 1, name: 'John Doe', email: 'john@example.com' },
    { id: 2, name: 'Jane Smith', email: 'jane@example.com' }
  ]

  const validTableColumn: I0TableProps['tableColumn'] = [
    { name: 'Name', prop: 'name', type: 'string' },
    { name: 'Email', prop: 'email', type: 'string' }
  ]

  const validPagination: PaginationConfig = {
    page: 1,
    pageSize: 20,
    total: 100
  }

  describe('必需 Props 验证', () => {
    it('应该接受有效的 tableData prop', () => {
      const wrapper = mount(MockI0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      expect(wrapper.props('tableData')).toEqual(validTableData)
      expect(wrapper.vm.$options.props.tableData.validator(validTableData)).toBe(true)
    })

    it('应该拒绝无效的 tableData prop', () => {
      expect(() => {
        mount(MockI0Table, {
          props: {
            tableData: 'not an array' as any,
            tableColumn: validTableColumn
          }
        })
      }).toThrow()
    })

    it('应该接受有效的 tableColumn prop', () => {
      const wrapper = mount(MockI0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      expect(wrapper.props('tableColumn')).toEqual(validTableColumn)
      expect(wrapper.vm.$options.props.tableColumn.validator(validTableColumn)).toBe(true)
    })

    it('应该拒绝无效的 tableColumn prop - 空数组', () => {
      const invalidTableColumn: TableColumn[] = []

      expect(() => {
        mount(MockI0Table, {
          props: {
            tableData: validTableData,
            tableColumn: invalidTableColumn
          }
        })
      }).toThrow()
    })

    it('应该拒绝无效的 tableColumn prop - 缺少必需属性', () => {
      const invalidTableColumn = [
        { name: 'Only Name' } as TableColumn, // 缺少 prop
        { prop: 'onlyProp' } as TableColumn  // 缺少 name
      ]

      expect(() => {
        mount(MockI0Table, {
          props: {
            tableData: validTableData,
            tableColumn: invalidTableColumn
          }
        })
      }).toThrow()
    })
  })

  describe('可选 Props 验证', () => {
    describe('loading prop', () => {
      it('应该接受布尔值 loading prop', () => {
        const wrapper = mount(MockI0Table, {
          props: {
            tableData: validTableData,
            tableColumn: validTableColumn,
            loading: true
          }
        })

        expect(wrapper.props('loading')).toBe(true)
      })

      it('应该有正确的默认值', () => {
        const wrapper = mount(MockI0Table, {
          props: {
            tableData: validTableData,
            tableColumn: validTableColumn
          }
        })

        expect(wrapper.props('loading')).toBe(false)
      })
    })

    describe('pagination prop', () => {
      it('应该接受有效的 pagination prop', () => {
        const wrapper = mount(MockI0Table, {
          props: {
            tableData: validTableData,
            tableColumn: validTableColumn,
            pagination: validPagination
          }
        })

        expect(wrapper.props('pagination')).toEqual(validPagination)
        expect(wrapper.vm.$options.props.pagination.validator(validPagination)).toBe(true)
      })

      it('应该拒绝无效的 pagination prop - 缺少必需字段', () => {
        const invalidPagination = {
          page: 1,
          pageSize: 20
          // 缺少 total
        } as PaginationConfig

        expect(() => {
          mount(MockI0Table, {
            props: {
              tableData: validTableData,
              tableColumn: validTableColumn,
              pagination: invalidPagination
            }
          })
        }).toThrow()
      })

      it('应该拒绝无效的 pagination prop - 负数页码', () => {
        const invalidPagination = {
          page: -1,
          pageSize: 20,
          total: 100
        }

        expect(() => {
          mount(MockI0Table, {
            props: {
              tableData: validTableData,
              tableColumn: validTableColumn,
              pagination: invalidPagination
            }
          })
        }).toThrow()
      })

      it('应该拒绝无效的 pagination prop - 零页面大小', () => {
        const invalidPagination = {
          page: 1,
          pageSize: 0,
          total: 100
        }

        expect(() => {
          mount(MockI0Table, {
            props: {
              tableData: validTableData,
              tableColumn: validTableColumn,
              pagination: invalidPagination
            }
          })
        }).toThrow()
      })

      it('应该拒绝无效的 pagination prop - 负总数', () => {
        const invalidPagination = {
          page: 1,
          pageSize: 20,
          total: -1
        }

        expect(() => {
          mount(MockI0Table, {
            props: {
              tableData: validTableData,
              tableColumn: validTableColumn,
              pagination: invalidPagination
            }
          })
        }).toThrow()
      })
    })

    describe('表格样式 props', () => {
      it('应该接受 stripe prop', () => {
        const wrapper = mount(MockI0Table, {
          props: {
            tableData: validTableData,
            tableColumn: validTableColumn,
            stripe: false
          }
        })

        expect(wrapper.props('stripe')).toBe(false)
      })

      it('应该接受 border prop', () => {
        const wrapper = mount(MockI0Table, {
          props: {
            tableData: validTableData,
            tableColumn: validTableColumn,
            border: false
          }
        })

        expect(wrapper.props('border')).toBe(false)
      })

      it('应该接受有效的 size prop', () => {
        const validSizes = ['small', 'default', 'large'] as const

        validSizes.forEach(size => {
          const wrapper = mount(MockI0Table, {
            props: {
              tableData: validTableData,
              tableColumn: validTableColumn,
              size
            }
          })

          expect(wrapper.props('size')).toBe(size)
        })
      })

      it('应该拒绝无效的 size prop', () => {
        expect(() => {
          mount(MockI0Table, {
            props: {
              tableData: validTableData,
              tableColumn: validTableColumn,
              size: 'invalid' as any
            }
          })
        }).toThrow()
      })

      it('应该接受 highlightCurrentRow prop', () => {
        const wrapper = mount(MockI0Table, {
          props: {
            tableData: validTableData,
            tableColumn: validTableColumn,
            highlightCurrentRow: true
          }
        })

        expect(wrapper.props('highlightCurrentRow')).toBe(true)
      })

      it('应该有正确的默认值', () => {
        const wrapper = mount(MockI0Table, {
          props: {
            tableData: validTableData,
            tableColumn: validTableColumn
          }
        })

        expect(wrapper.props('stripe')).toBe(true)
        expect(wrapper.props('border')).toBe(true)
        expect(wrapper.props('size')).toBe('default')
        expect(wrapper.props('highlightCurrentRow')).toBe(false)
      })
    })
  })

  describe('Column 配置验证', () => {
    it('应该验证必需的列属性', () => {
      const validColumns: TableColumn[] = [
        { name: 'ID', prop: 'id', type: 'string' },
        { name: 'Name', prop: 'name', type: 'string' }
      ]

      const wrapper = mount(MockI0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validColumns
        }
      })

      expect(wrapper.props('tableColumn')).toEqual(validColumns)
    })

    it('应该接受各种有效的列配置', () => {
      const columns: TableColumn[] = [
        {
          name: 'Name',
          prop: 'name',
          type: 'string',
          width: 200,
          fixed: 'left',
          sortable: true,
          align: 'center',
          showOverflowTooltip: true,
          className: 'custom-name'
        },
        {
          name: 'Age',
          prop: 'age',
          type: 'number',
          width: '100px',
          align: 'right',
          sortable: true
        },
        {
          name: 'Email',
          prop: 'email',
          type: 'string',
          showOverflowTooltip: false
        }
      ]

      const wrapper = mount(MockI0Table, {
        props: {
          tableData: validTableData,
          tableColumn: columns
        }
      })

      expect(wrapper.props('tableColumn')).toEqual(columns)
    })

    it('应该接受自定义格式化函数', () => {
      const columns: TableColumn[] = [
        {
          name: 'Custom',
          prop: 'custom',
          type: 'string',
          formatter: (value: any) => `Custom: ${value}`
        }
      ]

      const wrapper = mount(MockI0Table, {
        props: {
          tableData: validTableData,
          tableColumn: columns
        }
      })

      expect(wrapper.props('tableColumn')[0].formatter).toBeDefined()
      expect(typeof wrapper.props('tableColumn')[0].formatter).toBe('function')
    })
  })

  describe('数据类型验证', () => {
    it('应该处理各种数据类型', () => {
      const testData = [
        { id: 1, name: 'John', age: 30, active: true, salary: 50000.50, joinDate: '2023-01-01' },
        { id: 2, name: 'Jane', age: 25, active: false, salary: 60000.75, joinDate: '2023-06-01' }
      ]

      const columns: TableColumn[] = [
        { name: 'ID', prop: 'id', type: 'string' },
        { name: 'Name', prop: 'name', type: 'string' },
        { name: 'Age', prop: 'age', type: 'number' },
        { name: 'Active', prop: 'active', type: 'boolean' },
        { name: 'Salary', prop: 'salary', type: 'currency' },
        { name: 'Join Date', prop: 'joinDate', type: 'date' }
      ]

      const wrapper = mount(MockI0Table, {
        props: {
          tableData: testData,
          tableColumn: columns
        }
      })

      expect(wrapper.props('tableData')).toEqual(testData)
      expect(wrapper.props('tableColumn')).toEqual(columns)
    })

    it('应该处理空数据数组', () => {
      const wrapper = mount(MockI0Table, {
        props: {
          tableData: [],
          tableColumn: validTableColumn
        }
      })

      expect(wrapper.props('tableData')).toEqual([])
    })

    it('应该处理包含 null/undefined 值的数据', () => {
      const testData = [
        { id: 1, name: 'John', email: null },
        { id: 2, name: 'Jane', email: undefined }
      ]

      const wrapper = mount(MockI0Table, {
        props: {
          tableData: testData,
          tableColumn: validTableColumn
        }
      })

      expect(wrapper.props('tableData')).toEqual(testData)
    })
  })

  describe('响应式更新验证', () => {
    it('应该响应 props 更新', async () => {
      const wrapper = mount(MockI0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      // 更新 tableData
      const newData = [...validTableData, { id: 3, name: 'Bob Johnson', email: 'bob@example.com' }]
      await wrapper.setProps({ tableData: newData })

      expect(wrapper.props('tableData')).toEqual(newData)

      // 更新 loading 状态
      await wrapper.setProps({ loading: true })
      expect(wrapper.props('loading')).toBe(true)

      // 更新分页
      const newPagination = { ...validPagination, page: 2 }
      await wrapper.setProps({ pagination: newPagination })
      expect(wrapper.props('pagination')).toEqual(newPagination)
    })
  })
})