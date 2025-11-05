package com.i0.talent.gateway.persistence.repositories;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import com.i0.domain.core.pagination.Pageable;
import com.i0.persistence.spring.pagination.SpringPage;
import com.i0.talent.domain.dto.EmployeePageQuery;
import com.i0.talent.domain.entities.Employee;
import com.i0.talent.domain.repository.EmployeeRepository;
import com.i0.talent.domain.enums.DataLocation;
import com.i0.talent.domain.enums.EmployeeStatus;
import com.i0.talent.domain.exception.DomainException;
import com.i0.talent.domain.valueobjects.WorkLocation;
import com.i0.talent.domain.valueobjects.Nationality;
import com.i0.talent.gateway.persistence.dataobjects.EmployeeDO;
import com.i0.talent.gateway.persistence.mappers.EmployeeMapper;
import com.i0.talent.gateway.acl.LocationAdapter;
import com.i0.talent.gateway.acl.LocationBatchAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 员工仓储实现类
 *
 * 实现 Domain 层定义的 EmployeeRepository 接口
 * 继承 ServiceImpl 以使用 MyBatis-Plus 的内置方法
 * 遵循 Gateway 层规范，使用 LambdaQueryWrapper 构建查询
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl extends ServiceImpl<EmployeeMapper, EmployeeDO> implements EmployeeRepository {

    private final LocationAdapter locationAdapter;
    private final LocationBatchAdapter locationBatchAdapter;

    @Override
    public Employee save(Employee employee) {
        EmployeeDO employeeDO = convertToDO(employee);

        // 设置审计字段
        LocalDateTime now = LocalDateTime.now();
        if (employeeDO.getId() == null) {
            employeeDO.setCreatedAt(now);
            employeeDO.setCreatorId("system"); // 实际应该从上下文获取
            employeeDO.setCreator("system");
        }
        employeeDO.setUpdatedAt(now);
        employeeDO.setUpdaterId("system");
        employeeDO.setUpdater("system");

        boolean success = saveOrUpdate(employeeDO);
        if (!success) {
            throw new DomainException("保存员工信息失败");
        }

        return convertToEntity(employeeDO);
    }

    @Override
    public Optional<Employee> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }

        EmployeeDO employeeDO = getById(id);
        return employeeDO != null ? Optional.of(convertToEntity(employeeDO)) : Optional.empty();
    }

    @Override
    public Optional<Employee> findByEmployeeNumber(String employeeNumber) {
        if (!StringUtils.hasText(employeeNumber)) {
            return Optional.empty();
        }

        LambdaQueryWrapper<EmployeeDO> queryWrapper = new LambdaQueryWrapper<EmployeeDO>()
                .eq(EmployeeDO::getEmployeeNumber, employeeNumber)
                .eq(EmployeeDO::getIsDeleted, false);

        EmployeeDO employeeDO = getOne(queryWrapper);
        return employeeDO != null ? Optional.of(convertToEntity(employeeDO)) : Optional.empty();
    }

    @Override
    public boolean existsByEmployeeNumber(String employeeNumber) {
        if (!StringUtils.hasText(employeeNumber)) {
            return false;
        }

        return lambdaQuery()
                .eq(EmployeeDO::getEmployeeNumber, employeeNumber)
                .eq(EmployeeDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public List<Employee> findAll() {
        LambdaQueryWrapper<EmployeeDO> queryWrapper = new LambdaQueryWrapper<EmployeeDO>()
                .eq(EmployeeDO::getIsDeleted, false)
                .orderByDesc(EmployeeDO::getCreatedAt);

        List<EmployeeDO> employeeDOList = list(queryWrapper);
        return employeeDOList.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByDepartment(String department) {
        if (!StringUtils.hasText(department)) {
            return List.of();
        }

        LambdaQueryWrapper<EmployeeDO> queryWrapper = new LambdaQueryWrapper<EmployeeDO>()
                .eq(EmployeeDO::getDepartment, department)
                .eq(EmployeeDO::getIsDeleted, false)
                .orderByDesc(EmployeeDO::getCreatedAt);

        List<EmployeeDO> employeeDOList = list(queryWrapper);
        return employeeDOList.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByWorkLocation(WorkLocation workLocation) {
        if (workLocation == null) {
            return List.of();
        }

        LambdaQueryWrapper<EmployeeDO> queryWrapper = new LambdaQueryWrapper<EmployeeDO>()
                .eq(EmployeeDO::getWorkLocationId, workLocation.getLocationId())
                .eq(EmployeeDO::getIsDeleted, false)
                .orderByDesc(EmployeeDO::getCreatedAt);

        List<EmployeeDO> employeeDOList = list(queryWrapper);
        return employeeDOList.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByWorkLocationId(String workLocationId) {
        if (!StringUtils.hasText(workLocationId)) {
            return List.of();
        }

        LambdaQueryWrapper<EmployeeDO> queryWrapper = new LambdaQueryWrapper<EmployeeDO>()
                .eq(EmployeeDO::getWorkLocationId, workLocationId)
                .eq(EmployeeDO::getIsDeleted, false)
                .orderByDesc(EmployeeDO::getCreatedAt);

        List<EmployeeDO> employeeDOList = list(queryWrapper);
        return employeeDOList.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByNationality(Nationality nationality) {
        if (nationality == null) {
            return List.of();
        }

        LambdaQueryWrapper<EmployeeDO> queryWrapper = new LambdaQueryWrapper<EmployeeDO>()
                .eq(EmployeeDO::getNationalityId, nationality.getCountryId())
                .eq(EmployeeDO::getIsDeleted, false)
                .orderByDesc(EmployeeDO::getCreatedAt);

        List<EmployeeDO> employeeDOList = list(queryWrapper);
        return employeeDOList.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByNationalityId(String nationalityId) {
        if (!StringUtils.hasText(nationalityId)) {
            return List.of();
        }

        LambdaQueryWrapper<EmployeeDO> queryWrapper = new LambdaQueryWrapper<EmployeeDO>()
                .eq(EmployeeDO::getNationalityId, nationalityId)
                .eq(EmployeeDO::getIsDeleted, false)
                .orderByDesc(EmployeeDO::getCreatedAt);

        List<EmployeeDO> employeeDOList = list(queryWrapper);
        return employeeDOList.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByStatus(String status) {
        if (!StringUtils.hasText(status)) {
            return List.of();
        }

        LambdaQueryWrapper<EmployeeDO> queryWrapper = new LambdaQueryWrapper<EmployeeDO>()
                .eq(EmployeeDO::getStatus, status)
                .eq(EmployeeDO::getIsDeleted, false)
                .orderByDesc(EmployeeDO::getCreatedAt);

        List<EmployeeDO> employeeDOList = list(queryWrapper);
        return employeeDOList.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByDataLocation(String dataLocation) {
        if (!StringUtils.hasText(dataLocation)) {
            return List.of();
        }

        LambdaQueryWrapper<EmployeeDO> queryWrapper = new LambdaQueryWrapper<EmployeeDO>()
                .eq(EmployeeDO::getDataLocation, dataLocation)
                .eq(EmployeeDO::getIsDeleted, false)
                .orderByDesc(EmployeeDO::getCreatedAt);

        List<EmployeeDO> employeeDOList = list(queryWrapper);
        return employeeDOList.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Employee> findByConditionalField(String fieldName, String fieldValue) {
        // 注意：当前版本不涉及动态字段，此方法暂时返回空列表
        // 后续版本会实现条件字段查询功能
        return List.of();
    }

    @Override
    public List<Employee> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return findAll();
        }

        String searchKeyword = keyword.trim().toLowerCase();

        LambdaQueryWrapper<EmployeeDO> queryWrapper = new LambdaQueryWrapper<EmployeeDO>()
                .eq(EmployeeDO::getIsDeleted, false)
                .and(wrapper -> wrapper
                        .like(EmployeeDO::getName, searchKeyword)
                        .or()
                        .like(EmployeeDO::getEmail, searchKeyword)
                        .or()
                        .like(EmployeeDO::getEmployeeNumber, searchKeyword))
                .orderByDesc(EmployeeDO::getCreatedAt);

        List<EmployeeDO> employeeDOList = list(queryWrapper);
        return employeeDOList.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Pageable<Employee> findEmployeesByPage(EmployeePageQuery query) {
        log.debug("Finding employees by page: {}, size: {}, keyword: {}", query.getPage(), query.getSize(), query.getKeyword());

        Page<EmployeeDO> pageRequest = new Page<>(query.getPage() + 1, query.getSize()); // MyBatis-Plus页码从1开始

        // 构建动态查询条件
        LambdaQueryWrapper<EmployeeDO> queryWrapper = new LambdaQueryWrapper<EmployeeDO>()
                .eq(EmployeeDO::getIsDeleted, false);

        // 基础搜索条件 - 使用OR关系搜索关键词
        if (StringUtils.hasText(query.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(EmployeeDO::getName, query.getKeyword())
                    .or()
                    .like(EmployeeDO::getEmail, query.getKeyword())
                    .or()
                    .like(EmployeeDO::getEmployeeNumber, query.getKeyword()));
        }

        // 部门和地点筛选
        queryWrapper
                .eq(StringUtils.hasText(query.getDepartment()), EmployeeDO::getDepartment, query.getDepartment())
                .eq(StringUtils.hasText(query.getWorkLocation()), EmployeeDO::getWorkLocationId, query.getWorkLocation())
                .eq(StringUtils.hasText(query.getNationality()), EmployeeDO::getNationalityId, query.getNationality())
                // 客户ID筛选
                .eq(StringUtils.hasText(query.getClientId()), EmployeeDO::getClientId, query.getClientId())
                // 状态和数据位置筛选
                .eq(StringUtils.hasText(query.getStatus()), EmployeeDO::getStatus, query.getStatus())
                .eq(StringUtils.hasText(query.getDataLocation()), EmployeeDO::getDataLocation, query.getDataLocation())
                .eq(Boolean.TRUE.equals(query.getActiveOnly()), EmployeeDO::getStatus, EmployeeStatus.ACTIVE.name())
                // 职位筛选
                .eq(StringUtils.hasText(query.getPosition()), EmployeeDO::getPosition, query.getPosition())
                // 排除已离职员工
                .eq(Boolean.TRUE.equals(query.getExcludeTerminated()), EmployeeDO::getLeaveDate, null)
                // 邮箱域名筛选 - 修复空指针风险
                .like(StringUtils.hasText(query.getEmailDomain()), EmployeeDO::getEmail,
                      StringUtils.hasText(query.getEmailDomain()) ? "%" + query.getEmailDomain() : null)
                // 工号前缀筛选
                .likeRight(StringUtils.hasText(query.getEmployeeNumberPrefix()), EmployeeDO::getEmployeeNumber, query.getEmployeeNumberPrefix());

        // 入职日期范围筛选 - 修复空指针风险
        if (query.getJoinDateFrom() != null) {
            queryWrapper.ge(EmployeeDO::getJoinDate, query.getJoinDateFrom().atStartOfDay());
        }
        if (query.getJoinDateTo() != null) {
            queryWrapper.le(EmployeeDO::getJoinDate, query.getJoinDateTo().plusDays(1).atStartOfDay());
        }

        // 离职日期范围筛选 - 修复空指针风险
        if (query.getLeaveDateFrom() != null) {
            queryWrapper.ge(EmployeeDO::getLeaveDate, query.getLeaveDateFrom().atStartOfDay());
        }
        if (query.getLeaveDateTo() != null) {
            queryWrapper.le(EmployeeDO::getLeaveDate, query.getLeaveDateTo().plusDays(1).atStartOfDay());
        }

        // 构建排序条件
        if (StringUtils.hasText(query.getSortField())) {
            Sort.Direction direction = StringUtils.hasText(query.getSortDirection()) &&
                    "DESC".equalsIgnoreCase(query.getSortDirection()) ?
                    Sort.Direction.DESC : Sort.Direction.ASC;

            switch (query.getSortField().toLowerCase()) {
                case "name":
                    queryWrapper.orderBy(true, direction.equals(Sort.Direction.ASC), EmployeeDO::getName);
                    break;
                case "employeenumber":
                    queryWrapper.orderBy(true, direction.equals(Sort.Direction.ASC), EmployeeDO::getEmployeeNumber);
                    break;
                case "email":
                    queryWrapper.orderBy(true, direction.equals(Sort.Direction.ASC), EmployeeDO::getEmail);
                    break;
                case "department":
                    queryWrapper.orderBy(true, direction.equals(Sort.Direction.ASC), EmployeeDO::getDepartment);
                    break;
                case "joindate":
                    queryWrapper.orderBy(true, direction.equals(Sort.Direction.ASC), EmployeeDO::getJoinDate);
                    break;
                case "createdat":
                    queryWrapper.orderBy(true, direction.equals(Sort.Direction.ASC), EmployeeDO::getCreatedAt);
                    break;
                default:
                    // 默认按创建时间降序
                    queryWrapper.orderByDesc(EmployeeDO::getCreatedAt);
            }
        } else {
            // 默认排序
            queryWrapper.orderByDesc(EmployeeDO::getCreatedAt);
        }

        // 执行分页查询
        IPage<EmployeeDO> pageResult = page(pageRequest, queryWrapper);
        List<EmployeeDO> employeeDOList = pageResult.getRecords();

        // 性能优化：批量预加载Location信息，解决N+1查询问题
        List<String> workLocationIds = employeeDOList.stream()
                .map(EmployeeDO::getWorkLocationId)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());

        List<String> nationalityIds = employeeDOList.stream()
                .map(EmployeeDO::getNationalityId)
                .filter(StringUtils::hasText)
                .distinct()
                .collect(Collectors.toList());

        // 批量获取Location信息
        Map<String, WorkLocation> workLocationMap = locationBatchAdapter.fetchWorkLocationsBatch(workLocationIds);
        Map<String, Nationality> nationalityMap = locationBatchAdapter.fetchNationalitiesBatch(nationalityIds);

        // 转换为领域实体并创建分页结果
        return createSpringPageFromMyBatis(
                employeeDOList.stream()
                        .map(employeeDO -> convertToEntityWithCache(employeeDO, workLocationMap, nationalityMap))
                        .collect(Collectors.toList()),
                query.getPage(),
                query.getSize(),
                pageResult.getTotal()
        );
    }

    /**
     * 创建SpringPage从MyBatis分页结果
     */
    private Pageable<Employee> createSpringPageFromMyBatis(List<Employee> employees, int page, int size, long total) {
        return SpringPage.of(new PageImpl<>(
                employees,
                PageRequest.of(page, size),
                total
        ));
    }

    @Override
    public boolean existsById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        return lambdaQuery()
                .eq(EmployeeDO::getId, id)
                .eq(EmployeeDO::getIsDeleted, false)
                .exists();
    }

    @Override
    public void deleteById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return;
        }

        int result = getBaseMapper().deleteById(id);
        if (result == 0) {
            throw new DomainException("删除员工失败，员工不存在");
        }
    }

    @Override
    public Employee addConditionalField(String employeeId, String fieldName, String fieldValue) {
        // 注意：当前版本不涉及动态字段，此方法暂时抛出异常
        throw new DomainException("当前版本不支持条件字段功能");
    }

    @Override
    public Employee updateConditionalField(String employeeId, String fieldName, String newValue) {
        // 注意：当前版本不涉及动态字段，此方法暂时抛出异常
        throw new DomainException("当前版本不支持条件字段功能");
    }

    @Override
    public Employee removeConditionalField(String employeeId, String fieldName) {
        // 注意：当前版本不涉及动态字段，此方法暂时抛出异常
        throw new DomainException("当前版本不支持条件字段功能");
    }

    @Override
    public String getConditionalFieldValue(String employeeId, String fieldName) {
        // 注意：当前版本不涉及动态字段，此方法暂时返回null
        return null;
    }

    // ========== 私有转换方法 ==========

    /**
     * 使用预加载的缓存转换DO为Entity - 性能优化版本
     */
    private Employee convertToEntityWithCache(EmployeeDO employeeDO,
                                            Map<String, WorkLocation> workLocationMap,
                                            Map<String, Nationality> nationalityMap) {
        if (employeeDO == null) {
            return null;
        }

        // 从缓存中获取WorkLocation值对象
        WorkLocation workLocation = null;
        if (StringUtils.hasText(employeeDO.getWorkLocationId())) {
            workLocation = workLocationMap.get(employeeDO.getWorkLocationId());
            if (workLocation == null) {
                log.warn("WorkLocation not found in cache for ID: {}", employeeDO.getWorkLocationId());
                workLocation = WorkLocation.of(employeeDO.getWorkLocationId(), "Unknown", null);
            }
        }

        // 从缓存中获取Nationality值对象
        Nationality nationality = null;
        if (StringUtils.hasText(employeeDO.getNationalityId())) {
            nationality = nationalityMap.get(employeeDO.getNationalityId());
            if (nationality == null) {
                log.warn("Nationality not found in cache for ID: {}", employeeDO.getNationalityId());
                nationality = Nationality.of(employeeDO.getNationalityId(), "Unknown", null);
            }
        }

        return Employee.reconstruct(
                employeeDO.getId(),
                employeeDO.getName(),
                employeeDO.getEmployeeNumber(),
                workLocation,
                nationality,
                employeeDO.getEmail(),
                employeeDO.getDepartment(),
                employeeDO.getPosition(),
                employeeDO.getJoinDate() != null ? employeeDO.getJoinDate().toLocalDate() : null,
                employeeDO.getLeaveDate() != null ? employeeDO.getLeaveDate().toLocalDate() : null,
                DataLocation.valueOf(employeeDO.getDataLocation()),
                EmployeeStatus.valueOf(employeeDO.getStatus()),
                employeeDO.getClientId()
        );
    }

    private Employee convertToEntity(EmployeeDO employeeDO) {
        if (employeeDO == null) {
            return null;
        }

        // 通过LocationAdapter获取WorkLocation值对象
        WorkLocation workLocation = null;
        if (StringUtils.hasText(employeeDO.getWorkLocationId())) {
            try {
                workLocation = locationAdapter.fetchWorkLocation(employeeDO.getWorkLocationId());
            } catch (Exception e) {
                log.warn("Failed to fetch work location for ID: {}, error: {}",
                        employeeDO.getWorkLocationId(), e.getMessage());
                // 如果Location信息获取失败，创建一个基础的WorkLocation对象
                workLocation = WorkLocation.of(employeeDO.getWorkLocationId(), "Unknown", null);
            }
        }

        // 通过LocationAdapter获取Nationality值对象
        Nationality nationality = null;
        if (StringUtils.hasText(employeeDO.getNationalityId())) {
            try {
                nationality = locationAdapter.fetchNationality(employeeDO.getNationalityId());
            } catch (Exception e) {
                log.warn("Failed to fetch nationality for ID: {}, error: {}",
                        employeeDO.getNationalityId(), e.getMessage());
                // 如果Location信息获取失败，创建一个基础的Nationality对象
                nationality = Nationality.of(employeeDO.getNationalityId(), "Unknown", null);
            }
        }

        return Employee.reconstruct(
                employeeDO.getId(),
                employeeDO.getName(),
                employeeDO.getEmployeeNumber(),
                workLocation,
                nationality,
                employeeDO.getEmail(),
                employeeDO.getDepartment(),
                employeeDO.getPosition(),
                employeeDO.getJoinDate() != null ? employeeDO.getJoinDate().toLocalDate() : null,
                employeeDO.getLeaveDate() != null ? employeeDO.getLeaveDate().toLocalDate() : null,
                DataLocation.valueOf(employeeDO.getDataLocation()),
                EmployeeStatus.valueOf(employeeDO.getStatus()),
                employeeDO.getClientId()
        );
    }

    private EmployeeDO convertToDO(Employee employee) {
        if (employee == null) {
            return null;
        }

        // 从WorkLocation值对象中提取ID
        String workLocationId = null;
        WorkLocation workLocation = employee.getWorkLocation();
        if (workLocation != null) {
            workLocationId = workLocation.getLocationId();
        }

        // 从Nationality值对象中提取ID
        String nationalityId = null;
        Nationality nationality = employee.getNationality();
        if (nationality != null) {
            nationalityId = nationality.getCountryId();
        }

        return EmployeeDO.builder()
                .id(employee.getId())
                .name(employee.getName())
                .employeeNumber(employee.getEmployeeNumber())
                .workLocationId(workLocationId)
                .nationalityId(nationalityId)
                .email(employee.getEmail())
                .department(employee.getDepartment())
                .position(employee.getPosition())
                .joinDate(employee.getJoinDate() != null ? employee.getJoinDate().atStartOfDay() : null)
                .leaveDate(employee.getLeaveDate() != null ? employee.getLeaveDate().atStartOfDay() : null)
                .dataLocation(employee.getDataLocation().name())
                .status(employee.getStatus().name())
                .clientId(employee.getClientId())
                .isDeleted(false) // 默认未删除
                .build();
    }
}