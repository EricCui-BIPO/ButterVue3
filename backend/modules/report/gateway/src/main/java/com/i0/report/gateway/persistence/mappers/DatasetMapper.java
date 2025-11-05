package com.i0.report.gateway.persistence.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.i0.report.gateway.persistence.dataobjects.DatasetDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据集Mapper接口
 */
@Mapper
public interface DatasetMapper extends BaseMapper<DatasetDO> {
    // 继承BaseMapper<DatasetDO>，自动提供CRUD方法
    // 禁止在此接口中添加任何自定义SQL方法或注解
}