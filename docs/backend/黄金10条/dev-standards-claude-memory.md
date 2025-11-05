# 后端基础开发规范 (Claude Memory 版本)

> **核心理念**：统一代码风格，确保可读性、可维护性和Java 11兼容性

## 🎯 核心原则

1. **代码简化**：使用Lombok减少样板代码，提高开发效率
2. **命名规范**：见名知意，使用英文，避免误导性命名
3. **Java 11兼容**：严格禁止使用Java 16+特性，确保环境兼容性
4. **异常处理**：统一异常分类和处理原则，明确错误信息
5. **响应统一**：使用`ApiResponseWrapper`自动包装，避免手动构建

## 🛠️ Lombok 使用规范

### 推荐注解
- `@Data` - 生成getter、setter、toString、equals、hashCode方法
- `@Builder` - 生成建造者模式代码
- `@NoArgsConstructor` - 生成无参构造函数
- `@AllArgsConstructor` - 生成全参构造函数
- `@Slf4j` - 生成日志对象

### 谨慎使用
- `@EqualsAndHashCode` - 继承场景下需要设置callSuper=true
- `@ToString` - 避免在包含敏感信息的类上使用

### 标准示例
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

## 📝 命名规范

### 基本原则
- **见名知意**：命名应该清晰表达其用途和含义
- **避免缩写**：除非是广泛认知的缩写（如id、url、http等）
- **使用英文**：禁止使用拼音或中英文混合
- **避免误导**：命名不应引起歧义或误解

### 包命名 - 全部小写
```
com.company.project.module.layer
```

### 类命名 - 大驼峰 (PascalCase)
- **实体类**: `User`, `OrderItem`, `PaymentRecord`
- **服务类**: `UserService`, `OrderProcessingService`
- **控制器**: `UserController`, `OrderController`
- **异常类**: `UserNotFoundException`, `InvalidOrderException`

### 方法命名 - 小驼峰 (camelCase)
- **获取数据**: `getUser()`, `findUserById()`, `queryUserList()`
- **判断状态**: `isActive()`, `hasPermission()`, `canAccess()`
- **设置属性**: `setUsername()`, `updateStatus()`, `modifyPassword()`
- **业务操作**: `createUser()`, `processOrder()`, `calculateTotal()`

### 变量命名 - 小驼峰 (camelCase)
- **基本类型**: `userId`, `userName`, `totalAmount`
- **集合类型**: `userList`, `orderItems`, `configMap`
- **布尔类型**: `isActive`, `hasPermission`, `canEdit`

### 常量命名 - 全大写下划线分隔
```java
MAX_RETRY_COUNT = 3
DEFAULT_PAGE_SIZE = 20
USER_STATUS_ACTIVE = "ACTIVE"
```

### 枚举命名
```java
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    DELETED
}
```

## ☕ Java版本兼容性规范

### 🔴 强制要求
**项目Java版本：Java 11**
- 所有代码必须兼容Java 11
- 禁止使用Java 16+的新特性
- 代码提交前必须在Java 11环境下编译通过

### ✅ 允许的Java 11特性
- `String.isBlank()` 检查空白字符串（Java 11+）
- `Optional.isEmpty()` 检查Optional是否为空（Java 11+）
- `var` 关键字进行局部变量类型推断（Java 10+）
- Lambda表达式和Stream API（Java 8+）
- `Collectors.toList()` 将Stream转换为List

### ❌ 严格禁止的Java 16+特性
- `Stream.toList()` 方法（Java 16+）
- `Record` 类（Java 16+）
- `Pattern.matches()` 的增强API（Java 16+）
- `switch` 表达式（Java 14+）
- 文本块（Text Blocks，Java 15+）
- 密封类（Sealed Classes，Java 17+）

### Stream操作规范

**✅ 正确用法（Java 11兼容）：**
```java
// 转换为List
List<String> names = users.stream()
    .map(User::getName)
    .collect(Collectors.toList());  // ✅ Java 11兼容

// 过滤和计数
long count = users.stream()
    .filter(User::isActive)
    .count();  // ✅ Java 8+支持

// 分组操作
Map<String, List<User>> groupByStatus = users.stream()
    .collect(Collectors.groupingBy(User::getStatus));  // ✅ Java 8+支持
```

**❌ 错误用法（Java 16+特性）：**
```java
// 错误：使用Java 16+的Stream.toList()
List<String> names = users.stream()
    .map(User::getName)
    .toList();  // ❌ 编译错误：Java 16+特性
```

**必须导入：**
```java
import java.util.stream.Collectors;  // ✅ 必须导入Collectors
import java.util.stream.Stream;     // ✅ Stream操作需要
```

## ⚠️ 异常处理规范

### 异常分类
- **业务异常**: 继承RuntimeException，用于业务逻辑错误
- **系统异常**: 继承Exception，用于系统级错误
- **参数异常**: 使用IllegalArgumentException或自定义参数异常

### 异常命名
- **以Exception结尾**: 所有异常类都以Exception结尾
- **描述性命名**: 异常名称应该清楚描述错误类型
- **示例**: `UserNotFoundException`, `InvalidPasswordException`, `OrderProcessingException`

### 异常处理原则
- **及早发现**: 在参数校验阶段就发现并抛出异常
- **明确信息**: 异常信息应该明确指出错误原因和解决建议
- **统一处理**: 使用全局异常处理器统一处理异常响应
- **日志记录**: 重要异常必须记录日志，包含上下文信息

### 标准示例
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

## 📦 全局API响应规范

### 核心原则
**全局响应包装**：所有API响应必须使用`ApiResponseWrapper`自动包装，确保格式统一

### 自动包装机制
**✅ 必须使用**：
- 系统提供的 `ApiResponseWrapper` 自动包装机制
- 依赖系统自动处理Controller返回数据的包装
- 确保所有Controller方法返回可被`ApiResponseWrapper`处理的类型

**❌ 严格禁止**：
- 在Controller中手动包装返回数据
- 绕过 `ApiResponseWrapper` 自动包装机制
- 重复包装已经自动包装的响应数据
- 直接返回`ResponseEntity`等Spring原生响应类型
- 混用`ApiResult`和原始数据类型返回

### 开发约束
- **只允许** 直接返回业务数据对象，由系统自动完成包装
- **只允许** 通过全局异常处理器统一转换异常为`ApiResult`格式
- **只允许** 使用统一的错误码规范

### 自动包装规则
- 如果返回类型已经是`ApiResult`，则不进行二次包装
- 如果返回`null`，自动包装为`ApiResult.success()`
- 如果返回字符串，特殊处理避免序列化问题
- 其他类型数据自动包装为`ApiResult.success(data)`

### 推荐实践
```java
@RestController
public class UserController {

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        // ✅ 直接返回数据，ApiResponseWrapper会自动包装
        return userService.findById(id);
    }
}
```

## ✅ AI代码生成检查清单

### Lombok使用检查
- [ ] 是否使用了推荐的Lombok注解（@Data, @Builder, @Slf4j等）
- [ ] 是否谨慎使用了@EqualsAndHashCode和@ToString注解

### 命名规范检查
- [ ] 包名是否全部使用小写字母
- [ ] 类名是否使用大驼峰命名法（PascalCase）
- [ ] 方法和变量名是否使用小驼峰命名法（camelCase）
- [ ] 常量是否使用全大写下划线分隔
- [ ] 命名是否见名知意，避免缩写和拼音

### Java兼容性检查
- [ ] 是否使用`Collectors.toList()`而非`Stream.toList()`
- [ ] 是否导入了`java.util.stream.Collectors`
- [ ] 是否避免了Java 16+的新特性（Record、switch表达式等）
- [ ] Stream操作是否兼容Java 11
- [ ] 字符串操作是否使用Java 11兼容的方法

### 异常处理检查
- [ ] 业务异常是否继承RuntimeException
- [ ] 系统异常是否继承Exception
- [ ] 异常类名是否以Exception结尾
- [ ] 异常信息是否明确指出错误原因
- [ ] 是否使用全局异常处理器统一处理

### API响应处理检查
- [ ] 是否直接返回业务数据，而非手动包装
- [ ] 是否避免使用ResponseEntity等Spring原生响应类型
- [ ] 是否依赖ApiResponseWrapper自动包装机制
- [ ] 是否不重复包装已自动包装的响应数据

---

**遵循本规范可确保代码的一致性、可读性和可维护性，为项目的长期发展奠定坚实的基础。**