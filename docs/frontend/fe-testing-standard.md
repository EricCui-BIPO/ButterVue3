# 前端测试指南

## 概述

本指南提供了前端项目的测试最佳实践，包括单元测试、组件测试和端到端测试的规范。遵循项目开发标准，确保代码质量和可维护性。

**相关文档**: [前端研发规约](./fe-standards.md)

## 测试架构

### 主要框架

- **Vitest 1.0.4**: 现代化测试框架，基于 Vite 构建
- **Vue Test Utils 2.4.3**: Vue.js 官方测试工具库
- **Playwright 1.55.0**: 端到端测试框架
- **覆盖率工具**: V8 provider，支持 HTML/JSON/XML 报告

### 配置文件

- `configs/vitest.coverage.config.ts`: 覆盖率测试配置
- `playwright.config.ts`: E2E 测试配置
- 测试目录按模块组织：`test/` 或 `tests/e2e/`

## 测试组织原则

### 测试文件位置
- **共享模块**: 在模块同级目录创建 `__tests__/` 或使用项目级 test 目录
- **页面组件**: 在页面目录下创建 `__tests__/` 子目录
- **E2E 测试**: 统一放在 `tests/e2e/` 目录，按门户组织

### 测试优先级
1. **共享库模块**: 必须完整测试覆盖（90%+）
2. **业务组件**: 重点测试核心逻辑（80%+）
3. **页面组件**: 测试关键交互和集成
4. **工具函数**: 100% 覆盖率

## 测试分类

### 1. 组件测试 (Component Tests)

- **目标**: 测试 Vue 组件的渲染、交互和状态管理
- **范围**: 单个组件的功能验证
- **重点**: Props 传递、事件触发、计算属性、生命周期

### 2. 工具函数测试 (Utility Tests)

- **目标**: 测试纯函数和工具方法
- **范围**: 独立的业务逻辑函数
- **重点**: 输入输出验证、边界条件、异常处理

### E2E测试精选

端到端测试仅覆盖核心业务场景，确保关键用户旅程：
- 用户注册和登录流程
- 关键业务流程（如订单处理、支付流程）
- 跨页面导航和数据一致性
- 关键功能的端到端验证

### 智能场景选择

通过算法自动从集成测试用例中识别核心场景：
- 基于用户访问频率和重要性评分
- 识别涉及多个模块的复杂交互
- 覆盖关键业务路径和决策点
- 支持手动标记核心场景优先级

## 目录结构

```
e2e-tests/
├── features/                    # Gherkin特性文件
│   ├── integration/            # 集成测试特性
│   │   ├── admin-portal/       # 管理端集成测试
│   │   ├── client-portal/      # 客户端集成测试
│   │   ├── service-portal/     # 服务端集成测试
│   │   └── talent-portal/      # 人才端集成测试
│   └── e2e/                    # E2E测试特性
│       ├── core-scenarios/     # 核心场景
│       └── smoke-tests/        # 冒烟测试
├── step-definitions/           # 步骤定义
│   ├── common/                 # 通用步骤
│   ├── integration/            # 集成测试步骤
│   └── e2e/                    # E2E测试步骤
├── support/                    # 支持文件
│   ├── world.js               # Cucumber World配置
│   ├── hooks.js               # 生命周期钩子
│   ├── data-factory/          # 测试数据工厂
│   └── page-objects/          # 页面对象模型
├── config/                     # 配置文件
│   ├── cucumber.js            # Cucumber配置
│   ├── playwright.config.js   # Playwright配置
│   └── environments/          # 环境配置
└── reports/                   # 测试报告
    ├── cucumber/              # Cucumber报告
    └── playwright/            # Playwright报告

## 数据自动化管理

### 测试数据工厂

采用工厂模式自动创建和管理测试数据：
- **用户数据工厂**: 创建不同角色的测试用户
- **业务数据工厂**: 生成业务相关的测试数据
- **关联数据工厂**: 处理复杂的数据关联关系
- **清理策略**: 自动清理测试产生的数据

### 数据隔离策略

- **测试环境隔离**: 独立的测试数据库和环境
- **数据版本管理**: 支持不同版本的数据结构
- **并行执行**: 支持多测试用例并行执行
- **数据一致性**: 确保测试数据的状态一致性

## 智能场景识别

### 核心场景选择算法

```
场景重要性评分 = 用户访问频率 × 业务重要性 × 技术复杂度
```

**选择标准**:
- 评分高于阈值的场景自动纳入E2E测试
- 涉及关键业务路径的强制纳入
- 跨多个模块的复杂交互优先
- 支持手动调整和业务专家标注

### 场景分类标签

- **P0-核心**: 必须覆盖的关键业务流程
- **P1-重要**: 高频使用的核心功能
- **P2-一般**: 常规业务场景
- **P3-边缘**: 低频或辅助功能

## 测试执行策略

### 分层执行

1. **集成测试**: 每次代码提交触发，快速验证
2. **E2E核心场景**: 定时执行或关键版本发布前
3. **完整E2E**: 重大版本发布或定期全量回归

### 并行优化

- **场景级并行**: 独立的E2E场景并行执行
- **数据隔离**: 并行执行时的数据冲突处理
- **资源调度**: 动态分配测试资源
- **失败重试**: 智能重试机制提高稳定性

## 覆盖率标准

### 业务覆盖率

- **用户故事覆盖率**: ≥95%
- **业务场景覆盖率**: ≥90%
- **核心路径覆盖率**: 100%
- **异常流程覆盖率**: ≥80%

### 技术覆盖率

- **前端路由覆盖率**: ≥95%
- **API接口覆盖率**: ≥90%
- **关键组件交互**: 100%
- **跨端兼容性**: 100%

## 报告与分析

### 多维度报告

- **业务视角**: 按业务模块和场景分类
- **技术视角**: 按技术栈和组件分类
- **质量趋势**: 历史数据对比和趋势分析
- **风险评估**: 未覆盖区域的风险评估

### 实时监控

- **测试执行状态**: 实时显示测试进度
- **失败通知**: 及时通知测试失败信息
- **性能监控**: 测试执行时间和资源消耗
- **质量仪表板**: 可视化质量指标

## BDD最佳实践

### Gherkin语法规范

使用统一的Gherkin语法描述业务场景：

```gherkin
Feature: 用户登录功能
  作为系统用户
  我想要能够安全登录系统
  以便访问我的个人信息

  Background:
    Given 系统已初始化
    And 存在测试用户:
      | username | email             | password |
      | testuser | test@example.com  | 123456   |

  Scenario: 成功登录
    Given 我在登录页面
    When 输入用户名 "testuser"
    And 输入密码 "123456"
    And 点击登录按钮
    Then 应该跳转到用户仪表板
    And 应该显示欢迎消息 "欢迎回来, testuser"

  Scenario Outline: 登录验证
    Given 我在登录页面
    When 输入用户名 "<username>"
    And 输入密码 "<password>"
    And 点击登录按钮
    Then 应该显示错误消息 "<error_message>"

    Examples:
      | username | password | error_message     |
      | wronguser| 123456   | 用户名或密码错误 |
      | testuser | wrongpass| 用户名或密码错误 |
      |          | 123456   | 请输入用户名     |
```

### 步骤定义最佳实践

```javascript
// step-definitions/common/auth.steps.js
import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';

Given('我在登录页面', async function() {
  await this.page.goto('/login');
  await expect(this.page).toHaveURL('/login');
});

When('输入用户名 {string}', async function(username) {
  await this.page.fill('[data-testid="username"]', username);
});

When('输入密码 {string}', async function(password) {
  await this.page.fill('[data-testid="password"]', password);
});

When('点击登录按钮', async function() {
  await this.page.click('[data-testid="login-button"]');
});

Then('应该跳转到用户仪表板', async function() {
  await expect(this.page).toHaveURL('/dashboard');
});

Then('应该显示欢迎消息 {string}', async function(message) {
  await expect(this.page.locator('[data-testid="welcome-message"]')).
    toHaveText(message);
});
```

### 数据工厂模式

```javascript
// support/data-factory/user.factory.js
export class UserFactory {
  static async createAdminUser(overrides = {}) {
    const defaultData = {
      username: `admin_${Date.now()}`,
      email: `admin_${Date.now()}@test.com`,
      password: 'Test123!@#',
      role: 'admin',
      isActive: true
    };
    
    const userData = { ...defaultData, ...overrides };
    return await this.createUser(userData);
  }

  static async createClientUser(overrides = {}) {
    const defaultData = {
      username: `client_${Date.now()}`,
      email: `client_${Date.now()}@test.com`,
      password: 'Test123!@#',
      role: 'client',
      isActive: true
    };
    
    const userData = { ...defaultData, ...overrides };
    return await this.createUser(userData);
  }

  static async cleanupUsers() {
    // 清理测试创建的用户数据
    await this.deleteTestUsers();
  }
}
```

### 页面对象模式

```javascript
// support/page-objects/LoginPage.js
export class LoginPage {
  constructor(page) {
    this.page = page;
    this.usernameInput = '[data-testid="username"]';
    this.passwordInput = '[data-testid="password"]';
    this.loginButton = '[data-testid="login-button"]';
    this.errorMessage = '[data-testid="error-message"]';
  }

  async navigate() {
    await this.page.goto('/login');
  }

  async login(username, password) {
    await this.page.fill(this.usernameInput, username);
    await this.page.fill(this.passwordInput, password);
    await this.page.click(this.loginButton);
  }

  async getErrorMessage() {
    return await this.page.textContent(this.errorMessage);
  }
}
```

### 场景标签管理

```gherkin
@integration @admin @user-management
Feature: 管理员用户管理
  作为系统管理员
  我想要能够管理用户账户
  以便维护系统安全

  @core @smoke
  Scenario: 创建新用户
    Given 我以管理员身份登录
    When 我访问用户管理页面
    And 点击创建用户按钮
    And 填写用户信息
    And 提交表单
    Then 应该显示用户创建成功消息
    And 新用户应该出现在用户列表中

  @boundary @negative
  Scenario: 创建用户时邮箱已存在
    Given 系统中已存在邮箱 "test@example.com"
    When 我尝试创建相同邮箱的新用户
    Then 应该显示邮箱已存在错误
```

## 配置管理

### Cucumber配置

```javascript
// test/shared/components/SharedButton.test.js
describe("SharedButton", () => {
  it("should work in all portals", () => {
    // 测试共享组件在各端的兼容性
  });
});

// test/admin-portal/pages/Dashboard.test.js
describe("AdminPortal - Dashboard", () => {
  it("should use shared components correctly", () => {
    // 测试管理端页面使用共享组件
  });
});
```

## 覆盖率要求

### 覆盖率标准
- **共享库模块**: 90% 以上覆盖率
- **业务组件**: 80% 以上覆盖率
- **工具函数**: 100% 覆盖率
- **页面组件**: 按需测试核心功能

### 覆盖率配置
项目使用 V8 provider，已在 `configs/vitest.coverage.config.ts` 中配置。

### 运行测试
```bash
# 运行所有测试
yarn test

# 生成覆盖率报告
yarn test:coverage

# E2E 测试
yarn test:e2e
```

## 快速开始

### 1. 组件测试模板

```typescript
// __tests__/Component.test.ts
import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import Component from '../Component.vue'

describe('Component', () => {
  let wrapper

  beforeEach(() => {
    wrapper = mount(Component)
  })

  it('should render correctly', () => {
    expect(wrapper.exists()).toBe(true)
  })

  it('should handle user interaction', async () => {
    await wrapper.find('button').trigger('click')
    expect(wrapper.emitted()).toHaveProperty('click')
  })
})
```

### 2. Mock 外部依赖

```typescript
vi.mock('@I0/shared/api/domains/entity', () => ({
  entityAPI: {
    getEntities: vi.fn(() => Promise.resolve({ success: true, data: [] }))
  }
}))
```

### 3. 测试 Composables

```typescript
import { useFeatureManagement } from '../composables/useFeature'

describe('useFeatureManagement', () => {
  it('should load features correctly', async () => {
    const { features, loadFeatures } = useFeatureManagement()
    await loadFeatures()
    expect(Array.isArray(features.value)).toBe(true)
  })
})
```

## 相关资源

- [Vitest 文档](https://vitest.dev/)
- [Vue Test Utils 文档](https://test-utils.vuejs.org/)
- [Playwright 文档](https://playwright.dev/)