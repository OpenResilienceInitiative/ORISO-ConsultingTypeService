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
@AutoConfigureTestDatabase
class TenantDefaultConsultingTypeAuthorizationIT {

  private static final int NEW_TENANT_ID = 7701;
  private static final int SECOND_TENANT_ID = 7702;
  private static final EasyRandom EASY_RANDOM = new EasyRandom();
  private static final ObjectMapper OBJECT_MAPPER =
      new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

  @Autowired private WebApplicationContext context;
  @Autowired private ConsultingTypeRepository consultingTypeRepository;

  @MockitoBean private JwtDecoder jwtDecoder;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    removeTestTenants();
  }

  @AfterEach
  void removeTestTenants() {
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
