package com.i0.agents.domain.enums;

/**
 * 消息角色枚举
 * 定义聊天消息中的角色类型
 */
public enum MessageRole {
    /**
     * 用户角色
     */
    USER("用户"),

    /**
     * AI助手角色
     */
    ASSISTANT("助手"),

    /**
     * 系统角色
     */
    SYSTEM("系统"),

    /**
     * 函数调用角色
     */
    FUNCTION("函数调用");

    private final String description;

    MessageRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 判断是否为系统角色
     */
    public boolean isSystem() {
        return this == SYSTEM;
    }

    /**
     * 判断是否为用户角色
     */
    public boolean isUser() {
        return this == USER;
    }

    /**
     * 判断是否为AI助手角色
     */
    public boolean isAssistant() {
        return this == ASSISTANT;
    }

    /**
     * 判断是否为函数调用角色
     */
    public boolean isFunction() {
        return this == FUNCTION;
    }
}