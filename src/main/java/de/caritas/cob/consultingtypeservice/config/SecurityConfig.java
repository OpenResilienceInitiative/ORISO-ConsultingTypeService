package de.caritas.cob.consultingtypeservice.config;

import de.caritas.cob.consultingtypeservice.api.auth.RoleAuthorizationAuthorityMapper;
import de.caritas.cob.consultingtypeservice.filter.HttpTenantFilter;
import de.caritas.cob.consultingtypeservice.filter.StatelessCsrfFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/** Provides the Security configuration. */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig {

  @Value("${csrf.cookie.property}")
  private String csrfCookieProperty;

  @Value("${csrf.header.property}")
  private String csrfHeaderProperty;

  @Value("${multitenancy.enabled}")
  private boolean multitenancy;

  @Autowired(required = false)
  private @Nullable HttpTenantFilter tenantFilter;

  public static final String[] WHITE_LIST =
      new String[] {
        "/consultingtypes/docs",
        "/consultingtypes/docs/**",
        "/v3/api-docs/**",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/actuator/health",
        "/actuator/health/**"
      };

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http, RoleAuthorizationAuthorityMapper authorityMapper) throws Exception {
    var httpSecurity =
        http.csrf(csrf -> csrf.disable())
            .addFilterBefore(
                new StatelessCsrfFilter(csrfCookieProperty, csrfHeaderProperty), CsrfFilter.class);

    if (multitenancy) {
      httpSecurity = httpSecurity.addFilterAfter(this.tenantFilter, BearerTokenAuthenticationFilter.class);
    }

    httpSecurity
        .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(WHITE_LIST).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/settings")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/settings/*")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/topic/public")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/topic/public/*")).permitAll()
            .requestMatchers(new AntPathRequestMatcher("/topic")).authenticated()
            .requestMatchers(new AntPathRequestMatcher("/topic/*")).authenticated()
            .requestMatchers(HttpMethod.GET, "/topic-groups").permitAll()
            .requestMatchers(new AntPathRequestMatcher("/topicadmin")).authenticated()
            .requestMatchers(new AntPathRequestMatcher("/topicadmin/*")).authenticated()
            .requestMatchers(new AntPathRequestMatcher("/settingsadmin")).authenticated()
            .requestMatchers(new AntPathRequestMatcher("/settingsadmin/*")).authenticated()
            .requestMatchers(new NegatedRequestMatcher(new AntPathRequestMatcher("/topic"))).permitAll()
            .requestMatchers(new NegatedRequestMatcher(new AntPathRequestMatcher("/topic/*"))).permitAll()
            .requestMatchers(new NegatedRequestMatcher(new AntPathRequestMatcher("/topic-groups"))).permitAll()
            .requestMatchers(new NegatedRequestMatcher(new AntPathRequestMatcher("/topicadmin"))).permitAll()
            .requestMatchers(new NegatedRequestMatcher(new AntPathRequestMatcher("/topicadmin/*"))).permitAll()
            .anyRequest().permitAll())
        .headers(headers -> headers
            .xssProtection(xss -> {})
            .contentSecurityPolicy(csp -> csp.policyDirectives("script-src 'self'")))
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter(authorityMapper))));

    return httpSecurity.build();
  }

  private JwtAuthenticationConverter jwtAuthenticationConverter(
      RoleAuthorizationAuthorityMapper authorityMapper) {
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setPrincipalClaimName("preferred_username");
    converter.setJwtGrantedAuthoritiesConverter(jwt -> authorityMapper.mapAuthorities(extractRoleAuthorities(jwt)));
    return converter;
  }

  private Collection<? extends GrantedAuthority> extractRoleAuthorities(Jwt jwt) {
    List<GrantedAuthority> authorities = new ArrayList<>();

    Object realmAccess = jwt.getClaims().get("realm_access");
    if (realmAccess instanceof Map<?, ?> realmAccessMap) {
      Object roles = realmAccessMap.get("roles");
      if (roles instanceof Collection<?> rolesCollection) {
        rolesCollection.stream()
            .filter(String.class::isInstance)
            .map(String.class::cast)
            .map(SimpleGrantedAuthority::new)
            .forEach(authorities::add);
      }
    }

    Object resourceAccess = jwt.getClaims().get("resource_access");
    if (resourceAccess instanceof Map<?, ?> resourceAccessMap) {
      for (Object client : resourceAccessMap.values()) {
        if (client instanceof Map<?, ?> clientMap) {
          Object roles = clientMap.get("roles");
          if (roles instanceof Collection<?> rolesCollection) {
            rolesCollection.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(SimpleGrantedAuthority::new)
                .forEach(authorities::add);
          }
        }
      }
    }

    return authorities;
  }
}
