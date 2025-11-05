package com.i0.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i0.agents.application.dto.input.CreateChatSessionInput;
import com.i0.agents.application.dto.input.SendMessageInput;
import com.i0.agents.application.dto.output.ChatSessionOutput;
import com.i0.agents.application.dto.output.ChatMessageOutput;
import com.i0.agents.gateway.persistence.dataobjects.ChatSessionDO;
import com.i0.agents.gateway.persistence.dataobjects.ChatMessageDO;
import com.i0.agents.gateway.persistence.mappers.ChatSessionMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Agents模块集成测试
 * 测试从Controller到Repository的完整流程
 */
@Slf4j
@Transactional
@DisplayName("Agents模块集成测试")
@ActiveProfiles("test")
class AgentsIntegrationTest extends BasicIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatSessionMapper chatSessionMapper;

    private CreateChatSessionInput createSessionRequest;
    private SendMessageInput sendMessageRequest;

    @BeforeEach
    void setUp() {
        // 清理测试数据
        clearUpTestData(ChatSessionDO.class, ChatMessageDO.class);

        createSessionRequest = CreateChatSessionInput.builder()
                .title("集成测试聊天会话")
                .userId("test-user-integration")
                .build();

        sendMessageRequest = SendMessageInput.builder()
                .content("你好，我想创建一个实体")
                .userId("test-user-integration")
                .build();

        // 创建一个已存在的会话用于测试
        ChatSessionDO existingSession = ChatSessionDO.builder()
                .id("existing-session-id")
                .title("已存在的会话")
                .userId("test-user-existing")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        chatSessionMapper.insert(existingSession);
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据
        clearUpTestData(ChatSessionDO.class, ChatMessageDO.class);
    }

    @Test
    @DisplayName("完整的聊天会话生命周期测试")
    void shouldHandleCompleteChatSessionLifecycle() throws Exception {
        // 1. 创建聊天会话
        String createSessionResponse = mockMvc.perform(post("/api/v1/chat/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSessionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("集成测试聊天会话"))
                .andExpect(jsonPath("$.data.userId").value("test-user-integration"))
                .andExpect(jsonPath("$.data.status").isNotEmpty())
                .andExpect(jsonPath("$.data.messageCount").value(0))
                .andReturn().getResponse().getContentAsString();

        // 从响应中提取会话ID
        com.fasterxml.jackson.databind.JsonNode sessionResponseNode = objectMapper.readTree(createSessionResponse);
        ChatSessionOutput createdSession = objectMapper.treeToValue(sessionResponseNode.get("data"), ChatSessionOutput.class);
        String sessionId = createdSession.getId();

        // 2. 获取会话详情
        mockMvc.perform(get("/api/v1/chat/sessions/{id}", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(sessionId))
                .andExpect(jsonPath("$.data.title").value("集成测试聊天会话"))
                .andExpect(jsonPath("$.data.userId").value("test-user-integration"));

        // 3. 发送消息
        sendMessageRequest.setSessionId(sessionId);
        mockMvc.perform(post("/api/v1/chat/sessions/{sessionId}/messages", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendMessageRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sessionId").value(sessionId))
                .andExpect(jsonPath("$.data.content").isNotEmpty())
                .andExpect(jsonPath("$.data.role").value("ASSISTANT"))
                .andExpect(jsonPath("$.data.timestamp").isNotEmpty());

        // 4. 获取会话详情（包含消息）
        mockMvc.perform(get("/api/v1/chat/sessions/{id}/with-messages", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(sessionId))
                .andExpect(jsonPath("$.data.messages").isArray())
                .andExpect(jsonPath("$.data.messages.length()").value(2)) // 用户消息 + AI回复
                .andExpect(jsonPath("$.data.messageCount").value(2));

        // 5. 获取会话消息列表
        mockMvc.perform(get("/api/v1/chat/sessions/{sessionId}/messages", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].role").value("USER"))
                .andExpect(jsonPath("$.data[0].content").value("你好，我想创建一个实体"))
                .andExpect(jsonPath("$.data[1].role").value("ASSISTANT"))
                .andExpect(jsonPath("$.data[1].content").isNotEmpty());

        // 6. 删除会话
        mockMvc.perform(delete("/api/v1/chat/sessions/{id}", sessionId))
                .andExpect(status().isOk());

        // 7. 验证会话已被删除
        mockMvc.perform(get("/api/v1/chat/sessions/{id}", sessionId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("查询会话列表测试")
    void shouldHandleSessionListQuery() throws Exception {
        // 创建多个测试会话
        for (int i = 1; i <= 5; i++) {
            CreateChatSessionInput request = CreateChatSessionInput.builder()
                    .title("列表测试会话" + i)
                    .userId("test-user-list")
                    .build();

            mockMvc.perform(post("/api/v1/chat/sessions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        // 测试查询所有会话（不指定用户ID，应该返回空列表）
        mockMvc.perform(get("/api/v1/chat/sessions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));

        // 测试按用户ID查询
        mockMvc.perform(get("/api/v1/chat/sessions")
                        .param("userId", "test-user-list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(5));

      }

    @Test
    @DisplayName("按用户ID过滤查询会话测试")
    void shouldFilterSessionsByUserId() throws Exception {
        // 创建不同用户的会话
        CreateChatSessionInput user1Request = CreateChatSessionInput.builder()
                .title("用户1的会话")
                .userId("filter-user-1")
                .build();

        CreateChatSessionInput user2Request = CreateChatSessionInput.builder()
                .title("用户2的会话")
                .userId("filter-user-2")
                .build();

        mockMvc.perform(post("/api/v1/chat/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1Request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/chat/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2Request)))
                .andExpect(status().isOk());

        // 按用户ID查询
        mockMvc.perform(get("/api/v1/chat/sessions")
                        .param("userId", "filter-user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].userId").value("filter-user-1"));

        // 按已存在用户的ID查询
        mockMvc.perform(get("/api/v1/chat/sessions")
                        .param("userId", "test-user-existing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].userId").value("test-user-existing"));
    }

    @Test
    @DisplayName("消息发送和接收流程测试")
    void shouldSendAndReceiveMessages() throws Exception {
        // 1. 创建会话
        String createSessionResponse = mockMvc.perform(post("/api/v1/chat/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSessionRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        com.fasterxml.jackson.databind.JsonNode sessionResponseNode = objectMapper.readTree(createSessionResponse);
        ChatSessionOutput createdSession = objectMapper.treeToValue(sessionResponseNode.get("data"), ChatSessionOutput.class);
        String sessionId = createdSession.getId();

        // 2. 发送多条消息
        String[] messages = {
                "你好",
                "我想了解系统功能",
                "帮我创建一个实体"
        };

        for (String messageContent : messages) {
            sendMessageRequest.setSessionId(sessionId);
            sendMessageRequest.setContent(messageContent);

            mockMvc.perform(post("/api/v1/chat/sessions/{sessionId}/messages", sessionId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(sendMessageRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.data.sessionId").value(sessionId))
                    .andExpect(jsonPath("$.data.role").value("ASSISTANT"))
                    .andExpect(jsonPath("$.data.content").isNotEmpty());
        }

        // 3. 验证消息历史
        mockMvc.perform(get("/api/v1/chat/sessions/{sessionId}/messages", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(6)) // 3用户消息 + 3AI回复
                .andExpect(jsonPath("$..role", hasItems("USER", "ASSISTANT")));

        // 4. 验证会话消息计数更新
        mockMvc.perform(get("/api/v1/chat/sessions/{id}/with-messages", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.messageCount").value(6));
    }

    @Test
    @DisplayName("业务规则验证测试")
    void shouldValidateBusinessRules() throws Exception {
        // 测试创建会话时标题为空
        CreateChatSessionInput emptyTitleRequest = CreateChatSessionInput.builder()
                .title("") // 空标题
                .userId("test-user-empty")
                .build();

        mockMvc.perform(post("/api/v1/chat/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyTitleRequest)))
                .andExpect(status().isBadRequest());

        // 测试创建会话时用户ID为空
        CreateChatSessionInput emptyUserIdRequest = CreateChatSessionInput.builder()
                .title("测试会话")
                .userId("") // 空用户ID
                .build();

        mockMvc.perform(post("/api/v1/chat/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyUserIdRequest)))
                .andExpect(status().isBadRequest());

        // 测试发送消息到不存在的会话
        SendMessageInput nonExistentSessionRequest = SendMessageInput.builder()
                .sessionId("non-existent-session-id")
                .content("测试消息")
                .userId("test-user")
                .build();

        mockMvc.perform(post("/api/v1/chat/sessions/{sessionId}/messages", "non-existent-session-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentSessionRequest)))
                .andExpect(status().isBadRequest());

        // 测试发送空消息内容
        String createSessionResponse = mockMvc.perform(post("/api/v1/chat/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSessionRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        com.fasterxml.jackson.databind.JsonNode sessionResponseNode = objectMapper.readTree(createSessionResponse);
        ChatSessionOutput createdSession = objectMapper.treeToValue(sessionResponseNode.get("data"), ChatSessionOutput.class);
        String sessionId = createdSession.getId();

        SendMessageInput emptyContentRequest = SendMessageInput.builder()
                .sessionId(sessionId)
                .content("") // 空消息内容
                .userId("test-user")
                .build();

        mockMvc.perform(post("/api/v1/chat/sessions/{sessionId}/messages", sessionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyContentRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("错误处理测试")
    void shouldHandleErrors() throws Exception {
        // 测试获取不存在的会话
        mockMvc.perform(get("/api/v1/chat/sessions/{id}", "non-existent-session-id"))
                .andExpect(status().isBadRequest());

        // 测试获取不存在会话的消息
        mockMvc.perform(get("/api/v1/chat/sessions/{sessionId}/messages", "non-existent-session-id"))
                .andExpect(status().isBadRequest());

        // 测试获取不存在会话的详情（包含消息）
        mockMvc.perform(get("/api/v1/chat/sessions/{id}/with-messages", "non-existent-session-id"))
                .andExpect(status().isBadRequest());

        // 测试删除不存在的会话
        mockMvc.perform(delete("/api/v1/chat/sessions/{id}", "non-existent-session-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("API响应格式验证测试")
    void shouldValidateApiResponseFormat() throws Exception {
        // 测试创建会话的响应格式
        mockMvc.perform(post("/api/v1/chat/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSessionRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        // 测试查询会话列表的响应格式
        mockMvc.perform(get("/api/v1/chat/sessions")
                        .param("userId", "test-user-integration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        // 测试错误响应格式
        mockMvc.perform(get("/api/v1/chat/sessions/{id}", "non-existent-id"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").isNotEmpty())
                .andExpect(jsonPath("$.errorMessage").isNotEmpty())
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}