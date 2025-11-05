import { defineConfig, loadEnv } from 'vite'
import { createViteConfig } from '../../configs/vite.config.factory'

export default defineConfig(({ mode }) => {
  // 加载环境变量
  const env = loadEnv(mode, process.cwd(), 'VITE_')
  
  return createViteConfig(env, __dirname)
})