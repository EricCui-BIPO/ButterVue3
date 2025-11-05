# 后端基础开发规范

## 目录

- [1. 代码简化工具](#1-代码简化工具)
  - [1.1 Lombok 使用规范](#11-lombok-使用规范)
- [2. 命名规范](#2-命名规范)
  - [2.1 基本命名原则](#21-基本命名原则)
  - [2.2 包命名规范](#22-包命名规范)
  - [2.3 类命名规范](#23-类命名规范)
  - [2.4 方法命名规范](#24-方法命名规范)
  - [2.5 变量命名规范](#25-变量命名规范)
  - [2.6 常量命名规范](#26-常量命名规范)
  - [2.7 枚举命名规范](#27-枚举命名规范)
- [3. 异常处理规范](#3-异常处理规范)
  - [3.1 异常分类](#31-异常分类)
  - [3.2 异常命名](#32-异常命名)
  - [3.3 异常处理原则](#33-异常处理原则)
- [4. 全局API响应规范](#4-全局api响应规范)
  - [4.1 统一响应格式](#41-统一响应格式)
  - [4.2 响应创建规范](#42-响应创建规范)
  - [4.3 全局响应包装器](#43-全局响应包装器)
  - [4.4 API响应规范约束](#44-api响应规范约束)

---

## 1. 代码简化工具

### 1.1 Lombok 使用规范
- **使用 Lombok 减少样板代码**: 采用Lombok注解自动生成getter、setter、构造函数等样板代码
- **推荐注解**:
    - `@Data`: 生成getter、setter、toString、equals、hashCode方法
    - `@Builder`: 生成建造者模式代码
    - `@NoArgsConstructor`: 生成无参构造函数
    - `@AllArgsConstructor`: 生成全参构造函数
    - `@Slf4j`: 生成日志对象
- **谨慎使用的注解**:
    - `@EqualsAndHashCode`: 在继承场景下需要设置callSuper=true
    - `@ToString`: 避免在包含敏感信息的类上使用

**Lombok使用示例：**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class User {
    private Long id;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    
    public void logUserInfo() {
        log.info("User created: {}", username);
    }
}
```

## 2. 命名规范

### 2.1 基本命名原则
- **见名知意**: 命名应该清晰表达其用途和含义
- **避免缩写**: 除非是广泛认知的缩写（如id、url、http等）
- **使用英文**: 禁止使用拼音或中英文混合
- **避免误导**: 命名不应引起歧义或误解

### 2.2 包命名规范
- **全部小写**: 包名统一使用小写字母
- **点分隔**: 使用点号分隔层级
- **域名倒置**: 采用域名倒置的命名方式
- **示例**: `com.company.project.module.layer`

### 2.3 类命名规范
- **大驼峰命名法**: 首字母大写的驼峰命名（PascalCase）
- **名词或名词短语**: 类名应该是名词或名词短语
- **具体示例**:
    - 实体类: `User`, `OrderItem`, `PaymentRecord`
    - 服务类: `UserService`, `OrderProcessingService`
    - 控制器: `UserController`, `OrderController`
    - 异常类: `UserNotFoundException`, `InvalidOrderException`

### 2.4 方法命名规范
- **小驼峰命名法**: 首字母小写的驼峰命名（camelCase）
- **动词或动词短语**: 方法名应该是动词或动词短语
- **具体示例**:
    - 获取数据: `getUser()`, `findUserById()`, `queryUserList()`
    - 判断状态: `isActive()`, `hasPermission()`, `canAccess()`
    - 设置属性: `setUsername()`, `updateStatus()`, `modifyPassword()`
    - 业务操作: `createUser()`, `processOrder()`, `calculateTotal()`

### 2.5 变量命名规范
- **小驼峰命名法**: 首字母小写的驼峰命名（camelCase）
- **名词或名词短语**: 变量名应该是名词或名词短语
- **具体示例**:
    - 基本类型: `userId`, `userName`, `totalAmount`
    - 集合类型: `userList`, `orderItems`, `configMap`
    - 布尔类型: `isActive`, `hasPermission`, `canEdit`

### 2.6 常量命名规范
- **全大写**: 使用全大写字母
- **下划线分隔**: 单词间使用下划线分隔
- **具体示例**:
    - `MAX_RETRY_COUNT = 3`
    - `DEFAULT_PAGE_SIZE = 20`
    - `USER_STATUS_ACTIVE = "ACTIVE"`

### 2.7 枚举命名规范
- **枚举类**: 使用大驼峰命名法，通常以复数形式或Type结尾
- **枚举值**: 使用全大写字母，下划线分隔
- **具体示例**:
```java
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    DELETED
}

public enum OrderType {
    ONLINE_ORDER,
    OFFLINE_ORDER,
    BULK_ORDER
}
```

## 3. 异常处理规范

### 3.1 异常分类
- **业务异常**: 继承RuntimeException，用于业务逻辑错误
- **系统异常**: 继承Exception，用于系统级错误
- **参数异常**: 使用IllegalArgumentException或自定义参数异常

### 3.2 异常命名
- **以Exception结尾**: 所有异常类都以Exception结尾
- **描述性命名**: 异常名称应该清楚描述错误类型
- **具体示例**: `UserNotFoundException`, `InvalidPasswordException`, `OrderProcessingException`

### 3.3 异常处理原则
- **及早发现**: 在参数校验阶段就发现并抛出异常
- **明确信息**: 异常信息应该明确指出错误原因和解决建议
- **统一处理**: 使用全局异常处理器统一处理异常响应
- **日志记录**: 重要异常必须记录日志，包含上下文信息

```java
// 业务异常示例
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long userId) {
        super(String.format("User not found with id: %d", userId));
    }
}

// 使用示例
public User findUserById(Long userId) {
    if (userId == null) {
        throw new IllegalArgumentException("User ID cannot be null");
    }
    
    User user = userRepository.findById(userId);
    if (user == null) {
        throw new UserNotFoundException(userId);
    }
    
    return user;
}
```

## 4. 全局API响应规范

### 4.1 统一响应格式
所有API接口必须使用统一的响应格式，通过`ApiResult<T>`类进行封装。

**ApiResult结构**:
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {
    private String errorCode;      // 错误码
    private String errorMessage;   // 错误信息
    private Object errParams;      // 错误参数
    private T data;               // 响应数据
}
```

### 4.2 响应创建规范
- **成功响应**: 使用`ApiResult.success(data)`或`ApiResult.success()`
- **错误响应**: 使用`ApiResult.error(errorCode, errorMessage)`或`ApiResult.error(errorCode, errorMessage, errParams)`
- **状态检查**: 使用`isSuccess()`方法判断响应是否成功

**使用示例**:
```java
// Controller中的使用
@RestController
public class UserController {
    
    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        // 直接返回数据，ApiResponseWrapper会自动包装
        return userService.findById(id);
    }
}
```

### 4.3 全局响应包装器

#### API响应包装规范 ✅

**自动包装机制：**
- ✅ **必须** 使用系统提供的 `ApiResponseWrapper` 自动包装机制
- ✅ **必须** 依赖系统自动处理Controller返回数据的包装
- ✅ **必须** 确保所有Controller方法返回可被`ApiResponseWrapper`处理的类型

**严格禁止的操作：**
- ❌ **严格禁止** 在Controller中手动包装返回数据
- ❌ **严格禁止** 绕过 `ApiResponseWrapper` 自动包装机制
- ❌ **严格禁止** 重复包装已经自动包装的响应数据
- ❌ **严格禁止** 在Controller中直接返回`ResponseEntity`等Spring原生响应类型
- ❌ **严格禁止** 在Controller中混用`ApiResult`和原始数据类型返回
- ❌ **严格禁止** 绕过全局响应包装器直接操作HTTP响应

**开发约束：**
- **只允许** 直接返回业务数据对象，由系统自动完成包装
- **只允许** 通过全局异常处理器统一转换异常为`ApiResult`格式
- **只允许** 使用统一的错误码规范（参考`ResponseCode`枚举）

**自动包装规则：**
- 如果返回类型已经是`ApiResult`，则不进行二次包装
- 如果返回`null`，自动包装为`ApiResult.success()`
- 如果返回字符串，特殊处理避免序列化问题
- 其他类型数据自动包装为`ApiResult.success(data)`

**推荐实践：**
- ✅ 简单查询接口直接返回数据对象，让包装器自动处理
- ✅ 复杂业务逻辑可手动构造`ApiResult`以提供更详细的错误信息
- ✅ 使用`ApiResult.isSuccess()`进行响应状态判断

**包装器配置**:
```java
@Slf4j
@ControllerAdvice
public class ApiResponseWrapper implements ResponseBodyAdvice<Object> {
    
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 如果返回类型已经是ApiResult，则不需要再次包装
        return !returnType.getParameterType().equals(ApiResult.class);
    }
    
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, 
                                  MediaType selectedContentType, Class selectedConverterType, 
                                  ServerHttpRequest request, ServerHttpResponse response) {
        // 自动包装逻辑
        if (body instanceof String) {
            return ApiResult.success(body).toString();
        }
        if (body == null) {
            return ApiResult.success();
        }
        return ApiResult.success(body);
    }
}
```