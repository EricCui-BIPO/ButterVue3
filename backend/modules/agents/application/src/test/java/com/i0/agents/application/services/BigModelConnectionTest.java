package com.i0.agents.application.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i0.agents.domain.entities.ChatMessage;
import com.i0.agents.domain.enums.MessageRole;
import com.i0.agents.domain.valueobjects.MessageContent;
import com.i0.agents.domain.repositories.ChatMessageRepository;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * BigModel API连接测试
 * 专门用于验证API连接是否正常工作
 */
@ExtendWith(MockitoExtension.class)
class BigModelConnectionTest {

    private static final Logger log = LoggerFactory.getLogger(BigModelConnectionTest.class);

    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final String API_KEY = "c6000b5eb2b24c7abfa27c3053197b2e.O69l79m7xtlBDGUg";

    private OkHttpClient client;
    private ObjectMapper objectMapper;

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @BeforeEach
    void setUp() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("测试BigModel API基础连接")
    void testBasicConnection() {
        log.info("开始测试BigModel API基础连接...");

        try {
            // 构建简单的测试请求
            String requestBody = buildSimpleTestRequestBody();

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                    .build();

            log.info("发送请求到: {}", API_URL);
            log.info("请求体: {}", requestBody);

            // 同步执行请求，便于分析响应
            try (Response response = client.newCall(request).execute()) {
                log.info("响应状态码: {}", response.code());
                log.info("响应消息: {}", response.message());

                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "空响应体";
                    log.info("响应体: {}", responseBody);

                    // 验证响应格式
                    JsonNode jsonResponse = objectMapper.readTree(responseBody);
                    assertThat(jsonResponse.has("choices")).isTrue();
                    assertThat(jsonResponse.get("choices").isArray()).isTrue();

                    log.info("✅ API基础连接测试成功");
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "无错误详情";
                    log.error("❌ API请求失败");
                    log.error("状态码: {}", response.code());
                    log.error("错误响应: {}", errorBody);

                    // 分析具体错误
                    analyzeError(response.code(), errorBody);
                }
            }
        } catch (Exception e) {
            log.error("❌ 连接测试异常", e);
            analyzeException(e);
        }
    }

    @Test
    @DisplayName("测试BigModel流式连接")
    void testStreamConnection() throws Exception {
        log.info("开始测试BigModel API流式连接...");

        CompletableFuture<String> future = new CompletableFuture<>();

        try {
            // 构建流式请求
            String requestBody = buildStreamTestRequestBody();

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                    .build();

            // 创建事件源监听器
            EventSourceListener listener = new EventSourceListener() {
                private final StringBuilder responseBuilder = new StringBuilder();
                private int eventCount = 0;

                @Override
                public void onOpen(EventSource eventSource, Response response) {
                    log.info("✅ SSE连接已建立");
                    log.info("响应状态码: {}", response.code());
                }

                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    eventCount++;
                    log.info("收到事件 #{}: {}", eventCount, data);

                    try {
                        if ("[DONE]".equals(data)) {
                            log.info("✅ 流式响应完成，总共收到 {} 个事件", eventCount);
                            eventSource.cancel();
                            future.complete(responseBuilder.toString());
                            return;
                        }

                        JsonNode jsonNode = objectMapper.readTree(data);
                        JsonNode choices = jsonNode.get("choices");

                        if (choices != null && choices.isArray() && choices.size() > 0) {
                            JsonNode delta = choices.get(0).get("delta");
                            if (delta != null && delta.has("content")) {
                                String content = delta.get("content").asText();
                                responseBuilder.append(content);
                                log.info("累计内容: {}", responseBuilder.toString());
                            }
                        }
                    } catch (Exception e) {
                        log.error("处理SSE事件时发生异常", e);
                    }
                }

                @Override
                public void onClosed(EventSource eventSource) {
                    log.info("SSE连接已关闭");
                    if (!future.isDone()) {
                        future.complete(responseBuilder.toString());
                    }
                }

                @Override
                public void onFailure(EventSource eventSource, Throwable t, Response response) {
                    log.error("❌ SSE连接失败", t);
                    if (response != null) {
                        log.error("响应状态码: {}", response.code());
                        try {
                            String errorBody = response.body() != null ? response.body().string() : "无错误详情";
                            log.error("错误响应: {}", errorBody);
                            analyzeError(response.code(), errorBody);
                        } catch (IOException e) {
                            log.error("读取错误响应失败", e);
                        }
                    }
                    analyzeException(t);
                    future.completeExceptionally(t);
                }
            };

            // 创建事件源
            EventSource eventSource = EventSources.createFactory(client)
                    .newEventSource(request, listener);

            // 等待响应，最多等待30秒
            try {
                String result = future.get(30, TimeUnit.SECONDS);
                log.info("✅ 流式连接测试成功，响应内容: {}", result);
                assertThat(result).isNotEmpty();
            } catch (Exception e) {
                log.error("❌ 流式连接测试超时或失败", e);
                throw e;
            }

        } catch (Exception e) {
            log.error("❌ 流式连接测试异常", e);
            throw e;
        }
    }

    private String buildSimpleTestRequestBody() throws Exception {
        return "{"
                + "\"model\": \"glm-4\","
                + "\"messages\": ["
                + "  {\"role\": \"user\", \"content\": \"你好，请回复一个简短的问候语\"}"
                + "],"
                + "\"stream\": false"
                + "}";
    }

    private String buildStreamTestRequestBody() throws Exception {
        return "{"
                + "\"model\": \"glm-4\","
                + "\"messages\": ["
                + "  {\"role\": \"user\", \"content\": \"你好，请回复一个简短的问候语\"}"
                + "],"
                + "\"stream\": true"
                + "}";
    }

    private void analyzeError(int statusCode, String errorBody) {
        log.error("=== 错误分析 ===");
        log.error("状态码: {}", statusCode);

        switch (statusCode) {
            case 400:
                log.error("400 Bad Request - 请求参数错误，可能的原因：");
                log.error("- API Key格式错误");
                log.error("- 请求体格式错误");
                log.error("- 模型名称错误");
                break;
            case 401:
                log.error("401 Unauthorized - 认证失败，可能的原因：");
                log.error("- API Key无效或已过期");
                log.error("- API Key格式不正确");
                break;
            case 403:
                log.error("403 Forbidden - 权限不足，可能的原因：");
                log.error("- API Key没有访问该模型的权限");
                log.error("- 账户余额不足");
                break;
            case 429:
                log.error("429 Too Many Requests - 请求频率过高");
                break;
            case 500:
                log.error("500 Internal Server Error - 服务器内部错误");
                break;
            case 503:
                log.error("503 Service Unavailable - 服务暂时不可用");
                break;
            default:
                log.error("未知状态码: {}", statusCode);
        }

        if (errorBody.contains("invalid_api_key")) {
            log.error("❌ 确认：API Key无效");
        } else if (errorBody.contains("insufficient_quota")) {
            log.error("❌ 确认：账户余额不足");
        } else if (errorBody.contains("model_not_found")) {
            log.error("❌ 确认：模型不存在或无权访问");
        }
    }

    private void analyzeException(Throwable t) {
        log.error("=== 异常分析 ===");

        if (t instanceof java.net.UnknownHostException) {
            log.error("❌ 网络连接失败：无法解析主机名");
            log.error("解决方案：检查网络连接和DNS设置");
        } else if (t instanceof java.net.SocketTimeoutException) {
            log.error("❌ 连接超时：服务器响应时间过长");
            log.error("解决方案：增加超时时间或检查网络稳定性");
        } else if (t instanceof java.net.ConnectException) {
            log.error("❌ 连接被拒绝：服务器无法访问");
            log.error("解决方案：检查防火墙设置和服务器状态");
        } else if (t instanceof javax.net.ssl.SSLException) {
            log.error("❌ SSL连接失败：证书验证失败");
            log.error("解决方案：检查证书配置或尝试禁用证书验证");
        } else {
            log.error("❌ 未知异常类型: {}", t.getClass().getName());
        }
    }

    @Test
    @DisplayName("验证API Key格式")
    void testApiKeyFormat() {
        log.info("验证API Key格式...");

        assertThat(API_KEY).isNotEmpty();
        assertThat(API_KEY).contains(".");

        String[] parts = API_KEY.split("\\.");
        assertThat(parts).hasSize(2);
        log.info("✅ API Key格式正确");
        log.info("API Key前缀: {}...", parts[0].substring(0, Math.min(10, parts[0].length())));
    }
}