package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.TopicNameConflictException;
import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.service.TopicNameUniqueness.NamedTopic;
import de.caritas.cob.consultingtypeservice.api.validation.InputSanitizer;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class TopicNameUniquenessTest {

  private final TopicNameUniqueness uniqueness = new TopicNameUniqueness(new InputSanitizer());

  private static TopicEntity topic(long id, long tenantId, String germanName) {
    var topic = new TopicEntity();
    topic.setId(id);
    topic.setTenantId(tenantId);
    topic.setName("{\"de\":\"" + germanName + "\"}");
    return topic;
  }

  private final TopicEntity debt = topic(1, 1, "Schulden");
  private final TopicEntity family = topic(2, 1, "Familie");
  private final TopicEntity foreign = topic(3, 2, "Wohnen");
  private final List<TopicEntity> stored = List.of(debt, family, foreign);

  @Test
  void aRenameToAnotherTopicsName_isAConflictWithTheGermanName() {
    var changes = List.of(new NamedTopic(1L, 1L, Map.of("de", "FAMILIE")));

    assertThatThrownBy(() -> uniqueness.assertUniqueAfter(stored, changes))
        .isInstanceOf(TopicNameConflictException.class)
        .hasMessage("A topic named \"FAMILIE\" already exists");
  }

  @Test
  void keepingTheOwnName_orTakingANameOfAnotherTenant_isNoConflict() {
    var changes =
        List.of(
            new NamedTopic(1L, 1L, Map.of("de", "Schulden")),
            new NamedTopic(2L, 1L, Map.of("de", "Wohnen")));

    assertThatCode(() -> uniqueness.assertUniqueAfter(stored, changes)).doesNotThrowAnyException();
  }

  @Test
  void aNewTopicNamedLikeAStoredOne_isAConflict() {
    var changes = List.of(new NamedTopic(null, 1L, Map.of("de", "schulden")));

    assertThatThrownBy(() -> uniqueness.assertUniqueAfter(stored, changes))
        .isInstanceOf(TopicNameConflictException.class);
  }

  @Test
  void twoTopicsEndingWithTheSameName_areAConflict() {
    var changes =
        List.of(
            new NamedTopic(1L, 1L, Map.of("de", "Neu")),
            new NamedTopic(2L, 1L, Map.of("de", "neu")));

    assertThatThrownBy(() -> uniqueness.assertUniqueAfter(stored, changes))
        .isInstanceOf(TopicNameConflictException.class);
  }

  @Test
  void aSwap_isNoConflict_andBothTopicsReleaseTheirNamesFirst() {
    var changes =
        List.of(
            new NamedTopic(1L, 1L, Map.of("de", "Familie")),
            new NamedTopic(2L, 1L, Map.of("de", "Schulden")));

    assertThatCode(() -> uniqueness.assertUniqueAfter(stored, changes)).doesNotThrowAnyException();
    assertThat(uniqueness.namesToRelease(Map.of(1L, debt, 2L, family), changes))
        .containsExactlyInAnyOrder(debt, family);
  }

  @Test
  void aRenameWhoseOldNameNobodyTakes_releasesNothing() {
    var changes = List.of(new NamedTopic(1L, 1L, Map.of("de", "Neu")));

    assertThat(uniqueness.namesToRelease(Map.of(1L, debt, 2L, family), changes)).isEmpty();
  }

  @Test
  void topicsWithoutATenant_areNeverAConflict_likeInTheDatabase() {
    var withoutTenant = new TopicEntity();
    withoutTenant.setId(4L);
    withoutTenant.setName("{\"de\":\"Ohne\"}");
    var changes = List.of(new NamedTopic(null, null, Map.of("de", "Ohne")));

    assertThatCode(() -> uniqueness.assertUniqueAfter(List.of(withoutTenant), changes))
        .doesNotThrowAnyException();
  }
}
