package com.i0.agents.application.services;

import okhttp3.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * BigModel API简化测试
 * 针对性测试网络连接和API调用问题
 */
public class BigModelSimpleTest {

    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final String API_KEY = "e71b45242f014e49a93b3cbecded72ef.WFqjTqC7VivUQMRl";

    @Test
    @DisplayName("测试网络连通性")
    void testNetworkConnectivity() {
        System.out.println("=== 测试网络连通性 ===");

        try {
            // 首先测试基础网络连接
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();

            // 发送一个简单的HEAD请求测试连通性
            Request headRequest = new Request.Builder()
                    .url(API_URL)
                    .head()
                    .build();

            try (Response response = client.newCall(headRequest).execute()) {
                System.out.println("HEAD请求状态码: " + response.code());
                System.out.println("HEAD请求响应: " + response.message());

                if (response.code() == 405) {
                    System.out.println("✅ 网络连接正常，服务器可访问（405表示HEAD方法不被支持，但连接成功）");
                } else {
                    System.out.println("⚠️  意外的响应码: " + response.code());
                }
            }

            // 测试POST请求（不包含认证）
            String simpleBody = "{\"model\":\"glm-4\",\"messages\":[{\"role\":\"user\",\"content\":\"test\"}]}";
            Request postRequest = new Request.Builder()
                    .url(API_URL)
                    .post(RequestBody.create(simpleBody, MediaType.get("application/json")))
                    .build();

            try (Response response = client.newCall(postRequest).execute()) {
                System.out.println("POST请求（无认证）状态码: " + response.code());
                System.out.println("POST请求（无认证）响应: " + response.message());

                if (response.body() != null) {
                    String responseBody = response.body().string();
                    System.out.println("POST请求（无认证）响应体: " + responseBody);
                }

                if (response.code() == 401) {
                    System.out.println("✅ API端点正确，需要认证（401 Unauthorized）");
                }
            }

        } catch (Exception e) {
            System.err.println("❌ 网络连接测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("测试带认证的简单请求")
    void testAuthenticatedRequest() {
        System.out.println("=== 测试带认证的简单请求 ===");

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .build();

            String requestBody = "{"
                    + "\"model\": \"glm-4\","
                    + "\"messages\": ["
                    + "  {\"role\": \"user\", \"content\": \"请简单回复：你好\"}"
                    + "],"
                    + "\"stream\": false,"
                    + "\"max_tokens\": 50"
                    + "}";

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                    .build();

            System.out.println("发送认证请求...");
            System.out.println("请求URL: " + API_URL);
            System.out.println("请求体: " + requestBody);

            try (Response response = client.newCall(request).execute()) {
                System.out.println("响应状态码: " + response.code());
                System.out.println("响应消息: " + response.message());

                if (response.isSuccessful()) {
                    System.out.println("✅ 认证请求成功！");

                    if (response.body() != null) {
                        String responseBody = response.body().string();
                        System.out.println("响应体: " + responseBody);

                        // 简单验证响应格式
                        if (responseBody.contains("choices") && responseBody.contains("content")) {
                            System.out.println("✅ 响应格式正确，包含choices和content字段");
                        } else {
                            System.out.println("⚠️  响应格式可能不完整");
                        }
                    }
                } else {
                    System.err.println("❌ 认证请求失败");

                    if (response.body() != null) {
                        String errorBody = response.body().string();
                        System.err.println("错误响应: " + errorBody);

                        // 分析具体错误
                        if (errorBody.contains("invalid_api_key")) {
                            System.err.println("❌ API Key无效");
                        } else if (errorBody.contains("insufficient_quota")) {
                            System.err.println("❌ 账户余额不足");
                        } else if (errorBody.contains("rate_limit")) {
                            System.err.println("❌ 请求频率过高");
                        } else if (errorBody.contains("model_not_found")) {
                            System.err.println("❌ 模型不存在");
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ 认证请求异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("测试流式连接问题排查")
    void testStreamConnectionIssues() {
        System.out.println("=== 测试流式连接问题排查 ===");

        try {
            // 首先尝试非流式请求确认基础功能
            System.out.println("1. 测试非流式请求...");
            testNonStreamRequest();

            // 然后测试流式请求的具体问题
            System.out.println("2. 测试流式请求...");
            testStreamRequestWithDebug();

        } catch (Exception e) {
            System.err.println("❌ 流式连接测试异常: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void testNonStreamRequest() throws Exception {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        String requestBody = "{"
                + "\"model\": \"glm-4\","
                + "\"messages\": ["
                + "  {\"role\": \"user\", \"content\": \"请简单回复：测试\"}"
                + "],"
                + "\"stream\": false"
                + "}";

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("非流式请求状态码: " + response.code());
            if (response.isSuccessful()) {
                System.out.println("✅ 非流式请求成功");
            } else {
                System.out.println("❌ 非流式请求失败: " + response.code());
            }
        }
    }

    private void testStreamRequestWithDebug() throws Exception {
        // 设置较短的超时时间来快速发现问题
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)  // 较短的读取超时
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        String requestBody = "{"
                + "\"model\": \"glm-4\","
                + "\"messages\": ["
                + "  {\"role\": \"user\", \"content\": \"请简单回复：测试流式\"}"
                + "],"
                + "\"stream\": true"
                + "}";

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "text/event-stream")
                .addHeader("Cache-Control", "no-cache")
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .build();

        System.out.println("发送流式请求...");

        try (Response response = client.newCall(request).execute()) {
            System.out.println("流式请求状态码: " + response.code());
            System.out.println("响应头: " + response.headers());

            if (response.isSuccessful()) {
                System.out.println("✅ 流式连接建立成功");

                if (response.body() != null) {
                    String responseBody = response.body().string();
                    System.out.println("流式响应内容: " + responseBody);

                    if (responseBody.contains("data:")) {
                        System.out.println("✅ 收到SSE格式数据");
                    } else {
                        System.out.println("⚠️  响应不是标准的SSE格式");
                    }
                }
            } else {
                System.out.println("❌ 流式连接失败: " + response.code());
                if (response.body() != null) {
                    String errorBody = response.body().string();
                    System.out.println("流式错误响应: " + errorBody);
                }
            }
        } catch (java.net.SocketTimeoutException e) {
            System.out.println("⚠️  流式请求超时: " + e.getMessage());
            System.out.println("这可能是因为服务器不支持流式响应，或者响应时间过长");
        } catch (Exception e) {
            System.out.println("❌ 流式请求异常: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }
}