package de.caritas.cob.consultingtypeservice.api.service;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeConverter;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeIdAllocator;
import de.caritas.cob.consultingtypeservice.api.consultingtypes.ConsultingTypeValidator;
import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.ConflictException;
import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.InternalServerErrorException;
import de.caritas.cob.consultingtypeservice.api.mapper.FullConsultingTypeMapper;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTO;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity;
import de.caritas.cob.consultingtypeservice.api.model.FullConsultingTypeResponseDTO;
import de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim;
import de.caritas.cob.consultingtypeservice.config.security.TenantCreationContext;
import de.caritas.cob.consultingtypeservice.schemas.model.ConsultingType;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

/** Durable single-document fencing and exact owned readback without assuming Mongo transactions. */
@Service
public class TenantBootstrapService {
  private static final JsonMapper JSON =
      JsonMapper.builder()
          .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
          .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
          .build();
  private final MongoTemplate mongo;
  private final ConsultingTypeConverter converter;
  private final TenantCreationContext context;
  private final ConsultingTypeIdAllocator ids;
  private final ConsultingTypeValidator validator;

  public TenantBootstrapService(
      MongoTemplate mongo,
      ConsultingTypeConverter converter,
      TenantCreationContext context,
      ConsultingTypeIdAllocator ids,
      ConsultingTypeValidator validator) {
    this.mongo = mongo;
    this.converter = converter;
    this.context = context;
    this.ids = ids;
    this.validator = validator;
  }

  public FullConsultingTypeResponseDTO create(ConsultingTypeDTO dto, String proof) {
    String nonce = context.require(dto, proof);
    validator.validateBootstrapCommand(dto);
    Jwt actor = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    ConsultingType payload = materializedPayload(dto);
    String digest = digest(payload);
    String tenantId = dto.getTenantId().toString();
    // C nonces come from the private SQL INSERT; later attempts require terminal CTS acknowledgement
    // before that exact SQL ownership can be released. Arbitrary re-signing is outside this boundary.
    String claimId = nonce == null ? tenantId : tenantId + ":" + nonce;
    TenantBootstrapClaim claim = mongo.findById(claimId, TenantBootstrapClaim.class);
    if (claim == null && tenantHasTypes(dto.getTenantId())) {
      // Another request may have committed after our earlier empty claim read.
      claim = mongo.findById(claimId, TenantBootstrapClaim.class);
      if (claim == null) {
        throw new ConflictException("Tenant already has a consulting type");
      }
    }
    if (claim == null) {
      int resultId = ids.reserve();
      var proposed =
          new TenantBootstrapClaim(
              claimId,
              tenantId,
              nonce == null ? UUID.randomUUID().toString() : nonce,
              resultId,
              digest,
              actor.getClaimAsString("azp"),
              actor.getSubject(),
              actor.getIssuer().toString());
      try {
        claim = mongo.insert(proposed);
      } catch (DuplicateKeyException alreadyClaimed) {
        claim = mongo.findById(claimId, TenantBootstrapClaim.class);
      }
    }
    requireOwnedClaim(claim, actor, digest, tenantId, claimId, nonce);
    ConsultingTypeEntity result = mongo.findById(claim.resultId(), ConsultingTypeEntity.class);
    if (result != null) {
      return ownedResult(claim, result);
    }
    if (tenantHasTypes(dto.getTenantId())) {
      result = mongo.findById(claim.resultId(), ConsultingTypeEntity.class);
      if (result != null) {
        return ownedResult(claim, result);
      }
      throw new ConflictException("Tenant already has a consulting type");
    }
    var entity = new ConsultingTypeEntity();
    BeanUtils.copyProperties(payload, entity);
    entity.setId(claim.resultId());
    entity.setBootstrapAttempt(claim.attempt());
    entity.setBootstrapPayloadDigest(claim.payloadDigest());
    try {
      mongo.insert(entity);
    } catch (DuplicateKeyException alreadyCreated) {
      // Readback below must prove this exact attempt. A collision never authorizes overwrite.
    }
    result = mongo.findById(claim.resultId(), ConsultingTypeEntity.class);
    if (result == null) {
      throw new InternalServerErrorException("Tenant bootstrap outcome is not yet verifiable");
    }
    return ownedResult(claim, result);
  }

  private ConsultingType materializedPayload(ConsultingTypeDTO dto) {
    ConsultingType payload = converter.convert(dto);
    ConsultingType defaults = new ConsultingType();
    // Freeze the same declared defaults that Mongo readback applies to omitted null flags.
    if (payload.getConsultantBoundedToConsultingType() == null)
      payload.setConsultantBoundedToConsultingType(defaults.getConsultantBoundedToConsultingType());
    if (payload.getExcludeNonMainConsultantsFromTeamSessions() == null)
      payload.setExcludeNonMainConsultantsFromTeamSessions(
          defaults.getExcludeNonMainConsultantsFromTeamSessions());
    if (payload.getIsAnonymousConversationAllowed() == null)
      payload.setIsAnonymousConversationAllowed(defaults.getIsAnonymousConversationAllowed());
    if (payload.getIsSubsequentRegistrationAllowed() == null)
      payload.setIsSubsequentRegistrationAllowed(defaults.getIsSubsequentRegistrationAllowed());
    if (payload.getSendFurtherStepsMessage() == null)
      payload.setSendFurtherStepsMessage(defaults.getSendFurtherStepsMessage());
    return payload;
  }

  private boolean tenantHasTypes(Integer tenantId) {
    return mongo.exists(
        Query.query(Criteria.where("tenantId").is(tenantId)), ConsultingTypeEntity.class);
  }

  private void requireOwnedClaim(
      TenantBootstrapClaim claim, Jwt actor, String digest, String tenantId, String claimId,
      String nonce) {
    if (claim == null
        || claim.resultId() == null
        || claim.attempt() == null
        || !tenantId.equals(claim.tenantId())
        || !claimId.equals(claim.claimId())
        || (nonce != null && !nonce.equals(claim.attempt()))
        || !digest.equals(claim.payloadDigest())
        || !actor.getClaimAsString("azp").equals(claim.actorClient())
        || !actor.getSubject().equals(claim.actorSubject())
        || !actor.getIssuer().toString().equals(claim.actorIssuer())) {
      throw new ConflictException("Tenant bootstrap payload or owner does not match its claim");
    }
  }

  private FullConsultingTypeResponseDTO ownedResult(
      TenantBootstrapClaim claim, ConsultingTypeEntity result) {
    if (!claim.resultId().equals(result.getId())
        || !claim.tenantId().equals(String.valueOf(result.getTenantId()))
        || !claim.attempt().equals(result.getBootstrapAttempt())
        || !claim.payloadDigest().equals(result.getBootstrapPayloadDigest())
        || !claim.payloadDigest().equals(digest(result))) {
      throw new ConflictException("Tenant bootstrap result is foreign or differs from its claim");
    }
    return FullConsultingTypeMapper.mapConsultingType(result);
  }

  private String digest(ConsultingType payload) {
    var canonical = new ConsultingType();
    BeanUtils.copyProperties(payload, canonical);
    canonical.setId(null);
    try {
      return HexFormat.of()
          .formatHex(
              MessageDigest.getInstance("SHA-256").digest(JSON.writeValueAsBytes(canonical)));
    } catch (Exception invalidPayload) {
      throw new IllegalArgumentException(
          "Tenant bootstrap payload cannot be canonicalized", invalidPayload);
    }
  }
}
