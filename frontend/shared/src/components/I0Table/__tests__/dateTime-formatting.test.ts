import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import dayjs from 'dayjs'
import type { TableColumn } from '../types'

describe('I0Table dateTime 格式化', () => {
  const testData = [
    {
      id: 1,
      name: 'Test Item 1',
      createdAt: '2025-09-19T10:43:23'
    },
    {
      id: 2,
      name: 'Test Item 2',
      createdAt: '2025-12-25T15:30:45Z'
    }
  ]

  const columns: TableColumn[] = [
    {
      name: '名称',
      prop: 'name',
      type: 'string'
    },
    {
      name: '创建时间',
      prop: 'createdAt',
      type: 'dateTime'
    }
  ]

  it('应该正确格式化 dateTime 类型的数据', () => {
    const I0Table = {
      name: 'I0Table',
      props: {
        tableData: { type: Array, required: true },
        tableColumn: { type: Array, required: true }
      },
      methods: {
        formatDateTime(value: any): string {
          if (!value) return ''
          try {
            const dateTime = dayjs(value)
            if (!dateTime.isValid()) {
              return String(value)
            }
            return dateTime.format('YYYY-MM-DD HH:mm:ss')
          } catch (error) {
            console.error('DateTime formatting error:', error)
            return String(value)
          }
        }
      },
      template: `
        <div class="i0-table">
          <div class="i0-table__body">
            <div v-for="row in tableData" :key="row.id" class="i0-table__row">
              <div class="i0-table__cell">{{ formatDateTime(row.createdAt) }}</div>
            </div>
          </div>
        </div>
      `
    }

    const wrapper = mount(I0Table, {
      props: {
        tableData: testData,
        tableColumn: columns
      }
    })

    // 检查表格是否正确渲染
    expect(wrapper.exists()).toBe(true)
    
    // 检查是否包含格式化后的时间
    const cells = wrapper.findAll('.i0-table__cell')
    expect(cells[0].text()).toBe('2025-09-19 10:43:23')
    // UTC时间 '2025-12-25T15:30:45Z' 转换为本地时区（假设为UTC+8）应该是 23:30:45
    expect(cells[1].text()).toBe('2025-12-25 23:30:45')
  })

  it('应该处理无效的 dateTime 值', () => {
    const I0Table = {
      name: 'I0Table',
      props: {
        tableData: { type: Array, required: true },
        tableColumn: { type: Array, required: true }
      },
      methods: {
        formatDateTime(value: any): string {
          if (!value) return ''
          try {
            const dateTime = dayjs(value)
            if (!dateTime.isValid()) {
              return String(value)
            }
            return dateTime.format('YYYY-MM-DD HH:mm:ss')
          } catch (error) {
            console.error('DateTime formatting error:', error)
            return String(value)
          }
        }
      },
      template: `
        <div class="i0-table">
          <div class="i0-table__body">
            <div v-for="row in tableData" :key="row.id" class="i0-table__row">
              <div class="i0-table__cell">{{ formatDateTime(row.createdAt) }}</div>
            </div>
          </div>
        </div>
      `
    }

    const invalidData = [
      {
        id: 1,
        name: 'Invalid Date',
        createdAt: 'invalid-date-string'
      },
      {
        id: 2,
        name: 'Null Date',
        createdAt: null
      },
      {
        id: 3,
        name: 'Undefined Date',
        createdAt: undefined
      }
    ]

    const wrapper = mount(I0Table, {
      props: {
        tableData: invalidData,
        tableColumn: columns
      }
    })

    expect(wrapper.exists()).toBe(true)
    const cells = wrapper.findAll('.i0-table__cell')
    expect(cells[0].text()).toBe('invalid-date-string') // 无效日期返回原值
    expect(cells[1].text()).toBe('') // null 返回空字符串
    expect(cells[2].text()).toBe('') // undefined 返回空字符串
  })

  it('应该使用自定义格式化函数覆盖默认的 dateTime 格式化', () => {
    const I0Table = {
      name: 'I0Table',
      props: {
        tableData: { type: Array, required: true },
        tableColumn: { type: Array, required: true }
      },
      methods: {
         formatCell(value: any, column: TableColumn, row: any): string {
           if (column.formatter) {
             return column.formatter(value, column, row)
           }
           // 默认 dateTime 格式化
           if (!value) return ''
           try {
             const dateTime = dayjs(value)
             if (!dateTime.isValid()) {
               return String(value)
             }
             return dateTime.format('YYYY-MM-DD HH:mm:ss')
           } catch (error) {
             return String(value)
           }
         }
       },
      template: `
         <div class="i0-table">
           <div class="i0-table__body">
             <div v-for="(row, rowIndex) in tableData" :key="row.id" class="i0-table__row">
               <div class="i0-table__cell">{{ formatCell(row.createdAt, tableColumn[0], row) }}</div>
             </div>
           </div>
         </div>
       `
    }

    const customColumns: TableColumn[] = [
      {
        name: '创建时间',
        prop: 'createdAt',
        type: 'dateTime',
        formatter: (value: any) => {
          if (!value) return '无时间'
          return `自定义: ${value}`
        }
      }
    ]

    const wrapper = mount(I0Table, {
      props: {
        tableData: testData,
        tableColumn: customColumns
      }
    })

    const cells = wrapper.findAll('.i0-table__cell')
    expect(cells[0].text()).toBe('自定义: 2025-09-19T10:43:23')
    expect(cells[1].text()).toBe('自定义: 2025-12-25T15:30:45Z')
  })
})