package de.caritas.cob.consultingtypeservice.api.tenant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.auth.UserRole;
import de.caritas.cob.consultingtypeservice.api.controller.AuthenticationMockBuilder;
import de.caritas.cob.consultingtypeservice.api.controller.TopicGroupsController;
import de.caritas.cob.consultingtypeservice.api.service.TenantService;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.RestrictedTenantDTO;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.Settings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Not {@code @Transactional} and through the full filter chain, because production reads topics
 * without a caller transaction; only the Hibernate filter keeps other tenants out of topic groups.
 */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.profiles.active=testing")
@TestPropertySource(properties = "multitenancy.enabled=true")
@TestPropertySource(
    properties =
        "consulting.types.json.path=src/test/resources/consulting-type-settings-tenant-specific")
class TenantIsolationWithoutTransactionIT {

  private static final long OWN_TENANT = 1L;
  private static final long FOREIGN_TENANT = 2L;
  private static final long OWN_TOPIC = 9101L;
  private static final long FOREIGN_TOPIC = 9201L;
  private static final long SHARED_GROUP = 9001L;

  @Autowired private MockMvc mockMvc;
  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private TopicGroupsController topicGroupsController;

  @MockitoBean TenantService tenantService;

  @BeforeEach
  void seedOneTopicPerTenantInOneSharedGroup() {
    var topicsEnabled =
        new RestrictedTenantDTO().settings(new Settings().featureTopicsEnabled(true));
    when(tenantService.getRestrictedTenantDataNoCache(anyLong())).thenReturn(topicsEnabled);
    when(tenantService.getRestrictedTenantDataBySubdomainNoCache(any())).thenReturn(topicsEnabled);

    insertTopic(OWN_TOPIC, OWN_TENANT);
    insertTopic(FOREIGN_TOPIC, FOREIGN_TENANT);
    jdbcTemplate.update(
        "INSERT INTO topic_group (id, name, create_date, update_date)"
            + " VALUES (?, '{\"de\":\"isolation\"}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
        SHARED_GROUP);
    jdbcTemplate.update(
        "INSERT INTO topic_group_x_topic (group_id, topic_id) VALUES (?, ?), (?, ?)",
        SHARED_GROUP,
        OWN_TOPIC,
        SHARED_GROUP,
        FOREIGN_TOPIC);
  }

  @AfterEach
  void removeSeededRows() {
    TenantContext.clear();
    jdbcTemplate.update("DELETE FROM topic_group_x_topic WHERE group_id = ?", SHARED_GROUP);
    jdbcTemplate.update("DELETE FROM topic_group WHERE id = ?", SHARED_GROUP);
    jdbcTemplate.update("DELETE FROM topic WHERE id IN (?, ?)", OWN_TOPIC, FOREIGN_TOPIC);
  }

  @Test
  void getAllTopicsAsAdmin_Should_NotList_TopicOfAnotherTenant() throws Exception {
    var result = mockMvc.perform(asTopicAdminOf(OWN_TENANT, get("/topicadmin"))).andReturn();

    assertStatus(result, 200);
    var body = result.getResponse().getContentAsString();
    assertThat(body).contains("\"id\":" + OWN_TOPIC);
    assertThat(body).doesNotContain("\"id\":" + FOREIGN_TOPIC);
  }

  @Test
  void getTopicWithTranslationById_Should_Refuse_TopicOfAnotherTenant() throws Exception {
    var result =
        mockMvc
            .perform(asTopicAdminOf(OWN_TENANT, get("/topicadmin/" + FOREIGN_TOPIC)))
            .andReturn();

    // TopicNotFoundException is declared as 404, but the generic exception handler answers 500 for
    // any unknown id (own Träger too). What matters here: refused, and none of the foreign data.
    assertThat(result.getResponse().getStatus()).as("HTTP status").isGreaterThanOrEqualTo(400);
    assertThat(result.getResponse().getContentAsString()).doesNotContain("isolation-");
  }

  @Test
  void getAllTopicGroups_Should_NotReference_TopicOfAnotherTenant() throws Exception {
    var result = mockMvc.perform(asTopicAdminOf(OWN_TENANT, get("/topic-groups"))).andReturn();

    assertStatus(result, 200);
    var body = result.getResponse().getContentAsString();
    assertThat(body).contains(String.valueOf(OWN_TOPIC));
    assertThat(body).doesNotContain(String.valueOf(FOREIGN_TOPIC));
  }

  @Test
  void getAllTopicGroups_Should_ReferenceTopicsOfEveryTenant_When_TenantIsTheTechnicalTenantZero()
      throws Exception {
    var result = mockMvc.perform(asTopicAdminOf(0L, get("/topic-groups"))).andReturn();

    assertStatus(result, 200);
    var body = result.getResponse().getContentAsString();
    assertThat(body).contains(String.valueOf(OWN_TOPIC));
    assertThat(body).contains(String.valueOf(FOREIGN_TOPIC));
  }

  @Test
  void getAllTopicGroups_Should_ReferenceNoTopic_When_NoTenantIsSet() {
    TenantContext.clear();

    var groups = topicGroupsController.getAllTopicGroups().getBody();

    assertThat(groups).isNotNull();
    assertThat(groups.getData().getItems())
        .filteredOn(group -> group.getId() == SHARED_GROUP)
        .singleElement()
        .satisfies(group -> assertThat(group.getTopicIds()).isEmpty());
  }

  private static MockHttpServletRequestBuilder asTopicAdminOf(
      long tenantId, MockHttpServletRequestBuilder request) {
    return request
        .header("tenantId", String.valueOf(tenantId))
        .with(
            authentication(
                new AuthenticationMockBuilder()
                    .withUserRole(UserRole.TOPIC_ADMIN.getValue())
                    .withTenantId(String.valueOf(tenantId))
                    .build()))
        .accept(MediaType.APPLICATION_JSON);
  }

  private void insertTopic(long id, long tenantId) {
    jdbcTemplate.update(
        "INSERT INTO topic (id, tenant_id, name, description, status, create_date,"
            + " internal_identifier, titles_short, titles_long, titles_welcome, titles_dropdown,"
            + " slug) VALUES (?, ?, ?, '{\"de\":\"d\"}', 'ACTIVE', CURRENT_TIMESTAMP, ?,"
            + " '{\"de\":\"s\"}', '{\"de\":\"l\"}', 'w', 'dd', ?)",
        id,
        tenantId,
        "{\"de\":\"isolation-" + id + "\"}",
        "isolation-" + id,
        "isolation-" + id);
  }

  private static void assertStatus(MvcResult result, int expected) {
    assertThat(result.getResponse().getStatus()).as("HTTP status").isEqualTo(expected);
  }
}
