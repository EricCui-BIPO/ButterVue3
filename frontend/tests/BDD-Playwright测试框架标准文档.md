# BDD+Playwright 测试框架标准文档

## 📋 目录

1. [框架概述](#框架概述)
2. [技术栈](#技术栈)
3. [目录结构](#目录结构)
4. [配置说明](#配置说明)
5. [BDD 实现规范](#bdd-实现规范)
6. [Page Object 模式](#page-object-模式)
7. [测试数据管理](#测试数据管理)
8. [报告系统](#报告系统)
9. [运行命令](#运行命令)
10. [最佳实践](#最佳实践)

---

## 框架概述

本框架是基于 **Cucumber BDD + Playwright** 构建的端到端测试解决方案，专为多门户应用设计。框架支持中文场景描述，提供完整的测试数据管理、自动化清理和详细的测试报告功能。

### 核心特性

- ✅ **多门户支持**: Admin、Client、Service、Talent 四大门户
- ✅ **中文 BDD**: 支持中文 Feature 文件和步骤定义
- ✅ **自动数据管理**: 测试数据自动创建、验证和清理
- ✅ **完整报告**: Allure 报告 + 截图 + 视频 + Trace
- ✅ **调试支持**: 多种调试模式和追踪功能
- ✅ **模块化设计**: Page Object 模式和步骤复用

---

## 技术栈

### 核心依赖

```json
{
  "@cucumber/cucumber": "^12.2.0",
  "@playwright/test": "^1.55.0",
  "allure-cucumberjs": "^3.4.1",
  "allure-commandline": "^2.34.1",
  "ts-node": "^10.9.2",
  "typescript": "~5.3.3"
}
```

### 测试工具链

- **BDD 框架**: Cucumber.js
- **浏览器自动化**: Playwright
- **测试报告**: Allure Report
- **语言支持**: TypeScript
- **运行环境**: Node.js 20+

---

## 目录结构

```
tests/e2e/
├── admin-portal/                   # 管理端测试
│   ├── features/                  # BDD Feature 文件
│   │   ├── clients.feature
│   │   ├── entity.feature
│   │   ├── reports.feature
│   │   └── service-type.feature
│   ├── page-objects/              # 管理端页面对象
│   │   ├── ClientPage.ts
│   │   ├── EntityPage.ts
│   │   ├── ReportPage.ts
│   │   └── ServiceTypePage.ts
│   └── step-definitions/          # 管理端步骤定义
│       ├── clients.steps.ts
│       ├── common.steps.ts
│       ├── reports.steps.ts
│       └── service-type.steps.ts
├── client-portal/                 # 客户端测试（预留）
├── service-portal/                # 服务端测试（预留）
├── talent-portal/                 # 人才端测试（预留）
├── config/                        # 测试环境与 Portal 配置
│   └── test-config.ts
├── shared/                        # 跨 Portal 复用层
│   ├── components/                # 通用 UI 组件封装
│   │   ├── ActionDropdownComponent.ts
│   │   ├── DialogComponent.ts
│   │   ├── FilterComponent.ts
│   │   ├── FormComponent.ts
│   │   └── TableComponent.ts
│   ├── models/
│   │   ├── CrudPageConfig.ts
│   │   └── TestDataModel.ts
│   ├── page-objects/
│   │   ├── BasePage.ts
│   │   └── GenericCrudPage.ts
│   ├── types/
│   └── utils/
│       ├── chart-validators.ts
│       ├── constants.ts
│       ├── interaction-helpers.ts
│       ├── logger.ts
│       ├── selectors.ts
│       └── wait-helpers.ts
├── support/                       # Cucumber 基础设施
│   ├── allure-reporter.js
│   ├── hooks.ts
│   ├── test-data-factory.ts
│   ├── test-data-manager.ts
│   └── world.ts
├── performance/                   # 性能专项测试（预留）
├── reports/                       # 测试输出
│   ├── allure-report/
│   ├── allure-results/
│   └── test-results/
│       ├── screenshots/
│       ├── traces/
│       └── videos/
├── workflows/                     # 端到端流程测试入口
├── tsconfig.cucumber.json         # Cucumber TypeScript 配置
└── ...                            # 其他端或实验模块
```

---

## 配置说明

### Cucumber 配置 (cucumber.config.js)

```javascript
module.exports = {
  default: {
    requireModule: ['ts-node/register'],
    require: [
      'tests/e2e/support/**/*.ts',
      'tests/e2e/**/step-definitions/**/*.ts'
    ],
    format: [
      'progress',  // 或 'pretty'
      'summary',
      'allure-cucumberjs/reporter'
    ],
    formatOptions: {
      snippetInterface: 'async-await',
      resultsDir: 'tests/e2e/reports/allure-results'
    },
    paths: ['tests/e2e/**/*.feature']
  },
  // 特定模块配置
  'admin-entity': {
    paths: ['tests/e2e/admin-portal/features/entity.feature'],
    // ... 其他配置
  }
};
```

### 环境配置 (test-config.ts)

```typescript
export const PORTAL_CONFIGS: Record<string, PortalConfig> = {
  admin: {
    name: '管理端',
    baseUrl: 'http://localhost:3003',
    loginPath: '/login',
    defaultUsername: 'admin',
    defaultPassword: 'admin123'
  },
  client: {
    name: '客户端',
    baseUrl: 'http://localhost:3001',
    // ...
  },
  // ... 其他门户配置
};
```

### TypeScript 配置 (tsconfig.cucumber.json)

```json
{
  "extends": "../../node_modules/@tsconfig/node18/tsconfig.json",
  "compilerOptions": {
    "module": "CommonJS",
    "target": "ES2020",
    "moduleResolution": "node",
    "resolveJsonModule": true,
    "esModuleInterop": true,
    "skipLibCheck": true,
    "strict": false,
    "types": ["node"],
    "baseUrl": "."
  },
  "include": ["./**/*.ts"]
}
```

---

## BDD 实现规范

### Feature 文件规范

#### 文件命名
- 使用小写字母和连字符: `entity-management.feature`
- 或使用驼峰命名: `entityManagement.feature`

#### 场景编写规范

```gherkin
# 语言标识
Feature: 实体管理功能
  作为管理员
  我希望能够管理实体
  以便维护系统中的实体信息

  # Background: 每个场景前都会执行的步骤
  Background:
    Given 系统已初始化
    And 我以管理员身份登录系统
    When 我访问实体管理页面

  # Scenario: 具体测试场景
  Scenario: 创建新的实体
    When 我点击创建实体按钮
    Then 创建实体对话框应该打开
    When 我填写实体名称为 "Test Entity"
    And 我选择实体类型为 "Client"
    And 我填写实体描述为 "This is a test entity"
    And 我点击创建按钮
    Then 新创建的实体应该出现在表格中
    And 表格中应该显示实体名称 "Test Entity"
    And 表格中应该显示实体类型 "Client"

  # 使用 @skip 跳过场景
  @skip
  Scenario: 删除实体
    # 删除测试，避免破坏系统数据
    # ... 步骤定义

  # 使用 Given 定义前置条件
  Scenario: 编辑现有实体
    Given 存在一个名为 "Edit Test Entity" 的实体
    When 我点击该实体的编辑按钮
    # ... 后续步骤
```

### 步骤定义规范

#### 步骤定义结构

```typescript
import { Given, When, Then } from '@cucumber/cucumber';
import { expect } from '@playwright/test';
import type { I0World } from '../../support/world';

When('我填写实体名称为 {string}', async function (this: I0World, entityName: string) {
  // 自动生成唯一测试数据名称
  const uniqueEntityName = this.generateUniqueTestName(entityName);
  await this.entityPage.dialogEntityNameInput.fill(uniqueEntityName);
  // 保存到实例变量
  this.entityName = uniqueEntityName;
});

Then('应该显示 {string} 错误提示', async function (this: I0World, errorMessage: string) {
  await expect(this.page.locator('.el-message--error')).toContainText(errorMessage);
});
```

#### 步骤重用原则

- 通用步骤放在 `common.steps.ts` 中
- 模块特定步骤放在对应的步骤文件中
- 避免重复定义相同功能的步骤

---

## Page Object 模式

### 基础页面类 (BasePage)

```typescript
export abstract class BasePage implements TestDataCleanupCapable {
  readonly page: Page;
  protected readonly portalConfig: PortalConfig;

  readonly usernameInput: Locator;
  readonly passwordInput: Locator;
  readonly loginButton: Locator;

  constructor(page: Page, portalKey?: string) {
    this.page = page;
    this.portalConfig = portalKey ? getPortalConfig(portalKey) : getCurrentPortalConfig();
    this.usernameInput = page.locator('[data-testid="username"]');
    this.passwordInput = page.locator('[data-testid="password"]');
    this.loginButton = page.locator('[data-testid="login-button"]');
  }

  async initializeSystem(): Promise<void> {
    await this.page.goto(this.portalConfig.baseUrl);
    await this.page.waitForLoadState('networkidle');
  }

  async loginAsAdmin(): Promise<void> {
    await this.page.goto(`${this.portalConfig.baseUrl}${this.portalConfig.loginPath}`);
    await this.usernameInput.fill(this.portalConfig.defaultUsername);
    await this.passwordInput.fill(this.portalConfig.defaultPassword);
    await this.loginButton.click();
    await this.page.waitForURL('**/dashboard');
  }

  async login(username: string, password: string): Promise<void> {
    await this.page.goto(`${this.portalConfig.baseUrl}${this.portalConfig.loginPath}`);
    await this.usernameInput.fill(username);
    await this.passwordInput.fill(password);
    await this.loginButton.click();
    await this.page.waitForURL('**/dashboard');
  }

  async waitForPageLoad(timeout: number = 10000): Promise<void> {
    await this.page.waitForLoadState('networkidle', { timeout });
  }

  abstract cleanupTestData(dataName: string): Promise<void>;
  abstract cleanupTestEntities(dataNames: string[]): Promise<void>;
  abstract cleanupTestEntitiesBySuffix(suffix: string): Promise<void>;
}
```

> 关键点：基类自动解析多门户配置，默认读取当前场景的 Portal；如需跨 Portal 复用，可在构造函数中显式传入 `portalKey`。

### CRUD 操作接口

```typescript
export interface CrudPageOperations {
  // 搜索与筛选
  getSearchInput(): Locator;
  searchByName(name: string): Promise<void>;
  filterByStatus(status: string): Promise<void>;
  filterByType(type: string): Promise<void>;
  resetSearch(): Promise<void>;

  // CRUD 操作
  openCreateDialog(): Promise<void>;
  create(name: string, type: string, description?: string): Promise<void>;
  edit(dataName: string, newName?: string, newDescription?: string): Promise<void>;
  delete(dataName: string): Promise<void>;
  deleteWithConfirmation(dataName: string): Promise<void>;
  activate(dataName: string): Promise<void>;
  deactivate(dataName: string): Promise<void>;

  // 表单与按钮
  clickCreateButton(): Promise<void>;
  clickUpdateButton(): Promise<void>;
  clickSearchButton(): Promise<void>;
  attemptCreateEmpty(): Promise<void>;

  // 验证能力
  verifyExists(dataName: string): Promise<void>;
  verifyNotExists(dataName: string): Promise<void>;
  verifyStatus(dataName: string, status: string): Promise<void>;
  verifyAllResultsAreOfType(type: string): Promise<void>;
  verifyAllResultsAreOfStatus(status: string): Promise<void>;
  verifyNoDataMessage(message: string): Promise<void>;
  verifyNoDataState(): Promise<void>;
  verifyFormValidationError(errorMessage: string): Promise<void>;
  verifySearchResultContains(dataName: string): Promise<void>;
  verifySearchFormReset(): Promise<void>;

  // 其他支撑
  waitForTableData(): Promise<void>;
  goToNextPage(): Promise<void>;
  goToPreviousPage(): Promise<void>;
  verifyPageChanged(): Promise<void>;
  verifyDialogClosed(): Promise<void>;
  createActiveDataForTesting(dataName: string): Promise<void>;
  cleanupTestData(dataName: string): Promise<void>;

  // 组件访问
  dialog: any; // 暴露对话框组件以支持通用步骤
}
```

### 具体页面对象示例

```typescript
export class EntityPage extends GenericCrudPage {
  constructor(page: Page) {
    super(page, getCrudPageConfig('entity'));
  }

  // 只保留实体特有的校验或业务逻辑
  async verifyDuplicateTypeRule(): Promise<void> {
    await this.dialog.verifyValidationError('Please select entity type');
  }
}
```

---

## 测试数据管理

### 测试数据管理器

```typescript
export type TestDataResourceType =
  | 'entity'
  | 'serviceType'
  | 'client'
  | 'location'
  | 'service'
  | 'talent'
  | 'tenant';

export class TestDataManager {
  private records: TestDataRecord[] = [];
  private readonly testSessionId: string;

  constructor() {
    this.testSessionId = Date.now().toString();
  }

  // 记录测试数据
  recordData(type: TestDataResourceType, name: string, verified: boolean = false): void {
    const record: TestDataRecord = {
      type,
      name,
      createdAt: Date.now(),
      verified
    };
    this.records.push(record);
  }

  // 验证数据创建成功
  verifyDataCreated(type: TestDataResourceType, name: string): void {
    const record = this.records.find(r => r.type === type && r.name === name);
    if (record) {
      record.verified = true;
    }
  }

  // 获取指定类型的测试数据
  getRecordsByType(type: TestDataResourceType): TestDataRecord[] {
    return this.records.filter(record => record.type === type);
  }

  // 获取所有已验证的数据
  getVerifiedRecords(): TestDataRecord[] {
    return this.records.filter(record => record.verified);
  }
}
```

### World 类中的数据管理

```typescript
export class I0World extends World<WorldParameters> {
  testDataManager: TestDataManager;
  testDataSuffix?: string; // 数据隔离后缀

  constructor(options: IWorldOptions<WorldParameters>) {
    super(options);
    this.testDataSuffix = Date.now().toString();
    this.testDataManager = new TestDataManager();
  }

  // 生成唯一测试数据名称
  generateUniqueTestName(baseName: string): string {
    return `${baseName}_${this.testDataSuffix}`;
  }

  // 统一清理所有测试数据
  async cleanupAllTestData(): Promise<void> {
    const retainTestData = process.env.RETAIN_TEST_DATA?.toLowerCase() === 'true';
    if (retainTestData) {
      console.log('📌 调试模式: 保留测试数据，跳过清理');
      return;
    }

    const verifiedRecords = this.getVerifiedTestData();
    const recordsByType = verifiedRecords.reduce((acc, record) => {
      if (!acc[record.type]) acc[record.type] = [];
      acc[record.type].push(record.name);
      return acc;
    }, {} as Record<TestDataResourceType, string[]>);

    // 按类型清理数据
    for (const [type, names] of Object.entries(recordsByType)) {
      await this.cleanupTestDataByType(type as TestDataResourceType, names);
    }

    this.clearTestDataRecords();
  }
}
```

---

## 报告系统

### Allure 报告集成

#### 配置
```javascript
// cucumber.config.js
format: [
  'progress',
  'summary',
  'allure-cucumberjs/reporter'
],
formatOptions: {
  snippetInterface: 'async-await',
  resultsDir: 'tests/e2e/reports/allure-results'
}
```

#### 自动截图和视频

```typescript
// hooks.ts - After 钩子
After(async function (this: I0World, { result, pickle }) {
  const scenarioName = pickle?.name ?? 'Unknown Scenario';

  // 失败时截图
  if (result?.status === Status.FAILED && this.page) {
    const screenshotPath = buildArtifactPath(
      SCREENSHOT_DIR,
      'screenshot',
      scenarioName,
      'png'
    );
    const screenshotBuffer = await this.page.screenshot({
      path: screenshotPath,
      fullPage: true
    });
    this.attach(screenshotBuffer, 'image/png');
  }

  // Trace 记录
  if (enableTrace && this.context) {
    const tracePath = buildArtifactPath(TRACE_DIR, 'trace', scenarioName, 'zip');
    await this.context.tracing.stop({ path: tracePath });

    if (result?.status === Status.FAILED) {
      const traceBuffer = readFileSync(tracePath);
      this.attach(traceBuffer, 'application/zip');
    }
  }

  // 视频录制
  if (this.page && this.page.video()) {
    await this.page.close();
    const video = this.page.video();
    const videoPath = video ? await video.path() : undefined;

    if (videoPath && result?.status === Status.FAILED) {
      const videoBuffer = readFileSync(videoPath);
      this.attach(videoBuffer, 'video/webm');
    }
  }
});
```

### 报告生成器

```javascript
// support/allure-reporter.js
class AllureReporter {
  constructor() {
    this.allureResultsDir = path.join(__dirname, '../reports/allure-results');
    this.allureReportDir = path.join(__dirname, '../reports/allure-report');
  }

  async generateFullReport() {
    console.log('🚀 开始 Allure 报告生成流程...');

    this.ensureDirectories();

    const stats = this.getReportStats();
    if (!stats.hasResults) {
      console.log('⚠️ 没有测试结果，无法生成报告');
      return false;
    }

    const success = this.generateReport();

    if (success) {
      console.log('✅ Allure 报告生成完成！');
      console.log(`📁 报告位置: ${this.allureReportDir}`);
      console.log(`🌐 查看报告: npx allure open ${this.allureReportDir}`);
    }

    return success;
  }
}
```

---

## 运行命令

### 基础测试命令

```bash
# 运行所有 BDD 测试
yarn test:bdd

# 运行特定模块测试
yarn test:bdd:admin-entity          # 实体管理测试
yarn test:bdd:admin-service-type    # 服务类型测试

# 美化输出格式
yarn test:bdd:pretty

# 调试模式
yarn test:bdd:debug                 # 启用调试模式
yarn test:bdd:pwdebug              # Playwright 调试模式
yarn test:bdd:pwdebug:service-type # 特定模块调试
```

### 高级功能命令

```bash
# 启用 Trace 记录
yarn test:bdd:trace

# 启用视频录制
yarn test:bdd:video

# 启用所有高级功能
yarn test:bdd:full                  # Trace + Video

# 生成报告
yarn test:bdd:report                # 生成 Allure 报告
yarn test:bdd:allure:open           # 打开报告
yarn test:bdd:allure:serve          # 启动报告服务

# 运行测试并生成报告
yarn test:bdd:run-and-report
```

### 环境变量配置

```bash
# 浏览器配置
BROWSER=chromium                    # chromium, firefox, webkit
HEADLESS=false                      # 是否无头模式
SLOW_MO=1000                        # 慢速执行（毫秒）

# 调试配置
DEBUG_MODE=true                     # 调试模式
PWDEBUG=1                          # Playwright 调试
CODEGEN_MODE=true                   # 代码生成模式

# 功能开关
ENABLE_TRACE=true                   # 启用 Trace 记录
ENABLE_VIDEO=true                   # 启用视频录制
RETAIN_TEST_DATA=true              # 保留测试数据
AUTO_REPORT=true                   # 自动生成报告

# 输出配置
CUCUMBER_PROGRESS_FORMAT=pretty     # pretty, progress-bar, progress
E2E_VERBOSE_LOGS=true              # 详细日志
```

---

## 最佳实践

### 1. Feature 文件编写

#### ✅ 推荐做法
- 使用业务语言描述场景，避免技术细节
- 保持场景简洁，每个场景测试一个功能点
- 使用 Background 设置通用前置条件
- 合理使用数据表和示例

```gherkin
Feature: 用户管理
  Background:
    Given 系统已初始化
    And 我以管理员身份登录

  Scenario: 创建用户成功
    When 我填写用户信息
      | 字段      | 值              |
      | 姓名      | 张三            |
      | 邮箱      | zhang@test.com  |
      | 角色      | 普通用户        |
    And 我点击创建按钮
    Then 用户创建成功
    And 列表显示新用户
```

#### ❌ 避免做法
- 在 Feature 文件中包含具体的选择器或 XPath
- 场景过于复杂，测试多个功能点
- 使用硬编码的测试数据

### 2. 步骤定义实现

#### ✅ 推荐做法
- 步骤定义简洁明了，易于理解
- 使用页面对象封装页面操作
- 添加适当的错误处理和等待

```typescript
When('我填写用户信息', async function (this: I0World, dataTable: DataTable) {
  const data = dataTable.rowsHash();
  await this.userPage.fillUserForm(data);
});

Then('用户创建成功', async function (this: I0World) {
  await expect(this.page.locator('.success-message')).toBeVisible();
  await this.userPage.waitForUserList();
});
```

#### ❌ 避免做法
- 在步骤定义中直接操作 DOM 元素
- 使用固定的等待时间
- 步骤定义过于复杂，包含多个操作

### 3. 测试数据管理

#### ✅ 推荐做法
- 使用唯一后缀避免数据冲突
- 及时清理测试数据
- 使用测试数据管理器统一管理

```typescript
// 生成唯一测试数据
const uniqueName = this.generateUniqueTestName('Test User');

// 记录测试数据
this.recordTestData('user', uniqueName);

// 验证创建成功
this.verifyTestDataCreated('user', uniqueName);
```

#### ❌ 避免做法
- 使用硬编码的测试数据名称
- 不清理测试数据，污染测试环境
- 手动管理测试数据，容易遗漏

### 4. 页面对象设计

#### ✅ 推荐做法
- 遵循单一职责原则
- 提供高级别的业务方法
- 使用语义化的方法名

```typescript
export class UserPage extends BasePage {
  async createUser(userInfo: UserInfo): Promise<void> {
    await this.clickCreateButton();
    await this.fillUserForm(userInfo);
    await this.clickSaveButton();
    await this.waitForCreateSuccess();
  }

  async searchUser(keyword: string): Promise<void> {
    await this.searchInput.fill(keyword);
    await this.clickSearchButton();
    await this.waitForSearchResults();
  }
}
```

#### ❌ 避免做法
- 页面对象包含太多职责
- 只提供底层的元素操作方法
- 方法名不够语义化

### 5. 错误处理和调试

#### ✅ 推荐做法
- 添加清晰的错误消息
- 使用适当的等待策略
- 启用调试功能辅助排查

```typescript
// 等待元素可见
await expect(element).toBeVisible({ timeout: 10000 });

// 添加调试信息
console.log(`🔍 等待元素可见: ${element}`);

// 失败时截图
if (result?.status === Status.FAILED) {
  await this.page.screenshot({ path: 'error.png', fullPage: true });
}
```

### 6. 维护和扩展

#### ✅ 推荐做法
- 定期重构测试代码
- 添加必要的注释和文档
- 遵循团队编码规范

#### ❌ 避免做法
- 重复代码不重构
- 缺少必要的文档
- 不遵循编码规范


## 📚 参考资料

- [Cucumber.js 官方文档](https://cucumber.io/docs/cucumber/)
- [Playwright 官方文档](https://playwright.dev/)
- [Allure Report 文档](https://docs.qameta.io/allure/)
- [TypeScript 手册](https://www.typescriptlang.org/docs/)
- [BDD 最佳实践](https://cucumber.io/docs/bdd/)

---
