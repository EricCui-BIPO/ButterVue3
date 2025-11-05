package com.i0.talent.application.usecase;

import com.i0.talent.application.dto.input.CreateEmployeeInput;
import com.i0.talent.application.dto.output.EmployeeOutput;
import com.i0.talent.domain.entities.Employee;
import com.i0.talent.domain.enums.DataLocation;
import com.i0.talent.domain.enums.EmployeeStatus;
import com.i0.talent.domain.exception.DomainException;
import com.i0.talent.domain.repository.EmployeeRepository;
import com.i0.talent.domain.valueobjects.WorkLocation;
import com.i0.talent.domain.valueobjects.Nationality;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 创建员工UseCase
 *
 * 实现员工创建的业务逻辑
 * 遵循UseCase单一职责原则：一个UseCase只处理一个具体的业务场景
 */
@Component
@RequiredArgsConstructor
public class CreateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    /**
     * 执行员工创建
     *
     * @param input 创建员工参数
     * @return 创建的员工信息
     */
    @Transactional
    public EmployeeOutput execute(CreateEmployeeInput input) {
        // 验证输入参数
        validateInput(input);

        // 检查工号是否已存在
        if (employeeRepository.existsByEmployeeNumber(input.getEmployeeNumber())) {
            throw new DomainException("员工工号已存在: " + input.getEmployeeNumber());
        }

        // 创建基础的Location值对象（在Gateway层会通过ACL适配器获取详细信息）
        WorkLocation workLocation = WorkLocation.of(input.getWorkLocationId(), null, null);
        Nationality nationality = Nationality.of(input.getNationalityId(), null, null);

        // 转换枚举值
        DataLocation dataLocation = DataLocation.valueOf(input.getDataLocation());

        // 创建员工实体
        Employee employee = Employee.create(
                input.getName(),
                input.getEmployeeNumber(),
                workLocation,
                nationality,
                input.getEmail(),
                input.getDepartment(),
                input.getPosition(),
                input.getJoinDate(),
                dataLocation,
                input.getClientId()
        );

        // 保存员工
        Employee savedEmployee = employeeRepository.save(employee);

        // 转换为输出DTO
        return EmployeeOutput.fromEntity(savedEmployee);
    }

    /**
     * 验证输入参数
     */
    private void validateInput(CreateEmployeeInput input) {
        if (input == null) {
            throw new IllegalArgumentException("创建员工参数不能为空");
        }

        // 验证工号格式（示例：只允许字母、数字和下划线）
        if (!input.getEmployeeNumber().matches("^[A-Za-z0-9_]+$")) {
            throw new IllegalArgumentException("员工工号只能包含字母、数字和下划线");
        }

        // 验证入职日期不能是未来日期
        if (input.getJoinDate().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("入职日期不能是未来日期");
        }

        // 验证邮箱格式（额外的业务验证）
        if (!input.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("邮箱地址格式不正确");
        }
    }
}