package com.i0.app.integration.config;

import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.application.usecases.GetLocationUseCase;
import com.i0.location.application.usecases.GetLocationsBatchUseCase;
import com.i0.location.domain.valueobjects.LocationType;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Location模块测试配置
 * 
 * 为集成测试提供Location相关UseCase的模拟实现
 * 基于测试数据库中的location数据提供响应
 */
@TestConfiguration
public class LocationTestConfiguration {

    /**
     * 模拟GetLocationUseCase，基于测试数据提供Location信息
     */
    @Bean
    @Primary
    public GetLocationUseCase getLocationUseCase() {
        return new GetLocationUseCase(null) {
            @Override
            public LocationOutput execute(String locationId) {
                // 基于测试数据库中的location数据返回相应的LocationOutput
                LocationOutput locationOutput = new LocationOutput();
                locationOutput.setId(locationId);
                locationOutput.setActive(true);
                
                // 根据locationId返回对应的测试数据
                switch (locationId) {
                    case "city-gd-sz":
                        locationOutput.setName("深圳市");
                        locationOutput.setLocationType(LocationType.CITY);
                        locationOutput.setLocationTypeDisplayName("City");
                        locationOutput.setIsoCode("CN-GD-SZ");
                        break;
                    case "country-us":
                        locationOutput.setName("美国");
                        locationOutput.setLocationType(LocationType.COUNTRY);
                        locationOutput.setLocationTypeDisplayName("Country");
                        locationOutput.setIsoCode("US");
                        break;
                    case "country-cn":
                        locationOutput.setName("中国");
                        locationOutput.setLocationType(LocationType.COUNTRY);
                        locationOutput.setLocationTypeDisplayName("Country");
                        locationOutput.setIsoCode("CN");
                        break;
                    case "province-gd":
                        locationOutput.setName("广东省");
                        locationOutput.setLocationType(LocationType.PROVINCE);
                        locationOutput.setLocationTypeDisplayName("Province");
                        locationOutput.setIsoCode("CN-GD");
                        break;
                    case "city-bj-bj":
                        locationOutput.setName("北京市");
                        locationOutput.setLocationType(LocationType.CITY);
                        locationOutput.setLocationTypeDisplayName("City");
                        locationOutput.setIsoCode("CN-BJ");
                        break;
                    case "beijing-001":
                        locationOutput.setName("北京");
                        locationOutput.setLocationType(LocationType.CITY);
                        locationOutput.setLocationTypeDisplayName("City");
                        locationOutput.setIsoCode("CN-BJ-001");
                        break;
                    case "china-001":
                        locationOutput.setName("中国");
                        locationOutput.setLocationType(LocationType.COUNTRY);
                        locationOutput.setLocationTypeDisplayName("Country");
                        locationOutput.setIsoCode("CN");
                        break;
                    default:
                        // 对于未知的locationId，返回默认值
                        locationOutput.setName("Unknown Location");
                        locationOutput.setLocationType(LocationType.CITY);
                        locationOutput.setLocationTypeDisplayName("City");
                        locationOutput.setIsoCode("UNKNOWN");
                        break;
                }
                
                return locationOutput;
            }
        };
    }

    /**
     * 模拟GetLocationsBatchUseCase，支持批量查询
     */
    @Bean
    @Primary
    public GetLocationsBatchUseCase getLocationsBatchUseCase() {
        return new GetLocationsBatchUseCase(null) {
            @Override
            public Map<String, LocationOutput> execute(Collection<String> locationIds) {
                Map<String, LocationOutput> result = new HashMap<>();
                
                for (String locationId : locationIds) {
                    LocationOutput locationOutput = new LocationOutput();
                    locationOutput.setId(locationId);
                    locationOutput.setActive(true);
                    
                    // 根据locationId返回对应的测试数据
                    switch (locationId) {
                        case "city-gd-sz":
                            locationOutput.setName("深圳市");
                            locationOutput.setLocationType(LocationType.CITY);
                            locationOutput.setLocationTypeDisplayName("City");
                            locationOutput.setIsoCode("CN-GD-SZ");
                            break;
                        case "country-us":
                            locationOutput.setName("美国");
                            locationOutput.setLocationType(LocationType.COUNTRY);
                            locationOutput.setLocationTypeDisplayName("Country");
                            locationOutput.setIsoCode("US");
                            break;
                        case "country-cn":
                            locationOutput.setName("中国");
                            locationOutput.setLocationType(LocationType.COUNTRY);
                            locationOutput.setLocationTypeDisplayName("Country");
                            locationOutput.setIsoCode("CN");
                            break;
                        case "province-gd":
                            locationOutput.setName("广东省");
                            locationOutput.setLocationType(LocationType.PROVINCE);
                            locationOutput.setLocationTypeDisplayName("Province");
                            locationOutput.setIsoCode("CN-GD");
                            break;
                        case "city-bj-bj":
                            locationOutput.setName("北京市");
                            locationOutput.setLocationType(LocationType.CITY);
                            locationOutput.setLocationTypeDisplayName("City");
                            locationOutput.setIsoCode("CN-BJ");
                            break;
                        case "beijing-001":
                            locationOutput.setName("北京");
                            locationOutput.setLocationType(LocationType.CITY);
                            locationOutput.setLocationTypeDisplayName("City");
                            locationOutput.setIsoCode("CN-BJ-001");
                            break;
                        case "china-001":
                            locationOutput.setName("中国");
                            locationOutput.setLocationType(LocationType.COUNTRY);
                            locationOutput.setLocationTypeDisplayName("Country");
                            locationOutput.setIsoCode("CN");
                            break;
                        default:
                            // 对于未知的locationId，返回默认值
                            locationOutput.setName("Unknown Location");
                            locationOutput.setLocationType(LocationType.CITY);
                            locationOutput.setLocationTypeDisplayName("City");
                            locationOutput.setIsoCode("UNKNOWN");
                            break;
                    }
                    
                    result.put(locationId, locationOutput);
                }
                
                return result;
            }
        };
    }
}