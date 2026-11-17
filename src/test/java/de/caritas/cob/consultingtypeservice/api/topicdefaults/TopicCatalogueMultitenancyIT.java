package de.caritas.cob.consultingtypeservice.api.topicdefaults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.controller.AuthenticationMockBuilder;
import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicStatus;
import de.caritas.cob.consultingtypeservice.api.repository.TopicGroupRepository;
import de.caritas.cob.consultingtypeservice.api.repository.TopicRepository;
import de.caritas.cob.consultingtypeservice.api.service.TenantService;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import java.util.HashSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * With multitenancy on, the platform admin works in the technical tenant 0, while the default
 * topics belong to the default tenant. The catalogue reads and saves them across tenants.
 */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@TestPropertySource(properties = "multitenancy.enabled=true")
@TestPropertySource(
    properties =
        "consulting.types.json.path=src/test/resources/consulting-type-settings-tenant-specific")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TopicCatalogueMultitenancyIT {

  private static final String CATALOGUE = "/topicadmin/catalogue";
  private static final Long DEFAULT_TOPIC_TENANT = 1L;

  @Autowired private WebApplicationContext context;
  @Autowired private TopicRepository topicRepository;
  @Autowired private TopicGroupRepository topicGroupRepository;
  @Autowired private DefaultTopicSeeder defaultTopicSeeder;

  @MockitoBean TenantService tenantService;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    TenantContext.setCurrentTenant(0L);
    topicGroupRepository.deleteAll();
    topicRepository.deleteAll();
    defaultTopicSeeder.seedOrFillGaps();
  }

  @AfterEach
  void cleanUp() {
    TenantContext.clear();
  }

  private static Authentication platformAdmin() {
    return new AuthenticationMockBuilder().withUserRole("tenant-admin").withTenantId("0").build();
  }

  private ObjectNode catalogue() throws Exception {
    var body =
        mockMvc
            .perform(get(CATALOGUE).header("tenantId", "0").with(authentication(platformAdmin())))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return (ObjectNode) objectMapper.readTree(body);
  }

  @Test
  void theCatalogueOfThePlatformAdmin_showsTheTopicsOfTheDefaultTenant() throws Exception {
    var catalogue = catalogue();

    assertThat(catalogue.get("topics")).hasSize(20);
    var topicIds = new HashSet<Long>();
    catalogue.get("topics").forEach(topic -> topicIds.add(topic.get("id").asLong()));
    for (JsonNode group : catalogue.get("groups")) {
      group.get("topicIds").forEach(id -> assertThat(topicIds).contains(id.asLong()));
    }
  }

  @Test
  void thePlatformAdmin_savesChangedAndNewTopicsOfTheDefaultTenant() throws Exception {
    var catalogue = catalogue();
    var changed = (ObjectNode) catalogue.get("topics").get(0);
    changed.put("status", "INACTIVE");
    var newTopic = objectMapper.createObjectNode();
    newTopic.put("id", -1);
    newTopic.set("name", objectMapper.createObjectNode().put("de", "Wohnen"));
    newTopic.set("description", objectMapper.createObjectNode().put("de", "Beschreibung"));
    var firstGroup = (ObjectNode) catalogue.get("groups").get(0);
    ((ArrayNode) firstGroup.get("topicIds")).add(-1);
    var change = objectMapper.createObjectNode();
    change.set("groups", catalogue.get("groups"));
    change.set("topics", objectMapper.createArrayNode().add(changed).add(newTopic));

    mockMvc
        .perform(
            put(CATALOGUE)
                .header("tenantId", "0")
                .with(authentication(platformAdmin()))
                .contentType(APPLICATION_JSON)
                .content(change.toString()))
        .andExpect(status().isOk());

    // The request's tenant filter clears the tenant context of this thread when it is done.
    TenantContext.setCurrentTenant(0L);
    var stored = topicRepository.findById(changed.get("id").asLong()).orElseThrow();
    assertThat(stored.getStatus()).isEqualTo(TopicStatus.INACTIVE);
    var created =
        topicRepository.findAll().stream()
            .filter(topic -> "wohnen".equals(topic.getSlug()))
            .findFirst()
            .orElseThrow();
    assertThat(created.getTenantId()).isEqualTo(DEFAULT_TOPIC_TENANT);
    assertThat(created.getSendNextStepMessage()).isFalse();
    var saved = catalogue();
    assertThat(saved.get("topics")).hasSize(21);
    var savedFirstGroup = saved.get("groups").get(0).get("topicIds");
    assertThat(savedFirstGroup.get(savedFirstGroup.size() - 1).asLong()).isEqualTo(created.getId());
    assertThat(topicRepository.findAll()).extracting(TopicEntity::getTenantId).doesNotContainNull();
  }
}
