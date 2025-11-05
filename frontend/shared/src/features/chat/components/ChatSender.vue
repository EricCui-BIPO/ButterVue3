<template>
  <div class="smart-ai-container" :class="{ small: isSmall }">
    <h1 v-if="!isSmall">What can I help you?</h1>

    <Sender
      class="smart-ai-sender"
      v-model="aiQuery"
      :placeholder="'Tell me what you need, e.g., Help me handle EOR for employees in Singapore'"
      variant="updown"
      :auto-size="{ minRows: 1, maxRows: 6 }"
      submitType="enter"
      @submit="handleSubmit"
    >
      <template #prefix>
        <div class="left-actions">
          <el-upload
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleFileChange"
            multiple
          >
            <el-button :icon="Paperclip" circle />
          </el-upload>
          <span v-if="files.length" class="file-count"> {{ files.length }} file(s) selected </span>
        </div>
      </template>

      <template #action-list>
        <div class="right-actions">
          <el-button :icon="Microphone" circle />
          <el-button v-if="isLoading" type="danger" :icon="Close" circle @click="handleCancel" />
          <el-button v-else type="primary" :icon="Promotion" circle @click="handleSubmit" />
        </div>
      </template>
    </Sender>

    <div v-if="!isSmall" class="ai-suggestions">
      <template v-if="quickPromptsStore.quickPromptsLoading">
        <el-tag
          v-for="i in 3"
          :key="i"
          class="ai-suggestion ai-suggestion--skeleton"
          type="info"
          size="large"
        >
          Loading...
        </el-tag>
      </template>
      <template v-else>
        <el-tooltip
          v-for="suggestion in quickPromptsStore.quickPrompts"
          :key="suggestion.id"
          effect="dark"
          placement="top"
          :content="suggestion.content"
          :show-after="500"
        >
          <el-tag
            class="ai-suggestion"
            type="info"
            size="large"
            @click="handleSuggestionClick(suggestion.content)"
          >
            {{ suggestion.content }}
          </el-tag>
        </el-tooltip>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Microphone, Plus, Promotion, Close, Paperclip } from '@element-plus/icons-vue';
import { computed, nextTick, onMounted } from 'vue';
import { useChatQuickPrompts } from '@I0/shared/stores';
import { Sender } from 'vue-element-plus-x';

// Props
interface Props {
  modelValue?: string;
  connectInternet?: boolean;
  files?: any[];
  isLoading?: boolean;
  size?: 'default' | 'small';
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  connectInternet: false,
  files: () => [],
  isLoading: false,
  size: 'default'
});

// Emits
interface Emits {
  (e: 'update:modelValue', value: string): void;
  (e: 'update:connectInternet', value: boolean): void;
  (e: 'update:files', value: any[]): void;
  (e: 'submit', data: { message: string; connectInternet: boolean; files: any[] }): void;
  (e: 'file-change', files: any[]): void;
  (e: 'suggestion-click', suggestion: string): void;
  (e: 'cancel'): void;
}

const emit = defineEmits<Emits>();

// Data - 使用 computed 实现双向数据绑定
const aiQuery = computed<string>({
  get: () => props.modelValue,
  set: (value: string) => emit('update:modelValue', value)
});

const connectInternet = computed<boolean>({
  get: () => props.connectInternet,
  set: (value: boolean) => emit('update:connectInternet', value)
});

const files = computed<any[]>({
  get: () => props.files,
  set: (value: any[]) => emit('update:files', value)
});

// 计算属性
const isSmall = computed(() => props.size === 'small');

// 使用快速提示词状态管理
const quickPromptsStore = useChatQuickPrompts();

// 组件挂载时确保快速提示词已加载（带缓存）
onMounted(() => {
  quickPromptsStore.ensureQuickPromptsLoaded();
});

// Methods
function handleFileChange(_file: any, fileList: any[]) {
  const newFiles = fileList || [];
  files.value = newFiles;
  emit('file-change', newFiles);
}

function handleSubmit() {
  emit('submit', {
    message: aiQuery.value,
    connectInternet: connectInternet.value,
    files: files.value
  });
}

function handleSuggestionClick(suggestion: string) {
  aiQuery.value = suggestion;
  emit('suggestion-click', suggestion);
  nextTick(() => {
    handleSubmit();
  });
}

function handleCancel() {
  emit('cancel');
}

function handleKeyDown(event: KeyboardEvent) {
  if (event.key === 'Enter') {
    if (event.altKey) {
      // Alt+Enter: 手动插入换行符
      event.preventDefault(); // 阻止默认行为

      // 获取当前光标位置
      const textarea = event.target as HTMLTextAreaElement;
      const start = textarea.selectionStart;
      const end = textarea.selectionEnd;
      const currentValue = aiQuery.value;

      // 在光标位置插入换行符
      const newValue = currentValue.substring(0, start) + '\n' + currentValue.substring(end);
      aiQuery.value = newValue;

      // 设置光标位置到换行符后
      nextTick(() => {
        textarea.selectionStart = textarea.selectionEnd = start + 1;
        textarea.focus();
      });
    } else {
      // Enter: 提交表单
      event.preventDefault(); // 阻止默认的换行行为
      handleSubmit();
    }
  }
}
</script>

<style scoped lang="scss">
.smart-ai-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding-bottom: 50px;

  // Small 模式样式
  &.small {
    padding-bottom: 0;
  }
}

.smart-ai-sender {
  width: 100%;
  max-width: 800px;

  :deep(.el-sender) {
    background-color: #fff;
    border-radius: 15px;
  }
}

.ai-input-composer {
  background: #fff;
  padding: 15px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 15px;
  width: 100%;
  max-width: 800px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.no-border-textarea :deep(.el-textarea__inner) {
  border: none;
  box-shadow: none;
  background-color: transparent;
  resize: none;
  padding: 2px;
}

.ai-actions-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.left-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.right-actions {
  display: flex;
  align-items: center;
}

.file-count {
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.ai-suggestions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
  max-width: 800px;

  :deep(.el-tag__content) {
    max-width: 320px;
    text-align: left;
    text-overflow: ellipsis;
    overflow: hidden;
  }
}

.ai-suggestion {
  cursor: pointer;

  &--skeleton {
    opacity: 0.6;
    cursor: default;
    min-width: 120px;
  }
}
</style>
