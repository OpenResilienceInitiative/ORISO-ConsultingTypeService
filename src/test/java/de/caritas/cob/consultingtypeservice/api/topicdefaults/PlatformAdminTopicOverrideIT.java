package de.caritas.cob.consultingtypeservice.api.topicdefaults;

import static de.caritas.cob.consultingtypeservice.testHelper.TopicCsrfTestSupport.topicCsrf;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.controller.AuthenticationMockBuilder;
import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicStatus;
import de.caritas.cob.consultingtypeservice.api.repository.TopicGroupRepository;
import de.caritas.cob.consultingtypeservice.api.repository.TopicRepository;
import de.caritas.cob.consultingtypeservice.api.service.TenantService;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
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
 * The platform admin overrides the shipped default topics: texts, icon, visibility. What they save
 * wins over the defaults for good, and the installation never ends up without a visible topic.
 */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@TestPropertySource(properties = "multitenancy.enabled=false")
@TestPropertySource(properties = "feature.multitenancy.with.single.domain.enabled=true")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PlatformAdminTopicOverrideIT {

  private static final String DEFAULT_PREGNANCY_ICON =
      "/service/topic/public/icons/t-01-schwangerschaft.png";
  private static final String OWN_ICON = "data:image/png;base64,iVBORw0KGgo=";

  @Autowired private WebApplicationContext context;
  @Autowired private TopicRepository topicRepository;
  @Autowired private TopicGroupRepository topicGroupRepository;
  @Autowired private DefaultTopicSeeder defaultTopicSeeder;

  // The tenant's "topics" switch is off: the platform admin still manages the catalogue.
  @MockitoBean TenantService tenantService;

  private final ObjectMapper objectMapper = new ObjectMapper();
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    TenantContext.clear();
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    topicGroupRepository.deleteAll();
    topicRepository.deleteAll();
    defaultTopicSeeder.seedOrFillGaps();
  }

  private static Authentication platformAdmin() {
    return new AuthenticationMockBuilder().withUserRole("tenant-admin").withTenantId("0").build();
  }

  private static Authentication traegerAdmin() {
    return new AuthenticationMockBuilder().withUserRole("tenant-admin").withTenantId("5").build();
  }

  private Long idOf(String slug) {
    return topicRepository.findAll().stream()
        .filter(topic -> slug.equals(topic.getSlug()))
        .map(TopicEntity::getId)
        .findFirst()
        .orElseThrow();
  }

  private ObjectNode storedTopic(Long id) throws Exception {
    var body =
        mockMvc
            .perform(get("/topicadmin/" + id).with(authentication(platformAdmin())))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return (ObjectNode) objectMapper.readTree(body);
  }

  private void save(Long id, ObjectNode topic, Authentication who, int expectedStatus)
      throws Exception {
    mockMvc
        .perform(
            put("/topicadmin/" + id)
                .with(topicCsrf())
                .with(authentication(who))
                .contentType(APPLICATION_JSON)
                .content(topic.toString()))
        .andExpect(status().is(expectedStatus));
  }

  @Test
  void platformAdminWithoutTopicAdminRole_listsAndSavesTopics() throws Exception {
    mockMvc
        .perform(get("/topicadmin").with(authentication(platformAdmin())))
        .andExpect(status().isOk());
    var id = idOf("debt");
    var topic = storedTopic(id);
    ((ObjectNode) topic.get("name")).put("de", "Schuldnerberatung");

    save(id, topic, platformAdmin(), 200);

    mockMvc
        .perform(get("/topic/public").accept(APPLICATION_JSON))
        .andExpect(jsonPath("$[?(@.slug == 'debt')].name").value("Schuldnerberatung"));
  }

  @Test
  void traegerAdmin_cannotChangeThePlatformTopics() throws Exception {
    var id = idOf("debt");
    save(id, storedTopic(id), traegerAdmin(), 403);
  }

  @Test
  void savedTopic_isNeverRefilledFromTheDefaults() throws Exception {
    var id = idOf("pregnancy");
    var topic = storedTopic(id);
    ((ObjectNode) topic.get("name")).remove("fr");
    topic.put("icon", OWN_ICON);
    save(id, topic, platformAdmin(), 200);

    defaultTopicSeeder.seedOrFillGaps();

    var stored = storedTopic(id);
    org.assertj.core.api.Assertions.assertThat(stored.get("name").has("fr")).isFalse();
    org.assertj.core.api.Assertions.assertThat(stored.get("icon").asText()).isEqualTo(OWN_ICON);
  }

  @Test
  void removingTheOwnIcon_bringsBackTheDefaultIcon() throws Exception {
    var id = idOf("pregnancy");
    var topic = storedTopic(id);
    topic.put("icon", OWN_ICON);
    save(id, topic, platformAdmin(), 200);

    topic.put("icon", "");
    save(id, topic, platformAdmin(), 200);

    org.assertj.core.api.Assertions.assertThat(storedTopic(id).get("icon").asText())
        .isEqualTo(DEFAULT_PREGNANCY_ICON);
  }

  @Test
  void savingWithoutIcon_keepsTheStoredIcon() throws Exception {
    var id = idOf("pregnancy");
    var topic = storedTopic(id);
    topic.remove("icon");

    save(id, topic, platformAdmin(), 200);

    org.assertj.core.api.Assertions.assertThat(storedTopic(id).get("icon").asText())
        .isEqualTo(DEFAULT_PREGNANCY_ICON);
  }

  @Test
  void iconThatIsNoImage_isRefused() throws Exception {
    var id = idOf("pregnancy");
    var topic = storedTopic(id);
    topic.put("icon", "javascript:alert(1)");

    save(id, topic, platformAdmin(), 400);
  }

  @Test
  void newTopicWithAnIconThatIsNoImage_isRefused() throws Exception {
    var topic = storedTopic(idOf("pregnancy"));
    topic.remove("id");
    topic.put("slug", "new-topic");
    topic.put("icon", "javascript:alert(1)");

    mockMvc
        .perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/topicadmin")
                .with(topicCsrf())
                .with(authentication(platformAdmin()))
                .contentType(APPLICATION_JSON)
                .content(topic.toString()))
        .andExpect(status().isBadRequest());
  }

  @Test
  void hidingTheLastVisibleTopic_isRefused() throws Exception {
    var lastId = idOf("debt");
    for (TopicEntity other : topicRepository.findAll()) {
      if (!other.getId().equals(lastId)) {
        other.setStatus(TopicStatus.INACTIVE);
        topicRepository.save(other);
      }
    }
    var topic = storedTopic(lastId);
    topic.put("status", "INACTIVE");

    save(lastId, topic, platformAdmin(), 409);

    mockMvc
        .perform(get("/topic/public").accept(APPLICATION_JSON))
        .andExpect(jsonPath("$[0].slug").value("debt"));
  }
}
