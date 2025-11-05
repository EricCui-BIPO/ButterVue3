package com.i0.report.gateway.persistence.dataobjects;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据集数据对象
 */
@Data
@TableName("datasets")
public class DatasetDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 数据集名称
     */
    @TableField("name")
    private String name;

    /**
     * 数据集描述
     */
    @TableField("description")
    private String description;

    /**
     * 原始 SQL 查询语句
     */
    @TableField("sql_query")
    private String sqlQuery;

    /**
     * 数据集过滤条件（JSON格式）
     */
    @TableField("filters")
    private String filters;

    /**
     * 数据源类型
     */
    @TableField("data_source_type")
    private String dataSourceType;

    /**
     * 更新策略
     */
    @TableField("update_strategy")
    private String updateStrategy;

    /**
     * 更新间隔（分钟）
     */
    @TableField("update_interval")
    private Integer updateInterval;

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