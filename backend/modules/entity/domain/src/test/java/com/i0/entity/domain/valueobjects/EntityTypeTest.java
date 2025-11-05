package com.i0.entity.domain.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.*;

/**
 * EntityType枚举单元测试
 */
@DisplayName("EntityType枚举测试")
class EntityTypeTest {
    
    @Test
    @DisplayName("应该包含所有预期的实体类型")
    void shouldContainAllExpectedEntityTypes() {
        // When
        EntityType[] entityTypes = EntityType.values();
        
        // Then
        assertThat(entityTypes).hasSize(3);
        assertThat(entityTypes).containsExactlyInAnyOrder(
                EntityType.BIPO_ENTITY,
                EntityType.CLIENT_ENTITY,
                EntityType.VENDOR_ENTITY
        );
    }
    
    @Test
    @DisplayName("BIPO_ENTITY应该有正确的属性")
    void shouldHaveCorrectPropertiesForBipoEntity() {
        // Given
        EntityType entityType = EntityType.BIPO_ENTITY;
        
        // Then
        assertThat(entityType.getDisplayName()).isEqualTo("BIPO Entity");
        assertThat(entityType.getDescription()).isEqualTo("BIPO公司内部实体");
        assertThat(entityType.name()).isEqualTo("BIPO_ENTITY");
    }
    
    @Test
    @DisplayName("CLIENT_ENTITY应该有正确的属性")
    void shouldHaveCorrectPropertiesForClientEntity() {
        // Given
        EntityType entityType = EntityType.CLIENT_ENTITY;
        
        // Then
        assertThat(entityType.getDisplayName()).isEqualTo("Client Entity");
        assertThat(entityType.getDescription()).isEqualTo("外部客户公司实体");
        assertThat(entityType.name()).isEqualTo("CLIENT_ENTITY");
    }
    
    @Test
    @DisplayName("VENDOR_ENTITY应该有正确的属性")
    void shouldHaveCorrectPropertiesForVendorEntity() {
        // Given
        EntityType entityType = EntityType.VENDOR_ENTITY;
        
        // Then
        assertThat(entityType.getDisplayName()).isEqualTo("Vendor Entity");
        assertThat(entityType.getDescription()).isEqualTo("外部供应商实体");
        assertThat(entityType.name()).isEqualTo("VENDOR_ENTITY");
    }
    
    @ParameterizedTest
    @EnumSource(EntityType.class)
    @DisplayName("所有实体类型都应该有非空的显示名称")
    void shouldHaveNonNullDisplayName(EntityType entityType) {
        // Then
        assertThat(entityType.getDisplayName())
                .isNotNull()
                .isNotBlank();
    }
    
    @ParameterizedTest
    @EnumSource(EntityType.class)
    @DisplayName("所有实体类型都应该有非空的描述")
    void shouldHaveNonNullDescription(EntityType entityType) {
        // Then
        assertThat(entityType.getDescription())
                .isNotNull()
                .isNotBlank();
    }
    
    @Test
    @DisplayName("应该能够通过字符串值获取枚举")
    void shouldGetEnumByStringValue() {
        // When & Then
        assertThat(EntityType.valueOf("BIPO_ENTITY")).isEqualTo(EntityType.BIPO_ENTITY);
        assertThat(EntityType.valueOf("CLIENT_ENTITY")).isEqualTo(EntityType.CLIENT_ENTITY);
        assertThat(EntityType.valueOf("VENDOR_ENTITY")).isEqualTo(EntityType.VENDOR_ENTITY);
    }
    
    @Test
    @DisplayName("无效的字符串值应该抛出异常")
    void shouldThrowExceptionForInvalidStringValue() {
        // When & Then
        assertThatThrownBy(() -> EntityType.valueOf("INVALID_ENTITY"))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    @DisplayName("应该能够判断是否为BIPO实体")
    void shouldCheckIfBipoEntity() {
        // Then
        assertThat(EntityType.BIPO_ENTITY.isBipoEntity()).isTrue();
        assertThat(EntityType.CLIENT_ENTITY.isBipoEntity()).isFalse();
        assertThat(EntityType.VENDOR_ENTITY.isBipoEntity()).isFalse();
    }
    
    @Test
    @DisplayName("应该能够判断是否为客户实体")
    void shouldCheckIfClientEntity() {
        // Then
        assertThat(EntityType.CLIENT_ENTITY.isClientEntity()).isTrue();
        assertThat(EntityType.BIPO_ENTITY.isClientEntity()).isFalse();
        assertThat(EntityType.VENDOR_ENTITY.isClientEntity()).isFalse();
    }
    
    @Test
    @DisplayName("应该能够判断是否为供应商实体")
    void shouldCheckIfVendorEntity() {
        // Then
        assertThat(EntityType.VENDOR_ENTITY.isVendorEntity()).isTrue();
        assertThat(EntityType.BIPO_ENTITY.isVendorEntity()).isFalse();
        assertThat(EntityType.CLIENT_ENTITY.isVendorEntity()).isFalse();
    }
    
    @ParameterizedTest
    @EnumSource(EntityType.class)
    @DisplayName("toString应该返回显示名称")
    void shouldReturnDisplayNameInToString(EntityType entityType) {
        // When
        String toString = entityType.toString();
        
        // Then
        assertThat(toString).isEqualTo(entityType.getDisplayName());
    }
}