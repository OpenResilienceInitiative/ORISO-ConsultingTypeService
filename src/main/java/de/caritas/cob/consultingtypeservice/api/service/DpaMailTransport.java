package de.caritas.cob.consultingtypeservice.api.service;

import de.caritas.cob.consultingtypeservice.api.exception.SmtpSendException;

/**
 * Strict SMTP send contract: implementations either return a {@link DpaMailSendReceipt} after the
 * SMTP server accepted the message, or throw {@link SmtpSendException}. Silent success is not
 * allowed.
 */
public interface DpaMailTransport {

  /**
   * Sends the given message via the provided SMTP settings, as {@code multipart/alternative} — the
   * text part is what a text-only client, a screen reader in text mode, and a spam filter scoring
   * the message actually read.
   *
   * @return a receipt confirming the SMTP server accepted the message
   * @throws SmtpSendException if the message could not be handed over to the SMTP server
   */
  DpaMailSendReceipt send(
      DpaMailSettings settings, String recipient, String subject, String htmlBody, String textBody);
}
