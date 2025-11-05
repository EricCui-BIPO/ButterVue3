package com.i0.location.domain.repositories;

import com.i0.domain.core.pagination.Pageable;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.valueobjects.LocationType;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Location仓储接口
 * 定义Location实体的数据访问契约
 */
public interface LocationRepository {

    /**
     * 保存地理位置
     * @param location 要保存的地理位置
     * @return 保存后的地理位置（包含生成的ID）
     */
    Location save(Location location);

    /**
     * 根据ID查找地理位置
     * @param id 地理位置ID
     * @return 地理位置Optional包装
     */
    Optional<Location> findById(String id);

    /**
     * 根据ID集合批量查找地理位置
     * @param ids 地理位置ID集合
     * @return 地理位置列表
     */
    List<Location> findAllById(Collection<String> ids);

    /**
     * 根据名称查找地理位置
     * @param name 地理位置名称
     * @return 地理位置Optional包装
     */
    Optional<Location> findByName(String name);

    /**
     * 根据ISO代码查找地理位置
     * @param isoCode ISO代码
     * @return 地理位置Optional包装
     */
    Optional<Location> findByIsoCode(String isoCode);

    /**
     * 根据地理位置类型查找所有地理位置
     * @param locationType 地理位置类型
     * @return 地理位置列表
     */
    List<Location> findByLocationType(LocationType locationType);

    /**
     * 根据上级ID查找下级地理位置
     * @param parentId 上级地理位置ID
     * @return 下级地理位置列表
     */
    List<Location> findByParentId(String parentId);

    /**
     * 根据上级ID和地理位置类型查找下级地理位置
     * @param parentId 上级地理位置ID
     * @param locationType 地理位置类型
     * @return 下级地理位置列表
     */
    List<Location> findByParentIdAndLocationType(String parentId, LocationType locationType);

    /**
     * 查找所有激活的地理位置
     * @return 激活的地理位置列表
     */
    List<Location> findAllActive();

    /**
     * 根据地理位置类型查找所有激活的地理位置
     * @param locationType 地理位置类型
     * @return 激活的地理位置列表
     */
    List<Location> findActiveByLocationType(LocationType locationType);

    /**
     * 查找所有地理位置
     * @return 所有地理位置列表
     */
    List<Location> findAll();

    /**
     * 分页查询地理位置
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页结果
     */
    Pageable<Location> findAll(int page, int size);

    /**
     * 根据地理位置类型分页查询地理位置
     * @param locationType 地理位置类型
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页结果
     */
    Pageable<Location> findByLocationType(LocationType locationType, int page, int size);

    /**
     * 根据上级ID分页查询下级地理位置
     * @param parentId 上级地理位置ID
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页结果
     */
    Pageable<Location> findByParentId(String parentId, int page, int size);

    /**
     * 根据名称模糊查询地理位置
     * @param namePattern 名称模式
     * @return 匹配的地理位置列表
     */
    List<Location> findByNameContaining(String namePattern);

    /**
     * 检查地理位置名称是否已存在
     * @param name 地理位置名称
     * @return true如果名称已存在
     */
    boolean existsByName(String name);

    /**
     * 检查ISO代码是否已存在
     * @param isoCode ISO代码
     * @return true如果ISO代码已存在
     */
    boolean existsByIsoCode(String isoCode);

    /**
     * 检查地理位置名称是否已存在（排除指定ID）
     * @param name 地理位置名称
     * @param excludeId 要排除的地理位置ID
     * @return true如果名称已存在
     */
    boolean existsByNameAndIdNot(String name, String excludeId);

    /**
     * 检查ISO代码是否已存在（排除指定ID）
     * @param isoCode ISO代码
     * @param excludeId 要排除的地理位置ID
     * @return true如果ISO代码已存在
     */
    boolean existsByIsoCodeAndIdNot(String isoCode, String excludeId);

    /**
     * 删除地理位置
     * @param location 要删除的地理位置
     */
    void delete(Location location);

    /**
     * 根据ID删除地理位置
     * @param id 地理位置ID
     */
    void deleteById(String id);

    /**
     * 统计地理位置总数
     * @return 地理位置总数
     */
    long count();

    /**
     * 根据地理位置类型统计地理位置数量
     * @param locationType 地理位置类型
     * @return 地理位置数量
     */
    long countByLocationType(LocationType locationType);

    /**
     * 根据上级ID统计下级地理位置数量
     * @param parentId 上级地理位置ID
     * @return 下级地理位置数量
     */
    long countByParentId(String parentId);

    /**
     * 统计激活的地理位置数量
     * @return 激活的地理位置数量
     */
    long countActive();

    /**
     * 根据搜索条件分页查询地理位置
     * 这是一个综合查询方法，封装了复杂的查询逻辑
     * @param nameKeyword 名称关键字（可选）
     * @param locationType 地理位置类型（可选）
     * @param parentId 上级地理位置ID（可选）
     * @param activeOnly 是否只查询激活状态（可选，null表示不限制）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @return 分页结果
     */
    Pageable<Location> searchLocations(String nameKeyword, LocationType locationType, String parentId, Boolean activeOnly, int page, int size);
}