import { fileURLToPath, URL } from 'node:url';
import { defineConfig } from 'vitest/config';
import vue from '@vitejs/plugin-vue';

export default defineConfig({
  plugins: [
    vue(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./app', import.meta.url)),
      '@I0': fileURLToPath(new URL('./shared', import.meta.url))
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
      reporter: ['text', 'json', 'html']
    }
  }
})