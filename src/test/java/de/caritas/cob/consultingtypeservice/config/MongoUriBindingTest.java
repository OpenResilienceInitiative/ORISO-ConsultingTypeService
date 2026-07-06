package de.caritas.cob.consultingtypeservice.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.mongodb.autoconfigure.MongoAutoConfiguration;
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * Regression test for the Spring Boot 4 config-binding regression that crashlooped the Pre-Dev
 * consultingtypeservice (MongoTimeoutException / ConnectionRefused to localhost:27017) even though
 * the container had a correct {@code SPRING_DATA_MONGODB_URI} env var pointing at {@code
 * oriso-platform-mongodb.caritas.svc.cluster.local:27017}.
 *
 * <p><b>Root cause:</b> Spring Boot 4.0 renamed the Mongo config property from {@code
 * spring.data.mongodb.uri} to {@code spring.mongodb.uri}. The whole {@code spring.data.mongodb.*}
 * family is deprecation {@code level=error since 4.0.0} and is <b>no longer bound</b>. The
 * service's property files still declared {@code spring.data.mongodb.uri=...}, so {@code
 * MongoProperties.getUri()} bound to {@code null}; {@code
 * PropertiesMongoConnectionDetails.getConnectionString()} then silently falls back to the host/port
 * default ({@code localhost:27017}). Same SB4 config-binding regression family as the AgencyService
 * ehcache break (cf #95).
 *
 * <p>These tests drive the real {@link MongoAutoConfiguration} and assert the resolved {@link
 * MongoConnectionDetails} — the direct analog of the caching IT that caught the ehcache bug.
 */
class MongoUriBindingTest {

  private static final String SENTINEL_HOST_PORT = "mongo-test-host:27017";
  private static final String SENTINEL_URI = "mongodb://" + SENTINEL_HOST_PORT + "/ctsdb";

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(MongoAutoConfiguration.class));

  /**
   * GREEN after fix: the Spring Boot 4 property key {@code spring.mongodb.uri} (the key the
   * property files now use) must produce a MongoClient targeting the configured host, not
   * localhost.
   */
  @Test
  void springBoot4Key_targetsConfiguredHost() {
    this.contextRunner
        .withPropertyValues("spring.mongodb.uri=" + SENTINEL_URI)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              MongoConnectionDetails details = context.getBean(MongoConnectionDetails.class);
              assertThat(details.getConnectionString().getHosts())
                  .as("A properly-set spring.mongodb.uri must target its host, not localhost")
                  .contains(SENTINEL_HOST_PORT)
                  .doesNotContain("localhost:27017");
            });
  }

  /**
   * End-to-end through the exact production placeholder indirection: {@code
   * SPRING_DATA_MONGODB_URI} (the ConfigMap env var, unchanged) feeding {@code
   * spring.mongodb.uri=${SPRING_DATA_MONGODB_URI:}}. Proves the ConfigMap wiring still reaches the
   * driver after the property rename.
   */
  @Test
  void configMapEnvVar_throughPlaceholder_targetsConfiguredHost() {
    this.contextRunner
        .withPropertyValues(
            "SPRING_DATA_MONGODB_URI=" + SENTINEL_URI,
            "spring.mongodb.uri=${SPRING_DATA_MONGODB_URI:}")
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              MongoConnectionDetails details = context.getBean(MongoConnectionDetails.class);
              assertThat(details.getConnectionString().getHosts())
                  .contains(SENTINEL_HOST_PORT)
                  .doesNotContain("localhost:27017");
            });
  }

  /**
   * Documents the regression: the pre-fix key {@code spring.data.mongodb.uri} is dead in Spring
   * Boot 4 — a value set under it is ignored and the driver falls back to localhost:27017. This is
   * the assertion that would have caught the crashloop; it fails (host == localhost) if the
   * property files ever regress back to the old key.
   */
  @Test
  void legacyKey_isIgnoredAndFallsBackToLocalhost() {
    this.contextRunner
        .withPropertyValues("spring.data.mongodb.uri=" + SENTINEL_URI)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              MongoConnectionDetails details = context.getBean(MongoConnectionDetails.class);
              assertThat(details.getConnectionString().getHosts())
                  .as(
                      "The legacy spring.data.mongodb.uri key is unbound in Spring Boot 4; it must"
                          + " NOT be used to configure Mongo (it silently degrades to localhost).")
                  .doesNotContain(SENTINEL_HOST_PORT)
                  .containsExactly("localhost");
            });
  }
}
