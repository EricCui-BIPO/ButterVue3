# API 接口规范

> **核心理念**：统一API接口设计，确保一致性、可维护性和机器友好性，为AI代码生成提供清晰规范

## 目录
- [1. Controller层规范](#1-controller层规范)
- [2. 分页查询规范](#2-分页查询规范)
- [3. 接口设计规范](#3-接口设计规范)
- [4. 统一响应格式](#4-统一响应格式)
- [5. 错误处理规范](#5-错误处理规范)
- [6. 安全规范](#6-安全规范)
- [7. 性能优化规范](#7-性能优化规范)
- [8. AI代码生成约束清单](#8-ai代码生成约束清单)

---

## 1. Controller层规范

### <semantic-tag>核心职责</semantic-tag>
- **HTTP请求处理**：接收HTTP请求、参数验证
- **业务逻辑调用**：调用Application层UseCase
- **HTTP响应返回**：返回标准化的HTTP响应
- **异常处理**：处理并转换业务异常为HTTP响应

### <semantic-tag>强制要求</semantic-tag>

**必须使用：**
- `@RestController` 注解
- `@RequestMapping` 定义基础路径，遵循RESTful规范
- `@RequiredArgsConstructor` 进行依赖注入
- `@Validated` 启用方法级验证

**严格禁止：**
- 在Controller中包含业务逻辑
- 直接操作数据库或调用Gateway层
- 复杂的参数处理和转换逻辑
- 手动构建JSON响应结构

### <semantic-tag>命名规范</semantic-tag>

**✅ 正确格式**：`{Entity}Controller`

**❌ 禁止格式**：
- `Manager`、`Handler`等非标准命名
- 一个Controller处理多个无关的实体类型

### <semantic-tag>标准实现模板</semantic-tag>

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
    public Pageable<EntityPageOutput> searchEntities(
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

## 2. 分页查询规范

### <semantic-tag>强制要求</semantic-tag>

**必须使用：**
- `com.i0.domain.core.pagination.Pageable<T>` 作为分页返回类型
- `@RequestParam` 接收分页参数，默认值：`page=0`, `size=20`
- 可选参数：`required = false`
- 通过UseCase获取分页数据，不直接操作数据库
- `ApiResponseWrapper` 自动包装响应

### <semantic-tag>标准实现模板</semantic-tag>

```java
@GetMapping
public Pageable<EntityPageOutput> searchEntities(
        @RequestParam(defaultValue = "0") Integer page,
        @RequestParam(defaultValue = "20") Integer size,
        @RequestParam(required = false) String keyword) {

    EntityPageInput request = EntityPageInput.builder()
            .page(page)
            .size(size)
            .keyword(keyword)
            .build();

    return searchEntitiesUseCase.execute(request);
}
```

### <semantic-tag>严格禁止行为</semantic-tag>

**❌ 禁止操作：**
- 使用`@RequestBody`接收分页参数
- 手动构建Map结构响应
- 自定义分页响应包装类（如`PageResponse<T>`）
- 复用普通输出DTO进行分页查询
- 直接调用Mapper或Repository

---

## 3. 接口设计规范

### <semantic-tag>RESTful API 规范</semantic-tag>

**资源命名规范：**
- 使用名词复数形式：`/api/talents`
- 使用小写字母和连字符：`/api/talent-profiles`
- 避免使用动词，通过HTTP方法表示操作

**HTTP 方法规范：**
| 方法 | 操作类型 | 幂等性 | 说明 |
|------|----------|--------|------|
| GET | 查询 | 幂等 | 获取资源 |
| POST | 创建 | 非幂等 | 创建新资源 |
| PUT | 更新 | 幂等 | 完整更新资源 |
| DELETE | 删除 | 幂等 | 删除资源 |

**路径设计示例：**
```
GET    /api/talents          # 获取人才列表
GET    /api/talents/{id}     # 获取单个人才
POST   /api/talents          # 创建人才
PUT    /api/talents/{id}     # 更新人才
DELETE /api/talents/{id}     # 删除人才
```

### <semantic-tag>版本控制规范</semantic-tag>

**版本控制原则：**
- API版本在URL路径中明确标识：`/api/v1/talents`
- 破坏性更改必须创建新版本

**✅ 正确示例：**
```
/api/v1/talents     # v1版本
/api/v2/talents     # v2版本（破坏性更改）
```

### <semantic-tag>HTTP状态码规范</semantic-tag>

**状态码使用指导：**
- **业务数据不存在** → 400 Bad Request（非404）
- **API接口不存在** → 404 Not Found
- **所有业务异常** → 400 Bad Request
- **系统异常** → 500 Internal Server Error
- **创建成功** → 201 Created
- **删除成功** → 204 No Content

---

## 4. 统一响应格式

### <semantic-tag>核心原则</semantic-tag>
**全局响应包装**：所有API响应必须使用`ApiResponseWrapper`自动包装，确保格式统一

### <semantic-tag>标准响应格式</semantic-tag>

**成功响应：**
```json
{
  "success": true,
  "data": { "id": "123", "name": "测试数据" },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**错误响应：**
```json
{
  "success": false,
  "errorCode": "ENTITY_NOT_FOUND",
  "errorMessage": "实体不存在",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### <semantic-tag>自动包装机制</semantic-tag>

**✅ 正确实现：**
```java
@RestController
public class EntityController {

    @GetMapping("/{id}")
    public EntityOutput getEntity(@PathVariable String id) {
        // ✅ 直接返回业务数据，系统自动包装
        return getEntityUseCase.execute(id);
    }
}
```

**❌ 错误实现：**
```java
@RestController
public class EntityController {

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEntity(@PathVariable String id) {
        // ❌ 错误：手动包装响应
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", getEntityUseCase.execute(id));
        return ResponseEntity.ok(response);
    }
}
```

---

## 5. 错误处理规范

### <semantic-tag>错误处理原则</semantic-tag>
- **统一格式**：所有异常必须转换为统一的错误响应格式
- **信息安全**：敏感信息不得暴露在错误消息中
- **日志记录**：记录详细错误日志用于调试和监控

### <semantic-tag>错误分类</semantic-tag>

| 错误类型 | HTTP状态码 | 说明 |
|---------|-----------|------|
| 业务错误 | 400 Bad Request | 业务逻辑验证失败 |
| 系统错误 | 500 Internal Server Error | 技术层面的异常 |
| 安全错误 | 401/403 | 认证授权相关错误 |

---

## 6. 安全规范

### <semantic-tag>身份验证与授权</semantic-tag>
- **强制验证**：所有API接口必须进行身份验证和授权检查
- **JWT Token**：使用JWT Token进行身份验证
- **RBAC**：实施基于角色的访问控制

### <semantic-tag>数据传输安全</semantic-tag>
- **HTTPS强制**：敏感数据传输必须使用HTTPS
- **加密传输**：实施SSL/TLS加密

### <semantic-tag>接口安全防护</semantic-tag>
- **速率限制**：实施适当的速率限制防止滥用
- **注入防护**：输入验证和输出编码防止注入攻击
- **CORS控制**：实施CORS策略控制跨域访问

---

## 7. 性能优化规范

### <semantic-tag>缓存策略</semantic-tag>
- **HTTP缓存**：合理使用HTTP缓存头
- **接口缓存**：实施接口级缓存策略

### <semantic-tag>分页与限制</semantic-tag>
- **强制分页**：大数据量接口必须实施分页
- **页大小设置**：默认页大小建议20条，最大不超过100条

### <semantic-tag>响应优化</semantic-tag>
- **字段精简**：只返回必要的字段
- **压缩传输**：使用压缩传输（gzip）

---

## 8. AI代码生成约束清单

### <semantic-tag>Controller生成约束</semantic-tag>

**必须检查项目：**
- [ ] Controller类名是否符合`{Entity}Controller`格式
- [ ] 是否使用了正确的注解：`@RestController`、`@RequestMapping`、`@RequiredArgsConstructor`、`@Validated`
- [ ] 是否保持Controller层简洁，不包含业务逻辑
- [ ] 是否只负责HTTP层面的处理

### <semantic-tag>分页查询生成约束</semantic-tag>

**必须检查项目：**
- [ ] 是否使用 `Page<T>` 作为分页返回类型
- [ ] 分页参数是否有默认值：`page=0`, `size=20`
- [ ] 可选参数是否使用`required = false`
- [ ] 是否通过UseCase获取分页数据，而非直接操作数据库
- [ ] 是否使用ApiResponseWrapper自动包装响应

### <semantic-tag>参数验证生成约束</semantic-tag>

**必须检查项目：**
- [ ] Controller类是否使用`@Validated`注解
- [ ] 方法参数是否使用`@Valid`注解
- [ ] 输入DTO是否包含@NotNull、@NotBlank等验证注解

### <semantic-tag>响应处理生成约束</semantic-tag>

**必须检查项目：**
- [ ] 是否直接返回业务数据，而非手动包装
- [ ] 是否避免使用ResponseEntity等Spring原生响应类型
- [ ] 是否依赖ApiResponseWrapper自动包装机制

### <semantic-tag>异常处理生成约束</semantic-tag>

**必须检查项目：**
- [ ] 业务异常是否返回400 Bad Request
- [ ] 系统异常是否返回500 Internal Server Error
- [ ] API接口不存在是否返回404 Not Found
- [ ] 是否使用统一的错误响应格式