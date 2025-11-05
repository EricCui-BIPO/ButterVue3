import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { reportAPI } from '@I0/shared/api';
import type { ReportDataOutput } from '@I0/shared/types';

/**
 * 报表详情 Composable
 * 提供单个报表的详细信息和数据获取功能
 */
export function useReportDetail() {
  // 状态管理
  const reportData = ref<ReportDataOutput | null>(null);
  const loading = ref(false);

  const isLoading = computed(() => loading.value);

  // 获取（整合）报表数据：后端返回已包含基本信息
  const fetchReportData = async (reportId: string) => {
    loading.value = true;
    try {
      const response = await reportAPI.getReportData(reportId);

      if (response.success && response.data) {
        reportData.value = response.data;
        ElMessage.success('Report data loaded');
      } else {
        ElMessage.error(response.errorMessage || 'Failed to fetch report data');
        reportData.value = null;
      }
    } catch (error) {
      console.error('Failed to fetch report data:', error);
      ElMessage.error('Failed to fetch report data');
      reportData.value = null;
    } finally {
      loading.value = false;
    }
  };

  // 获取报表状态的标签类型
  const getStatusTagType = (status?: string | null) => {
    if (!status) return 'info';

    switch (status.toLowerCase()) {
      case 'active':
      case 'published':
        return 'success';
      case 'draft':
        return 'warning';
      case 'archived':
        return 'info';
      default:
        return 'info';
    }
  };

  // 清空数据
  const clearData = () => {
    reportData.value = null;
  };

  return {
    // 状态
    reportData,
    isLoading,

    // 方法
    fetchReportData,
    getStatusTagType,
    clearData
  };
}
