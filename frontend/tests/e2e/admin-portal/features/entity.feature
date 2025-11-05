Feature: 实体管理功能
  作为管理员
  我希望能够管理实体
  以便维护系统中的实体信息

  Background:
    Given 系统已初始化
    And 我以管理员身份登录系统
    When 我访问实体管理页面

  Scenario: 创建新的实体
    When 我点击创建实体按钮
    Then 创建实体对话框应该打开
    When 我填写实体名称为 "Test Entity"
    And 我选择实体类型为 "Client"
    And 我填写实体描述为 "This is a test entity for automation testing"
    And 我点击创建按钮
    Then 新创建的实体应该出现在表格中
    And 表格中应该显示实体名称 "Test Entity"
    And 表格中应该显示实体类型 "Client"

  Scenario: 按名称搜索实体
    When 我在搜索框中输入 "BIPO"
    And 我点击搜索按钮
    Then 搜索结果应该包含名称为 "BIPO" 的实体

  Scenario: 按实体类型筛选
    When 我选择实体类型筛选器为 "Vendor"
    And 我点击搜索按钮
    Then 搜索结果中所有项目都应该是 "Vendor" 类型

  Scenario: 按状态筛选实体
    When 我选择状态筛选器为 "Active"
    And 我点击搜索按钮
    Then 搜索结果中所有项目都应该是 "Active" 状态

  Scenario: 重置搜索筛选条件
    When 我在搜索框中输入 "test"
    And 我选择实体类型筛选器为 "Client"
    And 我点击重置按钮
    Then 搜索框应该被清空
    And 实体类型筛选器应该重置为默认值

  Scenario: 编辑现有实体
    Given 存在一个名为 "Edit Test Entity" 的实体
    When 我点击该实体的编辑按钮
    Then 编辑实体对话框应该打开
    When 我将实体描述修改为 "Updated description"
    And 我点击更新按钮
    Then 表格中应该显示更新后的实体描述 "Updated description"

  Scenario: 停用和激活实体
    Given 存在一个名为 "Status Test Entity" 的激活状态实体
    When 我点击该实体的停用按钮
    Then 该实体的状态应该变为 "Inactive"
    When 我点击该实体的激活按钮
    Then 该实体的状态应该变为 "Active"

  Scenario: 删除实体
    Given 存在一个名为 "Delete Test Entity" 的实体
    When 我点击该实体的删除按钮
    And 我确认删除操作
    Then 该实体应该从表格中消失

  Scenario: 验证创建实体时的必填字段
    When 我点击创建实体按钮
    Then 创建实体对话框应该打开
    When 我不填写任何字段直接点击创建按钮
    Then 应该显示 "Please enter entity name" 错误提示
    And 应该显示 "Please select entity type" 错误提示

  Scenario: 显示无数据提示
    When 我搜索一个不存在的实体 "NonExistentEntity12345"
    And 我点击搜索按钮
    Then 应该显示无数据提示或空表格
