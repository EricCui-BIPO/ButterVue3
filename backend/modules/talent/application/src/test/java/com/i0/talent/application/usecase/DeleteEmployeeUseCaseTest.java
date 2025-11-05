package com.i0.talent.application.usecase;

import com.i0.talent.domain.exception.DomainException;
import com.i0.talent.domain.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 删除员工UseCase测试
 *
 * 测试员工删除的业务逻辑
 * 遵循TDD开发规范：Red-Green-Refactor
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DeleteEmployeeUseCase测试")
class DeleteEmployeeUseCaseTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private DeleteEmployeeUseCase deleteEmployeeUseCase;

    @BeforeEach
    void setUp() {
        deleteEmployeeUseCase = new DeleteEmployeeUseCase(employeeRepository);
    }

    @Test
    @DisplayName("应该成功删除员工")
    void should_DeleteEmployee_When_ValidEmployeeIdProvided() {
        // Given
        String employeeId = "employee-001";
        when(employeeRepository.existsById(employeeId)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(employeeId);

        // When
        deleteEmployeeUseCase.execute(employeeId);

        // Then
        verify(employeeRepository, times(1)).existsById(employeeId);
        verify(employeeRepository, times(1)).deleteById(employeeId);
    }

    @Test
    @DisplayName("应该抛出异常当员工ID为空")
    void should_ThrowException_When_EmployeeIdIsNull() {
        // Given
        String employeeId = null;

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deleteEmployeeUseCase.execute(employeeId)
        );

        assertEquals("员工ID不能为空", exception.getMessage());
        verify(employeeRepository, never()).existsById(anyString());
        verify(employeeRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("应该抛出异常当员工ID为空字符串")
    void should_ThrowException_When_EmployeeIdIsEmpty() {
        // Given
        String employeeId = "   "; // 空白字符串

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deleteEmployeeUseCase.execute(employeeId)
        );

        assertEquals("员工ID不能为空", exception.getMessage());
        verify(employeeRepository, never()).existsById(anyString());
        verify(employeeRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("应该抛出异常当员工不存在")
    void should_ThrowException_When_EmployeeDoesNotExist() {
        // Given
        String employeeId = "non-existent-employee";
        when(employeeRepository.existsById(employeeId)).thenReturn(false);

        // When & Then
        DomainException exception = assertThrows(
                DomainException.class,
                () -> deleteEmployeeUseCase.execute(employeeId)
        );

        assertEquals("员工不存在: " + employeeId, exception.getMessage());
        verify(employeeRepository, times(1)).existsById(employeeId);
        verify(employeeRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("应该抛出异常当员工ID为空白字符串")
    void should_ThrowException_When_EmployeeIdIsBlank() {
        // Given
        String employeeId = "";

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deleteEmployeeUseCase.execute(employeeId)
        );

        assertEquals("员工ID不能为空", exception.getMessage());
        verify(employeeRepository, never()).existsById(anyString());
        verify(employeeRepository, never()).deleteById(anyString());
    }

    @Test
    @DisplayName("应该正确处理包含空格的员工ID")
    void should_HandleEmployeeIdWithSpaces_When_ValidIdProvided() {
        // Given
        String employeeId = "  employee-001  ";

        // 当前实现不trim输入，所以包含空格的ID被视为有效ID
        when(employeeRepository.existsById(employeeId)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(employeeId);

        // When & Then - 验证当前实现行为（不trim）
        deleteEmployeeUseCase.execute(employeeId);

        // Then - 验证调用参数包含空格
        verify(employeeRepository, times(1)).existsById(employeeId);
        verify(employeeRepository, times(1)).deleteById(employeeId);
    }

    @Test
    @DisplayName("应该验证Repository方法被正确调用")
    void should_VerifyRepositoryMethodCalls_When_EmployeeExists() {
        // Given
        String employeeId = "employee-123";
        when(employeeRepository.existsById(employeeId)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(employeeId);

        // When
        deleteEmployeeUseCase.execute(employeeId);

        // Then
        verify(employeeRepository, times(1)).existsById(employeeId);
        verify(employeeRepository, times(1)).deleteById(employeeId);
        verifyNoMoreInteractions(employeeRepository);
    }
}