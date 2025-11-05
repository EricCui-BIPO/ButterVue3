package com.i0.app.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.i0.service.application.dto.input.CreateServiceTypeInput;
import com.i0.service.application.dto.input.UpdateServiceTypeInput;
import com.i0.service.application.dto.output.ServiceTypeOutput;
import com.i0.service.domain.valueobjects.ServiceType;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ServiceTypeController集成测试
 * 测试从Controller到UseCase的完整流程
 */
@DisplayName("ServiceTypeController集成测试")
@Slf4j
class ServiceTypeIntegrationTest extends BasicIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private CreateServiceTypeInput createRequest;
    private UpdateServiceTypeInput updateRequest;

    @BeforeEach
    void setUp() {
        createRequest = CreateServiceTypeInput.builder()
                .name("基础测试服务类型")
                .serviceType(ServiceType.EOR)
                .description("这是一个测试服务类型")
                .build();

        updateRequest = UpdateServiceTypeInput.builder()
                .name("更新后的服务类型")
                .description("更新后的描述")
                .build();
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据，确保测试间的数据隔离
        jdbcTemplate.execute("TRUNCATE TABLE service_types");
    }

    @Test
    @DisplayName("完整的服务类型生命周期测试")
    void shouldHandleCompleteServiceTypeLifecycle() throws Exception {
        // 1. 创建服务类型
        String createResponse = mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("基础测试服务类型"))
                .andExpect(jsonPath("$.data.code").value("EOR"))
                .andExpect(jsonPath("$.data.displayName").value("Employer of Record Service"))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.data.outsourcingService").value(true))
                .andExpect(jsonPath("$.data.managementService").value(false))
                .andReturn().getResponse().getContentAsString();

        // 从响应中提取服务类型数据
        JsonNode responseNode = objectMapper.readTree(createResponse);
        ServiceTypeOutput createdServiceType = objectMapper.treeToValue(responseNode.get("data"), ServiceTypeOutput.class);
        String serviceTypeId = createdServiceType.getId();

        // 2. 根据ID获取服务类型
        mockMvc.perform(get("/api/v1/service-types/{id}", serviceTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(serviceTypeId))
                .andExpect(jsonPath("$.data.name").value("基础测试服务类型"))
                .andExpect(jsonPath("$.data.code").value("EOR"));

        // 3. 更新服务类型
        mockMvc.perform(put("/api/v1/service-types/{id}", serviceTypeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(serviceTypeId))
                .andExpect(jsonPath("$.data.name").value("更新后的服务类型"))
                .andExpect(jsonPath("$.data.description").value("更新后的描述"));

        // 4. 停用服务类型
        mockMvc.perform(put("/api/v1/service-types/{id}/deactivate", serviceTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(serviceTypeId))
                .andExpect(jsonPath("$.data.active").value(false));

        // 5. 激活服务类型
        mockMvc.perform(put("/api/v1/service-types/{id}/activate", serviceTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(serviceTypeId))
                .andExpect(jsonPath("$.data.active").value(true));

        // 6. 删除服务类型
        mockMvc.perform(delete("/api/v1/service-types/{id}", serviceTypeId))
                .andExpect(status().isOk());

        // 7. 验证服务类型已被删除
        mockMvc.perform(get("/api/v1/service-types/{id}", serviceTypeId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("创建不同类型的服务类型")
    void shouldCreateDifferentServiceTypes() throws Exception {
        // 测试创建EOR服务类型
        CreateServiceTypeInput eorRequest = CreateServiceTypeInput.builder()
                .name("EOR服务")
                .serviceType(ServiceType.EOR)
                .description("名义雇主服务")
                .build();

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("EOR"))
                .andExpect(jsonPath("$.data.outsourcingService").value(true))
                .andExpect(jsonPath("$.data.managementService").value(false));

        // 测试创建GPO服务类型
        CreateServiceTypeInput gpoRequest = CreateServiceTypeInput.builder()
                .name("GPO服务")
                .serviceType(ServiceType.GPO)
                .description("全球薪酬外包服务")
                .build();

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gpoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("GPO"))
                .andExpect(jsonPath("$.data.outsourcingService").value(true))
                .andExpect(jsonPath("$.data.managementService").value(false));

        // 测试创建CONTRACTOR服务类型
        CreateServiceTypeInput contractorRequest = CreateServiceTypeInput.builder()
                .name("承包商服务")
                .serviceType(ServiceType.CONTRACTOR)
                .description("独立承包商管理服务")
                .build();

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contractorRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("CONTRACTOR"))
                .andExpect(jsonPath("$.data.outsourcingService").value(false))
                .andExpect(jsonPath("$.data.managementService").value(true));

        // 测试创建SELF服务类型
        CreateServiceTypeInput selfRequest = CreateServiceTypeInput.builder()
                .name("自雇服务")
                .serviceType(ServiceType.SELF)
                .description("自雇管理服务")
                .build();

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(selfRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("SELF"))
                .andExpect(jsonPath("$.data.outsourcingService").value(false))
                .andExpect(jsonPath("$.data.managementService").value(true));
    }

    @Test
    @DisplayName("按服务类型过滤查询测试")
    void shouldFilterByServiceType() throws Exception {
        // 创建不同类型的服务类型
        CreateServiceTypeInput eorRequest = CreateServiceTypeInput.builder()
                .name("EOR类型服务")
                .serviceType(ServiceType.EOR)
                .description("EOR服务描述")
                .build();

        CreateServiceTypeInput gpoRequest = CreateServiceTypeInput.builder()
                .name("GPO类型服务")
                .serviceType(ServiceType.GPO)
                .description("GPO服务描述")
                .build();

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eorRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gpoRequest)))
                .andExpect(status().isOk());

        // 按EOR类型查询
        mockMvc.perform(get("/api/v1/service-types")
                        .param("page", "0")
                        .param("size", "10")
                        .param("serviceType", "EOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].code").value("EOR"));

        // 按GPO类型查询
        mockMvc.perform(get("/api/v1/service-types")
                        .param("page", "0")
                        .param("size", "10")
                        .param("serviceType", "GPO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].code").value("GPO"));
    }

    @Test
    @DisplayName("按名称关键字搜索测试")
    void shouldSearchByNameKeyword() throws Exception {
        // 创建测试服务类型
        CreateServiceTypeInput request1 = CreateServiceTypeInput.builder()
                .name("搜索测试服务A")
                .serviceType(ServiceType.EOR)
                .description("搜索测试描述A")
                .build();

        CreateServiceTypeInput request2 = CreateServiceTypeInput.builder()
                .name("搜索测试服务B")
                .serviceType(ServiceType.GPO)
                .description("搜索测试描述B")
                .build();

        CreateServiceTypeInput request3 = CreateServiceTypeInput.builder()
                .name("其他服务")
                .serviceType(ServiceType.CONTRACTOR)
                .description("其他描述")
                .build();

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request3)))
                .andExpect(status().isOk());

        // 按关键字"搜索"查询
        mockMvc.perform(get("/api/v1/service-types")
                        .param("page", "0")
                        .param("size", "10")
                        .param("nameKeyword", "搜索"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[*].name", containsInAnyOrder("搜索测试服务A", "搜索测试服务B")));
    }

    @Test
    @DisplayName("只查询激活服务类型测试")
    void shouldQueryOnlyActiveServiceTypes() throws Exception {
        // 创建服务类型
        String createResponse = mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // 从响应中提取服务类型数据
        JsonNode responseNode = objectMapper.readTree(createResponse);
        ServiceTypeOutput createdServiceType = objectMapper.treeToValue(responseNode.get("data"), ServiceTypeOutput.class);
        String serviceTypeId = createdServiceType.getId();

        // 停用服务类型
        mockMvc.perform(put("/api/v1/service-types/{id}/deactivate", serviceTypeId))
                .andExpect(status().isOk());

        // 查询所有服务类型（包含非激活的）
        mockMvc.perform(get("/api/v1/service-types")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(1));

        // 只查询激活的服务类型
        mockMvc.perform(get("/api/v1/service-types")
                        .param("page", "0")
                        .param("size", "10")
                        .param("activeOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content.length()").value(0));
    }

    @Test
    @DisplayName("获取激活服务类型列表测试")
    void shouldGetActiveServiceTypesList() throws Exception {
        // 创建不同类型的激活服务类型
        CreateServiceTypeInput eorRequest = CreateServiceTypeInput.builder()
                .name("激活EOR服务")
                .serviceType(ServiceType.EOR)
                .description("激活EOR服务描述")
                .build();

        CreateServiceTypeInput gpoRequest = CreateServiceTypeInput.builder()
                .name("激活GPO服务")
                .serviceType(ServiceType.GPO)
                .description("激活GPO服务描述")
                .build();

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eorRequest)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gpoRequest)))
                .andExpect(status().isOk());

        // 获取所有激活服务类型
        mockMvc.perform(get("/api/v1/service-types/active")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content[*].active", everyItem(is(true))));
    }

    @Test
    @DisplayName("参数验证测试")
    void shouldValidateRequestParameters() throws Exception {
        // 测试空名称 - 使用空白字符串
        CreateServiceTypeInput emptyNameRequest = CreateServiceTypeInput.builder()
                .name("   ") // 空白名称，会被@NotBlank验证拦截
                .serviceType(ServiceType.EOR)
                .description("空名称测试")
                .build();

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyNameRequest)))
                .andExpect(status().isBadRequest());

        // 测试null服务类型
        CreateServiceTypeInput nullTypeRequest = CreateServiceTypeInput.builder()
                .name("空类型服务")
                .serviceType(null) // null类型
                .description("空类型测试")
                .build();

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullTypeRequest)))
                .andExpect(status().isBadRequest());

        // 测试名称过长 - 确保超过100字符
        String longName = "这是一个非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常非常长的名称";
        CreateServiceTypeInput longNameRequest = CreateServiceTypeInput.builder()
                .name(longName) // 超过100字符
                .serviceType(ServiceType.EOR)
                .description("长名称测试")
                .build();

        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(longNameRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("错误处理测试")
    void shouldHandleErrors() throws Exception {
        // 测试获取不存在的服务类型
        mockMvc.perform(get("/api/v1/service-types/{id}", "non-existent-id"))
                .andExpect(status().isBadRequest());

        // 测试更新不存在的服务类型
        mockMvc.perform(put("/api/v1/service-types/{id}", "non-existent-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());

        // 测试删除不存在的服务类型
        mockMvc.perform(delete("/api/v1/service-types/{id}", "non-existent-id"))
                .andExpect(status().isBadRequest());

        // 测试激活不存在的服务类型
        mockMvc.perform(put("/api/v1/service-types/{id}/activate", "non-existent-id"))
                .andExpect(status().isBadRequest());

        // 测试停用不存在的服务类型
        mockMvc.perform(put("/api/v1/service-types/{id}/deactivate", "non-existent-id"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("路径参数验证测试")
    void shouldValidatePathParameters() throws Exception {
        // 测试空白ID - 这会触发@NotBlank验证
        mockMvc.perform(get("/api/v1/service-types/{id}", "   "))
                .andExpect(status().isBadRequest());

        // 测试不存在的ID - 这应该返回业务异常（400）
        mockMvc.perform(get("/api/v1/service-types/{id}", "non-existent-id"))
                .andExpect(status().isBadRequest());

        // 测试无效格式的ID - 这应该返回业务异常（400）
        mockMvc.perform(get("/api/v1/service-types/{id}", "invalid-uuid-format"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("激活/停用服务类型业务规则测试")
    void shouldFollowActivationBusinessRules() throws Exception {
        // 创建服务类型
        String createResponse = mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // 从响应中提取服务类型数据
        JsonNode responseNode = objectMapper.readTree(createResponse);
        ServiceTypeOutput createdServiceType = objectMapper.treeToValue(responseNode.get("data"), ServiceTypeOutput.class);
        String serviceTypeId = createdServiceType.getId();

        // 验证初始状态为激活
        mockMvc.perform(get("/api/v1/service-types/{id}", serviceTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.active").value(true));

        // 停用服务类型
        mockMvc.perform(put("/api/v1/service-types/{id}/deactivate", serviceTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.active").value(false));

        // 验证停用后状态
        mockMvc.perform(get("/api/v1/service-types/{id}", serviceTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.active").value(false));

        // 重复停用应该仍然成功但不改变状态
        mockMvc.perform(put("/api/v1/service-types/{id}/deactivate", serviceTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.active").value(false));

        // 激活服务类型
        mockMvc.perform(put("/api/v1/service-types/{id}/activate", serviceTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.active").value(true));

        // 验证激活后状态
        mockMvc.perform(get("/api/v1/service-types/{id}", serviceTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.active").value(true));

        // 重复激活应该仍然成功但不改变状态
        mockMvc.perform(put("/api/v1/service-types/{id}/activate", serviceTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    @DisplayName("逻辑删除后允许重新创建相同编码")
    void shouldRecreateServiceTypeAfterLogicalDelete() throws Exception {
        // 创建一条服务类型
        String createResponse = mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode createdNode = objectMapper.readTree(createResponse).get("data");
        String serviceTypeId = createdNode.get("id").asText();

        // 执行逻辑删除
        mockMvc.perform(delete("/api/v1/service-types/{id}", serviceTypeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 再次使用相同的名称和编码创建，应该成功
        mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value(createRequest.getServiceType().getCode()))
                .andExpect(jsonPath("$.data.name").value(createRequest.getName()));
    }

    @Test
    @DisplayName("响应数据结构验证测试")
    void shouldValidateResponseDataStructure() throws Exception {
        // 创建服务类型并验证响应结构
        String createResponse = mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").exists())
                .andExpect(jsonPath("$.data.code").exists())
                .andExpect(jsonPath("$.data.displayName").exists())
                .andExpect(jsonPath("$.data.description").exists())
                .andExpect(jsonPath("$.data.active").exists())
                .andExpect(jsonPath("$.data.outsourcingService").exists())
                .andExpect(jsonPath("$.data.managementService").exists())
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.updatedAt").exists())
                .andReturn().getResponse().getContentAsString();

        // 验证分页查询响应结构
        mockMvc.perform(get("/api/v1/service-types")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").exists())
                .andExpect(jsonPath("$.data.pagination").exists())
                .andExpect(jsonPath("$.data.pagination.page").exists())
                .andExpect(jsonPath("$.data.pagination.pageSize").exists())
                .andExpect(jsonPath("$.data.pagination.total").exists())
                .andExpect(jsonPath("$.data.pagination.totalPages").exists());
    }

    @Test
    @DisplayName("数据库基本操作测试")
    void shouldBasicDatabaseOperations() throws Exception {
        log.info("开始数据库基本操作测试");

        // 直接查询数据库验证初始状态
        Integer initialCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM service_types", Integer.class);
        log.info("初始数据库中的记录数量: {}", initialCount);

        // 1. 创建一条数据
        log.info("步骤1: 创建一条数据");
        CreateServiceTypeInput request = CreateServiceTypeInput.builder()
                .name("数据库操作测试服务类型")
                .serviceType(ServiceType.EOR)
                .description("测试描述")
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/service-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        String createResponse = createResult.getResponse().getContentAsString();
        log.info("创建响应: {}", createResponse);

        // 直接查询数据库验证数据是否存在
        Integer dbCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM service_types", Integer.class);
        log.info("创建后数据库中的记录数量: {}", dbCount);

        // 2. 分页查询
        log.info("步骤2: 分页查询");
        MvcResult pageResult = mockMvc.perform(get("/api/v1/service-types")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn();

        String pageResponse = pageResult.getResponse().getContentAsString();
        log.info("分页响应: {}", pageResponse);

        // 3. 检查实际数量
        String content = pageResult.getResponse().getContentAsString();
        int actualCount = JsonPath.read(content, "$.data.content.length()");
        log.info("实际查询到的数量: {}", actualCount);

        // 断言至少有一条数据
        assertThat(actualCount).isGreaterThan(0);
    }
}
