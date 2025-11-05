# 集成测试物理删除指南

## 概述

`BasicIntegrationTest` 提供了通用的物理删除功能，用于在集成测试中彻底清理测试数据，避免逻辑删除导致的主键冲突问题。

## 核心功能

### `clearUpTestData(Class<?>... entityClasses)`

这是核心方法，用于物理删除指定实体类对应的表数据。

**特性：**
- 支持多个实体类同时清理
- 自动获取表名（优先使用 `@TableName` 注解）
- 智能降级：TRUNCATE 失败时自动使用 DELETE
- 针对性的删除条件，避免误删生产数据

## 使用方法

### 1. 基础用法

```java
@SpringBootTest
class YourIntegrationTest extends BasicIntegrationTest {

    @BeforeEach
    void setUp() {
        // 清理单个表
        clearUpTestData(EntityDO.class);
    }

    @AfterEach
    void tearDown() {
        // 清理多个表
        clearUpTestData(EntityDO.class, AnotherEntityDO.class);
    }
}
```

### 2. 支持的实体类识别

**自动表名识别：**
- 优先使用 `@TableName("table_name")` 注解
- 自动将驼峰类名转换为蛇形表名（`UserProfileDO` → `user_profile`）

**删除策略：**
- `EntityDO` 类：使用针对 Entity 模式的删除条件
- 其他实体类：可扩展支持更多模式

### 3. 使用示例

#### Entity 模块
```java
@EntityIntegrationTest
class EntityIntegrationTest extends BasicIntegrationTest {

    @BeforeEach
    void setUp() {
        clearUpTestData(EntityDO.class);
        // ... 测试数据初始化
    }

    @AfterEach
    void tearDown() {
        clearUpTestData(EntityDO.class);
    }
}
```

#### Talent 模块
```java
@TalentIntegrationTest
class TalentIntegrationTest extends BasicIntegrationTest {

    @BeforeEach
    void setUp() {
        // 支持多个相关表同时清理
        clearUpTestData(TalentDO.class, TalentProfileDO.class);
    }
}
```

## 实现原理

### 1. 双重删除策略

```java
// 首先尝试 TRUNCATE（最高效）
String clearSql = "TRUNCATE TABLE " + tableName;
jdbcTemplate.execute(clearSql);

// 如果失败（如外键约束），则降级到 DELETE
tryDeleteByEntityClass(entityClass);
```

### 2. 智能删除条件

对于 Entity 相关的表，使用针对性删除条件：
```sql
DELETE FROM entities WHERE
    id = 'existing-entity-id' OR
    code LIKE 'INTEGRATION_%' OR
    code LIKE 'PAGE_%' OR
    -- ... 其他测试模式
```

### 3. 表名映射

- **注解优先**：`@TableName("custom_table_name")`
- **自动转换**：`UserOrderDO` → `user_order`

## 扩展指南

### 添加新的实体模式

在 `buildDeleteSql` 方法中添加新的实体类型支持：

```java
private String buildDeleteSql(String tableName, Class<?> entityClass) {
    String className = entityClass.getSimpleName();

    if (className.contains("Entity")) {
        // Entity 相关的删除条件
    } else if (className.contains("Talent")) {
        // Talent 相关的删除条件
        return "DELETE FROM " + tableName + " WHERE " +
               "name LIKE 'TEST_%' OR " +
               "email LIKE 'test%@%'";
    }

    // 默认删除所有数据（谨慎使用）
    return "DELETE FROM " + tableName;
}
```

### 自定义清理策略

如果需要特殊的清理逻辑，可以在子类中重写方法：

```java
@Override
protected void clearUpTestData(Class<?>... entityClasses) {
    // 自定义清理逻辑
    super.clearUpTestData(entityClasses);
    // 额外的清理步骤
}
```

## 最佳实践

### 1. 测试隔离
- 每个测试类都应该在 `@BeforeEach` 和 `@AfterEach` 中清理数据
- 使用具有描述性的测试数据前缀，便于识别和清理

### 2. 性能考虑
- TRUNCATE 比 DELETE 更快，但会重置自增ID
- 对于有外键约束的表，会自动降级到 DELETE

### 3. 安全性
- 删除条件都是针对测试数据的模式匹配
- 避免使用无条件的 `DELETE FROM table`

### 4. 维护性
- 集中管理删除逻辑，便于维护和修改
- 支持新实体类型的快速接入

## 注意事项

1. **数据安全**：确保删除条件只匹配测试数据
2. **外键约束**：复杂的外键关系可能需要手动处理
3. **事务管理**：与 `@Transactional` 注解配合使用
4. **性能影响**：大量数据的删除可能影响测试性能

## 故障排除

### 常见问题

1. **表名找不到**
   - 检查 `@TableName` 注解
   - 确认类名转换逻辑

2. **删除失败**
   - 检查外键约束
   - 查看日志中的详细错误信息

3. **权限问题**
   - 确保测试数据库用户有 DELETE/TRUNCATE 权限

### 调试技巧

启用 DEBUG 日志查看详细执行过程：
```yaml
logging:
  level:
    com.i0.app.integration.BasicIntegrationTest: DEBUG
```