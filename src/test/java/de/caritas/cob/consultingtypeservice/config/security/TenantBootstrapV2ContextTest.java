package de.caritas.cob.consultingtypeservice.config.security;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeConverter;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeIdAllocator;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity;
import de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim;
import de.caritas.cob.consultingtypeservice.api.service.TenantBootstrapService;
import de.caritas.cob.consultingtypeservice.schemas.model.ConsultingType;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/** Independent wire builder reaches the production guard before any Mongo operation. */
class TenantBootstrapV2ContextTest {
  private static final ObjectMapper JSON = new ObjectMapper();
  private static final byte[] KEY = new byte[32];
  private static final String ISSUER = "https://synthetic.identity/realms/context";

  @AfterEach
  void clearIdentity() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void exactMaterializedDtoPassesTheC16GuardBeforeTheFirstMongoQuery() throws Exception {
    var mongo = mock(MongoTemplate.class);
    var converter = mock(ConsultingTypeConverter.class);
    var ids = mock(ConsultingTypeIdAllocator.class);
    var dto = roundTripCommand();
    when(converter.convert(dto)).thenReturn(new ConsultingType());
    var reachedMongo = new IllegalStateException("finite first-query observation");
    when(mongo.findById(eq("42:0123456789abcdef0123456789abcdef"), eq(TenantBootstrapClaim.class)))
        .thenThrow(reachedMongo);
    var service = new TenantBootstrapService(mongo, converter, receiver(), ids, validator());
    String proof = proof(dto);
    assertSame(
        reachedMongo, assertThrows(IllegalStateException.class, () -> service.create(dto, proof)));
    verifyNoInteractions(ids);
  }

  @Test
  void independentlySignedProofForAnotherMaterializedDtoDeniesBeforeConverterOrMongo()
      throws Exception {
    var mongo = mock(MongoTemplate.class);
    var converter = mock(ConsultingTypeConverter.class);
    var ids = mock(ConsultingTypeIdAllocator.class);
    var signed = new ConsultingTypeDTO();
    signed.setTenantId(42);
    String proof = proof(signed);
    var received = new ConsultingTypeDTO();
    received.setTenantId(43);
    var service = new TenantBootstrapService(mongo, converter, receiver(), ids, validator());
    assertThrows(AccessDeniedException.class, () -> service.create(received, proof));
    verifyNoInteractions(mongo, converter, ids);
  }

  @Test
  void sameTenantWithChangedBodyDeniesBeforeConverterOrMongo() throws Exception {
    var mongo = mock(MongoTemplate.class);
    var converter = mock(ConsultingTypeConverter.class);
    var ids = mock(ConsultingTypeIdAllocator.class);
    var signed = new ConsultingTypeDTO();
    signed.setTenantId(42);
    signed.setDescription("signed-body");
    String proof = proof(signed);
    var received = new ConsultingTypeDTO();
    received.setTenantId(42);
    received.setDescription("changed-body");
    var service = new TenantBootstrapService(mongo, converter, receiver(), ids, validator());
    assertThrows(AccessDeniedException.class, () -> service.create(received, proof));
    verifyNoInteractions(mongo, converter, ids);
  }

  @Test
  void aMaterializedCommandSurvivesTheActualMongoMappingRoundTripAndOwnedReadback()
      throws Exception {
    var dto = roundTripCommand();
    var fixture = roundTripFixture();
    String proof = proof(dto);
    var response = assertDoesNotThrow(() -> fixture.service().create(dto, proof));
    assertEquals(42, response.getTenantId());
    assertEquals(9000, response.getId());
  }

  @Test
  void theVerifiedAttemptNonceOwnsBothTheDurableClaimAndItsMaterializedResult() throws Exception {
    var dto = roundTripCommand();
    var fixture = roundTripFixture();
    assertDoesNotThrow(() -> fixture.service().create(dto, proof(dto)));
    assertEquals("0123456789abcdef0123456789abcdef", fixture.claim().get().attempt());
    assertEquals(
        "0123456789abcdef0123456789abcdef", fixture.document().get().getString("bootstrapAttempt"));
    var claimDocument = new Document();
    var mapping = mapping();
    mapping.write(fixture.claim().get(), claimDocument);
    assertEquals("42:0123456789abcdef0123456789abcdef", claimDocument.getString("_id"));
  }

  @Test
  void anotherAttemptCannotAdoptTheFirstAttemptsSamePayloadOrResult() throws Exception {
    var dto = roundTripCommand();
    var fixture = roundTripFixture();
    String original = proof(dto);
    assertDoesNotThrow(() -> fixture.service().create(dto, original));
    String before = fixture.document().get().toJson();
    String foreignAttempt = proof(dto, "fedcba9876543210fedcba9876543210");
    assertThrows(
        de.caritas.cob.consultingtypeservice.api.exception.httpresponses.ConflictException.class,
        () -> fixture.service().create(dto, foreignAttempt));
    assertEquals(before, fixture.document().get().toJson());
    assertEquals(1, fixture.writes().get());
    assertDoesNotThrow(() -> fixture.service().create(dto, original));
    assertEquals(1, fixture.writes().get());
  }

  @Test
  void legacyPersistedClaimsKeepTheirOriginalTenantIdentity() throws Exception {
    var document =
        new Document("_id", "42")
            .append("attempt", "legacy-attempt")
            .append("resultId", 9000)
            .append("payloadDigest", "legacy-digest")
            .append("actorClient", "legacy-client")
            .append("actorSubject", "legacy-subject")
            .append("actorIssuer", ISSUER);
    var legacy = mapping().read(TenantBootstrapClaim.class, document);
    assertEquals("42", legacy.claimId());
    assertEquals("42", legacy.tenantId());
    assertEquals("legacy-attempt", legacy.attempt());
    assertEquals(9000, legacy.resultId());
  }

  @Test
  void cCannotAdoptAnUnboundLegacyClaimEvenWhenOwnerPayloadAndAttemptMatch() throws Exception {
    var dto = roundTripCommand();
    var fixture = roundTripFixture();
    String original = proof(dto);
    assertDoesNotThrow(() -> fixture.service().create(dto, original));
    var current = fixture.claim().get();
    fixture
        .claim()
        .set(
            new TenantBootstrapClaim(
                current.tenantId(),
                current.attempt(),
                current.resultId(),
                current.payloadDigest(),
                current.actorClient(),
                current.actorSubject(),
                current.actorIssuer()));
    String before = fixture.document().get().toJson();
    assertThrows(
        de.caritas.cob.consultingtypeservice.api.exception.httpresponses.ConflictException.class,
        () -> fixture.service().create(dto, original));
    assertEquals(before, fixture.document().get().toJson());
    assertEquals(1, fixture.writes().get());
  }

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.ValueSource(booleans = {false, true})
  void explicitBootstrapFlagsSurviveWithoutBeingReplacedBySchemaDefaults(boolean flag)
      throws Exception {
    var dto = roundTripCommand();
    explicitFlags(dto, flag);
    var fixture = roundTripFixture();
    String proof = proof(dto);
    var response = assertDoesNotThrow(() -> fixture.service().create(dto, proof));
    assertEquals(Boolean.valueOf(flag), response.getConsultantBoundedToConsultingType());
    assertEquals(Boolean.valueOf(flag), response.getExcludeNonMainConsultantsFromTeamSessions());
    assertEquals(Boolean.valueOf(flag), response.getIsAnonymousConversationAllowed());
    assertEquals(Boolean.valueOf(flag), response.getIsSubsequentRegistrationAllowed());
    assertEquals(Boolean.valueOf(flag), response.getSendFurtherStepsMessage());
  }

  @Test
  void aChangedPayloadCannotReuseTheOwnedClaimOrRewriteItsStoredResult() throws Exception {
    var dto = roundTripCommand();
    explicitFlags(dto, true);
    var fixture = roundTripFixture();
    String originalProof = proof(dto);
    assertDoesNotThrow(() -> fixture.service().create(dto, originalProof));
    String before = fixture.document().get().toJson();
    var changed = roundTripCommand();
    explicitFlags(changed, true);
    changed.setDescription("changed-owned-command");
    String changedProof = proof(changed);
    assertThrows(
        de.caritas.cob.consultingtypeservice.api.exception.httpresponses.ConflictException.class,
        () -> fixture.service().create(changed, changedProof));
    assertTrue(before.equals(fixture.document().get().toJson()), "owned result remains unchanged");
    assertEquals(1, fixture.writes().get());
    assertDoesNotThrow(() -> fixture.service().create(dto, originalProof));
    assertEquals(1, fixture.writes().get());
  }

  @org.junit.jupiter.params.ParameterizedTest
  @org.junit.jupiter.params.provider.ValueSource(
      strings = {"minimal", "urls", "registration", "notes", "mandatoryFields"})
  void invalidConfiguredCommandsAreRejectedBeforeAnyMongoQueryOrAllocation(String missing)
      throws Exception {
    var dto = roundTripCommand();
    if (missing.equals("minimal")) {
      dto =
          JSON.readValue(
              TenantBootstrapV2ContextTest.class.getResourceAsStream(
                  "/bootstrap-v2-invalid-minimal-consulting-type.json"),
              ConsultingTypeDTO.class);
      dto.setTenantId(42);
    } else if (missing.equals("urls")) dto.setUrls(null);
    else if (missing.equals("registration")) dto.setRegistration(null);
    else if (missing.equals("notes")) dto.getRegistration().setNotes(null);
    else dto.getRegistration().setMandatoryFields(null);
    var mongo = mock(MongoTemplate.class);
    var ids = mock(ConsultingTypeIdAllocator.class);
    var service =
        new TenantBootstrapService(
            mongo, new ConsultingTypeConverter(), receiver(), ids, validator());
    String signed = proof(dto);
    var command = dto;
    assertThrows(
        de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException.class,
        () -> service.create(command, signed));
    verifyNoInteractions(mongo, ids);
  }

  @Test
  void theCompleteConfiguredDtoSatisfiesTheExistingPersistenceSchemaWithoutAGeneratedId()
      throws Exception {
    var schema =
        new org.json.JSONObject(
            new org.json.JSONTokener(
                TenantBootstrapV2ContextTest.class.getResourceAsStream(
                    "/schemas/consulting-type.json")));
    var required = schema.getJSONArray("required");
    var commandRequired = new org.json.JSONArray();
    for (int index = 0; index < required.length(); index++) {
      if (!required.getString(index).equals("id")) commandRequired.put(required.getString(index));
    }
    schema.put("required", commandRequired);
    var command =
        new org.json.JSONObject(
            new org.json.JSONTokener(
                TenantBootstrapV2ContextTest.class.getResourceAsStream(
                    "/bootstrap-v2-complete-consulting-type.json")));
    // Tenant id is overwritten from the private live Grant, not taken from configuration.
    command.put("tenantId", 42);
    assertDoesNotThrow(
        () ->
            org.everit.json.schema.loader.SchemaLoader.builder()
                .schemaJson(schema)
                .useDefaults(true)
                .draftV7Support()
                .build()
                .load()
                .build()
                .validate(command));
  }

  @Test
  void validOptionalFlagsRequireMaterializationAcrossTheOrdinaryMappingBoundary() throws Exception {
    var dto = roundTripCommand();
    var payload = new ConsultingTypeConverter().convert(dto);
    var mappingContext = new MongoMappingContext();
    mappingContext.afterPropertiesSet();
    var mapping = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, mappingContext);
    mapping.afterPropertiesSet();
    var document = new Document();
    mapping.write(payload, document);
    var stored = mapping.read(ConsultingType.class, document);
    var expected = JSON.valueToTree(payload);
    var actual = JSON.valueToTree(stored);
    var changed = new java.util.TreeSet<String>();
    expected
        .fieldNames()
        .forEachRemaining(
            name -> {
              if (!Objects.equals(expected.get(name), actual.get(name))) changed.add(name);
            });
    actual
        .fieldNames()
        .forEachRemaining(
            name -> {
              if (!Objects.equals(expected.get(name), actual.get(name))) changed.add(name);
            });
    assertEquals(
        new java.util.TreeSet<>(
            List.of(
                "consultantBoundedToConsultingType",
                "excludeNonMainConsultantsFromTeamSessions",
                "isAnonymousConversationAllowed",
                "isSubsequentRegistrationAllowed",
                "sendFurtherStepsMessage")),
        changed);
  }

  @Test
  void nativePairedControlRequiresTheExactFiniteCommandAndIntegralSequence() throws Exception {
    assertEquals(
        "OBSERVE",
        de.caritas.cob.consultingtypeservice.api.controller.BootstrapV2ReceiverFixture
            .PairedControls.checkedCommand(
            JSON.readTree("{\"sequence\":1,\"command\":\"OBSERVE\"}")));
    for (String invalid :
        List.of(
            "{}",
            "null",
            "{\"sequence\":true,\"command\":\"ARM\"}",
            "{\"sequence\":1.5,\"command\":\"ARM\"}",
            "{\"sequence\":0,\"command\":\"ARM\"}",
            "{\"sequence\":1,\"command\":\"ARM\",\"extra\":true}")) {
      JsonNode command = JSON.readTree(invalid);
      assertThrows(
          IllegalStateException.class,
          () ->
              de.caritas.cob.consultingtypeservice.api.controller.BootstrapV2ReceiverFixture
                  .PairedControls.checkedCommand(command));
    }
  }

  @Test
  void nativePairedControlRefusalsNeverEchoPrivateCommandsOrValues() throws Exception {
    JsonNode command = JSON.readTree("{\"sequence\":1,\"command\":\"private-synthetic-bearer\"}");
    var failure =
        assertThrows(
            IllegalStateException.class,
            () ->
                de.caritas.cob.consultingtypeservice.api.controller.BootstrapV2ReceiverFixture
                    .PairedControls.checkedCommand(command));
    assertTrue(!failure.getMessage().contains("private-synthetic-bearer"));
    assertTrue(!failure.getMessage().contains("sequence"));
  }

  private static void explicitFlags(ConsultingTypeDTO dto, boolean flag) {
    dto.setConsultantBoundedToConsultingType(flag);
    dto.setExcludeNonMainConsultantsFromTeamSessions(flag);
    dto.setIsAnonymousConversationAllowed(flag);
    dto.setIsSubsequentRegistrationAllowed(flag);
    dto.setSendFurtherStepsMessage(flag);
  }

  private static ConsultingTypeDTO roundTripCommand() throws Exception {
    var dto =
        JSON.readValue(
            TenantBootstrapV2ContextTest.class.getResourceAsStream(
                "/bootstrap-v2-complete-consulting-type.json"),
            ConsultingTypeDTO.class);
    dto.setTenantId(42);
    // All five flags are optional in a valid command; explicit variants below remain exact.
    dto.setConsultantBoundedToConsultingType(null);
    dto.setExcludeNonMainConsultantsFromTeamSessions(null);
    dto.setIsAnonymousConversationAllowed(null);
    dto.setIsSubsequentRegistrationAllowed(null);
    dto.setSendFurtherStepsMessage(null);
    return dto;
  }

  private record RoundTripFixture(
      TenantBootstrapService service,
      AtomicReference<Document> document,
      AtomicReference<TenantBootstrapClaim> claim,
      java.util.concurrent.atomic.AtomicInteger writes) {}

  private static MappingMongoConverter mapping() throws Exception {
    var context = new MongoMappingContext();
    context.afterPropertiesSet();
    var mapping = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context);
    mapping.afterPropertiesSet();
    return mapping;
  }

  private static RoundTripFixture roundTripFixture() throws Exception {
    var converter = new ConsultingTypeConverter();
    var mappingContext = new MongoMappingContext();
    mappingContext.afterPropertiesSet();
    var mapping = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, mappingContext);
    mapping.afterPropertiesSet();
    var mongo = mock(MongoTemplate.class);
    var ids = mock(ConsultingTypeIdAllocator.class);
    var claim = new AtomicReference<TenantBootstrapClaim>();
    var document = new AtomicReference<Document>();
    var materialized = new AtomicReference<ConsultingTypeEntity>();
    var writes = new java.util.concurrent.atomic.AtomicInteger();
    when(ids.reserve()).thenReturn(9000);
    when(mongo.findById(any(String.class), eq(TenantBootstrapClaim.class)))
        .thenAnswer(call -> claim.get());
    when(mongo.insert(any(TenantBootstrapClaim.class)))
        .thenAnswer(
            call -> {
              claim.set(call.getArgument(0));
              return claim.get();
            });
    when(mongo.insert(any(ConsultingTypeEntity.class)))
        .thenAnswer(
            call -> {
              var stored = new Document();
              materialized.set(call.getArgument(0));
              mapping.write(materialized.get(), stored);
              writes.incrementAndGet();
              document.set(stored);
              return call.getArgument(0);
            });
    when(mongo.findById(eq(9000), eq(ConsultingTypeEntity.class)))
        .thenAnswer(
            call -> {
              if (document.get() == null) return null;
              var stored = mapping.read(ConsultingTypeEntity.class, document.get());
              roundTripDiagnostics(materialized.get(), stored, claim.get(), document.get());
              return stored;
            });
    return new RoundTripFixture(
        new TenantBootstrapService(mongo, converter, receiver(), ids, validator()),
        document,
        claim,
        writes);
  }

  private static void roundTripDiagnostics(
      ConsultingType command,
      ConsultingTypeEntity stored,
      TenantBootstrapClaim claim,
      Document document)
      throws Exception {
    var before = new ConsultingType();
    var after = new ConsultingType();
    org.springframework.beans.BeanUtils.copyProperties(command, before);
    org.springframework.beans.BeanUtils.copyProperties(stored, after);
    before.setId(null);
    after.setId(null);
    JsonNode expected = JSON.valueToTree(before);
    JsonNode actual = JSON.valueToTree(after);
    var fields = new TreeMap<String, Object>();
    // Fixed schema catalogue: values, arbitrary field names and identifiers are never emitted.
    for (String field :
        List.of(
            "id",
            "tenantId",
            "description",
            "groups",
            "furtherInformation",
            "slug",
            "excludeNonMainConsultantsFromTeamSessions",
            "lockedAgencies",
            "whiteSpot",
            "groupChat",
            "consultantBoundedToConsultingType",
            "welcomeMessage",
            "sendFurtherStepsMessage",
            "sendSaveSessionDataMessage",
            "sessionDataInitializing",
            "initializeFeedbackChat",
            "isPeerChat",
            "languageFormal",
            "roles",
            "notifications",
            "registration",
            "urls",
            "showAskerProfile",
            "isVideoCallAllowed",
            "isSubsequentRegistrationAllowed",
            "isAnonymousConversationAllowed",
            "requiredComponents",
            "welcomeScreen")) {
      JsonNode left = expected.get(field);
      JsonNode right = actual.get(field);
      fields.put(
          field,
          Map.of(
              "commandType",
              left == null ? "ABSENT" : left.getNodeType().name(),
              "storedType",
              right == null ? "ABSENT" : right.getNodeType().name(),
              "commandNull",
              left == null || left.isNull(),
              "storedNull",
              right == null || right.isNull(),
              "equal",
              Objects.equals(left, right),
              "persistedFieldPresent",
              document.containsKey(field)));
    }
    System.out.println(
        "C16_ROUNDTRIP "
            + JSON.writeValueAsString(
                Map.of(
                    "resultIdMatches",
                    claim.resultId().equals(stored.getId()),
                    "tenantMatches",
                    claim.tenantId().equals(String.valueOf(stored.getTenantId())),
                    "attemptMatches",
                    claim.attempt().equals(stored.getBootstrapAttempt()),
                    "storedDigestMatches",
                    claim.payloadDigest().equals(stored.getBootstrapPayloadDigest()),
                    "completeFingerprintModelEqual",
                    expected.equals(actual),
                    "fields",
                    fields)));
  }

  private static de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeValidator
      validator() {
    var validator =
        new de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeValidator();
    org.springframework.test.util.ReflectionTestUtils.setField(
        validator, "consultingTypeJsonSchemaFile", "/schemas/consulting-type.json");
    return validator;
  }

  private static TenantCreationContext receiver() {
    var environment =
        new MockEnvironment()
            .withProperty("TASK_IDENTITY_MODEL", "FIVE_ACTORS")
            .withProperty("TASK_IDENTITY_REQUIRED_TASKS", "CONFIG_WIZARD")
            .withProperty("TASK_IDENTITY_AUDIENCE", "consultingtypeservice")
            .withProperty("IDENTITY_SETUP_CLIENT_ID", "backend-setup")
            .withProperty("IDENTITY_SETUP_SERVICE_SUBJECT", "setup-subject")
            .withProperty("ORISO_TASK_CONTEXT_KEY", Base64.getEncoder().encodeToString(KEY));
    var jwt =
        Jwt.withTokenValue("synthetic-no-wire-token")
            .header("alg", "RS256")
            .issuer(ISSUER)
            .subject("setup-subject")
            .claim("azp", "backend-setup")
            .audience(List.of("consultingtypeservice"))
            .expiresAt(Instant.now().plusSeconds(120))
            .claim(
                "realm_access",
                Map.of(
                    "roles",
                    List.of("config-wizard", "invitation-reservations", "consultant-import")))
            .build();
    SecurityContextHolder.getContext()
        .setAuthentication(new JwtAuthenticationToken(jwt, List.of()));
    var identity = new TaskServiceIdentity(environment);
    assertTrue(identity.current("CONFIG_WIZARD"), "canonical three-role SETUP precondition");
    return new TenantCreationContext(environment, identity);
  }

  private static String proof(ConsultingTypeDTO dto) throws Exception {
    return proof(dto, "0123456789abcdef0123456789abcdef");
  }

  private static String proof(ConsultingTypeDTO dto, String nonce) throws Exception {
    long now = Instant.now().getEpochSecond();
    Map<String, Object> claims = new TreeMap<>();
    claims.put("v", 2);
    claims.put("iss", "tenantservice");
    claims.put("aud", "consultingtypeservice");
    claims.put("actor", "SETUP");
    claims.put("purpose", "CONFIG_WIZARD");
    claims.put("operation", "tenant.consulting-type.bootstrap");
    claims.put("method", "POST");
    claims.put("azp", "backend-setup");
    claims.put("sub", "setup-subject");
    claims.put("tokenIssuer", ISSUER);
    claims.put("tenantId", 42);
    claims.put("target", "tenant/42/consulting-type/default");
    claims.put(
        "payloadDigest",
        HexFormat.of().formatHex(mac("payload\n", canonical(JSON.valueToTree(dto)))));
    claims.put("nonce", nonce);
    claims.put("iat", now);
    claims.put("exp", now + 60);
    String encoded =
        Base64.getUrlEncoder().withoutPadding().encodeToString(canonical(JSON.valueToTree(claims)));
    return encoded
        + "."
        + Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(mac("proof\n", encoded.getBytes(StandardCharsets.US_ASCII)));
  }

  private static byte[] canonical(JsonNode value) throws Exception {
    if (value.isObject()) {
      var names = new ArrayList<String>();
      value.fieldNames().forEachRemaining(names::add);
      names.sort(String::compareTo);
      var fields = new ArrayList<String>();
      for (String name : names) {
        if (!value.get(name).isNull()) {
          fields.add(
              JSON.writeValueAsString(name)
                  + ":"
                  + new String(canonical(value.get(name)), StandardCharsets.UTF_8));
        }
      }
      return ("{" + String.join(",", fields) + "}").getBytes(StandardCharsets.UTF_8);
    }
    if (value.isArray()) {
      var items = new ArrayList<String>();
      for (JsonNode item : value) {
        items.add(new String(canonical(item), StandardCharsets.UTF_8));
      }
      return ("[" + String.join(",", items) + "]").getBytes(StandardCharsets.UTF_8);
    }
    return (value.isNumber()
            ? value.decimalValue().stripTrailingZeros().toPlainString()
            : JSON.writeValueAsString(value))
        .getBytes(StandardCharsets.UTF_8);
  }

  private static byte[] mac(String domain, byte[] payload) throws Exception {
    Mac mac = Mac.getInstance("HmacSHA256");
    mac.init(new SecretKeySpec(KEY, "HmacSHA256"));
    mac.update(domain.getBytes(StandardCharsets.US_ASCII));
    return mac.doFinal(payload);
  }
}
