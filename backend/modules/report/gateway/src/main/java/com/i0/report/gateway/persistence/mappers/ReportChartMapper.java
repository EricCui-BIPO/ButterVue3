package com.i0.report.gateway.persistence.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.i0.report.gateway.persistence.dataobjects.ReportChartDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报表图表关联数据访问接口
 */
@Mapper
public interface ReportChartMapper extends BaseMapper<ReportChartDO> {
}