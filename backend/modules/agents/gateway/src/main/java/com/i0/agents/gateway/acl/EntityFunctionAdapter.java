package com.i0.agents.gateway.acl;

import com.i0.agents.domain.valueobjects.BusinessFunction;
import com.i0.entity.application.dto.input.CreateEntityInput;
import com.i0.entity.application.dto.output.EntityOutput;
import com.i0.entity.application.usecases.CreateEntityUseCase;
import com.i0.entity.application.usecases.FindEntityByCodeUseCase;
import com.i0.entity.application.usecases.FindEntityByNameUseCase;
import com.i0.entity.application.usecases.GetEntityUseCase;
import com.i0.entity.domain.valueobjects.EntityType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity模块ACL适配器
 * 负责将agents领域的业务函数调用转换为entity领域的调用
 * 符合gateway-design.md中的ACL跨领域调用规范
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EntityFunctionAdapter {

    private final CreateEntityUseCase createEntityUseCase;
    private final GetEntityUseCase getEntityUseCase;
    private final FindEntityByCodeUseCase findEntityByCodeUseCase;
    private final FindEntityByNameUseCase findEntityByNameUseCase;

    /**
     * 处理创建实体的业务函数调用
     *
     * @param arguments AI传递的参数
     * @return 业务函数调用结果
     */
    public BusinessFunction.FunctionCallResult handleCreateEntity(Map<String, Object> arguments) {
        try {
            String name = (String) arguments.get("name");
            String entityTypeStr = (String) arguments.get("entityType");
            String code = (String) arguments.get("code");
            String description = (String) arguments.getOrDefault("description", "");

            log.info("Creating entity via ACL adapter: name={}, entityType={}, code={}", name, entityTypeStr, code);

            // 验证实体类型参数
            if (entityTypeStr == null || entityTypeStr.trim().isEmpty()) {
                return BusinessFunction.FunctionCallResult.failure("实体类型不能为空");
            }

            // 转换实体类型
            EntityType entityType;
            try {
                entityType = EntityType.valueOf(entityTypeStr);
            } catch (IllegalArgumentException e) {
                return BusinessFunction.FunctionCallResult.failure(
                    String.format("无效的实体类型: %s，支持的类型: BIPO_ENTITY, CLIENT_ENTITY, VENDOR_ENTITY", entityTypeStr)
                );
            }

            // 创建输入DTO
            CreateEntityInput input = CreateEntityInput.builder()
                .name(name)
                .entityType(entityType)
                .code(code)
                .description(description)
                .build();

            // 调用真实的实体创建服务
            EntityOutput result = createEntityUseCase.execute(input);

            // 构建返回数据
            Map<String, Object> entityData = new HashMap<>();
            entityData.put("id", result.getId());
            entityData.put("name", result.getName());
            entityData.put("entityType", result.getEntityType());
            entityData.put("code", result.getCode());
            entityData.put("description", result.getDescription());
            entityData.put("active", result.getActive());
            entityData.put("createdAt", result.getCreatedAt());

            // UI组件类型标识
            String uiComponentType = "entity-info";

            return BusinessFunction.FunctionCallResult.success(
                String.format("实体 %s 创建成功，ID：%s，类型：%s", name, result.getId(), entityType.getDisplayName()),
                entityData,
                uiComponentType
            );
        } catch (IllegalArgumentException e) {
            log.warn("Invalid entity creation request via ACL: {}", e.getMessage());
            return BusinessFunction.FunctionCallResult.failure("参数错误：" + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to create entity via ACL adapter", e);
            return BusinessFunction.FunctionCallResult.failure("实体创建失败：" + e.getMessage());
        }
    }

    /**
     * 处理查询实体的业务函数调用
     *
     * @param arguments AI传递的参数
     * @return 业务函数调用结果
     */
    public BusinessFunction.FunctionCallResult handleFindEntity(Map<String, Object> arguments) {
        try {
            String name = (String) arguments.get("name");
            String code = (String) arguments.get("code");

            // 验证参数：至少提供name或code其中一个
            if ((name == null || name.trim().isEmpty()) && (code == null || code.trim().isEmpty())) {
                return BusinessFunction.FunctionCallResult.failure("请提供实体名称（name）或实体代码（code）进行查询");
            }

            log.info("Finding entity via ACL adapter: name={}, code={}", name, code);

            EntityOutput result = null;
            String queryMethod = "";

            // 优先使用code进行精确查询
            if (code != null && !code.trim().isEmpty()) {
                result = findEntityByCode(code.trim());
                queryMethod = "代码";
            }
            // 如果没有code或者code查询没找到，使用name查询
            else if (name != null && !name.trim().isEmpty()) {
                result = findEntityByName(name.trim());
                queryMethod = "名称";
            }

            if (result == null) {
                return BusinessFunction.FunctionCallResult.failure("未找到匹配的实体");
            }

            // 构建返回数据
            Map<String, Object> entityData = new HashMap<>();
            entityData.put("id", result.getId());
            entityData.put("name", result.getName());
            entityData.put("entityType", result.getEntityType());
            entityData.put("code", result.getCode());
            entityData.put("description", result.getDescription());
            entityData.put("active", result.getActive());
            entityData.put("createdAt", result.getCreatedAt());
            entityData.put("updatedAt", result.getUpdatedAt());

            // 添加实体类型的详细信息
            EntityType entityType = result.getEntityType();
            entityData.put("entityTypeDisplayName", entityType.getDisplayName());
            entityData.put("entityTypeDescription", entityType.getDescription());
            entityData.put("isBipoEntity", entityType.isBipoEntity());
            entityData.put("isExternalEntity", entityType.isExternalEntity());

            // UI组件类型标识
            String uiComponentType = "entity-detail";

            return BusinessFunction.FunctionCallResult.success(
                String.format("通过%s找到实体：%s（%s）", queryMethod, result.getName(), entityType.getDisplayName()),
                entityData,
                uiComponentType
            );
        } catch (IllegalArgumentException e) {
            log.warn("Entity not found via ACL: {}", e.getMessage());
            return BusinessFunction.FunctionCallResult.failure("查询失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to find entity via ACL adapter", e);
            return BusinessFunction.FunctionCallResult.failure("查询实体失败：" + e.getMessage());
        }
    }

    /**
     * 通过代码查询实体
     */
    private EntityOutput findEntityByCode(String code) {
        try {
            log.info("Finding entity by code: {}", code);
            return findEntityByCodeUseCase.execute(code);
        } catch (IllegalArgumentException e) {
            log.warn("Entity not found by code: {}", e.getMessage());
            throw e; // 重新抛出异常，让上层处理
        } catch (Exception e) {
            log.error("Error finding entity by code: {}", e.getMessage());
            throw new IllegalArgumentException("通过代码查询实体时发生错误: " + e.getMessage());
        }
    }

    /**
     * 通过名称查询实体
     */
    private EntityOutput findEntityByName(String name) {
        try {
            log.info("Finding entity by name: {}", name);
            return findEntityByNameUseCase.execute(name);
        } catch (IllegalArgumentException e) {
            log.warn("Entity not found by name: {}", e.getMessage());
            throw e; // 重新抛出异常，让上层处理
        } catch (Exception e) {
            log.error("Error finding entity by name: {}", e.getMessage());
            throw new IllegalArgumentException("通过名称查询实体时发生错误: " + e.getMessage());
        }
    }
}