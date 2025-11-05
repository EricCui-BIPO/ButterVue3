<template>
  <div class="chat-sidebar" :class="{ collapsed: isCollapsed }" v-loading="!isCollapsed && loading">
    <!-- 侧边栏内容 -->
    <div class="sidebar-content">
      <!-- 新聊天按钮 -->
      <div class="sidebar-actions">
        <div v-if="!isCollapsed" class="sidebar-header-action">
          <div class="sidebar-action" @click="toggleCollapse">
            <el-icon><Fold /> </el-icon>
          </div>
        </div>

        <div v-else class="sidebar-action" @click="toggleCollapse">
          <el-icon><Expand /> </el-icon>
        </div>

        <div class="sidebar-action" @click="handleCreate">
          <el-icon><Edit /> </el-icon>
          <span v-if="!isCollapsed">新聊天</span>
        </div>
      </div>

      <!-- 会话列表 - 延迟显示以确保动画完成 -->
      <div class="conversations-section" v-if="shouldShowContent">
        <Conversations
          v-model:active="computedActiveId"
          :items="sessions"
          rowKey="id"
          labelKey="title"
          :show-built-in-menu="true"
          @menu-command="handleCommand"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Edit, Expand, Fold } from '@element-plus/icons-vue';
import { useChatSidebarStore } from '@I0/shared/stores';
import { storeToRefs } from 'pinia';
import { computed, ref, watch } from 'vue';
import { Conversations } from 'vue-element-plus-x';

// Props
interface Props {
  sessions: any[];
  activeId?: string;
  loading?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  sessions: () => [],
  activeId: '',
  loading: false
});

// Emits
interface Emits {
  (e: 'update:activeId', id: string): void;
  (e: 'create'): void;
  (e: 'select', session: any): void;
  (e: 'command', command: any, item: any): void;
}

const emit = defineEmits<Emits>();

// 使用 store 管理折叠状态
const chatSidebarStore = useChatSidebarStore();
const { isCollapsed } = storeToRefs(chatSidebarStore);
const { toggleCollapse } = chatSidebarStore;

// 控制内容显示的延迟变量
const shouldShowContent = ref(!isCollapsed.value);
let showContentTimer: NodeJS.Timeout | null = null;

const computedActiveId = computed({
  get() {
    return props.activeId;
  },
  set(value: string) {
    emit('update:activeId', value);

    const session = props.sessions.find(s => s.id === value);
    emit('select', session);
  }
});

// 监听折叠状态变化
watch(
  isCollapsed,
  (newValue, oldValue) => {
    // 当从折叠变为展开时，延迟显示内容
    if (oldValue === true && newValue === false) {
      // 清除之前的定时器
      if (showContentTimer) {
        clearTimeout(showContentTimer);
      }

      // 延迟 300ms（与CSS动画时长一致）显示内容
      showContentTimer = setTimeout(() => {
        shouldShowContent.value = true;
      }, 280);
    }
    // 当从展开变为折叠时，立即隐藏内容
    else if (oldValue === false && newValue === true) {
      shouldShowContent.value = false;

      // 清除定时器
      if (showContentTimer) {
        clearTimeout(showContentTimer);
        showContentTimer = null;
      }
    }
  },
  { immediate: true }
);

// 事件处理
function handleCreate() {
  emit('create');
}

function handleCommand(command: any, item: any) {
  emit('command', command, item);
}
</script>

<style scoped lang="scss">
.chat-sidebar {
  height: 100%;
  position: relative;
  width: 280px;
  background: white;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;

  &.collapsed {
    width: 60px;
  }
  .sidebar-content {
    height: 100%;
    display: flex;
    flex-direction: column;
    padding: 10px 0 0 0;
    overflow: hidden;
    opacity: 1;

    .sidebar-actions {
      padding: 0 10px 0 10px;
    }

    .sidebar-action {
      padding: 14px 10px;
      border-radius: 8px;
      cursor: pointer;
      font-size: 14px;
      color: #303133;
      display: flex;
      flex-direction: row;
      align-items: center;
      opacity: 1;
      transition: all 0.2s ease;

      &:hover {
        background-color: #f0f0f0;
      }

      .el-icon {
        font-size: 20px;
      }

      span {
        margin-left: 6px;
        white-space: nowrap;
      }
    }

    .sidebar-header-action {
      display: flex;
      flex-direction: row;
      justify-content: end;

      .sidebar-action {
        color: #30313380;
      }
    }

    .conversations-section {
      flex: 1;
      overflow: hidden;
    }

    :deep(.el-conversations) {
      height: 100%;
      background-color: transparent !important;
    }
  }
}
</style>
