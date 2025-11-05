package com.i0.client.domain.services;

import com.i0.client.domain.entities.Client;
import com.i0.client.domain.exceptions.ClientAlreadyExistsException;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ClientDomainService单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClientDomainService测试")
class ClientDomainServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientDomainService clientDomainService;

    private Client testClient;
    private final String testClientId = "test-client-id";
    private final String testClientName = "测试客户";
    private final String testClientCode = "TEST_CLIENT_001";

    @BeforeEach
    void setUp() {
        testClient = Client.create(testClientName, testClientCode, "测试别名", "CN", "测试客户描述");
        testClient.setId(testClientId);
    }

    @Test
    @DisplayName("应该成功验证客户名称唯一性")
    void shouldValidateNameUniquenessSuccessfully() {
        // Given
        when(clientRepository.existsByName(testClientName)).thenReturn(false);

        // When & Then
        assertThatCode(() -> clientDomainService.validateNameUniqueness(testClientName))
                .doesNotThrowAnyException();

        verify(clientRepository).existsByName(testClientName);
    }

    @Test
    @DisplayName("客户名称已存在时应该抛出异常")
    void shouldThrowExceptionWhenNameAlreadyExists() {
        // Given
        when(clientRepository.existsByName(testClientName)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clientDomainService.validateNameUniqueness(testClientName))
                .isInstanceOf(ClientAlreadyExistsException.class)
                .hasMessage("客户名称已存在: " + testClientName);

        verify(clientRepository).existsByName(testClientName);
    }

    @Test
    @DisplayName("应该成功验证客户代码唯一性")
    void shouldValidateCodeUniquenessSuccessfully() {
        // Given
        when(clientRepository.existsByCode(testClientCode)).thenReturn(false);

        // When & Then
        assertThatCode(() -> clientDomainService.validateCodeUniqueness(testClientCode))
                .doesNotThrowAnyException();

        verify(clientRepository).existsByCode(testClientCode);
    }

    @Test
    @DisplayName("客户代码已存在时应该抛出异常")
    void shouldThrowExceptionWhenCodeAlreadyExists() {
        // Given
        when(clientRepository.existsByCode(testClientCode)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clientDomainService.validateCodeUniqueness(testClientCode))
                .isInstanceOf(ClientAlreadyExistsException.class)
                .hasMessage("客户代码已存在: " + testClientCode);

        verify(clientRepository).existsByCode(testClientCode);
    }

    @Test
    @DisplayName("应该成功验证客户名称唯一性（排除指定ID）")
    void shouldValidateNameUniquenessWithExclusionSuccessfully() {
        // Given
        when(clientRepository.existsByNameAndIdNot(testClientName, testClientId)).thenReturn(false);

        // When & Then
        assertThatCode(() -> clientDomainService.validateNameUniqueness(testClientName, testClientId))
                .doesNotThrowAnyException();

        verify(clientRepository).existsByNameAndIdNot(testClientName, testClientId);
    }

    @Test
    @DisplayName("排除指定ID时客户名称已存在应该抛出异常")
    void shouldThrowExceptionWhenNameAlreadyExistsWithExclusion() {
        // Given
        when(clientRepository.existsByNameAndIdNot(testClientName, testClientId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clientDomainService.validateNameUniqueness(testClientName, testClientId))
                .isInstanceOf(ClientAlreadyExistsException.class)
                .hasMessage("客户名称已存在: " + testClientName);

        verify(clientRepository).existsByNameAndIdNot(testClientName, testClientId);
    }

    @Test
    @DisplayName("应该成功验证客户代码唯一性（排除指定ID）")
    void shouldValidateCodeUniquenessWithExclusionSuccessfully() {
        // Given
        when(clientRepository.existsByCodeAndIdNot(testClientCode, testClientId)).thenReturn(false);

        // When & Then
        assertThatCode(() -> clientDomainService.validateCodeUniqueness(testClientCode, testClientId))
                .doesNotThrowAnyException();

        verify(clientRepository).existsByCodeAndIdNot(testClientCode, testClientId);
    }

    @Test
    @DisplayName("排除指定ID时客户代码已存在应该抛出异常")
    void shouldThrowExceptionWhenCodeAlreadyExistsWithExclusion() {
        // Given
        when(clientRepository.existsByCodeAndIdNot(testClientCode, testClientId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> clientDomainService.validateCodeUniqueness(testClientCode, testClientId))
                .isInstanceOf(ClientAlreadyExistsException.class)
                .hasMessage("客户代码已存在: " + testClientCode);

        verify(clientRepository).existsByCodeAndIdNot(testClientCode, testClientId);
    }

    @Test
    @DisplayName("检查客户是否可以被删除应该返回true")
    void shouldReturnTrueWhenClientCanBeDeleted() {
        // Given
        when(clientRepository.findById(testClientId)).thenReturn(Optional.of(testClient));

        // When
        boolean result = clientDomainService.canBeDeleted(testClient);

        // Then
        assertThat(result).isTrue();
        verify(clientRepository).findById(testClientId);
    }
}