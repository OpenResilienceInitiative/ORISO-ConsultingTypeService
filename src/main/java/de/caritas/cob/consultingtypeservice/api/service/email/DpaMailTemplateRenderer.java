package de.caritas.cob.consultingtypeservice.api.service.email;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * Renders the DPA signing mail from the platform's e-mail design system instead of concatenating
 * HTML in Java (owner report 2026-09-10: the mail arrived in a design two rounds old).
 *
 * <p>The templates under {@code classpath:emails/} are the plain-<code>{{}}</code>-dialect output
 * of ORISO-Frontend's {@code npm run emails:build} — the same artefacts ORISO-UserService carries.
 * They are generated, not authored here: refresh them through the kit's build, never by editing the
 * markup, or this service drifts away from the design system exactly the way its predecessor did.
 *
 * <p>Substitution is single-pass, so a substituted value (a Träger name) can never smuggle in a
 * further placeholder. Values are HTML-escaped for the HTML part and taken raw for the text part.
 * The optional blocks are pre-rendered from template files, never from input, which is why they are
 * inserted unescaped.
 *
 * <p>The operator identity and the legal links are configuration and default to blank, because they
 * are tenant/operator data this service does not hold: a blank legal URL drops its link rather than
 * mailing a dead {@code href=""}. Wiring them to the real operator record is tracked separately.
 */
@Component
public class DpaMailTemplateRenderer {

  private static final String ROOT = "emails/de-sie/";
  private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{(\\w+)}}");
  private static final String SUBJECT = "Auftragsverarbeitungsvertrag zur Unterschrift";

  private final String html;
  private final String text;
  private final String providedAtRow;
  private final String privacyLink;
  private final String imprintLink;
  private final String linkSeparator;

  private final String platformName;
  private final String orgName;
  private final String orgAddress;
  private final String contactLine;
  private final String privacyUrl;
  private final String imprintUrl;
  private final String primaryColor;
  private final String accentColor;

  public DpaMailTemplateRenderer(
      @Value("${dpa.mail.platform-name:ORISO}") String platformName,
      @Value("${dpa.mail.org-name:}") String orgName,
      @Value("${dpa.mail.org-address:}") String orgAddress,
      @Value("${dpa.mail.contact-line:}") String contactLine,
      @Value("${dpa.mail.privacy-url:}") String privacyUrl,
      @Value("${dpa.mail.imprint-url:}") String imprintUrl,
      @Value("${dpa.mail.primary-color:#b90013}") String primaryColor,
      @Value("${dpa.mail.accent-color:#b90013}") String accentColor) {
    this.platformName = platformName;
    this.orgName = orgName;
    this.orgAddress = orgAddress;
    this.contactLine = contactLine;
    this.privacyUrl = privacyUrl;
    this.imprintUrl = imprintUrl;
    this.primaryColor = primaryColor;
    this.accentColor = accentColor;
    this.html = load("avv-unterschrift.html");
    this.text = load("avv-unterschrift.txt");
    this.providedAtRow = load("avv-provided-at-row.html");
    this.privacyLink = load("avv-footer-privacy-link.html");
    this.imprintLink = load("avv-footer-imprint-link.html");
    this.linkSeparator = load("avv-footer-link-separator.html");
  }

  /** The design system's subject, so no caller can compose one that mis-declines a Träger name. */
  public String subject() {
    return SUBJECT;
  }

  public String renderHtml(DpaMailContent content) {
    Map<String, String> values = new LinkedHashMap<>();
    values.put("tenantName", escape(content.getTenantName()));
    values.put("dpaExpiresAt", escape(content.getExpiresAt()));
    values.put("dpaUrl", escape(content.getDpaUrl()));
    values.put("platformName", escape(platformName));
    values.put("orgName", escape(orgName));
    values.put("orgAddress", escape(orgAddress));
    values.put("contactLine", escape(contactLine));
    values.put("primaryColor", primaryColor);
    values.put("accentColor", accentColor);
    values.put("logoCell", "");
    values.put(
        "providedAtRow",
        isBlank(content.getProvidedAt())
            ? ""
            : fill(providedAtRow, Map.of("dpaProvidedAt", escape(content.getProvidedAt()))));
    values.put("footerLinks", footerLinksHtml());
    return fill(html, values);
  }

  public String renderText(DpaMailContent content) {
    Map<String, String> values = new LinkedHashMap<>();
    values.put("tenantName", content.getTenantName());
    values.put("dpaExpiresAt", content.getExpiresAt());
    values.put("dpaUrl", content.getDpaUrl());
    values.put("platformName", platformName);
    values.put("orgName", orgName);
    values.put("orgAddress", orgAddress);
    values.put("contactLine", contactLine);
    values.put(
        "providedAtLine",
        isBlank(content.getProvidedAt())
            ? ""
            : "Bereitgestellt am: " + content.getProvidedAt() + "\n");
    values.put("footerLinks", footerLinksText());
    return fill(text, values);
  }

  private String footerLinksHtml() {
    List<String> links = new ArrayList<>();
    if (!isBlank(privacyUrl)) {
      links.add(fill(privacyLink, Map.of("privacyUrl", escape(privacyUrl))));
    }
    if (!isBlank(imprintUrl)) {
      links.add(fill(imprintLink, Map.of("imprintUrl", escape(imprintUrl))));
    }
    return String.join(linkSeparator, links);
  }

  private String footerLinksText() {
    List<String> lines = new ArrayList<>();
    if (!isBlank(privacyUrl)) {
      lines.add("Datenschutz: " + privacyUrl);
    }
    if (!isBlank(imprintUrl)) {
      lines.add("Impressum: " + imprintUrl);
    }
    return lines.isEmpty() ? "" : String.join("\n", lines) + "\n";
  }

  /** Unknown placeholders resolve to empty rather than shipping raw braces to a reader. */
  static String fill(String template, Map<String, String> values) {
    Matcher matcher = PLACEHOLDER.matcher(template);
    StringBuilder out = new StringBuilder();
    while (matcher.find()) {
      matcher.appendReplacement(
          out, Matcher.quoteReplacement(values.getOrDefault(matcher.group(1), "")));
    }
    matcher.appendTail(out);
    return out.toString();
  }

  /**
   * Escapes only what HTML requires and leaves UTF-8 intact. The single-argument {@code
   * HtmlUtils.htmlEscape} turns every non-ASCII character into a numeric entity, so a Träger called
   * "Sep10Träger" reached the reader as "Sep10Tr&auml;ger" — legible in a client, unreadable in the
   * source, and the same mechanism behind the ASCII transliterations found in the invite fixtures.
   * These mails are sent as UTF-8, so the charset overload is the correct one.
   */
  private static String escape(String value) {
    return value == null ? "" : HtmlUtils.htmlEscape(value, StandardCharsets.UTF_8.name());
  }

  private static String load(String name) {
    try (InputStream stream = new ClassPathResource(ROOT + name).getInputStream()) {
      return StreamUtils.copyToString(stream, StandardCharsets.UTF_8);
    } catch (IOException exception) {
      throw new IllegalStateException(
          "DPA mail template " + ROOT + name + " is missing", exception);
    }
  }
}
