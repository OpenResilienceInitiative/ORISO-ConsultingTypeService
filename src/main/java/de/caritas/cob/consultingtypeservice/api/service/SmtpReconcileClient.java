package de.caritas.cob.consultingtypeservice.api.service;

import static de.caritas.cob.consultingtypeservice.api.service.SmtpSynchronizationUnavailableException.Reason.*;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Sends revision metadata only, using a dedicated confidential technical client with no realm-admin
 * rights.
 */
@Component
public class SmtpReconcileClient {
  @lombok.Value
  public static class Acknowledgement {
    long appliedRevision;
    String status;
  }

  private final RestTemplate http;
  private final RestTemplate tokenHttp;
  private final ObjectProvider<JwtDecoder> decoder;
  private final String url;
  private final String keycloakUrl;
  private final String realm;
  private final String clientId;
  private final String subject;
  private final String clientSecret;

  public SmtpReconcileClient(
      RestTemplateBuilder builder,
      ObjectProvider<JwtDecoder> decoder,
      @Value("${settings.smtp.reconcile.url:}") String url,
      @Value("${keycloak.auth-server-url:}") String keycloakUrl,
      @Value("${keycloak.realm:}") String realm,
      @Value("${identity.technical.client-id:}") String clientId,
      @Value("${identity.technical.subject:}") String subject,
      @Value("${identity.technical.client-secret:}") String clientSecret) {
    this.http =
        builder.connectTimeout(Duration.ofSeconds(3)).readTimeout(Duration.ofSeconds(40)).build();
    this.tokenHttp =
        builder.connectTimeout(Duration.ofSeconds(3)).readTimeout(Duration.ofSeconds(8)).build();
    this.decoder = decoder;
    this.url = url;
    this.keycloakUrl = keycloakUrl;
    this.realm = realm;
    this.clientId = clientId;
    this.subject = subject;
    this.clientSecret = clientSecret;
  }

  /**
   * Blank URL: no push helper; the Keycloak SMTP Job pulls and acknowledges (Helm#420). Push mode
   * remains only for installs still running the old helper pod.
   */
  public boolean isPushConfigured() {
    return !blank(url);
  }

  public Acknowledgement reconcile(long revision) {
    URI helper = configuredUri(url, SMTP_SYNC_HELPER_NOT_CONFIGURED);
    String token = technicalToken();
    var headers = new HttpHeaders();
    headers.setBearerAuth(token);
    headers.setContentType(MediaType.APPLICATION_JSON);
    try {
      var response =
          http.exchange(
              helper,
              HttpMethod.POST,
              new HttpEntity<>(Map.of("revision", revision), headers),
              Map.class);
      Map<?, ?> body = response.getBody();
      if (response.getStatusCode().value() != 200
          || body == null
          || !(body.get("appliedRevision") instanceof Integer
              || body.get("appliedRevision") instanceof Long)
          || ((Number) body.get("appliedRevision")).longValue() < revision
          || !(SmtpSynchronizationStatus.APPLIED.equals(body.get("status"))
              || SmtpSynchronizationStatus.DISABLED.equals(body.get("status")))) {
        throw new SmtpSynchronizationUnavailableException(SMTP_SYNC_INVALID_ACK);
      }
      return new Acknowledgement(
          ((Number) body.get("appliedRevision")).longValue(), (String) body.get("status"));
    } catch (SmtpSynchronizationUnavailableException safe) {
      throw safe;
    } catch (RuntimeException ignored) {
      throw new SmtpSynchronizationUnavailableException(SMTP_SYNC_HELPER_UNAVAILABLE);
    }
  }

  private String technicalToken() {
    if (blank(realm) || blank(clientId) || blank(subject) || blank(clientSecret))
      throw new SmtpSynchronizationUnavailableException(SMTP_SYNC_IDENTITY_NOT_CONFIGURED);
    URI base = configuredUri(keycloakUrl, SMTP_SYNC_IDENTITY_NOT_CONFIGURED);
    URI endpoint =
        UriComponentsBuilder.fromUri(base)
            .pathSegment("realms", realm, "protocol", "openid-connect", "token")
            .build()
            .encode()
            .toUri();
    var form = new SensitiveFormData();
    form.add("grant_type", "client_credentials");
    form.add("client_id", clientId);
    form.add("client_secret", clientSecret);
    var headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    try {
      var response = tokenHttp.postForEntity(endpoint, new HttpEntity<>(form, headers), Map.class);
      if (response.getStatusCode().value() != 200
          || response.getBody() == null
          || !(response.getBody().get("access_token") instanceof String)
          || ((String) response.getBody().get("access_token")).isBlank())
        throw new SmtpSynchronizationUnavailableException(SMTP_SYNC_IDENTITY_UNAVAILABLE);
      String token = (String) response.getBody().get("access_token");
      var jwt = decoder.getObject().decode(token);
      Object realmAccess = jwt.getClaim("realm_access");
      if (!subject.equals(jwt.getSubject())
          || !clientId.equals(jwt.getClaimAsString("azp"))
          || jwt.getExpiresAt() == null
          || !jwt.getExpiresAt().isAfter(Instant.now())
          || !(realmAccess instanceof Map)
          || !(((Map<?, ?>) realmAccess).get("roles") instanceof Collection)
          || !((Collection<?>) ((Map<?, ?>) realmAccess).get("roles")).contains("technical"))
        throw new SmtpSynchronizationUnavailableException(SMTP_SYNC_IDENTITY_UNAVAILABLE);
      return token;
    } catch (SmtpSynchronizationUnavailableException safe) {
      throw safe;
    } catch (RuntimeException ignored) {
      throw new SmtpSynchronizationUnavailableException(SMTP_SYNC_IDENTITY_UNAVAILABLE);
    }
  }

  private static boolean blank(String value) {
    return value == null || value.isBlank();
  }

  private static final class SensitiveFormData extends LinkedMultiValueMap<String, String> {

    @Override
    public String toString() {
      var sanitized = new LinkedMultiValueMap<String, String>();
      sanitized.putAll(this);
      if (sanitized.containsKey("client_secret")) {
        sanitized.put("client_secret", java.util.List.of("[REDACTED]"));
      }
      return sanitized.toString();
    }
  }

  private static URI configuredUri(
      String value, SmtpSynchronizationUnavailableException.Reason failure) {
    try {
      URI uri = URI.create(value);
      if (!("http".equals(uri.getScheme()) || "https".equals(uri.getScheme()))
          || uri.getHost() == null
          || uri.getUserInfo() != null
          || uri.getQuery() != null
          || uri.getFragment() != null) throw new IllegalArgumentException();
      return uri;
    } catch (RuntimeException ignored) {
      throw new SmtpSynchronizationUnavailableException(failure);
    }
  }
}
