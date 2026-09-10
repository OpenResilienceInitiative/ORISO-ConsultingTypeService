package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.caritas.cob.consultingtypeservice.api.exception.SmtpSendException;
import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.BadRequestException;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import de.caritas.cob.consultingtypeservice.api.service.email.DpaMailTemplateRenderer;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalFeatureSystemNotificationEmailsEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpFrom;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpHost;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPassword;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPort;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpSecure;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpUsername;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DpaSigningEmailServiceTest {

  @Mock private ApplicationSettingsService applicationSettingsService;
  @Mock private SmtpPasswordEncryptionService smtpPasswordEncryptionService;
  @Mock private DpaMailTransport dpaMailTransport;

  private DpaSigningEmailService service;

  @BeforeEach
  void setUp() {
    service =
        new DpaSigningEmailService(
            applicationSettingsService,
            smtpPasswordEncryptionService,
            dpaMailTransport,
            new DpaMailTemplateRenderer(
                "ORISO",
                "Sunflower Care gGmbH",
                "Musterweg 1, 28195 Bremen",
                "kontakt@oriso.org",
                "https://app.oriso-dev.site/datenschutz",
                "https://app.oriso-dev.site/impressum",
                "#b90013",
                "#b90013"),
            "https://app.oriso-dev.site");
  }

  @Test
  void send_validDpaRequest_usesStoredCredentialsWithoutExposingThem() {
    when(applicationSettingsService.getApplicationSettings())
        .thenReturn(Optional.of(configuredSettings()));
    when(smtpPasswordEncryptionService.decrypt("encrypted-password")).thenReturn("secret");
    DpaMailSendReceipt transportReceipt =
        new DpaMailSendReceipt("bart.simpson@oriso.org", Instant.parse("2026-07-28T10:15:30Z"));
    when(dpaMailTransport.send(any(), anyString(), anyString(), anyString()))
        .thenReturn(transportReceipt);

    DpaMailSendReceipt receipt =
        service.send(
            new DpaSigningEmailService.DpaSigningEmailCommand(
                "bart.simpson@oriso.org",
                "E2E Full Gate 202607191747",
                "https://app.oriso-dev.site/dpa-sign/single-use-token",
                LocalDateTime.parse("2026-08-03T13:27:28.243207790")));

    assertThat(receipt).isSameAs(transportReceipt);
    ArgumentCaptor<String> subject = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> html = ArgumentCaptor.forClass(String.class);
    verify(dpaMailTransport)
        .send(
            eq(
                new DpaMailSettings(
                    "smtp.dreambau.com",
                    587,
                    false,
                    "mailer@dreambau.com",
                    "secret",
                    "oriso@dreambau.com")),
            eq("bart.simpson@oriso.org"),
            subject.capture(),
            html.capture());
    assertThat(subject.getValue()).isEqualTo("Auftragsverarbeitungsvertrag zur Unterschrift");
    assertThat(html.getValue())
        .contains("Bitte prüfen Sie den Vertrag")
        .contains("https://app.oriso-dev.site/dpa-sign/single-use-token")
        .contains("03.08.2026")
        .doesNotContain("secret");
  }

  /**
   * The AVV mail is the design system's `avv-unterschrift` page, not an HTML string concatenated in
   * Java (owner report 2026-09-10). Three defects the hand-written version shipped and this pins
   * shut:
   *
   * <ul>
   *   <li>the Träger name sat in the subject as "AVV für " + name, so the generic fallback of the
   *       forwarding service produced the ungrammatical "AVV für Ihrer Organisation" — the kit
   *       subject carries no name at all and cannot decline anything wrongly;
   *   <li>the frame was a second, older design than every other platform mail;
   *   <li>it advertised an "Einmal-Link", wording that appears nowhere else in the product.
   * </ul>
   */
  @Test
  void send_Should_renderTheDesignSystemAvvTemplate_NotAHandWrittenHtmlString() {
    when(applicationSettingsService.getApplicationSettings())
        .thenReturn(Optional.of(configuredSettings()));
    when(smtpPasswordEncryptionService.decrypt("encrypted-password")).thenReturn("secret");
    when(dpaMailTransport.send(any(), anyString(), anyString(), anyString()))
        .thenReturn(new DpaMailSendReceipt("bart.simpson@oriso.org", Instant.now()));

    service.send(
        new DpaSigningEmailService.DpaSigningEmailCommand(
            "bart.simpson@oriso.org",
            "Sep10Träger",
            "https://app.oriso-dev.site/dpa-sign/single-use-token",
            LocalDateTime.parse("2026-09-24T07:18:00")));

    ArgumentCaptor<String> subject = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> html = ArgumentCaptor.forClass(String.class);
    verify(dpaMailTransport)
        .send(any(), eq("bart.simpson@oriso.org"), subject.capture(), html.capture());

    assertThat(subject.getValue())
        .as("no tenant name in the subject — that is what produced the broken declension")
        .isEqualTo("Auftragsverarbeitungsvertrag zur Unterschrift");
    assertThat(html.getValue())
        .as("the kit frame")
        .contains("background-color:#f2efef")
        .contains("border-radius:24px")
        .contains("font-family:Inter,");
    assertThat(html.getValue())
        .as("the kit copy, with the name in a slot that reads grammatically")
        .contains("Für Sep10Träger wurde ein Auftragsverarbeitungsvertrag erstellt.");
    assertThat(html.getValue())
        .as("no invented wording, and a footer that exists at all")
        .doesNotContain("Einmal-Link")
        .doesNotContain("einmalig verwendbar")
        .contains("Impressum");
  }

  @Test
  void send_transportFailure_propagatesErrorInsteadOfSilentSuccess() {
    when(applicationSettingsService.getApplicationSettings())
        .thenReturn(Optional.of(configuredSettings()));
    when(smtpPasswordEncryptionService.decrypt("encrypted-password")).thenReturn("secret");
    when(dpaMailTransport.send(any(), anyString(), anyString(), anyString()))
        .thenThrow(new SmtpSendException("SMTP transport failed", new IllegalStateException()));

    var command =
        new DpaSigningEmailService.DpaSigningEmailCommand(
            "bart.simpson@oriso.org",
            "E2E Full Gate 202607191747",
            "https://app.oriso-dev.site/dpa-sign/single-use-token",
            LocalDateTime.parse("2026-08-03T13:27:28.243207790"));

    assertThatThrownBy(() -> service.send(command)).isInstanceOf(SmtpSendException.class);
  }

  @Test
  void send_foreignOrigin_rejectsBeforeReadingCredentials() {
    var command =
        new DpaSigningEmailService.DpaSigningEmailCommand(
            "bart.simpson@oriso.org",
            "E2E Full Gate 202607191747",
            "https://attacker.example/dpa-sign/stolen-token",
            LocalDateTime.parse("2026-08-03T13:27:28.243207790"));

    assertThatThrownBy(() -> service.send(command))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("signLink");
    verifyNoInteractions(
        applicationSettingsService, smtpPasswordEncryptionService, dpaMailTransport);
  }

  private ApplicationSettingsEntity configuredSettings() {
    var settings = new ApplicationSettingsEntity();
    settings.setGlobalFeatureSystemNotificationEmailsEnabled(
        new GlobalFeatureSystemNotificationEmailsEnabled().withValue(true).withReadOnly(false));
    settings.setGlobalSmtpEnabled(new GlobalSmtpEnabled().withValue(true).withReadOnly(false));
    settings.setGlobalSmtpHost(
        new GlobalSmtpHost().withValue("smtp.dreambau.com").withReadOnly(false));
    settings.setGlobalSmtpPort(new GlobalSmtpPort().withValue("587").withReadOnly(false));
    settings.setGlobalSmtpSecure(new GlobalSmtpSecure().withValue(false).withReadOnly(false));
    settings.setGlobalSmtpUsername(
        new GlobalSmtpUsername().withValue("mailer@dreambau.com").withReadOnly(false));
    settings.setGlobalSmtpPassword(
        new GlobalSmtpPassword().withValue("encrypted-password").withReadOnly(false));
    settings.setGlobalSmtpFrom(
        new GlobalSmtpFrom().withValue("oriso@dreambau.com").withReadOnly(false));
    return settings;
  }
}
