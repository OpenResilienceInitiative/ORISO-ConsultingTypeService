package de.caritas.cob.consultingtypeservice.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.caritas.cob.consultingtypeservice.api.ApiResponseEntityExceptionHandler;
import de.caritas.cob.consultingtypeservice.api.exception.SmtpSendException;
import de.caritas.cob.consultingtypeservice.api.model.DpaSigningEmailRequest;
import de.caritas.cob.consultingtypeservice.api.service.DpaMailSendReceipt;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService.DpaSigningEmailCommand;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService.DpaSigningEmailPreview;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class DpaSigningEmailControllerTest {

  private static final String VALID_REQUEST =
      "{\"recipientEmail\":\"bart.simpson@oriso.org\","
          + "\"tenantName\":\"E2E Full Gate 202607191747\","
          + "\"signLink\":\"https://app.oriso-dev.site/dpa-sign/single-use-token\","
          + "\"expiresAt\":\"2026-08-03T13:27:28.243207790\"}";

  @Mock private DpaSigningEmailService dpaSigningEmailService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new DpaSigningEmailController(dpaSigningEmailService))
            .setControllerAdvice(new ApiResponseEntityExceptionHandler())
            .build();
  }

  @Test
  void send_confirmedTransport_returnsSentStatusWithReceiptDetails() throws Exception {
    when(dpaSigningEmailService.send(any(DpaSigningEmailCommand.class)))
        .thenReturn(
            new DpaMailSendReceipt(
                "bart.simpson@oriso.org", Instant.parse("2026-07-28T10:15:30Z")));

    mockMvc
        .perform(
            post("/settingsadmin/dpa-signing-emails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_REQUEST))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("SENT"))
        .andExpect(jsonPath("$.recipientEmail").value("bart.simpson@oriso.org"))
        .andExpect(jsonPath("$.sentAt").value("2026-07-28T10:15:30Z"));

    ArgumentCaptor<DpaSigningEmailCommand> command =
        ArgumentCaptor.forClass(DpaSigningEmailCommand.class);
    verify(dpaSigningEmailService).send(command.capture());
    assertThat(command.getValue().getRecipientEmail()).isEqualTo("bart.simpson@oriso.org");
    assertThat(command.getValue().getExpiresAt())
        .isEqualTo(LocalDateTime.parse("2026-08-03T13:27:28.243207790"));
  }

  @Test
  void send_smtpFailure_returnsBadGatewayInsteadOfSuccess() throws Exception {
    when(dpaSigningEmailService.send(any(DpaSigningEmailCommand.class)))
        .thenThrow(new SmtpSendException("SMTP transport failed", new IllegalStateException()));

    mockMvc
        .perform(
            post("/settingsadmin/dpa-signing-emails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_REQUEST))
        .andExpect(status().isBadGateway());
  }

  @Test
  void preview_returnsRenderedContentWithoutCallingSend() throws Exception {
    when(dpaSigningEmailService.preview(any(DpaSigningEmailCommand.class)))
        .thenReturn(new DpaSigningEmailPreview("A subject", "<html>mail</html>"));

    mockMvc
        .perform(
            post("/settingsadmin/dpa-signing-emails/preview")
                .contentType(MediaType.APPLICATION_JSON)
                .content(VALID_REQUEST))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.subject").value("A subject"))
        .andExpect(jsonPath("$.html").value("<html>mail</html>"));

    verify(dpaSigningEmailService).preview(any(DpaSigningEmailCommand.class));
    verify(dpaSigningEmailService, never()).send(any(DpaSigningEmailCommand.class));
  }

  private static final String TENANT_SETTINGS_OR_TECHNICAL_DPA_MAIL =
      "hasAnyAuthority('AUTHORIZATION_PATCH_APPLICATION_SETTINGS',"
          + " 'AUTHORIZATION_TECHNICAL_SEND_DPA_SIGNING_EMAIL')";

  @Test
  void send_isRestrictedToTenantSettingsOrTechnicalDpaMailAuthority() throws Exception {
    Method method =
        DpaSigningEmailController.class.getMethod(
            "sendDpaSigningEmail", DpaSigningEmailRequest.class);
    assertThat(method.getAnnotation(PreAuthorize.class).value())
        .isEqualTo(TENANT_SETTINGS_OR_TECHNICAL_DPA_MAIL);
  }

  @Test
  void preview_isRestrictedToTenantSettingsOrTechnicalDpaMailAuthority() throws Exception {
    Method method =
        DpaSigningEmailController.class.getMethod(
            "previewDpaSigningEmail", DpaSigningEmailRequest.class);
    assertThat(method.getAnnotation(PreAuthorize.class).value())
        .isEqualTo(TENANT_SETTINGS_OR_TECHNICAL_DPA_MAIL);
  }
}
