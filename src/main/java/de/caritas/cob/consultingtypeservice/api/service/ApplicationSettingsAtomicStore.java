package de.caritas.cob.consultingtypeservice.api.service;

import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

/** One-document CAS keeps settings, revision and durable recovery state atomic on legacy Mongo. */
@Component
@RequiredArgsConstructor
public class ApplicationSettingsAtomicStore {
  private final MongoTemplate mongo;

  public boolean save(ApplicationSettingsEntity entity, Long expectedVersion) {
    entity.setSettingsVersion(expectedVersion == null ? 1 : Math.addExact(expectedVersion, 1));
    Document document = new Document();
    mongo.getConverter().write(entity, document);
    document.remove("_id");
    Update update = new Update();
    document.forEach(update::set);
    // A mapped null is omitted; explicitly remove cleared durable metadata.
    if (entity.getSmtpPendingRevision() == null) update.unset("smtpPendingRevision");
    if (entity.getSmtpNextAttemptAt() == null) update.unset("smtpNextAttemptAt");
    Query query =
        Query.query(
            Criteria.where("id").is(entity.getId()).and("settingsVersion").is(expectedVersion));
    return mongo.updateFirst(query, update, ApplicationSettingsEntity.class).getMatchedCount() == 1;
  }

  public Optional<ApplicationSettingsEntity> find(String id) {
    return Optional.ofNullable(mongo.findById(id, ApplicationSettingsEntity.class));
  }

  /** Called once at startup, never on an idle periodic loop. */
  public Optional<ApplicationSettingsEntity> findPendingAtStartup() {
    return Optional.ofNullable(
        mongo.findOne(
            Query.query(Criteria.where("smtpPendingRevision").ne(null)),
            ApplicationSettingsEntity.class));
  }

  public boolean acknowledge(String id, long revision, String status) {
    if (!SmtpSynchronizationStatus.APPLIED.equals(status)
        && !SmtpSynchronizationStatus.DISABLED.equals(status)) return false;
    return mongo
            .updateFirst(
                pending(id, revision),
                new Update()
                    .set("smtpAppliedRevision", revision)
                    .set("smtpSyncStatus", status)
                    .unset("smtpPendingRevision")
                    .unset("smtpNextAttemptAt")
                    .set("smtpSyncAttempts", 0)
                    .inc("settingsVersion", 1),
                ApplicationSettingsEntity.class)
            .getMatchedCount()
        == 1;
  }

  public boolean defer(String id, long revision, int attempts, Instant next) {
    return mongo
            .updateFirst(
                pending(id, revision),
                new Update()
                    .set("smtpSyncStatus", SmtpSynchronizationStatus.PENDING)
                    .set("smtpSyncAttempts", attempts)
                    .set("smtpNextAttemptAt", next)
                    .inc("settingsVersion", 1),
                ApplicationSettingsEntity.class)
            .getMatchedCount()
        == 1;
  }

  private Query pending(String id, long revision) {
    return Query.query(
        Criteria.where("id")
            .is(id)
            .and("smtpPendingRevision")
            .is(revision)
            .and("smtpRevision")
            .is(revision));
  }
}
