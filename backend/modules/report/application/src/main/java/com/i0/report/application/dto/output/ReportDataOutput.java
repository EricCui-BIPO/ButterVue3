package com.i0.report.application.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 报表数据输出DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDataOutput {

    /**
     * 报表ID
     */
    private String reportId;

    /**
     * 报表名称
     */
    private String reportName;

    /**
     * 报表状态
     */
    private String reportStatus;

    /**
     * 图表数据列表
     */
    private List<Map<String, Object>> chartData;

    /**
     * 刷新间隔（分钟）
     */
    private Integer refreshInterval;

    /**
     * 报表主题
     */
    private String theme;

    /**
     * 布局配置
     */
    private String layout;

    /**
     * 数据生成时间
     */
    private Long timestamp;

    /**
     * 静态转换方法
     */
    public static ReportDataOutput from(Map<String, Object> reportData) {
        if (reportData == null) {
            return null;
        }

        return ReportDataOutput.builder()
                .reportId((String) reportData.get("reportId"))
                .reportName((String) reportData.get("reportName"))
                .reportStatus((String) reportData.get("reportStatus"))
                .chartData((List<Map<String, Object>>) reportData.get("chartData"))
                .refreshInterval((Integer) reportData.get("refreshInterval"))
                .theme((String) reportData.get("theme"))
                .layout((String) reportData.get("layout"))
                .timestamp(System.currentTimeMillis())
                .build();
    }
}