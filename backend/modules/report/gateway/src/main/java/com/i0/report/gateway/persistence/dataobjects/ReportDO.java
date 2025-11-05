package com.i0.report.gateway.persistence.dataobjects;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 报表数据对象
 */
@Data
@TableName("reports")
public class ReportDO {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 报表名称
     */
    @TableField("name")
    private String name;

    /**
     * 报表描述
     */
    @TableField("description")
    private String description;

    /**
     * 报表包含的图表列表（JSON格式）
     */
    @TableField("charts")
    private String charts;

    /**
     * 报表全局过滤条件（JSON格式）
     */
    @TableField("filters")
    private String filters;

    /**
     * 报表布局配置（JSON格式）
     */
    @TableField("layout")
    private String layout;

    /**
     * 刷新间隔（分钟）
     */
    @TableField("refresh_interval")
    private Integer refreshInterval;

    /**
     * 报表状态
     */
    @TableField("status")
    private String status;

    /**
     * 报表主题
     */
    @TableField("theme")
    private String theme;

    /**
     * 报表标签
     */
    @TableField("tags")
    private String tags;

    /**
     * 报表分类
     */
    @TableField("category")
    private String category;

    /**
     * 是否公开访问
     */
    @TableField("public_access")
    private Boolean publicAccess;

    /**
     * 创建者
     */
    @TableField("creator")
    private String creator;

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
     * 更新者ID
     */
    @TableField("updater_id")
    private String updaterId;

    /**
     * 逻辑删除标记
     */
    @TableLogic(value = "false", delval = "true")
    @TableField("is_deleted")
    private Boolean isDeleted;
}