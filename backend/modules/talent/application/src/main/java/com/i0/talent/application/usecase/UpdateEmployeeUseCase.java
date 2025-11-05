package com.i0.talent.application.usecase;

import com.i0.talent.application.dto.input.UpdateEmployeeInput;
import com.i0.talent.application.dto.output.EmployeeOutput;
import com.i0.talent.domain.entities.Employee;
import com.i0.talent.domain.enums.DataLocation;
import com.i0.talent.domain.exception.DomainException;
import com.i0.talent.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 更新员工UseCase
 *
 * 实现员工更新的业务逻辑
 * 遵循UseCase单一职责原则：一个UseCase只处理一个具体的业务场景
 */
@Component
@RequiredArgsConstructor
public class UpdateEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    /**
     * 执行员工更新
     *
     * @param input 更新员工参数
     * @return 更新后的员工信息
     */
    @Transactional
    public EmployeeOutput execute(UpdateEmployeeInput input) {
        // 验证输入参数
        validateInput(input);

        // 查找现有员工
        Employee existingEmployee = employeeRepository.findById(input.getId())
                .orElseThrow(() -> new DomainException("员工不存在: " + input.getId()));

        // 检查工号是否被其他员工使用
        employeeRepository.findByEmployeeNumber(input.getEmployeeNumber())
                .ifPresent(employee -> {
                    if (!employee.getId().equals(input.getId())) {
                        throw new DomainException("员工工号已被其他员工使用: " + input.getEmployeeNumber());
                    }
                });

        // 转换枚举值
        DataLocation dataLocation = DataLocation.valueOf(input.getDataLocation());

        // 更新员工基本信息
        existingEmployee.updateBasicInfo(
                input.getName(),
                input.getEmail(),
                input.getDepartment(),
                input.getPosition()
        );

        // 更新所属客户
        existingEmployee.updateClientId(input.getClientId());

        // 处理离职状态
        if (input.getLeaveDate() != null) {
            if (input.getLeaveDate().isBefore(input.getJoinDate())) {
                throw new IllegalArgumentException("离职日期不能早于入职日期");
            }
            existingEmployee.terminate(input.getLeaveDate());
        }
        // 注意：如果员工已经是激活状态且没有离职日期，不需要调用reinstate
        // reinstate方法只适用于离职员工的重新入职

        // 保存更新
        Employee updatedEmployee = employeeRepository.save(existingEmployee);

        // 转换为输出DTO
        return EmployeeOutput.fromEntity(updatedEmployee);
    }

    /**
     * 验证输入参数
     */
    private void validateInput(UpdateEmployeeInput input) {
        if (input == null) {
            throw new IllegalArgumentException("更新员工参数不能为空");
        }

        if (input.getId() == null || input.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("员工ID不能为空");
        }

        // 验证工号格式
        if (!input.getEmployeeNumber().matches("^[A-Za-z0-9_]+$")) {
            throw new IllegalArgumentException("员工工号只能包含字母、数字和下划线");
        }

        // 验证入职日期不能是未来日期
        if (input.getJoinDate().isAfter(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("入职日期不能是未来日期");
        }

        // 验证邮箱格式
        if (!input.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("邮箱地址格式不正确");
        }

        // 验证离职日期逻辑
        if (input.getLeaveDate() != null) {
            if (input.getLeaveDate().isAfter(java.time.LocalDate.now())) {
                throw new IllegalArgumentException("离职日期不能是未来日期");
            }
        }
    }
}