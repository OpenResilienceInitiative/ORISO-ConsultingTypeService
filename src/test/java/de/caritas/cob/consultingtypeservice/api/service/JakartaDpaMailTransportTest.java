package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.caritas.cob.consultingtypeservice.api.exception.SmtpSendException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class JakartaDpaMailTransportTest {

  private final JakartaDpaMailTransport transport = new JakartaDpaMailTransport();

  @Test
  void send_transportFailure_throwsSmtpSendExceptionInsteadOfSilentSuccess() throws Exception {
    int closedPort;
    try (ServerSocket socket = new ServerSocket(0)) {
      closedPort = socket.getLocalPort();
    }
    DpaMailSettings settings =
        new DpaMailSettings("127.0.0.1", closedPort, false, "mailer", "secret", "from@oriso.org");

    assertThatThrownBy(
            () -> transport.send(settings, "admin@oriso.org", "subject", "<html></html>"))
        .isInstanceOf(SmtpSendException.class)
        .hasCauseInstanceOf(Exception.class);
  }

  @Test
  void send_successfulTransport_returnsReceiptWithRecipientAndTimestamp() throws Exception {
    try (FakeSmtpServer server = FakeSmtpServer.start()) {
      DpaMailSettings settings =
          new DpaMailSettings(
              "127.0.0.1", server.port(), false, "mailer", "secret", "from@oriso.org");
      Instant before = Instant.now();

      DpaMailSendReceipt receipt =
          transport.send(settings, "admin@oriso.org", "subject", "<html></html>");

      assertThat(receipt).isNotNull();
      assertThat(receipt.getRecipientEmail()).isEqualTo("admin@oriso.org");
      assertThat(receipt.getSentAt()).isNotNull().isAfterOrEqualTo(before);
      assertThat(server.awaitDelivery()).isTrue();
      assertThat(server.receivedCommands())
          .anyMatch(line -> line.toUpperCase().startsWith("RCPT TO"))
          .anyMatch(line -> line.contains("admin@oriso.org"));
    }
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

    boolean awaitDelivery() throws InterruptedException {
      return delivered.await(10, TimeUnit.SECONDS);
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
