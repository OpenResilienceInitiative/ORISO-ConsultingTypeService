package de.caritas.cob.consultingtypeservice.api.service;

import static de.caritas.cob.consultingtypeservice.api.tenant.TenantResolver.TECHNICAL_TENANT_ID;

import de.caritas.cob.consultingtypeservice.api.auth.AuthorisationService;
import de.caritas.cob.consultingtypeservice.api.converter.TopicConverter;
import de.caritas.cob.consultingtypeservice.api.exception.TopicNotFoundException;
import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.ConflictException;
import de.caritas.cob.consultingtypeservice.api.model.TopicDTO;
import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.model.TopicMultilingualDTO;
import de.caritas.cob.consultingtypeservice.api.model.TopicStatus;
import de.caritas.cob.consultingtypeservice.api.service.TopicNameUniqueness.NamedTopic;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import de.caritas.cob.consultingtypeservice.api.topicdefaults.DefaultTopicCatalogue;
import de.caritas.cob.consultingtypeservice.api.topicdefaults.DefaultTopicSeeder;
import de.caritas.cob.consultingtypeservice.api.topicdefaults.TopicIconPolicy;
import de.caritas.cob.consultingtypeservice.api.validation.TopicInputSanitizer;
import de.caritas.cob.consultingtypeservice.api.validation.TopicValidationService;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicServiceFacade {

  private final @NonNull TopicService topicService;
  private final @NonNull TopicConverter topicConverter;
  private final @NonNull TopicInputSanitizer topicInputSanitizer;
  private final @NonNull TopicValidationService topicValidationService;
  private final @NonNull DefaultTopicCatalogue defaultTopicCatalogue;
  private final @NonNull DefaultTopicSeeder defaultTopicSeeder;
  private final @NonNull AuthorisationService authorisationService;
  private final @NonNull TopicNameUniqueness topicNameUniqueness;

  /** The public topic list: the request's tenant only, see {@link TopicService#getAllTopics()}. */
  public List<TopicDTO> getAllTopics() {
    final var topicEntities = topicService.getAllTopics();
    return topicConverter.toDTOList(topicEntities);
  }

  /** The /topicadmin list, see {@link TopicService#getAllTopicsForAdmin()}. */
  public List<TopicDTO> getAllTopicsForAdmin() {
    return topicConverter.toDTOList(topicService.getAllTopicsForAdmin());
  }

  public List<TopicDTO> getAllActiveTopics() {
    final var topicEntities = topicService.getAllActiveTopics();
    return topicConverter.toDTOList(topicEntities);
  }

  /**
   * In the technical tenant 0 (the platform admin) the Hibernate interceptor assigns no tenant, so
   * the topic joins the default topics' tenant, as in the catalogue.
   */
  public TopicMultilingualDTO createTopic(final TopicMultilingualDTO topicDTO) {
    final var tenantId =
        authorisationService.isSuperAdmin()
                && TECHNICAL_TENANT_ID.equals(TenantContext.getCurrentTenant())
            ? defaultTopicSeeder.defaultTopicTenantId()
            : null;
    return createTopic(topicDTO, tenantId);
  }

  /** As {@link #createTopic(TopicMultilingualDTO)}, owned by the given tenant when not null. */
  public TopicMultilingualDTO createTopic(
      final TopicMultilingualDTO topicDTO, final Long tenantId) {
    topicValidationService.validate(topicDTO);
    final TopicMultilingualDTO sanitizedTopicDTO = topicInputSanitizer.sanitize(topicDTO);
    final var topicEntity = topicConverter.toEntity(sanitizedTopicDTO);
    if (tenantId != null) {
      topicEntity.setTenantId(tenantId);
    }
    if (topicEntity.getIcon() != null) {
      TopicIconPolicy.assertAllowed(topicEntity.getIcon());
    }
    final var savedTopic = topicService.createTopic(topicEntity);
    return topicConverter.toMultilingualDTO(savedTopic);
  }

  public TopicMultilingualDTO updateTopic(final Long id, final TopicMultilingualDTO topicDTO) {
    final var topicById = topicService.findTopicByIdForAdmin(id);
    if (topicById.isPresent()) {
      log.info("Found topic with id {}", topicById);
      return updateTopic(topicById.get(), topicDTO);
    } else {
      throw new TopicNotFoundException("Topic with given id could not be found : " + id);
    }
  }

  /** Updates a topic the caller already looked up, e.g. across tenants for the catalogue. */
  public TopicMultilingualDTO updateTopic(
      final TopicEntity stored, final TopicMultilingualDTO topicDTO) {
    return updateTopic(stored, topicDTO, true);
  }

  /**
   * As {@link #updateTopic(TopicEntity, TopicMultilingualDTO)}, but without the "last visible
   * topic" and "name taken" checks: a batch checks its end state once instead (see
   * TopicCatalogueService).
   */
  public TopicMultilingualDTO updateTopicInBatch(
      final TopicEntity stored, final TopicMultilingualDTO topicDTO) {
    return updateTopic(stored, topicDTO, false);
  }

  private TopicMultilingualDTO updateTopic(
      final TopicEntity stored, final TopicMultilingualDTO topicDTO, final boolean singleUpdate) {
    topicValidationService.validate(topicDTO);
    final var sanitizedTopicDTO = topicInputSanitizer.sanitize(topicDTO);
    if (singleUpdate) {
      assertNotHidingLastVisibleTopic(stored, sanitizedTopicDTO);
      assertNameFree(stored, topicDTO);
    }
    final var topicEntity = topicConverter.toEntity(stored, sanitizedTopicDTO);
    applyIcon(topicEntity, sanitizedTopicDTO.getIcon());
    // From now on this is the platform admin's version; the default seed leaves it alone.
    topicEntity.setCustomized(true);
    final var updatedEntity = topicService.updateTopic(topicEntity);
    return topicConverter.toMultilingualDTO(updatedEntity);
  }

  /** 409 instead of a 500 from the database when the new name is taken in the topic's tenant. */
  private void assertNameFree(final TopicEntity stored, final TopicMultilingualDTO update) {
    if (update.getName() == null) {
      return;
    }
    topicNameUniqueness.assertUniqueAfter(
        topicService.getAllTopicsOfTenant(stored.getTenantId()),
        List.of(new NamedTopic(stored.getId(), stored.getTenantId(), update.getName())));
  }

  private void assertNotHidingLastVisibleTopic(
      final TopicEntity stored, final TopicMultilingualDTO update) {
    final boolean hides =
        TopicStatus.ACTIVE.equals(stored.getStatus())
            && update.getStatus() != null
            && !TopicStatus.ACTIVE.name().equalsIgnoreCase(update.getStatus());
    if (hides && topicService.getAllActiveTopicsOfTenantOf(stored).size() <= 1) {
      // An installation must always offer at least one topic in the registration.
      throw new ConflictException(
          "Topic " + stored.getId() + " is the last visible topic and cannot be hidden");
    }
  }

  /**
   * Not sent: the stored icon stays. Empty: the own icon is removed, and a default topic shows its
   * shipped Caritas icon again. Anything else: the platform admin's own icon.
   */
  private void applyIcon(final TopicEntity topic, final String icon) {
    if (icon == null) {
      return;
    }
    if (icon.isEmpty()) {
      topic.setIcon(
          topic.getSlug() == null
              ? null
              : defaultTopicCatalogue
                  .findBySlug(topic.getSlug())
                  .map(defaultTopicSeeder::iconUrl)
                  .orElse(null));
      return;
    }
    TopicIconPolicy.assertAllowed(icon);
    topic.setIcon(icon);
  }

  /** Every topic the catalogue shows, see {@link TopicService#getCatalogueTopics()}. */
  public List<TopicMultilingualDTO> getCatalogueTopicsMultilingual() {
    return topicConverter.toMultilingualDTOList(topicService.getCatalogueTopics());
  }

  public List<TopicMultilingualDTO> getAllTopicsMultilingual() {
    final var topicEntities = topicService.getAllTopics();
    return topicConverter.toMultilingualDTOList(topicEntities);
  }

  public TopicMultilingualDTO getTopicMultilingualById(final Long id) {
    final var topicEntity = topicService.findTopicByIdForAdmin(id);
    if (topicEntity.isPresent()) {
      return topicConverter.toMultilingualDTO(topicEntity.get());
    } else {
      throw new TopicNotFoundException("Topic with given id could not be found : " + id);
    }
  }
}
