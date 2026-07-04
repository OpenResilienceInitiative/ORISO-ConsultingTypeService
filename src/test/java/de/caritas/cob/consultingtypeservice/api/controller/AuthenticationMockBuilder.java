package de.caritas.cob.consultingtypeservice.api.controller;

import de.caritas.cob.consultingtypeservice.api.auth.RoleAuthorizationAuthorityMapper;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class AuthenticationMockBuilder {

  private String userRole;
  private String tenantId;

  public AuthenticationMockBuilder withUserRole(String userRole) {
    this.userRole = userRole;
    return this;
  }

  public AuthenticationMockBuilder withTenantId(String tenantId) {
    this.tenantId = tenantId;
    return this;
  }

  public Authentication build() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", "test");
    claims.put("userId", "some userid");
    if (tenantId != null) {
      claims.put("tenantId", tenantId);
    }
    claims.put("realm_access", Map.of("roles", List.of(userRole)));

    Jwt jwt =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .subject("some userid")
            .claims(map -> map.putAll(claims))
            .build();

    Collection<? extends GrantedAuthority> authorities =
        new RoleAuthorizationAuthorityMapper()
            .mapAuthorities(List.of((GrantedAuthority) () -> userRole));

    return new JwtAuthenticationToken(jwt, authorities, "test");
  }
}
