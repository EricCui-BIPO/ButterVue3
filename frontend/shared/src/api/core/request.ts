import axios, { type AxiosInstance, type AxiosError } from 'axios';
import type { ApiResponse, ApiError, RequestConfig } from '@I0/shared/types';
import { ERROR_TYPES, HTTP_STATUS } from '@I0/shared/types';
import { formatErrorMessage, showErrorToast } from './utils';

// 增强的请求处理器 - 简化版本
class EnhancedRequest {
  private instance: AxiosInstance;

  constructor() {
    this.instance = this.createAxiosInstance();
    this.setupInterceptors();
  }

  private createAxiosInstance(): AxiosInstance {
    return axios.create({
      baseURL: `/services/api`,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  private setupInterceptors(): void {
    // 请求拦截器
    this.instance.interceptors.request.use(
      config => {
        return config;
      },
      error => {
        console.error('Request Error:', error);
        return Promise.reject(error);
      }
    );

    // 响应拦截器
    this.instance.interceptors.response.use(
      response => {
        const requestId = response.config.headers?.['X-Request-ID'];

        // 检查业务错误 - 匹配后端 ApiResult 格式
        const data = response.data;
        if (data && typeof data === 'object' && 'success' in data && !data.success) {
          const businessError: ApiError = {
            type: this.mapErrorCodeToType(data.errorCode),
            details: data.errorMessage || '业务处理失败',
            code: data.errorCode,
            requestId
          };

          // 自动显示业务错误Toast (可通过配置禁用)
          const config = response.config as any;
          if (config.showErrorToast !== false) {
            console.log('📱 显示错误Toast:', formatErrorMessage(businessError));
            showErrorToast(formatErrorMessage(businessError));
          }

          console.log('🚫 抛出错误，阻止后续操作');
          return Promise.reject(businessError);
        }

        return response;
      },
      (error: AxiosError) => {
        const requestId = error.config?.headers?.['X-Request-ID'];
        const config = error.config as any;

        const apiError: ApiError = this.handleApiError(error, requestId);
        console.groupEnd();

        // 自动显示网络错误Toast (可通过配置禁用)
        if (config?.showErrorToast !== false) {
          showErrorToast(formatErrorMessage(apiError));
        }

        console.log('🚫 抛出网络错误，阻止后续操作');
        return Promise.reject(apiError);
      }
    );
  }

  /**
   * 将后端错误码映射为前端错误类型
   */
  private mapErrorCodeToType(errorCode?: string): (typeof ERROR_TYPES)[keyof typeof ERROR_TYPES] {
    const codeMap: Record<string, (typeof ERROR_TYPES)[keyof typeof ERROR_TYPES]> = {
      // 业务错误 (6000-6999)
      '6000': ERROR_TYPES.BUSINESS_ERROR,
      '6001': ERROR_TYPES.BUSINESS_ERROR,
      '6002': ERROR_TYPES.BUSINESS_ERROR,
      '6003': ERROR_TYPES.BUSINESS_ERROR,

      // 参数验证错误 (7000-7999)
      '7000': ERROR_TYPES.VALIDATION_ERROR,
      '7001': ERROR_TYPES.VALIDATION_ERROR,
      '7002': ERROR_TYPES.VALIDATION_ERROR,
      '7003': ERROR_TYPES.VALIDATION_ERROR,
      '7004': ERROR_TYPES.VALIDATION_ERROR,

      // 客户端错误 (4000-4999)
      '4000': ERROR_TYPES.BUSINESS_ERROR,
      '4001': ERROR_TYPES.UNAUTHORIZED,
      '4003': ERROR_TYPES.FORBIDDEN,
      '4004': ERROR_TYPES.NOT_FOUND,
      '4005': ERROR_TYPES.BUSINESS_ERROR,

      // 服务端错误 (5000-5999)
      '5000': ERROR_TYPES.SERVER_ERROR,
      '5003': ERROR_TYPES.SERVER_ERROR
    };

    return codeMap[errorCode || ''] || ERROR_TYPES.BUSINESS_ERROR;
  }

  private handleApiError(error: AxiosError, requestId?: string): ApiError {
    if (error.response) {
      const responseData = error.response.data as any;
      switch (error.response.status) {
        case HTTP_STATUS.BAD_REQUEST:
          // 400 错误可能包含后端的业务错误信息
          return {
            type: this.mapErrorCodeToType(responseData?.errorCode),
            details: responseData?.errorMessage || responseData?.message || '请求参数错误',
            code: responseData?.errorCode || error.response.status,
            requestId
          };
        case HTTP_STATUS.UNAUTHORIZED:
          localStorage.removeItem('token');
          return {
            type: 'unauthorized' as const,
            details: responseData?.errorMessage || responseData?.message || '未授权，请重新登录',
            code: responseData?.errorCode || error.response.status,
            requestId
          };
        case HTTP_STATUS.FORBIDDEN:
          return {
            type: 'forbidden' as const,
            details: responseData?.errorMessage || responseData?.message || '拒绝访问',
            code: responseData?.errorCode || error.response.status,
            requestId
          };
        case HTTP_STATUS.NOT_FOUND:
          return {
            type: 'not_found' as const,
            details: responseData?.errorMessage || responseData?.message || '请求的资源不存在',
            code: responseData?.errorCode || error.response.status,
            requestId
          };
        case HTTP_STATUS.INTERNAL_SERVER_ERROR:
          return {
            type: 'server_error' as const,
            details: responseData?.errorMessage || responseData?.message || '服务器内部错误',
            code: responseData?.errorCode || error.response.status,
            requestId
          };
        default:
          return {
            type: 'http_error' as const,
            details:
              responseData?.errorMessage ||
              responseData?.message ||
              `HTTP ${error.response.status} 错误`,
            code: responseData?.errorCode || error.response.status,
            requestId
          };
      }
    } else if (error.request) {
      return {
        type: 'network_error' as const,
        details: '网络连接失败，请检查网络设置',
        requestId
      };
    } else {
      return {
        type: 'request_error' as const,
        details: '请求配置错误',
        requestId
      };
    }
  }

  // 核心请求方法
  async request<T = any>(
    config: RequestConfig & { url: string; method: string }
  ): Promise<ApiResponse<T>> {
    const { url, method, ...axiosConfig } = config;

    try {
      const response = await this.instance.request<ApiResponse<T>>({
        url,
        method: method.toLowerCase() as any,
        ...axiosConfig
      });

      return response.data;
    } catch (error) {
      const apiError = error as ApiError;

      // 自定义错误处理器（如果提供）
      if (config.customErrorHandler) {
        config.customErrorHandler(apiError);
      }

      // 控制台日志（可配置是否显示）
      if (config.showError !== false) {
        console.error('API Error:', formatErrorMessage(apiError));
      }

      throw apiError;
    }
  }

  // 便捷方法
  async get<T = any>(url: string, params?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.request<T>({ ...config, url, method: 'GET', params });
  }

  async post<T = any>(url: string, data?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.request<T>({ ...config, url, method: 'POST', data });
  }

  async put<T = any>(url: string, data?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.request<T>({ ...config, url, method: 'PUT', data });
  }

  async delete<T = any>(url: string, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.request<T>({ ...config, url, method: 'DELETE' });
  }

  async patch<T = any>(url: string, data?: any, config?: RequestConfig): Promise<ApiResponse<T>> {
    return this.request<T>({ ...config, url, method: 'PATCH', data });
  }

  // 文件上传
  async upload<T = any>(url: string, file: File, config?: RequestConfig): Promise<ApiResponse<T>> {
    const formData = new FormData();
    formData.append('file', file);

    return this.request<T>({
      ...config,
      url,
      method: 'POST',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 60000 // 上传超时时间更长
    });
  }

  // 文件下载
  async download(url: string, config?: RequestConfig): Promise<Blob> {
    const response = await this.instance.request({
      ...config,
      url,
      method: 'GET',
      responseType: 'blob'
    });

    return response.data;
  }
}

// 创建并导出请求实例
const enhancedRequest = new EnhancedRequest();
export default enhancedRequest;
