package de.caritas.cob.consultingtypeservice.api.service;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.ConflictException;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO;
import de.caritas.cob.consultingtypeservice.api.model.FullConsultingTypeResponseDTO;
import de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim;
import de.caritas.cob.consultingtypeservice.config.security.TenantCreationContext;
import java.util.UUID;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class TenantBootstrapService {
  private final MongoTemplate mongo;
  private final ConsultingTypeService types;
  private final TenantCreationContext context;

  public TenantBootstrapService(
      MongoTemplate mongo, ConsultingTypeService types, TenantCreationContext context) {
    this.mongo = mongo;
    this.types = types;
    this.context = context;
  }

  public FullConsultingTypeResponseDTO create(ConsultingTypeDTO dto, String proof) {
    context.require(dto.getTenantId() == null ? 0L : dto.getTenantId().longValue(), proof);
    String tenantId = dto.getTenantId().toString();
    String attempt = UUID.randomUUID().toString();
    try {
      mongo.insert(new TenantBootstrapClaim(tenantId, attempt));
    } catch (DuplicateKeyException duplicate) {
      throw new ConflictException("Initial tenant bootstrap has already been claimed");
    }
    try {
      return types.createInitialConsultingTypeForTenant(dto);
    } catch (RuntimeException failed) {
      // Only the inserting attempt can clear its failed claim; another caller never frees it.
      mongo.remove(
          Query.query(Criteria.where("_id").is(tenantId).and("attempt").is(attempt)),
          TenantBootstrapClaim.class);
      throw failed;
    }
  }
}
