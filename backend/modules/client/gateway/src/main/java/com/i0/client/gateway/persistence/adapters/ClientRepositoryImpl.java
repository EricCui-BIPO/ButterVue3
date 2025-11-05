package com.i0.client.gateway.persistence.adapters;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.i0.client.domain.entities.Client;
import com.i0.client.domain.repositories.ClientRepository;
import com.i0.client.gateway.persistence.dataobjects.ClientDO;
import com.i0.client.gateway.persistence.mappers.ClientMapper;
import com.i0.domain.core.pagination.Pageable;
import com.i0.persistence.spring.pagination.SpringPage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 客户仓储实现
 * 继承ServiceImpl以利用MyBatis-Plus提供的通用方法
 * 实现ClientRepository接口，提供数据访问功能
 */
@Repository
@Transactional
public class ClientRepositoryImpl extends ServiceImpl<ClientMapper, ClientDO> implements ClientRepository {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClientRepositoryImpl.class);

    /**
     * 创建SpringPage从MyBatis分页结果
     */
    private SpringPage<Client> createSpringPageFromMyBatis(List<Client> clients, int page, int size, long total) {
        return SpringPage.of(new PageImpl<>(
            clients,
            PageRequest.of(page, size),
            total
        ));
    }

    @Override
    public Client save(Client client) {
        log.debug("Saving client: {}", client.getName());

        ClientDO clientDO = ClientDO.from(client);
        saveOrUpdate(clientDO);

        return convertToDomain(getById(clientDO.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> findById(String id) {
        log.debug("Finding client by id: {}", id);

        ClientDO clientDO = getById(id);
        return Optional.ofNullable(clientDO).map(ClientDO::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> findByName(String name) {
        log.debug("Finding client by name: {}", name);

        ClientDO clientDO = lambdaQuery()
            .eq(ClientDO::getName, name)
            .eq(ClientDO::getIsDeleted, false)
            .one();

        return Optional.ofNullable(clientDO).map(ClientDO::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Client> findByCode(String code) {
        log.debug("Finding client by code: {}", code);

        ClientDO clientDO = lambdaQuery()
            .eq(ClientDO::getCode, code)
            .eq(ClientDO::getIsDeleted, false)
            .one();

        return Optional.ofNullable(clientDO).map(ClientDO::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findByLocationId(String locationId) {
        log.debug("Finding clients by location: {}", locationId);

        return lambdaQuery()
            .eq(ClientDO::getLocationId, locationId)
            .eq(ClientDO::getIsDeleted, false)
            .orderByDesc(ClientDO::getCreatedAt)
            .list()
            .stream()
            .map(ClientDO::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAllActive() {
        log.debug("Finding all active clients");

        return lambdaQuery()
            .eq(ClientDO::getIsActive, true)
            .eq(ClientDO::getIsDeleted, false)
            .orderByDesc(ClientDO::getCreatedAt)
            .list()
            .stream()
            .map(ClientDO::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findActiveByLocationId(String locationId) {
        log.debug("Finding active clients by location: {}", locationId);

        return lambdaQuery()
            .eq(ClientDO::getLocationId, locationId)
            .eq(ClientDO::getIsActive, true)
            .eq(ClientDO::getIsDeleted, false)
            .orderByDesc(ClientDO::getCreatedAt)
            .list()
            .stream()
            .map(ClientDO::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findByNameContaining(String namePattern) {
        log.debug("Finding clients by name pattern: {}", namePattern);

        return lambdaQuery()
            .like(ClientDO::getName, namePattern)
            .eq(ClientDO::getIsDeleted, false)
            .orderByDesc(ClientDO::getCreatedAt)
            .list()
            .stream()
            .map(ClientDO::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findByCodeContaining(String codePattern) {
        log.debug("Finding clients by code pattern: {}", codePattern);

        return lambdaQuery()
            .like(ClientDO::getCode, codePattern)
            .eq(ClientDO::getIsDeleted, false)
            .orderByDesc(ClientDO::getCreatedAt)
            .list()
            .stream()
            .map(ClientDO::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findByAliasNameContaining(String aliasNamePattern) {
        log.debug("Finding clients by alias name pattern: {}", aliasNamePattern);

        return lambdaQuery()
            .like(ClientDO::getAliasName, aliasNamePattern)
            .eq(ClientDO::getIsDeleted, false)
            .orderByDesc(ClientDO::getCreatedAt)
            .list()
            .stream()
            .map(ClientDO::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAll() {
        log.debug("Finding all clients");

        return lambdaQuery()
            .eq(ClientDO::getIsDeleted, false)
            .orderByDesc(ClientDO::getCreatedAt)
            .list()
            .stream()
            .map(ClientDO::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Pageable<Client> findAll(int page, int size) {
        log.debug("Finding all clients with page: {}, size: {}", page, size);

        Page<ClientDO> pageRequest = new Page<>(page + 1, size);

        IPage<ClientDO> result = page(pageRequest,
            lambdaQuery()
                .eq(ClientDO::getIsDeleted, false)
                .orderByDesc(ClientDO::getCreatedAt));

        List<Client> clients = result.getRecords().stream()
            .map(this::convertToDomain)
            .collect(Collectors.toList());

        return createSpringPageFromMyBatis(clients, page, size, result.getTotal());
    }

    @Override
    @Transactional(readOnly = true)
    public Pageable<Client> findByLocationId(String locationId, int page, int size) {
        log.debug("Finding clients by location: {}, page: {}, size: {}", locationId, page, size);

        Page<ClientDO> pageRequest = new Page<>(page + 1, size);

        IPage<ClientDO> result = page(pageRequest,
            lambdaQuery()
                .eq(ClientDO::getLocationId, locationId)
                .eq(ClientDO::getIsDeleted, false)
                .orderByDesc(ClientDO::getCreatedAt));

        List<Client> clients = result.getRecords().stream()
            .map(this::convertToDomain)
            .collect(Collectors.toList());

        return createSpringPageFromMyBatis(clients, page, size, result.getTotal());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        log.debug("Checking if client exists by name: {}", name);

        return lambdaQuery()
            .eq(ClientDO::getName, name)
            .eq(ClientDO::getIsDeleted, false)
            .exists();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        log.debug("Checking if client exists by code: {}", code);

        return lambdaQuery()
            .eq(ClientDO::getCode, code)
            .eq(ClientDO::getIsDeleted, false)
            .exists();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndIdNot(String name, String excludeId) {
        log.debug("Checking if client exists by name: {} excluding id: {}", name, excludeId);

        return lambdaQuery()
            .eq(ClientDO::getName, name)
            .ne(ClientDO::getId, excludeId)
            .eq(ClientDO::getIsDeleted, false)
            .exists();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCodeAndIdNot(String code, String excludeId) {
        log.debug("Checking if client exists by code: {} excluding id: {}", code, excludeId);

        return lambdaQuery()
            .eq(ClientDO::getCode, code)
            .ne(ClientDO::getId, excludeId)
            .eq(ClientDO::getIsDeleted, false)
            .exists();
    }

    @Override
    public void delete(Client client) {
        log.debug("Deleting client: {}", client.getName());

        removeById(client.getId());
    }

    @Override
    public void deleteById(String id) {
        log.debug("Deleting client by id: {}", id);

        removeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        log.debug("Counting all clients");

        return count(new LambdaQueryWrapper<ClientDO>()
            .eq(ClientDO::getIsDeleted, false));
    }

    @Override
    @Transactional(readOnly = true)
    public long countByLocationId(String locationId) {
        log.debug("Counting clients by location: {}", locationId);

        return count(new LambdaQueryWrapper<ClientDO>()
            .eq(ClientDO::getLocationId, locationId)
            .eq(ClientDO::getIsDeleted, false));
    }

    @Override
    @Transactional(readOnly = true)
    public long countActive() {
        log.debug("Counting active clients");

        return count(new LambdaQueryWrapper<ClientDO>()
            .eq(ClientDO::getIsActive, true)
            .eq(ClientDO::getIsDeleted, false));
    }

    @Override
    @Transactional(readOnly = true)
    public Pageable<Client> searchClients(String keyword, String locationId, Boolean activeOnly,
                                          int page, int size, String sortBy, String sortDirection) {
        log.debug("Searching clients with criteria: keyword={}, locationId={}, activeOnly={}, page={}, size={}",
            keyword, locationId, activeOnly, page, size);

        Page<ClientDO> pageRequest = new Page<>(page + 1, size);

        // 构建动态查询条件 - 使用 QueryWrapper 的条件方法
        LambdaQueryWrapper<ClientDO> queryWrapper = new LambdaQueryWrapper<ClientDO>()
                .eq(ClientDO::getIsDeleted, false)
                .and(StringUtils.isNotBlank(keyword), wrapper -> wrapper
                    .like(ClientDO::getName, keyword)
                    .or()
                    .like(ClientDO::getCode, keyword)
                    .or()
                    .like(ClientDO::getAliasName, keyword)
                )
                .eq(StringUtils.isNotBlank(locationId), ClientDO::getLocationId, locationId)
                .eq(activeOnly != null, ClientDO::getIsActive, activeOnly)
                .orderByDesc(ClientDO::getCreatedAt);

        IPage<ClientDO> result = page(pageRequest, queryWrapper);

        List<Client> clients = result.getRecords().stream()
            .map(this::convertToDomain)
            .collect(Collectors.toList());

        return createSpringPageFromMyBatis(clients, page, size, result.getTotal());
    }

    @Override
    @Transactional(readOnly = true)
    public Pageable<Client> searchClientsByFields(String name, String code, String aliasName,
                                                  String locationId, Boolean activeOnly,
                                                  int page, int size, String sortBy, String sortDirection) {
        log.debug("Searching clients by fields: name={}, code={}, aliasName={}, locationId={}, activeOnly={}, page={}, size={}",
            name, code, aliasName, locationId, activeOnly, page, size);

        Page<ClientDO> pageRequest = new Page<>(page + 1, size);

        // 构建动态查询条件 - 使用 QueryWrapper 的条件方法
        LambdaQueryWrapper<ClientDO> queryWrapper = new LambdaQueryWrapper<ClientDO>()
                .eq(ClientDO::getIsDeleted, false);

        // 处理字段搜索条件 - 使用 OR 逻辑
        boolean hasName = StringUtils.isNotBlank(name);
        boolean hasCode = StringUtils.isNotBlank(code);
        boolean hasAlias = StringUtils.isNotBlank(aliasName);

        if (hasName || hasCode || hasAlias) {
            queryWrapper.and(wrapper -> {
                if (hasName) {
                    wrapper.like(ClientDO::getName, name);
                }
                if (hasCode) {
                    if (hasName) wrapper.or();
                    wrapper.like(ClientDO::getCode, code);
                }
                if (hasAlias) {
                    if (hasName || hasCode) wrapper.or();
                    wrapper.like(ClientDO::getAliasName, aliasName);
                }
            });
        }

        queryWrapper.eq(StringUtils.isNotBlank(locationId), ClientDO::getLocationId, locationId)
                .eq(activeOnly != null, ClientDO::getIsActive, activeOnly)
                .orderByDesc(ClientDO::getCreatedAt);

        IPage<ClientDO> result = page(pageRequest, queryWrapper);

        List<Client> clients = result.getRecords().stream()
            .map(this::convertToDomain)
            .collect(Collectors.toList());

        return createSpringPageFromMyBatis(clients, page, size, result.getTotal());
    }

    /**
     * 将数据对象转换为领域对象
     *
     * @param clientDO 数据对象
     * @return 领域对象
     */
    private Client convertToDomain(ClientDO clientDO) {
        if (clientDO == null) {
            return null;
        }
        return clientDO.toDomain();
    }
}