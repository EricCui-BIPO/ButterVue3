# Domain层设计规范

## 目录

- [1. 职责定位](#1-职责定位)
- [2. 核心原则](#2-核心原则)
- [3. 设计约束](#3-设计约束)
- [4. 审计字段规范](#4-审计字段规范)
  - [4.1 核心原则](#41-核心原则)
  - [4.2 强制要求](#42-强制要求)
  - [4.3 实现示例](#43-实现示例)
- [5. 领域对象设计规范](#5-领域对象设计规范)
  - [5.1 实体（Entity）设计](#51-实体entity设计)
  - [5.2 值对象（Value Object）设计](#52-值对象value-object设计)
  - [5.3 枚举（Enum）设计](#53-枚举enum设计)
- [6. 领域服务设计](#6-领域服务设计)
  - [6.1 职责范围](#61-职责范围)
  - [6.2 标准实现](#62-标准实现)
- [7. Repository接口设计](#7-repository接口设计)
  - [7.1 设计原则](#71-设计原则)
  - [7.2 标准接口](#72-标准接口)
- [8. 领域异常设计](#8-领域异常设计)
  - [8.1 异常分类](#81-异常分类)
  - [8.2 标准实现](#82-标准实现)
- [9. AI代码生成约束清单](#9-ai代码生成约束清单)
  - [9.1 领域对象生成约束](#91-领域对象生成约束)
  - [9.2 领域服务生成约束](#92-领域服务生成约束)
  - [9.3 Repository接口生成约束](#93-repository接口生成约束)
  - [9.4 异常处理生成约束](#94-异常处理生成约束)
- [10. 质量保证](#10-质量保证)
  - [10.1 代码质量标准](#101-代码质量标准)
  - [10.2 架构约束](#102-架构约束)
  - [10.3 测试策略](#103-测试策略)

## 1. 职责定位
**领域模型定义**：定义实体、值对象、聚合根等领域概念
**核心职责**：业务规则和领域逻辑的核心载体
**边界范围**：位于架构最内层，不依赖任何外部框架或基础设施

## 2. 核心原则

**✅ 必须遵守：**
- **独立性**：只使用JDK基础库，不依赖外部框架
- **业务中心**：使用业务领域通用语言，专注业务规则
- **纯净性**：纯Java对象，避免ORM注解和SQL语句
- **可测试性**：无外部依赖，易于单元测试
- **依赖倒置**：Repository接口在Domain定义，不依赖实现

## 3. 设计约束

**❌ 严格禁止：**
- 任何外部框架依赖（Spring、MyBatis等）
- 数据库相关注解和SQL
- 审计字段（创建时间、更新时间、创建人、更新人）
- 持久化相关注解

**✅ 只允许：**
- JDK基础库
- 业务逻辑实现
- 领域规则定义
- Repository接口定义（不实现）

## 4. 审计字段规范

### 4.1 核心原则

审计字段属于数据存储层面的实现细节，必须与业务逻辑完全分离。

### 4.2 强制要求

**❌ 严格禁止：**
- 在Domain实体中包含任何审计字段
- 在Domain实体中包含 `createdAt`、`updatedAt`、`createdBy`、`updatedBy` 等字段
- 在业务逻辑中处理审计字段的赋值和更新

**✅ 必须遵守：**
- 审计字段只能存在于Gateway层的DO（Data Object）中
- 通过MyBatis-Plus的 `@TableField(fill = FieldFill.INSERT)` 等注解自动填充
- 确保Domain实体专注于业务规则，不被基础设施关注点污染

### 4.3 实现示例

**✅ Domain层实体（纯净的业务模型）：**
```java
// Domain层：专注业务逻辑，不包含审计字段
public class Entity {
    private String id;
    private String name;
    private String description;
    private EntityType entityType;

    // 只包含业务相关的字段和方法
    public void updateDescription(String newDescription) {
        // 业务规则验证
        if (StringUtils.isBlank(newDescription)) {
            throw new DomainException("描述不能为空");
        }
        this.description = newDescription;
    }
}
```

**✅ Gateway层DO（包含审计字段）：**
```java
// Gateway层：包含审计字段，用于数据持久化
@TableName("entities")
public class EntityDO {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String name;
    private String description;
    private String entityType;

    // 审计字段：只存在于持久化层
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    @TableLogic(value = "false", delval = "true")
    @TableField("is_deleted")
    private Boolean isDeleted;
}
```

**❌ 错误示例：**
```java
// 错误：Domain实体包含审计字段
public class Entity {
    private String id;
    private String name;
    private LocalDateTime createdAt;    // ❌ 违反规范
    private LocalDateTime updatedAt;    // ❌ 违反规范
    private String createdBy;           // ❌ 违反规范

    // 错误：在业务逻辑中处理审计字段
    public void updateName(String newName) {
        this.name = newName;
        this.updatedAt = LocalDateTime.now();  // ❌ 违反规范
    }
}
```

## 5. 领域对象设计规范

### 5.1 实体（Entity）设计

**✅ 必须包含：**
- 唯一标识符（ID）
- 业务属性
- 业务方法（封装业务规则）
- 不变性约束

**✅ 必须遵守：**
- 通过业务方法封装属性变更
- 在业务方法中实现业务规则验证
- 保持对象的一致性状态

**标准实现：**
```java
public class Order {
    private final String id;
    private String customerName;
    private BigDecimal amount;
    private OrderStatus status;

    private Order(String id, String customerName, BigDecimal amount) {
        this.id = id;
        this.customerName = customerName;
        this.amount = amount;
        this.status = OrderStatus.PENDING;
        validate();
    }

    public static Order create(String customerName, BigDecimal amount) {
        return new Order(UUID.randomUUID().toString(), customerName, amount);
    }

    public void confirm() {
        if (status != OrderStatus.PENDING) {
            throw new DomainException("只有待确认订单可以确认");
        }
        this.status = OrderStatus.CONFIRMED;
    }

    public void cancel() {
        if (status == OrderStatus.COMPLETED) {
            throw new DomainException("已完成订单不能取消");
        }
        this.status = OrderStatus.CANCELLED;
    }

    private void validate() {
        if (StringUtils.isBlank(customerName)) {
            throw new DomainException("客户名称不能为空");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("订单金额必须大于0");
        }
    }
}
```

### 5.2 值对象（Value Object）设计

**✅ 必须遵守：**
- 不可变性（immutable）
- 无标识符（通过属性值判断相等性）
- 自验证（创建时验证自身有效性）

**标准实现：**
```java
public class Money {
    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount, String currency) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("金额不能为负数");
        }
        if (StringUtils.isBlank(currency)) {
            throw new DomainException("币种不能为空");
        }
        this.amount = amount;
        this.currency = currency;
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new DomainException("不能添加不同币种的金额");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount) &&
               Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
```

### 5.3 枚举（Enum）设计

**✅ 必须遵守：**
- 使用枚举表示有限的业务状态
- 包含业务逻辑方法
- 提供状态转换验证

**标准实现：**
```java
public enum OrderStatus {
    PENDING("待确认"),
    CONFIRMED("已确认"),
    PROCESSING("处理中"),
    COMPLETED("已完成"),
    CANCELLED("已取消");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public boolean canTransitionTo(OrderStatus newStatus) {
        switch (this) {
            case PENDING:
                return newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED:
                return newStatus == PROCESSING || newStatus == CANCELLED;
            case PROCESSING:
                return newStatus == COMPLETED;
            case COMPLETED:
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }

    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED;
    }
}
```

## 6. 领域服务设计

### 6.1 职责范围

**✅ 必须处理：**
- 跨多个实体的业务规则
- 复杂的业务计算
- 外部系统集成（通过接口）
- 需要事务处理的业务流程

**❌ 严格禁止：**
- 包含数据访问逻辑
- 包含基础设施依赖
- 包含UI逻辑

### 6.2 标准实现

```java
public class PricingService {
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    public PricingService(ProductRepository productRepository,
                         DiscountRepository discountRepository) {
        this.productRepository = productRepository;
        this.discountRepository = discountRepository;
    }

    public Money calculateOrderPrice(List<OrderItem> items, Customer customer) {
        Money subtotal = calculateSubtotal(items);
        Money discount = calculateDiscount(subtotal, customer);
        Money tax = calculateTax(subtotal.subtract(discount));

        return subtotal.subtract(discount).add(tax);
    }

    private Money calculateSubtotal(List<OrderItem> items) {
        return items.stream()
            .map(item -> item.getProduct().getPrice().multiply(item.getQuantity()))
            .reduce(Money.ZERO, Money::add);
    }

    private Money calculateDiscount(Money subtotal, Customer customer) {
        // 复杂的折扣计算逻辑
        return customer.calculateDiscount(subtotal);
    }

    private Money calculateTax(Money amount) {
        // 税费计算逻辑
        return amount.multiply(new BigDecimal("0.10")); // 10% tax
    }
}
```

## 7. Repository接口设计

### 7.1 设计原则

**✅ 必须遵守：**
- 接口在Domain层定义
- 只包含业务需要的操作
- 使用领域对象作为参数和返回值
- 不包含任何实现细节

### 7.2 标准接口

```java
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findByCustomer(String customerId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByDateRange(LocalDateTime start, LocalDateTime end);
    void delete(String id);
    boolean existsById(String id);
}

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(String id);
    Optional<Customer> findByEmail(String email);
    List<Customer> findByStatus(CustomerStatus status);
    void delete(String id);
}
```

## 8. 领域异常设计

### 8.1 异常分类

**✅ 必须使用：**
- `DomainException`：所有领域异常的基类
- `BusinessRuleException`：业务规则违反异常
- `ValidationException`：验证失败异常
- `EntityNotFoundException`：实体不存在异常

### 8.2 标准实现

```java
public class DomainException extends RuntimeException {
    private final String errorCode;

    public DomainException(String message) {
        super(message);
        this.errorCode = "DOMAIN_ERROR";
    }

    public DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

public class BusinessRuleException extends DomainException {
    public BusinessRuleException(String message) {
        super("BUSINESS_RULE_VIOLATION", message);
    }
}

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entityType, String id) {
        super("ENTITY_NOT_FOUND",
              String.format("%s with id %s not found", entityType, id));
    }
}
```

## 9. AI代码生成约束清单

### 9.1 领域对象生成约束

**必须检查项目：**
- [ ] 领域对象是否只使用JDK基础库，不依赖外部框架
- [ ] 是否包含审计字段（createdAt、updatedAt等）
- [ ] 是否包含ORM注解或SQL语句
- [ ] 是否通过业务方法封装属性变更
- [ ] 是否在业务方法中实现了业务规则验证
- [ ] 值对象是否保证不可变性
- [ ] 枚举是否包含业务逻辑方法

### 9.2 领域服务生成约束

**必须检查项目：**
- [ ] 是否包含数据访问逻辑
- [ ] 是否包含基础设施依赖
- [ ] 是否只处理业务逻辑，不涉及技术实现
- [ ] 是否通过接口与外部系统交互
- [ ] 方法命名是否使用业务领域语言

### 9.3 Repository接口生成约束

**必须检查项目：**
- [ ] 接口是否在Domain层定义
- [ ] 是否只包含业务需要的操作
- [ ] 是否使用领域对象作为参数和返回值
- [ ] 是否包含任何实现细节
- [ ] 方法命名是否符合业务语义

### 9.4 异常处理生成约束

**必须检查项目：**
- [ ] 是否使用领域异常而非系统异常
- [ ] 异常消息是否使用业务领域语言
- [ ] 是否提供了有意义的错误代码
- [ ] 是否在适当的地方抛出异常
- [ ] 是否过度使用异常控制流程

## 10. 质量保证

### 10.1 代码质量标准

**必须达到的标准：**
- 100%符合本文档规范
- 通过所有单元测试
- 代码覆盖率≥95%
- 无外部框架依赖
- 业务规则完整性

### 10.2 架构约束

**必须遵守的约束：**
- 严格的分层架构
- 领域对象纯净性
- 业务规则内聚性
- 依赖倒置原则
- 接口面向编程

### 10.3 测试策略

**必须保证的测试：**
- 业务规则正确性
- 对象不变性
- 异常情况处理
- 边界条件验证
- 集成测试覆盖