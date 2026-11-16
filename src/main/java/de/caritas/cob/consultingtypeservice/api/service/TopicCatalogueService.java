package de.caritas.cob.consultingtypeservice.api.service;

import static de.caritas.cob.consultingtypeservice.api.util.JsonConverter.convertMapFromJson;
import static de.caritas.cob.consultingtypeservice.api.util.JsonConverter.convertToJson;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException;
import de.caritas.cob.consultingtypeservice.api.model.TopicCatalogueDTO;
import de.caritas.cob.consultingtypeservice.api.model.TopicCatalogueGroupDTO;
import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicGroupEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicMultilingualDTO;
import de.caritas.cob.consultingtypeservice.api.repository.TopicGroupRepository;
import de.caritas.cob.consultingtypeservice.api.topicdefaults.DefaultTopicSeeder;
import de.caritas.cob.consultingtypeservice.api.topicdefaults.TopicIconPolicy;
import de.caritas.cob.consultingtypeservice.api.validation.InputSanitizer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The platform admin's topic catalogue: categories (order, names, picture, topic order) and the
 * topics with all their translations, read and saved in one go.
 */
@Service
@RequiredArgsConstructor
public class TopicCatalogueService {

  private final @NonNull TopicGroupRepository topicGroupRepository;
  private final @NonNull TopicService topicService;
  private final @NonNull TopicServiceFacade topicServiceFacade;
  private final @NonNull DefaultTopicSeeder defaultTopicSeeder;
  private final @NonNull TopicGroupOrder topicGroupOrder;
  private final @NonNull InputSanitizer inputSanitizer;

  @Transactional(readOnly = true)
  public TopicCatalogueDTO getCatalogue() {
    return new TopicCatalogueDTO()
        .groups(
            topicGroupRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .map(this::toGroupDTO)
                .collect(Collectors.toList()))
        .topics(topicServiceFacade.getAllTopicsMultilingual());
  }

  @Transactional
  public TopicCatalogueDTO saveCatalogue(final TopicCatalogueDTO change) {
    Map<Long, TopicEntity> topicsById =
        topicService.getAllTopics().stream()
            .collect(Collectors.toMap(TopicEntity::getId, Function.identity()));
    var groups =
        change.getGroups() == null ? List.<TopicCatalogueGroupDTO>of() : change.getGroups();
    groups.forEach(group -> assertKnownTopics(group, topicsById));

    if (change.getTopics() != null) {
      for (TopicMultilingualDTO topic : change.getTopics()) {
        if (topic.getId() == null || !topicsById.containsKey(topic.getId())) {
          throw new BadRequestException("Unknown topic " + topic.getId());
        }
        topicServiceFacade.updateTopic(topic.getId(), topic);
      }
    }
    saveGroups(groups, topicsById);
    return getCatalogue();
  }

  private void saveGroups(List<TopicCatalogueGroupDTO> groups, Map<Long, TopicEntity> topics) {
    Map<Long, TopicGroupEntity> existing =
        topicGroupRepository.findAll().stream()
            .collect(Collectors.toMap(TopicGroupEntity::getId, Function.identity()));
    var kept =
        groups.stream()
            .map(TopicCatalogueGroupDTO::getId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    existing.values().stream()
        .filter(group -> !kept.contains(group.getId()))
        .forEach(topicGroupRepository::delete);

    var now = LocalDateTime.now(ZoneOffset.UTC);
    for (int position = 0; position < groups.size(); position++) {
      var dto = groups.get(position);
      var entity =
          dto.getId() != null && existing.containsKey(dto.getId())
              ? existing.get(dto.getId())
              : TopicGroupEntity.builder().createDate(now).build();
      entity.setName(convertToJson(inputSanitizer.sanitize(dto.getName())));
      entity.setIcon(resolveIcon(dto));
      entity.setSortOrder(position);
      entity.setCustomized(true);
      entity.setUpdateDate(now);
      var orderedIds = new ArrayList<>(new LinkedHashSet<>(dto.getTopicIds()));
      entity.setTopicEntities(
          orderedIds.stream().map(topics::get).collect(Collectors.toCollection(HashSet::new)));
      var saved = topicGroupRepository.saveAndFlush(entity);
      topicGroupOrder.store(saved.getId(), orderedIds);
    }
  }

  /** Empty removes the own picture: a Caritas category shows its shipped picture again. */
  private String resolveIcon(TopicCatalogueGroupDTO dto) {
    var icon = dto.getIcon();
    if (icon == null || icon.isEmpty()) {
      return defaultTopicSeeder.defaultGroupIcon(dto.getName()).orElse(null);
    }
    TopicIconPolicy.assertAllowed(icon);
    return icon;
  }

  private static void assertKnownTopics(
      TopicCatalogueGroupDTO group, Map<Long, TopicEntity> topicsById) {
    if (group.getName() == null || group.getName().isEmpty()) {
      throw new BadRequestException("A category needs a name");
    }
    if (group.getName().values().stream().anyMatch(name -> name == null || name.length() > 200)) {
      throw new BadRequestException("A category name has at most 200 characters per language");
    }
    if (group.getTopicIds() == null) {
      throw new BadRequestException("A category needs a topic list");
    }
    for (Long topicId : group.getTopicIds()) {
      if (!topicsById.containsKey(topicId)) {
        throw new BadRequestException("Unknown topic " + topicId);
      }
    }
  }

  private TopicCatalogueGroupDTO toGroupDTO(TopicGroupEntity group) {
    return new TopicCatalogueGroupDTO()
        .id(group.getId())
        .name(convertMapFromJson(group.getName()))
        .icon(group.getIcon())
        .topicIds(topicGroupOrder.topicIdsInOrder(group));
  }
}
