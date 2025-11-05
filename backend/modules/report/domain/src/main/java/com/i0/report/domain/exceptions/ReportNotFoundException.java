package com.i0.report.domain.exceptions;

/**
 * 报表不存在异常
 */
public class ReportNotFoundException extends ReportDomainException {

    public ReportNotFoundException(String reportId) {
        super("REPORT_NOT_FOUND", "报表不存在: " + reportId);
    }
}