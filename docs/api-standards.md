# API 接口与对接规范

本文档定义了项目中API接口的设计规范和前后端对接标准，确保接口的一致性和可维护性。

## 📋 目录

- [1. 接口设计规范](#1-接口设计规范)
  - [1.1 RESTful API 规范](#11-restful-api-规范)
  - [1.2 版本控制规范](#12-版本控制规范)
  - [1.3 HTTP状态码规范](#13-http状态码规范)
- [2. 统一响应格式](#2-统一响应格式)
  - [2.1 成功响应格式](#21-成功响应格式)
  - [2.2 分页响应格式](#22-分页响应格式)
  - [2.3 错误响应格式](#23-错误响应格式)
  - [2.4 响应字段说明](#24-响应字段说明)
- [3. 前端集成规范](#3-前端集成规范)
  - [3.1 TypeScript 类型定义](#31-typescript-类型定义)
  - [3.2 请求配置规范](#32-请求配置规范)
- [4. 错误处理规范](#4-错误处理规范)
  - [4.1 后端错误处理](#41-后端错误处理)
  - [4.2 前端错误处理](#42-前端错误处理)
  - [4.3 错误处理最佳实践](#43-错误处理最佳实践)
- [5. 安全规范](#5-安全规范)
  - [5.1 身份验证与授权](#51-身份验证与授权)
  - [5.2 数据传输安全](#52-数据传输安全)
  - [5.3 接口安全防护](#53-接口安全防护)
  - [5.4 日志与监控](#54-日志与监控)
- [6. 性能优化规范](#6-性能优化规范)
  - [6.1 缓存策略](#61-缓存策略)
  - [6.2 分页与限制](#62-分页与限制)
  - [6.3 响应优化](#63-响应优化)

## 1. 接口设计规范

### 1.1 RESTful API 规范

#### 资源命名规范
- 使用名词复数形式表示资源：`/api/talents`
- 资源路径使用小写字母和连字符：`/api/talent-profiles`
- 避免使用动词，通过HTTP方法表示操作

#### HTTP 方法规范
- **GET**：获取资源（幂等）
- **POST**：创建资源（非幂等）
- **PUT**：更新资源（完整更新，幂等）
- **PATCH**：更新资源（部分更新，幂等）
- **DELETE**：删除资源（幂等）

#### 路径设计示例
```
GET    /api/talents          # 获取人才列表
GET    /api/talents/{id}     # 获取单个人才
POST   /api/talents          # 创建人才
PUT    /api/talents/{id}     # 更新人才（完整）
PATCH  /api/talents/{id}     # 更新人才（部分）
DELETE /api/talents/{id}     # 删除人才
```

### 1.2 版本控制规范
- API版本应在URL路径中明确标识：`/api/v1/talents`
- 向后兼容的更改可在同一版本中进行
- 破坏性更改必须创建新版本
- 旧版本应有明确的废弃时间表和迁移指南

### 1.3 HTTP状态码规范

| 状态码 | 说明 | 使用场景 |
|--------|------|----------|
| 200 | 成功 | 请求成功处理 |
| 201 | 创建成功 | 资源创建成功 |
| 204 | 无内容 | 删除成功或更新成功无返回内容 |
| 400 | 请求错误 | 参数验证失败 |
| 401 | 未授权 | 身份验证失败 |
| 403 | 禁止访问 | 权限不足 |
| 404 | 资源不存在 | 请求的资源未找到 |
| 409 | 冲突 | 资源冲突（如重复创建） |
| 422 | 实体无法处理 | 请求格式正确但语义错误 |
| 500 | 服务器错误 | 内部服务器错误 |

## 2. 统一响应格式

所有API接口必须遵循统一的响应格式，确保前后端数据结构一致性。

### 2.1 成功响应格式

```json
{
  "success": true,
  "data": {
    // 实际业务数据
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 2.2 分页响应格式

```json
{
  "success": true,
  "data": {
    "content": [
      // 数据列表
    ],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 100,
      "totalPages": 5
    }
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 2.3 错误响应格式

```json
{
  "success": false,
  "errorCode": "6000",
  "errorMessage": "请求参数错误",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 2.4 响应字段说明

| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| success | boolean | 是 | 请求是否成功 |
| errorCode | string | 否 | 错误码，仅错误响应时包含 |
| errorMessage | string | 否 | 错误信息，仅错误响应时包含 |
| data | any | 否 | 业务数据，成功时返回；错误响应时为 null |
| timestamp | string | 是 | 响应时间戳（ISO 8601格式） |

## 3. 前端集成规范

### 3.1 TypeScript 类型定义

#### 核心响应类型
```typescript
// 通用API响应类型
interface ApiResponse<T = any> {
  success: boolean
  errorCode?: string  // 仅错误响应时包含
  errorMessage?: string  // 仅错误响应时包含
  data?: T
  timestamp: string
}

// 分页信息类型
interface PaginationInfo {
  page: number
  pageSize: number
  total: number
  totalPages: number
}
```

#### 请求参数类型
```typescript
// 分页请求参数
interface PaginationParams {
  page?: number
  pageSize?: number
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

// 查询参数基类
interface BaseQueryParams extends PaginationParams {
  keyword?: string
  filters?: Record<string, any>
}
```

### 3.2 请求配置规范

#### 标准请求头
前端请求应包含以下标准头部：

```typescript
{
  'Content-Type': 'application/json',
  'Accept': 'application/json',
  'Authorization': 'Bearer {token}', // 如需认证
  'X-Request-ID': '{uuid}', // 请求追踪ID
  'X-Client-Version': '{version}' // 客户端版本
}
```

#### 请求超时配置
- 普通请求：5秒超时
- 文件上传：30秒超时
- 长时间查询：15秒超时

## 4. 错误处理规范

### 4.1 后端错误处理

#### 错误处理原则
- 所有异常必须转换为统一的错误响应格式
- 敏感信息不得暴露在错误消息中
- 记录详细的错误日志用于调试和监控
- 提供有意义的错误代码和消息

#### 错误分类
- **业务错误**：业务逻辑验证失败
- **系统错误**：技术层面的异常
- **外部错误**：第三方服务调用失败
- **安全错误**：认证授权相关错误

### 4.2 前端错误处理

#### 分层错误处理架构

前端错误处理采用分层架构，确保职责清晰和代码可维护性：

**1. Request 层（统一拦截）**
- 在 `request.ts` 的响应拦截器中统一处理所有网络错误、HTTP状态码错误和业务错误
- 将所有错误标准化为 `ApiError` 格式并直接抛出
- 不在拦截器中进行用户提示，保持职责单一
- 记录错误日志用于调试

**2. 业务层（错误展示）**
- 只需要 catch 错误并根据错误类型展示用户友好的提示信息
- 不需要判断网络连接状态或HTTP状态码
- 专注于业务逻辑和用户体验
- 提供错误恢复机制

#### 错误类型定义

```typescript
interface ApiError {
  type: 'network_error' | 'unauthorized' | 'forbidden' | 'not_found' |
        'server_error' | 'business_error' | 'validation_error' | 'timeout_error'
  details: string
  field?: string
  code?: string
}

// 简化的错误处理，直接从响应中提取错误信息
interface SimplifiedApiError {
  errorCode?: string
  errorMessage?: string
}
```

#### 实现示例

**Request 层错误处理**
```typescript
// 响应拦截器中的错误处理
axiosInstance.interceptors.response.use(
  (response) => {
    // 检查业务层面的错误
    const data = response.data
    if (data && typeof data === 'object' && 'success' in data && !data.success) {
      const businessError: ApiError = {
        type: 'business_error',
        details: data.errorMessage || '业务处理失败',
        code: data.errorCode
      }
      // 记录错误日志
      console.error('API Business Error:', businessError)
      throw businessError
    }
    return response
  },
  (error: AxiosError) => {
    // 统一处理网络错误和HTTP错误
    const apiError: ApiError = {
      type: error.code === 'ECONNABORTED' ? 'timeout_error' : 'network_error',
      details: error.message || '网络请求失败',
      code: error.code
    }
    console.error('API Network Error:', apiError)
    throw apiError
  }
)
```

**业务层错误处理**
```typescript
const fetchData = async () => {
  try {
    const { data, success } = await api.getData()
    // 处理成功响应
    if (success && data) {
      setData(data)
    }
  } catch (err: any) {
    // 根据错误类型显示用户友好消息
    const errorMessages = {
      network_error: '网络连接失败，请检查网络设置后重试',
      timeout_error: '请求超时，请稍后重试',
      unauthorized: '登录已过期，请重新登录',
      forbidden: '权限不足，无法执行此操作',
      not_found: '请求的资源不存在',
      server_error: '服务器暂时不可用，请稍后重试',
      validation_error: `输入验证失败：${err.details}`,
      business_error: err.details || '业务处理失败'
    }
    
    const message = errorMessages[err.type] || err.details || '操作失败，请稍后重试'
    showError(message)
    
    // 特殊错误处理
    if (err.type === 'unauthorized') {
      // 跳转到登录页
      router.push('/login')
    }
  }
}
```

### 4.3 错误处理最佳实践

1. **职责分离**：Request 层负责错误拦截和标准化，业务层负责错误展示
2. **用户友好**：根据错误类型提供清晰的用户提示信息
3. **日志记录**：在 Request 层记录详细错误信息用于调试
4. **优雅降级**：为不同错误类型提供合适的降级策略
5. **避免重复**：不在业务层重复处理已在 Request 层处理的错误
6. **错误恢复**：提供重试机制和替代方案

## 5. 安全规范

### 5.1 身份验证与授权
- 所有API接口必须进行身份验证和授权检查
- 使用JWT Token进行身份验证
- 实施基于角色的访问控制（RBAC）
- Token过期时间合理设置（建议2小时）

### 5.2 数据传输安全
- 敏感数据传输必须使用HTTPS
- 实施SSL/TLS加密
- 敏感字段在传输和存储时进行加密

### 5.3 接口安全防护
- 实施适当的速率限制防止滥用
- 输入验证和输出编码防止注入攻击
- 实施CORS策略控制跨域访问
- 添加请求签名验证（高安全要求场景）

### 5.4 日志与监控
- 记录所有API访问日志
- 监控异常请求模式
- 实施安全事件告警机制

## 6. 性能优化规范

### 6.1 缓存策略
- 合理使用HTTP缓存头
- 实施接口级缓存策略
- 静态资源CDN加速

### 6.2 分页与限制
- 大数据量接口必须实施分页
- 设置合理的默认页大小（建议20条）
- 限制最大页大小（建议不超过100条）

### 6.3 响应优化
- 只返回必要的字段
- 使用压缩传输（gzip）
- 异步处理长时间任务
