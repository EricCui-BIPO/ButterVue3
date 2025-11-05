package com.i0.agents.domain.exceptions;

import com.i0.domain.core.exceptions.DomainException;

/**
 * Agents领域异常
 */
public class AgentsException extends DomainException {
    public AgentsException(String message) {
        super(message);
    }

    public AgentsException(String message, Throwable cause) {
        super(message, cause);
    }
}