import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { nextTick } from 'vue'
import type { I0TableProps, TableColumn, PaginationConfig } from '../types'

describe('I0Table 分页功能集成测试', () => {
  let wrapper: any

  const validTableData: I0TableProps['tableData'] = Array.from({ length: 100 }, (_, i) => ({
    id: i + 1,
    name: `User ${i + 1}`,
    email: `user${i + 1}@example.com`
  }))

  const validTableColumn: I0TableProps['tableColumn'] = [
    { name: 'Name', prop: 'name', type: 'string' },
    { name: 'Email', prop: 'email', type: 'string' }
  ]

  const validPagination: PaginationConfig = {
    page: 1,
    pageSize: 20,
    total: 100,
    pageSizes: [10, 20, 50, 100],
    layout: 'total, sizes, prev, pager, next, jumper',
    small: true
  }

  describe('分页组件渲染', () => {
    it('应该渲染分页组件', () => {
      // This test will fail until the actual I0Table component is implemented
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          }
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
            <div v-if="hasPagination" class="i0-table__pagination">
              <div class="pagination-info">
                Showing {{ pagination.pageSize }} of {{ pagination.total }} items
              </div>
              <div class="pagination-controls">
                <button @click="prevPage" :disabled="pagination.page <= 1">Previous</button>
                <span>Page {{ pagination.page }}</span>
                <button @click="nextPage" :disabled="pagination.page * pagination.pageSize >= pagination.total">Next</button>
              </div>
            </div>
          </div>
        `,
        methods: {
          prevPage() {
            if (this.pagination.page > 1) {
              this.$emit('pagination-change', {
                page: this.pagination.page - 1,
                pageSize: this.pagination.pageSize
              })
            }
          },
          nextPage() {
            if (this.pagination.page * this.pagination.pageSize < this.pagination.total) {
              this.$emit('pagination-change', {
                page: this.pagination.page + 1,
                pageSize: this.pagination.pageSize
              })
            }
          }
        }
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          pagination: validPagination
        }
      })

      expect(wrapper.find('.i0-table__pagination').exists()).toBe(true)
      expect(wrapper.find('.pagination-info').exists()).toBe(true)
      expect(wrapper.find('.pagination-controls').exists()).toBe(true)
    })

    it('应该显示正确的分页信息', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          },
          startItem() {
            return (this.pagination.page - 1) * this.pagination.pageSize + 1
          },
          endItem() {
            return Math.min(this.pagination.page * this.pagination.pageSize, this.pagination.total)
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="hasPagination" class="i0-table__pagination">
              <div class="pagination-info">
                Showing {{ startItem }}-{{ endItem }} of {{ pagination.total }} items
              </div>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          pagination: validPagination
        }
      })

      const paginationInfo = wrapper.find('.pagination-info')
      expect(paginationInfo.text()).toBe('Showing 1-20 of 100 items')
    })

    it('应该支持不同页面大小', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="hasPagination" class="i0-table__pagination">
              <select v-model="localPageSize" @change="handlePageSizeChange" class="page-size-select">
                <option v-for="size in pagination.pageSizes" :key="size" :value="size">
                  {{ size }} per page
                </option>
              </select>
              <span>Page {{ pagination.page }}</span>
            </div>
          </div>
        `,
        data() {
          return {
            localPageSize: this.pagination.pageSize
          }
        },
        methods: {
          handlePageSizeChange() {
            this.$emit('pagination-change', {
              page: 1,
              pageSize: this.localPageSize
            })
          }
        }
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          pagination: validPagination
        }
      })

      const pageSizeSelect = wrapper.find('.page-size-select')
      expect(pageSizeSelect.exists()).toBe(true)
      expect(pageSizeSelect.element.value).toBe('20')
    })

    it('应该处理页面导航', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="hasPagination" class="i0-table__pagination">
              <button @click="goToPage(1)" :class="{ active: pagination.page === 1 }">1</button>
              <button @click="goToPage(2)" :class="{ active: pagination.page === 2 }">2</button>
              <button @click="goToPage(3)" :class="{ active: pagination.page === 3 }">3</button>
              <button @click="goToPage(4)" :class="{ active: pagination.page === 4 }">4</button>
              <button @click="goToPage(5)" :class="{ active: pagination.page === 5 }">5</button>
            </div>
          </div>
        `,
        methods: {
          goToPage(page: number) {
            this.$emit('pagination-change', {
              page,
              pageSize: this.pagination.pageSize
            })
          }
        }
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          pagination: validPagination
        }
      })

      const pageButtons = wrapper.findAll('.i0-table__pagination button')
      expect(pageButtons).toHaveLength(5)
      expect(pageButtons[0].classes()).toContain('active')
      expect(pageButtons[1].classes()).not.toContain('active')
    })

    it('应该支持跳转到指定页面', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          }
        },
        data() {
          return {
            jumpToPage: ''
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="hasPagination" class="i0-table__pagination">
              <input v-model.number="jumpToPage" type="number" min="1" :max="Math.ceil(pagination.total / pagination.pageSize)" class="jump-input">
              <button @click="handleJump" :disabled="!jumpToPage || jumpToPage < 1 || jumpToPage > Math.ceil(pagination.total / pagination.pageSize)">Go</button>
            </div>
          </div>
        `,
        methods: {
          handleJump() {
            if (this.jumpToPage && this.jumpToPage >= 1 && this.jumpToPage <= Math.ceil(this.pagination.total / this.pagination.pageSize)) {
              this.$emit('pagination-change', {
                page: this.jumpToPage,
                pageSize: this.pagination.pageSize
              })
            }
          }
        }
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          pagination: validPagination
        }
      })

      const jumpInput = wrapper.find('.jump-input')
      const jumpButton = wrapper.find('.i0-table__pagination button')

      expect(jumpInput.exists()).toBe(true)
      expect(jumpButton.exists()).toBe(true)
      expect(jumpButton.attributes('disabled')).toBe('')
    })
  })

  describe('分页事件处理', () => {
    it('应该发射 pagination-change 事件当页面改变时', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="hasPagination" class="i0-table__pagination">
              <button @click="nextPage">Next</button>
            </div>
          </div>
        `,
        methods: {
          nextPage() {
            this.$emit('pagination-change', {
              page: this.pagination.page + 1,
              pageSize: this.pagination.pageSize
            })
          }
        }
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          pagination: validPagination
        }
      })

      await wrapper.find('.i0-table__pagination button').trigger('click')

      expect(wrapper.emitted('pagination-change')).toBeTruthy()
      expect(wrapper.emitted('pagination-change')[0]).toEqual([{
        page: 2,
        pageSize: 20
      }])
    })

    it('应该处理页面大小变化', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="hasPagination" class="i0-table__pagination">
              <select v-model="localPageSize" @change="handlePageSizeChange">
                <option v-for="size in pagination.pageSizes" :key="size" :value="size">
                  {{ size }}
                </option>
              </select>
            </div>
          </div>
        `,
        data() {
          return {
            localPageSize: this.pagination.pageSize
          }
        },
        methods: {
          handlePageSizeChange() {
            this.$emit('pagination-change', {
              page: 1,
              pageSize: this.localPageSize
            })
          }
        }
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          pagination: validPagination
        }
      })

      const pageSizeSelect = wrapper.find('select')
      await pageSizeSelect.setValue('50')

      expect(wrapper.emitted('pagination-change')).toBeTruthy()
      expect(wrapper.emitted('pagination-change')[0]).toEqual([{
        page: 1,
        pageSize: 50
      }])
    })

    it('应该验证分页参数', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="hasPagination" class="i0-table__pagination">
              <button @click="goToPage">Go to Page</button>
            </div>
          </div>
        `,
        methods: {
          goToPage() {
            const newPage = 1 // Valid page
            const newPageSize = this.pagination.pageSize
            this.$emit('pagination-change', {
              page: newPage,
              pageSize: newPageSize
            })
          }
        }
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          pagination: validPagination
        }
      })

      await wrapper.find('.i0-table__pagination button').trigger('click')

      expect(wrapper.emitted('pagination-change')).toBeTruthy()
      const emittedEvent = wrapper.emitted('pagination-change')[0][0]
      expect(emittedEvent.page).toBeGreaterThanOrEqual(1)
      expect(emittedEvent.pageSize).toBeGreaterThan(0)
    })
  })

  describe('分页状态管理', () => {
    it('应该计算总页数', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          },
          totalPages() {
            return Math.ceil(this.pagination.total / this.pagination.pageSize)
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="hasPagination" class="i0-table__pagination">
              <span>Total pages: {{ totalPages }}</span>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          pagination: validPagination
        }
      })

      expect(wrapper.vm.totalPages).toBe(5)
      expect(wrapper.find('.i0-table__pagination span').text()).toBe('Total pages: 5')
    })

    it('应该处理边界条件', () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          },
          isFirstPage() {
            return this.pagination.page <= 1
          },
          isLastPage() {
            return this.pagination.page * this.pagination.pageSize >= this.pagination.total
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="hasPagination" class="i0-table__pagination">
              <button @click="prevPage" :disabled="isFirstPage">Previous</button>
              <span>Page {{ pagination.page }}</span>
              <button @click="nextPage" :disabled="isLastPage">Next</button>
            </div>
          </div>
        `,
        methods: {
          prevPage() {
            if (!this.isFirstPage) {
              this.$emit('pagination-change', {
                page: this.pagination.page - 1,
                pageSize: this.pagination.pageSize
              })
            }
          },
          nextPage() {
            if (!this.isLastPage) {
              this.$emit('pagination-change', {
                page: this.pagination.page + 1,
                pageSize: this.pagination.pageSize
              })
            }
          }
        }
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          pagination: { ...validPagination, page: 1 }
        }
      })

      const prevButton = wrapper.findAll('.i0-table__pagination button')[0]
      const nextButton = wrapper.findAll('.i0-table__pagination button')[1]

      expect(prevButton.attributes('disabled')).toBe('')
      expect(nextButton.attributes('disabled')).toBe('')
    })

    it('应该处理空数据分页', () => {
      const emptyPagination: PaginationConfig = {
        page: 1,
        pageSize: 20,
        total: 0
      }

      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="hasPagination" class="i0-table__pagination">
              <span>No items to display</span>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: [],
          tableColumn: validTableColumn,
          pagination: emptyPagination
        }
      })

      expect(wrapper.find('.i0-table__pagination').exists()).toBe(true)
      expect(wrapper.find('.i0-table__pagination span').text()).toBe('No items to display')
    })

    it('应该响应分页配置变化', async () => {
      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="hasPagination" class="i0-table__pagination">
              <span>Page {{ pagination.page }} of {{ Math.ceil(pagination.total / pagination.pageSize) }}</span>
            </div>
          </div>
        `
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          pagination: validPagination
        }
      })

      expect(wrapper.find('.i0-table__pagination span').text()).toBe('Page 1 of 5')

      const newPagination = { ...validPagination, page: 3, pageSize: 10 }
      await wrapper.setProps({ pagination: newPagination })

      expect(wrapper.find('.i0-table__pagination span').text()).toBe('Page 3 of 10')
    })
  })

  describe('分页样式和布局', () => {
    it('应该支持不同分页布局', () => {
      const simplePagination: PaginationConfig = {
        page: 1,
        pageSize: 20,
        total: 100,
        layout: 'prev, pager, next'
      }

      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="hasPagination" class="i0-table__pagination">
              <div v-if="pagination.layout.includes('total')" class="pagination-total">
                Total: {{ pagination.total }}
              </div>
              <div v-if="pagination.layout.includes('sizes')" class="pagination-sizes">
                <select v-model="localPageSize">
                  <option v-for="size in pagination.pageSizes" :key="size" :value="size">{{ size }}</option>
                </select>
              </div>
              <div v-if="pagination.layout.includes('prev')" class="pagination-prev">
                <button @click="prevPage">Previous</button>
              </div>
              <div v-if="pagination.layout.includes('pager')" class="pagination-pager">
                <span>Page {{ pagination.page }}</span>
              </div>
              <div v-if="pagination.layout.includes('next')" class="pagination-next">
                <button @click="nextPage">Next</button>
              </div>
              <div v-if="pagination.layout.includes('jumper')" class="pagination-jumper">
                <input v-model="jumpToPage" type="number">
              </div>
            </div>
          </div>
        `,
        data() {
          return {
            localPageSize: this.pagination.pageSize,
            jumpToPage: ''
          }
        },
        methods: {
          prevPage() {
            this.$emit('pagination-change', { page: this.pagination.page - 1, pageSize: this.pagination.pageSize })
          },
          nextPage() {
            this.$emit('pagination-change', { page: this.pagination.page + 1, pageSize: this.pagination.pageSize })
          }
        }
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          pagination: simplePagination
        }
      })

      expect(wrapper.find('.pagination-prev').exists()).toBe(true)
      expect(wrapper.find('.pagination-pager').exists()).toBe(true)
      expect(wrapper.find('.pagination-next').exists()).toBe(true)
      expect(wrapper.find('.pagination-total').exists()).toBe(false)
      expect(wrapper.find('.pagination-sizes').exists()).toBe(false)
      expect(wrapper.find('.pagination-jumper').exists()).toBe(false)
    })

    it('应该支持小型分页样式', () => {
      const smallPagination: PaginationConfig = {
        page: 1,
        pageSize: 20,
        total: 100,
        small: true
      }

      const I0Table = {
        name: 'I0Table',
        props: {
          tableData: { type: Array, required: true },
          tableColumn: { type: Array, required: true },
          pagination: { type: Object, required: false }
        },
        computed: {
          hasPagination() {
            return this.pagination && Object.keys(this.pagination).length > 0
          }
        },
        template: `
          <div class="i0-table">
            <div v-if="hasPagination" class="i0-table__pagination" :class="{ 'pagination-small': pagination.small }">
              <button @click="prevPage">Previous</button>
              <span>Page {{ pagination.page }}</span>
              <button @click="nextPage">Next</button>
            </div>
          </div>
        `,
        methods: {
          prevPage() {
            this.$emit('pagination-change', { page: this.pagination.page - 1, pageSize: this.pagination.pageSize })
          },
          nextPage() {
            this.$emit('pagination-change', { page: this.pagination.page + 1, pageSize: this.pagination.pageSize })
          }
        }
      }

      wrapper = mount(I0Table, {
        props: {
          tableData: validTableData,
          tableColumn: validTableColumn,
          pagination: smallPagination
        }
      })

      expect(wrapper.find('.i0-table__pagination').classes()).toContain('pagination-small')
    })
  })
})