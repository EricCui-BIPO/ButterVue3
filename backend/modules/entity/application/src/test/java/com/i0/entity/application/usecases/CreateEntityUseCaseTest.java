package com.i0.entity.application.usecases;

import com.i0.entity.application.dto.input.CreateEntityInput;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CreateEntityUseCase单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateEntityUseCase测试")
class CreateEntityUseCaseTest {

    @Mock
    private EntityRepository entityRepository;

    @InjectMocks
    private CreateEntityUseCase createEntityUseCase;

    private CreateEntityInput createRequest;
    private Entity testEntity;
    private final String testEntityId = "test-entity-id";

    @BeforeEach
    void setUp() {
        createRequest = CreateEntityInput.builder()
                .name("测试实体")
                .entityType(EntityType.BIPO_ENTITY)
                .code("TEST_001")
                .description("测试实体描述")
                .build();

        testEntity = Entity.create(
                "测试实体",
                EntityType.BIPO_ENTITY,
                "TEST_001",
                "测试实体描述"
        );
        testEntity.setId(testEntityId);
    }

    @Test
    @DisplayName("应该能够成功创建实体")
    void shouldCreateEntitySuccessfully() {
        // Given
        when(entityRepository.existsByName(createRequest.getName())).thenReturn(false);
        when(entityRepository.existsByCode(createRequest.getCode())).thenReturn(false);
        when(entityRepository.save(any(Entity.class))).thenReturn(testEntity);

        // When
        EntityOutput response = createEntityUseCase.execute(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(createRequest.getName());
        assertThat(response.getEntityType()).isEqualTo(createRequest.getEntityType());
        assertThat(response.getCode()).isEqualTo(createRequest.getCode());
        assertThat(response.getDescription()).isEqualTo(createRequest.getDescription());

        verify(entityRepository).existsByName(createRequest.getName());
        verify(entityRepository).existsByCode(createRequest.getCode());
        verify(entityRepository).save(any(Entity.class));
    }

    @Test
    @DisplayName("创建实体时名称已存在应该抛出异常")
    void shouldThrowExceptionWhenNameAlreadyExists() {
        // Given
        when(entityRepository.existsByName(createRequest.getName())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> createEntityUseCase.execute(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("实体名称已存在: " + createRequest.getName());

        verify(entityRepository).existsByName(createRequest.getName());
        verify(entityRepository, never()).save(any(Entity.class));
    }

    @Test
    @DisplayName("创建实体时代码已存在应该抛出异常")
    void shouldThrowExceptionWhenCodeAlreadyExists() {
        // Given
        when(entityRepository.existsByName(createRequest.getName())).thenReturn(false);
        when(entityRepository.existsByCode(createRequest.getCode())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> createEntityUseCase.execute(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("实体代码已存在: " + createRequest.getCode());

        verify(entityRepository).existsByName(createRequest.getName());
        verify(entityRepository).existsByCode(createRequest.getCode());
        verify(entityRepository, never()).save(any(Entity.class));
    }

    @Test
    @DisplayName("创建实体时没有提供代码也应该成功")
    void shouldCreateEntitySuccessfullyWhenCodeIsNull() {
        // Given
        CreateEntityInput requestWithoutCode = CreateEntityInput.builder()
                .name("测试实体")
                .entityType(EntityType.BIPO_ENTITY)
                .code(null)
                .description("测试实体描述")
                .build();

        when(entityRepository.existsByName(requestWithoutCode.getName())).thenReturn(false);
        when(entityRepository.save(any(Entity.class))).thenReturn(testEntity);

        // When
        EntityOutput response = createEntityUseCase.execute(requestWithoutCode);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(requestWithoutCode.getName());

        verify(entityRepository).existsByName(requestWithoutCode.getName());
        verify(entityRepository, never()).existsByCode(anyString());
        verify(entityRepository).save(any(Entity.class));
    }

    @Test
    @DisplayName("创建实体时代码为空字符串也应该成功")
    void shouldCreateEntitySuccessfullyWhenCodeIsEmpty() {
        // Given
        CreateEntityInput requestWithEmptyCode = CreateEntityInput.builder()
                .name("测试实体")
                .entityType(EntityType.BIPO_ENTITY)
                .code("")
                .description("测试实体描述")
                .build();

        when(entityRepository.existsByName(requestWithEmptyCode.getName())).thenReturn(false);
        when(entityRepository.save(any(Entity.class))).thenReturn(testEntity);

        // When
        EntityOutput response = createEntityUseCase.execute(requestWithEmptyCode);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(requestWithEmptyCode.getName());

        verify(entityRepository).existsByName(requestWithEmptyCode.getName());
        verify(entityRepository, never()).existsByCode(anyString());
        verify(entityRepository).save(any(Entity.class));
    }
}