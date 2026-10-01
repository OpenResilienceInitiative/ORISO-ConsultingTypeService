package de.caritas.cob.consultingtypeservice.api.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.controller.TopicGroupsController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

/** Single-tenant deployments set no tenant at all, so fail-closed must not hide their topics. */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@TestPropertySource(properties = "multitenancy.enabled=false")
class TenantFilterMultitenancyOffIT {

  private static final long TOPIC_OF_TENANT_1 = 9301L;
  private static final long TOPIC_OF_TENANT_2 = 9302L;
  private static final long GROUP = 9003L;

  @Autowired private TopicGroupsController topicGroupsController;
  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void seedTwoTenantsTopicsInOneGroup() {
    insertTopic(TOPIC_OF_TENANT_1, 1L);
    insertTopic(TOPIC_OF_TENANT_2, 2L);
    jdbcTemplate.update(
        "INSERT INTO topic_group (id, name, create_date, update_date)"
            + " VALUES (?, '{\"de\":\"single\"}', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)",
        GROUP);
    jdbcTemplate.update(
        "INSERT INTO topic_group_x_topic (group_id, topic_id) VALUES (?, ?), (?, ?)",
        GROUP,
        TOPIC_OF_TENANT_1,
        GROUP,
        TOPIC_OF_TENANT_2);
  }

  @AfterEach
  void removeSeededRows() {
    TenantContext.clear();
    jdbcTemplate.update("DELETE FROM topic_group_x_topic WHERE group_id = ?", GROUP);
    jdbcTemplate.update("DELETE FROM topic_group WHERE id = ?", GROUP);
    jdbcTemplate.update(
        "DELETE FROM topic WHERE id IN (?, ?)", TOPIC_OF_TENANT_1, TOPIC_OF_TENANT_2);
  }

  @Test
  void getAllTopicGroups_Should_ReferenceEveryTopic_When_MultitenancyIsOffAndNoTenantIsSet() {
    TenantContext.clear();

    var groups = topicGroupsController.getAllTopicGroups().getBody();

    assertThat(groups).isNotNull();
    assertThat(groups.getData().getItems())
        .filteredOn(group -> group.getId() == GROUP)
        .singleElement()
        .satisfies(
            group ->
                assertThat(group.getTopicIds())
                    .containsExactlyInAnyOrder((int) TOPIC_OF_TENANT_1, (int) TOPIC_OF_TENANT_2));
  }

  private void insertTopic(long id, long tenantId) {
    jdbcTemplate.update(
        "INSERT INTO topic (id, tenant_id, name, description, status, create_date,"
            + " internal_identifier, titles_short, titles_long, titles_welcome, titles_dropdown,"
            + " slug) VALUES (?, ?, ?, '{\"de\":\"d\"}', 'ACTIVE', CURRENT_TIMESTAMP, ?,"
            + " '{\"de\":\"s\"}', '{\"de\":\"l\"}', 'w', 'dd', ?)",
        id,
        tenantId,
        "{\"de\":\"single-" + id + "\"}",
        "single-" + id,
        "single-" + id);
  }
}
