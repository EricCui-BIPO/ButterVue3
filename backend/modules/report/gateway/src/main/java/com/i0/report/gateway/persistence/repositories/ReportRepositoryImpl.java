package com.i0.report.gateway.persistence.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i0.report.domain.entities.Chart;
import com.i0.report.domain.entities.Report;
import com.i0.report.domain.repositories.ReportRepository;
import com.i0.report.domain.valueobjects.Filter;
import com.i0.report.gateway.persistence.mappers.ReportMapper;
import com.i0.report.gateway.persistence.dataobjects.ReportDO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 报表仓储实现
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ReportRepositoryImpl extends ServiceImpl<ReportMapper, ReportDO> implements ReportRepository {

    private final ReportMapper reportMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Report save(Report report) {
        ReportDO reportDO = convertToReportDO(report);

        if (StringUtils.hasText(report.getId())) {
            // 更新操作
            reportDO.setUpdatedAt(LocalDateTime.now());
            updateById(reportDO);
        } else {
            // 新增操作
            reportDO.setCreatedAt(LocalDateTime.now());
            reportDO.setUpdatedAt(LocalDateTime.now());
            save(reportDO);
        }

        return convertToReport(reportDO);
    }

    @Override
    public Optional<Report> findById(String id) {
        ReportDO reportDO = getById(id);
        return Optional.ofNullable(convertToReport(reportDO));
    }

    @Override
    public List<Report> findAll() {
        LambdaQueryWrapper<ReportDO> queryWrapper = new LambdaQueryWrapper<ReportDO>()
                .eq(ReportDO::getIsDeleted, false)
                .orderByDesc(ReportDO::getCreatedAt);

        List<ReportDO> reportDOList = list(queryWrapper);
        return reportDOList.stream()
                .map(this::convertToReport)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByStatus(String status) {
        LambdaQueryWrapper<ReportDO> queryWrapper = new LambdaQueryWrapper<ReportDO>()
                .eq(ReportDO::getIsDeleted, false)
                .eq(ReportDO::getStatus, status)
                .orderByDesc(ReportDO::getCreatedAt);

        List<ReportDO> reportDOList = list(queryWrapper);
        return reportDOList.stream()
                .map(this::convertToReport)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByCategory(String category) {
        LambdaQueryWrapper<ReportDO> queryWrapper = new LambdaQueryWrapper<ReportDO>()
                .eq(ReportDO::getIsDeleted, false)
                .eq(ReportDO::getCategory, category)
                .orderByDesc(ReportDO::getCreatedAt);

        List<ReportDO> reportDOList = list(queryWrapper);
        return reportDOList.stream()
                .map(this::convertToReport)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByCreator(String creator) {
        LambdaQueryWrapper<ReportDO> queryWrapper = new LambdaQueryWrapper<ReportDO>()
                .eq(ReportDO::getIsDeleted, false)
                .eq(ReportDO::getCreator, creator)
                .orderByDesc(ReportDO::getCreatedAt);

        List<ReportDO> reportDOList = list(queryWrapper);
        return reportDOList.stream()
                .map(this::convertToReport)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findEnabled() {
        LambdaQueryWrapper<ReportDO> queryWrapper = new LambdaQueryWrapper<ReportDO>()
                .eq(ReportDO::getIsDeleted, false)
                .eq(ReportDO::getEnabled, true)
                .orderByDesc(ReportDO::getCreatedAt);

        List<ReportDO> reportDOList = list(queryWrapper);
        return reportDOList.stream()
                .map(this::convertToReport)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findPublic() {
        LambdaQueryWrapper<ReportDO> queryWrapper = new LambdaQueryWrapper<ReportDO>()
                .eq(ReportDO::getIsDeleted, false)
                .eq(ReportDO::getPublicAccess, true)
                .eq(ReportDO::getEnabled, true)
                .orderByDesc(ReportDO::getCreatedAt);

        List<ReportDO> reportDOList = list(queryWrapper);
        return reportDOList.stream()
                .map(this::convertToReport)
                .collect(Collectors.toList());
    }

    @Override
    public List<Report> findByName(String name) {
        LambdaQueryWrapper<ReportDO> queryWrapper = new LambdaQueryWrapper<ReportDO>()
                .eq(ReportDO::getIsDeleted, false)
                .like(StringUtils.hasText(name), ReportDO::getName, name)
                .orderByDesc(ReportDO::getCreatedAt);

        List<ReportDO> reportDOList = list(queryWrapper);
        return reportDOList.stream()
                .map(this::convertToReport)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        removeById(id);
    }

    @Override
    public void delete(Report report) {
        removeById(report.getId());
    }

    @Override
    public boolean existsById(String id) {
        return lambdaQuery()
                .eq(ReportDO::getId, id)
                .eq(ReportDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public boolean existsByName(String name) {
        return lambdaQuery()
                .eq(ReportDO::getName, name)
                .eq(ReportDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public long count() {
        return lambdaQuery()
                .eq(ReportDO::getIsDeleted, false)
                .count();
    }

    @Override
    public long countByStatus(String status) {
        return lambdaQuery()
                .eq(ReportDO::getIsDeleted, false)
                .eq(ReportDO::getStatus, status)
                .count();
    }

    @Override
    public long countByCategory(String category) {
        return lambdaQuery()
                .eq(ReportDO::getIsDeleted, false)
                .eq(ReportDO::getCategory, category)
                .count();
    }

    @Override
    public long countEnabled() {
        return lambdaQuery()
                .eq(ReportDO::getIsDeleted, false)
                .eq(ReportDO::getEnabled, true)
                .count();
    }

    @Override
    public List<Report> saveAll(List<Report> reports) {
        List<ReportDO> reportDOList = reports.stream()
                .map(this::convertToReportDO)
                .collect(Collectors.toList());

        saveBatch(reportDOList);

        return reportDOList.stream()
                .map(this::convertToReport)
                .collect(Collectors.toList());
    }

    @Override
    public com.i0.domain.core.pagination.Pageable<Report> searchReports(
            String keyword,
            List<Filter> filters,
            int page,
            int size) {
        // 简化实现，返回空分页结果
        return com.i0.persistence.spring.pagination.SpringPage.of(
                new org.springframework.data.domain.PageImpl<>(new ArrayList<>())
        );
    }

    @Override
    public List<Report> getRecentlyUpdatedReports(int limit) {
        // 简化实现，返回空列表
        return new ArrayList<>();
    }

    @Override
    public List<Report> getPopularReports(int limit) {
        // 简化实现，返回空列表
        return new ArrayList<>();
    }

    // 以下方法为Repository接口要求的方法，暂时提供简化实现
    @Override
    public void recordAccess(String reportId, String userId, String accessType) {
        // 简化实现，暂时只记录日志
        log.info("记录报表访问: reportId={}, userId={}, accessType={}", reportId, userId, accessType);
    }

    @Override
    public List<Report> findByFilters(List<Filter> filters) {
        // 简化实现，返回所有启用报表
        return findEnabled();
    }

    @Override
    public com.i0.domain.core.pagination.Pageable<Report> findWithPagination(
            List<Filter> filters,
            int page,
            int size) {
        // 简化实现，返回空分页结果
        return com.i0.persistence.spring.pagination.SpringPage.of(
                new org.springframework.data.domain.PageImpl<>(new ArrayList<>())
        );
    }

    @Override
    public Object generateReportData(Report report, List<Filter> additionalFilters) {
        // 简化实现，返回基本报表信息
        return java.util.Map.of(
                "reportId", report.getId(),
                "reportName", report.getName(),
                "status", report.getStatus(),
                "timestamp", java.time.LocalDateTime.now()
        );
    }

    @Override
    public java.util.Map<String, Object> generateReportCharts(
            Report report,
            List<Filter> additionalFilters) {
        // 简化实现，返回空图表映射
        return new java.util.HashMap<>();
    }

    @Override
    public long countByCreator(String creator) {
        return lambdaQuery()
                .eq(ReportDO::getIsDeleted, false)
                .eq(ReportDO::getCreator, creator)
                .count();
    }

    // 其他未实现的方法暂时提供空实现或抛出UnsupportedOperationException
    @Override
    public boolean validateReportConfig(Report report) {
        return true; // 简化实现
    }

    @Override
    public Report publishReport(String reportId) {
        return findById(reportId).orElseThrow(); // 简化实现
    }

    @Override
    public Report archiveReport(String reportId) {
        return findById(reportId).orElseThrow(); // 简化实现
    }

    @Override
    public Report duplicateReport(String reportId, String newName, String newCreator) {
        return findById(reportId).orElseThrow(); // 简化实现
    }

    @Override
    public java.util.Map<String, Object> getPerformanceStats(String reportId) {
        return new java.util.HashMap<>(); // 简化实现
    }

    @Override
    public void cacheReportData(Report report, List<Filter> filters, Object data, long ttl) {
        // 简化实现，暂不缓存
    }

    @Override
    public Optional<Object> getCachedReportData(Report report, List<Filter> filters) {
        return Optional.empty(); // 简化实现
    }

    @Override
    public void clearCache(String reportId) {
        // 简化实现，暂无缓存
    }

    @Override
    public void clearAllCache() {
        // 简化实现，暂无缓存
    }

    @Override
    public String exportReportConfig(String reportId) {
        return "{}"; // 简化实现
    }

    @Override
    public Report importReportConfig(String configJson, String newCreator) {
        throw new UnsupportedOperationException("暂未实现"); // 简化实现
    }

    @Override
    public java.util.Map<String, Object> getAccessStats(
            String reportId,
            java.time.LocalDateTime startDate,
            java.time.LocalDateTime endDate) {
        return new java.util.HashMap<>(); // 简化实现
    }

    /**
     * 转换Report为ReportDO
     */
    private ReportDO convertToReportDO(Report report) {
        if (report == null) {
            return null;
        }

        ReportDO reportDO = new ReportDO();
        reportDO.setId(report.getId());
        reportDO.setName(report.getName());
        reportDO.setDescription(report.getDescription());
        reportDO.setRefreshInterval(report.getRefreshInterval());
        reportDO.setStatus(report.getStatus());
        reportDO.setTheme(report.getTheme());
        reportDO.setTags(report.getTags());
        reportDO.setCategory(report.getCategory());
        reportDO.setPublicAccess(report.getPublicAccess());
        reportDO.setCreator(report.getCreator());
        reportDO.setEnabled(report.getEnabled());

        // 转换图表列表为JSON
        if (report.getCharts() != null && !report.getCharts().isEmpty()) {
            try {
                String chartsJson = objectMapper.writeValueAsString(report.getCharts());
                reportDO.setCharts(chartsJson);
            } catch (JsonProcessingException e) {
                log.error("转换图表列表为JSON失败", e);
                reportDO.setCharts("[]");
            }
        }

        // 转换过滤条件为JSON
        if (report.getFilters() != null && !report.getFilters().isEmpty()) {
            try {
                String filtersJson = objectMapper.writeValueAsString(report.getFilters());
                reportDO.setFilters(filtersJson);
            } catch (JsonProcessingException e) {
                log.error("转换过滤条件为JSON失败", e);
                reportDO.setFilters("[]");
            }
        }

        // 转换布局配置为JSON
        if (StringUtils.hasText(report.getLayout())) {
            reportDO.setLayout(report.getLayout());
        }

        return reportDO;
    }

    /**
     * 转换ReportDO为Report
     */
    private Report convertToReport(ReportDO reportDO) {
        if (reportDO == null) {
            return null;
        }

        Report.ReportBuilder builder = Report.builder()
                .id(reportDO.getId())
                .name(reportDO.getName())
                .description(reportDO.getDescription())
                .refreshInterval(reportDO.getRefreshInterval())
                .status(reportDO.getStatus())
                .theme(reportDO.getTheme())
                .tags(reportDO.getTags())
                .category(reportDO.getCategory())
                .publicAccess(reportDO.getPublicAccess())
                .creator(reportDO.getCreator())
                .enabled(reportDO.getEnabled());

        // 转换JSON图表列表为对象
        if (StringUtils.hasText(reportDO.getCharts())) {
            try {
                List<Chart> charts = objectMapper.readValue(reportDO.getCharts(),
                        new TypeReference<List<Chart>>() {});
                builder.charts(charts);
            } catch (JsonProcessingException e) {
                log.error("转换JSON图表列表为对象失败", e);
                builder.charts(new ArrayList<>());
            }
        } else {
            builder.charts(new ArrayList<>());
        }

        // 转换JSON过滤条件为对象
        if (StringUtils.hasText(reportDO.getFilters())) {
            try {
                List<Filter> filters = objectMapper.readValue(reportDO.getFilters(),
                        new TypeReference<List<Filter>>() {});
                builder.filters(filters);
            } catch (JsonProcessingException e) {
                log.error("转换JSON过滤条件为对象失败", e);
                builder.filters(new ArrayList<>());
            }
        } else {
            builder.filters(new ArrayList<>());
        }

        // 转换布局配置
        if (StringUtils.hasText(reportDO.getLayout())) {
            builder.layout(reportDO.getLayout());
        }

        return builder.build();
    }
}