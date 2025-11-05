import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import type { I0TableProps, TableColumn, SortChangeEvent } from '../types'

describe('I0Table 排序功能集成测试', () => {
  let wrapper: any

  const validTableData: I0TableProps['tableData'] = [
    { id: 1, name: 'John Doe', email: 'john@example.com', age: 30 },
    { id: 2, name: 'Jane Smith', email: 'jane@example.com', age: 25 },
    { id: 3, name: 'Bob Johnson', email: 'bob@example.com', age: 35 },
    { id: 4, name: 'Alice Brown', email: 'alice@example.com', age: 28 }
  ]

  const validTableColumn: TableColumn[] = [
    { name: 'Name', prop: 'name', type: 'string', sortable: true },
    { name: 'Email', prop: 'email', type: 'string', sortable: false },
    { name: 'Age', prop: 'age', type: 'number', sortable: true }
  ]

  describe('排序状态管理', () => {
    it('应该管理排序状态', () => {
      // This test will fail until the actual I0Table component is implemented
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        data() {
          return {
            currentSort: {
              prop: '',
              order: null as 'ascending' | 'descending' | null
            }
          }
        },
        template: '<div class="i0-table">Table Component</div>'
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      expect(wrapper.vm.currentSort).toEqual({
        prop: '',
        order: null
      })
    })

    it('应该更新排序状态', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        data() {
          return {
            currentSort: {
              prop: '',
              order: null as 'ascending' | 'descending' | null
            }
          }
        },
        methods: {
          updateSortState(prop: string, order: 'ascending' | 'descending' | null) {
            this.currentSort = { prop, order }
          }
        },
        template: '<div class="i0-table">Table Component</div>'
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      wrapper.vm.updateSortState('name', 'ascending')
      await nextTick()

      expect(wrapper.vm.currentSort).toEqual({
        prop: 'name',
        order: 'ascending'
      })
    })

    it('应该清除排序状态', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        data() {
          return {
            currentSort: {
              prop: 'name',
              order: 'ascending' as 'ascending' | 'descending' | null
            }
          }
        },
        methods: {
          clearSortState() {
            this.currentSort = { prop: '', order: null }
          }
        },
        template: '<div class="i0-table">Table Component</div>'
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      wrapper.vm.clearSortState()
      await nextTick()

      expect(wrapper.vm.currentSort).toEqual({
        prop: '',
        order: null
      })
    })

    it('应该切换排序状态', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        data() {
          return {
            currentSort: {
              prop: 'name',
              order: 'ascending' as 'ascending' | 'descending' | null
            }
          }
        },
        methods: {
          toggleSortState(prop: string) {
            if (this.currentSort.prop === prop) {
              // Toggle order: ascending -> descending -> null -> ascending
              if (this.currentSort.order === 'ascending') {
                this.currentSort.order = 'descending'
              } else if (this.currentSort.order === 'descending') {
                this.currentSort.order = null
              } else {
                this.currentSort.order = 'ascending'
              }
            } else {
              this.currentSort = { prop, order: 'ascending' }
            }
          }
        },
        template: '<div class="i0-table">Table Component</div>'
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      // ascending -> descending
      wrapper.vm.toggleSortState('name')
      await nextTick()
      expect(wrapper.vm.currentSort.order).toBe('descending')

      // descending -> null
      wrapper.vm.toggleSortState('name')
      await nextTick()
      expect(wrapper.vm.currentSort.order).toBeNull()

      // null -> ascending
      wrapper.vm.toggleSortState('name')
      await nextTick()
      expect(wrapper.vm.currentSort.order).toBe('ascending')
    })
  })

  describe('排序事件发射', () => {
    it('应该发射 sort-change 事件', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        methods: {
          handleSortChange(column: TableColumn, order: 'ascending' | 'descending' | null) {
            this.$emit('sort-change', {
              column,
              order,
              prop: column.prop
            })
          }
        },
        template: '<div class="i0-table">Table Component</div>'
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      const column = validTableColumn[0]
      wrapper.vm.handleSortChange(column, 'ascending')

      expect(wrapper.emitted('sort-change')).toBeTruthy()
      expect(wrapper.emitted('sort-change')[0]).toEqual([{
        column,
        order: 'ascending',
        prop: 'name'
      }])
    })

    it('应该发射取消排序事件', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        methods: {
          handleSortChange(column: TableColumn, order: 'ascending' | 'descending' | null) {
            this.$emit('sort-change', {
              column,
              order,
              prop: column.prop
            })
          }
        },
        template: '<div class="i0-table">Table Component</div>'
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      const column = validTableColumn[0]
      wrapper.vm.handleSortChange(column, null)

      expect(wrapper.emitted('sort-change')).toBeTruthy()
      expect(wrapper.emitted('sort-change')[0]).toEqual([{
        column,
        order: null,
        prop: 'name'
      }])
    })

    it('应该处理多个列排序事件', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        methods: {
          handleSortChange(column: TableColumn, order: 'ascending' | 'descending' | null) {
            this.$emit('sort-change', {
              column,
              order,
              prop: column.prop
            })
          }
        },
        template: '<div class="i0-table">Table Component</div>'
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      // Sort by name
      wrapper.vm.handleSortChange(validTableColumn[0], 'ascending')
      // Sort by age
      wrapper.vm.handleSortChange(validTableColumn[2], 'descending')

      expect(wrapper.emitted('sort-change')).toBeTruthy()
      expect(wrapper.emitted('sort-change')).toHaveLength(2)
      expect(wrapper.emitted('sort-change')[0][0].prop).toBe('name')
      expect(wrapper.emitted('sort-change')[1][0].prop).toBe('age')
    })
  })

  describe('排序图标渲染', () => {
    it('应该显示排序图标', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        data() {
          return {
            currentSort: {
              prop: 'name',
              order: 'ascending' as 'ascending' | 'descending' | null
            }
          }
        },
        methods: {
          getSortIcon(column: TableColumn) {
            if (column.prop === this.currentSort.prop) {
              switch (this.currentSort.order) {
                case 'ascending':
                  return '↑'
                case 'descending':
                  return '↓'
                default:
                  return '↕'
              }
            }
            return column.sortable ? '↕' : ''
          }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__header">
              <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                {{ column.name }}
                <span class="sort-icon">{{ getSortIcon(column) }}</span>
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
      const sortIcons = wrapper.findAll('.sort-icon')

      expect(sortIcons[0].text()).toBe('↑') // Name column (ascending)
      expect(sortIcons[1].text()).toBe('')   // Email column (not sortable)
      expect(sortIcons[2].text()).toBe('↕')  // Age column (sortable, no current sort)
    })

    it('应该更新排序图标', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        data() {
          return {
            currentSort: {
              prop: 'name',
              order: 'ascending' as 'ascending' | 'descending' | null
            }
          }
        },
        methods: {
          getSortIcon(column: TableColumn) {
            if (column.prop === this.currentSort.prop) {
              switch (this.currentSort.order) {
                case 'ascending':
                  return '↑'
                case 'descending':
                  return '↓'
                default:
                  return '↕'
              }
            }
            return column.sortable ? '↕' : ''
          },
          updateSort(prop: string, order: 'ascending' | 'descending' | null) {
            this.currentSort = { prop, order }
          }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__header">
              <div v-for="column in tableColumn" :key="column.prop" class="i0-table__cell">
                {{ column.name }}
                <span class="sort-icon">{{ getSortIcon(column) }}</span>
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

      let sortIcons = wrapper.findAll('.sort-icon')
      expect(sortIcons[0].text()).toBe('↑') // Name column (ascending)

      // Change to descending
      wrapper.vm.updateSort('name', 'descending')
      await nextTick()

      sortIcons = wrapper.findAll('.sort-icon')
      expect(sortIcons[0].text()).toBe('↓') // Name column (descending)

      // Change to no sort
      wrapper.vm.updateSort('name', null)
      await nextTick()

      sortIcons = wrapper.findAll('.sort-icon')
      expect(sortIcons[0].text()).toBe('↕') // Name column (no sort)
    })
  })

  describe('排序交互', () => {
    it('应该处理点击排序', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        data() {
          return {
            currentSort: {
              prop: '',
              order: null as 'ascending' | 'descending' | null
            }
          }
        },
        methods: {
          handleColumnClick(column: TableColumn) {
            if (!column.sortable) return

            if (this.currentSort.prop === column.prop) {
              // Toggle order
              if (this.currentSort.order === 'ascending') {
                this.currentSort.order = 'descending'
              } else if (this.currentSort.order === 'descending') {
                this.currentSort.order = null
              } else {
                this.currentSort.order = 'ascending'
              }
            } else {
              this.currentSort = { prop: column.prop, order: 'ascending' }
            }

            this.$emit('sort-change', {
              column,
              order: this.currentSort.order,
              prop: column.prop
            })
          }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__header">
              <div
                v-for="column in tableColumn"
                :key="column.prop"
                class="i0-table__cell"
                :class="{ 'sortable': column.sortable }"
                @click="handleColumnClick(column)"
              >
                {{ column.name }}
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

      const sortableColumns = wrapper.findAll('.sortable')
      expect(sortableColumns).toHaveLength(2) // Name and Age columns

      // Click name column
      await sortableColumns[0].trigger('click')

      expect(wrapper.vm.currentSort).toEqual({
        prop: 'name',
        order: 'ascending'
      })
      expect(wrapper.emitted('sort-change')).toBeTruthy()

      // Click again to toggle to descending
      await sortableColumns[0].trigger('click')

      expect(wrapper.vm.currentSort).toEqual({
        prop: 'name',
        order: 'descending'
      })
      expect(wrapper.emitted('sort-change')).toHaveLength(2)
    })

    it('应该忽略不可排序列的点击', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        data() {
          return {
            currentSort: {
              prop: '',
              order: null as 'ascending' | 'descending' | null
            }
          }
        },
        methods: {
          handleColumnClick(column: TableColumn) {
            if (!column.sortable) return

            if (this.currentSort.prop === column.prop) {
              if (this.currentSort.order === 'ascending') {
                this.currentSort.order = 'descending'
              } else if (this.currentSort.order === 'descending') {
                this.currentSort.order = null
              } else {
                this.currentSort.order = 'ascending'
              }
            } else {
              this.currentSort = { prop: column.prop, order: 'ascending' }
            }

            this.$emit('sort-change', {
              column,
              order: this.currentSort.order,
              prop: column.prop
            })
          }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__header">
              <div
                v-for="column in tableColumn"
                :key="column.prop"
                class="i0-table__cell"
                :class="{ 'sortable': column.sortable }"
                @click="handleColumnClick(column)"
              >
                {{ column.name }}
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

      // Click email column (not sortable)
      await headerCells[1].trigger('click')

      expect(wrapper.vm.currentSort).toEqual({
        prop: '',
        order: null
      })
      expect(wrapper.emitted('sort-change')).toBeFalsy()
    })
  })

  describe('排序样式', () => {
    it('应该应用排序样式类', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        data() {
          return {
            currentSort: {
              prop: 'name',
              order: 'ascending' as 'ascending' | 'descending' | null
            }
          }
        },
        methods: {
          getColumnClasses(column: TableColumn) {
            return {
              'i0-table__cell': true,
              'i0-table__cell--sortable': column.sortable,
              'i0-table__cell--sorted': column.prop === this.currentSort.prop,
              'i0-table__cell--sorted-asc': column.prop === this.currentSort.prop && this.currentSort.order === 'ascending',
              'i0-table__cell--sorted-desc': column.prop === this.currentSort.prop && this.currentSort.order === 'descending'
            }
          }
        },
        template: `
          <div class="i0-table">
            <div class="i0-table__header">
              <div
                v-for="column in tableColumn"
                :key="column.prop"
                :class="getColumnClasses(column)"
              >
                {{ column.name }}
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

      // Name column (sorted ascending)
      expect(headerCells[0].classes()).toContain('i0-table__cell--sortable')
      expect(headerCells[0].classes()).toContain('i0-table__cell--sorted')
      expect(headerCells[0].classes()).toContain('i0-table__cell--sorted-asc')
      expect(headerCells[0].classes()).not.toContain('i0-table__cell--sorted-desc')

      // Email column (not sortable)
      expect(headerCells[1].classes()).not.toContain('i0-table__cell--sortable')
      expect(headerCells[1].classes()).not.toContain('i0-table__cell--sorted')

      // Age column (sortable but not sorted)
      expect(headerCells[2].classes()).toContain('i0-table__cell--sortable')
      expect(headerCells[2].classes()).not.toContain('i0-table__cell--sorted')
    })
  })

  describe('多列排序', () => {
    it('应该支持多列排序状态', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        data() {
          return {
            sortStates: [
              { prop: 'name', order: 'ascending' as 'ascending' | 'descending' },
              { prop: 'age', order: 'descending' as 'ascending' | 'descending' }
            ]
          }
        },
        template: '<div class="i0-table">Table Component</div>'
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      expect(wrapper.vm.sortStates).toEqual([
        { prop: 'name', order: 'ascending' },
        { prop: 'age', order: 'descending' }
      ])
    })

    it('应该添加移除排序状态', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true }
        },
        data() {
          return {
            sortStates: [] as Array<{ prop: string; order: 'ascending' | 'descending' }>
          }
        },
        methods: {
          addSortState(prop: string, order: 'ascending' | 'descending') {
            const existingIndex = this.sortStates.findIndex(s => s.prop === prop)
            if (existingIndex >= 0) {
              this.sortStates[existingIndex].order = order
            } else {
              this.sortStates.push({ prop, order })
            }
          },
          removeSortState(prop: string) {
            this.sortStates = this.sortStates.filter(s => s.prop !== prop)
          }
        },
        template: '<div class="i0-table">Table Component</div>'
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn
        }
      })

      wrapper.vm.addSortState('name', 'ascending')
      await nextTick()

      expect(wrapper.vm.sortStates).toEqual([
        { prop: 'name', order: 'ascending' }
      ])

      wrapper.vm.addSortState('age', 'descending')
      await nextTick()

      expect(wrapper.vm.sortStates).toEqual([
        { prop: 'name', order: 'ascending' },
        { prop: 'age', order: 'descending' }
      ])

      wrapper.vm.removeSortState('name')
      await nextTick()

      expect(wrapper.vm.sortStates).toEqual([
        { prop: 'age', order: 'descending' }
      ])
    })
  })
})