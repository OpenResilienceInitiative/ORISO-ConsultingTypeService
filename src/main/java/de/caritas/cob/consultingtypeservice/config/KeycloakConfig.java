package de.caritas.cob.consultingtypeservice.config;

import static java.util.Objects.nonNull;

import de.caritas.cob.consultingtypeservice.api.auth.AuthenticatedUser;
import de.caritas.cob.consultingtypeservice.api.exception.KeycloakException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import org.apache.commons.codec.binary.Base32;
import org.hibernate.validator.constraints.URL;
import org.keycloak.adapters.KeycloakConfigResolver;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.context.WebApplicationContext;

@Data
@Configuration
@Validated
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakConfig {

  @Bean
  @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public AuthenticatedUser authenticatedUser(HttpServletRequest request) {
    var userPrincipal = request.getUserPrincipal();
    var authenticatedUser = new AuthenticatedUser();

    if (nonNull(userPrincipal) && userPrincipal instanceof JwtAuthenticationToken) {
      JwtAuthenticationToken authToken = (JwtAuthenticationToken) userPrincipal;
      try {
        Jwt jwt = authToken.getToken();
        Map<String, Object> claimMap = jwt.getClaims();
        if (claimMap.containsKey("username")) {
          authenticatedUser.setUsername(decodeUsername(claimMap.get("username").toString()));
        }
        authenticatedUser.setUserId(jwt.getSubject());
        authenticatedUser.setAccessToken(jwt.getTokenValue());
        authenticatedUser.setRoles(extractRealmRoles(jwt));
      } catch (AccessDeniedException e) {
        throw e;
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

  @SuppressWarnings("unchecked")
  private Set<String> extractRealmRoles(Jwt jwt) {
    Object realmAccess = jwt.getClaims().get("realm_access");
    if (realmAccess instanceof Map) {
      Object rolesClaim = ((Map<String, Object>) realmAccess).get("roles");
      if (rolesClaim instanceof Collection) {
        Collection<?> roles = (Collection<?>) rolesClaim;
        return roles.stream().map(Object::toString).collect(Collectors.toSet());
      }
    }
    return new HashSet<>();
  }

  private String decodeUsername(String username) {
    if (username == null) {
      return null;
    }
    if (!username.startsWith("enc.")) {
      return username;
    }
    try {
      byte[] decoded = new Base32().decode(username.substring(4).toUpperCase().replace(".", "="));
      return new String(decoded, StandardCharsets.UTF_8);
    } catch (IllegalArgumentException e) {
      throw new AccessDeniedException("Invalid encoded username: " + username, e);
    }
  }

  @Bean
  public KeycloakConfigResolver keycloakConfigResolver() {
    return new KeycloakSpringBootConfigResolver();
  }

  @URL private String authServerUrl;

  @NotBlank private String realm;

  @NotBlank private String resource;

  @NotBlank private String principalAttribute;

  @NotNull private Boolean cors;
}
