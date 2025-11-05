package com.i0.app.exception;

import com.i0.app.api.ApiResult;
import com.i0.app.api.ResponseCode;
import com.i0.domain.core.exceptions.DomainException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理系统中各类异常，返回标准化的API响应格式
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 领域异常处理器
     * 处理所有领域层异常，根据错误消息内容映射到具体的错误码
     */
    @ExceptionHandler({DomainException.class, com.i0.talent.domain.exception.DomainException.class})
    public ResponseEntity<ApiResult<Object>> handleDomainException(RuntimeException e) {
        log.warn("领域异常: {}", e.getMessage());

        BusinessErrorInfo errorInfo = resolveBusinessErrorCode(e.getMessage());

        ApiResult<Object> result = ApiResult.error(errorInfo.getErrorCode(), e.getMessage());
        return ResponseEntity.status(errorInfo.getHttpStatus()).body(result);
    }

    /**
     * 业务异常处理器
     * 处理所有业务逻辑异常，根据错误消息内容映射到具体的错误码
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResult<Object>> handleBusinessException(IllegalArgumentException e) {
        log.warn("业务异常: {}", e.getMessage());

        BusinessErrorInfo errorInfo = resolveBusinessErrorCode(e.getMessage());

        ApiResult<Object> result = ApiResult.error(errorInfo.getErrorCode(), e.getMessage());
        return ResponseEntity.status(errorInfo.getHttpStatus()).body(result);
    }

    /**
     * 方法参数验证异常处理器
     * 处理 @Valid 注解触发的参数验证失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("方法参数验证失败: {}", e.getMessage());

        Map<String, String> errors = extractFieldErrors(e.getBindingResult().getAllErrors());

        ApiResult<Object> result = ApiResult.error(
            ResponseCode.VALIDATION_ERROR.getCode(),
            ResponseCode.VALIDATION_ERROR.getMessage(),
            errors
        );
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 绑定异常处理器
     * 处理数据绑定过程中的异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResult<Object>> handleBindException(BindException e) {
        log.warn("数据绑定异常: {}", e.getMessage());

        Map<String, String> errors = extractFieldErrors(e.getBindingResult().getAllErrors());

        ApiResult<Object> result = ApiResult.error(
            ResponseCode.VALIDATION_ERROR.getCode(),
            ResponseCode.VALIDATION_ERROR.getMessage(),
            errors
        );
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 约束验证异常处理器
     * 处理 javax.validation 约束验证失败
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResult<Object>> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn("约束验证异常: {}", e.getMessage());

        Map<String, String> errors = e.getConstraintViolations().stream()
            .collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage
            ));

        ApiResult<Object> result = ApiResult.error(
            ResponseCode.VALIDATION_ERROR.getCode(),
            ResponseCode.VALIDATION_ERROR.getMessage(),
            errors
        );
        return ResponseEntity.badRequest().body(result);
    }

    /**
     * 通用异常处理器
     * 处理所有未明确捕获的异常，作为最后的异常处理保障
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Object>> handleGenericException(Exception e) {
        log.error("系统异常", e);

        ApiResult<Object> result = ApiResult.error(
            ResponseCode.INTERNAL_ERROR.getCode(),
            ResponseCode.INTERNAL_ERROR.getMessage()
        );
        return ResponseEntity.internalServerError().body(result);
    }

    /**
     * 解析业务错误码
     * 根据错误消息内容映射到具体的错误码和HTTP状态
     *
     * @param errorMessage 错误消息
     * @return 业务错误信息对象
     */
    private BusinessErrorInfo resolveBusinessErrorCode(String errorMessage) {
        if (errorMessage == null) {
            return new BusinessErrorInfo(ResponseCode.BUSINESS_ERROR.getCode(), HttpStatus.BAD_REQUEST);
        }

        String errorCode = ResponseCode.BUSINESS_ERROR.getCode();

        if (errorMessage.contains("不存在")) {
            errorCode = ResponseCode.ENTITY_NOT_FOUND.getCode();
        } else if (errorMessage.contains("已存在")) {
            errorCode = ResponseCode.ENTITY_ALREADY_EXISTS.getCode();
        } else if (errorMessage.contains("冲突") || errorMessage.contains("状态")) {
            errorCode = ResponseCode.ENTITY_CONFLICT.getCode();
        }

        // 业务异常统一使用 400 Bad Request，404 仅用于接口资源找不到
        return new BusinessErrorInfo(errorCode, HttpStatus.BAD_REQUEST);
    }

    /**
     * 提取字段验证错误
     * 从验证错误中提取字段名和错误消息
     *
     * @param errors 验证错误列表
     * @return 字段错误映射表
     */
    private Map<String, String> extractFieldErrors(java.util.List<org.springframework.validation.ObjectError> errors) {
        Map<String, String> fieldErrors = new HashMap<>();

        errors.forEach(error -> {
            if (error instanceof FieldError) {
                FieldError fieldError = (FieldError) error;
                fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
            } else {
                // 非字段错误使用对象名作为key
                fieldErrors.put(error.getObjectName(), error.getDefaultMessage());
            }
        });

        return fieldErrors;
    }

    /**
     * 业务错误信息内部类
     * 封装错误码和HTTP状态码
     */
    @Getter
    private static class BusinessErrorInfo {
        private final String errorCode;
        private final HttpStatus httpStatus;

        public BusinessErrorInfo(String errorCode, HttpStatus httpStatus) {
            this.errorCode = errorCode;
            this.httpStatus = httpStatus;
        }

    }
}