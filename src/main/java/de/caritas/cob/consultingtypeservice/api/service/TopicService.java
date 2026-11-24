package de.caritas.cob.consultingtypeservice.api.service;

import static de.caritas.cob.consultingtypeservice.api.tenant.TenantResolver.TECHNICAL_TENANT_ID;
import static de.caritas.cob.consultingtypeservice.api.util.JsonConverter.convertToJson;

import de.caritas.cob.consultingtypeservice.api.auth.AuthorisationService;
import de.caritas.cob.consultingtypeservice.api.model.TopicEntity;
import de.caritas.cob.consultingtypeservice.api.repository.TopicRepository;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import de.caritas.cob.consultingtypeservice.api.topicdefaults.DefaultTopicSeeder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Map;
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
   * The topics of the request's tenant (tenant context) only, for every caller including the
   * platform admin. Feeds the public topic list; never looks into another tenant.
   */
  public Collection<TopicEntity> getAllTopics() {
    if (multitenancy) {
      return topicRepository.findAllForTenant(TenantContext.getCurrentTenant());
    } else {
      return topicRepository.findAll();
    }
  }

  /**
   * The topics the caller manages in /topicadmin: those of the tenant context, or for the platform
   * admin in the technical tenant 0 those of the default topic tenant. Admin endpoints only.
   */
  public Collection<TopicEntity> getAllTopicsForAdmin() {
    if (multitenancy) {
      return topicRepository.findAllForTenant(topicTenant());
    } else {
      return topicRepository.findAll();
    }
  }

  /** All topics of the given tenant; without multitenancy or tenant all topics. */
  public Collection<TopicEntity> getAllTopicsOfTenant(Long tenantId) {
    if (multitenancy && tenantId != null) {
      return topicRepository.findAllForTenant(tenantId);
    }
    return topicRepository.findAll();
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

  /** A topic of the request's tenant only, see {@link #getAllTopics()}. */
  public Optional<TopicEntity> findTopicById(Long id) {
    if (multitenancy) {
      return topicRepository.findByIdForTenant(id, TenantContext.getCurrentTenant());
    } else {
      return topicRepository.findById(id);
    }
  }

  /** A topic the caller manages in /topicadmin, see {@link #getAllTopicsForAdmin()}. */
  public Optional<TopicEntity> findTopicByIdForAdmin(Long id) {
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

  /**
   * Writes a unique placeholder name right away, so that within one save another topic can take
   * this topic's old name (for example two topics swapping their names) without the database's
   * unique name constraint seeing both at once. The caller sets the real name in the same
   * transaction.
   */
  public void releaseName(TopicEntity topicEntity) {
    topicEntity.setName(convertToJson(Map.of("released", "topic-" + topicEntity.getId())));
    topicRepository.saveAndFlush(topicEntity);
  }
}
