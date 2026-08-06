package de.caritas.cob.consultingtypeservice.config;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Validates that all required configuration values are provided via ConfigMaps/Secrets. Throws an
 * exception on startup if any required configuration is missing.
 */
@Component
@Profile("!testing")
public class ConfigurationValidator {

  @Value("${spring.datasource.url:}")
  private String datasourceUrl;

  @Value("${spring.datasource.username:}")
  private String datasourceUsername;

  @Value("${spring.datasource.password:}")
  private String datasourcePassword;

  // Spring Boot 4 renamed the Mongo config property from spring.data.mongodb.uri to
  // spring.mongodb.uri (old key is deprecation level=error since 4.0.0 and is no longer bound).
  // Validate the property the framework actually reads so a missing URI fails fast with a clear
  // message instead of the driver silently defaulting to localhost:27017 and crashlooping.
  @Value("${spring.mongodb.uri:}")
  private String mongodbUri;

  @Value("${keycloak.auth-server-url:}")
  private String keycloakAuthServerUrl;

  @Value("${keycloak.realm:}")
  private String keycloakRealm;

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
  private String jwtIssuerUri;

  @Value("${tenant.service.api.url:}")
  private String tenantServiceApiUrl;

  @PostConstruct
  public void validateConfiguration() {
    List<String> missingConfigs = new ArrayList<>();

    if (isEmpty(datasourceUrl)) {
      missingConfigs.add("spring.datasource.url (SPRING_DATASOURCE_URL)");
    }
    if (isEmpty(datasourceUsername)) {
      missingConfigs.add("spring.datasource.username (SPRING_DATASOURCE_USERNAME)");
    }
    if (isEmpty(datasourcePassword)) {
      missingConfigs.add("spring.datasource.password (SPRING_DATASOURCE_PASSWORD)");
    }
    if (isEmpty(mongodbUri)) {
      missingConfigs.add("spring.mongodb.uri (SPRING_DATA_MONGODB_URI)");
    }
    if (isEmpty(keycloakAuthServerUrl)) {
      missingConfigs.add("keycloak.auth-server-url (KEYCLOAK_AUTH_SERVER_URL)");
    }
    if (isEmpty(keycloakRealm)) {
      missingConfigs.add("keycloak.realm (KEYCLOAK_REALM)");
    }
    if (isEmpty(jwtIssuerUri)) {
      missingConfigs.add(
          "spring.security.oauth2.resourceserver.jwt.issuer-uri (SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI)");
    }
    if (isEmpty(tenantServiceApiUrl)) {
      missingConfigs.add("tenant.service.api.url (TENANT_SERVICE_API_URL)");
    }

    if (!missingConfigs.isEmpty()) {
      String errorMessage =
          String.format(
              "CRITICAL: Missing required configuration values. Please provide the following via ConfigMap/Secrets:\n%s\n\n"
                  + "IMPORTANT: Use Kubernetes DNS names (e.g., mariadb.caritas.svc.cluster.local:3306) NOT hardcoded IPs.\n"
                  + "DNS names ensure services can find resources even when pods are rescheduled or scaled.",
              String.join(
                  "\n",
                  missingConfigs.stream()
                      .map(config -> "  - config '" + config + "' is missing")
                      .toArray(String[]::new)));
      throw new IllegalStateException(errorMessage);
    }
  }

  private boolean isEmpty(String value) {
    return value == null || value.trim().isEmpty();
  }
}
