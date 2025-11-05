import { Page, expect } from '@playwright/test';
import { TIMEOUTS } from '../utils/constants';
import { WaitHelpers } from '../utils/wait-helpers';

/**
 * 通用操作下拉菜单组件
 * 封装所有操作下拉菜单相关的操作，支持 el-dropdown
 */
export class ActionDropdownComponent {
  constructor(private page: Page) {}

  /**
   * 打开指定行所在的操作下拉菜单
   * 注意：ActionDropdown 的菜单由 Element Plus 的 popper 渲染在全局（body）下，因此需要先触发 hover 打开
   */
  async openActionsDropdownForRow(rowText: string): Promise<void> {
    // 精确定位到包含指定文本的表格行
    const row = this.page.locator('.el-table').locator('tr').filter({ hasText: rowText }).first();
    await expect(row).toBeVisible();

    // 优先尝试最常见的触发方式，失败则尝试 hover
    const trigger = row.locator('.el-dropdown, .action-dropdown-trigger, .el-dropdown-trigger').first();
    await trigger.waitFor({ state: 'visible', timeout: TIMEOUTS.elementWait });
    
    try {
      await trigger.click();
      await WaitHelpers.waitForActionDropdownVisible(this.page);
    } catch {
      await trigger.hover();
      await WaitHelpers.waitForActionDropdownVisible(this.page);
    }
  }

  /**
   * 在已打开的操作下拉菜单中，点击指定名称的菜单项
   */
  async clickActionDropdownItem(actionText: 'Edit' | 'Deactivate' | 'Activate' | 'Delete'): Promise<void> {
    // 等待下拉菜单可见
    const visibleMenu = this.page.locator('.el-dropdown-menu:visible').first();
    await WaitHelpers.waitForElementVisible(visibleMenu, TIMEOUTS.dropdownWait);
    
    // 尝试多种方式点击菜单项
    const possibleSelectors = [
      `.el-dropdown-menu__item:has-text("${actionText}")`,
      `text=${actionText}`
    ];
    
    let itemClicked = false;
    for (const selector of possibleSelectors) {
      try {
        const itemLocator = visibleMenu.locator(selector);
        if (await itemLocator.count() === 0) {
          continue;
        }

        const item = itemLocator.first();
        await WaitHelpers.waitForElementVisible(item, TIMEOUTS.dropdownWait);
        await item.click();
        itemClicked = true;
        break;
      } catch {
        // 继续尝试下一个选择器
      }
    }
    
    if (!itemClicked) {
      // 如果上述方法都失败，尝试查找所有菜单项
      const allItems = visibleMenu.locator('.el-dropdown-menu__item');
      const itemCount = await allItems.count();
      
      for (let i = 0; i < itemCount; i++) {
        const item = allItems.nth(i);
        const itemText = await item.textContent().catch(() => '');
        
        if (itemText.includes(actionText)) {
          await WaitHelpers.waitForElementVisible(item, TIMEOUTS.dropdownWait);
          await item.click();
          itemClicked = true;
          break;
        }
      }
    }
    
    if (!itemClicked) {
      throw new Error(`无法找到或点击菜单项: ${actionText}`);
    }
    
    await WaitHelpers.waitForActionDropdownHidden(this.page);
  }

  /**
   * 在指定行中执行操作（组合方法）
   */
  async performActionInRow(rowText: string, action: 'Edit' | 'Deactivate' | 'Activate' | 'Delete'): Promise<void> {
    await this.openActionsDropdownForRow(rowText);
    await this.clickActionDropdownItem(action);
  }

  /**
   * 编辑指定行
   */
  async editRow(rowText: string): Promise<void> {
    await this.performActionInRow(rowText, 'Edit');
  }

  /**
   * 停用指定行
   */
  async deactivateRow(rowText: string): Promise<void> {
    await this.performActionInRow(rowText, 'Deactivate');
    const confirmDialog = this.page.locator('.el-message-box');
    const dialogVisible = await confirmDialog
      .waitFor({ state: 'visible', timeout: TIMEOUTS.dialogWait })
      .then(() => true)
      .catch(() => false);

    if (dialogVisible) {
      const confirmButton = confirmDialog.getByRole('button', { name: /Confirm|确认|确定/ });
      await WaitHelpers.waitForElementVisible(confirmButton, TIMEOUTS.dialogWait);
      await confirmButton.click();
      await WaitHelpers.waitForElementHidden(confirmDialog, TIMEOUTS.dialogWait).catch(() => undefined);
      // 移除网络空闲等待，由 GenericCrudPage 处理
    }
  }

  /**
   * 激活指定行
   */
  async activateRow(rowText: string): Promise<void> {
    await this.performActionInRow(rowText, 'Activate');
    const confirmDialog = this.page.locator('.el-message-box');
    const dialogVisible = await confirmDialog
      .waitFor({ state: 'visible', timeout: TIMEOUTS.dialogWait })
      .then(() => true)
      .catch(() => false);

    if (dialogVisible) {
      const confirmButton = confirmDialog.getByRole('button', { name: /Confirm|确认|确定/ });
      await WaitHelpers.waitForElementVisible(confirmButton, TIMEOUTS.dialogWait);
      await confirmButton.click();
      await WaitHelpers.waitForElementHidden(confirmDialog, TIMEOUTS.dialogWait).catch(() => undefined);
      // 移除网络空闲等待，由 GenericCrudPage 处理
    }
  }

  /**
   * 删除指定行（只打开确认对话框，不确认）
   */
  async deleteRow(rowText: string): Promise<void> {
    await this.performActionInRow(rowText, 'Delete');
    await this.page.locator('.el-message-box').waitFor({ state: 'visible', timeout: TIMEOUTS.dialogWait }).catch(() => undefined);
  }

  /**
   * 确认删除操作
   */
  async confirmDelete(): Promise<void> {
    const confirmDialog = this.page.locator('.el-message-box');
    await confirmDialog.waitFor({ state: 'visible', timeout: TIMEOUTS.dialogWait });

    const confirmButton = confirmDialog.getByRole('button', { name: /Confirm|确认|确定/ });
    await WaitHelpers.waitForElementVisible(confirmButton, TIMEOUTS.dialogWait);
    await confirmButton.click({ force: true });
    await WaitHelpers.waitForElementHidden(confirmDialog, TIMEOUTS.dialogWait).catch(() => undefined);
    await WaitHelpers.waitForNetworkIdle(this.page).catch(() => undefined);
  }

  /**
   * 删除指定行并确认
   */
  async deleteRowWithConfirmation(rowText: string): Promise<void> {
    await this.deleteRow(rowText);
    await this.confirmDelete();
  }

  /**
   * 检查操作下拉菜单是否可见
   */
  async isDropdownVisible(): Promise<boolean> {
    return await this.page.locator('.el-dropdown-menu:visible').first().isVisible();
  }

  /**
   * 等待操作下拉菜单可见
   */
  async waitForDropdownVisible(timeout: number = 5000): Promise<void> {
    await expect(this.page.locator('.el-dropdown-menu:visible').first()).toBeVisible({ timeout });
  }
}
