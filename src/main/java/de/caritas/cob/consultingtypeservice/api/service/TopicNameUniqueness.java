package de.caritas.cob.consultingtypeservice.api.service;

import static de.caritas.cob.consultingtypeservice.api.util.JsonConverter.convertMapFromJson;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.TopicNameConflictException;
import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.validation.InputSanitizer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * The database allows a topic name (all its languages together) only once per tenant and compares
 * it case-insensitively (MariaDB). This check answers 409 before anything is written, instead of a
 * 500 from the unique constraint. It looks at the state after a whole change, so topics that swap
 * their names in one save pass.
 */
@Component
@RequiredArgsConstructor
public class TopicNameUniqueness {

  private final @NonNull InputSanitizer inputSanitizer;

  /**
   * A topic's name after a change.
   *
   * @param id the stored topic's id, null for a new topic
   * @param tenantId the tenant the topic belongs to
   * @param name the name as sent; sanitized here as it will be stored
   */
  public record NamedTopic(Long id, Long tenantId, Map<String, String> name) {}

  private record TenantName(Long tenantId, Map<String, String> name) {}

  /**
   * @param stored the topics as stored, at least those of every tenant the changes touch
   * @param changes the new names; a stored topic not among them keeps its name
   * @throws TopicNameConflictException if two topics of one tenant would end up with the same name.
   *     Topics without a tenant are never compared: the constraint treats a missing tenant as
   *     distinct, so the database accepts their duplicate names.
   */
  public void assertUniqueAfter(Collection<TopicEntity> stored, List<NamedTopic> changes) {
    var changedIds =
        changes.stream().map(NamedTopic::id).filter(Objects::nonNull).collect(Collectors.toSet());
    Set<TenantName> taken = new HashSet<>();
    stored.stream()
        .filter(topic -> topic.getTenantId() != null)
        .filter(topic -> !changedIds.contains(topic.getId()))
        .forEach(
            topic ->
                taken.add(
                    new TenantName(
                        topic.getTenantId(), comparable(convertMapFromJson(topic.getName())))));
    for (NamedTopic change : changes) {
      if (change.tenantId() == null) {
        continue;
      }
      var name = sanitized(change.name());
      if (!taken.add(new TenantName(change.tenantId(), comparable(name)))) {
        throw new TopicNameConflictException(displayName(name));
      }
    }
  }

  /**
   * The stored topics whose current name another topic takes in the same change (a swap, or a new
   * topic named like a renamed one). They must release their name before the others are written, or
   * the database's unique constraint fires mid-save although the end state is unique.
   *
   * @param stored the stored topics by id
   * @param changes the new names, already checked with {@link #assertUniqueAfter}
   */
  public List<TopicEntity> namesToRelease(Map<Long, TopicEntity> stored, List<NamedTopic> changes) {
    return changes.stream()
        .filter(change -> change.id() != null && stored.containsKey(change.id()))
        .map(change -> stored.get(change.id()))
        .filter(topic -> isRenamed(topic, changes))
        .filter(topic -> isTakenByAnother(topic, changes))
        .distinct()
        .collect(Collectors.toList());
  }

  private boolean isRenamed(TopicEntity topic, List<NamedTopic> changes) {
    var storedName = comparable(convertMapFromJson(topic.getName()));
    return changes.stream()
        .filter(change -> topic.getId().equals(change.id()))
        .anyMatch(change -> !storedName.equals(comparable(sanitized(change.name()))));
  }

  private boolean isTakenByAnother(TopicEntity topic, List<NamedTopic> changes) {
    var storedName = comparable(convertMapFromJson(topic.getName()));
    return changes.stream()
        .filter(change -> !topic.getId().equals(change.id()))
        .filter(change -> Objects.equals(topic.getTenantId(), change.tenantId()))
        .anyMatch(change -> storedName.equals(comparable(sanitized(change.name()))));
  }

  /** The sanitizer cleans a map in place; the caller's name stays as sent. */
  private Map<String, String> sanitized(Map<String, String> name) {
    return name == null ? Map.of() : inputSanitizer.sanitize(new HashMap<>(name));
  }

  private static Map<String, String> comparable(Map<String, String> name) {
    if (name == null) {
      return Map.of();
    }
    return name.entrySet().stream()
        .filter(entry -> entry.getValue() != null)
        .collect(
            Collectors.toMap(
                Map.Entry::getKey, entry -> entry.getValue().toLowerCase(Locale.ROOT)));
  }

  private static String displayName(Map<String, String> name) {
    if (name.get("de") != null) {
      return name.get("de");
    }
    return name.values().stream().filter(Objects::nonNull).findFirst().orElse("");
  }
}
