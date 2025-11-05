import type { RouteRecordRaw } from "vue-router/auto";

const layouts: any = {
  main: () => import("@I0/shared/layouts/mainLayout.vue"),
};

export const setupLayouts = (routes: RouteRecordRaw[] = []): RouteRecordRaw[] => {
  function deepSetupLayout(routes: RouteRecordRaw[] = [], top = true) {
    if (!routes || !Array.isArray(routes)) return [];
    return routes.map((route) => {
      // just redirect
      if (route.name === "Index") {
        route.redirect = { name: "Home" };
      }

      if (route.children?.length) {
        route.children = deepSetupLayout(route.children, false);
      }

      const layout = route.meta?.layout as any;

      if (top && layout !== false) {
        return {
          path: route.path,
          component: layouts[layout],
          children: [{ ...route, path: "" }],
          meta: {
            isLayout: true,
          },
        };
      }

      if (layout) {
        return {
          path: route.path,
          component: layouts[layout],
          children: [{ ...route, path: "" }],
          meta: {
            isLayout: true,
          },
        };
      }

      return route;
    });
  }

  return deepSetupLayout(routes);
};
