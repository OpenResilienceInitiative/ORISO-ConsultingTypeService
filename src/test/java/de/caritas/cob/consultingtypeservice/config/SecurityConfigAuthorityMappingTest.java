package de.caritas.cob.consultingtypeservice.config;

import static org.assertj.core.api.Assertions.assertThat;

import de.caritas.cob.consultingtypeservice.api.auth.RoleAuthorizationAuthorityMapper;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Authorities come from realm roles only (ORISO-Helm#367). Client roles of any client in {@code
 * resource_access} are not role grants for this service.
 */
class SecurityConfigAuthorityMappingTest {

  private static final String PATCH_APPLICATION_SETTINGS =
      "AUTHORIZATION_PATCH_APPLICATION_SETTINGS";

  private Set<String> authoritiesOf(Map<String, Object> claims) {
    var securityConfig = new SecurityConfig(null);
    ReflectionTestUtils.setField(securityConfig, "principalAttribute", "preferred_username");
    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .subject("subject")
            .claims(map -> map.putAll(claims))
            .build();
    return securityConfig
        .jwtAuthenticationConverter(new RoleAuthorizationAuthorityMapper())
        .convert(jwt)
        .getAuthorities()
        .stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.toSet());
  }

  @Test
  void realmRole_Should_mapToItsAuthorities() {
    var authorities =
        authoritiesOf(Map.of("realm_access", Map.of("roles", List.of("tenant-admin"))));

    assertThat(authorities).contains(PATCH_APPLICATION_SETTINGS);
  }

  @Test
  void clientRoleOfAnotherClient_Should_notMapToAuthorities() {
    var authorities =
        authoritiesOf(
            Map.of(
                "realm_access", Map.of("roles", List.of("user")),
                "resource_access",
                    Map.of("some-client", Map.of("roles", List.of("tenant-admin", "technical")))));

    assertThat(authorities)
        .doesNotContain(PATCH_APPLICATION_SETTINGS, "AUTHORIZATION_TECHNICAL_DEFAULT");
  }

  @Test
  void clientRoleOfThisServicesOwnClient_Should_notMapToAuthorities() {
    // no Keycloak client "consulting-type-service" exists in the shipped realms, so its client
    // roles are not a supported grant either
    var authorities =
        authoritiesOf(
            Map.of(
                "resource_access",
                Map.of("consulting-type-service", Map.of("roles", List.of("tenant-admin")))));

    assertThat(authorities).doesNotContain(PATCH_APPLICATION_SETTINGS);
  }

  @Test
  void technicalRealmRole_Should_keepTechnicalDefault() {
    var authorities = authoritiesOf(Map.of("realm_access", Map.of("roles", List.of("technical"))));

    assertThat(authorities).contains("AUTHORIZATION_TECHNICAL_DEFAULT");
  }
}
