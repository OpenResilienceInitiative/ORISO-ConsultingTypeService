package de.caritas.cob.consultingtypeservice.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.context.WebServerApplicationContext;

/** Disposable real receiver launcher for the cross-repository public HTTP proof test. */
public final class BootstrapV2ReceiverFixture {
  private BootstrapV2ReceiverFixture() {}

  public static void main(String[] arguments) throws Exception {
    JsonNode fixture =
        new ObjectMapper()
            .readTree(Files.readString(Path.of(required("ORISO_TASK_TOKEN_FIXTURE"))));
    if (!"FIVE_ACTORS".equals(fixture.path("identityModel").asText())) {
      throw new IllegalStateException("Bootstrap receiver needs the five-actor synthetic fixture");
    }
    String issuer = fixture.path("issuer").asText();
    System.setProperty("spring.profiles.active", "testing");
    System.setProperty("server.address", "127.0.0.1");
    System.setProperty("server.port", "0");
    // Pin fixture persistence even when the parent shell carries deployment environment values.
    System.setProperty(
        "spring.datasource.url",
        "jdbc:h2:mem:bootstrap-native-cts;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MariaDB;DATABASE_TO_LOWER=TRUE");
    System.setProperty("spring.datasource.username", "sa");
    System.setProperty("spring.datasource.password", "synthetic-context-h2");
    System.setProperty("spring.datasource.driver-class-name", "org.h2.Driver");
    System.setProperty("spring.jpa.properties.hibernate.dialect", "org.hibernate.dialect.H2Dialect");
    System.setProperty("spring.datasource.hikari.maximum-pool-size", "4");
    System.setProperty("spring.datasource.hikari.minimum-idle", "0");
    System.setProperty("spring.liquibase.enabled", "false");
    System.setProperty("settings.smtp.reconcile.url", "");
    System.setProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri", issuer);
    System.setProperty(
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
        issuer + "/protocol/openid-connect/certs");
    System.setProperty("multitenancy.enabled", "true");
    System.setProperty(
        "consulting.types.json.path",
        "src/test/resources/consulting-type-settings-tenant-specific");
    String mongo = required("ORISO_BOOTSTRAP_MONGO_URI");
    if (!mongo.startsWith("mongodb://127.0.0.1:"))
      throw new IllegalStateException("Isolated Mongo URI required");
    System.setProperty("spring.mongodb.uri", mongo);
    System.setProperty(
        "spring.autoconfigure.exclude",
        "de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration");
    System.setProperty("feature.multitenancy.with.single.domain.enabled", "true");
    System.setProperty("csrf.header.property", "csrfHeader");
    System.setProperty("csrf.cookie.property", "csrfCookie");
    System.setProperty("TASK_IDENTITY_MODEL", "FIVE_ACTORS");
    System.setProperty("TASK_IDENTITY_REQUIRED_TASKS", "CONFIG_WIZARD");
    System.setProperty("TASK_IDENTITY_AUDIENCE", "consultingtypeservice");
    System.setProperty("ORISO_TASK_CONTEXT_KEY", required("ORISO_BOOTSTRAP_SHARED_KEY"));
    // Deliberately differs from the Tenant test's v1 key; the shared key is the only common key.
    byte[] oldReceiverKey = new byte[32];
    Arrays.fill(oldReceiverKey, (byte) 2);
    System.setProperty(
        "ORISO_TENANT_CREATION_CONTEXT_KEY", Base64.getEncoder().encodeToString(oldReceiverKey));
    System.setProperty("settings.smtp.password.encryption.secret", "synthetic-bootstrap-smtp-key");
    for (JsonNode actor : fixture.path("tasks")) {
      String prefix = "IDENTITY_" + actor.path("key").asText();
      System.setProperty(prefix + "_CLIENT_ID", actor.path("clientId").asText());
      System.setProperty(prefix + "_SERVICE_SUBJECT", actor.path("subject").asText());
    }
    try (var context = SpringApplication.run(ConsultingTypeServiceApplication.class, arguments)) {
      Files.writeString(
          Path.of(required("ORISO_BOOTSTRAP_RECEIVER_READY")),
          "http://127.0.0.1:" + ((WebServerApplicationContext) context).getWebServer().getPort(),
          StandardCharsets.UTF_8);
      System.in.read();
    }
  }

  private static String required(String name) {
    String value = System.getenv(name);
    if (value == null || value.isBlank()) {
      throw new IllegalStateException("Bootstrap receiver fixture configuration missing: " + name);
    }
    return value;
  }
}
