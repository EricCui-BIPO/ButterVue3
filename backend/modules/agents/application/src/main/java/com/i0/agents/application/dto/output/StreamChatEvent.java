package com.i0.agents.application.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 流式聊天事件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreamChatEvent {

    private String eventType;  // status, message, error, completed, ui_component
    private String contentType; // user, assistant, system
    private String content;
    private Object uiComponent;
    private Map<String, Object> data;
    private String timestamp;
    private String sessionId;

    public static StreamChatEvent status(String content, String sessionId) {
        return StreamChatEvent.builder()
                .eventType("status")
                .content(content)
                .sessionId(sessionId)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }

    public static StreamChatEvent message(String contentType, String content, String sessionId) {
        return StreamChatEvent.builder()
                .eventType("message")
                .contentType(contentType)
                .content(content)
                .sessionId(sessionId)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }

    public static StreamChatEvent error(String error, String sessionId) {
        return StreamChatEvent.builder()
                .eventType("error")
                .content(error)
                .sessionId(sessionId)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }

    public static StreamChatEvent completed(String sessionId) {
        return StreamChatEvent.builder()
                .eventType("completed")
                .content("completed")
                .sessionId(sessionId)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }

    public static StreamChatEvent uiComponent(Object uiComponent, java.util.Map<String, Object> data, String sessionId) {
        return StreamChatEvent.builder()
                .eventType("ui_component")
                .content("UI组件渲染")
                .uiComponent(uiComponent)
                .data(data)
                .sessionId(sessionId)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"type\":\"").append(eventType).append("\"");
        if (contentType != null) {
            json.append(",\"contentType\":\"").append(contentType).append("\"");
        }
        json.append(",\"content\":\"").append(escapeJson(content)).append("\"");

        // 添加UI组件信息
        if (uiComponent != null) {
            json.append(",\"uiComponent\":").append(objectToJson(uiComponent));
        }

        // 添加数据信息
        if (data != null) {
            json.append(",\"data\":").append(objectToJson(data));
        }

        json.append(",\"sessionId\":\"").append(sessionId).append("\"");
        json.append(",\"timestamp\":\"").append(timestamp).append("\"");
        json.append("}");
        return json.toString();
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String objectToJson(Object obj) {
        if (obj == null) {
            return "null";
        }

        // 简单的对象到JSON转换（实际项目中建议使用Jackson等库）
        try {
            // 如果是String类型
            if (obj instanceof String) {
                return "\"" + escapeJson((String) obj) + "\"";
            }

            // 如果是Map类型
            if (obj instanceof Map) {
                StringBuilder json = new StringBuilder();
                json.append("{");
                Map<?, ?> map = (Map<?, ?>) obj;
                boolean first = true;
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (!first) {
                        json.append(",");
                    }
                    json.append("\"").append(entry.getKey()).append("\":")
                        .append(objectToJson(entry.getValue()));
                    first = false;
                }
                json.append("}");
                return json.toString();
            }

            // 如果是List类型
            if (obj instanceof java.util.List) {
                StringBuilder json = new StringBuilder();
                json.append("[");
                java.util.List<?> list = (java.util.List<?>) obj;
                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        json.append(",");
                    }
                    json.append(objectToJson(list.get(i)));
                }
                json.append("]");
                return json.toString();
            }

            // 其他类型直接转换为字符串
            return "\"" + escapeJson(obj.toString()) + "\"";
        } catch (Exception e) {
            return "\"object\"";
        }
    }
}