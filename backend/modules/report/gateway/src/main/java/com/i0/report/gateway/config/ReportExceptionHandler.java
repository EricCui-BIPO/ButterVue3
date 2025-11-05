package com.i0.report.gateway.config;

import com.i0.report.domain.exceptions.DatasetNotFoundException;
import com.i0.report.domain.exceptions.IndicatorNotFoundException;
import com.i0.report.domain.exceptions.ReportDisabledException;
import com.i0.report.domain.exceptions.ReportDomainException;
import com.i0.report.domain.exceptions.ReportNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 报表模块全局异常处理器
 * 将Domain异常转换为HTTP响应
 */
@RestControllerAdvice
@Slf4j
public class ReportExceptionHandler {

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleReportNotFound(ReportNotFoundException e) {
        log.warn("报表不存在: {}", e.getMessage());
        Map<String, Object> response = createErrorResponse(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ReportDisabledException.class)
    public ResponseEntity<Map<String, Object>> handleReportDisabled(ReportDisabledException e) {
        log.warn("报表已禁用: {}", e.getMessage());
        Map<String, Object> response = createErrorResponse(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IndicatorNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleIndicatorNotFound(IndicatorNotFoundException e) {
        log.warn("指标不存在: {}", e.getMessage());
        Map<String, Object> response = createErrorResponse(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DatasetNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDatasetNotFound(DatasetNotFoundException e) {
        log.warn("数据集不存在: {}", e.getMessage());
        Map<String, Object> response = createErrorResponse(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ReportDomainException.class)
    public ResponseEntity<Map<String, Object>> handleReportDomainException(ReportDomainException e) {
        log.error("报表领域异常: {}", e.getMessage(), e);
        Map<String, Object> response = createErrorResponse(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        log.error("系统异常: {}", e.getMessage(), e);
        Map<String, Object> response = createErrorResponse("SYSTEM_ERROR", "系统内部错误");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private Map<String, Object> createErrorResponse(String errorCode, String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("errorCode", errorCode);
        response.put("errorMessage", errorMessage);
        response.put("data", null);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}