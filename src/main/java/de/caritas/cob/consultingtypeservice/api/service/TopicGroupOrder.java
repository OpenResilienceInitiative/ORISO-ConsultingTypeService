package de.caritas.cob.consultingtypeservice.api.service;

import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicGroupEntity;
import de.caritas.cob.consultingtypeservice.api.repository.TopicGroupRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Reads and writes the order of the topics inside a category. */
@Component
@RequiredArgsConstructor
public class TopicGroupOrder {

  private final @NonNull TopicGroupRepository topicGroupRepository;

  /**
   * The ids of the category's topics in display order, limited to the topics the caller may see
   * (the tenant filter already narrowed the group's topic set).
   */
  public List<Long> topicIdsInOrder(TopicGroupEntity group) {
    Set<Long> visible =
        group.getTopicEntities().stream()
            .filter(Objects::nonNull)
            .map(TopicEntity::getId)
            .collect(Collectors.toSet());
    var ordered =
        topicGroupRepository.findTopicIdsInOrder(group.getId()).stream()
            .filter(visible::contains)
            .distinct()
            .collect(Collectors.toCollection(ArrayList::new));
    // Placements not flushed to the join table yet keep their id order at the end.
    visible.stream().filter(id -> !ordered.contains(id)).sorted().forEach(ordered::add);
    return ordered;
  }

  /** Stores the given order; the placements must exist already (save the group first). */
  public void store(Long groupId, List<Long> topicIds) {
    for (int position = 0; position < topicIds.size(); position++) {
      topicGroupRepository.setTopicPosition(groupId, topicIds.get(position), position);
    }
  }
}
