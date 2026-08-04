package de.caritas.cob.consultingtypeservice.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Regression guard for the silent Liquibase outage described in #67.
 *
 * <p><b>Root cause:</b> Spring Boot 4 split {@code LiquibaseAutoConfiguration} out of {@code
 * spring-boot-autoconfigure} into its own {@code spring-boot-liquibase} module. This service
 * declared only {@code org.liquibase:liquibase-core}, so the auto-configuration was never a
 * candidate, every {@code spring.liquibase.*} property was ignored, and the application started
 * with <b>zero</b> log output about migrations. A fresh database then crashed late with {@code
 * Schema validation: missing table [topic]}; an already-migrated database silently froze at
 * whatever changeset it happened to carry.
 *
 * <p><b>Why this test and not the existing IT:</b> {@link
 * de.caritas.cob.consultingtypeservice.LiquibaseSchemaDriftIT} drives Liquibase through its own API
 * against a real MariaDB. It proves the changelog matches the JPA entities, but it never touches
 * Spring's auto-configuration, so it stayed green throughout the outage. This test asserts the
 * missing half: that Boot can actually discover and run Liquibase at startup. It is deliberately
 * string-based so that removing the dependency fails the assertion instead of breaking compilation.
 */
class LiquibaseAutoConfigurationPresenceTest {

  private static final String AUTO_CONFIGURATION_IMPORTS =
      "META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports";

  private static final String LIQUIBASE_AUTO_CONFIGURATION =
      "org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration";

  @Test
  void liquibaseAutoConfigurationShouldBeOnTheClasspath() {
    assertThatCode(
            () ->
                Class.forName(
                    LIQUIBASE_AUTO_CONFIGURATION,
                    false,
                    Thread.currentThread().getContextClassLoader()))
        .as(
            "spring-boot-liquibase must stay on the classpath; liquibase-core alone leaves"
                + " spring.liquibase.* silently unbound (see #67)")
        .doesNotThrowAnyException();
  }

  @Test
  void liquibaseAutoConfigurationShouldBeRegisteredAsAutoConfigurationCandidate()
      throws IOException {
    assertThat(autoConfigurationCandidates())
        .as(
            "Liquibase must be an auto-configuration candidate, otherwise Boot skips migrations"
                + " at startup without logging anything (see #67)")
        .contains(LIQUIBASE_AUTO_CONFIGURATION);
  }

  private List<String> autoConfigurationCandidates() throws IOException {
    final var classLoader = Thread.currentThread().getContextClassLoader();
    final var candidates = new ArrayList<String>();
    for (final URL url : Collections.list(classLoader.getResources(AUTO_CONFIGURATION_IMPORTS))) {
      try (var reader =
          new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
        reader
            .lines()
            .map(String::trim)
            .filter(line -> !line.isEmpty() && !line.startsWith("#"))
            .forEach(candidates::add);
      }
    }
    return candidates;
  }
}
