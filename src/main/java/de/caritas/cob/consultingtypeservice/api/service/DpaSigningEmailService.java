package de.caritas.cob.consultingtypeservice.api.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import de.caritas.cob.consultingtypeservice.api.service.email.DpaMailContent;
import de.caritas.cob.consultingtypeservice.api.service.email.DpaMailTemplateRenderer;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DpaSigningEmailService {

  private static final DateTimeFormatter EXPIRY_FORMAT =
      DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm 'Uhr'", Locale.GERMAN);

  private final ApplicationSettingsService applicationSettingsService;
  private final SmtpPasswordEncryptionService smtpPasswordEncryptionService;
  private final DpaMailTransport dpaMailTransport;
  private final DpaMailTemplateRenderer templateRenderer;
  private final URI permittedAppOrigin;

  public DpaSigningEmailService(
      @NonNull ApplicationSettingsService applicationSettingsService,
      @NonNull SmtpPasswordEncryptionService smtpPasswordEncryptionService,
      @NonNull DpaMailTransport dpaMailTransport,
      @NonNull DpaMailTemplateRenderer templateRenderer,
      @Value("${dpa.sign.frontend.base-url:https://app.oriso.org}") String appBaseUrl) {
    this.applicationSettingsService = applicationSettingsService;
    this.smtpPasswordEncryptionService = smtpPasswordEncryptionService;
    this.dpaMailTransport = dpaMailTransport;
    this.templateRenderer = templateRenderer;
    this.permittedAppOrigin = parseUri(appBaseUrl, "appBaseUrl");
  }

  /**
   * Builds and sends the DPA signing email via the global SMTP settings.
   *
   * @return a receipt confirming the SMTP server accepted the message; any transport failure
   *     propagates as {@link de.caritas.cob.consultingtypeservice.api.exception.SmtpSendException}
   *     - this method never reports success without a confirmed handover
   */
  public DpaMailSendReceipt send(DpaSigningEmailCommand command) {
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
    DpaMailContent content =
        new DpaMailContent(
            tenantName, null, EXPIRY_FORMAT.format(command.getExpiresAt()), signLink.toString());
    return dpaMailTransport.send(
        mailSettings,
        command.getRecipientEmail().trim(),
        templateRenderer.subject(),
        templateRenderer.renderHtml(content));
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
