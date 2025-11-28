package de.caritas.cob.consultingtypeservice.config;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Validates that all required configuration values are provided via ConfigMaps/Secrets.
 * Throws an exception on startup if any required configuration is missing.
 */
@Component
public class ConfigurationValidator {

  @Value("${spring.datasource.url:}")
  private String datasourceUrl;

  @Value("${spring.datasource.username:}")
  private String datasourceUsername;

  @Value("${spring.datasource.password:}")
  private String datasourcePassword;

  @Value("${spring.data.mongodb.uri:}")
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
      missingConfigs.add("spring.data.mongodb.uri (SPRING_DATA_MONGODB_URI)");
    }
    if (isEmpty(keycloakAuthServerUrl)) {
      missingConfigs.add("keycloak.auth-server-url (KEYCLOAK_AUTH_SERVER_URL)");
    }
    if (isEmpty(keycloakRealm)) {
      missingConfigs.add("keycloak.realm (KEYCLOAK_REALM)");
    }
    if (isEmpty(jwtIssuerUri)) {
      missingConfigs.add("spring.security.oauth2.resourceserver.jwt.issuer-uri (SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI)");
    }
    if (isEmpty(tenantServiceApiUrl)) {
      missingConfigs.add("tenant.service.api.url (TENANT_SERVICE_API_URL)");
    }

    if (!missingConfigs.isEmpty()) {
      String errorMessage = String.format(
          "CRITICAL: Missing required configuration values. Please provide the following via ConfigMap/Secrets:\n%s\n\n" +
          "IMPORTANT: Use Kubernetes DNS names (e.g., mariadb.caritas.svc.cluster.local:3306) NOT hardcoded IPs.\n" +
          "DNS names ensure services can find resources even when pods are rescheduled or scaled.",
          String.join("\n", missingConfigs.stream()
              .map(config -> "  - config '" + config + "' is missing")
              .toArray(String[]::new))
      );
      throw new IllegalStateException(errorMessage);
    }
  }

  private boolean isEmpty(String value) {
    return value == null || value.trim().isEmpty();
  }
}

