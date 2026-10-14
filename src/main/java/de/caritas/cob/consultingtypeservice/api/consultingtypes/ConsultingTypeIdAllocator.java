package de.caritas.cob.consultingtypeservice.api.consultingtypes;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.InternalServerErrorException;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeEntity;
import de.caritas.cob.consultingtypeservice.api.model.TenantBootstrapClaim;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/** Global, monotonic reservations; crashes and abandoned attempts may leave harmless gaps. */
@Component
@RequiredArgsConstructor
public class ConsultingTypeIdAllocator {
  private static final String COLLECTION = "consulting_type_id_sequences";
  private static final String KEY = "consulting-types";
  private final MongoTemplate mongo;

  public int reserve() {
    ConsultingTypeEntity lastType =
        mongo.findOne(
            new Query().with(Sort.by(Sort.Direction.DESC, "id")), ConsultingTypeEntity.class);
    TenantBootstrapClaim lastClaim =
        mongo.findOne(
            new Query().with(Sort.by(Sort.Direction.DESC, "resultId")), TenantBootstrapClaim.class);
    long floor = Math.max(-1L, lastType == null ? -1L : lastType.getId().longValue());
    if (lastClaim != null) {
      if (lastClaim.resultId() == null) {
        throw new InternalServerErrorException("Consulting type reservation is not verified");
      }
      floor = Math.max(floor, lastClaim.resultId().longValue());
    }
    Query key = Query.query(Criteria.where("_id").is(KEY));
    Update initialize = new Update().max("lastId", floor);
    try {
      mongo.upsert(key, initialize, COLLECTION);
    } catch (DuplicateKeyException concurrentInitialization) {
      if (mongo.updateFirst(key, initialize, COLLECTION).getMatchedCount() != 1) {
        throw new InternalServerErrorException("Consulting type allocator is not initialized");
      }
    }
    // Never upsert this conditional query: exhausted capacity must not create a new counter.
    Document reserved =
        mongo.findAndModify(
            Query.query(Criteria.where("_id").is(KEY).and("lastId").gte(-1L).lt(Integer.MAX_VALUE)),
            new Update().inc("lastId", 1L),
            FindAndModifyOptions.options().returnNew(true),
            Document.class,
            COLLECTION);
    if (reserved == null
        || !(reserved.get("lastId") instanceof Long || reserved.get("lastId") instanceof Integer)) {
      throw new InternalServerErrorException("Consulting type ID range is exhausted or invalid");
    }
    return Math.toIntExact(((Number) reserved.get("lastId")).longValue());
  }
}
