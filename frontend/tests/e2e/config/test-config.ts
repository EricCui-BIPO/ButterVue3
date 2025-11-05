/**
 * 测试环境配置文件
 * 定义各个端的域名配置和测试相关的常量
 */

export interface PortalConfig {
  /** 端口名称 */
  name: string;
  /** 基础域名 */
  baseUrl: string;
  /** 登录路径 */
  loginPath: string;
  /** 默认用户名 */
  defaultUsername: string;
  /** 默认密码 */
  defaultPassword: string;
}

/**
 * 各个端的配置信息
 */
export const PORTAL_CONFIGS: Record<string, PortalConfig> = {
  admin: {
    name: '管理端',
    baseUrl: 'http://localhost:3003',
    loginPath: '/login',
    defaultUsername: 'admin',
    defaultPassword: 'admin123'
  },
  client: {
    name: '客户端',
    baseUrl: 'http://localhost:3001',
    loginPath: '/login',
    defaultUsername: 'client',
    defaultPassword: 'client123'
  },
  service: {
    name: '服务端',
    baseUrl: 'http://localhost:3002',
    loginPath: '/login',
    defaultUsername: 'service',
    defaultPassword: 'service123'
  },
  talent: {
    name: '人才端',
    baseUrl: 'http://localhost:3004',
    loginPath: '/login',
    defaultUsername: 'talent',
    defaultPassword: 'talent123'
  }
};

/**
 * 当前测试的端配置（默认为管理端）
 */
export type PortalKey = keyof typeof PORTAL_CONFIGS;
export type Portal = PortalKey;

let currentPortal: PortalKey = 'admin';

/**
 * 获取当前端的配置
 */
export function getCurrentPortal(): PortalKey {
  return currentPortal;
}

/**
 * 设置当前测试的端类型
 */
export function setCurrentPortal(portal: PortalKey): void {
  if (!PORTAL_CONFIGS[portal]) {
    throw new Error(`未找到端 "${portal}" 的配置`);
  }
  currentPortal = portal;
}

/**
 * 获取当前端的配置
 */
export function getCurrentPortalConfig(): PortalConfig {
  return PORTAL_CONFIGS[currentPortal];
}

/**
 * 获取指定端的配置
 */
export function getPortalConfig(portal: string): PortalConfig {
  const config = PORTAL_CONFIGS[portal];
  if (!config) {
    throw new Error(`未找到端 "${portal}" 的配置`);
  }
  return config;
}

/**
 * 构建完整的 URL
 */
export function buildUrl(portal: string, path: string): string {
  const config = getPortalConfig(portal);
  return `${config.baseUrl}${path}`;
}

/**
 * 构建当前端的完整 URL
 */
export function buildCurrentPortalUrl(path: string): string {
  return buildUrl(currentPortal, path);
}

/**
 * 测试超时配置
 */
export const TEST_TIMEOUTS = {
  /** 页面加载超时时间（毫秒） */
  PAGE_LOAD: 30000,
  /** 元素等待超时时间（毫秒） */
  ELEMENT_WAIT: 10000,
  /** 网络请求超时时间（毫秒） */
  NETWORK_REQUEST: 15000,
  /** 表单提交超时时间（毫秒） */
  FORM_SUBMIT: 20000
};

/**
 * 测试数据配置
 */
export const TEST_DATA = {
  /** 默认等待时间（毫秒） */
  DEFAULT_WAIT: 1000,
  /** 长等待时间（毫秒） */
  LONG_WAIT: 3000,
  /** 短等待时间（毫秒） */
  SHORT_WAIT: 500
};