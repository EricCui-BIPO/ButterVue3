package com.i0.location.gateway.persistence.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.i0.domain.core.pagination.Pageable;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import com.i0.location.domain.valueobjects.LocationType;
import com.i0.location.gateway.persistence.dataobjects.LocationDO;
import com.i0.location.gateway.persistence.mappers.LocationMapper;
import com.i0.persistence.spring.pagination.SpringPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Location仓储实现
 * 继承ServiceImpl以利用MyBatis-Plus提供的通用方法
 * 实现LocationRepository接口，提供数据访问功能
 */
@Repository
@Transactional
public class LocationRepositoryImpl extends ServiceImpl<LocationMapper, LocationDO> implements LocationRepository {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LocationRepositoryImpl.class);

    /**
     * 创建SpringPage从MyBatis分页结果
     */
    private SpringPage<Location> createSpringPageFromMyBatis(List<Location> locations, int page, int size, long total) {
        return SpringPage.of(new PageImpl<>(
            locations,
            PageRequest.of(page, size),
            total
        ));
    }

    @Override
    public Location save(Location location) {
        log.debug("Saving location: {}", location.getName());

        LocationDO locationDO = LocationDO.from(location);
        saveOrUpdate(locationDO);

        return convertToDomain(getById(locationDO.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Location> findById(String id) {
        log.debug("Finding location by id: {}", id);

        LocationDO locationDO = getById(id);
        return Optional.ofNullable(locationDO).map(LocationDO::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> findAllById(Collection<String> ids) {
        log.debug("Finding locations by ids, count: {}", ids != null ? ids.size() : 0);

        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return lambdaQuery()
                .in(LocationDO::getId, ids)
                .eq(LocationDO::getIsDeleted, false)
                .orderByAsc(LocationDO::getLevel)
                .orderByAsc(LocationDO::getSortOrder)
                .orderByAsc(LocationDO::getName)
                .list()
                .stream()
                .map(LocationDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Location> findByName(String name) {
        log.debug("Finding location by name: {}", name);

        LocationDO locationDO = lambdaQuery()
            .eq(LocationDO::getName, name)
            .eq(LocationDO::getIsDeleted, false)
            .one();

        return Optional.ofNullable(locationDO).map(LocationDO::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Location> findByIsoCode(String isoCode) {
        log.debug("Finding location by iso code: {}", isoCode);

        LocationDO locationDO = lambdaQuery()
            .eq(LocationDO::getIsoCode, isoCode)
            .eq(LocationDO::getIsDeleted, false)
            .one();

        return Optional.ofNullable(locationDO).map(LocationDO::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> findByLocationType(LocationType locationType) {
        log.debug("Finding locations by type: {}", locationType);

        return lambdaQuery()
            .eq(LocationDO::getLocationType, locationType.name())
            .eq(LocationDO::getIsDeleted, false)
            .orderByAsc(LocationDO::getSortOrder)
            .orderByAsc(LocationDO::getName)
            .list()
            .stream()
            .map(LocationDO::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> findByParentId(String parentId) {
        log.debug("Finding locations by parent id: {}", parentId);

        return lambdaQuery()
            .eq(LocationDO::getParentId, parentId)
            .eq(LocationDO::getIsDeleted, false)
            .orderByAsc(LocationDO::getSortOrder)
            .orderByAsc(LocationDO::getName)
            .list()
            .stream()
            .map(LocationDO::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> findByParentIdAndLocationType(String parentId, LocationType locationType) {
        log.debug("Finding locations by parent id: {} and type: {}", parentId, locationType);

        return lambdaQuery()
            .eq(LocationDO::getParentId, parentId)
            .eq(LocationDO::getLocationType, locationType.name())
            .eq(LocationDO::getIsDeleted, false)
            .orderByAsc(LocationDO::getSortOrder)
            .orderByAsc(LocationDO::getName)
            .list()
            .stream()
            .map(LocationDO::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> findAllActive() {
        log.debug("Finding all active locations");

        return lambdaQuery()
            .eq(LocationDO::getIsActive, true)
            .eq(LocationDO::getIsDeleted, false)
            .orderByAsc(LocationDO::getLevel)
            .orderByAsc(LocationDO::getSortOrder)
            .orderByAsc(LocationDO::getName)
            .list()
            .stream()
            .map(LocationDO::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> findActiveByLocationType(LocationType locationType) {
        log.debug("Finding active locations by type: {}", locationType);

        return lambdaQuery()
            .eq(LocationDO::getLocationType, locationType.name())
            .eq(LocationDO::getIsActive, true)
            .eq(LocationDO::getIsDeleted, false)
            .orderByAsc(LocationDO::getSortOrder)
            .orderByAsc(LocationDO::getName)
            .list()
            .stream()
            .map(LocationDO::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Location> findAll() {
        log.debug("Finding all locations");

        return lambdaQuery()
            .eq(LocationDO::getIsDeleted, false)
            .orderByAsc(LocationDO::getLevel)
            .orderByAsc(LocationDO::getSortOrder)
            .orderByAsc(LocationDO::getName)
            .list()
            .stream()
            .map(LocationDO::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Pageable<Location> findAll(int page, int size) {
        log.debug("Finding all locations with page: {}, size: {}", page, size);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<LocationDO> pageRequest = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);

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

    @Override
    @Transactional(readOnly = true)
    public Pageable<Location> findByLocationType(LocationType locationType, int page, int size) {
        log.debug("Finding locations by type: {}, page: {}, size: {}", locationType, page, size);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<LocationDO> pageRequest = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);

        IPage<LocationDO> result = page(pageRequest,
            lambdaQuery()
                .eq(LocationDO::getLocationType, locationType.name())
                .eq(LocationDO::getIsDeleted, false)
                .orderByAsc(LocationDO::getSortOrder)
                .orderByAsc(LocationDO::getName));

        List<Location> locations = result.getRecords().stream()
            .map(this::convertToDomain)
            .collect(Collectors.toList());

        return createSpringPageFromMyBatis(locations, page, size, result.getTotal());
    }

    @Override
    @Transactional(readOnly = true)
    public Pageable<Location> findByParentId(String parentId, int page, int size) {
        log.debug("Finding locations by parent id: {}, page: {}, size: {}", parentId, page, size);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<LocationDO> pageRequest = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);

        IPage<LocationDO> result = page(pageRequest,
            lambdaQuery()
                .eq(LocationDO::getParentId, parentId)
                .eq(LocationDO::getIsDeleted, false)
                .orderByAsc(LocationDO::getSortOrder)
                .orderByAsc(LocationDO::getName));

        List<Location> locations = result.getRecords().stream()
            .map(this::convertToDomain)
            .collect(Collectors.toList());

        return createSpringPageFromMyBatis(locations, page, size, result.getTotal());
    }

    @Override
    @Transactional(readOnly = true)
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

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        log.debug("Checking if location exists by name: {}", name);

        return lambdaQuery()
            .eq(LocationDO::getName, name)
            .eq(LocationDO::getIsDeleted, false)
            .exists();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIsoCode(String isoCode) {
        log.debug("Checking if location exists by iso code: {}", isoCode);

        return lambdaQuery()
            .eq(LocationDO::getIsoCode, isoCode)
            .eq(LocationDO::getIsDeleted, false)
            .exists();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, String excludeId) {
        log.debug("Checking if location exists by name: {} excluding id: {}", name, excludeId);

        return lambdaQuery()
            .eq(LocationDO::getName, name)
            .ne(LocationDO::getId, excludeId)
            .eq(LocationDO::getIsDeleted, false)
            .exists();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIsoCodeAndIdNot(String isoCode, String excludeId) {
        log.debug("Checking if location exists by iso code: {} excluding id: {}", isoCode, excludeId);

        return lambdaQuery()
            .eq(LocationDO::getIsoCode, isoCode)
            .ne(LocationDO::getId, excludeId)
            .eq(LocationDO::getIsDeleted, false)
            .exists();
    }

    @Override
    public void delete(Location location) {
        log.debug("Deleting location: {}", location.getName());

        removeById(location.getId());
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting location by id: {}", id);

        removeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        log.debug("Counting all locations");

        return count(new LambdaQueryWrapper<LocationDO>()
            .eq(LocationDO::getIsDeleted, false));
    }

    @Override
    @Transactional(readOnly = true)
    public long countByLocationType(LocationType locationType) {
        log.debug("Counting locations by type: {}", locationType);

        return count(new LambdaQueryWrapper<LocationDO>()
            .eq(LocationDO::getLocationType, locationType.name())
            .eq(LocationDO::getIsDeleted, false));
    }

    @Override
    @Transactional(readOnly = true)
    public long countByParentId(String parentId) {
        log.debug("Counting locations by parent id: {}", parentId);

        return count(new LambdaQueryWrapper<LocationDO>()
            .eq(LocationDO::getParentId, parentId)
            .eq(LocationDO::getIsDeleted, false));
    }

    @Override
    @Transactional(readOnly = true)
    public long countActive() {
        log.debug("Counting active locations");

        return count(new LambdaQueryWrapper<LocationDO>()
            .eq(LocationDO::getIsActive, true)
            .eq(LocationDO::getIsDeleted, false));
    }

    @Override
    @Transactional(readOnly = true)
    public Pageable<Location> searchLocations(String nameKeyword, LocationType locationType, String parentId, Boolean activeOnly, int page, int size) {
        log.debug("Searching locations with criteria: name={}, type={}, parentId={}, activeOnly={}, page={}, size={}",
            nameKeyword, locationType, parentId, activeOnly, page, size);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<LocationDO> pageRequest = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);

        // 构建动态查询条件 - 使用 QueryWrapper 的条件方法
        LambdaQueryWrapper<LocationDO> queryWrapper = new LambdaQueryWrapper<LocationDO>()
                .eq(LocationDO::getIsDeleted, false)
                .like(StringUtils.isNotBlank(nameKeyword), LocationDO::getName, nameKeyword)
                .eq(locationType != null, LocationDO::getLocationType, locationType != null ? locationType.name() : null)
                .eq(StringUtils.isNotBlank(parentId), LocationDO::getParentId, parentId)
                .eq(activeOnly != null, LocationDO::getIsActive, activeOnly)
                .orderByAsc(LocationDO::getLevel)
                .orderByAsc(LocationDO::getSortOrder)
                .orderByAsc(LocationDO::getName);

        IPage<LocationDO> result = page(pageRequest, queryWrapper);

        List<Location> locations = result.getRecords().stream()
            .map(this::convertToDomain)
            .collect(Collectors.toList());

        return createSpringPageFromMyBatis(locations, page, size, result.getTotal());
    }

    /**
     * 将数据对象转换为领域对象
     *
     * @param locationDO 数据对象
     * @return 领域对象
     */
    private Location convertToDomain(LocationDO locationDO) {
        if (locationDO == null) {
            return null;
        }
        return locationDO.toDomain();
    }
}