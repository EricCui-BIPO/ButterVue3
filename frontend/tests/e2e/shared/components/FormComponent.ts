import { Page, Locator, expect } from '@playwright/test';
import { COMMON_SELECTORS } from '../utils/selectors';

/**
 * 通用表单组件
 * 封装所有表单相关的操作，支持 el-form
 */
export class FormComponent {
  private readonly form: Locator;

  constructor(private page: Page, private formSelector: string = COMMON_SELECTORS.form) {
    this.form = page.locator(formSelector).first();
  }

  /**
   * 填写文本输入框
   */
  async fillInput(placeholder: string, value: string): Promise<void> {
    const input = this.form.getByPlaceholder(placeholder);
    await input.fill(value);
  }

  /**
   * 清空文本输入框
   */
  async clearInput(placeholder: string): Promise<void> {
    const input = this.form.getByPlaceholder(placeholder);
    await input.clear();
  }

  /**
   * 选择下拉选项
   */
  async selectOption(selectName: string, optionText: string): Promise<void> {
    const select = this.form.getByRole('combobox', { name: selectName });
    await select.click();
    
    // 等待下拉菜单选项加载完成
    await this.page.locator('.el-select-dropdown__item').first().waitFor({ state: 'visible', timeout: 5000 });
    
    // 选择指定选项
    await this.page.locator('.el-select-dropdown__item').filter({ hasText: optionText }).click();
  }

  /**
   * 点击按钮
   */
  async clickButton(buttonText: string): Promise<void> {
    const button = this.form.getByRole('button', { name: buttonText });
    await button.click();
  }

  /**
   * 验证输入框的值
   */
  async verifyInputValue(placeholder: string, expectedValue: string): Promise<void> {
    const input = this.form.getByPlaceholder(placeholder);
    await expect(input).toHaveValue(expectedValue);
  }

  /**
   * 验证表单验证错误
   */
  async verifyValidationError(errorMessage: string): Promise<void> {
    const errorElement = this.form.locator('.el-form-item__error').filter({ hasText: errorMessage });
    
    try {
      await expect(errorElement.first()).toBeVisible({ timeout: 5000 });
    } catch {
      // 如果在错误元素中找不到，尝试在表单的任何文本中查找
      const formError = this.form.getByText(errorMessage);
      await expect(formError.first()).toBeVisible({ timeout: 5000 });
    }
  }

  /**
   * 验证表单是否重置
   */
  async verifyFormReset(): Promise<void> {
    // 验证所有输入框被清空
    const inputs = this.form.locator('input[type="text"], input[type="email"], textarea');
    const inputCount = await inputs.count();
    
    for (let i = 0; i < inputCount; i++) {
      const input = inputs.nth(i);
      const value = await input.inputValue();
      expect(value).toBe('');
    }
  }

  /**
   * 获取表单元素
   */
  getForm(): Locator {
    return this.form;
  }
}
