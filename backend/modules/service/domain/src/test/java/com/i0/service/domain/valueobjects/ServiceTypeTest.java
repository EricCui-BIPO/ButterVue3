package com.i0.service.domain.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceTypeTest {

    @Test
    void shouldHaveFourServiceTypes() {
        ServiceType[] serviceTypes = ServiceType.values();
        assertThat(serviceTypes).hasSize(4);

        // 验证所有预期的服务类型都存在
        assertThat(serviceTypes).containsExactly(
            ServiceType.EOR,
            ServiceType.GPO,
            ServiceType.CONTRACTOR,
            ServiceType.SELF
        );
    }

    @ParameterizedTest
    @EnumSource(ServiceType.class)
    void shouldHaveValidCodes(ServiceType serviceType) {
        assertThat(serviceType.getCode()).isNotNull();
        assertThat(serviceType.getCode()).isNotBlank();
        assertThat(serviceType.getCode()).isUpperCase();
    }

    @ParameterizedTest
    @EnumSource(ServiceType.class)
    void shouldHaveValidDisplayNames(ServiceType serviceType) {
        assertThat(serviceType.getDisplayName()).isNotNull();
        assertThat(serviceType.getDisplayName()).isNotBlank();
    }

    @ParameterizedTest
    @EnumSource(ServiceType.class)
    void shouldHaveValidDescriptions(ServiceType serviceType) {
        assertThat(serviceType.getDescription()).isNotNull();
        assertThat(serviceType.getDescription()).isNotBlank();
    }

    @Test
    void shouldFindServiceTypeByCode() {
        assertThat(ServiceType.fromCode("EOR")).isEqualTo(ServiceType.EOR);
        assertThat(ServiceType.fromCode("GPO")).isEqualTo(ServiceType.GPO);
        assertThat(ServiceType.fromCode("CONTRACTOR")).isEqualTo(ServiceType.CONTRACTOR);
        assertThat(ServiceType.fromCode("SELF")).isEqualTo(ServiceType.SELF);
    }

    @Test
    void shouldThrowExceptionForInvalidCode() {
        assertThatThrownBy(() -> ServiceType.fromCode("INVALID"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown service type code: INVALID");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void shouldThrowExceptionForNullOrEmptyCode(String code) {
        assertThatThrownBy(() -> ServiceType.fromCode(code))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Service type code cannot be null or empty");
    }

    @Test
    void shouldFindServiceTypeByDisplayName() {
        assertThat(ServiceType.fromDisplayName("Employer of Record Service")).isEqualTo(ServiceType.EOR);
        assertThat(ServiceType.fromDisplayName("Global Payroll Outsourcing")).isEqualTo(ServiceType.GPO);
        assertThat(ServiceType.fromDisplayName("Independent Contractor Management")).isEqualTo(ServiceType.CONTRACTOR);
        assertThat(ServiceType.fromDisplayName("Self-Employment Management")).isEqualTo(ServiceType.SELF);
    }

    @Test
    void shouldThrowExceptionForInvalidDisplayName() {
        assertThatThrownBy(() -> ServiceType.fromDisplayName("Invalid Service"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Unknown service type display name: Invalid Service");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void shouldThrowExceptionForNullOrEmptyDisplayName(String displayName) {
        assertThatThrownBy(() -> ServiceType.fromDisplayName(displayName))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Service type display name cannot be null or empty");
    }

    @Test
    void shouldIdentifyEORService() {
        assertThat(ServiceType.EOR.isEOR()).isTrue();
        assertThat(ServiceType.GPO.isEOR()).isFalse();
        assertThat(ServiceType.CONTRACTOR.isEOR()).isFalse();
        assertThat(ServiceType.SELF.isEOR()).isFalse();
    }

    @Test
    void shouldIdentifyGPOService() {
        assertThat(ServiceType.GPO.isGPO()).isTrue();
        assertThat(ServiceType.EOR.isGPO()).isFalse();
        assertThat(ServiceType.CONTRACTOR.isGPO()).isFalse();
        assertThat(ServiceType.SELF.isGPO()).isFalse();
    }

    @Test
    void shouldIdentifyContractorService() {
        assertThat(ServiceType.CONTRACTOR.isContractor()).isTrue();
        assertThat(ServiceType.EOR.isContractor()).isFalse();
        assertThat(ServiceType.GPO.isContractor()).isFalse();
        assertThat(ServiceType.SELF.isContractor()).isFalse();
    }

    @Test
    void shouldIdentifySELFService() {
        assertThat(ServiceType.SELF.isSELF()).isTrue();
        assertThat(ServiceType.EOR.isSELF()).isFalse();
        assertThat(ServiceType.GPO.isSELF()).isFalse();
        assertThat(ServiceType.CONTRACTOR.isSELF()).isFalse();
    }

    @Test
    void shouldIdentifyOutsourcingServices() {
        assertThat(ServiceType.EOR.isOutsourcingService()).isTrue();
        assertThat(ServiceType.GPO.isOutsourcingService()).isTrue();
        assertThat(ServiceType.CONTRACTOR.isOutsourcingService()).isFalse();
        assertThat(ServiceType.SELF.isOutsourcingService()).isFalse();
    }

    @Test
    void shouldIdentifyManagementServices() {
        assertThat(ServiceType.CONTRACTOR.isManagementService()).isTrue();
        assertThat(ServiceType.SELF.isManagementService()).isTrue();
        assertThat(ServiceType.EOR.isManagementService()).isFalse();
        assertThat(ServiceType.GPO.isManagementService()).isFalse();
    }

    @Test
    void shouldGetAllActiveTypes() {
        ServiceType[] allTypes = ServiceType.getAllActiveTypes();
        assertThat(allTypes).hasSize(4);
        assertThat(allTypes).containsExactly(
            ServiceType.EOR,
            ServiceType.GPO,
            ServiceType.CONTRACTOR,
            ServiceType.SELF
        );
    }

    @ParameterizedTest
    @MethodSource("validCodesProvider")
    void shouldValidateValidCodes(String code) {
        assertThat(ServiceType.isValidCode(code)).isTrue();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "invalid"})
    void shouldValidateInvalidCodes(String code) {
        assertThat(ServiceType.isValidCode(code)).isFalse();
    }

    private static Stream<String> validCodesProvider() {
        return Stream.of("EOR", "GPO", "CONTRACTOR", "SELF");
    }

    @Test
    void shouldHaveMeaningfulToString() {
        assertThat(ServiceType.EOR.toString()).isEqualTo("EOR - Employer of Record Service");
        assertThat(ServiceType.GPO.toString()).isEqualTo("GPO - Global Payroll Outsourcing");
        assertThat(ServiceType.CONTRACTOR.toString()).isEqualTo("CONTRACTOR - Independent Contractor Management");
        assertThat(ServiceType.SELF.toString()).isEqualTo("SELF - Self-Employment Management");
    }
}