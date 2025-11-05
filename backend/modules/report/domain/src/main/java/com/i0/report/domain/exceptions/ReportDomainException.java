package com.i0.report.domain.exceptions;

/**
 * 报表领域异常基类
 */
public class ReportDomainException extends RuntimeException {

    private final String errorCode;

    public ReportDomainException(String message) {
        super(message);
        this.errorCode = "REPORT_DOMAIN_ERROR";
    }

    public ReportDomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ReportDomainException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "REPORT_DOMAIN_ERROR";
    }

    public ReportDomainException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}