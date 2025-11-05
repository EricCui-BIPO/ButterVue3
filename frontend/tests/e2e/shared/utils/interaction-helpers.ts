import { Locator, Page } from '@playwright/test';

/**
 * 通用交互工具函数
 */
export class InteractionHelpers {
  /**
   * 根据占位符文本打开 el-select 下拉并返回弹层元素
   */
  static async openSelectDropdownByText(
    page: Page,
    locator: Locator | Page,
    text: string
  ): Promise<Locator> {
    const selectElement = locator.locator(`.el-select:has(:text("${text}"))`).first();
    await selectElement.click();

    const inputElement = selectElement.locator('input');
    const popupId = await inputElement.getAttribute('aria-controls');
    const popupElement = page.locator(`#${popupId}`);

    return popupElement;
  }
}
