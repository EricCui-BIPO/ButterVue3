package com.i0.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i0.app.integration.config.LocationTestConfiguration;
import com.i0.talent.application.dto.input.EmployeePageInput;
import com.i0.talent.domain.enums.DataLocation;
import com.i0.talent.domain.enums.EmployeeStatus;
import com.i0.talent.domain.repository.EmployeeRepository;
import com.i0.talent.gateway.persistence.dataobjects.EmployeeDO;
import com.i0.talent.gateway.persistence.mappers.EmployeeMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 员工管理集成测试
 *
 * 测试员工管理相关的REST接口
 * 验证完整的HTTP请求-响应流程和数据库操作
 */
@ActiveProfiles("test")
@Transactional
@DisplayName("员工管理集成测试")
@Import(LocationTestConfiguration.class)
class EmployeeIntegrationTest extends BasicIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    private EmployeePageInput pageInput;
    private String existingEmployeeId;

    @BeforeEach
    void setUp() {
        // 清理测试数据，确保每个测试开始时数据库是干净的
        clearUpTestData(EmployeeDO.class);

        pageInput = EmployeePageInput.builder()
                .page(0)
                .size(10)
                .build();

        // 创建一个已存在的员工用于测试
        createTestEmployee("已存在的员工", "技术部", EmployeeStatus.ACTIVE);
    }

    @AfterEach
    void tearDown() {
        // 使用通用的物理删除方法清理Employee相关的测试数据
        clearUpTestData(EmployeeDO.class);
    }

    @Test
    @DisplayName("应该成功获取员工列表")
    void should_GetEmployees_When_ValidRequest() throws Exception {
        // Given - 创建测试数据
        createTestEmployees(5);

        // When & Then - 验证分页查询结果
        mockMvc.perform(get("/api/v1/employees")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(6))
                .andExpect(jsonPath("$.data.pagination.page").value(1))
                .andExpect(jsonPath("$.data.pagination.pageSize").value(10))
                .andExpect(jsonPath("$.data.pagination.total").value(6)) // 5 + 1 existing
                .andExpect(jsonPath("$.data.pagination.totalPages").value(1));
    }

    @Test
    @DisplayName("应该正确分页当数据量超过页面大小")
    void should_HandlePagination_When_DataExceedsPageSize() throws Exception {
        // Given - 创建25条测试数据
        createTestEmployees(25);

        // When & Then - 验证第一页
        mockMvc.perform(get("/api/v1/employees")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(10))
                .andExpect(jsonPath("$.data.pagination.page").value(1))
                .andExpect(jsonPath("$.data.pagination.pageSize").value(10))
                .andExpect(jsonPath("$.data.pagination.total").value(26)) // 25 + 1 existing
                .andExpect(jsonPath("$.data.pagination.totalPages").value(3));

        // When & Then - 验证第二页
        mockMvc.perform(get("/api/v1/employees")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(10))
                .andExpect(jsonPath("$.data.pagination.page").value(2));

        // When & Then - 验证最后一页
        mockMvc.perform(get("/api/v1/employees")
                .param("page", "2")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(6)) // 5 + 1 existing
                .andExpect(jsonPath("$.data.pagination.page").value(3));
    }

    @Test
    @DisplayName("应该根据关键词搜索员工")
    void should_SearchEmployees_When_KeywordProvided() throws Exception {
        // Given - 创建包含特定关键词的测试数据
        createTestEmployee("张三", "技术部");
        createTestEmployee("李四", "市场部");
        createTestEmployee("王五", "技术部");
        createTestEmployee("张六", "人力资源部");

        // When & Then - 验证搜索功能
        mockMvc.perform(get("/api/v1/employees")
                .param("keyword", "张")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.pagination.page").value(1))
                .andExpect(jsonPath("$.data.pagination.total").value(2))
                .andExpect(jsonPath("$.data.pagination.totalPages").value(1));
    }

    @Test
    @DisplayName("应该根据部门筛选员工")
    void should_FilterEmployeesByDepartment_When_DepartmentProvided() throws Exception {
        // Given - 创建不同部门的测试数据
        createTestEmployee("张三", "技术部");
        createTestEmployee("李四", "市场部");
        createTestEmployee("王五", "技术部");
        createTestEmployee("赵六", "技术部");

        // When & Then - 验证部门筛选
        mockMvc.perform(get("/api/v1/employees")
                .param("department", "技术部")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(4))
                .andExpect(jsonPath("$.data.pagination.page").value(1))
                .andExpect(jsonPath("$.data.pagination.total").value(4))
                .andExpect(jsonPath("$.data.pagination.totalPages").value(1));
    }

    @Test
    @DisplayName("应该查询激活员工")
    void should_GetActiveEmployees_When_ActiveOnlyFlagSet() throws Exception {
        // Given - 创建不同状态的测试数据
        createTestEmployee("张三", "技术部", EmployeeStatus.ACTIVE);
        createTestEmployee("李四", "市场部", EmployeeStatus.ACTIVE);
        createTestEmployee("王五", "技术部", EmployeeStatus.TERMINATED);
        createTestEmployee("赵六", "人力资源部", EmployeeStatus.ACTIVE);

        // When & Then - 验证激活员工筛选
        mockMvc.perform(get("/api/v1/employees")
                .param("activeOnly", "true")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(4))
                .andExpect(jsonPath("$.data.pagination.page").value(1))
                .andExpect(jsonPath("$.data.pagination.total").value(4)) // 3 + 1 existing active
                .andExpect(jsonPath("$.data.pagination.totalPages").value(1));
    }

    @Test
    @DisplayName("应该通过专用搜索接口搜索员工")
    void should_SearchEmployees_When_UseSearchEndpoint() throws Exception {
        // Given - 创建测试数据
        createTestEmployee("张三", "技术部");
        createTestEmployee("李四", "市场部");
        createTestEmployee("王五", "技术部");

        // When & Then - 验证专用搜索接口
        mockMvc.perform(get("/api/v1/employees/search")
                .param("keyword", "张三")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("张三"))
                .andExpect(jsonPath("$.data.pagination.page").value(1))
                .andExpect(jsonPath("$.data.pagination.total").value(1))
                .andExpect(jsonPath("$.data.pagination.totalPages").value(1));
    }

    @Test
    @DisplayName("应该通过部门接口查询员工")
    void should_GetEmployeesByDepartment_When_UseDepartmentEndpoint() throws Exception {
        // Given - 创建测试数据
        createTestEmployee("张三", "技术部");
        createTestEmployee("李四", "市场部");
        createTestEmployee("王五", "技术部");

        // When & Then - 验证部门查询接口
        mockMvc.perform(get("/api/v1/employees/by-department")
                .param("department", "技术部")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.pagination.page").value(1))
                .andExpect(jsonPath("$.data.pagination.total").value(3))
                .andExpect(jsonPath("$.data.pagination.totalPages").value(1));
    }

    @Test
    @DisplayName("应该通过工作地点接口查询员工")
    void should_GetEmployeesByWorkLocation_When_UseWorkLocationEndpoint() throws Exception {
        // Given - 创建测试数据
        createTestEmployee("张三", "技术部", "北京", EmployeeStatus.ACTIVE, "client-default");
        createTestEmployee("李四", "市场部", "上海", EmployeeStatus.ACTIVE, "client-default");
        createTestEmployee("王五", "技术部", "北京", EmployeeStatus.ACTIVE, "client-default");

        // When & Then - 验证工作地点查询接口
        mockMvc.perform(get("/api/v1/employees/by-work-location")
                .param("workLocation", "beijing-001")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.pagination.page").value(1))
                .andExpect(jsonPath("$.data.pagination.total").value(3))
                .andExpect(jsonPath("$.data.pagination.totalPages").value(1));
    }

    @Test
    @DisplayName("应该通过激活员工接口查询")
    void should_GetActiveEmployees_When_UseActiveEndpoint() throws Exception {
        // Given - 创建测试数据
        createTestEmployee("张三", "技术部", EmployeeStatus.ACTIVE);
        createTestEmployee("李四", "市场部", EmployeeStatus.TERMINATED);
        createTestEmployee("王五", "技术部", EmployeeStatus.ACTIVE);

        // When & Then - 验证激活员工接口
        mockMvc.perform(get("/api/v1/employees/active")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(3))
                .andExpect(jsonPath("$.data.pagination.page").value(1))
                .andExpect(jsonPath("$.data.pagination.total").value(3)) // 2 + 1 existing active
                .andExpect(jsonPath("$.data.pagination.totalPages").value(1));
    }

    @Test
    @DisplayName("应该通过客户ID接口查询员工")
    void should_GetEmployeesByClientId_When_UseClientEndpoint() throws Exception {
        // Given - 创建测试数据
        createTestEmployee("张三", "技术部", "client-001", EmployeeStatus.ACTIVE, "client-001");
        createTestEmployee("李四", "市场部", "client-002", EmployeeStatus.ACTIVE, "client-default");
        createTestEmployee("王五", "技术部", "client-001", EmployeeStatus.ACTIVE, "client-001");

        // When & Then - 验证客户ID查询接口
        mockMvc.perform(get("/api/v1/employees/by-client")
                .param("clientId", "client-001")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2)) // 2 employees with client-001
                .andExpect(jsonPath("$.data.pagination.page").value(1))
                .andExpect(jsonPath("$.data.pagination.total").value(2))
                .andExpect(jsonPath("$.data.pagination.totalPages").value(1));
    }

    @Test
    @DisplayName("应该使用默认参数当参数未提供")
    void should_UseDefaultParameters_When_ParametersNotProvided() throws Exception {
        // Given - 创建测试数据
        createTestEmployees(5);

        // When & Then - 验证默认参数
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(6))
                .andExpect(jsonPath("$.data.pagination.page").value(1))  // 默认页码从1开始
                .andExpect(jsonPath("$.data.pagination.pageSize").value(20)) // 默认每页大小
                .andExpect(jsonPath("$.data.pagination.total").value(6)) // 5 + 1 existing
                .andExpect(jsonPath("$.data.pagination.totalPages").value(1));
    }

    @Test
    @DisplayName("应该返回空结果当没有员工数据")
    void should_ReturnEmptyResult_When_NoEmployeesExist() throws Exception {
        // Given - 清理所有数据包括已存在的员工
        clearUpTestData(EmployeeDO.class);

        // When & Then - 验证空结果
        mockMvc.perform(get("/api/v1/employees")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(0))
                .andExpect(jsonPath("$.data.pagination.page").value(1))
                .andExpect(jsonPath("$.data.pagination.pageSize").value(10))
                .andExpect(jsonPath("$.data.pagination.total").value(0))
                .andExpect(jsonPath("$.data.pagination.totalPages").value(0));
    }

    /**
     * 创建指定数量的测试员工
     */
    private void createTestEmployees(int count) {
        for (int i = 0; i < count; i++) {
            createTestEmployee("员工" + i, "技术部");
        }
    }

    /**
     * 创建测试员工
     */
    private void createTestEmployee(String name, String department) {
        createTestEmployee(name, department, EmployeeStatus.ACTIVE);
    }

    /**
     * 创建测试员工（指定状态）
     */
    private void createTestEmployee(String name, String department, EmployeeStatus status) {
        createTestEmployee(name, department, "北京", status, "client-default");
    }

    /**
     * 创建测试员工（指定部门和工作地点）
     */
    private void createTestEmployee(String name, String department, String workLocation, EmployeeStatus status, String clientId) {
        createTestEmployee(name, department, workLocation, clientId, status);
    }

    /**
     * 创建测试员工（指定部门、工作地点和客户ID）
     */
    private void createTestEmployee(String name, String department, String workLocation, String clientId, EmployeeStatus status) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomSuffix = String.valueOf((int) (Math.random() * 10000));

        EmployeeDO employeeDO = new EmployeeDO();
        employeeDO.setId("employee-" + timestamp + "-" + randomSuffix);
        employeeDO.setName(name);
        employeeDO.setEmployeeNumber("EMP" + timestamp + randomSuffix);

        // 根据工作地点参数设置不同的ID
        String workLocationId;
        switch (workLocation) {
            case "北京":
                workLocationId = "beijing-001";
                break;
            case "上海":
                workLocationId = "shanghai-001";
                break;
            default:
                workLocationId = "default-001";
                break;
        }
        employeeDO.setWorkLocationId(workLocationId);
        employeeDO.setNationalityId("china-001");
        employeeDO.setClientId(clientId);
        // 修复邮箱格式 - 使用英文字母符合邮箱验证规则
        String emailPrefix = "employee" + timestamp + randomSuffix;
        employeeDO.setEmail(emailPrefix + "@example.com");
        employeeDO.setDepartment(department);
        employeeDO.setPosition("工程师");
        employeeDO.setJoinDate(LocalDate.now().minusYears((int) (Math.random() * 10)).atStartOfDay());
        employeeDO.setLeaveDate(null);
        employeeDO.setDataLocation(DataLocation.NINGXIA.name());
        employeeDO.setStatus(status.name());
        employeeDO.setCreatedAt(LocalDateTime.now());
        employeeDO.setUpdatedAt(LocalDateTime.now());
        employeeDO.setCreator("system");
        employeeDO.setUpdater("system");
        employeeDO.setIsDeleted(false);

        employeeMapper.insert(employeeDO);
    }

    @Test
    @DisplayName("应该成功获取员工详情")
    void should_GetEmployeeDetail_When_ValidEmployeeIdProvided() throws Exception {
        // Given - 创建一个测试员工
        String employeeId = createTestEmployeeAndReturnId("测试员工", "技术部");

        // When & Then - 获取员工详情
        mockMvc.perform(get("/api/v1/employees/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(employeeId))
                .andExpect(jsonPath("$.data.name").value("测试员工"))
                .andExpect(jsonPath("$.data.employeeNumber").exists())
                .andExpect(jsonPath("$.data.email").exists())
                .andExpect(jsonPath("$.data.department").value("技术部"))
                .andExpect(jsonPath("$.data.position").exists())
                .andExpect(jsonPath("$.data.status").exists())
                .andExpect(jsonPath("$.data.active").exists())
                .andExpect(jsonPath("$.data.workYears").exists())
                // 验证 workLocation 是对象结构
                .andExpect(jsonPath("$.data.workLocation").isMap())
                .andExpect(jsonPath("$.data.workLocation.id").exists())
                .andExpect(jsonPath("$.data.workLocation.name").exists())
                .andExpect(jsonPath("$.data.workLocation.type").exists())
                .andExpect(jsonPath("$.data.workLocation.isoCode").exists())
                // 验证 nationality 是对象结构
                .andExpect(jsonPath("$.data.nationality").isMap())
                .andExpect(jsonPath("$.data.nationality.id").exists())
                .andExpect(jsonPath("$.data.nationality.name").exists())
                .andExpect(jsonPath("$.data.nationality.isoCode").exists());
    }

    @Test
    @DisplayName("应该成功删除员工")
    void should_DeleteEmployee_When_ValidEmployeeIdProvided() throws Exception {
        // Given - 创建一个测试员工
        String employeeId = createTestEmployeeAndReturnId("待删除员工", "技术部");

        // When & Then - 验证员工存在
        mockMvc.perform(get("/api/v1/employees/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(employeeId));

        // When & Then - 执行删除操作
        mockMvc.perform(delete("/api/v1/employees/" + employeeId))
                .andExpect(status().isNoContent());

        // When & Then - 验证员工已被删除
        mockMvc.perform(get("/api/v1/employees/" + employeeId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").exists());
    }

    @Test
    @DisplayName("删除不存在的员工应该抛出DomainException")
    void should_ThrowDomainException_When_DeleteNonExistentEmployee() throws Exception {
        // Given
        String nonExistentId = "non-existent-employee-id";

        // When & Then - 验证抛出DomainException并返回400错误
        mockMvc.perform(delete("/api/v1/employees/" + nonExistentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("6001"))
                .andExpect(jsonPath("$.errorMessage").value("员工不存在: " + nonExistentId));
    }

    @Test
    @DisplayName("删除员工后不应该出现在员工列表中")
    void should_NotAppearInEmployeeList_When_EmployeeDeleted() throws Exception {
        // Given - 创建一个测试员工
        String employeeId = createTestEmployeeAndReturnId("列表测试员工", "销售部");

        // When & Then - 验证员工在列表中
        mockMvc.perform(get("/api/v1/employees")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[*].id").value(hasItem(employeeId)));

        // When & Then - 删除员工
        mockMvc.perform(delete("/api/v1/employees/" + employeeId))
                .andExpect(status().isNoContent());

        // When & Then - 验证员工不在列表中
        mockMvc.perform(get("/api/v1/employees")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[?(@.id == '" + employeeId + "')]").doesNotExist());
    }

    @Test
    @DisplayName("应该处理删除员工ID为空的情况")
    void should_HandleError_When_DeleteEmployeeWithEmptyId() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/employees/ "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorMessage").exists());
    }


    /**
     * 创建测试员工并返回其ID
     */
    private String createTestEmployeeAndReturnId(String name, String department) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomSuffix = String.valueOf((int) (Math.random() * 10000));
        String employeeId = "employee-" + timestamp + "-" + randomSuffix;

        EmployeeDO employeeDO = new EmployeeDO();
        employeeDO.setId(employeeId);
        employeeDO.setName(name);
        employeeDO.setEmployeeNumber("EMP" + timestamp + randomSuffix);
        employeeDO.setWorkLocationId("beijing-001");
        employeeDO.setNationalityId("china-001");
        employeeDO.setClientId("client-default");
        employeeDO.setEmail("employee" + timestamp + randomSuffix + "@example.com");
        employeeDO.setDepartment(department);
        employeeDO.setPosition("工程师");
        employeeDO.setJoinDate(LocalDate.now().minusYears((int) (Math.random() * 10)).atStartOfDay());
        employeeDO.setLeaveDate(null);
        employeeDO.setDataLocation(DataLocation.NINGXIA.name());
        employeeDO.setStatus(EmployeeStatus.ACTIVE.name());
        employeeDO.setCreatedAt(LocalDateTime.now());
        employeeDO.setUpdatedAt(LocalDateTime.now());
        employeeDO.setCreator("system");
        employeeDO.setUpdater("system");
        employeeDO.setIsDeleted(false);

        employeeMapper.insert(employeeDO);
        return employeeId;
    }
}