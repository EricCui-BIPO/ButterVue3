package com.i0.talent.domain.entities;

import com.i0.talent.domain.enums.DataLocation;
import com.i0.talent.domain.enums.EmployeeStatus;
import com.i0.talent.domain.exception.DomainException;
import com.i0.talent.domain.valueobjects.WorkLocation;
import com.i0.talent.domain.valueobjects.Nationality;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 员工实体 - 核心业务对象
 *
 * 包含员工的基本信息和业务规则
 * 遵循 Domain 层纯净性原则，不包含审计字段和持久化注解
 */
public class Employee {

    /**
     * 员工唯一标识符
     */
    private final String id;

    /**
     * 员工姓名
     */
    private String name;

    /**
     * 员工工号
     */
    private String employeeNumber;

    /**
     * 工作地点
     */
    private WorkLocation workLocation;

    /**
     * 国籍
     */
    private Nationality nationality;

    /**
     * 邮箱地址
     */
    private String email;

    /**
     * 部门
     */
    private String department;

    /**
     * 岗位
     */
    private String position;

    /**
     * 入职日期
     */
    private LocalDate joinDate;

    /**
     * 离职日期（可选）
     */
    private LocalDate leaveDate;

    /**
     * 数据存储位置
     */
    private DataLocation dataLocation;

    /**
     * 员工状态
     */
    private EmployeeStatus status;

    /**
     * 所属客户ID
     */
    private String clientId;

  
    /**
     * 私有构造函数，通过工厂方法创建
     */
    private Employee(String id, String name, String employeeNumber, WorkLocation workLocation,
                   Nationality nationality, String email, String department, String position,
                   LocalDate joinDate, DataLocation dataLocation, String clientId) {
        this.id = Objects.requireNonNull(id, "员工ID不能为空");
        this.name = name;
        this.employeeNumber = employeeNumber;
        this.workLocation = Objects.requireNonNull(workLocation, "工作地点不能为空");
        this.nationality = Objects.requireNonNull(nationality, "国籍不能为空");
        this.email = email;
        this.department = department;
        this.position = position;
        this.joinDate = Objects.requireNonNull(joinDate, "入职日期不能为空");
        this.dataLocation = Objects.requireNonNull(dataLocation, "数据存储位置不能为空");
        this.clientId = clientId;
        this.status = EmployeeStatus.ACTIVE;

        validate();
    }

    /**
     * 创建新员工的工厂方法
     */
    public static Employee create(String name, String employeeNumber, WorkLocation workLocation,
                                Nationality nationality, String email, String department, String position,
                                LocalDate joinDate, DataLocation dataLocation, String clientId) {
        return new Employee(
                java.util.UUID.randomUUID().toString(),
                name,
                employeeNumber,
                workLocation,
                nationality,
                email,
                department,
                position,
                joinDate,
                dataLocation,
                clientId
        );
    }

    /**
     * 从现有数据重建员工对象
     */
    public static Employee reconstruct(String id, String name, String employeeNumber, WorkLocation workLocation,
                                     Nationality nationality, String email, String department, String position,
                                     LocalDate joinDate, LocalDate leaveDate, DataLocation dataLocation,
                                     EmployeeStatus status, String clientId) {
        Employee employee = new Employee(id, name, employeeNumber, workLocation, nationality,
                email, department, position, joinDate, dataLocation, clientId);
        employee.leaveDate = leaveDate;
        employee.status = status;
        return employee;
    }

    // ========== 业务方法 ==========

    /**
     * 更新员工基本信息
     */
    public void updateBasicInfo(String name, String email, String department, String position) {
        this.name = name;
        this.email = email;
        this.department = department;
        this.position = position;
        validate();
    }

    
    /**
     * 更新工作地点
     */
    public void updateWorkLocation(WorkLocation newWorkLocation) {
        if (this.status == EmployeeStatus.TERMINATED) {
            throw new DomainException("已离职员工不能更改工作地点");
        }
        this.workLocation = Objects.requireNonNull(newWorkLocation, "工作地点不能为空");
    }

    /**
     * 更新国籍
     */
    public void updateNationality(Nationality newNationality) {
        this.nationality = Objects.requireNonNull(newNationality, "国籍不能为空");
    }

    /**
     * 更新所属客户
     */
    public void updateClientId(String newClientId) {
        this.clientId = newClientId;
    }

    /**
     * 员工离职
     */
    public void terminate(LocalDate leaveDate) {
        if (this.status == EmployeeStatus.TERMINATED) {
            throw new DomainException("员工已经离职");
        }
        if (leaveDate == null || leaveDate.isBefore(this.joinDate)) {
            throw new DomainException("离职日期无效");
        }
        if (leaveDate.isAfter(LocalDate.now())) {
            throw new DomainException("离职日期不能是未来日期");
        }

        this.leaveDate = leaveDate;
        this.status = EmployeeStatus.TERMINATED;
    }

    /**
     * 重新入职员工
     */
    public void reinstate(LocalDate newJoinDate) {
        if (this.status != EmployeeStatus.TERMINATED) {
            throw new DomainException("只有离职员工可以重新入职");
        }
        if (newJoinDate == null || newJoinDate.isAfter(LocalDate.now())) {
            throw new DomainException("入职日期无效");
        }

        this.joinDate = newJoinDate;
        this.leaveDate = null;
        this.status = EmployeeStatus.ACTIVE;
    }

    
    /**
     * 验证员工基本信息
     */
    private void validate() {
        if (this.name == null || this.name.trim().isEmpty()) {
            throw new DomainException("员工姓名不能为空");
        }

        if (this.employeeNumber == null || this.employeeNumber.trim().isEmpty()) {
            throw new DomainException("员工工号不能为空");
        }

        if (this.email == null || !this.email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new DomainException("邮箱地址格式无效");
        }

        if (this.joinDate == null || this.joinDate.isAfter(LocalDate.now())) {
            throw new DomainException("入职日期无效");
        }
    }

    // ========== 查询方法 ==========

    public boolean isActive() {
        return this.status == EmployeeStatus.ACTIVE;
    }

    public boolean isTerminated() {
        return this.status == EmployeeStatus.TERMINATED;
    }

    
    // ========== Getters ==========

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public WorkLocation getWorkLocation() {
        return workLocation;
    }

    public Nationality getNationality() {
        return nationality;
    }

    public String getEmail() {
        return email;
    }

    public String getDepartment() {
        return department;
    }

    public String getPosition() {
        return position;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public LocalDate getLeaveDate() {
        return leaveDate;
    }

    public DataLocation getDataLocation() {
        return dataLocation;
    }

    public EmployeeStatus getStatus() {
        return status;
    }

    public String getClientId() {
        return clientId;
    }

    
    // ========== equals & hashCode ==========

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", status=" + status +
                '}';
    }
}