package de.caritas.cob.consultingtypeservice.api.service;

import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import lombok.Value;

/** Effective transport values used only in memory; secrets must never appear in diagnostics. */
@Value
class SmtpSettingsFingerprint {
  boolean enabled;
  boolean notifications;
  String host;
  String port;
  boolean secure;
  String from;
  String username;
  String password;

  static SmtpSettingsFingerprint from(
      ApplicationSettingsEntity e, SmtpPasswordEncryptionService encryption) {
    return new SmtpSettingsFingerprint(
        e.getGlobalSmtpEnabled() != null
            && Boolean.TRUE.equals(e.getGlobalSmtpEnabled().getValue()),
        e.getGlobalFeatureSystemNotificationEmailsEnabled() != null
            && Boolean.TRUE.equals(e.getGlobalFeatureSystemNotificationEmailsEnabled().getValue()),
        e.getGlobalSmtpHost() == null ? "" : e.getGlobalSmtpHost().getValue(),
        e.getGlobalSmtpPort() == null ? "" : e.getGlobalSmtpPort().getValue(),
        e.getGlobalSmtpSecure() != null && Boolean.TRUE.equals(e.getGlobalSmtpSecure().getValue()),
        e.getGlobalSmtpFrom() == null ? "" : e.getGlobalSmtpFrom().getValue(),
        e.getGlobalSmtpUsername() == null ? "" : e.getGlobalSmtpUsername().getValue(),
        e.getGlobalSmtpPassword() == null
            ? ""
            : encryption.decrypt(e.getGlobalSmtpPassword().getValue()));
  }

  @Override
  public String toString() {
    return "SmtpSettingsFingerprint[redacted]";
  }
}
