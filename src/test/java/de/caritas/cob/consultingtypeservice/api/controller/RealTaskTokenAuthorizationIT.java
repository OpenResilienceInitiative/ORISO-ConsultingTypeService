package de.caritas.cob.consultingtypeservice.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeRepository;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

/** Native issued bearer passes the real decoder, tenant filter and HTTP controller. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testing")
@EnabledIfEnvironmentVariable(named = "ORISO_TASK_TOKEN_FIXTURE", matches = ".+")
@TestPropertySource(
    properties = {
      "multitenancy.enabled=true",
      "consulting.types.json.path=src/test/resources/consulting-type-settings-tenant-specific",
      "feature.multitenancy.with.single.domain.enabled=true",
      "csrf.header.property=csrfHeader",
      "csrf.cookie.property=csrfCookie",
      "TASK_IDENTITY_AUDIENCE=consultingtypeservice",
      "ORISO_TENANT_CREATION_CONTEXT_KEY=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
      "settings.smtp.password.encryption.secret=synthetic-smtp-encryption-key"
    })
class RealTaskTokenAuthorizationIT {
  private static final ObjectMapper JSON = new ObjectMapper();
  private static final HttpClient HTTP = HttpClient.newHttpClient();
  @LocalServerPort private int port;
  @Autowired private ConsultingTypeRepository types;
  @Autowired private org.springframework.data.mongodb.core.MongoTemplate mongo;

  private static JsonNode fixture() {
    try {
      return JSON.readTree(Files.readString(Path.of(System.getenv("ORISO_TASK_TOKEN_FIXTURE"))));
    } catch (Exception failure) {
      throw new IllegalStateException("Synthetic native task fixture unavailable");
    }
  }

  @DynamicPropertySource
  static void nativeBindings(DynamicPropertyRegistry properties) {
    JsonNode fixture = fixture();
    String issuer = fixture.path("issuer").asText();
    properties.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> issuer);
    properties.add(
        "spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
        () -> issuer + "/protocol/openid-connect/certs");
    for (JsonNode task : fixture.path("tasks")) {
      String prefix = "IDENTITY_" + task.path("key").asText();
      properties.add(prefix + "_CLIENT_ID", () -> task.path("clientId").asText());
      properties.add(prefix + "_SERVICE_SUBJECT", () -> task.path("subject").asText());
    }
  }

  private String token(String key) throws Exception {
    JsonNode fixture = fixture();
    JsonNode task = null;
    for (JsonNode candidate : fixture.path("tasks")) {
      if (key.equals(candidate.path("key").asText())) {
        task = candidate;
      }
    }
    if (task == null) {
      throw new IllegalArgumentException("Unknown synthetic task");
    }
    String form =
        "grant_type=client_credentials&client_id="
            + URLEncoder.encode(task.path("clientId").asText(), StandardCharsets.UTF_8)
            + "&client_secret="
            + URLEncoder.encode(task.path("secret").asText(), StandardCharsets.UTF_8);
    var request =
        HttpRequest.newBuilder(
                URI.create(fixture.path("issuer").asText() + "/protocol/openid-connect/token"))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(form))
            .build();
    var response = HTTP.send(request, HttpResponse.BodyHandlers.ofString());
    assertThat(response.statusCode()).isEqualTo(200);
    return JSON.readTree(response.body()).path("access_token").asText();
  }

  private HttpResponse<String> call(String method, String path, String bearer, String body)
      throws Exception {
    return call(method, path, bearer, body, null);
  }

  private HttpResponse<String> call(
      String method, String path, String bearer, String body, String proof) throws Exception {
    var request =
        HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
            .header("Content-Type", "application/json")
            .header("tenantId", "0")
            .header("csrfHeader", "test")
            .header("Cookie", "csrfCookie=test");
    if (bearer != null) {
      request.header("Authorization", "Bearer " + bearer);
    }
    if (proof != null) {
      request.header("X-ORISO-Tenant-Creation-Context", proof);
    }
    request.method(method, HttpRequest.BodyPublishers.ofString(body == null ? "" : body));
    return HTTP.send(request.build(), HttpResponse.BodyHandlers.ofString());
  }

  @AfterEach
  void cleanup() {
    mongo.remove(
        org.springframework.data.mongodb.core.query.Query.query(
            org.springframework.data.mongodb.core.query.Criteria.where("_id").is("7719")),
        de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim.class);
    types.findAll().stream()
        .filter(
            type ->
                Long.valueOf(7719)
                    .equals(type.getTenantId() == null ? null : type.getTenantId().longValue()))
        .forEach(types::delete);
  }

  @Test
  void actualWizardRequiresCreationProofAndCreatesOnlyOneDefaultType() throws Exception {
    ConsultingTypeDTO dto =
        new org.jeasy.random.EasyRandom()
            .nextObject(ConsultingTypeDTO.class)
            .tenantId(7719)
            .slug("native-wizard-default");
    dto.getRoles().getConsultant().addRoleNames("test", List.of("consultant"));
    String body = JSON.writeValueAsString(dto);
    String wizard = token("CONFIG_WIZARD");
    assertThat(call("POST", "/consultingtypes", wizard, body).statusCode()).isEqualTo(403);
    String proof = proof(7719);
    assertThat(call("POST", "/consultingtypes", wizard, body, proof).statusCode()).isEqualTo(200);
    assertThat(call("POST", "/consultingtypes", wizard, body, proof).statusCode()).isEqualTo(409);
    assertThat(call("PATCH", "/consultingtypes/1", wizard, body, proof).statusCode())
        .isEqualTo(403);
    assertThat(
            types.findAll().stream()
                .filter(type -> Integer.valueOf(7719).equals(type.getTenantId()))
                .count())
        .isEqualTo(1);
  }

  @Test
  void smtpTransportAndSyncHaveReadOnlyAccessAndOtherTasksCannotReadCredentials() throws Exception {
    for (String key : List.of("SYSTEM_EMAIL_DELIVERY", "SMTP_SYNC")) {
      String actor = token(key);
      var read = call("GET", "/settingsadmin/smtp-credentials", actor, null);
      assertThat(read.statusCode()).isEqualTo(200);
      assertThat(read.headers().firstValue("Cache-Control")).contains("no-store");
      assertThat(call("GET", "/settingsadmin/smtp-sync-status", actor, null).statusCode())
          .isEqualTo(403);
      assertThat(call("PATCH", "/settingsadmin", actor, "{}").statusCode()).isEqualTo(403);
    }
    for (String key : List.of("CONFIG_WIZARD", "NOTIFICATION_DISPATCH", "ACCOUNT_MAINTENANCE")) {
      assertThat(call("GET", "/settingsadmin/smtp-credentials", token(key), null).statusCode())
          .isEqualTo(403);
    }
  }

  @Test
  void nativeSignedWrongBindingsAndUnrelatedGrantsCannotReadSmtpSnapshot() throws Exception {
    for (String key : List.of("SYSTEM_EMAIL_DELIVERY", "SMTP_SYNC")) {
      for (String fault : List.of("mixedRoles", "wrongAudience", "wrongSubject")) {
        String actor = fixture().path("variants").path(key).path(fault).asText();
        assertThat(actor).isNotBlank();
        assertThat(call("GET", "/settingsadmin/smtp-credentials", actor, null).statusCode())
            .isEqualTo(403);
      }
    }
  }

  @Test
  void tamperedRealSignatureCannotReadSmtpSnapshot() throws Exception {
    String actor = token("SMTP_SYNC");
    int signature = actor.lastIndexOf('.') + 1;
    String changed =
        actor.substring(0, signature)
            + (actor.charAt(signature) == 'A' ? 'B' : 'A')
            + actor.substring(signature + 1);
    assertThat(call("GET", "/settingsadmin/smtp-credentials", changed, null).statusCode())
        .isEqualTo(401);
  }

  private String proof(long tenant, long now) throws Exception {
    Map<String, Object> claims = new java.util.TreeMap<>();
    JsonNode wizard = null;
    for (JsonNode task : fixture().path("tasks")) {
      if ("CONFIG_WIZARD".equals(task.path("key").asText())) {
        wizard = task;
      }
    }
    if (wizard == null) {
      throw new IllegalStateException("Synthetic Wizard binding missing");
    }
    claims.put("aud", "consultingtypeservice");
    claims.put("azp", wizard.path("clientId").asText());
    claims.put("sub", wizard.path("subject").asText());
    claims.put("exp", now + 60);
    claims.put("iat", now);
    claims.put("iss", "tenantservice");
    claims.put("nonce", java.util.UUID.randomUUID().toString());
    claims.put("tenantId", tenant);
    claims.put("tokenIssuer", fixture().path("issuer").asText());
    claims.put("v", 1);
    String encoded =
        java.util.Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(JSON.writeValueAsBytes(claims));
    var mac = javax.crypto.Mac.getInstance("HmacSHA256");
    mac.init(new javax.crypto.spec.SecretKeySpec(new byte[32], "HmacSHA256"));
    return encoded
        + "."
        + java.util.Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(mac.doFinal(encoded.getBytes(StandardCharsets.US_ASCII)));
  }

  private String proof(long tenant) throws Exception {
    return proof(tenant, java.time.Instant.now().getEpochSecond());
  }
}
