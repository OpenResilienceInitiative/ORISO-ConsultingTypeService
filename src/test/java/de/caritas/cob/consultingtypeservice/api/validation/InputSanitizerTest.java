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
        "Übergang von Schule zu Beruf",
        "῾ Τρία ኣጸጋሚ ናብራን Трудные"
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
  void sanitize_keepsEmojiAsNumericReferences_becauseTheTopicTablesAreUtf8mb3() {
    var once = sanitizer.sanitize("Hilfe 😀 &#x1f499;");

    assertThat(once).isEqualTo("Hilfe &#x1f600; &#x1f499;");
    assertThat(sanitizer.sanitize(once)).isEqualTo(once);
  }

  @Test
  void sanitize_keepsTypedEntitiesOfAngleBracketsAsText() {
    // Typed as "&lt;b&gt;": the sanitizer reads it as the text "<b>" and drops the brackets.
    assertThat(sanitizer.sanitize("&lt;b&gt;Sucht")).isEqualTo("bSucht");
    // Typed as "&amp;lt;b&amp;gt;": the text "&lt;b&gt;" stays, harmless in HTML and in text.
    assertThat(sanitizer.sanitize("&amp;lt;b&amp;gt;")).isEqualTo("&lt;b&gt;");
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
