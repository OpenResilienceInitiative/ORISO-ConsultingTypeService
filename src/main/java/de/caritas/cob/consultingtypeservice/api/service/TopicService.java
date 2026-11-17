package de.caritas.cob.consultingtypeservice.api.service;

import static de.caritas.cob.consultingtypeservice.api.tenant.TenantResolver.TECHNICAL_TENANT_ID;

import de.caritas.cob.consultingtypeservice.api.auth.AuthorisationService;
import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.repository.TopicRepository;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import de.caritas.cob.consultingtypeservice.api.topicdefaults.DefaultTopicSeeder;
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
  private @NonNull DefaultTopicSeeder defaultTopicSeeder;

  @Value("${multitenancy.enabled}")
  private boolean multitenancy;

  /**
   * The topics of the caller's tenant. These lists feed the topic pickers (for example of an
   * agency), so they never mix tenants; the platform admin in tenant 0 gets the default topics.
   */
  public Collection<TopicEntity> getAllTopics() {
    if (multitenancy) {
      return topicRepository.findAllForTenant(topicTenant());
    } else {
      return topicRepository.findAll();
    }
  }

  /**
   * The tenant whose topics the caller manages: the tenant context, or for the platform admin in
   * the technical tenant 0 (which owns no topics) the tenant of the default topics.
   */
  private Long topicTenant() {
    var current = TenantContext.getCurrentTenant();
    return TECHNICAL_TENANT_ID.equals(current) && authorisationService.isSuperAdmin()
        ? defaultTopicSeeder.defaultTopicTenantId()
        : current;
  }

  /**
   * The topics of the catalogue: the platform admin sees the topics of every tenant, anyone else
   * only those of the tenant context. The cross-tenant read follows the authenticated identity
   * (token tenant 0 and the tenant-admin role), never the tenant context alone. The caller must
   * also run in tenant context 0, or the Hibernate tenant filter still narrows {@code findAll} (see
   * TopicAdminController).
   */
  public Collection<TopicEntity> getCatalogueTopics() {
    if (!multitenancy || authorisationService.isSuperAdmin()) {
      return topicRepository.findAll();
    }
    return topicRepository.findAllForTenant(TenantContext.getCurrentTenant());
  }

  /** Active topics of the tenant the given topic belongs to. */
  public Collection<TopicEntity> getAllActiveTopicsOfTenantOf(TopicEntity topic) {
    return getAllActiveTopicsOfTenant(topic.getTenantId());
  }

  /** Active topics of the given tenant; without a tenant those of the tenant context. */
  public Collection<TopicEntity> getAllActiveTopicsOfTenant(Long tenantId) {
    if (multitenancy && tenantId != null) {
      return topicRepository.findAllActiveForTenant(tenantId);
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

  /** A topic of the caller's tenant, see {@link #getAllTopics()}. */
  public Optional<TopicEntity> findTopicById(Long id) {
    if (multitenancy) {
      return topicRepository.findByIdForTenant(id, topicTenant());
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
