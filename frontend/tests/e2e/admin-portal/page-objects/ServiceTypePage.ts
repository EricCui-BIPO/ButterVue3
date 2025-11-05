import { Page } from 'playwright';
import { expect } from '@playwright/test';
import { GenericCrudPage } from '../../shared/page-objects/GenericCrudPage';
import { getCrudPageConfig } from '../../shared/models/CrudPageConfig';
import { WaitHelpers } from '../../shared/utils/wait-helpers';
import { TIMEOUTS } from '../../shared/utils/constants';
import { logger } from '../../shared/utils/logger';

/**
 * 服务类型管理页面对象类
 * 继承 GenericCrudPage，保留服务类型特有的逻辑
 * 遵循 Page Object Model (PO) 模式
 */
export class ServiceTypePage extends GenericCrudPage {

  constructor(page: Page) {
    super(page, getCrudPageConfig('serviceType'));
  }

  /**
   * Service Type 特有逻辑：验证重复类型错误
   * Service Type 有唯一性约束：每种类型只能存在一条记录
   */
  async verifyDuplicateTypeError(): Promise<void> {
    await this.verifyPageLevelError([
      'duplicate',
      'already exists',
      'exist',
      '唯一',
      '重复',
      'constraint',
      'violation'
    ]);
  }

  // ============================================================================
  // 包装方法，保持向后兼容
  // ============================================================================

  /**
   * 导航到服务类型管理页面
   * 包装方法，保持向后兼容
   */
  async navigateToServiceTypePage(): Promise<void> {
    await this.navigateToPage();
  }

  /**
   * 按名称搜索服务类型
   * 包装方法，保持向后兼容
   */
  async searchByName(serviceTypeName: string): Promise<void> {
    await super.searchByName(serviceTypeName);
  }

  /**
   * 按服务类型筛选
   * 包装方法，保持向后兼容
   */
  async filterByServiceType(serviceType: string): Promise<void> {
    await this.filterByType(serviceType);
  }

  /**
   * 创建新服务类型
   * 包装方法，保持向后兼容
   */
  async createServiceType(name: string, type: string, description?: string): Promise<void> {
    await this.create(name, type, description);
  }

  /**
   * 仅打开编辑对话框（不自动提交）
   * 包装方法，保持向后兼容
   */
  async openEditDialog(serviceName: string): Promise<void> {
    await super.openEditDialog(serviceName);
  }

  /**
   * 编辑服务类型（组合方法：打开→填充→提交）
   * 包装方法，保持向后兼容
   */
  async editServiceType(serviceName: string, newName?: string, newDescription?: string): Promise<void> {
    await this.edit(serviceName, newName, newDescription);
  }

  /**
   * 停用服务类型
   * 包装方法，保持向后兼容
   */
  async deactivateServiceType(serviceName: string): Promise<void> {
    await this.deactivate(serviceName);
  }

  /**
   * 激活服务类型
   * 包装方法，保持向后兼容
   */
  async activateServiceType(serviceName: string): Promise<void> {
    await this.activate(serviceName);
  }

  /**
   * 删除服务类型
   * 包装方法，保持向后兼容
   */
  async deleteServiceType(serviceName: string): Promise<void> {
    await this.delete(serviceName);
  }

  /**
   * 验证服务类型是否存在
   * 包装方法，保持向后兼容
   */
  async verifyServiceTypeExists(serviceTypeName: string): Promise<void> {
    await this.verifyExists(serviceTypeName);
  }

  /**
   * 验证服务类型不存在
   * 包装方法，保持向后兼容
   */
  async verifyServiceTypeNotExists(serviceTypeName: string): Promise<void> {
    await this.verifyNotExists(serviceTypeName);
  }

  /**
   * 验证服务类型状态
   * 包装方法，保持向后兼容
   */
  async verifyServiceTypeStatus(serviceName: string, status: string): Promise<void> {
    await this.verifyStatus(serviceName, status);
  }

  /**
   * 验证搜索结果包含指定服务类型
   * 包装方法，保持向后兼容
   */
  async verifySearchResultContains(serviceTypeName: string): Promise<void> {
    await super.verifySearchResultContains(serviceTypeName);
  }

  /**
   * 验证搜索结果中所有项目都是指定类型
   * 包装方法，保持向后兼容
   */
  async verifyAllResultsAreOfType(serviceType: string): Promise<void> {
    await super.verifyAllResultsAreOfType(serviceType);
  }

  /**
   * 验证搜索结果中所有项目都是指定状态
   * 包装方法，保持向后兼容
   */
  async verifyAllResultsAreOfStatus(status: string): Promise<void> {
    await super.verifyAllResultsAreOfStatus(status);
  }

  /**
   * 验证搜索表单已重置
   * 包装方法，保持向后兼容
   */
  async verifySearchFormReset(): Promise<void> {
    await super.verifySearchFormReset();
  }

  /**
   * 验证无数据提示
   * 包装方法，保持向后兼容
   */
  async verifyNoDataMessage(message: string): Promise<void> {
    await super.verifyNoDataMessage(message);
  }

  /**
   * 验证页面进入无数据状态
   * 包装方法，保持向后兼容
   */
  async verifyNoDataState(): Promise<void> {
    await super.verifyNoDataState();
  }

  /**
   * 验证表单验证错误信息
   * 包装方法，保持向后兼容
   */
  async verifyFormValidationError(errorMessage: string): Promise<void> {
    await super.verifyFormValidationError(errorMessage);
  }

  /**
   * 验证页面切换
   * 包装方法，保持向后兼容
   */
  async verifyPageChanged(): Promise<void> {
    await super.verifyPageChanged();
  }

  /**
   * 验证对话框已关闭
   * 包装方法，保持向后兼容
   */
  async verifyDialogClosed(): Promise<void> {
    await super.verifyDialogClosed();
  }

  /**
   * 验证更新后的服务类型描述
   * 包装方法，保持向后兼容
   */
  async verifyUpdatedServiceTypeDescription(serviceName: string, expectedDescription: string): Promise<void> {
    await this.verifyUpdatedDescription(serviceName, expectedDescription);
  }

  /**
   * 创建测试数据 - 激活状态的服务类型
   * 包装方法，保持向后兼容
   */
  async createActiveServiceTypeForTesting(serviceName: string): Promise<void> {
    await this.createActiveDataForTesting(serviceName);
  }

  /**
   * 尝试创建空的服务类型（用于验证测试）
   * 包装方法，保持向后兼容
   */
  async attemptCreateEmptyServiceType(): Promise<void> {
    await this.attemptCreateEmpty();
  }

  /**
   * 取消创建服务类型
   * 包装方法，保持向后兼容
   */
  async cancelCreateServiceType(): Promise<void> {
    await this.cancelCreate();
  }

  /**
   * 点击下一页
   * 包装方法，保持向后兼容
   */
  async goToNextPage(): Promise<void> {
    await super.goToNextPage();
  }

  /**
   * 点击上一页
   * 包装方法，保持向后兼容
   */
  async goToPreviousPage(): Promise<void> {
    await super.goToPreviousPage();
  }

  // ============================================================================
  // 向后兼容的getter方法
  // ============================================================================

  /**
   * 向后兼容：dialogServiceTypeNameInput
   */
  get dialogServiceTypeNameInput() {
    return this.dialogNameInput;
  }

  /**
   * 向后兼容：dialogServiceTypeSelect
   */
  get dialogServiceTypeSelect() {
    return this.dialogTypeSelect;
  }

  /**
   * 向后兼容：serviceTypeNameInput
   */
  get serviceTypeNameInput() {
    return this.nameInput;
  }
}
