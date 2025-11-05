package com.i0.domain.core.exceptions;

/**
 * 领域异常基类
 * 所有领域层的异常都应该继承此类
 */
public abstract class DomainException extends RuntimeException {
    
    public DomainException(String message) {
        super(message);
    }
    
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}