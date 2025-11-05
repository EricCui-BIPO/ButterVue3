// Report 模块类型定义，对应后端 ReportOutput DTO

// 报表基础输出结构
export interface ReportOutput {
  id: string;
  name: string;
  description?: string | null;
  status?: string | null;
  category?: string | null;
  tags?: string | null;
  layout?: string | null;
  theme?: string | null;
  refreshInterval?: number | null;
  enabled?: boolean | null;
  publicAccess?: boolean | null;
  creator?: string | null;
  chartCount?: number | null;
}

// 报表列表查询参数（预留，如后续扩展）
export interface ReportListParams {
  // 未来可添加分页、筛选、分类等参数
  category?: string;
  enabled?: boolean;
}

// 报表数据输出（对应后端 ReportDataOutput）
export interface ReportDataOutput {
  reportId: string;
  reportName: string;
  reportStatus: string;
  chartData: ChartData[];
  refreshInterval?: number | null;
  theme?: string | null;
  layout?: string | null;
  timestamp: number;
}

// 图表数据结构（对应后端 executeChartQuery 返回的结构）
export interface ChartData {
  chartId: string;
  chartName: string;
  chartType: 'pie' | 'bar' | 'line' | 'area' | 'metric' | 'table' | string;
  chartTitle: string;
  dimension?: string;
  indicatorName: string;
  unit?: string;
  config?: Record<string, any>;
  data: ChartDataContent;
}

// 图表数据内容（根据类型不同而不同）
export type ChartDataContent = PieChartData | XYChartData | MetricChartData | TableChartData;

// 饼图数据格式 - 后端返回的实际格式
export interface PieChartData {
  type: 'pie';
  data: Array<{
    name: string;
    value: number;
  }>;
}

// XY轴图表数据格式（柱状图、折线图、面积图）- 后端返回的实际格式
export interface XYChartData {
  type: 'xy';
  xAxis: string;
  data: {
    xAxis: string[];
    yAxis: number[];
  };
}

// 指标卡数据格式
export interface MetricChartData {
  type: 'metric';
  value: number | string;
  calculation?: string;
}

// 表格数据格式
export interface TableChartData {
  type: 'table';
  rows: Record<string, any>[];
}

// 过滤条件接口（对应后端 Filter 值对象）
export interface Filter {
  /** 过滤字段名 */
  field: string;
  /** 操作符：=, >, <, IN, LIKE, BETWEEN等 */
  operator: string;
  /** 过滤值 */
  value: any;
  /** 是否为强制过滤条件（不可绕过） */
  mandatory?: boolean;
}

// 图表数据输出（单个图表）- 用于 getChartData 接口
export interface ChartDataOutput extends ChartData {}
