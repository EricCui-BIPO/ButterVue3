import { createRouter, createWebHistory } from "vue-router";
import { routes } from "vue-router/auto-routes";
import { setupLayouts } from "./setupLayouts";
import { setupRouterGuards } from "./guards";

const router = createRouter({
  history: createWebHistory("/"),
  routes: setupLayouts(routes),
});

// 初始化路由守卫
setupRouterGuards(router);

export { router };