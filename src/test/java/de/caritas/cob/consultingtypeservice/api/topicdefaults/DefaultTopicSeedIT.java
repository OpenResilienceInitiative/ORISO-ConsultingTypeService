package de.caritas.cob.consultingtypeservice.api.topicdefaults;

import static de.caritas.cob.consultingtypeservice.testHelper.TopicPathConstants.PATH_GET_PUBLIC_TOPIC_LIST;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicStatus;
import de.caritas.cob.consultingtypeservice.api.repository.TopicGroupRepository;
import de.caritas.cob.consultingtypeservice.api.repository.TopicRepository;
import de.caritas.cob.consultingtypeservice.api.service.TenantService;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Every installation starts with the Caritas default topics: an empty topic table is filled at
 * startup, and default topics a platform admin never edited get missing icons and languages.
 */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@TestPropertySource(properties = "multitenancy.enabled=false")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DefaultTopicSeedIT {

  private static final String ICON_BASE = "/service/topic/public/icons/";

  @Autowired private WebApplicationContext context;
  @Autowired private TopicRepository topicRepository;
  @Autowired private TopicGroupRepository topicGroupRepository;
  @Autowired private DefaultTopicSeeder defaultTopicSeeder;

  @MockitoBean TenantService tenantService;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    TenantContext.clear();
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  private void givenNoTopicsAtAll() {
    topicGroupRepository.deleteAll();
    topicRepository.deleteAll();
  }

  @Test
  void emptyInstallation_getsTheTwentyCaritasDefaultTopicsWithIcons() throws Exception {
    givenNoTopicsAtAll();

    defaultTopicSeeder.seedOrFillGaps();

    mockMvc
        .perform(get(PATH_GET_PUBLIC_TOPIC_LIST).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(20)))
        .andExpect(jsonPath("$[?(@.slug == 'pregnancy')].name").value("Schwangerschaft"))
        .andExpect(
            jsonPath("$[?(@.slug == 'pregnancy')].icon")
                .value(ICON_BASE + "t-01-schwangerschaft.png"));
  }

  @Test
  void defaultTopics_comeInAllSixPlatformLanguages() throws Exception {
    givenNoTopicsAtAll();

    defaultTopicSeeder.seedOrFillGaps();

    mockMvc
        .perform(
            get(PATH_GET_PUBLIC_TOPIC_LIST)
                .cookie(new Cookie("lang", "fr"))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[?(@.slug == 'debt')].name").value("Endettement"));
    mockMvc
        .perform(
            get(PATH_GET_PUBLIC_TOPIC_LIST)
                .cookie(new Cookie("lang", "ti"))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[?(@.slug == 'debt')].name").value("ዕዳ"));
  }

  @Test
  void defaultCategories_areSeededWithTheirTopics() throws Exception {
    givenNoTopicsAtAll();

    defaultTopicSeeder.seedOrFillGaps();

    mockMvc
        .perform(get("/topic-groups").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.items", hasSize(6)));
  }

  @Test
  void secondStart_addsNothing() throws Exception {
    givenNoTopicsAtAll();
    defaultTopicSeeder.seedOrFillGaps();

    defaultTopicSeeder.seedOrFillGaps();

    mockMvc
        .perform(get(PATH_GET_PUBLIC_TOPIC_LIST).accept(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(20)));
  }

  @Test
  void untouchedDefaultTopicFromAnOlderSeed_getsIconAndMissingLanguages() throws Exception {
    givenNoTopicsAtAll();
    topicRepository.save(olderSeedTopic("pregnancy", false));

    defaultTopicSeeder.seedOrFillGaps();

    mockMvc
        .perform(
            get(PATH_GET_PUBLIC_TOPIC_LIST)
                .cookie(new Cookie("lang", "tr"))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].name").value("Hamilelik"))
        .andExpect(jsonPath("$[0].icon").value(ICON_BASE + "t-01-schwangerschaft.png"));
    mockMvc
        .perform(get(PATH_GET_PUBLIC_TOPIC_LIST).accept(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].name").value("Alte Schwangerschaft"));
  }

  @Test
  void topicThePlatformAdminChanged_staysExactlyAsSaved() throws Exception {
    givenNoTopicsAtAll();
    topicRepository.save(olderSeedTopic("pregnancy", true));

    defaultTopicSeeder.seedOrFillGaps();

    mockMvc
        .perform(
            get(PATH_GET_PUBLIC_TOPIC_LIST)
                .cookie(new Cookie("lang", "tr"))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].name").value("Alte Schwangerschaft"))
        .andExpect(jsonPath("$[0].icon").doesNotExist());
  }

  @Test
  void defaultIcon_isServedWithoutLogin() throws Exception {
    mockMvc
        .perform(get("/topic/public/icons/t-05-schulden.png"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.IMAGE_PNG));
  }

  @Test
  void unknownIcon_isNotFoundAndPathsCannotEscape() throws Exception {
    mockMvc.perform(get("/topic/public/icons/nope.png")).andExpect(status().isNotFound());
    mockMvc
        .perform(get("/topic/public/icons/..%2Fcaritas-default-topics.json"))
        .andExpect(status().is4xxClientError());
  }

  private static TopicEntity olderSeedTopic(String slug, boolean customized) {
    return TopicEntity.builder()
        .tenantId(1L)
        .name("{\"de\": \"Alte Schwangerschaft\", \"en\": \"Old pregnancy\"}")
        .description("{\"de\": \"Alt\", \"en\": \"Old\"}")
        .status(TopicStatus.ACTIVE)
        .internalIdentifier(slug)
        .slug(slug)
        .customized(customized)
        .createDate(LocalDateTime.now())
        .titlesShort("{\"de\": \"Alt\"}")
        .titlesLong("{\"de\": \"Alt\"}")
        .titlesWelcome("Willkommen")
        .titlesDropdown("Alt")
        .build();
  }
}
