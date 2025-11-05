# Gateway层设计规范

## 目录

- [1. 职责定位](#1-职责定位)
- [2. MyBatis-Plus数据访问规范](#2-mybatis-plus数据访问规范)
  - [2.1 Mapper接口规范](#21-mapper接口规范)
  - [2.2 查询条件构建规范](#22-查询条件构建规范)
  - [2.3 分页查询规范](#23-分页查询规范)
  - [2.4 逻辑删除规范](#24-逻辑删除规范)
- [3. ServiceImpl高级特性规范](#3-serviceimpl高级特性规范)
  - [3.1 继承关系](#31-继承关系)
  - [3.2 方法选择规范](#32-方法选择规范)
  - [3.3 条件查询最佳实践](#33-条件查询最佳实践)
- [4. ACL（Anti-Corruption Layer）跨领域调用规范](#4-aclanti-corruption-layer跨领域调用规范)
  - [4.1 职责范围](#41-职责范围)
  - [4.2 强制约束](#42-强制约束)
  - [4.3 目录结构](#43-目录结构)
  - [4.4 依赖配置](#44-依赖配置)
  - [4.5 适配器实现](#45-适配器实现)
  - [4.6 Gateway层依赖配置强制规范](#46-gateway层依赖配置强制规范)
- [5. 数据转换规范](#5-数据转换规范)
  - [5.1 转换原则](#51-转换原则)
  - [5.2 标准转换方法](#52-标准转换方法)
- [6. AI代码生成约束清单](#6-ai代码生成约束清单)
  - [6.1 Mapper接口生成约束](#61-mapper接口生成约束)
  - [6.2 Repository实现生成约束](#62-repository实现生成约束)
  - [6.3 逻辑删除生成约束](#63-逻辑删除生成约束)
  - [6.4 ACL适配器生成约束](#64-acl适配器生成约束)
  - [6.5 数据转换生成约束](#65-数据转换生成约束)
- [7. 质量保证](#7-质量保证)
  - [7.1 代码质量标准](#71-代码质量标准)
  - [7.2 架构约束](#72-架构约束)
  - [7.3 性能考虑](#73-性能考虑)

## 1. 职责定位

**核心职责**：外部系统集成层，处理数据持久化和外部服务调用
**边界范围**：位于架构最外层，依赖所有内部层，被Application层调用

## 2. MyBatis-Plus数据访问规范

### 2.1 Mapper接口规范

**✅ 必须遵守：**
- Mapper接口必须继承MyBatis-Plus的BaseMapper<T>接口
- 只允许使用BaseMapper提供的基础CRUD方法
- 保持Mapper接口简洁，无自定义方法

**❌ 严格禁止：**
- 在Mapper接口中添加带@Select、@Insert、@Update、@Delete注解的自定义方法
- 硬编码SQL语句
- 在Mapper接口中编写业务逻辑

**✅ 正确实现：**
```java
// ✅ 正确：简洁的Mapper接口
@Mapper
public interface EntityMapper extends BaseMapper<EntityDO> {
    // 仅继承BaseMapper，不添加自定义方法
}

// ✅ 正确：在Repository实现中使用LambdaQueryWrapper
@Repository
public class EntityRepositoryImpl extends ServiceImpl<EntityMapper, EntityDO> implements EntityRepository {

    private final EntityMapper entityMapper;

    @Override
    public Pageable<Entity> searchEntities(String nameKeyword, EntityType entityType, Boolean activeOnly, int page, int size) {
      log.debug("Searching entities with nameKeyword: {}, entityType: {}, activeOnly: {}, page: {}, size: {}",
              nameKeyword, entityType, activeOnly, page, size);

      Page<EntityDO> pageRequest = new Page<>(page + 1, size); // MyBatis-Plus页码从1开始

      // 构建动态查询条件 - 使用安全的条件构建方式
      LambdaQueryWrapper<EntityDO> queryWrapper = baseQuery();

      if (StringUtils.isNotBlank(nameKeyword)) {
        queryWrapper.like(EntityDO::getName, nameKeyword.trim());
      }
      if (entityType != null) {
        queryWrapper.eq(EntityDO::getEntityType, entityType.name());
      }
      if (activeOnly != null) {
        queryWrapper.eq(EntityDO::getIsActive, activeOnly);
      }

      queryWrapper.orderByDesc(EntityDO::getCreatedAt);

      IPage<EntityDO> pageResult = page(pageRequest, queryWrapper);

      return createSpringPageFromMyBatis(
              pageResult.getRecords().stream()
                      .map(this::convertToDomain)
                      .collect(Collectors.toList()),
              page, size, pageResult.getTotal()
      );
    }

  private SpringPage<Entity> createSpringPageFromMyBatis(List<Entity> entities, int page, int size, long total) {
    return SpringPage.of(new PageImpl<>(
            entities,
            PageRequest.of(page, size),
            total
    ));
  }

}
```

**❌ 错误实现：**
```java
// ❌ 错误：在Mapper接口中使用@Select注解
@Mapper
public interface EntityMapper extends BaseMapper<EntityDO> {
    @Select("SELECT COUNT(*) FROM entities WHERE name = #{name} AND deleted = 0")
    Long countByName(@Param("name") String name);

    @Select("SELECT * FROM entities WHERE name LIKE CONCAT('%', #{keyword}, '%') AND deleted = 0")
    List<EntityDO> findByNameContaining(@Param("keyword") String keyword);
}
```

### 2.2 查询条件构建规范

**✅ 必须使用：**
- LambdaQueryWrapper构建查询条件
- 优先使用LambdaQueryWrapper而非QueryWrapper
- 条件方法避免if判断

**✅ 正确实现：**
```java
// ✅ 推荐：使用LambdaQueryWrapper的链式调用
LambdaQueryWrapper<EntityDO> queryWrapper = new LambdaQueryWrapper<EntityDO>()
    .eq(EntityDO::getDeleted, false)
    .like(StringUtils.hasText(name), EntityDO::getName, name)
    .eq(type != null, EntityDO::getEntityType, type)
    .orderByDesc(EntityDO::getCreatedAt);

// ✅ 推荐：条件方法的使用
queryWrapper.like(StringUtils.hasText(name), EntityDO::getName, name);
// 等价于if判断，但代码更简洁
```

### 2.3 分页查询规范

**✅ 必须遵守：**
- 使用MyBatis-Plus的Page<T>进行分页查询
- 通过selectPage(Page<T> page, Wrapper<T> queryWrapper)方法实现
- 注意MyBatis-Plus页码从1开始，需要进行适当转换

### 2.4 逻辑删除规范

**✅ 必须配置：**
```java
// ✅ 正确：DO类中的@TableLogic配置
@TableLogic(value = "false", delval = "true")
@TableField("is_deleted")
private Boolean isDeleted;
```

**✅ 必须实现：**
```java
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
}
```

**❌ 严格禁止：**
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

## 3. ServiceImpl高级特性规范

### 3.1 继承关系

**✅ 必须遵守：**
- Repository实现类应继承ServiceImpl<M,T>
- M为Mapper接口，T为数据对象
- 优先使用ServiceImpl提供的内置方法

**✅ 标准实现：**
```java
@Repository
@Transactional
public class LocationRepositoryImpl extends ServiceImpl<LocationMapper, LocationDO> implements LocationRepository {

    // 使用ServiceImpl内置方法进行CRUD操作
    @Override
    public Location save(Location location) {
        LocationDO locationDO = LocationDO.from(location);
        saveOrUpdate(locationDO);  // 使用ServiceImpl的saveOrUpdate方法
        return convertToDomain(getById(locationDO.getId()));
    }

    // 使用exists()方法替代count() > 0，提高查询效率
    @Override
    public boolean existsByName(String name) {
        return lambdaQuery()
            .eq(LocationDO::getName, name)
            .eq(LocationDO::getIsDeleted, false)
            .exists();  // 使用exists()而非count() > 0
    }

    // 使用lambdaQuery()简化查询构建
    @Override
    public List<Location> findByNameContaining(String namePattern) {
        return lambdaQuery()
            .like(LocationDO::getName, namePattern)
            .eq(LocationDO::getIsDeleted, false)
            .list()
            .stream()
            .map(LocationDO::toDomain)
            .collect(Collectors.toList());
    }
}
```

### 3.2 方法选择规范

**✅ 优先使用：**
```java
// ✅ CRUD操作优化
saveOrUpdate(entity);     // 保存或更新
getById(id);              // 根据ID查询
removeById(id);           // 根据ID删除
updateById(entity);       // 根据ID更新
saveBatch(entities);      // 批量保存
updateBatchById(entities); // 批量更新

// ✅ exists() vs count() > 0 - exists更高效
return lambdaQuery()
    .eq(EntityDO::getName, name)
    .exists();  // 直接返回boolean，性能更好

// ✅ count()直接传递查询条件
return count(new LambdaQueryWrapper<EntityDO>()
    .eq(EntityDO::getType, type)
    .eq(EntityDO::getDeleted, false));

// ✅ lambdaQuery()简化查询构建
return lambdaQuery()
    .eq(EntityDO::getDeleted, false)
    .list();  // 直接获取列表
```

**❌ 避免使用：**
```java
// ❌ 避免：count() > 0
return lambdaQuery()
    .eq(EntityDO::getName, name)
    .count() > 0;  // 需要计算完整数量，性能较差
```

### 3.3 条件查询最佳实践

**✅ 使用条件方法避免if判断：**
```java
// ✅ 使用条件方法避免if判断
queryWrapper
    .like(StringUtils.isNotBlank(name), EntityDO::getName, name)
    .eq(type != null, EntityDO::getType, type)
    .eq(StringUtils.isNotBlank(parentId), EntityDO::getParentId, parentId);
```

## 4. ACL（Anti-Corruption Layer）跨领域调用规范

### 4.1 职责范围

**✅ 必须处理：**
- 领域隔离：防止外部领域概念污染当前领域模型
- 数据转换：将外部领域对象转换为当前领域对象
- 异常转换：将外部领域异常转换为当前领域异常

### 4.2 强制约束

**✅ 必须遵循：**
- Gateway模块必须在`build.gradle`中声明目标领域application模块依赖
- 跨领域调用必须通过ACL适配器进行
- 跨领域调用的实现必须在`gateway/acl/`目录下
- 禁止将外部领域对象直接传递给当前领域

**❌ 严格禁止：**
- 直接依赖其他领域的domain或gateway模块
- 在Domain或Application层进行跨领域调用
- 循环依赖或双向依赖

### 4.3 目录结构

```
gateway/
├── acl/                    # 防腐层适配器
│   ├── FetchLocationAdapter.java
│   └── FetchEntityAdapter.java
├── controllers/
├── repositories/
└── config/
```

### 4.4 依赖配置

**✅ 正确配置：**
```gradle
// client/gateway/build.gradle
dependencies {
    implementation project(':modules:client:domain')
    implementation project(':modules:client:application')
    implementation project(':modules:location:application')  // ✅ 正确
    // implementation project(':modules:location:domain')    // ❌ 禁止
}
```

### 4.5 适配器实现

**✅ 标准实现：**
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

### 4.6 Gateway层依赖配置强制规范

#### 4.6.1 强制依赖要求

**✅ 必须配置**：
- 对应业务模块的domain和application模块
- frameworks:gateway.context模块
- frameworks:domain.core模块
- frameworks:persistence.spring模块

**❌ 严格禁止**：
- 直接依赖其他领域的domain或gateway模块
- 缺少frameworks:gateway.context基础设施依赖
- 循环依赖或双向依赖

#### 4.6.2 标准配置模板

**✅ 正确配置**：
```gradle
// {domain}/gateway/build.gradle
dependencies {
    // 业务模块依赖（必须）
    implementation project(':modules:{domain}:domain')
    implementation project(':modules:{domain}:application')
    
    // Frameworks依赖（必须）
    implementation project(':frameworks:gateway.context')
    implementation project(':frameworks:domain.core')
    implementation project(':frameworks:persistence.spring')
    
    // 跨领域调用依赖（按需）
    implementation project(':modules:{other-domain}:application')
    
    // 测试依赖
    testImplementation project(':frameworks:test.context')
}
```

#### 4.6.3 依赖配置约束

**✅ 必须遵守**：
- Gateway模块必须依赖对应的domain和application模块
- 必须依赖frameworks:gateway.context提供基础设施
- 跨领域调用只能依赖目标领域的application模块
- 禁止依赖其他领域的domain或gateway模块

**❌ 错误配置示例**：
```gradle
// 错误配置示例
dependencies {
    implementation project(':modules:other-domain:domain')    // ❌ 禁止依赖其他领域domain
    implementation project(':modules:other-domain:gateway')   // ❌ 禁止依赖其他领域gateway
    // 缺少 frameworks:gateway.context                        // ❌ 缺少必要基础设施
    // 缺少 frameworks:persistence.spring                     // ❌ 缺少持久化框架
}
```

#### 4.6.4 配置验证清单

**必须检查项目**：
- [ ] 是否依赖了对应的domain模块
- [ ] 是否依赖了对应的application模块
- [ ] 是否依赖了frameworks:gateway.context
- [ ] 是否依赖了frameworks:domain.core
- [ ] 是否依赖了frameworks:persistence.spring
- [ ] 跨领域调用是否只依赖目标领域的application模块
- [ ] 是否避免了依赖其他领域的domain或gateway模块
- [ ] 测试依赖是否包含frameworks:test.context

## 5. 数据转换规范

### 5.1 转换原则

**✅ 必须遵守：**
- Gateway层负责Domain对象与DO对象之间的转换
- 转换方法必须在DO类中实现
- 保持转换逻辑的单一职责

### 5.2 标准转换方法

**✅ 正确实现：**
```java
// DO类中的转换方法
public class EntityDO {

    public static EntityDO from(Entity entity) {
        return EntityDO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .entityType(entity.getEntityType())
            .build();
    }

    public Entity toDomain() {
        return Entity.builder()
            .id(this.id)
            .name(this.name)
            .description(this.description)
            .entityType(this.entityType)
            .build();
    }
}
```

## 6. AI代码生成约束清单

### 6.1 Mapper接口生成约束

**必须检查项目：**
- [ ] Mapper接口是否继承BaseMapper<T>
- [ ] 是否包含任何自定义SQL方法
- [ ] 是否使用了@Select、@Insert等注解
- [ ] 是否保持接口简洁，无业务逻辑

### 6.2 Repository实现生成约束

**必须检查项目：**
- [ ] 是否继承ServiceImpl<M,T>
- [ ] 是否使用LambdaQueryChainWrapper构建查询条件
- [ ] 是否优先使用exists()而非count() > 0
- [ ] 是否使用lambdaQuery()简化查询构建
- [ ] 是否使用条件方法避免if判断

### 6.3 逻辑删除生成约束

**必须检查项目：**
- [ ] @TableLogic配置是否完整（value和delval）
- [ ] 是否配合@TableField指定字段名
- [ ] 是否使用deleteById()进行逻辑删除
- [ ] 是否避免手动设置删除标记

### 6.4 ACL适配器生成约束

**必须检查项目：**
- [ ] 是否在Gateway模块的build.gradle中正确配置依赖
- [ ] 是否只依赖目标领域的application模块
- [ ] 适配器是否完成数据转换和异常转换
- [ ] 是否避免直接传递外部领域对象

### 6.5 数据转换生成约束

**必须检查项目：**
- [ ] 转换方法是否在DO类中实现
- [ ] 是否提供from()和toDomain()方法
- [ ] 转换逻辑是否保持单一职责
- [ ] 是否正确处理所有字段映射

### 6.6 Gateway层依赖配置生成约束

**必须检查项目：**
- [ ] 是否依赖了对应的domain模块
- [ ] 是否依赖了对应的application模块
- [ ] 是否依赖了frameworks:gateway.context
- [ ] 是否依赖了frameworks:domain.core
- [ ] 是否依赖了frameworks:persistence.spring
- [ ] 跨领域调用是否只依赖目标领域的application模块
- [ ] 是否避免了依赖其他领域的domain或gateway模块
- [ ] 测试依赖是否包含frameworks:test.context
- [ ] 是否存在循环依赖或双向依赖
- [ ] 依赖配置是否符合4.6节的强制规范要求

## 7. 质量保证

### 7.1 代码质量标准

**必须达到的标准：**
- 100%符合本文档规范
- 通过所有集成测试
- 代码覆盖率≥85%
- 无SQL硬编码
- 类型安全的查询构建

### 7.2 架构约束

**必须遵守的约束：**
- 严格的分层架构
- 单向依赖关系
- ACL防腐层完整性
- 数据转换正确性
- 异常处理完整性

### 7.3 性能考虑

**必须优化的方面：**
- 使用exists()替代count() > 0
- 使用条件方法避免不必要的查询条件
- 批量操作减少数据库往返
- 分页查询性能优化