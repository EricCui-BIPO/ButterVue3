import { Page, Locator } from '@playwright/test';
import { getCurrentPortalConfig, getPortalConfig } from '../../config/test-config';
import type { PortalConfig } from '../../config/test-config';

/**
 * CRUD 页面操作接口
 * 定义所有 CRUD 页面的通用操作方法
 * 用于消除 common.steps.ts 中的 if-else 判断
 */
export interface CrudPageOperations {
  // 搜索框元素
  getSearchInput(): Locator;
  
  // 按钮操作
  clickCreateButton(): Promise<void>;
  clickUpdateButton(): Promise<void>;
  clickSearchButton(): Promise<void>;
  resetSearch(): Promise<void>;
  
  // 表单操作
  attemptCreateEmpty(): Promise<void>;
  
  // 筛选操作
  filterByStatus(status: string): Promise<void>;
  filterByType(type: string): Promise<void>;
  
  // 搜索操作
  searchByName(name: string): Promise<void>;
  
  // CRUD 操作
  create(name: string, type: string, description?: string): Promise<void>;
  edit(dataName: string, newName?: string, newDescription?: string): Promise<void>;
  delete(dataName: string): Promise<void>;
  activate(dataName: string): Promise<void>;
  deactivate(dataName: string): Promise<void>;
  
  // 对话框操作
  openCreateDialog(): Promise<void>;
  cancelCreate(): Promise<void>;
  verifyDialogClosed(): Promise<void>;
  
  // 分页操作
  goToNextPage(): Promise<void>;
  goToPreviousPage(): Promise<void>;
  verifyPageChanged(): Promise<void>;
  
  // 验证操作
  verifyFormValidationError(errorMessage: string): Promise<void>;
  verifyNoDataMessage(message: string): Promise<void>;
  verifyNoDataState(): Promise<void>;
  verifyAllResultsAreOfStatus(status: string): Promise<void>;
  verifyAllResultsAreOfType(type: string): Promise<void>;
  verifyExists(dataName: string): Promise<void>;
  verifyNotExists(dataName: string): Promise<void>;
  verifyStatus(dataName: string, status: string): Promise<void>;
  verifySearchResultContains(dataName: string): Promise<void>;
  verifySearchFormReset(): Promise<void>;
  waitForTableData(): Promise<void>;
  
  // 测试数据操作
  createActiveDataForTesting(dataName: string): Promise<void>;
  cleanupTestData(dataName: string): Promise<void>;
  
  // 组件访问
  dialog: any; // 临时解决方案，后续可以定义更具体的类型
}

/**
 * 测试数据清理接口
 * 定义所有页面对象必须实现的清理方法
 */
export interface TestDataCleanupCapable {
  /**
   * 清理单个测试数据
   * @param dataName 数据名称
   */
  cleanupTestData(dataName: string): Promise<void>;

  /**
   * 批量清理测试数据
   * @param dataNames 数据名称数组
   */
  cleanupTestEntities(dataNames: string[]): Promise<void>;

  /**
   * 按后缀清理测试数据（兜底机制）
   * @param suffix 后缀字符串
   */
  cleanupTestEntitiesBySuffix(suffix: string): Promise<void>;
}

/**
 * 基础页面对象类
 * 包含所有页面共用的基础功能，如系统初始化、登录等
 * 遵循 Page Object Model (PO) 模式
 */
export abstract class BasePage implements TestDataCleanupCapable {
  readonly page: Page;
  protected readonly portalConfig: PortalConfig;

  // 登录页面相关元素
  readonly usernameInput: Locator;
  readonly passwordInput: Locator;
  readonly loginButton: Locator;

  constructor(page: Page, portalKey?: string) {
    this.page = page;
    this.portalConfig = portalKey ? getPortalConfig(portalKey) : getCurrentPortalConfig();
    
    // 登录页面元素
    this.usernameInput = page.locator('[data-testid="username"]');
    this.passwordInput = page.locator('[data-testid="password"]');
    this.loginButton = page.locator('[data-testid="login-button"]');
  }

  /**
   * 系统初始化
   * 导航到系统首页并等待页面加载完成
   */
  async initializeSystem(): Promise<void> {
    await this.page.goto(this.portalConfig.baseUrl);
    await this.page.waitForLoadState('networkidle');
  }

  /**
   * 管理员登录
   * 使用默认的管理员账号密码登录系统
   */
  async loginAsAdmin(): Promise<void> {
    await this.page.goto(`${this.portalConfig.baseUrl}${this.portalConfig.loginPath}`);
    await this.usernameInput.fill(this.portalConfig.defaultUsername);
    await this.passwordInput.fill(this.portalConfig.defaultPassword);
    await this.loginButton.click();
    await this.page.waitForURL('**/dashboard');
  }

  /**
   * 自定义登录
   * @param username 用户名
   * @param password 密码
   */
  async login(username: string, password: string): Promise<void> {
    await this.page.goto(`${this.portalConfig.baseUrl}${this.portalConfig.loginPath}`);
    await this.usernameInput.fill(username);
    await this.passwordInput.fill(password);
    await this.loginButton.click();
    await this.page.waitForURL('**/dashboard');
  }

  /**
   * 等待页面加载完成
   * @param timeout 超时时间（毫秒）
   */
  async waitForPageLoad(timeout: number = 10000): Promise<void> {
    await this.page.waitForLoadState('networkidle', { timeout });
  }

  /**
   * 导航到指定URL
   * @param url 目标URL
   */
  async navigateTo(url: string): Promise<void> {
    await this.page.goto(url);
    await this.waitForPageLoad();
  }

  // ============================================================================
  // 抽象方法：测试数据清理接口
  // 子类必须实现这些方法
  // ============================================================================

  /**
   * 清理单个测试数据
   * @param dataName 数据名称
   */
  abstract cleanupTestData(dataName: string): Promise<void>;

  /**
   * 批量清理测试数据
   * @param dataNames 数据名称数组
   */
  abstract cleanupTestEntities(dataNames: string[]): Promise<void>;

  /**
   * 按后缀清理测试数据（兜底机制）
   * @param suffix 后缀字符串
   */
  abstract cleanupTestEntitiesBySuffix(suffix: string): Promise<void>;
}