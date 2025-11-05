package com.i0.entity.application.usecases;

import com.i0.entity.application.dto.output.EntityOutput;
import com.i0.entity.domain.entities.Entity;
import com.i0.entity.domain.repositories.EntityRepository;
import com.i0.entity.domain.valueobjects.EntityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * GetEntityUseCase单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetEntityUseCase测试")
class GetEntityUseCaseTest {

    @Mock
    private EntityRepository entityRepository;

    @InjectMocks
    private GetEntityUseCase getEntityUseCase;

    private Entity testEntity;
    private final String testEntityId = "test-entity-id";

    @BeforeEach
    void setUp() {
        testEntity = Entity.create(
                "测试实体",
                EntityType.BIPO_ENTITY,
                "TEST_001",
                "测试实体描述"
        );
        testEntity.setId(testEntityId);
    }

    @Test
    @DisplayName("应该能够根据ID获取实体")
    void shouldGetEntityById() {
        // Given
        when(entityRepository.findById(testEntityId)).thenReturn(Optional.of(testEntity));

        // When
        EntityOutput response = getEntityUseCase.execute(testEntityId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testEntityId);
        assertThat(response.getName()).isEqualTo(testEntity.getName());
        assertThat(response.getEntityType()).isEqualTo(testEntity.getEntityType());
        assertThat(response.getCode()).isEqualTo(testEntity.getCode());
        assertThat(response.getDescription()).isEqualTo(testEntity.getDescription());
        assertThat(response.getActive()).isEqualTo(testEntity.getActive());

        verify(entityRepository).findById(testEntityId);
    }

    @Test
    @DisplayName("根据不存在的ID获取实体应该抛出异常")
    void shouldThrowExceptionWhenEntityNotFound() {
        // Given
        when(entityRepository.findById(testEntityId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> getEntityUseCase.execute(testEntityId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("实体不存在: " + testEntityId);

        verify(entityRepository).findById(testEntityId);
    }

    @Test
    @DisplayName("当传入null ID时应该抛出异常")
    void shouldThrowExceptionWhenIdIsNull() {
        // Given
        when(entityRepository.findById(null)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> getEntityUseCase.execute(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("实体不存在: null");

        verify(entityRepository).findById(null);
    }

    @Test
    @DisplayName("当传入空字符串 ID时应该抛出异常")
    void shouldThrowExceptionWhenIdIsEmpty() {
        // Given
        when(entityRepository.findById("")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> getEntityUseCase.execute(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("实体不存在: ");

        verify(entityRepository).findById("");
    }
}