package com.i0.client.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 位置信息值对象
 * 封装位置相关的基本信息，避免跨域直接依赖Location实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationInfo {

    /**
     * 位置ID
     */
    private String id;

    /**
     * 位置名称
     */
    private String name;

    /**
     * 位置代码
     */
    private String code;

    /**
     * 位置类型
     */
    private String type;

    /**
     * 父位置ID
     */
    private String parentId;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否激活
     */
    private Boolean active;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 根据Location实体创建LocationInfo
     * @param location Location实体
     * @return LocationInfo对象
     */
    public static LocationInfo from(Object location) {
        if (location == null) {
            return null;
        }

        try {
            // 使用反射获取字段值，但简化实现
            return LocationInfo.builder()
                    .id(getFieldValue(location, "id", String.class))
                    .name(getFieldValue(location, "name", String.class))
                    .code(getFieldValue(location, "isoCode", String.class))
                    .type(getFieldValue(location, "locationType", Object.class) != null ?
                          getFieldValue(location, "locationType", Object.class).toString() : null)
                    .parentId(getFieldValue(location, "parentId", String.class))
                    .description(getFieldValue(location, "description", String.class))
                    .active(getFieldValue(location, "active", Boolean.class))
                    .createdAt(getFieldValue(location, "createdAt", LocalDateTime.class))
                    .updatedAt(getFieldValue(location, "updatedAt", LocalDateTime.class))
                    .build();
        } catch (Exception e) {
            // 如果反射失败，返回null而不是创建默认值
            return null;
        }
    }

    /**
     * 安全获取字段值
     */
    private static <T> T getFieldValue(Object obj, String fieldName, Class<T> type) {
        try {
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(obj);
            return type.cast(value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 创建简单的LocationInfo（只包含ID和名称）
     * @param id 位置ID
     * @param name 位置名称
     * @return LocationInfo对象
     */
    public static LocationInfo of(String id, String name) {
        return LocationInfo.builder()
                .id(id)
                .name(name)
                .build();
    }

    /**
     * 获取位置显示名称
     * @return 显示名称
     */
    public String getDisplayName() {
        if (name != null && !name.trim().isEmpty()) {
            return name;
        }
        return code != null ? code : "未知位置";
    }
}