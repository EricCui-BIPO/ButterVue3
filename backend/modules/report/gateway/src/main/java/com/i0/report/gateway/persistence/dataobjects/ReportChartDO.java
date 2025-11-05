package com.i0.report.gateway.persistence.dataobjects;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报表图表关联数据对象
 */
@Data
@TableName("report_charts")
public class ReportChartDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 报表ID
     */
    @TableField("report_id")
    private String reportId;

    /**
     * 图表ID
     */
    @TableField("chart_id")
    private String chartId;

    /**
     * 图表X坐标位置
     */
    @TableField("position_x")
    private Integer positionX;

    /**
     * 图表Y坐标位置
     */
    @TableField("position_y")
    private Integer positionY;

    /**
     * 图表宽度（栅格单位）
     */
    @TableField("width")
    private Integer width;

    /**
     * 图表高度（栅格单位）
     */
    @TableField("height")
    private Integer height;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除标记
     */
    @TableLogic(value = "false", delval = "true")
    @TableField("is_deleted")
    private Boolean isDeleted;
}