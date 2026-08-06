package de.caritas.cob.consultingtypeservice.api.tenant;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import de.caritas.cob.consultingtypeservice.api.service.TenantHeaderSupplier;
import de.caritas.cob.consultingtypeservice.api.service.TenantService;
import de.caritas.cob.consultingtypeservice.filter.SubdomainExtractor;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnExpression("${multitenancy.enabled:true}")
@Component
public class TenantResolver {

  public static final Long TECHNICAL_TENANT_ID = 0L;
  private final @NonNull TenantHeaderSupplier tenantHeaderSupplier;
  private @NonNull SubdomainExtractor subdomainExtractor;
  private @NonNull TenantService tenantService;

  public Long resolve() {
    return resolveForNonAuthenticatedUser();
  }

  private Long resolveForNonAuthenticatedUser() {
    Optional<Long> tenantId = resolveTenantFromHttpRequest();
    if (tenantId.isEmpty()) {
      log.warn("Tenant id could not be resolved for request");
      throw new AccessDeniedException("Tenant id could not be resolved");
    }
    return tenantId.get();
  }

  private Optional<Long> resolveTenantFromHttpRequest() {

    Optional<Long> tenantFromHeader = tenantHeaderSupplier.getTenantFromHeader();
    Optional<String> currentSubdomain = subdomainExtractor.getCurrentSubdomain();

    // The `tenantId` header is a trusted internal, service-to-service header (set by sibling
    // backends from their already-resolved TenantContext). It must never let a client override the
    // tenant derived from the request's subdomain. When a subdomain is present it is authoritative:
    // a header that contradicts it is a spoofing attempt and is rejected. Mirrors the cross-tenant
    // validation the sibling backends (User/AgencyService) perform in their TenantResolverService.
    if (currentSubdomain.isPresent()) {
      Long tenantFromSubdomain = getTenantIdBySubdomain(currentSubdomain.get());
      if (tenantFromHeader.isPresent() && !tenantFromHeader.get().equals(tenantFromSubdomain)) {
        log.warn(
            "Rejecting request: tenantId header ({}) contradicts subdomain-derived tenant ({}).",
            tenantFromHeader.get(),
            tenantFromSubdomain);
        throw new AccessDeniedException("Tenant id from header and subdomain do not match.");
      }
      return of(tenantFromSubdomain);
    }

    // No subdomain to cross-check (e.g. genuine internal service-to-service calls): fall back to
    // the
    // header as before.
    if (tenantFromHeader.isPresent()) {
      return tenantFromHeader;
    }

    return empty();
  }

  private Long getTenantIdBySubdomain(String currentSubdomain) {
    return tenantService.getRestrictedTenantDataBySubdomain(currentSubdomain).getId();
  }
}
