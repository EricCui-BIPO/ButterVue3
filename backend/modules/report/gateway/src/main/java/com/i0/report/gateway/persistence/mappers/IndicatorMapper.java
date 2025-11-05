package com.i0.report.gateway.persistence.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.i0.report.gateway.persistence.dataobjects.IndicatorDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 指标数据访问接口
 */
@Mapper
public interface IndicatorMapper extends BaseMapper<IndicatorDO> {
}