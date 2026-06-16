package de.caritas.cob.consultingtypeservice.api.auth;

import java.util.Optional;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/** Resolves authorisation details from the current security context. */
@Service("authorisationService")
public class AuthorisationService {

  public Optional<Long> findTenantIdInAccessToken() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof KeycloakAuthenticationToken) {
      KeycloakAuthenticationToken keycloakAuthenticationToken =
          (KeycloakAuthenticationToken) authentication;
      return parseTenantId(
          keycloakAuthenticationToken
              .getAccount()
              .getKeycloakSecurityContext()
              .getToken()
              .getOtherClaims()
              .get("tenantId"));
    }
    if (authentication != null && authentication.getPrincipal() instanceof KeycloakPrincipal) {
      KeycloakPrincipal<?> keycloakPrincipal = (KeycloakPrincipal<?>) authentication.getPrincipal();
      return parseTenantId(
          keycloakPrincipal
              .getKeycloakSecurityContext()
              .getToken()
              .getOtherClaims()
              .get("tenantId"));
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
