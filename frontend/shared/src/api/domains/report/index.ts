import request from '../../core/request';
import type {
  ApiResponse,
  ReportOutput,
  ReportListParams,
  ReportDataOutput,
  ChartDataOutput,
  Filter
} from '@I0/shared/types';

// 报表 API
class ReportAPI {
  private readonly basePath = '/reports';

  // 获取所有报表列表（后端: GET /api/reports）
  async getReports(params?: ReportListParams): Promise<ApiResponse<ReportOutput[]>> {
    return request.get<ReportOutput[]>(`${this.basePath}`, params);
  }

  // 获取报表配置信息（后端: GET /api/reports/{reportId}/info）
  async getReportInfo(reportId: string): Promise<ApiResponse<ReportOutput>> {
    return request.get<ReportOutput>(`${this.basePath}/${reportId}/info`);
  }

  // 获取报表数据（后端: POST /api/reports/{reportId}/data）
  async getReportData(reportId: string, filters?: Filter[]): Promise<ApiResponse<ReportDataOutput>> {
    return request.post<ReportDataOutput>(`${this.basePath}/${reportId}/data`, filters || []);
  }

  // 获取单个图表数据（后端: GET /api/reports/charts/{chartId}/data）
  async getChartData(chartId: string): Promise<ApiResponse<ChartDataOutput>> {
    return request.get<ChartDataOutput>(`${this.basePath}/charts/${chartId}/data`);
  }
}

export const reportAPI = new ReportAPI();
