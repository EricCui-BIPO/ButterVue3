Feature: 服务类型管理功能
  作为管理员
  我希望能够管理服务类型
  以便维护系统中的服务类型信息

  Background:
    Given 系统已初始化
    And 我以管理员身份登录系统
    When 我访问服务类型管理页面

  Scenario: 验证不能创建重复的服务类型
    When 我点击创建服务类型按钮
    Then 创建服务类型对话框应该打开
    When 我填写服务类型名称为 "Duplicate EOR Test"
    And 我选择服务类型为 "EOR"
    And 我填写描述为 "This should fail due to duplicate type"
    And 我点击创建按钮
    Then 应该显示重复服务类型的错误提示

  Scenario: 按名称搜索服务类型
    When 我在搜索框中输入 "Employer of Record"
    And 我点击搜索按钮
    Then 搜索结果应该包含名称为 "Employer of Record" 的服务类型

  Scenario: 按服务类型筛选
    When 我选择服务类型筛选器为 "EOR"
    And 我点击搜索按钮
    Then 搜索结果中所有项目都应该是 "EOR" 类型

  Scenario: 按状态筛选服务类型
    When 我选择状态筛选器为 "Active"
    And 我点击搜索按钮
    Then 搜索结果中所有项目都应该是 "Active" 状态

  Scenario: 重置搜索筛选条件
    When 我在搜索框中输入 "test"
    And 我选择服务类型筛选器为 "EOR"
    And 我点击重置按钮
    Then 搜索框应该被清空
    And 服务类型筛选器应该重置为默认值

  Scenario: 编辑现有服务类型
    Given 系统中存在 "Employer of Record" 服务类型
    When 我点击该服务类型的编辑按钮
    Then 编辑服务类型对话框应该打开
    When 我将描述修改为 "Updated description for automation testing"
    And 我点击更新按钮
    Then 服务类型描述应该更新成功

  Scenario: 停用和激活服务类型
    Given 系统中存在 "Employer of Record" 服务类型
    When 我点击该服务类型的停用按钮
    Then 该服务类型的状态应该变为 "Inactive"
    When 我点击该服务类型的激活按钮
    Then 该服务类型的状态应该变为 "Active"

  @skip
  Scenario: 删除服务类型
    # 跳过删除测试，避免破坏系统中仅有的4条服务类型记录
    # Service Type 有业务约束：每种类型只能存在一条记录
    Given 系统中存在 "Employer of Record" 服务类型
    When 我点击该服务类型的删除按钮
    And 我确认删除操作
    Then 该服务类型应该从表格中消失

  Scenario: 验证创建服务类型时的必填字段
    When 我点击创建服务类型按钮
    Then 创建服务类型对话框应该打开
    When 我不填写任何字段直接点击创建按钮
    Then 应该显示 "Please enter service type name" 错误提示
    And 应该显示 "Please select service type" 错误提示

  Scenario: 显示无数据提示
    When 我搜索一个不存在的服务类型 "NonExistentServiceType12345"
    And 我点击搜索按钮
    Then 应该显示无数据提示或空表格