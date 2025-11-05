package com.i0.entity.gateway.persistence.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.i0.domain.core.pagination.Pageable;
import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.repositories.EntityRepository;
import com.i0.persistence.spring.pagination.SpringPage;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.apache.commons.lang3.StringUtils;
import com.i0.entity.domain.valueobjects.EntityType;
import com.i0.entity.gateway.persistence.dataobjects.EntityDO;
import com.i0.entity.gateway.persistence.mappers.EntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * EntityRepository实现类
 * 使用MyBatis-Plus进行数据访问
 * 重构后充分利用 ServiceImpl 的链式查询方法
 */
@Slf4j
@Repository
@Transactional
public class EntityRepositoryImpl extends ServiceImpl<EntityMapper, EntityDO> implements EntityRepository {

    /**
     * 创建SpringPage从MyBatis分页结果
     */
    private SpringPage<Entity> createSpringPageFromMyBatis(List<Entity> entities, int page, int size, long total) {
        return SpringPage.of(new PageImpl<>(
            entities,
            PageRequest.of(page, size),
            total
        ));
    }

    /**
     * 创建基础查询条件（未删除的记录）
     */
    private LambdaQueryWrapper<EntityDO> baseQuery() {
        return new LambdaQueryWrapper<EntityDO>()
                .eq(EntityDO::getIsDeleted, false);
    }

    /**
     * 创建基础查询条件并按创建时间倒序排列
     */
    private LambdaQueryWrapper<EntityDO> baseQueryOrderByCreated() {
        return baseQuery().orderByDesc(EntityDO::getCreatedAt);
    }
    
    @Override
    public Entity save(Entity entity) {
        log.debug("Saving entity: {}", entity.getId());

        EntityDO entityDO = EntityDO.from(entity);
        saveOrUpdate(entityDO);  // 使用ServiceImpl的saveOrUpdate方法

        return convertToDomain(getById(entityDO.getId()));  // 使用ServiceImpl的getById方法
    }
    
    @Override
    public Optional<Entity> findById(String id) {
        log.debug("Finding entity by id: {}", id);

        return Optional.ofNullable(getById(id)).map(this::convertToDomain);
    }
    
    @Override
    public Optional<Entity> findByCode(String code) {
        log.debug("Finding entity by code: {}", code);

        return Optional.ofNullable(
                lambdaQuery()
                    .eq(EntityDO::getCode, code)
                    .eq(EntityDO::getIsDeleted, false)
                    .one()
            ).map(this::convertToDomain);
    }

    @Override
    public Optional<Entity> findByName(String name) {
        log.debug("Finding entity by name: {}", name);

        return Optional.ofNullable(
                lambdaQuery()
                    .eq(EntityDO::getName, name)
                    .eq(EntityDO::getIsDeleted, false)
                    .one()
            ).map(this::convertToDomain);
    }

    @Override
    public List<Entity> findByNameContaining(String nameKeyword) {
        log.debug("Finding entities by name containing: {}", nameKeyword);

        return lambdaQuery()
            .like(EntityDO::getName, nameKeyword)
            .eq(EntityDO::getIsDeleted, false)
            .orderByDesc(EntityDO::getCreatedAt)
            .list()
            .stream()
            .map(this::convertToDomain)
            .collect(Collectors.toList());
    }
    
    @Override
    public Pageable<Entity> findByEntityType(EntityType entityType, int page, int size) {
        log.debug("Finding entities by type: {}, page: {}, size: {}", entityType, page, size);

        Page<EntityDO> pageRequest = new Page<>(page + 1, size); // MyBatis-Plus页码从1开始
        IPage<EntityDO> pageResult = page(pageRequest,
                lambdaQuery()
                    .eq(EntityDO::getEntityType, entityType.name())
                    .eq(EntityDO::getIsDeleted, false)
                    .orderByDesc(EntityDO::getCreatedAt));

        return createSpringPageFromMyBatis(
                pageResult.getRecords().stream()
                        .map(this::convertToDomain)
                        .collect(Collectors.toList()),
                page, size, pageResult.getTotal()
        );
    }

    @Override
    public List<Entity> findByEntityType(EntityType entityType) {
        log.debug("Finding entities by type: {}", entityType);

        return lambdaQuery()
                .eq(EntityDO::getEntityType, entityType.name())
                .eq(EntityDO::getIsDeleted, false)
                .orderByDesc(EntityDO::getCreatedAt)
                .list()
                .stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Entity> findActiveByEntityType(EntityType entityType) {
        log.debug("Finding active entities by type: {}", entityType);

        return lambdaQuery()
                .eq(EntityDO::getEntityType, entityType.name())
                .eq(EntityDO::getIsActive, true)
                .eq(EntityDO::getIsDeleted, false)
                .orderByDesc(EntityDO::getCreatedAt)
                .list()
                .stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Entity> findAllActive() {
        log.debug("Finding all active entities");

        return lambdaQuery()
                .eq(EntityDO::getIsActive, true)
                .eq(EntityDO::getIsDeleted, false)
                .orderByDesc(EntityDO::getCreatedAt)
                .list()
                .stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Entity> findAllEntities() {
        log.debug("Finding all entities");

        return lambdaQuery()
                .eq(EntityDO::getIsDeleted, false)
                .orderByDesc(EntityDO::getCreatedAt)
                .list()
                .stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Entity> findAll() {
        return findAllEntities();
    }

    @Override
    public Pageable<Entity> findAll(int page, int size) {
        log.debug("Finding all entities, page: {}, size: {}", page, size);

        Page<EntityDO> pageRequest = new Page<>(page + 1, size); // MyBatis-Plus页码从1开始
        IPage<EntityDO> pageResult = page(pageRequest,
                lambdaQuery()
                    .eq(EntityDO::getIsDeleted, false)
                    .orderByDesc(EntityDO::getCreatedAt));

        return createSpringPageFromMyBatis(
                pageResult.getRecords().stream()
                        .map(this::convertToDomain)
                        .collect(Collectors.toList()),
                page, size, pageResult.getTotal()
        );
    }
    
    @Override
    public void delete(Entity entity) {
        log.debug("Deleting entity: {}", entity.getId());
        removeById(entity.getId());
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting entity by id: {}", id);
        removeById(id);
    }

    @Override
    public boolean existsByName(String name) {
        log.debug("Checking if entity exists by name: {}", name);
        return lambdaQuery()
                .eq(EntityDO::getName, name)
                .eq(EntityDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public boolean existsByCode(String code) {
        log.debug("Checking if entity exists by code: {}", code);
        return lambdaQuery()
                .eq(EntityDO::getCode, code)
                .eq(EntityDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public boolean existsByNameAndIdNot(String name, String excludeId) {
        log.debug("Checking if entity exists by name: {} excluding id: {}", name, excludeId);
        return lambdaQuery()
                .eq(EntityDO::getName, name)
                .ne(EntityDO::getId, excludeId)
                .eq(EntityDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public long count() {
        log.debug("Counting all entities");
        return count(baseQuery());
    }

    @Override
    public long countByEntityType(EntityType entityType) {
        log.debug("Counting entities by type: {}", entityType);
        return count(baseQuery().eq(EntityDO::getEntityType, entityType.name()));
    }

    @Override
    public long countActive() {
        log.debug("Counting active entities");
        return count(baseQuery().eq(EntityDO::getIsActive, true));
    }

    @Override
    public boolean existsByCodeAndIdNot(String code, String excludeId) {
        log.debug("Checking if entity code exists excluding ID: code={}, excludeId={}", code, excludeId);
        return lambdaQuery()
                .eq(EntityDO::getCode, code)
                .ne(EntityDO::getId, excludeId)
                .eq(EntityDO::getIsDeleted, false)
                .exists();
    }
    
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

    // 转换方法保持私有
    private Entity convertToDomain(EntityDO entityDO) {
        if (entityDO == null) {
            return null;
        }
        return entityDO.toDomain();
    }
}