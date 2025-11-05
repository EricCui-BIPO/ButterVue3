package com.i0.client.application.usecases;

import com.i0.client.application.dto.output.ClientOutput;
import com.i0.client.application.dto.output.ClientDetailOutput;
import com.i0.client.domain.entities.Client;
import com.i0.client.domain.exceptions.ClientNotFoundException;
import com.i0.client.domain.repositories.ClientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * GetClientUseCase单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetClientUseCase测试")
class GetClientUseCaseTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private GetClientUseCase getClientUseCase;

    private Client testClient;
    private final String testClientId = "client-001";
    private final String testClientName = "测试客户";
    private final String testClientCode = "CLIENT_001";

    @BeforeEach
    void setUp() {
        testClient = Client.create(testClientName, testClientCode, "测试别名", "CN", "测试描述");
        testClient.setId(testClientId);
    }

    @Test
    @DisplayName("应该能够根据ID成功获取客户")
    void shouldGetClientByIdSuccessfully() {
        // Given
        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(testClient));

        // When
        ClientDetailOutput result = getClientUseCase.getById(testClientId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testClientId);
        assertThat(result.getName()).isEqualTo(testClientName);
        assertThat(result.getCode()).isEqualTo(testClientCode);

        verify(clientRepository).findById(testClientId);
    }

    @Test
    @DisplayName("根据ID获取客户时客户不存在应该抛出异常")
    void shouldThrowExceptionWhenClientNotFoundById() {
        // Given
        String nonExistingId = "non-existing-id";
        when(clientRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> getClientUseCase.getById(nonExistingId))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("客户不存在: " + nonExistingId);

        verify(clientRepository).findById(nonExistingId);
    }

    @Test
    @DisplayName("应该能够根据名称成功获取客户")
    void shouldGetClientByNameSuccessfully() {
        // Given
        when(clientRepository.findByName(testClientName)).thenReturn(Optional.of(testClient));

        // When
        ClientDetailOutput result = getClientUseCase.getByName(testClientName);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testClientId);
        assertThat(result.getName()).isEqualTo(testClientName);
        assertThat(result.getCode()).isEqualTo(testClientCode);

        verify(clientRepository).findByName(testClientName);
    }

    @Test
    @DisplayName("根据名称获取客户时客户不存在应该抛出异常")
    void shouldThrowExceptionWhenClientNotFoundByName() {
        // Given
        String nonExistingName = "不存在的客户";
        when(clientRepository.findByName(nonExistingName)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> getClientUseCase.getByName(nonExistingName))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("客户不存在 (name=" + nonExistingName + ")");

        verify(clientRepository).findByName(nonExistingName);
    }

    @Test
    @DisplayName("应该能够根据代码成功获取客户")
    void shouldGetClientByCodeSuccessfully() {
        // Given
        when(clientRepository.findByCode(testClientCode)).thenReturn(Optional.of(testClient));

        // When
        ClientDetailOutput result = getClientUseCase.getByCode(testClientCode);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testClientId);
        assertThat(result.getName()).isEqualTo(testClientName);
        assertThat(result.getCode()).isEqualTo(testClientCode);

        verify(clientRepository).findByCode(testClientCode);
    }

    @Test
    @DisplayName("根据代码获取客户时客户不存在应该抛出异常")
    void shouldThrowExceptionWhenClientNotFoundByCode() {
        // Given
        String nonExistingCode = "NON_EXISTING_CODE";
        when(clientRepository.findByCode(nonExistingCode)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> getClientUseCase.getByCode(nonExistingCode))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessage("客户不存在 (code=" + nonExistingCode + ")");

        verify(clientRepository).findByCode(nonExistingCode);
    }

    @Test
    @DisplayName("应该正确转换客户实体为输出DTO")
    void shouldConvertClientEntityToOutputDtoCorrectly() {
        // Given
        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(testClient));

        // When
        ClientDetailOutput result = getClientUseCase.getById(testClientId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testClient.getId());
        assertThat(result.getName()).isEqualTo(testClient.getName());
        assertThat(result.getCode()).isEqualTo(testClient.getCode());
        assertThat(result.getAliasName()).isEqualTo(testClient.getAliasName());
        assertThat(result.getLocationId()).isEqualTo(testClient.getLocationId());
        assertThat(result.getDescription()).isEqualTo(testClient.getDescription());
        assertThat(result.getActive()).isEqualTo(testClient.getActive());
        assertThat(result.getCreatedAt()).isEqualTo(testClient.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(testClient.getUpdatedAt());
        assertThat(result.getVersion()).isEqualTo(testClient.getVersion());
    }

    @Test
    @DisplayName("应该正确处理null别名")
    void shouldHandleNullAliasName() {
        // Given
        Client clientWithoutAlias = Client.create(testClientName, testClientCode, null, "CN", "测试描述");
        clientWithoutAlias.setId(testClientId);
        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(clientWithoutAlias));

        // When
        ClientDetailOutput result = getClientUseCase.getById(testClientId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAliasName()).isNull();

        verify(clientRepository).findById(testClientId);
    }

    @Test
    @DisplayName("应该正确处理空别名")
    void shouldHandleEmptyAliasName() {
        // Given
        Client clientWithEmptyAlias = Client.create(testClientName, testClientCode, "", "CN", "测试描述");
        clientWithEmptyAlias.setId(testClientId);
        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(clientWithEmptyAlias));

        // When
        ClientDetailOutput result = getClientUseCase.getById(testClientId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAliasName()).isEmpty();

        verify(clientRepository).findById(testClientId);
    }

    @Test
    @DisplayName("应该正确处理null描述")
    void shouldHandleNullDescription() {
        // Given
        Client clientWithoutDescription = Client.create(testClientName, testClientCode, "测试别名", "CN", null);
        clientWithoutDescription.setId(testClientId);
        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(clientWithoutDescription));

        // When
        ClientDetailOutput result = getClientUseCase.getById(testClientId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isNull();

        verify(clientRepository).findById(testClientId);
    }

    @Test
    @DisplayName("应该正确处理激活和停用状态")
    void shouldHandleActiveAndInactiveStatus() {
        // Given
        Client activeClient = Client.create(testClientName, testClientCode, "测试别名", "CN", "测试描述");
        activeClient.setId(testClientId);
        activeClient.activate();

        Client inactiveClient = Client.create(testClientName + "停用", "INACTIVE_CLIENT", "停用别名", "CN", "停用描述");
        inactiveClient.setId("inactive-client-id");
        inactiveClient.deactivate();

        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(activeClient));
        when(clientRepository.findById("inactive-client-id")).thenReturn(Optional.of(inactiveClient));

        // When
        ClientDetailOutput activeResult = getClientUseCase.getById(testClientId);
        ClientDetailOutput inactiveResult = getClientUseCase.getById("inactive-client-id");

        // Then
        assertThat(activeResult.getActive()).isTrue();
        assertThat(inactiveResult.getActive()).isFalse();

        verify(clientRepository).findById(testClientId);
        verify(clientRepository).findById("inactive-client-id");
    }

    @Test
    @DisplayName("应该处理null和空ID参数")
    void shouldHandleNullAndEmptyIdParameters() {
        // Given
        when(clientRepository.findById(isNull())).thenReturn(Optional.empty());
        when(clientRepository.findById("")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> getClientUseCase.getById(null))
                .isInstanceOf(ClientNotFoundException.class);

        assertThatThrownBy(() -> getClientUseCase.getById(""))
                .isInstanceOf(ClientNotFoundException.class);

        verify(clientRepository).findById(null);
        verify(clientRepository).findById("");
    }

    @Test
    @DisplayName("应该处理null和空名称参数")
    void shouldHandleNullAndEmptyNameParameters() {
        // Given
        when(clientRepository.findByName(isNull())).thenReturn(Optional.empty());
        when(clientRepository.findByName("")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> getClientUseCase.getByName(null))
                .isInstanceOf(ClientNotFoundException.class);

        assertThatThrownBy(() -> getClientUseCase.getByName(""))
                .isInstanceOf(ClientNotFoundException.class);

        verify(clientRepository).findByName(null);
        verify(clientRepository).findByName("");
    }

    @Test
    @DisplayName("应该处理null和空代码参数")
    void shouldHandleNullAndEmptyCodeParameters() {
        // Given
        when(clientRepository.findByCode(isNull())).thenReturn(Optional.empty());
        when(clientRepository.findByCode("")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> getClientUseCase.getByCode(null))
                .isInstanceOf(ClientNotFoundException.class);

        assertThatThrownBy(() -> getClientUseCase.getByCode(""))
                .isInstanceOf(ClientNotFoundException.class);

        verify(clientRepository).findByCode(null);
        verify(clientRepository).findByCode("");
    }

    @Test
    @DisplayName("应该正确处理审计字段")
    void shouldHandleAuditFieldsCorrectly() {
        // Given
        Client clientWithAuditFields = Client.create(testClientName, testClientCode, "测试别名", "CN", "测试描述");
        clientWithAuditFields.setId(testClientId);
        clientWithAuditFields.setCreatorId("user-123");
        clientWithAuditFields.setCreator("测试用户");
        clientWithAuditFields.setUpdaterId("user-456");
        clientWithAuditFields.setUpdater("更新用户");

        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(clientWithAuditFields));

        // When
        ClientDetailOutput result = getClientUseCase.getById(testClientId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLocationId()).isEqualTo("CN");
        assertThat(result.getActive()).isTrue();

        verify(clientRepository).findById(testClientId);
    }

    @Test
    @DisplayName("应该正确处理版本号")
    void shouldHandleVersionNumberCorrectly() {
        // Given
        Client clientWithVersion = Client.create(testClientName, testClientCode, "测试别名", "CN", "测试描述");
        clientWithVersion.setId(testClientId);
        clientWithVersion.setVersion(5L);

        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(clientWithVersion));

        // When
        ClientDetailOutput result = getClientUseCase.getById(testClientId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getVersion()).isEqualTo(5L);

        verify(clientRepository).findById(testClientId);
    }
}