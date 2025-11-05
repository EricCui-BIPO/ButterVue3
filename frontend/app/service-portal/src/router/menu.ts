/**
 * Service Portal 菜单配置
 *
 * 包含 Basic、HR、Service 和 Advanced 相关菜单
 */

import type { MenuConfig } from '@I0/shared/types'
import { generateAdvancedMenu } from '@I0/shared/utils/menu'
import { routes } from 'vue-router/auto-routes'

// 简化的菜单配置，只包含基本信息
export const simpleMenuConfig: MenuConfig = {
  menus: [
    {
      title: 'Basic',
      children: [
        { name: 'Home' },
        { name: 'Message' },
        { name: 'Calendar' }
      ]
    },
    {
      title: 'HR',
      children: [
        { name: 'Talents' },
        { name: 'Times' },
        { name: 'Expenses' },
        { name: 'Payroll' }
      ]
    },
    {
      title: 'Service',
      children: [
        { name: 'Orders' },
        { name: 'Projects' },
        { name: 'Payments' },
        { name: 'Invoice' },
        { name: 'CalendarSchedule' }
      ]
    },
    {
      title: 'Advanced',
      children: [
        { name: 'Reports' },
        { name: 'Compliance' },
        { name: 'Insights' }
      ]
    }
  ]
}

// 使用 generateAdvancedMenu 生成完整的菜单配置
const advancedMenuConfig = generateAdvancedMenu(simpleMenuConfig, routes)

export default advancedMenuConfig