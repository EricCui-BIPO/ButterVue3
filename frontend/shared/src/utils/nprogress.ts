import NProgress from 'nprogress'
import 'nprogress/nprogress.css'

/**
 * NProgress 配置选项
 */
export interface NProgressConfig {
  /** 是否显示加载指示器 */
  showSpinner?: boolean
  /** 进度条颜色 */
  color?: string
  /** 进度条高度 */
  height?: string
  /** 最小百分比 */
  minimum?: number
  /** 缓动函数 */
  easing?: string
  /** 动画速度 */
  speed?: number
  /** 是否启用涓流动画 */
  trickle?: boolean
  /** 涓流速度 */
  trickleSpeed?: number
}

/**
 * 默认配置
 */
const defaultConfig: NProgressConfig = {
  showSpinner: false,
  color: '#3550e0', // Element Plus 主色调
  height: '2px',
  minimum: 0.08,
  easing: 'ease',
  speed: 200,
  trickle: true,
  trickleSpeed: 200
}

/**
 * 初始化 NProgress 配置
 * @param config 自定义配置
 */
export function initNProgress(config: NProgressConfig = {}) {
  const finalConfig = { ...defaultConfig, ...config }
  
  // 配置 NProgress
  NProgress.configure({
    showSpinner: finalConfig.showSpinner,
    minimum: finalConfig.minimum,
    easing: finalConfig.easing,
    speed: finalConfig.speed,
    trickle: finalConfig.trickle,
    trickleSpeed: finalConfig.trickleSpeed
  })

  // 动态设置样式
  const style = document.createElement('style')
  style.innerHTML = `
    #nprogress .bar {
      background: ${finalConfig.color} !important;
      height: ${finalConfig.height} !important;
    }
    #nprogress .peg {
      box-shadow: 0 0 10px ${finalConfig.color}, 0 0 5px ${finalConfig.color} !important;
    }
    #nprogress .spinner-icon {
      border-top-color: ${finalConfig.color} !important;
      border-left-color: ${finalConfig.color} !important;
    }
  `
  document.head.appendChild(style)
}

/**
 * 开始加载进度条
 */
export function startProgress() {
  NProgress.start()
}

/**
 * 完成加载进度条
 */
export function finishProgress() {
  NProgress.done()
}

/**
 * 设置进度条进度
 * @param progress 进度值 (0-1)
 */
export function setProgress(progress: number) {
  NProgress.set(progress)
}

/**
 * 增加进度条进度
 * @param amount 增加的进度值
 */
export function incrementProgress(amount?: number) {
  NProgress.inc(amount)
}

/**
 * 移除进度条
 */
export function removeProgress() {
  NProgress.remove()
}

/**
 * 检查进度条是否正在运行
 */
export function isProgressStarted(): boolean {
  return NProgress.isStarted()
}

// 导出 NProgress 实例以供高级用法
export { NProgress }

// 默认导出配置好的工具函数
export default {
  init: initNProgress,
  start: startProgress,
  finish: finishProgress,
  set: setProgress,
  inc: incrementProgress,
  remove: removeProgress,
  isStarted: isProgressStarted
}