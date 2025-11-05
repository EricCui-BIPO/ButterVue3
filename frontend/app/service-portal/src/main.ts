import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
//
import { router } from '@I0/shared/config';
//
import ElementPlus from 'element-plus';
import ElementPlusX from 'vue-element-plus-x';
import 'element-plus/dist/index.css';
import '@I0/shared/styles';

import { initializeMenuConfig } from '@I0/shared/config/menu';
import advancedMenuConfig from './router/menu';

const app = createApp(App);

// 注册 Element Plus
app.use(ElementPlus);
app.use(ElementPlusX);

// 初始化菜单配置
initializeMenuConfig(advancedMenuConfig);

app.use(createPinia());
app.use(router);
app.mount('#app');
