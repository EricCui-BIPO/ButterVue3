package com.i0.talent.gateway.persistence.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.i0.talent.gateway.persistence.dataobjects.EmployeeDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工数据访问接口
 *
 * 继承 MyBatis-Plus 的 BaseMapper，提供基础 CRUD 操作
 * 遵循 Gateway 层规范，不包含自定义 SQL 方法
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<EmployeeDO> {
}