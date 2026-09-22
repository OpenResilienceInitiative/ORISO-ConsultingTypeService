package de.caritas.cob.consultingtypeservice.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.service.ApplicationSettingsServiceFacade;
import de.caritas.cob.consultingtypeservice.api.service.DpaMailSendReceipt;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService;
import de.caritas.cob.consultingtypeservice.api.service.DpaSigningEmailService.DpaSigningEmailPreview;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Who may preview and send the DPA signing mail, through the real filter chain, JWT converter, role
 * mapper and method security (ORISO-Helm#367).
 *
 * <p>The public onboarding flow in UserService calls both operations as the service identity (realm
 * role {@code technical}). That identity gets a narrow authority for exactly these two operations;
 * human settings admins keep PATCH_APPLICATION_SETTINGS.
 */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
class DpaSigningEmailAuthorizationIT {

  private static final String REQUEST =
      "{\"recipientEmail\":\"bart.simpson@example.org\",\"tenantName\":\"Springfield\","
          + "\"signLink\":\"https://app.example.org/dpa-sign/single-use-token\","
          + "\"expiresAt\":\"2026-10-01T12:00:00\"}";

  @Autowired private WebApplicationContext context;

  @MockitoBean private JwtDecoder jwtDecoder;

  @MockitoBean private DpaSigningEmailService dpaSigningEmailService;

  @MockitoBean private ApplicationSettingsServiceFacade applicationSettingsServiceFacade;

  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    when(dpaSigningEmailService.preview(any()))
        .thenReturn(new DpaSigningEmailPreview("subject", "<a>signed</a>"));
    when(dpaSigningEmailService.send(any()))
        .thenReturn(
            new DpaMailSendReceipt(
                "bart.simpson@example.org", Instant.parse("2026-09-22T10:00:00Z")));
  }

  private void tokenWithRealmRoles(String... roles) {
    Jwt jwt =
        Jwt.withTokenValue("test-token")
            .header("alg", "none")
            .claim("sub", "subject")
            .claim("realm_access", Map.of("roles", List.of(roles)))
            .build();
    when(jwtDecoder.decode(any(String.class))).thenReturn(jwt);
  }

  private void expectStatus(String path, String role, int expectedStatus) throws Exception {
    tokenWithRealmRoles(role);
    mockMvc
        .perform(
            post(path)
                .header("Authorization", "Bearer test-token")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(REQUEST))
        .andExpect(status().is(expectedStatus));
  }

  @Test
  void preview_Should_beAllowed_When_callerIsTechnicalUser() throws Exception {
    expectStatus("/settingsadmin/dpa-signing-emails/preview", "technical", 200);
  }

  @Test
  void send_Should_beAllowed_When_callerIsTechnicalUser() throws Exception {
    expectStatus("/settingsadmin/dpa-signing-emails", "technical", 200);
  }

  @Test
  void preview_Should_stayAllowed_When_callerIsTenantAdmin() throws Exception {
    expectStatus("/settingsadmin/dpa-signing-emails/preview", "tenant-admin", 200);
  }

  @Test
  void send_Should_stayAllowed_When_callerIsTenantAdmin() throws Exception {
    expectStatus("/settingsadmin/dpa-signing-emails", "tenant-admin", 200);
  }

  @Test
  void send_Should_beForbidden_When_callerHasNeitherRole() throws Exception {
    expectStatus("/settingsadmin/dpa-signing-emails", "user", 403);
    verifyNoInteractions(dpaSigningEmailService);
  }

  @Test
  void patchApplicationSettings_Should_stayForbidden_When_callerIsTechnicalUser() throws Exception {
    tokenWithRealmRoles("technical");
    mockMvc
        .perform(
            patch("/settingsadmin")
                .header("Authorization", "Bearer test-token")
                .contentType(APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isForbidden());
    verifyNoInteractions(applicationSettingsServiceFacade);
  }
}
