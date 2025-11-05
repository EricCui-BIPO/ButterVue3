package com.i0.app.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应包装器
 * 自动将Controller返回的数据包装成统一的ApiResult格式
 */
@Slf4j
@ControllerAdvice
public class ApiResponseWrapper implements ResponseBodyAdvice<Object> {

    /**
     * 简单的JSON字符串转义方法
     */
    private String escapeJsonString(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\b", "\\b")
                    .replace("\f", "\\f")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // 如果返回类型已经是ApiResult，则不需要再次包装
        return !returnType.getParameterType().equals(ApiResult.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        // 如果返回的是ResponseEntity且body是ApiResult，直接返回body（不包装）
        if (body instanceof ResponseEntity) {
            ResponseEntity<?> responseEntity = (ResponseEntity<?>) body;
            if (responseEntity.getBody() instanceof ApiResult) {
                return responseEntity.getBody();
            }
        }

        // 如果返回的是ApiResult，直接返回（不包装）
        if (body instanceof ApiResult) {
            return body;
        }

        // 如果返回的是字符串，需要特殊处理
        if (body instanceof String) {
            // 设置响应内容类型为JSON
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            // 创建ApiResult并转换为JSON字符串
            String escapedData = escapeJsonString((String) body);
            return "{\"success\":true,\"data\":\"" + escapedData + "\",\"timestamp\":\"" + java.time.Instant.now().toString() + "\"}";
        }

        // 如果返回的是null，返回成功响应（无数据）
        if (body == null) {
            return ApiResult.success();
        }

        // 包装成成功响应
        return ApiResult.success(body);
    }
}