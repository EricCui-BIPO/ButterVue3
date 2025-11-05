import { defineConfig, devices } from '@playwright/test'

const isBasic = process.env.PW_MODE === 'basic' || process.env.PW_BASIC === '1';

const commonReporter: any = isBasic
  ? [
      ['html', { outputFolder: 'tests/e2e/reports/playwright-report' }],
      ['json', { outputFile: 'tests/e2e/reports/test-results/e2e-report/results.json' }],
    ]
  : [
      ['html', { outputFolder: 'tests/e2e/reports/playwright-report' }],
      ['json', { outputFile: 'tests/e2e/reports/test-results/e2e-report/results.json' }],
      ['junit', { outputFile: 'tests/e2e/reports/test-results/e2e-report/results.xml' }]
    ];

const portalProjects = [
  {
    name: 'client-portal',
    use: { 
      ...devices['Desktop Chrome'],
      baseURL: 'http://localhost:3001'
    },
    testDir: './tests/e2e/client-portal',
    outputDir: './tests/e2e/reports/test-results',
  },
  {
    name: 'service-portal',
    use: { 
      ...devices['Desktop Chrome'],
      baseURL: 'http://localhost:3002'
    },
    testDir: './tests/e2e/service-portal',
    outputDir: './tests/e2e/reports/test-results',
  },
  {
    name: 'admin-portal',
    use: { 
      ...devices['Desktop Chrome'],
      baseURL: 'http://localhost:3003'
    },
    testDir: './tests/e2e/admin-portal',
    outputDir: './tests/e2e/reports/test-results',
  },
  {
    name: 'talent-portal',
    use: { 
      ...devices['Desktop Chrome'],
      baseURL: 'http://localhost:3004'
    },
    testDir: './tests/e2e/talent-portal',
    outputDir: './tests/e2e/reports/test-results',
  },
];

const basicProjects = [
  { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
  { name: 'firefox', use: { ...devices['Desktop Firefox'] } },
  { name: 'webkit', use: { ...devices['Desktop Safari'] } },
  { name: 'Mobile Chrome', use: { ...devices['Pixel 5'] } },
  { name: 'Mobile Safari', use: { ...devices['iPhone 12'] } },
];

const webServerConfig = isBasic ? [] : [
  {
    command: 'cd ../backend && ./gradlew :modules:app:bootRun',
    port: 8088,
    reuseExistingServer: !process.env.CI,
    timeout: 120 * 1000, // 后端启动可能需要更长时间
  },
  { command: 'yarn dev:client', port: 3001, reuseExistingServer: !process.env.CI },
  { command: 'yarn dev:service', port: 3002, reuseExistingServer: !process.env.CI },
  { command: 'yarn dev:admin', port: 3003, reuseExistingServer: !process.env.CI },
  { command: 'yarn dev:talent', port: 3004, reuseExistingServer: !process.env.CI },
];

/**
 * @see https://playwright.dev/docs/test-configuration
 */
export default defineConfig({
  testDir: './tests/e2e',
  /* Run tests in files in parallel */
  fullyParallel: true,
  /* Fail the build on CI if you accidentally left test.only in the source code. */
  forbidOnly: !!process.env.CI,
  /* Retry on CI only */
  retries: process.env.CI ? 2 : 0,
  /* Opt out of parallel tests on CI. */
  workers: process.env.CI ? 1 : undefined,
  /* Reporter to use. See https://playwright.dev/docs/test-reporters */
  reporter: commonReporter,
  /* Shared settings for all the projects below. See https://playwright.dev/docs/api/class-testoptions. */
  use: {
    /* Collect trace when retrying the failed test. See https://playwright.dev/docs/trace-viewer */
    trace: 'on-first-retry',
    /* Take screenshot only when test fails */
    screenshot: 'only-on-failure',
    /* Record video for all tests when video is enabled */
    video: process.env.ENABLE_VIDEO === 'true' || process.env.CODEGEN_MODE === 'true' || process.env.DEBUG_MODE === 'true' ? 'on' : 'retain-on-failure',
  },

  /* Global output directory for all test artifacts */
  outputDir: './tests/e2e/reports/test-results',

  /* Configure projects for major browsers */
  projects: isBasic ? basicProjects : portalProjects,

  /* 配置本地开发服务器 */
  webServer: webServerConfig,
})