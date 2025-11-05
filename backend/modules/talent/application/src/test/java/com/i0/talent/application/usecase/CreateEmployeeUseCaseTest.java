package com.i0.talent.application.usecase;

import com.i0.talent.application.dto.input.CreateEmployeeInput;
import com.i0.talent.application.dto.output.EmployeeOutput;
import com.i0.talent.domain.entities.Employee;
import com.i0.talent.domain.enums.DataLocation;
import com.i0.talent.domain.enums.EmployeeStatus;
import com.i0.talent.domain.exception.DomainException;
import com.i0.talent.domain.repository.EmployeeRepository;
import com.i0.talent.domain.valueobjects.Nationality;
import com.i0.talent.domain.valueobjects.WorkLocation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 创建员工UseCase测试
 *
 * 测试员工创建的业务逻辑
 * 遵循TDD开发规范：Red-Green-Refactor
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateEmployeeUseCase测试")
class CreateEmployeeUseCaseTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private CreateEmployeeUseCase createEmployeeUseCase;

    @BeforeEach
    void setUp() {
        createEmployeeUseCase = new CreateEmployeeUseCase(employeeRepository);
    }

    @Test
    @DisplayName("应该成功创建员工")
    void should_CreateEmployee_When_ValidInputProvided() {
        // Given
        CreateEmployeeInput input = CreateEmployeeInput.builder()
                .name("张三")
                .employeeNumber("EMP001")
                .workLocationId("beijing-001")
                .nationalityId("china-001")
                .email("zhangsan@example.com")
                .department("技术部")
                .position("软件工程师")
                .joinDate(LocalDate.now().minusMonths(1))
                .dataLocation("NINGXIA")
                .build();

        Employee mockEmployee = Employee.reconstruct(
                "emp-001",
                input.getName(),
                input.getEmployeeNumber(),
                WorkLocation.of("beijing-001", "北京", "CITY"),
                Nationality.ofCountry("china-001", "中国"),
                input.getEmail(),
                input.getDepartment(),
                input.getPosition(),
                input.getJoinDate(),
                null,
                DataLocation.NINGXIA,
                EmployeeStatus.ACTIVE,
                input.getClientId()
        );

        when(employeeRepository.existsByEmployeeNumber(input.getEmployeeNumber())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);

        // When
        EmployeeOutput result = createEmployeeUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals("emp-001", result.getId());
        assertEquals(input.getName(), result.getName());
        assertEquals(input.getEmployeeNumber(), result.getEmployeeNumber());
        assertEquals(input.getDepartment(), result.getDepartment());
        assertEquals(EmployeeStatus.ACTIVE.name(), result.getStatus());
        assertTrue(result.getActive());

        // 验证Repository调用
        verify(employeeRepository, times(1)).existsByEmployeeNumber(input.getEmployeeNumber());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("应该抛出异常当工号已存在")
    void should_ThrowException_When_EmployeeNumberAlreadyExists() {
        // Given
        CreateEmployeeInput input = CreateEmployeeInput.builder()
                .name("李四")
                .employeeNumber("EMP001")
                .workLocationId("shanghai-001")
                .nationalityId("china-001")
                .email("lisi@example.com")
                .department("市场部")
                .position("市场专员")
                .joinDate(LocalDate.now().minusMonths(2))
                .dataLocation("NINGXIA")
                .build();

        when(employeeRepository.existsByEmployeeNumber(input.getEmployeeNumber())).thenReturn(true);

        // When & Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> createEmployeeUseCase.execute(input)
        );

        assertEquals("员工工号已存在: " + input.getEmployeeNumber(), exception.getMessage());

        verify(employeeRepository, times(1)).existsByEmployeeNumber(input.getEmployeeNumber());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("应该抛出异常当输入为null")
    void should_ThrowException_When_InputIsNull() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createEmployeeUseCase.execute(null)
        );

        assertEquals("创建员工参数不能为空", exception.getMessage());

        verify(employeeRepository, never()).existsByEmployeeNumber(anyString());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("应该抛出异常当工号格式不正确")
    void should_ThrowException_When_EmployeeNumberFormatIsInvalid() {
        // Given
        CreateEmployeeInput input = CreateEmployeeInput.builder()
                .name("王五")
                .employeeNumber("EMP-001") // 包含连字符，不符合格式要求
                .workLocationId("guangzhou-001")
                .nationalityId("china-001")
                .email("wangwu@example.com")
                .department("人事部")
                .position("HR专员")
                .joinDate(LocalDate.now().minusMonths(3))
                .dataLocation("NINGXIA")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createEmployeeUseCase.execute(input)
        );

        assertEquals("员工工号只能包含字母、数字和下划线", exception.getMessage());

        verify(employeeRepository, never()).existsByEmployeeNumber(anyString());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("应该抛出异常当入职日期是未来日期")
    void should_ThrowException_When_JoinDateIsInFuture() {
        // Given
        CreateEmployeeInput input = CreateEmployeeInput.builder()
                .name("赵六")
                .employeeNumber("EMP002")
                .workLocationId("shenzhen-001")
                .nationalityId("china-001")
                .email("zhaoliu@example.com")
                .department("财务部")
                .position("会计")
                .joinDate(LocalDate.now().plusDays(1)) // 未来日期
                .dataLocation("NINGXIA")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createEmployeeUseCase.execute(input)
        );

        assertEquals("入职日期不能是未来日期", exception.getMessage());

        verify(employeeRepository, never()).existsByEmployeeNumber(anyString());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("应该抛出异常当邮箱格式不正确")
    void should_ThrowException_When_EmailFormatIsInvalid() {
        // Given
        CreateEmployeeInput input = CreateEmployeeInput.builder()
                .name("钱七")
                .employeeNumber("EMP003")
                .workLocationId("hangzhou-001")
                .nationalityId("china-001")
                .email("invalid-email") // 无效邮箱格式
                .department("运营部")
                .position("运营专员")
                .joinDate(LocalDate.now().minusMonths(1))
                .dataLocation("NINGXIA")
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createEmployeeUseCase.execute(input)
        );

        assertEquals("邮箱地址格式不正确", exception.getMessage());

        verify(employeeRepository, never()).existsByEmployeeNumber(anyString());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("应该正确创建员工当所有参数都有效")
    void should_CreateEmployeeCorrectly_When_AllParametersAreValid() {
        // Given
        CreateEmployeeInput input = CreateEmployeeInput.builder()
                .name("孙八")
                .employeeNumber("EMP004")
                .workLocationId("chengdu-001")
                .nationalityId("china-001")
                .email("sunba@example.com")
                .department("产品部")
                .position("产品经理")
                .joinDate(LocalDate.now().minusMonths(6))
                .dataLocation("SINGAPORE")
                .build();

        ArgumentCaptor<Employee> employeeCaptor = ArgumentCaptor.forClass(Employee.class);

        when(employeeRepository.existsByEmployeeNumber(input.getEmployeeNumber())).thenReturn(false);

        Employee savedEmployee = Employee.reconstruct(
                "emp-004",
                input.getName(),
                input.getEmployeeNumber(),
                WorkLocation.of("chengdu-001", "成都", "CITY"),
                Nationality.ofCountry("china-001", "中国"),
                input.getEmail(),
                input.getDepartment(),
                input.getPosition(),
                input.getJoinDate(),
                null,
                DataLocation.SINGAPORE,
                EmployeeStatus.ACTIVE,
                input.getClientId()
        );

        when(employeeRepository.save(employeeCaptor.capture())).thenReturn(savedEmployee);

        // When
        EmployeeOutput result = createEmployeeUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals("emp-004", result.getId());
        assertEquals(input.getName(), result.getName());
        assertEquals(DataLocation.SINGAPORE.name(), result.getDataLocation());

        // 验证保存的员工实体
        Employee capturedEmployee = employeeCaptor.getValue();
        assertEquals(input.getName(), capturedEmployee.getName());
        assertEquals(input.getEmployeeNumber(), capturedEmployee.getEmployeeNumber());
        assertEquals(DataLocation.SINGAPORE, capturedEmployee.getDataLocation());
        assertEquals(EmployeeStatus.ACTIVE, capturedEmployee.getStatus());

        verify(employeeRepository, times(1)).existsByEmployeeNumber(input.getEmployeeNumber());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }
}