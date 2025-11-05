package com.i0.client.domain.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Client实体类单元测试
 */
@DisplayName("Client实体类测试")
class ClientTest {

    private Client client;
    private final String validName = "测试客户";
    private final String validCode = "TEST_CLIENT_001";
    private final String validAliasName = "测试客户别名";
    private final String validLocationId = "CN";
    private final String validDescription = "这是一个测试客户";

    @BeforeEach
    void setUp() {
        client = new Client();
    }

    @Test
    @DisplayName("应该能够创建有效的客户")
    void shouldCreateValidClient() {
        // When
        Client createdClient = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);

        // Then
        assertThat(createdClient).isNotNull();
        assertThat(createdClient.getName()).isEqualTo(validName);
        assertThat(createdClient.getCode()).isEqualTo(validCode);
        assertThat(createdClient.getAliasName()).isEqualTo(validAliasName);
        assertThat(createdClient.getLocationId()).isEqualTo(validLocationId);
        assertThat(createdClient.getDescription()).isEqualTo(validDescription);
        assertThat(createdClient.isActive()).isTrue();
        assertThat(createdClient.getCreatedAt()).isNotNull();
        assertThat(createdClient.getUpdatedAt()).isNotNull();
        assertThat(createdClient.getVersion()).isEqualTo(0L);
    }

    @Test
    @DisplayName("应该能够创建没有别名的客户")
    void shouldCreateClientWithoutAliasName() {
        // When
        Client createdClient = Client.create(validName, validCode, null, validLocationId, validDescription);

        // Then
        assertThat(createdClient).isNotNull();
        assertThat(createdClient.getName()).isEqualTo(validName);
        assertThat(createdClient.getCode()).isEqualTo(validCode);
        assertThat(createdClient.getAliasName()).isNull();
        assertThat(createdClient.getLocationId()).isEqualTo(validLocationId);
        assertThat(createdClient.getDescription()).isEqualTo(validDescription);
        assertThat(createdClient.isActive()).isTrue();
    }

    @Test
    @DisplayName("应该能够创建没有描述的客户")
    void shouldCreateClientWithoutDescription() {
        // When
        Client createdClient = Client.create(validName, validCode, validAliasName, validLocationId, null);

        // Then
        assertThat(createdClient).isNotNull();
        assertThat(createdClient.getName()).isEqualTo(validName);
        assertThat(createdClient.getCode()).isEqualTo(validCode);
        assertThat(createdClient.getAliasName()).isEqualTo(validAliasName);
        assertThat(createdClient.getLocationId()).isEqualTo(validLocationId);
        assertThat(createdClient.getDescription()).isNull();
        assertThat(createdClient.isActive()).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("创建客户时名称不能为空或空白")
    void shouldThrowExceptionWhenNameIsNullOrBlank(String invalidName) {
        // When & Then
        assertThatThrownBy(() -> Client.create(invalidName, validCode, validAliasName, validLocationId, validDescription))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("客户名称不能为空");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("创建客户时代码不能为空或空白")
    void shouldThrowExceptionWhenCodeIsNullOrBlank(String invalidCode) {
        // When & Then
        assertThatThrownBy(() -> Client.create(validName, invalidCode, validAliasName, validLocationId, validDescription))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("客户代码不能为空");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("创建客户时位置ID不能为空或空白")
    void shouldThrowExceptionWhenLocationIdIsNullOrBlank(String invalidLocationId) {
        // When & Then
        assertThatThrownBy(() -> Client.create(validName, validCode, validAliasName, invalidLocationId, validDescription))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("注册位置不能为空");
    }

    @Test
    @DisplayName("创建客户时名称不能超过200个字符")
    void shouldThrowExceptionWhenNameIsTooLong() {
        // Given
        String longName = "a".repeat(201);

        // When & Then
        assertThatThrownBy(() -> Client.create(longName, validCode, validAliasName, validLocationId, validDescription))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("客户名称不能超过200个字符");
    }

    @Test
    @DisplayName("创建客户时代码不能超过50个字符")
    void shouldThrowExceptionWhenCodeIsTooLong() {
        // Given
        String longCode = "a".repeat(51);

        // When & Then
        assertThatThrownBy(() -> Client.create(validName, longCode, validAliasName, validLocationId, validDescription))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("客户代码不能超过50个字符");
    }

    @ParameterizedTest
    @ValueSource(strings = {"test@client", "test client", "test.client", "test+client", "test#client"})
    @DisplayName("创建客户时代码只能包含字母、数字、下划线和连字符")
    void shouldThrowExceptionWhenCodeContainsInvalidCharacters(String invalidCode) {
        // When & Then
        assertThatThrownBy(() -> Client.create(validName, invalidCode, validAliasName, validLocationId, validDescription))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("客户代码只能包含字母、数字、下划线和连字符");
    }

    @ParameterizedTest
    @ValueSource(strings = {"test_client", "test-client", "testclient", "TEST123", "test_123", "123test"})
    @DisplayName("应该支持有效的客户代码格式")
    void shouldAcceptValidCodeFormats(String validCodeFormat) {
        // When
        Client createdClient = Client.create(validName, validCodeFormat, validAliasName, validLocationId, validDescription);

        // Then
        assertThat(createdClient).isNotNull();
        assertThat(createdClient.getCode()).isEqualTo(validCodeFormat);
    }

    @Test
    @DisplayName("应该能够更新客户信息")
    void shouldUpdateClientInfo() {
        // Given
        Client createdClient = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);
        LocalDateTime originalUpdatedAt = createdClient.getUpdatedAt();
        String newName = "更新后的客户名称";
        String newAliasName = "更新后的别名";
        String newLocationId = "US";
        String newDescription = "更新后的描述";

        // When
        createdClient.update(newName, newAliasName, newLocationId, newDescription);

        // Then
        assertThat(createdClient.getName()).isEqualTo(newName);
        assertThat(createdClient.getAliasName()).isEqualTo(newAliasName);
        assertThat(createdClient.getLocationId()).isEqualTo(newLocationId);
        assertThat(createdClient.getDescription()).isEqualTo(newDescription);
        assertThat(createdClient.getUpdatedAt()).isAfter(originalUpdatedAt);
        assertThat(createdClient.getCode()).isEqualTo(validCode); // 代码不应该改变
    }

    @Test
    @DisplayName("应该能够更新客户代码")
    void shouldUpdateClientCode() {
        // Given
        Client createdClient = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);
        LocalDateTime originalUpdatedAt = createdClient.getUpdatedAt();
        String newCode = "UPDATED_CLIENT_001";

        // When
        createdClient.updateCode(newCode);

        // Then
        assertThat(createdClient.getCode()).isEqualTo(newCode);
        assertThat(createdClient.getUpdatedAt()).isAfter(originalUpdatedAt);
        assertThat(createdClient.getName()).isEqualTo(validName); // 其他字段不应该改变
    }

    @Test
    @DisplayName("应该能够激活客户")
    void shouldActivateClient() {
        // Given
        Client createdClient = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);
        createdClient.deactivate(); // 先停用
        LocalDateTime originalUpdatedAt = createdClient.getUpdatedAt();

        // When
        createdClient.activate();

        // Then
        assertThat(createdClient.isActive()).isTrue();
        assertThat(createdClient.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("应该能够停用客户")
    void shouldDeactivateClient() {
        // Given
        Client createdClient = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);
        LocalDateTime originalUpdatedAt = createdClient.getUpdatedAt();

        // When
        createdClient.deactivate();

        // Then
        assertThat(createdClient.isActive()).isFalse();
        assertThat(createdClient.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("应该能够检查客户是否激活")
    void shouldCheckIfClientIsActive() {
        // Given
        Client activeClient = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);
        Client inactiveClient = Client.create(validName, "INACTIVE_CLIENT", validAliasName, validLocationId, validDescription);
        inactiveClient.deactivate();

        // When & Then
        assertThat(activeClient.isActive()).isTrue();
        assertThat(inactiveClient.isActive()).isFalse();
    }

    @Test
    @DisplayName("应该能够检查是否有别名")
    void shouldCheckIfClientHasAliasName() {
        // Given
        Client clientWithAlias = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);
        Client clientWithoutAlias = Client.create(validName, "NO_ALIAS_CLIENT", null, validLocationId, validDescription);

        // When & Then
        assertThat(clientWithAlias.hasAliasName()).isTrue();
        assertThat(clientWithoutAlias.hasAliasName()).isFalse();
    }

    @Test
    @DisplayName("应该能够获取显示名称")
    void shouldGetDisplayName() {
        // Given
        Client clientWithAlias = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);
        Client clientWithoutAlias = Client.create(validName, "NO_ALIAS_CLIENT", null, validLocationId, validDescription);

        // When & Then
        assertThat(clientWithAlias.getDisplayName()).isEqualTo(validAliasName);
        assertThat(clientWithoutAlias.getDisplayName()).isEqualTo(validName);
    }

    @Test
    @DisplayName("应该能够验证客户完整性")
    void shouldValidateClient() {
        // Given
        Client validClient = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);
        Client invalidClient = new Client();

        // When & Then
        assertThatCode(validClient::validate).doesNotThrowAnyException();
        assertThatThrownBy(invalidClient::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("更新客户时名称不能为空或空白")
    void shouldThrowExceptionWhenUpdateNameIsNullOrBlank(String invalidName) {
        // Given
        Client createdClient = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);

        // When & Then
        assertThatThrownBy(() -> createdClient.update(invalidName, "新别名", validLocationId, "新描述"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("客户名称不能为空");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("更新客户时位置ID不能为空或空白")
    void shouldThrowExceptionWhenUpdateLocationIdIsNullOrBlank(String invalidLocationId) {
        // Given
        Client createdClient = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);

        // When & Then
        assertThatThrownBy(() -> createdClient.update(validName, "新别名", invalidLocationId, "新描述"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("注册位置不能为空");
    }

    @Test
    @DisplayName("equals和hashCode应该基于ID")
    void shouldImplementEqualsAndHashCodeBasedOnId() {
        // Given
        Client client1 = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);
        client1.setId("client-id-1");

        Client client2 = Client.create("不同名称", "DIFF_CODE", "不同别名", "US", "不同描述");
        client2.setId("client-id-1");

        Client client3 = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);
        client3.setId("client-id-2");

        // When & Then
        assertThat(client1).isEqualTo(client2); // 相同ID
        assertThat(client1).isNotEqualTo(client3); // 不同ID
        assertThat(client1.hashCode()).isEqualTo(client2.hashCode());
        assertThat(client1.hashCode()).isNotEqualTo(client3.hashCode());
    }

    @Test
    @DisplayName("toString应该包含关键信息")
    void shouldImplementToString() {
        // Given
        Client createdClient = Client.create(validName, validCode, validAliasName, validLocationId, validDescription);
        createdClient.setId("client-id");

        // When
        String toString = createdClient.toString();

        // Then
        assertThat(toString)
                .contains("client-id")
                .contains(validName)
                .contains(validCode)
                .contains(validAliasName)
                .contains(validLocationId);
    }

    @Test
    @DisplayName("应该正确处理null别名")
    void shouldHandleNullAliasName() {
        // When
        Client createdClient = Client.create(validName, validCode, null, validLocationId, validDescription);

        // Then
        assertThat(createdClient.getAliasName()).isNull();
        assertThat(createdClient.hasAliasName()).isFalse();
        assertThat(createdClient.getDisplayName()).isEqualTo(validName);
    }

    @Test
    @DisplayName("应该正确处理空别名")
    void shouldHandleEmptyAliasName() {
        // When
        Client createdClient = Client.create(validName, validCode, "", validLocationId, validDescription);

        // Then
        assertThat(createdClient.getAliasName()).isEmpty();
        assertThat(createdClient.hasAliasName()).isFalse();
        assertThat(createdClient.getDisplayName()).isEqualTo(validName);
    }

    @Test
    @DisplayName("应该正确处理null描述")
    void shouldHandleNullDescription() {
        // When
        Client createdClient = Client.create(validName, validCode, validAliasName, validLocationId, null);

        // Then
        assertThat(createdClient.getDescription()).isNull();
        assertThatCode(createdClient::validate).doesNotThrowAnyException();
    }
}