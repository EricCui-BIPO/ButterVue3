package com.i0.service.gateway.persistence.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.i0.service.gateway.persistence.dataobjects.ServiceTypeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 服务类型数据访问映射器
 */
@Mapper
public interface ServiceTypeMapper extends BaseMapper<ServiceTypeDO> {
    // 继承BaseMapper，无需自定义SQL方法
}