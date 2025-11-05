import { After, AfterAll, Before, BeforeAll, Status, setDefaultTimeout } from '@cucumber/cucumber';
import { chromium, firefox, webkit } from 'playwright';
import type { Browser, LaunchOptions } from 'playwright';
import type { I0World } from './world';
import { existsSync, mkdirSync, readFileSync, unlinkSync } from 'fs';
import * as path from 'path';
import { logger } from '../shared/utils/logger';
import type { Portal } from '../config/test-config';

const DEFAULT_TIMEOUT = 120 * 1000;
const VIEWPORT = { width: 1280, height: 720 } as const;
const DEFAULT_ARGS = process.platform === 'linux'
  ? [
      '--no-sandbox',
      '--disable-setuid-sandbox',
      '--disable-dev-shm-usage',
      '--disable-web-security',
      '--allow-running-insecure-content'
    ]
  : [];

const REPORTS_ROOT = path.resolve(__dirname, '../reports');
const TEST_RESULTS_ROOT = path.join(REPORTS_ROOT, 'test-results');
const SCREENSHOT_DIR = path.join(TEST_RESULTS_ROOT, 'screenshots');
const TRACE_DIR = path.join(TEST_RESULTS_ROOT, 'traces');
const VIDEO_DIR = path.join(TEST_RESULTS_ROOT, 'videos');
const ALLURE_RESULTS_DIR = process.env.ALLURE_RESULTS_DIR || path.join(REPORTS_ROOT, 'allure-results');

function ensureDir(dir: string): void {
  if (!existsSync(dir)) {
    mkdirSync(dir, { recursive: true });
  }
}

[REPORTS_ROOT, TEST_RESULTS_ROOT, SCREENSHOT_DIR, TRACE_DIR, VIDEO_DIR, ALLURE_RESULTS_DIR].forEach(
  ensureDir
);

process.env.ALLURE_RESULTS_DIR = ALLURE_RESULTS_DIR;

function slugify(value: string): string {
  return (
    value
      .normalize('NFKD')
      .replace(/[^\w\d]+/g, '-')
      .replace(/^-+|-+$/g, '')
      .toLowerCase() || 'scenario'
  );
}

function timestamp(): string {
  return new Date().toISOString().replace(/[:.]/g, '-');
}

function buildArtifactPath(folder: string, prefix: string, name: string, extension: string): string {
  const safeName = slugify(name);
  return path.join(folder, `${prefix}-${safeName}-${timestamp()}.${extension}`);
}

let sharedBrowser: Browser | undefined;
let browserLaunchError: Error | undefined;

setDefaultTimeout(DEFAULT_TIMEOUT);

function inferPortalFromPickle(uri?: string): Portal {
  if (!uri) {
    return 'admin';
  }

  const normalizedPath = uri.replace(/\\/g, '/');
  if (normalizedPath.includes('/client-portal/')) {
    return 'client';
  }
  if (normalizedPath.includes('/service-portal/')) {
    return 'service';
  }
  if (normalizedPath.includes('/talent-portal/')) {
    return 'talent';
  }
  if (normalizedPath.includes('/admin-portal/')) {
    return 'admin';
  }
  return 'admin';
}

function resolveBoolean(value: string | boolean | undefined, fallback: boolean): boolean {
  if (value === undefined) {
    return fallback;
  }
  if (typeof value === 'boolean') {
    return value;
  }
  return value.toLowerCase() === 'true';
}

function resolveNumber(value: string | number | undefined, fallback: number): number {
  if (value === undefined) {
    return fallback;
  }
  if (typeof value === 'number') {
    return value;
  }
  const parsed = Number(value);
  return Number.isNaN(parsed) ? fallback : parsed;
}

function getBrowserName(): 'chromium' | 'firefox' | 'webkit' {
  const name = (process.env.BROWSER || 'chromium').toLowerCase();
  if (name === 'firefox' || name === 'webkit') {
    return name;
  }
  return 'chromium';
}

async function tryLaunch(name: 'chromium' | 'firefox' | 'webkit', options: LaunchOptions): Promise<Browser> {
  if (name === 'firefox') {
    return firefox.launch(options);
  }
  if (name === 'webkit') {
    return webkit.launch(options);
  }
  return chromium.launch(options);
}

async function launchBrowser(): Promise<Browser> {
  const headless = resolveBoolean(process.env.HEADLESS, true);
  const slowMo = resolveNumber(process.env.SLOW_MO, 0);
  const debugMode = resolveBoolean(process.env.DEBUG_MODE, false);
  const codegenMode = resolveBoolean(process.env.CODEGEN_MODE, false);
  
  const launchOptions: LaunchOptions = {
    headless: codegenMode || debugMode ? false : headless,
    slowMo: debugMode ? Math.max(slowMo, 1000) : slowMo,
    args: DEFAULT_ARGS,
    devtools: debugMode
  };
  
  // 如果是 debug 模式，配置调试选项
  if (debugMode) {
    launchOptions.headless = false;
    launchOptions.slowMo = Math.max(launchOptions.slowMo || 0, 1000);
    launchOptions.devtools = true;
    
    // 启用 Playwright Inspector
    process.env.PWDEBUG = '1';
    
    logger.info('🔍 Debug Mode 已启用:');
    logger.info('  - 浏览器窗口可见');
    logger.info('  - 开发者工具已开启');
    logger.info('  - 慢速执行模式 (1秒间隔)');
    logger.info('  - Playwright Inspector 已启用');
    logger.info('  - 使用 page.pause() 可以逐步调试');
  }
  const preferred = getBrowserName();
  const fallbacks: Array<'chromium' | 'firefox' | 'webkit'> =
    preferred === 'chromium'
      ? ['chromium', 'webkit', 'firefox']
      : preferred === 'firefox'
        ? ['firefox', 'webkit', 'chromium']
        : ['webkit', 'chromium', 'firefox'];

  let lastError: unknown;
  for (const name of fallbacks) {
    try {
      const browser = await tryLaunch(name, launchOptions);
      process.env.ACTUAL_BROWSER_USED = name;
      return browser;
    } catch (error) {
      lastError = error;
      logger.warn(`Playwright failed to launch ${name}: ${(error as Error).message}`);
    }
  }

  const detail = lastError instanceof Error ? `${lastError.name}: ${lastError.message}` : String(lastError);
  throw new Error(
    `Playwright failed to launch any browser (tried ${fallbacks.join(', ')}). ` +
      'This environment appears to block GUI process startup. ' +
      'Please run the tests on an environment that permits spawning browsers or provide remote browser access. ' +
      `Last error: ${detail}`
  );
}

BeforeAll(async function () {
  logger.info('🚀 启动测试套件...');
  try {
    logger.info('🌐 正在启动浏览器...');
    sharedBrowser = await launchBrowser();
    logger.info('✅ 浏览器启动成功');
  } catch (error) {
    browserLaunchError = error as Error;
    logger.error('❌ Playwright 无法启动浏览器，后续场景将被标记为跳过。');
  }
});

Before({ timeout: 30 * 1000 }, async function (this: I0World, { pickle }) {
  // 简化输出，让 After 钩子来显示场景名称
  logger.info('📋 开始新场景...');

  if (browserLaunchError) {
    this.attach(`Playwright 浏览器启动失败：${browserLaunchError.message}`, 'text/plain');
    // 直接抛出异常来跳过场景
    throw new Error('浏览器启动失败，跳过场景');
  }

  if (!sharedBrowser) {
    try {
      sharedBrowser = await launchBrowser();
    } catch (error) {
      browserLaunchError = error as Error;
      this.attach(`Playwright 浏览器启动失败：${browserLaunchError.message}`, 'text/plain');
      // 直接抛出异常来跳过场景
      throw new Error('浏览器启动失败，跳过场景');
    }
  }

  this.browser = sharedBrowser;

  const inferredPortal = inferPortalFromPickle(pickle?.uri);
  this.setPortal(inferredPortal);

  // 🔥 增强的上下文配置 - 支持 Trace、Video 和 Screenshots
  const contextOptions: Parameters<typeof sharedBrowser.newContext>[0] = {
    baseURL: this.baseUrl,
    ignoreHTTPSErrors: true,
    viewport: VIEWPORT
  };

  // 🎬 视频录制配置 - 默认启用，必要时可通过环境变量关闭
  const enableVideo =
    resolveBoolean(process.env.ENABLE_VIDEO, true) ||
    resolveBoolean(process.env.CODEGEN_MODE, false) ||
    resolveBoolean(process.env.DEBUG_MODE, false);

  if (enableVideo) {
    contextOptions.recordVideo = {
      dir: VIDEO_DIR,
      size: VIEWPORT
    };
    logger.debug(`🎬 Video recording enabled: ${VIDEO_DIR}`);
  }

  // 📸 截图配置 - 在失败时自动截图
  contextOptions.recordHar = undefined; // 可选：启用 HAR 记录

  this.context = await sharedBrowser.newContext(contextOptions);

  // 🔍 Trace 记录配置 - 完整捕获测试执行过程
  const enableTrace = resolveBoolean(process.env.ENABLE_TRACE, true); // 默认启用
  if (enableTrace) {
    await this.context.tracing.start({
      name: `trace-${Date.now()}`,
      title: `Cucumber Test Trace`,
      screenshots: true,
      snapshots: true,
      sources: true
    });
    logger.debug('🔍 Trace recording started with screenshots and snapshots');
  }

  this.page = await this.context.newPage();

  // 如果是 debug 模式，添加调试辅助
  if (resolveBoolean(process.env.DEBUG_MODE, false)) {
    this.page.on('framenavigated', () => {
      logger.debug(`🔍 Debug Mode: Navigated to ${this.page.url()}`);
    });
  }

  // 📊 为报告记录测试开始时间
  this.testStartTime = Date.now();
});

After(async function (this: I0World, { result, pickle }) {
  if (browserLaunchError || !this.context || !this.page) {
    return;
  }

  const scenarioName = pickle?.name ?? 'Unknown Scenario';

  // 显示场景完成信息
  const status = result?.status === Status.PASSED ? '✅ 通过' :
                 result?.status === Status.FAILED ? '❌ 失败' :
                 result?.status === Status.SKIPPED ? '⏭️  跳过' : 
                 result?.status === Status.UNDEFINED ? '❓ 未定义' :
                 result?.status === Status.AMBIGUOUS ? '❓ 模糊' :
                 result?.status === Status.PENDING ? '⏳ 待处理' :
                 `⚠️ 未知 (${result?.status})`;
  logger.info(`${status} 场景: ${scenarioName}`);

  // 🧹 清理所有测试数据（无论成功或失败）
  await this.cleanupAllTestData();

  // 📸 失败时截图并附加到报告 / Allure
  if (result?.status === Status.FAILED && this.page) {
    try {
      const screenshotPath = buildArtifactPath(SCREENSHOT_DIR, 'screenshot', scenarioName, 'png');
      const screenshotBuffer = await this.page.screenshot({ path: screenshotPath, fullPage: true });
      this.attach(screenshotBuffer, 'image/png');
      // 截图成功，无需详细日志
    } catch (error) {
      logger.warn('⚠️ Failed to capture screenshot:', error);
    }
  }

  // 🔍 保存 Trace 文件并附加失败信息
  const enableTrace = resolveBoolean(process.env.ENABLE_TRACE, true);
  if (enableTrace && this.context) {
    const tracePath = buildArtifactPath(TRACE_DIR, 'trace', scenarioName, 'zip');

    try {
      await this.context.tracing.stop({ path: tracePath });
      // Trace 保存成功，无需详细日志

      if (result?.status === Status.FAILED) {
        const traceBuffer = readFileSync(tracePath);
        this.attach(traceBuffer, 'application/zip');
      } else if (!resolveBoolean(process.env.RETAIN_TRACES_ON_SUCCESS, false) && existsSync(tracePath)) {
        unlinkSync(tracePath);
      }
    } catch (error) {
      logger.warn('⚠️ Failed to save trace:', error);
    }
  }

  // 🎬 获取视频文件路径并将失败视频附加到报告
  if (this.page && this.page.video()) {
    try {
      // 先关闭页面，确保视频写入完成
      await this.page.close();

      const video = this.page.video();
      const videoPath = video ? await video.path() : undefined;

      if (videoPath) {
        // 视频保存成功，无需详细日志

        const retainVideosOnSuccess = resolveBoolean(process.env.RETAIN_VIDEOS_ON_SUCCESS, false);

        if (result?.status === Status.FAILED) {
          let attempts = 0;
          const maxAttempts = 10;

          while (attempts < maxAttempts && !existsSync(videoPath)) {
            await new Promise(resolve => setTimeout(resolve, 300));
            attempts += 1;
          }

          if (existsSync(videoPath)) {
            try {
              const videoBuffer = readFileSync(videoPath);
              this.attach(videoBuffer, 'video/webm');
              // 视频附加成功，无需详细日志
            } catch (readError) {
              logger.warn('⚠️ Failed to read video for attachment:', readError);
            }
          } else {
            logger.warn(`⚠️ Video file not ready for attachment: ${videoPath}`);
          }
        } else if (!retainVideosOnSuccess) {
          try {
            if (existsSync(videoPath)) {
              unlinkSync(videoPath);
            }
          } catch (cleanupError) {
            logger.warn('⚠️ Failed to cleanup video file:', cleanupError);
          }
        }
      }
    } catch (error) {
      logger.warn('⚠️ Failed to process video recording:', error);
    }
  } else {
    await this.page?.close();
  }

  // 📊 记录测试执行时间
  if (this.testStartTime) {
    const duration = Date.now() - this.testStartTime;
    this.attach(`Test duration: ${duration}ms`, 'text/plain');
  }

  await this.context?.close();
});

AfterAll(async function () {
  // 测试套件完成，开始清理
  
  if (sharedBrowser) {
    await sharedBrowser.close();
  }

  // 🚀 自动生成增强报告
  const autoReport = resolveBoolean(process.env.AUTO_REPORT, true); // 默认启用自动报告
  if (autoReport) {
    // 开始生成增强报告
    try {
      // 临时禁用报告生成功能，避免模块未找到错误
      // 报告生成功能暂时禁用
      // const { generateEnhancedReport } = await import('./report-generator.js');
      // await generateEnhancedReport();
      // logger.info('✅ Enhanced test report generated successfully!');
    } catch (error) {
      logger.error('❌ Failed to generate enhanced report:', error);
    }
  }
  
  // 🧹 最后清理剩余的测试数据（可选）
  const finalCleanup = resolveBoolean(process.env.FINAL_CLEANUP, false);
  if (finalCleanup) {
    // 开始最后的测试数据清理
    // 这里可以添加全局清理逻辑，但通常不推荐
    // 因为可能影响其他正在运行的测试
  }

  // 清理完成
  sharedBrowser = undefined;
  if (browserLaunchError) {
    logger.error('❌ Playwright 浏览器启动失败：', browserLaunchError.message);
    process.exitCode = process.exitCode ?? 1;
  }
  browserLaunchError = undefined;
});
