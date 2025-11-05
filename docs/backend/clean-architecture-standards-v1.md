# 后端架构设计规范
> **AI代码生成核心原则**：严格遵循本规范，确保生成代码的一致性、完整性和高质量

## 目录

1. [架构概述](#1-架构概述)
    - 1.1 [架构模式](#11-架构模式)
    - 1.2 [核心分层](#12-核心分层)
    - 1.3 [依赖规则](#13-依赖规则)

2. [核心设计原则](#2-核心设计原则)
    - 2.1 [Frameworks Module - 通用框架模块](#21-frameworks-module---通用框架模块)
        - 2.1.1 [分层架构定义](#211-分层架构定义)
        - 2.1.2 [核心交互规则](#212-核心交互规则)
        - 2.1.3 [依赖关系限制](#213-依赖关系限制)
        - 2.1.4 [关键代码示例](#214-关键代码示例)
    - 2.2 [App Module - 应用主入口](#22-app-module---应用主入口)
    - 2.3 [业务领域 Modules](#23-业务领域-modules)
    - 2.4 [标准目录结构](#24-标准目录结构)

3. [Domain层设计规范](#3-domain层设计规范)
    - 3.1 [核心原则](#31-核心原则)
    - 3.2 [设计要求](#32-设计要求)
    - 3.3 [审计字段规范](#33-审计字段规范)

4. [Application层设计规范](#4-application层设计规范)
    - 4.1 [UseCase设计规范](#41-usecase设计规范)
        - 4.1.1 [UseCase强制性要求](#411-usecase强制性要求)
        - 4.1.2 [UseCase实现规范](#412-usecase实现规范)
        - 4.1.3 [UseCase 测试规范](#413-usecase-测试规范)
    - 4.2 [DTO设计规范](#42-dto设计规范)
        - 4.2.1 [DTO强制性要求](#421-dto强制性要求)
        - 4.2.2 [DTO 与 Domain 转换](#422-dto-与-domain-转换)
    - 4.3 [Application层禁止行为](#43-application层禁止行为)

5. [Gateway层设计规范](#5-gateway层设计规范)
    - 5.1 [MyBatis-Plus 数据访问规范](#51-mybatis-plus-数据访问规范)
        - 5.1.1 [数据访问层设计原则](#511-数据访问层设计原则)
        - 5.1.2 [Mapper接口规范](#512-mapper接口规范)
        - 5.1.3 [数据访问层重构最佳实践](#513-数据访问层重构最佳实践)
        - 5.1.4 [逻辑删除规范](#514-逻辑删除规范)
        - 5.1.5 [ServiceImpl 高级特性使用规范](#515-serviceimpl-高级特性使用规范)
    - 5.2 [ACL（Anti-Corruption Layer）跨领域调用规范](#52-acl-anti-corruption-layer-跨领域调用规范)
        - 5.2.1 [职责](#521-职责)
        - 5.2.2 [规范（约束）](#522-规范约束)
        - 5.2.3 [示例](#523-示例)

6. [总结](#6-总结)

---

## 1. 架构概述

### 1.1 架构模式
**Clean Architecture + Domain Driven Design (DDD)**

### 1.2 核心分层
```
Gateway Layer (外层)
    ↓ 依赖
Application Layer (中层)  
    ↓ 依赖
Domain Layer (内层)
```

### 1.3 依赖规则

- ✅ 外层可依赖内层
- ❌ 内层不得依赖外层
- ✅ 跨层通信通过接口
- ❌ Domain层禁止框架依赖

## 2. 核心设计原则

### 2.1 Frameworks Module - 通用框架模块

**核心定位**：系统基础设施层，提供跨业务模块的通用组件和抽象接口。

#### 2.1.1 分层架构定义
```
frameworks/
├── domain.core/           # 领域核心抽象（Pageable接口、通用值对象、异常基类）
├── persistence.spring/    # 持久化适配器（SpringPage、仓储基类）
├── gateway.context/       # 网关层基础设施（全局异常处理、API响应封装）
└── test.context/         # 测试基础设施（测试配置、数据工厂、构建器）
```

#### 2.1.2 核心交互规则

**依赖方向**：外层依赖内层，内层不依赖外层
- **Domain层** → 只能依赖 `frameworks:domain.core`
- **Application层** → 可依赖 `frameworks:domain.core` +`frameworks:persistence.spring`
- **Gateway层** → 可依赖 `frameworks:domain.core` + `frameworks:persistence.spring` + `frameworks:gateway.context`
- **测试代码** → 可依赖 `frameworks:test.context`（仅限测试范围）

**接口优先**：业务代码必须使用frameworks抽象接口，禁止直接使用框架API

#### 2.1.3 依赖关系限制

**强制要求**：
1. **显式声明**：必须在`build.gradle`中明确声明framework依赖，禁止隐式依赖 ✅
2. **构建验证**：必须在编译时检查依赖声明与代码使用的一致性，严格禁止不一致的依赖配置 ✅
3. **版本统一**：所有模块必须使用相同版本的frameworks，禁止版本冲突 ✅
4. **依赖范围限制**：只允许使用已批准的framework模块，禁止引入未授权的第三方依赖 ✅

**依赖配置**：
```gradle
dependencies {
    // Domain层
    implementation project(':frameworks:domain.core')

     // Application层
    implementation project(':frameworks:domain.core')
    implementation project(':frameworks:persistence.spring')
    
    //Gateway层额外依赖
    implementation project(':frameworks:persistence.spring')
    implementation project(':frameworks:gateway.context')
    testImplementation project(':frameworks:test.context')  //测试依赖（仅测试范围）
}
```

#### 2.1.4 关键代码示例

**✅ 正确用法**：
```java
// 使用统一分页接口
import com.i0.frameworks.domain.core.pagination.Pageable;

@RestController
public class LocationController {
    public Pageable<LocationOutput> getLocations() {
        return locationService.findLocations();
    }
}
```

**❌ 违规示例**：
```java
// 错误：直接使用Spring Data接口
import org.springframework.data.domain.Page; // 违反接口统一原则

// 错误：未声明依赖就使用
public Pageable<Location> getLocations() { // 编译错误：找不到Page类
```

#### 2.1.5 Frameworks 模块详细说明

##### frameworks:test.context - 测试基础设施模块

**核心定位**：为所有业务模块提供统一的测试基础设施和配置，确保测试环境的一致性和可维护性。

**主要功能**：

1. **测试配置统一管理**
   - 提供标准化的测试配置类 `TestMybatisPlusConfig`
   - 集成 MyBatis-Plus 分页插件配置
   - 统一扫描 `gateway.context` 组件（如 `GlobalExceptionHandler`、`ApiResponseWrapper`）
   - 避免使用测试专用的异常处理器，确保测试环境与生产环境行为一致

2. **测试依赖集成**
   - 集成 Spring Boot Test 测试框架
   - 提供 H2 内存数据库支持
   - 统一 MyBatis-Plus 测试环境配置
   - 依赖 `gateway.context` 确保全局组件可用

3. **配置聚合设计**
   - 采用"配置聚合"设计模式
   - 作为测试配置的统一入口点
   - 避免分散的测试配置，提高维护性
   - 确保关键组件（如 `GlobalExceptionHandler`）在测试中始终可用

**技术架构**：
```
test.context/
└── src/main/java/com/i0/test/context/
    └── config/
        └── TestMybatisPlusConfig.java  # 测试配置聚合类
```

**依赖关系**：
```gradle
dependencies {
    // Spring Boot 测试框架
    api 'org.springframework.boot:spring-boot-starter-test'
    
    // MyBatis-Plus 支持
    api "com.baomidou:mybatis-plus-boot-starter:${mybatisPlusVersion}"
    
    // 网关上下文框架（提供全局组件）
    api project(':frameworks:gateway.context')
    
    // H2 内存数据库
    api 'com.h2database:h2'
}
```

**使用方式**：
```java
// 在集成测试中使用
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestMybatisPlusConfig.class)  // 导入测试配置
public class LocationControllerIntegrationTest {
    // 测试代码...
}
```

**核心价值**：
- **一致性保障**：确保测试环境与生产环境的异常处理行为一致
- **配置统一**：避免各模块重复配置测试基础设施
- **维护简化**：集中管理测试相关配置，降低维护成本
- **质量提升**：通过统一的测试基础设施提高测试质量和可靠性

**使用规范**：
1. **强制依赖**：所有需要集成测试的模块必须依赖 `frameworks:test.context`
2. **配置导入**：集成测试类必须使用 `@Import(TestMybatisPlusConfig.class)` 导入配置
3. **范围限制**：仅在 `testImplementation` 范围内使用，不得在生产代码中引用
4. **组件扫描**：依赖 `@ComponentScan("com.i0.gateway.context")` 确保全局组件可用


### 2.2 App Module - 应用主入口
**职责：**
- **应用启动入口**：Spring Boot 主应用程序入口
- **数据库迁移管理**：集成 Flyway 进行数据库版本控制和迁移
- **全局 API 响应封装**：统一的 API 响应格式和数据结构
- **全局异常处理**：统一的异常处理机制和错误响应
- **全局配置管理**：应用级别的配置和 Bean 定义
- **跨领域协调**：协调各业务领域模块间的交互

**特点：**
- 不包含具体业务逻辑
- 提供基础设施和全局性封装
- 依赖各业务领域模块
- 负责应用程序的整体生命周期管理

### 2.3 业务领域 Modules
**职责：**
- **领域专注**：每个模块专注于特定的业务领域（如 talent、entity、location、service、tenant）
- **业务逻辑封装**：承接各自领域的完整业务需求
- **独立性**：各领域模块相对独立，减少耦合
- **完整性**：每个领域模块包含完整的三层架构（domain、application、gateway）

**模块间关系：**
```
app (应用主入口)
├── 依赖 → talent (人才领域)
├── 依赖 → entity (实体领域)
├── 依赖 → location (位置领域)
├── 依赖 → service (服务领域)
└── 依赖 → tenant (租户领域)

各业务领域模块间：
- 通过定义良好的接口进行交互
- 领域间可直接调用，但需遵循领域调用规范
- 遵循领域边界，保持高内聚低耦合
- app 模块不参与业务领域间的协调，仅提供全局基础设施
```

### 2.4 标准目录结构
```
{domain}/
├── domain/          # 领域层：实体、值对象、仓储接口、领域服务
├── application/     # 应用层：UseCase、DTO、应用服务
└── gateway/         # 网关层：控制器、仓储实现、外部集成
```

**目录结构：**
```
modules/
├── app/                       # 应用主入口模块
│   ├── build.gradle
│   └── src/main/java/
│       ├── Application.java   # Spring Boot 启动类
│       ├── config/           # 全局配置
│       ├── exception/        # 全局异常处理
│       ├── response/         # 全局响应封装
│       └── flyway/           # 数据库迁移脚本
├── [domain]/                  # 业务领域模块
│   ├── domain/
│   │   ├── entities/          # 实体层：核心业务逻辑和规则
│   │   ├── valueobjects/      # 值对象
│   │   ├── aggregates/        # 聚合
│   │   └── repositories/      # 仓储接口
│   ├── application/
│   │   ├── usecases/          # 用例层：单一职责的业务行为（如 SearchEntityUseCase）
│   │   └── dto/               # 数据传输对象
│   └── gateway/
│       ├── controllers/       # 网关层：控制器
│       ├── routes/            # 路由
│       ├── middleware/        # 中间件
│       ├── repositories/      # 仓储实现
│       ├── external/          # 外部服务
│       └── config/            # 配置
```
这里是一个 talent 领域的例子:
```
modules/
├── talent/
│   ├── domain/
│   │   ├── build.gradle
│   │   └── src/main/java/com/i0/talent/domain/
│   │       ├── entities/       # 实体类
│   │       ├── valueobjects/   # 值对象
│   │       └── repositories/   # 仓储接口
│   │   └── src/test/java/com/i0/talent/domain/
│   │       ├── entities/       # 重点单元测试
│   │       ├── valueobjects/   # 重点单元测试
│   │       └── repositories/   # 接口契约测试
│   ├── application/
│   │   ├── build.gradle
│   │   └── src/main/java/com/i0/talent/application/
│   │       ├── usecases/       # 单一行为用例（如 CreateTalentUseCase, SearchTalentUseCase）
│   │       └── dto/            # 数据传输对象
│   │   └── src/test/java/com/i0/talent/application/
│   │       ├── usecases/       # 重点单元测试 + Mock 依赖
│   │       └── dto/            # 轻量单元测试
│   └── gateways/
│       ├── build.gradle
│       └── src/main/java/com/i0/talent/gateway/
│           ├── controllers/    # REST 控制器
│           ├── repositories/   # 仓储实现
│           ├── external/       # 外部服务
│           └── config/         # 配置
│       └── src/test/java/com/i0/talent/gateway/
│           ├── controllers/    # Web 层集成测试
│           ├── repositories/   # 数据层集成测试
│           ├── external/       # 外部服务集成测试
│           └── config/         # 配置集成测试
```

## 3. Domain层设计规范

### 3.1 核心原则
- **独立性**：不依赖任何外部框架、数据库、UI或外部服务，仅使用JDK基础库
- **业务中心**：使用业务领域通用语言，专注业务规则而非技术实现
- **纯净性**：使用纯Java对象，保持纯函数风格，避免ORM注解和SQL语句
- **可测试性**：易于单元测试，无外部依赖，聚焦业务规则验证
- **依赖倒置**：Repository接口在Domain定义，提供服务端口但不依赖外部实现
### 3.2 设计要求
- ❌ **禁止**：任何外部框架依赖（Spring、MyBatis等）
- ❌ **禁止**：数据库相关注解和SQL
- ❌ **禁止**：审计字段（创建时间、更新时间、创建人、更新人等）
- ✅ **允许**：JDK基础库、业务逻辑、领域规则
- ✅ **允许**：定义Repository接口（不实现）

### 3.3 审计字段规范

**核心原则**：审计字段属于数据存储层面的实现细节，必须与业务逻辑分离。

**强制要求**：
- ❌ **严格禁止**：在Domain实体中包含任何审计字段
- ❌ **严格禁止**：在Domain实体中包含 `createdAt`、`updatedAt`、`createdBy`、`updatedBy` 等字段
- ❌ **严格禁止**：在业务逻辑中处理审计字段的赋值和更新
- ✅ **必须**：审计字段只能存在于Gateway层的DO（Data Object）中
- ✅ **必须**：通过MyBatis-Plus的 `@TableField(fill = FieldFill.INSERT)` 等注解自动填充
- ✅ **必须**：确保Domain实体专注于业务规则，不被基础设施关注点污染

**正确示例**：

**✅ Domain层实体（纯净的业务模型）**：
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

**✅ Gateway层DO（包含审计字段）**：
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

**❌ 错误示例**：
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

**设计优势**：
1. **关注点分离**：业务逻辑与基础设施关注点完全分离
2. **测试简化**：Domain实体测试不需要考虑审计字段
3. **可维护性**：审计字段的变更不影响业务逻辑
4. **架构纯净性**：保持Clean Architecture的依赖规则

## 4. Application层设计规范

**职责定位**：业务流程编排者，协调领域对象完成业务用例

### 4.1 UseCase设计规范

#### 4.1.1 UseCase强制性要求
- ✅ **有且仅有一个**public方法：`execute()`
- ✅ **可以有**：private辅助方法
- ✅ **必须使用**： 
    - `@Component`、`@Service`：用于依赖注入和组件管理
    - `@Transactional`：**只允许**在数据写入或更新操作的UseCase中使用，**禁止**在查询场景中使用事务注解
    - `@RequiredArgsConstructor`：Lombok注解，用于构造函数注入
- ❌ **禁止**：多个public业务方法
- ❌ **禁止包含业务规则** - 业务规则必须在Domain层实现
- ❌ **禁止直接数据访问** - 必须通过Repository接口进行数据访问
- ❌ **禁止UI逻辑处理** - UI相关逻辑必须在Gateway层处理

**命名规范**：使用`{Action}{Entity}UseCase`格式，但必须确保Action描述具体的单一行为：
    - ✅ **正确示例**：`CreateTalentUseCase`、`FindTalentByCodeUseCase`、`FindTalentByNameUseCase`
    - ❌ **严格禁止宽泛命名**：`FindTalentUseCase`（过于宽泛，容易导致多个公开方法）
    - ❌ **严格禁止在单个UseCase中添加多个方法** - 如果需要多种查找方式，必须创建多个具体的UseCase类

**✅ 正确示例：**
```java
// 每个UseCase只有一个execute方法，专注单一职责
public class GetLocationHierarchyPathUseCase {
    public List<LocationOutput> execute(Long locationId) { ... }
}

public class GetLocationChildrenUseCase {
    public List<LocationOutput> execute(Long parentId) { ... }
}

public class GetLocationsByTypeUseCase {
    public List<LocationOutput> execute(LocationType type) { ... }
}
```

#### 4.1.2 UseCase实现规范（标准案例）

**UseCase标准实现**
```java
@Component
@RequiredArgsConstructor
@Transactional
public class CreateTalentUseCase {
    private final TalentRepository talentRepository;
    
    public TalentOutput execute(CreateTalentInput input) {
        // 1. 输入验证
        validateInput(input);
        // 2. 业务规则检查
        checkBusinessRules(input);
        // 3. 创建领域对象
        Talent talent = Talent.create(input.getName(), input.getSkills());
        // 4. 持久化并返回
        return TalentOutput.from(talentRepository.save(talent));
    }
}


@Service
public class SearchEntitiesUseCase {
    private final EntityRepository entityRepository;
    
    public Pageable<EntityOutput> execute(EntityPageInput input) {
        // 直接调用Repository接口方法，传递查询参数
        Pageable<Entity> entities = entityRepository.searchEntities(
            input.getName(), 
            input.getEntityType(), 
            input.getPage(), 
            input.getSize()
        );
        return entities.map(EntityPageOutput::from);
    }
}
```

**异常处理原则：**
- UseCase应该捕获Domain异常并转换为应用层异常
- 使用有意义的错误消息
- 不要吞噬异常，确保错误能够被上层处理

**✅ 正确示例：**
```java
public EntityOutput execute(CreateEntityInput input) {
    try {
        validateInput(input);
        
        // 检查业务规则
        if (entityRepository.existsByName(input.getName())) {
            throw new BusinessException("实体名称已存在: " + input.getName());
        }
        
        Entity entity = Entity.builder()
            .name(input.getName())
            .description(input.getDescription())
            .build();
            
        Entity savedEntity = entityRepository.save(entity);
        return convertToOutput(savedEntity);
        
    } catch (DomainException e) {
        logger.error("领域异常: {}", e.getMessage());
        throw new BusinessException("业务处理失败: " + e.getMessage(), e);
    } catch (Exception e) {
        logger.error("创建实体时发生未知错误", e);
        throw new ApplicationException("系统错误，请稍后重试", e);
    }
}
```

#### 4.1.3 UseCase 测试规范
**测试重点：**
- 业务逻辑正确性
- 输入验证
- 异常处理
- DTO转换

**✅ 测试示例：**
```java
import com.i0.persistence.spring.pagination.SpringPage;
@ExtendWith(MockitoExtension.class)
class SearchEntitiesUseCaseTest {
    
    @Mock
    private EntityRepository entityRepository;
    
    @InjectMocks
    private SearchEntitiesUseCase useCase;
    
    @Test
    void should_return_entities_when_search_with_valid_input() {
        // Given
        EntityPageInput input = EntityPageInput.builder()
            .name("test")
            .page(0)
            .size(10)
            .build();
            
        List<Entity> entities = Arrays.asList(
            Entity.builder().id("1").name("test1").build(),
            Entity.builder().id("2").name("test2").build()
        );
        
        Pageable<Entity> page = SpringPage.of(entities, PageRequest.of(0, 10), 2);
        when(entityRepository.searchEntities("test", null, 0, 10))
            .thenReturn(page);
        
        // When
        EntityPageOutput result = useCase.execute(input);
        
        // Then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
    }
    
    @Test
    void should_throw_exception_when_page_is_negative() {
        // Given
        EntityPageInput input = EntityPageInput.builder()
            .page(-1)
            .size(10)
            .build();
        
        // When & Then
        assertThatThrownBy(() -> useCase.execute(input))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("页码不能为负数");
    }
}
```

### 4.2 DTO设计规范

**目录结构：**
```
application/dto/
├── input/          # 输入DTO
│   ├── Create{Entity}Input.java
│   ├── Update{Entity}Input.java
│   └── {Entity}PageInput.java
└── output/         # 输出DTO
    ├── {Entity}Output.java
    └── {Entity}PageOutput.java
```

**命名规范：**
- 输入DTO：`Create{Entity}Input`、`Update{Entity}Input`、`{Entity}PageInput`
- 输出DTO：`{Entity}Output`、`{Entity}PageOutput`

**设计原则：**
- 单一职责：每个DTO只负责特定数据传输场景
- 验证注解：输入DTO包含@NotNull、@NotBlank等验证
- 不可变性：使用final字段和构造函数
- 序列化友好：确保正确序列化/反序列化

#### 4.2.1 DTO强制性要求
**AI代码生成强制要求：**
- **分页查询场景检查**：当生成分页查询相关代码时，必须检查是否存在`{Entity}PageOutput`类
- **如果不存在分页输出DTO**：必须创建专门的`{Entity}PageOutput`类，不得复用普通输出DTO
- **UseCase返回类型验证**：分页查询UseCase的返回类型必须是`Pageable<{Entity}PageOutput>`
- **命名一致性检查**：确保分页输出DTO的命名严格遵循`{Entity}PageOutput`格式
- **目录结构验证**：分页输出DTO必须放置在正确的目录位置

**DTO实现检查清单：**
1. ✅ 是否为分页查询创建了专门的`{Entity}PageOutput`类？
2. ✅ 分页输出DTO是否只包含实体字段，不包含分页元数据？
3. ✅ UseCase返回类型是否使用`Pageable<{Entity}PageOutput>`？
4. ✅ 是否提供了`from(Entity entity)`静态转换方法？
5. ✅ 是否使用`Page.map()`进行批量转换？
6. ✅ 目录结构是否正确（`application/dto/output/`）？

**标准实现模板：**
```java
// 输入DTO
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CreateEntityInput {
    @NotBlank(message = "名称不能为空")
    private String name;
    
    @Size(max = 500, message = "描述不能超过500字符")
    private String description;
}

// 输出DTO
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EntityOutput {
    private String id;
    private String name;
    private String description;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}

// ✅ 正确的分页输出DTO实现
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EntityPageOutput {
    private String id;
    private String name;
    private String description;
    private EntityType entityType;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // 仅包含实体字段，不包含分页信息
    public static EntityPageOutput from(Entity entity) {
        return EntityPageOutput.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .entityType(entity.getEntityType())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}

// ✅ 正确的分页查询UseCase实现
@Service
public class GetEntityPageUseCase {
    public Pageable<EntityPageOutput> execute(EntityPageInput input) {
        Pageable<Entity> page = entityRepository.findByConditions(...);
        return page.map(EntityPageOutput::from);  // 利用Spring Data的map方法
    }
}
```

**❌ 错误的分页输出DTO实现：**
```java
// ❌ 错误1：在DTO中包含分页信息
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EntityPageOutput {
    private List<EntityOutput> content;  // ❌ 不应包含内容列表
    private Integer page;                // ❌ 不应包含分页信息
    private Integer size;                // ❌ 不应包含分页信息
    private Long totalElements;          // ❌ 不应包含分页信息
    private Boolean hasNext;             // ❌ 不应包含分页信息
    
    public static EntityPageOutput from(Pageable<Entity> page) {
        // ❌ 错误的转换方式
        return EntityPageOutput.builder()
                .content(page.getContent().stream().map(EntityOutput::from).collect(Collectors.toList()))
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .hasNext(page.hasNext())
                .build();
    }
}

// ❌ 错误2：复用普通输出DTO进行分页查询
public class SearchEntitiesUseCase {
    public Pageable<EntityOutput> execute(EntityPageInput input) {  // ❌ 应使用EntityPageOutput
        Pageable<Entity> page = entityRepository.findByConditions(...);
        return page.map(EntityOutput::from);  // ❌ 违反命名规范和职责分离原则
    }
}

// ❌ 错误3：缺少专门的分页输出DTO
// 如果只有EntityOutput而没有EntityPageOutput，说明违反了分页输出DTO规范
```

**常用验证注解：**
- `@NotNull`、`@NotBlank`、`@NotEmpty`：空值验证
- `@Size(min, max)`：长度限制
- `@Min(value)`、`@Max(value)`：数值范围
- `@Pattern(regexp)`：正则表达式
- `@Email`：邮箱格式
- `@Valid`：级联验证
- `@AssertTrue`：自定义验证逻辑

**自定义验证示例：**
```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EntityCodeValidator.class)
public @interface ValidEntityCode {
    String message() default "编码格式不正确";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class EntityCodeValidator implements ConstraintValidator<ValidEntityCode, String> {
    private static final Pattern CODE_PATTERN = Pattern.compile("^[A-Z]{2,3}[0-9]{4,6}$");
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || CODE_PATTERN.matcher(value).matches();
    }
}
```

#### 4.2.2 DTO 与 Domain 转换
**转换原则：**
- UseCase 负责 DTO 与 Domain 对象之间的转换
- Domain 层不依赖 DTO，保持纯净性
- 转换方法定义在 DTO 类中或专门的 Mapper 类中

**标准实现模板**
```java
// 输入DTO转换
public class CreateEntityInput {
    public Entity toDomain() {
        return Entity.create(name, description, entityType);
    }
}

// 输出DTO转换
public class EntityOutput {
    public static EntityOutput from(Entity entity) {
        return EntityOutput.builder()
            .id(entity.getId())
            .name(entity.getName())
            .build();
    }
    
    public static List<EntityOutput> fromList(List<Entity> entities) {
        return entities.stream().map(EntityOutput::from).collect(Collectors.toList());
    }
}
```


**UseCase 中的使用：**
```java
@Service
public class CreateEntityUseCase {
    public EntityOutput execute(CreateEntityInput input) {
        Entity entity = input.toDomain();  // 输入转换
        Entity saved = entityRepository.save(entity);
        return EntityOutput.from(saved);   // 输出转换
    }
}
```


#### 4.3 Application层禁止行为
❌ **禁止在Application层做的事情**：
```java
// ❌ 禁止：构建复杂查询条件
LambdaQueryWrapper<TalentDO> wrapper = new LambdaQueryWrapper<>();
wrapper.like(TalentDO::getName, name);

// ❌ 禁止：直接操作数据库实现
@Autowired
private TalentMapper talentMapper;

// ❌ 禁止：在UseCase中写SQL逻辑
public List<Talent> findTalents() {
    // SQL查询逻辑应该在Gateway层
}

// ❌ 禁止：多个public方法
public class TalentUseCase {
    public TalentOutput create(CreateTalentInput input) { ... }
    public TalentOutput update(UpdateTalentInput input) { ... } // 违反规范
}
```


## 5. Gateway层设计规范

### 5.1 MyBatis-Plus 数据访问规范

#### 5.1.1 数据访问层设计原则
本项目使用MyBatis-Plus作为ORM框架，遵循以下数据访问规范：

#### 5.1.2 Mapper接口规范

**1. 基础CRUD操作**
- ❌ **严格禁止**：在Mapper接口中添加带@Select、@Insert、@Update、@Delete注解的自定义方法
- ✅ **必须**：Mapper接口必须继承MyBatis-Plus的BaseMapper<T>接口
- ✅ **只允许**：使用BaseMapper提供的基础CRUD方法，保持Mapper接口简洁

**2. 复杂查询实现**
- ❌ **严格禁止**：硬编码SQL语句，必须通过条件判断动态构建查询条件
- ✅ **必须**：在Repository实现类中使用LambdaQueryWrapper构建查询条件
- ✅ **必须**：优先使用LambdaQueryWrapper而非QueryWrapper，确保编译时类型检查

**✅ 正确示例：**
```java
// ✅ 正确：简洁的Mapper接口
@Mapper
public interface EntityMapper extends BaseMapper<EntityDO> {
    // 仅继承BaseMapper，不添加自定义方法
}

// ✅ 正确：在Repository实现中使用LambdaQueryWrapper
@Repository
public class EntityRepositoryImpl implements EntityRepository {
    
    private final EntityMapper entityMapper;
    
    @Override
    public Pageable<Entity> searchEntities(String name, EntityType type, int page, int size) {
        LambdaQueryWrapper<EntityDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EntityDO::getDeleted, false);
        
        if (StringUtils.hasText(name)) {
            queryWrapper.like(EntityDO::getName, name);
        }
        
        if (type != null) {
            queryWrapper.eq(EntityDO::getEntityType, type);
        }
        
        Page<EntityDO> pageRequest = new Page<>(page + 1, size);
        Page<EntityDO> result = entityMapper.selectPage(pageRequest, queryWrapper);
        
        return result.convert(this::convertToDomain);
    }
    
    @Override
    public boolean existsByName(String name) {
        LambdaQueryWrapper<EntityDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EntityDO::getName, name)
                   .eq(EntityDO::getDeleted, false);
        return entityMapper.selectCount(queryWrapper) > 0;
    }
}

// ❌ 错误：在Mapper接口中使用@Select注解
@Mapper
public interface EntityMapper extends BaseMapper<EntityDO> {
    @Select("SELECT COUNT(*) FROM entities WHERE name = #{name} AND deleted = 0")
    Long countByName(@Param("name") String name);
    
    @Select("SELECT * FROM entities WHERE name LIKE CONCAT('%', #{keyword}, '%') AND deleted = 0")
    List<EntityDO> findByNameContaining(@Param("keyword") String keyword);
}
```

**3. 分页查询规范**
- **统一方式**：使用MyBatis-Plus的Page<T>进行分页查询
- **参数传递**：通过selectPage(Page<T> page, Wrapper<T> queryWrapper)方法实现
- **页码转换**：注意MyBatis-Plus页码从1开始，需要进行适当转换

**4. 查询条件构建最佳实践**
```java
// ✅ 推荐：使用LambdaQueryWrapper的链式调用
LambdaQueryWrapper<EntityDO> queryWrapper = new LambdaQueryWrapper<EntityDO>()
    .eq(EntityDO::getDeleted, false)
    .like(StringUtils.hasText(name), EntityDO::getName, name)
    .eq(type != null, EntityDO::getEntityType, type)
    .orderByDesc(EntityDO::getCreatedAt);

// ✅ 推荐：条件方法的使用
queryWrapper.like(StringUtils.hasText(name), EntityDO::getName, name);
// 等价于
if (StringUtils.hasText(name)) {
    queryWrapper.like(EntityDO::getName, name);
}
```


#### 5.1.3 数据访问层重构最佳实践

**重构原则：**
- **简化Mapper接口**：移除所有自定义SQL方法，仅保留BaseMapper继承
- **统一查询方式**：所有复杂查询通过LambdaQueryWrapper在Repository实现中完成
- **提高可维护性**：减少SQL硬编码，提升代码的类型安全性和可读性
- **增强可扩展性**：通过程序化查询条件构建，支持更灵活的动态查询需求

**重构前后对比：**
```java
// ❌ 重构前：Mapper接口包含自定义SQL方法
@Mapper
public interface EntityMapper extends BaseMapper<EntityDO> {
    @Select("SELECT COUNT(*) FROM entities WHERE name = #{name} AND deleted = 0")
    Long countByName(@Param("name") String name);
    
    @Select("SELECT * FROM entities WHERE entity_type = #{type} AND deleted = 0")
    List<EntityDO> selectByEntityType(@Param("type") String type);
    
    @Select("SELECT * FROM entities WHERE name LIKE CONCAT('%', #{keyword}, '%') AND deleted = 0 LIMIT #{offset}, #{limit}")
    List<EntityDO> findAllWithPage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
}

// ✅ 重构后：简洁的Mapper接口
@Mapper
public interface EntityMapper extends BaseMapper<EntityDO> {
    // 仅继承BaseMapper，不添加任何自定义方法
}

// ✅ 重构后：Repository实现中的查询逻辑
@Repository
public class EntityRepositoryImpl implements EntityRepository {
    
    @Override
    public boolean existsByName(String name) {
        LambdaQueryWrapper<EntityDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EntityDO::getName, name)
                   .eq(EntityDO::getDeleted, false);
        return entityMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public List<Entity> findByEntityType(EntityType type) {
        LambdaQueryWrapper<EntityDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EntityDO::getEntityType, type)
                   .eq(EntityDO::getDeleted, false);
        return entityMapper.selectList(queryWrapper)
                          .stream()
                          .map(this::convertToDomain)
                          .collect(Collectors.toList());
    }
    
    @Override
    public Pageable<Entity> searchEntities(String keyword, int page, int size) {
        LambdaQueryWrapper<EntityDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EntityDO::getDeleted, false);
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.like(EntityDO::getName, keyword);
        }
        
        Page<EntityDO> pageRequest = new Page<>(page + 1, size);
        Page<EntityDO> result = entityMapper.selectPage(pageRequest, queryWrapper);
        
        return result.convert(this::convertToDomain);
    }
}
```

**测试层面的变化：**
```java
// ❌ 重构前：测试需要mock具体的SQL方法
@Test
void shouldCountEntitiesByName() {
    when(entityMapper.countByName("testName")).thenReturn(2L);
    
    boolean exists = entityRepository.existsByName("testName");
    
    assertThat(exists).isTrue();
    verify(entityMapper).countByName("testName");
}

// ✅ 重构后：测试mock通用的BaseMapper方法
@Test
void shouldCountEntitiesByName() {
    when(entityMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(2L);
    
    boolean exists = entityRepository.existsByName("testName");
    
    assertThat(exists).isTrue();
    verify(entityMapper).selectCount(any(LambdaQueryWrapper.class));
}
```

**重构收益：**
1. **代码简洁性**：Mapper接口更加简洁，职责单一
2. **类型安全**：LambdaQueryWrapper提供编译时类型检查
3. **可维护性**：查询逻辑集中在Repository实现中，便于维护
4. **可扩展性**：动态查询条件构建，支持复杂业务场景
5. **测试友好**：测试代码更加稳定，不依赖具体SQL实现

#### 5.1.4 逻辑删除规范

**@TableLogic 注解配置强制要求：**
- ✅ **必须**：使用 `@TableLogic(value = "false", delval = "true")` 完整配置
- ❌ **禁止**：仅使用 `@TableLogic` 不指定 value 和 delval 属性
- ✅ **必须**：配合 `@TableField("is_deleted")` 指定数据库字段名

**逻辑删除实现强制要求：**
- ✅ **必须**：使用 MyBatis-Plus 的 `deleteById()` 方法执行逻辑删除
- ❌ **禁止**：手动设置 `isDeleted` 字段后调用 `updateById()` 方法
- ❌ **禁止**：先查询记录再手动更新删除标记的两步操作

**✅ 正确示例：**
```java
// ✅ 正确：DO类中的@TableLogic配置
@TableLogic(value = "false", delval = "true")
@TableField("is_deleted")
private Boolean isDeleted;

// ✅ 正确：Repository中的逻辑删除实现
public void delete(ServiceTypeEntity serviceTypeEntity) {
    if (serviceTypeEntity == null || serviceTypeEntity.getId() == null) {
        return;
    }
    
    // 使用 MyBatis-Plus 的逻辑删除特性
    int result = serviceTypeMapper.deleteById(serviceTypeEntity.getId());
    if (result == 0) {
        throw new RuntimeException("Failed to delete service type");
    }
    log.info("Service type logically deleted with id: {}", serviceTypeEntity.getId());
}
```

**❌ 错误示例：**
```java
// ❌ 错误：@TableLogic配置不完整
@TableLogic  // 缺少 value 和 delval 属性
@TableField("is_deleted")
private Boolean isDeleted;

// ❌ 错误：手动设置删除标记的实现方式
public void delete(ServiceTypeEntity serviceTypeEntity) {
    // 错误：手动查询和更新
    ServiceTypeDO serviceTypeDO = serviceTypeMapper.selectById(serviceTypeEntity.getId());
    if (serviceTypeDO != null) {
        serviceTypeDO.setIsDeleted(true);  // 手动设置删除标记
        serviceTypeMapper.updateById(serviceTypeDO);  // 手动更新
    }
}
```

**逻辑删除优势：**
- **自动处理**：MyBatis-Plus 自动将 `deleteById` 转换为 UPDATE 语句
- **性能优化**：避免先查询再更新的两次数据库操作
- **代码简洁**：一行代码完成逻辑删除操作
- **框架一致性**：符合 MyBatis-Plus 推荐的标准实现方式

#### 5.1.5 ServiceImpl 高级特性使用规范

**ServiceImpl 使用原则：**
- **继承关系**：Repository实现类应继承ServiceImpl<M,T>，其中M为Mapper接口，T为数据对象
- **方法复用**：优先使用ServiceImpl提供的内置方法，避免重复实现已有功能
- **性能优化**：选择更高效的内置方法替代低效实现

**RepositoryImpl 标准实现模板：**
```java
@Repository
@Transactional
public class LocationRepositoryImpl extends ServiceImpl<LocationMapper, LocationDO> implements LocationRepository {

    private static final Logger log = LoggerFactory.getLogger(LocationRepositoryImpl.class);

    // 使用ServiceImpl内置方法进行CRUD操作
    @Override
    public Location save(Location location) {
        log.debug("Saving location: {}", location.getName());

        LocationDO locationDO = LocationDO.from(location);
        saveOrUpdate(locationDO);  // 使用ServiceImpl的saveOrUpdate方法

        return convertToDomain(getById(locationDO.getId()));  // 使用ServiceImpl的getById方法
    }

    // 使用exists()方法替代count() > 0，提高查询效率
    @Override
    public boolean existsByName(String name) {
        log.debug("Checking if location exists by name: {}", name);

        return lambdaQuery()
            .eq(LocationDO::getName, name)
            .eq(LocationDO::getIsDeleted, false)
            .exists();  // 使用exists()而非count() > 0
    }

    // 使用count()方法直接传递查询条件
    @Override
    public long countByLocationType(LocationType locationType) {
        log.debug("Counting locations by type: {}", locationType);

        return count(new LambdaQueryWrapper<LocationDO>()
            .eq(LocationDO::getLocationType, locationType.name())
            .eq(LocationDO::getIsDeleted, false));
    }

    // 使用lambdaQuery()简化查询构建
    @Override
    public List<Location> findByNameContaining(String namePattern) {
        log.debug("Finding locations by name pattern: {}", namePattern);

        return lambdaQuery()
            .like(LocationDO::getName, namePattern)
            .eq(LocationDO::getIsDeleted, false)
            .orderByAsc(LocationDO::getLevel)
            .orderByAsc(LocationDO::getSortOrder)
            .orderByAsc(LocationDO::getName)
            .list()
            .stream()
            .map(LocationDO::toDomain)
            .collect(Collectors.toList());
    }

    // 分页查询使用lambdaQuery()简化
    @Override
    public Pageable<Location> findAll(int page, int size) {
        log.debug("Finding all locations with page: {}, size: {}", page, size);

        Page<LocationDO> pageRequest = new Page<>(page + 1, size);

        IPage<LocationDO> result = page(pageRequest,
            lambdaQuery()
                .eq(LocationDO::getIsDeleted, false)
                .orderByAsc(LocationDO::getLevel)
                .orderByAsc(LocationDO::getSortOrder)
                .orderByAsc(LocationDO::getName));

        List<Location> locations = result.getRecords().stream()
            .map(this::convertToDomain)
            .collect(Collectors.toList());

        return createSpringPageFromMyBatis(locations, page, size, result.getTotal());
    }

    // 动态条件查询的优雅实现
    @Override
    public Pageable<Location> searchLocations(String nameKeyword, LocationType locationType,
                                         String parentId, Boolean activeOnly, int page, int size) {
        log.debug("Searching locations with criteria: name={}, type={}, parentId={}, activeOnly={}, page={}, size={}",
            nameKeyword, locationType, parentId, activeOnly, page, size);

        Page<LocationDO> pageRequest = new Page<>(page + 1, size);

        IPage<LocationDO> result = page(pageRequest,
            lambdaQuery()
                .eq(LocationDO::getIsDeleted, false)
                .like(StringUtils.isNotBlank(nameKeyword), LocationDO::getName, nameKeyword)
                .eq(locationType != null, LocationDO::getLocationType,
                    locationType != null ? locationType.name() : null)
                .eq(StringUtils.isNotBlank(parentId), LocationDO::getParentId, parentId)
                .eq(activeOnly != null, LocationDO::getIsActive, activeOnly)
                .orderByAsc(LocationDO::getLevel)
                .orderByAsc(LocationDO::getSortOrder)
                .orderByAsc(LocationDO::getName));

        List<Location> locations = result.getRecords().stream()
            .map(this::convertToDomain)
            .collect(Collectors.toList());

        return createSpringPageFromMyBatis(locations, page, size, result.getTotal());
    }

    // 转换方法保持私有
    private Location convertToDomain(LocationDO locationDO) {
        if (locationDO == null) {
            return null;
        }
        return locationDO.toDomain();
    }
}
```

**ServiceImpl 内置方法使用指南：**

**1. CRUD 操作优化**
```java
// ✅ 优先使用ServiceImpl方法
saveOrUpdate(entity);     // 保存或更新
getById(id);              // 根据ID查询
removeById(id);           // 根据ID删除
updateById(entity);       // 根据ID更新
saveBatch(entities);      // 批量保存
updateBatchById(entities); // 批量更新
```

**2. 查询方法选择**
```java
// ✅ exists() vs count() > 0 - exists更高效
return lambdaQuery()
    .eq(EntityDO::getName, name)
    .exists();  // 直接返回boolean，性能更好

// ❌ 避免：count() > 0
return lambdaQuery()
    .eq(EntityDO::getName, name)
    .count() > 0;  // 需要计算完整数量，性能较差

// ✅ count()直接传递查询条件
return count(new LambdaQueryWrapper<EntityDO>()
    .eq(EntityDO::getType, type)
    .eq(EntityDO::getDeleted, false));

// ✅ lambdaQuery()简化查询构建
return lambdaQuery()
    .eq(EntityDO::getDeleted, false)
    .list();  // 直接获取列表
```

**3. 条件查询最佳实践**
```java
// ✅ 使用条件方法避免if判断
queryWrapper
    .like(StringUtils.isNotBlank(name), EntityDO::getName, name)
    .eq(type != null, EntityDO::getType, type)
    .eq(StringUtils.isNotBlank(parentId), EntityDO::getParentId, parentId);

// 等价于以下if判断，但代码更简洁
if (StringUtils.isNotBlank(name)) {
    queryWrapper.like(EntityDO::getName, name);
}
if (type != null) {
    queryWrapper.eq(EntityDO::getType, type);
}
if (StringUtils.isNotBlank(parentId)) {
    queryWrapper.eq(EntityDO::getParentId, parentId);
}
```

**4. 分页查询优化**
```java
// ✅ 使用lambdaQuery()简化分页查询
Page<EntityDO> pageRequest = new Page<>(page + 1, size);
IPage<EntityDO> result = page(pageRequest,
    lambdaQuery()
        .eq(EntityDO::getDeleted, false)
        .orderByAsc(EntityDO::getCreatedAt));

// ✅ 使用page()方法替代selectPage()
// page()方法是ServiceImpl提供的，可以直接使用lambdaQuery()
```

**重构收益和最佳实践：**

**1. 性能提升**
- **exists()**：相比count() > 0，数据库查询到第一条匹配记录就返回，避免全表扫描
- **条件查询**：使用boolean参数的条件方法，减少不必要的if判断和查询条件构建
- **批量操作**：使用saveBatch()、updateBatchById()等批量方法，减少数据库往返次数

**2. 代码简洁性**
- **方法链式调用**：lambdaQuery()支持流畅的链式调用，代码更加易读
- **内置方法复用**：充分利用ServiceImpl提供的丰富内置方法，避免重复实现
- **类型安全**：Lambda表达式提供编译时类型检查，减少运行时错误

**3. 可维护性**
- **查询逻辑集中**：所有查询逻辑在Repository实现中集中管理，便于维护
- **统一接口**：Repository接口保持业务语义，Gateway层负责技术实现
- **测试友好**：可以mock BaseMapper的通用方法进行单元测试

**4. 迁移指南**
对于现有代码，建议按以下步骤进行重构：

```java
// 步骤1：检查是否存在重复的CRUD实现
// ❌ 重复实现的基础CRUD方法
public Entity save(Entity entity) {
    if (entity.getId() == null) {
        entityMapper.insert(entityDO);
    } else {
        entityMapper.updateById(entityDO);
    }
}

// ✅ 使用ServiceImpl内置方法
public Entity save(Entity entity) {
    saveOrUpdate(entityDO);
}

// 步骤2：优化exists方法
// ❌ 使用count() > 0
public boolean existsByName(String name) {
    return entityMapper.selectCount(queryWrapper) > 0;
}

// ✅ 使用exists()
public boolean existsByName(String name) {
    return lambdaQuery()
        .eq(EntityDO::getName, name)
        .exists();
}

// 步骤3：简化查询构建
// ❌ 手动创建LambdaQueryWrapper
LambdaQueryWrapper<EntityDO> queryWrapper = new LambdaQueryWrapper<>();
queryWrapper.eq(EntityDO::getDeleted, false);
if (StringUtils.hasText(name)) {
    queryWrapper.like(EntityDO::getName, name);
}
List<EntityDO> result = entityMapper.selectList(queryWrapper);

// ✅ 使用lambdaQuery()
List<EntityDO> result = lambdaQuery()
    .eq(EntityDO::getDeleted, false)
    .like(StringUtils.hasText(name), EntityDO::getName, name)
    .list();
```

**测试策略：**
```java
@ExtendWith(MockitoExtension.class)
class LocationRepositoryImplTest {

    @Mock
    private LocationMapper locationMapper;

    @InjectMocks
    private LocationRepositoryImpl repository;

    @Test
    void shouldUseExistsMethodForNameCheck() {
        // 模拟exists查询返回true
        when(locationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        boolean exists = repository.existsByName("testName");

        assertThat(exists).isTrue();
        verify(locationMapper).selectCount(any(LambdaQueryWrapper.class));
    }

    @Test
    void shouldUseCountMethodWithTypeCondition() {
        // 模拟count查询
        when(locationMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);

        long count = repository.countByLocationType(LocationType.COUNTRY);

        assertThat(count).isEqualTo(5L);
        verify(locationMapper).selectCount(any(LambdaQueryWrapper.class));
    }
}
```



### 5.2 ACL（Anti-Corruption Layer）跨领域调用规范

#### 5.2.1 职责
- **领域隔离**：防止外部领域概念污染当前领域模型
- **数据转换**：将外部领域对象转换为当前领域对象
- **异常转换**：将外部领域异常转换为当前领域异常

#### 5.2.2 规范（约束）

**✅ 必须遵循**：
- **依赖配置**：Gateway模块**必须**在`build.gradle`中声明目标领域application模块依赖
- **唯一入口**：跨领域调用**必须**通过ACL适配器进行
- **数据转换**：**禁止**将外部领域对象直接传递给当前领域

**❌ 严格禁止**：
- **直接依赖**：**禁止**依赖其他领域的domain或gateway模块
- **跨层调用**：**禁止**在Domain或Application层进行跨领域调用
- **双向依赖**：**禁止**循环依赖或双向依赖

#### 5.2.3 示例

**目录结构**：
```
gateway/
├── acl/                    # 防腐层适配器
│   ├── FetchLocationAdapter.java
│   └── FetchEntityAdapter.java
├── controllers/
├── repositories/
└── config/
```

**依赖配置**：
```gradle
// client/gateway/build.gradle
dependencies {
    implementation project(':modules:client:domain')
    implementation project(':modules:client:application')
    implementation project(':modules:location:application')  // ✅ 正确
    // implementation project(':modules:location:domain')    // ❌ 禁止
}
```

**适配器实现**：
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
    
    private ClientLocation convertToClientLocation(LocationOutput output) {
        return ClientLocation.builder()
            .id(output.getId())
            .name(output.getName())
            .build();
    }
}
```

## 6. 总结

本规范定义了基于Clean Architecture + DDD的后端架构设计标准，包括：

- **架构分层**：Domain、Application、Gateway三层架构，严格遵循依赖倒置原则
- **设计原则**：依赖倒置、单一职责、框架无关，确保内层不依赖外层
- **实现规范**：UseCase设计、DTO转换、依赖注入的标准化实现模式
- **数据访问规范**：基于MyBatis-Plus BaseMapper的简化数据访问层设计
    - 禁止在Mapper接口中使用@Select等注解编写自定义SQL
    - 统一使用LambdaQueryWrapper构建类型安全的查询条件
    - 在Repository实现层集中处理复杂查询逻辑
- **ACL跨领域调用规范**：Anti-Corruption Layer防腐层设计，确保领域模型纯净性
    - 必须通过ACL层进行所有跨领域调用，严格禁止直接依赖
    - 只允许gateway模块依赖其他领域的application模块
    - 必须在ACL层完成领域对象转换和异常转换
    - 严格禁止双向依赖和循环依赖
- **测试规范**：支持高效的单元测试和集成测试，通过mock BaseMapper方法实现测试隔离
- **质量保证**：测试驱动开发、代码规范、架构约束的全面质量管控

**架构优势：**
1. **可维护性**：清晰的分层结构和职责分离，便于代码维护和功能扩展
2. **可测试性**：依赖注入和接口抽象，支持高效的单元测试和mock
3. **可扩展性**：基于接口的设计和动态查询构建，适应业务变化需求
4. **类型安全**：LambdaQueryWrapper提供编译时类型检查，减少运行时错误
5. **代码简洁**：统一的数据访问模式，减少样板代码和SQL硬编码

**AI代码生成质量保证：**
- **规范遵循度检查**：生成的代码必须100%符合本文档规范，不得有任何偏差
- **实现完整性验证**：确保所有必需的组件都已正确生成（特别是分页输出DTO）
- **命名一致性审查**：所有类名、方法名必须严格遵循命名规范
- **架构层次验证**：确保代码放置在正确的架构层次和目录结构中
- **依赖关系检查**：验证依赖方向和接口使用的正确性
- **ACL规范检查**：确保跨领域调用严格遵循防腐层设计，验证依赖配置和数据转换的正确性

遵循本规范可确保代码的高质量、高可维护性和强扩展性，为项目的长期发展奠定坚实的架构基础，同时确保AI生成的代码始终符合项目标准。
