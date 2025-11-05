import type { EChartsOption } from 'echarts';
import type { 
  ChartValidationResult, 
  ChartValidationOptions, 
  ChartDataStructureResult,
  BackendDataValidationResult,
  PieDataItem,
  LineSeriesData,
  BarSeriesData
} from '../types/chart-validation';

/**
 * 图表验证器类
 * 提供各种图表类型的数据结构验证功能
 */
export class ChartValidators {
  
  /**
   * 验证饼图结构
   */
  static validatePieChart(
    option: EChartsOption, 
    options: ChartValidationOptions = {}
  ): ChartValidationResult {
    const errors: string[] = [];
    const warnings: string[] = [];
    
    try {
      // 1. 验证 series 存在且为数组
      if (!option.series || !Array.isArray(option.series) || option.series.length === 0) {
        errors.push('Series configuration is missing or empty');
        return { isValid: false, errors, warnings };
      }
      
      const series = option.series[0];
      
      // 2. 验证图表类型
      if (series.type !== 'pie') {
        errors.push(`Expected chart type 'pie', but got '${series.type}'`);
      }
      
      // 3. 验证数据结构
      if (!series.data || !Array.isArray(series.data)) {
        errors.push('Pie chart data should be an array');
        return { isValid: false, errors, warnings };
      }
      
      const data = series.data as PieDataItem[];
      
      // 4. 验证数据项结构
      data.forEach((item, index) => {
        if (!item || typeof item !== 'object') {
          errors.push(`Data item ${index} is not an object`);
          return;
        }
        
        if (!item.name || typeof item.name !== 'string') {
          errors.push(`Data item ${index} missing or invalid name field`);
        }
        
        if (typeof item.value !== 'number' || isNaN(item.value)) {
          errors.push(`Data item ${index} missing or invalid value field`);
        }
      });
      
      // 5. 验证数据点数量
      if (data.length === 0 && !options.allowEmptyData) {
        errors.push('Pie chart data cannot be empty');
      }
      
      if (options.minDataPoints && data.length < options.minDataPoints) {
        errors.push(`Pie chart requires at least ${options.minDataPoints} data points`);
      }
      
      if (options.maxDataPoints && data.length > options.maxDataPoints) {
        warnings.push(`Pie chart has more than ${options.maxDataPoints} data points`);
      }
      
      // 6. 验证数据合理性
      const total = data.reduce((sum, item) => sum + (item.value || 0), 0);
      if (total <= 0 && data.length > 0) {
        errors.push('Pie chart data total should be positive');
      }
      
      // 7. 验证可选配置项（仅警告）
      if (!option.tooltip) {
        warnings.push('Tooltip configuration is missing');
      }
      if (!option.legend) {
        warnings.push('Legend configuration is missing');
      }
      if (!(series as any).radius) {
        warnings.push('Pie chart radius configuration is missing');
      }
      
      return { 
        isValid: errors.length === 0, 
        errors, 
        warnings: warnings.length > 0 ? warnings : undefined 
      };
      
    } catch (error) {
      return { 
        isValid: false, 
        errors: [`Pie chart validation failed: ${error}`] 
      };
    }
  }
  
  /**
   * 验证折线图结构
   */
  static validateLineChart(
    option: EChartsOption, 
    options: ChartValidationOptions = {}
  ): ChartValidationResult {
    const errors: string[] = [];
    const warnings: string[] = [];
    
    try {
      // 1. 验证 series 存在
      if (!option.series || !Array.isArray(option.series) || option.series.length === 0) {
        errors.push('Series configuration is missing or empty');
        return { isValid: false, errors, warnings };
      }
      
      const series = option.series[0];
      
      // 2. 验证图表类型
      if (series.type !== 'line') {
        errors.push(`Expected chart type 'line', but got '${series.type}'`);
      }
      
      // 3. 验证 xAxis 配置（兼容数组形式）
      const xAxis = Array.isArray(option.xAxis) ? option.xAxis[0] : option.xAxis;
      if (!xAxis || !(xAxis as any).data || !Array.isArray((xAxis as any).data)) {
        errors.push('X-axis data is missing or not an array');
        return { isValid: false, errors, warnings };
      }
      
      const xAxisData = (xAxis as any).data as string[];
      
      // 4. 验证 series 数据
      if (!series.data || !Array.isArray(series.data)) {
        errors.push('Line chart series data should be an array');
        return { isValid: false, errors, warnings };
      }
      
      const yAxisData = series.data as number[];
      
      // 5. 验证数据长度一致性
      if (xAxisData.length !== yAxisData.length) {
        errors.push(`X-axis data length (${xAxisData.length}) does not match Y-axis data length (${yAxisData.length})`);
      }
      
      // 6. 验证数据点数量
      if (xAxisData.length === 0 && !options.allowEmptyData) {
        errors.push('Line chart data cannot be empty');
      }
      
      if (options.minDataPoints && xAxisData.length < options.minDataPoints) {
        errors.push(`Line chart requires at least ${options.minDataPoints} data points`);
      }
      
      // 7. 验证数据合理性
      const hasValidNumbers = yAxisData.every(value => typeof value === 'number' && !isNaN(value));
      if (!hasValidNumbers) {
        errors.push('Line chart Y-axis data contains invalid numbers');
      }
      
      // 8. 验证可选配置项（仅警告）
      if (!option.yAxis) {
        warnings.push('Y-axis configuration is missing');
      }
      if (!option.grid) {
        warnings.push('Grid configuration is missing');
      }
      if (!(series as any).smooth && (series as any).smooth !== false) {
        warnings.push('Line smooth configuration is not explicitly set');
      }
      
      return { 
        isValid: errors.length === 0, 
        errors, 
        warnings: warnings.length > 0 ? warnings : undefined 
      };
      
    } catch (error) {
      return { 
        isValid: false, 
        errors: [`Line chart validation failed: ${error}`] 
      };
    }
  }
  
  /**
   * 验证柱状图结构
   */
  static validateBarChart(
    option: EChartsOption, 
    options: ChartValidationOptions = {}
  ): ChartValidationResult {
    const errors: string[] = [];
    const warnings: string[] = [];
    
    try {
      // 1. 验证 series 存在
      if (!option.series || !Array.isArray(option.series) || option.series.length === 0) {
        errors.push('Series configuration is missing or empty');
        return { isValid: false, errors, warnings };
      }
      
      const series = option.series[0];
      
      // 2. 验证图表类型
      if (series.type !== 'bar') {
        errors.push(`Expected chart type 'bar', but got '${series.type}'`);
      }
      
      // 3. 验证 xAxis 配置（兼容数组形式）
      const xAxis = Array.isArray(option.xAxis) ? option.xAxis[0] : option.xAxis;
      if (!xAxis || !(xAxis as any).data || !Array.isArray((xAxis as any).data)) {
        errors.push('X-axis data is missing or not an array');
        return { isValid: false, errors, warnings };
      }
      
      const xAxisData = (xAxis as any).data as string[];
      
      // 4. 验证 series 数据
      if (!series.data || !Array.isArray(series.data)) {
        errors.push('Bar chart series data should be an array');
        return { isValid: false, errors, warnings };
      }
      
      const yAxisData = series.data as number[];
      
      // 5. 验证数据长度一致性
      if (xAxisData.length !== yAxisData.length) {
        errors.push(`X-axis data length (${xAxisData.length}) does not match Y-axis data length (${yAxisData.length})`);
      }
      
      // 6. 验证数据点数量
      if (xAxisData.length === 0 && !options.allowEmptyData) {
        errors.push('Bar chart data cannot be empty');
      }
      
      if (options.minDataPoints && xAxisData.length < options.minDataPoints) {
        errors.push(`Bar chart requires at least ${options.minDataPoints} data points`);
      }
      
      // 7. 验证数据合理性
      const hasValidNumbers = yAxisData.every(value => typeof value === 'number' && !isNaN(value));
      if (!hasValidNumbers) {
        errors.push('Bar chart Y-axis data contains invalid numbers');
      }
      
      // 8. 验证可选配置项（仅警告）
      if (!option.yAxis) {
        warnings.push('Y-axis configuration is missing');
      }
      if (!option.grid) {
        warnings.push('Grid configuration is missing');
      }
      if (!(series as any).barGap && (series as any).barGap !== 0) {
        warnings.push('Bar gap configuration is not explicitly set');
      }
      
      return { 
        isValid: errors.length === 0, 
        errors, 
        warnings: warnings.length > 0 ? warnings : undefined 
      };
      
    } catch (error) {
      return { 
        isValid: false, 
        errors: [`Bar chart validation failed: ${error}`] 
      };
    }
  }
  
  /**
   * 验证数据趋势合理性
   */
  static validateDataTrend(data: number[]): boolean {
    if (!Array.isArray(data) || data.length === 0) {
      return false;
    }
    
    // 检查数据非负
    const hasNegativeValues = data.some(value => value < 0);
    if (hasNegativeValues) {
      return false;
    }
    
    // 检查数据范围合理（避免异常大的数值）
    const maxValue = Math.max(...data);
    const minValue = Math.min(...data);
    
    // 如果最大值过大或最小值过小，可能数据异常
    if (maxValue > 1e10 || (minValue < 0 && minValue !== 0)) {
      return false;
    }
    
    return true;
  }
  
  /**
   * 验证图表数据结构
   */
  static validateChartDataStructure(
    option: EChartsOption, 
    chartType: string
  ): ChartDataStructureResult {
    const errors: string[] = [];
    const warnings: string[] = [];
    
    try {
      let dataCount = 0;
      let dataType = 'unknown';
      
      switch (chartType.toLowerCase()) {
        case 'pie':
          if (option.series && option.series[0] && option.series[0].data) {
            dataCount = (option.series[0].data as any[]).length;
            dataType = 'array';
          }
          break;
          
        case 'line':
        case 'bar':
          const xAxis = Array.isArray(option.xAxis) ? option.xAxis[0] : option.xAxis;
          if (xAxis && (xAxis as any).data) {
            dataCount = ((xAxis as any).data as any[]).length;
            dataType = 'xy';
          }
          break;
          
        default:
          warnings.push(`Unknown chart type: ${chartType}`);
      }
      
      if (dataCount === 0) {
        errors.push('No data found in chart');
      }
      
      return {
        hasValidStructure: errors.length === 0,
        dataType,
        dataCount,
        errors,
        warnings
      };
      
    } catch (error) {
      return {
        hasValidStructure: false,
        dataType: 'error',
        dataCount: 0,
        errors: [`Structure validation failed: ${error}`],
        warnings: []
      };
    }
  }
  
  /**
   * 验证后端接口与前端组件对齐性
   */
  static validateBackendAlignment(
    backendData: any,
    frontendOption: EChartsOption
  ): BackendDataValidationResult {
    const errors: string[] = [];
    
    try {
      // 1. 验证图表类型匹配
      const backendType = backendData.chartType;
      const frontendType = frontendOption.series?.[0]?.type;
      const chartTypeMatch = backendType === frontendType;
      
      if (!chartTypeMatch) {
        errors.push(`Chart type mismatch: backend '${backendType}' vs frontend '${frontendType}'`);
      }
      
      // 2. 验证数据格式匹配
      let dataFormatMatch = false;
      
      if (backendType === 'pie' && backendData.data?.type === 'pie') {
        dataFormatMatch = true;
      } else if ((backendType === 'line' || backendType === 'bar') && backendData.data?.type === 'xy') {
        dataFormatMatch = true;
      }
      
      if (!dataFormatMatch) {
        errors.push(`Data format mismatch: backend type '${backendData.data?.type}' does not match chart type '${backendType}'`);
      }
      
      // 3. 验证维度匹配
      const dimensionMatch = backendData.dimension === backendData.data?.xAxis;
      if (!dimensionMatch) {
        errors.push(`Dimension mismatch: backend dimension '${backendData.dimension}' vs data xAxis '${backendData.data?.xAxis}'`);
      }
      
      return {
        isAligned: errors.length === 0,
        chartTypeMatch,
        dataFormatMatch,
        dimensionMatch,
        errors
      };
      
    } catch (error) {
      return {
        isAligned: false,
        chartTypeMatch: false,
        dataFormatMatch: false,
        dimensionMatch: false,
        errors: [`Backend alignment validation failed: ${error}`]
      };
    }
  }
}
