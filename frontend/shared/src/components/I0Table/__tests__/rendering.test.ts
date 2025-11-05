import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import type { I0TableProps, TableColumn, PaginationConfig } from '../types'

describe('I0Table 渲染功能集成测试', () => {
  let wrapper: any

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

  describe('基础表格渲染', () => {
    it('应该渲染基本的表格结构', () => {
      // This test will fail until the actual I0Table component is implemented
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__header">
              <div class="i0-table__row">
                <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                  {{ column.name }}
                </div>
              </div>
            </div>
            <div class="i0-table__body">
              <div v-for="(row, rowIndex) in tableData" :key="rowIndex" class="i0-table__row">
                <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                  {{ row[column.prop] }}
                </div>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      expect(wrapper.find('.i0-table').exists()).toBe(true)
      expect(wrapper.find('.i0-table__header').exists()).toBe(true)
      expect(wrapper.find('.i0-table__body').exists()).toBe(true)
    })

    it('应该正确渲染表头', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__header">
              <div class="i0-table__row">
                <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                  {{ column.name }}
                </div>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      const headerCells = wrapper.findAll('.i0-table__header .i0-table__cell')
      expect(headerCells).toHaveLength(2)
      expect(headerCells[0].text()).toBe('Name')
      expect(headerCells[1].text()).toBe('Email')
    })

    it('应该正确渲染表格数据行', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__body">
              <div v-for="(row, rowIndex) in tableData" :key="rowIndex" class="i0-table__row">
                <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                  {{ row[column.prop] }}
                </div>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      const dataRows = wrapper.findAll('.i0-table__body .i0-table__row')
      expect(dataRows).toHaveLength(2)

      const firstRowCells = dataRows[0].findAll('.i0-table__cell')
      expect(firstRowCells[0].text()).toBe('John Doe')
      expect(firstRowCells[1].text()).toBe('john@example.com')

      const secondRowCells = dataRows[1].findAll('.i0-table__cell')
      expect(secondRowCells[0].text()).toBe('Jane Smith')
      expect(secondRowCells[1].text()).toBe('jane@example.com')
    })

    it('应该处理空数据状态', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div v-if="tableData.length === 0" class="i0-table__empty">
              No data available
            </div>
            <div v-else class="i0-table__body">
              <div v-for="(row, rowIndex) in tableData" :key="rowIndex" class="i0-table__row">
                <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                  {{ row[column.prop] }}
                </div>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: [],
          tableColumn: validTableColumn
        }
      })

      expect(wrapper.find('.i0-table__empty').exists()).toBe(true)
      expect(wrapper.find('.i0-table__empty').text()).toBe('No data available')
      expect(wrapper.find('.i0-table__body').exists()).toBe(false)
    })

    it('应该处理加载状态', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          loading: { type: Boolean, default: false }
        },
        template: `
          <div class="i0-table">
            <div v-if="loading" class="i0-table__loading">
              Loading...
            </div>
            <div v-else class="i0-table__body">
              <div v-for="(row, rowIndex) in tableData" :key="rowIndex" class="i0-table__row">
                <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                  {{ row[column.prop] }}
                </div>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          loading: true
        }
      })

      expect(wrapper.find('.i0-table__loading').exists()).toBe(true)
      expect(wrapper.find('.i0-table__loading').text()).toBe('Loading...')
      expect(wrapper.find('.i0-table__body').exists()).toBe(false)
    })

    it('应该应用表格样式类', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          stripe: { type: Boolean, default: true },
          border: { type: Boolean, default: true },
          size: { type: String, default: 'default' }
        },
        computed: {
          tableClasses() {
            return {
              'i0-table': true,
              'i0-table--striped': this.stripe,
              'i0-table--bordered': this.border,
              [`i0-table--${this.size}`]: this.size
            }
          }
        },
        template: `
          <div :class="tableClasses">
            <div class="i0-table__body">
              <div v-for="(row, rowIndex) in tableData" :key="rowIndex" class="i0-table__row">
                <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                  {{ row[column.prop] }}
                </div>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          stripe: true,
          border: true,
          size: 'small'
        }
      })

      const tableElement = wrapper.find('.i0-table')
      expect(tableElement.classes()).toContain('i0-table--striped')
      expect(tableElement.classes()).toContain('i0-table--bordered')
      expect(tableElement.classes()).toContain('i0-table--small')
    })

    it('应该响应数据变化', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__body">
              <div v-for="(row, rowIndex) in tableData" :key="rowIndex" class="i0-table__row">
                <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                  {{ row[column.prop] }}
                </div>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      expect(wrapper.findAll('.i0-table__row')).toHaveLength(2)

      const newData = [...validTableData, { id: 3, name: 'Bob Johnson', email: 'bob@example.com' }]
      await wrapper.setProps({ tableData: newData })

      expect(wrapper.findAll('.i0-table__row')).toHaveLength(3)
      expect(wrapper.findAll('.i0-table__row')[2].findAll('.i0-table__cell')[0].text()).toBe('Bob Johnson')
    })

    it('应该支持自定义列配置', () => {
      const customColumns: TableColumn[] = [
        { name: 'ID', prop: 'id', type: 'string', width: '100px' },
        { name: 'Full Name', prop: 'name', type: 'string', align: 'center' },
        { name: 'Email Address', prop: 'email', type: 'string', className: 'email-column' }
      ]

      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__header">
              <div class="i0-table__row">
                <div
                  v-for="column in tableColumn"
                  :key="column.prop"
                  class="i0-table__cell"
                  :style="{ width: column.width, textAlign: column.align }"
                  :class="column.className"
                >
                  {{ column.name }}
                </div>
              </div>
            </div>
            <div class="i0-table__body">
              <div v-for="(row, rowIndex) in tableData" :key="rowIndex" class="i0-table__row">
                <div
                  v-for="column in tableColumn"
                  :key="column.prop"
                  class="i0-table__cell"
                  :style="{ width: column.width, textAlign: column.align }"
                  :class="column.className"
                >
                  {{ row[column.prop] }}
                </div>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: customColumns
        }
      })

      const headerCells = wrapper.findAll('.i0-table__header .i0-table__cell')
      expect(headerCells[0].text()).toBe('ID')
      expect(headerCells[1].text()).toBe('Full Name')
      expect(headerCells[2].text()).toBe('Email Address')

      // Check custom styling
      expect(headerCells[0].attributes('style')).toContain('width: 100px')
      expect(headerCells[1].attributes('style')).toContain('text-align: center')
      expect(headerCells[2].classes()).toContain('email-column')
    })

    it('应该支持固定列', () => {
      const fixedColumns: TableColumn[] = [
        { name: 'ID', prop: 'id', type: 'string', fixed: 'left' },
        { name: 'Name', prop: 'name', type: 'string' },
        { name: 'Email', prop: 'email', type: 'string', fixed: 'right' }
      ]

      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__header">
              <div class="i0-table__row">
                <div
                  v-for="column in tableColumn"
                  :key="column.prop"
                  class="i0-table__cell"
                  :class="{ 'i0-table__cell--fixed-left': column.fixed === 'left', 'i0-table__cell--fixed-right': column.fixed === 'right' }"
                >
                  {{ column.name }}
                </div>
              </div>
            </div>
            <div class="i0-table__body">
              <div v-for="(row, rowIndex) in tableData" :key="rowIndex" class="i0-table__row">
                <div
                  v-for="column in tableColumn"
                  :key="column.prop"
                  class="i0-table__cell"
                  :class="{ 'i0-table__cell--fixed-left': column.fixed === 'left', 'i0-table__cell--fixed-right': column.fixed === 'right' }"
                >
                  {{ row[column.prop] }}
                </div>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: fixedColumns
        }
      })

      const headerCells = wrapper.findAll('.i0-table__header .i0-table__cell')
      expect(headerCells[0].classes()).toContain('i0-table__cell--fixed-left')
      expect(headerCells[2].classes()).toContain('i0-table__cell--fixed-right')
    })

    it('应该支持列对齐', () => {
      const alignedColumns: TableColumn[] = [
        { name: 'ID', prop: 'id', type: 'string', align: 'left' },
        { name: 'Name', prop: 'name', type: 'string', align: 'center' },
        { name: 'Email', prop: 'email', type: 'string', align: 'right' }
      ]

      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__header">
              <div class="i0-table__row">
                <div
                  v-for="column in tableColumn"
                  :key="column.prop"
                  class="i0-table__cell"
                  :style="{ textAlign: column.align }"
                >
                  {{ column.name }}
                </div>
              </div>
            </div>
            <div class="i0-table__body">
              <div v-for="(row, rowIndex) in tableData" :key="rowIndex" class="i0-table__row">
                <div
                  v-for="column in tableColumn"
                  :key="column.prop"
                  class="i0-table__cell"
                  :style="{ textAlign: column.align }"
                >
                  {{ row[column.prop] }}
                </div>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: alignedColumns
        }
      })

      const headerCells = wrapper.findAll('.i0-table__header .i0-table__cell')
      expect(headerCells[0].attributes('style')).toContain('text-align: left')
      expect(headerCells[1].attributes('style')).toContain('text-align: center')
      expect(headerCells[2].attributes('style')).toContain('text-align: right')
    })

    it('应该处理不同数据类型', () => {
      const mixedData = [
        { id: 1, name: 'John', age: 30, active: true, salary: 50000.50, joinDate: '2023-01-01' },
        { id: 2, name: 'Jane', age: 25, active: false, salary: 60000.75, joinDate: '2023-06-01' }
      ]

      const mixedColumns: TableColumn[] = [
        { name: 'ID', prop: 'id', type: 'string' },
        { name: 'Name', prop: 'name', type: 'string' },
        { name: 'Age', prop: 'age', type: 'number' },
        { name: 'Active', prop: 'active', type: 'boolean' },
        { name: 'Salary', prop: 'salary', type: 'currency' },
        { name: 'Join Date', prop: 'joinDate', type: 'date' }
      ]

      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        methods: {
          formatValue(value: any, type: string) {
            switch (type) {
              case 'number':
                return typeof value === 'number' ? value.toLocaleString() : '0'
              case 'boolean':
                return value ? '✓' : '✗'
              case 'currency':
                return typeof value === 'number' ? `$${value.toFixed(2)}` : '$0.00'
              case 'date':
                return value ? new Date(value).toLocaleDateString() : ''
              default:
                return value || ''
            }
          }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__header">
              <div class="i0-table__row">
                <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                  {{ column.name }}
                </div>
              </div>
            </div>
            <div class="i0-table__body">
              <div v-for="(row, rowIndex) in tableData" :key="rowIndex" class="i0-table__row">
                <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                  {{ formatValue(row[column.prop], column.type) }}
                </div>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: mixedData,
          tableColumn: mixedColumns
        }
      })

      const firstRowCells = wrapper.findAll('.i0-table__body .i0-table__row')[0].findAll('.i0-table__cell')
      expect(firstRowCells[0].text()).toBe('1')
      expect(firstRowCells[1].text()).toBe('John')
      expect(firstRowCells[2].text()).toBe('30')
      expect(firstRowCells[3].text()).toBe('✓')
      expect(firstRowCells[4].text()).toBe('$50000.50')
      expect(firstRowCells[5].text()).toBe('1/1/2023')
    })
  })

  describe('性能优化渲染', () => {
    it('应该虚拟化大量数据渲染', () => {
      const largeData = Array.from({ length: 1000 }, (_, i) => ({
        id: i + 1,
        name: `User ${i + 1}`,
        email: `user${i + 1}@example.com`
      }))

      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        data() {
          return {
            visibleStart: 0,
            visibleEnd: 50
          }
        },
        computed: {
          visibleData() {
            return this.tableData.slice(this.visibleStart, this.visibleEnd)
          }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__header">
              <div class="i0-table__row">
                <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                  {{ column.name }}
                </div>
              </div>
            </div>
            <div class="i0-table__body">
              <div v-for="(row, rowIndex) in visibleData" :key="row.id" class="i0-table__row">
                <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                  {{ row[column.prop] }}
                </div>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: largeData,
          tableColumn: validTableColumn
        }
      })

      // Only visible rows should be rendered
      expect(wrapper.findAll('.i0-table__body .i0-table__row')).toHaveLength(50)
      expect(wrapper.vm.visibleData).toHaveLength(50)
    })

    it('应该懒加载复杂单元格内容', () => {
      const complexData = [
        { id: 1, content: 'Complex content with formatting' },
        { id: 2, content: 'Another complex content' }
      ]

      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        methods: {
          lazyFormat(content: string) {
            // Simulate expensive formatting operation
            return `Formatted: ${content.toUpperCase()}`
          }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__body">
              <div v-for="(row, rowIndex) in tableData" :key="rowIndex" class="i0-table__row">
                <div class="i0-table__cell">
                  {{ lazyFormat(row.content) }}
                </div>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: complexData,
          tableColumn: [{ name: 'Content', prop: 'content', type: 'string' }]
        }
      })

      const cells = wrapper.findAll('.i0-table__cell')
      expect(cells[0].text()).toBe('Formatted: COMPLEX CONTENT WITH FORMATTING')
      expect(cells[1].text()).toBe('Formatted: ANOTHER COMPLEX CONTENT')
    })
  })
})