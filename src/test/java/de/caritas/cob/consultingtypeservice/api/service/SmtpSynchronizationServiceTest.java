package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class SmtpSynchronizationServiceTest {
  ApplicationSettingsAtomicStore store;
  SmtpReconcileClient client;
  ScheduledExecutorService scheduler;
  SmtpSynchronizationService service;
  ApplicationSettingsEntity pending;

  @BeforeEach
  void setup() {
    store = mock(ApplicationSettingsAtomicStore.class);
    client = mock(SmtpReconcileClient.class);
    scheduler = mock(ScheduledExecutorService.class);
    service = new SmtpSynchronizationService(store, client, scheduler);
    pending = new ApplicationSettingsEntity();
    pending.setId("known-settings");
    pending.setSmtpRevision(2);
    pending.setSmtpPendingRevision(2L);
    when(store.find("known-settings")).thenReturn(Optional.of(pending));
    when(client.isPushConfigured()).thenReturn(true);
  }

  // Helm#420: without a push helper the Keycloak SMTP Job pulls and acknowledges.
  @Test
  void withoutPushHelperSaveStaysPendingWithoutDispatchOrRetry() {
    when(client.isPushConfigured()).thenReturn(false);
    service.synchronizeAfterSave("known-settings", 2);
    when(store.findPendingAtStartup()).thenReturn(Optional.of(pending));
    service.recoverPendingAtStartup();
    verify(client, never()).reconcile(anyLong());
    verify(store, never()).defer(any(), anyLong(), anyInt(), any());
    verifyNoInteractions(scheduler);
  }

  @Test
  void noPendingStartupDoesNotStartPeriodicWork() {
    when(store.findPendingAtStartup()).thenReturn(Optional.empty());
    service.recoverPendingAtStartup();
    verify(store).findPendingAtStartup();
    verifyNoInteractions(scheduler, client);
  }

  @Test
  void startupOnlySchedulesKnownPersistedRevision() {
    when(store.findPendingAtStartup()).thenReturn(Optional.of(pending));
    service.recoverPendingAtStartup();
    var task = ArgumentCaptor.forClass(Runnable.class);
    verify(scheduler).schedule(task.capture(), eq(0L), eq(TimeUnit.MILLISECONDS));
    when(client.reconcile(2)).thenReturn(new SmtpReconcileClient.Acknowledgement(2, "APPLIED"));
    task.getValue().run();
    verify(store).acknowledge("known-settings", 2, "APPLIED");
    verify(store, times(1)).findPendingAtStartup();
  }

  @Test
  void unavailableHelperPersistsCappedBackoffWithoutThrowingAfterSave() {
    pending.setSmtpSyncAttempts(10);
    when(client.reconcile(2))
        .thenThrow(
            new SmtpSynchronizationUnavailableException(
                SmtpSynchronizationUnavailableException.Reason.SMTP_SYNC_HELPER_UNAVAILABLE));
    when(store.defer(eq("known-settings"), eq(2L), eq(10), any())).thenReturn(true);
    assertThatCode(() -> service.synchronizeAfterSave("known-settings", 2))
        .doesNotThrowAnyException();
    var next = ArgumentCaptor.forClass(Instant.class);
    verify(store).defer(eq("known-settings"), eq(2L), eq(10), next.capture());
    assertThat(next.getValue())
        .isBetween(Instant.now().plusSeconds(298), Instant.now().plusSeconds(301));
    verify(scheduler).schedule(any(Runnable.class), anyLong(), eq(TimeUnit.MILLISECONDS));
    verify(store, never()).acknowledge(anyString(), anyLong(), anyString());
  }

  @Test
  void obsoleteQueuedRevisionDoesNotReadHelperOrScheduleAgain() {
    service.synchronizeAfterSave("known-settings", 1);
    verifyNoInteractions(client, scheduler);
  }

  @Test
  void helperMayApplyTheNewerActualSnapshotAndOnlyThatRevisionIsAcknowledged() {
    when(client.reconcile(2)).thenReturn(new SmtpReconcileClient.Acknowledgement(3, "APPLIED"));
    service.synchronizeAfterSave("known-settings", 2);
    verify(store).acknowledge("known-settings", 3, "APPLIED");
    verify(store, never()).acknowledge("known-settings", 2, "APPLIED");
  }

  @Test
  void persistenceOutageAfterCommittedSaveKeepsNamedPendingRetry() {
    when(store.find("known-settings")).thenThrow(new IllegalStateException("private-db-detail"));
    assertThatCode(() -> service.synchronizeAfterSave("known-settings", 2))
        .doesNotThrowAnyException();
    verify(scheduler).schedule(any(Runnable.class), anyLong(), eq(TimeUnit.MILLISECONDS));
    verifyNoInteractions(client);
  }

  @Test
  void appliedProofRequiresExactMatchingRevisionAndNoPending() {
    pending.setSmtpPendingRevision(null);
    pending.setSmtpSyncStatus("APPLIED");
    pending.setSmtpAppliedRevision(1L);
    assertThat(SmtpSynchronizationStatus.from(pending).getStatus()).isEqualTo("UNKNOWN");
    pending.setSmtpAppliedRevision(2L);
    assertThat(SmtpSynchronizationStatus.from(pending).getStatus()).isEqualTo("APPLIED");
    pending.setSmtpPendingRevision(3L);
    assertThat(SmtpSynchronizationStatus.from(pending).getStatus()).isEqualTo("SMTP_SYNC_PENDING");
  }
}
