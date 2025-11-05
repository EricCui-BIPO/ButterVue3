package com.i0.agents.gateway.acl;

import com.i0.agents.domain.valueobjects.BusinessFunction;
import com.i0.client.application.dto.output.ClientDetailOutput;
import com.i0.client.application.usecases.GetClientUseCase;
import com.i0.domain.core.pagination.Pageable;
import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.application.usecases.GetLocationUseCase;
import com.i0.talent.application.dto.input.CreateEmployeeInput;
import com.i0.talent.application.dto.input.EmployeePageInput;
import com.i0.talent.application.dto.output.EmployeeOutput;
import com.i0.talent.application.dto.output.EmployeePageOutput;
import com.i0.talent.application.usecase.CreateEmployeeUseCase;
import com.i0.talent.application.usecase.GetEmployeeListUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

/**
 * 员工函数适配器
 * 处理跨领域调用，统一异常转换和对象映射
 * 遵循ACL防腐层设计原则
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmployeeFunctionAdapter {

    private final CreateEmployeeUseCase createEmployeeUseCase;
    private final GetEmployeeListUseCase getEmployeeListUseCase;
    private final GetLocationUseCase getLocationUseCase;
    private final GetClientUseCase getClientUseCase;
    private final LocationSearchAdapter locationSearchAdapter;

    /**
     * 处理创建员工业务函数调用
     *
     * @param arguments AI传入的参数
     * @return 创建结果
     */
    public BusinessFunction.FunctionCallResult handleCreateEmployee(Map<String, Object> arguments) {
        try {
            log.info("Handling create_employee function call with arguments: {}", arguments);

            // 验证必需参数
            validateCreateEmployeeArguments(arguments);

            // 构建CreateEmployeeInput
            CreateEmployeeInput input = buildCreateEmployeeInput(arguments);

            // 调用Talent模块创建员工
            EmployeeOutput employeeOutput = createEmployeeUseCase.execute(input);

            // 转换为AI友好的返回格式
            Map<String, Object> resultData = new java.util.HashMap<>();
            resultData.put("employeeId", employeeOutput.getId());
            resultData.put("employeeNumber", employeeOutput.getEmployeeNumber());
            resultData.put("name", employeeOutput.getName());
            resultData.put("email", employeeOutput.getEmail());
            resultData.put("department", employeeOutput.getDepartment());
            resultData.put("position", employeeOutput.getPosition());
            resultData.put("workLocationName", employeeOutput.getWorkLocationName());
            resultData.put("nationalityName", employeeOutput.getNationalityName());
            resultData.put("status", employeeOutput.getStatus());

            // UI组件类型标识
            String uiComponentType = "employee-info";

            log.info("Successfully created employee: {}", employeeOutput.getId());
            return BusinessFunction.FunctionCallResult.success("员工创建成功", resultData, uiComponentType);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid arguments for create_employee: {}", e.getMessage());
            return BusinessFunction.FunctionCallResult.failure("参数错误：" + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to create employee", e);
            return BusinessFunction.FunctionCallResult.failure("创建员工失败：" + e.getMessage());
        }
    }

    /**
     * 验证创建员工参数
     */
    private void validateCreateEmployeeArguments(Map<String, Object> arguments) {
        if (arguments == null || arguments.isEmpty()) {
            throw new IllegalArgumentException("创建员工参数不能为空");
        }

        // 验证必需字段
        String[] requiredFields = {"name", "employeeNumber", "email"};
        for (String field : requiredFields) {
            if (!arguments.containsKey(field) || arguments.get(field) == null
                || arguments.get(field).toString().trim().isEmpty()) {
                throw new IllegalArgumentException(field + "不能为空");
            }
        }

        // 验证邮箱格式
        String email = arguments.get("email").toString();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("邮箱地址格式不正确");
        }
    }

    /**
     * 构建CreateEmployeeInput
     */
    private CreateEmployeeInput buildCreateEmployeeInput(Map<String, Object> arguments) {
        // 处理工作地点 - 支持地区名称自动匹配
        String workLocationId = processWorkLocation(
            (String) arguments.get("workLocationId"),
            (String) arguments.get("workLocation")
        );

        // 处理国籍 - 支持地区名称自动匹配
        String nationalityId = processNationality(
            (String) arguments.get("nationalityId"),
            (String) arguments.get("nationality")
        );

        // 验证客户ID
        String clientId = validateAndGetClientId((String) arguments.get("clientId"));

        return CreateEmployeeInput.builder()
            .name((String) arguments.get("name"))
            .employeeNumber((String) arguments.get("employeeNumber"))
            .email((String) arguments.get("email"))
            .department((String) arguments.get("department"))
            .position((String) arguments.get("position"))
            .workLocationId(workLocationId)
            .nationalityId(nationalityId)
            .clientId(clientId)
            .joinDate(arguments.containsKey("joinDate")
                ? LocalDate.parse(arguments.get("joinDate").toString())
                : LocalDate.now())
            .dataLocation(arguments.containsKey("dataLocation")
                ? (String) arguments.get("dataLocation")
                : "NINGXIA")
            .build();
    }

    /**
     * 验证并获取Client ID
     */
    private String validateAndGetClientId(String clientId) {
        if (clientId != null && !clientId.trim().isEmpty()) {
            try {
                ClientDetailOutput client = getClientUseCase.getById(clientId);
                log.debug("Validated client: {} ({})", client.getName(), clientId);
                return clientId;
            } catch (Exception e) {
                log.warn("Invalid client ID: {} - {}", clientId, e.getMessage());
                throw new IllegalArgumentException("客户ID无效: " + clientId);
            }
        }
        return null; // 客户ID可以为空
    }

    /**
     * 处理工作地点 - 支持地区名称自动匹配
     */
    private String processWorkLocation(String workLocationId, String workLocation) {
        // 如果提供了ID，直接验证并使用
        if (workLocationId != null && !workLocationId.trim().isEmpty()) {
            try {
                LocationOutput location = getLocationUseCase.execute(workLocationId);
                log.debug("Using provided work location ID: {} ({})", location.getName(), workLocationId);
                return workLocationId;
            } catch (Exception e) {
                log.warn("Invalid work location ID: {}, trying name match", workLocationId);
            }
        }

        // 如果提供了地区名称，使用自动匹配
        if (workLocation != null && !workLocation.trim().isEmpty()) {
            return locationSearchAdapter.searchWorkLocationId(workLocation);
        }

        // 都没有提供，使用默认值
        return locationSearchAdapter.searchWorkLocationId(null);
    }

    /**
     * 处理国籍 - 支持地区名称自动匹配
     */
    private String processNationality(String nationalityId, String nationality) {
        // 如果提供了ID，直接验证并使用
        if (nationalityId != null && !nationalityId.trim().isEmpty()) {
            try {
                LocationOutput location = getLocationUseCase.execute(nationalityId);
                log.debug("Using provided nationality ID: {} ({})", location.getName(), nationalityId);
                return nationalityId;
            } catch (Exception e) {
                log.warn("Invalid nationality ID: {}, trying name match", nationalityId);
            }
        }

        // 如果提供了国籍名称，使用自动匹配
        if (nationality != null && !nationality.trim().isEmpty()) {
            return locationSearchAdapter.searchNationalityId(nationality);
        }

        // 都没有提供，使用默认值
        return locationSearchAdapter.searchNationalityId(null);
    }

    /**
     * 处理查询员工业务函数调用
     *
     * @param arguments AI传入的参数
     * @return 查询结果
     */
    public BusinessFunction.FunctionCallResult handleFindEmployee(Map<String, Object> arguments) {
        try {
            log.info("Handling find_employee function call with arguments: {}", arguments);

            // 验证必需参数
            validateFindEmployeeArguments(arguments);

            // 构建查询参数
            EmployeePageInput queryInput = buildFindEmployeeQuery(arguments);

            // 调用Talent模块查询员工
            Pageable<EmployeePageOutput> employeePage = getEmployeeListUseCase.execute(queryInput);

            // 处理查询结果
            if (employeePage.getContent().isEmpty()) {
                return BusinessFunction.FunctionCallResult.success("未找到匹配的员工信息",
                    Map.of("message", "根据查询条件未找到相关员工"), "employee-info");
            }

            // 如果只找到一个员工，返回详细信息
            if (employeePage.getContent().size() == 1) {
                EmployeePageOutput employee = employeePage.getContent().get(0);
                Map<String, Object> resultData = convertEmployeeDetailToMap(employee);

                log.info("Successfully found exactly one employee: {}", employee.getId());
                return BusinessFunction.FunctionCallResult.success("找到匹配的员工信息", resultData, "employee-detail");
            }

            // 如果找到多个员工，返回简化信息并引导用户补充查询条件
            Map<String, Object> resultData = new java.util.HashMap<>();
            resultData.put("total", employeePage.getTotal());

            // 生成引导用户补充查询条件的建议
            String suggestions = generateSearchSuggestions(arguments, employeePage.getContent().size());
            log.info("Found {} employees, suggesting to refine search criteria", employeePage.getTotal());
            return BusinessFunction.FunctionCallResult.success(suggestions, resultData);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid arguments for find_employee: {}", e.getMessage());
            return BusinessFunction.FunctionCallResult.failure("参数错误：" + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to find employee", e);
            return BusinessFunction.FunctionCallResult.failure("查询员工失败：" + e.getMessage());
        }
    }

    /**
     * 验证查询员工参数
     */
    private void validateFindEmployeeArguments(Map<String, Object> arguments) {
        if (arguments == null || arguments.isEmpty()) {
            throw new IllegalArgumentException("查询员工参数不能为空");
        }

        // 验证必需字段
        String query = (String) arguments.get("query");
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("查询内容不能为空");
        }

        // 验证查询类型
        String queryType = (String) arguments.get("queryType");
        if (queryType != null && !queryType.trim().isEmpty()) {
            if (!java.util.Arrays.asList("NAME", "EMPLOYEE_NUMBER", "EMAIL").contains(queryType)) {
                throw new IllegalArgumentException("查询类型必须是：NAME、EMPLOYEE_NUMBER或EMAIL");
            }
        }
    }

    /**
     * 构建员工查询参数
     */
    private EmployeePageInput buildFindEmployeeQuery(Map<String, Object> arguments) {
        String query = arguments.get("query").toString().trim();
        String queryType = (String) arguments.get("queryType");

        EmployeePageInput.EmployeePageInputBuilder builder = EmployeePageInput.builder()
            .page(0)
            .size(20); // 限制返回数量，避免结果过多

        // 根据查询类型设置不同的查询条件
        if ("EMPLOYEE_NUMBER".equals(queryType)) {
            builder.employeeNumberPrefix(query);
        } else if ("EMAIL".equals(queryType)) {
            builder.emailDomain(query.contains("@") ? query.substring(query.indexOf("@") + 1) : query);
        } else {
            // 默认按姓名查询，或全字段模糊查询
            builder.keyword(query);
        }

        return builder.build();
    }

    /**
     * 将员工详细信息转换为Map格式
     */
    private Map<String, Object> convertEmployeeDetailToMap(EmployeePageOutput employee) {
        Map<String, Object> employeeMap = new java.util.HashMap<>();
        employeeMap.put("employeeId", employee.getId());
        employeeMap.put("employeeNumber", employee.getEmployeeNumber());
        employeeMap.put("name", employee.getName());
        employeeMap.put("email", employee.getEmail());
        employeeMap.put("department", employee.getDepartment());
        employeeMap.put("position", employee.getPosition());
        employeeMap.put("status", employee.getStatus());
        employeeMap.put("joinDate", employee.getJoinDate());
        employeeMap.put("leaveDate", employee.getLeaveDate());
        employeeMap.put("dataLocation", employee.getDataLocation());
        employeeMap.put("active", employee.getActive());

        // 处理工作地点信息
        if (employee.getWorkLocation() != null) {
            employeeMap.put("workLocationId", employee.getWorkLocation().getId());
            employeeMap.put("workLocationName", employee.getWorkLocation().getName());
            employeeMap.put("workLocationType", employee.getWorkLocation().getType());
        }

        // 处理国籍信息
        if (employee.getNationality() != null) {
            employeeMap.put("nationalityId", employee.getNationality().getId());
            employeeMap.put("nationalityName", employee.getNationality().getName());
            employeeMap.put("nationalityIsoCode", employee.getNationality().getIsoCode());
        }

        return employeeMap;
    }

    /**
     * 将员工摘要信息转换为Map格式（用于列表展示）
     */
    private Map<String, Object> convertEmployeeSummaryToMap(EmployeePageOutput employee) {
        Map<String, Object> employeeMap = new java.util.HashMap<>();
        employeeMap.put("employeeId", employee.getId());
        employeeMap.put("employeeNumber", employee.getEmployeeNumber());
        employeeMap.put("name", employee.getName());
        employeeMap.put("email", employee.getEmail());
        employeeMap.put("department", employee.getDepartment());
        employeeMap.put("position", employee.getPosition());
        employeeMap.put("status", employee.getStatus());

        // 简化的工作地点信息
        if (employee.getWorkLocation() != null) {
            employeeMap.put("workLocation", employee.getWorkLocation().getName());
        }

        return employeeMap;
    }

    /**
     * 生成搜索建议
     */
    private String generateSearchSuggestions(Map<String, Object> arguments, int resultCount) {
        String queryType = (String) arguments.get("queryType");

        StringBuilder suggestions = new StringBuilder();
        suggestions.append("找到").append(resultCount).append("个匹配的员工。");

        if ("NAME".equals(queryType) || queryType == null) {
            suggestions.append("您可以提供更具体的姓名，或指定查询类型：");
            suggestions.append("\n• 如果知道工号，使用查询类型：EMPLOYEE_NUMBER");
            suggestions.append("\n• 如果知道邮箱，使用查询类型：EMAIL");
        } else if ("EMPLOYEE_NUMBER".equals(queryType)) {
            suggestions.append("请提供完整的工号以精确匹配。");
        } else if ("EMAIL".equals(queryType)) {
            suggestions.append("请提供完整的邮箱地址以精确匹配。");
        }

        suggestions.append("\n• 您也可以提供部门、岗位等额外信息来缩小范围。");

        return suggestions.toString();
    }

}