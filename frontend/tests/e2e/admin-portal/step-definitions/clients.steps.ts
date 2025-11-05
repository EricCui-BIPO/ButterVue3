import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { ClientPage } from '../page-objects/ClientPage';
import type { I0World } from '../../support/world';

/**
 * 客户管理功能步骤定义
 * 对应 clients.feature 文件中的场景步骤
 */

// ============================================================================
// Background: 背景步骤
// ============================================================================
// 注意：背景步骤已在 common.steps.ts 中定义，这里不需要重复实现

When('我访问客户管理页面', async function (this: I0World) {
  this.clientPage = new ClientPage(this.page);
  this.setCurrentPage(this.clientPage);
  await this.clientPage.navigateToClientPage();
});

// ============================================================================
// Scenario: 创建新的客户
// ============================================================================
When('我点击创建客户按钮', async function (this: I0World) {
  await this.clientPage!.openCreateDialog();
});

Then('创建客户对话框应该打开', async function (this: I0World) {
  await expect(this.clientPage!.dialog.getDialog().getByText('Create Client')).toBeVisible();
});

When('我填写客户名称为 {string}', async function (this: I0World, clientName: string) {
  const uniqueClientName = this.generateUniqueTestName(clientName);
  await this.clientPage!.dialogClientNameInput.fill(uniqueClientName);
  this.clientName = uniqueClientName;
});

When('我填写客户代码为 {string}', async function (this: I0World, clientCode: string) {
  const uniqueClientCode = this.generateUniqueTestName(clientCode);
  await this.clientPage!.dialogClientCodeInput.fill(uniqueClientCode);
});

When('我选择客户位置为 {string}', async function (this: I0World, locationName: string) {
  await this.clientPage!.dialog.selectOption(locationName);
});

When('我填写客户描述为 {string}', async function (this: I0World, description: string) {
  await this.clientPage!.dialogDescriptionTextarea.fill(description);
});

Then('新创建的客户应该出现在表格中', async function (this: I0World) {
  await this.clientPage!.verifyClientExists(this.clientName!);
  this.recordTestData('client', this.clientName!, true);
});

Then('表格中应该显示客户名称 {string}', async function (this: I0World, clientName: string) {
  await this.clientPage!.verifyClientExists(this.clientName!);
});

Then('表格中应该显示客户代码 {string}', async function (this: I0World, clientCode: string) {
  const clientRow = this.page.locator('tbody tr').filter({ hasText: this.clientName! });
  await expect(clientRow).toContainText(clientCode);
});

Then('表格中应该显示客户状态 {string}', async function (this: I0World, status: string) {
  const clientRow = this.page.locator('tbody tr').filter({ hasText: this.clientName! });
  await expect(clientRow).toContainText(status);
});

// ============================================================================
// Scenario: 按名称搜索客户
// ============================================================================
// 注意:"我在搜索框中输入" 和 "我点击搜索按钮" 已在 common.steps.ts 中定义

Then('搜索结果应该包含名称为 {string} 的客户', async function (this: I0World, clientName: string) {
  await this.clientPage!.verifySearchResultContains(clientName);
});

// ============================================================================
// Scenario: 按位置筛选客户
// ============================================================================
When('我选择客户位置筛选器为 {string}', async function (this: I0World, locationName: string) {
  await this.clientPage!.filterByLocation(locationName);
});

Then('搜索结果中所有项目都应该位于 {string}', async function (this: I0World, locationName: string) {
  await this.clientPage!.verifyAllResultsInLocation(locationName);
});

// ============================================================================
// Scenario: 按状态筛选客户
// ============================================================================
// 注意:"我选择状态筛选器为" 和 "搜索结果中所有项目都应该是 {string} 状态" 已在 common.steps.ts 中定义

// ============================================================================
// Scenario: 重置搜索筛选条件
// ============================================================================
Then('客户位置筛选器应该重置为默认值', async function (this: I0World) {
  const selectLocator = this.page.locator('.el-form').first().locator('.el-select').filter({ hasText: 'location' }).first();
  await selectLocator.waitFor({ state: 'visible', timeout: 3000 });
  await expect(selectLocator).toBeVisible();
});

Then('状态筛选器应该重置为默认值', async function (this: I0World) {
  const selectLocator = this.page.locator('.el-form').first().locator('.el-select').filter({ hasText: 'status' }).first();
  await selectLocator.waitFor({ state: 'visible', timeout: 3000 });
  await expect(selectLocator).toBeVisible();
});

// ============================================================================
// Scenario: 编辑现有客户
// ============================================================================
Given('存在一个名为 {string} 的客户', async function (this: I0World, clientName: string) {
  const uniqueClientName = this.generateUniqueTestName(clientName);
  await this.clientPage!.createClient(
    uniqueClientName,
    this.generateUniqueTestName('TEST-CODE'),
    '中国',
    'Original description'
  );
  this.clientName = uniqueClientName;
  this.recordTestData('client', uniqueClientName, true);
});

When('我点击该客户的编辑按钮', async function (this: I0World) {
  await this.clientPage!.openEditDialog(this.clientName!);
});

Then('编辑客户对话框应该打开', async function (this: I0World) {
  await expect(this.clientPage!.dialog.getDialog().getByText('Edit Client')).toBeVisible();
});

When('我将客户描述修改为 {string}', async function (this: I0World, newDescription: string) {
  await this.clientPage!.dialogDescriptionTextarea.clear();
  await this.clientPage!.dialogDescriptionTextarea.fill(newDescription);
});

Then('表格中应该显示客户描述 {string}', async function (this: I0World, updatedDescription: string) {
  await this.clientPage!.verifyUpdatedClientDescription(this.clientName!, updatedDescription);
});

// ============================================================================
// Scenario: 停用和激活客户
// ============================================================================
Given('存在一个名为 {string} 的激活状态客户', async function (this: I0World, clientName: string) {
  const uniqueClientName = this.generateUniqueTestName(clientName);
  await this.clientPage!.createClient(
    uniqueClientName,
    this.generateUniqueTestName('TOGGLE-CODE'),
    '中国',
    'Client for status toggle testing'
  );
  this.clientName = uniqueClientName;
  this.recordTestData('client', uniqueClientName, true);
});

When('我点击该客户的停用按钮', async function (this: I0World) {
  await this.clientPage!.deactivateClient(this.clientName!);
});

Then('该客户的状态应该变为 {string}', async function (this: I0World, status: string) {
  await this.clientPage!.verifyClientStatus(this.clientName!, status);
});

When('我点击该客户的激活按钮', async function (this: I0World) {
  await this.clientPage!.activateClient(this.clientName!);
});

// ============================================================================
// Scenario: 删除客户
// ============================================================================
When('我点击该客户的删除按钮', async function (this: I0World) {
  await this.clientPage!.deleteClient(this.clientName!);
});

// 注意:"我确认删除操作" 已在 common.steps.ts 中定义

Then('该客户应该从表格中消失', async function (this: I0World) {
  await this.clientPage!.verifyClientNotExists(this.clientName!);
});

// ============================================================================
// Scenario: 验证创建客户时的必填字段
// ============================================================================
// 注意:"我不填写任何字段直接点击创建按钮" 和 "应该显示 {string} 错误提示" 已在 common.steps.ts 中定义

// ============================================================================
// Scenario: 显示无数据提示
// ============================================================================
When('我搜索一个不存在的客户 {string}', async function (this: I0World, searchText: string) {
  await this.clientPage!.clientNameInput.fill(searchText);
  await this.clientPage!.searchButton.click();
});

// 注意:"应该显示无数据提示或空表格" 已在 common.steps.ts 中定义
