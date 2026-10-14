package de.caritas.cob.consultingtypeservice.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@TestPropertySource(
    properties = {
      "spring.profiles.active=testing",
      "feature.multitenancy.with.single.domain.enabled=true",
      "csrf.header.property=csrfHeader",
      "csrf.cookie.property=csrfCookie",
      "TASK_IDENTITY_MODEL=FIVE_ACTORS",
      "TASK_IDENTITY_REQUIRED_TASKS=CONFIG_WIZARD,SMTP_SYNC,SYSTEM_EMAIL_DELIVERY",
      "TASK_IDENTITY_AUDIENCE=consultingtypeservice",
      "IDENTITY_SETUP_CLIENT_ID=backend-setup",
      "IDENTITY_SETUP_SERVICE_SUBJECT=setup-subject",
      "IDENTITY_MAIL_CLIENT_ID=backend-mail",
      "IDENTITY_MAIL_SERVICE_SUBJECT=mail-subject",
      "IDENTITY_ACCOUNT_ADMIN_CLIENT_ID=backend-account-admin",
      "IDENTITY_ACCOUNT_ADMIN_SERVICE_SUBJECT=account-admin-subject",
      "ORISO_TENANT_CREATION_CONTEXT_KEY=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
      "settings.smtp.password.encryption.secret=test-only-smtp-secret"
    })
class GroupedTaskServiceIdentityAuthorizationIT {
  private static final List<String> MAIL_ROLES =
      List.of(
          "smtp-sync", "system-email-delivery", "notification-dispatch", "notifications-technical");
  private static final List<String> SETUP_ROLES =
      List.of("config-wizard", "invitation-reservations", "consultant-import");
  private static final com.fasterxml.jackson.databind.ObjectMapper JSON =
      new com.fasterxml.jackson.databind.ObjectMapper();
  @Autowired private WebApplicationContext context;

  @Autowired
  @Qualifier("jwtAuthenticationConverter")
  private Converter<Jwt, AbstractAuthenticationToken> converter;

  @Autowired
  private de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeRepository types;

  @Autowired
  private de.caritas.cob.consultingtypeservice.api.repository.ApplicationSettingsRepository
      settings;

  @org.springframework.test.context.bean.override.mockito.MockitoSpyBean
  private org.springframework.data.mongodb.core.MongoTemplate mongo;

  private MockMvc mvc;

  @BeforeEach
  void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    cleanup();
  }

  @AfterEach
  void cleanup() {
    types.findAll().stream()
        .filter(type -> Integer.valueOf(9719).equals(type.getTenantId()))
        .forEach(types::delete);
    mongo.remove(
        org.springframework.data.mongodb.core.query.Query.query(
            org.springframework.data.mongodb.core.query.Criteria.where("_id").is("9719")),
        "tenant_bootstrap_claims");
  }

  enum LostDatabaseResponse {
    CLAIM_INSERT,
    BEFORE_TYPE_INSERT,
    TYPE_INSERT,
    TYPE_READBACK
  }

  /** One-shot faults surround real Mongo operations; the HTTP retry cannot see test-side state. */
  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.EnumSource(LostDatabaseResponse.class)
  void samePayloadRecoversAfterLostResponsesOrAStoppedWrite(LostDatabaseResponse fault)
      throws Exception {
    var failed = new java.util.concurrent.atomic.AtomicBoolean();
    var written = new java.util.concurrent.atomic.AtomicBoolean();
    org.mockito.stubbing.Answer<Object> databaseBoundary1 =
        call -> {
          Object entity = call.getArgument(0);
          if (entity instanceof de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity
              && fault == LostDatabaseResponse.BEFORE_TYPE_INSERT
              && !failed.getAndSet(true)) {
            throw new org.springframework.dao.DataAccessResourceFailureException(
                "Synthetic stopped write");
          }
          Object result = call.callRealMethod();
          if (entity
              instanceof de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity) {
            written.set(true);
          }
          if ((entity instanceof de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim
                  && fault == LostDatabaseResponse.CLAIM_INSERT)
              || (entity
                      instanceof de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity
                  && fault == LostDatabaseResponse.TYPE_INSERT)) {
            if (!failed.getAndSet(true)) {
              throw new org.springframework.dao.DataAccessResourceFailureException(
                  "Synthetic lost insert response");
            }
          }
          return result;
        };
    doAnswer(databaseBoundary1).when(mongo).insert(org.mockito.ArgumentMatchers.any(Object.class));
    org.mockito.stubbing.Answer<Object> databaseBoundary2 =
        call -> {
          if (written.get()
              && fault == LostDatabaseResponse.TYPE_READBACK
              && !failed.getAndSet(true)) {
            throw new org.springframework.dao.DataAccessResourceFailureException(
                "Synthetic lost readback response");
          }
          return call.callRealMethod();
        };
    doAnswer(databaseBoundary2)
        .when(mongo)
        .findById(
            org.mockito.ArgumentMatchers.any(),
            eq(de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity.class));
    var setup = actor("setup-subject", "backend-setup", SETUP_ROLES);
    String body = payload("recoverable-default");
    mvc.perform(create(setup, body, creationProof(9719)))
        .andExpect(status().isInternalServerError());
    String recovered =
        mvc.perform(create(setup, body, creationProof(9719)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    mvc.perform(create(setup, body, creationProof(9719)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(JSON.readTree(recovered).path("id").asInt()));
    assertOnePublicType();
  }

  @Test
  void changedPayloadAndForeignExistingTypeCannotTakeOverABootstrapClaim() throws Exception {
    var setup = actor("setup-subject", "backend-setup", SETUP_ROLES);
    String body = payload("owned-default");
    mvc.perform(create(setup, body, creationProof(9719)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    var changed = JSON.readTree(body);
    ((com.fasterxml.jackson.databind.node.ObjectNode) changed).put("slug", "different-default");
    mvc.perform(create(setup, JSON.writeValueAsString(changed), creationProof(9719)))
        .andExpect(status().isConflict());
    assertOnePublicType();
    cleanup();
    // An ordinary human write has no bootstrap provenance, even when its payload is identical.
    var human = actor("human-subject", "human-admin", List.of("tenant-admin"));
    mvc.perform(create(human, body, creationProof(9719))).andExpect(status().isOk());
    mvc.perform(create(setup, body, creationProof(9719))).andExpect(status().isConflict());
    assertOnePublicType();
  }

  @Test
  void foreignTypeAtReservedIdIsNeverOverwrittenAfterAnInterruptedBootstrap() throws Exception {
    var first = new java.util.concurrent.atomic.AtomicBoolean(true);
    org.mockito.stubbing.Answer<Object> databaseBoundary3 =
        call -> {
          if (first.getAndSet(false)) {
            throw new org.springframework.dao.DataAccessResourceFailureException(
                "Synthetic stopped write");
          }
          return call.callRealMethod();
        };
    doAnswer(databaseBoundary3)
        .when(mongo)
        .insert(
            org.mockito.ArgumentMatchers.any(
                de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity.class));
    var setup = actor("setup-subject", "backend-setup", SETUP_ROLES);
    String body = payload("reserved-default");
    mvc.perform(create(setup, body, creationProof(9719)))
        .andExpect(status().isInternalServerError());
    var human = actor("human-subject", "human-admin", List.of("tenant-admin"));
    String foreign =
        mvc.perform(create(human, body, creationProof(9719)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    mvc.perform(create(setup, body, creationProof(9719))).andExpect(status().isConflict());
    String reread =
        mvc.perform(get("/consultingtypes/" + JSON.readTree(foreign).path("id").asInt() + "/full"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    assertThat(JSON.readTree(reread)).isEqualTo(JSON.readTree(foreign));
    assertOnePublicType();
  }

  @Test
  void groupedMailAcknowledgesOnlyTheExactSavedRevisionAndCannotWriteSettings() throws Exception {
    var entity = settings.findAll().get(0);
    entity.setSmtpRevision(7);
    entity.setSmtpPendingRevision(7L);
    entity.setSmtpAppliedRevision(6L);
    entity.setSmtpSyncStatus(
        de.caritas.cob.consultingtypeservice.api.service.SmtpSynchronizationStatus.PENDING);
    settings.save(entity);
    var mail = actor("mail-subject", "backend-mail", MAIL_ROLES);
    for (long revision : List.of(6L, 8L)) {
      mvc.perform(ack(mail, revision)).andExpect(status().isConflict());
    }
    mvc.perform(ack(mail, 7)).andExpect(status().isNoContent());
    mvc.perform(ack(mail, 7)).andExpect(status().isNoContent());
    var human = actor("human-subject", "human-admin", List.of("tenant-admin"));
    mvc.perform(get("/settingsadmin/smtp-sync-status").with(human))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.appliedRevision").value(7));
    mvc.perform(ack(human, 7)).andExpect(status().isForbidden());
    mvc.perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch(
                    "/settingsadmin")
                .with(mail)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("{}")
                .header("csrfHeader", "test")
                .cookie(new jakarta.servlet.http.Cookie("csrfCookie", "test")))
        .andExpect(status().isForbidden());
  }

  private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder ack(
      RequestPostProcessor caller, long revision) {
    return post("/settingsadmin/smtp-sync-acknowledgement")
        .with(caller)
        .header("csrfHeader", "test")
        .cookie(new jakarta.servlet.http.Cookie("csrfCookie", "test"))
        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
        .content("{\"revision\":" + revision + ",\"status\":\"APPLIED\"}");
  }

  private String payload(String slug) throws Exception {
    var dto =
        new org.jeasy.random.EasyRandom()
            .nextObject(de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO.class)
            .tenantId(9719)
            .slug(slug);
    dto.getRoles().getConsultant().addRoleNames("test", List.of("consultant"));
    return JSON.writeValueAsString(dto);
  }

  private void assertOnePublicType() throws Exception {
    String basic =
        mvc.perform(get("/consultingtypes/basic"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    long count =
        java.util.stream.StreamSupport.stream(JSON.readTree(basic).spliterator(), false)
            .filter(type -> type.path("tenantId").asInt() == 9719)
            .count();
    assertThat(count).isEqualTo(1);
  }

  @Test
  void concurrentSamePayloadRecoversAResultCommittedAfterAnEarlierEmptyClaimRead()
      throws Exception {
    var setup = actor("setup-subject", "backend-setup", SETUP_ROLES);
    var dto =
        new org.jeasy.random.EasyRandom()
            .nextObject(de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO.class)
            .tenantId(9719)
            .slug("concurrent-grouped-default");
    dto.getRoles().getConsultant().addRoleNames("test", List.of("consultant"));
    String body = JSON.writeValueAsString(dto);
    var paused = new java.util.concurrent.CountDownLatch(1);
    var release = new java.util.concurrent.CountDownLatch(1);
    var first = new java.util.concurrent.atomic.AtomicBoolean(true);
    org.mockito.stubbing.Answer<Object> databaseBoundary4 =
        call -> {
          Object result = call.callRealMethod();
          if (result == null && first.getAndSet(false)) {
            paused.countDown();
            assertThat(release.await(10, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
          }
          return result;
        };
    doAnswer(databaseBoundary4)
        .when(mongo)
        .findById(
            eq("9719"),
            eq(de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim.class));
    var executor = java.util.concurrent.Executors.newSingleThreadExecutor();
    try {
      var delayed =
          executor.submit(
              () ->
                  mvc.perform(create(setup, body, creationProof(9719))).andReturn().getResponse());
      assertThat(paused.await(10, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
      String created =
          mvc.perform(create(setup, body, creationProof(9719)))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();
      release.countDown();
      var recovered = delayed.get(10, java.util.concurrent.TimeUnit.SECONDS);
      assertThat(recovered.getStatus()).isEqualTo(200);
      assertThat(JSON.readTree(recovered.getContentAsString()).path("id"))
          .isEqualTo(JSON.readTree(created).path("id"));
    } finally {
      release.countDown();
      executor.shutdownNow();
      doCallRealMethod()
          .when(mongo)
          .findById(
              eq("9719"),
              eq(de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim.class));
    }
  }

  @Test
  void setupCreatesOnlyTheSignedTenantAndRecoversTheSamePayloadIdempotently() throws Exception {
    var setup = actor("setup-subject", "backend-setup", SETUP_ROLES);
    var dto =
        new org.jeasy.random.EasyRandom()
            .nextObject(de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO.class)
            .tenantId(9719)
            .slug("grouped-default-type");
    dto.getRoles().getConsultant().addRoleNames("test", List.of("consultant"));
    String body = JSON.writeValueAsString(dto);
    mvc.perform(
            post("/consultingtypes")
                .with(setup)
                .header("csrfHeader", "test")
                .cookie(new jakarta.servlet.http.Cookie("csrfCookie", "test"))
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isForbidden());
    mvc.perform(create(setup, body, creationProof(9720))).andExpect(status().isForbidden());
    String response =
        mvc.perform(create(setup, body, creationProof(9719)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tenantId").value(9719))
            .andReturn()
            .getResponse()
            .getContentAsString();
    int resultId = JSON.readTree(response).path("id").asInt();
    mvc.perform(create(setup, body, creationProof(9719)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(resultId));
  }

  private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder create(
      RequestPostProcessor caller, String body, String proof) {
    return post("/consultingtypes")
        .with(caller)
        .header("csrfHeader", "test")
        .cookie(new jakarta.servlet.http.Cookie("csrfCookie", "test"))
        .header("X-ORISO-Tenant-Creation-Context", proof)
        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
        .content(body);
  }

  private String creationProof(long tenant) throws Exception {
    long now = Instant.now().getEpochSecond();
    var claims = new java.util.TreeMap<String, Object>();
    claims.put("aud", "consultingtypeservice");
    claims.put("azp", "backend-setup");
    claims.put("exp", now + 60);
    claims.put("iat", now);
    claims.put("iss", "tenantservice");
    claims.put("nonce", java.util.UUID.randomUUID().toString());
    claims.put("sub", "setup-subject");
    claims.put("tenantId", tenant);
    claims.put("tokenIssuer", "https://identity.example/realms/test");
    claims.put("v", 1);
    String payload =
        java.util.Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(JSON.writeValueAsBytes(claims));
    var mac = javax.crypto.Mac.getInstance("HmacSHA256");
    mac.init(new javax.crypto.spec.SecretKeySpec(new byte[32], "HmacSHA256"));
    return payload
        + "."
        + java.util.Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(
                mac.doFinal(payload.getBytes(java.nio.charset.StandardCharsets.US_ASCII)));
  }

  @Test
  void configuredActorsCannotAcquirePlatformAuthorityFromInheritedHumanRoles() throws Exception {
    for (var caller :
        List.of(
            actor(
                "mail-subject",
                "backend-mail",
                List.of(
                    "smtp-sync",
                    "system-email-delivery",
                    "notification-dispatch",
                    "notifications-technical",
                    "tenant-admin")),
            actor("account-admin-subject", "backend-account-admin", List.of("tenant-admin")))) {
      mvc.perform(get("/settingsadmin/smtp-sync-status").with(caller))
          .andExpect(status().isForbidden());
      mvc.perform(get("/settingsadmin/smtp-credentials").with(caller))
          .andExpect(status().isForbidden());
    }
  }

  @Test
  void groupedMailReadsOnlyTheNoStoreSmtpSnapshot() throws Exception {
    var mail = actor("mail-subject", "backend-mail", MAIL_ROLES);
    mvc.perform(get("/settingsadmin/smtp-credentials").with(mail))
        .andExpect(status().isOk())
        .andExpect(header().string("Cache-Control", "no-store"));
    mvc.perform(get("/settingsadmin/smtp-sync-status").with(mail))
        .andExpect(status().isForbidden());
  }

  private RequestPostProcessor actor(String subject, String client, List<String> roles) {
    return jwt()
        .jwt(
            token ->
                token
                    .subject(subject)
                    .claim("azp", client)
                    .issuer("https://identity.example/realms/test")
                    .audience(List.of("consultingtypeservice"))
                    .expiresAt(Instant.now().plusSeconds(60))
                    .claim("tenantId", 0L)
                    .claim("realm_access", Map.of("roles", roles)))
        .authorities(token -> converter.convert(token).getAuthorities());
  }
}
