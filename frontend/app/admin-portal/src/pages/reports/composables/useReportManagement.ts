import { ref, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { reportAPI } from '@I0/shared/api';
import type { ReportOutput, ReportListParams } from '@I0/shared/types';

/**
 * 报表管理 Composable
 * 提供报表列表的数据获取和管理功能
 */
export function useReportManagement() {
  // 状态管理
  const reports = ref<ReportOutput[]>([]);
  const loading = ref(false);

  // 计算属性 - 按分类分组的报表
  const reportsByCategory = computed(() => {
    const grouped = new Map<string, ReportOutput[]>();

    reports.value.forEach(report => {
      const category = report.category || 'Uncategorized';
      if (!grouped.has(category)) {
        grouped.set(category, []);
      }
      grouped.get(category)!.push(report);
    });

    // 转换为数组并排序
    return Array.from(grouped.entries())
      .map(([category, items]) => ({
        category,
        reports: items
      }))
      .sort((a, b) => {
        // 将 "Uncategorized" 放到最后
        if (a.category === 'Uncategorized') return 1;
        if (b.category === 'Uncategorized') return -1;
        return a.category.localeCompare(b.category);
      });
  });

  const isLoading = computed(() => loading.value);

  // 获取报表列表
  const fetchReports = async (params?: ReportListParams) => {
    loading.value = true;
    try {
      const response = await reportAPI.getReports(params);

      if (response.success && response.data) {
        reports.value = response.data;
      } else {
        ElMessage.error(response.errorMessage || 'Failed to fetch reports');
        reports.value = [];
      }
    } catch (error) {
      console.error('Failed to fetch reports:', error);
      ElMessage.error('Failed to fetch reports');
      reports.value = [];
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

  return {
    // 状态
    reports,
    reportsByCategory,
    isLoading,

    // 方法
    fetchReports,
    getStatusTagType
  };
}
