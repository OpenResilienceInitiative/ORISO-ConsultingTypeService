package de.caritas.cob.consultingtypeservice.api.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

import de.caritas.cob.consultingtypeservice.api.service.TenantHeaderSupplier;
import de.caritas.cob.consultingtypeservice.api.service.TenantService;
import de.caritas.cob.consultingtypeservice.filter.SubdomainExtractor;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.RestrictedTenantDTO;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class TenantResolverTest {

  @InjectMocks private TenantResolver tenantResolver;

  @Mock private TenantHeaderSupplier tenantHeaderSupplier;

  @Mock private SubdomainExtractor subdomainExtractor;

  @Mock private TenantService tenantService;

  private void givenSubdomainResolvesToTenant(String subdomain, Long tenantId) {
    when(subdomainExtractor.getCurrentSubdomain()).thenReturn(Optional.of(subdomain));
    var restrictedTenantDTO = new RestrictedTenantDTO().id(tenantId);
    when(tenantService.getRestrictedTenantDataBySubdomain(subdomain))
        .thenReturn(restrictedTenantDTO);
  }

  @Test
  void resolve_Should_ThrowAccessDenied_When_HeaderTenantContradictsSubdomainTenant() {
    // given: request is on tenant-1's subdomain but carries a spoofed tenantId header for tenant 2
    when(tenantHeaderSupplier.getTenantFromHeader()).thenReturn(Optional.of(2L));
    givenSubdomainResolvesToTenant("tenant-one", 1L);

    // when / then: the spoofed header must NOT override the subdomain-derived tenant
    assertThatExceptionOfType(AccessDeniedException.class)
        .isThrownBy(() -> tenantResolver.resolve());
  }

  @Test
  void resolve_Should_ReturnHeaderTenant_When_HeaderMatchesSubdomainTenant() {
    // given: header and subdomain agree (legitimate)
    when(tenantHeaderSupplier.getTenantFromHeader()).thenReturn(Optional.of(1L));
    givenSubdomainResolvesToTenant("tenant-one", 1L);

    // when
    Long resolved = tenantResolver.resolve();

    // then
    assertThat(resolved).isEqualTo(1L);
  }

  @Test
  void resolve_Should_ReturnHeaderTenant_When_NoSubdomainPresent() {
    // given: internal service-to-service call — header present, no subdomain to cross-check
    when(tenantHeaderSupplier.getTenantFromHeader()).thenReturn(Optional.of(5L));
    when(subdomainExtractor.getCurrentSubdomain()).thenReturn(Optional.empty());

    // when
    Long resolved = tenantResolver.resolve();

    // then: legitimate internal callers (no subdomain) keep working
    assertThat(resolved).isEqualTo(5L);
  }

  @Test
  void resolve_Should_ReturnSubdomainTenant_When_NoHeaderPresent() {
    // given: browser request, no tenantId header, subdomain drives resolution
    when(tenantHeaderSupplier.getTenantFromHeader()).thenReturn(Optional.empty());
    givenSubdomainResolvesToTenant("tenant-one", 1L);

    // when
    Long resolved = tenantResolver.resolve();

    // then
    assertThat(resolved).isEqualTo(1L);
  }

  @Test
  void resolve_Should_ThrowAccessDenied_When_NeitherHeaderNorSubdomainPresent() {
    // given
    when(tenantHeaderSupplier.getTenantFromHeader()).thenReturn(Optional.empty());
    when(subdomainExtractor.getCurrentSubdomain()).thenReturn(Optional.empty());

    // when / then
    assertThatExceptionOfType(AccessDeniedException.class)
        .isThrownBy(() -> tenantResolver.resolve());
  }
}
