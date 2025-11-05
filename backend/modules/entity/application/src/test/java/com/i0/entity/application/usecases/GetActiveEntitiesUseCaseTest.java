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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * GetActiveEntitiesUseCase单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetActiveEntitiesUseCase测试")
class GetActiveEntitiesUseCaseTest {

    @Mock
    private EntityRepository entityRepository;

    @InjectMocks
    private GetActiveEntitiesUseCase getActiveEntitiesUseCase;

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
        testEntity.activate(); // 确保实体是激活状态
    }

    @Test
    @DisplayName("应该能够根据实体类型获取激活的实体")
    void shouldGetActiveEntitiesByType() {
        // Given
        List<Entity> entities = Arrays.asList(testEntity);
        when(entityRepository.findActiveByEntityType(EntityType.BIPO_ENTITY)).thenReturn(entities);

        // When
        List<EntityOutput> responses = getActiveEntitiesUseCase.getByType(EntityType.BIPO_ENTITY);

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(testEntityId);
        assertThat(responses.get(0).getEntityType()).isEqualTo(EntityType.BIPO_ENTITY);
        assertThat(responses.get(0).getActive()).isTrue();

        verify(entityRepository).findActiveByEntityType(EntityType.BIPO_ENTITY);
    }

    @Test
    @DisplayName("应该能够获取所有激活的实体")
    void shouldGetAllActiveEntities() {
        // Given
        List<Entity> entities = Arrays.asList(testEntity);
        when(entityRepository.findAllActive()).thenReturn(entities);

        // When
        List<EntityOutput> responses = getActiveEntitiesUseCase.getAll();

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getId()).isEqualTo(testEntityId);
        assertThat(responses.get(0).getActive()).isTrue();

        verify(entityRepository).findAllActive();
    }

    @Test
    @DisplayName("getByTypeOrNull应该根据实体类型调用正确的方法")
    void shouldGetByTypeOrNullCallCorrectMethodWhenTypeIsNotNull() {
        // Given
        List<Entity> entities = Arrays.asList(testEntity);
        when(entityRepository.findActiveByEntityType(EntityType.BIPO_ENTITY)).thenReturn(entities);

        // When
        List<EntityOutput> responses = getActiveEntitiesUseCase.getByTypeOrNull(EntityType.BIPO_ENTITY);

        // Then
        assertThat(responses).hasSize(1);
        verify(entityRepository).findActiveByEntityType(EntityType.BIPO_ENTITY);
        verify(entityRepository, never()).findAllActive();
    }

    @Test
    @DisplayName("getByTypeOrNull应该获取所有激活实体当类型为null时")
    void shouldGetByTypeOrNullCallGetAllWhenTypeIsNull() {
        // Given
        List<Entity> entities = Arrays.asList(testEntity);
        when(entityRepository.findAllActive()).thenReturn(entities);

        // When
        List<EntityOutput> responses = getActiveEntitiesUseCase.getByTypeOrNull(null);

        // Then
        assertThat(responses).hasSize(1);
        verify(entityRepository).findAllActive();
        verify(entityRepository, never()).findActiveByEntityType(any());
    }

    @Test
    @DisplayName("当没有激活实体时应该返回空列表")
    void shouldReturnEmptyListWhenNoActiveEntities() {
        // Given
        when(entityRepository.findAllActive()).thenReturn(Arrays.asList());

        // When
        List<EntityOutput> responses = getActiveEntitiesUseCase.getAll();

        // Then
        assertThat(responses).isEmpty();
        verify(entityRepository).findAllActive();
    }

    @Test
    @DisplayName("当指定类型没有激活实体时应该返回空列表")
    void shouldReturnEmptyListWhenNoActiveEntitiesForType() {
        // Given
        when(entityRepository.findActiveByEntityType(EntityType.BIPO_ENTITY)).thenReturn(Arrays.asList());

        // When
        List<EntityOutput> responses = getActiveEntitiesUseCase.getByType(EntityType.BIPO_ENTITY);

        // Then
        assertThat(responses).isEmpty();
        verify(entityRepository).findActiveByEntityType(EntityType.BIPO_ENTITY);
    }
}