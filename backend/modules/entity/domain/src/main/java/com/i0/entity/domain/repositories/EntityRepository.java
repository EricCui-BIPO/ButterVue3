package com.i0.entity.domain.repositories;

import com.i0.domain.core.pagination.Pageable;
import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.valueobjects.EntityType;

import java.util.List;
import java.util.Optional;

/**
 * Entity仓储接口
 * 定义Entity实体的数据访问契约
 */
public interface EntityRepository {
    
    /**
     * 保存实体
     * @param entity 要保存的实体
     * @return 保存后的实体（包含生成的ID）
     */
    Entity save(Entity entity);
    
    /**
     * 根据ID查找实体
     * @param id 实体ID
     * @return 实体Optional包装
     */
    Optional<Entity> findById(String id);
    
    /**
     * 根据名称查找实体
     * @param name 实体名称
     * @return 实体Optional包装
     */
    Optional<Entity> findByName(String name);
    
    /**
     * 根据代码查找实体
     * @param code 实体代码
     * @return 实体Optional包装
     */
    Optional<Entity> findByCode(String code);
    
    /**
     * 根据实体类型查找所有实体
     * @param entityType 实体类型
     * @return 实体列表
     */
    List<Entity> findByEntityType(EntityType entityType);
    
    /**
     * 查找所有激活的实体
     * @return 激活的实体列表
     */
    List<Entity> findAllActive();
    
    /**
     * 根据实体类型查找所有激活的实体
     * @param entityType 实体类型
     * @return 激活的实体列表
     */
    List<Entity> findActiveByEntityType(EntityType entityType);
    
    /**
     * 查找所有实体
     * @return 所有实体列表
     */
    List<Entity> findAllEntities();

    /**
     * 查找所有实体
     * @return 所有实体列表
     */
    List<Entity> findAll();
    
    /**
     * 分页查询实体
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页结果
     */
    Pageable<Entity> findAll(int page, int size);

    /**
     * 根据实体类型分页查询实体
     * @param entityType 实体类型
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页结果
     */
    Pageable<Entity> findByEntityType(EntityType entityType, int page, int size);
    
    /**
     * 根据名称模糊查询实体
     * @param namePattern 名称模式
     * @return 匹配的实体列表
     */
    List<Entity> findByNameContaining(String namePattern);
    
    /**
     * 检查实体名称是否已存在
     * @param name 实体名称
     * @return true如果名称已存在
     */
    boolean existsByName(String name);
    
    /**
     * 检查实体代码是否已存在
     * @param code 实体代码
     * @return true如果代码已存在
     */
    boolean existsByCode(String code);
    
    /**
     * 检查实体名称是否已存在（排除指定ID）
     * @param name 实体名称
     * @param excludeId 要排除的实体ID
     * @return true如果名称已存在
     */
    boolean existsByNameAndIdNot(String name, String excludeId);
    
    /**
     * 检查实体代码是否已存在（排除指定ID）
     * @param code 实体代码
     * @param excludeId 要排除的实体ID
     * @return true如果代码已存在
     */
    boolean existsByCodeAndIdNot(String code, String excludeId);
    
    /**
     * 删除实体
     * @param entity 要删除的实体
     */
    void delete(Entity entity);
    
    /**
     * 根据ID删除实体
     * @param id 实体ID
     */
    void deleteById(String id);
    
    /**
     * 统计实体总数
     * @return 实体总数
     */
    long count();
    
    /**
     * 根据实体类型统计实体数量
     * @param entityType 实体类型
     * @return 实体数量
     */
    long countByEntityType(EntityType entityType);
    
    /**
     * 统计激活的实体数量
     * @return 激活的实体数量
     */
    long countActive();
    
    /**
     * 根据搜索条件分页查询实体
     * 这是一个综合查询方法，封装了复杂的查询逻辑
     * @param nameKeyword 名称关键字（可选）
     * @param entityType 实体类型（可选）
     * @param activeOnly 是否只查询激活状态（可选，null表示不限制）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页结果
     */
    Pageable<Entity> searchEntities(String nameKeyword, EntityType entityType, Boolean activeOnly, int page, int size);
}