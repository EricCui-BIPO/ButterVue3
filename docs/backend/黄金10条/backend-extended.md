# Extended Development Standards

## 架构与目录结构
- frameworks 模块依赖规则：Domain 只能依赖 `frameworks:domain.core`；Application 可依赖 `persistence.spring`；Gateway 可依赖所有。
- 标准目录结构：每个领域模块包含 domain、application、gateway 三层，应用入口模块只负责全局配置/生命周期。

## Domain 层
- 值对象必须不可变；通过属性值判断相等。
- 枚举需包含状态转换逻辑，避免“哑枚举”。
- 领域服务只处理跨实体业务规则和复杂计算，不允许依赖基础设施。

## Application 层
- UseCase 必须使用 `@Component/@Service` + `@RequiredArgsConstructor`，写操作用 `@Transactional`。
- 异常处理：DomainException → 转换为 BusinessException/ApplicationException。
- DTO 必须包含校验注解（@NotNull, @NotBlank 等），分页查询返回 `Pageable<{Entity}PageOutput>`。

## Gateway 层
- 数据访问必须使用 `LambdaQueryWrapper`，避免 if 判断。
- 分页必须用 MyBatis-Plus `Page<T>`，页码从 1 开始。
- 逻辑删除必须用 `@TableLogic(value="false", delval="true")`。
- Repository 实现推荐继承 `ServiceImpl<M,T>`，优先用 exists() 替代 count() > 0。
- Controller层必须遵循[API接口规范](../api-standards.md)，使用统一的`Pageable<T>`接口处理分页。

## 数据库规范
- 命名：表名复数，下划线分隔；索引 `idx_表_字段`；外键 `fk_表_关联表`。
- 字段设计：金额用 DECIMAL，禁止 FLOAT/DOUBLE；时间字段用 DATETIME/TIMESTAMP。
- 数据迁移：必须使用 Flyway，文件命名 `V{版本号}__{描述}.sql`，禁止修改已提交文件。

## 开发基础规范
- Lombok 推荐 `@Data, @Builder, @Slf4j`，谨慎用 `@ToString`。
- 异常分类：业务异常继承 RuntimeException，系统异常继承 Exception。
- API 响应统一使用 `ApiResult<T>`，提供 success()/error() 工厂方法。

## 测试规范
- 遵循 TDD：Red-Green-Refactor 循环。
- 覆盖率要求：单元测试 ≥90%，集成测试 ≥80%，关键路径 100%。
- Controller 层禁止重复写单元测试，必须优先用集成测试覆盖完整流程。
- 集成测试异常断言必须符合全局异常处理规范（400/404/500 映射）。  
