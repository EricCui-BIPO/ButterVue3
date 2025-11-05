package com.i0.client.gateway.persistence.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.i0.client.gateway.persistence.dataobjects.ClientDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户数据访问映射器
 * 继承BaseMapper以利用MyBatis-Plus提供的通用方法
 * 所有查询都通过LambdaQueryWrapper实现，无需自定义SQL方法
 */
@Mapper
public interface ClientMapper extends BaseMapper<ClientDO> {
    // 所有查询方法都通过BaseMapper和LambdaQueryWrapper实现
    // 这里无需定义额外的自定义方法
}