import type { ApiError, PaginationParams } from '@I0/shared/types';
import { ElMessage } from 'element-plus';

// Toast显示函数 - 动态导入Element Plus的ElMessage
export const showErrorToast = (message: string): void => {
  ElMessage.error(message);
};

// 生成唯一请求ID
export const generateRequestId = (): string => {
  return 'req_' + Date.now() + '_' + Math.random().toString(36).slice(2, 11);
};

// 格式化错误信息
export const formatErrorMessage = (error: ApiError): string => {
  const errorMessages = {
    network_error: '网络连接失败，请检查网络设置后重试',
    unauthorized: '登录已过期，请重新登录',
    forbidden: '权限不足，无法访问该资源',
    not_found: '请求的资源不存在',
    server_error: '服务器暂时不可用，请稍后重试',
    business_error: error.details || '业务处理失败',
    http_error: error.details || 'HTTP请求错误',
    request_error: error.details || '请求配置错误',
    validation_error: error.details || '数据验证失败'
  };

  return errorMessages[error.type] || error.details || '操作失败，请稍后重试';
};

// Mock 装饰器工厂
export function withMock(mockService: any, methodName: string, delay: number = 300) {
  return function (_target: any, _propertyKey: string, descriptor: PropertyDescriptor) {
    const originalMethod = descriptor.value;

    descriptor.value = async function (...args: any[]) {
      // 开发环境使用 Mock 数据
      if (import.meta.env.DEV && import.meta.env.VITE_USE_MOCK_API === 'true') {
        await new Promise(resolve => setTimeout(resolve, delay));
        return await mockService[methodName](...args);
      }

      return originalMethod.apply(this, args);
    };

    return descriptor;
  };
}

// 检查文件类型是否允许
export const isFileTypeAllowed = (file: File, allowedTypes: string[]): boolean => {
  return allowedTypes.includes(file.type);
};

// 检查文件大小是否超过限制
export const isFileSizeExceeded = (file: File, maxSize: number): boolean => {
  return file.size > maxSize;
};

// 格式化文件大小
export const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 清理对象中的 undefined 值
export const cleanObject = <T extends Record<string, any>>(obj: T): Partial<T> => {
  const cleaned = {} as Partial<T>;
  for (const key in obj) {
    if (obj[key] !== undefined) {
      cleaned[key] = obj[key];
    }
  }
  return cleaned;
};

// 合并分页参数
export const mergePaginationParams = (
  defaultParams: PaginationParams,
  userParams?: PaginationParams
): PaginationParams => {
  return {
    page: userParams?.page ?? defaultParams.page,
    size: userParams?.size ?? defaultParams.size,
    sortBy: userParams?.sortBy ?? defaultParams.sortBy,
    sortOrder: userParams?.sortOrder ?? defaultParams.sortOrder
  };
};
