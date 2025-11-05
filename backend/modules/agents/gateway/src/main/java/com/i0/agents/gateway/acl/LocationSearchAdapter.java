package com.i0.agents.gateway.acl;

import com.i0.location.application.dto.input.LocationPageInput;
import com.i0.location.application.dto.output.LocationOutput;
import com.i0.location.application.usecases.GetLocationsByTypeUseCase;
import com.i0.location.application.usecases.SearchLocationsUseCase;
import com.i0.location.domain.valueobjects.LocationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Location搜索适配器
 * 处理地区名称到Location ID的自动匹配
 * 支持模糊搜索和类型过滤
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocationSearchAdapter {

    private final SearchLocationsUseCase searchLocationsUseCase;
    private final GetLocationsByTypeUseCase getLocationsByTypeUseCase;

    /**
     * 根据地区名称搜索工作地点Location ID
     * 优先搜索城市，如果没有结果则搜索省份，最后尝试模糊搜索
     *
     * @param locationName 地区名称（如："深圳"、"广东"、"广东省"）
     * @return 匹配的Location ID，如果没有找到返回默认值
     */
    public String searchWorkLocationId(String locationName) {
        if (locationName == null || locationName.trim().isEmpty()) {
            return getDefaultWorkLocationId();
        }

        log.debug("Searching work location for name: {}", locationName);
        String normalizedName = normalizeLocationName(locationName);

        // 1. 精确匹配城市级别的地点（去除市、县等后缀）
        String cityId = searchLocationByExactName(locationName, LocationType.CITY);
        if (cityId != null) {
            log.info("Found work location (city exact): {} -> {}", locationName, cityId);
            return cityId;
        }

        // 2. 精确匹配省份级别的地点（去除省、自治区等后缀）
        String provinceId = searchLocationByExactName(locationName, LocationType.PROVINCE);
        if (provinceId != null) {
            log.info("Found work location (province exact): {} -> {}", locationName, provinceId);
            return provinceId;
        }

        // 3. 模糊搜索城市级别的地点
        String cityFuzzyId = searchLocationFuzzyByType(normalizedName, LocationType.CITY);
        if (cityFuzzyId != null) {
            log.info("Found work location (city fuzzy): {} -> {}", locationName, cityFuzzyId);
            return cityFuzzyId;
        }

        // 4. 模糊搜索省份级别的地点
        String provinceFuzzyId = searchLocationFuzzyByType(normalizedName, LocationType.PROVINCE);
        if (provinceFuzzyId != null) {
            log.info("Found work location (province fuzzy): {} -> {}", locationName, provinceFuzzyId);
            return provinceFuzzyId;
        }

        // 5. 最后尝试通用模糊搜索
        String genericFuzzyId = searchLocationFuzzy(locationName);
        if (genericFuzzyId != null) {
            log.info("Found work location (generic fuzzy): {} -> {}", locationName, genericFuzzyId);
            return genericFuzzyId;
        }

        log.warn("No work location found for: {}, using default", locationName);
        return getDefaultWorkLocationId();
    }

    /**
     * 根据地区名称搜索国籍Location ID
     * 优先搜索国家/地区级别
     *
     * @param nationalityName 国籍名称（如："中国"、"美国"、"China"）
     * @return 匹配的Location ID，如果没有找到返回默认值
     */
    public String searchNationalityId(String nationalityName) {
        if (nationalityName == null || nationalityName.trim().isEmpty()) {
            return getDefaultNationalityId();
        }

        log.debug("Searching nationality for name: {}", nationalityName);

        // 搜索国家/地区级别的地点
        String countryId = searchLocationByExactName(nationalityName, LocationType.COUNTRY);
        if (countryId != null) {
            log.info("Found nationality (country exact): {} -> {}", nationalityName, countryId);
            return countryId;
        }

        // 模糊搜索国家级别的地点
        String normalizedName = normalizeLocationName(nationalityName);
        String countryFuzzyId = searchLocationFuzzyByType(normalizedName, LocationType.COUNTRY);
        if (countryFuzzyId != null) {
            log.info("Found nationality (country fuzzy): {} -> {}", nationalityName, countryFuzzyId);
            return countryFuzzyId;
        }

        // 最后尝试通用模糊搜索
        String genericFuzzyId = searchLocationFuzzy(nationalityName);
        if (genericFuzzyId != null) {
            log.info("Found nationality (generic fuzzy): {} -> {}", nationalityName, genericFuzzyId);
            return genericFuzzyId;
        }

        log.warn("No nationality found for: {}, using default", nationalityName);
        return getDefaultNationalityId();
    }

    /**
     * 根据精确名称和类型搜索Location
     */
    private String searchLocationByExactName(String name, LocationType type) {
        try {
            // 首先尝试精确匹配原始名称
            String exactMatch = searchExactMatch(name, type);
            if (exactMatch != null) {
                return exactMatch;
            }

            // 然后尝试标准化后的名称匹配
            String normalizedName = normalizeLocationName(name);
            return searchExactMatch(normalizedName, type);
        } catch (Exception e) {
            log.warn("Failed to search location by exact name: {} and type: {}", name, type, e);
            return null;
        }
    }

    /**
     * 精确匹配搜索
     */
    private String searchExactMatch(String name, LocationType type) {
        LocationPageInput input = LocationPageInput.builder()
                .name(name)
                .locationType(type)
                .activeOnly(true)
                .page(0)
                .size(5)
                .build();

        return searchLocationsUseCase.execute(input)
                .getContent()
                .stream()
                .filter(location -> location.getName().equalsIgnoreCase(name) ||
                                normalizeLocationName(location.getName()).equalsIgnoreCase(name))
                .findFirst()
                .map(LocationOutput::getId)
                .orElse(null);
    }

    /**
     * 按类型模糊搜索
     */
    private String searchLocationFuzzyByType(String normalizedName, LocationType type) {
        try {
            List<LocationOutput> allLocations = getLocationsByTypeUseCase.execute(type);

            return allLocations.stream()
                    .filter(location -> isFuzzyMatch(location.getName(), normalizedName))
                    .findFirst()
                    .map(LocationOutput::getId)
                    .orElse(null);
        } catch (Exception e) {
            log.warn("Failed to fuzzy search location by type: {} and name: {}", type, normalizedName, e);
            return null;
        }
    }

    /**
     * 模糊搜索Location
     */
    private String searchLocationFuzzy(String name) {
        try {
            LocationPageInput input = LocationPageInput.builder()
                    .name(name)
                    .activeOnly(true)
                    .page(0)
                    .size(5)
                    .build();

            Optional<LocationOutput> match = searchLocationsUseCase.execute(input)
                    .getContent()
                    .stream()
                    .filter(location -> location.getName().contains(name) || name.contains(location.getName()))
                    .findFirst();

            return match.map(LocationOutput::getId).orElse(null);
        } catch (Exception e) {
            log.warn("Failed to fuzzy search location: {}", name, e);
            return null;
        }
    }

    /**
     * 判断是否是精确匹配或合理的模糊匹配
     */
    private boolean isExactOrFuzzyMatch(String locationName, String searchName) {
        if (locationName.equalsIgnoreCase(searchName)) {
            return true;
        }

        // 处理常见的别名情况
        String normalizedLocation = normalizeLocationName(locationName);
        String normalizedSearch = normalizeLocationName(searchName);

        return isFuzzyMatch(locationName, searchName);
    }

    /**
     * 模糊匹配判断
     */
    private boolean isFuzzyMatch(String locationName, String searchName) {
        String normalizedLocation = normalizeLocationName(locationName);
        String normalizedSearch = normalizeLocationName(searchName);

        // 精确匹配
        if (normalizedLocation.equals(normalizedSearch)) {
            return true;
        }

        // 包含匹配
        if (normalizedLocation.contains(normalizedSearch) || normalizedSearch.contains(normalizedLocation)) {
            return true;
        }

        // 反向包含匹配：处理搜索词是标准化后的，但原地名可能带后缀的情况
        if (locationName.contains(searchName) || searchName.contains(locationName)) {
            return true;
        }

        // 特殊情况处理
        return handleSpecialCases(locationName, searchName);
    }

    /**
     * 处理特殊匹配情况
     */
    private boolean handleSpecialCases(String locationName, String searchName) {
        // 英文别名匹配 - 支持带后缀和不带后缀的情况
        if (searchName.equalsIgnoreCase("shenzhen") && (locationName.contains("深圳") || locationName.contains("深圳市"))) {
            return true;
        }
        if (searchName.equalsIgnoreCase("beijing") && (locationName.contains("北京") || locationName.contains("北京市"))) {
            return true;
        }
        if (searchName.equalsIgnoreCase("shanghai") && (locationName.contains("上海") || locationName.contains("上海市"))) {
            return true;
        }
        if (searchName.equalsIgnoreCase("guangzhou") && (locationName.contains("广州") || locationName.contains("广州市"))) {
            return true;
        }
        if (searchName.equalsIgnoreCase("china") && locationName.contains("中国")) {
            return true;
        }
        if (searchName.equalsIgnoreCase("usa") && locationName.contains("美国")) {
            return true;
        }
        if (searchName.equalsIgnoreCase("japan") && locationName.contains("日本")) {
            return true;
        }
        if (searchName.equalsIgnoreCase("korea") && locationName.contains("韩国")) {
            return true;
        }

        // 简称匹配
        if (searchName.equals("京") && locationName.contains("北京")) {
            return true;
        }
        if (searchName.equals("沪") && locationName.contains("上海")) {
            return true;
        }
        if (searchName.equals("粤") && locationName.contains("广东")) {
            return true;
        }

        return false;
    }

    /**
     * 标准化地区名称
     */
    private String normalizeLocationName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }

        return name.toLowerCase()
                // 去除常见的行政区划后缀
                .replace("省", "")
                .replace("自治区", "")
                .replace("特别行政区", "")
                .replace("直辖市", "")
                .replace("地区", "")
                .replace("自治州", "")
                .replace("自治县", "")
                .replace("市", "")
                .replace("县", "")
                .replace("区", "")
                .replace("镇", "")
                .replace("乡", "")
                // 去除常见的国家/地区标识
                .replace("共和国", "")
                .replace("王国", "")
                .replace("联邦", "")
                .replace("合众国", "")
                .replace("社会主义共和国", "")
                .replace("人民共和国", "")
                // 去除括号内容
                .replaceAll("\\s*\\([^)]*\\)", "")
                // 去除多余空格
                .trim();
    }

    /**
     * 获取默认工作地点ID
     */
    private String getDefaultWorkLocationId() {
        try {
            List<LocationOutput> cities = getLocationsByTypeUseCase.execute(LocationType.CITY);
            // 优先返回深圳，如果没有则返回第一个城市
            return cities.stream()
                    .filter(city -> city.getName().contains("深圳"))
                    .findFirst()
                    .map(LocationOutput::getId)
                    .orElse(cities.isEmpty() ? "city-gd-sz" : cities.get(0).getId());
        } catch (Exception e) {
            log.warn("Failed to get default work location, using fallback", e);
            return "city-gd-sz"; // 深圳作为默认
        }
    }

    /**
     * 获取默认国籍ID
     */
    private String getDefaultNationalityId() {
        try {
            List<LocationOutput> countries = getLocationsByTypeUseCase.execute(LocationType.COUNTRY);
            // 优先返回中国，如果没有则返回第一个国家
            return countries.stream()
                    .filter(country -> country.getName().contains("中国") ||
                                        country.getIsoCode().equalsIgnoreCase("CN"))
                    .findFirst()
                    .map(LocationOutput::getId)
                    .orElse(countries.isEmpty() ? "country-cn" : countries.get(0).getId());
        } catch (Exception e) {
            log.warn("Failed to get default nationality, using fallback", e);
            return "country-cn"; // 中国作为默认
        }
    }

    /**
     * 获取支持的地区类型说明
     */
    public String getSupportedLocationTypesInfo() {
        return "支持以下地区类型：\n" +
                "- 工作地点：可输入城市名称（如：深圳、北京、上海）或省份名称（如：广东、江苏）\n" +
                "- 国籍：可输入国家/地区名称（如：中国、美国、日本、香港）\n" +
                "- 系统会自动匹配最合适的Location ID";
    }
}