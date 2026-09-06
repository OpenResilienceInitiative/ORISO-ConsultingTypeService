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
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalFeatureSystemNotificationEmailsEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpFrom;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpHost;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPassword;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPort;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpSecure;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpUsername;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.RestrictedTenantDTO;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.Theming;
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
  @Mock private TenantService tenantService;

  private DpaSigningEmailService service;

  @BeforeEach
  void setUp() {
    service =
        new DpaSigningEmailService(
            applicationSettingsService,
            smtpPasswordEncryptionService,
            dpaMailTransport,
            tenantService,
            "https://app.oriso-dev.site");
    org.springframework.test.util.ReflectionTestUtils.setField(
        service, "platformName", "Online-Beratung");
    org.springframework.test.util.ReflectionTestUtils.setField(service, "orgName", "ORISO");
    org.springframework.test.util.ReflectionTestUtils.setField(service, "orgAddress", "");
    org.springframework.test.util.ReflectionTestUtils.setField(service, "contactLine", "");
    org.springframework.test.util.ReflectionTestUtils.setField(service, "logoUrl", "");
    service.loadTemplates();
  }

  @Test
  void send_validDpaRequest_usesStoredCredentialsWithoutExposingThem() {
    when(applicationSettingsService.getApplicationSettings())
        .thenReturn(Optional.of(configuredSettings()));
    when(smtpPasswordEncryptionService.decrypt("encrypted-password")).thenReturn("secret");
    DpaMailSendReceipt transportReceipt =
        new DpaMailSendReceipt("bart.simpson@oriso.org", Instant.parse("2026-07-28T10:15:30Z"));
    when(dpaMailTransport.send(any(), anyString(), anyString(), anyString(), anyString()))
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
    ArgumentCaptor<String> text = ArgumentCaptor.forClass(String.class);
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
            html.capture(),
            text.capture());
    assertThat(subject.getValue()).isEqualTo("Auftragsverarbeitungsvertrag zur Unterschrift");
    assertThat(html.getValue())
        .contains("<!DOCTYPE html>")
        .contains("E2E Full Gate 202607191747")
        .contains("https://app.oriso-dev.site/dpa-sign/single-use-token")
        .contains("03.08.2026")
        .doesNotContain("secret");
    assertThat(text.getValue())
        .contains("E2E Full Gate 202607191747")
        .contains("https://app.oriso-dev.site/dpa-sign/single-use-token")
        .contains("03.08.2026")
        .doesNotContain("secret");
  }

  @Test
  void send_transportFailure_propagatesErrorInsteadOfSilentSuccess() {
    when(applicationSettingsService.getApplicationSettings())
        .thenReturn(Optional.of(configuredSettings()));
    when(smtpPasswordEncryptionService.decrypt("encrypted-password")).thenReturn("secret");
    when(dpaMailTransport.send(any(), anyString(), anyString(), anyString(), anyString()))
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

  @Test
  void send_withoutLogo_omitsImageCellAndUnresolvedTokens() {
    String html = renderBrandMail();
    assertThat(html).doesNotContain("<img", "src=\"\"", "{{");
  }

  @Test
  void send_platformBrandIsIndependentOfRecipientTenant() {
    var platform = new RestrictedTenantDTO();
    platform.setId(7L);
    platform.setName("Platform Brand");
    var theming = new Theming();
    theming.setLogo("data:image/png;base64,aGVsbG8=");
    theming.setPrimaryColor("#123456");
    platform.setTheming(theming);
    when(tenantService.getPlatformTenantData("app")).thenReturn(platform);
    de.caritas.cob.consultingtypeservice.api.tenant.TenantContext.setCurrentTenant(40L);
    try {
      assertThat(renderBrandMail())
          .contains("Platform Brand", "/service/tenant/public/branding/7/logo", "#123456")
          .contains("zwischen ORISO und Recipient Tenant")
          .doesNotContain("data:image", "/branding/40/", "{{");
    } finally {
      de.caritas.cob.consultingtypeservice.api.tenant.TenantContext.clear();
    }
  }

  @Test
  void send_foreignLogoDoesNotLeaveFirstPartyOrigin() {
    org.springframework.test.util.ReflectionTestUtils.setField(
        service, "logoUrl", "https://external.example/track.png");
    assertThat(renderBrandMail()).doesNotContain("<img", "external.example");
  }

  @Test
  void send_sameOriginHttpsLogoIsEscapedAndIncluded() {
    org.springframework.test.util.ReflectionTestUtils.setField(
        service, "logoUrl", "https://app.oriso-dev.site/assets/oriso.png");
    assertThat(renderBrandMail()).contains("<img", "https://app.oriso-dev.site/assets/oriso.png");
  }

  @Test
  void send_missingPlatformIdDoesNotInventImageEndpoint() {
    var platform = new RestrictedTenantDTO();
    var theming = new Theming();
    theming.setLogo("data:image/png;base64,aGVsbG8=");
    theming.setPrimaryColor("#ffffff");
    platform.setTheming(theming);
    when(tenantService.getPlatformTenantData("app")).thenReturn(platform);
    assertThat(renderBrandMail())
        .doesNotContain("<img", "/branding/null/")
        .contains("bgcolor=\"#a5000a\"");
  }

  @Test
  void send_legacyStoredBase64UsesReturnedPlatformId() {
    var platform = new RestrictedTenantDTO();
    platform.setId(7L);
    var theming = new Theming();
    theming.setLogo("aGVsbG8=");
    platform.setTheming(theming);
    when(tenantService.getPlatformTenantData("app")).thenReturn(platform);
    assertThat(renderBrandMail()).contains("/service/tenant/public/branding/7/logo");
  }

  @Test
  void send_platformLookupUnavailableRetainsConfiguredBrand() {
    when(tenantService.getPlatformTenantData("app"))
        .thenThrow(new IllegalStateException("owner unavailable"));
    assertThat(renderBrandMail()).contains("Online-Beratung", "ORISO").doesNotContain("{{", "<img");
  }

  private String renderBrandMail() {
    when(applicationSettingsService.getApplicationSettings())
        .thenReturn(Optional.of(configuredSettings()));
    when(smtpPasswordEncryptionService.decrypt("encrypted-password")).thenReturn("secret");
    service.send(
        new DpaSigningEmailService.DpaSigningEmailCommand(
            "bart.simpson@oriso.org",
            "Recipient Tenant",
            "https://app.oriso-dev.site/dpa-sign/synthetic-token",
            LocalDateTime.parse("2026-08-03T13:27:28")));
    ArgumentCaptor<String> html = ArgumentCaptor.forClass(String.class);
    verify(dpaMailTransport).send(any(), anyString(), anyString(), html.capture(), anyString());
    return html.getValue();
  }

  private ApplicationSettingsEntity configuredSettings() {
    var settings = new ApplicationSettingsEntity();
    settings.setGlobalFeatureSystemNotificationEmailsEnabled(
        new GlobalFeatureSystemNotificationEmailsEnabled().withValue(true).withReadOnly(false));
    settings.setMainTenantSubdomainForSingleDomainMultitenancy(
        new de.caritas.cob.consultingtypeservice.schemas.model
                .MainTenantSubdomainForSingleDomainMultitenancy()
            .withValue("app"));
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
