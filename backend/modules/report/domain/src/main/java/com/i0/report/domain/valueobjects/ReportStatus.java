package com.i0.report.domain.valueobjects;

/**
 * 报表状态值对象
 */
public enum ReportStatus {
    DRAFT("draft", "草稿"),
    PUBLISHED("published", "已发布"),
    ARCHIVED("archived", "已归档");

    private final String code;
    private final String description;

    ReportStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 检查状态代码是否有效
     */
    public static boolean isValid(String statusCode) {
        if (statusCode == null || statusCode.trim().isEmpty()) {
            return false;
        }

        for (ReportStatus status : ReportStatus.values()) {
            if (status.code.equals(statusCode.toLowerCase().trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据状态代码获取枚举
     */
    public static ReportStatus fromCode(String statusCode) {
        if (statusCode == null || statusCode.trim().isEmpty()) {
            throw new IllegalArgumentException("状态代码不能为空");
        }

        for (ReportStatus status : ReportStatus.values()) {
            if (status.code.equals(statusCode.toLowerCase().trim())) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的状态代码: " + statusCode);
    }

    /**
     * 检查是否为草稿状态
     */
    public boolean isDraft() {
        return this == DRAFT;
    }

    /**
     * 检查是否为已发布状态
     */
    public boolean isPublished() {
        return this == PUBLISHED;
    }

    /**
     * 检查是否为已归档状态
     */
    public boolean isArchived() {
        return this == ARCHIVED;
    }

    /**
     * 检查是否可以进行发布操作
     */
    public boolean canPublish() {
        return this == DRAFT;
    }

    /**
     * 检查是否可以进行归档操作
     */
    public boolean canArchive() {
        return this == PUBLISHED || this == DRAFT;
    }

    /**
     * 检查是否可以编辑
     */
    public boolean canEdit() {
        return this == DRAFT;
    }
}