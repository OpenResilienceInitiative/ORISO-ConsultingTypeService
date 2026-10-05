package de.caritas.cob.consultingtypeservice.api.service;

import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/** Immediate save dispatch plus one-shot retries for a known durable pending revision. */
@Service
@Slf4j
public class SmtpSynchronizationService {
  private final ApplicationSettingsAtomicStore store;
  private final SmtpReconcileClient client;
  private final ScheduledExecutorService retry;

  @Autowired
  public SmtpSynchronizationService(
      ApplicationSettingsAtomicStore store, SmtpReconcileClient client) {
    this(
        store,
        client,
        Executors.newSingleThreadScheduledExecutor(
            task -> {
              Thread thread = new Thread(task, "smtp-sync-pending-retry");
              thread.setDaemon(true);
              return thread;
            }));
  }

  SmtpSynchronizationService(
      ApplicationSettingsAtomicStore store,
      SmtpReconcileClient client,
      ScheduledExecutorService retry) {
    this.store = store;
    this.client = client;
    this.retry = retry;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void recoverPendingAtStartup() {
    store
        .findPendingAtStartup()
        .ifPresent(
            entity ->
                schedule(
                    entity.getId(),
                    entity.getSmtpPendingRevision(),
                    entity.getSmtpNextAttemptAt()));
  }

  public void synchronizeAfterSave(String id, long revision) {
    attempt(id, revision);
  }

  private void attempt(String id, long revision) {
    try {
      var pending =
          store
              .find(id)
              .filter(entity -> Long.valueOf(revision).equals(entity.getSmtpPendingRevision()));
      if (pending.isEmpty()) return;
      var entity = pending.get();
      if (entity.getSmtpNextAttemptAt() != null
          && entity.getSmtpNextAttemptAt().isAfter(Instant.now())) {
        schedule(id, revision, entity.getSmtpNextAttemptAt());
        return;
      }
      try {
        var applied = client.reconcile(revision);
        store.acknowledge(id, applied.getAppliedRevision(), applied.getStatus());
      } catch (SmtpSynchronizationUnavailableException safe) {
        log.warn("SMTP_SYNC_PENDING: {}", safe.getReason());
        int attempts = Math.min(entity.getSmtpSyncAttempts() + 1, 10);
        Instant next = Instant.now().plusSeconds(Math.min(300, 1L << attempts));
        if (store.defer(id, revision, attempts, next)) schedule(id, revision, next);
      }
    } catch (RuntimeException ignored) {
      // The original atomic save already recorded pending state. Never pretend it rolled back.
      log.warn("SMTP_SYNC_PENDING: durable recovery remains required");
      schedule(id, revision, Instant.now().plusSeconds(30));
    }
  }

  private void schedule(String id, long revision, Instant at) {
    long delay = at == null ? 0 : Math.max(0, Duration.between(Instant.now(), at).toMillis());
    try {
      retry.schedule(() -> attempt(id, revision), delay, TimeUnit.MILLISECONDS);
    } catch (java.util.concurrent.RejectedExecutionException ignored) {
      // Shutdown does not erase the committed pending state; startup will recover it.
    }
  }

  @PreDestroy
  public void close() {
    retry.shutdownNow();
  }
}
