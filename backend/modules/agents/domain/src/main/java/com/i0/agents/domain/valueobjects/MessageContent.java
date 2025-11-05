package com.i0.agents.domain.valueobjects;

import org.apache.commons.lang3.StringUtils;

/**
 * 消息内容值对象
 * 表示聊天消息的内容，保证不可变性和自验证
 */
public class MessageContent {
    private final String content;
    private final int length;

    private MessageContent(String content) {
        this.content = content;
        this.length = content != null ? content.length() : 0;
    }

    /**
     * 创建消息内容
     */
    public static MessageContent of(String content) {
        if (content == null) {
            throw new IllegalArgumentException("消息内容不能为空");
        }

        String trimmedContent = content.trim();
        if (trimmedContent.isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空字符串");
        }

        if (trimmedContent.length() > 10000) {
            throw new IllegalArgumentException("消息内容长度不能超过10000字符");
        }

        return new MessageContent(trimmedContent);
    }

    /**
     * 创建空消息内容
     */
    public static MessageContent empty() {
        return new MessageContent("");
    }

    /**
     * 获取内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 获取内容长度
     */
    public int getLength() {
        return length;
    }

    /**
     * 判断是否为空
     */
    public boolean isEmpty() {
        return StringUtils.isEmpty(content);
    }

    /**
     * 判断是否包含指定关键词
     */
    public boolean contains(String keyword) {
        return StringUtils.isNotBlank(content) &&
               StringUtils.isNotBlank(keyword) &&
               content.contains(keyword);
    }

    /**
     * 截取前N个字符
     */
    public MessageContent truncate(int maxLength) {
        if (length <= maxLength) {
            return this;
        }
        return new MessageContent(content.substring(0, maxLength) + "...");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageContent that = (MessageContent) o;
        return content.equals(that.content);
    }

    @Override
    public int hashCode() {
        return content.hashCode();
    }

    @Override
    public String toString() {
        return "MessageContent{" +
                "content='" + (length > 50 ? content.substring(0, 50) + "..." : content) + '\'' +
                ", length=" + length +
                '}';
    }
}