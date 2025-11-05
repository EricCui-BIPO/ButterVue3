package com.i0.entity.gateway.persistence.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.i0.entity.gateway.persistence.dataobjects.EntityDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * Entity Mapper接口
 * 使用MyBatis-Plus进行数据访问
 * 遵循Clean Architecture规范，不使用@Select注解
 * 所有查询逻辑通过Repository层的LambdaQueryWrapper实现
 */
@Mapper
public interface EntityMapper extends BaseMapper<EntityDO> {
    // 继承BaseMapper提供的基础CRUD方法
    // 复杂查询通过Repository层的LambdaQueryWrapper实现
    // 不在Mapper层定义具体的SQL查询方法
}