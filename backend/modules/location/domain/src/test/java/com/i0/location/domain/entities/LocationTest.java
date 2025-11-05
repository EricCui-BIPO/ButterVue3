package com.i0.location.domain.entities;

import com.i0.location.domain.valueobjects.LocationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Location实体测试类
 */
@DisplayName("Location实体测试")
class LocationTest {

    private Location location;

    @BeforeEach
    void setUp() {
        location = Location.create(
                "测试城市",
                LocationType.CITY,
                "TC",
                "测试城市描述",
                "parent-id"
        );
    }

    @Test
    @DisplayName("创建地理位置应该成功")
    void shouldCreateLocationSuccessfully() {
        // Given
        String name = "北京";
        LocationType type = LocationType.CITY;
        String isoCode = "BJ";
        String description = "中国首都";
        String parentId = "country-cn";

        // When
        Location location = Location.create(name, type, isoCode, description, parentId);

        // Then
        assertThat(location).isNotNull();
        assertThat(location.getName()).isEqualTo(name);
        assertThat(location.getLocationType()).isEqualTo(type);
        assertThat(location.getIsoCode()).isEqualTo(isoCode);
        assertThat(location.getDescription()).isEqualTo(description);
        assertThat(location.getParentId()).isEqualTo(parentId);
        assertThat(location.getLevel()).isEqualTo(3);
        assertThat(location.isActive()).isTrue();
        assertThat(location.getCreatedAt()).isNotNull();
        assertThat(location.getUpdatedAt()).isNotNull();
        assertThat(location.getVersion()).isEqualTo(0L);
    }

    @Test
    @DisplayName("创建大洲地理位置时parentId应该为null")
    void shouldHaveNullParentIdForContinent() {
        // Given
        String name = "亚洲";
        LocationType type = LocationType.CONTINENT;
        String isoCode = "AS";
        String description = "亚洲大洲";

        // When
        Location location = Location.create(name, type, isoCode, description, null);

        // Then
        assertThat(location.getParentId()).isNull();
        assertThat(location.getLevel()).isEqualTo(0);
    }

    @Test
    @DisplayName("创建地理位置时名称不能为空")
    void shouldThrowExceptionWhenNameIsEmpty() {
        // Given
        String name = "";
        LocationType type = LocationType.CITY;
        String isoCode = "TC";
        String description = "测试描述";
        String parentId = "parent-id";

        // When & Then
        assertThatThrownBy(() -> Location.create(name, type, isoCode, description, parentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("地理位置名称不能为空");
    }

    @Test
    @DisplayName("创建地理位置时类型不能为空")
    void shouldThrowExceptionWhenLocationTypeIsNull() {
        // Given
        String name = "测试城市";
        LocationType type = null;
        String isoCode = "TC";
        String description = "测试描述";
        String parentId = "parent-id";

        // When & Then
        assertThatThrownBy(() -> Location.create(name, type, isoCode, description, parentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("地理位置类型不能为空");
    }

    @Test
    @DisplayName("创建国家时ISO代码不能为空")
    void shouldThrowExceptionWhenCountryIsoCodeIsEmpty() {
        // Given
        String name = "测试国家";
        LocationType type = LocationType.COUNTRY;
        String isoCode = "";
        String description = "测试国家描述";
        String parentId = "continent-as";

        // When & Then
        assertThatThrownBy(() -> Location.create(name, type, isoCode, description, parentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("国家的ISO代码不能为空");
    }

    @Test
    @DisplayName("创建大洲时不能有上级地理位置")
    void shouldThrowExceptionWhenContinentHasParent() {
        // Given
        String name = "测试大洲";
        LocationType type = LocationType.CONTINENT;
        String isoCode = "TC";
        String description = "测试大洲描述";
        String parentId = "invalid-parent";

        // When & Then
        assertThatThrownBy(() -> Location.create(name, type, isoCode, description, parentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("大洲不能有上级地理位置");
    }

    @Test
    @DisplayName("非大洲地理位置必须有上级")
    void shouldThrowExceptionWhenNonContinentHasNoParent() {
        // Given
        String name = "测试城市";
        LocationType type = LocationType.CITY;
        String isoCode = "TC";
        String description = "测试城市描述";
        String parentId = null;

        // When & Then
        assertThatThrownBy(() -> Location.create(name, type, isoCode, description, parentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("市必须有上级地理位置");
    }

    @Test
    @DisplayName("ISO代码格式验证")
    void shouldValidateIsoCodeFormat() {
        // Given
        String name = "测试国家";
        LocationType type = LocationType.COUNTRY;
        String description = "测试国家描述";
        String parentId = "continent-as";

        // When & Then - 有效的ISO代码应该成功创建
        assertThatCode(() -> Location.create(name, type, "CN", description, parentId))
                .doesNotThrowAnyException();
        
        assertThatCode(() -> Location.create(name, type, "CN-44", description, parentId))
                .doesNotThrowAnyException();
        
        assertThatCode(() -> Location.create(name, type, "CN-44-001", description, parentId))
                .doesNotThrowAnyException();

        // 无效的ISO代码应该抛出异常
        assertThatThrownBy(() -> Location.create(name, type, "CN-44-001-X", description, parentId)) // 超过10位
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISO代码长度不能超过10位");
        
        assertThatThrownBy(() -> Location.create(name, type, "CN@44", description, parentId)) // 包含特殊字符
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ISO代码只能包含字母、数字和连字符");
    }

    @Test
    @DisplayName("更新地理位置信息应该成功")
    void shouldUpdateLocationSuccessfully() {
        // Given
        LocalDateTime originalUpdatedAt = location.getUpdatedAt();

        // When
        location.update("更新后的城市", "更新后的描述", 10);

        // Then
        assertThat(location.getName()).isEqualTo("更新后的城市");
        assertThat(location.getDescription()).isEqualTo("更新后的描述");
        assertThat(location.getSortOrder()).isEqualTo(10);
        assertThat(location.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("激活地理位置应该成功")
    void shouldActivateLocationSuccessfully() {
        // Given
        location.setActive(false);
        LocalDateTime originalUpdatedAt = location.getUpdatedAt();

        // When
        location.activate();

        // Then
        assertThat(location.isActive()).isTrue();
        assertThat(location.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("停用地理位置应该成功")
    void shouldDeactivateLocationSuccessfully() {
        // Given
        location.setActive(true);
        LocalDateTime originalUpdatedAt = location.getUpdatedAt();

        // When
        location.deactivate();

        // Then
        assertThat(location.isActive()).isFalse();
        assertThat(location.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    @DisplayName("检查地理位置类型方法应该正确返回")
    void shouldCheckLocationTypeCorrectly() {
        // Given & When
        Location continent = Location.create("亚洲", LocationType.CONTINENT, "AS", "亚洲大洲", null);
        Location country = Location.create("中国", LocationType.COUNTRY, "CN", "中国", "continent-as");
        Location province = Location.create("北京", LocationType.PROVINCE, null, "北京市", "country-cn");
        Location city = Location.create("朝阳区", LocationType.CITY, null, "北京市朝阳区", "province-bj");

        // Then
        assertThat(continent.isContinent()).isTrue();
        assertThat(continent.isCountry()).isFalse();
        assertThat(continent.isProvince()).isFalse();
        assertThat(continent.isCity()).isFalse();

        assertThat(country.isContinent()).isFalse();
        assertThat(country.isCountry()).isTrue();
        assertThat(country.isProvince()).isFalse();
        assertThat(country.isCity()).isFalse();

        assertThat(province.isContinent()).isFalse();
        assertThat(province.isCountry()).isFalse();
        assertThat(province.isProvince()).isTrue();
        assertThat(province.isCity()).isFalse();

        assertThat(city.isContinent()).isFalse();
        assertThat(city.isCountry()).isFalse();
        assertThat(city.isProvince()).isFalse();
        assertThat(city.isCity()).isTrue();
    }

    @Test
    @DisplayName("检查地理位置关系方法应该正确返回")
    void shouldCheckLocationRelationshipsCorrectly() {
        // Given
        Location continent = Location.create("亚洲", LocationType.CONTINENT, "AS", "亚洲大洲", null);
        Location country = Location.create("中国", LocationType.COUNTRY, "CN", "中国", "continent-as");
        Location province = Location.create("北京", LocationType.PROVINCE, null, "北京市", "country-cn");
        Location city = Location.create("朝阳区", LocationType.CITY, null, "北京市朝阳区", "province-bj");

        // Then
        assertThat(continent.hasParent()).isFalse();
        assertThat(continent.canHaveChildren()).isTrue();

        assertThat(country.hasParent()).isTrue();
        assertThat(country.canHaveChildren()).isTrue();

        assertThat(province.hasParent()).isTrue();
        assertThat(province.canHaveChildren()).isTrue();

        assertThat(city.hasParent()).isTrue();
        assertThat(city.canHaveChildren()).isFalse();
    }

    @Test
    @DisplayName("地理位置equals和hashCode应该基于ID")
    void shouldEqualsAndHashCodeBasedOnId() {
        // Given
        String id = "test-id";
        Location location1 = Location.create("城市1", LocationType.CITY, "BJ", "描述1", "parent1");
        Location location2 = Location.create("城市2", LocationType.CITY, "SH", "描述2", "parent2");
        location1.setId(id);
        location2.setId(id);

        // When & Then
        assertThat(location1).isEqualTo(location2);
        assertThat(location1.hashCode()).isEqualTo(location2.hashCode());

        Location location3 = Location.create("城市3", LocationType.CITY, "GZ", "描述3", "parent3");
        location3.setId("different-id");

        assertThat(location1).isNotEqualTo(location3);
        assertThat(location1.hashCode()).isNotEqualTo(location3.hashCode());
    }

    @Test
    @DisplayName("验证地理位置完整性应该成功")
    void shouldValidateLocationSuccessfully() {
        // Given
        Location validLocation = Location.create(
                "有效城市",
                LocationType.CITY,
                "VC",
                "有效城市描述",
                "parent-id"
        );

        // When & Then
        assertThatCode(validLocation::validate).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("验证无效地理位置应该抛出异常")
    void shouldThrowExceptionWhenValidatingInvalidLocation() {
        // Given
        Location invalidLocation = new Location();
        invalidLocation.setName("");
        invalidLocation.setLocationType(null);
        invalidLocation.setIsoCode("INVALID");

        // When & Then
        assertThatThrownBy(invalidLocation::validate)
                .isInstanceOf(IllegalArgumentException.class);
    }
}