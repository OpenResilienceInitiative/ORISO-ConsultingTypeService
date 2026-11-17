package de.caritas.cob.consultingtypeservice.api.service;

import static de.caritas.cob.consultingtypeservice.api.util.JsonConverter.convertMapFromJson;
import static de.caritas.cob.consultingtypeservice.api.util.JsonConverter.convertToJson;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException;
import de.caritas.cob.consultingtypeservice.api.model.TitlesMultilingualDTO;
import de.caritas.cob.consultingtypeservice.api.model.TopicCatalogueDTO;
import de.caritas.cob.consultingtypeservice.api.model.TopicCatalogueGroupDTO;
import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicGroupEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicMultilingualDTO;
import de.caritas.cob.consultingtypeservice.api.model.TopicStatus;
import de.caritas.cob.consultingtypeservice.api.repository.TopicGroupRepository;
import de.caritas.cob.consultingtypeservice.api.topicdefaults.DefaultTopicSeeder;
import de.caritas.cob.consultingtypeservice.api.topicdefaults.TopicIconPolicy;
import de.caritas.cob.consultingtypeservice.api.validation.InputSanitizer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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

  /**
   * Topics with a negative id are new: they are created first, and the categories may place them by
   * that negative id.
   */
  @Transactional
  public TopicCatalogueDTO saveCatalogue(final TopicCatalogueDTO change) {
    Map<Long, TopicEntity> topicsById =
        topicService.getAllTopics().stream()
            .collect(Collectors.toMap(TopicEntity::getId, Function.identity()));
    var groups =
        change.getGroups() == null ? List.<TopicCatalogueGroupDTO>of() : change.getGroups();
    var topics = change.getTopics() == null ? List.<TopicMultilingualDTO>of() : change.getTopics();
    var newTopicIds =
        topics.stream()
            .map(TopicMultilingualDTO::getId)
            .filter(id -> id != null && id < 0)
            .collect(Collectors.toSet());
    groups.forEach(group -> assertKnownTopics(group, topicsById, newTopicIds));

    Map<Long, Long> createdIds = new HashMap<>();
    var takenSlugs =
        topicsById.values().stream()
            .map(TopicEntity::getSlug)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(HashSet::new));
    for (TopicMultilingualDTO topic : topics) {
      if (topic.getId() != null && topic.getId() < 0) {
        if (createdIds.containsKey(topic.getId())) {
          throw new BadRequestException("Duplicate new topic " + topic.getId());
        }
        createdIds.put(topic.getId(), createTopic(topic, takenSlugs).getId());
      } else if (topic.getId() == null || !topicsById.containsKey(topic.getId())) {
        throw new BadRequestException("Unknown topic " + topic.getId());
      } else {
        topicServiceFacade.updateTopic(topic.getId(), topic);
      }
    }
    if (!createdIds.isEmpty()) {
      topicService.getAllTopics().forEach(topic -> topicsById.put(topic.getId(), topic));
      groups.forEach(
          group ->
              group.setTopicIds(
                  group.getTopicIds().stream()
                      .map(id -> createdIds.getOrDefault(id, id))
                      .collect(Collectors.toList())));
    }
    saveGroups(groups, topicsById);
    return getCatalogue();
  }

  /**
   * The admin dialog sends name, description and icon only; slug, internal identifier, titles and
   * status are derived from the German name.
   */
  private TopicMultilingualDTO createTopic(TopicMultilingualDTO topic, Set<String> takenSlugs) {
    var germanName = topic.getName() == null ? null : topic.getName().get("de");
    if (germanName == null || germanName.isBlank()) {
      throw new BadRequestException("A new topic needs a German name");
    }
    var slug = uniqueSlug(TopicSlugs.slugify(germanName), takenSlugs);
    takenSlugs.add(slug);
    topic.setId(null);
    topic.setSlug(slug);
    topic.setInternalIdentifier(slug);
    if (topic.getStatus() == null || topic.getStatus().isBlank()) {
      topic.setStatus(TopicStatus.ACTIVE.name());
    }
    if (topic.getDescription() == null) {
      topic.setDescription(Map.of());
    }
    if (topic.getTitles() == null) {
      // Titles are not cleaned by the topic sanitizer, so derive them from the cleaned name.
      var cleanName = inputSanitizer.sanitize(topic.getName());
      topic.setTitles(
          new TitlesMultilingualDTO()
              ._short(cleanName)
              ._long(cleanName)
              .welcome(cleanName.get("de"))
              .registrationDropdown(cleanName.get("de")));
    }
    return topicServiceFacade.createTopic(topic);
  }

  private static String uniqueSlug(String base, Set<String> takenSlugs) {
    if (!takenSlugs.contains(base)) {
      return base;
    }
    for (int suffix = 2; ; suffix++) {
      var candidate = TopicSlugs.withSuffix(base, suffix);
      if (!takenSlugs.contains(candidate)) {
        return candidate;
      }
    }
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
      TopicCatalogueGroupDTO group, Map<Long, TopicEntity> topicsById, Set<Long> newTopicIds) {
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
      if (!topicsById.containsKey(topicId) && !newTopicIds.contains(topicId)) {
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
