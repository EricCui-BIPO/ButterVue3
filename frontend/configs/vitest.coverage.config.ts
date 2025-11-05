import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vitest/config'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('../app', import.meta.url))
      // '@I0': fileURLToPath(new URL('../shared', import.meta.url))
    }
  },
  test: {
    globals: true,
    environment: 'jsdom',
    include: [
      'app/*/src/**/*.{test,spec}.{js,ts,jsx,tsx}',
      'shared/**/*.{test,spec}.{js,ts,jsx,tsx}'
    ],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov', 'clover'],
      thresholds: {
        global: {
          branches: 80,
          functions: 80,
          lines: 80,
          statements: 80
        }
      },
      include: ['app/*/src/**/*.{js,ts,vue}', 'shared/**/*.{js,ts,vue}'],
      exclude: [
        'node_modules/',
        'dist/',
        '**/*.d.ts',
        '**/*.config.{js,ts}',
        '**/coverage/**',
        '**/tests/**',
        '**/__tests__/**'
      ],
      watermarks: {
        statements: [70, 90],
        branches: [70, 90],
        functions: [70, 90],
        lines: [70, 90]
      }
    },
    setupFiles: ['./shared/src/test/setup.ts']
  }
})
