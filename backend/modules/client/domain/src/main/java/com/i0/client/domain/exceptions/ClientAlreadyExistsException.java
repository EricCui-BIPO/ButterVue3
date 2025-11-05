package com.i0.client.domain.exceptions;

import com.i0.domain.core.exceptions.DomainException;

/**
 * 客户已存在异常
 */
public class ClientAlreadyExistsException extends DomainException {
    
    public ClientAlreadyExistsException(String message) {
        super(message);
    }
    
    public static ClientAlreadyExistsException byName(String name) {
        return new ClientAlreadyExistsException("客户名称已存在: " + name);
    }

    public static ClientAlreadyExistsException byCode(String code) {
        return new ClientAlreadyExistsException("客户代码已存在: " + code);
    }
}