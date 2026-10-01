package de.caritas.cob.consultingtypeservice.api.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TenantFilterParameterResolverTest {

  private final TenantFilterParameterResolver resolver = new TenantFilterParameterResolver();

  @AfterEach
  void clearTenant() {
    TenantContext.clear();
  }

  @Test
  void get_Should_ReturnTheCurrentTenant() {
    TenantContext.setCurrentTenant(5L);

    assertThat(resolver.get()).isEqualTo(5L);
  }

  @Test
  void get_Should_LiftTheRestriction_When_TenantIsTheTechnicalTenantZero() {
    TenantContext.setCurrentTenant(0L);

    assertThat(resolver.get()).isZero();
  }

  @Test
  void get_Should_MatchNoTenant_When_NoTenantIsSet() {
    assertThat(resolver.get()).isEqualTo(-1L);
  }

  @Test
  void get_Should_LiftTheRestriction_When_MultitenancyIsOff() {
    setField(resolver, "multitenancyEnabled", false);

    assertThat(resolver.get()).isZero();
    TenantContext.setCurrentTenant(5L);
    assertThat(resolver.get()).isZero();
  }
}
