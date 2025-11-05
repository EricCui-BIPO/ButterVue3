package com.i0.service.domain.entities;

import com.i0.service.domain.valueobjects.ServiceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceTypeEntityTest {

    @Test
    void shouldCreateServiceTypeEntityWithValidData() {
        // Given
        String name = "Test Service";
        ServiceType serviceType = ServiceType.EOR;
        String description = "Test description";

        // When
        ServiceTypeEntity entity = ServiceTypeEntity.create(name, serviceType, description);

        // Then
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getName()).isEqualTo(name);
        assertThat(entity.getServiceType()).isEqualTo(serviceType);
        assertThat(entity.getDescription()).isEqualTo(description);
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getVersion()).isEqualTo(0L);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void shouldThrowExceptionWhenNameIsNullOrEmpty(String name) {
        // Given
        ServiceType serviceType = ServiceType.EOR;
        String description = "Test description";

        // When & Then
        assertThatThrownBy(() -> ServiceTypeEntity.create(name, serviceType, description))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Service type name cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenNameIsTooLong() {
        // Given
        String name = "a".repeat(101); // 101 characters
        ServiceType serviceType = ServiceType.EOR;
        String description = "Test description";

        // When & Then
        assertThatThrownBy(() -> ServiceTypeEntity.create(name, serviceType, description))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Service type name cannot exceed 100 characters");
    }

    @Test
    void shouldThrowExceptionWhenServiceTypeIsNull() {
        // Given
        String name = "Test Service";
        String description = "Test description";

        // When & Then
        assertThatThrownBy(() -> ServiceTypeEntity.create(name, null, description))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Service type cannot be null");
    }

    @Test
    void shouldUpdateServiceTypeEntity() {
        // Given
        ServiceTypeEntity entity = ServiceTypeEntity.create("Original Name", ServiceType.EOR, "Original Description");
        LocalDateTime originalUpdatedAt = entity.getUpdatedAt();

        // When
        entity.update("Updated Name", "Updated Description");

        // Then
        assertThat(entity.getName()).isEqualTo("Updated Name");
        assertThat(entity.getDescription()).isEqualTo("Updated Description");
        assertThat(entity.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void shouldNotUpdateWhenDataIsUnchanged() {
        // Given
        ServiceTypeEntity entity = ServiceTypeEntity.create("Test Name", ServiceType.EOR, "Test Description");
        LocalDateTime originalUpdatedAt = entity.getUpdatedAt();

        // When
        entity.update("Test Name", "Test Description");

        // Then
        assertThat(entity.getName()).isEqualTo("Test Name");
        assertThat(entity.getDescription()).isEqualTo("Test Description");
        assertThat(entity.getUpdatedAt()).isEqualTo(originalUpdatedAt);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    void shouldThrowExceptionWhenUpdatingWithInvalidName(String name) {
        // Given
        ServiceTypeEntity entity = ServiceTypeEntity.create("Original Name", ServiceType.EOR, "Original Description");

        // When & Then
        assertThatThrownBy(() -> entity.update(name, "Updated Description"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Service type name cannot be null or empty");
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWithTooLongName() {
        // Given
        ServiceTypeEntity entity = ServiceTypeEntity.create("Original Name", ServiceType.EOR, "Original Description");
        String longName = "a".repeat(101); // 101 characters

        // When & Then
        assertThatThrownBy(() -> entity.update(longName, "Updated Description"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Service type name cannot exceed 100 characters");
    }

    @Test
    void shouldActivateInactiveServiceType() {
        // Given
        ServiceTypeEntity entity = ServiceTypeEntity.create("Test Service", ServiceType.EOR, "Test Description");
        entity.setActive(false);
        LocalDateTime originalUpdatedAt = entity.getUpdatedAt();

        // When
        entity.activate();

        // Then
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void shouldNotUpdateWhenActivatingActiveServiceType() {
        // Given
        ServiceTypeEntity entity = ServiceTypeEntity.create("Test Service", ServiceType.EOR, "Test Description");
        LocalDateTime originalUpdatedAt = entity.getUpdatedAt();

        // When
        entity.activate();

        // Then
        assertThat(entity.isActive()).isTrue();
        assertThat(entity.getUpdatedAt()).isEqualTo(originalUpdatedAt);
    }

    @Test
    void shouldDeactivateActiveServiceType() {
        // Given
        ServiceTypeEntity entity = ServiceTypeEntity.create("Test Service", ServiceType.EOR, "Test Description");
        LocalDateTime originalUpdatedAt = entity.getUpdatedAt();

        // When
        entity.deactivate();

        // Then
        assertThat(entity.isActive()).isFalse();
        assertThat(entity.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void shouldNotUpdateWhenDeactivatingInactiveServiceType() {
        // Given
        ServiceTypeEntity entity = ServiceTypeEntity.create("Test Service", ServiceType.EOR, "Test Description");
        entity.setActive(false);
        LocalDateTime originalUpdatedAt = entity.getUpdatedAt();

        // When
        entity.deactivate();

        // Then
        assertThat(entity.isActive()).isFalse();
        assertThat(entity.getUpdatedAt()).isEqualTo(originalUpdatedAt);
    }

    @Test
    void shouldReturnCorrectCode() {
        // Given
        ServiceTypeEntity entity = ServiceTypeEntity.create("Test Service", ServiceType.EOR, "Test Description");

        // When & Then
        assertThat(entity.getCode()).isEqualTo("EOR");
    }

    @Test
    void shouldReturnNullCodeWhenServiceTypeIsNull() {
        // Given
        ServiceTypeEntity entity = new ServiceTypeEntity();
        entity.setServiceType(null);

        // When & Then
        assertThat(entity.getCode()).isNull();
    }

    @Test
    void shouldReturnCorrectDisplayName() {
        // Given
        ServiceTypeEntity entity = ServiceTypeEntity.create("Test Service", ServiceType.EOR, "Test Description");

        // When & Then
        assertThat(entity.getServiceTypeDisplayName()).isEqualTo("Employer of Record Service");
    }

    @Test
    void shouldReturnNullDisplayNameWhenServiceTypeIsNull() {
        // Given
        ServiceTypeEntity entity = new ServiceTypeEntity();
        entity.setServiceType(null);

        // When & Then
        assertThat(entity.getServiceTypeDisplayName()).isNull();
    }

    @ParameterizedTest
    @CsvSource({
        "EOR, true, false, false, false",
        "GPO, false, true, false, false",
        "CONTRACTOR, false, false, true, false",
        "SELF, false, false, false, true"
    })
    void shouldIdentifyServiceTypeCorrectly(ServiceType serviceType, boolean isEOR, boolean isGPO, boolean isContractor, boolean isSELF) {
        // Given
        ServiceTypeEntity entity = ServiceTypeEntity.create("Test Service", serviceType, "Test Description");

        // When & Then
        assertThat(entity.isEOR()).isEqualTo(isEOR);
        assertThat(entity.isGPO()).isEqualTo(isGPO);
        assertThat(entity.isContractor()).isEqualTo(isContractor);
        assertThat(entity.isSELF()).isEqualTo(isSELF);
    }

    @ParameterizedTest
    @CsvSource({
        "EOR, true, false",
        "GPO, true, false",
        "CONTRACTOR, false, true",
        "SELF, false, true"
    })
    void shouldIdentifyServiceCategoriesCorrectly(ServiceType serviceType, boolean isOutsourcing, boolean isManagement) {
        // Given
        ServiceTypeEntity entity = ServiceTypeEntity.create("Test Service", serviceType, "Test Description");

        // When & Then
        assertThat(entity.isOutsourcingService()).isEqualTo(isOutsourcing);
        assertThat(entity.isManagementService()).isEqualTo(isManagement);
    }

    @Test
    void shouldImplementEqualsAndHashCodeBasedOnId() {
        // Given
        String id = UUID.randomUUID().toString();
        ServiceTypeEntity entity1 = new ServiceTypeEntity();
        entity1.setId(id);
        entity1.setName("Test Service");
        entity1.setServiceType(ServiceType.EOR);

        ServiceTypeEntity entity2 = new ServiceTypeEntity();
        entity2.setId(id);
        entity2.setName("Different Service");
        entity2.setServiceType(ServiceType.GPO);

        ServiceTypeEntity entity3 = new ServiceTypeEntity();
        entity3.setId(UUID.randomUUID().toString());
        entity3.setName("Test Service");
        entity3.setServiceType(ServiceType.EOR);

        // When & Then
        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity1).isNotEqualTo(entity3);
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
        assertThat(entity1.hashCode()).isNotEqualTo(entity3.hashCode());
    }

    @Test
    void shouldHaveMeaningfulToString() {
        // Given
        ServiceTypeEntity entity = ServiceTypeEntity.create("Test Service", ServiceType.EOR, "Test Description");

        // When & Then
        String toString = entity.toString();
        assertThat(toString).contains("id='" + entity.getId() + "'");
        assertThat(toString).contains("name='Test Service'");
        assertThat(toString).contains("serviceType=EOR");
        assertThat(toString).contains("active=true");
    }

    @Test
    void shouldHandleNullServiceTypeInBusinessMethods() {
        // Given
        ServiceTypeEntity entity = new ServiceTypeEntity();
        entity.setServiceType(null);

        // When & Then
        assertThat(entity.isEOR()).isFalse();
        assertThat(entity.isGPO()).isFalse();
        assertThat(entity.isContractor()).isFalse();
        assertThat(entity.isSELF()).isFalse();
        assertThat(entity.isOutsourcingService()).isFalse();
        assertThat(entity.isManagementService()).isFalse();
    }
}