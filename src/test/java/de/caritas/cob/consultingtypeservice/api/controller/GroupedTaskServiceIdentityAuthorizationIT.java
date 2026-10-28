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
      "ORISO_TASK_CONTEXT_KEY=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
      "settings.smtp.password.encryption.secret=test-only-smtp-secret"
    })
class GroupedTaskServiceIdentityAuthorizationIT {
  private static final List<String> MAIL_ROLES =
      List.of("system-email-delivery", "notification-dispatch", "notifications-technical");
  private static final List<String> SETUP_ROLES =
      List.of("config-wizard", "invitation-reservations", "consultant-import", "smtp-sync");
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
  private final java.util.concurrent.ConcurrentMap<Long, String> bootstrapNonces =
      new java.util.concurrent.ConcurrentHashMap<>();

  @BeforeEach
  void setup() {
    mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    cleanup();
    var fixture = actorJwt("setup-subject", "backend-setup", SETUP_ROLES);
    var identity =
        context.getBean(
            de.caritas.cob.consultingtypeservice.config.security.TaskServiceIdentity.class);
    assertThat(
            identity.allows(
                new org.springframework.security.oauth2.server.resource.authentication
                    .JwtAuthenticationToken(fixture, List.of()),
                "CONFIG_WIZARD"))
        .isTrue();
  }

  @AfterEach
  void cleanup() {
    types.findAll().stream()
        .filter(
            type ->
                Integer.valueOf(9719).equals(type.getTenantId())
                    || Integer.valueOf(9720).equals(type.getTenantId()))
        .forEach(types::delete);
    mongo.remove(
        org.springframework.data.mongodb.core.query.Query.query(
            new org.springframework.data.mongodb.core.query.Criteria()
                .orOperator(
                    org.springframework.data.mongodb.core.query.Criteria.where("_id")
                        .in("9719", "9720"),
                    org.springframework.data.mongodb.core.query.Criteria.where("tenantId")
                        .in("9719", "9720"))),
        "tenant_bootstrap_claims");
    mongo.dropCollection("consulting_type_id_sequences");
  }

  enum InterveningCreator {
    SETUP,
    HUMAN
  }

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.EnumSource(InterveningCreator.class)
  void interruptedTenantRecoversAfterADifferentTenantCompletes(InterveningCreator creator)
      throws Exception {
    var stopped = new java.util.concurrent.atomic.AtomicBoolean();
    org.mockito.stubbing.Answer<Object> beforeResult =
        call -> {
          var entity =
              (de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity)
                  call.getArgument(0);
          if (Integer.valueOf(9719).equals(entity.getTenantId()) && !stopped.getAndSet(true)) {
            throw new org.springframework.dao.DataAccessResourceFailureException(
                "Synthetic interruption after claim, before result");
          }
          return call.callRealMethod();
        };
    doAnswer(beforeResult)
        .when(mongo)
        .insert(
            org.mockito.ArgumentMatchers.any(
                de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity.class));
    var setup = actor("setup-subject", "backend-setup", SETUP_ROLES);
    String firstBody = payload("interrupted-tenant", 9719);
    String secondBody = payload("completed-other-tenant", 9720);
    mvc.perform(create(setup, firstBody, 9719L)).andExpect(status().isInternalServerError());
    var other =
        creator == InterveningCreator.SETUP
            ? setup
            : actor("human-subject", "human-admin", List.of("tenant-admin"));
    String second =
        mvc.perform(create(other, secondBody, 9720L))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    String first =
        mvc.perform(create(setup, firstBody, 9719L))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    assertThat(JSON.readTree(first).path("id")).isNotEqualTo(JSON.readTree(second).path("id"));
    mvc.perform(create(setup, firstBody, 9719L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(JSON.readTree(first).path("id").asInt()));
    mvc.perform(get("/consultingtypes/" + JSON.readTree(second).path("id").asInt() + "/full"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tenantId").value(9720));
  }

  @Test
  void concurrentDifferentTenantsReserveDistinctRecoverableResults() throws Exception {
    var claimed = new java.util.concurrent.CountDownLatch(2);
    var release = new java.util.concurrent.CountDownLatch(1);
    org.mockito.stubbing.Answer<Object> afterClaim =
        call -> {
          Object result = call.callRealMethod();
          claimed.countDown();
          assertThat(release.await(20, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
          return result;
        };
    doAnswer(afterClaim)
        .when(mongo)
        .insert(
            org.mockito.ArgumentMatchers.any(
                de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim.class));
    var setup = actor("setup-subject", "backend-setup", SETUP_ROLES);
    String firstBody = payload("parallel-first-tenant", 9719);
    String secondBody = payload("parallel-second-tenant", 9720);
    var executor = java.util.concurrent.Executors.newFixedThreadPool(2);
    try {
      var first =
          executor.submit(
              () -> mvc.perform(create(setup, firstBody, 9719L)).andReturn().getResponse());
      var second =
          executor.submit(
              () -> mvc.perform(create(setup, secondBody, 9720L)).andReturn().getResponse());
      assertThat(claimed.await(20, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
      release.countDown();
      var firstResult = first.get(20, java.util.concurrent.TimeUnit.SECONDS);
      var secondResult = second.get(20, java.util.concurrent.TimeUnit.SECONDS);
      assertThat(firstResult.getStatus()).isEqualTo(200);
      assertThat(secondResult.getStatus()).isEqualTo(200);
      int firstId = JSON.readTree(firstResult.getContentAsString()).path("id").asInt();
      int secondId = JSON.readTree(secondResult.getContentAsString()).path("id").asInt();
      assertThat(firstId).isNotEqualTo(secondId);
      mvc.perform(create(setup, firstBody, 9719L))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(firstId));
      mvc.perform(create(setup, secondBody, 9720L))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(secondId));
    } finally {
      release.countDown();
      executor.shutdownNow();
      doCallRealMethod()
          .when(mongo)
          .insert(
              org.mockito.ArgumentMatchers.any(
                  de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim.class));
    }
  }

  @Test
  void bothOrdinaryRepositoriesInitializeAndReserveOneGlobalSequenceConcurrently()
      throws Exception {
    var start = new java.util.concurrent.CountDownLatch(1);
    var executor = java.util.concurrent.Executors.newFixedThreadPool(8);
    try {
      var futures = new java.util.ArrayList<java.util.concurrent.Future<Integer>>();
      for (int index = 0; index < 8; index++) {
        String name = index % 2 == 0 ? "tenantAware" : "tenantUnaware";
        var repository = ordinaryRepository(name);
        futures.add(
            executor.submit(
                () -> {
                  assertThat(start.await(20, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
                  return repository.getNextId();
                }));
      }
      start.countDown();
      var reserved = new java.util.TreeSet<Integer>();
      for (var future : futures) {
        reserved.add(future.get(20, java.util.concurrent.TimeUnit.SECONDS));
      }
      assertThat(reserved).hasSize(8);
      assertThat(reserved.last() - reserved.first()).isEqualTo(7);
      assertThat(mongo.getCollection("consulting_type_id_sequences").countDocuments()).isEqualTo(1);
    } finally {
      start.countDown();
      executor.shutdownNow();
    }
  }

  @Test
  void initialAllocationFloorIncludesStoredTypesAndPendingClaimsAndKeepsGaps() throws Exception {
    mongo.insert(manualType(payload("pre-existing-high-type", 9720), 1000000));
    mongo.insert(
        new de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim(
            "9719", "existing-attempt", 1000007, "existing-digest", "owner", "subject", "issuer"));
    var repository =
        context.getBean(
            "tenantUnaware",
            de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeRepositoryService
                .class);
    assertThat(repository.getNextId()).isEqualTo(1000008);
    // No type is inserted for this reservation: the next caller must preserve that gap.
    assertThat(repository.getNextId()).isEqualTo(1000009);
    assertThat(
            mongo.findById(
                1000007, de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity.class))
        .isNull();
  }

  @Test
  void lostAllocationResponseLeavesAGapAndPublicRetryCreatesOneOwnedResult() throws Exception {
    var failed = new java.util.concurrent.atomic.AtomicBoolean();
    var abandoned = new java.util.concurrent.atomic.AtomicInteger(-1);
    org.mockito.stubbing.Answer<Object> afterAllocation =
        call -> {
          org.bson.Document reserved = (org.bson.Document) call.callRealMethod();
          if (reserved != null && !failed.getAndSet(true)) {
            abandoned.set(((Number) reserved.get("lastId")).intValue());
            throw new org.springframework.dao.DataAccessResourceFailureException(
                "Synthetic lost allocation response");
          }
          return reserved;
        };
    doAnswer(afterAllocation)
        .when(mongo)
        .findAndModify(
            org.mockito.ArgumentMatchers.any(
                org.springframework.data.mongodb.core.query.Query.class),
            org.mockito.ArgumentMatchers.any(
                org.springframework.data.mongodb.core.query.Update.class),
            org.mockito.ArgumentMatchers.any(
                org.springframework.data.mongodb.core.FindAndModifyOptions.class),
            eq(org.bson.Document.class),
            eq("consulting_type_id_sequences"));
    var setup = actor("setup-subject", "backend-setup", SETUP_ROLES);
    String body = payload("lost-global-reservation");
    mvc.perform(create(setup, body, 9719L)).andExpect(status().isInternalServerError());
    String created =
        mvc.perform(create(setup, body, 9719L))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    int id = JSON.readTree(created).path("id").asInt();
    assertThat(id).isGreaterThan(abandoned.get());
    mvc.perform(create(setup, body, 9719L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id));
    assertOnePublicType();
  }

  @Test
  void lastIntegerIdIsUsableButExhaustionNeverWrapsOrCreatesAnotherCounter() throws Exception {
    mongo.insert(
        new org.bson.Document("_id", "consulting-types")
            .append("lastId", (long) Integer.MAX_VALUE - 1),
        "consulting_type_id_sequences");
    var setup = actor("setup-subject", "backend-setup", SETUP_ROLES);
    mvc.perform(create(setup, payload("last-integer-type"), 9719L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(Integer.MAX_VALUE));
    for (int retry = 0; retry < 2; retry++) {
      mvc.perform(create(setup, payload("exhausted-other-tenant", 9720), 9720L))
          .andExpect(status().isInternalServerError());
    }
    var counter =
        mongo.findById("consulting-types", org.bson.Document.class, "consulting_type_id_sequences");
    assertThat(counter.getLong("lastId")).isEqualTo((long) Integer.MAX_VALUE);
    assertThat(mongo.getCollection("consulting_type_id_sequences").countDocuments()).isEqualTo(1);
  }

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.ValueSource(strings = {"tenantAware", "tenantUnaware"})
  void explicitConcurrentForeignCreationIsRefusedWithoutOverwritingItsData(String name)
      throws Exception {
    var injected = new java.util.concurrent.atomic.AtomicBoolean();
    org.mockito.stubbing.Answer<Object> beforeExplicitInsert =
        call -> {
          var incoming =
              (de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity)
                  call.getArgument(0);
          if (!injected.getAndSet(true)) {
            mongo.insert(manualType(payload("foreign-explicit-type", 9720), incoming.getId()));
          }
          return call.callRealMethod();
        };
    doAnswer(beforeExplicitInsert)
        .when(mongo)
        .insert(
            org.mockito.ArgumentMatchers.any(
                de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity.class),
            eq("consulting_types"));
    var repository = ordinaryRepository(name);
    assertThat(repository.addConsultingType(manualType(payload("refused-explicit-type"), 900001)))
        .isEmpty();
    mvc.perform(get("/consultingtypes/900001/full"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tenantId").value(9720))
        .andExpect(jsonPath("$.slug").value("foreign-explicit-type"));
  }

  private de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity manualType(
      String body, int id) throws Exception {
    var dto =
        JSON.readValue(
            body, de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO.class);
    var typeConverter =
        context.getBean(
            de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeConverter.class);
    var entity = new de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity();
    org.springframework.beans.BeanUtils.copyProperties(typeConverter.convert(dto), entity);
    entity.setId(id);
    return entity;
  }

  private de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeRepositoryService
      ordinaryRepository(String name) {
    if ("tenantUnaware".equals(name)) {
      return context.getBean(
          name,
          de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeRepositoryService
              .class);
    }
    // The testing profile disables multitenancy. Exercise the other implementation with
    // a real Spring Data Mongo proxy without changing the HTTP security profile.
    var repository =
        new org.springframework.data.mongodb.repository.support.MongoRepositoryFactory(mongo)
            .getRepository(
                de.caritas.cob.consultingtypeservice.api.consultingtypes
                    .ConsultingTypeTenantAwareRepository.class);
    return new de.caritas.cob.consultingtypeservice.api.consultingtypes
        .ConsultingTypeMongoTenantAwareRepositoryService(
        repository,
        context.getBean(
            de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeConverter.class),
        context.getBean(
            de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeIdAllocator
                .class));
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
    mvc.perform(create(setup, body, 9719L)).andExpect(status().isInternalServerError());
    String recovered =
        mvc.perform(create(setup, body, 9719L))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    mvc.perform(create(setup, body, 9719L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(JSON.readTree(recovered).path("id").asInt()));
    assertOnePublicType();
  }

  @Test
  void changedPayloadAndForeignExistingTypeCannotTakeOverABootstrapClaim() throws Exception {
    var setup = actor("setup-subject", "backend-setup", SETUP_ROLES);
    String body = payload("owned-default");
    mvc.perform(create(setup, body, 9719L))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    var changed = JSON.readTree(body);
    ((com.fasterxml.jackson.databind.node.ObjectNode) changed).put("slug", "different-default");
    mvc.perform(create(setup, JSON.writeValueAsString(changed), 9719L))
        .andExpect(status().isConflict());
    assertOnePublicType();
    cleanup();
    // An ordinary human write has no bootstrap provenance, even when its payload is identical.
    var human = actor("human-subject", "human-admin", List.of("tenant-admin"));
    mvc.perform(create(human, body, 9719L)).andExpect(status().isOk());
    mvc.perform(create(setup, body, 9719L)).andExpect(status().isConflict());
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
    mvc.perform(create(setup, body, 9719L)).andExpect(status().isInternalServerError());
    var claim =
        mongo.findById(
            claimKey(9719L),
            de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim.class);
    mongo.insert(manualType(payload("explicit-foreign-reserved-id", 9720), claim.resultId()));
    String foreign =
        mvc.perform(get("/consultingtypes/" + claim.resultId() + "/full"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    mvc.perform(create(setup, body, 9719L)).andExpect(status().isConflict());
    String reread =
        mvc.perform(get("/consultingtypes/" + JSON.readTree(foreign).path("id").asInt() + "/full"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    assertThat(JSON.readTree(reread)).isEqualTo(JSON.readTree(foreign));
    mvc.perform(get("/consultingtypes/basic"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[?(@.tenantId == 9719)]").isEmpty());
  }

  @Test
  void groupedSetupAcknowledgesOnlyTheExactSavedRevisionAndCannotWriteSettings() throws Exception {
    var entity = settings.findAll().get(0);
    entity.setSmtpRevision(7);
    entity.setSmtpPendingRevision(7L);
    entity.setSmtpAppliedRevision(6L);
    entity.setSmtpSyncStatus(
        de.caritas.cob.consultingtypeservice.api.service.SmtpSynchronizationStatus.PENDING);
    settings.save(entity);
    var mail = actor("setup-subject", "backend-setup", SETUP_ROLES);
    mvc.perform(ack(actor("mail-subject", "backend-mail", MAIL_ROLES), 7))
        .andExpect(status().isForbidden());
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
                .with(actor("mail-subject", "backend-mail", MAIL_ROLES))
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
    return payload(slug, 9719);
  }

  private String payload(String slug, int tenantId) throws Exception {
    try (var source =
        getClass().getResourceAsStream("/bootstrap-v2-complete-consulting-type.json")) {
      assertThat(source != null).isTrue();
      var dto =
          JSON.readValue(
                  source, de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO.class)
              .tenantId(tenantId)
              .slug(slug);
      context
          .getBean(
              de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeValidator
                  .class)
          .validateBootstrapCommand(dto);
      return JSON.writeValueAsString(dto);
    }
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
    String body = payload("concurrent-grouped-default");
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
            eq(claimKey(9719L)),
            eq(de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim.class));
    var executor = java.util.concurrent.Executors.newSingleThreadExecutor();
    try {
      var delayed =
          executor.submit(() -> mvc.perform(create(setup, body, 9719L)).andReturn().getResponse());
      assertThat(paused.await(10, java.util.concurrent.TimeUnit.SECONDS)).isTrue();
      String created =
          mvc.perform(create(setup, body, 9719L))
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
              eq(claimKey(9719L)),
              eq(de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim.class));
    }
  }

  @Test
  void setupCreatesOnlyTheSignedTenantAndRecoversTheSamePayloadIdempotently() throws Exception {
    var setup = actor("setup-subject", "backend-setup", SETUP_ROLES);
    String body = payload("grouped-default-type");
    mvc.perform(
            post("/consultingtypes")
                .with(setup)
                .header("csrfHeader", "test")
                .cookie(new jakarta.servlet.http.Cookie("csrfCookie", "test"))
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content(body))
        .andExpect(status().isForbidden());
    mvc.perform(create(setup, body, 9720L)).andExpect(status().isForbidden());
    String response =
        mvc.perform(create(setup, body, 9719L))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tenantId").value(9719))
            .andReturn()
            .getResponse()
            .getContentAsString();
    int resultId = JSON.readTree(response).path("id").asInt();
    mvc.perform(create(setup, body, 9719L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(resultId));
  }

  private org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder create(
      RequestPostProcessor caller, String body, long signedTenant) throws Exception {
    return post("/consultingtypes")
        .with(caller)
        .header("csrfHeader", "test")
        .cookie(new jakarta.servlet.http.Cookie("csrfCookie", "test"))
        .header("X-ORISO-Tenant-Creation-Context", creationProof(signedTenant, body))
        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
        .content(body);
  }

  private String claimKey(long tenant) {
    return tenant + ":" + nonce(tenant);
  }

  private String nonce(long tenant) {
    return bootstrapNonces.computeIfAbsent(
        tenant, ignored -> java.util.UUID.randomUUID().toString().replace("-", ""));
  }

  private String creationProof(long tenant, String body) throws Exception {
    long now = Instant.now().getEpochSecond();
    var dto =
        JSON.readValue(
            body, de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO.class);
    var claims = new java.util.TreeMap<String, Object>();
    claims.put("v", 2);
    claims.put("iss", "tenantservice");
    claims.put("aud", "consultingtypeservice");
    claims.put("actor", "SETUP");
    claims.put("purpose", "CONFIG_WIZARD");
    claims.put("operation", "tenant.consulting-type.bootstrap");
    claims.put("method", "POST");
    claims.put("target", "tenant/" + tenant + "/consulting-type/default");
    claims.put("azp", "backend-setup");
    claims.put("sub", "setup-subject");
    claims.put("tokenIssuer", "https://identity.example/realms/test");
    claims.put("tenantId", tenant);
    claims.put(
        "payloadDigest",
        java.util.HexFormat.of().formatHex(mac("payload\n", canonical(JSON.valueToTree(dto)))));
    claims.put("nonce", nonce(tenant));
    claims.put("iat", now);
    claims.put("exp", now + 60);
    String encoded =
        java.util.Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(canonical(JSON.valueToTree(claims)));
    return encoded
        + "."
        + java.util.Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(
                mac("proof\n", encoded.getBytes(java.nio.charset.StandardCharsets.US_ASCII)));
  }

  private byte[] canonical(com.fasterxml.jackson.databind.JsonNode value) throws Exception {
    if (value.isObject()) {
      var names = new java.util.ArrayList<String>();
      value.fieldNames().forEachRemaining(names::add);
      names.sort(String::compareTo);
      var fields = new java.util.ArrayList<String>();
      for (String name : names)
        if (!value.get(name).isNull()) {
          fields.add(
              JSON.writeValueAsString(name)
                  + ":"
                  + new String(
                      canonical(value.get(name)), java.nio.charset.StandardCharsets.UTF_8));
        }
      return ("{" + String.join(",", fields) + "}")
          .getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
    if (value.isArray()) {
      var items = new java.util.ArrayList<String>();
      for (var item : value)
        items.add(new String(canonical(item), java.nio.charset.StandardCharsets.UTF_8));
      return ("[" + String.join(",", items) + "]")
          .getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
    return (value.isNumber()
            ? value.decimalValue().stripTrailingZeros().toPlainString()
            : JSON.writeValueAsString(value))
        .getBytes(java.nio.charset.StandardCharsets.UTF_8);
  }

  private byte[] mac(String domain, byte[] value) throws Exception {
    var mac = javax.crypto.Mac.getInstance("HmacSHA256");
    mac.init(new javax.crypto.spec.SecretKeySpec(new byte[32], "HmacSHA256"));
    mac.update(domain.getBytes(java.nio.charset.StandardCharsets.US_ASCII));
    return mac.doFinal(value);
  }

  @Test
  void configuredActorsCannotAcquirePlatformAuthorityFromInheritedHumanRoles() throws Exception {
    for (var caller :
        List.of(
            actor(
                "mail-subject",
                "backend-mail",
                List.of(
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
        .jwt(actorJwt(subject, client, roles))
        .authorities(token -> converter.convert(token).getAuthorities());
  }

  private org.springframework.security.oauth2.jwt.Jwt actorJwt(
      String subject, String client, List<String> roles) {
    var audiences =
        "backend-setup".equals(client)
            ? List.of(
                "tenantservice",
                "agencyservice",
                "consultingtypeservice",
                "userservice",
                "oriso-task-commands")
            : "backend-mail".equals(client)
                ? List.of("tenantservice", "agencyservice", "consultingtypeservice", "userservice")
                : List.of("consultingtypeservice");
    return org.springframework.security.oauth2.jwt.Jwt.withTokenValue("synthetic-no-wire-token")
        .header("alg", "RS256")
        .subject(subject)
        .claim("azp", client)
        .issuer("https://identity.example/realms/test")
        .audience(audiences)
        .expiresAt(Instant.now().plusSeconds(60))
        .claim("tenantId", 0L)
        .claim("realm_access", Map.of("roles", roles))
        .build();
  }
}
