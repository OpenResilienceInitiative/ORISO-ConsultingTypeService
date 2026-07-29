package de.caritas.cob.consultingtypeservice.api.service;

import de.caritas.cob.consultingtypeservice.api.exception.SmtpSendException;
import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.time.Instant;
import java.util.Properties;
import org.springframework.stereotype.Component;

@Component
public class JakartaDpaMailTransport implements DpaMailTransport {

  @Override
  public DpaMailSendReceipt send(
      DpaMailSettings settings, String recipient, String subject, String htmlBody) {
    // parse the recipient before anything touches the transport: an unparseable address is a
    // client error (400), not an upstream SMTP failure (502) - the broad catch below must
    // never reclassify it (U5 verify finding)
    InternetAddress[] recipients;
    try {
      recipients = InternetAddress.parse(recipient, true);
    } catch (AddressException exception) {
      throw new BadRequestException("Recipient email address is not valid: " + recipient);
    }
    try {
      Session session =
          Session.getInstance(
              buildSessionProperties(settings),
              new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(settings.getUsername(), settings.getPassword());
                }
              });
      Message message = new MimeMessage(session);
      message.setFrom(new InternetAddress(settings.getFrom()));
      message.setRecipients(Message.RecipientType.TO, recipients);
      message.setSubject(subject);
      message.setContent(htmlBody, "text/html; charset=UTF-8");
      Transport.send(message);
      return new DpaMailSendReceipt(recipient, Instant.now());
    } catch (Exception exception) {
      throw new SmtpSendException("DPA signing email could not be sent", exception);
    }
  }

  /**
   * Builds the jakarta.mail session properties for the configured security mode. Hardening (parity
   * with the ORISO-Admin#569 follow-up in UserService {@code JakartaInviteMailTransport}): in
   * STARTTLS mode ({@code isSecure() == false}) the upgrade is mandatory — {@code
   * mail.smtp.starttls.required} refuses servers (or men-in-the-middle) that do not offer STARTTLS
   * instead of silently sending credentials and the DPA signing link in plaintext. In both modes
   * the server certificate must match the configured host ({@code
   * mail.smtp.ssl.checkserveridentity}).
   */
  static Properties buildSessionProperties(DpaMailSettings settings) {
    Properties properties = new Properties();
    properties.put("mail.smtp.auth", "true");
    properties.put("mail.smtp.host", settings.getHost());
    properties.put("mail.smtp.port", String.valueOf(settings.getPort()));
    properties.put("mail.smtp.connectiontimeout", "10000");
    properties.put("mail.smtp.timeout", "10000");
    properties.put("mail.smtp.writetimeout", "10000");
    properties.put("mail.smtp.ssl.checkserveridentity", "true");
    if (settings.isSecure()) {
      properties.put("mail.smtp.ssl.enable", "true");
    } else {
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.starttls.required", "true");
    }
    return properties;
  }
}
