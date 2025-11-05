# Application层设计规范

## 目录
- [1. 职责定位](#1-职责定位)
- [2. UseCase设计规范](#2-usecase设计规范)
    - [2.1 强制性要求](#21-强制性要求)
    - [2.2 命名规范](#22-命名规范)
    - [2.3 标准实现模板](#23-标准实现模板)
    - [2.4 异常处理规范](#24-异常处理规范)
    - [2.5 测试规范](#25-测试规范)
- [3. DTO设计规范](#3-dto设计规范)
    - [3.1 目录结构](#31-目录结构)
    - [3.2 命名规范](#32-命名规范)
    - [3.3 设计原则](#33-设计原则)
    - [3.4 分页DTO强制规范](#34-分页dto强制规范)
    - [3.5 验证注解](#35-验证注解)
    - [3.6 DTO与Domain转换](#36-dto与domain转换)
- [4. Application层禁止行为](#4-application层禁止行为)
    - [4.1 严格禁止的操作](#41-严格禁止的操作)
    - [4.2 错误示例](#42-错误示例)
- [5. Application层依赖配置强制规范](#5-application层依赖配置强制规范)
    - [5.1 强制依赖要求](#51-强制依赖要求)
    - [5.2 标准配置模板](#52-标准配置模板)
    - [5.3 依赖配置约束](#53-依赖配置约束)
    - [5.4 配置验证清单](#54-配置验证清单)
- [6. AI代码生成约束清单](#6-ai代码生成约束清单)
    - [6.1 UseCase生成约束](#61-usecase生成约束)
    - [6.2 DTO生成约束](#62-dto生成约束)
    - [6.3 依赖关系约束](#63-依赖关系约束)
    - [6.4 测试生成约束](#64-测试生成约束)
- [7. 质量保证](#7-质量保证)
    - [7.1 代码质量标准](#71-代码质量标准)
    - [7.2 架构约束](#72-架构约束)
    - [7.3 性能考虑](#73-性能考虑)

## 1. 职责定位

**核心职责**：业务流程编排者，协调领域对象完成业务用例
**边界范围**：位于Domain层之上，Gateway层之下，负责业务流程协调

## 2. UseCase设计规范

### 2.1 强制性要求

**✅ 必须遵守：**
- 有且仅有一个public方法：`execute()`
- 必须使用注解：`@Component`/`@Service`、`@RequiredArgsConstructor`
- 必须使用`@Transactional`：只允许在数据写入或更新操作的UseCase中使用
- 必须单一职责：一个UseCase只处理一个具体的业务场景

**❌ 严格禁止：**
- 禁止多个public业务方法
- 禁止包含业务规则（必须在Domain层实现）
- 禁止直接数据访问（必须通过Repository接口）
- 禁止UI逻辑处理（必须在Gateway层处理）
- 禁止在查询场景使用`@Transactional`

### 2.2 命名规范

**✅ 正确格式**：`{Action}{Entity}UseCase`
- `CreateTalentUseCase`、`FindTalentByCodeUseCase`、`FindTalentByNameUseCase`

**❌ 严格禁止：**
- 宽泛命名：`FindTalentUseCase`（过于宽泛，容易导致多个公开方法）
- 单个UseCase包含多个方法（必须创建多个具体的UseCase类）

### 2.3 标准实现模板

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

### 2.4 异常处理规范

**✅ 必须遵守：**
- 捕获Domain异常并转换为应用层异常
- 使用有意义的错误消息
- 不允许吞噬异常，确保错误能够被上层处理

**标准实现：**
```java
public EntityOutput execute(CreateEntityInput input) {
    try {
        validateInput(input);

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

### 2.5 测试规范

**测试重点：**
- 业务逻辑正确性
- 输入验证
- 异常处理
- DTO转换

**标准测试模板：**
```java
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
    }
}
```

## 3. DTO设计规范

### 3.1 目录结构

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

### 3.2 命名规范

**输入DTO**：`Create{Entity}Input`、`Update{Entity}Input`、`{Entity}PageInput`
**输出DTO**：`{Entity}Output`、`{Entity}PageOutput`

### 3.3 设计原则

**✅ 必须遵守：**
- 单一职责：每个DTO只负责特定数据传输场景
- 验证注解：输入DTO必须包含@NotNull、@NotBlank等验证
- 不可变性：使用final字段和构造函数
- 序列化友好：确保正确序列化/反序列化

### 3.4 分页DTO强制规范

**AI代码生成强制要求：**
- 分页查询必须检查是否存在`{Entity}PageOutput`类
- 必须创建专门的`{Entity}PageOutput`类，禁止复用普通输出DTO
- UseCase返回类型必须是`Pageable<{Entity}PageOutput>`
- 分页输出DTO命名必须严格遵循`{Entity}PageOutput`格式
- 分页输出DTO必须放置在`application/dto/output/`目录

**DTO实现检查清单：**
1. ✅ 是否为分页查询创建了专门的`{Entity}PageOutput`类？
2. ✅ 分页输出DTO是否只包含实体字段，不包含分页元数据？
3. ✅ UseCase返回类型是否使用`Pageable<{Entity}PageOutput>`？
4. ✅ 是否提供了`from(Entity entity)`静态转换方法？
5. ✅ 是否使用`Page.map()`进行批量转换？
6. ✅ 目录结构是否正确（`application/dto/output/`）？

**✅ 正确实现：**
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

// 分页输出DTO
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class EntityPageOutput {
    private String id;
    private String name;
    private String description;
    private EntityType entityType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

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

// 分页查询UseCase
@Service
public class GetEntityPageUseCase {
    public Pageable<EntityPageOutput> execute(EntityPageInput input) {
        Pageable<Entity> page = entityRepository.findByConditions(...);
        return page.map(EntityPageOutput::from);
    }
}
```

**❌ 错误实现：**
```java
// 错误1：在DTO中包含分页信息
@Data @Builder
public class EntityPageOutput {
    private List<EntityOutput> content;  // ❌ 不应包含内容列表
    private Integer page;                // ❌ 不应包含分页信息
    private Integer size;                // ❌ 不应包含分页信息
    private Long totalElements;          // ❌ 不应包含分页信息
}

// 错误2：复用普通输出DTO进行分页查询
public class SearchEntitiesUseCase {
    public Pageable<EntityOutput> execute(EntityPageInput input) {  // ❌ 应使用EntityPageOutput
        Pageable<Entity> page = entityRepository.findByConditions(...);
        return page.map(EntityOutput::from);  // ❌ 违反命名规范
    }
}
```

### 3.5 验证注解

**常用注解：**
- `@NotNull`、`@NotBlank`、`@NotEmpty`：空值验证
- `@Size(min, max)`：长度限制
- `@Min(value)`、`@Max(value)`：数值范围
- `@Pattern(regexp)`：正则表达式
- `@Email`：邮箱格式
- `@Valid`：级联验证
- `@AssertTrue`：自定义验证逻辑

### 3.6 DTO与Domain转换

**转换原则：**
- UseCase负责DTO与Domain对象之间的转换
- Domain层不依赖DTO，保持纯净性
- 转换方法定义在DTO类中

**标准转换模板：**
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

// UseCase中的使用
@Service
public class CreateEntityUseCase {
    public EntityOutput execute(CreateEntityInput input) {
        Entity entity = input.toDomain();  // 输入转换
        Entity saved = entityRepository.save(entity);
        return EntityOutput.from(saved);   // 输出转换
    }
}
```

## 4. Application层禁止行为

### 4.1 严格禁止的操作

**❌ 禁止在Application层：**
- 构建复杂查询条件（LambdaQueryWrapper）
- 直接操作数据库实现（@Autowired Mapper）
- 编写SQL逻辑
- 多个public方法
- 业务规则实现
- UI逻辑处理

### 4.2 错误示例

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

## 5. Application层依赖配置强制规范

### 5.1 强制依赖要求

**🔴 强制要求：Application层模块必须在build.gradle中明确配置以下依赖：**

1. **Domain层依赖（必须）**
    - 必须依赖对应的Domain层模块
    - 格式：`implementation project(':模块名-domain')`

2. **Frameworks依赖（必须）**
    - 必须依赖`frameworks:domain.core`模块
    - 必须依赖`frameworks:persistence.spring`模块
    - 格式：`implementation project(':frameworks:domain.core')`
    - 格式：`implementation project(':frameworks:persistence.spring')`

3. **Spring Boot依赖（必须）**
    - Spring Boot Starter
    - Spring Boot Test（测试依赖）

### 5.2 标准配置模板

**Application层标准build.gradle配置模板：**

```gradle
dependencies {
    // 🔴 强制：Domain层依赖
    implementation project(':模块名-domain')  // 替换为实际模块名
    
    // 🔴 强制：Frameworks依赖
    implementation project(':frameworks:domain.core')
    implementation project(':frameworks:persistence.spring')
    
    // 🔴 强制：Spring Boot依赖
    implementation 'org.springframework.boot:spring-boot-starter'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    
    // 🔴 强制：测试依赖
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testImplementation 'org.junit.jupiter:junit-jupiter'
    testImplementation 'org.mockito:mockito-core'
    testImplementation 'org.assertj:assertj-core'
}

// 🔴 强制：测试配置
test {
    useJUnitPlatform()
    testLogging {
        events "passed", "skipped", "failed"
    }
}
```

### 5.3 依赖配置约束

**🚫 严格禁止的依赖配置：**

1. **禁止直接依赖Gateway层**
   ```gradle
   // ❌ 禁止
   implementation project(':模块名-gateway')
   ```

2. **禁止依赖其他Application层模块**
   ```gradle
   // ❌ 禁止
   implementation project(':模块名-application')
   ```

3. **禁止依赖数据库相关依赖**
   ```gradle
   // ❌ 禁止
   implementation 'mysql:mysql-connector-java'
   implementation 'com.baomidou:mybatis-plus-boot-starter'
   ```

4. **禁止依赖Web相关依赖**
   ```gradle
   // ❌ 禁止
   implementation 'org.springframework.boot:spring-boot-starter-web'
   ```

### 5.4 配置验证清单

**Application层依赖配置检查清单：**

- [ ] ✅ 已配置对应Domain层模块依赖
- [ ] ✅ 已配置`frameworks:domain.core`依赖
- [ ] ✅ 已配置`frameworks:persistence.spring`依赖
- [ ] ✅ 已配置Spring Boot基础依赖
- [ ] ✅ 已配置完整的测试依赖
- [ ] ✅ 未配置Gateway层依赖
- [ ] ✅ 未配置其他Application层模块依赖
- [ ] ✅ 未配置数据库相关依赖
- [ ] ✅ 未配置Web相关依赖
- [ ] ✅ 测试配置正确（useJUnitPlatform）

### 5.5 代码导入规范强制要求

**🔴 强制约束：Domain类显式导入**

Application层虽然通过build.gradle依赖了domain模块，但必须在代码中显式导入所有使用的domain类，否则会导致编译错误。

**必须显式导入的domain类：**
- Domain实体类：`import com.i0.module.domain.entities.{Entity}Entity`
- 值对象类：`import com.i0.module.domain.valueobjects.{ValueObject}`
- Repository接口：`import com.i0.module.domain.repositories.{Entity}Repository`
- 领域异常：`import com.i0.module.domain.exceptions.{DomainException}`

**正确示例：**
```java
package com.i0.service.application.usecases;

import com.i0.service.application.dto.input.UpdateServiceTypeInput;
import com.i0.service.application.dto.output.ServiceTypeOutput;
import com.i0.service.domain.entities.ServiceTypeEntity;        // ✅ 必须显式导入
import com.i0.service.domain.repositories.ServiceTypeRepository; // ✅ 必须显式导入
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateServiceTypeUseCase {
    private final ServiceTypeRepository serviceTypeRepository;

    public Optional<ServiceTypeOutput> execute(String id, UpdateServiceTypeInput input) {
        return serviceTypeRepository.findById(id)
                .map(serviceType -> {
                    // ✅ 可以直接使用ServiceTypeEntity，因为已显式导入
                    serviceType.update(input.getName(), input.getDescription());
                    return ServiceTypeOutput.from(serviceTypeRepository.save(serviceType));
                });
    }
}
```

**错误示例：**
```java
package com.i0.service.application.usecases;

import com.i0.service.application.dto.input.UpdateServiceTypeInput;
import com.i0.service.application.dto.output.ServiceTypeOutput;
import com.i0.service.domain.repositories.ServiceTypeRepository; // ❌ 缺少ServiceTypeEntity导入
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateServiceTypeUseCase {
    private final ServiceTypeRepository serviceTypeRepository;

    public Optional<ServiceTypeOutput> execute(String id, UpdateServiceTypeInput input) {
        return serviceTypeRepository.findById(id)
                .map(serviceType -> {
                    // ❌ 编译错误：ServiceTypeEntity未导入
                    serviceType.update(input.getName(), input.getDescription());
                    return ServiceTypeOutput.from(serviceTypeRepository.save(serviceType));
                });
    }
}
```

**AI代码生成约束检查项：**
- [ ] ✅ Application层代码中是否显式导入了所有使用的domain实体类
- [ ] ✅ Application层代码中是否显式导入了所有使用的值对象类
- [ ] ✅ Application层代码中是否显式导入了所有使用的Repository接口
- [ ] ✅ Application层代码中是否显式导入了所有使用的领域异常类
- [ ] ✅ 是否存在未导入domain类导致的编译错误
- [ ] ✅ 代码是否遵守[Java 11兼容性规范](../../dev-standards.md#3-java版本兼容性规范)

## 6. AI代码生成约束清单

### 5.1 UseCase生成约束

**必须检查项目：**
- [ ] UseCase类名是否符合`{Action}{Entity}UseCase`格式
- [ ] 是否只有一个public方法`execute()`
- [ ] 是否正确使用了`@Component`/`@Service`、`@RequiredArgsConstructor`
- [ ] 写入操作是否使用了`@Transactional`，查询操作是否未使用
- [ ] 是否通过Repository接口访问数据，而非直接操作数据库
- [ ] 业务规则是否在Domain层实现，而非UseCase中

### 5.2 DTO生成约束

**必须检查项目：**
- [ ] 分页查询是否创建了专门的`{Entity}PageOutput`类
- [ ] 分页输出DTO是否只包含实体字段，不包含分页元数据
- [ ] UseCase返回类型是否使用`Pageable<{Entity}PageOutput>`
- [ ] 是否提供了`from(Entity entity)`静态转换方法
- [ ] 是否使用`Page.map()`进行批量转换
- [ ] 目录结构是否正确（`application/dto/output/`）
- [ ] 输入DTO是否包含适当的验证注解

### 6.3 依赖关系约束

**必须检查项目：**
- [ ] Application层是否只依赖Domain层，不依赖Gateway层
- [ ] UseCase是否只通过Repository接口访问数据
- [ ] DTO转换逻辑是否在UseCase中处理
- [ ] 是否存在循环依赖
- [ ] 是否违反了依赖倒置原则

**🔴 新增：Application层依赖配置检查项：**
- [ ] build.gradle中是否配置了对应Domain层模块依赖
- [ ] build.gradle中是否配置了`frameworks:domain.core`依赖
- [ ] build.gradle中是否配置了`frameworks:persistence.spring`依赖
- [ ] build.gradle中是否配置了Spring Boot基础依赖
- [ ] build.gradle中是否配置了完整的测试依赖
- [ ] build.gradle中是否禁止了Gateway层依赖
- [ ] build.gradle中是否禁止了其他Application层模块依赖
- [ ] build.gradle中是否禁止了数据库相关依赖
- [ ] build.gradle中是否禁止了Web相关依赖
- [ ] 测试配置是否正确（useJUnitPlatform）

**🔴 新增：Application层代码导入检查项：**
- [ ] Application层代码中是否显式导入了所有使用的domain实体类
- [ ] Application层代码中是否显式导入了所有使用的值对象类
- [ ] Application层代码中是否显式导入了所有使用的Repository接口
- [ ] Application层代码中是否显式导入了所有使用的领域异常类
- [ ] 是否存在未导入domain类导致的编译错误

### 6.4 测试生成约束

**必须检查项目：**
- [ ] 是否为每个UseCase生成了对应的测试类
- [ ] 测试类名是否符合`{UseCaseName}Test`格式
- [ ] 是否测试了正常业务流程
- [ ] 是否测试了异常情况
- [ ] 是否测试了输入验证
- [ ] 是否使用了Mockito进行依赖mock

## 7. 质量保证

### 7.1 代码质量标准

**必须达到的标准：**
- 100%符合本文档规范
- 通过所有单元测试
- 代码覆盖率≥90%
- 通过静态代码分析
- 遵循单一职责原则

### 7.2 架构约束

**必须遵守的约束：**
- 严格的分层架构
- 单向依赖关系
- 接口面向编程
- 依赖注入模式
- 统一的异常处理

### 7.3 性能考虑

**必须考虑的因素：**
- 批量数据加载
- 事务边界控制
- 缓存策略使用
- 查询性能优化
- 内存使用效率