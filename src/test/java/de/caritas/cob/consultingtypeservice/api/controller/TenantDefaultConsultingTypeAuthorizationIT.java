package de.caritas.cob.consultingtypeservice.api.controller;

import static de.caritas.cob.consultingtypeservice.testHelper.PathConstants.ROOT_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeRepository;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Creating the default consulting type of a newly created tenant (ORISO-Helm#367), through the real
 * filter chain, JWT converter, role mapper, method security and the real service and repository.
 *
 * <p>TenantService creates a tenant and then forwards its own token to {@code POST
 * /consultingtypes} to create the tenant's default consulting type. When that token is the service
 * identity (realm role {@code technical}) it may only create the first consulting type of a tenant;
 * it can never add to or overwrite a tenant that already has one. Tenant admins keep the unchanged
 * create.
 */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@ActiveProfiles("testing")
@org.springframework.test.context.TestPropertySource(
    properties = {
      "TASK_IDENTITY_AUDIENCE=consultingtypeservice",
      "ORISO_TENANT_CREATION_CONTEXT_KEY=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
      "IDENTITY_CONFIG_WIZARD_CLIENT_ID=backend-config-wizard",
      "IDENTITY_CONFIG_WIZARD_SERVICE_SUBJECT=wizard-subject"
    })
@AutoConfigureTestDatabase
class TenantDefaultConsultingTypeAuthorizationIT {

  private static final int NEW_TENANT_ID = 7701;
  private static final int SECOND_TENANT_ID = 7702;
  private static final EasyRandom EASY_RANDOM = new EasyRandom();
  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

  @Autowired private WebApplicationContext context;
  @Autowired private ConsultingTypeRepository consultingTypeRepository;
  @Autowired private org.springframework.data.mongodb.core.MongoTemplate mongo;

  @MockitoBean private JwtDecoder jwtDecoder;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    removeTestTenants();
  }

  @AfterEach
  void removeTestTenants() {
    mongo.remove(
        org.springframework.data.mongodb.core.query.Query.query(
            org.springframework.data.mongodb.core.query.Criteria.where("_id").in("7701", "7702")),
        de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim.class);
    consultingTypeRepository.findAll().stream()
        .filter(
            entity ->
                entity.getTenantId() != null
                    && (entity.getTenantId() == NEW_TENANT_ID
                        || entity.getTenantId() == SECOND_TENANT_ID))
        .forEach(consultingTypeRepository::delete);
  }

  private void tokenWithRealmRoles(String... roles) {
    Jwt jwt =
        Jwt.withTokenValue("test-token")
            .header("alg", "none")
            .claim("sub", "subject")
            .claim("realm_access", Map.of("roles", List.of(roles)))
            .build();
    when(jwtDecoder.decode(any(String.class))).thenReturn(jwt);
  }

  private static ConsultingTypeDTO consultingTypeFor(Integer tenantId, String slug) {
    ConsultingTypeDTO dto = EASY_RANDOM.nextObject(ConsultingTypeDTO.class).tenantId(tenantId);
    dto.slug(slug);
    dto.getRoles().getConsultant().addRoleNames("test", Arrays.asList("test"));
    return dto;
  }

  private ResultActions create(String role, ConsultingTypeDTO dto) throws Exception {
    tokenWithRealmRoles(role);
    return mockMvc.perform(
        post(ROOT_PATH)
            .header("Authorization", "Bearer test-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(dto)));
  }

  private long consultingTypesOf(int tenantId) {
    return consultingTypeRepository.findAll().stream()
        .filter(entity -> entity.getTenantId() != null && entity.getTenantId() == tenantId)
        .count();
  }

  @Test
  void wizardCreatesInitialTypeOnlyAndRequiresExactIdentity() throws Exception {
    var caller =
        org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
            .jwt()
            .jwt(
                token ->
                    token
                        .issuer("https://identity.example/realms/test")
                        .subject("wizard-subject")
                        .claim("azp", "backend-config-wizard")
                        .audience(List.of("consultingtypeservice"))
                        .issuedAt(java.time.Instant.now().minusSeconds(10))
                        .expiresAt(java.time.Instant.now().plusSeconds(60))
                        .claim("realm_access", Map.of("roles", List.of("config-wizard"))));
    var dto = consultingTypeFor(NEW_TENANT_ID, "wizard-default");
    mockMvc
        .perform(
            post(ROOT_PATH)
                .with(caller)
                .header(
                    "X-ORISO-Tenant-Creation-Context",
                    bootstrapProof(NEW_TENANT_ID, java.time.Instant.now().getEpochSecond()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(dto)))
        .andExpect(status().isOk());
    mockMvc
        .perform(
            post(ROOT_PATH)
                .with(caller)
                .header(
                    "X-ORISO-Tenant-Creation-Context",
                    bootstrapProof(NEW_TENANT_ID, java.time.Instant.now().getEpochSecond()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(dto)))
        .andExpect(status().isConflict());
    mockMvc
        .perform(
            patch(ROOT_PATH + "/1")
                .with(caller)
                .header(
                    "X-ORISO-Tenant-Creation-Context",
                    bootstrapProof(NEW_TENANT_ID, java.time.Instant.now().getEpochSecond()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(OBJECT_MAPPER.writeValueAsString(dto)))
        .andExpect(status().isForbidden());
    var foreign =
        org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
            .jwt()
            .jwt(
                token ->
                    token
                        .subject("foreign-subject")
                        .claim("azp", "backend-config-wizard")
                        .audience(List.of("consultingtypeservice"))
                        .expiresAt(java.time.Instant.now().plusSeconds(60))
                        .claim("realm_access", Map.of("roles", List.of("config-wizard"))));
    mockMvc
        .perform(
            post(ROOT_PATH)
                .with(foreign)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    OBJECT_MAPPER.writeValueAsString(
                        consultingTypeFor(SECOND_TENANT_ID, "foreign-default"))))
        .andExpect(status().isForbidden());
    assertThat(consultingTypesOf(NEW_TENANT_ID)).isEqualTo(1);
    assertThat(consultingTypesOf(SECOND_TENANT_ID)).isZero();
  }

  @Test
  void wizardCannotBootstrapUnrelatedEmptyTenantWithMissingForeignOrExpiredProof()
      throws Exception {
    var caller =
        org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
            .jwt()
            .jwt(
                token ->
                    token
                        .issuer("https://identity.example/realms/test")
                        .subject("wizard-subject")
                        .claim("azp", "backend-config-wizard")
                        .audience(List.of("consultingtypeservice"))
                        .expiresAt(java.time.Instant.now().plusSeconds(60))
                        .claim("realm_access", Map.of("roles", List.of("config-wizard"))));
    String payload =
        OBJECT_MAPPER.writeValueAsString(
            consultingTypeFor(SECOND_TENANT_ID, "unauthorised-default"));
    mockMvc
        .perform(
            post(ROOT_PATH).with(caller).contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isForbidden());
    mockMvc
        .perform(
            post(ROOT_PATH)
                .with(caller)
                .header(
                    "X-ORISO-Tenant-Creation-Context",
                    bootstrapProof(NEW_TENANT_ID, java.time.Instant.now().getEpochSecond()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isForbidden());
    mockMvc
        .perform(
            post(ROOT_PATH)
                .with(caller)
                .header(
                    "X-ORISO-Tenant-Creation-Context",
                    bootstrapProof(
                        SECOND_TENANT_ID, java.time.Instant.now().getEpochSecond() - 120))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isForbidden());
    mockMvc
        .perform(
            post(ROOT_PATH)
                .with(caller)
                .header(
                    "X-ORISO-Tenant-Creation-Context",
                    bootstrapProof(SECOND_TENANT_ID, java.time.Instant.now().getEpochSecond())
                        + "tamper")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
        .andExpect(status().isForbidden());
    assertThat(consultingTypesOf(SECOND_TENANT_ID)).isZero();
  }

  @Test
  void simultaneousReplayOfValidProofCreatesAtMostOneInitialType() throws Exception {
    var caller =
        org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
            .jwt()
            .jwt(
                token ->
                    token
                        .issuer("https://identity.example/realms/test")
                        .subject("wizard-subject")
                        .claim("azp", "backend-config-wizard")
                        .audience(List.of("consultingtypeservice"))
                        .expiresAt(java.time.Instant.now().plusSeconds(60))
                        .claim("realm_access", Map.of("roles", List.of("config-wizard"))));
    String proof = bootstrapProof(NEW_TENANT_ID, java.time.Instant.now().getEpochSecond());
    String body =
        OBJECT_MAPPER.writeValueAsString(consultingTypeFor(NEW_TENANT_ID, "simultaneous-default"));
    var start = new java.util.concurrent.CountDownLatch(1);
    var pool = java.util.concurrent.Executors.newFixedThreadPool(2);
    try {
      java.util.concurrent.Callable<Integer> request =
          () -> {
            start.await();
            return mockMvc
                .perform(
                    post(ROOT_PATH)
                        .with(caller)
                        .header("X-ORISO-Tenant-Creation-Context", proof)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn()
                .getResponse()
                .getStatus();
          };
      var one = pool.submit(request);
      var two = pool.submit(request);
      start.countDown();
      assertThat(
              List.of(
                  one.get(15, java.util.concurrent.TimeUnit.SECONDS),
                  two.get(15, java.util.concurrent.TimeUnit.SECONDS)))
          .containsExactlyInAnyOrder(200, 409);
      assertThat(consultingTypesOf(NEW_TENANT_ID)).isEqualTo(1);
    } finally {
      pool.shutdownNow();
    }
  }

  private String bootstrapProof(long tenantId, long now) throws Exception {
    Map<String, Object> claims = new java.util.TreeMap<>();
    claims.put("aud", "consultingtypeservice");
    claims.put("azp", "backend-config-wizard");
    claims.put("exp", now + 60);
    claims.put("iat", now);
    claims.put("iss", "tenantservice");
    claims.put("nonce", "disposable-test-bootstrap");
    claims.put("sub", "wizard-subject");
    claims.put("tenantId", tenantId);
    claims.put("tokenIssuer", "https://identity.example/realms/test");
    claims.put("v", 1);
    String encoded =
        java.util.Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(OBJECT_MAPPER.writeValueAsBytes(claims));
    var mac = javax.crypto.Mac.getInstance("HmacSHA256");
    mac.init(new javax.crypto.spec.SecretKeySpec(new byte[32], "HmacSHA256"));
    return encoded
        + "."
        + java.util.Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(
                mac.doFinal(encoded.getBytes(java.nio.charset.StandardCharsets.US_ASCII)));
  }

  @Test
  void create_Should_succeed_When_technicalUserCreatesTheFirstConsultingTypeOfATenant()
      throws Exception {
    create("technical", consultingTypeFor(NEW_TENANT_ID, "tenant-7701-default"))
        .andExpect(status().isOk());

    assertThat(consultingTypesOf(NEW_TENANT_ID)).isEqualTo(1);
  }

  @Test
  void create_Should_beRejected_When_technicalUserTargetsATenantThatAlreadyHasOne()
      throws Exception {
    create("technical", consultingTypeFor(NEW_TENANT_ID, "tenant-7701-default"))
        .andExpect(status().isOk());

    create("technical", consultingTypeFor(NEW_TENANT_ID, "tenant-7701-second"))
        .andExpect(status().isConflict());

    assertThat(consultingTypesOf(NEW_TENANT_ID)).isEqualTo(1);
  }

  @Test
  void create_Should_beRejected_When_technicalUserSendsNoTenant() throws Exception {
    create("technical", consultingTypeFor(null, "no-tenant-default"))
        .andExpect(status().isBadRequest());
  }

  @Test
  void create_Should_stayUnchanged_When_tenantAdminAddsToATenantThatAlreadyHasOne()
      throws Exception {
    create("tenant-admin", consultingTypeFor(SECOND_TENANT_ID, "tenant-7702-first"))
        .andExpect(status().isOk());

    create("tenant-admin", consultingTypeFor(SECOND_TENANT_ID, "tenant-7702-second"))
        .andExpect(status().isOk());
  }

  @Test
  void create_Should_beForbidden_When_callerHasNeitherRole() throws Exception {
    create("user", consultingTypeFor(NEW_TENANT_ID, "tenant-7701-default"))
        .andExpect(status().isForbidden());

    assertThat(consultingTypesOf(NEW_TENANT_ID)).isZero();
  }

  @Test
  void patch_Should_stayForbidden_When_callerIsTechnicalUser() throws Exception {
    tokenWithRealmRoles("technical");
    mockMvc
        .perform(
            patch(ROOT_PATH + "/1")
                .header("Authorization", "Bearer test-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isForbidden());
  }
}
