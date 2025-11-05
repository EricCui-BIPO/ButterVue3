package com.i0.location.application.usecases;

import com.i0.location.application.dto.input.LocationPageInput;
import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.domain.entities.Location;
import com.i0.location.domain.repositories.LocationRepository;
import com.i0.location.domain.valueobjects.LocationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.i0.domain.core.pagination.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

/**
 * SearchLocationsUseCase测试类
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("分页查询地理位置用例测试")
class SearchLocationsUseCaseTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private SearchLocationsUseCase searchLocationsUseCase;

    private Location location1;
    private Location location2;
    private Location location3;
    private Pageable<Location> locationPage;

    /**
     * 创建用于测试的Location Page对象
     */
    private Pageable<Location> createTestPage(List<Location> locations, int page, int size, long total) {
        Pageable<Location> mockPage = mock(Pageable.class);
        when(mockPage.getContent()).thenReturn(locations);
        when(mockPage.getPage()).thenReturn(page);
        when(mockPage.getSize()).thenReturn(locations.size());
        when(mockPage.getTotal()).thenReturn(total);
        when(mockPage.getTotalPages()).thenReturn((int) Math.ceil((double) total / size));
        when(mockPage.getNumberOfElements()).thenReturn(locations.size());
        when(mockPage.isEmpty()).thenReturn(locations.isEmpty());
        when(mockPage.isFirst()).thenReturn(page == 0);
        when(mockPage.isLast()).thenReturn(page >= (int) Math.ceil((double) total / size) - 1);
        when(mockPage.hasNext()).thenReturn(page < (int) Math.ceil((double) total / size) - 1);
        when(mockPage.hasPrevious()).thenReturn(page > 0);
        when(mockPage.map(any())).thenAnswer(invocation -> {
            Function<Location, ?> mapper = invocation.getArgument(0);
            List<LocationOutput> mappedContent = locations.stream()
                    .map(location -> LocationOutput.from(location))
                    .collect(java.util.stream.Collectors.toList());
            return createTestOutputPage(mappedContent, page, size, total);
        });
        return mockPage;
    }

    /**
     * 创建用于测试的LocationOutput Page对象
     */
    @SuppressWarnings("unchecked")
    private <T> Pageable<T> createTestOutputPage(List<T> content, int page, int size, long total) {
        Pageable<T> mockPage = mock(Pageable.class);
        when(mockPage.getContent()).thenReturn(content);
        when(mockPage.getPage()).thenReturn(page);
        when(mockPage.getSize()).thenReturn(content.size());
        when(mockPage.getTotal()).thenReturn(total);
        when(mockPage.getTotalPages()).thenReturn((int) Math.ceil((double) total / size));
        when(mockPage.getNumberOfElements()).thenReturn(content.size());
        when(mockPage.isEmpty()).thenReturn(content.isEmpty());
        when(mockPage.isFirst()).thenReturn(page == 0);
        when(mockPage.isLast()).thenReturn(page >= (int) Math.ceil((double) total / size) - 1);
        when(mockPage.hasNext()).thenReturn(page < (int) Math.ceil((double) total / size) - 1);
        when(mockPage.hasPrevious()).thenReturn(page > 0);
        when(mockPage.map(any())).thenAnswer(invocation -> mockPage);
        return mockPage;
    }

    @BeforeEach
    void setUp() {
        // 创建测试数据
        location1 = Location.create("北京", LocationType.CITY, "BJ", "北京市", "province-bj");
        location1.setId(UUID.randomUUID().toString());

        location2 = Location.create("上海", LocationType.CITY, "SH", "上海市", "province-sh");
        location2.setId(UUID.randomUUID().toString());

        location3 = Location.create("广州", LocationType.CITY, "GZ", "广州市", "province-gd");
        location3.setId(UUID.randomUUID().toString());

        List<Location> locations = Arrays.asList(location1, location2, location3);
        locationPage = createTestPage(locations, 0, 10, 3);
    }

    @Test
    @DisplayName("分页查询所有地理位置应该成功")
    void shouldSearchAllLocationsSuccessfully() {
        // Given
        LocationPageInput input = LocationPageInput.builder()
                .page(0)
                .size(10)
                .build();

        when(locationRepository.searchLocations(
                isNull(), isNull(), isNull(), isNull(), eq(0), eq(10)
        )).thenReturn(locationPage);

        // When
        Pageable<LocationOutput> result = searchLocationsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotal()).isEqualTo(3);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(3);

        // 验证转换后的数据
        List<LocationOutput> content = result.getContent();
        assertThat(content.get(0).getName()).isEqualTo("北京");
        assertThat(content.get(1).getName()).isEqualTo("上海");
        assertThat(content.get(2).getName()).isEqualTo("广州");

        // Verify repository call
        verify(locationRepository).searchLocations(null, null, null, null, 0, 10);
    }

    @Test
    @DisplayName("根据名称关键字查询地理位置应该成功")
    void shouldSearchLocationsByNameKeywordSuccessfully() {
        // Given
        LocationPageInput input = LocationPageInput.builder()
                .name("北京")
                .page(0)
                .size(10)
                .build();

        List<Location> filteredLocations = Arrays.asList(location1);
        Pageable<Location> filteredPage = createTestPage(filteredLocations, 0, 10, 1);

        when(locationRepository.searchLocations(
                eq("北京"), isNull(), isNull(), isNull(), eq(0), eq(10)
        )).thenReturn(filteredPage);

        // When
        Pageable<LocationOutput> result = searchLocationsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("北京");

        // Verify repository call
        verify(locationRepository).searchLocations("北京", null, null, null, 0, 10);
    }

    @Test
    @DisplayName("根据地理位置类型查询应该成功")
    void shouldSearchLocationsByTypeSuccessfully() {
        // Given
        LocationPageInput input = LocationPageInput.builder()
                .locationType(LocationType.CITY)
                .page(0)
                .size(10)
                .build();

        when(locationRepository.searchLocations(
                isNull(), eq(LocationType.CITY), isNull(), isNull(), eq(0), eq(10)
        )).thenReturn(locationPage);

        // When
        Pageable<LocationOutput> result = searchLocationsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getLocationType()).isEqualTo(LocationType.CITY);

        // Verify repository call
        verify(locationRepository).searchLocations(null, LocationType.CITY, null, null, 0, 10);
    }

    @Test
    @DisplayName("根据上级ID查询应该成功")
    void shouldSearchLocationsByParentIdSuccessfully() {
        // Given
        LocationPageInput input = LocationPageInput.builder()
                .parentId("province-bj")
                .page(0)
                .size(10)
                .build();

        List<Location> filteredLocations = Arrays.asList(location1);
        Pageable<Location> filteredPage = createTestPage(filteredLocations, 0, 10, 1);

        when(locationRepository.searchLocations(
                isNull(), isNull(), eq("province-bj"), isNull(), eq(0), eq(10)
        )).thenReturn(filteredPage);

        // When
        Pageable<LocationOutput> result = searchLocationsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getParentId()).isEqualTo("province-bj");

        // Verify repository call
        verify(locationRepository).searchLocations(null, null, "province-bj", null, 0, 10);
    }

    @Test
    @DisplayName("查询激活状态的地理位置应该成功")
    void shouldSearchActiveLocationsSuccessfully() {
        // Given
        LocationPageInput input = LocationPageInput.builder()
                .activeOnly(true)
                .page(0)
                .size(10)
                .build();

        // 设置location1为激活状态，其他为未激活状态
        location1.setActive(true);
        location2.setActive(false);
        location3.setActive(false);

        List<Location> activeLocations = Arrays.asList(location1);
        Pageable<Location> activePage = createTestPage(activeLocations, 0, 10, 1);

        when(locationRepository.searchLocations(
                isNull(), isNull(), isNull(), eq(true), eq(0), eq(10)
        )).thenReturn(activePage);

        // When
        Pageable<LocationOutput> result = searchLocationsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getActive()).isTrue();

        // Verify repository call
        verify(locationRepository).searchLocations(null, null, null, true, 0, 10);
    }

    @Test
    @DisplayName("综合条件查询应该成功")
    void shouldSearchLocationsWithMultipleCriteriaSuccessfully() {
        // Given
        LocationPageInput input = LocationPageInput.builder()
                .name("京")
                .locationType(LocationType.CITY)
                .parentId("province-bj")
                .activeOnly(true)
                .page(0)
                .size(10)
                .build();

        List<Location> filteredLocations = Arrays.asList(location1);
        Pageable<Location> filteredPage = createTestPage(filteredLocations, 0, 10, 1);

        when(locationRepository.searchLocations(
                eq("京"), eq(LocationType.CITY), eq("province-bj"), eq(true), eq(0), eq(10)
        )).thenReturn(filteredPage);

        // When
        Pageable<LocationOutput> result = searchLocationsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        LocationOutput output = result.getContent().get(0);
        assertThat(output.getName()).isEqualTo("北京");
        assertThat(output.getLocationType()).isEqualTo(LocationType.CITY);
        assertThat(output.getParentId()).isEqualTo("province-bj");
        assertThat(output.getActive()).isTrue();

        // Verify repository call
        verify(locationRepository).searchLocations("京", LocationType.CITY, "province-bj", true, 0, 10);
    }

    @Test
    @DisplayName("分页参数应该正确传递")
    void shouldPassPaginationParametersCorrectly() {
        // Given
        LocationPageInput input = LocationPageInput.builder()
                .page(2)
                .size(5)
                .build();

        // Create a page with page number 2 for this specific test
        Pageable<Location> page2Result = createTestPage(Arrays.asList(location1, location2, location3), 2, 5, 3);

        when(locationRepository.searchLocations(
                isNull(), isNull(), isNull(), isNull(), eq(2), eq(5)
        )).thenReturn(page2Result);

        // When
        Pageable<LocationOutput> result = searchLocationsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getPage()).isEqualTo(2);

        // Verify repository call
        verify(locationRepository).searchLocations(null, null, null, null, 2, 5);
    }

    @Test
    @DisplayName("查询结果为空时应该返回空页面")
    void shouldReturnEmptyPageWhenNoResultsFound() {
        // Given
        LocationPageInput input = LocationPageInput.builder()
                .name("不存在的城市")
                .page(0)
                .size(10)
                .build();

        Pageable<Location> emptyPage = createTestPage(List.of(), 0, 10, 0);

        when(locationRepository.searchLocations(
                eq("不存在的城市"), isNull(), isNull(), isNull(), eq(0), eq(10)
        )).thenReturn(emptyPage);

        // When
        Pageable<LocationOutput> result = searchLocationsUseCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotal()).isZero();

        // Verify repository call
        verify(locationRepository).searchLocations("不存在的城市", null, null, null, 0, 10);
    }
}