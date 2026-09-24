package de.caritas.cob.consultingtypeservice.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.service.DpaMailSendReceipt;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService.DpaSigningEmailCommand;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService.DpaSigningEmailPreview;
import java.time.Instant;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Goes through the real dispatcher: the standalone controller test cannot see a shadowing mapping.
 */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@AutoConfigureMockMvc(addFilters = false)
class DpaSigningEmailRoutingIT {

  private static final String REQUEST =
      "{\"recipientEmail\":\"bart.simpson@example.org\",\"tenantName\":\"Springfield\","
          + "\"signLink\":\"https://app.example.org/dpa-sign/single-use-token\","
          + "\"expiresAt\":\"2026-10-01T12:00:00\"}";

  private static final RequestPostProcessor SETTINGS_ADMIN =
      user("settings-admin")
          .authorities(new SimpleGrantedAuthority("AUTHORIZATION_PATCH_APPLICATION_SETTINGS"));

  @Autowired private WebApplicationContext context;

  @MockitoBean private DpaSigningEmailService dpaSigningEmailService;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  void preview_Should_renderTheMail_When_theClientSendsJsonWithAnAbsoluteSignLink()
      throws Exception {
    when(dpaSigningEmailService.preview(any()))
        .thenReturn(new DpaSigningEmailPreview("subject", "<a>signed</a>"));

    mockMvc
        .perform(
            post("/settingsadmin/dpa-signing-emails/preview")
                .with(SETTINGS_ADMIN)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(REQUEST))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.html", containsString("signed")));

    ArgumentCaptor<DpaSigningEmailCommand> command =
        ArgumentCaptor.forClass(DpaSigningEmailCommand.class);
    verify(dpaSigningEmailService).preview(command.capture());
    org.assertj.core.api.Assertions.assertThat(command.getValue().getSignLink())
        .isEqualTo("https://app.example.org/dpa-sign/single-use-token");
    org.assertj.core.api.Assertions.assertThat(command.getValue().getExpiresAt())
        .isEqualTo(LocalDateTime.parse("2026-10-01T12:00:00"));
  }

  @Test
  void send_Should_reportSent_When_theClientSendsJsonWithAnAbsoluteSignLink() throws Exception {
    when(dpaSigningEmailService.send(any()))
        .thenReturn(
            new DpaMailSendReceipt(
                "bart.simpson@example.org", Instant.parse("2026-09-22T10:00:00Z")));

    mockMvc
        .perform(
            post("/settingsadmin/dpa-signing-emails")
                .with(SETTINGS_ADMIN)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(REQUEST))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("SENT"))
        .andExpect(jsonPath("$.recipientEmail").value("bart.simpson@example.org"));
  }

  @Test
  void preview_Should_answer400_When_signLinkIsBlank() throws Exception {
    mockMvc
        .perform(
            post("/settingsadmin/dpa-signing-emails/preview")
                .with(SETTINGS_ADMIN)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(REQUEST.replace("https://app.example.org/dpa-sign/single-use-token", "")))
        .andExpect(status().isBadRequest());
  }
}
