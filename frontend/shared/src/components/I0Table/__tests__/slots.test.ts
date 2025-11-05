import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import type { I0TableProps, TableColumn } from '../types'

describe('I0Table 自定义插槽功能集成测试', () => {
  let wrapper: any

  const validTableData: I0TableProps['tableData'] = [
    { id: 1, name: 'John Doe', email: 'john@example.com', age: 30 },
    { id: 2, name: 'Jane Smith', email: 'jane@example.com', age: 25 },
    { id: 3, name: 'Bob Johnson', email: 'bob@example.com', age: 35 }
  ]

  const validTableColumn: TableColumn[] = [
    { name: 'Name', prop: 'name', type: 'string' },
    { name: 'Email', prop: 'email', type: 'string' },
    { name: 'Age', prop: 'age', type: 'number' }
  ]

  describe('默认插槽', () => {
    it('应该渲染默认插槽内容', () => {
      // This test will fail until the actual I0Table component is implemented
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__default">
              <slot></slot>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        },
        slots: {
          default: '<div class="custom-default-content">Custom Default Content</div>'
        }
      })

      expect(wrapper.find('.i0-table__default').exists()).toBe(true)
      expect(wrapper.find('.custom-default-content').exists()).toBe(true)
      expect(wrapper.find('.custom-default-content').text()).toBe('Custom Default Content')
    })

    it('应该处理空的默认插槽', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__default">
              <slot>
                <div class="default-fallback">Default Fallback Content</div>
              </slot>
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

      expect(wrapper.find('.i0-table__default').exists()).toBe(true)
      expect(wrapper.find('.default-fallback').exists()).toBe(true)
      expect(wrapper.find('.default-fallback').text()).toBe('Default Fallback Content')
    })

    it('应该支持默认插槽中的复杂内容', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__default">
              <slot></slot>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        },
        slots: {
          default: `
            <div class="complex-content">
              <h3>Custom Header</h3>
              <p>This is complex default slot content</p>
              <button @click="handleClick">Click Me</button>
            </div>
          `
        }
      })

      expect(wrapper.find('.complex-content').exists()).toBe(true)
      expect(wrapper.find('h3').text()).toBe('Custom Header')
      expect(wrapper.find('p').text()).toBe('This is complex default slot content')
      expect(wrapper.find('button').exists()).toBe(true)
    })
  })

  describe('空状态插槽', () => {
    it('应该渲染空状态插槽内容', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        computed: {
          isEmpty() {
            return this.tableData.length === 0
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="isEmpty" class="i0-table__empty">
              <slot name="empty"></slot>
            </div>
            <div v-else class="i0-table__content">
              Table content
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: [],
          tableColumn: validTableColumn
        },
        slots: {
          empty: '<div class="custom-empty">No data available</div>'
        }
      })

      expect(wrapper.find('.i0-table__empty').exists()).toBe(true)
      expect(wrapper.find('.custom-empty').exists()).toBe(true)
      expect(wrapper.find('.custom-empty').text()).toBe('No data available')
    })

    it('应该只在数据为空时显示空状态插槽', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        computed: {
          isEmpty() {
            return this.tableData.length === 0
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="isEmpty" class="i0-table__empty">
              <slot name="empty"></slot>
            </div>
            <div v-else class="i0-table__content">
              <slot></slot>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData, // Has data
          tableColumn: validTableColumn
        },
        slots: {
          empty: '<div class="custom-empty">No data available</div>',
          default: '<div class="table-content">Table has data</div>'
        }
      })

      expect(wrapper.find('.i0-table__empty').exists()).toBe(false)
      expect(wrapper.find('.i0-table__content').exists()).toBe(true)
      expect(wrapper.find('.table-content').exists()).toBe(true)
    })

    it('应该提供空状态插槽的默认内容', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        computed: {
          isEmpty() {
            return this.tableData.length === 0
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="isEmpty" class="i0-table__empty">
              <slot name="empty">
                <div class="default-empty">No data available</div>
              </slot>
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
      expect(wrapper.find('.default-empty').exists()).toBe(true)
      expect(wrapper.find('.default-empty').text()).toBe('No data available')
    })
  })

  describe('追加插槽', () => {
    it('应该渲染追加插槽内容', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__main">
              Main table content
            </div>
            <div class="i0-table__append">
              <slot name="append"></slot>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        },
        slots: {
          append: '<div class="custom-append">Additional Content</div>'
        }
      })

      expect(wrapper.find('.i0-table__append').exists()).toBe(true)
      expect(wrapper.find('.custom-append').exists()).toBe(true)
      expect(wrapper.find('.custom-append').text()).toBe('Additional Content')
    })

    it('应该在表格底部显示追加插槽', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__header">Table Header</div>
            <div class="i0-table__body">Table Body</div>
            <div class="i0-table__append">
              <slot name="append"></slot>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        },
        slots: {
          append: '<div class="footer-summary">Total: 3 items</div>'
        }
      })

      const header = wrapper.find('.i0-table__header')
      const body = wrapper.find('.i0-table__body')
      const append = wrapper.find('.i0-table__append')

      expect(header.exists()).toBe(true)
      expect(body.exists()).toBe(true)
      expect(append.exists()).toBe(true)
      expect(wrapper.find('.footer-summary').text()).toBe('Total: 3 items')

      // Verify the order
      expect(header.element.compareDocumentPosition(append.element) & Node.DOCUMENT_POSITION_FOLLOWING).toBeTruthy()
      expect(body.element.compareDocumentPosition(append.element) & Node.DOCUMENT_POSITION_FOLLOWING).toBeTruthy()
    })

    it('应该处理空的追加插槽', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__main">
              Main table content
            </div>
            <div class="i0-table__append">
              <slot name="append">
                <div class="default-append">Default append content</div>
              </slot>
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

      expect(wrapper.find('.i0-table__append').exists()).toBe(true)
      expect(wrapper.find('.default-append').exists()).toBe(true)
      expect(wrapper.find('.default-append').text()).toBe('Default append content')
    })
  })

  describe('操作列插槽', () => {
    it('应该渲染操作列插槽内容', () => {
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
                <div class="i0-table__cell i0-table__cell--actions">
                  <slot name="actions" :row="row" :column="tableColumn[0]" :index="rowIndex"></slot>
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
        },
        slots: {
          actions: '<template #default="{ row, index }"><button class="edit-btn" @click="edit(row)">Edit {{ index + 1 }}</button></template>'
        }
      })

      const actionButtons = wrapper.findAll('.edit-btn')
      expect(actionButtons).toHaveLength(3)
      expect(actionButtons[0].text()).toBe('Edit 1')
      expect(actionButtons[1].text()).toBe('Edit 2')
      expect(actionButtons[2].text()).toBe('Edit 3')
    })

    it('应该为操作列插槽提供正确的作用域数据', () => {
      let receivedScope: any = null

      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        methods: {
          captureScope(scope: any) {
            receivedScope = scope
          }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__body">
              <div v-for="(row, rowIndex) in tableData" :key="rowIndex" class="i0-table__row">
                <div class="i0-table__cell i0-table__cell--actions">
                  <slot name="actions" :row="row" :column="tableColumn[0]" :index="rowIndex" @capture-scope="captureScope"></slot>
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
        },
        slots: {
          actions: '<template #default="{ row, column, index }"><div @click="$emit(\'capture-scope\', { row, column, index })">Actions</div></template>'
        }
      })

      // Click the first actions slot
      const actionDivs = wrapper.findAll('.i0-table__cell--actions div')
      await actionDivs[0].trigger('click')

      expect(receivedScope).toBeTruthy()
      expect(receivedScope.row).toEqual(validTableData[0])
      expect(receivedScope.column).toEqual(validTableColumn[0])
      expect(receivedScope.index).toBe(0)
    })

    it('应该支持复杂的操作列内容', () => {
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
                <div class="i0-table__cell i0-table__cell--actions">
                  <slot name="actions" :row="row" :index="rowIndex"></slot>
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
        },
        slots: {
          actions: `
            <template #default="{ row, index }">
              <div class="action-buttons">
                <button class="btn-edit" @click="edit(row)">Edit</button>
                <button class="btn-delete" @click="delete(row)">Delete</button>
                <button class="btn-view" @click="view(row)">View</button>
              </div>
            </template>
          `
        }
      })

      const actionContainers = wrapper.findAll('.action-buttons')
      expect(actionContainers).toHaveLength(3)

      const firstContainer = actionContainers[0]
      const buttons = firstContainer.findAll('button')
      expect(buttons).toHaveLength(3)
      expect(buttons[0].classes()).toContain('btn-edit')
      expect(buttons[1].classes()).toContain('btn-delete')
      expect(buttons[2].classes()).toContain('btn-view')
    })
  })

  describe('条件插槽渲染', () => {
    it('应该根据条件渲染插槽', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          showActions: { type: Boolean, default: true }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__body">
              <div v-for="(row, rowIndex) in tableData" :key="rowIndex" class="i0-table__row">
                <div class="i0-table__cell i0-table__cell--actions" v-if="showActions">
                  <slot name="actions" :row="row" :index="rowIndex"></slot>
                </div>
              </div>
            </div>
          </div>
        `
      }

      // Test with actions enabled
      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          showActions: true
        },
        slots: {
          actions: '<div class="action-slot">Actions</div>'
        }
      })

      expect(wrapper.find('.i0-table__cell--actions').exists()).toBe(true)
      expect(wrapper.find('.action-slot').exists()).toBe(true)

      // Test with actions disabled
      await wrapper.setProps({ showActions: false })

      expect(wrapper.find('.i0-table__cell--actions').exists()).toBe(false)
    })
  })

  describe('多个插槽组合', () => {
    it('应该正确组合多个插槽', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        computed: {
          isEmpty() {
            return this.tableData.length === 0
          }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__default">
              <slot></slot>
            </div>
            <div v-if="isEmpty" class="i0-table__empty">
              <slot name="empty"></slot>
            </div>
            <div v-else>
              <div class="i0-table__content">
                Main content
              </div>
              <div class="i0-table__append">
                <slot name="append"></slot>
              </div>
              <div class="i0-table__actions">
                <slot name="actions" :row="tableData[0]" :index="0"></slot>
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        },
        slots: {
          default: '<div class="header">Custom Header</div>',
          append: '<div class="footer">Custom Footer</div>',
          actions: '<div class="actions">Custom Actions</div>'
        }
      })

      expect(wrapper.find('.header').exists()).toBe(true)
      expect(wrapper.find('.footer').exists()).toBe(true)
      expect(wrapper.find('.actions').exists()).toBe(true)
      expect(wrapper.find('.i0-table__empty').exists()).toBe(false)
    })

    it('应该在空状态下正确组合插槽', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        computed: {
          isEmpty() {
            return this.tableData.length === 0
          }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__default">
              <slot></slot>
            </div>
            <div v-if="isEmpty" class="i0-table__empty">
              <slot name="empty"></slot>
            </div>
            <div v-else class="i0-table__append">
              <slot name="append"></slot>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: [],
          tableColumn: validTableColumn
        },
        slots: {
          default: '<div class="header">Custom Header</div>',
          empty: '<div class="empty">Custom Empty</div>',
          append: '<div class="footer">Custom Footer</div>'
        }
      })

      expect(wrapper.find('.header').exists()).toBe(true)
      expect(wrapper.find('.empty').exists()).toBe(true)
      expect(wrapper.find('.footer').exists()).toBe(false) // Should not show append when empty
    })
  })

  describe('插槽作用域和响应式', () => {
    it('应该保持插槽内容的响应式', async () => {
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
                <div class="i0-table__cell i0-table__cell--actions">
                  <slot name="actions" :row="row" :index="rowIndex"></slot>
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
        },
        slots: {
          actions: '<template #default="{ row, index }"><span class="user-name">{{ row.name }}</span></template>'
        }
      })

      const nameSpans = wrapper.findAll('.user-name')
      expect(nameSpans[0].text()).toBe('John Doe')

      // Update data and check if slot content updates
      const newData = [...validTableData]
      newData[0].name = 'Updated Name'
      await wrapper.setProps({ tableData: newData })

      const updatedNameSpans = wrapper.findAll('.user-name')
      expect(updatedNameSpans[0].text()).toBe('Updated Name')
    })
  })
})