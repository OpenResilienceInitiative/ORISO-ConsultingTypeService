package de.caritas.cob.consultingtypeservice.api.auth;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

/** Resolves authorisation details from the current security context. */
@Service("authorisationService")
public class AuthorisationService {

  private static final String TENANT_ADMIN_ROLE = "tenant-admin";

  public Optional<Long> findTenantIdInAccessToken() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken) {
      JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
      return parseTenantId(jwtAuthenticationToken.getToken().getClaims().get("tenantId"));
    }
    if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
      Jwt jwt = (Jwt) authentication.getPrincipal();
      return parseTenantId(jwt.getClaims().get("tenantId"));
    }
    return Optional.empty();
  }

  /**
   * The platform administrator: tenant 0 <em>and</em> the realm role {@code tenant-admin}, as in
   * TenantService. The tenant claim alone is not enough, because other identities (for example
   * service accounts) can also carry tenant 0.
   */
  public boolean isSuperAdmin() {
    Optional<Long> tenantId = findTenantIdInAccessToken();
    return tenantId.isPresent() && tenantId.get().equals(0L) && hasRealmRole(TENANT_ADMIN_ROLE);
  }

  @SuppressWarnings("unchecked")
  private boolean hasRealmRole(String role) {
    return findJwt()
        .map(jwt -> jwt.getClaims().get("realm_access"))
        .filter(Map.class::isInstance)
        .map(realmAccess -> ((Map<String, Object>) realmAccess).get("roles"))
        .filter(Collection.class::isInstance)
        .map(roles -> ((Collection<Object>) roles).stream().anyMatch(role::equals))
        .orElse(false);
  }

  private Optional<Jwt> findJwt() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken) {
      return Optional.of(((JwtAuthenticationToken) authentication).getToken());
    }
    if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
      return Optional.of((Jwt) authentication.getPrincipal());
    }
    return Optional.empty();
  }

  private Optional<Long> parseTenantId(Object tenantIdClaim) {
    if (tenantIdClaim == null) {
      return Optional.empty();
    }
    if (tenantIdClaim instanceof Number) {
      return Optional.of(((Number) tenantIdClaim).longValue());
    }
    try {
      return Optional.of(Long.parseLong(tenantIdClaim.toString()));
    } catch (NumberFormatException exception) {
      return Optional.empty();
    }
  }
}
