package com.i0.app.integration;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 基础集成测试
 * 验证Spring上下文和基本配置是否正常
 * 提供通用的物理删除功能供子类使用
 */
@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BasicIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    Flyway flyway;

    @BeforeAll
    void resetDb() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    void testApplicationContextLoads() {
        // 这个测试验证Spring上下文能够正常加载
    }

    @Test
    void testBasicEntityEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/entities/types")).andExpect(status().isOk());
    }

    /**
     * 物理删除指定实体类对应的表数据
     * 按领域来清理测试数据，支持多个实体类
     *
     * @param entityClasses 需要清理数据的实体类数组
     */
    protected void clearUpTestData(Class<?>... entityClasses) {
        Arrays.stream(entityClasses).forEach(entityClass -> {
            try {
                // 获取实体类对应的表名
                String tableName = getTableName(entityClass);

                // 构造SQL清空表的语句（使用TRUNCATE TABLE，效率更高）
                String clearSql = "TRUNCATE TABLE " + tableName;

                jdbcTemplate.execute(clearSql);

                log.debug("Table {} cleared successfully", tableName);
            } catch (Exception e) {
                log.error("Failed to clear table for entity class: {}", entityClass.getSimpleName(), e);
                // 如果TRUNCATE失败（可能因为外键约束），则使用DELETE
                tryDeleteByEntityClass(entityClass);
            }
        });
    }

    /**
     * 根据实体类获取对应的表名
     * 优先使用@TableName注解，否则使用类名的蛇形命名
     *
     * @param entityClass 实体类
     * @return 表名
     */
    private String getTableName(Class<?> entityClass) {
        try {
            // 尝试获取@TableName注解
            Object tableNameAnnotation = entityClass.getAnnotation(com.baomidou.mybatisplus.annotation.TableName.class);
            if (tableNameAnnotation != null) {
                java.lang.reflect.Method valueMethod = tableNameAnnotation.getClass().getMethod("value");
                String tableName = (String) valueMethod.invoke(tableNameAnnotation);
                if (!tableName.isEmpty()) {
                    return tableName;
                }
            }
        } catch (Exception e) {
            log.debug("Failed to get @TableName annotation from class: {}", entityClass.getSimpleName(), e);
        }

        // 如果没有@TableName注解或获取失败，使用类名的蛇形命名
        return camelToSnake(entityClass.getSimpleName());
    }

    /**
     * 将驼峰命名转换为蛇形命名
     *
     * @param camelCase 驼峰命名字符串
     * @return 蛇形命名字符串
     */
    private String camelToSnake(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        return camelCase.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
    }

    /**
     * 当TRUNCATE失败时，使用DELETE语句清理数据
     * 支持按常见模式删除测试数据
     *
     * @param entityClass 实体类
     */
    private void tryDeleteByEntityClass(Class<?> entityClass) {
        String tableName = getTableName(entityClass);

        // 构建针对不同实体类的删除条件
        String deleteSql = buildDeleteSql(tableName, entityClass);

        try {
            int deletedRows = jdbcTemplate.update(deleteSql);
            log.debug("Deleted {} rows from table {} using DELETE", deletedRows, tableName);
        } catch (Exception e) {
            log.error("Failed to delete data from table: {}", tableName, e);
            throw new RuntimeException("Failed to clear test data for table: " + tableName, e);
        }
    }

    /**
     * 根据实体类构建针对性的删除SQL
     *
     * @param tableName   表名
     * @param entityClass 实体类
     * @return 删除SQL语句
     */
    private String buildDeleteSql(String tableName, Class<?> entityClass) {
        String className = entityClass.getSimpleName();

        // 根据不同的实体类构建针对性的删除条件
        if (className.contains("Entity")) {
            return "DELETE FROM " + tableName + " WHERE " + "id = 'existing-entity-id' OR " + "code LIKE 'INTEGRATION_%' OR " + "code LIKE 'PAGE_%' OR " + "code LIKE '%_FILTER_%' OR " + "code LIKE 'SEARCH_%' OR " + "code LIKE 'ACTIVE_%' OR " + "code LIKE 'DUPLICATE_%' OR " + "code LIKE 'EMPTY_%' OR " + "code LIKE 'NULL_%' OR " + "code LIKE 'OTHER_%'";
        }

        // 默认情况：删除所有数据（谨慎使用）
        log.warn("Using DELETE FROM {} without conditions - this will delete all data", tableName);
        return "DELETE FROM " + tableName;
    }
}