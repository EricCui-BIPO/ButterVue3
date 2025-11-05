package com.i0.app.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 统一API响应封装类
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> {

    /**
     * 请求是否成功
     */
    private Boolean success;

    /**
     * 错误码（仅错误响应使用）
     */
    private String errorCode;

    /**
     * 错误信息（仅错误响应使用）
     */
    private String errorMessage;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间戳
     */
    private String timestamp;

    /**
     * 创建成功响应
     * @param data 响应数据
     * @return ApiResult
     */
    public static <T> ApiResult<T> success(T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setSuccess(true);
        result.setData(data);
        result.setTimestamp(Instant.now().toString());
        return result;
    }

    /**
     * 创建成功响应（无数据）
     * @return ApiResult
     */
    public static <T> ApiResult<T> success() {
        return success(null);
    }

    /**
     * 创建错误响应
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @return ApiResult
     */
    public static <T> ApiResult<T> error(String errorCode, String errorMessage) {
        ApiResult<T> result = new ApiResult<>();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        result.setData(null);
        result.setTimestamp(Instant.now().toString());
        return result;
    }

    /**
     * 创建错误响应（带错误数据）
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @param data 错误数据
     * @return ApiResult
     */
    public static <T> ApiResult<T> error(String errorCode, String errorMessage, T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setSuccess(false);
        result.setErrorCode(errorCode);
        result.setErrorMessage(errorMessage);
        result.setData(data);
        result.setTimestamp(Instant.now().toString());
        return result;
    }

    /**
     * 检查是否成功
     * @return 是否成功
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }
}