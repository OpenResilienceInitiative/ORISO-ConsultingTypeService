package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.caritas.cob.consultingtypeservice.api.exception.SmtpSendException;
import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class JakartaDpaMailTransportTest {

  private final JakartaDpaMailTransport transport = new JakartaDpaMailTransport();

  /**
   * Hardening (parity with ORISO-Admin#569 / UserService JakartaInviteMailTransport): without
   * {@code mail.smtp.starttls.required} a malicious or misconfigured SMTP server that does not
   * offer STARTTLS silently downgrades the session to plaintext — including the AUTH exchange, i.e.
   * the operator SMTP credentials, and the DPA signing link itself.
   */
  @Test
  void buildSessionProperties_Should_requireStartTls_When_settingsNotSecure() {
    Properties properties = JakartaDpaMailTransport.buildSessionProperties(insecureSettings());

    assertThat(properties.getProperty("mail.smtp.starttls.enable")).isEqualTo("true");
    assertThat(properties.getProperty("mail.smtp.starttls.required")).isEqualTo("true");
  }

  /** The negotiated TLS certificate must match the configured host (MITM defense in depth). */
  @Test
  void buildSessionProperties_Should_checkServerIdentity_When_settingsNotSecure() {
    Properties properties = JakartaDpaMailTransport.buildSessionProperties(insecureSettings());

    assertThat(properties.getProperty("mail.smtp.ssl.checkserveridentity")).isEqualTo("true");
  }

  @Test
  void buildSessionProperties_Should_enableImplicitTlsAndCheckServerIdentity_When_settingsSecure() {
    Properties properties = JakartaDpaMailTransport.buildSessionProperties(secureSettings());

    assertThat(properties.getProperty("mail.smtp.ssl.enable")).isEqualTo("true");
    assertThat(properties.getProperty("mail.smtp.ssl.checkserveridentity")).isEqualTo("true");
    // Implicit TLS sessions must not also announce STARTTLS.
    assertThat(properties.getProperty("mail.smtp.starttls.enable")).isNull();
  }

  @Test
  void buildSessionProperties_Should_keepConnectionBasicsAndTimeouts() {
    Properties properties = JakartaDpaMailTransport.buildSessionProperties(insecureSettings());

    assertThat(properties.getProperty("mail.smtp.auth")).isEqualTo("true");
    assertThat(properties.getProperty("mail.smtp.host")).isEqualTo("mail.example.org");
    assertThat(properties.getProperty("mail.smtp.port")).isEqualTo("587");
    assertThat(properties.getProperty("mail.smtp.connectiontimeout")).isEqualTo("10000");
    assertThat(properties.getProperty("mail.smtp.timeout")).isEqualTo("10000");
    assertThat(properties.getProperty("mail.smtp.writetimeout")).isEqualTo("10000");
  }

  @Test
  void send_transportFailure_throwsSmtpSendExceptionInsteadOfSilentSuccess() throws Exception {
    // deterministic transport failure: the port stays bound by this test for its whole
    // duration (no TOCTOU window as with a probed-then-closed port) and every connection is
    // closed before the SMTP greeting, so the client fails fast instead of timing out
    try (ServerSocket rejectingServer = new ServerSocket(0)) {
      Thread rejector =
          new Thread(
              () -> {
                while (!rejectingServer.isClosed()) {
                  try {
                    rejectingServer.accept().close();
                  } catch (IOException e) {
                    return;
                  }
                }
              },
              "rejecting-smtp");
      rejector.setDaemon(true);
      rejector.start();
      DpaMailSettings settings =
          new DpaMailSettings(
              "127.0.0.1",
              rejectingServer.getLocalPort(),
              false,
              "mailer",
              "secret",
              "from@oriso.org");

      assertThatThrownBy(
              () -> transport.send(settings, "admin@oriso.org", "subject", "<html></html>"))
          .isInstanceOf(SmtpSendException.class)
          .hasCauseInstanceOf(Exception.class);
    }
  }

  @Test
  void send_malformedRecipient_throwsBadRequestInsteadOfSmtpSendException() {
    // a recipient that cannot even be parsed is a client error (400), not an upstream SMTP
    // failure (502): no connection attempt must happen, so no server is needed here
    DpaMailSettings settings =
        new DpaMailSettings("127.0.0.1", 25, false, "mailer", "secret", "from@oriso.org");

    assertThatThrownBy(
            () -> transport.send(settings, "not an email address", "subject", "<html></html>"))
        .isInstanceOf(BadRequestException.class);
  }

  /**
   * End-to-end proof of the hardening against a real (plaintext-only) SMTP conversation partner:
   * the server never announces STARTTLS, so the send must fail instead of continuing in the clear.
   * The command log proves the credentials and the DPA signing link never left the process — the
   * conversation stops at EHLO, before AUTH / MAIL FROM / DATA.
   */
  @Test
  void send_serverWithoutStartTls_failsInsteadOfDowngradingToPlaintext() throws Exception {
    try (FakeSmtpServer server = FakeSmtpServer.start()) {
      DpaMailSettings settings =
          new DpaMailSettings(
              "127.0.0.1", server.port(), false, "mailer", "secret", "from@oriso.org");

      assertThatThrownBy(
              () -> transport.send(settings, "admin@oriso.org", "subject", "<html></html>"))
          .isInstanceOf(SmtpSendException.class);

      assertThat(server.awaitDelivery(250)).isFalse();
      assertThat(server.receivedCommands())
          .anyMatch(line -> line.toUpperCase().startsWith("EHLO"))
          .noneMatch(line -> line.toUpperCase().startsWith("AUTH"))
          .noneMatch(line -> line.toUpperCase().startsWith("MAIL FROM"))
          .noneMatch(line -> line.toUpperCase().startsWith("RCPT TO"))
          .noneMatch(line -> line.toUpperCase().startsWith("DATA"));
    }
  }

  private static DpaMailSettings insecureSettings() {
    return new DpaMailSettings(
        "mail.example.org", 587, false, "user", "secret", "noreply@example.org");
  }

  private static DpaMailSettings secureSettings() {
    return new DpaMailSettings(
        "mail.example.org", 465, true, "user", "secret", "noreply@example.org");
  }

  /** Minimal in-process SMTP conversation partner; accepts one message, no TLS. */
  private static final class FakeSmtpServer implements AutoCloseable {

    private final ServerSocket serverSocket;
    private final Thread thread;
    private final List<String> commands = new ArrayList<>();
    private final CountDownLatch delivered = new CountDownLatch(1);

    private FakeSmtpServer(ServerSocket serverSocket) {
      this.serverSocket = serverSocket;
      this.thread = new Thread(this::serve, "fake-smtp");
      this.thread.setDaemon(true);
    }

    static FakeSmtpServer start() throws IOException {
      FakeSmtpServer server = new FakeSmtpServer(new ServerSocket(0));
      server.thread.start();
      return server;
    }

    int port() {
      return serverSocket.getLocalPort();
    }

    boolean awaitDelivery(long timeoutMillis) throws InterruptedException {
      return delivered.await(timeoutMillis, TimeUnit.MILLISECONDS);
    }

    synchronized List<String> receivedCommands() {
      return List.copyOf(commands);
    }

    private void serve() {
      try (Socket socket = serverSocket.accept();
          BufferedReader in =
              new BufferedReader(
                  new InputStreamReader(socket.getInputStream(), StandardCharsets.US_ASCII));
          PrintWriter out =
              new PrintWriter(socket.getOutputStream(), true, StandardCharsets.US_ASCII)) {
        out.print("220 fake-smtp ready\r\n");
        out.flush();
        boolean inData = false;
        String line;
        while ((line = in.readLine()) != null) {
          if (inData) {
            if (".".equals(line)) {
              inData = false;
              out.print("250 message accepted\r\n");
              out.flush();
              delivered.countDown();
            }
            continue;
          }
          synchronized (this) {
            commands.add(line);
          }
          String upper = line.toUpperCase();
          if (upper.startsWith("EHLO") || upper.startsWith("HELO")) {
            out.print("250-fake-smtp\r\n250 AUTH PLAIN LOGIN\r\n");
          } else if (upper.startsWith("AUTH")) {
            out.print("235 authenticated\r\n");
          } else if (upper.startsWith("MAIL FROM") || upper.startsWith("RCPT TO")) {
            out.print("250 ok\r\n");
          } else if (upper.startsWith("DATA")) {
            inData = true;
            out.print("354 end with <CRLF>.<CRLF>\r\n");
          } else if (upper.startsWith("QUIT")) {
            out.print("221 bye\r\n");
            out.flush();
            return;
          } else {
            out.print("250 ok\r\n");
          }
          out.flush();
        }
      } catch (IOException ignored) {
        // socket closed at end of test
      }
    }

    @Override
    public void close() throws IOException {
      serverSocket.close();
    }
  }
}
