# Core Development Standards (黄金 10 条)

## 架构与分层
1. **Clean Architecture + DDD**：分层为 Domain → Application → Gateway。
2. **依赖倒置**：外层可依赖内层，Domain 禁止依赖框架/数据库。

## Domain 层
3. **Domain 层纯净性**：只保留业务逻辑、实体/值对象/聚合；禁止 ORM 注解、审计字段。
4. **Repository 接口定义**：必须在 Domain 层定义，Gateway 层实现。

## Application 层
5. **UseCase 单一职责**：只有一个 `execute()` 方法，不得包含业务规则或直接数据访问。
6. **DTO 规范**：输入/输出 DTO 独立，分页 DTO 必须独立定义，不得复用普通 DTO。
7. **Domain类显式导入**：必须在代码中显式导入所有使用的domain类，包括实体、值对象、Repository接口等，避免编译错误。

## Gateway 层
8. **数据访问约束**：Mapper 只继承 `BaseMapper<T>`，禁止写 @Select/@Update 等自定义 SQL。
9. **ACL 防腐层**：跨领域调用必须通过 ACL，统一完成对象和异常转换。

## 基础设施
10. **数据库标准**：所有表必须有 `created_at, updated_at, is_deleted`，软删除用逻辑删除。
11. **统一规范**：全局异常处理、统一 API 响应封装、统一命名规则（包小写，类大驼峰，常量全大写）。
12. **Java版本兼容性**：必须使用Java 11兼容语法，禁止使用Java 16+特性（如Stream.toList()），详细规范参考[Java版本兼容性规范](../../dev-standards.md#3-java版本兼容性规范)。
