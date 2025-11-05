package com.i0.report.gateway.persistence.dataobjects;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 指标数据对象
 */
@Data
@TableName("indicators")
public class IndicatorDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 指标名称
     */
    @TableField("name")
    private String name;

    /**
     * 指标描述
     */
    @TableField("description")
    private String description;

    /**
     * 引用的数据集ID
     */
    @TableField("dataset_id")
    private String datasetId;

    /**
     * 计算表达式
     */
    @TableField("calculation")
    private String calculation;

    /**
     * 维度字段列表（JSON格式）
     */
    @TableField("dimensions")
    private String dimensions;

    /**
     * 指标过滤条件（JSON格式）
     */
    @TableField("filters")
    private String filters;

    /**
     * 指标类型
     */
    @TableField("type")
    private String type;

    /**
     * 数据单位
     */
    @TableField("unit")
    private String unit;

    /**
     * 数据格式化模式
     */
    @TableField("format_pattern")
    private String formatPattern;

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