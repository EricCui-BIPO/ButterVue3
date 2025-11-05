package com.i0.report.domain.services;

import com.i0.report.domain.entities.Chart;
import com.i0.report.domain.entities.Indicator;
import com.i0.report.domain.valueobjects.ChartType;
import com.i0.report.domain.valueobjects.IndicatorType;

import java.util.*;

/**
 * 图表数据格式化服务
 * 负责根据图表类型格式化数据
 */
public class ChartDataFormattingService {

    /**
     * 根据图表类型格式化数据
     *
     * @param chart 图表配置
     * @param indicator 指标配置
     * @param indicatorData 原始指标数据
     * @return 格式化后的数据
     */
    public Map<String, Object> formatDataByChartType(Chart chart, Indicator indicator, Object indicatorData) {
        if (!chart.isValid()) {
            throw new IllegalArgumentException("图表配置无效");
        }
        if (!indicator.isValid()) {
            throw new IllegalArgumentException("指标配置无效");
        }

        String chartType = chart.getType();
        String dimension = chart.getDimension();
        String indicatorCalculation = indicator.getCalculation();

        Map<String, Object> formattedData = new HashMap<>();

        // 检查是否是直接从SQL计算得到的结果
        if (isDirectSqlResult(indicatorData)) {
            return formatDirectSqlResult(chartType, indicatorData, chart, indicator);
        }

        // 使用值对象进行类型判断，更安全和类型友好
        if (!ChartType.isValid(chartType)) {
            formattedData.put("type", "unknown");
            formattedData.put("data", indicatorData);
            return formattedData;
        }

        ChartType chartTypeEnum = ChartType.fromCode(chartType);

        // 根据图表类型进行数据格式化
        if (chartTypeEnum.isPieChart()) {
            formattedData = formatPieData(indicatorData, dimension);
        } else if (chartTypeEnum.isBarChart() || chartTypeEnum.isLineChart() || chartTypeEnum.isAreaChart()) {
            formattedData = formatXYData(indicatorData, dimension);
        } else if (chartTypeEnum.isMetric()) {
            formattedData = formatMetricData(indicatorData, indicatorCalculation);
        } else if (chartTypeEnum.isTable()) {
            formattedData = formatTableData(indicatorData);
        } else {
            formattedData.put("type", chartType);
            formattedData.put("data", indicatorData);
        }

        return formattedData;
    }

    /**
     * 判断是否是直接SQL查询结果
     */
    private boolean isDirectSqlResult(Object data) {
        if (data instanceof List) {
            List<?> dataList = (List<?>) data;
            if (!dataList.isEmpty() && dataList.get(0) instanceof Map) {
                Map<?, ?> firstRecord = (Map<?, ?>) dataList.get(0);
                // 检查是否包含任何数值类型的字段（统计字段）或者包含 dimension_field/calculated_value
                boolean hasDimensionField = firstRecord.containsKey("dimension_field") || firstRecord.containsKey("calculated_value");
                boolean hasNumericField = firstRecord.values().stream()
                    .anyMatch(value -> value instanceof Number);
                return hasDimensionField || hasNumericField;
            }
        }
        return false;
    }

    /**
     * 格式化直接SQL查询结果
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> formatDirectSqlResult(String chartType, Object data, Chart chart, Indicator indicator) {
        Map<String, Object> result = new HashMap<>();

        if (!(data instanceof List)) {
            result.put("type", chartType);
            result.put("data", data);
            return result;
        }

        List<Map<String, Object>> records = (List<Map<String, Object>>) data;

        // 使用值对象进行类型判断
        if (!ChartType.isValid(chartType)) {
            result.put("type", "unknown");
            result.put("data", data);
            return result;
        }

        ChartType chartTypeEnum = ChartType.fromCode(chartType);

        if (chartTypeEnum.isPieChart()) {
            result = formatPieFromDirectSql(records, chart, indicator);
        } else if (chartTypeEnum.isBarChart() || chartTypeEnum.isLineChart() || chartTypeEnum.isAreaChart()) {
            result = formatXYFromDirectSql(records, chart, indicator);
        } else if (chartTypeEnum.isMetric()) {
            result = formatMetricFromDirectSql(records, indicator);
        } else {
            result.put("type", chartType);
            result.put("data", data);
        }

        return result;
    }

    /**
     * 从直接SQL结果格式化饼图数据
     */
    private Map<String, Object> formatPieFromDirectSql(List<Map<String, Object>> records, Chart chart, Indicator indicator) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "pie");

        String dimensionField = chart.getDimension();
        String indicatorCalculation = indicator.getCalculation();

        List<Map<String, Object>> pieData = new ArrayList<>();
        for (Map<String, Object> record : records) {
            Map<String, Object> item = new HashMap<>();

            // 动态提取维度字段值
            String dimensionValue = extractDimensionValue(record, dimensionField);
            dimensionValue = beautifyDimensionValue(dimensionValue, dimensionField);

            // 动态提取统计字段值
            Object statisticalValue = extractStatisticalValue(record, indicatorCalculation);

            item.put("name", dimensionValue);
            item.put("value", statisticalValue);
            pieData.add(item);
        }

        result.put("data", pieData);
        return result;
    }

    /**
     * 从记录中提取维度字段值
     */
    private String extractDimensionValue(Map<String, Object> record, String dimensionField) {
        if (dimensionField == null || dimensionField.trim().isEmpty()) {
            return "未知";
        }

        Object value = record.get(dimensionField);
        if (value == null) {
            // 尝试使用默认字段名
            value = record.get("dimension_field");
        }

        return String.valueOf(value != null ? value : "未知");
    }

    /**
     * 从记录中提取统计字段值
     */
    private Object extractStatisticalValue(Map<String, Object> record, String indicatorCalculation) {
        return extractStatisticalValue(record, indicatorCalculation, null);
    }

    /**
     * 从记录中提取统计字段值（重载版本，用于排除特定字段）
     */
    private Object extractStatisticalValue(Map<String, Object> record, String indicatorCalculation, String excludeField) {
        if (indicatorCalculation == null || indicatorCalculation.trim().isEmpty()) {
            return 0;
        }

        // 首先尝试使用 indicatorCalculation 作为字段名
        Object value = record.get(indicatorCalculation);
        if (value != null) {
            return value;
        }

        // 尝试使用默认字段名
        value = record.get("calculated_value");
        if (value != null) {
            return value;
        }

        // 如果是数字类型的统计，尝试找到第一个数值字段
        for (Map.Entry<String, Object> entry : record.entrySet()) {
            Object val = entry.getValue();
            if (val instanceof Number && !entry.getKey().equals(excludeField)) {
                return val;
            }
        }

        return 0;
    }

    /**
     * 美化维度值显示
     */
    private String beautifyDimensionValue(String dimensionValue, String dimensionField) {
        if (dimensionValue == null || dimensionValue.trim().isEmpty() || "未知".equals(dimensionValue)) {
            return "未知";
        }

        // 根据字段类型进行美化
        if ("nationality_id".equals(dimensionField) && dimensionValue.startsWith("country-")) {
            // 国家代码转换
            return convertCountryCode(dimensionValue);
        }

        // 其他维度字段的美化逻辑可以在这里添加
        return dimensionValue;
    }

    /**
     * 国家代码转换
     */
    private String convertCountryCode(String countryCode) {
        switch (countryCode) {
            case "country-cn": return "中国";
            case "country-us": return "美国";
            case "country-jp": return "日本";
            case "country-kr": return "韩国";
            case "country-gb": return "英国";
            case "country-fr": return "法国";
            case "country-de": return "德国";
            case "country-ca": return "加拿大";
            case "country-au": return "澳大利亚";
            case "country-in": return "印度";
            case "country-sg": return "新加坡";
            case "country-tw": return "台湾";
            case "country-es": return "西班牙";
            case "country-se": return "瑞典";
            default:
                // 提取最后一个部分作为显示名称
                if (countryCode.contains("-")) {
                    return countryCode.substring(countryCode.lastIndexOf("-") + 1).toUpperCase();
                }
                return countryCode;
        }
    }

    /**
     * 从直接SQL结果格式化XY轴图表数据
     */
    private Map<String, Object> formatXYFromDirectSql(List<Map<String, Object>> records, Chart chart, Indicator indicator) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "xy");

        String dimensionField = chart.getDimension();
        String indicatorCalculation = indicator.getCalculation();
        result.put("xAxis", dimensionField != null ? dimensionField : "维度");

        List<String> xAxis = new ArrayList<>();
        List<Object> yAxis = new ArrayList<>();

        for (Map<String, Object> record : records) {
            // 动态提取维度字段值
            String dimensionValue = extractDimensionValue(record, dimensionField);
            dimensionValue = beautifyDimensionValue(dimensionValue, dimensionField);

            // 动态提取统计字段值
            Object statisticalValue = extractStatisticalValue(record, indicatorCalculation);

            xAxis.add(dimensionValue);
            yAxis.add(statisticalValue);
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("xAxis", xAxis);
        chartData.put("yAxis", yAxis);
        result.put("data", chartData);

        return result;
    }

    /**
     * 从直接SQL结果格式化指标卡数据
     */
    private Map<String, Object> formatMetricFromDirectSql(List<Map<String, Object>> records, Indicator indicator) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "metric");

        String indicatorCalculation = indicator.getCalculation();
        result.put("calculation", indicatorCalculation);

        if (!records.isEmpty()) {
            Map<String, Object> firstRecord = records.get(0);

            // 动态提取统计字段值
            Object statisticalValue = extractStatisticalValue(firstRecord, indicatorCalculation);

            // 格式化指标值
            Object formattedValue = formatMetricValue(statisticalValue, indicatorCalculation);
            result.put("value", formattedValue);
        } else {
            result.put("value", 0);
        }

        return result;
    }

    /**
     * 格式化饼图数据
     */
    private Map<String, Object> formatPieData(Object data, String dimension) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "pie");

        if (data instanceof Map && ((Map<?, ?>) data).containsKey("groups")) {
            // 如果是分组数据，转换为饼图格式
            Map<?, ?> groupData = (Map<?, ?>) data;
            if (groupData.get("groups") instanceof Map) {
                Map<?, ?> groups = (Map<?, ?>) groupData.get("groups");
                List<Map<String, Object>> pieData = new ArrayList<>();

                for (Map.Entry<?, ?> entry : groups.entrySet()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", entry.getKey());
                    item.put("value", entry.getValue());
                    pieData.add(item);
                }

                result.put("data", pieData);
                return result;
            }
        }

        // 默认处理
        result.put("data", data);
        return result;
    }

    /**
     * 格式化XY轴图表数据（柱状图、折线图、面积图）
     */
    private Map<String, Object> formatXYData(Object data, String dimension) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "xy");
        result.put("xAxis", dimension);

        if (data instanceof Map && ((Map<?, ?>) data).containsKey("groups")) {
            // 如果是分组数据，转换为XY轴图格式
            Map<?, ?> groupData = (Map<?, ?>) data;
            if (groupData.get("groups") instanceof Map) {
                Map<?, ?> groups = (Map<?, ?>) groupData.get("groups");

                List<String> xAxis = new ArrayList<>();
                List<Object> yAxis = new ArrayList<>();

                for (Map.Entry<?, ?> entry : groups.entrySet()) {
                    xAxis.add(String.valueOf(entry.getKey()));
                    yAxis.add(entry.getValue());
                }

                Map<String, Object> chartData = new HashMap<>();
                chartData.put("xAxis", xAxis);
                chartData.put("yAxis", yAxis);
                result.put("data", chartData);
                return result;
            }
        }

        // 默认处理
        result.put("data", data);
        return result;
    }

    /**
     * 格式化指标卡数据
     */
    private Map<String, Object> formatMetricData(Object data, String calculation) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "metric");

        // 格式化数值
        Object formattedValue = formatMetricValue(data, calculation);
        result.put("value", formattedValue);
        result.put("calculation", calculation);

        return result;
    }

    /**
     * 格式化表格数据
     */
    private Map<String, Object> formatTableData(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", "table");

        if (data instanceof List) {
            result.put("rows", data);
            result.put("total", ((List<?>) data).size());
        } else {
            result.put("rows", data);
            result.put("total", 1);
        }

        return result;
    }

    /**
     * 格式化指标值
     */
    private Object formatMetricValue(Object value, String calculation) {
        if (value == null) {
            return 0;
        }

        if (value instanceof Number) {
            double numValue = ((Number) value).doubleValue();

            // 使用值对象进行类型判断和格式化
            if (!IndicatorType.isValid(calculation)) {
                return numValue;
            }

            IndicatorType indicatorType = IndicatorType.fromCode(calculation);

            if (indicatorType.isCountIndicator()) {
                return Math.round(numValue);
            } else if (indicatorType.getCode().equals("avg")) {
                return Math.round(numValue * 100.0) / 100.0; // 保留两位小数
            } else {
                return numValue;
            }
        }

        return value;
    }
}