/**
 * Admin Portal 菜单配置
 *
 * 包含 Basic、Service、Business、System 和 Logs 相关菜单
 */
import type { MenuConfig } from '@I0/shared/types';
import { routes } from 'vue-router/auto-routes';
import { generateAdvancedMenu } from '@I0/shared/utils/menu';
import {
  House,
  ChatDotRound,
  Message,
  OfficeBuilding,
  Folder,
  DataLine,
  Location,
  School,
  DataAnalysis,
  User,
  Key,
  Lock,
  MessageBox,
  Guide
} from '@element-plus/icons-vue';

/**
 * 简化的菜单配置
 */
export const simpleMenuConfig: MenuConfig = {
  menus: [
    {
      title: 'Basic',
      children: [
        { name: 'Home', icon: House },
        { name: 'AIChat', icon: ChatDotRound },
        { name: 'Message', icon: Message }
      ]
    },
    {
      title: 'Service',
      children: [
        { name: 'Clients', icon: OfficeBuilding },
        { name: 'Projects', icon: Folder },
        { name: 'Reports', icon: DataLine }
      ]
    },
    {
      title: 'Business',
      children: [
        { name: 'Entity', icon: School },
        { name: 'Location', icon: Location },
        { name: 'ServiceType', icon: Guide },
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

const advancedMenuConfig = generateAdvancedMenu(simpleMenuConfig, routes);

export default advancedMenuConfig;
