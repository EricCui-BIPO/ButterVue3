import type { MenuConfig } from '@I0/shared/types'

/**
 * 全局菜单配置单例
 */
export class GlobalMenuConfig {
  private static instance: GlobalMenuConfig | null = null
  private config: MenuConfig | null = null

  private constructor() {}

  /**
   * 获取单例实例
   */
  static getInstance(): GlobalMenuConfig {
    if (!GlobalMenuConfig.instance) {
      GlobalMenuConfig.instance = new GlobalMenuConfig()
    }
    return GlobalMenuConfig.instance
  }

  /**
   * 初始化菜单配置
   */
  initialize(config: MenuConfig) {
    this.config = config
  }

  /**
   * 获取菜单配置
   */
  getConfig(): MenuConfig | null {
    return this.config
  }

  /**
   * 清除配置
   */
  clear() {
    this.config = null
  }
}

/**
 * 菜单配置初始化函数
 * 各应用端需要在启动时调用此函数初始化全局菜单配置
 */
export function initializeMenuConfig(config: MenuConfig) {
  GlobalMenuConfig.getInstance().initialize(config)
}

// 导出类型
export type { MenuConfig } from '@I0/shared/types'