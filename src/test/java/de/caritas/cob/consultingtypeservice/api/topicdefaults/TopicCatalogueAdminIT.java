package de.caritas.cob.consultingtypeservice.api.topicdefaults;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.controller.AuthenticationMockBuilder;
import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.repository.TopicGroupRepository;
import de.caritas.cob.consultingtypeservice.api.repository.TopicRepository;
import de.caritas.cob.consultingtypeservice.api.service.TenantService;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import java.util.ArrayList;
import java.util.List;
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
 * The platform admin's topic catalogue: categories with their names in every language, their
 * picture and the order of their topics, read and saved in one go. The registration reads the saved
 * order and pictures from the public topic-groups list.
 */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@TestPropertySource(properties = "multitenancy.enabled=false")
@TestPropertySource(properties = "feature.multitenancy.with.single.domain.enabled=true")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TopicCatalogueAdminIT {

  private static final String CATALOGUE = "/topicadmin/catalogue";
  private static final String OWN_ICON = "data:image/png;base64,iVBORw0KGgo=";

  @Autowired private WebApplicationContext context;
  @Autowired private TopicRepository topicRepository;
  @Autowired private TopicGroupRepository topicGroupRepository;
  @Autowired private DefaultTopicSeeder defaultTopicSeeder;

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

  private ObjectNode catalogue() throws Exception {
    var body =
        mockMvc
            .perform(get(CATALOGUE).with(authentication(platformAdmin())))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    return (ObjectNode) objectMapper.readTree(body);
  }

  private static ObjectNode group(JsonNode catalogue, String germanName) {
    for (JsonNode group : catalogue.get("groups")) {
      if (germanName.equals(group.get("name").get("de").asText())) {
        return (ObjectNode) group;
      }
    }
    throw new AssertionError("no group " + germanName);
  }

  private static List<Long> ids(JsonNode array) {
    var ids = new ArrayList<Long>();
    array.forEach(node -> ids.add(node.asLong()));
    return ids;
  }

  @Test
  void catalogue_showsTheCaritasCategoriesInOrderWithTheirTopicsAndPictures() throws Exception {
    var catalogue = catalogue();

    assertThat(catalogue.get("groups")).hasSize(6);
    assertThat(catalogue.get("groups").get(0).get("name").get("de").asText()).isEqualTo("Alter");
    var family = group(catalogue, "Kinder, Jugend, Erwachsene, Schwangerschaft und Familie");
    assertThat(ids(family.get("topicIds")).subList(0, 2))
        .containsExactly(idOf("general-social-counselling"), idOf("parents-and-family"));
    assertThat(family.get("icon").asText()).isEqualTo("/service/topic/public/icons/cat-01.png");
    assertThat(catalogue.get("topics")).hasSize(20);
    assertThat(catalogue.get("topics").get(0).get("name").size()).isEqualTo(6);
  }

  @Test
  void savingTheCatalogue_keepsOrderNamesAndPictures_andTheRegistrationSeesThem() throws Exception {
    var catalogue = catalogue();
    var migration = group(catalogue, "Migration");
    var reversed = objectMapper.createArrayNode();
    var migrationIds = ids(migration.get("topicIds"));
    reversed.add(migrationIds.get(1)).add(migrationIds.get(0));
    migration.set("topicIds", reversed);
    ((ObjectNode) migration.get("name")).put("fr", "Migration et intégration");
    migration.put("icon", OWN_ICON);
    var groups = (ArrayNode) catalogue.get("groups");
    // Move "Migration" to the front.
    var reordered = objectMapper.createArrayNode().add(migration);
    groups.forEach(
        node -> {
          if (node != migration) {
            reordered.add(node);
          }
        });
    var change = objectMapper.createObjectNode();
    change.set("groups", reordered);
    change.set("topics", objectMapper.createArrayNode());

    mockMvc
        .perform(
            put(CATALOGUE)
                .with(authentication(platformAdmin()))
                .contentType(APPLICATION_JSON)
                .content(change.toString()))
        .andExpect(status().isOk());

    var saved = catalogue();
    assertThat(saved.get("groups").get(0).get("name").get("fr").asText())
        .isEqualTo("Migration et intégration");
    assertThat(ids(saved.get("groups").get(0).get("topicIds")))
        .containsExactly(migrationIds.get(1), migrationIds.get(0));
    mockMvc
        .perform(get("/topic-groups").accept(APPLICATION_JSON))
        .andExpect(jsonPath("$.data.items", hasSize(6)))
        .andExpect(jsonPath("$.data.items[0].name").value("Migration"))
        .andExpect(jsonPath("$.data.items[0].icon").value(OWN_ICON))
        .andExpect(jsonPath("$.data.items[0].topicIds[0]").value(migrationIds.get(1)));
  }

  @Test
  void removingACategory_keepsItsTopics() throws Exception {
    var catalogue = catalogue();
    var groups = (ArrayNode) catalogue.get("groups");
    groups.remove(0);
    var change = objectMapper.createObjectNode();
    change.set("groups", groups);
    change.set("topics", objectMapper.createArrayNode());

    mockMvc
        .perform(
            put(CATALOGUE)
                .with(authentication(platformAdmin()))
                .contentType(APPLICATION_JSON)
                .content(change.toString()))
        .andExpect(status().isOk());

    var saved = catalogue();
    assertThat(saved.get("groups")).hasSize(5);
    assertThat(saved.get("topics")).hasSize(20);
  }

  @Test
  void topicsChangedInTheCatalogue_areSavedWithTheirTranslationsAndIcon() throws Exception {
    var catalogue = catalogue();
    ObjectNode debt = null;
    for (JsonNode topic : catalogue.get("topics")) {
      if ("debt".equals(topic.get("slug").asText())) {
        debt = (ObjectNode) topic;
      }
    }
    ((ObjectNode) debt.get("name")).put("ti", "ዕዳ ምኽሪ");
    debt.put("icon", OWN_ICON);
    var change = objectMapper.createObjectNode();
    change.set("groups", catalogue.get("groups"));
    change.set("topics", objectMapper.createArrayNode().add(debt));

    mockMvc
        .perform(
            put(CATALOGUE)
                .with(authentication(platformAdmin()))
                .contentType(APPLICATION_JSON)
                .content(change.toString()))
        .andExpect(status().isOk());

    var stored = topicRepository.findById(idOf("debt")).orElseThrow();
    assertThat(stored.getName()).contains("ዕዳ ምኽሪ");
    assertThat(stored.getIcon()).isEqualTo(OWN_ICON);
    assertThat(stored.getCustomized()).isTrue();
  }

  @Test
  void traegerAdmin_cannotSaveTheCatalogue() throws Exception {
    var change = objectMapper.createObjectNode();
    change.set("groups", objectMapper.createArrayNode());
    change.set("topics", objectMapper.createArrayNode());

    mockMvc
        .perform(
            put(CATALOGUE)
                .with(authentication(traegerAdmin()))
                .contentType(APPLICATION_JSON)
                .content(change.toString()))
        .andExpect(status().isForbidden());
  }
}
