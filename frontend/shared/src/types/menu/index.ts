/**
 * 菜单系统类型定义
 */

import type { Component } from 'vue'

/**
 * 菜单项基础接口
 */
export interface MenuItem {
  /** 菜单项显示标题 */
  title?: string
  /** 菜单项图标组件 */
  icon?: Component | string
  /** 菜单项路由名称 */
  name?: string
  /** 菜单项路由路径 */
  path?: string
  /** 菜单项元数据 */
  meta?: MenuItemMeta
  /** 子菜单项 */
  children?: MenuItem[]
}

/**
 * 菜单组接口 - 复用 MenuItem 类型
 */
export type MenuGroup = MenuItem

/**
 * 菜单项元数据
 */
export interface MenuItemMeta {
  /** 菜单项唯一标识 */
  title?: string
  /** 权限标识 */
  permission?: string | string[]
  /** 自定义样式类名 */
  className?: string
  /** 允许其他字段 */
  [key: string]: any
}

/**
 * 菜单配置接口
 */
export interface MenuConfig {
  /** 菜单项列表 */
  menus: MenuGroup[]
}

/**
 * 菜单权限检查函数类型
 */
export type PermissionChecker = (permission: string | string[]) => boolean

/**
 * 菜单过滤选项
 */
export interface MenuFilterOptions {
  /** 权限检查函数 */
  permissionChecker?: PermissionChecker
  /** 是否显示隐藏项 */
  showHidden?: boolean
  /** 自定义过滤函数 */
  customFilter?: (item: MenuItem) => boolean
}