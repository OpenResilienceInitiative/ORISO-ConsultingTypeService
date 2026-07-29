package de.caritas.cob.consultingtypeservice.api.service;

import java.time.Instant;
import lombok.Value;

/**
 * Confirmation that the SMTP server accepted a message. Returned only after the transport completed
 * without error, so callers may safely mark the delivery as SENT.
 */
@Value
public class DpaMailSendReceipt {
  String recipientEmail;
  Instant sentAt;
}
