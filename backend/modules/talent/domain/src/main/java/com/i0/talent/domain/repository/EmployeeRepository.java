package com.i0.talent.domain.repository;

import com.i0.domain.core.pagination.Pageable;
import com.i0.talent.domain.dto.EmployeePageQuery;
import com.i0.talent.domain.entities.Employee;
import com.i0.talent.domain.valueobjects.WorkLocation;
import com.i0.talent.domain.valueobjects.Nationality;

import java.util.List;
import java.util.Optional;

/**
 * 员工仓储接口
 *
 * 在Domain层定义，Gateway层实现
 * 只包含业务需要的操作，不包含实现细节
 */
public interface EmployeeRepository {
    
    /**
     * 保存员工信息
     */
    Employee save(Employee employee);
    
    /**
     * 根据ID查找员工
     */
    Optional<Employee> findById(String id);

    /**
     * 检查员工ID是否存在
     */
    boolean existsById(String id);
    
    /**
     * 根据员工工号查找员工
     */
    Optional<Employee> findByEmployeeNumber(String employeeNumber);
    
    /**
     * 检查员工工号是否已存在
     */
    boolean existsByEmployeeNumber(String employeeNumber);
    
    /**
     * 查询所有员工
     */
    List<Employee> findAll();
    
    /**
     * 根据部门查询员工
     */
    List<Employee> findByDepartment(String department);
    
    /**
     * 根据工作地点查询员工
     */
    List<Employee> findByWorkLocation(WorkLocation workLocation);

    /**
     * 根据工作地点ID查询员工
     */
    List<Employee> findByWorkLocationId(String workLocationId);

    /**
     * 根据国籍查询员工
     */
    List<Employee> findByNationality(Nationality nationality);

    /**
     * 根据国籍ID查询员工
     */
    List<Employee> findByNationalityId(String nationalityId);
    
    /**
     * 根据员工状态查询员工
     */
    List<Employee> findByStatus(String status);
    
    /**
     * 根据数据存储位置查询员工
     */
    List<Employee> findByDataLocation(String dataLocation);
    
    /**
     * 根据条件字段查询员工
     */
    List<Employee> findByConditionalField(String fieldName, String fieldValue);
    
    /**
     * 搜索员工（按姓名、邮箱、工号）
     */
    List<Employee> search(String keyword);

    /**
     * 分页查询员工
     *
     * @param query 分页查询参数
     * @return 分页查询结果
     */
    Pageable<Employee> findEmployeesByPage(EmployeePageQuery query);

    /**
     * 删除员工（逻辑删除）
     */
    void deleteById(String id);
    
    /**
     * 添加条件字段到员工
     */
    Employee addConditionalField(String employeeId, String fieldName, String fieldValue);

    /**
     * 更新员工条件字段
     */
    Employee updateConditionalField(String employeeId, String fieldName, String newValue);

    /**
     * 移除员工条件字段
     */
    Employee removeConditionalField(String employeeId, String fieldName);

    /**
     * 获取员工条件字段值
     */
    String getConditionalFieldValue(String employeeId, String fieldName);
}
