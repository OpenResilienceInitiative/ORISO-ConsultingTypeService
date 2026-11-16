package de.caritas.cob.consultingtypeservice.api.topicdefaults;

import static de.caritas.cob.consultingtypeservice.api.util.JsonConverter.convertMapFromJson;
import static de.caritas.cob.consultingtypeservice.api.util.JsonConverter.convertToJson;

import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicGroupEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicStatus;
import de.caritas.cob.consultingtypeservice.api.repository.TopicGroupRepository;
import de.caritas.cob.consultingtypeservice.api.repository.TopicRepository;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantResolver;
import de.caritas.cob.consultingtypeservice.api.topicdefaults.DefaultTopicCatalogue.DefaultTopic;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Makes sure every installation starts with the Caritas default topics.
 *
 * <ul>
 *   <li>Empty topic table: all default topics and categories are created.
 *   <li>Topics exist already (for example from the Helm bootstrap, which only knew German and
 *       English): default topics a platform admin never saved get their missing icon and missing
 *       languages. Nothing that is already there is changed.
 *   <li>A topic a platform admin saved ({@code customized}) is never touched again.
 * </ul>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultTopicSeeder implements ApplicationRunner {

  private final DefaultTopicCatalogue catalogue;
  private final TopicRepository topicRepository;
  private final TopicGroupRepository topicGroupRepository;
  private final TransactionTemplate transactionTemplate;

  @Value("${topics.defaults.enabled:true}")
  private boolean enabled;

  @Value("${topics.defaults.tenant-id:1}")
  private Long tenantId;

  @Value("${topics.defaults.icon-base-url:/service/topic/public/icons/}")
  private String iconBaseUrl;

  @Override
  public void run(ApplicationArguments args) {
    if (!enabled) {
      log.info("Default topic seed is switched off (topics.defaults.enabled=false)");
      return;
    }
    try {
      seedOrFillGaps();
    } catch (RuntimeException e) {
      // A failed seed must never keep the service from starting; the next start tries again.
      log.error("Default topic seed failed", e);
    }
  }

  public void seedOrFillGaps() {
    var previousTenant = TenantContext.getCurrentTenant();
    TenantContext.setCurrentTenant(TenantResolver.TECHNICAL_TENANT_ID);
    try {
      transactionTemplate.executeWithoutResult(status -> seedOrFillGapsInTransaction());
    } finally {
      TenantContext.setCurrentTenant(previousTenant);
    }
  }

  private void seedOrFillGapsInTransaction() {
    List<TopicEntity> existing = topicRepository.findAll();
    if (existing.isEmpty()) {
      seedAll();
    } else {
      fillGaps(existing);
    }
  }

  private void seedAll() {
    var now = LocalDateTime.now(ZoneOffset.UTC);
    Map<String, TopicEntity> bySlug =
        catalogue.topics().stream()
            .map(defaultTopic -> topicRepository.save(newTopic(defaultTopic, now)))
            .collect(
                Collectors.toMap(
                    TopicEntity::getSlug, Function.identity(), (a, b) -> a, LinkedHashMap::new));
    if (topicGroupRepository.count() == 0) {
      catalogue
          .groups()
          .forEach(
              group ->
                  topicGroupRepository.save(
                      TopicGroupEntity.builder()
                          .name(convertToJson(group.getName()))
                          .createDate(now)
                          .updateDate(now)
                          .topicEntities(
                              group.getTopics().stream()
                                  .map(placement -> bySlug.get(placement.getSlug()))
                                  .filter(Objects::nonNull)
                                  .collect(Collectors.toCollection(HashSet::new)))
                          .build()));
    }
    log.info(
        "Seeded {} default topics and {} categories", bySlug.size(), catalogue.groups().size());
  }

  private TopicEntity newTopic(DefaultTopic defaultTopic, LocalDateTime now) {
    return TopicEntity.builder()
        .tenantId(tenantId)
        .slug(defaultTopic.getSlug())
        .internalIdentifier(defaultTopic.getInternalIdentifier())
        .name(convertToJson(defaultTopic.getName()))
        .description(convertToJson(defaultTopic.getDescription()))
        .status(TopicStatus.ACTIVE)
        .icon(iconUrl(defaultTopic))
        .titlesShort(convertToJson(defaultTopic.getTitles().getShortTitles()))
        .titlesLong(convertToJson(defaultTopic.getTitles().getLongTitles()))
        .titlesWelcome(defaultTopic.getTitles().getWelcome())
        .titlesDropdown(defaultTopic.getTitles().getRegistrationDropdown())
        .sendNextStepMessage(false)
        .customized(false)
        .createDate(now)
        .updateDate(now)
        .build();
  }

  private void fillGaps(List<TopicEntity> existing) {
    int filled = 0;
    for (TopicEntity topic : existing) {
      if (Boolean.TRUE.equals(topic.getCustomized()) || topic.getSlug() == null) {
        continue;
      }
      var defaultTopic = catalogue.findBySlug(topic.getSlug());
      if (defaultTopic.isPresent() && fillGaps(topic, defaultTopic.get())) {
        topicRepository.save(topic);
        filled++;
      }
    }
    if (filled > 0) {
      log.info("Added missing icons or languages to {} default topics", filled);
    }
  }

  private boolean fillGaps(TopicEntity topic, DefaultTopic defaultTopic) {
    boolean changed = false;
    if (topic.getIcon() == null || topic.getIcon().isBlank()) {
      topic.setIcon(iconUrl(defaultTopic));
      changed = true;
    }
    changed |= addMissingLanguages(topic.getName(), defaultTopic.getName(), topic::setName);
    changed |=
        addMissingLanguages(
            topic.getDescription(), defaultTopic.getDescription(), topic::setDescription);
    changed |=
        addMissingLanguages(
            topic.getTitlesShort(),
            defaultTopic.getTitles().getShortTitles(),
            topic::setTitlesShort);
    changed |=
        addMissingLanguages(
            topic.getTitlesLong(), defaultTopic.getTitles().getLongTitles(), topic::setTitlesLong);
    return changed;
  }

  /** Adds the languages the stored text lacks; languages already stored are kept as they are. */
  private static boolean addMissingLanguages(
      String storedJson, Map<String, String> defaults, Consumer<String> setter) {
    if (defaults == null || defaults.isEmpty()) {
      return false;
    }
    Map<String, String> stored =
        storedJson == null || storedJson.isBlank()
            ? new LinkedHashMap<>()
            : new LinkedHashMap<>(convertMapFromJson(storedJson));
    boolean changed = false;
    for (var entry : defaults.entrySet()) {
      if (!stored.containsKey(entry.getKey())) {
        stored.put(entry.getKey(), entry.getValue());
        changed = true;
      }
    }
    if (changed) {
      setter.accept(convertToJson(stored));
    }
    return changed;
  }

  public String iconUrl(DefaultTopic defaultTopic) {
    return iconBaseUrl + defaultTopic.getIcon();
  }
}
