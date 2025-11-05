# API 接口规范 (Claude Memory 版本)

> **核心理念**：统一API接口设计，确保一致性、可维护性和机器友好性

## 🎯 核心原则

1. **全局响应包装**：所有API响应必须使用`ApiResponseWrapper`自动包装
2. **Controller职责单一**：只负责HTTP层面处理，不包含业务逻辑
3. **RESTful设计**：使用名词复数形式，通过HTTP方法表示操作
4. **统一错误处理**：业务异常返回400，系统异常返回500，API不存在返回404

## 📋 Controller层规范

### 必须使用的注解
- `@RestController` - 定义REST控制器
- `@RequestMapping("/api/entities")` - 基础路径，遵循RESTful规范
- `@RequiredArgsConstructor` - 依赖注入
- `@Validated` - 启用方法级验证

### 严格禁止行为
- ❌ 在Controller中包含业务逻辑
- ❌ 直接操作数据库或调用Gateway层
- ❌ 手动构建JSON响应结构
- ❌ 使用`ResponseEntity`等Spring原生响应类型

### 标准实现模板
```java
@RestController
@RequestMapping("/api/entities")
@RequiredArgsConstructor
@Validated
public class EntityController {

    private final CreateEntityUseCase createEntityUseCase;
    private final SearchEntitiesUseCase searchEntitiesUseCase;

    @PostMapping
    public EntityOutput createEntity(@Valid @RequestBody CreateEntityInput input) {
        return createEntityUseCase.execute(input);
    }

    @GetMapping
    public Page<EntityPageOutput> searchEntities(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        EntityPageInput request = EntityPageInput.builder()
                .page(page)
                .size(size)
                .build();

        return searchEntitiesUseCase.execute(request);
    }
}
```

## 📄 分页查询规范

### 必须遵守
- 使用`Page<T>`作为分页返回类型
- 使用`@RequestParam`接收分页参数，默认值：`page=0`, `size=20`
- 可选参数使用`required = false`
- 通过UseCase获取分页数据，不直接操作数据库
- 依赖`ApiResponseWrapper`自动包装响应

### 严格禁止
- ❌ 使用`@RequestBody`接收分页参数
- ❌ 手动构建Map结构响应
- ❌ 自定义分页响应包装类（如`PageResponse<T>`）
- ❌ 复用普通输出DTO进行分页查询

## 🌐 RESTful API设计

### 资源命名规范
- 使用名词复数形式：`/api/talents`
- 使用小写字母和连字符：`/api/talent-profiles`
- 避免使用动词，通过HTTP方法表示操作

### HTTP方法规范
| 方法 | 操作类型 | 说明 |
|------|----------|------|
| GET | 查询 | 获取资源 |
| POST | 创建 | 创建新资源 |
| PUT | 更新 | 完整更新资源 |
| DELETE | 删除 | 删除资源 |

### 版本控制
- API版本在URL路径中明确标识：`/api/v1/talents`
- 破坏性更改必须创建新版本

## 📦 统一响应格式

### 成功响应
```json
{
  "success": true,
  "data": { "id": "123", "name": "测试数据" },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 错误响应
```json
{
  "success": false,
  "errorCode": "ENTITY_NOT_FOUND",
  "errorMessage": "实体不存在",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 自动包装机制
✅ **正确实现**：直接返回业务数据，系统自动包装
❌ **错误实现**：手动包装响应结构

## 🚨 错误处理规范

### HTTP状态码映射
- **业务数据不存在** → 400 Bad Request（非404）
- **API接口不存在** → 404 Not Found
- **所有业务异常** → 400 Bad Request
- **系统异常** → 500 Internal Server Error
- **创建成功** → 201 Created
- **删除成功** → 204 No Content

### 错误分类
| 错误类型 | HTTP状态码 | 说明 |
|---------|-----------|------|
| 业务错误 | 400 Bad Request | 业务逻辑验证失败 |
| 系统错误 | 500 Internal Server Error | 技术层面的异常 |
| 安全错误 | 401/403 | 认证授权相关错误 |

## 🔒 安全规范

### 身份验证与授权
- **强制验证**：所有API接口必须进行身份验证和授权检查
- **JWT Token**：使用JWT Token进行身份验证
- **RBAC**：实施基于角色的访问控制

### 数据传输安全
- **HTTPS强制**：敏感数据传输必须使用HTTPS
- **加密传输**：实施SSL/TLS加密

### 接口安全防护
- **速率限制**：实施适当的速率限制防止滥用
- **注入防护**：输入验证和输出编码防止注入攻击
- **CORS控制**：实施CORS策略控制跨域访问

## ⚡ 性能优化规范

### 缓存策略
- **HTTP缓存**：合理使用HTTP缓存头
- **接口缓存**：实施接口级缓存策略

### 分页与限制
- **强制分页**：大数据量接口必须实施分页
- **页大小设置**：默认页大小建议20条，最大不超过100条

### 响应优化
- **字段精简**：只返回必要的字段
- **压缩传输**：使用压缩传输（gzip）

## ✅ AI代码生成检查清单

### Controller生成检查
- [ ] Controller类名是否符合`{Entity}Controller`格式
- [ ] 是否使用了正确的注解：`@RestController`、`@RequestMapping`、`@RequiredArgsConstructor`、`@Validated`
- [ ] 是否保持Controller层简洁，不包含业务逻辑
- [ ] 是否只负责HTTP层面的处理

### 分页查询检查
- [ ] 是否使用 `Page<T>` 作为分页返回类型
- [ ] 分页参数是否有默认值：`page=0`, `size=20`
- [ ] 可选参数是否使用`required = false`
- [ ] 是否通过UseCase获取分页数据，而非直接操作数据库
- [ ] 是否使用ApiResponseWrapper自动包装响应

### 参数验证检查
- [ ] Controller类是否使用`@Validated`注解
- [ ] 方法参数是否使用`@Valid`注解
- [ ] 输入DTO是否包含@NotNull、@NotBlank等验证注解

### 响应处理检查
- [ ] 是否直接返回业务数据，而非手动包装
- [ ] 是否避免使用ResponseEntity等Spring原生响应类型
- [ ] 是否依赖ApiResponseWrapper自动包装机制

### 异常处理检查
- [ ] 业务异常是否返回400 Bad Request
- [ ] 系统异常是否返回500 Internal Server Error
- [ ] API接口不存在是否返回404 Not Found
- [ ] 是否使用统一的错误响应格式

---

**遵循本规范可确保API接口的一致性、可维护性和机器友好性，为AI代码生成提供清晰的指导原则。**