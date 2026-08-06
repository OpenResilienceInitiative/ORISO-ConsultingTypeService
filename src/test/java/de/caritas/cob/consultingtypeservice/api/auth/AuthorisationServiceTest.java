package de.caritas.cob.consultingtypeservice.api.auth;

import static org.assertj.core.api.Assertions.assertThat;

import de.caritas.cob.consultingtypeservice.api.controller.AuthenticationMockBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

class AuthorisationServiceTest {

  private final AuthorisationService authorisationService = new AuthorisationService();

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void isSuperAdmin_Should_ReturnTrue_When_TenantIdIsZero() {
    var authentication =
        new AuthenticationMockBuilder().withUserRole("tenant-admin").withTenantId("0").build();
    SecurityContextHolder.getContext().setAuthentication(authentication);

    assertThat(authorisationService.isSuperAdmin()).isTrue();
    assertThat(authorisationService.findTenantIdInAccessToken()).contains(0L);
  }

  @Test
  void isSuperAdmin_Should_ReturnFalse_When_TenantIdIsNotZero() {
    var authentication =
        new AuthenticationMockBuilder().withUserRole("tenant-admin").withTenantId("1").build();
    SecurityContextHolder.getContext().setAuthentication(authentication);

    assertThat(authorisationService.isSuperAdmin()).isFalse();
    assertThat(authorisationService.findTenantIdInAccessToken()).contains(1L);
  }

  @Test
  void isSuperAdmin_Should_ReturnFalse_When_TenantIdIsMissing() {
    var authentication = new AuthenticationMockBuilder().withUserRole("tenant-admin").build();
    SecurityContextHolder.getContext().setAuthentication(authentication);

    assertThat(authorisationService.isSuperAdmin()).isFalse();
    assertThat(authorisationService.findTenantIdInAccessToken()).isEmpty();
  }
}
