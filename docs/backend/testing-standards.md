# 后端测试规范与AI执行指南 (Backend Testing Standards & AI Execution Guide)

> **AI核心目标**：生成高质量、标准化的测试代码，确保测试的一致性和可维护性

## 目录 (Table of Contents)

- [1. TDD核心理念](#1-tdd核心理念)
- [2. 测试规范](#2-测试规范)
  - [2.1 FIRST原则](#21-first原则)
  - [2.2 测试层次](#22-测试层次)
  - [2.3 测试覆盖率要求](#23-测试覆盖率要求)
  - [2.4 测试优先级原则](#24-测试优先级原则)
- [3. 测试文件组织](#3-测试文件组织)
- [4. 测试框架与工具](#4-测试框架与工具)
- [5. TDD开发流程](#5-tdd开发流程)
- [6. 测试命名规范](#6-测试命名规范)
- [7. JUnit 5使用指南](#7-junit-5使用指南)
- [8. Mockito使用指南](#8-mockito使用指南)
- [9. 实践建议与最佳实践](#9-实践建议与最佳实践)
- [10. 集成测试异常断言规范](#10-集成测试异常断言规范)
- [11. AI集成测试生成指南](#11-ai集成测试生成指南)

---

## 1. TDD核心理念

测试驱动开发遵循 **Red-Green-Refactor** 循环：
- **Red (写失败的测试)** → **Green (写最少的代码让测试通过)** → **Refactor (重构代码)**
- **核心原则**：先写测试再写实现代码

---

## 2. 测试规范

### 2.1 FIRST原则
- **Fast**: 测试应该快速执行
- **Independent**: 测试之间相互独立
- **Repeatable**: 测试结果可重复
- **Self-Validating**: 测试有明确的通过/失败结果
- **Timely**: 测试应该及时编写

### 2.2 测试层次
- **单元测试**：测试单个类或方法
- **集成测试**：测试模块间交互
- **端到端测试**：测试完整业务流程

### 2.3 测试覆盖率要求
- **单元测试覆盖率** ≥ 90%
- **集成测试覆盖率** ≥ 80%
- **关键业务路径** 100% 覆盖

### 2.4 测试优先级原则

#### 2.4.1 编译优先原则 ✅
- **必须** 优先确保代码能够编译通过
- **必须** 将编译错误作为最高优先级问题首先解决
- **禁止** 在存在编译错误的情况下进行测试验证

#### 2.4.2 主干分支集成测试策略 ✅

**必须覆盖的核心场景：**
- ✅ **必须** 覆盖关键业务流程的端到端验证
- ✅ **必须** 验证模块间的主要交互路径
- ✅ **必须** 测试外部依赖的集成点
- ✅ **必须** 验证数据库操作的正确性
- ✅ **必须** 确保API接口的兼容性

**严格禁止的测试策略：**
- ❌ **严格禁止** 为边缘情况编写过多的集成测试
- ❌ **严格禁止** 为异常流程编写过于细粒度的集成测试用例

**测试范围限制：**
- **只允许** 在主干分支覆盖核心业务场景
- **只允许** 将边缘情况和异常流程测试放在单元测试中

#### 2.4.3 Gateway层Controller测试策略 ✅

**集成测试优先原则：**
- ✅ **必须** 优先使用集成测试覆盖Gateway层Controller的完整业务流程
- ✅ **必须** 通过集成测试验证从Controller到Repository的端到端流程
- ✅ **必须** 在集成测试中验证API接口的正确性和数据库操作

**Controller单元测试限制：**
- ❌ **严格禁止** 为已有集成测试覆盖的Gateway层Controller编写重复的单元测试
- ❌ **严格禁止** 同时维护功能重叠的Controller单元测试和集成测试

---

## 3. 测试文件组织

### 3.1 测试目录结构
**业务集成测试类统一在app module中管理**：

```
modules/
├── app/                           # 集成测试统一管理模块
│   ├── src/test/java/com/i0/app/integration/
│   │   ├── BasicIntegrationTest.java           # 基础集成测试抽象类
│   │   ├── EntityIntegrationTest.java           # 实体模块集成测试
│   │   ├── ServiceTypeIntegrationTest.java      # 服务类型集成测试
│   │   └── README.md                           # 集成测试说明文档
│   └── src/test/resources/                      # 集成测试共享资源
│       ├── db/migration/h2/                     # H2数据库测试专用脚本
│       │   ├── V1.0.1__Create_user_table.sql    # H2兼容的表结构脚本
│       │   ├── V1.0.2__Insert_test_data.sql     # H2兼容的测试数据
│       │   └── test-data.sql                    # 测试数据初始化脚本
│       └── application-test.yml                 # 测试环境配置
├── entity/
│   ├── domain/
│   │   ├── src/main/java/com/i0/entity/domain/
│   │   └── src/test/java/com/i0/entity/domain/     # Domain层单元测试
│   ├── application/
│   │   ├── src/main/java/com/i0/entity/application/
│   │   └── src/test/java/com/i0/entity/application/ # Application层单元测试
│   └── gateway/
│       ├── src/main/java/com/i0/entity/gateway/
│       └── src/test/java/com/i0/entity/gateway/    # Gateway层单元测试
```

### 3.1.1 H2数据库测试脚本规范

**脚本分离原则**：
- **生产脚本**：`modules/app/src/main/resources/db/migration/` (MySQL语法)
- **测试脚本**：`modules/app/src/test/resources/db/migration/h2/` (H2兼容语法)

**核心要求**：
- **必须**为H2创建独立测试脚本，确保功能一致性
- **必须**使用H2支持的SQL语法，避免MySQL特有语法

**常见语法差异**：
```sql
-- MySQL → H2 兼容性转换
-- ON UPDATE CURRENT_TIMESTAMP → 应用层处理
-- ENGINE=InnoDB, CHARSET=utf8mb4 → 移除
```

**测试配置**：
```yaml
spring:
  flyway:
    locations: classpath:db/migration/h2  # H2脚本路径
```

### 3.2 测试类存放规则
- **单元测试**：与被测试类在相同的包路径下，文件名为 `{ClassName}Test.java`
- **集成测试**：**统一存放在** `modules/app/src/test/java/com/i0/app/integration/` 目录下，文件名为 `{Entity}IntegrationTest.java`

### 3.3 集成测试继承规范
**所有业务集成测试类必须继承 `BasicIntegrationTest` 抽象类**：

```java
// ✅ 正确 - 继承BasicIntegrationTest
@SpringBootTest
@Transactional
class EntityIntegrationTest extends BasicIntegrationTest {
    // 测试实现
}
```

**BasicIntegrationTest 提供的核心功能**：
- **MockMvc** 和 **JdbcTemplate** 的自动配置
- **Spring 上下文加载验证**
- **基本端点连通性测试**
- **通用测试数据清理方法** `clearUpTestData(Class<?>... entityClasses)`
- **表名自动推断和TRUNCATE/DELETE操作**

---

## 4. 测试框架与工具
- **JUnit 5**: 现代化测试框架，支持参数化测试、动态测试、嵌套测试
- **Mockito**: Mock 框架，用于隔离依赖和验证交互
- **AssertJ**: 流畅断言库，提供更好的可读性和错误信息

---

## 5. TDD开发流程

**Red (写失败的测试)**
```java
@Test
void should_CreateUser_When_ValidDataProvided() {
    // Given
    CreateUserRequest request = CreateUserRequest.builder()
        .email("test@example.com")
        .name("Test User")
        .build();

    // When & Then
    assertThatThrownBy(() -> userService.createUser(request))
        .isInstanceOf(UnsupportedOperationException.class);
}
```

**Green (写最少的代码让测试通过)**
```java
@Service
public class UserService {
    public User createUser(CreateUserRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
```

**Refactor (重构代码)**
```java
@Service
public class UserService {

    private final UserRepository userRepository;

    public User createUser(CreateUserRequest request) {
        validateRequest(request);

        User user = User.builder()
            .email(request.getEmail())
            .name(request.getName())
            .createdAt(LocalDateTime.now())
            .build();

        return userRepository.save(user);
    }

    private void validateRequest(CreateUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
    }
}
```

---

## 6. 测试命名规范

### 6.1 测试类命名
- **单元测试**: `{ClassName}Test`
- **集成测试**: `{ClassName}IntegrationTest`
- **端到端测试**: `{Feature}E2ETest`

### 6.2 测试方法命名
采用 `should_ExpectedBehavior_When_StateUnderTest` 模式：
- `should_ReturnUser_When_ValidIdProvided()`
- `should_ThrowException_When_UserNotFound()`
- `should_UpdateUserStatus_When_ValidRequest()`

---

## 7. JUnit 5使用指南

### 7.1 基础注解
- `@Test`: 标记测试方法
- `@BeforeEach/@AfterEach`: 每个测试前后执行
- `@BeforeAll/@AfterAll`: 所有测试前后执行一次
- `@DisplayName`: 自定义测试显示名称
- `@Disabled`: 禁用测试

### 7.2 参数化测试
- `@ParameterizedTest`: 支持多种数据源
- `@ValueSource`: 简单值数组
- `@CsvSource`: CSV 格式数据
- `@MethodSource`: 方法提供数据

### 7.3 动态测试和嵌套测试
- `@TestFactory`: 动态生成测试
- `@Nested`: 组织相关测试用例

---

## 8. Mockito使用指南

### 8.1 Mock对象管理
- `@Mock`: 创建 Mock 对象
- `@InjectMocks`: 注入 Mock 依赖
- `@Spy`: 部分 Mock 真实对象

### 8.2 行为定义和验证
- `when().thenReturn()`: 定义方法返回值
- `when().thenThrow()`: 定义异常抛出
- `verify()`: 验证方法调用
- `verifyNoInteractions()`: 验证无交互

---

## 9. 实践建议与最佳实践

### 9.1 开发流程建议
1. **编译优先**：每次代码修改后，首先运行 `./gradlew compileJava` 确保编译通过
2. **单元测试优先**：先运行单元测试 (`./gradlew test`)，确保核心逻辑正确
3. **集成测试验证**：最后运行集成测试，验证模块间协作
4. **持续集成**：在 CI/CD 流程中，编译失败应该立即中断构建流程

### 9.2 集成测试实现最佳实践

#### 集成测试标准模板（基于BasicIntegrationTest）
```java
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class {Entity}IntegrationTest extends BasicIntegrationTest {

    @Autowired
    private {Entity}Controller {entity}Controller;

    @Autowired
    private {Entity}Repository {entity}Repository;

    @Test
    @DisplayName("完整的创建和查询流程")
    void should_CreateAndRetrieve{Entity}_When_ValidRequest() {
        // Given - 准备测试数据
        Create{Entity}Input request = Create{Entity}Input.builder()
            .name("测试名称")
            .code("TEST_CODE")
            .build();

        // When - 执行业务操作
        {Entity}Output created = {entity}Controller.create{Entity}(request);

        // Then - 验证结果
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("测试名称");

        // 验证数据库持久化
        Optional<{Entity}Entity> entity = {entity}Repository.findById(created.getId());
        assertThat(entity).isPresent();
        assertThat(entity.get().getName()).isEqualTo("测试名称");
    }

    @AfterEach
    void tearDown() {
        // 使用父类提供的数据清理方法
        clearUpTestData({Entity}DO.class);
    }
}
```

#### 数据清理最佳实践
```java
@AfterEach
void tearDown() {
    // ✅ 推荐 - 使用父类提供的清理方法
    clearUpTestData(EntityDO.class, ServiceTypeDO.class);

    // ✅ 支持多个实体类同时清理
    clearUpTestData(
        EntityDO.class,           // 实体表
        ServiceTypeDO.class,      // 服务类型表
        LocationDO.class          // 位置表
    );
}

// ❌ 不推荐 - 手动编写清理逻辑
@AfterEach
void tearDown() {
    jdbcTemplate.execute("DELETE FROM entities WHERE code LIKE 'TEST_%'");
}
```

---

## 10. 集成测试异常断言规范

### 10.1 HTTP状态码映射规则
- **400 Bad Request**: 所有业务异常、参数验证异常、业务数据不存在
- **404 Not Found**: 仅用于API接口资源不存在，不用于业务数据不存在
- **500 Internal Server Error**: 系统异常和未捕获异常

### 10.2 标准断言模板
```java
mockMvc.perform(/* API 调用 */)
    .andExpect(status().isBadRequest())                    // HTTP 状态码
    .andExpect(jsonPath("$.success").value(false))         // 响应状态
    .andExpect(jsonPath("$.code").value("ERROR_CODE"))     // 错误码
    .andExpect(jsonPath("$.message").value(containsString("关键词"))) // 错误信息
    .andExpect(jsonPath("$.data").isEmpty());              // 数据为空
```

### 10.3 异常断言规范总结

| 异常类型 | HTTP状态码 | 使用场景 | 断言方法 |
|---------|-----------|---------|----------|
| 业务异常 | 400 Bad Request | 实体不存在、已存在、状态冲突等 | `status().isBadRequest()` |
| 参数验证异常 | 400 Bad Request | 请求参数验证失败 | `status().isBadRequest()` |
| 系统异常 | 500 Internal Server Error | 未捕获异常、系统错误 | `status().isInternalServerError()` |
| 接口不存在 | 404 Not Found | API接口路径不存在 | `status().isNotFound()` |

**核心原则：**
- ✅ **业务数据不存在 = 400 Bad Request**
- ✅ **API接口不存在 = 404 Not Found**
- ✅ **所有业务异常 = 400 Bad Request**
- ✅ **系统异常 = 500 Internal Server Error**

---

## 11. AI集成测试生成指南

### 11.1 AI执行核心约束 (Must Follow)

#### 🎯 集成测试黄金法则
1. **继承强制**：所有集成测试必须继承 `BasicIntegrationTest`
2. **路径统一**：集成测试必须放在 `modules/app/src/test/java/com/i0/app/integration/`
3. **模板标准**：必须使用标准测试模板和命名规范
4. **清理自动化**：必须使用父类的 `clearUpTestData()` 方法

#### 📁 测试文件组织
- **单元测试**：与被测试类同路径，文件名 `{ClassName}Test.java`
- **集成测试**：统一存放在 `modules/app/src/test/java/com/i0/app/integration/`，文件名 `{Entity}IntegrationTest.java`

#### 🧪 测试优先级策略
- **编译优先**：必须先确保代码编译通过
- **集成测试优先**：Gateway层Controller优先使用集成测试，禁止重复的单元测试
- **核心场景覆盖**：只覆盖关键业务流程，边缘情况放在单元测试

### 11.2 AI生成集成测试标准流程

#### 第一步：检查继承关系 ✅
```java
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class {Entity}IntegrationTest extends BasicIntegrationTest {
    // 必须继承 BasicIntegrationTest
}
```

#### 第二步：标准依赖注入 ✅
```java
@Autowired
private {Entity}Controller {entity}Controller;

@Autowired
private {Entity}Repository {entity}Repository;
```

#### 第三步：标准测试方法 ✅
```java
@Test
@DisplayName("完整的创建和查询流程")
void should_CreateAndRetrieve{Entity}_When_ValidRequest() {
    // Given - 准备测试数据
    Create{Entity}Input request = Create{Entity}Input.builder()
        .name("测试名称")
        .code("TEST_CODE")
        .build();

    // When - 执行业务操作
    {Entity}Output created = {entity}Controller.create{Entity}(request);

    // Then - 验证结果和数据库持久化
    assertThat(created.getId()).isNotNull();
    assertThat(created.getName()).isEqualTo("测试名称");

    Optional<{Entity}Entity> entity = {entity}Repository.findById(created.getId());
    assertThat(entity).isPresent();
    assertThat(entity.get().getName()).isEqualTo("测试名称");
}
```

#### 第四步：数据清理 ✅
```java
@AfterEach
void tearDown() {
    clearUpTestData({Entity}DO.class); // 必须使用父类方法
}
```

### 11.3 AI生成检查清单 (Must Check)

#### 文件结构 ✅
- [ ] 文件路径：`modules/app/src/test/java/com/i0/app/integration/{Entity}IntegrationTest.java`
- [ ] 类名格式：`{Entity}IntegrationTest`
- [ ] 继承关系：`extends BasicIntegrationTest`

#### 注解要求 ✅
- [ ] `@SpringBootTest`
- [ ] `@Transactional`
- [ ] `@ActiveProfiles("test")`
- [ ] 测试方法：`@Test` + `@DisplayName`

#### 依赖注入 ✅
- [ ] Controller：`@Autowired private {Entity}Controller {entity}Controller`
- [ ] Repository：`@Autowired private {Entity}Repository {entity}Repository`

#### 测试方法 ✅
- [ ] 命名规范：`should_ExpectedBehavior_When_StateUnderTest`
- [ ] 结构：Given-When-Then
- [ ] 验证：AssertJ断言 + 数据库持久化验证

#### 数据清理 ✅
- [ ] 实现：`@AfterEach tearDown()`
- [ ] 方法：调用 `clearUpTestData({Entity}DO.class)`
- [ ] 禁止：手动编写SQL清理逻辑

### 11.4 ❌ 常见错误模式 (AI Must Avoid)

```java
// 错误1：未继承BasicIntegrationTest
class TalentIntegrationTest { // ❌ 缺少extends BasicIntegrationTest
}

// 错误2：路径错误
// modules/talent/gateway/src/test/java/.../TalentIntegrationTest.java // ❌

// 错误3：手动清理数据
@AfterEach
void tearDown() {
    jdbcTemplate.execute("DELETE FROM talents WHERE name LIKE 'TEST_%'"); // ❌
}

// 错误4：缺少数据清理
class TalentIntegrationTest extends BasicIntegrationTest {
    @Test
    void testCreateTalent() { /* 测试实现 */ }
    // ❌ 缺少 @AfterEach tearDown()
}
```

### 11.5 标准模板示例 (AI Must Use)

```java
// modules/app/src/test/java/com/i0/app/integration/{Entity}IntegrationTest.java
package com.i0.app.integration;

// imports...

/**
 * {Entity}模块集成测试
 * 验证完整的业务流程：Controller → UseCase → Repository → Database
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class {Entity}IntegrationTest extends BasicIntegrationTest {

    @Autowired
    private {Entity}Controller {entity}Controller;

    @Autowired
    private {Entity}Repository {entity}Repository;

    @Test
    @DisplayName("完整的创建和查询流程")
    void should_CreateAndRetrieve{Entity}_When_ValidRequest() {
        // Given - 准备测试数据
        Create{Entity}Input request = Create{Entity}Input.builder()
            .name("测试名称")
            .code("TEST_CODE")
            .build();

        // When - 执行业务操作
        {Entity}Output created = {entity}Controller.create{Entity}(request);

        // Then - 验证结果
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("测试名称");

        // 验证数据库持久化
        Optional<{Entity}Entity> entity = {entity}Repository.findById(created.getId());
        assertThat(entity).isPresent();
        assertThat(entity.get().getName()).isEqualTo("测试名称");
    }

    @AfterEach
    void tearDown() {
        clearUpTestData({Entity}DO.class);
    }
}
```

### 11.6 AI快速执行清单 (Quick Reference)

#### 生成集成测试的4个步骤：
1. **继承** → `extends BasicIntegrationTest`
2. **依赖** → 注入Controller和Repository
3. **方法** → Given-When-Then + AssertJ
4. **清理** → `@AfterEach clearUpTestData()`

#### 必须检查的5个要点：
- [ ] 文件路径：`modules/app/src/test/java/com/i0/app/integration/`
- [ ] 继承关系：`extends BasicIntegrationTest`
- [ ] 注解完整：`@SpringBootTest @Transactional @ActiveProfiles("test")`
- [ ] 数据清理：`clearUpTestData({Entity}DO.class)`
- [ ] 禁止手动SQL清理

**AI核心原则：继承BasicIntegrationTest + 使用标准模板 + 自动数据清理**