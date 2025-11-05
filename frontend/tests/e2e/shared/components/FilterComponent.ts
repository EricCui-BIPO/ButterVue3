import { Page, Locator, expect } from '@playwright/test';
import { COMMON_SELECTORS } from '../utils/selectors';
import { InteractionHelpers } from '../utils/interaction-helpers';

/**
 * 通用筛选器组件
 * 封装所有筛选器相关的操作，支持 el-select
 */
export class FilterComponent {
  private readonly form: Locator;

  constructor(private page: Page, private formSelector: string = COMMON_SELECTORS.form) {
    this.form = page.locator(formSelector).first();
  }

  /**
   * 按状态筛选
   */
  async filterByStatus(status: string): Promise<void> {
    const selectDropdown = await InteractionHelpers.openSelectDropdownByText(
      this.page,
      this.page,
      'Select status'
    );

    // 等待下拉菜单选项加载完成
    await selectDropdown.locator('.el-select-dropdown__item').first().waitFor({ state: 'visible', timeout: 5000 });

    await selectDropdown.getByRole('option', { name: status, exact: true }).click();

    // 等待网络请求完成
    await this.page.waitForLoadState('networkidle');

    // 额外等待表格数据更新（保留原有等待，确保稳定性）
    await this.page.waitForTimeout(1000);
  }

  /**
   * 按类型筛选
   */
  async filterByType(typeName: string, selectPlaceholder: string): Promise<void> {
    const selectDropdown = await InteractionHelpers.openSelectDropdownByText(
      this.page,
      this.page,
      selectPlaceholder
    );

    // 等待下拉菜单选项加载完成
    await selectDropdown.locator('.el-select-dropdown__item').first().waitFor({ state: 'visible', timeout: 5000 });

    // 使用更精确的选择器
    await selectDropdown.getByRole('option', { name: typeName, exact: true }).click();

    // 等待网络请求完成
    await this.page.waitForLoadState('networkidle');

    // 额外等待表格数据更新（保留原有等待，确保稳定性）
    await this.page.waitForTimeout(1000);
  }

  /**
   * 重置筛选条件
   */
  async resetFilters(): Promise<void> {
    const resetButton = this.form.getByRole('button', { name: 'Reset' });
    await resetButton.click();
  }

  /**
   * 点击搜索按钮
   */
  async clickSearchButton(): Promise<void> {
    const searchButton = this.form.getByRole('button', { name: 'Search' });
    await searchButton.click();
  }

  /**
   * 验证筛选器是否重置
   */
  async verifyFiltersReset(): Promise<void> {
    // 验证搜索框被清空
    const searchInput = this.form.locator('input[placeholder*="Enter"]').first();
    await expect(searchInput).toHaveValue('');
    
    // 验证下拉选择器重置为默认值
    const selects = this.form.locator('.el-select');
    const selectCount = await selects.count();
    
    for (let i = 0; i < selectCount; i++) {
      const select = selects.nth(i);
      const text = await select.textContent();
      // 检查是否包含默认占位符文本
      expect(text).toMatch(/Select \w+/);
    }
  }

  /**
   * 获取筛选表单元素
   */
  getForm(): Locator {
    return this.form;
  }
}
