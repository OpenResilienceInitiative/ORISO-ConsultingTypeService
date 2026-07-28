package de.caritas.cob.consultingtypeservice.api.exception;

/**
 * Signals that an email could not be handed over to the SMTP server. The global SMTP send path must
 * never report success when this is thrown - callers must treat it as a failed delivery.
 */
public class SmtpSendException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public SmtpSendException(String message, Throwable cause) {
    super(message, cause);
  }
}
