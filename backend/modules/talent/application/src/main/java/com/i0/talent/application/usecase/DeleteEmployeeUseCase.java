package com.i0.talent.application.usecase;

import com.i0.talent.domain.exception.DomainException;
import com.i0.talent.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 删除员工UseCase
 *
 * 实现员工删除的业务逻辑
 * 遵循UseCase单一职责原则：一个UseCase只处理一个具体的业务场景
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteEmployeeUseCase {

    private final EmployeeRepository employeeRepository;

    /**
     * 执行员工删除
     *
     * @param employeeId 员工ID
     * @throws DomainException 当员工不存在或无法删除时抛出
     */
    @Transactional
    public void execute(String employeeId) {
        // 验证输入参数
        validateInput(employeeId);

        log.info("开始删除员工 - ID: {}", employeeId);

        // 检查员工是否存在
        if (!employeeRepository.existsById(employeeId)) {
            throw new DomainException("员工不存在: " + employeeId);
        }

        // 执行删除（逻辑删除）
        employeeRepository.deleteById(employeeId);

        log.info("成功删除员工 - ID: {}", employeeId);
    }

    /**
     * 验证输入参数
     *
     * @param employeeId 员工ID
     */
    private void validateInput(String employeeId) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("员工ID不能为空");
        }
    }
}