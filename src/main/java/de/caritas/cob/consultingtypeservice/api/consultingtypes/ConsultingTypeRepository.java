package de.caritas.cob.consultingtypeservice.api.consultingtypes;

import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ConsultingTypeRepository extends MongoRepository<ConsultingTypeEntity, String> {

  @Override
  @Query("{'bootstrapCompensated': {$ne: true}}")
  List<ConsultingTypeEntity> findAll();

  @Query("{'slug': ?0, 'bootstrapCompensated': {$ne: true}}")
  List<ConsultingTypeEntity> findBySlug(String slug);

  @Query(value = "{'_id': ?0, 'bootstrapCompensated': {$ne: true}}")
  ConsultingTypeEntity findByConsultingTypeId(Integer consultingtypeId);

  ConsultingTypeEntity findFirstByOrderByIdDesc();

  @Query("{'tenantId': ?0, 'bootstrapCompensated': {$ne: true}}")
  ConsultingTypeEntity findByTenantId(Integer tenantId);

  @Query(value = "{'tenantId': ?0, 'bootstrapCompensated': {$ne: true}}", exists = true)
  boolean existsByTenantId(Integer tenantId);
}
