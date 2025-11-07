# Backend 后端开发学习指南

> 面向前端开发者的后端开发完整教程
>
> 本文档将帮助你理解项目的后端架构，并能够独立开发一个完整的菜单模块

---

## 目录

1. [项目架构概述](#1-项目架构概述)
2. [技术栈介绍](#2-技术栈介绍)
3. [三层架构详解](#3-三层架构详解)
4. [Location模块示例分析](#4-location模块示例分析)
5. [完整开发流程：菜单模块实战](#5-完整开发流程菜单模块实战)
6. [数据库设计与Flyway](#6-数据库设计与flyway)
7. [API开发与测试](#7-api开发与测试)
8. [常见问题与最佳实践](#8-常见问题与最佳实践)

---

## 1. 项目架构概述

### 1.1 整体目录结构

```
backend/
├── frameworks/              # 框架层：公共基础设施
│   ├── domain.core/        # 领域核心框架（分页、异常等）
│   ├── gateway.context/    # 网关上下文（API响应包装、全局异常处理）
│   └── persistence.spring/ # 持久化框架（MyBatis-Plus配置）
│
├── modules/                # 业务模块层
│   ├── location/          # 示例：地理位置模块
│   │   ├── domain/        # 领域层（核心业务逻辑）
│   │   ├── application/   # 应用层（用例编排）
│   │   └── gateway/       # 网关层（API、数据库）
│   │
│   ├── talent/            # 人才模块
│   ├── client/            # 客户模块
│   └── app/               # 主应用启动模块
│
├── build.gradle           # Gradle构建配置
└── settings.gradle        # 项目设置
```

### 1.2 架构设计原则

本项目采用 **Clean Architecture + DDD（领域驱动设计）**：

- **依赖倒置**：外层依赖内层，内层不依赖外层
- **单一职责**：每个类只负责一个明确的功能
- **业务逻辑隔离**：核心业务逻辑在Domain层，不依赖任何框架

```
┌─────────────────────────────────────────┐
│         Gateway Layer (外层)            │
│  - Controllers (REST API)               │
│  - Repository Implementations           │
│  - Database Mappers                     │
│  - 依赖：Spring Boot, MyBatis-Plus      │
└──────────────┬──────────────────────────┘
               │ 依赖
┌──────────────▼──────────────────────────┐
│      Application Layer (中层)           │
│  - Use Cases (业务用例)                 │
│  - DTOs (数据传输对象)                  │
│  - 依赖：Spring Context                 │
└──────────────┬──────────────────────────┘
               │ 依赖
┌──────────────▼──────────────────────────┐
│         Domain Layer (内层)             │
│  - Entities (实体)                      │
│  - Value Objects (值对象)               │
│  - Repository Interfaces                │
│  - 依赖：仅JDK                          │
└─────────────────────────────────────────┘
```

---

## 2. 技术栈介绍

### 2.1 核心技术

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 11 | 编程语言 |
| Spring Boot | 2.7.18 | 应用框架 |
| MyBatis-Plus | 3.x | ORM框架（数据库操作） |
| MySQL | 8.0 | 关系型数据库 |
| Gradle | 8.5 | 构建工具 |
| Flyway | - | 数据库版本管理 |
| Lombok | - | 减少样板代码 |
| JUnit 5 | - | 单元测试框架 |

### 2.2 关键注解说明

```java
// Lombok注解（减少getter/setter等样板代码）
@Data           // 生成getter、setter、toString、equals、hashCode
@Builder        // 生成建造者模式代码
@NoArgsConstructor  // 生成无参构造函数
@AllArgsConstructor // 生成全参构造函数
@RequiredArgsConstructor // 生成final字段的构造函数

// Spring注解
@Component      // 标记为Spring组件
@Service        // 标记为服务层组件
@Repository     // 标记为数据访问层组件
@RestController // 标记为REST控制器
@RequestMapping // 映射HTTP请求路径
@Transactional  // 声明事务边界

// 验证注解（Bean Validation）
@Valid          // 触发验证
@NotNull        // 不能为null
@NotBlank       // 不能为空字符串
@Size           // 限制字符串长度
```

---

## 3. 三层架构详解

### 3.1 Domain Layer（领域层）- 核心业务逻辑

**职责**：定义业务实体、业务规则、仓储接口

**特点**：
- ✅ 只使用JDK基础库，不依赖任何框架
- ✅ 包含业务规则验证
- ✅ 通过方法封装属性变更
- ❌ 不包含审计字段（created_at、updated_at等）
- ❌ 不包含ORM注解（@Table、@Column等）

**目录结构**：
```
domain/
└── src/main/java/com/i0/{module}/domain/
    ├── entities/           # 实体类
    ├── valueobjects/       # 值对象（枚举等）
    └── repositories/       # 仓储接口（只定义，不实现）
```

**示例：实体类（Entity）**

```java
package com.i0.location.domain.entities;

public class Location {
    private String id;
    private String name;
    private LocationType locationType;
    private String isoCode;
    private String parentId;
    private Boolean active;

    // ✅ 通过静态工厂方法创建实体
    public static Location create(String name, LocationType locationType,
                                   String isoCode, String parentId) {
        validateName(name);  // 业务规则验证
        validateLocationType(locationType);

        return Location.builder()
                .name(name)
                .locationType(locationType)
                .isoCode(isoCode)
                .parentId(parentId)
                .active(true)
                .build();
    }

    // ✅ 通过业务方法修改状态
    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    // ✅ 业务规则验证
    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("地理位置名称不能为空");
        }
    }
}
```

**示例：值对象（Value Object）**

```java
package com.i0.location.domain.valueobjects;

public enum LocationType {
    CONTINENT("Continent", "大洲"),
    COUNTRY("Country", "国家"),
    PROVINCE("Province", "省/州"),
    CITY("City", "市");

    private final String displayName;
    private final String chineseName;

    LocationType(String displayName, String chineseName) {
        this.displayName = displayName;
        this.chineseName = chineseName;
    }

    // ✅ 包含业务逻辑方法
    public boolean canHaveChildren() {
        return this == CONTINENT || this == COUNTRY || this == PROVINCE;
    }
}
```

**示例：仓储接口（Repository Interface）**

```java
package com.i0.location.domain.repositories;

public interface LocationRepository {
    Location save(Location location);
    Optional<Location> findById(String id);
    List<Location> findByLocationType(LocationType type);
    boolean existsByName(String name);
    void deleteById(String id);
}
```

### 3.2 Application Layer（应用层）- 用例编排

**职责**：编排业务用例、定义DTO、协调Domain和Gateway

**特点**：
- ✅ 每个UseCase只有一个public方法：`execute()`
- ✅ 使用`@Component`或`@Service`注解
- ✅ 写操作使用`@Transactional`，查询操作不使用
- ✅ 通过Repository接口访问数据
- ❌ 不包含业务规则（业务规则在Domain层）

**目录结构**：
```
application/
└── src/main/java/com/i0/{module}/application/
    ├── dto/
    │   ├── input/          # 输入DTO
    │   └── output/         # 输出DTO
    └── usecases/           # 用例类
```

**示例：UseCase**

```java
package com.i0.location.application.usecases;

@Component
@RequiredArgsConstructor  // Lombok：生成final字段的构造函数
public class CreateLocationUseCase {

    private final LocationRepository locationRepository;

    @Transactional  // 写操作需要事务
    public LocationOutput execute(CreateLocationInput input) {
        // 1. 验证唯一性
        if (locationRepository.existsByName(input.getName())) {
            throw new IllegalArgumentException("地理位置名称已存在");
        }

        // 2. 创建领域对象（业务逻辑在Domain层）
        Location location = Location.create(
            input.getName(),
            input.getLocationType(),
            input.getIsoCode(),
            input.getParentId()
        );

        // 3. 保存
        Location saved = locationRepository.save(location);

        // 4. 转换为输出DTO
        return LocationOutput.from(saved);
    }
}
```

**示例：输入DTO**

```java
package com.i0.location.application.dto.input;

@Data
@Builder
public class CreateLocationInput {

    @NotBlank(message = "名称不能为空")
    @Size(max = 100, message = "名称不能超过100个字符")
    private String name;

    @NotNull(message = "类型不能为空")
    private LocationType locationType;

    @Size(max = 10, message = "ISO代码不能超过10个字符")
    private String isoCode;

    private String parentId;
}
```

**示例：输出DTO**

```java
package com.i0.location.application.dto.output;

@Data
@Builder
public class LocationOutput {
    private String id;
    private String name;
    private LocationType locationType;
    private String isoCode;
    private Boolean active;

    // ✅ 提供从实体转换的静态方法
    public static LocationOutput from(Location location) {
        return LocationOutput.builder()
                .id(location.getId())
                .name(location.getName())
                .locationType(location.getLocationType())
                .isoCode(location.getIsoCode())
                .active(location.getActive())
                .build();
    }
}
```

### 3.3 Gateway Layer（网关层）- 技术实现

**职责**：REST API、数据库访问、外部系统集成

**特点**：
- ✅ Controller提供REST API
- ✅ 使用MyBatis-Plus的BaseMapper和ServiceImpl
- ✅ 数据对象(DO)包含ORM注解和审计字段
- ✅ RepositoryImpl实现Domain层定义的Repository接口

**目录结构**：
```
gateway/
└── src/main/java/com/i0/{module}/gateway/
    ├── web/
    │   └── controllers/    # REST控制器
    └── persistence/
        ├── dataobjects/    # 数据对象（DO）
        ├── mappers/        # MyBatis Mapper接口
        └── repositories/   # Repository实现类
```

**示例：Controller**

```java
package com.i0.location.gateway.web.controllers;

@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final CreateLocationUseCase createLocationUseCase;
    private final GetLocationUseCase getLocationUseCase;

    @PostMapping
    public LocationOutput createLocation(@Valid @RequestBody CreateLocationInput input) {
        return createLocationUseCase.execute(input);
    }

    @GetMapping("/{id}")
    public LocationOutput getLocation(@PathVariable String id) {
        return getLocationUseCase.execute(id);
    }

    @GetMapping
    public Pageable<LocationOutput> searchLocations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String name) {

        LocationPageInput input = LocationPageInput.builder()
                .page(page)
                .size(size)
                .name(name)
                .build();

        return searchLocationsUseCase.execute(input);
    }
}
```

**示例：数据对象（DO）**

```java
package com.i0.location.gateway.persistence.dataobjects;

@Data
@TableName("locations")  // MyBatis-Plus注解：指定表名
public class LocationDO {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("name")
    private String name;

    @TableField("location_type")
    private String locationType;

    // ✅ 审计字段（只在DO中，Domain实体不包含）
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    // ✅ 逻辑删除字段
    @TableLogic(value = "false", delval = "true")
    @TableField("is_deleted")
    private Boolean isDeleted;

    // 转换方法
    public static LocationDO from(Location location) {
        return LocationDO.builder()
                .id(location.getId())
                .name(location.getName())
                .locationType(location.getLocationType().name())
                .build();
    }

    public Location toDomain() {
        Location location = new Location();
        location.setId(this.id);
        location.setName(this.name);
        location.setLocationType(LocationType.valueOf(this.locationType));
        return location;
    }
}
```

**示例：Mapper接口**

```java
package com.i0.location.gateway.persistence.mappers;

// ✅ 继承BaseMapper，自动获得CRUD方法
public interface LocationMapper extends BaseMapper<LocationDO> {
    // ❌ 不添加自定义SQL方法
    // ✅ 所有查询通过LambdaQueryWrapper构建
}
```

**示例：Repository实现**

```java
package com.i0.location.gateway.persistence.repositories;

@Repository
@Transactional
public class LocationRepositoryImpl
        extends ServiceImpl<LocationMapper, LocationDO>
        implements LocationRepository {

    @Override
    public Location save(Location location) {
        LocationDO locationDO = LocationDO.from(location);
        saveOrUpdate(locationDO);  // MyBatis-Plus提供的方法
        return getById(locationDO.getId()).toDomain();
    }

    @Override
    public Optional<Location> findById(String id) {
        LocationDO locationDO = getById(id);
        return Optional.ofNullable(locationDO).map(LocationDO::toDomain);
    }

    @Override
    public List<Location> findByLocationType(LocationType locationType) {
        // ✅ 使用LambdaQueryWrapper构建查询
        return lambdaQuery()
                .eq(LocationDO::getLocationType, locationType.name())
                .eq(LocationDO::getIsDeleted, false)
                .list()
                .stream()
                .map(LocationDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return lambdaQuery()
                .eq(LocationDO::getName, name)
                .eq(LocationDO::getIsDeleted, false)
                .exists();  // ✅ 使用exists()而非count() > 0
    }
}
```

---

## 4. Location模块示例分析

### 4.1 业务场景

Location模块管理地理位置信息（大洲、国家、省、市），是一个典型的层级结构数据。

**核心功能**：
- 创建、查询、更新、删除地理位置
- 按类型、父级查询
- 激活/停用地理位置
- 分页查询
- 树形结构查询

### 4.2 数据模型

```sql
CREATE TABLE locations (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    location_type VARCHAR(20) NOT NULL,
    iso_code VARCHAR(10),
    description TEXT,
    parent_id VARCHAR(36),
    level INT NOT NULL,
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);
```

### 4.3 API端点

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/locations` | 创建地理位置 |
| GET | `/api/v1/locations/{id}` | 获取单个地理位置 |
| PUT | `/api/v1/locations/{id}` | 更新地理位置 |
| DELETE | `/api/v1/locations/{id}` | 删除地理位置 |
| GET | `/api/v1/locations` | 分页查询 |
| GET | `/api/v1/locations/by-type/{type}` | 按类型查询 |
| GET | `/api/v1/locations/by-parent/{parentId}` | 按父级查询 |
| PATCH | `/api/v1/locations/{id}/activate` | 激活 |
| PATCH | `/api/v1/locations/{id}/deactivate` | 停用 |
| GET | `/api/v1/locations/tree` | 树形结构 |

---

## 5. 完整开发流程：菜单模块实战

现在让我们从零开始开发一个**菜单（Menu）模块**，用于管理系统菜单。

### 5.1 需求分析

**业务需求**：
- 菜单分为**目录**、**菜单**、**按钮**三种类型
- 菜单有层级关系（父菜单、子菜单）
- 菜单包含：名称、路径、图标、排序、权限标识、是否显示
- 支持CRUD操作
- 支持激活/停用
- 支持树形结构查询

### 5.2 Step 1：创建模块目录结构

```bash
backend/modules/menu/
├── domain/
│   └── src/main/java/com/i0/menu/domain/
│       ├── entities/Menu.java
│       ├── valueobjects/MenuType.java
│       └── repositories/MenuRepository.java
├── application/
│   └── src/main/java/com/i0/menu/application/
│       ├── dto/input/
│       │   ├── CreateMenuInput.java
│       │   ├── UpdateMenuInput.java
│       │   └── MenuPageInput.java
│       ├── dto/output/MenuOutput.java
│       └── usecases/
│           ├── CreateMenuUseCase.java
│           ├── GetMenuUseCase.java
│           ├── UpdateMenuUseCase.java
│           ├── DeleteMenuUseCase.java
│           └── GetMenuTreeUseCase.java
└── gateway/
    └── src/main/java/com/i0/menu/gateway/
        ├── web/controllers/MenuController.java
        └── persistence/
            ├── dataobjects/MenuDO.java
            ├── mappers/MenuMapper.java
            └── repositories/MenuRepositoryImpl.java
```

### 5.3 Step 2：Domain层开发

**创建值对象：MenuType.java**

```java
package com.i0.menu.domain.valueobjects;

public enum MenuType {
    DIRECTORY("Directory", "目录"),
    MENU("Menu", "菜单"),
    BUTTON("Button", "按钮");

    private final String displayName;
    private final String chineseName;

    MenuType(String displayName, String chineseName) {
        this.displayName = displayName;
        this.chineseName = chineseName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getChineseName() {
        return chineseName;
    }

    public boolean isDirectory() {
        return this == DIRECTORY;
    }

    public boolean isMenu() {
        return this == MENU;
    }

    public boolean isButton() {
        return this == BUTTON;
    }

    public boolean canHaveChildren() {
        return this == DIRECTORY || this == MENU;
    }
}
```

**创建实体：Menu.java**

```java
package com.i0.menu.domain.entities;

import com.i0.menu.domain.valueobjects.MenuType;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class Menu {
    private String id;
    private String name;              // 菜单名称
    private MenuType menuType;        // 菜单类型
    private String path;              // 路由路径
    private String icon;              // 图标
    private String permission;        // 权限标识
    private String parentId;          // 父菜单ID
    private Integer sortOrder;        // 排序
    private Boolean visible;          // 是否显示
    private Boolean active;           // 是否激活

    public static Menu create(String name, MenuType menuType, String path,
                              String icon, String permission, String parentId) {
        validateName(name);
        validateMenuType(menuType);
        validatePath(path, menuType);

        return Menu.builder()
                .name(name)
                .menuType(menuType)
                .path(path)
                .icon(icon)
                .permission(permission)
                .parentId(parentId)
                .sortOrder(0)
                .visible(true)
                .active(true)
                .build();
    }

    public void update(String name, String path, String icon,
                       String permission, Integer sortOrder, Boolean visible) {
        validateName(name);
        this.name = name;
        this.path = path;
        this.icon = icon;
        this.permission = permission;
        if (sortOrder != null) {
            this.sortOrder = sortOrder;
        }
        if (visible != null) {
            this.visible = visible;
        }
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("菜单名称不能为空");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("菜单名称不能超过50个字符");
        }
    }

    private static void validateMenuType(MenuType menuType) {
        if (menuType == null) {
            throw new IllegalArgumentException("菜单类型不能为空");
        }
    }

    private static void validatePath(String path, MenuType menuType) {
        if (menuType == MenuType.MENU && (path == null || path.trim().isEmpty())) {
            throw new IllegalArgumentException("菜单类型必须指定路径");
        }
    }
}
```

**创建仓储接口：MenuRepository.java**

```java
package com.i0.menu.domain.repositories;

import com.i0.menu.domain.entities.Menu;
import com.i0.menu.domain.valueobjects.MenuType;
import com.i0.domain.core.pagination.Pageable;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {
    Menu save(Menu menu);
    Optional<Menu> findById(String id);
    List<Menu> findAll();
    List<Menu> findByMenuType(MenuType menuType);
    List<Menu> findByParentId(String parentId);
    boolean existsByName(String name);
    boolean existsByNameAndIdNot(String name, String excludeId);
    void deleteById(String id);

    Pageable<Menu> searchMenus(String nameKeyword, MenuType menuType,
                               String parentId, Boolean activeOnly, int page, int size);
}
```

### 5.4 Step 3：Application层开发

**创建输入DTO：CreateMenuInput.java**

```java
package com.i0.menu.application.dto.input;

import com.i0.menu.domain.valueobjects.MenuType;
import lombok.Data;
import lombok.Builder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class CreateMenuInput {

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称不能超过50个字符")
    private String name;

    @NotNull(message = "菜单类型不能为空")
    private MenuType menuType;

    @Size(max = 200, message = "路径不能超过200个字符")
    private String path;

    @Size(max = 50, message = "图标不能超过50个字符")
    private String icon;

    @Size(max = 100, message = "权限标识不能超过100个字符")
    private String permission;

    private String parentId;
    private Integer sortOrder;
    private Boolean visible;
}
```

**创建输出DTO：MenuOutput.java**

```java
package com.i0.menu.application.dto.output;

import com.i0.menu.domain.entities.Menu;
import com.i0.menu.domain.valueobjects.MenuType;
import lombok.Data;
import lombok.Builder;

import java.util.List;
import java.util.ArrayList;

@Data
@Builder
public class MenuOutput {
    private String id;
    private String name;
    private MenuType menuType;
    private String menuTypeDisplayName;
    private String path;
    private String icon;
    private String permission;
    private String parentId;
    private Integer sortOrder;
    private Boolean visible;
    private Boolean active;
    private List<MenuOutput> children;  // 用于树形结构

    public static MenuOutput from(Menu menu) {
        if (menu == null) {
            return null;
        }

        return MenuOutput.builder()
                .id(menu.getId())
                .name(menu.getName())
                .menuType(menu.getMenuType())
                .menuTypeDisplayName(menu.getMenuType().getChineseName())
                .path(menu.getPath())
                .icon(menu.getIcon())
                .permission(menu.getPermission())
                .parentId(menu.getParentId())
                .sortOrder(menu.getSortOrder())
                .visible(menu.getVisible())
                .active(menu.getActive())
                .children(new ArrayList<>())
                .build();
    }
}
```

**创建UseCase：CreateMenuUseCase.java**

```java
package com.i0.menu.application.usecases;

import com.i0.menu.application.dto.input.CreateMenuInput;
import com.i0.menu.application.dto.output.MenuOutput;
import com.i0.menu.domain.entities.Menu;
import com.i0.menu.domain.repositories.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateMenuUseCase {

    private final MenuRepository menuRepository;

    @Transactional
    public MenuOutput execute(CreateMenuInput input) {
        // 验证名称唯一性
        if (menuRepository.existsByName(input.getName())) {
            throw new IllegalArgumentException("菜单名称已存在: " + input.getName());
        }

        // 验证父菜单存在
        if (input.getParentId() != null && !input.getParentId().trim().isEmpty()) {
            menuRepository.findById(input.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("父菜单不存在"));
        }

        // 创建菜单
        Menu menu = Menu.create(
                input.getName(),
                input.getMenuType(),
                input.getPath(),
                input.getIcon(),
                input.getPermission(),
                input.getParentId()
        );

        // 设置可选字段
        if (input.getSortOrder() != null) {
            menu.setSortOrder(input.getSortOrder());
        }
        if (input.getVisible() != null) {
            menu.setVisible(input.getVisible());
        }

        // 生成ID并保存
        menu.setId(UUID.randomUUID().toString());
        Menu saved = menuRepository.save(menu);

        return MenuOutput.from(saved);
    }
}
```

### 5.5 Step 4：Gateway层开发

**创建数据对象：MenuDO.java**

```java
package com.i0.menu.gateway.persistence.dataobjects;

import com.baomidou.mybatisplus.annotation.*;
import com.i0.menu.domain.entities.Menu;
import com.i0.menu.domain.valueobjects.MenuType;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("menus")
public class MenuDO {

    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("name")
    private String name;

    @TableField("menu_type")
    private String menuType;

    @TableField("path")
    private String path;

    @TableField("icon")
    private String icon;

    @TableField("permission")
    private String permission;

    @TableField("parent_id")
    private String parentId;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("is_visible")
    private Boolean isVisible;

    @TableField("is_active")
    private Boolean isActive;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic(value = "false", delval = "true")
    @TableField("is_deleted")
    private Boolean isDeleted;

    public static MenuDO from(Menu menu) {
        return MenuDO.builder()
                .id(menu.getId())
                .name(menu.getName())
                .menuType(menu.getMenuType().name())
                .path(menu.getPath())
                .icon(menu.getIcon())
                .permission(menu.getPermission())
                .parentId(menu.getParentId())
                .sortOrder(menu.getSortOrder())
                .isVisible(menu.getVisible())
                .isActive(menu.getActive())
                .build();
    }

    public Menu toDomain() {
        return Menu.builder()
                .id(this.id)
                .name(this.name)
                .menuType(MenuType.valueOf(this.menuType))
                .path(this.path)
                .icon(this.icon)
                .permission(this.permission)
                .parentId(this.parentId)
                .sortOrder(this.sortOrder)
                .visible(this.isVisible)
                .active(this.isActive)
                .build();
    }
}
```

**创建Mapper：MenuMapper.java**

```java
package com.i0.menu.gateway.persistence.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.i0.menu.gateway.persistence.dataobjects.MenuDO;

public interface MenuMapper extends BaseMapper<MenuDO> {
    // 不添加自定义方法
}
```

**创建Repository实现：MenuRepositoryImpl.java**

```java
package com.i0.menu.gateway.persistence.repositories;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.i0.menu.domain.entities.Menu;
import com.i0.menu.domain.repositories.MenuRepository;
import com.i0.menu.domain.valueobjects.MenuType;
import com.i0.menu.gateway.persistence.dataobjects.MenuDO;
import com.i0.menu.gateway.persistence.mappers.MenuMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Transactional
public class MenuRepositoryImpl
        extends ServiceImpl<MenuMapper, MenuDO>
        implements MenuRepository {

    @Override
    public Menu save(Menu menu) {
        MenuDO menuDO = MenuDO.from(menu);
        saveOrUpdate(menuDO);
        return getById(menuDO.getId()).toDomain();
    }

    @Override
    public Optional<Menu> findById(String id) {
        MenuDO menuDO = getById(id);
        return Optional.ofNullable(menuDO).map(MenuDO::toDomain);
    }

    @Override
    public List<Menu> findAll() {
        return lambdaQuery()
                .eq(MenuDO::getIsDeleted, false)
                .orderByAsc(MenuDO::getSortOrder)
                .list()
                .stream()
                .map(MenuDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findByMenuType(MenuType menuType) {
        return lambdaQuery()
                .eq(MenuDO::getMenuType, menuType.name())
                .eq(MenuDO::getIsDeleted, false)
                .orderByAsc(MenuDO::getSortOrder)
                .list()
                .stream()
                .map(MenuDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Menu> findByParentId(String parentId) {
        return lambdaQuery()
                .eq(MenuDO::getParentId, parentId)
                .eq(MenuDO::getIsDeleted, false)
                .orderByAsc(MenuDO::getSortOrder)
                .list()
                .stream()
                .map(MenuDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return lambdaQuery()
                .eq(MenuDO::getName, name)
                .eq(MenuDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public void deleteById(String id) {
        removeById(id);
    }
}
```

**创建Controller：MenuController.java**

```java
package com.i0.menu.gateway.web.controllers;

import com.i0.menu.application.dto.input.CreateMenuInput;
import com.i0.menu.application.dto.output.MenuOutput;
import com.i0.menu.application.usecases.CreateMenuUseCase;
import com.i0.menu.application.usecases.GetMenuUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
public class MenuController {

    private final CreateMenuUseCase createMenuUseCase;
    private final GetMenuUseCase getMenuUseCase;

    @PostMapping
    public MenuOutput createMenu(@Valid @RequestBody CreateMenuInput input) {
        return createMenuUseCase.execute(input);
    }

    @GetMapping("/{id}")
    public MenuOutput getMenu(@PathVariable String id) {
        return getMenuUseCase.execute(id);
    }

    @GetMapping("/tree")
    public List<MenuOutput> getMenuTree() {
        return getMenuTreeUseCase.execute();
    }
}
```

### 5.6 Step 5：配置build.gradle

**domain/build.gradle**

```gradle
plugins {
    id 'java-library'
}

dependencies {
    implementation project(':frameworks:domain.core')
    implementation 'org.apache.commons:commons-lang3'
    compileOnly "org.projectlombok:lombok:${lombokVersion}"
    annotationProcessor "org.projectlombok:lombok:${lombokVersion}"
    implementation 'org.slf4j:slf4j-api'
}

configurations.all {
    exclude group: 'org.springframework'
}
```

**application/build.gradle**

```gradle
plugins {
    id 'java-library'
}

dependencies {
    implementation project(':frameworks:domain.core')
    implementation project(':frameworks:persistence.spring')
    implementation project(':menu-domain')

    implementation 'org.springframework:spring-context'
    implementation "javax.validation:validation-api:${validationApiVersion}"

    compileOnly "org.projectlombok:lombok:${lombokVersion}"
    annotationProcessor "org.projectlombok:lombok:${lombokVersion}"
}
```

**gateway/build.gradle**

```gradle
plugins {
    id 'java-library'
}

dependencies {
    implementation project(':frameworks:domain.core')
    implementation project(':frameworks:persistence.spring')
    implementation project(':menu-domain')
    implementation project(':menu-application')

    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation "com.baomidou:mybatis-plus-boot-starter:${mybatisPlusVersion}"
    runtimeOnly "mysql:mysql-connector-java:${mysqlConnectorVersion}"

    compileOnly "org.projectlombok:lombok:${lombokVersion}"
    annotationProcessor "org.projectlombok:lombok:${lombokVersion}"
}
```

---

## 6. 数据库设计与Flyway

### 6.1 创建数据库迁移脚本

在 `gateway/src/main/resources/db/migration/` 创建文件：

**V1__Create_menus_table.sql**

```sql
-- 创建菜单表
CREATE TABLE menus (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_type VARCHAR(20) NOT NULL COMMENT '菜单类型：DIRECTORY、MENU、BUTTON',
    path VARCHAR(200) COMMENT '路由路径',
    icon VARCHAR(50) COMMENT '图标',
    permission VARCHAR(100) COMMENT '权限标识',
    parent_id VARCHAR(36) COMMENT '父菜单ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    is_visible BOOLEAN DEFAULT TRUE COMMENT '是否显示',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_deleted BOOLEAN DEFAULT FALSE COMMENT '是否删除',

    KEY idx_parent_id (parent_id),
    KEY idx_menu_type (menu_type),
    KEY idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 插入初始数据
INSERT INTO menus (id, name, menu_type, path, icon, permission, parent_id, sort_order) VALUES
('menu-1', '系统管理', 'DIRECTORY', '/system', 'setting', NULL, NULL, 1),
('menu-2', '用户管理', 'MENU', '/system/users', 'user', 'system:user:list', 'menu-1', 1),
('menu-3', '角色管理', 'MENU', '/system/roles', 'team', 'system:role:list', 'menu-1', 2),
('menu-4', '菜单管理', 'MENU', '/system/menus', 'menu', 'system:menu:list', 'menu-1', 3);
```

### 6.2 Flyway配置

在 `application.yml` 中配置：

```yaml
spring:
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

---

## 7. API开发与测试

### 7.1 使用Postman测试API

**1. 创建菜单**

```http
POST /api/v1/menus
Content-Type: application/json

{
  "name": "系统管理",
  "menuType": "DIRECTORY",
  "path": "/system",
  "icon": "setting",
  "sortOrder": 1,
  "visible": true
}
```

**2. 查询菜单**

```http
GET /api/v1/menus/menu-1
```

**3. 分页查询**

```http
GET /api/v1/menus?page=0&size=20&name=系统
```

### 7.2 统一响应格式

所有API响应都会被 `ApiResponseWrapper` 自动包装：

**成功响应**：
```json
{
  "success": true,
  "data": {
    "id": "menu-1",
    "name": "系统管理",
    "menuType": "DIRECTORY",
    "path": "/system"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**错误响应**：
```json
{
  "success": false,
  "errorCode": "6000",
  "errorMessage": "菜单名称已存在",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## 8. 常见问题与最佳实践

### 8.1 常见错误

❌ **错误1：在Domain层使用Spring注解**
```java
// 错误示例
@Component  // Domain层不应该有Spring注解
public class Menu {
}
```

✅ **正确做法**：Domain层只使用Lombok注解和JDK

❌ **错误2：在UseCase中包含业务规则**
```java
// 错误示例
public MenuOutput execute(CreateMenuInput input) {
    if (input.getName() == null || input.getName().isEmpty()) {
        throw new IllegalArgumentException("名称不能为空");
    }
}
```

✅ **正确做法**：业务规则在Domain层验证

❌ **错误3：Mapper接口包含自定义SQL**
```java
// 错误示例
public interface MenuMapper extends BaseMapper<MenuDO> {
    @Select("SELECT * FROM menus WHERE name = #{name}")
    MenuDO findByName(String name);
}
```

✅ **正确做法**：使用LambdaQueryWrapper

### 8.2 开发检查清单

**Domain层**：
- [ ] 实体类不包含审计字段
- [ ] 不包含Spring/MyBatis注解
- [ ] 业务规则在实体方法中
- [ ] Repository只是接口定义

**Application层**：
- [ ] UseCase只有一个execute方法
- [ ] 使用@Component/@Service注解
- [ ] 写操作有@Transactional
- [ ] DTO有验证注解

**Gateway层**：
- [ ] DO包含审计字段和ORM注解
- [ ] Mapper继承BaseMapper，无自定义方法
- [ ] RepositoryImpl继承ServiceImpl
- [ ] Controller方法返回类型正确

**数据库**：
- [ ] 使用Flyway迁移脚本
- [ ] 包含所有审计字段
- [ ] 使用逻辑删除
- [ ] 创建必要的索引

### 8.3 快速开发技巧

**1. 使用IDE模板**

在IntelliJ IDEA中创建Live Template：
- `uc` → UseCase类模板
- `dto` → DTO类模板
- `ent` → Entity类模板

**2. 参考现有模块**

开发新模块时，参考 `location` 模块的结构和代码风格。

**3. 复制粘贴策略**

- 复制整个模块目录结构
- 全局替换类名（Location → Menu）
- 修改业务逻辑部分

### 8.4 学习资源

**项目内文档**：
- `/backend/CLAUDE-Backend.md` - 后端开发规范
- `/docs/backend/黄金10条/` - 核心规范
- `/docs/backend/architecture-overview.md` - 架构概览

**外部资源**：
- MyBatis-Plus官方文档：https://baomidou.com/
- Spring Boot官方文档：https://spring.io/projects/spring-boot
- Clean Architecture：《架构整洁之道》

---

## 总结

通过本文档，你应该已经掌握：

1. ✅ 理解三层架构（Domain、Application、Gateway）
2. ✅ 能够开发Domain层实体和业务逻辑
3. ✅ 能够创建Application层UseCase和DTO
4. ✅ 能够实现Gateway层API和数据库访问
5. ✅ 能够使用Flyway管理数据库版本
6. ✅ 能够独立开发一个完整的菜单模块

**下一步**：
- 实际动手开发菜单模块
- 编写单元测试和集成测试
- 学习更高级的特性（分页、树形结构、权限控制等）

祝你学习愉快！🚀
