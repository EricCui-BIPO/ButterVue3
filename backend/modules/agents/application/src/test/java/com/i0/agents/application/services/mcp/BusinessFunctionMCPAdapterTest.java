package com.i0.agents.application.services.mcp;

import com.i0.agents.domain.services.BusinessFunctionRegistry;
import com.i0.agents.domain.services.mcp.MCPTool;
import com.i0.agents.domain.services.mcp.MCPToolResult;
import com.i0.agents.domain.services.mcp.MCPServerType;
import com.i0.agents.domain.valueobjects.BusinessFunction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessFunctionMCPAdapterTest {

    @Mock
    private BusinessFunctionRegistry businessFunctionRegistry;

    private BusinessFunctionMCPAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new BusinessFunctionMCPAdapter(businessFunctionRegistry);
    }

    @Test
    void shouldReturnCorrectServerInfo() {
        // When & Then
        assertThat(adapter.getName()).isEqualTo("business-functions");
        assertThat(adapter.getVersion()).isEqualTo("1.0.0");
        assertThat(adapter.getType()).isEqualTo(MCPServerType.BUSINESS_FUNCTIONS);
    }

    @Test
    void shouldBeAvailableWhenRegistryHasFunctions() {
        // Given
        when(businessFunctionRegistry.count()).thenReturn(5);

        // When & Then
        assertThat(adapter.isAvailable()).isTrue();
    }

    @Test
    void shouldNotBeAvailableWhenRegistryIsEmpty() {
        // Given
        when(businessFunctionRegistry.count()).thenReturn(0);

        // When & Then
        assertThat(adapter.isAvailable()).isFalse();
    }

    @Test
    void shouldNotBeAvailableWhenRegistryIsNull() {
        // Given
        BusinessFunctionMCPAdapter nullAdapter = new BusinessFunctionMCPAdapter(null);

        // When & Then
        assertThat(nullAdapter.isAvailable()).isFalse();
    }

    @Test
    void shouldGetAvailableTools() {
        // Given
        BusinessFunction function1 = createMockBusinessFunction("search", "Search function");
        BusinessFunction function2 = createMockBusinessFunction("create", "Create function");

        when(businessFunctionRegistry.getAllFunctions()).thenReturn(Arrays.asList(function1, function2));

        // When
        List<MCPTool> tools = adapter.getAvailableTools();

        // Then
        assertThat(tools).hasSize(2);
        assertThat(tools.get(0).getName()).isEqualTo("search");
        assertThat(tools.get(0).getSourceServer()).isEqualTo("business-functions");
        assertThat(tools.get(1).getName()).isEqualTo("create");
        assertThat(tools.get(1).getSourceServer()).isEqualTo("business-functions");
    }

    @Test
    void shouldReturnEmptyToolsWhenRegistryIsEmpty() {
        // Given
        when(businessFunctionRegistry.getAllFunctions()).thenReturn(Collections.emptyList());

        // When
        List<MCPTool> tools = adapter.getAvailableTools();

        // Then
        assertThat(tools).isEmpty();
    }

    @Test
    void shouldExecuteToolSuccessfully() {
        // Given
        String toolName = "searchFunction";
        Map<String, Object> arguments = Map.of("query", "test");
        BusinessFunction.FunctionCallResult expectedResult = BusinessFunction.FunctionCallResult.success("Search completed", Map.of("count", 5));

        when(businessFunctionRegistry.executeFunction(toolName, arguments)).thenReturn(expectedResult);

        // When
        MCPToolResult result = adapter.executeTool(toolName, arguments);

        // Then
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getResult()).isEqualTo("Search completed");
        assertThat(result.getToolName()).isEqualTo(toolName);
        assertThat(result.getServerName()).isEqualTo("business-functions");
        assertThat(result.getData()).isEqualTo(Map.of("count", 5));
        verify(businessFunctionRegistry).executeFunction(toolName, arguments);
    }

    @Test
    void shouldHandleToolExecutionFailure() {
        // Given
        String toolName = "failingFunction";
        Map<String, Object> arguments = Map.of();
        BusinessFunction.FunctionCallResult failureResult = BusinessFunction.FunctionCallResult.failure("Execution failed");

        when(businessFunctionRegistry.executeFunction(toolName, arguments)).thenReturn(failureResult);

        // When
        MCPToolResult result = adapter.executeTool(toolName, arguments);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isEqualTo("Execution failed");
        assertThat(result.getToolName()).isEqualTo(toolName);
        assertThat(result.getServerName()).isEqualTo("business-functions");
        verify(businessFunctionRegistry).executeFunction(toolName, arguments);
    }

    @Test
    void shouldHandleExecutionException() {
        // Given
        String toolName = "exceptionFunction";
        Map<String, Object> arguments = Map.of();

        when(businessFunctionRegistry.executeFunction(toolName, arguments))
                .thenThrow(new RuntimeException("Database error"));

        // When
        MCPToolResult result = adapter.executeTool(toolName, arguments);

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).contains("Execution failed: Database error");
        assertThat(result.getToolName()).isEqualTo(toolName);
        assertThat(result.getServerName()).isEqualTo("business-functions");
    }

    @Test
    void shouldHandleNullRegistry() {
        // Given
        BusinessFunctionMCPAdapter nullAdapter = new BusinessFunctionMCPAdapter(null);

        // When
        MCPToolResult result = nullAdapter.executeTool("testTool", Map.of());

        // Then
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getError()).isEqualTo("Business function registry is not available");
        assertThat(result.getToolName()).isEqualTo("testTool");
        assertThat(result.getServerName()).isEqualTo("business-functions");
    }

    @Test
    void shouldGetRegisteredFunctionCount() {
        // Given
        when(businessFunctionRegistry.count()).thenReturn(10);

        // When
        int count = adapter.getRegisteredFunctionCount();

        // Then
        assertThat(count).isEqualTo(10);
    }

    @Test
    void shouldReturnZeroCountWhenRegistryIsNull() {
        // Given
        BusinessFunctionMCPAdapter nullAdapter = new BusinessFunctionMCPAdapter(null);

        // When
        int count = nullAdapter.getRegisteredFunctionCount();

        // Then
        assertThat(count).isEqualTo(0);
    }

    @Test
    void shouldCheckHasFunction() {
        // Given
        String functionName = "testFunction";
        when(businessFunctionRegistry.isRegistered(functionName)).thenReturn(true);

        // When
        boolean hasFunction = adapter.hasFunction(functionName);

        // Then
        assertThat(hasFunction).isTrue();
        verify(businessFunctionRegistry).isRegistered(functionName);
    }

    @Test
    void shouldGetFunctionNames() {
        // Given
        Set<String> functionNames = Set.of("func1", "func2", "func3");
        when(businessFunctionRegistry.getFunctionNames()).thenReturn(functionNames);

        // When
        Set<String> result = adapter.getFunctionNames();

        // Then
        assertThat(result).isEqualTo(functionNames);
        verify(businessFunctionRegistry).getFunctionNames();
    }

    @Test
    void shouldReturnEmptyFunctionNamesWhenRegistryIsNull() {
        // Given
        BusinessFunctionMCPAdapter nullAdapter = new BusinessFunctionMCPAdapter(null);

        // When
        Set<String> result = nullAdapter.getFunctionNames();

        // Then
        assertThat(result).isEmpty();
    }

    private BusinessFunction createMockBusinessFunction(String name, String description) {
        Map<String, BusinessFunction.PropertyDefinition> parameters = Map.of(
                "query", BusinessFunction.PropertyDefinition.builder()
                        .type("string")
                        .description("Search query")
                        .build()
        );

        return BusinessFunction.builder()
                .name(name)
                .description(description)
                .parameter("query", parameters.get("query"))
                .required("query")
                .handler(args -> BusinessFunction.FunctionCallResult.success("Mock result"))
                .build();
    }
}