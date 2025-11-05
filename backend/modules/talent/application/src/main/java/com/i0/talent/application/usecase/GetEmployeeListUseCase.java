package com.i0.talent.application.usecase;

import com.i0.domain.core.pagination.Pageable;
import com.i0.talent.application.dto.input.EmployeePageInput;
import com.i0.talent.application.dto.output.EmployeePageOutput;
import com.i0.talent.domain.dto.EmployeePageQuery;
import com.i0.talent.domain.entities.Employee;
import com.i0.talent.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 获取员工列表UseCase
 *
 * 实现员工列表查询的业务逻辑
 * 遵循UseCase单一职责原则：一个UseCase只处理一个具体的业务场景
 */
@Component
@RequiredArgsConstructor
public class GetEmployeeListUseCase {

    private final EmployeeRepository employeeRepository;

    /**
     * 执行员工列表查询
     *
     * @param input 分页查询参数
     * @return 分页查询结果
     */
    public Pageable<EmployeePageOutput> execute(EmployeePageInput input) {
        if (input == null) {
            input = EmployeePageInput.builder().build();
        }

        // 验证输入参数
        validateInput(input);

        // 转换为Domain层查询对象
        EmployeePageQuery query = EmployeePageQuery.builder()
                .page(input.getPage())
                .size(input.getSize())
                .keyword(input.getKeyword())
                .department(input.getDepartment())
                .workLocation(input.getWorkLocation())
                .nationality(input.getNationality())
                .status(input.getStatus())
                .dataLocation(input.getDataLocation())
                .activeOnly(input.getActiveOnly())
                .sortField(input.getSortField())
                .sortDirection(input.getSortDirection())
                .joinDateFrom(input.getJoinDateFrom())
                .joinDateTo(input.getJoinDateTo())
                .leaveDateFrom(input.getLeaveDateFrom())
                .leaveDateTo(input.getLeaveDateTo())
                .position(input.getPosition())
                .employeeType(input.getEmployeeType())
                .excludeTerminated(input.getExcludeTerminated())
                .emailDomain(input.getEmailDomain())
                .employeeNumberPrefix(input.getEmployeeNumberPrefix())
                .clientId(input.getClientId())
                .build();

        // 使用数据库级分页查询
        Pageable<Employee> employeePage = employeeRepository.findEmployeesByPage(query);

        // 转换为输出DTO
        return employeePage.map(EmployeePageOutput::fromEntity);
    }

    /**
     * 验证输入参数
     */
    private void validateInput(EmployeePageInput input) {
        if (input.getPage() == null || input.getPage() < 0) {
            throw new IllegalArgumentException("页码不能小于0");
        }

        if (input.getSize() == null || input.getSize() < 1 || input.getSize() > 100) {
            throw new IllegalArgumentException("每页大小必须在1-100之间");
        }

        // 验证日期范围的合理性
        if (input.getJoinDateFrom() != null && input.getJoinDateTo() != null) {
            if (input.getJoinDateFrom().isAfter(input.getJoinDateTo())) {
                throw new IllegalArgumentException("入职开始日期不能晚于结束日期");
            }
        }

        if (input.getLeaveDateFrom() != null && input.getLeaveDateTo() != null) {
            if (input.getLeaveDateFrom().isAfter(input.getLeaveDateTo())) {
                throw new IllegalArgumentException("离职开始日期不能晚于结束日期");
            }
        }

        // 验证排序方向
        if (input.getSortDirection() != null && 
            !input.getSortDirection().equalsIgnoreCase("ASC") && 
            !input.getSortDirection().equalsIgnoreCase("DESC")) {
            throw new IllegalArgumentException("排序方向只能是ASC或DESC");
        }
    }
}