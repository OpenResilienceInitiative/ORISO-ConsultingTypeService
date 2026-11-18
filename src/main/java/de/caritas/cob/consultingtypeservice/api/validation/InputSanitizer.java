package de.caritas.cob.consultingtypeservice.api.validation;

import static java.util.Objects.isNull;

import java.util.Map;
import java.util.regex.Pattern;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

/**
 * Cleans plain-text input such as topic and category names.
 *
 * <p>Markup is removed, but the remaining text is stored as typed: an ampersand stays an ampersand.
 * The HTML sanitizer encodes the text it keeps ({@code &} becomes {@code &amp;}), so its entities
 * are decoded again. Storing them made the registration show {@code &amp;}
 * (ORISO-ConsultingTypeService#207).
 *
 * <p>Angle brackets are dropped. Some clients render these names as HTML, so a stored value must
 * never be able to form a tag, not even from encoded input such as {@code &lt;img&gt;}.
 */
@Component
public class InputSanitizer {

  private static final PolicyFactory NO_MARKUP = new HtmlPolicyBuilder().toFactory();
  private static final Pattern ANGLE_BRACKETS = Pattern.compile("[<>]");

  public String sanitize(final String input) {
    final var withoutMarkup = NO_MARKUP.sanitize(input);
    return ANGLE_BRACKETS.matcher(HtmlUtils.htmlUnescape(withoutMarkup)).replaceAll("");
  }

  public Map<String, String> sanitize(final Map<String, String> inputByLanguage) {
    if (!isNull(inputByLanguage)) {
      inputByLanguage.entrySet().forEach(entry -> entry.setValue(sanitize(entry.getValue())));
    }
    return inputByLanguage;
  }
}
