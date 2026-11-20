package de.caritas.cob.consultingtypeservice.api.validation;

import static java.util.Objects.isNull;

import java.util.Map;
import org.owasp.html.HtmlPolicyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

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
   * For values the clients show as text, never as HTML (topic and category names): removes every
   * tag like {@link #sanitize(String)}, then turns the escaped characters back into text, so
   * "Gesundheit &amp; Sucht" is stored as "Gesundheit & Sucht" and not shown with "&amp;amp;".
   */
  public String sanitizePlainText(final String input) {
    return isNull(input) ? null : HtmlUtils.htmlUnescape(sanitize(input));
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
