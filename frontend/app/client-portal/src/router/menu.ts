/**
 * Client Portal 菜单配置
 *
 * 包含 Basic、HR、Service、Advanced、System 和 Logs 相关菜单
 */

import type { MenuConfig } from '@I0/shared/types';
import { generateAdvancedMenu } from '@I0/shared/utils/menu';
import { routes } from 'vue-router/auto-routes';
import {
  House,
  ChatDotRound,
  Calendar,
  Clock,
  ScaleToOriginal,
  CreditCard,
  DataLine,
  Message,
  DataAnalysis,
  User,
  Key,
  Lock,
  MessageBox,
  Printer,
  CircleCheck,
  DocumentCopy,
  FolderOpened,
  Money
} from '@element-plus/icons-vue';

// 简化的菜单配置，只包含基本信息
export const simpleMenuConfig: MenuConfig = {
  menus: [
    {
      title: 'Basic',
      children: [
        { name: 'Home', icon: House },
        { name: 'AIChat', icon: ChatDotRound },
        { name: 'Message', icon: Message },
        { name: 'Calendar', icon: Calendar }
      ]
    },
    {
      title: 'HR',
      children: [
        { name: 'Talents', icon: User },
        { name: 'Times', icon: Clock },
        { name: 'Expenses', icon: ScaleToOriginal },
        { name: 'Payroll', icon: CreditCard }
      ]
    },
    {
      title: 'Service',
      children: [
        { name: 'Orders', icon: DocumentCopy },
        { name: 'Projects', icon: FolderOpened },
        { name: 'Payments', icon: Money },
        { name: 'Invoice', icon: Printer }
      ]
    },
    {
      title: 'Advanced',
      children: [
        { name: 'Reports', icon: DataLine },
        { name: 'Compliance', icon: CircleCheck },
        { name: 'Insights', icon: DataAnalysis }
      ]
    },
    {
      title: 'System',
      children: [
        { name: 'Users', icon: User },
        { name: 'Roles', icon: Key },
        { name: 'Access', icon: Lock }
      ]
    },
    {
      title: 'Logs',
      children: [
        { name: 'ActiveLog', icon: MessageBox },
        { name: 'EmailLog', icon: MessageBox },
        { name: 'ErrorLog', icon: MessageBox },
        { name: 'DataLog', icon: MessageBox }
      ]
    }
  ]
};

// 使用 generateAdvancedMenu 生成完整的菜单配置
const advancedMenuConfig = generateAdvancedMenu(simpleMenuConfig, routes);

export default advancedMenuConfig;
