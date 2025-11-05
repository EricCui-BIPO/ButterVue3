import { Page, Locator, expect } from '@playwright/test';
import { BasePage, CrudPageOperations } from './BasePage';
import { CrudPageConfig } from '../models/CrudPageConfig';
import { TableComponent } from '../components/TableComponent';
import { FormComponent } from '../components/FormComponent';
import { DialogComponent } from '../components/DialogComponent';
import { FilterComponent } from '../components/FilterComponent';
import { ActionDropdownComponent } from '../components/ActionDropdownComponent';
import { WaitHelpers } from '../utils/wait-helpers';
import { TIMEOUTS } from '../utils/constants';
import { buildCurrentPortalUrl } from '../../config/test-config';
import { logger } from '../utils/logger';

/**
 * 通用 CRUD 页面基类
 * 抽取所有 CRUD 页面的通用逻辑，支持多 Portal 扩展
 */
export abstract class GenericCrudPage extends BasePage implements CrudPageOperations {
  protected readonly config: CrudPageConfig;
  protected readonly table: TableComponent;
  protected readonly form: FormComponent;
  // 组件访问 - 公共属性
  readonly dialog: DialogComponent;
  protected readonly filter: FilterComponent;
  protected readonly actionDropdown: ActionDropdownComponent;

  // 页面导航相关
  readonly menuItem: Locator;

  // 搜索表单相关
  readonly searchForm: Locator;
  readonly nameInput: Locator;
  readonly typeSelect: Locator;
  readonly statusSelect: Locator;
  readonly searchButton: Locator;
  readonly resetButton: Locator;

  // 表格相关
  readonly dataTable: Locator;
  readonly tableRows: Locator;
  readonly noDataText: Locator;
  readonly emptyTable: Locator;

  // 对话框相关
  readonly createButton: Locator;
  readonly dialogNameInput: Locator;
  readonly dialogTypeSelect: Locator;
  readonly dialogDescriptionTextarea: Locator;
  readonly createDialogButton: Locator;
  readonly updateDialogButton: Locator;
  readonly cancelDialogButton: Locator;

  // 确认对话框相关
  readonly confirmDialog: Locator;
  readonly confirmButton: Locator;

  // 分页相关
  readonly paginationNext: Locator;
  readonly paginationPrev: Locator;
  readonly paginationNumbers: Locator;

  constructor(page: Page, config: CrudPageConfig) {
    super(page, config.portal);
    this.config = config;

    // 初始化组件
    this.table = new TableComponent(page);
    this.form = new FormComponent(page);
    this.dialog = new DialogComponent(page);
    this.filter = new FilterComponent(page);
    this.actionDropdown = new ActionDropdownComponent(page);

    // 页面导航
    this.menuItem = page.locator(`.el-menu-item:has-text("${this.getDisplayName()}")`);

    // 搜索表单
    this.searchForm = page.locator('.el-form').first();
    this.nameInput = this.searchForm.getByPlaceholder(this.config.pageConfig?.searchPlaceholder || `Enter ${this.config.entityName} name`);
    this.typeSelect = this.searchForm.getByRole('combobox', { name: this.config.pageConfig?.typeSelectPlaceholder || `Select ${this.config.entityName} type` });
    this.statusSelect = this.searchForm.getByRole('combobox', { name: this.config.pageConfig?.statusSelectPlaceholder || 'Select status' });
    this.searchButton = this.searchForm.getByRole('button', { name: 'Search' });
    this.resetButton = this.searchForm.getByRole('button', { name: 'Reset' });

    // 表格
    this.dataTable = page.locator('.el-table');
    this.tableRows = page.locator('tbody tr');
    this.noDataText = page.locator('.el-empty__description, .no-data, [data-testid="no-data"]');
    this.emptyTable = page.locator('.el-table__empty-block');

    // 对话框
    this.createButton = page.getByRole('button', { name: this.config.pageConfig?.createButtonText || `Create ${this.getDisplayName()}` });
    this.dialogNameInput = this.dialog.getDialog().getByRole('textbox').first();
    this.dialogTypeSelect = this.dialog.getDialog().getByRole('combobox').first();
    this.dialogDescriptionTextarea = this.dialog.getDialog().getByRole('textbox').last();
    this.createDialogButton = this.dialog.getDialog().getByRole('button', { name: 'Create', exact: true });
    this.updateDialogButton = this.dialog.getDialog().getByRole('button', { name: 'Update' });
    this.cancelDialogButton = this.dialog.getDialog().getByRole('button', { name: 'Cancel' });

    // 确认对话框
    this.confirmDialog = page.locator('.el-message-box');
    this.confirmButton = page.getByRole('button', { name: 'Confirm' });

    // 分页
    this.paginationNext = page.locator('.el-pagination .btn-next');
    this.paginationPrev = page.locator('.el-pagination .btn-prev');
    this.paginationNumbers = page.locator('.el-pagination .number');
  }

  /**
   * 获取显示名称
   */
  protected getDisplayName(): string {
    return this.config.entityName.split('-').map(word => 
      word.charAt(0).toUpperCase() + word.slice(1)
    ).join(' ');
  }

  /**
   * 导航到页面
   */
  async navigateToPage(): Promise<void> {
    await this.page.goto(buildCurrentPortalUrl(this.config.route));
    await WaitHelpers.waitForPageLoad(this.page);
  }

  /**
   * 等待表格数据加载
   */
  async waitForTableData(timeout: number = TIMEOUTS.tableLoad): Promise<void> {
    await WaitHelpers.waitForTableLoad(this.page, timeout);
  }

  /**
   * 按名称搜索
   */
  async searchByName(name: string): Promise<void> {
    await this.nameInput.fill(name);
    await this.searchButton.click();
    await WaitHelpers.waitForNetworkIdle(this.page);
  }

  /**
   * 按类型筛选
   */
  async filterByType(type: string): Promise<void> {
    await this.filter.filterByType(type, this.config.pageConfig?.typeSelectPlaceholder || `Select ${this.config.entityName} type`);
  }

  /**
   * 按状态筛选
   */
  async filterByStatus(status: string): Promise<void> {
    await this.filter.filterByStatus(status);
  }

  /**
   * 重置搜索条件
   */
  async resetSearch(): Promise<void> {
    await this.filter.resetFilters();
  }

  /**
   * 打开创建对话框
   */
  async openCreateDialog(): Promise<void> {
    await this.createButton.click();
    await this.dialog.waitForOpen();
    await expect(this.dialog.getDialog().getByText(this.config.pageConfig?.createButtonText || `Create ${this.getDisplayName()}`)).toBeVisible();
  }

  /**
   * 创建新数据
   */
  async create(name: string, type: string, description?: string): Promise<void> {
    await this.openCreateDialog();
    
    // 填写名称
    await this.dialog.fillFirstInput(name);
    
    // 选择类型
    await this.dialog.selectOption(type);
    
    // 填写描述（可选）
    if (description) {
      await this.dialog.fillLastInput(description);
    }
    
    // 点击创建按钮
    await this.dialog.clickCreateButton();
    
    // 等待表格更新
    await WaitHelpers.waitForTableDataUpdate(this.page);
  }

  /**
   * 仅打开编辑对话框（不自动提交）
   * 用于需要手动填充字段并提交的测试场景
   */
  async openEditDialog(dataName: string): Promise<void> {
    // 打开操作下拉菜单并点击 Edit
    await this.actionDropdown.editRow(dataName);

    // 验证编辑对话框打开
    await expect(this.dialog.getDialog().getByText(this.config.pageConfig?.editButtonText || `Edit ${this.getDisplayName()}`)).toBeVisible();
  }

  /**
   * 编辑数据（组合方法：打开→填充→提交）
   * 用于一次性完成完整编辑流程的场景
   */
  async edit(dataName: string, newName?: string, newDescription?: string): Promise<void> {
    // 打开操作下拉菜单并点击 Edit
    await this.actionDropdown.editRow(dataName);

    // 验证编辑对话框打开
    await expect(this.dialog.getDialog().getByText(this.config.pageConfig?.editButtonText || `Edit ${this.getDisplayName()}`)).toBeVisible();
    
    // 修改名称
    if (newName) {
      await this.dialog.clearFirstInput();
      await this.dialog.fillFirstInput(newName);
    }
    
    // 修改描述
    if (newDescription) {
      await this.dialog.clearLastInput();
      await this.dialog.fillLastInput(newDescription);
    }
    
    // 点击更新按钮
    await this.dialog.clickUpdateButton();
  }

  /**
   * 等待状态变更
   */
  private async waitForStatusChange(dataName: string, expectedStatus: string, timeout: number = TIMEOUTS.medium): Promise<void> {
    const row = this.page.locator('.el-table').locator('tr').filter({ hasText: dataName }).first();
    await expect(row.locator(`text=${expectedStatus}`)).toBeVisible({ timeout });
  }

  /**
   * 停用数据
   */
  async deactivate(dataName: string): Promise<void> {
    await this.actionDropdown.deactivateRow(dataName);
    
    // 只等待操作完成，不验证状态变更
    await WaitHelpers.wait(TIMEOUTS.short);
  }

  /**
   * 激活数据
   */
  async activate(dataName: string): Promise<void> {
    await this.actionDropdown.activateRow(dataName);
    
    // 只等待操作完成，不验证状态变更
    await WaitHelpers.wait(TIMEOUTS.short);
  }

  /**
   * 删除数据（只打开确认对话框）
   */
  async delete(dataName: string): Promise<void> {
    await this.actionDropdown.deleteRow(dataName);
  }

  /**
   * 删除数据并确认
   */
  async deleteWithConfirmation(dataName: string): Promise<void> {
    await this.actionDropdown.deleteRowWithConfirmation(dataName);
  }

  /**
   * 取消创建
   */
  async cancelCreate(): Promise<void> {
    await this.dialog.clickCancelButton();
    await this.dialog.waitForClose();
  }

  /**
   * 分页操作
   */
  async goToNextPage(): Promise<void> {
    if (await this.paginationNext.isVisible()) {
      await this.paginationNext.click();
    }
  }

  async goToPreviousPage(): Promise<void> {
    if (await this.paginationPrev.isVisible()) {
      await this.paginationPrev.click();
    }
  }

  /**
   * 验证方法
   */
  async verifyExists(dataName: string): Promise<void> {
    await this.table.verifyRowExists(dataName);
  }

  async verifyNotExists(dataName: string): Promise<void> {
    await this.table.verifyRowNotExists(dataName);
  }

  async verifyStatus(dataName: string, status: string): Promise<void> {
    // 等待页面更新
    await WaitHelpers.waitForNetworkIdle(this.page);
    
    // 搜索数据以确保在表格中
    await this.nameInput.clear();
    await this.nameInput.fill(dataName);
    await this.searchButton.click();
    
    // 等待表格行出现
    await this.page.locator('tbody tr').first().waitFor({ state: 'visible', timeout: 5000 });
    
    // 查找包含数据名称的行
    const row = this.page.locator('tr').filter({ hasText: dataName }).first();
    await expect(row).toBeVisible();
    
    // 尝试多种状态文本变体
    const statusVariants = [
      status,
      status.toLowerCase(),
      status.toUpperCase()
    ];
    
    // 尝试多种方式查找状态
    const statusSelectors = [
      `.el-tag:has-text("${status}")`,
      `text=${status}`,
      `.status-tag:has-text("${status}")`,
      `.badge:has-text("${status}")`,
      `.el-tag`,
      `.badge`,
      `.status`
    ];
    
    let statusFound = false;
    for (const selector of statusSelectors) {
      try {
        const statusElement = row.locator(selector).first();
        const isVisible = await statusElement.isVisible().catch(() => false);
        
        if (isVisible) {
          const elementText = await statusElement.textContent().catch(() => '');
          
          // 检查是否匹配任何状态变体
          for (const variant of statusVariants) {
            if (elementText && elementText.includes(variant)) {
              await expect(statusElement).toBeVisible();
              statusFound = true;
              break;
            }
          }
          
          if (statusFound) break;
        }
      } catch (error) {
        // 继续尝试下一个选择器
      }
    }
    
    if (!statusFound) {
      // 如果特定选择器失败，尝试在整个行中查找状态文本
      const rowText = await row.textContent();
      
      for (const variant of statusVariants) {
        if (rowText && rowText.includes(variant)) {
          statusFound = true;
          break;
        }
      }
      
      if (!statusFound) {
        // 尝试查找所有可能的标签
        const allTags = row.locator('.el-tag, .badge, .status, [class*="tag"], [class*="badge"]');
        const tagCount = await allTags.count();
        
        for (let i = 0; i < tagCount; i++) {
          const tag = allTags.nth(i);
          const tagText = await tag.textContent().catch(() => '');
          
          for (const variant of statusVariants) {
            if (tagText && tagText.includes(variant)) {
              await expect(tag).toBeVisible();
              statusFound = true;
              break;
            }
          }
          
          if (statusFound) break;
        }
      }
    }
    
    if (!statusFound) {
      throw new Error(`无法找到状态 "${status}" for ${dataName}`);
    }
  }

  async verifySearchResultContains(dataName: string): Promise<void> {
    await this.table.verifyRowExists(dataName);
  }

  async verifyAllResultsAreOfType(type: string): Promise<void> {
    const columnIndex = this.config.columnIndexes?.typeColumn ?? 1;
    await this.table.verifyAllRowsHaveTag(columnIndex, type);
  }

  async verifyAllResultsAreOfStatus(status: string): Promise<void> {
    const columnIndex = this.config.columnIndexes?.statusColumn ?? 2;
    await this.table.verifyAllRowsHaveTag(columnIndex, status);
  }

  async verifySearchFormReset(): Promise<void> {
    await this.filter.verifyFiltersReset();
  }

  async verifyNoDataMessage(message: string): Promise<void> {
    await expect(this.page.getByText(message)).toBeVisible();
  }

  async verifyNoDataState(): Promise<void> {
    await this.table.verifyEmptyState();
  }

  async verifyFormValidationError(errorMessage: string): Promise<void> {
    await this.dialog.verifyValidationError(errorMessage);
  }

  async verifyPageChanged(): Promise<void> {
    await expect(this.paginationNumbers.locator('.is-active')).not.toHaveText('1');
  }

  async verifyDialogClosed(): Promise<void> {
    await this.dialog.waitForClose();
  }

  /**
   * 验证更新后的描述
   * 改进版本：使用重试机制和更可靠的验证逻辑
   */
  async verifyUpdatedDescription(dataName: string, expectedDescription: string): Promise<void> {
    const maxRetries = 3;
    let lastError: Error | null = null;
    
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        // 第一次重试时记录，避免冗余输出
        if (attempt === 1) {
          logger.debug(`🔄 验证更新描述: ${dataName}`);
        }
        
        // 等待表格刷新
        await WaitHelpers.waitForNetworkIdle(this.page);
        await WaitHelpers.wait(TIMEOUTS.short);
        
        // 搜索数据
        await this.nameInput.clear();
        await this.nameInput.fill(dataName);
        await this.searchButton.click();
        await WaitHelpers.wait(TIMEOUTS.medium);
        
        // 检查搜索结果
        const rows = this.page.locator('tbody tr');
        const rowCount = await rows.count();
        
        if (rowCount === 0) {
          throw new Error(`${this.getDisplayName()} ${dataName} 不存在，可能更新失败`);
        }
        
        // 找到包含数据名称的行
        const dataRow = rows.filter({ hasText: dataName }).first();
        await expect(dataRow).toBeVisible();
        
        // 获取行内容并验证
        const rowText = await dataRow.textContent();
        
        // 尝试在描述列中查找（通常是第4列，索引3）
        const cells = dataRow.locator('td');
        const cellCount = await cells.count();
        
        if (cellCount > 3) {
          const descriptionCell = cells.nth(3);
          const descriptionText = await descriptionCell.textContent();
          
          if (descriptionText && descriptionText.includes(expectedDescription)) {
            logger.info(`✅ 验证更新描述成功: ${dataName}`);
            return; // 验证成功
          }
        }
        
        // 如果特定列没找到，尝试在整个行中查找
        if (rowText && rowText.includes(expectedDescription)) {
          logger.info(`✅ 验证更新描述成功: ${dataName}`);
          return; // 验证成功
        }
        
        // 如果还没到最大重试次数，等待后重试
        if (attempt < maxRetries) {
          await WaitHelpers.wait(TIMEOUTS.medium);
          continue;
        }
        
        // 最后一次尝试失败，抛出断言错误
        const errorMessage = 
          `❌ 描述验证失败！\n` +
          `   ${this.getDisplayName()}: ${dataName}\n` +
          `   期望描述: "${expectedDescription}"\n` +
          `   实际行内容: "${rowText}"`;
        logger.error(errorMessage);
        throw new Error(errorMessage);
        
      } catch (error) {
        lastError = error as Error;
        logger.warn(`⚠️ 验证更新描述失败 (尝试 ${attempt}/${maxRetries}): ${String(error)}`);
        
        if (attempt < maxRetries) {
          await WaitHelpers.wait(TIMEOUTS.medium);
        }
      }
    }
    
    // 如果所有重试都失败了，抛出最后的错误
    logger.error(`❌ 验证更新描述最终失败: ${dataName}`);
    throw lastError || new Error(`验证更新描述失败: ${dataName}`);
  }

  /**
   * 创建测试数据
   */
  async createActiveDataForTesting(dataName: string): Promise<void> {
    const defaultType = this.config.entityName === 'entity' ? 'Client' : 'EOR';
    await this.create(dataName, defaultType);
    await this.verifyExists(dataName);
  }

  /**
   * 尝试创建空数据（用于测试必填字段校验）
   */
  async attemptCreateEmpty(): Promise<void> {
    await this.createDialogButton.click();
    await WaitHelpers.wait(TIMEOUTS.short);
  }

  /**
   * 清理单个测试数据
   * 改进版本：更可靠的清理流程
   */
  async cleanupTestData(dataName: string): Promise<void> {
    const maxRetries = 2;
    let lastError: Error | null = null;
    
    for (let attempt = 1; attempt <= maxRetries; attempt++) {
      try {
        // 第一次清理时记录，避免冗余输出
        if (attempt === 1) {
          logger.debug(`🧹 清理${this.getDisplayName()}测试数据: ${dataName}`);
        }
        
        // 关闭可能打开的对话框
        await this.closeAnyOpenDialogs();
        
        // 等待页面稳定
        await WaitHelpers.waitForNetworkIdle(this.page);
        await WaitHelpers.wait(TIMEOUTS.short);
        
        // 搜索数据
        await this.nameInput.fill(dataName);
        await this.searchButton.click();
        await WaitHelpers.wait(TIMEOUTS.short);
        
        // 检查是否存在
        const row = this.page.locator('tbody tr').filter({ hasText: dataName }).first();
        const exists = await row.isVisible({ timeout: TIMEOUTS.medium }).catch(() => false);
        
        if (!exists) {
          // 不存在，无需清理
          return;
        }
        
        // 删除数据
        await this.actionDropdown.deleteRowWithConfirmation(dataName);
        
        // 验证删除成功
        await WaitHelpers.waitForNetworkIdle(this.page);
        await WaitHelpers.wait(TIMEOUTS.short);
        
        const stillExists = await row.isVisible({ timeout: TIMEOUTS.short }).catch(() => false);
        if (!stillExists) {
          logger.info(`✅ 已清理${this.getDisplayName()}测试数据: ${dataName}`);
          return;
        }
        
        // 如果还存在，可能是删除失败
        if (attempt < maxRetries) {
          logger.warn(`⚠️ 删除可能失败，重试: ${dataName}`);
          await WaitHelpers.wait(TIMEOUTS.medium);
          continue;
        }

        logger.warn(`⚠️ 清理${this.getDisplayName()}测试数据失败: ${dataName}`);
        
      } catch (error) {
        lastError = error as Error;
        logger.warn(`⚠️ 清理${this.getDisplayName()}测试数据失败 (尝试 ${attempt}/${maxRetries}): ${String(error)}`);
        
        if (attempt < maxRetries) {
          await WaitHelpers.wait(TIMEOUTS.medium);
        }
      }
    }
    
    // 记录最终失败
    if (lastError) {
      logger.error(`❌ 清理${this.getDisplayName()}测试数据最终失败: ${dataName}`, lastError);
    }
  }

  /**
   * 批量清理测试数据
   */
  async cleanupTestEntities(dataNames: string[]): Promise<void> {
    for (const dataName of dataNames) {
      await this.cleanupTestData(dataName);
    }
  }

  /**
   * 按后缀清理测试数据
   */
  async cleanupTestEntitiesBySuffix(suffix: string): Promise<void> {
    try {
      logger.debug(`🧹 清理带后缀的${this.getDisplayName()}测试数据: *_${suffix}`);
      
      // 关闭可能打开的对话框
      await this.closeAnyOpenDialogs();
      
      // 等待页面稳定
      await WaitHelpers.waitForNetworkIdle(this.page);
      await WaitHelpers.wait(TIMEOUTS.short);
      
      // 重置搜索并使用后缀搜索
      await this.nameInput.fill(`_${suffix}`);
      await this.searchButton.click();
      await WaitHelpers.wait(TIMEOUTS.short);
      
      // 获取所有匹配的行
      const rows = this.page.locator('tbody tr');
      const rowCount = await rows.count().catch(() => 0);
      
      if (rowCount === 0) {
        return; // 没有找到需要清理的数据
      }
      
      // 收集所有需要清理的数据名称
      const dataToClean: string[] = [];
      for (let i = 0; i < rowCount; i++) {
        const row = rows.nth(i);
        const nameCell = row.locator('td').first();
        const dataName = await nameCell.textContent().catch(() => null);
        
        if (dataName && dataName.includes(`_${suffix}`)) {
          dataToClean.push(dataName.trim());
        }
      }
      
      // 逐个清理
      for (const dataName of dataToClean) {
        await this.cleanupTestData(dataName);
      }
      
      logger.info(`✅ 完成后缀为 ${suffix} 的${this.getDisplayName()}测试数据清理`);
    } catch (error) {
      logger.warn(`⚠️ 批量清理${this.getDisplayName()}测试数据失败 [suffix: ${suffix}]:`, error);
    }
  }

  /**
   * 关闭所有打开的对话框
   * 改进版本：更可靠的对话框关闭逻辑
   */
  private async closeAnyOpenDialogs(): Promise<void> {
    // 首先尝试关闭确认对话框
    const confirmDialogs = this.page.locator('.el-message-box, .el-overlay-message-box');
    const confirmCount = await confirmDialogs.count();
    
    for (let i = 0; i < confirmCount; i++) {
      const dialog = confirmDialogs.nth(i);
      if (await dialog.isVisible().catch(() => false)) {
        // 尝试点击取消按钮
        const cancelBtn = dialog.locator('button').filter({ hasText: /Cancel|取消|取消删除/ });
        if (await cancelBtn.isVisible().catch(() => false)) {
          await cancelBtn.click({ force: true }).catch(() => {});
          await WaitHelpers.wait(TIMEOUTS.short);
        } else {
          // 如果没有取消按钮，按Escape键
          await this.page.keyboard.press('Escape');
          await WaitHelpers.wait(TIMEOUTS.short);
        }
      }
    }
    
    // 然后尝试关闭普通对话框
    const dialogs = this.page.locator('.el-dialog__wrapper:visible');
    const count = await dialogs.count();
    
    for (let i = 0; i < count; i++) {
      const dialog = dialogs.nth(i);
      if (await dialog.isVisible().catch(() => false)) {
        // 尝试点击关闭按钮
        const closeBtn = dialog.locator('.el-dialog__close');
        if (await closeBtn.isVisible().catch(() => false)) {
          await closeBtn.click({ force: true }).catch(() => {});
          await WaitHelpers.wait(TIMEOUTS.short);
        } else {
          // 如果没有关闭按钮，按Escape键
          await this.page.keyboard.press('Escape');
          await WaitHelpers.wait(TIMEOUTS.short);
        }
      }
    }
    
    // 最后确保所有对话框都关闭
    await WaitHelpers.wait(TIMEOUTS.short);
  }

  // ============================================================================
  // CrudPageOperations 接口实现
  // ============================================================================

  getSearchInput(): Locator {
    return this.nameInput;
  }

  async clickCreateButton(): Promise<void> {
    await this.createDialogButton.click();
    await WaitHelpers.wait(TIMEOUTS.short);
    await this.waitForTableData();
  }

  async clickUpdateButton(): Promise<void> {
    await this.updateDialogButton.click();
    
    // 等待成功或错误消息出现
    await Promise.race([
      this.page.locator('.el-message--success').waitFor({ state: 'visible', timeout: 3000 }),
      this.page.locator('.el-message--error').waitFor({ state: 'visible', timeout: 3000 })
    ]).catch(() => {});
    
    // 检查是否有错误消息
    const errorMessage = this.page.locator('.el-message--error');
    const isErrorVisible = await errorMessage.isVisible().catch(() => false);
    if (isErrorVisible) {
      const errorText = await errorMessage.textContent();
      throw new Error(`更新失败: ${errorText}`);
    }
    
    // 等待对话框关闭
    await this.dialog.waitForClose();
    
    // 等待网络请求完成
    await WaitHelpers.waitForNetworkIdle(this.page);
  }

  async clickSearchButton(): Promise<void> {
    await this.searchButton.click();
  }

  /**
   * 验证页面级错误消息（ElMessage、ElNotification 等）
   * 用于验证 API 错误、业务逻辑错误等后端返回的错误
   * 
   * @param expectedKeywords - 期望的错误关键词（支持中英文）
   * @param timeout - 超时时间（默认 5000ms）
   */
  async verifyPageLevelError(
    expectedKeywords: string[], 
    timeout: number = 5000
  ): Promise<void> {
    logger.debug('开始验证页面级错误消息...');
    
    // 主要错误选择器（优化顺序，最常见的放在前面）
    const errorSelectors = [
      '.el-message--error',           // ElMessage.error() 的主要选择器
      '.el-message',                  // 通用 ElMessage
      '.el-notification__content',    // ElNotification
      '.el-message-box__message'      // ElMessageBox
    ];
    
    // 尝试每个选择器，使用 waitFor 等待元素出现
    for (const selector of errorSelectors) {
      try {
        const errorElement = this.page.locator(selector).first();
        
        // 等待元素出现（关键改进：不再固定等待，而是等待元素出现）
        await errorElement.waitFor({ state: 'visible', timeout });
        
        const errorText = await errorElement.textContent();
        
        if (errorText && expectedKeywords.some(keyword => 
          errorText.toLowerCase().includes(keyword.toLowerCase())
        )) {
          logger.info(`✅ 找到页面级错误: "${errorText}"`);
          return; // 成功找到，直接返回
        }
        
        // 如果元素存在但不包含关键词，继续尝试下一个选择器
        logger.debug(`元素 ${selector} 存在但不包含预期关键词: "${errorText}"`);
      } catch (error) {
        // 元素未出现或超时，继续尝试下一个选择器
        logger.debug(`选择器 ${selector} 未找到元素，尝试下一个`);
        continue;
      }
    }
    
    // 所有选择器都失败，输出调试信息
    const allMessages = await this.page.locator('.el-message, [class*="message"]').allTextContents();
    logger.warn('❌ 未找到预期的错误提示');
    logger.debug('页面中所有消息元素:', allMessages);
    
    throw new Error(`未找到包含关键词 [${expectedKeywords.join(', ')}] 的错误提示`);
  }
}
