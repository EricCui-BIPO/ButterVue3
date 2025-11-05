package com.i0.report.gateway.persistence.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.i0.report.gateway.persistence.dataobjects.ChartDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图表数据访问接口
 */
@Mapper
public interface ChartMapper extends BaseMapper<ChartDO> {
}