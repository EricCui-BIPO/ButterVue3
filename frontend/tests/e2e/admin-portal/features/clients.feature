Feature: 客户管理功能
  作为管理员
  我希望能够管理客户
  以便维护系统中的客户信息

  Background:
    Given 系统已初始化
    And 我以管理员身份登录系统
    When 我访问客户管理页面

  Scenario: 创建新的客户
    When 我点击创建客户按钮
    Then 创建客户对话框应该打开
    When 我填写客户名称为 "Auto Client Alpha"
    And 我填写客户代码为 "AUTO-CLIENT-ALPHA"
    And 我选择客户位置为 "中国"
    And 我填写客户描述为 "用于自动化测试的客户记录"
    And 我点击创建按钮
    Then 新创建的客户应该出现在表格中
    And 表格中应该显示客户名称 "Auto Client Alpha"
    And 表格中应该显示客户代码 "AUTO-CLIENT-ALPHA"
    And 表格中应该显示客户状态 "Active"

  Scenario: 按名称搜索客户
    When 我在搜索框中输入 "Tencent Holdings"
    And 我点击搜索按钮
    Then 搜索结果应该包含名称为 "Tencent Holdings Limited" 的客户

  Scenario: 按位置筛选客户
    When 我选择客户位置筛选器为 "中国"
    And 我点击搜索按钮
    Then 搜索结果中所有项目都应该位于 "中国"

  Scenario: 按状态筛选客户
    When 我选择状态筛选器为 "Active"
    And 我点击搜索按钮
    Then 搜索结果中所有项目都应该是 "Active" 状态

  Scenario: 重置搜索筛选条件
    When 我在搜索框中输入 "Temp Client"
    And 我选择客户位置筛选器为 "中国"
    And 我选择状态筛选器为 "Inactive"
    And 我点击重置按钮
    Then 搜索框应该被清空
    And 客户位置筛选器应该重置为默认值
    And 状态筛选器应该重置为默认值

  Scenario: 编辑现有客户
    Given 存在一个名为 "Editable Client" 的客户
    When 我点击该客户的编辑按钮
    Then 编辑客户对话框应该打开
    When 我将客户描述修改为 "更新后的客户描述"
    And 我点击更新按钮
    Then 表格中应该显示客户描述 "更新后的客户描述"

  Scenario: 停用和激活客户
    Given 存在一个名为 "Toggle Status Client" 的激活状态客户
    When 我点击该客户的停用按钮
    Then 该客户的状态应该变为 "Inactive"
    When 我点击该客户的激活按钮
    Then 该客户的状态应该变为 "Active"

  Scenario: 删除客户
    Given 存在一个名为 "Disposable Client" 的客户
    When 我点击该客户的删除按钮
    And 我确认删除操作
    Then 该客户应该从表格中消失

  Scenario: 验证创建客户时的必填字段
    When 我点击创建客户按钮
    Then 创建客户对话框应该打开
    When 我不填写任何字段直接点击创建按钮
    Then 应该显示 "请输入客户名称" 错误提示
    And 应该显示 "请输入客户代码" 错误提示
    And 应该显示 "请选择位置" 错误提示

  Scenario: 显示无数据提示
    When 我搜索一个不存在的客户 "NonExistentClient999"
    And 我点击搜索按钮
    Then 应该显示无数据提示或空表格
