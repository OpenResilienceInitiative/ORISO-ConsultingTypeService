package de.caritas.cob.consultingtypeservice.api.topicdefaults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.RestrictedTenantDTO;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.Settings;
import java.util.HashSet;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
    return catalogueInTenant("0");
  }

  /** The platform admin's catalogue, requested with the given tenant header. */
  private ObjectNode catalogueInTenant(String tenantId) throws Exception {
    var body =
        mockMvc
            .perform(
                get(CATALOGUE).header("tenantId", tenantId).with(authentication(platformAdmin())))
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

  private static int placements(JsonNode catalogue) {
    var count = 0;
    for (JsonNode group : catalogue.get("groups")) {
      count += group.get("topicIds").size();
    }
    return count;
  }

  /**
   * The request's tenant (subdomain or header) feeds the Hibernate tenant filter. The platform
   * admin's catalogue must ignore it: a filtered save would rewrite every category from only that
   * tenant's topics and silently drop the default topics from all categories.
   */
  @Test
  void thePlatformAdmin_inAnotherTenantsRequest_seesAndSavesTheWholeCatalogue() throws Exception {
    var catalogue = catalogueInTenant("5");
    assertThat(catalogue.get("topics")).hasSize(20);
    var placementsBefore = placements(catalogue);
    assertThat(placementsBefore).isPositive();
    var newTopic = objectMapper.createObjectNode();
    newTopic.put("id", -1);
    newTopic.set("name", objectMapper.createObjectNode().put("de", "Wohnen"));
    newTopic.set("description", objectMapper.createObjectNode().put("de", "Beschreibung"));
    ((ArrayNode) catalogue.get("groups").get(0).get("topicIds")).add(-1);
    var change = objectMapper.createObjectNode();
    change.set("groups", catalogue.get("groups"));
    change.set("topics", objectMapper.createArrayNode().add(newTopic));

    mockMvc
        .perform(
            put(CATALOGUE)
                .header("tenantId", "5")
                .with(authentication(platformAdmin()))
                .contentType(APPLICATION_JSON)
                .content(change.toString()))
        .andExpect(status().isOk());

    var saved = catalogue();
    assertThat(saved.get("topics")).hasSize(21);
    assertThat(placements(saved)).isEqualTo(placementsBefore + 1);
    assertThat(seededTopic("wohnen").getTenantId()).isEqualTo(DEFAULT_TOPIC_TENANT);
  }

  private static Authentication topicAdminOfTenant5() {
    return new AuthenticationMockBuilder().withUserRole("topic-admin").withTenantId("5").build();
  }

  private TopicEntity seededTopic(String slug) {
    TenantContext.setCurrentTenant(0L);
    return topicRepository.findAll().stream()
        .filter(topic -> slug.equals(topic.getSlug()))
        .findFirst()
        .orElseThrow();
  }

  @Test
  void thePlatformAdmin_readsAndUpdatesASeededTopicById() throws Exception {
    var debt = seededTopic("debt");
    var body =
        mockMvc
            .perform(
                get("/topicadmin/" + debt.getId())
                    .header("tenantId", "0")
                    .with(authentication(platformAdmin())))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    var topic = (ObjectNode) objectMapper.readTree(body);
    assertThat(topic.get("slug").asText()).isEqualTo("debt");
    ((ObjectNode) topic.get("name")).put("en", "Debt counselling, changed");

    mockMvc
        .perform(
            put("/topicadmin/" + debt.getId())
                .header("tenantId", "0")
                .with(authentication(platformAdmin()))
                .contentType(APPLICATION_JSON)
                .content(topic.toString()))
        .andExpect(status().isOk());

    var stored = seededTopic("debt");
    assertThat(stored.getName()).contains("Debt counselling, changed");
    assertThat(stored.getTenantId()).isEqualTo(DEFAULT_TOPIC_TENANT);
  }

  @Test
  void thePlatformAdmin_listsTheSeededTopics() throws Exception {
    var body =
        mockMvc
            .perform(
                get("/topicadmin").header("tenantId", "0").with(authentication(platformAdmin())))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertThat(objectMapper.readTree(body)).hasSize(20);
  }

  private static void assertRefusedWithoutDebt(MockHttpServletResponse response) throws Exception {
    assertThat(response.getStatus()).isEqualTo(404);
    assertThat(response.getContentAsString()).doesNotContain("debt");
  }

  @Test
  void aTopicAdminOfAnotherTenant_doesNotFindASeededTopic() throws Exception {
    when(tenantService.getRestrictedTenantDataNoCache(anyLong()))
        .thenReturn(new RestrictedTenantDTO().settings(new Settings().featureTopicsEnabled(true)));
    var debt = seededTopic("debt");
    var topic = objectMapper.createObjectNode();
    topic.set("name", objectMapper.createObjectNode().put("de", "Fremd"));
    topic.set("description", objectMapper.createObjectNode().put("de", "Fremd"));
    topic.put("status", "ACTIVE");
    topic.put("internalIdentifier", "fremd");

    mockMvc
        .perform(
            get("/topicadmin/" + debt.getId())
                .header("tenantId", "5")
                .with(authentication(topicAdminOfTenant5())))
        .andExpect(result -> assertRefusedWithoutDebt(result.getResponse()));
    mockMvc
        .perform(
            put("/topicadmin/" + debt.getId())
                .header("tenantId", "5")
                .with(authentication(topicAdminOfTenant5()))
                .contentType(APPLICATION_JSON)
                .content(topic.toString()))
        .andExpect(result -> assertRefusedWithoutDebt(result.getResponse()));

    assertThat(seededTopic("debt").getName()).doesNotContain("Fremd");
  }

  private ObjectNode adminTopic(Long id) throws Exception {
    var body =
        mockMvc
            .perform(
                get("/topicadmin/" + id)
                    .header("tenantId", "0")
                    .with(authentication(platformAdmin())))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return (ObjectNode) objectMapper.readTree(body);
  }

  private ResultActions putTopic(Long id, ObjectNode topic) throws Exception {
    return mockMvc.perform(
        put("/topicadmin/" + id)
            .header("tenantId", "0")
            .with(authentication(platformAdmin()))
            .contentType(APPLICATION_JSON)
            .content(topic.toString()));
  }

  private ResultActions saveCatalogue(ObjectNode catalogue, ObjectNode... topics) throws Exception {
    var change = objectMapper.createObjectNode();
    change.set("groups", catalogue.get("groups"));
    var changed = objectMapper.createArrayNode();
    for (ObjectNode topic : topics) {
      changed.add(topic);
    }
    change.set("topics", changed);
    return mockMvc.perform(
        put(CATALOGUE)
            .header("tenantId", "0")
            .with(authentication(platformAdmin()))
            .contentType(APPLICATION_JSON)
            .content(change.toString()));
  }

  private static ObjectNode catalogueTopic(JsonNode catalogue, Long id) {
    for (JsonNode topic : catalogue.get("topics")) {
      if (topic.get("id").asLong() == id) {
        return (ObjectNode) topic;
      }
    }
    throw new AssertionError("no topic " + id);
  }

  private static void expectNameTaken(ResultActions result, String germanName) throws Exception {
    result
        .andExpect(status().isConflict())
        .andExpect(header().string("X-Reason", "TOPIC_NAME_TAKEN"))
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(
            jsonPath("$.detail").value("A topic named \"" + germanName + "\" already exists"));
  }

  @Test
  void renamingATopicToTheNameOfAnother_isAConflict_andChangesNothing() throws Exception {
    var debt = seededTopic("debt");
    var pregnancy = adminTopic(seededTopic("pregnancy").getId());
    var topic = adminTopic(debt.getId());
    topic.set("name", pregnancy.get("name").deepCopy());

    expectNameTaken(putTopic(debt.getId(), topic), pregnancy.get("name").get("de").asText());

    assertThat(seededTopic("debt").getName()).isEqualTo(debt.getName());
  }

  @Test
  void renamingATopicToTheNameOfAnotherInOtherCase_isAConflict() throws Exception {
    var debt = seededTopic("debt");
    var pregnancy = adminTopic(seededTopic("pregnancy").getId());
    var topic = adminTopic(debt.getId());
    // The name is unique in all its languages together; only the German one changes case here.
    var upperCased = (ObjectNode) pregnancy.get("name").deepCopy();
    upperCased.put("de", upperCased.get("de").asText().toUpperCase(Locale.ROOT));
    topic.set("name", upperCased);

    expectNameTaken(putTopic(debt.getId(), topic), upperCased.get("de").asText());

    assertThat(seededTopic("debt").getName()).isEqualTo(debt.getName());
  }

  @Test
  void keepingTheOwnName_isNoConflict() throws Exception {
    var debt = seededTopic("debt");
    var topic = adminTopic(debt.getId());
    topic.put("status", "ACTIVE");

    putTopic(debt.getId(), topic).andExpect(status().isOk());
  }

  @Test
  void renamingATopicInTheCatalogueToTheNameOfAnother_isAConflict_andChangesNothing()
      throws Exception {
    var debt = seededTopic("debt");
    var catalogue = catalogue();
    var pregnancyName = catalogueTopic(catalogue, seededTopic("pregnancy").getId()).get("name");
    var renamed = catalogueTopic(catalogue, debt.getId());
    renamed.set("name", pregnancyName.deepCopy());

    expectNameTaken(saveCatalogue(catalogue, renamed), pregnancyName.get("de").asText());

    assertThat(seededTopic("debt").getName()).isEqualTo(debt.getName());
  }

  @Test
  void twoTopicsRenamedToTheSameNameInOneSave_areAConflict_andChangeNothing() throws Exception {
    var debt = seededTopic("debt");
    var pregnancy = seededTopic("pregnancy");
    var catalogue = catalogue();
    var first = catalogueTopic(catalogue, debt.getId());
    var second = catalogueTopic(catalogue, pregnancy.getId());
    first.set("name", objectMapper.createObjectNode().put("de", "Gleich"));
    second.set("name", objectMapper.createObjectNode().put("de", "gleich"));

    expectNameTaken(saveCatalogue(catalogue, first, second), "gleich");

    assertThat(seededTopic("debt").getName()).isEqualTo(debt.getName());
    assertThat(seededTopic("pregnancy").getName()).isEqualTo(pregnancy.getName());
  }

  @Test
  void twoTopicsSwappingTheirNamesInOneSave_isAllowed() throws Exception {
    var debt = seededTopic("debt");
    var pregnancy = seededTopic("pregnancy");
    var catalogue = catalogue();
    var first = catalogueTopic(catalogue, debt.getId());
    var second = catalogueTopic(catalogue, pregnancy.getId());
    var firstName = first.get("name").deepCopy();
    var secondName = second.get("name").deepCopy();
    first.set("name", secondName.deepCopy());
    second.set("name", firstName.deepCopy());

    saveCatalogue(catalogue, first, second).andExpect(status().isOk());

    var saved = catalogue();
    assertThat(catalogueTopic(saved, debt.getId()).get("name")).isEqualTo(secondName);
    assertThat(catalogueTopic(saved, pregnancy.getId()).get("name")).isEqualTo(firstName);
  }

  @Test
  void aTopicTakingTheOldNameOfAnotherRenamedTopic_isAllowed() throws Exception {
    var debt = seededTopic("debt");
    var pregnancy = seededTopic("pregnancy");
    var catalogue = catalogue();
    var first = catalogueTopic(catalogue, debt.getId());
    var second = catalogueTopic(catalogue, pregnancy.getId());
    var secondOldName = second.get("name").deepCopy();
    second.set("name", objectMapper.createObjectNode().put("de", "Ganz neu"));
    first.set("name", secondOldName);

    saveCatalogue(catalogue, first, second).andExpect(status().isOk());

    var saved = catalogue();
    assertThat(catalogueTopic(saved, debt.getId()).get("name")).isEqualTo(secondOldName);
    assertThat(catalogueTopic(saved, pregnancy.getId()).get("name").get("de").asText())
        .isEqualTo("Ganz neu");
  }

  @Test
  void aTopicThePlatformAdminCreates_belongsToTheDefaultTopicTenant() throws Exception {
    var topic = objectMapper.createObjectNode();
    topic.set("name", objectMapper.createObjectNode().put("de", "Neues Thema"));
    topic.set("description", objectMapper.createObjectNode().put("de", "Beschreibung"));
    topic.put("status", "ACTIVE");
    topic.put("internalIdentifier", "neues-thema");
    topic.put("slug", "neues-thema");
    var titles = objectMapper.createObjectNode();
    titles.set("short", objectMapper.createObjectNode().put("de", "Neues Thema"));
    titles.set("long", objectMapper.createObjectNode().put("de", "Neues Thema"));
    titles.put("welcome", "Neues Thema");
    titles.put("registrationDropdown", "Neues Thema");
    topic.set("titles", titles);

    mockMvc
        .perform(
            post("/topicadmin")
                .header("tenantId", "0")
                .with(authentication(platformAdmin()))
                .contentType(APPLICATION_JSON)
                .content(topic.toString()))
        .andExpect(status().isOk());

    assertThat(seededTopic("neues-thema").getTenantId()).isEqualTo(DEFAULT_TOPIC_TENANT);
  }
}
