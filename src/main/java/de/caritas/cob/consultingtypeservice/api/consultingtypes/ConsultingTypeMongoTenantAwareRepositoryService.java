package de.caritas.cob.consultingtypeservice.api.consultingtypes;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.NotFoundException;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity;
import de.caritas.cob.consultingtypeservice.api.service.LogService;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import de.caritas.cob.consultingtypeservice.schemas.model.ConsultingType;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

/** Repository for {@link ConsultingType}. */
@Repository("tenantAware")
@Primary
@ConditionalOnExpression("${multitenancy.enabled:true}")
@AllArgsConstructor
@Slf4j
public class ConsultingTypeMongoTenantAwareRepositoryService
    implements ConsultingTypeRepositoryService {

  private static final Long TECHNICAL_TENANT_ID = 0L;
  private @NonNull ConsultingTypeTenantAwareRepository consultingTypeMongoTenantAwareRepository;
  private @NonNull ConsultingTypeConverter consultingTypeConverter;
  private final @NonNull ConsultingTypeIdAllocator ids;

  @org.springframework.beans.factory.annotation.Autowired
  private org.springframework.data.mongodb.core.MongoTemplate mongo;

  @org.springframework.beans.factory.annotation.Autowired
  private org.springframework.core.env.Environment environment;

  /**
   * Get a complete list of all {@link ConsultingType}.
   *
   * @return a {@link List} of {@link ConsultingType}
   */
  public List<ConsultingType> getListOfConsultingTypes() {
    if (isTechnicalTenantContext()) {
      List<ConsultingTypeEntity> consultingTypeEntities =
          consultingTypeMongoTenantAwareRepository.findAll();
      return consultingTypeConverter.convertList(consultingTypeEntities);
    } else {
      List<ConsultingTypeEntity> allHavingTenantId =
          consultingTypeMongoTenantAwareRepository.findAllHavingTenantId(
              TenantContext.getCurrentTenant());
      return consultingTypeConverter.convertList(allHavingTenantId);
    }
  }

  private boolean isTechnicalTenantContext() {
    return TECHNICAL_TENANT_ID.equals(TenantContext.getCurrentTenant());
  }

  /**
   * Get a consulting type by its id.
   *
   * @param consultingTypeId the id of the consulting type
   * @return the {@link ConsultingType} instance
   */
  public ConsultingType getConsultingTypeById(Integer consultingTypeId) {

    Optional<ConsultingType> byId = findById(consultingTypeId);

    if (byId.isEmpty()) {
      throw new NotFoundException(
          String.format("Consulting type with id %s not found.", consultingTypeId));
    }
    return byId.get();
  }

  private Optional<ConsultingType> findById(Integer consultingTypeId) {
    if (isTechnicalTenantContext()) {
      return Optional.ofNullable(
          consultingTypeMongoTenantAwareRepository.findByConsultingTypeId(consultingTypeId));
    }
    return Optional.ofNullable(
        consultingTypeMongoTenantAwareRepository.findConsultingTypeByIdAndTenantId(
            consultingTypeId, TenantContext.getCurrentTenant()));
  }

  /**
   * Get a consulting type by its slug.
   *
   * @param slug the slug of the consulting type
   * @return the {@link ConsultingType} instance
   */
  public ConsultingType getConsultingTypeBySlug(String slug) {
    return findBySlug(slug).stream()
        .findFirst()
        .orElseThrow(
            () ->
                new NotFoundException(
                    String.format("Consulting type with slug %s not found.", slug)));
  }

  private List<ConsultingType> findBySlug(String slug) {
    if (isTechnicalTenantContext()) {
      List<ConsultingTypeEntity> bySlug = consultingTypeMongoTenantAwareRepository.findBySlug(slug);
      return consultingTypeConverter.convertList(bySlug);
    }
    List<ConsultingTypeEntity> bySlugAndTenantId =
        consultingTypeMongoTenantAwareRepository.findBySlugAndTenantId(
            slug, TenantContext.getCurrentTenant());
    return consultingTypeConverter.convertList(bySlugAndTenantId);
  }

  /**
   * Add a consulting type to the repository.
   *
   * @param consultingType the {@link ConsultingType} to add
   */
  public Optional<ConsultingTypeEntity> addConsultingType(final ConsultingType consultingType) {
    log.debug("Using tenant aware repository service to try to add consulting type");
    if (isConsultingTypeWithGivenIdPresent(consultingType)
        || isConsultingTypeWithGivenSlugPresent(consultingType)) {
      LogService.logWarning(
          String.format(
              "Could not add consulting type. id %s or slug %s is not unique",
              consultingType.getId(), consultingType.getSlug()));
      return Optional.empty();
    } else {
      final ConsultingTypeEntity consultingTypeEntity = new ConsultingTypeEntity();
      BeanUtils.copyProperties(consultingType, consultingTypeEntity);
      try {
        return Optional.of(
            this.consultingTypeMongoTenantAwareRepository.insert(consultingTypeEntity));
      } catch (DuplicateKeyException concurrentCreation) {
        return Optional.empty();
      }
    }
  }

  @Override
  public ConsultingTypeEntity update(ConsultingType consultingType) {
    if (environment != null
        && de.caritas.cob.consultingtypeservice.config.security.TaskContextProof.grouped(
            environment)) {
      var current = (ConsultingTypeEntity) consultingType;
      var criteria =
          org.springframework.data.mongodb.core.query.Criteria.where("_id")
              .is(current.getId())
              .and("bootstrapCompensated")
              .ne(true);
      if (!isTechnicalTenantContext())
        criteria.and("tenantId").is(TenantContext.getCurrentTenant());
      var changed =
          mongo.findAndReplace(
              org.springframework.data.mongodb.core.query.Query.query(criteria),
              current,
              org.springframework.data.mongodb.core.FindAndReplaceOptions.options().returnNew(),
              ConsultingTypeEntity.class,
              mongo.getCollectionName(ConsultingTypeEntity.class),
              ConsultingTypeEntity.class);
      if (changed == null) throw new NotFoundException("Consulting type is absent or terminal");
      return changed;
    }
    return this.consultingTypeMongoTenantAwareRepository.save(
        (ConsultingTypeEntity) consultingType);
  }

  @Override
  public Integer getNextId() {
    return ids.reserve();
  }

  @Override
  public ConsultingType getConsultingTypeByTenantId(Integer tenantId) {
    return consultingTypeMongoTenantAwareRepository.findByTenantId(tenantId);
  }

  @Override
  public boolean hasConsultingTypesForTenant(Integer tenantId) {
    return consultingTypeMongoTenantAwareRepository.existsByTenantId(tenantId);
  }

  private boolean isConsultingTypeWithGivenIdPresent(ConsultingType consultingType) {
    return findById(consultingType.getId()).isPresent();
  }

  protected boolean isConsultingTypeWithGivenSlugPresent(ConsultingType consultingType) {
    return !consultingTypeMongoTenantAwareRepository
        .findBySlugAndTenantId(consultingType.getSlug(), Long.valueOf(consultingType.getTenantId()))
        .isEmpty();
  }
}
