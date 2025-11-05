import type { EChartsOption } from 'echarts';

/**
 * 图表验证结果接口
 */
export interface ChartValidationResult {
  isValid: boolean;
  errors: string[];
  warnings?: string[];
}

/**
 * 饼图数据项接口
 */
export interface PieDataItem {
  name: string;
  value: number;
  itemStyle?: {
    color?: string;
  };
}

/**
 * 折线图系列数据接口
 */
export interface LineSeriesData {
  name: string;
  data: number[];
}

/**
 * 柱状图系列数据接口
 */
export interface BarSeriesData {
  name: string;
  data: number[];
  stack?: string;
  barWidth?: string | number;
}

/**
 * 图表类型枚举
 */
export type ChartType = 'pie' | 'line' | 'bar' | 'area' | 'metric' | 'table';

/**
 * 图表配置验证选项
 */
export interface ChartValidationOptions {
  strictMode?: boolean; // 严格模式，检查所有配置项
  allowEmptyData?: boolean; // 是否允许空数据
  minDataPoints?: number; // 最少数据点数量
  maxDataPoints?: number; // 最多数据点数量
}

/**
 * 图表数据结构验证结果
 */
export interface ChartDataStructureResult {
  hasValidStructure: boolean;
  dataType: string;
  dataCount: number;
  errors: string[];
  warnings: string[];
}

/**
 * 后端接口数据验证结果
 */
export interface BackendDataValidationResult {
  isAligned: boolean;
  chartTypeMatch: boolean;
  dataFormatMatch: boolean;
  dimensionMatch: boolean;
  errors: string[];
}
