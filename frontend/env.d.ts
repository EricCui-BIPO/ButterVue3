/// <reference types="vite/client" />
/// <reference types="@vue/runtime-dom" />

declare namespace JSX {
  interface IntrinsicElements {
    [elem: string]: any
  }
}

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

declare module 'vue-router/auto' {
  export * from 'vue-router'
}

declare module 'vue-router/auto-routes' {
  import type { RouteRecordRaw } from 'vue-router'
  export const routes: RouteRecordRaw[]
}

interface ImportMetaEnv {
  readonly VITE_APP_NAME: string
  readonly VITE_APP_TYPE: string
  readonly VITE_APP_PORT: string
  readonly VITE_API_BASE_URL: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

// Global constants
declare const __APP_NAME__: string
declare const __APP_TYPE__: string