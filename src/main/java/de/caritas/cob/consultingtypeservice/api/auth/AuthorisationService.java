package de.caritas.cob.consultingtypeservice.api.auth;

import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

/** Resolves authorisation details from the current security context. */
@Service("authorisationService")
public class AuthorisationService {

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

  public boolean isSuperAdmin() {
    Optional<Long> tenantId = findTenantIdInAccessToken();
    return tenantId.isPresent() && tenantId.get().equals(0L);
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
