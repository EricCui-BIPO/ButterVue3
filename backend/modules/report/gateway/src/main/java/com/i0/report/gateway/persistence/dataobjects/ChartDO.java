package com.i0.report.gateway.persistence.dataobjects;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图表数据对象
 */
@Data
@TableName("charts")
public class ChartDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 图表名称
     */
    @TableField("name")
    private String name;

    /**
     * 图表描述
     */
    @TableField("description")
    private String description;

    /**
     * 图表类型
     */
    @TableField("type")
    private String type;

    /**
     * 绑定的指标ID
     */
    @TableField("indicator_id")
    private String indicatorId;

    /**
     * 主维度字段
     */
    @TableField("dimension")
    private String dimension;

    /**
     * 图表过滤条件（JSON格式）
     */
    @TableField("filters")
    private String filters;

    /**
     * 图表标题
     */
    @TableField("title")
    private String title;

    /**
     * X轴标签
     */
    @TableField("x_axis_label")
    private String xAxisLabel;

    /**
     * Y轴标签
     */
    @TableField("y_axis_label")
    private String yAxisLabel;

    /**
     * 图表宽度
     */
    @TableField("width")
    private Integer width;

    /**
     * 图表高度
     */
    @TableField("height")
    private Integer height;

    /**
     * 图表配置（JSON格式）
     */
    @TableField("config")
    private String config;

    /**
     * 是否启用
     */
    @TableField("enabled")
    private Boolean enabled;

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
     * 创建者ID
     */
    @TableField("creator_id")
    private String creatorId;

    /**
     * 创建者姓名
     */
    @TableField("creator")
    private String creator;

    /**
     * 更新者ID
     */
    @TableField("updater_id")
    private String updaterId;

    /**
     * 更新者姓名
     */
    @TableField("updater")
    private String updater;

    /**
     * 逻辑删除标记
     */
    @TableLogic(value = "false", delval = "true")
    @TableField("is_deleted")
    private Boolean isDeleted;
}