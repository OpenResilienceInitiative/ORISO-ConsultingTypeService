package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import de.caritas.cob.consultingtypeservice.api.util.TranslationUtils;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class TranslationServiceTest {

  private static final String TOPIC_NAME =
      "{\"de\":\"Sucht\",\"en\":\"Addiction\",\"tr\":\"Bağımlılık\"}";

  private final TranslationService translationService = new TranslationService();
  private MockHttpServletRequest request;

  @BeforeEach
  void bindRequest() {
    request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
  }

  @AfterEach
  void releaseRequest() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  void headerOnly_Should_resolveHeaderLanguage() {
    request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en");

    assertThat(translationService.getLanguagePreferences()).containsExactly("en", "de");
    assertThat(resolvedTopicName()).isEqualTo("Addiction");
  }

  @Test
  void cookieOnly_Should_resolveCookieLanguage() {
    request.setCookies(new Cookie("other", "tr"), new Cookie("lang", "en"));

    assertThat(translationService.getLanguagePreferences()).containsExactly("en", "de");
    assertThat(resolvedTopicName()).isEqualTo("Addiction");
  }

  @Test
  void headerAndCookieDiffer_Should_preferCookieSelectedByUser() {
    // ORISO Frontend keeps the selected UI language in the cookie while the browser sends its own
    request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "de");
    request.setCookies(new Cookie("lang", "en"));

    assertThat(translationService.getLanguagePreferences()).containsExactly("en", "de");
    assertThat(resolvedTopicName()).isEqualTo("Addiction");
  }

  @Test
  void unsupportedCookie_Should_fallBackToHeader() {
    request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en");
    request.setCookies(new Cookie("lang", "fr"));

    assertThat(translationService.getLanguagePreferences()).containsExactly("fr", "en", "de");
    assertThat(resolvedTopicName()).isEqualTo("Addiction");
  }

  @Test
  void neitherHeaderNorCookie_Should_resolveGerman() {
    assertThat(translationService.getLanguagePreferences()).containsExactly("de");
    assertThat(resolvedTopicName()).isEqualTo("Sucht");
  }

  @ParameterizedTest
  @ValueSource(strings = {"fr", "*", "not a language;;", "en;q=0", " "})
  void unsupportedHeader_Should_fallBackToCookie(String header) {
    request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, header);
    request.setCookies(new Cookie("lang", "tr"));

    assertThat(resolvedTopicName()).isEqualTo("Bağımlılık");
  }

  @ParameterizedTest
  @ValueSource(strings = {"fr", "*", "not a language;;"})
  void unsupportedHeaderWithoutCookie_Should_fallBackToGerman(String header) {
    request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, header);

    assertThat(resolvedTopicName()).isEqualTo("Sucht");
  }

  @Test
  void unsupportedHeaderAndUnsupportedCookie_Should_fallBackToGerman() {
    request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "fr");
    request.setCookies(new Cookie("lang", "es"));

    assertThat(resolvedTopicName()).isEqualTo("Sucht");
  }

  @Test
  void weightedHeader_Should_orderByPreferenceAndUsePrimaryLanguage() {
    request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "de;q=0.5, fr-CH, en-GB;q=0.8, *;q=0.1");

    assertThat(translationService.getLanguagePreferences()).containsExactly("fr", "en", "de");
    assertThat(resolvedTopicName()).isEqualTo("Addiction");
  }

  private String resolvedTopicName() {
    return TranslationUtils.getTranslatedStringFromMap(
        TOPIC_NAME, translationService.getLanguagePreferences());
  }
}
