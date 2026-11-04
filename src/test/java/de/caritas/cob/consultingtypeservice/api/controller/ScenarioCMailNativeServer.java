package de.caritas.cob.consultingtypeservice.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.repository.ApplicationSettingsRepository;
import de.caritas.cob.consultingtypeservice.api.service.SmtpPasswordEncryptionService;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalFeatureSystemNotificationEmailsEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpFrom;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpHost;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPassword;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPort;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpSecure;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpUsername;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.server.context.WebServerApplicationContext;

/** Test-only real CTS HTTP server with one saved synthetic revision; stdin owns its lifetime. */
public final class ScenarioCMailNativeServer {
  private ScenarioCMailNativeServer() {}

  public static void main(String[] args) throws Exception {
    if (args.length != 2)
      throw new IllegalArgumentException("Fixture and nonsecret readiness path required");
    JsonNode fixture = new ObjectMapper().readTree(Files.readString(Path.of(args[0])));
    if (!"FIVE_ACTORS".equals(fixture.path("identityModel").asText())
        || fixture.path("tasks").size() != 5) {
      throw new IllegalArgumentException("Five native actor fixture required");
    }
    var options = new ArrayList<String>();
    options.add("--spring.profiles.active=testing");
    options.add("--server.address=127.0.0.1");
    options.add("--server.port=0");
    String mongo = System.getenv("ORISO_NATIVE_MAIL_MONGO_URI");
    if (mongo == null || !mongo.startsWith("mongodb://127.0.0.1:")) {
      throw new IllegalArgumentException("Isolated native Mongo URI required");
    }
    options.add("--spring.mongodb.uri=" + mongo);
    options.add(
        "--spring.autoconfigure.exclude=de.flapdoodle.embed.mongo.spring.autoconfigure.EmbeddedMongoAutoConfiguration");
    options.add("--multitenancy.enabled=true");
    options.add(
        "--consulting.types.json.path=src/test/resources/consulting-type-settings-tenant-specific");
    options.add("--feature.multitenancy.with.single.domain.enabled=true");
    options.add("--settings.smtp.password.encryption.secret=synthetic-native-mail-encryption");
    options.add("--settings.smtp.reconcile.url=");
    options.add("--TASK_IDENTITY_MODEL=FIVE_ACTORS");
    options.add("--TASK_IDENTITY_REQUIRED_TASKS=SMTP_SYNC,SYSTEM_EMAIL_DELIVERY");
    options.add("--TASK_IDENTITY_AUDIENCE=consultingtypeservice");
    options.add(
        "--spring.security.oauth2.resourceserver.jwt.issuer-uri="
            + fixture.path("issuer").asText());
    options.add(
        "--spring.security.oauth2.resourceserver.jwt.jwk-set-uri="
            + fixture.path("issuer").asText()
            + "/protocol/openid-connect/certs");
    for (JsonNode actor : fixture.path("tasks")) {
      String prefix = "--IDENTITY_" + actor.path("key").asText();
      options.add(prefix + "_CLIENT_ID=" + actor.path("clientId").asText());
      options.add(prefix + "_SERVICE_SUBJECT=" + actor.path("subject").asText());
    }
    try (var context =
        SpringApplication.run(
            ConsultingTypeServiceApplication.class, options.toArray(String[]::new))) {
      var repository = context.getBean(ApplicationSettingsRepository.class);
      var settings = repository.findAll();
      if (settings.size() != 1)
        throw new IllegalStateException("One saved fixture settings row required");
      var saved = settings.get(0);
      saved.setGlobalSmtpEnabled(new GlobalSmtpEnabled().withValue(true));
      saved.setGlobalFeatureSystemNotificationEmailsEnabled(
          new GlobalFeatureSystemNotificationEmailsEnabled().withValue(true));
      saved.setGlobalSmtpHost(new GlobalSmtpHost().withValue("smtp.fixture.internal"));
      saved.setGlobalSmtpPort(new GlobalSmtpPort().withValue("587"));
      saved.setGlobalSmtpFrom(new GlobalSmtpFrom().withValue("Fixture <mail@fixture.internal>"));
      saved.setGlobalSmtpUsername(new GlobalSmtpUsername().withValue("synthetic-smtp-user"));
      saved.setGlobalSmtpPassword(
          new GlobalSmtpPassword()
              .withValue(
                  context
                      .getBean(SmtpPasswordEncryptionService.class)
                      .encrypt("synthetic-SMTP-password-0001")));
      saved.setGlobalSmtpSecure(new GlobalSmtpSecure().withValue(false));
      saved.setSmtpRevision(7);
      saved.setSmtpPendingRevision(7L);
      saved.setSmtpAppliedRevision(6L);
      saved.setSmtpSyncStatus("PENDING");
      repository.save(saved);
      int port = ((WebServerApplicationContext) context).getWebServer().getPort();
      Files.writeString(Path.of(args[1]), "http://127.0.0.1:" + port);
      // The owning test runner closes stdin, including on an exception.
      System.in.read();
      var after = repository.findAll().get(0);
      if (after.getSmtpPendingRevision() != null
          || !Long.valueOf(7).equals(after.getSmtpAppliedRevision())
          || !"APPLIED".equals(after.getSmtpSyncStatus())) {
        throw new IllegalStateException("Exact saved revision was not acknowledged");
      }
    }
  }
}
