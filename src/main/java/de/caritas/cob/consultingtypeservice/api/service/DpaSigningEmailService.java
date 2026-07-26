package de.caritas.cob.consultingtypeservice.api.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class DpaSigningEmailService {

  private static final DateTimeFormatter EXPIRY_FORMAT =
      DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm 'Uhr'", Locale.GERMAN);

  private final ApplicationSettingsService applicationSettingsService;
  private final SmtpPasswordEncryptionService smtpPasswordEncryptionService;
  private final DpaMailTransport dpaMailTransport;
  private final URI permittedAppOrigin;

  public DpaSigningEmailService(
      @NonNull ApplicationSettingsService applicationSettingsService,
      @NonNull SmtpPasswordEncryptionService smtpPasswordEncryptionService,
      @NonNull DpaMailTransport dpaMailTransport,
      @Value("${dpa.sign.frontend.base-url:https://app.oriso.org}") String appBaseUrl) {
    this.applicationSettingsService = applicationSettingsService;
    this.smtpPasswordEncryptionService = smtpPasswordEncryptionService;
    this.dpaMailTransport = dpaMailTransport;
    this.permittedAppOrigin = parseUri(appBaseUrl, "appBaseUrl");
  }

  public void send(DpaSigningEmailCommand command) {
    if (command == null
        || isBlank(command.getRecipientEmail())
        || isBlank(command.getTenantName())
        || command.getExpiresAt() == null) {
      throw new BadRequestException(
          "recipientEmail, tenantName, signLink and expiresAt are required");
    }
    URI signLink = parseUri(command.getSignLink(), "signLink");
    if (!hasSameOrigin(signLink, permittedAppOrigin)
        || signLink.getPath() == null
        || !signLink.getPath().startsWith("/dpa-sign/")) {
      throw new BadRequestException("signLink must use the configured ORISO App origin");
    }

    ApplicationSettingsEntity entity =
        applicationSettingsService
            .getApplicationSettings()
            .orElseThrow(() -> new IllegalStateException("Global SMTP settings are unavailable"));
    DpaMailSettings mailSettings = toMailSettings(entity);
    String tenantName = command.getTenantName().trim();
    dpaMailTransport.send(
        mailSettings,
        command.getRecipientEmail().trim(),
        "ORISO: AVV für " + tenantName,
        buildHtml(tenantName, signLink.toString(), command.getExpiresAt()));
  }

  private DpaMailSettings toMailSettings(ApplicationSettingsEntity entity) {
    boolean systemEmailsEnabled =
        entity.getGlobalFeatureSystemNotificationEmailsEnabled() != null
            && Boolean.TRUE.equals(
                entity.getGlobalFeatureSystemNotificationEmailsEnabled().getValue());
    boolean smtpEnabled =
        entity.getGlobalSmtpEnabled() != null
            && Boolean.TRUE.equals(entity.getGlobalSmtpEnabled().getValue());
    String host = entity.getGlobalSmtpHost() == null ? null : entity.getGlobalSmtpHost().getValue();
    String rawPort =
        entity.getGlobalSmtpPort() == null ? null : entity.getGlobalSmtpPort().getValue();
    String username =
        entity.getGlobalSmtpUsername() == null ? null : entity.getGlobalSmtpUsername().getValue();
    String encryptedPassword =
        entity.getGlobalSmtpPassword() == null ? null : entity.getGlobalSmtpPassword().getValue();
    String from = entity.getGlobalSmtpFrom() == null ? null : entity.getGlobalSmtpFrom().getValue();
    Integer port = parsePort(rawPort);
    if (!systemEmailsEnabled
        || !smtpEnabled
        || isBlank(host)
        || port == null
        || isBlank(username)
        || isBlank(encryptedPassword)
        || isBlank(from)) {
      throw new IllegalStateException("Global SMTP settings are incomplete or disabled");
    }
    String password = smtpPasswordEncryptionService.decrypt(encryptedPassword);
    if (isBlank(password)) {
      throw new IllegalStateException("Global SMTP credentials are unavailable");
    }
    boolean secure =
        entity.getGlobalSmtpSecure() != null
            && Boolean.TRUE.equals(entity.getGlobalSmtpSecure().getValue());
    return new DpaMailSettings(host, port, secure, username, password, from);
  }

  private String buildHtml(String tenantName, String signLink, LocalDateTime expiresAt) {
    String safeTenantName = HtmlUtils.htmlEscape(tenantName);
    String safeLink = HtmlUtils.htmlEscape(signLink);
    String safeExpiry = HtmlUtils.htmlEscape(EXPIRY_FORMAT.format(expiresAt));
    return "<!doctype html><html lang=\"de\"><body style=\"margin:0;padding:0;background:#f3f2f2;font-family:Arial,sans-serif;color:#202020;\">"
        + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding:32px 16px;\"><tr><td align=\"center\">"
        + "<table role=\"presentation\" width=\"620\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:620px;background:#ffffff;border:1px solid #cbc8c8;border-radius:12px;overflow:hidden;\">"
        + "<tr><td style=\"padding:20px 28px;background:#e7e5e5;color:#4a0000;font-size:20px;font-weight:700;\">ORISO</td></tr>"
        + "<tr><td style=\"padding:30px 28px 10px;font-size:24px;line-height:32px;font-weight:700;\">Auftragsverarbeitungsvereinbarung prüfen</td></tr>"
        + "<tr><td style=\"padding:0 28px 16px;font-size:16px;line-height:25px;\">Für <strong>"
        + safeTenantName
        + "</strong> wurde eine Auftragsverarbeitungsvereinbarung bereitgestellt. Über den folgenden Einmal-Link können Sie den vollständigen Vertrag lesen und verbindlich bestätigen.</td></tr>"
        + "<tr><td style=\"padding:4px 28px 22px;\"><a href=\""
        + safeLink
        + "\" style=\"display:inline-block;background:#b90013;color:#ffffff;text-decoration:none;padding:13px 20px;border-radius:24px;font-weight:700;\">Vereinbarung ansehen und bestätigen</a></td></tr>"
        + "<tr><td style=\"padding:0 28px 12px;color:#5f5c5c;font-size:14px;line-height:22px;\">Der Link ist einmalig verwendbar und gültig bis "
        + safeExpiry
        + ".</td></tr>"
        + "<tr><td style=\"padding:0 28px 28px;color:#5f5c5c;font-size:13px;line-height:20px;word-break:break-all;\">Falls die Schaltfläche nicht funktioniert: <a href=\""
        + safeLink
        + "\">"
        + safeLink
        + "</a></td></tr></table></td></tr></table></body></html>";
  }

  private static Integer parsePort(String value) {
    try {
      int port = Integer.parseInt(value);
      return port > 0 && port <= 65535 ? port : null;
    } catch (Exception exception) {
      return null;
    }
  }

  private static URI parseUri(String value, String field) {
    if (isBlank(value)) {
      throw new BadRequestException(field + " is required");
    }
    try {
      URI uri = URI.create(value.trim());
      if (isBlank(uri.getScheme()) || isBlank(uri.getHost())) {
        throw new IllegalArgumentException("absolute URI required");
      }
      return uri;
    } catch (IllegalArgumentException exception) {
      throw new BadRequestException(field + " is invalid");
    }
  }

  private static boolean hasSameOrigin(URI left, URI right) {
    return left.getScheme().equalsIgnoreCase(right.getScheme())
        && left.getHost().equalsIgnoreCase(right.getHost())
        && effectivePort(left) == effectivePort(right)
        && Objects.equals(left.getUserInfo(), right.getUserInfo());
  }

  private static int effectivePort(URI uri) {
    return uri.getPort() >= 0
        ? uri.getPort()
        : "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
  }

  @lombok.Value
  public static class DpaSigningEmailCommand {
    String recipientEmail;
    String tenantName;
    String signLink;
    LocalDateTime expiresAt;
  }
}
