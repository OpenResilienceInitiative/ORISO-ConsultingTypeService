package de.caritas.cob.consultingtypeservice.api.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Topic and category names are plain text: they are stored as typed and rendered as text. The
 * sanitizer removes markup but must not HTML-encode what is left (ORISO-ConsultingTypeService#207).
 */
class InputSanitizerTest {

  private final InputSanitizer sanitizer = new InputSanitizer();

  @ParameterizedTest
  @ValueSource(
      strings = {
        "Gesundheit & Sucht",
        "Besondere Lebenssituationen & Krisen",
        "Boys and Men's Support",
        "Rat + Tat = Hilfe @ home",
        "Sie sagte \"Hallo\"",
        "„Kinder“ – Jugend · Familie",
        "Übergang von Schule zu Beruf"
      })
  void sanitize_keepsPlainTextUnchanged(String plainText) {
    assertThat(sanitizer.sanitize(plainText)).isEqualTo(plainText);
  }

  @Test
  void sanitize_isIdempotent_soSavingAgainChangesNothing() {
    var once = sanitizer.sanitize("Gesundheit & Sucht's \"Hilfe\"");

    assertThat(sanitizer.sanitize(sanitizer.sanitize(once))).isEqualTo(once);
  }

  @ParameterizedTest
  @CsvSource(
      delimiter = '|',
      value = {
        "<b>Sucht</b> & Krise|Sucht & Krise",
        "<script>alert(1)</script>Sucht|Sucht",
        "Sucht<img src=x onerror=alert(1)>|Sucht",
        "<a href=\"javascript:alert(1)\">Sucht</a>|Sucht"
      })
  void sanitize_stillRemovesMarkup(String input, String expected) {
    assertThat(sanitizer.sanitize(input)).isEqualTo(expected);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "&lt;img src=x onerror=alert(1)&gt;",
        "&#60;script&#62;alert(1)&#60;/script&#62;",
        "a < b > c",
        "<<script>script>alert(1)<</script>/script>"
      })
  void sanitize_neverReturnsAngleBrackets_soInnerHtmlConsumersStaySafe(String input) {
    assertThat(sanitizer.sanitize(input)).doesNotContain("<", ">");
  }

  @Test
  void sanitize_cleansEveryLanguageOfAMap() {
    Map<String, String> name = new HashMap<>();
    name.put("de", "Gesundheit & <b>Sucht</b>");
    name.put("en", "Health & Addiction");

    assertThat(sanitizer.sanitize(name))
        .containsEntry("de", "Gesundheit & Sucht")
        .containsEntry("en", "Health & Addiction");
  }
}
