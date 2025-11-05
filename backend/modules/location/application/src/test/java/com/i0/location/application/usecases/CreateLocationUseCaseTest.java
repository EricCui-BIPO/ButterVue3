package com.i0.location.application.usecases;

import com.i0.location.application.dto.input.CreateLocationInput;
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

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * CreateLocationUseCase测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("创建地理位置用例测试")
class CreateLocationUseCaseTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private CreateLocationUseCase createLocationUseCase;

    private CreateLocationInput validInput;
    private Location savedLocation;

    @BeforeEach
    void setUp() {
        validInput = CreateLocationInput.builder()
                .name("测试城市")
                .locationType(LocationType.CITY)
                .isoCode("TC")
                .description("测试城市描述")
                .parentId("parent-id")
                .sortOrder(1)
                .build();

        savedLocation = Location.create(
                "测试城市",
                LocationType.CITY,
                "TC",
                "测试城市描述",
                "parent-id"
        );
        savedLocation.setId(UUID.randomUUID().toString());
        savedLocation.setSortOrder(1);
    }

    @Test
    @DisplayName("创建地理位置应该成功")
    void shouldCreateLocationSuccessfully() {
        // Given
        when(locationRepository.existsByName(anyString())).thenReturn(false);
        when(locationRepository.existsByIsoCode(anyString())).thenReturn(false);
        when(locationRepository.findById(anyString())).thenReturn(Optional.of(savedLocation));
        when(locationRepository.save(any(Location.class))).thenReturn(savedLocation);

        // When
        LocationOutput result = createLocationUseCase.execute(validInput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("测试城市");
        assertThat(result.getLocationType()).isEqualTo(LocationType.CITY);
        assertThat(result.getIsoCode()).isEqualTo("TC");
        assertThat(result.getDescription()).isEqualTo("测试城市描述");
        assertThat(result.getParentId()).isEqualTo("parent-id");
        assertThat(result.getSortOrder()).isEqualTo(1);
        assertThat(result.getActive()).isTrue();

        // Verify repository calls
        verify(locationRepository).existsByName("测试城市");
        verify(locationRepository).existsByIsoCode("TC");
        verify(locationRepository).findById("parent-id");
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    @DisplayName("创建大洲地理位置时不需要验证上级")
    void shouldCreateContinentWithoutParentValidation() {
        // Given
        CreateLocationInput continentInput = CreateLocationInput.builder()
                .name("测试大洲")
                .locationType(LocationType.CONTINENT)
                .isoCode("TC")
                .description("测试大洲描述")
                .parentId(null)
                .build();

        Location continent = Location.create(
                "测试大洲",
                LocationType.CONTINENT,
                "TC",
                "测试大洲描述",
                null
        );
        continent.setId(UUID.randomUUID().toString());

        when(locationRepository.existsByName(anyString())).thenReturn(false);
        when(locationRepository.existsByIsoCode(anyString())).thenReturn(false);
        when(locationRepository.save(any(Location.class))).thenReturn(continent);

        // When
        LocationOutput result = createLocationUseCase.execute(continentInput);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("测试大洲");
        assertThat(result.getLocationType()).isEqualTo(LocationType.CONTINENT);
        assertThat(result.getParentId()).isNull();

        // Verify repository calls - should not call findById for continent
        verify(locationRepository).existsByName("测试大洲");
        verify(locationRepository).existsByIsoCode("TC");
        verify(locationRepository, never()).findById(anyString());
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    @DisplayName("地理位置名称已存在时应该抛出异常")
    void shouldThrowExceptionWhenNameAlreadyExists() {
        // Given
        when(locationRepository.existsByName("测试城市")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> createLocationUseCase.execute(validInput))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("地理位置名称已存在: 测试城市");

        // Verify repository calls
        verify(locationRepository).existsByName("测试城市");
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    @DisplayName("ISO代码已存在时应该抛出异常")
    void shouldThrowExceptionWhenIsoCodeAlreadyExists() {
        // Given
        when(locationRepository.existsByName(anyString())).thenReturn(false);
        when(locationRepository.existsByIsoCode("TC")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> createLocationUseCase.execute(validInput))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISO代码已存在: TC");

        // Verify repository calls
        verify(locationRepository).existsByName("测试城市");
        verify(locationRepository).existsByIsoCode("TC");
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    @DisplayName("上级地理位置不存在时应该抛出异常")
    void shouldThrowExceptionWhenParentLocationNotFound() {
        // Given
        when(locationRepository.existsByName(anyString())).thenReturn(false);
        when(locationRepository.existsByIsoCode(anyString())).thenReturn(false);
        when(locationRepository.findById("parent-id")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> createLocationUseCase.execute(validInput))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("上级地理位置不存在或未激活: parent-id");

        // Verify repository calls
        verify(locationRepository).existsByName("测试城市");
        verify(locationRepository).existsByIsoCode("TC");
        verify(locationRepository).findById("parent-id");
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    @DisplayName("上级地理位置未激活时应该抛出异常")
    void shouldThrowExceptionWhenParentLocationIsInactive() {
        // Given
        Location inactiveParent = Location.create(
                "上级城市",
                LocationType.PROVINCE,
                null,
                "上级城市描述",
                "country-cn"
        );
        inactiveParent.setActive(false);

        when(locationRepository.existsByName(anyString())).thenReturn(false);
        when(locationRepository.existsByIsoCode(anyString())).thenReturn(false);
        when(locationRepository.findById("parent-id")).thenReturn(Optional.of(inactiveParent));

        // When & Then
        assertThatThrownBy(() -> createLocationUseCase.execute(validInput))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("上级地理位置不存在或未激活: parent-id");

        // Verify repository calls
        verify(locationRepository).existsByName("测试城市");
        verify(locationRepository).existsByIsoCode("TC");
        verify(locationRepository).findById("parent-id");
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    @DisplayName("没有提供ISO代码时不应验证ISO代码唯一性")
    void shouldNotValidateIsoCodeWhenNotProvided() {
        // Given
        CreateLocationInput inputWithoutIsoCode = CreateLocationInput.builder()
                .name("测试省份")
                .locationType(LocationType.PROVINCE)
                .isoCode(null) // 省份不需要ISO代码
                .description("测试省份描述")
                .parentId("parent-id")
                .build();

        Location savedProvince = Location.create(
                "测试省份",
                LocationType.PROVINCE,
                null,
                "测试省份描述",
                "parent-id"
        );
        savedProvince.setId(UUID.randomUUID().toString());

        when(locationRepository.existsByName(anyString())).thenReturn(false);
        when(locationRepository.findById(anyString())).thenReturn(Optional.of(savedLocation));
        when(locationRepository.save(any(Location.class))).thenReturn(savedProvince);

        // When
        LocationOutput result = createLocationUseCase.execute(inputWithoutIsoCode);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("测试省份");
        assertThat(result.getIsoCode()).isNull();

        // Verify repository calls - should not call existsByIsoCode
        verify(locationRepository).existsByName("测试省份");
        verify(locationRepository, never()).existsByIsoCode(anyString());
        verify(locationRepository).findById("parent-id");
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    @DisplayName("创建地理位置时应该设置正确的默认值")
    void shouldSetCorrectDefaultValuesWhenCreatingLocation() {
        // Given
        CreateLocationInput inputWithoutSortOrder = CreateLocationInput.builder()
                .name("测试城市")
                .locationType(LocationType.CITY)
                .isoCode("TC")
                .description("测试城市描述")
                .parentId("parent-id")
                .sortOrder(null) // 不设置排序序号
                .build();

        // Create a mock location with default sort order for this specific test
        Location defaultLocation = Location.create(
                "测试城市",
                LocationType.CITY,
                "TC",
                "测试城市描述",
                "parent-id"
        );
        defaultLocation.setId(UUID.randomUUID().toString());
        // Keep the default sortOrder of 0 from the factory method

        when(locationRepository.existsByName(anyString())).thenReturn(false);
        when(locationRepository.existsByIsoCode(anyString())).thenReturn(false);
        when(locationRepository.findById(anyString())).thenReturn(Optional.of(savedLocation));
        when(locationRepository.save(any(Location.class))).thenReturn(defaultLocation);

        // When
        LocationOutput result = createLocationUseCase.execute(inputWithoutSortOrder);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSortOrder()).isEqualTo(0); // 默认值

        // Verify that save was called with a location that has sortOrder 0
        verify(locationRepository).save(argThat(location -> location.getSortOrder() == 0));
    }
}