package de.caritas.cob.consultingtypeservice.api.service;

/** Named, credential-free availability failures; never retain an upstream exception or payload. */
public class SmtpSynchronizationUnavailableException extends RuntimeException {
  public enum Reason {
    SMTP_SYNC_HELPER_NOT_CONFIGURED,
    SMTP_SYNC_IDENTITY_NOT_CONFIGURED,
    SMTP_SYNC_IDENTITY_UNAVAILABLE,
    SMTP_SYNC_HELPER_UNAVAILABLE,
    SMTP_SYNC_INVALID_ACK
  }

  private final Reason reason;

  public SmtpSynchronizationUnavailableException(Reason reason) {
    super(reason.name());
    this.reason = reason;
  }

  public Reason getReason() {
    return reason;
  }
}
