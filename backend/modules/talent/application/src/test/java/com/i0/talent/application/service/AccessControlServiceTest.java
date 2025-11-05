package com.i0.talent.application.service;

import com.i0.talent.domain.entities.Employee;
import com.i0.talent.domain.enums.DataLocation;
import com.i0.talent.domain.enums.EmployeeStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 访问控制服务测试
 *
 * 测试员工敏感信息访问控制和访问日志记录
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AccessControlService测试")
class AccessControlServiceTest {

    private AccessControlService accessControlService;

    @BeforeEach
    void setUp() {
        accessControlService = new AccessControlService();
    }

    @Test
    @DisplayName("应该允许管理员访问任何员工数据")
    void should_AllowAccess_When_UserIsAdmin() {
        // Given
        Employee employee = createTestEmployee(DataLocation.GERMANY);
        String userId = "admin-001";
        String userRole = "ADMIN";
        LocalDateTime accessTime = LocalDateTime.now();

        // When
        boolean result = accessControlService.checkAccessPermission(employee, userId, userRole, accessTime);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("应该允许HR访问非敏感数据")
    void should_AllowAccess_When_HrAccessingNonSensitiveData() {
        // Given
        Employee employee = createTestEmployee(DataLocation.NINGXIA); // 非敏感数据
        String userId = "hr-001";
        String userRole = "HR";
        LocalDateTime accessTime = LocalDateTime.now();

        // When
        boolean result = accessControlService.checkAccessPermission(employee, userId, userRole, accessTime);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("应该拒绝普通员工访问敏感数据")
    void should_DenyAccess_When_EmployeeAccessingSensitiveData() {
        // Given
        Employee employee = createTestEmployee(DataLocation.GERMANY); // 敏感数据
        String userId = "emp-001";
        String userRole = "EMPLOYEE";
        LocalDateTime accessTime = LocalDateTime.now();

        // When
        boolean result = accessControlService.checkAccessPermission(employee, userId, userRole, accessTime);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("应该允许HR经理访问敏感数据")
    void should_AllowAccess_When_HrManagerAccessingSensitiveData() {
        // Given
        Employee employee = createTestEmployee(DataLocation.GERMANY); // 敏感数据
        String userId = "hr-manager-001";
        String userRole = "HR_MANAGER";
        LocalDateTime accessTime = LocalDateTime.now();

        // When
        boolean result = accessControlService.checkAccessPermission(employee, userId, userRole, accessTime);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("应该允许部门经理访问基础数据")
    void should_AllowAccess_When_ManagerAccessingBasicData() {
        // Given
        Employee employee = createTestEmployee(DataLocation.SINGAPORE); // 基础数据
        String userId = "manager-001";
        String userRole = "MANAGER";
        LocalDateTime accessTime = LocalDateTime.now();

        // When
        boolean result = accessControlService.checkAccessPermission(employee, userId, userRole, accessTime);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("应该拒绝没有角色的用户访问")
    void should_DenyAccess_When_UserHasNoRole() {
        // Given
        Employee employee = createTestEmployee(DataLocation.NINGXIA);
        String userId = "user-001";
        String userRole = null;
        LocalDateTime accessTime = LocalDateTime.now();

        // When
        boolean result = accessControlService.checkAccessPermission(employee, userId, userRole, accessTime);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("应该拒绝无效用户ID的访问")
    void should_DenyAccess_When_UserIdIsNull() {
        // Given
        Employee employee = createTestEmployee(DataLocation.NINGXIA);
        String userId = null;
        String userRole = "HR";
        LocalDateTime accessTime = LocalDateTime.now();

        // When
        boolean result = accessControlService.checkAccessPermission(employee, userId, userRole, accessTime);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("应该正确识别敏感数据")
    void should_IdentifySensitiveData_When_LocationIsGermany() {
        // Given
        Employee germanEmployee = createTestEmployee(DataLocation.GERMANY);
        Employee singaporeEmployee = createTestEmployee(DataLocation.SINGAPORE);
        Employee ningxiaEmployee = createTestEmployee(DataLocation.NINGXIA);

        // When & Then
        assertTrue(accessControlService.getAccessPermissionDescription("ADMIN", DataLocation.GERMANY).contains("敏感数据"));
        assertFalse(accessControlService.getAccessPermissionDescription("ADMIN", DataLocation.SINGAPORE).contains("敏感数据"));
        assertFalse(accessControlService.getAccessPermissionDescription("ADMIN", DataLocation.NINGXIA).contains("敏感数据"));
    }

    @Test
    @DisplayName("应该提供正确的权限描述")
    void should_ProvideCorrectPermissionDescription() {
        // Given & When & Then
        String adminDesc = accessControlService.getAccessPermissionDescription("ADMIN", DataLocation.GERMANY);
        assertTrue(adminDesc.contains("基础访问权限"));
        assertTrue(adminDesc.contains("敏感数据访问权限"));

        String hrDesc = accessControlService.getAccessPermissionDescription("HR", DataLocation.GERMANY);
        assertTrue(hrDesc.contains("基础访问权限"));
        assertTrue(hrDesc.contains("无敏感数据访问权限"));

        String employeeDesc = accessControlService.getAccessPermissionDescription("EMPLOYEE", DataLocation.NINGXIA);
        assertTrue(employeeDesc.contains("无基础访问权限"));
    }

    @Test
    @DisplayName("应该处理大小写不敏感的角色名称")
    void should_HandleCaseInsensitiveRoleNames() {
        // Given
        Employee employee = createTestEmployee(DataLocation.NINGXIA);
        String userId = "user-001";
        LocalDateTime accessTime = LocalDateTime.now();

        // When & Then
        assertTrue(accessControlService.checkAccessPermission(employee, userId, "admin", accessTime));
        assertTrue(accessControlService.checkAccessPermission(employee, userId, "Admin", accessTime));
        assertTrue(accessControlService.checkAccessPermission(employee, userId, "ADMIN", accessTime));

        assertTrue(accessControlService.checkAccessPermission(employee, userId, "hr", accessTime));
        assertTrue(accessControlService.checkAccessPermission(employee, userId, "Hr", accessTime));
        assertTrue(accessControlService.checkAccessPermission(employee, userId, "HR", accessTime));
    }

    @Test
    @DisplayName("应该处理null员工对象")
    void should_HandleNullEmployee() {
        // Given
        Employee employee = null;
        String userId = "user-001";
        String userRole = "ADMIN";
        LocalDateTime accessTime = LocalDateTime.now();

        // When
        boolean result = accessControlService.checkAccessPermission(employee, userId, userRole, accessTime);

        // Then
        assertFalse(result);
    }

    @Test
    @DisplayName("应该处理默认数据位置")
    void should_HandleDefaultDataLocation() {
        // Given
        Employee employee = Employee.reconstruct(
                "emp-001",
                "测试员工",
                "EMP001",
                com.i0.talent.domain.valueobjects.WorkLocation.of("location-001", "测试地点", "CITY"),
                com.i0.talent.domain.valueobjects.Nationality.ofCountry("country-001", "测试国籍"),
                "test@example.com",
                "测试部门",
                "测试职位",
                LocalDate.now(),
                null,
                DataLocation.NINGXIA, // 使用默认数据位置
                EmployeeStatus.ACTIVE,
                "client-001"
        );

        String userId = "user-001";
        String userRole = "ADMIN";
        LocalDateTime accessTime = LocalDateTime.now();

        // When
        boolean result = accessControlService.checkAccessPermission(employee, userId, userRole, accessTime);

        // Then
        assertTrue(result); // 管理员应该可以访问
    }

    /**
     * 创建测试员工对象
     *
     * @param dataLocation 数据存储位置
     * @return 测试员工对象
     */
    private Employee createTestEmployee(DataLocation dataLocation) {
        return Employee.reconstruct(
                "emp-001",
                "测试员工",
                "EMP001",
                com.i0.talent.domain.valueobjects.WorkLocation.of("location-001", "测试地点", "CITY"),
                com.i0.talent.domain.valueobjects.Nationality.ofCountry("country-001", "测试国籍"),
                "test@example.com",
                "测试部门",
                "测试职位",
                LocalDate.now().minusYears(1),
                null,
                dataLocation,
                EmployeeStatus.ACTIVE,
                "client-001"
        );
    }
}