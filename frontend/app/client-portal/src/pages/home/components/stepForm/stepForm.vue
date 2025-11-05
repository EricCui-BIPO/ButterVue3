<template>
  <div class="step-form-container">
    <!-- 顶部：水平步骤条 -->
    <div class="top-steps">
      <el-steps :active="stepActive" finish-status="success" align-center>
        <el-step v-for="step in stepConfig" :key="step.key" :title="step.title" />
      </el-steps>
    </div>

    <!-- 下方左右结构：左侧表单，右侧说明文字 -->
    <div class="page-grid">
      <main class="center-form">
        <div class="step-content">
          <div class="step-panel" v-if="currentStep">
            <el-form :model="currentStep.formData">
              <el-form-item v-for="field in currentStep.fields" :key="field.key" :label="field.label"
                label-position="top">
                <!-- 文本输入框 -->
                <el-input v-if="field.type === 'input'" v-model="currentStep.formData[field.key]"
                  :placeholder="field.placeholder" @focus="handleFieldFocus(field.focusKey)" />

                <!-- 文本域 -->
                <el-input v-else-if="field.type === 'textarea'" type="textarea"
                  v-model="currentStep.formData[field.key]" :rows="field.rows || 3" :placeholder="field.placeholder"
                  @focus="handleFieldFocus(field.focusKey)" />

                <!-- 数字输入框 -->
                <el-input-number v-else-if="field.type === 'number'" v-model="currentStep.formData[field.key]"
                  :min="field.min || 0" @focus="handleFieldFocus(field.focusKey)" />

                <!-- 下拉选择框 -->
                <el-select v-else-if="field.type === 'select'" v-model="currentStep.formData[field.key]"
                  :placeholder="field.placeholder" :multiple="!!field.multiple" :clearable="field.clearable !== false"
                  @focus="handleFieldFocus(field.focusKey)">
                  <el-option v-for="option in field.options" :key="option.value" :label="option.label"
                    :value="option.value" />
                </el-select>

                <!-- 日期选择器 -->
                <el-date-picker v-else-if="field.type === 'date'" v-model="currentStep.formData[field.key]"
                  :type="field.dateType || 'date'" :placeholder="field.placeholder"
                  :start-placeholder="field.startPlaceholder" :end-placeholder="field.endPlaceholder"
                  :value-format="field.valueFormat || 'YYYY-MM-DD'" :clearable="field.clearable !== false"
                  @focus="handleFieldFocus(field.focusKey)" />

                <!-- 开关 -->
                <el-switch v-else-if="field.type === 'switch'" v-model="currentStep.formData[field.key]"
                  :active-value="field.activeValue ?? true" :inactive-value="field.inactiveValue ?? false"
                  :active-text="field.activeText" :inactive-text="field.inactiveText"
                  @change="() => handleFieldFocus(field.focusKey)" />

                <!-- 单选组 -->
                <el-radio-group v-else-if="field.type === 'radio'" v-model="currentStep.formData[field.key]"
                  @change="() => handleFieldFocus(field.focusKey)">
                  <el-radio v-for="option in field.options" :key="option.value" :label="option.value">{{ option.label
                    }}</el-radio>
                </el-radio-group>

                <!-- 复选组 -->
                <el-checkbox-group v-else-if="field.type === 'checkbox-group'" v-model="currentStep.formData[field.key]"
                  @change="() => handleFieldFocus(field.focusKey)">
                  <el-checkbox v-for="option in field.options" :key="option.value" :label="option.value">{{ option.label
                    }}</el-checkbox>
                </el-checkbox-group>

                <!-- 单个复选框 -->
                <el-checkbox v-else-if="field.type === 'checkbox'" v-model="currentStep.formData[field.key]"
                  @change="() => handleFieldFocus(field.focusKey)">{{ field.label }}</el-checkbox>
              </el-form-item>
            </el-form>
          </div>
        </div>

        <!-- 底部操作按钮：上一页 / 下一页 / 提交 -->
        <div class="step-actions">
          <el-button>Fill with AI</el-button>
          <div>
            <el-button v-if="stepActive > 0" @click="goPrev">Previous</el-button>
            <el-button v-if="stepActive < stepConfig.length - 1" type="primary" @click="goNext">Next</el-button>
            <el-button v-if="stepActive === stepConfig.length - 1" type="success" @click="onSubmit">Submit</el-button>
          </div>
        </div>
      </main>

      <!-- 右侧：字段说明 -->
      <aside class="right-explain">
        <div class="explain-container">
          <div v-for="item in currentExplanations" :key="item.key" :id="'explain-' + item.key" class="explanation-item"
            :class="{ active: activeField === item.key }">
            <h4>{{ item.title }}</h4>
            <p>{{ item.text }}</p>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, computed, ref, nextTick, watch } from 'vue'
import type {  StepConfig } from './types'

// 类型从共享文件引入，移除本地定义

interface ExplanationItem {
  key: string
  title: string
  text: string
}

const props = defineProps<{ stepConfig: StepConfig[]; stepActive: number }>()
const emit = defineEmits<{ (e: 'update:stepActive', val: number): void; (e: 'submit'): void }>()

const currentStep = computed<StepConfig | undefined>(() => props.stepConfig[props.stepActive])

const activeField = ref<string>('')

const handleFieldFocus = (key: string) => {
  activeField.value = key
  nextTick(() => {
    const anchor = document.getElementById('explain-' + key)
    if (anchor) {
      anchor.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'nearest' })
    }
  })
}

const goPrev = () => {
  if (props.stepActive > 0) emit('update:stepActive', props.stepActive - 1)
}
const goNext = () => {
  if (props.stepActive < props.stepConfig.length - 1) emit('update:stepActive', props.stepActive + 1)
}
const onSubmit = () => {
  emit('submit')
}

watch(
  () => props.stepActive,
  () => {
    // 步骤变化时，默认选中当前步骤第一个字段的说明
    const step = currentStep.value
    activeField.value = step?.fields?.[0]?.focusKey || ''
    nextTick(() => {
      const anchor = document.getElementById('explain-' + activeField.value)
      if (anchor) anchor.scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'nearest' })
    })
  },
  { immediate: true }
)

const currentExplanations = computed<ExplanationItem[]>(() => {
  const step = currentStep.value
  if (!step) return []
  return step.fields.map((field) => ({
    key: field.focusKey,
    title: field.explainTitle,
    text: field.explainText,
  }))
})
</script>

<style scoped>
.step-form-container {
  width: 100%;
}

/* 顶部水平步骤条样式 */
.top-steps {
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
}

.page-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  /* 左侧表单，右侧说明 */
  gap: 16px;
  align-items: start;
  margin-top: 12px;
}

.center-form .step-content {
  background: var(--el-fill-color-blank);
  border: 1px solid var(--el-border-color);
  border-radius: 15px;
  padding: 16px;
}

.step-actions {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
}


.right-explain {
  position: sticky;
  top: 12px;
  height: calc(100vh - 300px);
  max-height: 500px;
  overflow: auto;
  /* 底部渐变隐藏 */
  mask-image: linear-gradient(to bottom, black 90%, transparent 100%);
  -webkit-mask-image: linear-gradient(to bottom, black 90%, transparent 100%);
}

.explain-container {
  overflow-y: auto;
  padding-bottom: 50px;
}

.explanation-item {
  padding: 10px 12px;
  border-left: 3px solid transparent;
  margin-bottom: 12px;
  transition: background-color 0.2s ease, border-color 0.2s ease;
}

.explanation-item.active {
  border-left-color: var(--el-color-primary);
  background-color: var(--el-color-primary-light-9);
}

.explanation-item h4 {
  margin: 0 0 6px;
  font-size: 14px;
  color: var(--el-text-color-primary);
}

.explanation-item p {
  margin: 0;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}
</style>