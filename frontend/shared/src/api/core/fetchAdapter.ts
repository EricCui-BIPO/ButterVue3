// fetch 适配器用于处理流式数据
export interface FetchAdapterConfig {
  url: string;
  method: string;
  headers?: Record<string, string>;
  data?: any;
  params?: Record<string, any>;
  baseURL?: string;
}

export const fetchAdapter = async (config: FetchAdapterConfig) => {
  const { url, method, headers, data, params, baseURL } = config;

  // 构造完整的 URL
  let fullUrl = url;
  if (baseURL && !url.startsWith('http')) {
    // 如果 url 不是完整的 URL，则拼接 baseURL
    if (baseURL.endsWith('/')) {
      // baseURL 以 / 结尾
      fullUrl = url.startsWith('/') ? baseURL + url.slice(1) : baseURL + url;
    } else {
      // baseURL 不以 / 结尾
      fullUrl = url.startsWith('/') ? baseURL + '/' + url.slice(1) : baseURL + '/' + url;
    }
  }

  // 处理查询参数
  if (params) {
    const searchParams = new URLSearchParams();
    Object.keys(params).forEach(key => {
      if (params[key] !== undefined && params[key] !== null) {
        searchParams.append(key, String(params[key]));
      }
    });

    const paramString = searchParams.toString();
    if (paramString) {
      // 拼接查询参数到 URL
      fullUrl += (fullUrl.includes('?') ? '&' : '?') + paramString;
    }
  }

  // 构造 fetch 请求配置
  const fetchOptions: RequestInit = {
    method,
    headers
  };

  // 如果是 POST、PUT 或 PATCH 请求，添加 body
  if (['POST', 'PUT', 'PATCH'].includes(method.toUpperCase())) {
    if (data instanceof FormData) {
      fetchOptions.body = data;
    } else {
      fetchOptions.body = JSON.stringify(data);
    }
  }

  console.log('FetchAdapter Request:', {
    originalUrl: url,
    fullUrl,
    method,
    headers,
    params,
    hasBody: !!fetchOptions.body
  });

  const response = await fetch(fullUrl, fetchOptions);

  return {
    data: response.body, // 直接返回 ReadableStream
    status: response.status,
    statusText: response.statusText,
    headers: response.headers,
    config,
    request: response
  };
};
