package com.i0.talent.application.service;

import com.i0.talent.domain.entities.Employee;
import com.i0.talent.domain.enums.DataLocation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 访问控制服务
 *
 * 负责员工敏感信息访问控制和访问日志记录
 */
@Service
@Slf4j
public class AccessControlService {

    /**
     * 检查用户是否有权限访问员工详情
     *
     * @param employee   员工信息
     * @param userId     当前用户ID
     * @param userRole   当前用户角色
     * @param accessTime 访问时间
     * @return 是否允许访问
     */
    public boolean checkAccessPermission(Employee employee, String userId, String userRole, LocalDateTime accessTime) {
        if (employee == null || userId == null) {
            return false;
        }

        // 记录访问日志
        logAccessAttempt(employee.getId(), userId, userRole, accessTime);

        // 基础权限检查
        if (!hasBasicPermission(userRole)) {
            log.warn("用户 {}({}) 无基础权限访问员工 {} 详情", userId, userRole, employee.getId());
            return false;
        }

        // 敏感数据特殊权限检查
        if (isSensitiveData(employee.getDataLocation())) {
            return hasSensitiveDataPermission(userRole);
        }

        return true;
    }

    /**
     * 检查是否具有基础权限
     *
     * @param userRole 用户角色
     * @return 是否具有基础权限
     */
    private boolean hasBasicPermission(String userRole) {
        if (userRole == null) {
            return false;
        }

        // 管理员、HR相关角色、部门经理有基础权限
        switch (userRole.toUpperCase()) {
            case "ADMIN":
            case "HR":
            case "HR_MANAGER":
            case "MANAGER":
                return true;
            default:
                return false;
        }
    }

    /**
     * 检查是否为敏感数据
     *
     * @param dataLocation 数据存储位置
     * @return 是否敏感
     */
    private boolean isSensitiveData(DataLocation dataLocation) {
        if (dataLocation == null) {
            return false;
        }

        // 德国数据通常有更严格的隐私保护要求
        return dataLocation == DataLocation.GERMANY;
    }

    /**
     * 检查是否具有敏感数据访问权限
     *
     * @param userRole 用户角色
     * @return 是否具有权限
     */
    private boolean hasSensitiveDataPermission(String userRole) {
        if (userRole == null) {
            return false;
        }

        // 只有管理员和特定HR角色可以访问敏感数据
        switch (userRole.toUpperCase()) {
            case "ADMIN":
            case "HR_MANAGER":
            case "COMPLIANCE_OFFICER":
                return true;
            default:
                return false;
        }
    }

    /**
     * 记录访问日志
     *
     * @param employeeId 员工ID
     * @param userId     用户ID
     * @param userRole   用户角色
     * @param accessTime 访问时间
     */
    private void logAccessAttempt(String employeeId, String userId, String userRole, LocalDateTime accessTime) {
        if (accessTime == null) {
            accessTime = LocalDateTime.now();
        }

        log.info("员工详情访问记录 - 员工ID: {}, 访问用户: {}, 用户角色: {}, 访问时间: {}",
                employeeId, userId, userRole, accessTime);

        // TODO: 这里可以扩展为将访问日志保存到数据库
        // accessLogRepository.save(createAccessLog(employeeId, userId, userRole, accessTime));
    }

    /**
     * 记录访问成功日志
     *
     * @param employeeId 员工ID
     * @param userId     用户ID
     * @param userRole   用户角色
     */
    public void logAccessSuccess(String employeeId, String userId, String userRole) {
        log.info("员工详情访问成功 - 员工ID: {}, 访问用户: {}, 用户角色: {}, 访问时间: {}",
                employeeId, userId, userRole, LocalDateTime.now());
    }

    /**
     * 记录访问失败日志
     *
     * @param employeeId 员工ID
     * @param userId     用户ID
     * @param userRole   用户角色
     * @param reason     失败原因
     */
    public void logAccessDenied(String employeeId, String userId, String userRole, String reason) {
        log.warn("员工详情访问被拒绝 - 员工ID: {}, 访问用户: {}, 用户角色: {}, 失败原因: {}, 访问时间: {}",
                employeeId, userId, userRole, reason, LocalDateTime.now());
    }

    /**
     * 获取用户角色对员工数据的访问权限描述
     *
     * @param userRole   用户角色
     * @param dataLocation 数据存储位置
     * @return 权限描述
     */
    public String getAccessPermissionDescription(String userRole, DataLocation dataLocation) {
        StringBuilder description = new StringBuilder();

        // 基础权限描述
        if (hasBasicPermission(userRole)) {
            description.append("基础访问权限");
        } else {
            description.append("无基础访问权限");
            return description.toString();
        }

        // 敏感数据权限描述
        if (isSensitiveData(dataLocation)) {
            if (hasSensitiveDataPermission(userRole)) {
                description.append("，敏感数据访问权限");
            } else {
                description.append("，无敏感数据访问权限");
            }
        }

        return description.toString();
    }
}