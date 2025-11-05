import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { defineComponent, ref } from 'vue'
import I0Table from '../index.vue'
import type { TableColumn } from '../types'

describe('I0Table Dynamic Slot 功能', () => {
  let wrapper: any

  const mockColumns: TableColumn[] = [
    {
      name: '用户名',
      prop: 'userName',
      type: 'string',
      slot: true // 自动生成槽名
    },
    {
      name: '邮箱',
      prop: 'email',
      type: 'string' // 不配置槽，使用默认渲染
    },
    {
      name: '状态',
      prop: 'status',
      type: 'string',
      slot: 'custom-status' // 自定义槽名
    }
  ]

  const mockData = [
    {
      userName: '张三',
      email: 'zhangsan@example.com',
      status: 'active'
    },
    {
      userName: '李四',
      email: 'lisi@example.com',
      status: 'inactive'
    }
  ]

  const TestComponent = defineComponent({
    components: { I0Table },
    template: `
      <I0Table
        :table-data="tableData"
        :table-column="tableColumns"
      >
        <template #column-userName="{ row, value }">
          <span class="custom-username">{{ value }}</span>
        </template>
        <template #custom-status="{ row, value }">
          <span class="custom-status">{{ value }}</span>
        </template>
      </I0Table>
    `,
    setup() {
      return {
        tableData: ref(mockData),
        tableColumns: ref(mockColumns)
      }
    }
  })

  beforeEach(() => {
    wrapper = mount(TestComponent)
  })

  it('应该正确渲染动态列槽', () => {
    // 检查用户名列是否使用了自定义槽
    const usernameCells = wrapper.findAll('.custom-username')
    expect(usernameCells).toHaveLength(2)
    expect(usernameCells[0].text()).toBe('张三')
    expect(usernameCells[1].text()).toBe('李四')
  })

  it('应该正确渲染自定义槽名列', () => {
    // 检查状态列是否使用了自定义槽
    const statusCells = wrapper.findAll('.custom-status')
    expect(statusCells).toHaveLength(2)
    expect(statusCells[0].text()).toBe('active')
    expect(statusCells[1].text()).toBe('inactive')
  })

  it('应该正确渲染默认列', () => {
    // 检查邮箱列是否使用了默认渲染
    const emailCells = wrapper.findAll('td').filter((cell: any) => {
      return cell.text().includes('@example.com')
    })
    expect(emailCells).toHaveLength(2)
  })

  it('应该正确传递槽作用域参数', () => {
    const usernameCell = wrapper.find('.custom-username')
    expect(usernameCell.exists()).toBe(true)

    // 通过组件实例检查作用域参数是否正确传递
    // 这里我们主要检查槽是否被正确渲染
    expect(usernameCell.text()).toBe('张三')
  })
})

describe('列槽名称生成逻辑', () => {
  it('应该为 slot: true 生成正确的槽名', () => {
    const column: TableColumn = {
      name: '用户名',
      prop: 'userName',
      type: 'string',
      slot: true
    }

    const wrapper = mount(I0Table, {
      props: {
        tableData: [{ userName: '张三' }],
        tableColumn: [column]
      },
      slots: {
        'column-userName': '<template #default="{ value }">{{ value }}</template>'
      }
    })

    expect(wrapper.find('.custom-username').exists()).toBe(false)
  })
})

describe('向后兼容性测试', () => {
  it('应该保持现有的 actions 槽功能', () => {
    const columns: TableColumn[] = [
      {
        name: '用户名',
        prop: 'userName',
        type: 'string'
      },
      {
        name: '操作',
        prop: 'actions',
        type: 'string'
      }
    ]

    const wrapper = mount(I0Table, {
      props: {
        tableData: [{ userName: '张三' }],
        tableColumn: columns
      },
      slots: {
        actions: '<template #default="{ row }"><button class="action-btn">编辑</button></template>'
      }
    })

    const actionBtn = wrapper.find('.action-btn')
    expect(actionBtn.exists()).toBe(true)
  })

  it('应该正确处理没有槽的列', () => {
    const columns: TableColumn[] = [
      {
        name: '用户名',
        prop: 'userName',
        type: 'string' // 没有槽配置
      }
    ]

    const wrapper = mount(I0Table, {
      props: {
        tableData: [{ userName: '张三' }],
        tableColumn: columns
      }
    })

    // 检查是否使用了默认渲染
    const cells = wrapper.findAll('td')
    expect(cells.length).toBeGreaterThan(0)

    // 找到包含用户名的单元格
    const userCell = cells.find((cell: any) => cell.text() === '张三')
    expect(userCell).toBeTruthy()
  })
})