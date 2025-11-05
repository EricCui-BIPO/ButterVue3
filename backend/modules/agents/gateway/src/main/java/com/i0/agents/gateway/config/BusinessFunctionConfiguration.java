package com.i0.agents.gateway.config;

import com.i0.agents.domain.services.BusinessFunctionRegistry;
import com.i0.agents.domain.valueobjects.BusinessFunction;
import com.i0.agents.gateway.acl.EmployeeFunctionAdapter;
import com.i0.agents.gateway.acl.EntityFunctionAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * 业务函数配置
 * 负责注册所有可被AI调用的业务函数
 * <p>
 * 已从Application层移动到Gateway层，符合架构规范：
 * - 跨模块调用通过ACL适配器进行
 * - Gateway层负责外部集成和配置
 * - 遵循gateway-design.md的ACL规范
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class BusinessFunctionConfiguration {

    private final EntityFunctionAdapter entityFunctionAdapter;
    private final EmployeeFunctionAdapter employeeFunctionAdapter;

    /**
     * 创建业务函数注册器并注册基础业务函数
     */
    @Bean
    public BusinessFunctionRegistry businessFunctionRegistry() {
        BusinessFunctionRegistry registry = new BusinessFunctionRegistry();
        // 注册实体相关函数
        registerEntityFunctions(registry);
        // 注册员工相关函数
        registerEmployeeFunctions(registry);
        log.info("Business function registry initialized with {} functions", registry.count());
        log.info("Registered functions: {}", registry.getFunctionNames());
        return registry;
    }

    /**
     * 注册实体相关函数
     */
    private void registerEntityFunctions(BusinessFunctionRegistry registry) {
        // 创建实体函数
        BusinessFunction createEntityFunction = BusinessFunction.builder()
            .name("create_entity")
            .description("创建一个新的业务实体，支持BIPO实体、客户实体、供应商实体等类型")
            .parameter("name", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("实体名称，长度不超过100个字符")
                .build())
            .parameter("entityType", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("实体类型：BIPO_ENTITY(内部实体)、CLIENT_ENTITY(客户实体)、VENDOR_ENTITY(供应商实体)")
                .enumValues(Arrays.asList("BIPO_ENTITY", "CLIENT_ENTITY", "VENDOR_ENTITY"))
                .build())
            .parameter("code", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("实体代码，可选，长度不超过50个字符")
                .build())
            .parameter("description", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("实体描述，可选，长度不超过500个字符")
                .build())
            .required("name")
            .required("entityType")
            .handler(entityFunctionAdapter::handleCreateEntity)
            .build();

        // 查询实体函数
        BusinessFunction findEntityFunction = BusinessFunction.builder()
            .name("find_entity")
            .description("根据业务字段查询实体的详细信息。支持通过name（名称）或code（代码）进行查询，无需知晓系统内部ID")
            .parameter("name", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("实体的名称，可选参数。提供实体名称进行模糊匹配查询")
                .build())
            .parameter("code", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("实体的代码，可选参数。提供实体代码进行精确匹配查询")
                .build())
            .handler(entityFunctionAdapter::handleFindEntity)
            .build();

        registry.register(createEntityFunction);
        registry.register(findEntityFunction);
    }

    /**
     * 注册员工相关函数
     */
    private void registerEmployeeFunctions(BusinessFunctionRegistry registry) {
        // 创建员工函数
        BusinessFunction createEmployeeFunction = BusinessFunction.builder()
            .name("create_employee")
            .description("快速创建一个新的员工，支持设置基本信息、工作地点、国籍等。注意：员工工号和邮箱地址必须全局唯一，不可重复使用")
            .parameter("name", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("员工姓名，长度不超过100个字符")
                .build())
            .parameter("employeeNumber", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("员工工号，全局唯一标识符，不可与其他员工重复。建议使用格式：EMP + 年份 + 序号，如 EMP2024001")
                .build())
            .parameter("email", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("邮箱地址，全局唯一，不可与其他员工重复。必须是有效的邮箱格式，如：zhang.san@company.com")
                .build())
            .parameter("department", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("部门名称，可选")
                .build())
            .parameter("position", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("岗位名称，可选")
                .build())
            .parameter("workLocation", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("工作地点（国家/地区），如：中国、美国、日本、香港、新加坡等，系统会自动匹配对应的Location ID")
                .build())
            .parameter("nationality", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("国籍名称，如：中国、美国、日本、香港等，系统会自动匹配对应的Location ID")
                .build())
            .parameter("workLocationId", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("工作地点ID，关联Location模块的地理位置（可选，推荐使用workLocation参数）")
                .build())
            .parameter("nationalityId", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("国籍ID，关联Location模块的国家地区（可选，推荐使用nationality参数）")
                .build())
            .parameter("clientId", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("所属客户ID，关联Client模块的客户，可选")
                .build())
            .parameter("joinDate", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("入职日期，格式：YYYY-MM-DD，默认为今天")
                .build())
            .parameter("dataLocation", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("数据存储位置：根据工作地点自动选择规则 - 中国大陆：NINGXIA(宁夏)；亚洲（非中国大陆）：SINGAPORE(新加坡)；非亚洲地区：GERMANY(德国)")
                .enumValues(Arrays.asList("NINGXIA", "SINGAPORE", "GERMANY"))
                .build())
            .required("name")
            .required("employeeNumber")
            .required("email")
            .handler(employeeFunctionAdapter::handleCreateEmployee)
            .build();

        // 查询员工函数
        BusinessFunction findEmployeeFunction = BusinessFunction.builder()
            .name("find_employee")
            .description("根据员工姓名/工号/邮箱查询员工详细信息，用于快速查询员工的入职情况和员工状态")
            .parameter("query", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("员工姓名、工号或邮箱，支持模糊查询")
                .build())
            .parameter("queryType", BusinessFunction.PropertyDefinition.builder()
                .type("string")
                .description("查询类型：NAME(姓名)、EMPLOYEE_NUMBER(工号)、EMAIL(邮箱)，不指定时默认为全字段模糊查询")
                .enumValues(Arrays.asList("NAME", "EMPLOYEE_NUMBER", "EMAIL"))
                .build())
            .required("query")
            .handler(employeeFunctionAdapter::handleFindEmployee)
            .build();

        registry.register(createEmployeeFunction);
        registry.register(findEmployeeFunction);
    }
}