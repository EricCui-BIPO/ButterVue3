import vue from '@vitejs/plugin-vue';
import { defineConfig, loadEnv, UserConfig } from 'vite';
import { fileURLToPath, URL } from 'node:url';
import VueRouter from 'unplugin-vue-router/vite';
import { resolve } from 'node:path';
// import { CodeInspectorPlugin } from 'code-inspector-plugin';

/**
 * 创建 Vite 配置的工厂函数
 * @param env - 环境变量对象
 * @param __dirname - 当前文件的目录路径
 * @returns Vite 配置对象
 */
export function createViteConfig(env: Record<string, string>, __dirname: string): UserConfig {
  const appType = env.VITE_APP_TYPE || 'client';
  const port = env.VITE_APP_PORT || 3001;
  const appName = env.VITE_APP_NAME || 'App';

  return {
    plugins: [
      VueRouter({
        routeBlockLang: 'yaml',
        routesFolder: resolve(__dirname, 'src/pages'),
        dts: resolve(__dirname, 'src/router/types.d.ts'),
        exclude: ['**/components/**/*']
      }),
      // CodeInspectorPlugin({
      //   bundler: 'vite',
      //   hideDomPathAttr: true,
      //   match: /\.(vue|jsx|tsx)$/,
      // }),
      vue()
    ],
    resolve: {
      alias: {
        '@': fileURLToPath(new URL('src', import.meta.url))
      }
    },
    server: {
      strictPort: true,
      port: Number(port),
      host: true,
      proxy: {
        '/services': {
          changeOrigin: true,
          target: env.VITE_API_BASE_URL || 'http://localhost:8088/',
          rewrite: (path: string) => path.replace(/^\/services/, '')
        }
      }
    },
    build: {
      outDir: `../../dist/${appType}-portal`,
      emptyOutDir: true, // 允许清空项目根目录外的输出目录
      sourcemap: false, // 生产环境不生成 sourcemap
      chunkSizeWarningLimit: 12 * 1024, // 调整 chunk 大小警告限制到 12Mb
      rollupOptions: {
        output: {
          chunkFileNames: 'assets/js/[name]-[hash].js',
          entryFileNames: 'assets/js/[name]-[hash].js',
          assetFileNames: 'assets/[ext]/[name]-[hash].[ext]',
          manualChunks: {
            'vue-vendor': ['vue', 'vue-router', 'vue-i18n', 'pinia', '@vueuse/core'],
            'element-vendor': ['element-plus', 'vue-element-plus-x'],
            'charts-vendor': ['echarts'],
            'tools-vendor': ['lodash', 'dayjs', 'axios']
          }
        }
      }
    },
    optimizeDeps: {
      include: [
        '@vueuse/core',
        'echarts',
        'dayjs',
        'vue',
        'vue-router',
        'vue-i18n',
        'pinia',
        'element-plus',
        'vue-element-plus-x',
        'axios'
      ]
    },
    css: {
      // preprocessorOptions: {
      //   scss: {
      //     additionalData: ``,
      //     charset: false
      //   }
      // }
    },
    esbuild: {
      jsxFactory: 'h',
      jsxFragment: 'Fragment'
    },
    define: {
      __APP_NAME__: JSON.stringify(appName),
      __APP_TYPE__: JSON.stringify(appType),
      global: 'globalThis',
      'process.env': {}
    }
  };
}

/**
 * 默认的 defineConfig 导出函数
 * @param __dirname - 当前文件的目录路径
 * @returns defineConfig 函数
 */
export function createDefineConfig(__dirname: string) {
  return defineConfig(({ mode }) => {
    // 加载环境变量
    const env = loadEnv(mode, process.cwd(), 'VITE_');

    return createViteConfig(env, __dirname);
  });
}
