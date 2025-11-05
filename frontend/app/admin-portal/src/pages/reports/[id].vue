<template>
  <div class="report-detail-page">
    <!-- 首次加载状态 -->
    <div v-if="isInitialLoading && isLoading" class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>

    <!-- 报表详情内容 -->
    <div v-else-if="reportData" class="report-detail-content">
      <el-page-header @back="goBack">
        <template #content>
          <div class="chart-header">
            <span class="text-large font-600 mr-3"> {{ reportData.reportName || 'Unnamed Report' }} </span>
            <el-tag v-if="reportData.reportStatus" :type="getStatusTagType(reportData.reportStatus)" size="small" round>
              {{ reportData.reportStatus }}
            </el-tag>
          </div>
        </template>
      </el-page-header>

      <!-- 有图表数据时显示图表 -->
      <div v-if="hasChartData" class="charts-grid">
        <div v-for="(chart, index) in reportData.chartData" :key="index" class="chart-container">
          <div class="chart-title">
            {{ getChartTitle(chart, index) }}
          </div>

          <!-- 饼图 -->
          <PieChart v-if="isPieChart(chart)" :data="getPieChartData(chart)" />

          <!-- 折线图 -->
          <LineChart v-else-if="isLineChart(chart)" :x-axis-data="getLineChartData(chart).xAxisData"
            :series="getLineChartData(chart).series" />

          <!-- 柱状图 -->
          <BarChart v-else-if="isBarChart(chart)" :x-axis-data="getBarChartData(chart).xAxisData"
            :series="getBarChartData(chart).series" />

          <!-- 不支持的图表类型 -->
          <el-empty v-else :description="`Unsupported chart type: ${chart.chartType}`" :image-size="100" />
        </div>
      </div>

      <!-- 无图表数据时显示空状态 -->
      <el-empty v-else description="No chart data available for this report" :image-size="150">
        <el-button type="primary" :icon="Refresh" @click="handleRefresh" :loading="isLoading">
          Refresh Data
        </el-button>
      </el-empty>

    </div>

    <!-- 错误状态 -->
    <el-result v-else icon="error" title="Report Not Found" sub-title="The requested report could not be found.">
      <template #extra>
        <el-button type="primary" @click="goBack">Back to Reports</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ArrowLeft, DocumentCopy, Collection, User, Refresh } from '@element-plus/icons-vue';
import { PieChart, LineChart, BarChart } from '@I0/shared/components';
import type { PieDataItem, LineSeriesData, BarSeriesData } from '@I0/shared/components';
import type { ChartData } from '@I0/shared/types';
import { useReportDetail } from './composables/useReportDetail';

const router = useRouter();
const route = useRoute();

// 使用 report 详情 composable
const { reportData, isLoading, fetchReportData, getStatusTagType } = useReportDetail();

// 获取路由参数中的 reportId
const reportId = route.params.id as string;

// 标记是否为首次加载
const isInitialLoading = ref(true);

// 页面加载时直接获取整合后的数据
onMounted(async () => {
  await fetchReportData(reportId);
  isInitialLoading.value = false;
});

// 返回列表页
const goBack = () => {
  router.push('/reports');
};

// 刷新数据
const handleRefresh = async () => {
  await fetchReportData(reportId);
};

// 计算属性：是否有图表数据
const hasChartData = computed(() => {
  return reportData.value?.chartData && reportData.value.chartData.length > 0;
});

// ==================== 图表数据处理逻辑 ====================

// 获取图表标题
const getChartTitle = (chart: ChartData, index: number): string => {
  return chart.chartTitle || chart.chartName || `Chart ${index + 1}`;
};

// 判断是否为饼图
const isPieChart = (chart: ChartData): boolean => {
  return chart.chartType === 'pie';
};

// 判断是否为折线图
const isLineChart = (chart: ChartData): boolean => {
  return chart.chartType === 'line';
};

// 判断是否为柱状图
const isBarChart = (chart: ChartData): boolean => {
  return chart.chartType === 'bar';
};

// 获取饼图数据
// 后端返回格式: { type: 'pie', data: [{ name: '类别', value: 100 }, ...] }
const getPieChartData = (chart: ChartData): PieDataItem[] => {
  const chartData = chart.data as any;

  // 检查新格式：{ type: 'pie', data: [{ name, value }] }
  if (chartData && chartData.type === 'pie' && Array.isArray(chartData.data)) {
    return chartData.data.map((item: any) => ({
      name: item.name || 'Unknown',
      value: item.value || 0
    }));
  }

  // 如果直接是数组格式
  if (Array.isArray(chartData)) {
    return chartData.map((item: any) => ({
      name: item.name || 'Unknown',
      value: item.value || 0
    }));
  }

  return [];
};

// 获取折线图数据
// 后端返回格式: { type: 'xy', xAxis: '维度名', data: { xAxis: [...], yAxis: [...] } }
const getLineChartData = (chart: ChartData) => {
  const chartData = chart.data as any;

  // 检查新格式：{ type: 'xy', data: { xAxis: [], yAxis: [] } }
  if (chartData && chartData.type === 'xy' && chartData.data) {
    const data = chartData.data;
    const xAxisData = data.xAxis || [];
    const yAxisData = data.yAxis || [];

    const series: LineSeriesData[] = [
      {
        name: chart.indicatorName || chart.chartName || 'Data',
        data: yAxisData
      }
    ];

    return { xAxisData, series };
  }

  return { xAxisData: [], series: [] };
};

// 获取柱状图数据
// 后端返回格式: { type: 'xy', xAxis: '维度名', data: { xAxis: [...], yAxis: [...] } }
const getBarChartData = (chart: ChartData) => {
  const chartData = chart.data as any;

  // 检查新格式：{ type: 'xy', data: { xAxis: [], yAxis: [] } }
  if (chartData && chartData.type === 'xy' && chartData.data) {
    const data = chartData.data;
    const xAxisData = data.xAxis || [];
    const yAxisData = data.yAxis || [];

    const series: BarSeriesData[] = [
      {
        name: chart.indicatorName || chart.chartName || 'Data',
        data: yAxisData
      }
    ];

    return { xAxisData, series };
  }

  return { xAxisData: [], series: [] };
};
</script>

<style scoped>
.report-detail-page {
  padding: 0;
  /* 移除页面内边距 */
}

.loading-container {
  padding: 24px;
}

.report-detail-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

/* 图表可视化样式 */
.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 10px;
}

.chart-container {
  background: var(--el-fill-color-blank);
  border: 1px solid var(--el-border-color);
  border-radius: 7px;
  padding: 20px;
  min-height: 400px;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
  text-align: center;
  flex-shrink: 0;
}

.chart-container :deep(.base-chart) {
  flex: 1;
  min-height: 300px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .report-detail-page {
    padding: 0;
  }

  .charts-grid {
    grid-template-columns: 1fr;
  }
}
</style>

<route lang="yaml">
name: ReportDetail
meta:
  title: 'Report Detail'
  layout: 'main'
</route>
