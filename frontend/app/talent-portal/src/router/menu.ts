/**
 * Talent Portal 菜单配置
 *
 * 包含 Basic、HR、Service 相关菜单
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
        { name: 'Message' }
      ]
    },
    {
      title: 'HR',
      children: [
        { name: 'Profiles' },
        { name: 'Times' },
        { name: 'Expenses' },
        { name: 'Payslips' }
      ]
    },
    {
      title: 'Service',
      children: [
        { name: 'Invoice' },
        { name: 'WorkVisa' },
        { name: 'Contract' }
      ]
    }
  ]
}

// 使用 generateAdvancedMenu 生成完整的菜单配置
const advancedMenuConfig = generateAdvancedMenu(simpleMenuConfig, routes)

export default advancedMenuConfig