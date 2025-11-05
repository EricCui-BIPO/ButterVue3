Feature: 报表管理功能
  作为管理员
  我希望能够查看和分析报表数据
  以便了解系统运营状况

  Background:
    Given 系统已初始化
    And 我以管理员身份登录系统

  # 场景1: 报表列表基本功能
  Scenario: 查看报表列表页面
    When 我访问报表管理页面
    Then 页面应该正确加载
    And 应该显示报表分类
    And 每个报表卡片应该包含基本信息

  # 场景2: 饼图报表验证（核心场景）
  Scenario: 验证饼图报表的数据结构
    When 我访问报表管理页面
    And 我点击第一个包含"饼图"的报表
    Then "饼图"应该成功渲染
    And 饼图配置应该符合 PieChartData 接口规范
    And 饼图数据项应该包含 name 和 value 字段

  # 场景3: 折线图报表验证
  Scenario: 验证折线图报表的数据结构
    When 我访问报表管理页面
    And 我点击第一个包含"折线图"的报表
    Then "折线图"应该成功渲染
    And 折线图配置应该符合 XYChartData 接口规范
    And x轴和y轴数据长度应该一致

  # 场景4: 柱状图报表验证
  Scenario: 验证柱状图报表的数据结构
    When 我访问报表管理页面
    And 我点击第一个包含"柱状图"的报表
    Then "柱状图"应该成功渲染
    And 柱状图配置应该符合 XYChartData 接口规范


