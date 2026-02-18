package de.caritas.cob.consultingtypeservice.config;

import static java.util.Objects.nonNull;

import de.caritas.cob.consultingtypeservice.api.auth.AuthenticatedUser;
import de.caritas.cob.consultingtypeservice.api.exception.KeycloakException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;

@Data
@Configuration
@Validated
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakConfig {

  public AuthenticatedUser authenticatedUser(HttpServletRequest request) {
    var authenticatedUser = new AuthenticatedUser();

    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken jwtAuth) {
      Jwt jwt = jwtAuth.getToken();
      Map<String, Object> claimMap = jwt.getClaims();

      try {
        if (claimMap.containsKey("username")) {
          authenticatedUser.setUsername(claimMap.get("username").toString());
        } else if (claimMap.containsKey("preferred_username")) {
          authenticatedUser.setUsername(claimMap.get("preferred_username").toString());
        }
        authenticatedUser.setUserId(jwt.getSubject());
        authenticatedUser.setAccessToken(jwt.getTokenValue());
        authenticatedUser.setRoles(extractRealmRoles(claimMap));
      } catch (Exception exception) {
        throw new KeycloakException("Keycloak data missing.", exception);
      }

      var authorities =
          SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
              .map(Object::toString)
              .collect(Collectors.toSet());
      authenticatedUser.setGrantedAuthorities(authorities);
    }

    return authenticatedUser;
  }

  private Set<String> extractRealmRoles(Map<String, Object> claims) {
    Object realmAccess = claims.get("realm_access");
    if (realmAccess instanceof Map<?, ?> realmAccessMap) {
      Object roles = realmAccessMap.get("roles");
      if (roles instanceof Collection<?> rolesCollection) {
        return rolesCollection.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .collect(Collectors.toSet());
      }
    }
    return Set.of();
  }

  @URL private String authServerUrl;

  @NotBlank private String realm;

  @NotBlank private String resource;

  @NotBlank private String principalAttribute;

  @NotNull private Boolean cors;
}
