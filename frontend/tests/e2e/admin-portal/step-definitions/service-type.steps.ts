import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import { ServiceTypePage } from '../page-objects/ServiceTypePage';
import { BasePage } from '../../shared/page-objects/BasePage';
import type { I0World } from '../../support/world';

/**
 * 服务类型管理功能步骤定义
 * 对应 service-type.feature 文件中的场景步骤
 */

// ============================================================================
// Background: 背景步骤
// ============================================================================
// 注意：背景步骤已在 common.steps.ts 中定义，这里不需要重复实现

When('我访问服务类型管理页面', async function (this: I0World) {
  this.serviceTypePage = new ServiceTypePage(this.page);
  this.setCurrentPage(this.serviceTypePage); // 设置当前页面
  await this.serviceTypePage.navigateToServiceTypePage();
});

// ============================================================================
// Scenario: 创建新的服务类型
// ============================================================================
When('我点击创建服务类型按钮', async function (this: I0World) {
  await this.serviceTypePage.openCreateDialog();
});

Then('创建服务类型对话框应该打开', async function (this: I0World) {
  await expect(this.serviceTypePage.dialog.getDialog().getByText('Create Service Type')).toBeVisible();
});

When('我填写服务类型名称为 {string}', async function (this: I0World, serviceName: string) {
  // 对于重复类型测试，不添加唯一后缀
  await this.serviceTypePage.dialogServiceTypeNameInput.fill(serviceName);
  this.serviceName = serviceName;
});

When('我选择服务类型为 {string}', async function (this: I0World, serviceType: string) {
  await this.serviceTypePage.dialog.selectOption(serviceType);
});

When('我填写描述为 {string}', async function (this: I0World, description: string) {
  await this.serviceTypePage.dialogDescriptionTextarea.fill(description);
});


Then('应该显示重复服务类型的错误提示', async function (this: I0World) {
  await this.serviceTypePage.verifyDuplicateTypeError();
});

Then('表格中应该显示服务类型名称 {string}', async function (this: I0World, serviceName: string) {
  await this.serviceTypePage.verifyServiceTypeExists(serviceName);
});

Then('表格中应该显示服务类型 {string}', async function (this: I0World, serviceType: string) {
  // 使用更精确的选择器，只检查当前创建的服务类型行
  const serviceTypeRow = this.page.locator('tbody tr').filter({ hasText: this.serviceName! });
  await expect(serviceTypeRow).toContainText(serviceType);
});

// ============================================================================
// Scenario: 按名称搜索服务类型
// ============================================================================
// 注意:"我在搜索框中输入" 和 "我点击搜索按钮" 已在 common.steps.ts 中定义

Then('搜索结果应该包含名称为 {string} 的服务类型', async function (this: I0World, serviceName: string) {
  await this.serviceTypePage.verifySearchResultContains(serviceName);
});

// ============================================================================
// Scenario: 按服务类型筛选 & 按状态筛选服务类型
// ============================================================================
When('我选择服务类型筛选器为 {string}', async function (this: I0World, serviceType: string) {
  await this.serviceTypePage.filterByServiceType(serviceType);
});

// 注意:"搜索结果中所有项目都应该是 {string} 类型" 和 "我选择状态筛选器为 {string}" 已在 common.steps.ts 中定义
// 注意:"搜索结果中所有项目都应该是 {string} 状态" 已在 common.steps.ts 中定义

// ============================================================================
// Scenario: 重置搜索筛选条件
// ============================================================================
// 注意：这个场景重用了其他场景中已定义的步骤：
// - "我在搜索框中输入 {string}" (已在搜索场景中定义)
// - "我选择服务类型筛选器为 {string}" (已在筛选场景中定义)

// 注意:"我点击重置按钮" 和 "搜索框应该被清空" 已在 common.steps.ts 中定义

Then('服务类型筛选器应该重置为默认值', async function (this: I0World) {
  // 等待表单元素状态稳定
  const selectLocator = this.page.locator('.el-form').first().locator('.el-select').filter({ hasText: 'service type' }).first();
  await selectLocator.waitFor({ state: 'visible', timeout: 3000 });
  await expect(selectLocator).toBeVisible();
});

// ============================================================================
// Scenario: 编辑现有服务类型
// ============================================================================
Given('系统中存在 {string} 服务类型', async function (this: I0World, serviceName: string) {
  // 直接使用系统中已存在的服务类型，不创建新数据
  this.serviceName = serviceName;
  // 验证该服务类型确实存在
  await this.serviceTypePage.verifyServiceTypeExists(serviceName);
});

When('我点击该服务类型的编辑按钮', async function (this: I0World) {
  // 仅打开编辑对话框，不自动提交
  // 使用 openEditDialog 方法，避免调用 editServiceType 组合方法
  await this.serviceTypePage.openEditDialog(this.serviceName!);
});

Then('编辑服务类型对话框应该打开', async function (this: I0World) {
  await expect(this.serviceTypePage.dialog.getDialog().getByText('Edit Service Type')).toBeVisible();
});

// 注意: "我将描述修改为" 步骤已存在，无需修改名称相关步骤

When('我将描述修改为 {string}', async function (this: I0World, newDescription: string) {
  await this.serviceTypePage.dialogDescriptionTextarea.clear();
  await this.serviceTypePage.dialogDescriptionTextarea.fill(newDescription);
});

// 注意:"我点击更新按钮" 已在 common.steps.ts 中定义

Then('服务类型描述应该更新成功', async function (this: I0World) {
  // 验证描述是否更新
  await this.serviceTypePage.verifyUpdatedServiceTypeDescription(this.serviceName!, "Updated description for automation testing");
});

// ============================================================================
// Scenario: 停用和激活服务类型
// ============================================================================
// 该步骤已被 "系统中存在 {string} 服务类型" 替代，此处保留以防其他场景使用
// Given('存在一个名为 {string} 的激活状态服务类型' - 已废弃

When('我点击该服务类型的停用按钮', async function (this: I0World) {
  await this.serviceTypePage.deactivateServiceType(this.serviceName!);
});

Then('该服务类型的状态应该变为 {string}', async function (this: I0World, status: string) {
  await this.serviceTypePage.verifyServiceTypeStatus(this.serviceName!, status);
});

When('我点击该服务类型的激活按钮', async function (this: I0World) {
  await this.serviceTypePage.activateServiceType(this.serviceName!);
});

// ============================================================================
// Scenario: 删除服务类型
// ============================================================================
When('我点击该服务类型的删除按钮', async function (this: I0World) {
  await this.serviceTypePage.deleteServiceType(this.serviceName!);
});

// 注意:"我确认删除操作" 已在 common.steps.ts 中定义

Then('该服务类型应该从表格中消失', async function (this: I0World) {
  await this.serviceTypePage.verifyServiceTypeNotExists(this.serviceName!);
});

// ============================================================================
// Scenario: 验证创建服务类型时的必填字段
// ============================================================================
// 注意:"我不填写任何字段直接点击创建按钮" 和 "应该显示 {string} 错误提示" 已在 common.steps.ts 中定义

// ============================================================================
// Scenario: 显示无数据提示
// ============================================================================
When('我搜索一个不存在的服务类型 {string}', async function (this: I0World, searchText: string) {
  await this.serviceTypePage.serviceTypeNameInput.fill(searchText);
  await this.serviceTypePage.searchButton.click();
});

// 注意:"应该显示 {string} 提示信息" 和 "应该显示无数据提示或空表格" 已在 common.steps.ts 中定义
