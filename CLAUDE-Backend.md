# 后端开发规范 (Backend Development Standards)

## 需要长期记忆
核心规范(黄金10条): @docs/backend/黄金10条/backend-core.md
拓展规范(黄金10条): @docs/backend/黄金10条/backend-extended.md
API标准规范(黄金10条): @docs/backend/黄金10条/backend-api-standards-claude-memory.md
开发基础规范(黄金10条): @docs/backend/黄金10条/dev-standards-claude-memory.md

系统架构: @docs/backend/architecture-overview.md
数据库规范: @docs/backend/database_standards.md
测试规范: @docs/backend/testing-standards.md
## 目录
- [1. 架构规范](#1-架构规范)
- [2. Domain层规范](#2-domain层规范)
- [3. Application层规范](#3-application层规范)
- [4. Gateway层规范](#4-gateway层规范)
- [5. 数据库规范](#5-数据库规范)
- [6. 测试规范](#6-测试规范)
- [7. API规范](#7-api规范)
- [8. AI特别约束清单](#8-ai特别约束清单)

---

## 1. 架构规范

### 架构模式
**必须**使用 Clean Architecture + DDD
**必须**遵循三层架构：Domain → Application → Gateway
**必须**遵循依赖倒置原则：外层依赖内层，内层不依赖外层

### 分层职责
**Domain层（内层）**：
- 必须只使用JDK基础库，禁止依赖外部框架
- 必须专注业务逻辑，禁止包含审计字段和持久化注解
- 必须通过业务方法封装属性变更，在方法中实现业务规则验证
- 必须定义Repository接口，但不包含实现细节

**Application层（中层）**：
- 必须单一职责：一个UseCase只处理一个具体的业务场景
- 必须有且仅有一个public方法：`execute()`
- 必须使用`@Component`/`@Service`、`@RequiredArgsConstructor`注解
- 必须在数据写入操作中使用`@Transactional`，查询操作禁止使用
- 必须为分页查询创建专门的`{Entity}PageOutput`类

**Gateway层（外层）**：
- 必须继承MyBatis-Plus的BaseMapper<T>接口，禁止自定义SQL方法
- 必须使用LambdaQueryWrapper构建查询条件，禁止硬编码SQL
- 必须继承ServiceImpl<M,T>，优先使用内置方法
- 必须使用`@TableLogic(value="false", delval="true")`配置逻辑删除
- 必须通过ACL层进行跨领域调用，禁止直接依赖其他领域

### Frameworks模块依赖规则
**Domain层** → 只能依赖 `frameworks:domain.core`
**Application层** → 可依赖 `frameworks:domain.core` + `frameworks:persistence.spring`
**Gateway层** → 可依赖所有frameworks模块
**测试代码** → 可依赖 `frameworks:test.context`（仅限测试范围）

**禁止**业务代码直接使用框架API
**禁止**未声明依赖就使用frameworks组件
**禁止**使用未批准的framework模块

---

## 2. Domain层规范

### 领域对象设计
**实体（Entity）**：
- 必须包含唯一标识符（ID）、业务属性、业务方法
- 必须通过业务方法封装属性变更
- 必须在业务方法中实现业务规则验证
- 必须保持对象的一致性状态

**值对象（Value Object）**：
- 必须保证不可变性（immutable）
- 必须通过属性值判断相等性，无标识符
- 必须自验证（创建时验证自身有效性）

**枚举（Enum）**：
- 必须包含状态转换逻辑，避免"哑枚举"
- 必须提供业务逻辑方法

**禁止**在Domain实体中包含审计字段
**禁止**包含ORM注解或SQL语句
**禁止**依赖任何外部框架

### Repository接口
**必须**在Domain层定义
**必须**只包含业务需要的操作
**必须**使用领域对象作为参数和返回值
**禁止**包含任何实现细节

### 领域异常
**必须**使用领域异常而非系统异常
**必须**使用业务领域语言描述异常消息
**必须**提供有意义的错误代码

---

## 3. Application层规范

### UseCase设计
**必须**符合命名规范：`{Action}{Entity}UseCase`
**必须**只有一个public方法：`execute()`
**必须**使用`@Component`/`@Service`、`@RequiredArgsConstructor`
**必须**在写入操作使用`@Transactional`，查询操作禁止使用
**必须**通过Repository接口访问数据，禁止直接数据访问

**禁止**多个public业务方法
**禁止**包含业务规则（必须在Domain层实现）
**禁止**直接数据访问或SQL逻辑
**禁止**UI逻辑处理

### DTO设计
**输入DTO**：`Create{Entity}Input`、`Update{Entity}Input`、`{Entity}PageInput`
**输出DTO**：`{Entity}Output`、`{Entity}PageOutput`

**必须**为分页查询创建专门的`{Entity}PageOutput`类
**必须**使用`Pageable<{Entity}PageOutput>`作为返回类型
**必须**提供`from(Entity entity)`静态转换方法
**必须**使用`Page.map()`进行批量转换

**禁止**复用普通输出DTO进行分页查询
**禁止**在DTO中包含分页元数据

### 验证注解
**必须**在输入DTO中包含验证注解：@NotNull、@NotBlank、@Size等
**必须**使用@Valid进行级联验证

---

## 4. Gateway层规范

### MyBatis-Plus使用
**必须**继承BaseMapper<T>，保持接口简洁
**禁止**在Mapper接口中添加带@Select、@Insert等注解的自定义方法
**禁止**硬编码SQL语句
**必须**使用LambdaQueryWrapper构建类型安全的查询条件
**必须**使用条件方法避免if判断

### ServiceImpl使用
**必须**继承ServiceImpl<M,T>
**必须**优先使用exists()而非count() > 0
**必须**使用lambdaQuery()简化查询构建
**必须**使用saveOrUpdate()、saveBatch()等批量操作方法

### 逻辑删除
**必须**配置：`@TableLogic(value="false", delval="true")`
**必须**配合`@TableField`指定字段名
**必须**使用deleteById()进行逻辑删除
**禁止**手动设置删除标记

### ACL跨领域调用
**必须**通过ACL适配器进行所有跨领域调用
**必须**在`gateway/acl/`目录下创建适配器
**必须**将外部领域对象转换为当前领域对象
**必须**将外部领域异常转换为当前领域异常
**禁止**直接依赖其他领域的domain或gateway模块
**禁止**将外部领域对象直接传递给当前领域

---

## 5. 数据库规范

### 表设计
**必须**包含审计字段：
- `created_at`、`updated_at` - 创建和更新时间
- `creator_id`、`updater_id` - 创建者和更新者ID
- `creator`、`updater` - 创建者和更新者姓名
- `is_deleted` - 逻辑删除标记

**必须**使用软删除，禁止物理删除数据
**必须**使用Flyway进行数据库版本控制
**必须**使用`V{版本号}__{描述}.sql`格式命名迁移文件

### 命名规范
**表名**：小写字母，下划线分隔，名词复数形式
**字段名**：小写字母，下划线分隔
**索引名**：`idx_表名_字段名`
**外键名**：`fk_表名_关联表名`

### 数据类型
**必须**使用VARCHAR作为主键类型
**必须**使用DECIMAL处理金额，禁止使用FLOAT/DOUBLE
**必须**使用DATETIME/TIMESTAMP处理时间
**必须**使用TEXT处理大文本

---

## 6. 测试规范

### TDD开发流程
**必须**遵循Red-Green-Refactor循环
**必须**先写测试再写实现代码
**必须**确保编译通过后再关注测试

### 测试覆盖率
**必须**达到：
- 单元测试覆盖率 ≥ 90%
- 集成测试覆盖率 ≥ 80%
- 关键业务路径 100% 覆盖

### 测试优先级
**必须**优先解决编译错误
**必须**在主干分支只覆盖核心业务场景
**必须**优先使用集成测试覆盖Gateway层Controller
**禁止**为已有集成测试覆盖的Controller编写重复单元测试

### 集成测试异常断言
**必须**遵循HTTP状态码映射：
- 业务异常 = 400 Bad Request
- 参数验证异常 = 400 Bad Request
- 系统异常 = 500 Internal Server Error
- 接口不存在 = 404 Not Found

**禁止**业务数据不存在返回404
**禁止**业务异常返回500

---

## 7. API规范

### RESTful API
**必须**使用名词复数形式表示资源：`/api/talents`
**必须**使用小写字母和连字符：`/api/talent-profiles`
**必须**通过HTTP方法表示操作，避免使用动词

**必须**使用版本控制：`/api/v1/talents`

### 统一响应格式
**成功响应**：
```json
{
  "success": true,
  "data": { },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**分页响应**：
```json
{
  "success": true,
  "data": {
    "content": [ ],
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

**错误响应**：
```json
{
  "success": false,
  "errorCode": "6000",
  "errorMessage": "请求参数错误",
  "data": null,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### 全局响应包装
**必须**使用系统提供的`ApiResponseWrapper`自动包装机制
**必须**依赖系统自动处理Controller返回数据的包装
**必须**确保所有Controller方法返回可被`ApiResponseWrapper`处理的类型

**禁止**在Controller中手动包装返回数据
**禁止**绕过`ApiResponseWrapper`自动包装机制
**禁止**重复包装已经自动包装的响应数据
**禁止**在Controller中直接返回`ResponseEntity`等Spring原生响应类型

---

## 8. AI特别约束清单

### 8.1 架构分层约束
**必须检查项目**：
- [ ] 是否严格遵循三层架构（Domain、Application、Gateway）
- [ ] 依赖方向是否正确（外层依赖内层）
- [ ] Domain层是否只依赖frameworks:domain.core
- [ ] Application层是否正确依赖frameworks模块
- [ ] Gateway层是否完整依赖所有必要frameworks模块
- [ ] 是否存在内层依赖外层的违规情况

### 8.2 领域对象生成约束
**必须检查项目**：
- [ ] 领域对象是否只使用JDK基础库，不依赖外部框架
- [ ] 是否包含审计字段（createdAt、updatedAt等）
- [ ] 是否包含ORM注解或SQL语句
- [ ] 是否通过业务方法封装属性变更
- [ ] 是否在业务方法中实现了业务规则验证
- [ ] 值对象是否保证不可变性
- [ ] 枚举是否包含业务逻辑方法

### 8.3 UseCase生成约束
**必须检查项目**：
- [ ] UseCase类名是否符合`{Action}{Entity}UseCase`格式
- [ ] 是否只有一个public方法`execute()`
- [ ] 是否正确使用了`@Component`/`@Service`、`@RequiredArgsConstructor`
- [ ] 写入操作是否使用了`@Transactional`，查询操作是否未使用
- [ ] 是否通过Repository接口访问数据，而非直接操作数据库
- [ ] 业务规则是否在Domain层实现，而非UseCase中

### 8.4 DTO生成约束
**必须检查项目**：
- [ ] 分页查询是否创建了专门的`{Entity}PageOutput`类
- [ ] 分页输出DTO是否只包含实体字段，不包含分页元数据
- [ ] UseCase返回类型是否使用`Pageable<{Entity}PageOutput>`
- [ ] 是否提供了`from(Entity entity)`静态转换方法
- [ ] 是否使用`Page.map()`进行批量转换
- [ ] 目录结构是否正确（`application/dto/output/`）
- [ ] 输入DTO是否包含适当的验证注解

### 8.5 Gateway层生成约束
**必须检查项目**：
- [ ] Mapper接口是否继承BaseMapper<T>
- [ ] 是否包含任何自定义SQL方法
- [ ] 是否使用了@Select、@Insert等注解
- [ ] 是否使用LambdaQueryWrapper构建查询条件
- [ ] 是否继承ServiceImpl<M,T>
- [ ] 逻辑删除配置是否完整（value和delval）
- [ ] ACL适配器是否正确实现

### 8.6 数据库生成约束
**必须检查项目**：
- [ ] 是否包含所有必需的审计字段
- [ ] 是否使用逻辑删除而非物理删除
- [ ] 字段命名是否符合规范
- [ ] 数据类型选择是否正确
- [ ] 是否使用Flyway迁移脚本

### 8.7 测试生成约束
**必须检查项目**：
- [ ] 是否为每个UseCase生成了对应的测试类
- [ ] 测试类名是否符合`{UseCaseName}Test`格式
- [ ] 是否测试了正常业务流程
- [ ] 是否测试了异常情况
- [ ] 是否测试了输入验证
- [ ] 是否使用了Mockito进行依赖mock
- [ ] 集成测试是否正确导入TestMybatisPlusConfig

### 8.8 代码质量约束
**必须检查项目**：
- [ ] 是否在build.gradle中正确声明frameworks依赖
- [ ] 业务代码是否使用frameworks抽象接口而非框架API
- [ ] 是否使用了未声明的frameworks模块
- [ ] 测试代码是否正确依赖frameworks:test.context
- [ ] 是否存在循环依赖或不合规的依赖关系
- [ ] 代码覆盖率是否达到要求标准

### 8.9 异常处理约束
**必须检查项目**：
- [ ] 是否使用统一的异常处理机制
- [ ] 业务异常是否正确映射为HTTP状态码
- [ ] 集成测试异常断言是否符合规范
- [ ] 是否提供有意义的错误信息
- [ ] 是否过度使用异常控制流程