package com.i0.report.gateway.persistence.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.i0.report.gateway.persistence.dataobjects.ReportDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 报表数据访问接口
 */
@Mapper
public interface ReportMapper extends BaseMapper<ReportDO> {
}