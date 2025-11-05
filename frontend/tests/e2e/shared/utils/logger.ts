type LogLevelName = 'silent' | 'error' | 'warn' | 'info' | 'debug';

const LOG_LEVELS: Record<LogLevelName, number> = {
  silent: 0,
  error: 1,
  warn: 2,
  info: 3,
  debug: 4
};

function normalizeLevel(value?: string | null): LogLevelName {
  if (!value) {
    return process.env.E2E_VERBOSE_LOGS === 'true' ? 'debug' : 'info';
  }

  const normalized = value.toLowerCase();
  if (normalized in LOG_LEVELS) {
    return normalized as LogLevelName;
  }

  switch (normalized) {
    case 'trace':
    case 'verbose':
      return 'debug';
    case 'warning':
      return 'warn';
    case 'fatal':
      return 'error';
    default:
      return 'info';
  }
}

const currentLevel = LOG_LEVELS[normalizeLevel(process.env.E2E_LOG_LEVEL)];

function shouldLog(level: LogLevelName): boolean {
  return LOG_LEVELS[level] <= currentLevel;
}

function format(args: unknown[]): unknown[] {
  if (args.length === 0) {
    return args;
  }
  const [first, ...rest] = args;
  if (typeof first === 'string') {
    return [`[E2E] ${first}`, ...rest];
  }
  return ['[E2E]', ...args];
}

export const logger = {
  debug: (...args: unknown[]): void => {
    if (shouldLog('debug')) {
      console.debug(...format(args));
    }
  },
  info: (...args: unknown[]): void => {
    if (shouldLog('info')) {
      console.log(...format(args));
    }
  },
  warn: (...args: unknown[]): void => {
    if (shouldLog('warn')) {
      console.warn(...format(args));
    }
  },
  error: (...args: unknown[]): void => {
    if (shouldLog('error')) {
      console.error(...format(args));
    }
  }
};

export type Logger = typeof logger;
