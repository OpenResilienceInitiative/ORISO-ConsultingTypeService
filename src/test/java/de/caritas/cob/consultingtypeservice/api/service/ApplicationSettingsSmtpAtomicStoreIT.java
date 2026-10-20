package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsPatchDTO;
import de.caritas.cob.consultingtypeservice.api.repository.ApplicationSettingsRepository;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpFrom;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpHost;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPassword;
import java.time.Instant;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@DataMongoTest
@ContextConfiguration(classes = ConsultingTypeServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
class ApplicationSettingsSmtpAtomicStoreIT {
  @Autowired MongoTemplate mongo;
  ApplicationSettingsAtomicStore store;
  SmtpPasswordEncryptionService crypto = new SmtpPasswordEncryptionService("test-only-key");

  @BeforeEach
  void setup() {
    mongo.dropCollection(ApplicationSettingsEntity.class);
    store = new ApplicationSettingsAtomicStore(mongo);
  }

  ApplicationSettingsEntity legacy() {
    var entity = new ApplicationSettingsEntity();
    entity.setGlobalSmtpHost(new GlobalSmtpHost().withValue("old-host"));
    entity.setGlobalSmtpFrom(new GlobalSmtpFrom().withValue("old@example.test"));
    entity.setGlobalSmtpPassword(
        new GlobalSmtpPassword().withValue(crypto.encrypt("private-password")));
    return mongo.insert(entity);
  }

  @Test
  void legacyDocumentWithoutVersionIsUpdatedAtomicallyAndCredentialsRemainEncrypted() {
    var entity = legacy();
    assertThat(mongo.getCollection("application_settings").find().first())
        .doesNotContainKey("settingsVersion");
    entity.setSmtpRevision(1);
    entity.setSmtpPendingRevision(1L);
    assertThat(store.save(entity, null)).isTrue();
    var saved = store.find(entity.getId()).orElseThrow();
    assertThat(saved.getSettingsVersion()).isEqualTo(1L);
    assertThat(saved.getSmtpRevision()).isEqualTo(1);
    assertThat(saved.getSmtpPendingRevision()).isEqualTo(1L);
    assertThat(saved.getGlobalSmtpPassword().getValue())
        .startsWith("ENC:")
        .doesNotContain("private-password");
  }

  @Test
  void concurrentDifferentPatchesPreserveBothChangesAndMonotonicPendingRevision() throws Exception {
    legacy();
    var repo = new MongoRepositoryFactory(mongo).getRepository(ApplicationSettingsRepository.class);
    var settings = spy(new ApplicationSettingsService(repo, store));
    var firstReads = new CountDownLatch(2);
    var reads = new AtomicInteger();
    doAnswer(
            invocation -> {
              var snapshot = invocation.callRealMethod();
              if (reads.incrementAndGet() <= 2) {
                firstReads.countDown();
                assertThat(firstReads.await(10, TimeUnit.SECONDS)).isTrue();
              }
              return snapshot;
            })
        .when(settings)
        .getApplicationSettings();
    var facade =
        new ApplicationSettingsServiceFacade(
            settings,
            mock(ApplicationSettingsConverter.class),
            crypto,
            mock(SmtpSynchronizationService.class));
    var pool = Executors.newFixedThreadPool(2);
    try {
      var host =
          pool.submit(
              () ->
                  facade.patchApplicationSettings(
                      new ApplicationSettingsPatchDTO().globalSmtpHost("new-host")));
      var from =
          pool.submit(
              () ->
                  facade.patchApplicationSettings(
                      new ApplicationSettingsPatchDTO().globalSmtpFrom("new@example.test")));
      assertThat(host.get(15, TimeUnit.SECONDS)).isPresent();
      assertThat(from.get(15, TimeUnit.SECONDS)).isPresent();
      var saved = settings.getApplicationSettings().orElseThrow();
      assertThat(saved.getGlobalSmtpHost().getValue()).isEqualTo("new-host");
      assertThat(saved.getGlobalSmtpFrom().getValue()).isEqualTo("new@example.test");
      assertThat(saved.getSmtpRevision()).isEqualTo(2);
      assertThat(saved.getSmtpPendingRevision()).isEqualTo(2L);
      assertThat(saved.getSettingsVersion()).isEqualTo(2L);
    } finally {
      pool.shutdownNow();
    }
  }

  @Test
  void oldAckCannotEraseNewerPendingAndAckCannotBeOverwrittenByStaleSettingsSave() {
    var entity = legacy();
    entity.setSmtpRevision(1);
    entity.setSmtpPendingRevision(1L);
    assertThat(store.save(entity, null)).isTrue();
    var stale = store.find(entity.getId()).orElseThrow();
    entity = store.find(entity.getId()).orElseThrow();
    entity.setSmtpRevision(2);
    entity.setSmtpPendingRevision(2L);
    assertThat(store.save(entity, 1L)).isTrue();
    assertThat(store.acknowledge(entity.getId(), 1, "APPLIED")).isFalse();
    assertThat(store.find(entity.getId()).orElseThrow().getSmtpPendingRevision()).isEqualTo(2L);
    assertThat(store.acknowledge(entity.getId(), 2, "APPLIED")).isTrue();
    assertThat(store.save(stale, 1L)).isFalse();
    var applied = store.find(entity.getId()).orElseThrow();
    assertThat(applied.getSmtpPendingRevision()).isNull();
    assertThat(applied.getSmtpAppliedRevision()).isEqualTo(2L);
    assertThat(SmtpSynchronizationStatus.from(applied).getStatus()).isEqualTo("APPLIED");
  }

  @Test
  void pendingRevisionAndRetryBackoffSurviveARecreatedStore() {
    var entity = legacy();
    entity.setSmtpRevision(1);
    entity.setSmtpPendingRevision(1L);
    assertThat(store.save(entity, null)).isTrue();
    Instant next = Instant.now().plusSeconds(100);
    assertThat(store.defer(entity.getId(), 1, 4, next)).isTrue();
    var restartedStore = new ApplicationSettingsAtomicStore(mongo);
    var restored = restartedStore.findPendingAtStartup().orElseThrow();
    assertThat(restored.getId()).isEqualTo(entity.getId());
    assertThat(restored.getSmtpPendingRevision()).isEqualTo(1L);
    assertThat(restored.getSmtpSyncAttempts()).isEqualTo(4);
    assertThat(restored.getSmtpNextAttemptAt().toEpochMilli()).isEqualTo(next.toEpochMilli());
    var scheduler = mock(ScheduledExecutorService.class);
    var pushClient = mock(SmtpReconcileClient.class);
    when(pushClient.isPushConfigured()).thenReturn(true);
    var service = new SmtpSynchronizationService(restartedStore, pushClient, scheduler);
    service.recoverPendingAtStartup();
    verify(scheduler).schedule(any(Runnable.class), anyLong(), eq(TimeUnit.MILLISECONDS));
  }
}
