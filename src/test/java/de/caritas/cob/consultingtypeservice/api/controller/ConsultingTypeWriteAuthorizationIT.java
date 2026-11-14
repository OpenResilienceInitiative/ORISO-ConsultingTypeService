package de.caritas.cob.consultingtypeservice.api.controller;

import static de.caritas.cob.consultingtypeservice.testHelper.PathConstants.ROOT_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeConverter;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeRepository;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypePatchDTO;
import de.caritas.cob.consultingtypeservice.config.apiclient.TenantServiceApiControllerFactory;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.BooleanPermissionPolicy;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.PermissionPolicyMode;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.TenantPermissionPolicies;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

/**
 * Who may write a consulting type (ORISO-ConsultingTypeService#190), through the real filter chain,
 * JWT converter, role mapper, method security, controller, service and repository. Only
 * TenantService is replaced.
 *
 * <p>Every Träger admin holds {@code tenant-admin}, exactly like the platform admin; the token's
 * tenant claim ("0" = platform) is the only difference, as in TenantService. The tokens here carry
 * the real role set and a String claim.
 */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@ActiveProfiles("testing")
@AutoConfigureTestDatabase
class ConsultingTypeWriteAuthorizationIT {

  private static final int OWN_TENANT = 7801;
  private static final int OTHER_TENANT = 7802;
  private static final int OWN_TYPE = 78011;
  private static final int OTHER_TYPE = 78021;
  private static final int UNKNOWN_TYPE = 78999;
  private static final int SEED_TYPE_WITHOUT_TENANT = 1;
  private static final String ORGANISATION_ADMIN = "tenant-admin";
  private static final String LIMITED_ADMIN = "single-tenant-admin";
  private static final EasyRandom EASY_RANDOM = new EasyRandom();
  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

  @Autowired private WebApplicationContext context;
  @Autowired private ConsultingTypeRepository consultingTypeRepository;
  @Autowired private ConsultingTypeConverter consultingTypeConverter;

  @MockitoBean private JwtDecoder jwtDecoder;
  @MockitoBean private TenantServiceApiControllerFactory tenantServiceFactory;

  private MockMvc mockMvc;
  private TenantControllerApi tenantApi;
  private ApiClient tenantApiClient;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    removeTestTypes();
    store(OWN_TYPE, OWN_TENANT, "own-type");
    store(OTHER_TYPE, OTHER_TENANT, "other-type");
    tenantApi = mock(TenantControllerApi.class);
    tenantApiClient = mock(ApiClient.class);
    when(tenantApi.getApiClient()).thenReturn(tenantApiClient);
    reset(tenantServiceFactory);
    when(tenantServiceFactory.createControllerApi(any(RestTemplate.class))).thenReturn(tenantApi);
  }

  @AfterEach
  void removeTestTypes() {
    consultingTypeRepository.findAll().stream()
        .filter(
            type ->
                type.getId() == OWN_TYPE
                    || type.getId() == OTHER_TYPE
                    || (type.getTenantId() != null
                        && (type.getTenantId() == OWN_TENANT
                            || type.getTenantId() == OTHER_TENANT)))
        .forEach(consultingTypeRepository::delete);
  }

  private void store(int id, int tenantId, String slug) {
    ConsultingTypeDTO dto = EASY_RANDOM.nextObject(ConsultingTypeDTO.class).tenantId(tenantId);
    dto.slug(slug);
    dto.getRoles().getConsultant().addRoleNames("test", Arrays.asList("test"));
    var converted = consultingTypeConverter.convert(dto);
    converted.setId(id);
    converted.setLanguageFormal(true);
    converted.setIsVideoCallAllowed(false);
    var entity = new ConsultingTypeEntity();
    BeanUtils.copyProperties(converted, entity);
    consultingTypeRepository.insert(entity);
  }

  /** The tenant claim is a String in live tokens; {@code null} leaves the claim out. */
  private void token(String tenantClaim, String... roles) {
    Jwt.Builder builder =
        Jwt.withTokenValue("test-token")
            .header("alg", "none")
            .claim("sub", "subject")
            .claim("realm_access", Map.of("roles", List.of(roles)));
    if (tenantClaim != null) {
      builder.claim("tenantId", tenantClaim);
    }
    when(jwtDecoder.decode(any(String.class))).thenReturn(builder.build());
  }

  private void policy(boolean value, PermissionPolicyMode mode) {
    when(tenantApi.getTenantPermissionPolicies(anyLong()))
        .thenReturn(
            new TenantPermissionPolicies()
                .tenantId((long) OWN_TENANT)
                .putPoliciesItem(
                    "appearance", new BooleanPermissionPolicy().value(value).mode(mode)));
  }

  private ResultActions patchType(int id, ConsultingTypePatchDTO body) throws Exception {
    return mockMvc.perform(
        patch(ROOT_PATH + "/" + id)
            .header("Authorization", "Bearer test-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(body)));
  }

  /** Keeps the stored form of address and video setting, changes only the further-steps flag. */
  private static ConsultingTypePatchDTO unrelatedChange() {
    return new ConsultingTypePatchDTO()
        .languageFormal(true)
        .isVideoCallAllowed(false)
        .sendFurtherStepsMessage(true);
  }

  private static ConsultingTypePatchDTO formOfAddressChange() {
    return new ConsultingTypePatchDTO()
        .languageFormal(false)
        .isVideoCallAllowed(false)
        .sendFurtherStepsMessage(true);
  }

  private Boolean storedFormOfAddress(int id) {
    return consultingTypeRepository.findByConsultingTypeId(id).getLanguageFormal();
  }

  // --- organisation check ---

  @Test
  void patch_Should_succeed_When_organisationAdminChangesOwnConsultingType() throws Exception {
    token(String.valueOf(OWN_TENANT), ORGANISATION_ADMIN);
    policy(true, PermissionPolicyMode.SUGGESTED);

    patchType(OWN_TYPE, formOfAddressChange()).andExpect(status().isOk());

    assertThat(storedFormOfAddress(OWN_TYPE)).isFalse();
  }

  @Test
  void patch_Should_answerLikeAMissingId_When_organisationAdminNamesAnotherOrganisation()
      throws Exception {
    token(String.valueOf(OWN_TENANT), ORGANISATION_ADMIN);

    MvcResult foreign = patchType(OTHER_TYPE, formOfAddressChange()).andReturn();
    MvcResult missing = patchType(UNKNOWN_TYPE, formOfAddressChange()).andReturn();

    assertThat(foreign.getResponse().getStatus()).isEqualTo(404);
    assertThat(missing.getResponse().getStatus()).isEqualTo(404);
    assertThat(foreign.getResponse().getContentAsString())
        .isEqualTo(missing.getResponse().getContentAsString());
    assertThat(storedFormOfAddress(OTHER_TYPE)).isTrue();
    verifyNoInteractions(tenantApi);
  }

  @Test
  void patch_Should_answerNotFound_When_organisationAdminNamesAConsultingTypeWithoutOrganisation()
      throws Exception {
    token(String.valueOf(OWN_TENANT), ORGANISATION_ADMIN);

    patchType(SEED_TYPE_WITHOUT_TENANT, formOfAddressChange()).andExpect(status().isNotFound());

    assertThat(storedFormOfAddress(SEED_TYPE_WITHOUT_TENANT)).isTrue();
  }

  @Test
  void patch_Should_answerLikeAMissingId_When_theTokenNamesNoOrganisation() throws Exception {
    token(null, ORGANISATION_ADMIN);

    MvcResult existing = patchType(OWN_TYPE, formOfAddressChange()).andReturn();
    MvcResult missing = patchType(UNKNOWN_TYPE, formOfAddressChange()).andReturn();

    assertThat(existing.getResponse().getStatus()).isEqualTo(404);
    assertThat(missing.getResponse().getStatus()).isEqualTo(404);
    assertThat(existing.getResponse().getContentAsString())
        .isEqualTo(missing.getResponse().getContentAsString());
    assertThat(storedFormOfAddress(OWN_TYPE)).isTrue();
  }

  @Test
  void patch_Should_succeedForEveryOrganisation_When_callerIsThePlatformAdmin() throws Exception {
    token("0", ORGANISATION_ADMIN);

    patchType(OTHER_TYPE, formOfAddressChange()).andExpect(status().isOk());
    patchType(SEED_TYPE_WITHOUT_TENANT, unrelatedChange()).andExpect(status().isOk());

    assertThat(storedFormOfAddress(OTHER_TYPE)).isFalse();
    verifyNoInteractions(tenantApi);
  }

  // --- editing lock ---

  @Test
  void patch_Should_beForbidden_When_organisationIsLockedAndTheFormOfAddressChanges()
      throws Exception {
    token(String.valueOf(OWN_TENANT), ORGANISATION_ADMIN);
    policy(false, PermissionPolicyMode.ENFORCED);

    patchType(OWN_TYPE, formOfAddressChange()).andExpect(status().isForbidden());

    assertThat(storedFormOfAddress(OWN_TYPE)).isTrue();
  }

  @Test
  void patch_Should_beForbidden_When_organisationIsLockedAndTheFormOfAddressIsLeftOut()
      throws Exception {
    token(String.valueOf(OWN_TENANT), ORGANISATION_ADMIN);
    policy(false, PermissionPolicyMode.ENFORCED);

    patchType(OWN_TYPE, new ConsultingTypePatchDTO().isVideoCallAllowed(false))
        .andExpect(status().isForbidden());

    assertThat(storedFormOfAddress(OWN_TYPE)).isTrue();
  }

  @Test
  void patch_Should_succeed_When_organisationIsLockedButTheFormOfAddressIsUnchanged()
      throws Exception {
    token(String.valueOf(OWN_TENANT), ORGANISATION_ADMIN);
    policy(false, PermissionPolicyMode.ENFORCED);

    patchType(OWN_TYPE, unrelatedChange()).andExpect(status().isOk());

    assertThat(storedFormOfAddress(OWN_TYPE)).isTrue();
  }

  @Test
  void patch_Should_succeed_When_platformAdminChangesALockedOrganisation() throws Exception {
    token("0", ORGANISATION_ADMIN);
    policy(false, PermissionPolicyMode.ENFORCED);

    patchType(OWN_TYPE, formOfAddressChange()).andExpect(status().isOk());

    assertThat(storedFormOfAddress(OWN_TYPE)).isFalse();
  }

  @Test
  void patch_Should_askTenantServiceFreshWithTheCallersOwnToken() throws Exception {
    token(String.valueOf(OWN_TENANT), ORGANISATION_ADMIN);
    policy(true, PermissionPolicyMode.SUGGESTED);

    patchType(OWN_TYPE, formOfAddressChange()).andExpect(status().isOk());
    patchType(OWN_TYPE, new ConsultingTypePatchDTO().languageFormal(true).isVideoCallAllowed(false))
        .andExpect(status().isOk());
    patchType(OWN_TYPE, formOfAddressChange()).andExpect(status().isOk());

    // Each change of the form of address asks again: nothing is cached between calls.
    verify(tenantApi, org.mockito.Mockito.times(3)).getTenantPermissionPolicies((long) OWN_TENANT);
    verify(tenantApiClient, org.mockito.Mockito.times(3))
        .addDefaultHeader("Authorization", "Bearer test-token");
  }

  @Test
  void patch_Should_notAskTenantService_When_theFormOfAddressIsNotChanged() throws Exception {
    token(String.valueOf(OWN_TENANT), ORGANISATION_ADMIN);

    patchType(OWN_TYPE, unrelatedChange()).andExpect(status().isOk());

    verify(tenantApi, never()).getTenantPermissionPolicies(anyLong());
  }

  // --- the lock cannot be read: closed, and not mistaken for a refusal ---

  @Test
  void patch_Should_answerUnavailable_When_tenantServiceFails() throws Exception {
    token(String.valueOf(OWN_TENANT), ORGANISATION_ADMIN);
    when(tenantApi.getTenantPermissionPolicies(anyLong()))
        .thenThrow(new RestClientException("connection refused"));

    patchType(OWN_TYPE, formOfAddressChange()).andExpect(status().isServiceUnavailable());

    assertThat(storedFormOfAddress(OWN_TYPE)).isTrue();
  }

  @Test
  void patch_Should_answerUnavailable_When_tenantServiceAnswersWithoutTheAppearancePolicy()
      throws Exception {
    token(String.valueOf(OWN_TENANT), ORGANISATION_ADMIN);
    when(tenantApi.getTenantPermissionPolicies(anyLong()))
        .thenReturn(new TenantPermissionPolicies().tenantId((long) OWN_TENANT));

    patchType(OWN_TYPE, formOfAddressChange()).andExpect(status().isServiceUnavailable());

    assertThat(storedFormOfAddress(OWN_TYPE)).isTrue();
  }

  @Test
  void patch_Should_stillSucceed_When_tenantServiceFailsButNothingLockedChanges() throws Exception {
    token(String.valueOf(OWN_TENANT), ORGANISATION_ADMIN);
    when(tenantApi.getTenantPermissionPolicies(anyLong()))
        .thenThrow(new RestClientException("connection refused"));

    patchType(OWN_TYPE, unrelatedChange()).andExpect(status().isOk());
  }

  // --- the limited administrator ---

  @Test
  void patch_Should_succeed_When_limitedAdminChangesOnlyAllowedSettingsOfOwnOrganisation()
      throws Exception {
    token(String.valueOf(OWN_TENANT), LIMITED_ADMIN);

    patchType(OWN_TYPE, unrelatedChange()).andExpect(status().isOk());
  }

  @Test
  void patch_Should_beForbidden_When_limitedAdminChangesTheFormOfAddress() throws Exception {
    token(String.valueOf(OWN_TENANT), LIMITED_ADMIN);
    policy(true, PermissionPolicyMode.SUGGESTED);

    patchType(OWN_TYPE, formOfAddressChange()).andExpect(status().isForbidden());

    assertThat(storedFormOfAddress(OWN_TYPE)).isTrue();
  }

  @Test
  void patch_Should_answerNotFound_When_limitedAdminNamesAnotherOrganisation() throws Exception {
    token(String.valueOf(OWN_TENANT), LIMITED_ADMIN);

    patchType(OTHER_TYPE, unrelatedChange()).andExpect(status().isNotFound());
  }

  // --- create ---

  private ResultActions create(int tenantId, String slug) throws Exception {
    ConsultingTypeDTO dto = EASY_RANDOM.nextObject(ConsultingTypeDTO.class).tenantId(tenantId);
    dto.slug(slug);
    dto.getRoles().getConsultant().addRoleNames("test", Arrays.asList("test"));
    return mockMvc.perform(
        post(ROOT_PATH)
            .header("Authorization", "Bearer test-token")
            .contentType(MediaType.APPLICATION_JSON)
            .content(OBJECT_MAPPER.writeValueAsString(dto)));
  }

  private long typesOf(int tenantId) {
    return Stream.of(consultingTypeRepository.findAll())
        .flatMap(List::stream)
        .filter(type -> type.getTenantId() != null && type.getTenantId() == tenantId)
        .count();
  }

  @Test
  void create_Should_beForbidden_When_organisationAdminCreatesForAnotherOrganisation()
      throws Exception {
    token(String.valueOf(OWN_TENANT), ORGANISATION_ADMIN);

    create(OTHER_TENANT, "intruder").andExpect(status().isForbidden());

    assertThat(typesOf(OTHER_TENANT)).isEqualTo(1);
  }

  @Test
  void create_Should_beForbidden_When_organisationAdminCreatesForOwnOrganisation()
      throws Exception {
    token(String.valueOf(OWN_TENANT), ORGANISATION_ADMIN);
    policy(false, PermissionPolicyMode.ENFORCED);

    create(OWN_TENANT, "second-own").andExpect(status().isForbidden());

    assertThat(typesOf(OWN_TENANT)).isEqualTo(1);
    verifyNoInteractions(tenantApi);
  }

  @Test
  void create_Should_beForbidden_When_theTokenNamesNoOrganisation() throws Exception {
    token(null, ORGANISATION_ADMIN);

    create(OWN_TENANT, "no-claim").andExpect(status().isForbidden());

    assertThat(typesOf(OWN_TENANT)).isEqualTo(1);
  }

  @Test
  void create_Should_succeed_When_platformAdminCreatesForAnyOrganisation() throws Exception {
    token("0", ORGANISATION_ADMIN);

    create(OTHER_TENANT, "by-platform").andExpect(status().isOk());

    assertThat(typesOf(OTHER_TENANT)).isEqualTo(2);
  }
}
