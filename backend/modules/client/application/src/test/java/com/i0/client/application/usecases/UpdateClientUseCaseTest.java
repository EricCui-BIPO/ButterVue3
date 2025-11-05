package com.i0.client.application.usecases;

import com.i0.client.application.dto.input.UpdateClientInput;
import com.i0.client.application.dto.output.ClientOutput;
import com.i0.client.domain.entities.Client;
import com.i0.client.domain.exceptions.ClientNotFoundException;
import com.i0.client.domain.repositories.ClientRepository;
import com.i0.client.domain.services.ClientDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UpdateClientUseCase单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateClientUseCase测试")
class UpdateClientUseCaseTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientDomainService clientDomainService;

    @InjectMocks
    private UpdateClientUseCase updateClientUseCase;

    private Client existingClient;
    private UpdateClientInput updateRequest;
    private final String testClientId = "client-001";

    @BeforeEach
    void setUp() {
        existingClient = Client.create("原始客户", "ORIGINAL_CLIENT", "原始别名", "CN", "原始描述");
        existingClient.setId(testClientId);
        existingClient.setCreatedAt(LocalDateTime.now().minusDays(1));
        existingClient.setUpdatedAt(LocalDateTime.now().minusDays(1));

        updateRequest = UpdateClientInput.builder()
                .name("更新后的客户")
                .code("UPDATED_CLIENT")
                .aliasName("更新后的别名")
                .locationId("US")
                .description("更新后的描述")
                .build();
    }

    @Test
    @DisplayName("应该能够成功更新客户信息")
    void shouldUpdateClientSuccessfully() {
        // Given
        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(existingClient));
        doNothing().when(clientDomainService).validateNameUniqueness(updateRequest.getName(), testClientId);
        doNothing().when(clientDomainService).validateCodeUniqueness(any(), eq(testClientId));
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        // When
        ClientOutput result = updateClientUseCase.execute(testClientId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(updateRequest.getName());
        assertThat(result.getAliasName()).isEqualTo(updateRequest.getAliasName());
        assertThat(result.getLocationId()).isEqualTo(updateRequest.getLocationId());
        assertThat(result.getDescription()).isEqualTo(updateRequest.getDescription());

        verify(clientRepository).findById(testClientId);
        verify(clientDomainService).validateNameUniqueness(updateRequest.getName(), testClientId);
        verify(clientDomainService).validateCodeUniqueness(any(), eq(testClientId));
        verify(clientRepository).save(existingClient);
    }

    @Test
    @DisplayName("更新客户时客户不存在应该抛出异常")
    void shouldThrowExceptionWhenClientNotFound() {
        // Given
        String nonExistingId = "non-existing-id";
        when(clientRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> updateClientUseCase.execute(nonExistingId, updateRequest))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("客户不存在: " + nonExistingId);

        verify(clientRepository).findById(nonExistingId);
        verify(clientDomainService, never()).validateNameUniqueness(anyString(), anyString());
        verify(clientDomainService, never()).validateCodeUniqueness(anyString(), anyString());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("更新客户时名称已存在应该抛出异常")
    void shouldThrowExceptionWhenNameAlreadyExists() {
        // Given
        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(existingClient));
        doThrow(new RuntimeException("客户名称已存在: 更新后的客户"))
                .when(clientDomainService).validateNameUniqueness(updateRequest.getName(), testClientId);

        // When & Then
        assertThatThrownBy(() -> updateClientUseCase.execute(testClientId, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("客户名称已存在: 更新后的客户");

        verify(clientRepository).findById(testClientId);
        verify(clientDomainService).validateNameUniqueness(updateRequest.getName(), testClientId);
        verify(clientDomainService, never()).validateCodeUniqueness(anyString(), anyString());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("应该能够更新客户的别名为null")
    void shouldUpdateClientWithNullAliasName() {
        // Given
        UpdateClientInput requestWithNullAlias = UpdateClientInput.builder()
                .name("更新后的客户")
                .aliasName(null)
                .locationId("US")
                .description("更新后的描述")
                .build();

        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(existingClient));
        doNothing().when(clientDomainService).validateNameUniqueness(requestWithNullAlias.getName(), testClientId);
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        // When
        ClientOutput result = updateClientUseCase.execute(testClientId, requestWithNullAlias);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(requestWithNullAlias.getName());
        assertThat(result.getAliasName()).isNull();
        assertThat(result.getLocationId()).isEqualTo(requestWithNullAlias.getLocationId());
        assertThat(result.getDescription()).isEqualTo(requestWithNullAlias.getDescription());

        verify(clientRepository).save(argThat(client ->
            client.getName().equals(requestWithNullAlias.getName()) &&
            client.getAliasName() == null
        ));
    }

    @Test
    @DisplayName("应该能够更新客户的描述为null")
    void shouldUpdateClientWithNullDescription() {
        // Given
        UpdateClientInput requestWithNullDescription = UpdateClientInput.builder()
                .name("更新后的客户")
                .aliasName("更新后的别名")
                .locationId("US")
                .description(null)
                .build();

        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(existingClient));
        doNothing().when(clientDomainService).validateNameUniqueness(requestWithNullDescription.getName(), testClientId);
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        // When
        ClientOutput result = updateClientUseCase.execute(testClientId, requestWithNullDescription);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(requestWithNullDescription.getName());
        assertThat(result.getAliasName()).isEqualTo(requestWithNullDescription.getAliasName());
        assertThat(result.getLocationId()).isEqualTo(requestWithNullDescription.getLocationId());
        assertThat(result.getDescription()).isNull();

        verify(clientRepository).save(argThat(client ->
            client.getName().equals(requestWithNullDescription.getName()) &&
            client.getDescription() == null
        ));
    }

    @Test
    @DisplayName("应该正确更新客户的时间戳")
    void shouldUpdateClientTimestampsCorrectly() {
        // Given
        LocalDateTime originalUpdatedAt = existingClient.getUpdatedAt();

        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(existingClient));
        doNothing().when(clientDomainService).validateNameUniqueness(updateRequest.getName(), testClientId);
        doNothing().when(clientDomainService).validateCodeUniqueness(any(), eq(testClientId));
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client savedClient = invocation.getArgument(0);
            // 模拟更新时间戳的变化
            savedClient.setUpdatedAt(LocalDateTime.now());
            return savedClient;
        });

        // When
        ClientOutput result = updateClientUseCase.execute(testClientId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUpdatedAt()).isAfter(originalUpdatedAt);
        assertThat(result.getCreatedAt()).isEqualTo(existingClient.getCreatedAt()); // 创建时间不应该改变

        verify(clientRepository).save(argThat(client ->
            client.getUpdatedAt().isAfter(originalUpdatedAt) &&
            client.getCreatedAt().equals(existingClient.getCreatedAt())
        ));
    }

    @Test
    @DisplayName("更新时应该保持客户代码不变")
    void shouldKeepClientCodeUnchangedDuringUpdate() {
        // Given
        String originalCode = existingClient.getCode();

        // 创建一个不包含代码的更新请求
        UpdateClientInput requestWithoutCode = UpdateClientInput.builder()
                .name("更新后的客户")
                .aliasName("更新后的别名")
                .locationId("US")
                .description("更新后的描述")
                .build();

        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(existingClient));
        doNothing().when(clientDomainService).validateNameUniqueness(requestWithoutCode.getName(), testClientId);
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        // When
        ClientOutput result = updateClientUseCase.execute(testClientId, requestWithoutCode);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(originalCode); // 代码应该保持不变

        verify(clientRepository).save(argThat(client ->
            client.getCode().equals(originalCode)
        ));

        // 验证没有调用代码唯一性验证
        verify(clientDomainService, never()).validateCodeUniqueness(anyString(), eq(testClientId));
    }

    @Test
    @DisplayName("应该能够更新客户代码")
    void shouldUpdateClientCodeSuccessfully() {
        // Given
        String newCode = "NEW_UPDATED_CODE";

        // 创建一个包含新代码的更新请求
        UpdateClientInput requestWithNewCode = UpdateClientInput.builder()
                .name("更新后的客户")
                .code(newCode)
                .aliasName("更新后的别名")
                .locationId("US")
                .description("更新后的描述")
                .build();

        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(existingClient));
        doNothing().when(clientDomainService).validateNameUniqueness(requestWithNewCode.getName(), testClientId);
        doNothing().when(clientDomainService).validateCodeUniqueness(newCode, testClientId);
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        // When
        ClientOutput result = updateClientUseCase.execute(testClientId, requestWithNewCode);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getCode()).isEqualTo(newCode); // 代码应该被更新

        verify(clientRepository).save(argThat(client ->
            client.getCode().equals(newCode)
        ));

        // 验证调用了代码唯一性验证
        verify(clientDomainService).validateCodeUniqueness(newCode, testClientId);
    }

    @Test
    @DisplayName("更新时应该保持客户激活状态不变")
    void shouldKeepClientActiveStatusUnchangedDuringUpdate() {
        // Given
        Boolean originalActiveStatus = existingClient.getActive();

        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(existingClient));
        doNothing().when(clientDomainService).validateNameUniqueness(updateRequest.getName(), testClientId);
        doNothing().when(clientDomainService).validateCodeUniqueness(any(), eq(testClientId));
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        // When
        ClientOutput result = updateClientUseCase.execute(testClientId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getActive()).isEqualTo(originalActiveStatus); // 激活状态应该保持不变

        verify(clientRepository).save(argThat(client ->
            client.getActive() == originalActiveStatus
        ));
    }

    @Test
    @DisplayName("更新时应该保持客户版本号不变")
    void shouldKeepClientVersionUnchangedDuringUpdate() {
        // Given
        Long originalVersion = existingClient.getVersion();

        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(existingClient));
        doNothing().when(clientDomainService).validateNameUniqueness(updateRequest.getName(), testClientId);
        doNothing().when(clientDomainService).validateCodeUniqueness(any(), eq(testClientId));
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        // When
        ClientOutput result = updateClientUseCase.execute(testClientId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getVersion()).isEqualTo(originalVersion); // 版本号应该保持不变

        verify(clientRepository).save(argThat(client ->
            client.getVersion() == originalVersion
        ));
    }

    @Test
    @DisplayName("应该正确处理数据库保存失败")
    void shouldHandleDatabaseSaveFailure() {
        // Given
        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(existingClient));
        doNothing().when(clientDomainService).validateNameUniqueness(updateRequest.getName(), testClientId);
        doNothing().when(clientDomainService).validateCodeUniqueness(any(), eq(testClientId));
        when(clientRepository.save(any(Client.class)))
                .thenThrow(new RuntimeException("数据库保存失败"));

        // When & Then
        assertThatThrownBy(() -> updateClientUseCase.execute(testClientId, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("数据库保存失败");

        verify(clientRepository).findById(testClientId);
        verify(clientDomainService).validateNameUniqueness(updateRequest.getName(), testClientId);
        verify(clientDomainService).validateCodeUniqueness(any(), eq(testClientId));
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("应该正确传递所有输入参数到更新方法")
    void shouldPassAllInputParametersToUpdateMethod() {
        // Given
        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(existingClient));
        doNothing().when(clientDomainService).validateNameUniqueness(updateRequest.getName(), testClientId);
        doNothing().when(clientDomainService).validateCodeUniqueness(any(), eq(testClientId));
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        // When
        updateClientUseCase.execute(testClientId, updateRequest);

        // Then
        verify(clientRepository).save(argThat(client ->
            client.getName().equals(updateRequest.getName()) &&
            client.getAliasName().equals(updateRequest.getAliasName()) &&
            client.getLocationId().equals(updateRequest.getLocationId()) &&
            client.getDescription().equals(updateRequest.getDescription())
        ));
    }

    @Test
    @DisplayName("应该正确转换更新后的客户为输出DTO")
    void shouldConvertUpdatedClientToOutputDtoCorrectly() {
        // Given
        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(existingClient));
        doNothing().when(clientDomainService).validateNameUniqueness(updateRequest.getName(), testClientId);
        doNothing().when(clientDomainService).validateCodeUniqueness(any(), eq(testClientId));
        when(clientRepository.save(any(Client.class))).thenReturn(existingClient);

        // When
        ClientOutput result = updateClientUseCase.execute(testClientId, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(existingClient.getId());
        assertThat(result.getName()).isEqualTo(updateRequest.getName());
        assertThat(result.getAliasName()).isEqualTo(updateRequest.getAliasName());
        assertThat(result.getLocationId()).isEqualTo(updateRequest.getLocationId());
        assertThat(result.getDescription()).isEqualTo(updateRequest.getDescription());
        assertThat(result.getCode()).isEqualTo(existingClient.getCode()); // 代码未改变
        assertThat(result.getActive()).isEqualTo(existingClient.getActive()); // 状态未改变
    }

    @Test
    @DisplayName("应该处理null和空ID参数")
    void shouldHandleNullAndEmptyIdParameters() {
        // Given
        when(clientRepository.findById(isNull())).thenReturn(Optional.empty());
        when(clientRepository.findById("")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> updateClientUseCase.execute(null, updateRequest))
                .isInstanceOf(ClientNotFoundException.class);

        assertThatThrownBy(() -> updateClientUseCase.execute("", updateRequest))
                .isInstanceOf(ClientNotFoundException.class);

        verify(clientRepository).findById(null);
        verify(clientRepository).findById("");
    }

    @Test
    @DisplayName("应该处理null输入参数")
    void shouldHandleNullInputParameter() {
        // Given
        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(existingClient));

        // When & Then
        assertThatThrownBy(() -> updateClientUseCase.execute(testClientId, null))
                .isInstanceOf(NullPointerException.class);

        verify(clientRepository).findById(testClientId);
        verify(clientDomainService, never()).validateNameUniqueness(anyString(), anyString());
        verify(clientDomainService, never()).validateCodeUniqueness(anyString(), anyString());
        verify(clientRepository, never()).save(any(Client.class));
    }
}