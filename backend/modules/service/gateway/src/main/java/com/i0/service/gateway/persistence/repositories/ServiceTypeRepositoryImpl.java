package com.i0.service.gateway.persistence.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.i0.domain.core.pagination.Pageable;
import com.i0.persistence.spring.pagination.SpringPage;
import com.i0.service.domain.entities.ServiceTypeEntity;
import com.i0.service.domain.repositories.ServiceTypeRepository;
import com.i0.service.domain.valueobjects.ServiceType;
import com.i0.service.gateway.persistence.dataobjects.ServiceTypeDO;
import com.i0.service.gateway.persistence.mappers.ServiceTypeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ServiceTypeRepositoryImpl implements ServiceTypeRepository {

    private final ServiceTypeMapper serviceTypeMapper;

    /**
     * 创建SpringPage从MyBatis分页结果
     */
    private Pageable<ServiceTypeEntity> createCustomPageFromMyBatis(List<ServiceTypeEntity> entities, int page, int size, long total) {
        return SpringPage.of(new PageImpl<>(
                entities,
                PageRequest.of(page, size),
                total
        ));
    }

    /**
     * 将数据对象转换为领域对象
     * @param serviceTypeDO 数据对象
     * @return 领域对象
     */
    private ServiceTypeEntity convertToDomain(ServiceTypeDO serviceTypeDO) {
        if (serviceTypeDO == null) {
            return null;
        }
        return serviceTypeDO.toDomain();
    }

    @Override
    public ServiceTypeEntity save(ServiceTypeEntity serviceTypeEntity) {
        ServiceTypeDO serviceTypeDO = ServiceTypeDO.from(serviceTypeEntity);

        // 确保ID已设置
        if (serviceTypeDO.getId() == null || serviceTypeDO.getId().trim().isEmpty()) {
            throw new RuntimeException("Service type ID must be set before saving");
        }

        // 检查数据库中是否已存在该ID的记录来判断是插入还是更新
        ServiceTypeDO existingDO = serviceTypeMapper.selectById(serviceTypeDO.getId());
        
        if (existingDO == null) {
            // 新增 - 数据库中不存在该ID的记录
            log.debug("Inserting new service type with ID: {}", serviceTypeDO.getId());
            int result = serviceTypeMapper.insert(serviceTypeDO);
            if (result == 0) {
                throw new RuntimeException("Failed to save service type");
            }
            log.info("Successfully inserted service type with ID: {}", serviceTypeDO.getId());
        } else {
            // 更新 - 数据库中已存在该ID的记录
            existingDO.updateFrom(serviceTypeEntity);
            int result = serviceTypeMapper.updateById(existingDO);
            if (result == 0) {
                throw new RuntimeException("Failed to update service type");
            }
            serviceTypeDO = existingDO;
            log.info("Successfully updated service type with ID: {}", serviceTypeDO.getId());
        }

        return serviceTypeDO.toDomain();
    }

    @Override
    public Optional<ServiceTypeEntity> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }

        ServiceTypeDO serviceTypeDO = serviceTypeMapper.selectById(id);
        return Optional.ofNullable(serviceTypeDO).map(ServiceTypeDO::toDomain);
    }

    @Override
    public Optional<ServiceTypeEntity> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }

        LambdaQueryWrapper<ServiceTypeDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceTypeDO::getName, name.trim())
                   .eq(ServiceTypeDO::getIsDeleted, false);

        ServiceTypeDO serviceTypeDO = serviceTypeMapper.selectOne(queryWrapper);
        return Optional.ofNullable(serviceTypeDO).map(ServiceTypeDO::toDomain);
    }

    @Override
    public Optional<ServiceTypeEntity> findByServiceType(ServiceType serviceType) {
        if (serviceType == null) {
            return Optional.empty();
        }

        LambdaQueryWrapper<ServiceTypeDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceTypeDO::getCode, serviceType.getCode())
                   .eq(ServiceTypeDO::getIsDeleted, false);

        ServiceTypeDO serviceTypeDO = serviceTypeMapper.selectOne(queryWrapper);
        return Optional.ofNullable(serviceTypeDO).map(ServiceTypeDO::toDomain);
    }

    @Override
    public List<ServiceTypeEntity> findAllActive() {
        LambdaQueryWrapper<ServiceTypeDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceTypeDO::getIsActive, true)
                   .eq(ServiceTypeDO::getIsDeleted, false)
                   .orderByDesc(ServiceTypeDO::getCreatedAt);

        List<ServiceTypeDO> serviceTypeDOs = serviceTypeMapper.selectList(queryWrapper);
        return serviceTypeDOs.stream()
                .map(ServiceTypeDO::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Pageable<ServiceTypeEntity> findAll(int page, int size) {
        return searchServiceTypes(null, null, null, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public Pageable<ServiceTypeEntity> searchServiceTypes(String nameKeyword, ServiceType serviceType, Boolean activeOnly, int page, int size) {
        log.debug("Searching service types with criteria: name={}, serviceType={}, activeOnly={}, page={}, size={}",
                nameKeyword, serviceType, activeOnly, page, size);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ServiceTypeDO> pageRequest = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page + 1, size);
        LambdaQueryWrapper<ServiceTypeDO> queryWrapper = new LambdaQueryWrapper<>();

        // 基础条件：未删除
        queryWrapper.eq(ServiceTypeDO::getIsDeleted, false);

        // 名称关键字
        if (StringUtils.isNotBlank(nameKeyword)) {
            queryWrapper.like(ServiceTypeDO::getName, nameKeyword.trim());
        }

        // 服务类型
        if (serviceType != null) {
            queryWrapper.eq(ServiceTypeDO::getCode, serviceType.getCode());
        }

        // 激活状态
        if (activeOnly != null) {
            queryWrapper.eq(ServiceTypeDO::getIsActive, activeOnly);
        }

        // 排序
        queryWrapper.orderByDesc(ServiceTypeDO::getCreatedAt);

        IPage<ServiceTypeDO> result = serviceTypeMapper.selectPage(pageRequest, queryWrapper);
        List<ServiceTypeEntity> entities = result.getRecords().stream()
                .map(this::convertToDomain)
                .collect(Collectors.toList());

        return createCustomPageFromMyBatis(entities, page, size, result.getTotal());
    }

    @Override
    public boolean existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        LambdaQueryWrapper<ServiceTypeDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceTypeDO::getName, name.trim())
                   .eq(ServiceTypeDO::getIsDeleted, false);

        return serviceTypeMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean existsByServiceType(ServiceType serviceType) {
        if (serviceType == null) {
            return false;
        }

        LambdaQueryWrapper<ServiceTypeDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceTypeDO::getCode, serviceType.getCode())
                   .eq(ServiceTypeDO::getIsDeleted, false);

        return serviceTypeMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean existsByNameExcludingId(String name, String excludeId) {
        if (name == null || name.trim().isEmpty() || excludeId == null || excludeId.trim().isEmpty()) {
            return false;
        }

        LambdaQueryWrapper<ServiceTypeDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceTypeDO::getName, name.trim())
                   .ne(ServiceTypeDO::getId, excludeId.trim())
                   .eq(ServiceTypeDO::getIsDeleted, false);

        return serviceTypeMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public boolean existsByServiceTypeExcludingId(ServiceType serviceType, String excludeId) {
        if (serviceType == null || excludeId == null || excludeId.trim().isEmpty()) {
            return false;
        }

        LambdaQueryWrapper<ServiceTypeDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceTypeDO::getCode, serviceType.getCode())
                   .ne(ServiceTypeDO::getId, excludeId.trim())
                   .eq(ServiceTypeDO::getIsDeleted, false);

        return serviceTypeMapper.selectCount(queryWrapper) > 0;
    }

    @Override
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

    @Override
    public long count() {
        LambdaQueryWrapper<ServiceTypeDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceTypeDO::getIsDeleted, false);
        return serviceTypeMapper.selectCount(queryWrapper);
    }

    @Override
    public long countActive() {
        LambdaQueryWrapper<ServiceTypeDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceTypeDO::getIsActive, true)
                   .eq(ServiceTypeDO::getIsDeleted, false);
        return serviceTypeMapper.selectCount(queryWrapper);
    }

    }