package com.i0.agents.application.services.mcp;

import com.i0.agents.domain.services.mcp.MCPTool;
import com.i0.agents.domain.services.mcp.MCPToolResult;
import com.i0.agents.domain.services.mcp.MCPServer;
import com.i0.agents.domain.services.mcp.MCPServerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MCPProtocolManagerTest {

    private MCPProtocolManager mcpProtocolManager;

    @Mock
    private MCPServer mockServer1;

    @Mock
    private MCPServer mockServer2;

    @BeforeEach
    void setUp() {
        mcpProtocolManager = new MCPProtocolManager();
    }

    @Test
    void shouldRegisterServerSuccessfully() {
        // Given
        when(mockServer1.getName()).thenReturn("server1");
        when(mockServer1.isAvailable()).thenReturn(true);

        // When
        mcpProtocolManager.registerServer(mockServer1);

        // Then
        assertThat(mcpProtocolManager.getRegisteredServerCount()).isEqualTo(1);
        assertThat(mcpProtocolManager.getAvailableServerCount()).isEqualTo(1);
        verify(mockServer1).getName();
    }

    @Test
    void shouldUnregisterServerSuccessfully() {
        // Given
        when(mockServer1.getName()).thenReturn("server1");
        mcpProtocolManager.registerServer(mockServer1);

        // When
        boolean result = mcpProtocolManager.unregisterServer("server1");

        // Then
        assertThat(result).isTrue();
        assertThat(mcpProtocolManager.getRegisteredServerCount()).isEqualTo(0);
    }

    @Test
    void shouldReturnFalseWhenUnregisteringNonExistentServer() {
        // When
        boolean result = mcpProtocolManager.unregisterServer("nonexistent");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldGetServerByName() {
        // Given
        when(mockServer1.getName()).thenReturn("server1");
        mcpProtocolManager.registerServer(mockServer1);

        // When
        Optional<MCPServer> result = mcpProtocolManager.getServer("server1");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(mockServer1);
    }

    @Test
    void shouldReturnEmptyForNonExistentServer() {
        // When
        Optional<MCPServer> result = mcpProtocolManager.getServer("nonexistent");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldGetAllAvailableTools() {
        // Given
        MCPTool tool1 = createMockTool("tool1", "Tool 1");
        when(mockServer1.getName()).thenReturn("server1");
        when(mockServer1.getAvailableTools()).thenReturn(Arrays.asList(tool1));
        when(mockServer1.isAvailable()).thenReturn(true);

        mcpProtocolManager.registerServer(mockServer1);

        // When
        List<MCPTool> tools = mcpProtocolManager.getAllAvailableTools();

        // Then
        assertThat(tools).hasSize(1);
        assertThat(tools).containsExactly(tool1);
    }

    @Test
    void shouldFindToolByName() {
        // Given
        MCPTool tool1 = createMockTool("searchTool", "Search Tool");
        when(mockServer1.getName()).thenReturn("server1");
        when(mockServer1.getAvailableTools()).thenReturn(Arrays.asList(tool1));
        when(mockServer1.isAvailable()).thenReturn(true);

        mcpProtocolManager.registerServer(mockServer1);

        // When
        Optional<MCPTool> result = mcpProtocolManager.findToolByName("searchTool");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("searchTool");
    }

    @Test
    void shouldReturnEmptyWhenToolNotFound() {
        // Given
        when(mockServer1.getName()).thenReturn("server1");
        when(mockServer1.getAvailableTools()).thenReturn(Arrays.asList());
        when(mockServer1.isAvailable()).thenReturn(true);
        mcpProtocolManager.registerServer(mockServer1);

        // When
        Optional<MCPTool> result = mcpProtocolManager.findToolByName("nonexistent");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldExecuteToolSuccessfully() {
        // Given
        MCPToolResult expectedResult = MCPToolResult.success("testTool", "server1", "Success", Map.of("key", "value"));
        MCPTool tool1 = createMockTool("testTool", "Test Tool");

        when(mockServer1.getName()).thenReturn("server1");
        when(mockServer1.getAvailableTools()).thenReturn(Arrays.asList(tool1));
        when(mockServer1.isAvailable()).thenReturn(true);
        when(mockServer1.executeTool("testTool", Map.of("param", "value"))).thenReturn(expectedResult);

        mcpProtocolManager.registerServer(mockServer1);

        // When
        MCPToolResult result = mcpProtocolManager.executeTool("testTool", Map.of("param", "value"));

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getResult()).isEqualTo("Success");
        assertThat(result.getToolName()).isEqualTo("testTool");
        assertThat(result.getServerName()).isEqualTo("server1");
        verify(mockServer1).executeTool("testTool", Map.of("param", "value"));
    }

    @Test
    void shouldReturnFailureResultWhenToolNotFound() {
        // Given
        when(mockServer1.getName()).thenReturn("server1");
        when(mockServer1.getAvailableTools()).thenReturn(Arrays.asList());
        when(mockServer1.isAvailable()).thenReturn(true);
        mcpProtocolManager.registerServer(mockServer1);

        // When
        MCPToolResult result = mcpProtocolManager.executeTool("nonexistent", Map.of());

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).contains("Tool 'nonexistent' not found");
        assertThat(result.getToolName()).isEqualTo("nonexistent");
        assertThat(result.getServerName()).isEqualTo("unknown");
    }

    @Test
    void shouldCheckHasAvailableServers() {
        // Given
        when(mockServer1.getName()).thenReturn("server1");
        when(mockServer1.isAvailable()).thenReturn(true);
        when(mockServer2.getName()).thenReturn("server2");
        when(mockServer2.isAvailable()).thenReturn(false);
        mcpProtocolManager.registerServer(mockServer1);
        mcpProtocolManager.registerServer(mockServer2);

        // When & Then
        assertThat(mcpProtocolManager.hasAvailableServers()).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNoAvailableServers() {
        // Given
        when(mockServer2.getName()).thenReturn("server2");
        when(mockServer2.isAvailable()).thenReturn(false);
        mcpProtocolManager.registerServer(mockServer2);

        // When & Then
        assertThat(mcpProtocolManager.hasAvailableServers()).isFalse();
    }

    @Test
    void shouldClearAllServers() {
        // Given
        when(mockServer1.getName()).thenReturn("server1");
        when(mockServer2.getName()).thenReturn("server2");
        mcpProtocolManager.registerServer(mockServer1);
        mcpProtocolManager.registerServer(mockServer2);

        // When
        mcpProtocolManager.clear();

        // Then
        assertThat(mcpProtocolManager.getRegisteredServerCount()).isEqualTo(0);
        assertThat(mcpProtocolManager.getAvailableServerCount()).isEqualTo(0);
    }

    private MCPTool createMockTool(String name, String description) {
        return MCPTool.builder()
                .name(name)
                .description(description)
                .parameters(Map.of("type", "object", "properties", Map.of()))
                .sourceServer("server1")
                .build();
    }
}