package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException;
import java.net.URI;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Drives {@link DpaSigningEmailService} through Spring's real {@code @Value} resolution. The
 * constructor tests pass the origin directly and would stay green if the fallback silently went
 * back to a production host; this test pins the contract that a missing {@code
 * dpa.sign.frontend.base-url} resolves to this environment's own {@code app.base.url}
 * (ORISO-Helm#349).
 */
class DpaSigningEmailServiceOriginBindingTest {

  private static final String OWN_ORIGIN = "https://self.example.org";

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner().withUserConfiguration(Collaborators.class);

  @Test
  void missingDpaSignBaseUrl_fallsBackToOwnAppBaseUrl_neverToProduction() {
    contextRunner
        .withPropertyValues("app.base.url=" + OWN_ORIGIN)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              DpaSigningEmailService service = context.getBean(DpaSigningEmailService.class);

              assertThat(ReflectionTestUtils.getField(service, "permittedAppOrigin"))
                  .isEqualTo(URI.create(OWN_ORIGIN));

              assertThatThrownBy(
                      () -> service.send(command("https://app.oriso.org/dpa-sign/token")))
                  .as("the former production default must no longer be an accepted origin")
                  .isInstanceOf(BadRequestException.class)
                  .hasMessageContaining("signLink");
              assertThatThrownBy(() -> service.send(command(OWN_ORIGIN + "/dpa-sign/token")))
                  .as("a link on the own origin passes the origin check")
                  .isNotInstanceOf(BadRequestException.class);
            });
  }

  @Test
  void explicitDpaSignBaseUrl_winsOverAppBaseUrl() {
    contextRunner
        .withPropertyValues(
            "app.base.url=" + OWN_ORIGIN, "dpa.sign.frontend.base-url=https://dpa.example.org")
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              DpaSigningEmailService service = context.getBean(DpaSigningEmailService.class);
              assertThat(ReflectionTestUtils.getField(service, "permittedAppOrigin"))
                  .isEqualTo(URI.create("https://dpa.example.org"));
            });
  }

  private static DpaSigningEmailService.DpaSigningEmailCommand command(String signLink) {
    return new DpaSigningEmailService.DpaSigningEmailCommand(
        "bart.simpson@oriso.org",
        "Springfield Beratung",
        signLink,
        LocalDateTime.parse("2026-08-03T13:27:28"));
  }

  /**
   * Deliberately NOT a {@code @Configuration}: the real {@code @SpringBootTest} ITs component-scan
   * this package and would otherwise pick these mock beans up, producing duplicate {@code
   * DpaMailTransport} beans (CI run 105014992109). {@code @Bean} methods on a plain class are
   * registered only through {@code withUserConfiguration}.
   */
  static class Collaborators {
    @Bean
    ApplicationSettingsService applicationSettingsService() {
      return mock(ApplicationSettingsService.class);
    }

    @Bean
    SmtpPasswordEncryptionService smtpPasswordEncryptionService() {
      return mock(SmtpPasswordEncryptionService.class);
    }

    @Bean
    DpaMailTransport dpaMailTransport() {
      return mock(DpaMailTransport.class);
    }

    @Bean
    DpaSigningEmailService dpaSigningEmailService(
        ApplicationSettingsService settings,
        SmtpPasswordEncryptionService encryption,
        DpaMailTransport transport,
        @org.springframework.beans.factory.annotation.Value(
                "${dpa.sign.frontend.base-url:${app.base.url}}")
            String appBaseUrl) {
      return new DpaSigningEmailService(settings, encryption, transport, appBaseUrl);
    }
  }
}
