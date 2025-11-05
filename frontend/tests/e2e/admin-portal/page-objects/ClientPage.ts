import { Page, Locator } from 'playwright';
import { expect } from '@playwright/test';
import { GenericCrudPage } from '../../shared/page-objects/GenericCrudPage';
import { getCrudPageConfig } from '../../shared/models/CrudPageConfig';
import { WaitHelpers } from '../../shared/utils/wait-helpers';
import { TIMEOUTS } from '../../shared/utils/constants';

/**
 * 客户管理页面对象类
 * 继承 GenericCrudPage，保留客户特有的逻辑
 * 遵循 Page Object Model (PO) 模式
 */
export class ClientPage extends GenericCrudPage {
  readonly dialogCodeInput: Locator;
  readonly dialogLocationSelect: Locator;
  readonly locationSelect: Locator;

  constructor(page: Page) {
    super(page, getCrudPageConfig('client'));
    
    // 客户代码输入框（对话框中）
    this.dialogCodeInput = this.dialog.getDialog().getByPlaceholder('Enter client code');
    
    // 位置选择器（对话框中）
    this.dialogLocationSelect = this.dialog.getDialog().getByRole('combobox', { name: 'Select location' });
    
    // 位置筛选器（搜索表单中）
    this.locationSelect = this.searchForm.getByRole('combobox', { name: 'Select location' });
  }

  /**
   * Client 特有逻辑：创建客户（包含 code 和 location）
   */
  async createClient(name: string, code: string, locationName: string, description?: string): Promise<void> {
    await this.openCreateDialog();
    await this.dialogNameInput.fill(name);
    await this.dialogCodeInput.fill(code);
    await this.dialog.selectOption(locationName);
    if (description) {
      await this.dialogDescriptionTextarea.fill(description);
    }
    await this.dialog.clickCreateButton();
    await WaitHelpers.waitForTableDataUpdate(this.page);
  }

  /**
   * Client 特有逻辑：按位置筛选
   */
  async filterByLocation(locationName: string): Promise<void> {
    await this.filter.filterByType(locationName, 'Select location');
  }

  /**
   * Client 特有逻辑：验证所有结果都位于指定位置
   */
  async verifyAllResultsInLocation(locationName: string): Promise<void> {
    const locationColumnIndex = 2;
    await this.table.verifyAllRowsContainText(locationColumnIndex, locationName);
  }

  // ============================================================================
  // 包装方法，保持向后兼容
  // ============================================================================

  /**
   * 导航到客户管理页面
   * 包装方法，保持向后兼容
   */
  async navigateToClientPage(): Promise<void> {
    await this.navigateToPage();
  }

  /**
   * 按名称搜索客户
   * 包装方法，保持向后兼容
   */
  async searchByName(clientName: string): Promise<void> {
    await super.searchByName(clientName);
  }

  /**
   * 仅打开编辑对话框（不自动提交）
   * 包装方法，保持向后兼容
   */
  async openEditDialog(clientName: string): Promise<void> {
    await super.openEditDialog(clientName);
  }

  /**
   * 编辑客户（组合方法：打开→填充→提交）
   * 包装方法，保持向后兼容
   */
  async editClient(clientName: string, newName?: string, newDescription?: string): Promise<void> {
    await this.edit(clientName, newName, newDescription);
  }

  /**
   * 停用客户
   * 包装方法，保持向后兼容
   */
  async deactivateClient(clientName: string): Promise<void> {
    await this.deactivate(clientName);
  }

  /**
   * 激活客户
   * 包装方法，保持向后兼容
   */
  async activateClient(clientName: string): Promise<void> {
    await this.activate(clientName);
  }

  /**
   * 删除客户
   * 包装方法，保持向后兼容
   */
  async deleteClient(clientName: string): Promise<void> {
    await this.delete(clientName);
  }

  /**
   * 验证客户是否存在
   * 包装方法，保持向后兼容
   */
  async verifyClientExists(clientName: string): Promise<void> {
    await this.verifyExists(clientName);
  }

  /**
   * 验证客户不存在
   * 包装方法，保持向后兼容
   */
  async verifyClientNotExists(clientName: string): Promise<void> {
    await this.verifyNotExists(clientName);
  }

  /**
   * 验证客户状态
   * 包装方法，保持向后兼容
   */
  async verifyClientStatus(clientName: string, status: string): Promise<void> {
    await this.verifyStatus(clientName, status);
  }

  /**
   * 验证搜索结果包含指定客户
   * 包装方法，保持向后兼容
   */
  async verifySearchResultContains(clientName: string): Promise<void> {
    await super.verifySearchResultContains(clientName);
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
   * 验证更新后的客户描述
   * 包装方法，保持向后兼容
   */
  async verifyUpdatedClientDescription(clientName: string, expectedDescription: string): Promise<void> {
    await this.verifyUpdatedDescription(clientName, expectedDescription);
  }

  /**
   * 创建测试数据 - 激活状态的客户
   * 包装方法，保持向后兼容
   */
  async createActiveClientForTesting(clientName: string): Promise<void> {
    await this.createActiveDataForTesting(clientName);
  }

  /**
   * 尝试创建空的客户（用于验证测试）
   * 包装方法，保持向后兼容
   */
  async attemptCreateEmptyClient(): Promise<void> {
    await this.attemptCreateEmpty();
  }

  /**
   * 取消创建客户
   * 包装方法，保持向后兼容
   */
  async cancelCreateClient(): Promise<void> {
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
   * 向后兼容：dialogClientNameInput
   */
  get dialogClientNameInput() {
    return this.dialogNameInput;
  }

  /**
   * 向后兼容：dialogClientCodeInput
   */
  get dialogClientCodeInput() {
    return this.dialogCodeInput;
  }

  /**
   * 向后兼容：clientNameInput
   */
  get clientNameInput() {
    return this.nameInput;
  }
}
