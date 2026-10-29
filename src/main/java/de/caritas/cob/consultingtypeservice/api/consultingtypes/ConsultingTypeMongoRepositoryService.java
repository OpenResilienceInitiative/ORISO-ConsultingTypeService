package de.caritas.cob.consultingtypeservice.api.consultingtypes;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.NotFoundException;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity;
import de.caritas.cob.consultingtypeservice.api.service.LogService;
import de.caritas.cob.consultingtypeservice.schemas.model.ConsultingType;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

/** Repository for {@link ConsultingType}. */
@Repository("tenantUnaware")
@RequiredArgsConstructor
@Slf4j
public class ConsultingTypeMongoRepositoryService implements ConsultingTypeRepositoryService {

  private final @NonNull ConsultingTypeRepository consultingTypeRepository;
  private final @NonNull ConsultingTypeConverter consultingTypeConverter;
  private final @NonNull ConsultingTypeIdAllocator ids;

  @Value("${feature.multitenancy.with.single.domain.enabled}")
  private boolean multitenancyWithSingleDomainEnabled;

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
    var consultingTypeEntities = consultingTypeRepository.findAll();
    return consultingTypeConverter.convertList(consultingTypeEntities);
  }

  /**
   * Get a consulting type by its id.
   *
   * @param consultingTypeId the id of the consulting type
   * @return the {@link ConsultingType} instance
   */
  public ConsultingType getConsultingTypeById(Integer consultingTypeId) {
    var byId =
        Optional.ofNullable(consultingTypeRepository.findByConsultingTypeId(consultingTypeId));

    if (byId.isEmpty()) {
      throw new NotFoundException(
          String.format("Consulting type with id %s not found.", consultingTypeId));
    }
    return byId.get();
  }

  /**
   * Get a consulting type by its slug.
   *
   * @param slug the slug of the consulting type
   * @return the {@link ConsultingType} instance
   */
  public ConsultingType getConsultingTypeBySlug(String slug) {
    return consultingTypeRepository.findBySlug(slug).stream()
        .findFirst()
        .orElseThrow(
            () ->
                new NotFoundException(
                    String.format("Consulting type with slug %s not found.", slug)));
  }

  @Override
  /*
  This method implementation is required for single-domain-mode.
  In this mode only consultingTypeService is deployed with multitenancyEnabled=false,
  and this functionality is needed for super-admins in admin console.
  */
  public ConsultingType getConsultingTypeByTenantId(Integer tenantId) {
    return consultingTypeRepository.findByTenantId(tenantId);
  }

  @Override
  public boolean hasConsultingTypesForTenant(Integer tenantId) {
    return consultingTypeRepository.existsByTenantId(tenantId);
  }

  /**
   * Add a consulting type to the repository.
   *
   * @param consultingType the {@link ConsultingType} to add
   */
  public Optional<ConsultingTypeEntity> addConsultingType(final ConsultingType consultingType) {
    log.debug("Using tenant unaware repository service to try to add a consulting type");
    if (isConsultingTypeWithGivenIdPresent(consultingType) || slugIsNotValid(consultingType)) {
      LogService.logWarning(
          String.format(
              "Could not add consulting type. id %s or slug %s is not unique",
              consultingType.getId(), consultingType.getSlug()));
      return Optional.empty();
    } else {
      final var consultingTypeEntity = new ConsultingTypeEntity();
      BeanUtils.copyProperties(consultingType, consultingTypeEntity);
      try {
        return Optional.of(this.consultingTypeRepository.insert(consultingTypeEntity));
      } catch (DuplicateKeyException concurrentCreation) {
        return Optional.empty();
      }
    }
  }

  @Override
  public Integer getNextId() {
    return ids.reserve();
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
    return this.consultingTypeRepository.save((ConsultingTypeEntity) consultingType);
  }

  private boolean slugIsNotValid(ConsultingType consultingType) {
    return nonSingleDomainMode() && isConsultingTypeWithGivenSlugPresent(consultingType);
  }

  private boolean nonSingleDomainMode() {
    return !multitenancyWithSingleDomainEnabled;
  }

  private boolean isConsultingTypeWithGivenIdPresent(ConsultingType consultingType) {
    return consultingTypeRepository.findById(consultingType.getId().toString()).isPresent();
  }

  protected boolean isConsultingTypeWithGivenSlugPresent(ConsultingType consultingType) {
    return !consultingTypeRepository.findBySlug(consultingType.getSlug()).isEmpty();
  }
}
