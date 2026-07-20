package de.caritas.cob.consultingtypeservice.api.service;

public interface DpaMailTransport {
  void send(DpaMailSettings settings, String recipient, String subject, String htmlBody);
}
