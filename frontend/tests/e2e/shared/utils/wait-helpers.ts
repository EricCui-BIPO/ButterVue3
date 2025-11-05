import { Page, Locator, expect } from '@playwright/test';
import { TIMEOUTS } from './constants';

/**
 * 统一的等待策略工具类
 * 避免在代码中使用硬编码的 timeout
 */
export class WaitHelpers {
  /**
   * 等待表格数据加载完成
   */
  static async waitForTableLoad(page: Page, timeout: number = TIMEOUTS.tableLoad): Promise<void> {
    await page.waitForFunction(
      () => {
        const hasVisibleLoading = Array.from(document.querySelectorAll('.el-loading-mask'))
          .some(element => {
            const el = element as HTMLElement;
            if (!el) return false;
            const style = window.getComputedStyle(el);
            return style.visibility !== 'hidden' && style.display !== 'none' && style.opacity !== '0';
          });

        if (hasVisibleLoading) {
          return false;
        }

        const rows = document.querySelectorAll('tbody tr');
        if (rows.length > 0) {
          return true;
        }

        const emptyStates = document.querySelectorAll('.el-table__empty-block, .el-empty');
        return Array.from(emptyStates).some(element => {
          const el = element as HTMLElement;
          if (!el) return false;
          const style = window.getComputedStyle(el);
          return style.visibility !== 'hidden' && style.display !== 'none' && style.opacity !== '0';
        });
      },
      { timeout }
    );
  }

  /**
   * 等待对话框打开
   */
  static async waitForDialogOpen(page: Page, timeout: number = TIMEOUTS.dialogWait): Promise<void> {
    await expect(page.getByRole('dialog')).toBeVisible({ timeout });
  }

  /**
   * 等待对话框关闭
   */
  static async waitForDialogClose(page: Page, timeout: number = TIMEOUTS.dialogWait): Promise<void> {
    await expect(page.getByRole('dialog')).not.toBeVisible({ timeout });
  }

  /**
   * 等待网络空闲
   */
  static async waitForNetworkIdle(page: Page, timeout: number = TIMEOUTS.network): Promise<void> {
    await page.waitForLoadState('networkidle', { timeout });
  }

  /**
   * 等待页面完全加载
   */
  static async waitForPageLoad(page: Page, timeout: number = TIMEOUTS.pageLoad): Promise<void> {
    await page.waitForLoadState('networkidle', { timeout });
    await page.waitForLoadState('domcontentloaded', { timeout });
  }

  /**
   * 等待元素可见
   */
  static async waitForElementVisible(
    locator: Locator, 
    timeout: number = TIMEOUTS.elementWait
  ): Promise<void> {
    await expect(locator).toBeVisible({ timeout });
  }

  /**
   * 等待元素不可见
   */
  static async waitForElementHidden(
    locator: Locator, 
    timeout: number = TIMEOUTS.elementWait
  ): Promise<void> {
    await expect(locator).not.toBeVisible({ timeout });
  }

  /**
   * 等待下拉菜单打开
   */
  static async waitForDropdownOpen(page: Page, timeout: number = TIMEOUTS.dropdownWait): Promise<void> {
    await expect(page.locator('.el-select-dropdown__item').first()).toBeVisible({ timeout });
  }

  /**
   * 等待下拉菜单关闭
   */
  static async waitForDropdownClose(page: Page, timeout: number = TIMEOUTS.dropdownWait): Promise<void> {
    await expect(page.locator('.el-select-dropdown')).not.toBeVisible({ timeout });
  }

  /**
   * 等待操作下拉菜单可见
   */
  static async waitForActionDropdownVisible(page: Page, timeout: number = TIMEOUTS.dropdownWait): Promise<void> {
    await expect(page.locator('.el-dropdown-menu:visible').first()).toBeVisible({ timeout });
  }

  /**
   * 等待操作下拉菜单关闭
   */
  static async waitForActionDropdownHidden(page: Page, timeout: number = TIMEOUTS.dropdownWait): Promise<void> {
    await expect(page.locator('.el-dropdown-menu:visible').first()).not.toBeVisible({ timeout });
  }

  /**
   * 等待确认对话框出现
   */
  static async waitForConfirmDialog(page: Page, timeout: number = TIMEOUTS.dialogWait): Promise<void> {
    await expect(page.locator('.el-message-box')).toBeVisible({ timeout });
  }

  /**
   * 等待消息提示出现
   */
  static async waitForMessage(page: Page, timeout: number = TIMEOUTS.medium): Promise<void> {
    await expect(page.locator('.el-message')).toBeVisible({ timeout });
  }

  /**
   * 等待错误消息出现
   */
  static async waitForErrorMessage(page: Page, timeout: number = TIMEOUTS.medium): Promise<void> {
    await expect(page.locator('.el-message--error')).toBeVisible({ timeout });
  }

  /**
   * 等待成功消息出现
   */
  static async waitForSuccessMessage(page: Page, timeout: number = TIMEOUTS.medium): Promise<void> {
    await expect(page.locator('.el-message--success')).toBeVisible({ timeout });
  }

  /**
   * 等待表格数据更新
   * 先等待网络空闲，再等待表格数据加载
   */
  static async waitForTableDataUpdate(page: Page): Promise<void> {
    await this.waitForNetworkIdle(page);
    await this.waitForTableLoad(page);
  }

  /**
   * 等待表单验证错误出现
   */
  static async waitForValidationError(page: Page, timeout: number = TIMEOUTS.medium): Promise<void> {
    await expect(page.locator('.el-form-item__error')).toBeVisible({ timeout });
  }

  /**
   * 等待加载状态消失
   */
  static async waitForLoadingComplete(page: Page, timeout: number = TIMEOUTS.long): Promise<void> {
    await expect(page.locator('.el-loading-mask')).not.toBeVisible({ timeout });
  }

  /**
   * 智能等待 - 根据操作类型选择合适的等待策略
   */
  static async smartWait(page: Page, operation: 'create' | 'update' | 'delete' | 'search' | 'filter'): Promise<void> {
    switch (operation) {
      case 'create':
      case 'update':
      case 'delete':
        await this.waitForNetworkIdle(page);
        await this.waitForTableDataUpdate(page);
        break;
      case 'search':
      case 'filter':
        await this.waitForNetworkIdle(page);
        await this.waitForTableLoad(page);
        break;
      default:
        await this.waitForNetworkIdle(page);
    }
  }

  /**
   * 等待指定时间（最后手段）
   * 尽量使用其他等待方法替代
   */
  static async wait(timeout: number = TIMEOUTS.short): Promise<void> {
    await new Promise(resolve => setTimeout(resolve, timeout));
  }
}
