package com.i0.report.domain.exceptions;

/**
 * 报表已禁用异常
 */
public class ReportDisabledException extends ReportDomainException {

    public ReportDisabledException(String reportId) {
        super("REPORT_DISABLED", "报表已禁用: " + reportId);
    }
}