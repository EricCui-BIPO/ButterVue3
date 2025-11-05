import { Page } from 'playwright';
import { GenericCrudPage } from '../../shared/page-objects/GenericCrudPage';
import { getCrudPageConfig } from '../../shared/models/CrudPageConfig';

/**
 * 实体管理页面对象类
 * 继承 GenericCrudPage，只保留实体特有的逻辑
 * 遵循 Page Object Model (PO) 模式
 */
export class EntityPage extends GenericCrudPage {

  constructor(page: Page) {
    super(page, getCrudPageConfig('entity'));
  }

  /**
   * 导航到实体管理页面
   * 包装方法，保持向后兼容
   */
  async navigateToEntityPage(): Promise<void> {
    await this.navigateToPage();
  }

  /**
   * 按名称搜索实体
   * 包装方法，保持向后兼容
   */
  async searchByName(entityName: string): Promise<void> {
    await super.searchByName(entityName);
  }

  /**
   * 按实体类型筛选
   * 包装方法，保持向后兼容
   */
  async filterByEntityType(entityType: string): Promise<void> {
    await this.filterByType(entityType);
  }

  /**
   * 创建新实体
   * 包装方法，保持向后兼容
   */
  async createEntity(name: string, type: string, description?: string): Promise<void> {
    await this.create(name, type, description);
  }

  /**
   * 仅打开编辑对话框（不自动提交）
   * 包装方法，保持向后兼容
   */
  async openEditDialog(entityName: string): Promise<void> {
    await super.openEditDialog(entityName);
  }

  /**
   * 编辑实体（组合方法：打开→填充→提交）
   * 包装方法，保持向后兼容
   */
  async editEntity(entityName: string, newName?: string, newDescription?: string): Promise<void> {
    await this.edit(entityName, newName, newDescription);
  }

  /**
   * 停用实体
   * 包装方法，保持向后兼容
   */
  async deactivateEntity(entityName: string): Promise<void> {
    await this.deactivate(entityName);
  }

  /**
   * 激活实体
   * 包装方法，保持向后兼容
   */
  async activateEntity(entityName: string): Promise<void> {
    await this.activate(entityName);
  }

  /**
   * 删除实体
   * 包装方法，保持向后兼容
   */
  async deleteEntity(entityName: string): Promise<void> {
    await this.delete(entityName);
  }

  /**
   * 验证实体是否存在
   * 包装方法，保持向后兼容
   */
  async verifyEntityExists(entityName: string): Promise<void> {
    await this.verifyExists(entityName);
  }

  /**
   * 验证实体不存在
   * 包装方法，保持向后兼容
   */
  async verifyEntityNotExists(entityName: string): Promise<void> {
    await this.verifyNotExists(entityName);
  }

  /**
   * 验证实体状态
   * 包装方法，保持向后兼容
   */
  async verifyEntityStatus(entityName: string, status: string): Promise<void> {
    await this.verifyStatus(entityName, status);
  }

  /**
   * 验证搜索结果包含指定实体
   * 包装方法，保持向后兼容
   */
  async verifySearchResultContains(entityName: string): Promise<void> {
    await super.verifySearchResultContains(entityName);
  }

  /**
   * 验证搜索结果中所有项目都是指定类型
   * 包装方法，保持向后兼容
   */
  async verifyAllResultsAreOfType(entityType: string): Promise<void> {
    await super.verifyAllResultsAreOfType(entityType);
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
   * 验证更新后的实体描述
   * 包装方法，保持向后兼容
   */
  async verifyUpdatedEntityDescription(entityName: string, expectedDescription: string): Promise<void> {
    await this.verifyUpdatedDescription(entityName, expectedDescription);
  }

  /**
   * 创建测试数据 - 激活状态的实体
   * 包装方法，保持向后兼容
   */
  async createActiveEntityForTesting(entityName: string): Promise<void> {
    await this.createActiveDataForTesting(entityName);
  }

  /**
   * 尝试创建空实体（用于测试必填字段校验）
   * 包装方法，保持向后兼容
   */
  async attemptCreateEmptyEntity(): Promise<void> {
    await this.attemptCreateEmpty();
  }

  /**
   * 取消创建实体
   * 包装方法，保持向后兼容
   */
  async cancelCreateEntity(): Promise<void> {
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
  // 向后兼容性属性访问器
  // ============================================================================

  /**
   * 向后兼容：对话框实体名称输入框
   */
  get dialogEntityNameInput() {
    return this.dialogNameInput;
  }

  /**
   * 向后兼容：对话框实体类型选择器
   */
  get dialogEntityTypeSelect() {
    return this.dialogTypeSelect;
  }

  /**
   * 向后兼容：实体名称输入框
   */
  get entityNameInput() {
    return this.nameInput;
  }
}
