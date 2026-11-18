package de.caritas.cob.consultingtypeservice.api.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.auth.UserRole;
import de.caritas.cob.consultingtypeservice.api.controller.AuthenticationMockBuilder;
import de.caritas.cob.consultingtypeservice.api.service.TenantService;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.RestrictedTenantDTO;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.Settings;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * The public topic lists ({@code /topic}, {@code /topic/public}) answer with the topics of the
 * request's tenant only, also for the platform admin. Only {@code /topicadmin} may look into
 * another tenant for the platform admin.
 */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.profiles.active=testing")
@TestPropertySource(properties = "multitenancy.enabled=true")
@TestPropertySource(
    properties =
        "consulting.types.json.path=src/test/resources/consulting-type-settings-tenant-specific")
class PublicTopicListTenantIsolationIT {

  private static final long TENANT_1 = 1L;
  private static final long TENANT_2 = 2L;
  private static final long TOPIC_OF_TENANT_1 = 9311L;
  private static final long TOPIC_OF_TENANT_2 = 9321L;

  @Autowired private MockMvc mockMvc;
  @Autowired private JdbcTemplate jdbcTemplate;

  @MockitoBean TenantService tenantService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void seedOneTopicInTenantOneAndTwo() {
    var topicsEnabled =
        new RestrictedTenantDTO().settings(new Settings().featureTopicsEnabled(true));
    when(tenantService.getRestrictedTenantDataNoCache(anyLong())).thenReturn(topicsEnabled);
    when(tenantService.getRestrictedTenantDataBySubdomainNoCache(any())).thenReturn(topicsEnabled);
    insertTopic(TOPIC_OF_TENANT_1, TENANT_1);
    insertTopic(TOPIC_OF_TENANT_2, TENANT_2);
  }

  @AfterEach
  void removeSeededRows() {
    TenantContext.clear();
    jdbcTemplate.update(
        "DELETE FROM topic WHERE id IN (?, ?)", TOPIC_OF_TENANT_1, TOPIC_OF_TENANT_2);
  }

  private static Authentication platformAdmin() {
    return new AuthenticationMockBuilder()
        .withUserRole(UserRole.TENANT_ADMIN.getValue())
        .withTenantId("0")
        .build();
  }

  private static Authentication userOfTenant2() {
    return new AuthenticationMockBuilder()
        .withUserRole(UserRole.TOPIC_ADMIN.getValue())
        .withTenantId(String.valueOf(TENANT_2))
        .build();
  }

  private static MockHttpServletRequestBuilder inTenant(
      long tenantId, MockHttpServletRequestBuilder request) {
    return request.header("tenantId", String.valueOf(tenantId)).accept(MediaType.APPLICATION_JSON);
  }

  @Test
  void publicTopicList_ofThePlatformAdmin_showsNoTopicOfAnotherTenant() throws Exception {
    var result =
        mockMvc
            .perform(inTenant(0L, get("/topic")).with(authentication(platformAdmin())))
            .andReturn();

    assertThat(result.getResponse().getStatus()).isIn(200, 204);
    assertThat(tenantsOf(listedTopicIds(result))).allMatch(tenant -> tenant == 0L);
  }

  @Test
  void publicActiveTopicList_ofThePlatformAdmin_showsNoTopicOfAnotherTenant() throws Exception {
    var result =
        mockMvc
            .perform(inTenant(0L, get("/topic/public")).with(authentication(platformAdmin())))
            .andReturn();

    assertThat(result.getResponse().getStatus()).isIn(200, 204);
    assertThat(tenantsOf(listedTopicIds(result))).allMatch(tenant -> tenant == 0L);
  }

  @Test
  void publicTopicList_ofATenant2User_showsOnlyTenant2Topics() throws Exception {
    var result =
        mockMvc
            .perform(inTenant(TENANT_2, get("/topic")).with(authentication(userOfTenant2())))
            .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    var ids = listedTopicIds(result);
    assertThat(ids).contains(TOPIC_OF_TENANT_2).doesNotContain(TOPIC_OF_TENANT_1);
    assertThat(tenantsOf(ids)).allMatch(tenant -> tenant == TENANT_2);
  }

  @Test
  void publicActiveTopicList_ofTenant2_showsOnlyTenant2Topics() throws Exception {
    var result = mockMvc.perform(inTenant(TENANT_2, get("/topic/public"))).andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    var ids = listedTopicIds(result);
    assertThat(ids).contains(TOPIC_OF_TENANT_2).doesNotContain(TOPIC_OF_TENANT_1);
    assertThat(tenantsOf(ids)).allMatch(tenant -> tenant == TENANT_2);
  }

  @Test
  void adminTopicList_ofThePlatformAdmin_stillShowsTheDefaultTopicTenant() throws Exception {
    var result =
        mockMvc
            .perform(inTenant(0L, get("/topicadmin")).with(authentication(platformAdmin())))
            .andReturn();

    assertThat(result.getResponse().getStatus()).isEqualTo(200);
    assertThat(listedTopicIds(result))
        .contains(TOPIC_OF_TENANT_1)
        .doesNotContain(TOPIC_OF_TENANT_2);
  }

  private List<Long> listedTopicIds(MvcResult result) throws Exception {
    var ids = new ArrayList<Long>();
    var body = result.getResponse().getContentAsString();
    if (body.isBlank()) {
      return ids;
    }
    objectMapper.readTree(body).forEach(topic -> ids.add(topic.get("id").asLong()));
    return ids;
  }

  private List<Long> tenantsOf(List<Long> topicIds) {
    return topicIds.stream()
        .map(
            id ->
                jdbcTemplate.queryForObject(
                    "SELECT tenant_id FROM topic WHERE id = ?", Long.class, id))
        .toList();
  }

  private void insertTopic(long id, long tenantId) {
    jdbcTemplate.update(
        "INSERT INTO topic (id, tenant_id, name, description, status, create_date,"
            + " internal_identifier, titles_short, titles_long, titles_welcome, titles_dropdown,"
            + " slug) VALUES (?, ?, ?, '{\"de\":\"d\"}', 'ACTIVE', CURRENT_TIMESTAMP, ?,"
            + " '{\"de\":\"s\"}', '{\"de\":\"l\"}', 'w', 'dd', ?)",
        id,
        tenantId,
        "{\"de\":\"public-list-" + id + "\"}",
        "public-list-" + id,
        "public-list-" + id);
  }
}
