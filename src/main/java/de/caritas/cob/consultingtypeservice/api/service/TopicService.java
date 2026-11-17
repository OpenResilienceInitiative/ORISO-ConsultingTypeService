package de.caritas.cob.consultingtypeservice.api.service;

import de.caritas.cob.consultingtypeservice.api.auth.AuthorisationService;
import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.repository.TopicRepository;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicService {

  private @NonNull TopicRepository topicRepository;
  private @NonNull AuthorisationService authorisationService;

  @Value("${multitenancy.enabled}")
  private boolean multitenancy;

  public Collection<TopicEntity> getAllTopics() {
    if (multitenancy) {
      return topicRepository.findAllForTenant(TenantContext.getCurrentTenant());
    } else {
      return topicRepository.findAll();
    }
  }

  /**
   * The topics of the catalogue: the platform admin sees the topics of every tenant, anyone else
   * only those of the tenant context. The cross-tenant read follows the authenticated identity
   * (token tenant 0 and the tenant-admin role), never the tenant context alone.
   */
  public Collection<TopicEntity> getCatalogueTopics() {
    if (!multitenancy || authorisationService.isSuperAdmin()) {
      return topicRepository.findAll();
    }
    return topicRepository.findAllForTenant(TenantContext.getCurrentTenant());
  }

  /** Active topics of the tenant the given topic belongs to. */
  public Collection<TopicEntity> getAllActiveTopicsOfTenantOf(TopicEntity topic) {
    if (multitenancy && topic.getTenantId() != null) {
      return topicRepository.findAllActiveForTenant(topic.getTenantId());
    }
    return getAllActiveTopics();
  }

  public Collection<TopicEntity> getAllActiveTopics() {
    if (multitenancy) {
      return topicRepository.findAllActiveForTenant(TenantContext.getCurrentTenant());
    } else {
      return topicRepository.findAllActive();
    }
  }

  public Optional<TopicEntity> findTopicById(Long id) {
    if (multitenancy) {
      return topicRepository.findByIdForTenant(id, TenantContext.getCurrentTenant());
    } else {
      return topicRepository.findById(id);
    }
  }

  public TopicEntity createTopic(TopicEntity topicEntity) {
    topicEntity.setCreateDate(LocalDateTime.now(ZoneOffset.UTC));
    return topicRepository.save(topicEntity);
  }

  public TopicEntity updateTopic(TopicEntity topicEntity) {
    topicEntity.setUpdateDate(LocalDateTime.now(ZoneOffset.UTC));
    return topicRepository.save(topicEntity);
  }
}
