package com.i0.client.domain.services;

import com.i0.client.domain.valueobjects.LocationInfo;

import java.util.List;

/**
 * 位置信息查询服务接口
 * 定义了客户端域需要的位置信息访问能力
 * 遵循依赖倒置原则，通过接口抽象外部域的访问
 */
public interface LocationQueryService {

    /**
     * 批量获取位置信息
     * @param locationIds 位置ID集合
     * @return 位置信息列表
     */
    List<LocationInfo> getLocations(Iterable<String> locationIds);

    /**
     * 获取单个位置信息
     * @param locationId 位置ID
     * @return 位置信息，如果不存在则返回null
     */
    LocationInfo getLocation(String locationId);

    /**
     * 获取所有激活的国家位置
     * @return 国家位置列表
     */
    List<LocationInfo> getCountryLocations();
}