/**
 * 菜单工具函数
 *
 * 提供菜单数据处理、过滤、查找等工具函数
 */

import type {
  MenuItem,
  MenuGroup,
  MenuConfig,
  MenuFilterOptions,
  PermissionChecker
} from '@I0/shared/types';

// 获取菜单项的索引（用于el-menu的index属性）
export function getMenuItemIndex(item: MenuItem | any): string {
  return item.name || '';
}

/**
 * 根据菜单索引查找菜单项
 * 支持多种索引方式：meta.index、path、name
 */
export function findMenuItemByIndex(groups: MenuGroup[], index: string): MenuItem | null {
  for (const group of groups) {
    if (group.children) {
      for (const item of group.children) {
        // 检查多种索引方式
        const itemIndex = getMenuItemIndex(item);
        if (itemIndex === index) {
          return item;
        }

        // 递归检查子项
        if (item.children) {
          const found = findMenuItemByIndex([{ children: item.children }], index);
          if (found) {
            return found;
          }
        }
      }
    }
  }
  return null;
}

/**
 * 根据路由路径查找对应的菜单项
 */
export function findMenuItemByRoute(groups: MenuGroup[], route: any): MenuItem | null {
  const routeIndex = getMenuItemIndex(route);
  return findMenuItemByIndex(groups, routeIndex);
}

/**
 * 获取菜单项的完整路径
 */
export function getMenuItemPath(groups: MenuGroup[], targetIndex: string): string[] {
  function findPath(items: MenuItem[], path: string[] = []): string[] | null {
    for (const item of items) {
      const itemIndex = item.meta?.index || '';
      const currentPath = [...path, itemIndex];

      if (itemIndex === targetIndex) {
        return currentPath;
      }

      if (item.children) {
        const found = findPath(item.children, currentPath);
        if (found) {
          return found;
        }
      }
    }
    return null;
  }

  for (const group of groups) {
    if (group.children) {
      const path = findPath(group.children);
      if (path) {
        return path;
      }
    }
  }

  return [];
}

/**
 * 过滤菜单项（根据权限、隐藏状态等）
 */
export function filterMenuGroups(
  groups: MenuGroup[],
  options: MenuFilterOptions = {}
): MenuGroup[] {
  const { permissionChecker, showHidden = false } = options;

  return groups
    .filter(group => {
      // 过滤隐藏的菜单组
      if (!showHidden && group.meta?.hidden) {
        return false;
      }

      // 检查菜单组权限
      if (group.meta?.permission && permissionChecker) {
        return permissionChecker(group.meta.permission);
      }

      return true;
    })
    .map(group => ({
      ...group,
      children: group.children ? filterMenuItems(group.children, options) : undefined
    }))
    .filter(group => group.children && group.children.length > 0); // 移除空的菜单组
}

/**
 * 过滤菜单项
 */
export function filterMenuItems(items: MenuItem[], options: MenuFilterOptions = {}): MenuItem[] {
  const { permissionChecker, showHidden = false, customFilter } = options;

  return items
    .filter(item => {
      // 过滤隐藏的菜单项
      if (!showHidden && item.meta?.hidden) {
        return false;
      }

      // 检查菜单项权限
      if (item.meta?.permission && permissionChecker) {
        return permissionChecker(item.meta.permission);
      }

      // 自定义过滤
      if (customFilter) {
        return customFilter(item);
      }

      return true;
    })
    .map(item => ({
      ...item,
      children: item.children ? filterMenuItems(item.children, options) : undefined
    }))
    .filter(item => {
      // 如果有子菜单，确保至少有一个可见的子菜单项
      if (item.children) {
        return item.children.length > 0;
      }
      return true;
    });
}

/**
 * 根据当前路由获取激活的菜单项索引
 */
export function getActiveMenuIndex(groups: MenuGroup[], currentRoute: any): string {
  // 首先尝试精确匹配
  const exactMatch = findMenuItemByRoute(groups, currentRoute);
  if (exactMatch) {
    return getMenuItemIndex(exactMatch);
  }

  // 然后尝试部分匹配（找到最长匹配的路径）
  const allItems = groups.flatMap(group =>
    group.children ? flattenMenuItems(group.children) : []
  );
  let bestMatch = '';
  let bestMatchLength = 0;

  for (const item of allItems) {
    if (item.path && currentRoute.path.startsWith(item.path)) {
      if (item.path.length > bestMatchLength) {
        bestMatch = getMenuItemIndex(item);
        bestMatchLength = item.path.length;
      }
    }
  }

  return bestMatch;
}

/**
 * 扁平化菜单项（将嵌套的菜单项转换为扁平数组）
 */
export function flattenMenuItems(items: MenuItem[]): MenuItem[] {
  const result: MenuItem[] = [];

  function flatten(items: MenuItem[]) {
    for (const item of items) {
      result.push(item);
      if (item.children) {
        flatten(item.children);
      }
    }
  }

  flatten(items);
  return result;
}

/**
 * 创建默认权限检查器
 */
export function createPermissionChecker(userPermissions: string[]): PermissionChecker {
  return (permission: string | string[]) => {
    if (typeof permission === 'string') {
      return userPermissions.includes(permission);
    }

    // 数组权限，需要满足所有权限
    return permission.every(p => userPermissions.includes(p));
  };
}

/**
 * 根据简单菜单配置和路由信息生成完整的高级菜单配置
 * @param simpleMenu 简化的菜单配置，支持树形结构
 * @param routes 自动生成的路由配置
 * @returns 完整的高级菜单配置
 */
export function generateAdvancedMenu(simpleMenu: MenuConfig, routes: any[]): MenuConfig {
  // 创建路由名称到路由信息的映射
  const routeMap = new Map<string, any>();
  // 创建路由名称到完整路径的映射
  const routePathMap = new Map<string, string>();

  // 递归遍历路由，建立映射关系
  function buildRouteMap(routeList: any[], parentPath: string = '') {
    routeList.forEach(route => {
      if (route.name && typeof route.name === 'string') {
        routeMap.set(route.name, route);
        
        // 计算完整路径：父路径 + 当前路径
        const fullPath = parentPath + (route.path || '');
        routePathMap.set(route.name, fullPath);
      }
      
      if (route.children && route.children.length > 0) {
        // 递归处理子路由，传递当前完整路径作为父路径
        const currentFullPath = parentPath + (route.path || '');
        buildRouteMap(route.children, currentFullPath);
      }
    });
  }

  buildRouteMap(routes);

  // 递归处理菜单项
  function processMenuItem(item: MenuItem): MenuItem {
    const route = routeMap.get(item.name!);
    const routeMeta = route?.meta ?? {};
    const itemMeta = item.meta ?? {};
    const meta = { ...routeMeta, ...itemMeta };

    // 使用 routePathMap 获取完整的路由路径
    const fullPath = routePathMap.get(item.name!) || route?.path;

    const menuItem: MenuItem = {
      title: item.title || meta.title || '',
      path: fullPath,
      name: item.name,
      icon: item.icon || meta.icon, // 优先使用 item.icon，然后才是 meta.icon
      meta: meta
    };

    if (item.children && item.children.length > 0) {
      menuItem.children = item.children.map(child => processMenuItem(child));
    }

    return menuItem;
  }

  // 生成高级菜单配置
  const advancedMenu: MenuConfig = {
    ...simpleMenu,
    menus: simpleMenu.menus.map(item => processMenuItem(item))
  };

  return advancedMenu;
}
