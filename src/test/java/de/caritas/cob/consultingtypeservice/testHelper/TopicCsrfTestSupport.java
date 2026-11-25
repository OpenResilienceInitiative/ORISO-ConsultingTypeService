package de.caritas.cob.consultingtypeservice.testHelper;

import jakarta.servlet.http.Cookie;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class TopicCsrfTestSupport {
  public static final String HEADER = "X-CSRF-Token";
  public static final String COOKIE = "CSRF-TOKEN";
  public static final String VALUE = "topic-write-test-token";

  private TopicCsrfTestSupport() {}

  public static RequestPostProcessor topicCsrf() {
    return request -> {
      request.addHeader(HEADER, VALUE);
      request.setCookies(new Cookie(COOKIE, VALUE));
      return request;
    };
  }
}
