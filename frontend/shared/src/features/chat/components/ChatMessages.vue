<template>
  <div>
    <BubbleList class="bubble-list" :list="bubbleMessages" v-loading="loading">
      <!-- 自定义 AI 头像插槽 -->
      <template #avatar="{ item }">
        <div v-if="item.role === 'ai'" class="ai-avatar icon i-myicon-naozi">
          <img
            src="data:image/svg+xml;utf8,%3Csvg display='inline-block' vertical-align='middle' fill='currentColor' t='1748504028646' class='icon' viewBox='0 0 1024 1024' version='1.1' xmlns='http://www.w3.org/2000/svg' p-id='5410' xmlns:xlink='http://www.w3.org/1999/xlink' width='256' height='256'%3E%3Cpath d='M305.152 128.512c35.498667-27.648 81.92-42.922667 128-42.922667 27.605333 0 49.92 9.472 66.858667 24.533334 4.608 4.053333 8.661333 8.448 12.288 12.970666 3.584-4.522667 7.68-8.917333 12.245333-12.970666 16.896-15.061333 39.253333-24.533333 66.816-24.533334 46.165333 0 92.586667 15.274667 128.042667 42.922667 27.605333 21.461333 49.237333 51.114667 57.728 86.912 17.92 2.986667 33.877333 12.117333 46.762666 24.192 20.48 19.242667 35.072 47.061333 44.288 75.690667 9.301333 29.056 13.994667 61.696 12.586667 93.056-0.725333 16.042667-3.072 32.426667-7.594667 48.085333l2.816 1.28c15.786667 7.424 28.586667 19.072 38.144 34.56 18.133333 29.226667 24.533333 71.296 24.533334 124.928 0 61.653333-23.552 103.509333-53.845334 129.493333a147.072 147.072 0 0 1-54.186666 29.44 220.672 220.672 0 0 1-43.477334 91.178667c-30.848 38.570667-78.762667 71.168-143.232 71.168-51.626667 0-92.288-28.586667-118.4-55.893333a228.608 228.608 0 0 1-13.226666-15.232 228.693333 228.693333 0 0 1-13.226667 15.189333c-26.154667 27.392-66.816 55.936-118.442667 55.936-64.426667 0-112.384-32.597333-143.232-71.168a220.629333 220.629333 0 0 1-43.477333-91.178667 147.072 147.072 0 0 1-54.186667-29.44c-30.293333-26.026667-53.845333-67.84-53.845333-129.493333 0-53.632 6.4-95.701333 24.533333-124.928a89.429333 89.429333 0 0 1 40.96-35.84 208.725333 208.725333 0 0 1-7.552-48.085333c-1.408-31.36 3.242667-64 12.586667-93.056 9.173333-28.586667 23.765333-56.448 44.288-75.690667a90.538667 90.538667 0 0 1 46.72-24.192c8.490667-35.84 30.122667-65.450667 57.728-86.912z m39.296 50.474667c-23.253333 18.133333-36.906667 42.282667-36.906667 70.101333a32 32 0 0 1-42.112 30.336c-5.888-1.962667-12.032-1.493333-20.949333 6.869333-9.856 9.258667-19.925333 26.026667-27.136 48.554667a206.634667 206.634667 0 0 0-9.6 70.656c1.066667 23.722667 6.698667 43.434667 15.616 56.746667a32.085333 32.085333 0 0 1 3.541333 7.082666H273.066667a121.6 121.6 0 0 1 121.258666 112.725334 85.333333 85.333333 0 1 1-64.298666 0.426666A57.6 57.6 0 0 0 273.066667 533.333333H162.133333a29.866667 29.866667 0 0 1-1.066666 0c-6.4 14.848-11.178667 40.490667-11.178667 83.882667 0 43.562667 15.872 67.456 31.573333 80.938667 17.152 14.72 36.053333 18.56 41.514667 18.56a32 32 0 0 1 32 32c0 17.92 9.898667 50.474667 32.426667 78.634666 21.717333 27.178667 52.608 47.146667 93.226666 47.146667 27.178667 0 52.224-15.274667 72.106667-36.096 9.642667-10.154667 17.152-20.693333 22.101333-29.226667a76.8 76.8 0 0 0 4.949334-9.941333l0.213333-0.597333v-104.746667-0.554667-298.666666h-38.186667a85.333333 85.333333 0 1 1 0-64h38.186667V222.378667l-0.085333-2.432a130.261333 130.261333 0 0 0-7.68-38.570667c-3.84-10.154667-8.832-18.176-14.762667-23.466667-5.248-4.693333-12.458667-8.32-24.32-8.32-32.64 0-65.066667 11.008-88.704 29.44zM544.554667 725.333333v73.301334l0.213333 0.597333c0.853333 2.261333 2.474667 5.632 4.949333 9.941333 4.949333 8.533333 12.458667 19.072 22.101334 29.226667 19.882667 20.821333 44.928 36.096 72.106666 36.096 40.661333 0 71.509333-19.968 93.226667-47.146667 22.528-28.16 32.426667-60.757333 32.426667-78.634666a32 32 0 0 1 32-32c5.461333 0 24.32-3.84 41.557333-18.56 15.658667-13.482667 31.530667-37.376 31.530667-80.938667 0-51.541333-6.741333-78.08-14.890667-91.178667a25.6 25.6 0 0 0-11.093333-10.453333c-4.352-2.048-10.794667-3.584-20.821334-3.584a32 32 0 0 1-26.666666-49.749333c8.917333-13.354667 14.549333-33.024 15.616-56.746667a206.677333 206.677333 0 0 0-9.6-70.656c-7.253333-22.528-17.237333-39.253333-27.136-48.554667-8.874667-8.362667-15.061333-8.832-20.906667-6.869333a32 32 0 0 1-42.154667-30.336c0-27.818667-13.653333-51.968-36.906666-70.101333-23.637333-18.389333-56.021333-29.44-88.746667-29.44-11.818667 0-19.029333 3.712-24.277333 8.362666-5.930667 5.290667-10.88 13.312-14.72 23.466667a130.261333 130.261333 0 0 0-7.68 38.570667 87.722667 87.722667 0 0 0-0.128 2.432V661.333333h27.178666a57.6 57.6 0 0 0 57.6-57.6v-76.586666a85.333333 85.333333 0 1 1 64 0v76.586666A121.6 121.6 0 0 1 571.733333 725.333333h-27.178666zM362.666667 341.333333a21.333333 21.333333 0 1 0 0 42.666667 21.333333 21.333333 0 0 0 0-42.666667zM341.333333 661.333333a21.333333 21.333333 0 1 0 42.666667 0 21.333333 21.333333 0 0 0-42.666667 0z m298.666667-213.333333a21.333333 21.333333 0 1 0 42.666667 0 21.333333 21.333333 0 0 0-42.666667 0z' p-id='5411'%3E%3C/path%3E%3C/svg%3E"
            alt="AI Brain Icon"
          />
        </div>
      </template>

      <!-- 自定义内容插槽，用于显示 UI 组件缩略图 -->
      <template #content="{ item }">
        <div class="message-content">
          <!-- 显示消息内容 -->

          <Typewriter
            class="text-content"
            :content="item.content"
            :is-markdown="item.isMarkdown"
            :is-fog="item.isFog"
            :typing="item.typing"
          />

          <!-- 显示 UI 组件缩略图 -->
          <ChatUIComponentsThumbnails
            v-if="item.uiComponents && item.uiComponents.length > 0"
            :components="item.uiComponents"
            @thumbnail-click="emit('thumbnail-click', $event)"
          />

        </div>
      </template>
    </BubbleList>
  </div>
</template>

<script setup lang="ts">
import type { ChatMessageOutput, UIComponentData } from '@I0/shared/types';
import { computed } from 'vue';
import { BubbleList } from 'vue-element-plus-x';
import ChatUIComponentsThumbnails from './ChatUIComponentsThumbnails.vue';

interface Props {
  messages: ChatMessageOutput[];
  loading?: boolean;
  currentAssistantMessageId?: string;
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  currentAssistantMessageId: undefined
});

// 定义组件事件
const emit = defineEmits<{
  'thumbnail-click': [component: UIComponentData];
}>();

// 转换消息数据格式以适配 BubbleList 组件
const bubbleMessages = computed<any[]>(() => {
  return props.messages.map(message => {
    const role = message.role === 'ASSISTANT' ? 'ai' : 'user';
    const placement = role === 'ai' ? 'start' : 'end';
    const key = message.id;
    const content = message.content;
    const loading = !content;
    const shape = 'corner';
    const variant = role === 'ai' ? 'filled' : 'outlined';
    const isMarkdown = role === 'ai';
    const isCurrentAssistantMessage =
      role === 'ai' && message.id === props.currentAssistantMessageId;
    const typing = isCurrentAssistantMessage;
    const isFog = isCurrentAssistantMessage;

    return {
      key, // 唯一标识
      role, // user | ai 自行更据模型定义
      placement, // start | end 气泡位置
      content, // 消息内容 流式接受的时候，只需要改这个值即可
      loading, // 当前气泡的加载状态
      shape, // 气泡的形状
      variant, // 气泡的样式
      isMarkdown, // 是否渲染为 markdown
      typing, // 是否开启打字器效果 该属性不会和流式接受冲突
      isFog, // 是否开启打字雾化效果，该效果 v1.1.6 新增，且在 typing 为 true 时生效，该效果会覆盖 typing 的 suffix 属性
      uiComponents: message.uiComponents || [] // UI 组件数据
    };
  });
});
</script>

<style scoped lang="scss">
.bubble-list {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}
.el-bubble-list {
  height: 100%;
  max-height: unset;
}

// 消息内容样式
.message-content {
  .text-content {
    margin-bottom: 8px;
  }
}

// AI 头像样式
.ai-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

// 简约脑子图标样式
.icon.i-myicon-naozi {
  background: linear-gradient(135deg, #f0f4f8 0%, #d9e2ec 100%);
  border: 1px solid #e0e6ed;
  color: #475569;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

  // SVG图标样式
  img {
    width: 20px;
    height: 20px;
    display: block;
    object-fit: contain;
  }
}

::v-deep(.el-bubble-end) {
  .el-bubble-content {
    border-start-end-radius: calc(var(--el-border-radius-base) - -16px) !important;
    border-radius: 24px 24px 7px;
    background-color: var(--el-fill-color);
    border: none;
  }
}

::v-deep(.el-bubble-start) {
  .el-bubble-content {
    max-width: 100%;
    width: 100%;
    background-color: transparent;
  }
}
</style>
