package com.i0.location.domain.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * LocationType枚举测试类
 */
@DisplayName("LocationType枚举测试")
class LocationTypeTest {

    @Test
    @DisplayName("根据显示名称获取枚举值应该成功")
    void shouldGetLocationTypeByDisplayName() {
        // When & Then
        assertThat(LocationType.fromDisplayName("Continent")).isEqualTo(LocationType.CONTINENT);
        assertThat(LocationType.fromDisplayName("Country")).isEqualTo(LocationType.COUNTRY);
        assertThat(LocationType.fromDisplayName("Province")).isEqualTo(LocationType.PROVINCE);
        assertThat(LocationType.fromDisplayName("City")).isEqualTo(LocationType.CITY);
    }

    @Test
    @DisplayName("根据无效显示名称获取枚举值应该抛出异常")
    void shouldThrowExceptionWhenDisplayNameIsInvalid() {
        // When & Then
        assertThatThrownBy(() -> LocationType.fromDisplayName("Invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown location type: Invalid");
    }

    @Test
    @DisplayName("根据中文名称获取枚举值应该成功")
    void shouldGetLocationTypeByChineseName() {
        // When & Then
        assertThat(LocationType.fromChineseName("大洲")).isEqualTo(LocationType.CONTINENT);
        assertThat(LocationType.fromChineseName("国家")).isEqualTo(LocationType.COUNTRY);
        assertThat(LocationType.fromChineseName("省/州")).isEqualTo(LocationType.PROVINCE);
        assertThat(LocationType.fromChineseName("市")).isEqualTo(LocationType.CITY);
    }

    @Test
    @DisplayName("根据无效中文名称获取枚举值应该抛出异常")
    void shouldThrowExceptionWhenChineseNameIsInvalid() {
        // When & Then
        assertThatThrownBy(() -> LocationType.fromChineseName("无效"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown location type: 无效");
    }

    @Test
    @DisplayName("检查类型方法应该正确返回")
    void shouldCheckTypeMethodsCorrectly() {
        // Given & When & Then
        assertThat(LocationType.CONTINENT.isContinent()).isTrue();
        assertThat(LocationType.CONTINENT.isCountry()).isFalse();
        assertThat(LocationType.CONTINENT.isProvince()).isFalse();
        assertThat(LocationType.CONTINENT.isCity()).isFalse();

        assertThat(LocationType.COUNTRY.isContinent()).isFalse();
        assertThat(LocationType.COUNTRY.isCountry()).isTrue();
        assertThat(LocationType.COUNTRY.isProvince()).isFalse();
        assertThat(LocationType.COUNTRY.isCity()).isFalse();

        assertThat(LocationType.PROVINCE.isContinent()).isFalse();
        assertThat(LocationType.PROVINCE.isCountry()).isFalse();
        assertThat(LocationType.PROVINCE.isProvince()).isTrue();
        assertThat(LocationType.PROVINCE.isCity()).isFalse();

        assertThat(LocationType.CITY.isContinent()).isFalse();
        assertThat(LocationType.CITY.isCountry()).isFalse();
        assertThat(LocationType.CITY.isProvince()).isFalse();
        assertThat(LocationType.CITY.isCity()).isTrue();
    }

    @Test
    @DisplayName("检查关系方法应该正确返回")
    void shouldCheckRelationshipMethodsCorrectly() {
        // Given & When & Then
        assertThat(LocationType.CONTINENT.canHaveParent()).isFalse();
        assertThat(LocationType.CONTINENT.canHaveChildren()).isTrue();

        assertThat(LocationType.COUNTRY.canHaveParent()).isTrue();
        assertThat(LocationType.COUNTRY.canHaveChildren()).isTrue();

        assertThat(LocationType.PROVINCE.canHaveParent()).isTrue();
        assertThat(LocationType.PROVINCE.canHaveChildren()).isTrue();

        assertThat(LocationType.CITY.canHaveParent()).isTrue();
        assertThat(LocationType.CITY.canHaveChildren()).isFalse();
    }

    @Test
    @DisplayName("获取显示名称应该正确返回")
    void shouldGetDisplayNameCorrectly() {
        // When & Then
        assertThat(LocationType.CONTINENT.getDisplayName()).isEqualTo("Continent");
        assertThat(LocationType.COUNTRY.getDisplayName()).isEqualTo("Country");
        assertThat(LocationType.PROVINCE.getDisplayName()).isEqualTo("Province");
        assertThat(LocationType.CITY.getDisplayName()).isEqualTo("City");
    }

    @Test
    @DisplayName("获取中文名称应该正确返回")
    void shouldGetChineseNameCorrectly() {
        // When & Then
        assertThat(LocationType.CONTINENT.getChineseName()).isEqualTo("大洲");
        assertThat(LocationType.COUNTRY.getChineseName()).isEqualTo("国家");
        assertThat(LocationType.PROVINCE.getChineseName()).isEqualTo("省/州");
        assertThat(LocationType.CITY.getChineseName()).isEqualTo("市");
    }

    @Test
    @DisplayName("toString应该返回显示名称")
    void shouldToStringReturnDisplayName() {
        // When & Then
        assertThat(LocationType.CONTINENT.toString()).isEqualTo("Continent");
        assertThat(LocationType.COUNTRY.toString()).isEqualTo("Country");
        assertThat(LocationType.PROVINCE.toString()).isEqualTo("Province");
        assertThat(LocationType.CITY.toString()).isEqualTo("City");
    }

    @Test
    @DisplayName("values方法应该返回所有枚举值")
    void shouldValuesReturnAllEnumValues() {
        // When
        LocationType[] values = LocationType.values();

        // Then
        assertThat(values).hasSize(4);
        assertThat(values).containsExactlyInAnyOrder(
                LocationType.CONTINENT,
                LocationType.COUNTRY,
                LocationType.PROVINCE,
                LocationType.CITY
        );
    }

    @Test
    @DisplayName("valueOf方法应该正确返回枚举值")
    void shouldValueOfReturnCorrectEnumValue() {
        // When & Then
        assertThat(LocationType.valueOf("CONTINENT")).isEqualTo(LocationType.CONTINENT);
        assertThat(LocationType.valueOf("COUNTRY")).isEqualTo(LocationType.COUNTRY);
        assertThat(LocationType.valueOf("PROVINCE")).isEqualTo(LocationType.PROVINCE);
        assertThat(LocationType.valueOf("CITY")).isEqualTo(LocationType.CITY);
    }
}