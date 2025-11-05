package com.i0.client.domain.exceptions;

import com.i0.domain.core.exceptions.DomainException;

/**
 * 客户未找到异常
 */
public class ClientNotFoundException extends DomainException {
    
    public ClientNotFoundException(String message) {
        super(message);
    }
    
    public static ClientNotFoundException byId(String id) {
        return new ClientNotFoundException("客户不存在: " + id);
    }

    public static ClientNotFoundException byField(String field, String value) {
        return new ClientNotFoundException("客户不存在 (" + field + "=" + value + ")");
    }
}