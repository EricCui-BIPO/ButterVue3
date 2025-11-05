<template>
  <div class="home-container">
    <ChatSender v-model="aiInput" :files="aiFiles" @submit="handleSubmit" />
    <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
      <el-tab-pane label="EOR" name="EOR">
        <EorQuick />
        <EorEmpChart />
        <EorEmpTable />
      </el-tab-pane>
      <el-tab-pane label="COR" name="COR">
        <CorQuick />
        <CorEmpChart />
        <CorEmpTable />
      </el-tab-pane>
      <el-tab-pane label="GPO" name="GPO">
        <GpoQuick />
        <GpoEmpChart />
        <GpoEmpTable />
      </el-tab-pane>
      <el-tab-pane label="Direct (Self-Employed)" name="Direct">
        <DirectQuick />
        <DirectEmpChart />
        <DirectEmpTable />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ChatSender } from '@I0/shared/features';
import { useChatCacheStore } from '@I0/shared/stores';
import EorEmpTable from './eor/eTable.vue';
import EorQuick from './eor/quick.vue';
import EorEmpChart from './eor/eChart.vue';
import CorEmpTable from './cor/cTable.vue';
import CorQuick from './cor/quick.vue';
import CorEmpChart from './cor/cChart.vue';
import GpoEmpTable from './gpo/pTable.vue';
import GpoQuick from './gpo/quick.vue';
import GpoEmpChart from './gpo/pChart.vue';
import DirectEmpTable from './direct/dTable.vue';
import DirectQuick from './direct/quick.vue';
import DirectEmpChart from './direct/dChart.vue';



import type { TabsPaneContext } from 'element-plus';

const router = useRouter();
const chatCacheStore = useChatCacheStore();
const activeName = ref('EOR');
const aiInput = ref('');
const aiFiles = ref<any[]>([]);

const handleClick = (tab: TabsPaneContext, event: Event) => {
  console.log(tab, event);
};

// 处理 ChatSender 提交事件
const handleSubmit = (data: any) => {
  // 将输入数据存储到 Pinia store，以便在 AIChat 页面中使用
  chatCacheStore.setChatData(data);

  // 跳转到 AIChat 页面
  router.push({ name: 'AIChat' });
};
</script>

<style scoped lang="scss">
.home-container {
  max-width: var(--container-width-large);
  margin: 0 auto;
}
</style>

<route lang="yaml">
name: Home
meta:
  title: 'Home'
  layout: 'main'
</route>
