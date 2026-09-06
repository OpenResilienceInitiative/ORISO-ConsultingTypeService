package de.caritas.cob.consultingtypeservice.api.service;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.RestrictedTenantDTO;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

/**
 * Builds and sends the DPA signing email through the ORISO e-mail design system.
 *
 * <p>{@code src/main/resources/emails/avv-unterschrift.{html,txt}} is generated in ORISO-Frontend
 * (`src/emails/`, `npm run emails:build`) and copied in by hand — this service has no
 * `sync-email-templates.sh` yet, unlike UserService (see ADR-020 there). They are never edited in
 * this repository; the wording is reviewed in Storybook, next to every other ORISO mail.
 *
 * <p>Single tone, single locale: unlike UserService's occasions, this mail has no Du/Sie or
 * language switch — it always goes to a Träger admin, formally, in German — so this class reads one
 * template pair rather than porting the full multi-tone renderer.
 */
@Service
public class DpaSigningEmailService {

  private static final DateTimeFormatter EXPIRY_FORMAT =
      DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm 'Uhr'", Locale.GERMAN);
  private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{(\\w+)}}");
  private static final String TEMPLATE_ID = "avv-unterschrift";

  // From the catalogue's de-sie tone — this mail has no other tone, so the subject is not read
  // dynamically the way UserService's multi-tone OrisoEmailRenderer does.
  private static final String SUBJECT = "Auftragsverarbeitungsvertrag zur Unterschrift";

  // Platform defaults when effective platform branding is unavailable.
  // The recipient tenant and SMTP transport settings do not select legal-mail branding.
  private static final String PRIMARY_COLOR = "#a5000a";
  private static final String ACCENT_COLOR = "#cc1e1c";

  private final ApplicationSettingsService applicationSettingsService;
  private final SmtpPasswordEncryptionService smtpPasswordEncryptionService;
  private final DpaMailTransport dpaMailTransport;
  private final TenantService tenantService;
  private final URI permittedAppOrigin;

  @Value("${email.brand.platform-name:ORISO}")
  private String platformName;

  @Value("${email.brand.org-name:ORISO}")
  private String orgName;

  @Value("${email.brand.org-address:}")
  private String orgAddress;

  @Value("${email.brand.contact-line:}")
  private String contactLine;

  @Value("${email.brand.logo-url:}")
  private String logoUrl;

  private String htmlTemplate;
  private String textTemplate;

  public DpaSigningEmailService(
      @NonNull ApplicationSettingsService applicationSettingsService,
      @NonNull SmtpPasswordEncryptionService smtpPasswordEncryptionService,
      @NonNull DpaMailTransport dpaMailTransport,
      @NonNull TenantService tenantService,
      @Value("${dpa.sign.frontend.base-url:https://app.oriso.org}") String appBaseUrl) {
    this.applicationSettingsService = applicationSettingsService;
    this.smtpPasswordEncryptionService = smtpPasswordEncryptionService;
    this.dpaMailTransport = dpaMailTransport;
    this.tenantService = tenantService;
    this.permittedAppOrigin = parseUri(appBaseUrl, "appBaseUrl");
  }

  @PostConstruct
  void loadTemplates() {
    htmlTemplate = readResource(TEMPLATE_ID + ".html");
    textTemplate = readResource(TEMPLATE_ID + ".txt");
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
    final DpaMailSettings mailSettings = toMailSettings(entity);

    Map<String, String> values = brandValues(entity);
    values.put("tenantName", command.getTenantName().trim());
    // The command carries no "provided at" timestamp — approximated as send time, which is when
    // the sign link actually becomes usable.
    values.put("dpaProvidedAt", EXPIRY_FORMAT.format(LocalDateTime.now()));
    values.put("dpaExpiresAt", EXPIRY_FORMAT.format(command.getExpiresAt()));
    values.put("dpaUrl", signLink.toString());

    return dpaMailTransport.send(
        mailSettings,
        command.getRecipientEmail().trim(),
        SUBJECT,
        substitute(withOptionalLogo(htmlTemplate, values), values, true),
        substitute(textTemplate, values, false));
  }

  private Map<String, String> brandValues(ApplicationSettingsEntity settings) {
    String base = trimTrailingSlash(permittedAppOrigin.toString());
    Map<String, String> values = new LinkedHashMap<>();
    var platform = loadPlatformBrand(settings);
    final var theming = platform == null ? null : platform.getTheming();
    values.put(
        "platformName",
        platform != null && isNotBlank(platform.getName())
            ? platform.getName()
            : (isNotBlank(platformName) ? platformName : "ORISO"));
    values.put("orgName", orgName);
    values.put("orgAddress", orgAddress);
    values.put("contactLine", contactLine);
    String effectiveLogo = firstPartyLogo(logoUrl);
    if (theming != null) {
      String source =
          isNotBlank(theming.getLogo()) ? theming.getLogo() : theming.getAssociationLogo();
      String absoluteLogo = firstPartyLogo(source);
      if (isNotBlank(absoluteLogo)) {
        effectiveLogo = absoluteLogo;
      } else if (isNotBlank(source)
          && isStoredImage(source)
          && platform.getId() != null
          && platform.getId() >= 0
          && "https".equalsIgnoreCase(permittedAppOrigin.getScheme())) {
        effectiveLogo = base + "/service/tenant/public/branding/" + platform.getId() + "/logo";
      }
    }
    values.put("logoUrl", effectiveLogo);
    String accent = theming == null ? null : theming.getPrimaryColor();
    boolean validColor = accent != null && accent.matches("#[A-Fa-f0-9]{6}");
    values.put("primaryColor", validColor && whiteContrast(accent) >= 4.5 ? accent : PRIMARY_COLOR);
    values.put("accentColor", validColor ? accent : ACCENT_COLOR);
    values.put("privacyUrl", base + "/datenschutz");
    values.put("imprintUrl", base + "/impressum");
    return values;
  }

  private RestrictedTenantDTO loadPlatformBrand(ApplicationSettingsEntity settings) {
    try {
      var mainTenant = settings.getMainTenantSubdomainForSingleDomainMultitenancy();
      return mainTenant == null || isBlank(mainTenant.getValue())
          ? null
          : tenantService.getPlatformTenantData(mainTenant.getValue());
    } catch (RuntimeException unavailable) {
      // A legal invitation still has configured platform branding during an owner-service outage.
      return null;
    }
  }

  private String firstPartyLogo(String candidate) {
    if (isBlank(candidate)) {
      return "";
    }
    try {
      URI uri = URI.create(candidate.trim());
      return "https".equalsIgnoreCase(uri.getScheme())
              && uri.getHost() != null
              && uri.getUserInfo() == null
              && uri.getQuery() == null
              && uri.getFragment() == null
              && hasSameOrigin(uri, permittedAppOrigin)
          ? uri.toString()
          : "";
    } catch (IllegalArgumentException invalid) {
      return "";
    }
  }

  private static boolean isStoredImage(String value) {
    // These are the uploader formats served by TenantService's BrandingAssetDecoder.
    return value.matches("(?is)data:image/(png|jpeg|jpg|x-icon|vnd.microsoft.icon);base64,.+")
        || value.matches("[A-Za-z0-9+/=\\s]+");
  }

  private static double whiteContrast(String color) {
    double[] linear = new double[3];
    for (int channel = 0; channel < 3; channel++) {
      double value = Integer.parseInt(color.substring(1 + 2 * channel, 3 + 2 * channel), 16) / 255d;
      linear[channel] = value <= 0.04045 ? value / 12.92 : Math.pow((value + 0.055) / 1.055, 2.4);
    }
    return 1.05 / (0.2126 * linear[0] + 0.7152 * linear[1] + 0.0722 * linear[2] + 0.05);
  }

  private static String withOptionalLogo(String template, Map<String, String> values) {
    String logoCell =
        isBlank(values.get("logoUrl"))
            ? ""
            : "<td width=\"36\" valign=\"middle\" style=\"width:36px;padding-right:12px;\">"
                + "<img src=\"{{logoUrl}}\" width=\"36\" height=\"36\" alt=\"{{platformName}}\""
                + " style=\"display:block;width:36px;height:36px;border:0;border-radius:8px;\"></td>";
    return template.replace("{{logoCell}}", logoCell);
  }

  /**
   * Substitutes {@code {{placeholders}}}. The HTML part escapes markup-significant characters — the
   * tenant name is user-controlled input written into a document, not a log line.
   */
  private static String substitute(String source, Map<String, String> values, boolean escape) {
    Matcher matcher = PLACEHOLDER.matcher(source);
    StringBuilder out = new StringBuilder();
    while (matcher.find()) {
      String replacement = values.get(matcher.group(1));
      if (replacement == null) {
        matcher.appendReplacement(out, Matcher.quoteReplacement(matcher.group()));
        continue;
      }
      matcher.appendReplacement(
          out,
          Matcher.quoteReplacement(
              escape
                  ? HtmlUtils.htmlEscape(replacement, StandardCharsets.UTF_8.name())
                  : replacement));
    }
    matcher.appendTail(out);
    return out.toString();
  }

  private static String readResource(String fileName) {
    String path = "/emails/" + fileName;
    try (InputStream in = DpaSigningEmailService.class.getResourceAsStream(path)) {
      if (in == null) {
        throw new IllegalStateException("e-mail template " + path + " is missing");
      }
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new IllegalStateException("could not read e-mail template " + path, exception);
    }
  }

  private static String trimTrailingSlash(String url) {
    if (!isNotBlank(url)) {
      return "";
    }
    return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
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
