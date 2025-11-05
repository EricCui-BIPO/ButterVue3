package com.i0.app.integration;

import com.i0.entity.application.dto.input.CreateEntityInput;
import com.i0.entity.application.dto.input.UpdateEntityInput;
import com.i0.entity.application.dto.output.EntityOutput;
import com.i0.entity.domain.valueobjects.EntityType;
import com.i0.entity.gateway.persistence.dataobjects.EntityDO;
import com.i0.entity.gateway.persistence.mappers.EntityMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Entity模块集成测试
 * 测试从Controller到Repository的完整流程
 */
@Transactional
@DisplayName("Entity模块集成测试")
@ActiveProfiles("test")
class EntityIntegrationTest extends BasicIntegrationTest{

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EntityMapper entityMapper;

    private CreateEntityInput createRequest;
    private UpdateEntityInput updateRequest;

    @BeforeEach
    void setUp() {
        // 使用通用的物理删除方法清理Entity相关的测试数据
        clearUpTestData(EntityDO.class);

        createRequest = CreateEntityInput.builder()
                .name("集成测试实体")
                .entityType(EntityType.BIPO_ENTITY)
                .code("INTEGRATION_001")
                .description("集成测试实体描述")
                .build();

        updateRequest = UpdateEntityInput.builder()
                .name("更新后的集成测试实体")
                .description("更新后的描述")
                .build();

        // 创建一个已存在的实体用于测试
        EntityDO existingEntity = EntityDO.builder()
                .id("existing-entity-id")
                .name("已存在的实体")
                .entityType(EntityType.CLIENT_ENTITY.name())
                .code("EXISTING_001")
                .description("已存在的实体描述")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isDeleted(false)
                .build();
        entityMapper.insert(existingEntity);
    }


    @AfterEach
    void tearDown() {
        // 使用通用的物理删除方法清理Entity相关的测试数据
        clearUpTestData(EntityDO.class);
    }

    @Test
    @DisplayName("完整的实体生命周期测试")
    void shouldHandleCompleteEntityLifecycle() throws Exception {
        // 1. 创建实体
        String createResponse = mockMvc.perform(post("/api/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("集成测试实体"))
                .andExpect(jsonPath("$.data.entityType").value("BIPO_ENTITY"))
                .andExpect(jsonPath("$.data.code").value("INTEGRATION_001"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andReturn().getResponse().getContentAsString();

        // 从 ApiResult 中提取实际的实体数据
        com.fasterxml.jackson.databind.JsonNode responseNode = objectMapper.readTree(createResponse);
        EntityOutput createdEntity = objectMapper.treeToValue(responseNode.get("data"), EntityOutput.class);
        String entityId = createdEntity.getId();

        // 2. 根据ID获取实体
        mockMvc.perform(get("/api/v1/entities/{id}", entityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(entityId))
                .andExpect(jsonPath("$.data.name").value("集成测试实体"))
                .andExpect(jsonPath("$.data.entityType").value("BIPO_ENTITY"));

        // 3. 更新实体
        mockMvc.perform(put("/api/v1/entities/{id}", entityId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(entityId))
                .andExpect(jsonPath("$.data.name").value("更新后的集成测试实体"))
                .andExpect(jsonPath("$.data.description").value("更新后的描述"));

        // 4. 停用实体
        mockMvc.perform(put("/api/v1/entities/{id}/deactivate", entityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(entityId))
                .andExpect(jsonPath("$.data.active").value(false));

        // 5. 激活实体
        mockMvc.perform(put("/api/v1/entities/{id}/activate", entityId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(entityId))
                .andExpect(jsonPath("$.data.active").value(true));

        // 6. 删除实体
        mockMvc.perform(delete("/api/v1/entities/{id}", entityId))
                .andExpect(status().isOk());

        // 7. 验证实体已被删除
        mockMvc.perform(get("/api/v1/entities/{id}", entityId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("分页查询功能测试")
    void shouldHandlePaginationQuery() throws Exception {
        // 创建多个测试实体
        for (int i = 1; i <= 15; i++) {
            CreateEntityInput request = CreateEntityInput.builder()
                    .name("分页测试实体" + i)
                    .entityType(i <= 8 ? EntityType.BIPO_ENTITY : EntityType.CLIENT_ENTITY)
                    .code("PAGE_" + String.format("%03d", i))
                    .description("分页测试描述" + i)
                    .build();

            mockMvc.perform(post("/api/v1/entities")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        // 测试第一页
        mockMvc.perform(get("/api/v1/entities")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(10))
                .andExpect(jsonPath("$.data.pagination.page").value(1))
                .andExpect(jsonPath("$.data.pagination.pageSize").value(10))
                .andExpect(jsonPath("$.data.pagination.total").value(16)) // 15 + 1 existing
                .andExpect(jsonPath("$.data.pagination.totalPages").value(2));

        // 测试第二页
        mockMvc.perform(get("/api/v1/entities")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(6))
                .andExpect(jsonPath("$.data.pagination.page").value(2));
    }

    @Test
    @DisplayName("按实体类型过滤查询测试")
    void shouldFilterByEntityType() throws Exception {
        // 创建不同类型的实体
        CreateEntityInput bipoRequest = CreateEntityInput.builder()
                .name("BIPO实体")
                .entityType(EntityType.BIPO_ENTITY)
                .code("BIPO_FILTER_001")
                .description("BIPO实体描述")
                .build();

        CreateEntityInput vendorRequest = CreateEntityInput.builder()
                .name("供应商实体")
                .entityType(EntityType.VENDOR_ENTITY)
                .code("VENDOR_FILTER_001")
                .description("供应商实体描述")
                .build();

        mockMvc.perform(post("/api/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bipoRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vendorRequest)))
                .andExpect(status().isOk());

        // 按BIPO_ENTITY类型查询
        mockMvc.perform(get("/api/v1/entities")
                        .param("page", "0")
                        .param("size", "10")
                        .param("entityType", "BIPO_ENTITY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].entityType").value("BIPO_ENTITY"));

        // 按CLIENT_ENTITY类型查询（包含已存在的实体）
        mockMvc.perform(get("/api/v1/entities")
                        .param("page", "0")
                        .param("size", "10")
                        .param("entityType", "CLIENT_ENTITY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].entityType").value("CLIENT_ENTITY"));
    }

    @Test
    @DisplayName("按名称关键字搜索测试")
    void shouldSearchByNameKeyword() throws Exception {
        // 创建测试实体
        CreateEntityInput request1 = CreateEntityInput.builder()
                .name("搜索测试实体A")
                .entityType(EntityType.BIPO_ENTITY)
                .code("SEARCH_A_001")
                .description("搜索测试描述A")
                .build();

        CreateEntityInput request2 = CreateEntityInput.builder()
                .name("搜索测试实体B")
                .entityType(EntityType.CLIENT_ENTITY)
                .code("SEARCH_B_001")
                .description("搜索测试描述B")
                .build();

        CreateEntityInput request3 = CreateEntityInput.builder()
                .name("其他实体")
                .entityType(EntityType.VENDOR_ENTITY)
                .code("OTHER_001")
                .description("其他描述")
                .build();

        mockMvc.perform(post("/api/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isOk());

        // 按关键字"搜索"查询
        mockMvc.perform(get("/api/v1/entities")
                        .param("page", "0")
                        .param("size", "10")
                        .param("nameKeyword", "搜索"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[*].name", containsInAnyOrder("搜索测试实体A", "搜索测试实体B")));
    }

    @Test
    @DisplayName("只查询激活实体测试")
    void shouldQueryOnlyActiveEntities() throws Exception {
        // 创建实体
        String createResponse = mockMvc.perform(post("/api/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // 从 ApiResult 中提取实际的实体数据
        com.fasterxml.jackson.databind.JsonNode responseNode = objectMapper.readTree(createResponse);
        EntityOutput createdEntity = objectMapper.treeToValue(responseNode.get("data"), EntityOutput.class);
        String entityId = createdEntity.getId();

        // 停用实体
        mockMvc.perform(put("/api/v1/entities/{id}/deactivate", entityId))
                .andExpect(status().isOk());

        // 查询所有实体（包含非激活的）
        mockMvc.perform(get("/api/v1/entities")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(2)); // 1 existing + 1 created

        // 只查询激活的实体
        mockMvc.perform(get("/api/v1/entities")
                        .param("page", "0")
                        .param("size", "10")
                        .param("activeOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1)) // 只有existing entity是激活的
                .andExpect(jsonPath("$.data.content[0].active").value(true));
    }

    @Test
    @DisplayName("获取激活实体列表测试")
    void shouldGetActiveEntitiesList() throws Exception {
        // 创建不同类型的激活实体
        CreateEntityInput bipoRequest = CreateEntityInput.builder()
                .name("激活BIPO实体")
                .entityType(EntityType.BIPO_ENTITY)
                .code("ACTIVE_BIPO_001")
                .description("激活BIPO实体描述")
                .build();

        CreateEntityInput clientRequest = CreateEntityInput.builder()
                .name("激活客户实体")
                .entityType(EntityType.CLIENT_ENTITY)
                .code("ACTIVE_CLIENT_001")
                .description("激活客户实体描述")
                .build();

        mockMvc.perform(post("/api/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bipoRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientRequest)))
                .andExpect(status().isOk());

        // 获取所有激活实体
        mockMvc.perform(get("/api/v1/entities/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3)) // 1 existing + 2 created
                .andExpect(jsonPath("$.data[*].active", everyItem(is(true))));

        // 按类型获取激活实体
        mockMvc.perform(get("/api/v1/entities/active")
                        .param("entityType", "BIPO_ENTITY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].entityType").value("BIPO_ENTITY"));
    }

    @Test
    @DisplayName("获取实体类型列表测试")
    void shouldGetEntityTypes() throws Exception {
        mockMvc.perform(get("/api/v1/entities/types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data", containsInAnyOrder("BIPO_ENTITY", "CLIENT_ENTITY", "VENDOR_ENTITY")));
    }

    @Test
    @DisplayName("业务规则验证测试")
    void shouldValidateBusinessRules() throws Exception {
        // 测试重复名称
        CreateEntityInput duplicateNameRequest = CreateEntityInput.builder()
                .name("已存在的实体") // 与setUp中的实体名称相同
                .entityType(EntityType.BIPO_ENTITY)
                .code("DUPLICATE_NAME_001")
                .description("重复名称测试")
                .build();

        mockMvc.perform(post("/api/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateNameRequest)))
                .andExpect(status().isBadRequest());

        // 测试重复代码
        CreateEntityInput duplicateCodeRequest = CreateEntityInput.builder()
                .name("重复代码实体")
                .entityType(EntityType.BIPO_ENTITY)
                .code("EXISTING_001") // 与setUp中的实体代码相同
                .description("重复代码测试")
                .build();

        mockMvc.perform(post("/api/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateCodeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("参数验证测试")
    void shouldValidateRequestParameters() throws Exception {
        // 测试空名称
        CreateEntityInput emptyNameRequest = CreateEntityInput.builder()
                .name("") // 空名称
                .entityType(EntityType.BIPO_ENTITY)
                .code("EMPTY_NAME_001")
                .description("空名称测试")
                .build();

        mockMvc.perform(post("/api/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyNameRequest)))
                .andExpect(status().isBadRequest());

        // 测试null实体类型
        CreateEntityInput nullTypeRequest = CreateEntityInput.builder()
                .name("空类型实体")
                .entityType(null) // null类型
                .code("NULL_TYPE_001")
                .description("空类型测试")
                .build();

        mockMvc.perform(post("/api/v1/entities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullTypeRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("错误处理测试")
    void shouldHandleErrors() throws Exception {
        // 测试获取不存在的实体
        mockMvc.perform(get("/api/v1/entities/{id}", "non-existent-id"))
                .andExpect(status().isBadRequest());

        // 测试更新不存在的实体
        mockMvc.perform(put("/api/v1/entities/{id}", "non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());

        // 测试删除不存在的实体
        mockMvc.perform(delete("/api/v1/entities/{id}", "non-existent-id"))
                .andExpect(status().isBadRequest());

        // 测试激活不存在的实体
        mockMvc.perform(put("/api/v1/entities/{id}/activate", "non-existent-id"))
                .andExpect(status().isBadRequest());

        // 测试停用不存在的实体
        mockMvc.perform(put("/api/v1/entities/{id}/deactivate", "non-existent-id"))
                .andExpect(status().isBadRequest());
    }
}