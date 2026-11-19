package de.caritas.cob.consultingtypeservice.api.service;

import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import lombok.Value;

/** Credential-free proof of the latest saved SMTP revision. */
@Value
public class SmtpSynchronizationStatus {
  long revision;
  Long appliedRevision;
  String status;
  public static final String UNKNOWN = "UNKNOWN";
  public static final String PENDING = "SMTP_SYNC_PENDING";
  public static final String APPLIED = "APPLIED";
  public static final String DISABLED = "DISABLED_OR_INCOMPLETE";

  public static SmtpSynchronizationStatus from(ApplicationSettingsEntity entity) {
    if (entity == null) return new SmtpSynchronizationStatus(0, null, UNKNOWN);
    String status = entity.getSmtpPendingRevision() != null ? PENDING : entity.getSmtpSyncStatus();
    if (!APPLIED.equals(status) && !DISABLED.equals(status) && !PENDING.equals(status))
      status = UNKNOWN;
    if ((APPLIED.equals(status) || DISABLED.equals(status))
        && !Long.valueOf(entity.getSmtpRevision()).equals(entity.getSmtpAppliedRevision()))
      status = UNKNOWN;
    return new SmtpSynchronizationStatus(
        entity.getSmtpRevision(), entity.getSmtpAppliedRevision(), status);
  }
}
