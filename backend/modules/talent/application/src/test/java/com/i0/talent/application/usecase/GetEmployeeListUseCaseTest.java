package com.i0.talent.application.usecase;

import com.i0.domain.core.pagination.Pageable;
import com.i0.domain.core.pagination.SimplePageable;
import com.i0.talent.application.dto.input.EmployeePageInput;
import com.i0.talent.application.dto.output.EmployeePageOutput;
import com.i0.talent.domain.dto.EmployeePageQuery;
import com.i0.talent.domain.entities.Employee;
import com.i0.talent.domain.enums.DataLocation;
import com.i0.talent.domain.enums.EmployeeStatus;
import com.i0.talent.domain.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

/**
 * 获取员工列表UseCase测试
 *
 * 测试员工列表查询的业务逻辑
 * 遵循TDD开发规范：Red-Green-Refactor
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetEmployeeListUseCase测试")
class GetEmployeeListUseCaseTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private GetEmployeeListUseCase getEmployeeListUseCase;

    @BeforeEach
    void setUp() {
        getEmployeeListUseCase = new GetEmployeeListUseCase(employeeRepository);
    }

    @Test
    @DisplayName("应该成功获取员工列表")
    void should_GetEmployees_When_ValidInputProvided() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(0)
                .size(10)
                .build();

        List<Employee> mockEmployees = createMockEmployees(10); // 数据库分页：第一页只返回10条记录
        Pageable<Employee> mockPage = SimplePageable.of(mockEmployees, 0, 10, 15);

        // 使用ArgumentCaptor来捕获查询参数
        ArgumentCaptor<EmployeePageQuery> queryCaptor = ArgumentCaptor.forClass(EmployeePageQuery.class);
        when(employeeRepository.findEmployeesByPage(queryCaptor.capture())).thenReturn(mockPage);

        // When
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(15, result.getTotal());
        assertEquals(2, result.getTotalPages());
        assertEquals(10, result.getContent().size()); // 数据库分页：第一页返回10条记录
        assertFalse(result.isEmpty());
        assertTrue(result.isFirst());
        assertFalse(result.isLast());

        // 验证查询参数
        EmployeePageQuery capturedQuery = queryCaptor.getValue();
        assertEquals(0, capturedQuery.getPage());
        assertEquals(10, capturedQuery.getSize());
        assertNull(capturedQuery.getKeyword());
        assertNull(capturedQuery.getDepartment());

        verify(employeeRepository, times(1)).findEmployeesByPage(any(EmployeePageQuery.class));
    }

    @Test
    @DisplayName("应该返回空结果当没有员工数据")
    void should_ReturnEmptyResult_When_NoEmployeesExist() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(0)
                .size(10)
                .build();

        Pageable<Employee> mockPage = SimplePageable.of(List.of(), 0, 10, 0);
        when(employeeRepository.findEmployeesByPage(any(EmployeePageQuery.class))).thenReturn(mockPage);

        // When
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(0, result.getTotal());
        assertEquals(0, result.getTotalPages());
        assertTrue(result.isEmpty());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());

        verify(employeeRepository, times(1)).findEmployeesByPage(any(EmployeePageQuery.class));
    }

    @Test
    @DisplayName("应该分页正确当页数超出范围")
    void should_HandleOutOfRangePage_When_PageExceedsTotal() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(5)
                .size(10)
                .build();

        List<Employee> mockEmployees = createMockEmployees(25);
        Pageable<Employee> mockPage = SimplePageable.of(List.of(), 5, 10, 25); // 超出范围的页返回空列表
        when(employeeRepository.findEmployeesByPage(any(EmployeePageQuery.class))).thenReturn(mockPage);

        // When
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getPage());
        assertEquals(10, result.getSize());
        assertEquals(25, result.getTotal());
        assertEquals(3, result.getTotalPages());
        assertTrue(result.isEmpty());
        assertFalse(result.isFirst());
        assertTrue(result.isLast());

        verify(employeeRepository, times(1)).findEmployeesByPage(any(EmployeePageQuery.class));
    }

    @Test
    @DisplayName("应该根据关键词搜索员工")
    void should_SearchEmployees_When_KeywordProvided() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(0)
                .size(10)
                .keyword("张三")
                .build();

        List<Employee> mockEmployees = createMockEmployees(5);
        Pageable<Employee> mockPage = SimplePageable.of(mockEmployees, 0, 10, 5);

        ArgumentCaptor<EmployeePageQuery> queryCaptor = ArgumentCaptor.forClass(EmployeePageQuery.class);
        when(employeeRepository.findEmployeesByPage(queryCaptor.capture())).thenReturn(mockPage);

        // When
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getTotal());
        assertEquals(5, result.getContent().size());

        // 验证查询参数包含关键词
        EmployeePageQuery capturedQuery = queryCaptor.getValue();
        assertEquals("张三", capturedQuery.getKeyword());

        verify(employeeRepository, times(1)).findEmployeesByPage(any(EmployeePageQuery.class));
    }

    @Test
    @DisplayName("应该根据部门筛选员工")
    void should_FilterEmployeesByDepartment_When_DepartmentProvided() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(0)
                .size(10)
                .department("技术部")
                .build();

        List<Employee> mockEmployees = createMockEmployees(8);
        Pageable<Employee> mockPage = SimplePageable.of(mockEmployees, 0, 10, 8);

        ArgumentCaptor<EmployeePageQuery> queryCaptor = ArgumentCaptor.forClass(EmployeePageQuery.class);
        when(employeeRepository.findEmployeesByPage(queryCaptor.capture())).thenReturn(mockPage);

        // When
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(8, result.getTotal());
        assertEquals(8, result.getContent().size());

        // 验证查询参数包含部门
        EmployeePageQuery capturedQuery = queryCaptor.getValue();
        assertEquals("技术部", capturedQuery.getDepartment());

        verify(employeeRepository, times(1)).findEmployeesByPage(any(EmployeePageQuery.class));
    }

    @Test
    @DisplayName("应该查询激活员工")
    void should_GetActiveEmployees_When_ActiveOnlyFlagSet() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(0)
                .size(10)
                .activeOnly(true)
                .build();

        List<Employee> mockEmployees = createMockEmployees(10); // 数据库分页：返回请求的页大小记录数
        Pageable<Employee> mockPage = SimplePageable.of(mockEmployees, 0, 10, 12);

        ArgumentCaptor<EmployeePageQuery> queryCaptor = ArgumentCaptor.forClass(EmployeePageQuery.class);
        when(employeeRepository.findEmployeesByPage(queryCaptor.capture())).thenReturn(mockPage);

        // When
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(12, result.getTotal());
        assertEquals(10, result.getContent().size()); // 数据库分页：返回请求的页大小记录数

        // 验证查询参数设置activeOnly
        EmployeePageQuery capturedQuery = queryCaptor.getValue();
        assertTrue(capturedQuery.getActiveOnly());

        verify(employeeRepository, times(1)).findEmployeesByPage(any(EmployeePageQuery.class));
    }

    @Test
    @DisplayName("应该抛出异常当页码为负数")
    void should_ThrowException_When_PageIsNegative() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(-1)
                .size(10)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getEmployeeListUseCase.execute(input)
        );

        assertEquals("页码不能小于0", exception.getMessage());

        verify(employeeRepository, never()).findAll();
    }

    @Test
    @DisplayName("应该抛出异常当每页大小超出范围")
    void should_ThrowException_When_SizeIsInvalid() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(0)
                .size(0)
                .build();

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> getEmployeeListUseCase.execute(input)
        );

        assertEquals("每页大小必须在1-100之间", exception.getMessage());

        verify(employeeRepository, never()).findAll();
    }

    @Test
    @DisplayName("应该使用默认参数当输入为null")
    void should_UseDefaultParameters_When_InputIsNull() {
        // Given
        List<Employee> mockEmployees = createMockEmployees(5);
        Pageable<Employee> mockPage = SimplePageable.of(mockEmployees, 0, 20, 5);

        ArgumentCaptor<EmployeePageQuery> queryCaptor = ArgumentCaptor.forClass(EmployeePageQuery.class);
        when(employeeRepository.findEmployeesByPage(queryCaptor.capture())).thenReturn(mockPage);

        // When
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(null);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getPage()); // 默认页码
        assertEquals(20, result.getSize()); // 默认每页大小
        assertEquals(5, result.getTotal());

        // 验证查询参数使用默认值
        EmployeePageQuery capturedQuery = queryCaptor.getValue();
        assertEquals(0, capturedQuery.getPage());
        assertEquals(20, capturedQuery.getSize());

        verify(employeeRepository, times(1)).findEmployeesByPage(any(EmployeePageQuery.class));
    }

    @Test
    @DisplayName("应该根据日期范围筛选员工")
    void should_FilterEmployeesByDateRange_When_DateRangeProvided() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(0)
                .size(10)
                .joinDateFrom(LocalDate.now().minusMonths(3))
                .joinDateTo(LocalDate.now())
                .build();

        List<Employee> mockEmployees = createMockEmployees(5);
        Pageable<Employee> mockPage = SimplePageable.of(mockEmployees, 0, 10, 5);

        ArgumentCaptor<EmployeePageQuery> queryCaptor = ArgumentCaptor.forClass(EmployeePageQuery.class);
        when(employeeRepository.findEmployeesByPage(queryCaptor.capture())).thenReturn(mockPage);

        // When
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(5, result.getTotal());

        // 验证查询参数包含日期范围
        EmployeePageQuery capturedQuery = queryCaptor.getValue();
        assertEquals(LocalDate.now().minusMonths(3), capturedQuery.getJoinDateFrom());
        assertEquals(LocalDate.now(), capturedQuery.getJoinDateTo());

        verify(employeeRepository, times(1)).findEmployeesByPage(any(EmployeePageQuery.class));
    }

    @Test
    @DisplayName("应该根据职位筛选员工")
    void should_FilterEmployeesByPosition_When_PositionProvided() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(0)
                .size(10)
                .position("工程师")
                .build();

        List<Employee> mockEmployees = createMockEmployees(3);
        Pageable<Employee> mockPage = SimplePageable.of(mockEmployees, 0, 10, 3);

        ArgumentCaptor<EmployeePageQuery> queryCaptor = ArgumentCaptor.forClass(EmployeePageQuery.class);
        when(employeeRepository.findEmployeesByPage(queryCaptor.capture())).thenReturn(mockPage);

        // When
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(3, result.getTotal());

        // 验证查询参数包含职位
        EmployeePageQuery capturedQuery = queryCaptor.getValue();
        assertEquals("工程师", capturedQuery.getPosition());

        verify(employeeRepository, times(1)).findEmployeesByPage(any(EmployeePageQuery.class));
    }

    @Test
    @DisplayName("应该排除已离职员工")
    void should_ExcludeTerminatedEmployees_When_ExcludeTerminatedFlagSet() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(0)
                .size(10)
                .excludeTerminated(true)
                .build();

        List<Employee> mockEmployees = createMockEmployees(8);
        Pageable<Employee> mockPage = SimplePageable.of(mockEmployees, 0, 10, 8);

        ArgumentCaptor<EmployeePageQuery> queryCaptor = ArgumentCaptor.forClass(EmployeePageQuery.class);
        when(employeeRepository.findEmployeesByPage(queryCaptor.capture())).thenReturn(mockPage);

        // When
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(8, result.getTotal());

        // 验证查询参数设置excludeTerminated
        EmployeePageQuery capturedQuery = queryCaptor.getValue();
        assertTrue(capturedQuery.getExcludeTerminated());

        verify(employeeRepository, times(1)).findEmployeesByPage(any(EmployeePageQuery.class));
    }

    @Test
    @DisplayName("应该根据邮箱域名筛选员工")
    void should_FilterEmployeesByEmailDomain_When_EmailDomainProvided() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(0)
                .size(10)
                .emailDomain("example.com")
                .build();

        List<Employee> mockEmployees = createMockEmployees(4);
        Pageable<Employee> mockPage = SimplePageable.of(mockEmployees, 0, 10, 4);

        ArgumentCaptor<EmployeePageQuery> queryCaptor = ArgumentCaptor.forClass(EmployeePageQuery.class);
        when(employeeRepository.findEmployeesByPage(queryCaptor.capture())).thenReturn(mockPage);

        // When
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(4, result.getTotal());

        // 验证查询参数包含邮箱域名
        EmployeePageQuery capturedQuery = queryCaptor.getValue();
        assertEquals("example.com", capturedQuery.getEmailDomain());

        verify(employeeRepository, times(1)).findEmployeesByPage(any(EmployeePageQuery.class));
    }

    @Test
    @DisplayName("应该根据工号前缀筛选员工")
    void should_FilterEmployeesByEmployeeNumberPrefix_When_PrefixProvided() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(0)
                .size(10)
                .employeeNumberPrefix("EMP")
                .build();

        List<Employee> mockEmployees = createMockEmployees(6);
        Pageable<Employee> mockPage = SimplePageable.of(mockEmployees, 0, 10, 6);

        ArgumentCaptor<EmployeePageQuery> queryCaptor = ArgumentCaptor.forClass(EmployeePageQuery.class);
        when(employeeRepository.findEmployeesByPage(queryCaptor.capture())).thenReturn(mockPage);

        // When
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(6, result.getTotal());

        // 验证查询参数包含工号前缀
        EmployeePageQuery capturedQuery = queryCaptor.getValue();
        assertEquals("EMP", capturedQuery.getEmployeeNumberPrefix());

        verify(employeeRepository, times(1)).findEmployeesByPage(any(EmployeePageQuery.class));
    }

    @Test
    @DisplayName("应该支持自定义排序")
    void should_SortEmployees_When_SortParametersProvided() {
        // Given
        EmployeePageInput input = EmployeePageInput.builder()
                .page(0)
                .size(10)
                .sortField("name")
                .sortDirection("ASC")
                .build();

        List<Employee> mockEmployees = createMockEmployees(7);
        Pageable<Employee> mockPage = SimplePageable.of(mockEmployees, 0, 10, 7);

        ArgumentCaptor<EmployeePageQuery> queryCaptor = ArgumentCaptor.forClass(EmployeePageQuery.class);
        when(employeeRepository.findEmployeesByPage(queryCaptor.capture())).thenReturn(mockPage);

        // When
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(7, result.getTotal());

        // 验证查询参数包含排序信息
        EmployeePageQuery capturedQuery = queryCaptor.getValue();
        assertEquals("name", capturedQuery.getSortField());
        assertEquals("ASC", capturedQuery.getSortDirection());

        verify(employeeRepository, times(1)).findEmployeesByPage(any(EmployeePageQuery.class));
    }

    /**
     * 创建模拟员工数据
     */
    private List<Employee> createMockEmployees(int count) {
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(i -> Employee.reconstruct(
                        "employee-" + i,
                        "员工" + i,
                        "EMP" + String.format("%03d", i),
                        com.i0.talent.domain.valueobjects.WorkLocation.of("beijing-" + i, "北京", "CITY", "CN-11"),
                        com.i0.talent.domain.valueobjects.Nationality.ofCountry("china", "中国"),
                        "employee" + i + "@example.com",
                        "技术部",
                        "工程师",
                        LocalDate.now().minusYears(i),
                        null,
                        DataLocation.NINGXIA,
                        EmployeeStatus.ACTIVE,
                        "client-" + String.format("%03d", i)
                ))
                .collect(java.util.stream.Collectors.toList());
    }
}