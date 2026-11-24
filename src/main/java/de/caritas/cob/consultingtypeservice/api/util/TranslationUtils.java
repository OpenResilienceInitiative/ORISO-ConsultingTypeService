package de.caritas.cob.consultingtypeservice.api.util;

import static de.caritas.cob.consultingtypeservice.api.util.JsonConverter.convertMapFromJson;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TranslationUtils {

  public static final String DE = "de";

  private TranslationUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Returns the text in the first of the given languages that has one, most preferred first, and
   * the German text when none of them has.
   */
  public static String getTranslatedStringFromMap(
      final String jsonValue, final List<String> languages) {
    final Map<String, String> translations = convertMapFromJson(jsonValue);
    for (final String language : languages) {
      if (translations.containsKey(language)) {
        return translations.get(language);
      }
    }
    if (translations.containsKey(DE)) {
      return translations.get(DE);
    }
    log.warn("Default translation for value not available");
    return "";
  }
}
