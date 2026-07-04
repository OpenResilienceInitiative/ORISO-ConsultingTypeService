package de.caritas.cob.consultingtypeservice;

import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicGroupEntity;
import java.sql.Connection;
import java.sql.DriverManager;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

/**
 * Permanent drift guard for the MariaDB part of this service (Liquibase Re-Enablement Plan
 * 2026-07-04, package L1).
 *
 * <p>Applies the single master changelog to a fresh MariaDB instance and then runs Hibernate schema
 * validation against the JPA entities. If a schema change is ever made to the entities (or directly
 * on an environment database) without a matching changeset, this test fails.
 *
 * <p>The test is skipped unless LIQUIBASE_IT_DB_URL is set, because it needs a real, EMPTY MariaDB
 * database named {@code consultingtypeservice} (the legacy changesets hard-code that schema name).
 * Local run:
 *
 * <pre>
 * docker run -d --name cts-drift-mariadb -p 3314:3306 \
 *   -e MARIADB_ROOT_PASSWORD=root -e MARIADB_DATABASE=consultingtypeservice mariadb:10.11
 * LIQUIBASE_IT_DB_URL='jdbc:mariadb://localhost:3314/consultingtypeservice' \
 *   ./mvnw test -Dtest=LiquibaseSchemaDriftIT
 * </pre>
 */
@EnabledIfEnvironmentVariable(named = "LIQUIBASE_IT_DB_URL", matches = ".+")
class LiquibaseSchemaDriftIT {

  private static final String MASTER_CHANGELOG = "db/changelog/consultingtypeservice-master.xml";

  @Test
  void freshDatabaseMigratedByLiquibase_shouldMatchJpaEntities() throws Exception {
    final var url = System.getenv("LIQUIBASE_IT_DB_URL");
    final var username = envOrDefault("LIQUIBASE_IT_DB_USERNAME", "root");
    final var password = envOrDefault("LIQUIBASE_IT_DB_PASSWORD", "root");

    runLiquibaseUpdate(url, username, password);
    validateJpaMappingAgainstDatabase(url, username, password);
  }

  private void runLiquibaseUpdate(final String url, final String username, final String password)
      throws Exception {
    try (Connection connection = DriverManager.getConnection(url, username, password)) {
      final Database database =
          DatabaseFactory.getInstance()
              .findCorrectDatabaseImplementation(new JdbcConnection(connection));
      try (Liquibase liquibase =
          new Liquibase(MASTER_CHANGELOG, new ClassLoaderResourceAccessor(), database)) {
        // "seed" = the superset used by local/dev; staging/prod run with no contexts
        liquibase.update(new Contexts("seed"), new LabelExpression());
      }
    }
  }

  private void validateJpaMappingAgainstDatabase(
      final String url, final String username, final String password) {
    final StandardServiceRegistry registry =
        new StandardServiceRegistryBuilder()
            .applySetting("hibernate.connection.url", url)
            .applySetting("hibernate.connection.username", username)
            .applySetting("hibernate.connection.password", password)
            .applySetting("hibernate.hbm2ddl.auto", "validate")
            .build();
    try {
      new MetadataSources(registry)
          .addAnnotatedClass(TopicEntity.class)
          .addAnnotatedClass(TopicGroupEntity.class)
          .buildMetadata()
          .buildSessionFactory()
          .close();
    } finally {
      StandardServiceRegistryBuilder.destroy(registry);
    }
  }

  private static String envOrDefault(final String name, final String defaultValue) {
    final var value = System.getenv(name);
    return value == null || value.isBlank() ? defaultValue : value;
  }
}
