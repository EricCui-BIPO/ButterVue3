const fs = require('fs');
const path = require('path');

const tsconfigPath = path.resolve(__dirname, 'tests/e2e/tsconfig.cucumber.json');
const allureResultsDir = path.resolve(__dirname, 'tests/e2e/reports/allure-results');
const portalBaseUrls = {
  admin: 'http://localhost:3003',
  client: 'http://localhost:3001',
  service: 'http://localhost:3002',
  talent: 'http://localhost:3004'
};
const defaultFormatter = (() => {
  const preferred = process.env.CUCUMBER_PROGRESS_FORMAT;
  // 优先使用 pretty 格式，它会显示场景名称和步骤
  if (preferred === 'pretty' && process.stdout.isTTY) {
    return 'pretty';
  }
  if (preferred === 'progress-bar' && process.stdout.isTTY) {
    return 'progress-bar';
  }
  // 如果不是TTY环境，使用 progress
  if (preferred && preferred.length > 0) {
    return preferred;
  }
  // 默认使用 progress 格式，提供实时进度反馈
  return process.stdout.isTTY ? 'progress' : 'progress';
})();

process.env.TS_NODE_PROJECT = tsconfigPath;
process.env.TS_NODE_TRANSPILE_ONLY = process.env.TS_NODE_TRANSPILE_ONLY ?? 'true';
process.env.ALLURE_RESULTS_DIR = process.env.ALLURE_RESULTS_DIR || allureResultsDir;
process.env.CUCUMBER_PUBLISH_QUIET = process.env.CUCUMBER_PUBLISH_QUIET || 'false'; // 启用控制台输出

if (!fs.existsSync(allureResultsDir)) {
  fs.mkdirSync(allureResultsDir, { recursive: true });
}

module.exports = {
  default: {
    requireModule: ['ts-node/register'],
    require: [
      'tests/e2e/support/**/*.ts',
      'tests/e2e/**/step-definitions/**/*.ts'
    ],
    format: [
    defaultFormatter,
    'summary',
    'allure-cucumberjs/reporter'
  ],
    formatOptions: {
      snippetInterface: 'async-await',
      resultsDir: 'tests/e2e/reports/allure-results'
    },
    paths: ['tests/e2e/**/*.feature'],
    worldParameters: {
      baseUrl: process.env.BASE_URL,
      defaultPortal: process.env.DEFAULT_PORTAL || 'admin',
      portalBaseUrls
    },
    tags: 'not @skip'
  },
  'admin-service-type': {
    requireModule: ['ts-node/register'],
    require: [
      'tests/e2e/support/**/*.ts',
      'tests/e2e/admin-portal/step-definitions/**/*.ts'
    ],
    format: [
    defaultFormatter,
    'summary',
    'allure-cucumberjs/reporter'
  ],
    formatOptions: {
      snippetInterface: 'async-await',
      resultsDir: 'tests/e2e/reports/allure-results'
    },
    paths: ['tests/e2e/admin-portal/features/service-type.feature'],
    worldParameters: {
      baseUrl: process.env.BASE_URL,
      defaultPortal: 'admin',
      portalBaseUrls
    },
    tags: 'not @skip'
  },
  'admin-entity': {
    requireModule: ['ts-node/register'],
    require: [
      'tests/e2e/support/**/*.ts',
      'tests/e2e/admin-portal/step-definitions/**/*.ts'
    ],
    format: [
    defaultFormatter,
    'summary',
    'allure-cucumberjs/reporter'
  ],
    formatOptions: {
      snippetInterface: 'async-await',
      resultsDir: 'tests/e2e/reports/allure-results'
    },
    paths: ['tests/e2e/admin-portal/features/entity.feature'],
    worldParameters: {
      baseUrl: process.env.BASE_URL,
      defaultPortal: 'admin',
      portalBaseUrls
    },
    tags: 'not @skip'
  },
  'admin-client': {
    requireModule: ['ts-node/register'],
    require: [
      'tests/e2e/support/**/*.ts',
      'tests/e2e/admin-portal/step-definitions/**/*.ts'
    ],
    format: [
    defaultFormatter,
    'summary',
    'allure-cucumberjs/reporter'
  ],
    formatOptions: {
      snippetInterface: 'async-await',
      resultsDir: 'tests/e2e/reports/allure-results'
    },
    paths: ['tests/e2e/admin-portal/features/clients.feature'],
    worldParameters: {
      baseUrl: process.env.BASE_URL,
      defaultPortal: 'admin',
      portalBaseUrls
    },
    tags: 'not @skip'
  },
  'admin-reports': {
    requireModule: ['ts-node/register'],
    require: [
      'tests/e2e/support/**/*.ts',
      'tests/e2e/admin-portal/step-definitions/**/*.ts'
    ],
    format: [
    defaultFormatter,
    'summary',
    'allure-cucumberjs/reporter'
  ],
    formatOptions: {
      snippetInterface: 'async-await',
      resultsDir: 'tests/e2e/reports/allure-results'
    },
    paths: ['tests/e2e/admin-portal/features/reports.feature'],
    worldParameters: {
      baseUrl: process.env.BASE_URL,
      defaultPortal: 'admin',
      portalBaseUrls
    },
    tags: 'not @skip'
  }
};
