package com.i0.agents.domain.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * UI组件引用值对象
 * 表示与聊天消息关联的UI组件信息，用于历史消息重现时重新渲染UI组件
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UIComponentReference {

    /**
     * 组件类型（如表单、图表、列表等）
     */
    private String componentType;

    /**
     * 组件数据（用于组件渲染的具体数据）
     */
    private Object componentData;

    /**
     * 组件创建时间
     */
    private LocalDateTime timestamp;

    /**
     * 创建UI组件引用
     */
    public static UIComponentReference of(String componentType, Object componentData) {
        if (StringUtils.isBlank(componentType)) {
            throw new IllegalArgumentException("组件类型不能为空");
        }

        return UIComponentReference.builder()
                .componentType(componentType.trim())
                .componentData(componentData)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 判断是否为有效的组件引用
     */
    public boolean validStatus() {
        return StringUtils.isNotBlank(componentType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UIComponentReference that = (UIComponentReference) o;
        return Objects.equals(componentType, that.componentType) &&
               Objects.equals(componentData, that.componentData) &&
               Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentType, componentData, timestamp);
    }

    @Override
    public String toString() {
        return "UIComponentReference{" +
                "componentType='" + componentType + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}