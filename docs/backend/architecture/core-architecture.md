# 核心架构设计规范

## 目录

- [1. 架构模式与分层](#1-架构模式与分层)
  - [1.1 核心架构模式](#11-核心架构模式)
  - [1.2 核心分层结构](#12-核心分层结构)
  - [1.3 依赖规则](#13-依赖规则)
- [2. Frameworks通用框架模块](#2-frameworks通用框架模块)
  - [2.1 职责定位](#21-职责定位)
  - [2.2 分层架构定义](#22-分层架构定义)
  - [2.3 核心交互规则](#23-核心交互规则)
  - [2.4 依赖配置规范](#24-依赖配置规范)
  - [2.5 使用规范](#25-使用规范)
  - [2.6 测试基础设施模块 (test.context)](#26-测试基础设施模块-testcontext)
    - [2.6.1 职责定位](#261-职责定位)
    - [2.6.2 主要功能](#262-主要功能)
    - [2.6.3 依赖配置](#263-依赖配置)
    - [2.6.4 使用规范](#264-使用规范)
- [3. 应用主入口模块](#3-应用主入口模块)
  - [3.1 职责范围](#31-职责范围)
  - [3.2 模块特点](#32-模块特点)
- [4. 业务领域模块](#4-业务领域模块)
  - [4.1 职责定位](#41-职责定位)
  - [4.2 模块间关系](#42-模块间关系)
- [5. 标准目录结构](#5-标准目录结构)
  - [5.1 领域模块标准结构](#51-领域模块标准结构)
  - [5.2 完整目录结构](#52-完整目录结构)
  - [5.3 Talent领域示例](#53-talent领域示例)
- [6. AI代码生成约束清单](#6-ai代码生成约束清单)
  - [6.1 架构分层约束](#61-架构分层约束)
  - [6.2 Frameworks使用约束](#62-frameworks使用约束)
  - [6.3 目录结构约束](#63-目录结构约束)
  - [6.4 模块依赖约束](#64-模块依赖约束)
  - [6.5 代码质量约束](#65-代码质量约束)
- [7. 质量保证](#7-质量保证)
  - [7.1 架构质量标准](#71-架构质量标准)
  - [7.2 架构约束](#72-架构约束)
  - [7.3 技术债务控制](#73-技术债务控制)

## 1. 架构模式与分层

### 1.1 核心架构模式

**✅ 必须使用**：Clean Architecture + Domain Driven Design (DDD)

### 1.2 核心分层结构

```
Gateway Layer (外层)
    ↓ 依赖
Application Layer (中层)
    ↓ 依赖
Domain Layer (内层)
```

### 1.3 依赖规则

**✅ 允许**：
- 外层可依赖内层
- 跨层通信通过接口

**❌ 严格禁止**：
- 内层不得依赖外层
- Domain层禁止框架依赖

## 2. Frameworks通用框架模块

### 2.1 职责定位

**核心职责**：系统基础设施层，提供跨业务模块的通用组件和抽象接口

### 2.2 分层架构定义

```
frameworks/
├── domain.core/           # 领域核心抽象（Pageable接口、通用值对象、异常基类）
├── persistence.spring/    # 持久化适配器（SpringPage、仓储基类）
├── gateway.context/       # 网关层基础设施（全局异常处理、API响应封装）
└── test.context/         # 测试基础设施（测试配置、数据工厂、构建器）
```

### 2.3 核心交互规则

**依赖方向**：外层依赖内层，内层不依赖外层
- **Domain层** → 只能依赖 `frameworks:domain.core`
- **Application层** → 可依赖 `frameworks:domain.core` + `frameworks:persistence.spring`
- **Gateway层** → 可依赖所有frameworks模块
- **测试代码** → 可依赖 `frameworks:test.context`（仅限测试范围）

**❌ 严格禁止**：
- 业务代码直接使用框架API
- 未声明依赖就使用frameworks组件
- 使用未批准的framework模块

### 2.4 依赖配置规范

**✅ 必须配置**：
```gradle
dependencies {
    // Domain层
    implementation project(':frameworks:domain.core')

    // Application层
    implementation project(':frameworks:domain.core')
    implementation project(':frameworks:persistence.spring')

    // Gateway层
    implementation project(':frameworks:domain.core')
    implementation project(':frameworks:persistence.spring')
    implementation project(':frameworks:gateway.context')
    testImplementation project(':frameworks:test.context')
}
```

### 2.5 使用规范

**✅ 正确用法**：
```java
// 使用统一分页接口
import com.i0.domain.core.pagination.Pageable;

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

### 2.6 测试基础设施模块 (test.context)

#### 2.6.1 职责定位

**核心职责**：为所有业务模块提供统一的测试基础设施和配置

#### 2.6.2 主要功能

**✅ 必须提供**：
- 标准化测试配置类 `TestMybatisPlusConfig`
- MyBatis-Plus分页插件配置
- 全局组件扫描（GlobalExceptionHandler、ApiResponseWrapper）
- H2内存数据库支持
- 统一测试环境配置

#### 2.6.3 依赖配置

**✅ 必须配置**：
```gradle
dependencies {
    api 'org.springframework.boot:spring-boot-starter-test'
    api "com.baomidou:mybatis-plus-boot-starter:${mybatisPlusVersion}"
    api project(':frameworks:gateway.context')
    api 'com.h2database:h2'
}
```

#### 2.6.4 使用规范

**✅ 必须使用**：
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

**❌ 严格禁止**：
- 在生产代码中引用test.context
- 不使用@Import(TestMybatisPlusConfig.class)
- 自定义测试配置而不使用统一基础设施

## 3. 应用主入口模块

### 3.1 职责范围

**✅ 必须负责**：
- Spring Boot主应用程序入口
- 数据库迁移管理（Flyway）
- 全局API响应封装
- 全局异常处理
- 全局配置管理
- 跨领域协调

**❌ 严格禁止**：
- 包含具体业务逻辑
- 参与业务领域间协调
- 直接处理业务请求

### 3.2 模块特点

**✅ 必须具备**：
- 不包含具体业务逻辑
- 提供基础设施和全局性封装
- 依赖各业务领域模块
- 负责应用程序的整体生命周期管理

## 4. 业务领域模块

### 4.1 职责定位

**核心职责**：
- 领域专注：每个模块专注于特定业务领域
- 业务逻辑封装：承接各自领域的完整业务需求
- 独立性：各领域模块相对独立，减少耦合
- 完整性：每个领域模块包含完整的三层架构

### 4.2 模块间关系

**依赖结构**：
```
app (应用主入口)
├── 依赖 → talent (人才领域)
├── 依赖 → entity (实体领域)
├── 依赖 → location (位置领域)
├── 依赖 → service (服务领域)
└── 依赖 → tenant (租户领域)
```

**交互规范**：
- ✅ 通过定义良好的接口进行交互
- ✅ 领域间可直接调用，但需遵循领域调用规范
- ✅ 遵循领域边界，保持高内聚低耦合
- ❌ app模块参与业务领域间的协调

## 5. 标准目录结构

### 5.1 领域模块标准结构

```
{domain}/
├── domain/          # 领域层：实体、值对象、仓储接口、领域服务
├── application/     # 应用层：UseCase、DTO、应用服务
└── gateway/         # 网关层：控制器、仓储实现、外部集成
```

### 5.2 完整目录结构

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
│   │   ├── usecases/          # 用例层：单一职责的业务行为
│   │   └── dto/               # 数据传输对象
│   └── gateway/
│       ├── controllers/       # 网关层：控制器
│       ├── repositories/      # 仓储实现
│       ├── external/          # 外部服务
│       └── config/            # 配置
```

### 5.3 Talent领域示例

```
modules/
├── talent/
│   ├── domain/
│   │   ├── src/main/java/com/i0/talent/domain/
│   │   │   ├── entities/       # 实体类
│   │   │   ├── valueobjects/   # 值对象
│   │   │   └── repositories/   # 仓储接口
│   │   └── src/test/java/com/i0/talent/domain/
│   │       ├── entities/       # 重点单元测试
│   │       ├── valueobjects/   # 重点单元测试
│   │       └── repositories/   # 接口契约测试
│   ├── application/
│   │   ├── src/main/java/com/i0/talent/application/
│   │   │   ├── usecases/       # 单一行为用例
│   │   │   └── dto/            # 数据传输对象
│   │   └── src/test/java/com/i0/talent/application/
│   │       ├── usecases/       # 重点单元测试 + Mock依赖
│   │       └── dto/            # 轻量单元测试
│   └── gateways/
│       ├── src/main/java/com/i0/talent/gateway/
│       │   ├── controllers/    # REST控制器
│       │   ├── repositories/   # 仓储实现
│       │   ├── external/       # 外部服务
│       │   └── config/         # 配置
│       └── src/test/java/com/i0/talent/gateway/
│           ├── controllers/    # Web层集成测试
│           ├── repositories/   # 数据层集成测试
│           ├── external/       # 外部服务集成测试
│           └── config/         # 配置集成测试
```

## 6. AI代码生成约束清单

### 6.1 架构分层约束

**必须检查项目：**
- [ ] 是否严格遵循三层架构（Domain、Application、Gateway）
- [ ] 依赖方向是否正确（外层依赖内层）
- [ ] Domain层是否只依赖frameworks:domain.core
- [ ] Application层是否正确依赖frameworks模块
- [ ] Gateway层是否完整依赖所有必要frameworks模块
- [ ] 是否存在内层依赖外层的违规情况

### 6.2 Frameworks使用约束

**必须检查项目：**
- [ ] 是否在build.gradle中正确声明frameworks依赖
- [ ] 业务代码是否使用frameworks抽象接口而非框架API
- [ ] 是否使用了未声明的frameworks模块
- [ ] 测试代码是否正确依赖frameworks:test.context
- [ ] 是否在集成测试中正确导入TestMybatisPlusConfig

### 6.3 目录结构约束

**必须检查项目：**
- [ ] 是否遵循标准的三层目录结构
- [ ] 每个领域模块是否包含完整的domain、application、gateway三层
- [ ] 测试目录结构是否与源代码结构对应
- [ ] 是否存在违规的目录结构或文件放置

### 6.4 模块依赖约束

**必须检查项目：**
- [ ] app模块是否只包含基础设施，不包含业务逻辑
- [ ] 业务领域模块是否保持相对独立
- [ ] 领域间交互是否通过良好定义的接口
- [ ] 是否存在循环依赖或不合规的依赖关系

#### 6.4.1 业务模块层级依赖规范

**✅ Application层依赖约束（必须遵守）：**
- [ ] 业务application层是否依赖了对应的业务domain层
- [ ] 业务application层是否正确依赖frameworks:domain.core
- [ ] 业务application层是否正确依赖frameworks:persistence.spring
- [ ] 业务application层是否避免了依赖gateway层或其他业务领域的domain层

**✅ Gateway层依赖约束（必须遵守）：**
- [ ] 业务gateway层是否依赖了对应的业务domain层
- [ ] 业务gateway层是否依赖了对应的业务application层
- [ ] 业务gateway层是否依赖了frameworks:gateway.context
- [ ] 业务gateway层是否依赖了frameworks:domain.core
- [ ] 业务gateway层是否依赖了frameworks:persistence.spring
- [ ] 跨领域调用是否只依赖目标领域的application层

**✅ Domain层依赖约束（必须遵守）：**
- [ ] 业务domain层是否只依赖frameworks:domain.core
- [ ] 业务domain层是否避免了依赖application层或gateway层
- [ ] 业务domain层是否避免了依赖其他业务领域的任何层级

#### 6.4.2 依赖关系层级图

```
Gateway Layer (外层)
    ↓ 必须依赖
Application Layer (中层)
    ↓ 必须依赖  
Domain Layer (内层)
    ↓ 只能依赖
frameworks:domain.core
```

**严格禁止的依赖关系：**
- ❌ Domain层依赖Application层或Gateway层
- ❌ Application层依赖Gateway层
- ❌ 任何层级的循环依赖
- ❌ 跨业务领域的domain层直接依赖

### 6.5 代码质量约束

**必须检查项目：**
- [ ] 是否存在违反依赖倒置原则的代码
- [ ] 接口是否在正确的层定义
- [ ] 业务逻辑是否在合适的层实现
- [ ] 是否存在跨层直接依赖具体实现的违规代码

## 7. 质量保证

### 7.1 架构质量标准

**必须达到的标准：**
- 100%符合本文档规范
- 严格的分层架构实现
- 正确的依赖关系
- 统一的frameworks使用
- 完整的测试覆盖

### 7.2 架构约束

**必须遵守的约束：**
- 单一职责原则
- 依赖倒置原则
- 开闭原则
- 接口隔离原则
- 里氏替换原则

### 7.3 技术债务控制

**必须控制的方面：**
- 架构违规代码数量为0
- 依赖关系清晰明确
- 模块间耦合度控制在最低
- 代码结构一致性
- 技术选型统一性