package com.i0.report.application.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 报表输出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportOutput {

    /**
     * 报表唯一标识
     */
    private String id;

    /**
     * 报表名称
     */
    private String name;

    /**
     * 报表描述
     */
    private String description;

    /**
     * 报表状态
     */
    private String status;

    /**
     * 报表分类
     */
    private String category;

    /**
     * 报表标签
     */
    private String tags;

    /**
     * 报表布局
     */
    private String layout;

    /**
     * 报表主题
     */
    private String theme;

    /**
     * 刷新间隔（分钟）
     */
    private Integer refreshInterval;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否公开
     */
    private Boolean publicAccess;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 报表包含的图表数量
     */
    private Integer chartCount;

    /**
     * 从Domain实体转换为输出DTO
     */
    public static ReportOutput from(com.i0.report.domain.entities.Report report) {
        if (report == null) {
            return null;
        }

        return ReportOutput.builder()
                .id(report.getId())
                .name(report.getName())
                .description(report.getDescription())
                .status(report.getStatus())
                .category(report.getCategory())
                .tags(report.getTags())
                .layout(report.getLayout())
                .theme(report.getTheme())
                .refreshInterval(report.getRefreshInterval())
                .enabled(report.getEnabled())
                .publicAccess(report.getPublicAccess())
                .creator(report.getCreator())
                .chartCount(report.getCharts() != null ? report.getCharts().size() : 0)
                .build();
    }

    /**
     * 从Domain实体列表转换为输出DTO列表
     */
    public static List<ReportOutput> from(List<com.i0.report.domain.entities.Report> reports) {
        if (reports == null) {
            return new java.util.ArrayList<>();
        }
        return reports.stream()
                .map(ReportOutput::from)
                .collect(java.util.stream.Collectors.toList());
    }
}