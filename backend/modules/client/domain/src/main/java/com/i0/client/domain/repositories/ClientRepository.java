package com.i0.client.domain.repositories;

import com.i0.domain.core.pagination.Pageable;
import com.i0.client.domain.entities.Client;

import java.util.List;
import java.util.Optional;

/**
 * Client仓储接口
 * 定义Client实体的数据访问契约
 */
public interface ClientRepository {
    
    /**
     * 保存客户
     * @param client 要保存的客户
     * @return 保存后的客户（包含生成的ID）
     */
    Client save(Client client);
    
    /**
     * 根据ID查找客户
     * @param id 客户ID
     * @return 客户Optional包装
     */
    Optional<Client> findById(String id);
    
    /**
     * 根据名称查找客户
     * @param name 客户名称
     * @return 客户Optional包装
     */
    Optional<Client> findByName(String name);
    
    /**
     * 根据代码查找客户
     * @param code 客户代码
     * @return 客户Optional包装
     */
    Optional<Client> findByCode(String code);
    
    /**
     * 根据位置ID查找所有客户
     * @param locationId 位置ID
     * @return 客户列表
     */
    List<Client> findByLocationId(String locationId);
    
    /**
     * 查找所有激活的客户
     * @return 激活的客户列表
     */
    List<Client> findAllActive();
    
    /**
     * 根据位置ID查找所有激活的客户
     * @param locationId 位置ID
     * @return 激活的客户列表
     */
    List<Client> findActiveByLocationId(String locationId);
    
    /**
     * 查找所有客户
     * @return 所有客户列表
     */
    List<Client> findAll();
    
    /**
     * 分页查询客户
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页结果
     */
    Pageable<Client> findAll(int page, int size);

    /**
     * 根据位置ID分页查询客户
     * @param locationId 位置ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页结果
     */
    Pageable<Client> findByLocationId(String locationId, int page, int size);
    
    /**
     * 根据名称模糊查询客户
     * @param namePattern 名称模式
     * @return 匹配的客户列表
     */
    List<Client> findByNameContaining(String namePattern);
    
    /**
     * 根据代码模糊查询客户
     * @param codePattern 代码模式
     * @return 匹配的客户列表
     */
    List<Client> findByCodeContaining(String codePattern);
    
    /**
     * 根据别名模糊查询客户
     * @param aliasNamePattern 别名模式
     * @return 匹配的客户列表
     */
    List<Client> findByAliasNameContaining(String aliasNamePattern);
    
    /**
     * 检查客户名称是否已存在
     * @param name 客户名称
     * @return true如果名称已存在
     */
    boolean existsByName(String name);
    
    /**
     * 检查客户代码是否已存在
     * @param code 客户代码
     * @return true如果代码已存在
     */
    boolean existsByCode(String code);
    
    /**
     * 检查客户名称是否已存在（排除指定ID）
     * @param name 客户名称
     * @param excludeId 要排除的客户ID
     * @return true如果名称已存在
     */
    boolean existsByNameAndIdNot(String name, String excludeId);
    
    /**
     * 检查客户代码是否已存在（排除指定ID）
     * @param code 客户代码
     * @param excludeId 要排除的客户ID
     * @return true如果代码已存在
     */
    boolean existsByCodeAndIdNot(String code, String excludeId);
    
    /**
     * 删除客户
     * @param client 要删除的客户
     */
    void delete(Client client);
    
    /**
     * 根据ID删除客户
     * @param id 客户ID
     */
    void deleteById(String id);
    
    /**
     * 统计客户总数
     * @return 客户总数
     */
    long count();
    
    /**
     * 根据位置ID统计客户数量
     * @param locationId 位置ID
     * @return 客户数量
     */
    long countByLocationId(String locationId);
    
    /**
     * 统计激活的客户数量
     * @return 激活的客户数量
     */
    long countActive();
    
    /**
     * 根据搜索条件分页查询客户
     * 这是一个综合查询方法，封装了复杂的查询逻辑
     * @param keyword 关键字（可选，用于搜索名称、代码、别名）
     * @param locationId 位置ID（可选）
     * @param activeOnly 是否只查询激活状态（可选，null表示不限制）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sortBy 排序字段（可选，如：name, code, createdAt等）
     * @param sortDirection 排序方向（可选，ASC或DESC）
     * @return 分页结果
     */
    Pageable<Client> searchClients(String keyword, String locationId, Boolean activeOnly,
                                   int page, int size, String sortBy, String sortDirection);
    
    /**
     * 根据多个字段进行复合搜索
     * @param name 名称关键字（可选）
     * @param code 代码关键字（可选）
     * @param aliasName 别名关键字（可选）
     * @param locationId 位置ID（可选）
     * @param activeOnly 是否只查询激活状态（可选）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sortBy 排序字段（可选）
     * @param sortDirection 排序方向（可选）
     * @return 分页结果
     */
    Pageable<Client> searchClientsByFields(String name, String code, String aliasName, String locationId,
                                           Boolean activeOnly, int page, int size, String sortBy, String sortDirection);
}