import { Page, Locator } from 'playwright';
import { expect } from '@playwright/test';
import { BasePage } from '../../shared/page-objects/BasePage';
import { ChartValidators } from '../../shared/utils/chart-validators';
import type { EChartsOption } from 'echarts';
import type { ChartValidationResult } from '../../shared/types/chart-validation';

/**
 * 报表管理页面对象类
 * 支持报表列表页和详情页的操作
 */
export class ReportPage extends BasePage {
  // 报表列表页元素
  readonly reportCards: Locator;
  readonly categoryHeaders: Locator;
  readonly loadingContainer: Locator;
  
  // 报表详情页元素
  readonly pageHeader: Locator;
  readonly chartContainers: Locator;
  readonly refreshButton: Locator;
  readonly backButton: Locator;
  
  // 报表卡片元素
  readonly reportCardNames: Locator;
  readonly reportCardDescriptions: Locator;
  readonly reportCardStatuses: Locator;
  readonly reportCardTags: Locator;

  constructor(page: Page) {
    super(page);
    
    // 报表列表页元素定位
    this.reportCards = page.locator('.report-card');
    this.categoryHeaders = page.locator('.category-title');
    this.loadingContainer = page.locator('.loading-container');
    
    // 报表详情页元素定位
    this.pageHeader = page.locator('.chart-header');
    this.chartContainers = page.locator('.chart-container');
    this.refreshButton = page.locator('button').filter({ hasText: 'Refresh Data' });
    this.backButton = page.locator('.el-page-header__back');
    
    // 报表卡片元素定位
    this.reportCardNames = page.locator('.report-name');
    this.reportCardDescriptions = page.locator('.report-description');
    this.reportCardStatuses = page.locator('.el-tag');
    this.reportCardTags = page.locator('.report-tags .el-tag');
  }

  /**
   * 导航到报表列表页
   */
  async navigateToReportsPage(): Promise<void> {
    await this.page.goto('/reports');
    await this.waitForReportsLoaded();
  }

  /**
   * 等待报表列表加载完成（监听网络请求）
   */
  async waitForReportsLoaded(): Promise<void> {
    try {
      await this.page.waitForResponse(
        response => response.url().includes('/api/reports') && response.status() === 200,
        { timeout: 10000 }
      );
      await this.reportCards.first().waitFor({ state: 'visible', timeout: 5000 });
    } catch (error) {
      // 兜底：至少等待页面加载完成
      await this.waitForPageLoad();
    }
  }

  /**
   * 导航到报表详情页
   */
  async navigateToReportDetail(reportId: string): Promise<void> {
    await this.page.goto(`/reports/${reportId}`);
    await this.waitForPageLoad();
  }

  /**
   * 点击报表卡片进入详情页
   */
  async clickReportCard(reportName: string): Promise<void> {
    const card = this.reportCards.filter({ hasText: reportName }).first();
    await card.click();
    await this.waitForPageLoad();
  }

  /**
   * 按图表类型查找报表（改进版）
   */
  async findReportByChartType(chartType: 'pie' | 'line' | 'bar'): Promise<Locator> {
    // 按报表名称关键词查找
    const chartTypeKeywords = {
      pie: ['饼图', 'Pie', 'pie', '分布', 'Distribution'],
      line: ['折线', 'Line', 'line', '趋势', 'Trend'],
      bar: ['柱状', 'Bar', 'bar', '对比', 'Comparison', '概览', 'Overview']
    };
    
    const keywords = chartTypeKeywords[chartType];
    
    // 尝试按关键词查找
    for (const keyword of keywords) {
      const card = this.reportCards.filter({ hasText: keyword });
      const count = await card.count();
      if (count > 0) {
        // 验证卡片确实包含目标关键词
        const cardText = await card.first().textContent();
        if (cardText?.includes(keyword)) {
          return card.first();
        }
      }
    }
    
    // 如果找不到，抛出错误而不是返回第一个
    throw new Error(`未找到包含 ${chartType} 类型的报表卡片。请确保测试环境中有对应的报表数据。`);
  }

  /**
   * 获取报表卡片信息
   */
  async getReportCardInfo(reportName: string): Promise<{
    name: string;
    description: string;
    status: string;
    tags: string[];
  }> {
    const card = this.reportCards.filter({ hasText: reportName }).first();
    
    const name = await card.locator('.report-name').textContent() || '';
    const description = await card.locator('.report-description').textContent() || '';
    const status = await card.locator('.el-tag').textContent() || '';
    const tags = await card.locator('.report-tags .el-tag').allTextContents();
    
    return { name, description, status, tags };
  }

  /**
   * 验证报表列表结构
   */
  async verifyReportListStructure(): Promise<void> {
    // 验证页面加载完成
    await expect(this.page.locator('.reports-page')).toBeVisible();
    
    // 验证分类标题存在
    await expect(this.categoryHeaders.first()).toBeVisible();
    
    // 验证报表卡片存在
    await expect(this.reportCards.first()).toBeVisible();
  }

  /**
   * 验证报表卡片结构
   */
  async verifyReportCardStructure(reportName: string): Promise<void> {
    const card = this.reportCards.filter({ hasText: reportName }).first();
    
    // 验证卡片元素存在
    await expect(card.locator('.report-name')).toBeVisible();
    await expect(card.locator('.report-description')).toBeVisible();
    await expect(card.locator('.el-tag')).toBeVisible();
  }

  /**
   * 验证报表详情页加载
   */
  async verifyReportDetailLoaded(): Promise<void> {
    await expect(this.pageHeader).toBeVisible();
    await expect(this.backButton).toBeVisible();
  }

  /**
   * 验证图表容器存在
   */
  async verifyChartContainersExist(): Promise<void> {
    await expect(this.chartContainers.first()).toBeVisible();
  }

  /**
   * 获取图表数量
   */
  async getChartCount(): Promise<number> {
    return await this.chartContainers.count();
  }

  /**
   * 等待图表渲染完成
   */
  async waitForChartReady(chartIndex: number = 0): Promise<void> {
    try {
      // 1. Wait for chart container to be visible first
      await this.chartContainers.nth(chartIndex).waitFor({ state: 'visible', timeout: 5000 });
      
      // 2. Wait for ECharts instance creation with better retry logic
      await this.page.waitForFunction((index) => {
        const containers = document.querySelectorAll('.chart-container');
        if (index >= containers.length) return false;
        
        const container = containers[index];
        const chartElement = container.querySelector('.base-chart');
        if (!chartElement) return false;
        
        // Check if instance exists via stored reference
        if ((chartElement as any)._echarts_instance_) {
          return true;
        }
        
        // Check via global echarts object
        const echarts = (window as any).echarts;
        if (echarts) {
          const instance = echarts.getInstanceByDom(chartElement);
          if (instance) return true;
        }
        
        return false;
      }, chartIndex, { timeout: 10000 }); // Increased timeout
      
      // 3. Additional wait for chart to finish rendering
      await this.page.waitForTimeout(1000);
      
    } catch (error) {
      console.warn(`等待图表 ${chartIndex} 就绪失败:`, error);
      throw error; // Don't swallow errors, let tests fail properly
    }
  }

  /**
   * 获取图表实例的 ECharts 配置（优化版）
   * 这是关键方法，用于获取图表的 getOption 数据
   */
  async getChartOption(chartIndex: number = 0): Promise<EChartsOption> {
    // 确保图表已准备就绪
    await this.waitForChartReady(chartIndex);
    
    try {
      const option = await this.page.evaluate((index) => {
        const containers = document.querySelectorAll('.chart-container');
        if (index >= containers.length) {
          throw new Error(`Chart container at index ${index} not found`);
        }
        
        const container = containers[index];
        const chartElement = container.querySelector('.base-chart');
        
        if (!chartElement) {
          throw new Error('Base chart element not found');
        }
        
        // 方式1: 通过 window.echarts
        const echarts = (window as any).echarts;
        if (echarts) {
          const instance = echarts.getInstanceByDom(chartElement);
          if (instance && instance.getOption) {
            return instance.getOption();
          }
        }
        
        // 方式2: 通过元素属性（兜底方案）
        const instance = (chartElement as any)._echarts_instance_;
        if (instance && instance.getOption) {
          return instance.getOption();
        }
        
        throw new Error('无法获取 ECharts 实例');
      }, chartIndex);
      
      return option;
    } catch (error) {
      throw new Error(`Failed to get chart option at index ${chartIndex}: ${error}`);
    }
  }

  /**
   * 获取所有图表的配置
   */
  async getAllChartOptions(): Promise<EChartsOption[]> {
    const chartCount = await this.getChartCount();
    const options: EChartsOption[] = [];
    
    for (let i = 0; i < chartCount; i++) {
      try {
        const option = await this.getChartOption(i);
        options.push(option);
      } catch (error) {
        console.warn(`Failed to get chart option at index ${i}:`, error);
      }
    }
    
    return options;
  }

  /**
   * 验证饼图数据结构
   */
  async verifyPieChartStructure(chartIndex: number = 0): Promise<ChartValidationResult> {
    const option = await this.getChartOption(chartIndex);
    return ChartValidators.validatePieChart(option, { strictMode: true });
  }

  /**
   * 验证折线图数据结构
   */
  async verifyLineChartStructure(chartIndex: number = 0): Promise<ChartValidationResult> {
    const option = await this.getChartOption(chartIndex);
    return ChartValidators.validateLineChart(option, { strictMode: true });
  }

  /**
   * 验证柱状图数据结构
   */
  async verifyBarChartStructure(chartIndex: number = 0): Promise<ChartValidationResult> {
    const option = await this.getChartOption(chartIndex);
    return ChartValidators.validateBarChart(option, { strictMode: true });
  }

  /**
   * 验证图表类型
   */
  async verifyChartType(chartIndex: number, expectedType: string): Promise<boolean> {
    const option = await this.getChartOption(chartIndex);
    const actualType = option.series?.[0]?.type;
    return actualType === expectedType;
  }

  /**
   * 验证图表数据长度一致性
   */
  async verifyChartDataConsistency(chartIndex: number = 0): Promise<boolean> {
    const option = await this.getChartOption(chartIndex);
    const chartType = option.series?.[0]?.type;
    
    if (chartType === 'pie') {
      // 饼图不需要验证长度一致性
      return true;
    }
    
    if (chartType === 'line' || chartType === 'bar') {
      const xAxis = option.xAxis as any;
      const xAxisData = xAxis?.data as string[];
      const yAxisData = option.series?.[0]?.data as number[];
      
      if (!xAxisData || !yAxisData) {
        return false;
      }
      
      return xAxisData.length === yAxisData.length;
    }
    
    return true;
  }

  /**
   * 验证图表配置完整性
   */
  async verifyChartConfiguration(chartIndex: number = 0): Promise<{
    hasTooltip: boolean;
    hasLegend: boolean;
    hasGrid: boolean;
    hasAxis: boolean;
  }> {
    const option = await this.getChartOption(chartIndex);
    
    return {
      hasTooltip: !!option.tooltip,
      hasLegend: !!option.legend,
      hasGrid: !!option.grid,
      hasAxis: !!(option.xAxis && option.yAxis)
    };
  }

  /**
   * 点击刷新按钮
   */
  async clickRefreshButton(): Promise<void> {
    await this.refreshButton.click();
    await this.waitForPageLoad();
  }

  /**
   * 点击返回按钮
   */
  async clickBackButton(): Promise<void> {
    await this.backButton.click();
    await this.waitForPageLoad();
  }

  /**
   * 验证加载状态
   */
  async verifyLoadingState(): Promise<void> {
    await expect(this.loadingContainer).toBeVisible();
  }


  /**
   * 等待图表加载完成
   */
  async waitForChartsToLoad(): Promise<void> {
    // 等待图表容器可见
    await this.chartContainers.first().waitFor({ state: 'visible' });
    
    // 等待 ECharts 实例创建
    await this.page.waitForFunction(() => {
      const chartContainers = document.querySelectorAll('.chart-container');
      if (chartContainers.length === 0) return false;
      
      const firstContainer = chartContainers[0];
      const baseChartElement = firstContainer.querySelector('.base-chart');
      if (!baseChartElement) return false;
      
      const chartInstance = (window as any).echarts?.getInstanceByDom(baseChartElement);
      return !!chartInstance;
    }, { timeout: 10000 });
  }

  /**
   * 获取报表分类信息
   */
  async getReportCategories(): Promise<string[]> {
    const categories = await this.categoryHeaders.allTextContents();
    return categories;
  }

  /**
   * 验证报表分类存在
   */
  async verifyReportCategoriesExist(): Promise<void> {
    const categories = await this.getReportCategories();
    expect(categories.length).toBeGreaterThan(0);
  }

  /**
   * 验证报表卡片包含必需信息
   */
  async verifyReportCardInfo(reportName: string): Promise<void> {
    const cardInfo = await this.getReportCardInfo(reportName);
    
    expect(cardInfo.name).toBeTruthy();
    expect(cardInfo.description).toBeTruthy();
    expect(cardInfo.status).toBeTruthy();
  }

  /**
   * 验证图表标题
   */
  async verifyChartTitle(chartIndex: number, expectedTitle: string): Promise<void> {
    const chartContainer = this.chartContainers.nth(chartIndex);
    const title = chartContainer.locator('.chart-title');
    await expect(title).toContainText(expectedTitle);
  }

  /**
   * 验证图表渲染状态
   */
  async verifyChartRendered(chartIndex: number): Promise<void> {
    const chartContainer = this.chartContainers.nth(chartIndex);
    const baseChart = chartContainer.locator('.base-chart');
    
    await expect(baseChart).toBeVisible();
    
    // 验证图表有内容（不是空白）
    const chartHeight = await baseChart.boundingBox();
    expect(chartHeight?.height).toBeGreaterThan(100);
  }

  /**
   * 获取图表数据统计信息
   */
  async getChartDataStats(chartIndex: number = 0): Promise<{
    dataCount: number;
    dataType: string;
    hasValidData: boolean;
  }> {
    const option = await this.getChartOption(chartIndex);
    const chartType = option.series?.[0]?.type;
    
    let dataCount = 0;
    let dataType = 'unknown';
    let hasValidData = false;
    
    if (chartType === 'pie') {
      const data = option.series?.[0]?.data as any[];
      dataCount = data?.length || 0;
      dataType = 'array';
      hasValidData = dataCount > 0;
    } else if (chartType === 'line' || chartType === 'bar') {
      const xAxis = option.xAxis as any;
      const xAxisData = xAxis?.data as any[];
      dataCount = xAxisData?.length || 0;
      dataType = 'xy';
      hasValidData = dataCount > 0;
    }
    
    return { dataCount, dataType, hasValidData };
  }

  // ============================================================================
  // 实现 BasePage 抽象方法
  // ============================================================================

  /**
   * 清理测试数据（报表页面通常不需要清理，因为报表是只读的）
   */
  async cleanupTestData(dataName: string): Promise<void> {
    // 报表页面通常不需要清理测试数据
    console.log(`ReportPage: 跳过清理测试数据 ${dataName}（报表是只读的）`);
  }

  /**
   * 批量清理测试实体
   */
  async cleanupTestEntities(dataNames: string[]): Promise<void> {
    // 报表页面通常不需要清理测试数据
    console.log(`ReportPage: 跳过批量清理测试数据 ${dataNames.join(', ')}（报表是只读的）`);
  }

  /**
   * 按后缀清理测试实体
   */
  async cleanupTestEntitiesBySuffix(suffix: string): Promise<void> {
    // 报表页面通常不需要清理测试数据
    console.log(`ReportPage: 跳过按后缀清理测试数据 ${suffix}（报表是只读的）`);
  }

  /**
   * 根据图表类型查找图表索引
   * @param chartType 图表类型
   * @returns 图表索引，如果未找到则抛出错误
   */
  async findChartByType(chartType: 'pie' | 'line' | 'bar'): Promise<number> {
    const chartCount = await this.chartContainers.count();
    
    for (let i = 0; i < chartCount; i++) {
      try {
        const option = await this.getChartOption(i);
        const series = option.series?.[0];
        if (series?.type === chartType) {
          return i;
        }
      } catch (error) {
        // 如果获取图表配置失败，继续查找下一个
        console.warn(`获取图表 ${i} 配置失败:`, error);
        continue;
      }
    }
    
    throw new Error(`未找到 ${chartType} 类型的图表`);
  }
}
