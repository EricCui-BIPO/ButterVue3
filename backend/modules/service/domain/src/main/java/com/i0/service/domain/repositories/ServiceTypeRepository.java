package com.i0.service.domain.repositories;

import com.i0.service.domain.entities.ServiceTypeEntity;
import com.i0.service.domain.valueobjects.ServiceType;
import com.i0.domain.core.pagination.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 服务类型仓储接口
 * 定义服务类型数据访问的契约
 */
public interface ServiceTypeRepository {

    /**
     * 保存服务类型实体
     * @param serviceTypeEntity 服务类型实体
     * @return 保存后的服务类型实体
     */
    ServiceTypeEntity save(ServiceTypeEntity serviceTypeEntity);

    /**
     * 根据ID查找服务类型
     * @param id 服务类型ID
     * @return 服务类型实体，如果不存在返回Optional.empty()
     */
    Optional<ServiceTypeEntity> findById(String id);

    /**
     * 根据名称查找服务类型
     * @param name 服务类型名称
     * @return 服务类型实体，如果不存在返回Optional.empty()
     */
    Optional<ServiceTypeEntity> findByName(String name);

    /**
     * 根据服务类型代码查找服务类型
     * @param serviceType 服务类型代码
     * @return 服务类型实体，如果不存在返回Optional.empty()
     */
    Optional<ServiceTypeEntity> findByServiceType(ServiceType serviceType);

    /**
     * 查找所有活跃的服务类型
     * @return 活跃的服务类型列表
     */
    List<ServiceTypeEntity> findAllActive();

    /**
     * 查找所有服务类型（分页）
     * @param page 页码（从0开始）
     * @param size 页面大小
     * @return 分页的服务类型列表
     */
    Pageable<ServiceTypeEntity> findAll(int page, int size);

    /**
     * 搜索服务类型
     * @param nameKeyword 名称关键词（可选）
     * @param serviceType 服务类型过滤（可选）
     * @param activeOnly 是否只返回活跃的（可选）
     * @param page 页码（从0开始）
     * @param size 页面大小
     * @return 分页的搜索结果
     */
    Pageable<ServiceTypeEntity> searchServiceTypes(String nameKeyword, ServiceType serviceType, Boolean activeOnly, int page, int size);

    /**
     * 检查名称是否已存在
     * @param name 服务类型名称
     * @return 如果名称已存在返回true
     */
    boolean existsByName(String name);

    /**
     * 检查服务类型代码是否已存在
     * @param serviceType 服务类型代码
     * @return 如果代码已存在返回true
     */
    boolean existsByServiceType(ServiceType serviceType);

    /**
     * 检查名称是否已存在（排除指定ID）
     * @param name 服务类型名称
     * @param excludeId 要排除的ID
     * @return 如果名称已存在（排除指定ID）返回true
     */
    boolean existsByNameExcludingId(String name, String excludeId);

    /**
     * 检查服务类型代码是否已存在（排除指定ID）
     * @param serviceType 服务类型代码
     * @param excludeId 要排除的ID
     * @return 如果代码已存在（排除指定ID）返回true
     */
    boolean existsByServiceTypeExcludingId(ServiceType serviceType, String excludeId);

    /**
     * 删除服务类型（逻辑删除）
     * @param serviceTypeEntity 要删除的服务类型实体
     */
    void delete(ServiceTypeEntity serviceTypeEntity);

    /**
     * 统计服务类型总数
     * @return 服务类型总数
     */
    long count();

    /**
     * 统计活跃服务类型数量
     * @return 活跃服务类型数量
     */
    long countActive();
}