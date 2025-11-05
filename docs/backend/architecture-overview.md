# 后端架构设计规范 - 核心精华版
> **AI代码生成核心原则**：严格遵循本规范，确保生成代码的一致性、完整性和高质量

## 目录结构

1. [架构总览](#架构总览)
   - [核心架构模式](#核心架构模式)
   - [分层职责](#分层职责)
   - [依赖规则](#依赖规则)

2. [三层架构规范](#三层架构规范)
   - [Domain层（内层）](#1-domain层内层)
   - [Application层（中层）](#2-application层中层)
   - [Gateway层（外层）](#3-gateway层外层)

3. [Frameworks模块规范](#frameworks模块规范)
   - [模块结构](#模块结构)
   - [依赖规则](#依赖规则)
   - [使用约束](#使用约束)

4. [ACL防腐层规范](#acl防腐层规范)
   - [核心原则](#核心原则)
   - [实现规范](#实现规范)
   - [依赖配置](#依赖配置)

5. [数据访问规范](#数据访问规范)
   - [MyBatis-Plus核心约束](#mybatis-plus核心约束)
   - [ServiceImpl最佳实践](#serviceimpl最佳实践)
   - [逻辑删除规范](#逻辑删除规范)

6. [测试与质量保证](#测试与质量保证)
   - [测试分层策略](#测试分层策略)
   - [TDD开发流程](#tdd开发流程)
   - [代码质量标准](#代码质量标准)

7. [AI代码生成约束清单](#ai代码生成约束清单)
   - [架构约束检查](#架构约束检查)
   - [实现完整性验证](#实现完整性验证)
   - [代码质量标准](#代码质量标准)

8. [架构优势](#架构优势)
   - [技术优势](#技术优势)
   - [业务价值](#业务价值)

## 架构总览

### 核心架构模式
**必须使用**：Clean Architecture + Domain Driven Design (DDD)

### 分层职责
**Domain层（内层）**：业务规则和领域逻辑的核心载体
**Application层（中层）**：业务流程编排者，协调领域对象完成业务用例
**Gateway层（外层）**：外部系统集成层，处理数据持久化和外部服务调用

### 依赖规则
```
Gateway Layer (外层)
    ↓ 依赖
Application Layer (中层)
    ↓ 依赖
Domain Layer (内层)
    ↓ 只能依赖
frameworks:domain.core
```

**✅ 允许**：外层可依赖内层，跨层通信通过接口
**❌ 严格禁止**：内层不得依赖外层，Domain层禁止框架依赖

## 三层架构规范

### 1. Domain层（内层）
**核心职责**: 业务规则和领域逻辑的核心载体

**强制约束**:
- **纯净性**: 只使用JDK基础库，禁止依赖任何外部框架
- **业务中心**: 专注业务逻辑，禁止包含审计字段和持久化注解
- **方法封装**: 通过业务方法封装属性变更，在方法中实现业务规则验证
- **接口定义**: 必须定义Repository接口，但不包含实现细节

**领域对象设计**:
- **实体**: 包含唯一标识符、业务属性、业务方法，通过业务方法封装属性变更
- **值对象**: 保证不可变性，通过属性值判断相等性，自验证
- **枚举**: 包含状态转换逻辑，避免"哑枚举"

**❌ 严格禁止**:
- 审计字段（createdAt、updatedAt、createdBy、updatedBy）
- ORM注解或SQL语句
- 任何外部框架依赖

**AI生成约束检查项**:
- [ ] 领域对象是否只使用JDK基础库，不依赖外部框架
- [ ] 是否包含审计字段、ORM注解或SQL语句
- [ ] 是否通过业务方法封装属性变更并实现业务规则验证
- [ ] 值对象是否保证不可变性，枚举是否包含业务逻辑方法

### 2. Application层（中层）
**核心职责**: 业务流程编排者，协调领域对象完成业务用例

**强制约束**:
- **单一职责**: 一个UseCase只处理一个具体的业务场景
- **方法规范**: 有且仅有一个public方法：`execute()`
- **注解规范**: 必须使用`@Component`/`@Service`、`@RequiredArgsConstructor`
- **事务规范**: 数据写入操作必须使用`@Transactional`，查询操作禁止使用
- **DTO规范**: 分页查询必须创建专门的`{Entity}PageOutput`类

**UseCase命名规范**:
- **✅ 正确**: `CreateTalentUseCase`、`FindTalentByCodeUseCase`
- **❌ 错误**: `FindTalentUseCase`（过于宽泛，容易导致多个公开方法）

**DTO设计强制规范**:
- 分页查询必须检查是否存在`{Entity}PageOutput`类
- 必须创建专门的`{Entity}PageOutput`类，禁止复用普通输出DTO
- UseCase返回类型必须是`Pageable<{Entity}PageOutput>`
- 分页输出DTO命名必须严格遵循`{Entity}PageOutput`格式

**❌ 严格禁止**:
- 多个public业务方法
- 包含业务规则（必须在Domain层实现）
- 直接数据访问（必须通过Repository接口）
- UI逻辑处理

**AI生成约束检查项**:
- [ ] UseCase类名是否符合`{Action}{Entity}UseCase`格式
- [ ] 是否只有一个public方法`execute()`
- [ ] 是否正确使用了`@Component`/`@Service`、`@RequiredArgsConstructor`
- [ ] 写入操作是否使用了`@Transactional`，查询操作是否未使用
- [ ] 分页查询是否创建了专门的`{Entity}PageOutput`类
- [ ] 是否通过Repository接口访问数据，而非直接操作数据库

### 3. Gateway层（外层）
**核心职责**: 外部系统集成层，处理数据持久化和外部服务调用

**强制约束**:
- **Mapper规范**: 必须继承MyBatis-Plus的BaseMapper<T>接口，禁止自定义SQL方法
- **查询构建**: 必须使用LambdaQueryWrapper构建查询条件，禁止硬编码SQL
- **服务实现**: 必须继承ServiceImpl<M,T>，优先使用内置方法
- **逻辑删除**: 必须使用`@TableLogic(value = "false", delval = "true")`配置
- **跨领域调用**: 必须通过ACL层进行，禁止直接依赖其他领域

**MyBatis-Plus使用规范**:
- **✅ 必须遵守**: 继承BaseMapper<T>，保持接口简洁
- **❌ 严格禁止**: 在Mapper接口中使用@Select、@Insert等注解
- **✅ 查询构建**: 使用LambdaQueryWrapper构建类型安全的查询条件
- **✅ 条件方法**: 使用条件方法避免if判断

**ServiceImpl最佳实践**:
- **✅ 优先使用**: `saveOrUpdate()`、`exists()`、`lambdaQuery()`
- **❌ 避免**: `count() > 0`（应使用`exists()`）
- **✅ 继承**: Repository实现类应继承ServiceImpl<M,T>

**AI生成约束检查项**:
- [ ] Mapper接口是否继承BaseMapper<T>
- [ ] 是否包含任何自定义SQL方法或注解
- [ ] 是否使用LambdaQueryWrapper构建查询条件
- [ ] 是否继承ServiceImpl<M,T>并优先使用内置方法
- [ ] 逻辑删除配置是否完整（value和delval）
- [ ] ACL适配器是否正确实现

## Frameworks模块规范

### 模块结构与职责
```
frameworks/
├── domain.core/           # 领域核心抽象（Pageable接口、通用值对象、异常基类）
├── persistence.spring/    # 持久化适配器（SpringPage、仓储基类）
├── gateway.context/       # 网关层基础设施（全局异常处理、API响应封装）
└── test.context/         # 测试基础设施（测试配置、数据工厂、构建器）
```

### 依赖规则（必须遵守）
- **Domain层** → 只能依赖 `frameworks:domain.core`
- **Application层** → 可依赖 `frameworks:domain.core` + `frameworks:persistence.spring`
- **Gateway层** → 可依赖所有frameworks模块
- **测试代码** → 可依赖 `frameworks:test.context`（仅限测试范围）

### 使用约束
- **必须**在build.gradle中正确声明frameworks依赖
- **必须**使用frameworks抽象接口而非框架API
- **禁止**使用未声明的frameworks模块
- **必须**在集成测试中导入`TestMybatisPlusConfig.class`

**❌ 严格禁止**:
- 业务代码直接使用框架API
- 未声明依赖就使用frameworks组件
- 使用未批准的framework模块

## ACL防腐层规范

### 核心原则
- **领域隔离**: 通过ACL适配器进行所有跨领域调用，防止外部领域概念污染当前领域模型
- **依赖约束**: 只允许gateway模块依赖其他领域的application模块
- **转换完整性**: 必须在ACL层完成领域对象转换和异常转换
- **单向依赖**: 严格禁止双向依赖和循环依赖

### 实现规范
- **目录结构**: 必须在`gateway/acl/`目录下创建适配器
- **对象转换**: 必须将外部领域对象转换为当前领域对象
- **异常转换**: 必须将外部领域异常转换为当前领域异常
- **接口隔离**: 禁止将外部领域对象直接传递给当前领域

### 依赖配置
**✅ 正确配置**:
```gradle
// client/gateway/build.gradle
dependencies {
    implementation project(':modules:client:domain')
    implementation project(':modules:client:application')
    implementation project(':modules:location:application')  // ✅ 正确
    // implementation project(':modules:location:domain')    // ❌ 禁止
}
```

**❌ 严格禁止**:
- 直接依赖其他领域的domain或gateway模块
- 在Domain或Application层进行跨领域调用
- 循环依赖或双向依赖

### 适配器实现示例
```java
@Component
@RequiredArgsConstructor
public class FetchLocationAdapter {
    private final GetLocationByIdUseCase getLocationByIdUseCase;

    public ClientLocation fetchLocationById(String locationId) {
        try {
            LocationOutput output = getLocationByIdUseCase.execute(locationId);
            return convertToClientLocation(output);
        } catch (LocationNotFoundException e) {
            throw new ClientBusinessException("位置信息不存在: " + locationId, e);
        }
    }
}
```

## 数据访问规范

### MyBatis-Plus核心约束

**Mapper接口规范**:
- **✅ 必须遵守**: 继承BaseMapper<T>，保持接口简洁
- **❌ 严格禁止**: 在Mapper接口中使用@Select、@Insert等注解
- **❌ 严格禁止**: 硬编码SQL语句

**查询构建规范**:
- **✅ 必须使用**: LambdaQueryWrapper构建类型安全的查询条件
- **✅ 条件方法**: 使用条件方法避免if判断
```java
// ✅ 正确实现
LambdaQueryWrapper<EntityDO> queryWrapper = new LambdaQueryWrapper<EntityDO>()
    .eq(EntityDO::getDeleted, false)
    .like(StringUtils.hasText(name), EntityDO::getName, name)
    .eq(type != null, EntityDO::getType, type)
    .orderByDesc(EntityDO::getCreatedAt);
```

### ServiceImpl最佳实践

**继承关系**:
- **✅ 必须继承**: Repository实现类应继承ServiceImpl<M,T>
- **✅ 优先使用**: 使用ServiceImpl提供的内置方法

**方法选择规范**:
- **✅ 推荐**: `saveOrUpdate()`、`exists()`、`lambdaQuery()`
- **❌ 避免**: `count() > 0`（应使用`exists()`）
```java
// ✅ 正确：使用exists()而非count() > 0
return lambdaQuery()
    .eq(EntityDO::getName, name)
    .exists();  // 直接返回boolean，性能更好

// ✅ 正确：使用lambdaQuery()简化查询构建
return lambdaQuery()
    .eq(EntityDO::getDeleted, false)
    .list();  // 直接获取列表
```

### 逻辑删除规范

**配置要求**:
```java
// ✅ 正确：DO类中的@TableLogic配置
@TableLogic(value = "false", delval = "true")
@TableField("is_deleted")
private Boolean isDeleted;
```

**实现规范**:
- **✅ 必须使用**: deleteById()进行逻辑删除
- **❌ 严格禁止**: 手动设置删除标记
```java
// ✅ 正确：使用MyBatis-Plus的逻辑删除特性
public void delete(Entity entity) {
    if (entity == null || entity.getId() == null) {
        return;
    }
    int result = entityMapper.deleteById(entity.getId());
    if (result == 0) {
        throw new RuntimeException("Failed to delete entity");
    }
}
```

## 测试与质量保证

### 测试分层策略
- **单元测试**：测试单个类或方法，覆盖率≥90%
- **集成测试**：测试模块间交互，覆盖率≥80%
- **端到端测试**：测试完整业务流程

### TDD开发流程
- **Red-Green-Refactor循环**: 必须遵循TDD开发模式
- **测试先行**: 必须先写测试再写实现代码
- **编译优先**: 必须确保编译通过后再关注测试
- **集成测试优先**: 必须优先使用集成测试覆盖Gateway层Controller

### 测试优先级
1. **编译优先原则**: 必须优先解决编译错误
2. **主干分支集成测试策略**: 只覆盖核心业务场景
3. **Gateway层Controller测试策略**: 优先使用集成测试，禁止重复的单元测试

### 代码质量标准
- **覆盖率要求**: 单元测试≥90%，集成测试≥80%
- **测试配置**: 必须在集成测试中导入`TestMybatisPlusConfig.class`
- **异常断言**: 集成测试异常断言必须符合全局异常处理规范

## AI代码生成约束清单

### 架构约束检查
- [ ] 是否严格遵循三层架构（Domain、Application、Gateway）
- [ ] 依赖方向是否正确（外层依赖内层）
- [ ] Domain层是否只依赖frameworks:domain.core
- [ ] Application层是否正确依赖frameworks模块
- [ ] Gateway层是否完整依赖所有必要frameworks模块
- [ ] 是否存在内层依赖外层的违规情况

### 实现完整性验证
- [ ] 是否在build.gradle中正确声明frameworks依赖
- [ ] 业务代码是否使用frameworks抽象接口而非框架API
- [ ] 是否使用了未声明的frameworks模块
- [ ] 测试代码是否正确依赖frameworks:test.context
- [ ] 是否在集成测试中正确导入TestMybatisPlusConfig

### 代码质量标准
- [ ] 是否遵循标准的三层目录结构
- [ ] 每个领域模块是否包含完整的domain、application、gateway三层
- [ ] 测试目录结构是否与源代码结构对应
- [ ] 是否存在违规的目录结构或文件放置
- [ ] 代码覆盖率是否达到要求标准

### 依赖关系检查
- [ ] app模块是否只包含基础设施，不包含业务逻辑
- [ ] 业务领域模块是否保持相对独立
- [ ] 领域间交互是否通过良好定义的接口
- [ ] ACL跨领域调用是否严格遵循防腐层设计
- [ ] 是否存在循环依赖或不合规的依赖关系

### 各层特定约束
**Domain层约束**:
- [ ] 领域对象是否只使用JDK基础库，不依赖外部框架
- [ ] 是否包含审计字段、ORM注解或SQL语句
- [ ] 是否通过业务方法封装属性变更并实现业务规则验证
- [ ] 值对象是否保证不可变性，枚举是否包含业务逻辑方法

**Application层约束**:
- [ ] UseCase类名是否符合`{Action}{Entity}UseCase`格式
- [ ] 是否只有一个public方法`execute()`
- [ ] 是否正确使用了`@Component`/`@Service`、`@RequiredArgsConstructor`
- [ ] 写入操作是否使用了`@Transactional`，查询操作是否未使用
- [ ] 分页查询是否创建了专门的`{Entity}PageOutput`类
- [ ] 是否通过Repository接口访问数据，而非直接操作数据库

**Gateway层约束**:
- [ ] Mapper接口是否继承BaseMapper<T>
- [ ] 是否包含任何自定义SQL方法或注解
- [ ] 是否使用LambdaQueryWrapper构建查询条件
- [ ] 是否继承ServiceImpl<M,T>并优先使用内置方法
- [ ] 逻辑删除配置是否完整（value和delval）
- [ ] ACL适配器是否正确实现

## 架构优势

### 技术优势
1. **可维护性**: 清晰的分层结构和职责分离，便于代码维护和功能扩展
2. **可测试性**: 依赖注入和接口抽象，支持高效的单元测试和mock
3. **可扩展性**: 基于接口的设计和动态查询构建，适应业务变化需求
4. **类型安全**: LambdaQueryWrapper提供编译时类型检查，减少运行时错误
5. **代码简洁**: 统一的数据访问模式，减少样板代码和SQL硬编码

### 业务价值
1. **领域纯净**: 业务逻辑与技术基础设施完全分离
2. **模块独立**: 各业务领域可独立开发和部署
3. **技术无关**: 核心业务逻辑不依赖具体技术框架
4. **质量保证**: 通过架构约束和测试规范确保代码质量
5. **AI友好**: 结构化的架构设计便于AI代码生成和理解

---

**遵循本规范可确保代码的高质量、高可维护性和强扩展性，为项目的长期发展奠定坚实的架构基础，同时确保AI生成的代码始终符合项目标准。**