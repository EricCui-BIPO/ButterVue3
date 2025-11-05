package com.i0.client.application.usecases;

import com.i0.client.application.dto.input.CreateClientInput;
import com.i0.client.application.dto.output.ClientOutput;
import com.i0.client.domain.entities.Client;
import com.i0.client.domain.repositories.ClientRepository;
import com.i0.client.domain.services.ClientDomainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CreateClientUseCase单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateClientUseCase测试")
class CreateClientUseCaseTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientDomainService clientDomainService;

    @InjectMocks
    private CreateClientUseCase createClientUseCase;

    private CreateClientInput createRequest;
    private Client testClient;
    private final String testClientId = "test-client-id";

    @BeforeEach
    void setUp() {
        createRequest = CreateClientInput.builder()
                .name("测试客户")
                .code("TEST_CLIENT_001")
                .aliasName("测试客户别名")
                .locationId("CN")
                .description("这是一个测试客户")
                .build();

        testClient = Client.create(
                "测试客户",
                "TEST_CLIENT_001",
                "测试客户别名",
                "CN",
                "这是一个测试客户"
        );
        testClient.setId(testClientId);
    }

    @Test
    @DisplayName("应该能够成功创建客户")
    void shouldCreateClientSuccessfully() {
        // Given
        doNothing().when(clientDomainService).validateNameUniqueness(createRequest.getName());
        doNothing().when(clientDomainService).validateCodeUniqueness(createRequest.getCode());
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // When
        ClientOutput response = createClientUseCase.execute(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(createRequest.getName());
        assertThat(response.getCode()).isEqualTo(createRequest.getCode());
        assertThat(response.getAliasName()).isEqualTo(createRequest.getAliasName());
        assertThat(response.getLocationId()).isEqualTo(createRequest.getLocationId());
        assertThat(response.getDescription()).isEqualTo(createRequest.getDescription());
        assertThat(response.getActive()).isTrue();

        verify(clientDomainService).validateNameUniqueness(createRequest.getName());
        verify(clientDomainService).validateCodeUniqueness(createRequest.getCode());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("应该能够创建没有别名的客户")
    void shouldCreateClientWithoutAliasNameSuccessfully() {
        // Given
        CreateClientInput requestWithoutAlias = CreateClientInput.builder()
                .name("测试客户")
                .code("TEST_CLIENT_001")
                .aliasName(null)
                .locationId("CN")
                .description("这是一个测试客户")
                .build();

        Client clientWithoutAlias = Client.create(
                "测试客户",
                "TEST_CLIENT_001",
                null,
                "CN",
                "这是一个测试客户"
        );
        clientWithoutAlias.setId(testClientId);

        doNothing().when(clientDomainService).validateNameUniqueness(requestWithoutAlias.getName());
        doNothing().when(clientDomainService).validateCodeUniqueness(requestWithoutAlias.getCode());
        when(clientRepository.save(any(Client.class))).thenReturn(clientWithoutAlias);

        // When
        ClientOutput response = createClientUseCase.execute(requestWithoutAlias);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(requestWithoutAlias.getName());
        assertThat(response.getCode()).isEqualTo(requestWithoutAlias.getCode());
        assertThat(response.getAliasName()).isNull();
        assertThat(response.getLocationId()).isEqualTo(requestWithoutAlias.getLocationId());
        assertThat(response.getDescription()).isEqualTo(requestWithoutAlias.getDescription());

        verify(clientDomainService).validateNameUniqueness(requestWithoutAlias.getName());
        verify(clientDomainService).validateCodeUniqueness(requestWithoutAlias.getCode());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("应该能够创建没有描述的客户")
    void shouldCreateClientWithoutDescriptionSuccessfully() {
        // Given
        CreateClientInput requestWithoutDescription = CreateClientInput.builder()
                .name("测试客户")
                .code("TEST_CLIENT_001")
                .aliasName("测试客户别名")
                .locationId("CN")
                .description(null)
                .build();

        Client clientWithoutDescription = Client.create(
                "测试客户",
                "TEST_CLIENT_001",
                "测试客户别名",
                "CN",
                null
        );
        clientWithoutDescription.setId(testClientId);

        doNothing().when(clientDomainService).validateNameUniqueness(requestWithoutDescription.getName());
        doNothing().when(clientDomainService).validateCodeUniqueness(requestWithoutDescription.getCode());
        when(clientRepository.save(any(Client.class))).thenReturn(clientWithoutDescription);

        // When
        ClientOutput response = createClientUseCase.execute(requestWithoutDescription);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo(requestWithoutDescription.getName());
        assertThat(response.getCode()).isEqualTo(requestWithoutDescription.getCode());
        assertThat(response.getAliasName()).isEqualTo(requestWithoutDescription.getAliasName());
        assertThat(response.getLocationId()).isEqualTo(requestWithoutDescription.getLocationId());
        assertThat(response.getDescription()).isNull();

        verify(clientDomainService).validateNameUniqueness(requestWithoutDescription.getName());
        verify(clientDomainService).validateCodeUniqueness(requestWithoutDescription.getCode());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("客户名称已存在时应该抛出异常")
    void shouldThrowExceptionWhenNameAlreadyExists() {
        // Given
        doThrow(new RuntimeException("客户名称已存在: 测试客户"))
                .when(clientDomainService).validateNameUniqueness(createRequest.getName());

        // When & Then
        assertThatThrownBy(() -> createClientUseCase.execute(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("客户名称已存在: 测试客户");

        verify(clientDomainService).validateNameUniqueness(createRequest.getName());
        verify(clientDomainService, never()).validateCodeUniqueness(anyString());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("客户代码已存在时应该抛出异常")
    void shouldThrowExceptionWhenCodeAlreadyExists() {
        // Given
        doNothing().when(clientDomainService).validateNameUniqueness(createRequest.getName());
        doThrow(new RuntimeException("客户代码已存在: TEST_CLIENT_001"))
                .when(clientDomainService).validateCodeUniqueness(createRequest.getCode());

        // When & Then
        assertThatThrownBy(() -> createClientUseCase.execute(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("客户代码已存在: TEST_CLIENT_001");

        verify(clientDomainService).validateNameUniqueness(createRequest.getName());
        verify(clientDomainService).validateCodeUniqueness(createRequest.getCode());
        verify(clientRepository, never()).save(any(Client.class));
    }

    @Test
    @DisplayName("应该正确处理保存失败的异常")
    void shouldHandleSaveFailureException() {
        // Given
        doNothing().when(clientDomainService).validateNameUniqueness(createRequest.getName());
        doNothing().when(clientDomainService).validateCodeUniqueness(createRequest.getCode());
        when(clientRepository.save(any(Client.class)))
                .thenThrow(new RuntimeException("数据库保存失败"));

        // When & Then
        assertThatThrownBy(() -> createClientUseCase.execute(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("数据库保存失败");

        verify(clientDomainService).validateNameUniqueness(createRequest.getName());
        verify(clientDomainService).validateCodeUniqueness(createRequest.getCode());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("创建客户时应该设置正确的默认值")
    void shouldSetCorrectDefaultValuesWhenCreatingClient() {
        // Given
        doNothing().when(clientDomainService).validateNameUniqueness(createRequest.getName());
        doNothing().when(clientDomainService).validateCodeUniqueness(createRequest.getCode());
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // When
        ClientOutput response = createClientUseCase.execute(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getActive()).isTrue();
        assertThat(response.getCreatedAt()).isNotNull();
        assertThat(response.getUpdatedAt()).isNotNull();
        assertThat(response.getVersion()).isEqualTo(0L);

        verify(clientRepository).save(argThat(client ->
            client.getActive() &&
            client.getCreatedAt() != null &&
            client.getUpdatedAt() != null &&
            client.getVersion() == 0L
        ));
    }

    @Test
    @DisplayName("应该正确传递所有输入参数到Client实体")
    void shouldPassAllInputParametersToClientEntity() {
        // Given
        doNothing().when(clientDomainService).validateNameUniqueness(createRequest.getName());
        doNothing().when(clientDomainService).validateCodeUniqueness(createRequest.getCode());
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // When
        createClientUseCase.execute(createRequest);

        // Then
        verify(clientRepository).save(argThat(client ->
            client.getName().equals(createRequest.getName()) &&
            client.getCode().equals(createRequest.getCode()) &&
            client.getAliasName().equals(createRequest.getAliasName()) &&
            client.getLocationId().equals(createRequest.getLocationId()) &&
            client.getDescription().equals(createRequest.getDescription())
        ));
    }

    @Test
    @DisplayName("应该正确转换Client实体为ClientOutput")
    void shouldConvertClientEntityToClientOutputCorrectly() {
        // Given
        doNothing().when(clientDomainService).validateNameUniqueness(createRequest.getName());
        doNothing().when(clientDomainService).validateCodeUniqueness(createRequest.getCode());
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // When
        ClientOutput response = createClientUseCase.execute(createRequest);

        // Then
        assertThat(response.getId()).isEqualTo(testClient.getId());
        assertThat(response.getName()).isEqualTo(testClient.getName());
        assertThat(response.getCode()).isEqualTo(testClient.getCode());
        assertThat(response.getAliasName()).isEqualTo(testClient.getAliasName());
        assertThat(response.getLocationId()).isEqualTo(testClient.getLocationId());
        assertThat(response.getDescription()).isEqualTo(testClient.getDescription());
        assertThat(response.getActive()).isEqualTo(testClient.getActive());
        assertThat(response.getCreatedAt()).isEqualTo(testClient.getCreatedAt());
        assertThat(response.getUpdatedAt()).isEqualTo(testClient.getUpdatedAt());
        assertThat(response.getVersion()).isEqualTo(testClient.getVersion());
    }

    @Test
    @DisplayName("应该处理null和空字符串的别名")
    void shouldHandleNullAndEmptyAliasName() {
        // Given
        CreateClientInput requestWithEmptyAlias = CreateClientInput.builder()
                .name("测试客户")
                .code("TEST_CLIENT_001")
                .aliasName("")
                .locationId("CN")
                .description("这是一个测试客户")
                .build();

        Client clientWithEmptyAlias = Client.create(
                "测试客户",
                "TEST_CLIENT_001",
                "",
                "CN",
                "这是一个测试客户"
        );
        clientWithEmptyAlias.setId(testClientId);

        doNothing().when(clientDomainService).validateNameUniqueness(requestWithEmptyAlias.getName());
        doNothing().when(clientDomainService).validateCodeUniqueness(requestWithEmptyAlias.getCode());
        when(clientRepository.save(any(Client.class))).thenReturn(clientWithEmptyAlias);

        // When
        ClientOutput response = createClientUseCase.execute(requestWithEmptyAlias);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAliasName()).isEmpty();

        verify(clientRepository).save(argThat(client ->
            client.getAliasName() != null && client.getAliasName().isEmpty()
        ));
    }
}