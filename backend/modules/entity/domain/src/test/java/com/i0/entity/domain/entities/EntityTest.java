package com.i0.entity.domain.entities;

import com.i0.entity.domain.valueobjects.EntityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Entity实体类单元测试
 */
@DisplayName("Entity实体类测试")
class EntityTest {
    
    private Entity entity;
    private final String validName = "测试实体";
    private final EntityType validType = EntityType.BIPO_ENTITY;
    private final String validCode = "TEST_001";
    private final String validDescription = "这是一个测试实体";
    
    @BeforeEach
    void setUp() {
        entity = new Entity();
    }
    
    @Test
    @DisplayName("应该能够创建有效的实体")
    void shouldCreateValidEntity() {
        // When
        Entity createdEntity = Entity.create(validName, validType, validCode, validDescription);
        
        // Then
        assertThat(createdEntity).isNotNull();
        assertThat(createdEntity.getName()).isEqualTo(validName);
        assertThat(createdEntity.getEntityType()).isEqualTo(validType);
        assertThat(createdEntity.getCode()).isEqualTo(validCode);
        assertThat(createdEntity.getDescription()).isEqualTo(validDescription);
        assertThat(createdEntity.isActive()).isTrue();
        assertThat(createdEntity.getCreatedAt()).isNotNull();
        assertThat(createdEntity.getUpdatedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("应该能够创建没有代码的实体")
    void shouldCreateEntityWithoutCode() {
        // When
        Entity createdEntity = Entity.create(validName, validType, null, validDescription);
        
        // Then
        assertThat(createdEntity).isNotNull();
        assertThat(createdEntity.getName()).isEqualTo(validName);
        assertThat(createdEntity.getEntityType()).isEqualTo(validType);
        assertThat(createdEntity.getCode()).isNull();
        assertThat(createdEntity.getDescription()).isEqualTo(validDescription);
        assertThat(createdEntity.isActive()).isTrue();
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("创建实体时名称不能为空或空白")
    void shouldThrowExceptionWhenNameIsNullOrBlank(String invalidName) {
        // When & Then
        assertThatThrownBy(() -> Entity.create(invalidName, validType, validCode, validDescription))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("实体名称不能为空");
    }
    
    @Test
    @DisplayName("创建实体时实体类型不能为空")
    void shouldThrowExceptionWhenEntityTypeIsNull() {
        // When & Then
        assertThatThrownBy(() -> Entity.create(validName, null, validCode, validDescription))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("实体类型不能为空");
    }
    
    @ParameterizedTest
    @EnumSource(EntityType.class)
    @DisplayName("应该支持所有实体类型")
    void shouldSupportAllEntityTypes(EntityType entityType) {
        // When
        Entity createdEntity = Entity.create(validName, entityType, validCode, validDescription);
        
        // Then
        assertThat(createdEntity.getEntityType()).isEqualTo(entityType);
    }
    
    @Test
    @DisplayName("应该能够更新实体信息")
    void shouldUpdateEntityInfo() {
        // Given
        Entity createdEntity = Entity.create(validName, validType, validCode, validDescription);
        LocalDateTime originalUpdatedAt = createdEntity.getUpdatedAt();
        String newName = "更新后的实体名称";
        String newDescription = "更新后的描述";
        
        // When
        createdEntity.update(newName, newDescription);
        
        // Then
        assertThat(createdEntity.getName()).isEqualTo(newName);
        assertThat(createdEntity.getDescription()).isEqualTo(newDescription);
        assertThat(createdEntity.getUpdatedAt()).isAfter(originalUpdatedAt);
    }
    
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("更新实体时名称不能为空或空白")
    void shouldThrowExceptionWhenUpdateNameIsNullOrBlank(String invalidName) {
        // Given
        Entity createdEntity = Entity.create(validName, validType, validCode, validDescription);
        
        // When & Then
        assertThatThrownBy(() -> createdEntity.update(invalidName, "新描述"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("实体名称不能为空");
    }
    
    @Test
    @DisplayName("应该能够激活实体")
    void shouldActivateEntity() {
        // Given
        Entity createdEntity = Entity.create(validName, validType, validCode, validDescription);
        createdEntity.deactivate(); // 先停用
        LocalDateTime originalUpdatedAt = createdEntity.getUpdatedAt();
        
        // When
        createdEntity.activate();
        
        // Then
        assertThat(createdEntity.isActive()).isTrue();
        assertThat(createdEntity.getUpdatedAt()).isAfter(originalUpdatedAt);
    }
    
    @Test
    @DisplayName("应该能够停用实体")
    void shouldDeactivateEntity() {
        // Given
        Entity createdEntity = Entity.create(validName, validType, validCode, validDescription);
        LocalDateTime originalUpdatedAt = createdEntity.getUpdatedAt();
        
        // When
        createdEntity.deactivate();
        
        // Then
        assertThat(createdEntity.isActive()).isFalse();
        assertThat(createdEntity.getUpdatedAt()).isAfter(originalUpdatedAt);
    }
    
    @Test
    @DisplayName("应该能够验证实体有效性")
    void shouldValidateEntity() {
        // Given
        Entity validEntity = Entity.create(validName, validType, validCode, validDescription);
        Entity invalidEntity = new Entity();
        
        // When & Then
        assertThatCode(validEntity::validate).doesNotThrowAnyException();
        assertThatThrownBy(invalidEntity::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    @DisplayName("equals和hashCode应该基于ID")
    void shouldImplementEqualsAndHashCodeBasedOnId() {
        // Given
        Entity entity1 = Entity.create(validName, validType, validCode, validDescription);
        entity1.setId("test-id-1");
        
        Entity entity2 = Entity.create("不同名称", EntityType.CLIENT_ENTITY, "DIFF_CODE", "不同描述");
        entity2.setId("test-id-1");
        
        Entity entity3 = Entity.create(validName, validType, validCode, validDescription);
        entity3.setId("test-id-2");
        
        // When & Then
        assertThat(entity1).isEqualTo(entity2); // 相同ID
        assertThat(entity1).isNotEqualTo(entity3); // 不同ID
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
        assertThat(entity1.hashCode()).isNotEqualTo(entity3.hashCode());
    }
    
    @Test
    @DisplayName("toString应该包含关键信息")
    void shouldImplementToString() {
        // Given
        Entity createdEntity = Entity.create(validName, validType, validCode, validDescription);
        createdEntity.setId("test-id");
        
        // When
        String toString = createdEntity.toString();
        
        // Then
        assertThat(toString)
                .contains("test-id")
                .contains(validName)
                .contains(validType.toString())
                .contains(validCode);
    }
    
    @Test
    @DisplayName("应该正确处理空描述")
    void shouldHandleNullDescription() {
        // When
        Entity createdEntity = Entity.create(validName, validType, validCode, null);
        
        // Then
        assertThat(createdEntity.getDescription()).isNull();
        assertThatCode(createdEntity::validate).doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("应该正确处理空代码")
    void shouldHandleNullCode() {
        // When
        Entity createdEntity = Entity.create(validName, validType, null, validDescription);
        
        // Then
        assertThat(createdEntity.getCode()).isNull();
        assertThatCode(createdEntity::validate).doesNotThrowAnyException();
    }
}