<template>
  <div class="reports-page">
    <!-- 加载状态 -->
    <div v-if="isLoading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>

    <!-- 报表分类列表 -->
    <div v-else class="reports-container">
      <div v-for="group in reportsByCategory" :key="group.category" class="report-category-section">
        <!-- 分类标题 -->
        <div class="category-header">
          <h3 class="category-title">{{ group.category }}</h3>
        </div>

        <!-- 报表卡片网格 -->
        <div class="report-cards-grid">
          <el-card
            v-for="report in group.reports"
            :key="report.id"
            class="report-card"
            shadow="hover"
            :body-style="{ padding: '20px' }"
            @click="handleReportClick(report)"
          >
            <!-- 报表头部 -->
            <div class="report-card-header">
              <h4 class="report-name">{{ report.name }}</h4>
              <div class="report-status">
                <el-tag
                  v-if="report.status"
                  :type="getStatusTagType(report.status)"
                  size="small"
                  round
                >
                  {{ report.status }}
                </el-tag>
              </div>
            </div>
            
            <!-- 报表描述 -->
            <p v-if="report.description" class="report-description">
              {{ report.description }}
            </p>
            <p v-else class="report-description text-muted">No description available</p>

            <!-- 报表标签 -->
            <div v-if="report.tags" class="report-tags">
              <el-tag
                v-for="tag in parseTagsArray(report.tags)"
                :key="tag"
                size="small"
                effect="plain"
                round
              >
                {{ tag }}
              </el-tag>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="reportsByCategory.length === 0" description="No reports available" />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import type { ReportOutput } from '@I0/shared/types';
import { useReportManagement } from './composables/useReportManagement';

const router = useRouter();

// 使用 report 管理 composable
const { reportsByCategory, isLoading, fetchReports, getStatusTagType } = useReportManagement();

// 页面加载时获取报表列表
onMounted(() => {
  fetchReports();
});

// 处理报表卡片点击 - 跳转到详情页
const handleReportClick = (report: ReportOutput) => {
  router.push(`/reports/${report.id}`);
};

// 解析标签字符串为数组
const parseTagsArray = (tags: string | null | undefined): string[] => {
  if (!tags) return [];
  try {
    // 假设后端返回的是 JSON 字符串数组或逗号分隔的字符串
    if (tags.startsWith('[')) {
      return JSON.parse(tags);
    }
    return tags
      .split(',')
      .map(t => t.trim())
      .filter(t => t);
  } catch {
    return [];
  }
};
</script>

<style scoped>
.loading-container {
  padding: 24px;
}

.reports-container {
  display: flex;
  flex-direction: column;
  gap: 48px;
}

.report-category-section {
  width: 100%;
}

.category-header {
  margin-bottom: 24px;
}

.category-title {
  font-size: 20px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 12px 0;
}

.report-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 10px;
}

.report-card {
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 8px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.report-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.report-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.report-status {
  flex-shrink: 0;
}

.report-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  margin: 0 0 12px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.report-description {
  font-size: 14px;
  color: var(--el-text-color-regular);
  margin:0;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  min-height: 64px;
}

.text-muted {
  color: var(--el-text-color-secondary);
  font-style: italic;
}

.report-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 16px;
  min-height: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .reports-page {
    padding: 16px;
  }

  .report-cards-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .category-title {
    font-size: 18px;
  }
}

@media (min-width: 769px) and (max-width: 1200px) {
  .report-cards-grid {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  }
}
</style>

<route lang="yaml">
name: Reports
meta:
  title: 'Reports'
  layout: 'main'
</route>
