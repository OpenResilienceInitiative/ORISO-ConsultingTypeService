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
 * Drives the real {@link DpaSigningEmailService} constructor through Spring's {@code @Value}
 * resolution. A missing origin must stop the service from starting, never fall back to any host.
 */
class DpaSigningEmailServiceOriginBindingTest {

  private static final String OWN_ORIGIN = "https://self.example.org";

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withUserConfiguration(Collaborators.class, DpaSigningEmailService.class);

  @Test
  void missingDpaSignBaseUrl_failsStartup_evenWhenAppBaseUrlIsSet() {
    contextRunner
        .withPropertyValues("app.base.url=" + OWN_ORIGIN)
        .run(
            context ->
                assertThat(context)
                    .hasFailed()
                    .getFailure()
                    .rootCause()
                    .hasMessageContaining("DPA_SIGN_FRONTEND_BASE_URL"));
  }

  @Test
  void blankDpaSignBaseUrl_failsStartup_withTheEnvVarNamed() {
    contextRunner
        .withPropertyValues("dpa.sign.frontend.base-url= ")
        .run(
            context ->
                assertThat(context)
                    .hasFailed()
                    .getFailure()
                    .rootCause()
                    .hasMessageContaining("DPA_SIGN_FRONTEND_BASE_URL"));
  }

  @Test
  void configuredDpaSignBaseUrl_isTheOnlyAcceptedOrigin() {
    contextRunner
        .withPropertyValues("dpa.sign.frontend.base-url=" + OWN_ORIGIN)
        .run(
            context -> {
              assertThat(context).hasNotFailed();
              DpaSigningEmailService service = context.getBean(DpaSigningEmailService.class);

              assertThat(ReflectionTestUtils.getField(service, "permittedAppOrigin"))
                  .isEqualTo(URI.create(OWN_ORIGIN));
              assertThatThrownBy(
                      () -> service.send(command("https://other.example.org/dpa-sign/token")))
                  .isInstanceOf(BadRequestException.class)
                  .hasMessageContaining("signLink");
            });
  }

  private static DpaSigningEmailService.DpaSigningEmailCommand command(String signLink) {
    return new DpaSigningEmailService.DpaSigningEmailCommand(
        "bart.simpson@example.org",
        "Springfield Beratung",
        signLink,
        LocalDateTime.parse("2026-08-03T13:27:28"));
  }

  /**
   * Deliberately NOT a {@code @Configuration}: the real {@code @SpringBootTest} ITs component-scan
   * this package and would otherwise pick these mock beans up (CI run 105014992109).
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
  }
}
