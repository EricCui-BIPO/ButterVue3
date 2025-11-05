import { Page, Locator, expect } from '@playwright/test';
import { COMMON_SELECTORS } from '../utils/selectors';

/**
 * 通用对话框组件
 * 封装所有对话框相关的操作，支持 el-dialog
 */
export class DialogComponent {
  private readonly dialog: Locator;
  private readonly dialogTitle: Locator;

  constructor(private page: Page, private dialogSelector: string = COMMON_SELECTORS.dialog) {
    this.dialog = page.getByRole('dialog');
    this.dialogTitle = this.dialog.locator('.el-dialog__title');
  }

  /**
   * 等待对话框打开
   */
  async waitForOpen(timeout: number = 5000): Promise<void> {
    await expect(this.dialog).toBeVisible({ timeout });
  }

  /**
   * 等待对话框关闭
   */
  async waitForClose(timeout: number = 5000): Promise<void> {
    await expect(this.dialog).not.toBeVisible({ timeout });
  }

  /**
   * 验证对话框标题
   */
  async verifyTitle(expectedTitle: string): Promise<void> {
    await expect(this.dialogTitle).toHaveText(expectedTitle);
  }

  /**
   * 填写对话框中的文本输入框
   */
  async fillInput(placeholder: string, value: string): Promise<void> {
    const input = this.dialog.getByPlaceholder(placeholder);
    await input.fill(value);
  }

  /**
   * 清空对话框中的文本输入框
   */
  async clearInput(placeholder: string): Promise<void> {
    const input = this.dialog.getByPlaceholder(placeholder);
    await input.clear();
  }

  /**
   * 填写对话框中的第一个文本输入框
   */
  async fillFirstInput(value: string): Promise<void> {
    const input = this.dialog.getByRole('textbox').first();
    await input.fill(value);
  }

  /**
   * 填写对话框中的最后一个文本输入框（通常是描述字段）
   */
  async fillLastInput(value: string): Promise<void> {
    const input = this.dialog.getByRole('textbox').last();
    await input.fill(value);
  }

  /**
   * 清空对话框中的第一个文本输入框
   */
  async clearFirstInput(): Promise<void> {
    const input = this.dialog.getByRole('textbox').first();
    await input.clear();
  }

  /**
   * 清空对话框中的最后一个文本输入框
   */
  async clearLastInput(): Promise<void> {
    const input = this.dialog.getByRole('textbox').last();
    await input.clear();
  }

  /**
   * 选择对话框中的下拉选项
   */
  async selectOption(optionText: string): Promise<void> {
    const combobox = this.dialog.getByRole('combobox').first();
    await combobox.locator('xpath=ancestor::*[contains(@class, "el-select")]').first().click();
    const selectId = await combobox.getAttribute('aria-controls');
    await this.page.locator(`#${selectId} .el-select-dropdown__item`).filter({ hasText: optionText }).click();
  }

  /**
   * 点击对话框中的按钮
   */
  async clickButton(buttonText: string): Promise<void> {
    const button = this.dialog.getByRole('button', { name: buttonText });
    await button.click();
  }

  /**
   * 点击创建按钮
   */
  async clickCreateButton(): Promise<void> {
    await this.clickButton('Create');
  }

  /**
   * 点击更新按钮
   */
  async clickUpdateButton(): Promise<void> {
    await this.clickButton('Update');
  }

  /**
   * 点击取消按钮
   */
  async clickCancelButton(): Promise<void> {
    await this.clickButton('Cancel');
  }

  /**
   * 验证对话框中的表单验证错误
   */
  async verifyValidationError(errorMessage: string): Promise<void> {
    const errorElement = this.dialog.locator('.el-form-item__error').filter({ hasText: errorMessage });
    
    try {
      await expect(errorElement.first()).toBeVisible({ timeout: 5000 });
    } catch {
      // 如果在错误元素中找不到，尝试在对话框的任何文本中查找
      const dialogError = this.dialog.getByText(errorMessage);
      await expect(dialogError.first()).toBeVisible({ timeout: 5000 });
    }
  }

  /**
   * 关闭对话框
   */
  async close(): Promise<void> {
    const closeButton = this.dialog.locator('.el-dialog__close');
    if (await closeButton.isVisible()) {
      await closeButton.click();
    }
  }

  /**
   * 获取对话框元素
   */
  getDialog(): Locator {
    return this.dialog;
  }

  /**
   * 检查对话框是否可见
   */
  async isVisible(): Promise<boolean> {
    return await this.dialog.isVisible();
  }
}
