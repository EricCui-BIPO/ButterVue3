package com.i0.client.application.usecases;

import com.i0.client.application.dto.input.ClientPageInput;
import com.i0.client.application.dto.output.ClientDetailOutput;
import com.i0.client.domain.entities.Client;
import com.i0.client.domain.repositories.ClientRepository;
import com.i0.client.domain.services.LocationQueryService;
import com.i0.client.domain.valueobjects.LocationInfo;
import com.i0.domain.core.pagination.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SearchClientsUseCase单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SearchClientsUseCase测试")
class SearchClientsUseCaseTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private LocationQueryService locationQueryService;

    @InjectMocks
    private SearchClientsUseCase searchClientsUseCase;

    private Client testClient1;
    private Client testClient2;
    private final String testClientId1 = "client-001";
    private final String testClientId2 = "client-002";
    private final String testContinentId1 = "550e8400-e29b-41d4-a716-446655440010";
    private final String testContinentId2 = "550e8400-e29b-41d4-a716-446655440011";
    private final String testLocationId1 = "550e8400-e29b-41d4-a716-446655440000";
    private final String testLocationId2 = "550e8400-e29b-41d4-a716-446655440001";

    @BeforeEach
    void setUp() {

        // Create test clients with proper location IDs
        testClient1 = Client.create("测试客户1", "CLIENT_001", "客户1别名", testLocationId1, "客户1描述");
        testClient1.setId(testClientId1);

        testClient2 = Client.create("测试客户2", "CLIENT_002", "客户2别名", testLocationId2, "客户2描述");
        testClient2.setId(testClientId2);

        // Mock location query service responses
        List<LocationInfo> locationInfos = java.util.Arrays.asList(
            LocationInfo.of(testLocationId1, "中国"),
            LocationInfo.of(testLocationId2, "美国")
        );
        when(locationQueryService.getLocations(any(Iterable.class))).thenReturn(locationInfos);
        when(locationQueryService.getLocation(testLocationId1)).thenReturn(LocationInfo.of(testLocationId1, "中国"));
        when(locationQueryService.getLocation(testLocationId2)).thenReturn(LocationInfo.of(testLocationId2, "美国"));
    }

    @Test
    @DisplayName("应该成功搜索客户")
    void shouldSearchClientsSuccessfully() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(0)
            .size(10)
            .keyword("测试")
            .locationId(testLocationId1)
            .activeOnly(true)
            .sortBy("name")
            .sortDirection("asc")
            .build();

        List<Client> clientList = Arrays.asList(testClient1, testClient2);
        Pageable<Client> mockPage = createTestPage(clientList, 0, 10, 2);

        // Debug: Check if mock page is created correctly
        assertThat(mockPage).isNotNull();
        assertThat(mockPage.getContent()).hasSize(2);

        when(clientRepository.searchClients(eq("测试"), eq(testLocationId1), eq(true), eq(0), eq(10), eq("name"), eq("asc")))
            .thenReturn(mockPage);

        // When
        Pageable<ClientDetailOutput> result = searchClientsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getTotalPages()).isEqualTo(1);

        ClientDetailOutput firstClient = result.getContent().get(0);
        assertThat(firstClient.getName()).isEqualTo("测试客户1");
        assertThat(firstClient.getCode()).isEqualTo("CLIENT_001");
        assertThat(firstClient.getLocationId()).isEqualTo(testLocationId1);
        assertThat(firstClient.getLocation().getName()).isEqualTo("中国");

        verify(clientRepository).searchClients("测试", testLocationId1, true, 0, 10, "name", "asc");
    }

    @Test
    @DisplayName("应该使用关键字搜索当提供关键字时")
    void shouldUseKeywordSearchWhenKeywordIsProvided() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(0)
            .size(10)
            .keyword("测试客户1")
            .locationId(testLocationId1)
            .activeOnly(true)
            .build();

        List<Client> clientList = Arrays.asList(testClient1, testClient2);
        Pageable<Client> mockPage = createTestPage(clientList, 0, 10, 2);

        when(clientRepository.searchClients(anyString(), anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString()))
            .thenReturn(mockPage);

        // When
        Pageable<ClientDetailOutput> result = searchClientsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        verify(clientRepository).searchClients("测试客户1", testLocationId1, true, 0, 10, "createdAt", "DESC");
    }

    @Test
    @DisplayName("应该使用关键字搜索当没有具体字段条件时")
    void shouldUseKeywordSearchWhenNoSpecificFieldsAreProvided() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(0)
            .size(10)
            .keyword("测试")
            .locationId(testLocationId1)
            .activeOnly(true)
            .build();

        List<Client> clientList = Arrays.asList(testClient1, testClient2);
        Pageable<Client> mockPage = createTestPage(clientList, 0, 10, 2);

        when(clientRepository.searchClients(anyString(), anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString()))
            .thenReturn(mockPage);

        // When
        Pageable<ClientDetailOutput> result = searchClientsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        verify(clientRepository).searchClients("测试", testLocationId1, true, 0, 10, "createdAt", "DESC");
    }

    @Test
    @DisplayName("应该正确处理null关键字")
    void shouldHandleNullKeyword() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(0)
            .size(10)
            .keyword(null)
            .locationId(testLocationId1)
            .activeOnly(true)
            .build();

        List<Client> clientList = Arrays.asList(testClient1, testClient2);
        Pageable<Client> mockPage = createTestPage(clientList, 0, 10, 2);

        when(clientRepository.searchClients(isNull(), anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString()))
            .thenReturn(mockPage);

        // When
        Pageable<ClientDetailOutput> result = searchClientsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        verify(clientRepository).searchClients(null, testLocationId1, true, 0, 10, "createdAt", "DESC");
    }

    @Test
    @DisplayName("应该正确处理空关键字")
    void shouldHandleEmptyKeyword() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(0)
            .size(10)
            .keyword("")
            .locationId(testLocationId1)
            .activeOnly(true)
            .build();

        List<Client> clientList = Arrays.asList(testClient1, testClient2);
        Pageable<Client> mockPage = createTestPage(clientList, 0, 10, 2);

        when(clientRepository.searchClients(isNull(), anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString()))
            .thenReturn(mockPage);

        // When
        Pageable<ClientDetailOutput> result = searchClientsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        verify(clientRepository).searchClients(null, testLocationId1, true, 0, 10, "createdAt", "DESC");
    }

    @Test
    @DisplayName("应该正确处理空白关键字")
    void shouldHandleBlankKeyword() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(0)
            .size(10)
            .keyword("   ")
            .locationId(testLocationId1)
            .activeOnly(true)
            .build();

        List<Client> clientList = Arrays.asList(testClient1, testClient2);
        Pageable<Client> mockPage = createTestPage(clientList, 0, 10, 2);

        when(clientRepository.searchClients(isNull(), anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString()))
            .thenReturn(mockPage);

        // When
        Pageable<ClientDetailOutput> result = searchClientsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        verify(clientRepository).searchClients(null, testLocationId1, true, 0, 10, "createdAt", "DESC");
    }


    @Test
    @DisplayName("页码为负数时应该抛出异常")
    void shouldThrowExceptionWhenPageIsNegative() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(-1)
            .size(10)
            .build();

        // When & Then
        assertThatThrownBy(() -> searchClientsUseCase.execute(input))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("页码不能为负数");

        verify(clientRepository, never()).searchClients(anyString(), anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    @DisplayName("每页大小为0时应该抛出异常")
    void shouldThrowExceptionWhenSizeIsZero() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(0)
            .size(0)
            .build();

        // When & Then
        assertThatThrownBy(() -> searchClientsUseCase.execute(input))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("每页大小必须大于0");

        verify(clientRepository, never()).searchClients(anyString(), anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    @DisplayName("每页大小为负数时应该抛出异常")
    void shouldThrowExceptionWhenSizeIsNegative() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(0)
            .size(-1)
            .build();

        // When & Then
        assertThatThrownBy(() -> searchClientsUseCase.execute(input))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("每页大小必须大于0");

        verify(clientRepository, never()).searchClients(anyString(), anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    @DisplayName("每页大小超过1000时应该抛出异常")
    void shouldThrowExceptionWhenSizeExceeds1000() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(0)
            .size(1001)
            .build();

        // When & Then
        assertThatThrownBy(() -> searchClientsUseCase.execute(input))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("每页大小不能超过1000");

        verify(clientRepository, never()).searchClients(anyString(), anyString(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString());
    }

    @Test
    @DisplayName("应该正确处理边界值每页大小1000")
    void shouldHandleBoundarySize1000() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(0)
            .size(1000)
            .keyword("测试")
            .build();

        List<Client> clientList = Arrays.asList(testClient1, testClient2);
        Pageable<Client> mockPage = createTestPage(clientList, 0, 10, 2);

        when(clientRepository.searchClients(anyString(), isNull(), isNull(), anyInt(), anyInt(), anyString(), anyString()))
            .thenReturn(mockPage);

        // When
        Pageable<ClientDetailOutput> result = searchClientsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        verify(clientRepository).searchClients("测试", null, null, 0, 1000, "createdAt", "DESC");
    }

    @Test
    @DisplayName("应该正确处理null和undefined排序参数")
    void shouldHandleNullAndUndefinedSortParameters() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(0)
            .size(10)
            .keyword("测试")
            .sortBy(null)
            .sortDirection(null)
            .build();

        List<Client> clientList = Arrays.asList(testClient1, testClient2);
        Pageable<Client> mockPage = createTestPage(clientList, 0, 10, 2);

        when(clientRepository.searchClients(anyString(), isNull(), isNull(), anyInt(), anyInt(), anyString(), anyString()))
            .thenReturn(mockPage);

        // When
        Pageable<ClientDetailOutput> result = searchClientsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        verify(clientRepository).searchClients("测试", null, null, 0, 10, "createdAt", "DESC");
    }

    @Test
    @DisplayName("应该正确处理null和undefinedactiveOnly参数")
    void shouldHandleNullAndUndefinedActiveOnlyParameter() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(0)
            .size(10)
            .keyword("测试")
            .activeOnly(null)
            .build();

        List<Client> clientList = Arrays.asList(testClient1, testClient2);
        Pageable<Client> mockPage = createTestPage(clientList, 0, 10, 2);

        when(clientRepository.searchClients(anyString(), isNull(), isNull(), anyInt(), anyInt(), anyString(), anyString()))
            .thenReturn(mockPage);

        // When
        Pageable<ClientDetailOutput> result = searchClientsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        verify(clientRepository).searchClients("测试", null, null, 0, 10, "createdAt", "DESC");
    }

    @Test
    @DisplayName("应该正确处理null和undefinedlocationId参数")
    void shouldHandleNullAndUndefinedCountryIdParameter() {
        // Given
        ClientPageInput input = ClientPageInput.builder()
            .page(0)
            .size(10)
            .keyword("测试")
            .locationId(null)
            .build();

        List<Client> clientList = Arrays.asList(testClient1, testClient2);
        Pageable<Client> mockPage = createTestPage(clientList, 0, 10, 2);

        when(clientRepository.searchClients(anyString(), isNull(), isNull(), anyInt(), anyInt(), anyString(), anyString()))
            .thenReturn(mockPage);

        // When
        Pageable<ClientDetailOutput> result = searchClientsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);

        verify(clientRepository).searchClients("测试", null, null, 0, 10, "createdAt", "DESC");
    }

    /**
     * 创建用于测试的Client Page对象
     */
    private Pageable<Client> createTestPage(List<Client> clients, int page, int size, long total) {
        Pageable<Client> mockPage = mock(Pageable.class);
        when(mockPage.getContent()).thenReturn(clients);
        when(mockPage.getPage()).thenReturn(page);
        when(mockPage.getSize()).thenReturn(size);
        when(mockPage.getTotal()).thenReturn(total);
        when(mockPage.getTotalPages()).thenReturn((int) Math.ceil((double) total / size));
        when(mockPage.getNumberOfElements()).thenReturn(clients.size());
        when(mockPage.isFirst()).thenReturn(page == 0);
        when(mockPage.isLast()).thenReturn(page >= (int) Math.ceil((double) total / size) - 1);
        when(mockPage.hasNext()).thenReturn(page < (int) Math.ceil((double) total / size) - 1);
        when(mockPage.hasPrevious()).thenReturn(page > 0);
        when(mockPage.isEmpty()).thenReturn(clients.isEmpty());

        // Stub the map method to return a properly configured Pageable<ClientOutput>
        Pageable<ClientDetailOutput> mockOutputPage = mock(Pageable.class);
        List<ClientDetailOutput> clientOutputs = clients.stream()
            .map(client -> {
                if (client.getLocationId().equals(testLocationId1)) {
                    return ClientDetailOutput.from(client, "中国");
                } else if (client.getLocationId().equals(testLocationId2)) {
                    return ClientDetailOutput.from(client, "美国");
                } else {
                    return ClientDetailOutput.from(client, null);
                }
            })
            .collect(java.util.stream.Collectors.toList());
        when(mockOutputPage.getContent()).thenReturn(clientOutputs);
        when(mockOutputPage.getPage()).thenReturn(page);
        when(mockOutputPage.getSize()).thenReturn(size);
        when(mockOutputPage.getTotal()).thenReturn(total);
        when(mockOutputPage.getTotalPages()).thenReturn((int) Math.ceil((double) total / size));
        when(mockOutputPage.getNumberOfElements()).thenReturn(clientOutputs.size());
        when(mockOutputPage.isFirst()).thenReturn(page == 0);
        when(mockOutputPage.isLast()).thenReturn(page >= (int) Math.ceil((double) total / size) - 1);
        when(mockOutputPage.hasNext()).thenReturn(page < (int) Math.ceil((double) total / size) - 1);
        when(mockOutputPage.hasPrevious()).thenReturn(page > 0);
        when(mockOutputPage.isEmpty()).thenReturn(clientOutputs.isEmpty());

        when(mockPage.map(any(Function.class))).thenReturn(mockOutputPage);

        return mockPage;
    }
}