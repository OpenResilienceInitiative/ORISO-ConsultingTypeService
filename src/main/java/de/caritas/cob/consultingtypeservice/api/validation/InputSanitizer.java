package de.caritas.cob.consultingtypeservice.api.validation;

import static java.util.Objects.isNull;

import java.util.Map;
import org.owasp.html.HtmlPolicyBuilder;
import org.springframework.stereotype.Component;

@Component
public class InputSanitizer {

  public String sanitize(final String input) {
    final var sanitizer = new HtmlPolicyBuilder().toFactory();
    return sanitizer.sanitize(input);
  }

  public Map<String, String> sanitize(final Map<String, String> inputByLanguage) {
    if (!isNull(inputByLanguage)) {
      inputByLanguage.entrySet().forEach(entry -> entry.setValue(sanitize(entry.getValue())));
    }
    return inputByLanguage;
  }

  /**
   * The escapes the sanitizer writes for characters that are harmless as text. "&lt;" and "&gt;"
   * stay escaped, so no markup can come back. "&amp;" is undone last, so "&amp;lt;" ends up as the
   * text "&lt;" and never as "<".
   */
  private static final String[][] TEXT_ESCAPES = {
    {"&#34;", "\""},
    {"&#39;", "'"},
    {"&#43;", "+"},
    {"&#61;", "="},
    {"&#64;", "@"},
    {"&#96;", "`"},
    {"&amp;", "&"}
  };

  /**
   * For values the clients show as text (topic and category names): removes every tag like {@link
   * #sanitize(String)} and keeps "&", quotes and similar characters as text, so "Gesundheit &
   * Sucht" is not stored as "Gesundheit &amp; Sucht". Angle brackets stay escaped.
   */
  public String sanitizePlainText(final String input) {
    if (isNull(input)) {
      return null;
    }
    var text = sanitize(input);
    for (var escape : TEXT_ESCAPES) {
      text = text.replace(escape[0], escape[1]);
    }
    return text;
  }

  /** {@link #sanitizePlainText(String)} for every language; cleans the map in place. */
  public Map<String, String> sanitizePlainText(final Map<String, String> inputByLanguage) {
    if (!isNull(inputByLanguage)) {
      inputByLanguage
          .entrySet()
          .forEach(entry -> entry.setValue(sanitizePlainText(entry.getValue())));
    }
    return inputByLanguage;
  }
}
