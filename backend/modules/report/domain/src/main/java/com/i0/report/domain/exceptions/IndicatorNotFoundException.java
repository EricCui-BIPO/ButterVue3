package com.i0.report.domain.exceptions;

/**
 * 指标不存在异常
 */
public class IndicatorNotFoundException extends ReportDomainException {

    public IndicatorNotFoundException(String indicatorId) {
        super("INDICATOR_NOT_FOUND", "指标不存在: " + indicatorId);
    }
}