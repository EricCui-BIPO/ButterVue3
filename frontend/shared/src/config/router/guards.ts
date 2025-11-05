import type { Router } from 'vue-router'
import { startProgress, finishProgress, initNProgress } from '../../utils/nprogress'

export function setupRouterGuards(router: Router) {
  // 初始化 NProgress
  initNProgress({})

  router.beforeEach((to, _from, next) => {
    // 开始页面加载进度条
    startProgress()
    
    // 设置页面标题
    if (to.meta?.title) {
      document.title = `${to.meta.title} - ${import.meta.env.VITE_APP_NAME}`
    }
    next()
  })

  router.afterEach(() => {
    // 完成页面加载进度条
    finishProgress()
  })

  router.onError(() => {
    // 路由错误时也要完成进度条
    finishProgress()
  })
}