package com.i0.talent.application.usecase;

import com.i0.talent.application.dto.output.EmployeeDetailOutput;
import com.i0.talent.application.service.AccessControlService;
import com.i0.talent.application.service.LegalComplianceService;
import com.i0.talent.domain.entities.Employee;
import com.i0.talent.domain.exception.DomainException;
import com.i0.talent.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 获取员工详情UseCase
 *
 * 实现员工详情查看的业务逻辑
 * 集成访问控制、法律法规提示和访问日志记录
 * 遵循UseCase单一职责原则：一个UseCase只处理一个具体的业务场景
 */
@Component
@RequiredArgsConstructor
public class GetEmployeeDetailUseCase {

    private final EmployeeRepository employeeRepository;
    private final AccessControlService accessControlService;
    private final LegalComplianceService legalComplianceService;

    /**
     * 执行获取员工详情
     *
     * @param employeeId 员工ID
     * @param userId     当前用户ID
     * @param userRole   当前用户角色
     * @return 员工详情信息
     */
    public EmployeeDetailOutput execute(String employeeId, String userId, String userRole) {
        // 验证输入参数
        validateInput(employeeId, userId, userRole);

        // 查找员工
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new DomainException("员工不存在: " + employeeId));

        // 检查访问权限
        LocalDateTime accessTime = LocalDateTime.now();
        boolean hasPermission = accessControlService.checkAccessPermission(employee, userId, userRole, accessTime);

        if (!hasPermission) {
            accessControlService.logAccessDenied(employeeId, userId, userRole, "无访问权限");
            throw new DomainException("您没有权限查看此员工详情");
        }

        // 记录访问成功
        accessControlService.logAccessSuccess(employeeId, userId, userRole);

        // 转换为输出DTO（包含法律法规提示）
        return EmployeeDetailOutput.fromEntity(employee);
    }

    /**
     * 执行获取员工详情（简化版本，无权限控制）
     *
     * @param employeeId 员工ID
     * @return 员工详情信息
     */
    public EmployeeDetailOutput execute(String employeeId) {
        // 验证输入参数
        validateInput(employeeId, null, null);

        // 查找员工
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("员工不存在: " + employeeId));

        // 转换为输出DTO（包含法律法规提示）
        return EmployeeDetailOutput.fromEntity(employee);
    }

    /**
     * 验证输入参数
     *
     * @param employeeId 员工ID
     * @param userId     当前用户ID
     * @param userRole   当前用户角色
     */
    private void validateInput(String employeeId, String userId, String userRole) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("员工ID不能为空");
        }

        // 如果提供用户信息，则进行验证
        if (userId != null && userId.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        if (userRole != null && userRole.trim().isEmpty()) {
            throw new IllegalArgumentException("用户角色不能为空");
        }
    }

    /**
     * 获取访问权限描述
     *
     * @param employeeId 员工ID
     * @param userId     当前用户ID
     * @param userRole   当前用户角色
     * @return 权限描述
     */
    public String getAccessPermissionDescription(String employeeId, String userId, String userRole) {
        validateInput(employeeId, userId, userRole);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new DomainException("员工不存在: " + employeeId));

        return accessControlService.getAccessPermissionDescription(userRole, employee.getDataLocation());
    }

    /**
     * 获取法律法规提示
     *
     * @param employeeId 员工ID
     * @return 法律法规提示
     */
    public String getLegalNotice(String employeeId) {
        validateInput(employeeId, null, null);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new DomainException("员工不存在: " + employeeId));

        return legalComplianceService.getLegalNotice(employee.getDataLocation());
    }
}