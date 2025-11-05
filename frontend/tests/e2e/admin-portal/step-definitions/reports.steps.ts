import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { ReportPage } from '../page-objects/ReportPage';
import { ChartValidators } from '../../shared/utils/chart-validators';
import type { I0World } from '../../support/world';

/**
 * 报表管理功能步骤定义（优化版）
 * 对应 reports.feature 中的6个核心场景
 */

// ============================================================================
// Background: 系统初始化和登录
// ============================================================================
// 注意：这些步骤已在 common.steps.ts 中定义

// ============================================================================
// 场景1: 查看报表列表页面
// ============================================================================

When('我访问报表管理页面', async function (this: I0World) {
  this.reportPage = new ReportPage(this.page);
  // ReportPage 不实现 CrudPageOperations，不设置 currentPage
  await this.reportPage.navigateToReportsPage();
});

Then('页面应该正确加载', async function (this: I0World) {
  await this.reportPage!.verifyReportListStructure();
});

Then('应该显示报表分类', async function (this: I0World) {
  await this.reportPage!.verifyReportCategoriesExist();
});

Then('每个报表卡片应该包含基本信息', async function (this: I0World) {
  const reportCards = this.page.locator('.report-card');
  const firstCard = reportCards.first();
  
  await expect(firstCard.locator('.report-name')).toBeVisible();
  await expect(firstCard.locator('.report-description')).toBeVisible();
  await expect(firstCard.locator('.el-tag')).toBeVisible();
});

// ============================================================================
// 场景2-4: 验证不同类型图表的数据结构（参数化实现）
// ============================================================================

When('我点击第一个包含{string}的报表', async function (this: I0World, chartType: string) {
  const typeMap: Record<string, 'pie' | 'line' | 'bar'> = {
    '饼图': 'pie',
    '折线图': 'line',
    '柱状图': 'bar'
  };
  
  const type = typeMap[chartType];
  if (!type) {
    throw new Error(`未知的图表类型: ${chartType}`);
  }
  
  // Find and click report card
  const reportCard = await this.reportPage!.findReportByChartType(type);
  await reportCard.click();
  
  // Wait for page navigation and chart rendering
  await this.reportPage!.waitForPageLoad();
  await this.page.waitForTimeout(2000); // Give charts time to initialize
  
  // Store expected chart type for later validation
  this.currentChartType = type;
});

Then('{string}应该成功渲染', async function (this: I0World, chartType: string) {
  await this.reportPage!.verifyChartRendered(0);
});

Then('饼图配置应该符合 PieChartData 接口规范', async function (this: I0World) {
  // Find chart by stored type instead of searching again
  const chartIndex = await this.reportPage!.findChartByType(this.currentChartType || 'pie');
  const option = await this.reportPage!.getChartOption(chartIndex);
  const result = ChartValidators.validatePieChart(option);
  
  if (!result.isValid) {
    console.error('饼图验证失败:', result.errors);
    if (result.warnings && result.warnings.length > 0) {
      console.warn('饼图验证警告:', result.warnings);
    }
  }
  
  expect(result.isValid).toBeTruthy();
  expect(result.errors).toHaveLength(0);
});

Then('饼图数据项应该包含 name 和 value 字段', async function (this: I0World) {
  // Find chart by stored type instead of searching again
  const chartIndex = await this.reportPage!.findChartByType(this.currentChartType || 'pie');
  const option = await this.reportPage!.getChartOption(chartIndex);
  const data = option.series?.[0]?.data as any[];
  
  expect(data).toBeDefined();
  expect(Array.isArray(data)).toBeTruthy();
  expect(data.length).toBeGreaterThan(0);
  
  // 验证第一个数据项的结构
  const firstItem = data[0];
  expect(firstItem.name).toBeDefined();
  expect(firstItem.value).toBeDefined();
  expect(typeof firstItem.name).toBe('string');
  expect(typeof firstItem.value).toBe('number');
});

Then('折线图配置应该符合 XYChartData 接口规范', async function (this: I0World) {
  // Find chart by stored type instead of searching again
  const chartIndex = await this.reportPage!.findChartByType(this.currentChartType || 'line');
  const option = await this.reportPage!.getChartOption(chartIndex);
  const result = ChartValidators.validateLineChart(option);
  
  if (!result.isValid) {
    console.error('折线图验证失败:', result.errors);
    if (result.warnings && result.warnings.length > 0) {
      console.warn('折线图验证警告:', result.warnings);
    }
  }
  
  expect(result.isValid).toBeTruthy();
  expect(result.errors).toHaveLength(0);
});

Then('柱状图配置应该符合 XYChartData 接口规范', async function (this: I0World) {
  // Find chart by stored type instead of searching again
  const chartIndex = await this.reportPage!.findChartByType(this.currentChartType || 'bar');
  const option = await this.reportPage!.getChartOption(chartIndex);
  const result = ChartValidators.validateBarChart(option);
  
  if (!result.isValid) {
    console.error('柱状图验证失败:', result.errors);
    if (result.warnings && result.warnings.length > 0) {
      console.warn('柱状图验证警告:', result.warnings);
    }
  }
  
  expect(result.isValid).toBeTruthy();
  expect(result.errors).toHaveLength(0);
});

Then('x轴和y轴数据长度应该一致', async function (this: I0World) {
  // Find chart by stored type instead of searching again
  const chartIndex = await this.reportPage!.findChartByType(this.currentChartType || 'line');
  const option = await this.reportPage!.getChartOption(chartIndex);
  const xAxis = Array.isArray(option.xAxis) ? option.xAxis[0] : option.xAxis;
  const xAxisData = (xAxis as any)?.data || [];
  const yAxisData = option.series?.[0]?.data || [];
  
  expect(xAxisData.length).toBe(yAxisData.length);
  expect(xAxisData.length).toBeGreaterThan(0);
});
