package com.i0.report.application.usecases;

import com.i0.report.application.dto.input.ReportDataGenerationInput;
import com.i0.report.application.dto.output.ReportDataOutput;
import com.i0.report.domain.entities.Chart;
import com.i0.report.domain.entities.Dataset;
import com.i0.report.domain.entities.Indicator;
import com.i0.report.domain.entities.Report;
import com.i0.report.domain.repositories.ChartRepository;
import com.i0.report.domain.repositories.IndicatorRepository;
import com.i0.report.domain.repositories.ReportRepository;
import com.i0.report.domain.services.ChartDataFormattingService;
import com.i0.report.domain.services.FilterMergingService;
import com.i0.report.domain.services.ReportDataCalculationService;
import com.i0.report.domain.valueobjects.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Refactored GenerateReportDataUseCase tests
 * Tests the clean architecture implementation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Refactored GenerateReportDataUseCase Tests")
class GenerateReportDataUseCaseRefactoredTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private ChartRepository chartRepository;

    @Mock
    private ReportDataCalculationService reportDataCalculationService;

    @Mock
    private FilterMergingService filterMergingService;

    @InjectMocks
    private GenerateReportDataUseCase generateReportDataUseCase;

    private Report mockReport;
    private Chart mockChart;
    private List<Filter> additionalFilters;
    private ReportDataGenerationInput input;

    @BeforeEach
    void setUp() {
        // Setup mock report
        mockReport = Report.builder()
                .id("report-123")
                .name("Test Report")
                .status("published")
                .enabled(true)
                .refreshInterval(30)
                .theme("light")
                .layout("grid")
                .filters(Arrays.asList(
                        Filter.builder().field("department").operator("=").value("IT").build()
                ))
                .build();

        // Setup mock chart
        mockChart = Chart.builder()
                .id("chart-456")
                .name("Department Count")
                .type("bar")
                .title("Employee Count by Department")
                .dimension("department")
                .indicatorId("indicator-789")
                .enabled(true)
                .config("{}")
                .build();

        // Setup additional filters
        additionalFilters = Arrays.asList(
                Filter.builder().field("status").operator("=").value("active").build()
        );

        // Setup input
        input = ReportDataGenerationInput.builder()
                .reportId("report-123")
                .additionalFilters(additionalFilters)
                .build();
    }

    @Test
    @DisplayName("Should generate report data successfully with clean architecture")
    void shouldGenerateReportData_When_CleanArchitectureImplementation() {
        // Given
        Map<String, Object> expectedChartData = Map.of(
                "chartId", "chart-456",
                "chartName", "Department Count",
                "chartType", "bar",
                "data", Map.of("test", "data")
        );

        when(reportRepository.findById("report-123")).thenReturn(Optional.of(mockReport));
        when(chartRepository.findByReportId("report-123")).thenReturn(Arrays.asList(mockChart));
        when(reportDataCalculationService.generateChartData(eq(mockChart), eq(additionalFilters)))
                .thenReturn(expectedChartData);

        // When
        ReportDataOutput result = generateReportDataUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals("report-123", result.getReportId());
        assertEquals("Test Report", result.getReportName());
        assertEquals("published", result.getReportStatus());
        assertEquals(30, result.getRefreshInterval());
        assertEquals("light", result.getTheme());
        assertEquals("grid", result.getLayout());
        assertNotNull(result.getTimestamp());
        assertNotNull(result.getChartData());
        assertEquals(1, result.getChartData().size());
        assertEquals(expectedChartData, result.getChartData().get(0));

        // Verify service interactions
        verify(reportRepository).findById("report-123");
        verify(chartRepository).findByReportId("report-123");
        verify(reportDataCalculationService).generateChartData(mockChart, additionalFilters);
        verifyNoInteractions(filterMergingService); // Should not be used in UseCase directly
    }

    @Test
    @DisplayName("Should use proper domain exceptions when report not found")
    void shouldThrowDomainException_When_ReportNotFound() {
        // Given
        when(reportRepository.findById("non-existent")).thenReturn(Optional.empty());

        ReportDataGenerationInput input = ReportDataGenerationInput.builder()
                .reportId("non-existent")
                .build();

        // When & Then
        assertThrows(Exception.class, () -> {
            generateReportDataUseCase.execute(input);
        });

        verify(reportRepository).findById("non-existent");
        verifyNoInteractions(chartRepository, reportDataCalculationService, filterMergingService);
    }

    @Test
    @DisplayName("Should use proper domain exceptions when report disabled")
    void shouldThrowDomainException_When_ReportDisabled() {
        // Given
        mockReport.setEnabled(false);
        when(reportRepository.findById("report-123")).thenReturn(Optional.of(mockReport));

        // When & Then
        assertThrows(Exception.class, () -> {
            generateReportDataUseCase.execute(input);
        });

        verify(reportRepository).findById("report-123");
        verifyNoInteractions(chartRepository, reportDataCalculationService, filterMergingService);
    }

    @Test
    @DisplayName("Should skip disabled charts in clean architecture")
    void shouldSkipDisabledCharts_When_ProcessingCharts() {
        // Given
        Chart disabledChart = Chart.builder()
                .id("disabled-chart")
                .name("Disabled Chart")
                .enabled(false)
                .build();

        Map<String, Object> expectedChartData = Map.of("chartId", "chart-456");

        when(reportRepository.findById("report-123")).thenReturn(Optional.of(mockReport));
        when(chartRepository.findByReportId("report-123"))
                .thenReturn(Arrays.asList(mockChart, disabledChart));
        when(reportDataCalculationService.generateChartData(eq(mockChart), eq(additionalFilters)))
                .thenReturn(expectedChartData);

        // When
        ReportDataOutput result = generateReportDataUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getChartData().size());

        verify(reportRepository).findById("report-123");
        verify(chartRepository).findByReportId("report-123");
        verify(reportDataCalculationService).generateChartData(mockChart, additionalFilters);
        verify(reportDataCalculationService, never()).generateChartData(eq(disabledChart), any());
    }

    @Test
    @DisplayName("Should handle chart generation errors gracefully in clean architecture")
    void shouldHandleChartErrors_When_ServiceThrowsException() {
        // Given
        Chart errorChart = Chart.builder()
                .id("error-chart")
                .name("Error Chart")
                .indicatorId("error-indicator")
                .enabled(true)
                .build();

        Map<String, Object> successChartData = Map.of("chartId", "chart-456");

        when(reportRepository.findById("report-123")).thenReturn(Optional.of(mockReport));
        when(chartRepository.findByReportId("report-123"))
                .thenReturn(Arrays.asList(mockChart, errorChart));
        when(reportDataCalculationService.generateChartData(eq(mockChart), eq(additionalFilters)))
                .thenReturn(successChartData);
        when(reportDataCalculationService.generateChartData(eq(errorChart), eq(additionalFilters)))
                .thenThrow(new RuntimeException("Service error"));

        // When
        ReportDataOutput result = generateReportDataUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getChartData().size()); // Only successful chart
        assertEquals(successChartData, result.getChartData().get(0));

        verify(reportDataCalculationService).generateChartData(mockChart, additionalFilters);
        verify(reportDataCalculationService).generateChartData(errorChart, additionalFilters);
    }

    @Test
    @DisplayName("Should handle empty chart list gracefully")
    void shouldHandleEmptyCharts_When_NoChartsExist() {
        // Given
        when(reportRepository.findById("report-123")).thenReturn(Optional.of(mockReport));
        when(chartRepository.findByReportId("report-123")).thenReturn(new ArrayList<>());

        // When
        ReportDataOutput result = generateReportDataUseCase.execute(input);

        // Then
        assertNotNull(result);
        assertEquals("report-123", result.getReportId());
        assertNotNull(result.getChartData());
        assertTrue(result.getChartData().isEmpty());

        verify(reportRepository).findById("report-123");
        verify(chartRepository).findByReportId("report-123");
        verifyNoInteractions(reportDataCalculationService);
    }

    @Test
    @DisplayName("Should handle null additional filters gracefully")
    void shouldHandleNullFilters_When_NoAdditionalFilters() {
        // Given
        ReportDataGenerationInput inputWithNullFilters = ReportDataGenerationInput.builder()
                .reportId("report-123")
                .additionalFilters(null)
                .build();

        Map<String, Object> expectedChartData = Map.of("chartId", "chart-456");

        when(reportRepository.findById("report-123")).thenReturn(Optional.of(mockReport));
        when(chartRepository.findByReportId("report-123")).thenReturn(Arrays.asList(mockChart));
        when(reportDataCalculationService.generateChartData(eq(mockChart), isNull()))
                .thenReturn(expectedChartData);

        // When
        ReportDataOutput result = generateReportDataUseCase.execute(inputWithNullFilters);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getChartData().size());

        verify(reportDataCalculationService).generateChartData(mockChart, null);
    }

    @Test
    @DisplayName("Should validate input parameters properly")
    void shouldValidateInput_When_InvalidInput() {
        // Given
        ReportDataGenerationInput invalidInput = ReportDataGenerationInput.builder()
                .reportId("") // Empty report ID
                .build();

        // When & Then
        // This will likely fail at the domain validation level
        when(reportRepository.findById("")).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> {
            generateReportDataUseCase.execute(invalidInput);
        });

        verify(reportRepository).findById("");
    }
}