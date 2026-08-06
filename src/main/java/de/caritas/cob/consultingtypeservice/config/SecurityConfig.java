package de.caritas.cob.consultingtypeservice.config;

import de.caritas.cob.consultingtypeservice.api.auth.RoleAuthorizationAuthorityMapper;
import de.caritas.cob.consultingtypeservice.filter.HttpTenantFilter;
import de.caritas.cob.consultingtypeservice.filter.StatelessCsrfFilter;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;

/** Provides the Security configuration. */
@KeycloakConfiguration
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  public static final String[] WHITE_LIST =
      new String[] {
        "/consultingtypes/docs",
        "/consultingtypes/docs/**",
        "/v2/api-docs",
        "/configuration/ui",
        "/swagger-resources/**",
        "/configuration/security",
        "/swagger-ui.html",
        "/webjars/**",
        "/actuator/health",
        "/actuator/health/**"
      };

  private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
      new JwtGrantedAuthoritiesConverter();

  @Value("${csrf.cookie.property}")
  private String csrfCookieProperty;

  @Value("${csrf.header.property}")
  private String csrfHeaderProperty;

  @Value("${multitenancy.enabled}")
  private boolean multitenancy;

  @Value("${keycloak.principal-attribute:preferred_username}")
  private String principalAttribute;

  @Nullable private final HttpTenantFilter tenantFilter;

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter)
      throws Exception {
    var httpSecurity =
        http.csrf(csrf -> csrf.disable())
            .addFilterBefore(
                new StatelessCsrfFilter(csrfCookieProperty, csrfHeaderProperty), CsrfFilter.class);

    httpSecurity = enableTenantFilterIfMultitenancyEnabled(httpSecurity);

    httpSecurity
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers(WHITE_LIST)
                    .permitAll()
                    .requestMatchers("/settings", "/settings/*")
                    .permitAll()
                    .requestMatchers("/topic/public", "/topic/public/*")
                    .permitAll()
                    .requestMatchers("/topic", "/topic/*")
                    .authenticated()
                    .requestMatchers(HttpMethod.GET, "/topic-groups")
                    .permitAll()
                    .requestMatchers("/topicadmin", "/topicadmin/*")
                    .authenticated()
                    .requestMatchers("/settingsadmin", "/settingsadmin/*")
                    .authenticated()
                    .anyRequest()
                    .permitAll())
        .oauth2ResourceServer(
            oauth2 ->
                oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

    return httpSecurity.build();
  }

  @Bean
  public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter(
      RoleAuthorizationAuthorityMapper authorityMapper) {
    return jwt -> {
      Collection<GrantedAuthority> authorities = grantedAuthorities(jwt, authorityMapper);
      return new JwtAuthenticationToken(jwt, authorities, principalName(jwt));
    };
  }

  private HttpSecurity enableTenantFilterIfMultitenancyEnabled(HttpSecurity httpSecurity)
      throws Exception {
    if (multitenancy && tenantFilter != null) {
      httpSecurity.addFilterAfter(tenantFilter, BearerTokenAuthenticationFilter.class);
    }
    return httpSecurity;
  }

  private Collection<GrantedAuthority> grantedAuthorities(
      Jwt jwt, RoleAuthorizationAuthorityMapper authorityMapper) {
    var authorities = new HashSet<GrantedAuthority>();
    Collection<GrantedAuthority> jwtAuthorities = jwtGrantedAuthoritiesConverter.convert(jwt);
    if (jwtAuthorities != null) {
      authorities.addAll(jwtAuthorities);
    }

    Set<GrantedAuthority> roleAuthorities =
        extractKeycloakRoles(jwt).stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    authorities.addAll(authorityMapper.mapAuthorities(roleAuthorities));
    return authorities;
  }

  @SuppressWarnings("unchecked")
  private Set<String> extractKeycloakRoles(Jwt jwt) {
    var roles = new HashSet<String>();
    Object realmAccess = jwt.getClaims().get("realm_access");
    if (realmAccess instanceof Map) {
      addRoles(roles, ((Map<String, Object>) realmAccess).get("roles"));
    }

    Object resourceAccess = jwt.getClaims().get("resource_access");
    if (resourceAccess instanceof Map) {
      Map<String, Object> resourceAccessMap = (Map<String, Object>) resourceAccess;
      resourceAccessMap.values().stream()
          .filter(Map.class::isInstance)
          .map(value -> (Map<String, Object>) value)
          .forEach(clientAccess -> addRoles(roles, clientAccess.get("roles")));
    }
    return roles;
  }

  private void addRoles(Set<String> roles, Object rolesClaim) {
    if (rolesClaim instanceof Collection) {
      Collection<?> roleCollection = (Collection<?>) rolesClaim;
      roleCollection.stream().filter(Objects::nonNull).map(Object::toString).forEach(roles::add);
    }
  }

  private String principalName(Jwt jwt) {
    String principal = jwt.getClaimAsString(principalAttribute);
    return principal == null ? jwt.getSubject() : principal;
  }
}
