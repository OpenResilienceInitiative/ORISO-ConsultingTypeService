package de.caritas.cob.consultingtypeservice.api.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/** Resolves the languages in which the current request wants localized names. */
@Service
public class TranslationService {

  private static final String DEFAULT_LANGUAGE = "de";
  private static final String LANGUAGE_COOKIE_NAME = "lang";
  private static final String WILDCARD = "*";

  /**
   * The languages of the current request, most preferred first: the {@code Accept-Language} header
   * in the client's order of preference, then the {@code lang} cookie kept for existing clients,
   * then German. A caller takes the first language it has a text for, so a header value that cannot
   * be served falls through to the cookie and then to German.
   */
  public List<String> getLanguagePreferences() {
    final HttpServletRequest currentRequest =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

    final Set<String> languages = new LinkedHashSet<>();
    headerLanguages(currentRequest).forEach(languages::add);
    cookieLanguage(currentRequest).forEach(languages::add);
    languages.add(DEFAULT_LANGUAGE);
    return List.copyOf(languages);
  }

  private Stream<String> headerLanguages(final HttpServletRequest currentRequest) {
    final String header = currentRequest.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
    if (!StringUtils.hasText(header)) {
      return Stream.empty();
    }
    try {
      // parse() returns the ranges sorted by descending weight
      return Locale.LanguageRange.parse(header).stream()
          .filter(range -> range.getWeight() > 0)
          .map(range -> primaryLanguageOf(range.getRange()))
          .filter(language -> !WILDCARD.equals(language));
    } catch (IllegalArgumentException malformedHeader) {
      return Stream.empty();
    }
  }

  private Stream<String> cookieLanguage(final HttpServletRequest currentRequest) {
    final Cookie[] cookies = currentRequest.getCookies();
    if (cookies == null) {
      return Stream.empty();
    }
    return Arrays.stream(cookies)
        .filter(cookie -> LANGUAGE_COOKIE_NAME.equals(cookie.getName()))
        .map(Cookie::getValue)
        .filter(StringUtils::hasText)
        .limit(1);
  }

  /** {@code en-GB} is served by the {@code en} text; texts are stored per primary language. */
  private static String primaryLanguageOf(final String languageRange) {
    final int separator = languageRange.indexOf('-');
    return separator < 0 ? languageRange : languageRange.substring(0, separator);
  }
}
