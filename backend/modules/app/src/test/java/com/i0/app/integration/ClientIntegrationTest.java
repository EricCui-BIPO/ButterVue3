package com.i0.app.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i0.app.integration.config.LocationTestConfiguration;
import com.i0.client.application.dto.input.CreateClientInput;
import com.i0.client.application.dto.input.UpdateClientInput;
import com.i0.client.application.dto.output.ClientDetailOutput;
import com.i0.client.gateway.persistence.dataobjects.ClientDO;
import com.i0.client.gateway.persistence.mappers.ClientMapper;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Client模块集成测试
 * 验证Controller → UseCase → Repository → Database的完整链路
 */
@Slf4j
@Transactional
@ActiveProfiles("test")
@DisplayName("Client模块集成测试")
@Import(LocationTestConfiguration.class)
class ClientIntegrationTest extends BasicIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientMapper clientMapper;

    private CreateClientInput createRequest;
    private UpdateClientInput updateRequest;

    @BeforeEach
    void setUp() {
        clearUpTestData(ClientDO.class);

        createRequest = CreateClientInput.builder()
            .name("集成测试客户")
            .code("CLIENT_INT_001")
            .aliasName("客户别名一号")
            .locationId("country_us")
            .description("Client模块集成测试数据")
            .build();

        updateRequest = UpdateClientInput.builder()
            .name("更新后的集成测试客户")
            .code("CLIENT_INT_001_UPDATED")
            .aliasName("客户别名二号")
            .locationId("country_cn")
            .description("更新后的Client模块集成测试数据")
            .build();

        insertExistingClient();
    }

    @AfterEach
    void tearDown() {
        clearUpTestData(ClientDO.class);
    }

    @Test
    @DisplayName("应完整执行客户生命周期操作")
    void shouldHandleCompleteClientLifecycle() throws Exception {
        String createResponse = mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("集成测试客户"))
            .andExpect(jsonPath("$.data.code").value("CLIENT_INT_001"))
            .andExpect(jsonPath("$.data.aliasName").value("客户别名一号"))
            .andExpect(jsonPath("$.data.active").value(true))
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode responseNode = objectMapper.readTree(createResponse);
        ClientDetailOutput createdClient = objectMapper.treeToValue(responseNode.get("data"), ClientDetailOutput.class);
        String clientId = createdClient.getId();

        mockMvc.perform(get("/api/v1/clients/{id}", clientId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(clientId))
            .andExpect(jsonPath("$.data.locationId").value("country_us"));

        mockMvc.perform(put("/api/v1/clients/{id}", clientId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.name").value("更新后的集成测试客户"))
            .andExpect(jsonPath("$.data.code").value("CLIENT_INT_001_UPDATED"))
            .andExpect(jsonPath("$.data.aliasName").value("客户别名二号"))
            .andExpect(jsonPath("$.data.locationId").value("country_cn"));

        mockMvc.perform(put("/api/v1/clients/{id}/deactivate", clientId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.active").value(false));

        mockMvc.perform(put("/api/v1/clients/{id}/activate", clientId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.active").value(true));

        mockMvc.perform(delete("/api/v1/clients/{id}", clientId))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/clients/{id}", clientId))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("应支持按名称和代码查询客户")
    void shouldRetrieveClientByNameAndCode() throws Exception {
        CreateClientInput request = CreateClientInput.builder()
            .name("名称查询客户")
            .code("NAME_SEARCH_001")
            .locationId("country_us")
            .description("名称查询客户描述")
            .build();

        String createResponse = mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode createNode = objectMapper.readTree(createResponse);
        ClientDetailOutput createdClient = objectMapper.treeToValue(createNode.get("data"), ClientDetailOutput.class);

        mockMvc.perform(get("/api/v1/clients/by-name/{name}", "名称查询客户"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(createdClient.getId()))
            .andExpect(jsonPath("$.data.code").value("NAME_SEARCH_001"));

        mockMvc.perform(get("/api/v1/clients/by-code/{code}", "NAME_SEARCH_001"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(createdClient.getId()))
            .andExpect(jsonPath("$.data.name").value("名称查询客户"));
    }

    @Test
    @DisplayName("应正确处理客户分页和多条件搜索")
    void shouldHandlePaginationAndSearch() throws Exception {
        for (int i = 1; i <= 18; i++) {
            CreateClientInput input = CreateClientInput.builder()
                .name("分页客户" + i)
                .code("PAGE_CLIENT_" + String.format("%03d", i))
                .aliasName("分页别名" + i)
                .locationId(i % 2 == 0 ? "country_us" : "country_cn")
                .description("分页测试客户" + i)
                .build();

            mockMvc.perform(post("/api/v1/clients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk());
        }

        mockMvc.perform(get("/api/v1/clients")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content", hasSize(Matchers.lessThanOrEqualTo(10))))
            .andExpect(jsonPath("$.data.pagination.page").isNumber())
            .andExpect(jsonPath("$.data.pagination.pageSize").value(10))
            .andExpect(jsonPath("$.data.pagination.total").value(Matchers.greaterThanOrEqualTo(19)))
            .andExpect(jsonPath("$.data.pagination.totalPages").value(Matchers.greaterThanOrEqualTo(2)));

        mockMvc.perform(get("/api/v1/clients")
                .param("page", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray());

        String searchResponse = mockMvc.perform(get("/api/v1/clients")
                .param("q", "分页客户1")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode searchNode = objectMapper.readTree(searchResponse);
        JsonNode searchContent = searchNode.path("data").path("content");
        assertThat(searchContent.isArray()).isTrue();
        assertThat(searchContent).anyMatch(item -> item.path("code").asText().contains("PAGE_CLIENT_001"));

        String locationFilterResponse = mockMvc.perform(get("/api/v1/clients")
                .param("locationId", "country_us")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode locationFilterNode = objectMapper.readTree(locationFilterResponse);
        JsonNode locationFilterContent = locationFilterNode.path("data").path("content");
        assertThat(locationFilterContent.isArray()).isTrue();
        assertThat(locationFilterContent).allMatch(item -> "country_us".equals(item.path("locationId").asText()));

        mockMvc.perform(get("/api/v1/clients")
                .param("activeOnly", "true")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content[*].active", everyItem(is(true))));
    }

    @Test
    @DisplayName("应返回激活客户列表及位置过滤结果")
    void shouldGetActiveClientsWithLocationFilter() throws Exception {
        mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isOk());

        String activeResponse = mockMvc.perform(get("/api/v1/clients/active"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode activeNode = objectMapper.readTree(activeResponse);
        JsonNode activeData = activeNode.path("data");
        assertThat(activeData.isArray()).isTrue();
        assertThat(activeData).allMatch(item -> item.path("active").asBoolean());

        String activeByLocationResponse = mockMvc.perform(get("/api/v1/clients/active/by-location/{locationId}", "country_us"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode activeByLocationNode = objectMapper.readTree(activeByLocationResponse);
        JsonNode activeByLocationData = activeByLocationNode.path("data");
        assertThat(activeByLocationData.isArray()).isTrue();
        assertThat(activeByLocationData).allMatch(item -> "country_us".equals(item.path("locationId").asText()));
    }

    @Test
    @DisplayName("应返回可用的客户位置列表")
    void shouldGetClientLocations() throws Exception {
        String response = mockMvc.perform(get("/api/v1/clients/locations"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data", Matchers.notNullValue()))
            .andReturn()
            .getResponse()
            .getContentAsString();

        JsonNode node = objectMapper.readTree(response);
        JsonNode dataArray = node.get("data");
        assertThat(dataArray).isNotNull();
        assertThat(dataArray.isArray()).isTrue();
        assertThat(dataArray.size()).isGreaterThan(0);
        assertThat(dataArray.get(0).get("id")).isNotNull();
        assertThat(dataArray.get(0).get("name")).isNotNull();
    }

    @Test
    @DisplayName("应校验客户创建和更新的业务规则")
    void shouldValidateBusinessRules() throws Exception {
        CreateClientInput duplicateNameRequest = CreateClientInput.builder()
            .name("已存在客户")
            .code("DUPLICATE_NAME")
            .locationId("country_us")
            .description("重复名称测试")
            .build();

        mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateNameRequest)))
            .andExpect(status().isBadRequest());

        CreateClientInput duplicateCodeRequest = CreateClientInput.builder()
            .name("重复代码客户")
            .code("EXISTING_CODE")
            .locationId("country_us")
            .description("重复代码测试")
            .build();

        mockMvc.perform(post("/api/v1/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateCodeRequest)))
            .andExpect(status().isBadRequest());

        UpdateClientInput invalidUpdate = UpdateClientInput.builder()
            .name("")
            .locationId("")
            .build();

        mockMvc.perform(put("/api/v1/clients/{id}", "existing-client-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdate)))
            .andExpect(status().isBadRequest());
    }

    private void insertExistingClient() {
        ClientDO existing = ClientDO.builder()
            .id("existing-client-id")
            .name("已存在客户")
            .code("EXISTING_CODE")
            .aliasName("已存在客户别名")
            .locationId("country_us")
            .description("预置客户用于重复校验")
            .isActive(1)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .version(0L)
            .isDeleted(0)
            .creatorId("system")
            .updaterId("system")
            .creator("system")
            .updater("system")
            .build();

        clientMapper.insert(existing);
    }
}
