package de.caritas.cob.consultingtypeservice.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService.DpaSigningEmailCommand;
import java.lang.reflect.Method;
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

  @Mock private DpaSigningEmailService dpaSigningEmailService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new DpaSigningEmailController(dpaSigningEmailService))
            .build();
  }

  @Test
  void send_validRequest_forwardsFixedDpaPayload() throws Exception {
    mockMvc
        .perform(
            post("/settingsadmin/dpa-signing-emails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"recipientEmail\":\"bart.simpson@oriso.org\","
                        + "\"tenantName\":\"E2E Full Gate 202607191747\","
                        + "\"signLink\":\"https://app.oriso-dev.site/dpa-sign/single-use-token\","
                        + "\"expiresAt\":\"2026-08-03T13:27:28.243207790\"}"))
        .andExpect(status().isNoContent());

    ArgumentCaptor<DpaSigningEmailCommand> command =
        ArgumentCaptor.forClass(DpaSigningEmailCommand.class);
    verify(dpaSigningEmailService).send(command.capture());
    assertThat(command.getValue().getRecipientEmail()).isEqualTo("bart.simpson@oriso.org");
    assertThat(command.getValue().getExpiresAt())
        .isEqualTo(LocalDateTime.parse("2026-08-03T13:27:28.243207790"));
  }

  @Test
  void send_isRestrictedToTenantSettingsAuthority() throws Exception {
    Method method =
        DpaSigningEmailController.class.getMethod(
            "send", DpaSigningEmailController.DpaSigningEmailRequest.class);
    assertThat(method.getAnnotation(PreAuthorize.class).value())
        .isEqualTo("hasAuthority('AUTHORIZATION_PATCH_APPLICATION_SETTINGS')");
  }
}
