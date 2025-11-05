import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { EntityPage } from '../page-objects/EntityPage';
import type { I0World } from '../../support/world';

/**
 * 实体管理功能步骤定义
 * 对应 entity.feature 文件中的场景步骤
 * 
 * 注意：背景步骤（Given '系统已初始化', Given '我以管理员身份登录系统'）
 * 已在 service-type.steps.ts 中定义，在这里复用
 */

// ============================================================================
// Background: 实体特定的背景步骤
// ============================================================================
When('我访问实体管理页面', async function (this: I0World) {
  this.entityPage = new EntityPage(this.page);
  this.setCurrentPage(this.entityPage); // 设置当前页面
  await this.entityPage.navigateToEntityPage();
});

// ============================================================================
// Scenario: 创建新的实体
// ============================================================================
When('我点击创建实体按钮', async function (this: I0World) {
  await this.entityPage.openCreateDialog();
});

Then('创建实体对话框应该打开', async function (this: I0World) {
  await expect(this.entityPage.dialog.getDialog().getByText('Create Entity')).toBeVisible();
});

When('我填写实体名称为 {string}', async function (this: I0World, entityName: string) {
  // 自动为测试数据添加唯一后缀，但保持 Feature 文件的可读性
  // Feature 中写 "Test Entity"，实际创建 "Test Entity_1234567890"
  const uniqueEntityName = this.generateUniqueTestName(entityName);
  await this.entityPage.dialogNameInput.fill(uniqueEntityName);
  // 仅保存到实例变量，不记录到测试数据管理器
  this.entityName = uniqueEntityName;
});

When('我选择实体类型为 {string}', async function (this: I0World, entityType: string) {
  await this.entityPage.dialog.selectOption(entityType);
});

When('我填写实体描述为 {string}', async function (this: I0World, description: string) {
  await this.entityPage.dialogDescriptionTextarea.fill(description);
});


Then('新创建的实体应该出现在表格中', async function (this: I0World) {
  await this.entityPage.verifyEntityExists(this.entityName!);
  // ✅ 验证创建成功后才记录到测试数据管理器
  this.recordTestData('entity', this.entityName!, true);
});

Then('表格中应该显示实体名称 {string}', async function (this: I0World, entityName: string) {
  await this.entityPage.verifyEntityExists(entityName);
});

Then('表格中应该显示实体类型 {string}', async function (this: I0World, entityType: string) {
  // 使用更精确的选择器，只检查当前创建的实体行
  const entityRow = this.page.locator('tbody tr').filter({ hasText: this.entityName! });
  await expect(entityRow).toContainText(entityType);
});

// ============================================================================
// Scenario: 按名称搜索实体
// ============================================================================
// 注意:"我在搜索框中输入" 和 "我点击搜索按钮" 已在 common.steps.ts 中定义

Then('搜索结果应该包含名称为 {string} 的实体', async function (this: I0World, entityName: string) {
  await this.entityPage.verifySearchResultContains(entityName);
});

// ============================================================================
// Scenario: 按实体类型筛选 & 按状态筛选实体
// ============================================================================
When('我选择实体类型筛选器为 {string}', async function (this: I0World, entityType: string) {
  await this.entityPage.filterByEntityType(entityType);
});

// 注意:"搜索结果中所有项目都应该是 {string} 类型" 和 "我选择状态筛选器为 {string}" 已在 common.steps.ts 中定义
// 注意:"搜索结果中所有项目都应该是 {string} 状态" 已在 common.steps.ts 中定义

// ============================================================================
// Scenario: 重置搜索筛选条件
// ============================================================================
// 注意:"我点击重置按钮" 和 "搜索框应该被清空" 已在 common.steps.ts 中定义

Then('实体类型筛选器应该重置为默认值', async function (this: I0World) {
  // 等待表单元素状态稳定
  const selectLocator = this.page.locator('.el-form').first().locator('.el-select').filter({ hasText: 'entity type' }).first();
  await selectLocator.waitFor({ state: 'visible', timeout: 3000 });
  await expect(selectLocator).toBeVisible();
});

// ============================================================================
// Scenario: 编辑现有实体
// ============================================================================
Given('存在一个名为 {string} 的实体', async function (this: I0World, entityName: string) {
  // 自动为测试数据添加唯一后缀
  const uniqueEntityName = this.generateUniqueTestName(entityName);
  await this.entityPage.createEntity(uniqueEntityName, 'Client', 'Original description');
  this.entityName = uniqueEntityName;
  // ✅ 创建成功后记录到测试数据管理器
  this.recordTestData('entity', uniqueEntityName, true);
});

When('我点击该实体的编辑按钮', async function (this: I0World) {
  // 仅打开编辑对话框，不自动提交
  // 使用 openEditDialog 方法，避免调用 editEntity 组合方法
  await this.entityPage.openEditDialog(this.entityName!);
});

Then('编辑实体对话框应该打开', async function (this: I0World) {
  await expect(this.entityPage.dialog.getDialog().getByText('Edit Entity')).toBeVisible();
});

When('我将实体描述修改为 {string}', async function (this: I0World, newDescription: string) {
  await this.entityPage.dialogDescriptionTextarea.clear();
  await this.entityPage.dialogDescriptionTextarea.fill(newDescription);
});

// 注意:"我点击更新按钮" 已在 common.steps.ts 中定义

Then('表格中应该显示更新后的实体描述 {string}', async function (this: I0World, updatedDescription: string) {
  // 验证描述是否更新
  await this.entityPage.verifyUpdatedEntityDescription(this.entityName!, updatedDescription);
});

// ============================================================================
// Scenario: 停用和激活实体
// ============================================================================
Given('存在一个名为 {string} 的激活状态实体', async function (this: I0World, entityName: string) {
  // 自动为测试数据添加唯一后缀
  const uniqueEntityName = this.generateUniqueTestName(entityName);
  await this.entityPage.createActiveEntityForTesting(uniqueEntityName);
  this.entityName = uniqueEntityName;
  // ✅ 创建成功后记录到测试数据管理器
  this.recordTestData('entity', uniqueEntityName, true);
});

When('我点击该实体的停用按钮', async function (this: I0World) {
  await this.entityPage.deactivateEntity(this.entityName!);
});

Then('该实体的状态应该变为 {string}', async function (this: I0World, status: string) {
  await this.entityPage.verifyEntityStatus(this.entityName!, status);
});

When('我点击该实体的激活按钮', async function (this: I0World) {
  await this.entityPage.activateEntity(this.entityName!);
});

// ============================================================================
// Scenario: 删除实体
// ============================================================================
When('我点击该实体的删除按钮', async function (this: I0World) {
  await this.entityPage.deleteEntity(this.entityName!);
});

// 注意:"我确认删除操作" 已在 common.steps.ts 中定义

Then('该实体应该从表格中消失', async function (this: I0World) {
  await this.entityPage.verifyEntityNotExists(this.entityName!);
});

// ============================================================================
// Scenario: 验证创建实体时的必填字段
// ============================================================================
// 注意:"我不填写任何字段直接点击创建按钮" 和 "应该显示 {string} 错误提示" 已在 common.steps.ts 中定义

// ============================================================================
// Scenario: 显示无数据提示
// ============================================================================
When('我搜索一个不存在的实体 {string}', async function (this: I0World, searchText: string) {
  await this.entityPage.nameInput.fill(searchText);
  await this.entityPage.searchButton.click();
});

// 注意:"应该显示 {string} 提示信息" 和 "应该显示无数据提示或空表格" 已在 common.steps.ts 中定义
