package de.caritas.cob.consultingtypeservice.api.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import java.util.List;
import org.junit.jupiter.api.Test;

class TranslationUtilsTest {

  @Test
  void shouldResolveProperLanguageValue_When_LanguageKeyIsFound() {
    // given
    // when
    String translation =
        TranslationUtils.getTranslatedStringFromMap(
            "{\"de\":\"German\",\"en\":\"English\"}", List.of("en"));
    // then
    assertThat(translation).isEqualTo("English");
  }

  @Test
  void shouldFallbackToGerman_When_LanguageNotFound() {
    // given
    // when
    String translation =
        TranslationUtils.getTranslatedStringFromMap(
            "{\"de\":\"German\",\"en\":\"English\"}", List.of("fr"));
    // then
    assertThat(translation).isEqualTo("German");
  }

  @Test
  void shouldReturnEmptyString_When_GermanFallbackNotFound() {
    // given
    // when
    String translation =
        TranslationUtils.getTranslatedStringFromMap(
            "{\"es\":\"Spanish\",\"en\":\"English\"}", List.of("fr"));
    // then
    assertThat(translation).isEmpty();
  }

  @Test
  void shouldThrowException_When_GivenStringNotInJsonFormat() {
    // given
    // when
    // then
    assertThrows(
        RuntimeJsonMappingException.class,
        () -> TranslationUtils.getTranslatedStringFromMap("aSimpleString", List.of("fr")));
  }

  @Test
  void shouldResolveFirstLanguageThatHasAText_When_PreferredLanguageIsNotFound() {
    String translation =
        TranslationUtils.getTranslatedStringFromMap(
            "{\"de\":\"German\",\"en\":\"English\"}", List.of("fr", "en", "de"));

    assertThat(translation).isEqualTo("English");
  }
}
