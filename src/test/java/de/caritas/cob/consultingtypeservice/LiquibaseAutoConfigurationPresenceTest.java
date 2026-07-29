package de.caritas.cob.consultingtypeservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.annotation.ImportCandidates;

/**
 * Guard against the Spring Boot 4 Liquibase silent-noop regression (bug #569, chain-test lane).
 *
 * <p>Spring Boot 4 moved {@code LiquibaseAutoConfiguration} out of {@code
 * spring-boot-autoconfigure} into the separate {@code spring-boot-liquibase} module. With only
 * {@code liquibase-core} on the classpath, every {@code spring.liquibase.*} property is silently
 * ignored at boot: no DATABASECHANGELOG is written, no migration runs, and the service crash-loops
 * on Hibernate's schema validation ("missing table [topic]") on any fresh database.
 *
 * <p>This test fails at build time if the {@code spring-boot-liquibase} dependency is ever dropped
 * from the pom again, instead of letting the defect surface as a runtime crash-loop on deploy. The
 * class name is referenced as a string on purpose so this test still compiles (and fails
 * meaningfully) when the module is missing.
 */
class LiquibaseAutoConfigurationPresenceTest {

  private static final String LIQUIBASE_AUTO_CONFIGURATION =
      "org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration";

  @Test
  void liquibaseAutoConfigurationClassIsOnClasspath() {
    assertThat(isClassPresent(LIQUIBASE_AUTO_CONFIGURATION))
        .as(
            "%s must be loadable; add the org.springframework.boot:spring-boot-liquibase "
                + "dependency (Spring Boot 4 no longer ships Liquibase auto-configuration in "
                + "spring-boot-autoconfigure)",
            LIQUIBASE_AUTO_CONFIGURATION)
        .isTrue();
  }

  @Test
  void liquibaseAutoConfigurationIsRegisteredAsImportCandidate() {
    final var candidates =
        ImportCandidates.load(AutoConfiguration.class, getClass().getClassLoader());

    assertThat(candidates)
        .as(
            "%s must be registered in META-INF/spring/"
                + "org.springframework.boot.autoconfigure.AutoConfiguration.imports; otherwise "
                + "spring.liquibase.* is silently ignored and fresh databases never get migrated",
            LIQUIBASE_AUTO_CONFIGURATION)
        .contains(LIQUIBASE_AUTO_CONFIGURATION);
  }

  private static boolean isClassPresent(final String className) {
    try {
      Class.forName(className, false, getClassLoader());
      return true;
    } catch (final ClassNotFoundException e) {
      return false;
    }
  }

  private static ClassLoader getClassLoader() {
    return LiquibaseAutoConfigurationPresenceTest.class.getClassLoader();
  }
}
