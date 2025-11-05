import { Page, Locator, expect } from '@playwright/test';
import { COMMON_SELECTORS } from '../utils/selectors';

/**
 * 通用表格组件
 * 封装所有表格相关的操作，支持 i0-table 和 el-table
 */
export class TableComponent {
  private readonly table: Locator;
  private readonly rows: Locator;
  private readonly noDataText: Locator;
  private readonly emptyTable: Locator;

  constructor(private page: Page, private tableSelector: string = COMMON_SELECTORS.table) {
    this.table = page.locator(tableSelector);
    this.rows = page.locator('tbody tr');
    this.noDataText = page.locator('.el-empty__description, .no-data, [data-testid="no-data"]');
    this.emptyTable = page.locator('.el-table__empty-block');
  }

  /**
   * 等待表格数据加载
   */
  async waitForDataLoad(timeout: number = 10000): Promise<void> {
    await this.page.waitForSelector('tbody tr', { timeout });
  }

  /**
   * 根据文本内容获取表格行
   */
  async getRowByText(text: string): Promise<Locator> {
    return this.rows.filter({ hasText: text }).first();
  }

  /**
   * 验证指定文本的行是否存在
   */
  async verifyRowExists(text: string): Promise<void> {
    const row = await this.getRowByText(text);
    await expect(row).toBeVisible();
  }

  /**
   * 验证指定文本的行不存在
   */
  async verifyRowNotExists(text: string): Promise<void> {
    await expect(this.page.getByText(text)).not.toBeVisible();
  }

  /**
   * 获取表格中所有可见行
   */
  async getVisibleRows(): Promise<Locator[]> {
    const rows: Locator[] = [];
    const rowCount = await this.rows.count();

    for (let index = 0; index < rowCount; index += 1) {
      const row = this.rows.nth(index);
      if (await row.isVisible()) {
        rows.push(row);
      }
    }

    return rows;
  }

  /**
   * 验证表格为空状态
   */
  async verifyEmptyState(): Promise<void> {
    const hasNoDataText = await this.noDataText.first().isVisible().catch(() => false);
    if (hasNoDataText) {
      await expect(this.noDataText.first()).toBeVisible();
      return;
    }

    await expect(this.rows).toHaveCount(0);
    if (await this.emptyTable.isVisible().catch(() => false)) {
      await expect(this.emptyTable).toBeVisible();
    }
  }

  /**
   * 验证所有行的指定列都包含指定文本
   */
  async verifyAllRowsContainText(columnIndex: number, expectedText: string): Promise<void> {
    await this.waitForDataLoad();
    const visibleRows = await this.getVisibleRows();
    expect(visibleRows.length).toBeGreaterThan(0);

    for (const row of visibleRows) {
      const cell = row.locator('td').nth(columnIndex);
      await expect(cell).toContainText(expectedText);
    }
  }

  /**
   * 验证所有行的指定列都包含指定标签文本
   */
  async verifyAllRowsHaveTag(columnIndex: number, expectedTagText: string): Promise<void> {
    await this.waitForDataLoad();
    const visibleRows = await this.getVisibleRows();
    expect(visibleRows.length).toBeGreaterThan(0);

    for (const row of visibleRows) {
      const tag = row.locator('td').nth(columnIndex).locator('.el-tag');
      await expect(tag.first()).toHaveText(expectedTagText);
    }
  }

  /**
   * 获取表格行数
   */
  async getRowCount(): Promise<number> {
    return await this.rows.count();
  }

  /**
   * 检查表格是否有数据
   */
  async hasData(): Promise<boolean> {
    const count = await this.getRowCount();
    return count > 0;
  }
}
