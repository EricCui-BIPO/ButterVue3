package com.i0.app.api;

/**
 * 响应状态码枚举
 */
public enum ResponseCode {

    // 成功状态
    SUCCESS("0000", "操作成功"),

    // 客户端错误
    BAD_REQUEST("4000", "请求参数错误"),
    UNAUTHORIZED("4001", "未授权访问"),
    FORBIDDEN("4003", "禁止访问"),
    NOT_FOUND("4004", "资源不存在"),
    METHOD_NOT_ALLOWED("4005", "请求方法不允许"),

    // 服务端错误
    INTERNAL_ERROR("5000", "服务器内部错误"),
    SERVICE_UNAVAILABLE("5003", "服务暂时不可用"),

    // 业务错误
    BUSINESS_ERROR("6000", "业务处理错误"),
    ENTITY_NOT_FOUND("6001", "实体不存在"),
    ENTITY_ALREADY_EXISTS("6002", "实体已存在"),
    ENTITY_CONFLICT("6003", "实体状态冲突"),

    // 参数验证错误
    VALIDATION_ERROR("7000", "参数验证失败"),
    REQUIRED_FIELD_MISSING("7001", "必填字段缺失"),
    INVALID_FORMAT("7002", "格式不正确"),
    INVALID_LENGTH("7003", "长度超出限制"),
    INVALID_RANGE("7004", "数值超出范围");

    private final String code;
    private final String message;

    ResponseCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 根据code获取枚举
     * @param code 状态码
     * @return ResponseCode
     */
    public static ResponseCode fromCode(String code) {
        for (ResponseCode responseCode : values()) {
            if (responseCode.code.equals(code)) {
                return responseCode;
            }
        }
        return INTERNAL_ERROR;
    }
}