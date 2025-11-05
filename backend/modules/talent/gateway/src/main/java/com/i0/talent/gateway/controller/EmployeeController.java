package com.i0.talent.gateway.controller;

import com.i0.domain.core.pagination.Pageable;
import com.i0.talent.application.dto.input.CreateEmployeeInput;
import com.i0.talent.application.dto.input.EmployeePageInput;
import com.i0.talent.application.dto.input.UpdateEmployeeInput;
import com.i0.talent.application.dto.output.EmployeeDetailOutput;
import com.i0.talent.application.dto.output.EmployeeOutput;
import com.i0.talent.application.dto.output.EmployeePageOutput;
import com.i0.talent.application.usecase.CreateEmployeeUseCase;
import com.i0.talent.application.usecase.DeleteEmployeeUseCase;
import com.i0.talent.application.usecase.GetEmployeeDetailUseCase;
import com.i0.talent.application.usecase.GetEmployeeListUseCase;
import com.i0.talent.application.usecase.UpdateEmployeeUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 员工管理控制器
 *
 * 提供员工相关的REST接口
 * 遵循Gateway层规范，只负责HTTP层面的处理
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Validated
public class EmployeeController {

    private final GetEmployeeListUseCase getEmployeeListUseCase;
    private final GetEmployeeDetailUseCase getEmployeeDetailUseCase;
    private final CreateEmployeeUseCase createEmployeeUseCase;
    private final UpdateEmployeeUseCase updateEmployeeUseCase;
    private final DeleteEmployeeUseCase deleteEmployeeUseCase;

    /**
     * 获取员工列表（分页）
     *
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @param department 部门筛选
     * @param workLocation 工作地点筛选
     * @param nationality 国籍筛选
     * @param status 员工状态筛选
     * @param dataLocation 数据存储位置筛选
     * @param activeOnly 是否只查询激活的员工
     * @return 分页查询结果
     */
    @GetMapping
    public Pageable<EmployeePageOutput> getEmployees(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String workLocation,
            @RequestParam(required = false) String nationality,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dataLocation,
            @RequestParam(required = false) Boolean activeOnly) {

        log.info("Getting employees list - page: {}, size: {}, keyword: {}, department: {}, workLocation: {}, nationality: {}, status: {}, dataLocation: {}, activeOnly: {}",
                page, size, keyword, department, workLocation, nationality, status, dataLocation, activeOnly);

        // 构建查询参数
        EmployeePageInput input = EmployeePageInput.builder()
                .page(page)
                .size(size)
                .keyword(keyword)
                .department(department)
                .workLocation(workLocation)
                .nationality(nationality)
                .status(status)
                .dataLocation(dataLocation)
                .activeOnly(activeOnly)
                .build();

        // 执行查询
        Pageable<EmployeePageOutput> result = getEmployeeListUseCase.execute(input);

        log.info("Retrieved {} employees (page {} of {}, total {})",
                result.getContent().size(),
                result.getPage() + 1,
                result.getTotalPages(),
                result.getTotal());

        return result;
    }

    /**
     * 搜索员工
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 分页搜索结果
     */
    @GetMapping("/search")
    public Pageable<EmployeePageOutput> searchEmployees(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("Searching employees with keyword: {}, page: {}, size: {}", keyword, page, size);

        EmployeePageInput input = EmployeePageInput.builder()
                .page(page)
                .size(size)
                .keyword(keyword)
                .build();

        return getEmployeeListUseCase.execute(input);
    }

    /**
     * 根据部门查询员工
     *
     * @param department 部门名称
     * @param page 页码
     * @param size 每页大小
     * @return 分页查询结果
     */
    @GetMapping("/by-department")
    public Pageable<EmployeePageOutput> getEmployeesByDepartment(
            @RequestParam String department,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("Getting employees by department: {}, page: {}, size: {}", department, page, size);

        EmployeePageInput input = EmployeePageInput.builder()
                .page(page)
                .size(size)
                .department(department)
                .build();

        return getEmployeeListUseCase.execute(input);
    }

    /**
     * 根据工作地点查询员工
     *
     * @param workLocation 工作地点
     * @param page 页码
     * @param size 每页大小
     * @return 分页查询结果
     */
    @GetMapping("/by-work-location")
    public Pageable<EmployeePageOutput> getEmployeesByWorkLocation(
            @RequestParam String workLocation,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("Getting employees by work location: {}, page: {}, size: {}", workLocation, page, size);

        EmployeePageInput input = EmployeePageInput.builder()
                .page(page)
                .size(size)
                .workLocation(workLocation)
                .build();

        return getEmployeeListUseCase.execute(input);
    }

    /**
     * 查询激活员工
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页查询结果
     */
    @GetMapping("/active")
    public Pageable<EmployeePageOutput> getActiveEmployees(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("Getting active employees, page: {}, size: {}", page, size);

        EmployeePageInput input = EmployeePageInput.builder()
                .page(page)
                .size(size)
                .activeOnly(true)
                .build();

        return getEmployeeListUseCase.execute(input);
    }

    /**
     * 根据客户ID查询员工
     *
     * @param clientId 客户ID
     * @param page 页码
     * @param size 每页大小
     * @return 分页查询结果
     */
    @GetMapping("/by-client")
    public Pageable<EmployeePageOutput> getEmployeesByClientId(
            @RequestParam String clientId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        log.info("Getting employees by client ID: {}, page: {}, size: {}", clientId, page, size);

        EmployeePageInput input = EmployeePageInput.builder()
                .page(page)
                .size(size)
                .clientId(clientId)
                .build();

        return getEmployeeListUseCase.execute(input);
    }

    /**
     * 创建员工
     *
     * @param input 创建员工参数
     * @return 创建的员工信息
     */
    @PostMapping
    public EmployeeOutput createEmployee(@Valid @RequestBody CreateEmployeeInput input) {
        log.info("Creating new employee - name: {}, employeeNumber: {}, department: {}",
                input.getName(), input.getEmployeeNumber(), input.getDepartment());

        EmployeeOutput result = createEmployeeUseCase.execute(input);

        log.info("Successfully created employee with ID: {}", result.getId());

        return result;
    }

    /**
     * 更新员工信息
     *
     * @param id 员工ID
     * @param input 更新员工参数
     * @return 更新后的员工信息
     */
    @PutMapping("/{id}")
    public EmployeeOutput updateEmployee(
            @PathVariable String id,
            @Valid @RequestBody UpdateEmployeeInput input) {

        // 确保ID一致性
        input.setId(id);

        log.info("Updating employee - ID: {}, name: {}, employeeNumber: {}, department: {}",
                id, input.getName(), input.getEmployeeNumber(), input.getDepartment());

        EmployeeOutput result = updateEmployeeUseCase.execute(input);

        log.info("Successfully updated employee with ID: {}", result.getId());

        return result;
    }

    /**
     * 部分更新员工信息
     *
     * @param id 员工ID
     * @param input 部分更新参数
     * @return 更新后的员工信息
     */
    @PatchMapping("/{id}")
    public EmployeeOutput patchEmployee(
            @PathVariable String id,
            @Valid @RequestBody UpdateEmployeeInput input) {

        // 确保ID一致性
        input.setId(id);

        log.info("Patching employee - ID: {}", id);

        EmployeeOutput result = updateEmployeeUseCase.execute(input);

        log.info("Successfully patched employee with ID: {}", result.getId());

        return result;
    }

    /**
     * 获取员工详情
     *
     * @param id 员工ID
     * @param userId 当前用户ID（从请求头获取）
     * @param userRole 当前用户角色（从请求头获取）
     * @return 员工详情信息
     */
    @GetMapping("/{id}")
    public EmployeeDetailOutput getEmployeeDetail(
            @PathVariable String id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        log.info("Getting employee detail - ID: {}, User: {}, Role: {}", id, userId, userRole);

        // 如果没有提供用户信息，使用简化版本
        if (userId == null || userRole == null) {
            log.warn("Accessing employee detail without authentication - ID: {}", id);
            EmployeeDetailOutput result = getEmployeeDetailUseCase.execute(id);
            log.info("Successfully retrieved employee detail for ID: {} (without auth)", id);
            return result;
        }

        // 带权限控制的版本
        EmployeeDetailOutput result = getEmployeeDetailUseCase.execute(id, userId, userRole);

        log.info("Successfully retrieved employee detail for ID: {} (with auth)", id);

        return result;
    }

    /**
     * 获取员工访问权限描述
     *
     * @param id 员工ID
     * @param userId 当前用户ID
     * @param userRole 当前用户角色
     * @return 权限描述
     */
    @GetMapping("/{id}/access-permission")
    public String getAccessPermission(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String userRole) {

        log.info("Getting access permission for employee - ID: {}, User: {}, Role: {}", id, userId, userRole);

        String permission = getEmployeeDetailUseCase.getAccessPermissionDescription(id, userId, userRole);

        log.info("Successfully retrieved access permission for employee ID: {}", id);

        return permission;
    }

    /**
     * 获取员工法律法规提示
     *
     * @param id 员工ID
     * @return 法律法规提示
     */
    @GetMapping("/{id}/legal-notice")
    public String getLegalNotice(@PathVariable String id) {

        log.info("Getting legal notice for employee - ID: {}", id);

        String legalNotice = getEmployeeDetailUseCase.getLegalNotice(id);

        log.info("Successfully retrieved legal notice for employee ID: {}", id);

        return legalNotice;
    }

    /**
     * 删除员工
     *
     * @param id 员工ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable String id) {

        log.info("Deleting employee - ID: {}", id);

        deleteEmployeeUseCase.execute(id);

        log.info("Successfully deleted employee - ID: {}", id);
    }
}