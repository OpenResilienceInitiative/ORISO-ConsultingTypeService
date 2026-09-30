package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

class SmtpReconcileClientTest {
  HttpServer server;
  String url;
  JwtDecoder decoder;
  ObjectProvider<JwtDecoder> decoders;
  AtomicInteger helperCalls;
  String helperBody;
  String bearer;
  String tokenForm;
  int helperStatus;
  String ack;

  @BeforeEach
  void setup() throws Exception {
    decoder = mock(JwtDecoder.class);
    decoders = mock(ObjectProvider.class);
    when(decoders.getObject()).thenReturn(decoder);
    when(decoder.decode("opaque-signed-token"))
        .thenReturn(token("technical-sub", "app-client", true));
    helperCalls = new AtomicInteger();
    helperStatus = 200;
    ack = "{\"appliedRevision\":2,\"status\":\"APPLIED\"}";
    server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
    server.createContext(
        "/realms/realm/protocol/openid-connect/token",
        exchange -> {
          tokenForm = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
          byte[] body =
              "{\"access_token\":\"opaque-signed-token\"}".getBytes(StandardCharsets.UTF_8);
          exchange.getResponseHeaders().add("Content-Type", "application/json");
          exchange.sendResponseHeaders(200, body.length);
          exchange.getResponseBody().write(body);
          exchange.close();
        });
    server.createContext(
        "/smtp/reconcile",
        exchange -> {
          helperCalls.incrementAndGet();
          helperBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
          bearer = exchange.getRequestHeaders().getFirst("Authorization");
          byte[] body = ack.getBytes(StandardCharsets.UTF_8);
          exchange.getResponseHeaders().add("Content-Type", "application/json");
          exchange.sendResponseHeaders(helperStatus, body.length);
          exchange.getResponseBody().write(body);
          exchange.close();
        });
    server.start();
    url = "http://127.0.0.1:" + server.getAddress().getPort();
  }

  @AfterEach
  void cleanup() {
    server.stop(0);
  }

  SmtpReconcileClient client(String subject) {
    return new SmtpReconcileClient(
        new RestTemplateBuilder(),
        decoders,
        url + "/smtp/reconcile",
        url,
        "realm",
        "app-client",
        subject,
        "technical-user",
        "test-password");
  }

  Jwt token(String subject, String client, boolean valid) {
    return Jwt.withTokenValue("opaque-signed-token")
        .header("alg", "RS256")
        .subject(subject)
        .claim("azp", client)
        .claim("realm_access", Map.of("roles", List.of("technical")))
        .expiresAt(Instant.now().plusSeconds(valid ? 60 : -60))
        .build();
  }

  @Test
  void sendsOnlyRevisionAfterVerifiedTechnicalAuthentication() {
    var result = client("technical-sub").reconcile(1);
    assertThat(result.getAppliedRevision()).isEqualTo(2);
    assertThat(result.getStatus()).isEqualTo("APPLIED");
    assertThat(helperBody)
        .isEqualTo("{\"revision\":1}")
        .doesNotContain("test-password", "technical-user");
    assertThat(bearer).isEqualTo("Bearer opaque-signed-token");
    assertThat(tokenForm)
        .contains("grant_type=password", "client_id=app-client", "username=technical-user");
  }

  @ParameterizedTest
  @ValueSource(strings = {"subject", "client", "expiry", "signature", "role"})
  void untrustedOrWrongIdentityNeverReachesHelper(String reason) {
    if ("signature".equals(reason))
      when(decoder.decode(anyString())).thenThrow(new JwtException("private signature payload"));
    else if ("role".equals(reason))
      when(decoder.decode(anyString()))
          .thenReturn(
              Jwt.withTokenValue("opaque")
                  .header("alg", "RS256")
                  .subject("technical-sub")
                  .claim("azp", "app-client")
                  .expiresAt(Instant.now().plusSeconds(60))
                  .build());
    else
      when(decoder.decode(anyString()))
          .thenReturn(
              token(
                  "subject".equals(reason) ? "wrong" : "technical-sub",
                  "client".equals(reason) ? "wrong" : "app-client",
                  !"expiry".equals(reason)));
    assertThatThrownBy(() -> client("technical-sub").reconcile(1))
        .isInstanceOf(SmtpSynchronizationUnavailableException.class)
        .hasMessage("SMTP_SYNC_IDENTITY_UNAVAILABLE")
        .hasNoCause();
    assertThat(helperCalls.get()).isZero();
  }

  @Test
  void missingPinFailsNamedBeforeRequest() {
    assertThatThrownBy(() -> client("").reconcile(1))
        .hasMessage("SMTP_SYNC_IDENTITY_NOT_CONFIGURED");
    assertThat(helperCalls.get()).isZero();
  }

  @ParameterizedTest
  @ValueSource(ints = {403, 409, 502, 503})
  void failedApplyReturnsOnlySafeReason(int status) {
    helperStatus = status;
    ack = "PRIVATE SMTP PASSWORD error";
    assertThatThrownBy(() -> client("technical-sub").reconcile(1))
        .hasMessage("SMTP_SYNC_HELPER_UNAVAILABLE")
        .hasNoCause();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{}",
        "{\"appliedRevision\":0,\"status\":\"APPLIED\"}",
        "{\"appliedRevision\":1.5,\"status\":\"APPLIED\"}",
        "{\"appliedRevision\":2,\"status\":\"UNKNOWN\"}"
      })
  void malformedOrStaleAckCannotClearPending(String body) {
    ack = body;
    assertThatThrownBy(() -> client("technical-sub").reconcile(1))
        .hasMessage("SMTP_SYNC_INVALID_ACK");
  }
}
