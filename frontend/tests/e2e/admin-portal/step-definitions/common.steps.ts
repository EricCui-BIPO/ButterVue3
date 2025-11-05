import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import type { I0World } from '../../support/world';
import { TestDataResourceType } from '../../support/test-data-manager';
import { logger } from '../../shared/utils/logger';

/**
 * 通用步骤定义
 * 包含所有页面共用的步骤，避免代码重复
 * 这些步骤可以在任何页面对象中使用
 * 
 * 注意：这些步骤需要根据具体的测试场景来调用相应的页面对象方法
 */

// ============================================================================
// 通用操作步骤
// ============================================================================

When('我点击创建按钮', async function (this: I0World) {
  const page = this.getCurrentPage();
  await page.clickCreateButton();
});

When('我点击更新按钮', async function (this: I0World) {
  const page = this.getCurrentPage();
  await page.clickUpdateButton();
});

When('我确认删除操作', async function (this: I0World) {
  // 尝试多种方式找到确认按钮
  const confirmButton = this.page.locator('button').filter({ hasText: /^Confirm$/ }).first();
  
  // 等待按钮可见
  await confirmButton.waitFor({ state: 'visible', timeout: 10000 });
  
  // 强制点击，避免元素遮挡问题
  await confirmButton.click({ force: true });
  
  // 等待对话框消失
  await this.page.locator('.el-message-box').waitFor({ state: 'hidden', timeout: 5000 }).catch(() => {});
});

// ============================================================================
// 通用搜索步骤
// ============================================================================

When('我在搜索框中输入 {string}', async function (this: I0World, searchText: string) {
  const page = this.getCurrentPage();
  await page.getSearchInput().fill(searchText);
});

When('我点击搜索按钮', async function (this: I0World) {
  const page = this.getCurrentPage();
  await page.clickSearchButton();
});

When('我点击重置按钮', async function (this: I0World) {
  const page = this.getCurrentPage();
  await page.resetSearch();
});

Then('搜索框应该被清空', async function (this: I0World) {
  const page = this.getCurrentPage();
  await expect(page.getSearchInput()).toHaveValue('');
});

// ============================================================================
// 通用筛选步骤
// ============================================================================

When('我选择状态筛选器为 {string}', async function (this: I0World, status: string) {
  const page = this.getCurrentPage();
  await page.filterByStatus(status);
});

Then('搜索结果中所有项目都应该是 {string} 状态', async function (this: I0World, status: string) {
  const page = this.getCurrentPage();
  await page.verifyAllResultsAreOfStatus(status);
});

Then('搜索结果中所有项目都应该是 {string} 类型', async function (this: I0World, type: string) {
  const page = this.getCurrentPage();
  await page.verifyAllResultsAreOfType(type);
});

// ============================================================================
// 通用验证步骤
// ============================================================================

When('我不填写任何字段直接点击创建按钮', async function (this: I0World) {
  const page = this.getCurrentPage();
  await page.attemptCreateEmpty();
});

Then('应该显示 {string} 错误提示', async function (this: I0World, errorMessage: string) {
  const page = this.getCurrentPage();
  await page.verifyFormValidationError(errorMessage);
});

Then('应该显示 {string} 提示信息', async function (this: I0World, message: string) {
  const page = this.getCurrentPage();
  await page.verifyNoDataMessage(message);
});

Then('应该显示无数据提示或空表格', async function (this: I0World) {
  const page = this.getCurrentPage();
  await page.verifyNoDataState();
});

// ============================================================================
// 通用背景步骤
// ============================================================================

Given('系统已初始化', async function (this: I0World) {
  // 系统初始化逻辑需要具体的页面对象实现
  // 这里只做标记，实际初始化在具体页面步骤中完成
  logger.debug('📋 系统初始化步骤已调用');
});

Given('我以管理员身份登录系统', async function (this: I0World) {
  // 如果需要实际登录逻辑，可在这里实现
  // if (!this.basePage) {
  //   this.basePage = new BasePage(this.page);
  // }
  // await this.basePage.loginAsAdmin();
});

// ============================================================================
// 新增：参数化通用步骤
// ============================================================================

When('我点击{string}的{string}按钮', async function (this: I0World, entityName: string, action: string) {
  const page = this.getCurrentPage();
  
  switch (action.toLowerCase()) {
    case 'edit':
    case '编辑':
      await page.edit(entityName);
      break;
    case 'delete':
    case '删除':
      await page.delete(entityName);
      break;
    case 'activate':
    case '激活':
      await page.activate(entityName);
      break;
    case 'deactivate':
    case '停用':
      await page.deactivate(entityName);
      break;
    default:
      throw new Error(`不支持的操作: ${action}`);
  }
});

When('我创建{string}名称为{string}类型为{string}', async function (this: I0World, entityType: string, name: string, type: string) {
  const page = this.getCurrentPage();
  await page.create(name, type);
});

When('我创建{string}名称为{string}类型为{string}描述为{string}', async function (this: I0World, entityType: string, name: string, type: string, description: string) {
  const page = this.getCurrentPage();
  await page.create(name, type, description);
});

When('我编辑{string}名称为{string}', async function (this: I0World, entityName: string, newName: string) {
  const page = this.getCurrentPage();
  await page.edit(entityName, newName);
});

When('我编辑{string}描述为{string}', async function (this: I0World, entityName: string, newDescription: string) {
  const page = this.getCurrentPage();
  await page.edit(entityName, undefined, newDescription);
});

Then('{string}应该存在', async function (this: I0World, entityName: string) {
  const page = this.getCurrentPage();
  await page.verifyExists(entityName);
});

Then('{string}应该不存在', async function (this: I0World, entityName: string) {
  const page = this.getCurrentPage();
  await page.verifyNotExists(entityName);
});

Then('{string}的状态应该为{string}', async function (this: I0World, entityName: string, status: string) {
  const page = this.getCurrentPage();
  await page.verifyStatus(entityName, status);
});

When('我按{string}筛选{string}', async function (this: I0World, filterType: string, value: string) {
  const page = this.getCurrentPage();
  
  switch (filterType.toLowerCase()) {
    case 'type':
    case '类型':
      await page.filterByType(value);
      break;
    case 'status':
    case '状态':
      await page.filterByStatus(value);
      break;
    default:
      throw new Error(`不支持的筛选类型: ${filterType}`);
  }
});

When('我搜索{string}', async function (this: I0World, searchText: string) {
  const page = this.getCurrentPage();
  await page.searchByName(searchText);
});

Then('搜索结果应该包含{string}', async function (this: I0World, entityName: string) {
  const page = this.getCurrentPage();
  await page.verifySearchResultContains(entityName);
});

Then('所有结果都应该是{string}类型', async function (this: I0World, type: string) {
  const page = this.getCurrentPage();
  await page.verifyAllResultsAreOfType(type);
});

Then('所有结果都应该是{string}状态', async function (this: I0World, status: string) {
  const page = this.getCurrentPage();
  await page.verifyAllResultsAreOfStatus(status);
});

When('我重置搜索条件', async function (this: I0World) {
  const page = this.getCurrentPage();
  await page.resetSearch();
});

Then('搜索表单应该被重置', async function (this: I0World) {
  const page = this.getCurrentPage();
  await page.verifySearchFormReset();
});

When('我打开创建{string}对话框', async function (this: I0World, entityType: string) {
  const page = this.getCurrentPage();
  await page.openCreateDialog();
});

Then('创建{string}对话框应该打开', async function (this: I0World, entityType: string) {
  const page = this.getCurrentPage();
  await page.dialog.waitForOpen();
});

When('我取消创建{string}', async function (this: I0World, entityType: string) {
  const page = this.getCurrentPage();
  await page.cancelCreate();
});

Then('创建{string}对话框应该关闭', async function (this: I0World, entityType: string) {
  const page = this.getCurrentPage();
  await page.verifyDialogClosed();
});

When('我点击下一页', async function (this: I0World) {
  const page = this.getCurrentPage();
  await page.goToNextPage();
});

When('我点击上一页', async function (this: I0World) {
  const page = this.getCurrentPage();
  await page.goToPreviousPage();
});

Then('页面应该切换', async function (this: I0World) {
  const page = this.getCurrentPage();
  await page.verifyPageChanged();
});

// ============================================================================
// 新增：数据管理相关步骤
// ============================================================================

When('我创建测试数据{string}名称为{string}', async function (this: I0World, entityType: string, name: string) {
  const page = this.getCurrentPage();
  const uniqueName = this.generateUniqueTestName(name);
  await page.createActiveDataForTesting(uniqueName);
  this.recordTestData(entityType.toLowerCase() as TestDataResourceType, uniqueName, true);
});

When('我清理测试数据{string}', async function (this: I0World, dataName: string) {
  const page = this.getCurrentPage();
  await page.cleanupTestData(dataName);
});

When('我批量清理测试数据', async function (this: I0World) {
  const page = this.getCurrentPage();
  const testDataManager = this.testDataManager;
  const allRecords = testDataManager.getAllRecords();
  
  for (const record of allRecords) {
    await page.cleanupTestData(record.name);
  }
  
  testDataManager.clearRecords();
});

Then('测试数据应该被清理', async function (this: I0World) {
  const testDataManager = this.testDataManager;
  const stats = testDataManager.getStats();
  expect(stats.totalRecords).toBe(0);
});