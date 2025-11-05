# 业务函数注册系统使用指南

## 概述

本系统提供了一个完整的业务函数注册机制，允许将本地业务API注册到AI上下文中，使AI能够动态识别并调用具体的业务接口。

## 核心组件

### 1. BusinessFunction (领域值对象)
描述一个可被AI调用的业务函数，包含：
- 函数名称和描述
- 参数定义（类型、描述、是否必需）
- 函数处理器（FunctionHandler）

### 2. BusinessFunctionRegistry (领域服务)
业务函数注册器，负责：
- 管理所有已注册的业务函数
- 执行指定的业务函数
- 提供函数查询和管理功能

### 3. AIConversationService (应用服务)
AI对话服务，已集成：
- 智能工具调用检测
- 简化的单轮对话处理
- 自动摘要生成
- 参数解析和结果处理

### 4. BusinessFunctionManagerService (应用服务)
业务函数管理服务，提供：
- 函数注册和注销
- 同步/异步函数执行
- 参数验证
- 批量执行

## 使用方法

### 1. 定义业务函数

```java
BusinessFunction createUserFunction = BusinessFunction.builder()
    .name("create_user")
    .description("创建一个新的用户账户")
    .parameter("username", BusinessFunction.PropertyDefinition.builder()
        .type("string")
        .description("用户名，长度3-20个字符")
        .build())
    .parameter("email", BusinessFunction.PropertyDefinition.builder()
        .type("string")
        .description("邮箱地址")
        .build())
    .required("username")
    .required("email")
    .handler(arguments -> {
        String username = (String) arguments.get("username");
        String email = (String) arguments.get("email");

        // 实际业务逻辑
        // userService.createUser(username, email);

        return BusinessFunction.FunctionCallResult.success(
            "用户创建成功",
            Map.of("username", username, "email", email)
        );
    })
    .build();
```

### 2. 注册业务函数

```java
@Autowired
private BusinessFunctionRegistry registry;

// 注册单个函数
registry.register(createUserFunction);

// 批量注册
registry.registerAll(Arrays.asList(function1, function2));
```

### 3. 通过配置类自动注册

在 `BusinessFunctionConfiguration` 中添加函数定义：

```java
@Configuration
public class BusinessFunctionConfiguration {

    @Bean
    public BusinessFunctionRegistry businessFunctionRegistry() {
        BusinessFunctionRegistry registry = new BusinessFunctionRegistry();
        registerBasicBusinessFunctions(registry);
        return registry;
    }

    private void registerBasicBusinessFunctions(BusinessFunctionRegistry registry) {
        // 注册基础业务函数
    }
}
```

### 4. 使用业务函数管理服务

```java
@Autowired
private BusinessFunctionManagerService functionManager;

// 执行函数
BusinessFunction.FunctionCallResult result = functionManager.executeFunction(
    "create_user",
    Map.of("username", "testuser", "email", "test@example.com")
);

// 异步执行
CompletableFuture<BusinessFunction.FunctionCallResult> future =
    functionManager.executeFunctionAsync("create_user", arguments);

// 验证参数
List<String> errors = functionManager.validateFunctionParameters("create_user", arguments);
```

## AI对话中的函数调用

当用户与AI对话时，AI会根据已注册的函数定义自动识别需要调用的业务函数：

1. **用户输入**: "帮我创建一个用户，用户名是test，邮箱是test@example.com"

2. **AI分析**: 识别出需要调用 `create_user` 函数

3. **参数提取**: 提取 username="test", email="test@example.com"

4. **函数执行**: 调用注册的业务函数处理器

5. **结果返回**: 将执行结果返回给AI，AI生成自然语言回复

## 已注册的示例函数

系统已预注册以下示例函数：

### 用户管理函数
- `create_user`: 创建用户账户
- `find_user`: 查询用户信息

### 实体管理函数
- `create_entity`: 创建业务实体
- `find_entity`: 查询实体信息

### 查询函数
- `search`: 系统搜索
- `get_statistics`: 获取统计信息

## 最佳实践

### 1. 函数命名
- 使用描述性的函数名
- 采用动词+名词的模式（如：create_user, find_entity）
- 保持命名一致性

### 2. 参数设计
- 提供清晰的参数描述
- 合理设置必需参数
- 为可选参数提供默认值
- 使用枚举限制参数范围

### 3. 错误处理
- 函数处理器中要捕获异常
- 返回有意义的错误信息
- 避免敏感信息泄露

### 4. 结果返回
- 提供用户友好的执行结果描述
- 返回结构化数据供后续处理
- 保持结果格式一致性

## 扩展指南

### 添加新的业务函数类型

1. 在对应的配置类中添加函数定义
2. 实现具体的业务逻辑
3. 添加相应的测试用例
4. 更新文档说明

### 集成外部服务

```java
.handler(arguments -> {
    // 调用外部服务
    ExternalServiceResponse response = externalService.process(arguments);

    if (response.isSuccess()) {
        return BusinessFunction.FunctionCallResult.success(
            "操作成功",
            response.getData()
        );
    } else {
        return BusinessFunction.FunctionCallResult.failure(
            "操作失败：" + response.getErrorMessage()
        );
    }
})
```

## 注意事项

1. **安全性**: 函数处理器中要注意参数验证和权限控制
2. **性能**: 避免在函数处理器中执行耗时操作
3. **事务**: 涉及数据库操作的函数要考虑事务管理
4. **日志**: 记录函数调用和执行结果，便于调试和监控
5. **测试**: 为每个业务函数编写单元测试和集成测试

## 故障排除

### 常见问题

1. **函数未找到**: 检查函数名称是否正确注册
2. **参数验证失败**: 检查参数类型和必需性设置
3. **函数执行超时**: 检查函数处理器性能
4. **AI未识别函数**: 检查函数描述是否清晰准确

### 调试方法

1. 启用DEBUG日志级别查看详细执行信息
2. 使用 `BusinessFunctionManagerService` 的验证方法检查参数
3. 通过 `getAllFunctions()` 查看已注册的函数列表
4. 运行集成测试验证函数调用流程