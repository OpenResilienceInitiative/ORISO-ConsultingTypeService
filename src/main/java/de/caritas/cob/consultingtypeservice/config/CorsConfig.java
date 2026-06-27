package de.caritas.cob.consultingtypeservice.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@ConditionalOnProperty(name = "consulting.type.cors.enabled", havingValue = "true")
public class CorsConfig {

  @Value("${consulting.type.cors.allowed.origins}")
  private List<String> allowedOrigins;

  @Value("${consulting.type.cors.allowed.paths}")
  private String[] allowedPaths;

  @Bean
  public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
    validateAllowedOrigins();

    var configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(allowedOrigins);
    configuration.setAllowedMethods(
        Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);

    var source = new UrlBasedCorsConfigurationSource();
    Arrays.stream(allowedPaths)
        .forEach(path -> source.registerCorsConfiguration(path, configuration));

    var registrationBean = new FilterRegistrationBean<>(new CorsFilter(source));
    registrationBean.setOrder(Integer.MIN_VALUE);
    return registrationBean;
  }

  private void validateAllowedOrigins() {
    if (allowedOrigins.contains("*")) {
      throw new IllegalArgumentException(
          "CONSULTING_TYPE_CORS_ALLOWED_ORIGINS must contain explicit origins when credentials are"
              + " allowed. Use values like http://localhost:9001,http://127.0.0.1:9001 instead of"
              + " *.");
    }
  }
}
