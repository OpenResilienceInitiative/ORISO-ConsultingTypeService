package de.caritas.cob.consultingtypeservice.api.util;

import static de.caritas.cob.consultingtypeservice.api.util.JsonConverter.convertMapFromJson;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TranslationUtils {

  public static final String DE = "de";

  private TranslationUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static String getTranslatedStringFromMap(final String jsonValue, final String lang) {
    final Map<String, String> translations = convertMapFromJson(jsonValue);
    if (lang == null || !translations.containsKey(lang)) {
      if (translations.containsKey(DE)) {
        return translations.get(DE);
      } else {
        log.warn("Default translation for value not available");
        return "";
      }
    } else {
      return translations.get(lang);
    }
  }
}
