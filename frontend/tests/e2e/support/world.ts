import { IWorldOptions, World, setWorldConstructor } from '@cucumber/cucumber';
import type { Browser, BrowserContext, Page } from 'playwright';
import type { BasePage } from '../shared/page-objects/BasePage';
import type { ServiceTypePage } from '../admin-portal/page-objects/ServiceTypePage';
import type { EntityPage } from '../admin-portal/page-objects/EntityPage';
import type { ClientPage } from '../admin-portal/page-objects/ClientPage';
import type { ReportPage } from '../admin-portal/page-objects/ReportPage';
import type { CrudPageOperations } from '../shared/page-objects/BasePage';
import { TestDataManager, TestDataResourceType } from './test-data-manager';
import { logger } from '../shared/utils/logger';
import { PORTAL_CONFIGS, getPortalConfig, setCurrentPortal, type Portal } from '../config/test-config';

export type SupportedBrowser = 'chromium' | 'firefox' | 'webkit';

export interface WorldParameters {
  baseUrl?: string;
  browser?: SupportedBrowser;
  headless?: string | boolean;
  slowMo?: string | number;
  defaultPortal?: Portal;
  portalBaseUrls?: Partial<Record<Portal, string>>;
}

export class I0World extends World<WorldParameters> {
  browser?: Browser;
  context?: BrowserContext;
  page!: Page;
  basePage?: BasePage;
  serviceTypePage?: ServiceTypePage;
  serviceName?: string;
  newServiceName?: string;
  entityPage?: EntityPage;
  entityName?: string;
  newEntityName?: string;
  clientPage?: ClientPage;
  clientName?: string;
  newClientName?: string;
  reportPage?: ReportPage;
  currentChartType?: 'pie' | 'line' | 'bar'; // 当前测试的图表类型
  testDataSuffix?: string; // 测试数据唯一后缀，用于数据隔离
  testDataManager: TestDataManager; // 统一的测试数据管理器
  testStartTime?: number; // 📊 测试开始时间，用于报告生成
  baseUrl: string;
  readonly browserName: SupportedBrowser;
  readonly headless: boolean;
  readonly slowMo: number;
  scenarioName?: string; // 添加场景名称属性
  
  // 新增：当前活动页面
  currentPage?: CrudPageOperations;
  defaultPortal: Portal;
  currentPortal: Portal;
  portalBaseUrls: Partial<Record<Portal, string>>;
  customBaseUrl?: string;

  constructor(options: IWorldOptions<WorldParameters>) {
    super(options);
    this.portalBaseUrls = this.initializePortalBaseUrls(options.parameters.portalBaseUrls);
    this.defaultPortal = this.normalizePortal(options.parameters.defaultPortal);
    this.currentPortal = this.defaultPortal;
    setCurrentPortal(this.currentPortal);

    const providedBaseUrl = options.parameters.baseUrl || process.env.BASE_URL;
    if (typeof providedBaseUrl === 'string' && providedBaseUrl.length > 0) {
      this.customBaseUrl = providedBaseUrl;
      this.baseUrl = providedBaseUrl;
    } else {
      this.baseUrl = this.resolvePortalBaseUrl(this.currentPortal);
    }
    const browser = (options.parameters.browser || process.env.BROWSER || 'chromium').toLowerCase();
    if (browser === 'firefox' || browser === 'webkit') {
      this.browserName = browser;
    } else {
      this.browserName = 'chromium';
    }
    const headlessParam = options.parameters.headless ?? process.env.HEADLESS ?? 'true';
    this.headless = `${headlessParam}`.toLowerCase() === 'true';
    const slowMoParam = options.parameters.slowMo ?? process.env.SLOW_MO ?? '0';
    const parsedSlowMo = Number(slowMoParam);
    this.slowMo = Number.isNaN(parsedSlowMo) ? 0 : parsedSlowMo;
    
    // 生成测试数据唯一后缀，用于数据隔离
    this.testDataSuffix = Date.now().toString();
    // 初始化测试数据管理器
    this.testDataManager = new TestDataManager();
  }

  // 设置场景名称的方法
  setScenarioName(name: string): void {
    this.scenarioName = name;
  }

  // 获取场景名称的方法
  getScenarioName(): string {
    return this.scenarioName || 'Unknown Scenario';
  }

  // ============================================================================
  // 当前页面上下文管理方法
  // ============================================================================

  /**
   * 设置当前活动页面
   * @param page 实现了 CrudPageOperations 接口的页面对象
   */
  setCurrentPage(page: CrudPageOperations): void {
    this.currentPage = page;
  }

  /**
   * 获取当前活动页面（带验证）
   * @returns 当前活动页面对象
   * @throws Error 如果没有设置当前页面
   */
  getCurrentPage(): CrudPageOperations {
    if (!this.currentPage) {
      throw new Error('没有设置当前页面，请先访问具体的管理页面');
    }
    return this.currentPage;
  }

  private initializePortalBaseUrls(
    overrides?: Partial<Record<Portal, string>>
  ): Partial<Record<Portal, string>> {
    const baseUrls: Partial<Record<Portal, string>> = {};
    const portals = Object.keys(PORTAL_CONFIGS) as Portal[];
    for (const portal of portals) {
      const overrideUrl = overrides?.[portal];
      if (overrideUrl) {
        baseUrls[portal] = overrideUrl;
      }
    }
    return baseUrls;
  }

  private resolvePortalBaseUrl(portal: Portal): string {
    return this.portalBaseUrls[portal] || getPortalConfig(portal).baseUrl;
  }

  private normalizePortal(portal?: Portal | string): Portal {
    if (!portal) {
      return 'admin';
    }
    const portalKey = `${portal}`.toLowerCase() as Portal;
    if (Object.prototype.hasOwnProperty.call(PORTAL_CONFIGS, portalKey)) {
      return portalKey;
    }
    return 'admin';
  }

  setPortal(portal: Portal): void {
    const normalized = this.normalizePortal(portal);
    this.currentPortal = normalized;
    setCurrentPortal(normalized);
    if (this.customBaseUrl) {
      this.baseUrl = this.customBaseUrl;
    } else {
      this.baseUrl = this.resolvePortalBaseUrl(normalized);
    }
  }

  getCurrentPortal(): Portal {
    return this.currentPortal;
  }

  // 生成唯一的测试数据名称
  generateUniqueTestName(baseName: string): string {
    return `${baseName}_${this.testDataSuffix}`;
  }

  // 记录测试数据（通用方法）
  recordTestData(type: TestDataResourceType, name: string, verified: boolean = false): void {
    this.testDataManager.recordData(type, name, verified);
  }

  // 验证测试数据创建成功
  verifyTestDataCreated(type: TestDataResourceType, name: string): void {
    this.testDataManager.verifyDataCreated(type, name);
  }

  // 获取指定类型的测试数据记录
  getTestDataByType(type: TestDataResourceType) {
    return this.testDataManager.getRecordsByType(type);
  }

  // 获取所有已验证的测试数据
  getVerifiedTestData() {
    return this.testDataManager.getVerifiedRecords();
  }

  // 检查是否有指定类型的测试数据
  hasTestDataOfType(type: TestDataResourceType): boolean {
    return this.testDataManager.hasRecordsOfType(type);
  }

  // 检查是否有任何测试数据
  hasAnyTestData(): boolean {
    return this.testDataManager.hasAnyRecords();
  }

  // 清空所有测试数据记录
  clearTestDataRecords(): void {
    this.testDataManager.clearRecords();
  }

  // 打印测试数据状态（调试用）
  printTestDataStatus(): void {
    this.testDataManager.printStatus();
  }

  // 清理所有测试数据（统一入口）
  async cleanupAllTestData(): Promise<void> {
    // 检查是否设置了保留测试数据的环境变量（调试时使用）
    const retainTestData = process.env.RETAIN_TEST_DATA?.toLowerCase() === 'true';
    if (retainTestData) {
      logger.info('📌 调试模式: 保留测试数据，跳过清理');
      this.printTestDataStatus();
      return;
    }

    // 检查是否有任何测试数据需要清理
    if (!this.hasAnyTestData()) {
      logger.debug('📝 没有测试数据需要清理');
      return;
    }

    try {
      logger.info('🧹 开始清理所有测试数据...');
      this.printTestDataStatus();

      // 获取所有已验证的测试数据
      const verifiedRecords = this.getVerifiedTestData();
      
      // 按类型分组清理
      const recordsByType = verifiedRecords.reduce((acc, record) => {
        if (!acc[record.type]) {
          acc[record.type] = [];
        }
        acc[record.type].push(record.name);
        return acc;
      }, {} as Record<TestDataResourceType, string[]>);

      // 清理各类型的数据
      for (const [type, names] of Object.entries(recordsByType)) {
        await this.cleanupTestDataByType(type as TestDataResourceType, names);
      }

      // 兜底清理：使用后缀清理（防止遗漏）
      const enableSuffixCleanup = process.env.ENABLE_SUFFIX_CLEANUP?.toLowerCase() !== 'false';
      if (enableSuffixCleanup && this.testDataSuffix) {
        logger.debug(`🧹 执行后缀兜底清理: *_${this.testDataSuffix}`);
        await this.cleanupTestDataBySuffix(this.testDataSuffix);
      }

      // 清空记录
      this.clearTestDataRecords();
      
      logger.info('✅ 所有测试数据清理完成');
    } catch (error) {
      logger.warn('⚠️ 测试数据清理失败:', error);
      // 清理失败不应中断测试流程
    }
  }

  // 按类型清理测试数据
  private async cleanupTestDataByType(type: TestDataResourceType, names: string[]): Promise<void> {
    if (names.length === 0) return;

    logger.debug(`🧹 清理 ${type} 类型测试数据: ${names.length} 条`);

    try {
      switch (type) {
        case 'entity':
          if (this.entityPage) {
            await this.entityPage.cleanupTestEntities(names);
          }
          break;
        case 'serviceType':
          if (this.serviceTypePage) {
            await this.serviceTypePage.cleanupTestEntities(names);
          }
          break;
        case 'client':
          if (this.clientPage) {
            await this.clientPage.cleanupTestEntities(names);
          }
          break;
        case 'location':
        case 'service':
        case 'talent':
        case 'tenant':
          // 这些模块的页面对象还未实现，暂时跳过
          logger.warn(`⚠️ ${type} 类型的清理方法尚未实现，跳过清理`);
          break;
        default:
          logger.warn(`⚠️ 未知的测试数据类型: ${type}`);
      }
    } catch (error) {
      logger.warn(`⚠️ 清理 ${type} 类型测试数据失败:`, error);
    }
  }

  // 按后缀清理测试数据（兜底机制）
  private async cleanupTestDataBySuffix(suffix: string): Promise<void> {
    try {
      // 清理实体数据
      if (this.entityPage) {
        await this.entityPage.cleanupTestEntitiesBySuffix(suffix);
      }

      // 清理服务类型数据
      if (this.serviceTypePage) {
        await this.serviceTypePage.cleanupTestEntitiesBySuffix(suffix);
      }

      // 清理客户数据
      if (this.clientPage) {
        await this.clientPage.cleanupTestEntitiesBySuffix(suffix);
      }

      // 其他模块的清理方法待实现...
    } catch (error) {
      logger.warn(`⚠️ 后缀清理失败 [suffix: ${suffix}]:`, error);
    }
  }
}

setWorldConstructor(I0World);
