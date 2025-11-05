package com.i0.location.gateway.persistence.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.i0.location.gateway.persistence.dataobjects.LocationDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * Location数据访问接口
 * 继承MyBatis-Plus的BaseMapper，提供基础CRUD操作
 */
@Mapper
public interface LocationMapper extends BaseMapper<LocationDO> {
    // 继承BaseMapper的所有基础方法，不需要添加自定义方法
}