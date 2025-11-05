package com.i0.app.integration;

import com.i0.agents.gateway.acl.LocationSearchAdapter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LocationSearchAdapter集成测试
 * 验证地区名称到Location ID的自动匹配功能
 * 遵循标准集成测试模式：Controller → UseCase → Repository → Database
 */
@Slf4j
@Transactional
@ActiveProfiles("test")
class LocationSearchAdapterIntegrationTest extends BasicIntegrationTest {

    @Autowired
    private LocationSearchAdapter locationSearchAdapter;

    @BeforeEach
    void setUp() {
        log.info("LocationSearchAdapter集成测试初始化完成");
    }

    @AfterEach
    void tearDown() {
        // 清理测试数据（如果需要）
        log.info("LocationSearchAdapter集成测试清理完成");
    }

    @Test
    @DisplayName("工作地点精确匹配测试")
    void should_MatchWorkLocationExactly_When_CityNameProvided() {
        // Given - 准备测试数据，测试常用城市名称
        String[] cityInputs = {"深圳", "北京", "上海", "广州", "杭州"};

        // When & Then - 执行测试并验证结果
        for (String inputName : cityInputs) {
            log.info("Testing city match: Input='{}'", inputName);

            // When - 执行工作地点搜索
            String actualId = locationSearchAdapter.searchWorkLocationId(inputName);

            // Then - 验证返回有效的ID字符串且不为null
            assertThat(actualId)
                .as("工作地点搜索应返回有效ID，输入: %s", inputName)
                .isNotNull();
            assertThat(actualId)
                .as("工作地点ID应为非空字符串，输入: %s, 输出: %s", inputName, actualId)
                .isNotEmpty();
            assertThat(actualId)
                .as("工作地点ID应包含连字符，输入: %s, 输出: %s", inputName, actualId)
                .contains("-");

            log.info("工作地点精确匹配: 输入='{}', 输出='{}'", inputName, actualId);
        }

        log.info("工作地点精确匹配测试通过，共测试{}个用例", cityInputs.length);
    }

    @Test
    @DisplayName("工作地点省份匹配测试")
    void should_MatchWorkLocation_When_ProvinceNameProvided() {
        // Given - 准备测试数据，测试常用省份名称
        String[] provinceInputs = {"广东省", "广东", "浙江省", "浙江", "江苏省", "江苏"};

        // When & Then - 执行测试并验证结果
        for (String inputName : provinceInputs) {
            log.info("Testing province match: Input='{}'", inputName);

            // When - 执行工作地点搜索
            String actualId = locationSearchAdapter.searchWorkLocationId(inputName);

            // Then - 验证返回有效的ID字符串且不为null
            assertThat(actualId)
                .as("省份搜索应返回有效ID，输入: %s", inputName)
                .isNotNull();
            assertThat(actualId)
                .as("省份ID应为非空字符串，输入: %s, 输出: %s", inputName, actualId)
                .isNotEmpty();
            assertThat(actualId)
                .as("省份ID应包含连字符，输入: %s, 输出: %s", inputName, actualId)
                .contains("-");

            log.info("工作地点省份匹配: 输入='{}', 输出='{}'", inputName, actualId);
        }

        log.info("工作地点省份匹配测试通过，共测试{}个用例", provinceInputs.length);
    }

    @Test
    @DisplayName("国籍精确匹配测试")
    void should_MatchNationalityExactly_When_CountryNameProvided() {
        // Given - 准备测试数据，测试常用国家名称
        String[] countryInputs = {"中国", "中华人民共和国", "美国", "美利坚合众国", "日本", "韩国"};

        // When & Then - 执行测试并验证结果
        for (String inputName : countryInputs) {
            log.info("Testing country match: Input='{}'", inputName);

            // When - 执行国籍搜索
            String actualId = locationSearchAdapter.searchNationalityId(inputName);

            // Then - 验证返回的ID格式正确且不为null
            assertThat(actualId)
                .as("国籍搜索应返回有效ID，输入: %s", inputName)
                .isNotNull();
            assertThat(actualId)
                .as("国籍ID应为非空字符串，输入: %s, 输出: %s", inputName, actualId)
                .isNotEmpty();
            assertThat(actualId)
                .as("国籍ID应包含连字符，输入: %s, 输出: %s", inputName, actualId)
                .contains("-");

            log.info("国籍精确匹配: 输入='{}', 输出='{}'", inputName, actualId);
        }

        log.info("国籍精确匹配测试通过，共测试{}个用例", countryInputs.length);
    }

    @Test
    @DisplayName("工作地点模糊匹配测试")
    void should_MatchWorkLocationFuzzy_When_PartialNameProvided() {
        // Given - 准备测试数据，测试英文和模糊匹配
        String[] fuzzyInputs = {"shenzhen", "beijing", "shanghai", "guangdong", "zhejiang"};

        // When & Then - 执行测试并验证结果
        for (String inputName : fuzzyInputs) {
            log.info("Testing fuzzy work location match: Input='{}'", inputName);

            // When - 执行工作地点模糊搜索
            String actualId = locationSearchAdapter.searchWorkLocationId(inputName);

            // Then - 验证匹配结果
            assertThat(actualId)
                .as("模糊匹配应返回有效ID，输入: %s", inputName)
                .isNotNull();
            assertThat(actualId)
                .as("模糊匹配ID应为非空字符串，输入: %s, 输出: %s", inputName, actualId)
                .isNotEmpty();
            assertThat(actualId)
                .as("模糊匹配ID应包含连字符，输入: %s, 输出: %s", inputName, actualId)
                .contains("-");

            log.info("工作地点模糊匹配: 输入='{}', 输出='{}'", inputName, actualId);
        }

        log.info("工作地点模糊匹配测试通过，共测试{}个用例", fuzzyInputs.length);
    }

    @Test
    @DisplayName("国籍模糊匹配测试")
    void should_MatchNationalityFuzzy_When_PartialNameProvided() {
        // Given - 准备国籍模糊匹配测试数据
        String[] nationalityFuzzyInputs = {"china", "usa", "japan", "korea"};

        // When & Then - 执行测试并验证结果
        for (String inputName : nationalityFuzzyInputs) {
            log.info("Testing fuzzy nationality match: Input='{}'", inputName);

            // When - 执行国籍模糊搜索
            String actualId = locationSearchAdapter.searchNationalityId(inputName);

            // Then - 验证匹配结果
            assertThat(actualId)
                .as("国籍模糊匹配应返回有效ID，输入: %s", inputName)
                .isNotNull();
            assertThat(actualId)
                .as("国籍模糊匹配ID应为非空字符串，输入: %s, 输出: %s", inputName, actualId)
                .isNotEmpty();
            assertThat(actualId)
                .as("国籍模糊匹配ID应包含连字符，输入: %s, 输出: %s", inputName, actualId)
                .contains("-");

            log.info("国籍模糊匹配: 输入='{}', 输出='{}'", inputName, actualId);
        }

        log.info("国籍模糊匹配测试通过，共测试{}个用例", nationalityFuzzyInputs.length);
    }

    @Test
    @DisplayName("默认值处理测试")
    void should_ReturnDefaultValues_When_NoMatchFound() {
        // Given - 准备不存在的地区和国家名称
        String nonExistentCity = "不存在的城市";
        String nonExistentCountry = "不存在的国家";

        // When - 执行搜索
        String actualWorkLocationId = locationSearchAdapter.searchWorkLocationId(nonExistentCity);
        String actualNationalityId = locationSearchAdapter.searchNationalityId(nonExistentCountry);

        // Then - 验证返回合理的默认值
        assertThat(actualWorkLocationId)
            .as("不存在的城市应返回非null默认值")
            .isNotNull();
        assertThat(actualNationalityId)
            .as("不存在的国家应返回非null默认值")
            .isNotNull();

        // 验证默认值格式正确
        assertThat(actualWorkLocationId)
            .as("工作地点默认值应以city-开头")
            .startsWith("city-");
        assertThat(actualNationalityId)
            .as("国籍默认值应以country-开头")
            .startsWith("country-");

        log.info("不存在地区测试通过 - 工作地点: {}, 国籍: {}", actualWorkLocationId, actualNationalityId);
    }

    @Test
    @DisplayName("空值处理测试")
    void should_ReturnDefaultValues_When_NullOrEmptyProvided() {
        // Given - 准备各种空值输入
        String[] nullInputs = {null, "", "   "};

        // When & Then - 测试工作地点空值处理
        String[] workLocationResults = new String[nullInputs.length];
        for (int i = 0; i < nullInputs.length; i++) {
            workLocationResults[i] = locationSearchAdapter.searchWorkLocationId(nullInputs[i]);
            log.info("工作地点空值测试[{}]: 输入='{}', 输出='{}'", i, nullInputs[i], workLocationResults[i]);
        }

        // When & Then - 测试国籍空值处理
        String[] nationalityResults = new String[nullInputs.length];
        for (int i = 0; i < nullInputs.length; i++) {
            nationalityResults[i] = locationSearchAdapter.searchNationalityId(nullInputs[i]);
            log.info("国籍空值测试[{}]: 输入='{}', 输出='{}'", i, nullInputs[i], nationalityResults[i]);
        }

        // Then - 验证所有空值输入返回相同的默认值
        for (int i = 1; i < workLocationResults.length; i++) {
            assertThat(workLocationResults[i])
                .as("所有工作地点空值输入应返回相同默认值")
                .isEqualTo(workLocationResults[0]);
            assertThat(nationalityResults[i])
                .as("所有国籍空值输入应返回相同默认值")
                .isEqualTo(nationalityResults[0]);
        }

        // 验证默认值不为null
        assertThat(workLocationResults[0])
            .as("工作地点默认值不应为null")
            .isNotNull();
        assertThat(nationalityResults[0])
            .as("国籍默认值不应为null")
            .isNotNull();

        log.info("空值处理测试通过 - 工作地点默认值: {}, 国籍默认值: {}",
                workLocationResults[0], nationalityResults[0]);
    }

    @Test
    @DisplayName("完整搜索流程测试")
    void should_DemonstrateCompleteSearchProcess_When_RealDataProvided() {
        // Given - 准备具有代表性的测试数据
        String[] workLocationInputs = {"深圳", "北京", "广东"};
        String[] nationalityInputs = {"中国", "美国", "日本"};

        log.info("=== LocationSearchAdapter 完整搜索流程测试开始 ===");

        // When & Then - 测试工作地点搜索流程
        log.info("1. 工作地点搜索测试");
        for (String input : workLocationInputs) {
            // When - 执行工作地点搜索
            String locationId = locationSearchAdapter.searchWorkLocationId(input);

            // Then - 验证结果有效性
            assertThat(locationId)
                .as("工作地点搜索结果不应为null，输入: %s", input)
                .isNotNull();
            assertThat(locationId)
                .as("工作地点ID应为非空字符串，输入: %s, 输出: %s", input, locationId)
                .isNotEmpty();

            log.info("   工作地点搜索: 输入='{}', 输出='{}'", input, locationId);
        }

        // When & Then - 测试国籍搜索流程
        log.info("2. 国籍搜索测试");
        for (String input : nationalityInputs) {
            // When - 执行国籍搜索
            String nationalityId = locationSearchAdapter.searchNationalityId(input);

            // Then - 验证结果有效性
            assertThat(nationalityId)
                .as("国籍搜索结果不应为null，输入: %s", input)
                .isNotNull();
            assertThat(nationalityId)
                .as("国籍ID应为非空字符串，输入: %s, 输出: %s", input, nationalityId)
                .isNotEmpty();

            log.info("   国籍搜索: 输入='{}', 输出='{}'", input, nationalityId);
        }

        log.info("=== LocationSearchAdapter 完整搜索流程测试通过 ===");
    }
}