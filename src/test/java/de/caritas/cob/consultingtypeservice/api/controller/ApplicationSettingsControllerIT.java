package de.caritas.cob.consultingtypeservice.api.controller;

import static de.caritas.cob.consultingtypeservice.api.auth.UserRole.TENANT_ADMIN;
import static de.caritas.cob.consultingtypeservice.api.auth.UserRole.TOPIC_ADMIN;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.auth.UserRole;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsPatchDTO;
import de.caritas.cob.consultingtypeservice.api.repository.ApplicationSettingsRepository;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import de.caritas.cob.consultingtypeservice.api.util.JsonConverter;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPassword;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpUsername;
import java.util.Map;
import javax.servlet.http.Cookie;
import org.assertj.core.util.Lists;
import org.assertj.core.util.Maps;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.adapters.RefreshableKeycloakSecurityContext;
import org.keycloak.adapters.spi.KeycloakAccount;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@TestPropertySource(properties = "feature.multitenancy.with.single.domain.enabled=true")
@AutoConfigureMockMvc(addFilters = false)
class ApplicationSettingsControllerIT {

  private MockMvc mockMvc;

  @Autowired private WebApplicationContext context;

  @Autowired private ApplicationSettingsRepository applicationSettingsRepository;

  @BeforeEach
  public void setup() {
    TenantContext.clear();
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
  }

  @Test
  void
      getApplicationSettings_Should_NotExposeSmtpCredentials_When_UserIsNotAuthenticatedAndCredentialsAreConfigured()
          throws Exception {
    // given
    ApplicationSettingsEntity entity = applicationSettingsRepository.findAll().get(0);
    entity.setGlobalSmtpUsername(
        new GlobalSmtpUsername().withValue("secret-smtp-user").withReadOnly(false));
    entity.setGlobalSmtpPassword(
        new GlobalSmtpPassword().withValue("secret-smtp-pass").withReadOnly(false));
    applicationSettingsRepository.save(entity);

    // when / then — CTS-C01: public GET /settings must not leak SMTP credentials
    mockMvc
        .perform(MockMvcRequestBuilders.get("/settings").accept(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.globalSmtpUsername").doesNotExist())
        .andExpect(jsonPath("$.globalSmtpPassword").doesNotExist());

    // clean up
    entity.setGlobalSmtpUsername(new GlobalSmtpUsername().withValue("").withReadOnly(false));
    entity.setGlobalSmtpPassword(new GlobalSmtpPassword().withValue("").withReadOnly(false));
    applicationSettingsRepository.save(entity);
  }

  @Test
  void getApplicationSettings_Should_ReturnApplicationSettings_When_UserIsNotAuthenticated()
      throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/settings").accept(APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.multitenancyWithSingleDomainEnabled.value").value(true))
        .andExpect(jsonPath("$.multitenancyWithSingleDomainEnabled.readOnly").value(true))
        .andExpect(jsonPath("$.multitenancyEnabled.value").value(false))
        .andExpect(jsonPath("$.multitenancyEnabled.readOnly").value(true))
        .andExpect(jsonPath("$.useTenantService.value").value(true))
        .andExpect(jsonPath("$.useTenantService.readOnly").value(false))
        .andExpect(jsonPath("$.useConsultingTypesForAgencies.value").value(false))
        .andExpect(jsonPath("$.useConsultingTypesForAgencies.readOnly").value(false))
        .andExpect(jsonPath("$.enableWalkthrough.value").value(false))
        .andExpect(jsonPath("$.enableWalkthrough.readOnly").value(false))
        .andExpect(jsonPath("$.disableVideoAppointments.value").value(true))
        .andExpect(jsonPath("$.disableVideoAppointments.readOnly").value(false))
        .andExpect(jsonPath("$.mainTenantSubdomainForSingleDomainMultitenancy.value").value("app"))
        .andExpect(
            jsonPath("$.mainTenantSubdomainForSingleDomainMultitenancy.readOnly").value(false))
        .andExpect(jsonPath("$.budibaseAuthClientId.value").value("budibaseAuthClientId"))
        .andExpect(jsonPath("$.budibaseAuthClientId.readOnly").value(false))
        .andExpect(jsonPath("$.calcomUrl.value").value("calcomUrl"))
        .andExpect(jsonPath("$.calcomUrl.readOnly").value(false))
        .andExpect(jsonPath("$.budibaseUrl.value").value("budibaseUrl"))
        .andExpect(jsonPath("$.budibaseUrl.readOnly").value(false))
        .andExpect(jsonPath("$.calendarAppUrl.value").value("calendarAppUrl"))
        .andExpect(jsonPath("$.calendarAppUrl.readOnly").value(false))
        .andExpect(jsonPath("$.useOverviewPage.value").value(false))
        .andExpect(jsonPath("$.useOverviewPage.readOnly").value(false))
        .andExpect(jsonPath("$.legalContentChangesBySingleTenantAdminsAllowed.value").value(true))
        .andExpect(
            jsonPath("$.legalContentChangesBySingleTenantAdminsAllowed.readOnly").value(false))
        .andExpect(jsonPath("$.globalFeatureSystemNotificationEmailsEnabled.value").value(false))
        .andExpect(jsonPath("$.globalFeatureSystemNotificationEmailsEnabled.readOnly").value(false))
        .andExpect(jsonPath("$.globalSmtpEnabled.value").value(false))
        .andExpect(jsonPath("$.globalSmtpEnabled.readOnly").value(false))
        .andExpect(jsonPath("$.globalSmtpHost.value").value(""))
        .andExpect(jsonPath("$.globalSmtpHost.readOnly").value(false))
        .andExpect(jsonPath("$.globalSmtpPort.value").value("587"))
        .andExpect(jsonPath("$.globalSmtpPort.readOnly").value(false))
        .andExpect(jsonPath("$.globalSmtpSecure.value").value(false))
        .andExpect(jsonPath("$.globalSmtpSecure.readOnly").value(false))
        .andExpect(jsonPath("$.globalSmtpUsername").doesNotExist())
        .andExpect(jsonPath("$.globalSmtpPassword").doesNotExist())
        .andExpect(jsonPath("$.globalSmtpFrom.value").value(""))
        .andExpect(jsonPath("$.globalSmtpFrom.readOnly").value(false))
        .andExpect(jsonPath("$.globalSmtpEmailThemeColor.value").value("#0f3b8f"))
        .andExpect(jsonPath("$.globalSmtpEmailThemeColor.readOnly").value(false))
        .andExpect(jsonPath("$.documentationEnabled.value").value(false))
        .andExpect(jsonPath("$.documentationEnabled.readOnly").value(true));
  }

  @Test
  void
      patchApplicationSettings_Should_ReturnUpdatedApplicationSettings_When_PatchOperationSuccessful()
          throws Exception {
    // given
    giveApplicationSettingEntityWithDynamicReleaseToggles();
    AuthenticationMockBuilder builder = new AuthenticationMockBuilder();
    Authentication authentication = builder.withUserRole(TENANT_ADMIN.getValue()).build();
    ApplicationSettingsPatchDTO patchDTO = new ApplicationSettingsPatchDTO();
    patchDTO.setLegalContentChangesBySingleTenantAdminsAllowed(false);
    patchDTO.setMainTenantSubdomainForSingleDomainMultitenancy("app2");
    patchDTO.setGlobalFeatureSystemNotificationEmailsEnabled(true);
    patchDTO.setGlobalSmtpEnabled(true);
    patchDTO.setGlobalSmtpHost("smtp.global.example");
    patchDTO.setGlobalSmtpPort("2525");
    patchDTO.setGlobalSmtpSecure(true);
    patchDTO.setGlobalSmtpUsername("global-user");
    patchDTO.setGlobalSmtpPassword("global-pass");
    patchDTO.setGlobalSmtpFrom("noreply@global.example");
    patchDTO.setGlobalSmtpEmailThemeColor("#112233");
    String jsonRequest = JsonConverter.convertToJson(patchDTO);
    mockMvc
        .perform(
            patch("/settingsadmin")
                .with(authentication(authentication))
                .header("csrfHeader", "csrfToken")
                .cookie(new Cookie("csrfCookie", "csrfToken"))
                .contentType(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .contentType(javax.ws.rs.core.MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.multitenancyWithSingleDomainEnabled.value").value(true))
        .andExpect(jsonPath("$.multitenancyWithSingleDomainEnabled.readOnly").value(true))
        .andExpect(jsonPath("$.multitenancyEnabled.value").value(false))
        .andExpect(jsonPath("$.multitenancyEnabled.readOnly").value(true))
        .andExpect(jsonPath("$.useTenantService.value").value(true))
        .andExpect(jsonPath("$.useTenantService.readOnly").value(false))
        .andExpect(jsonPath("$.useConsultingTypesForAgencies.value").value(false))
        .andExpect(jsonPath("$.useConsultingTypesForAgencies.readOnly").value(false))
        .andExpect(jsonPath("$.enableWalkthrough.value").value(false))
        .andExpect(jsonPath("$.enableWalkthrough.readOnly").value(false))
        .andExpect(jsonPath("$.disableVideoAppointments.value").value(true))
        .andExpect(jsonPath("$.disableVideoAppointments.readOnly").value(false))
        .andExpect(jsonPath("$.mainTenantSubdomainForSingleDomainMultitenancy.value").value("app2"))
        .andExpect(
            jsonPath("$.mainTenantSubdomainForSingleDomainMultitenancy.readOnly").value(false))
        .andExpect(jsonPath("$.budibaseAuthClientId.value").value("budibaseAuthClientId"))
        .andExpect(jsonPath("$.budibaseAuthClientId.readOnly").value(false))
        .andExpect(jsonPath("$.calcomUrl.value").value("calcomUrl"))
        .andExpect(jsonPath("$.calcomUrl.readOnly").value(false))
        .andExpect(jsonPath("$.budibaseUrl.value").value("budibaseUrl"))
        .andExpect(jsonPath("$.budibaseUrl.readOnly").value(false))
        .andExpect(jsonPath("$.calendarAppUrl.value").value("calendarAppUrl"))
        .andExpect(jsonPath("$.calendarAppUrl.readOnly").value(false))
        .andExpect(jsonPath("$.useOverviewPage.value").value(false))
        .andExpect(jsonPath("$.useOverviewPage.readOnly").value(false))
        .andExpect(jsonPath("$.legalContentChangesBySingleTenantAdminsAllowed.value").value(false))
        .andExpect(
            jsonPath("$.legalContentChangesBySingleTenantAdminsAllowed.readOnly").value(false))
        .andExpect(jsonPath("$.globalFeatureSystemNotificationEmailsEnabled.value").value(true))
        .andExpect(jsonPath("$.globalSmtpEnabled.value").value(true))
        .andExpect(jsonPath("$.globalSmtpHost.value").value("smtp.global.example"))
        .andExpect(jsonPath("$.globalSmtpPort.value").value("2525"))
        .andExpect(jsonPath("$.globalSmtpSecure.value").value(true))
        .andExpect(jsonPath("$.globalSmtpUsername").doesNotExist())
        .andExpect(jsonPath("$.globalSmtpPassword").doesNotExist())
        .andExpect(jsonPath("$.globalSmtpFrom.value").value("noreply@global.example"))
        .andExpect(jsonPath("$.globalSmtpEmailThemeColor.value").value("#112233"))
        .andExpect(jsonPath("$.releaseToggles.featureToggleTenantCreationEnabled").value(true));

    // clean up
    resetSettingsToPreviousState(authentication);
  }

  private void resetSettingsToPreviousState(Authentication authentication) throws Exception {
    var patchDTO = new ApplicationSettingsPatchDTO();
    patchDTO.setLegalContentChangesBySingleTenantAdminsAllowed(true);
    patchDTO.setMainTenantSubdomainForSingleDomainMultitenancy("app");
    patchDTO.setGlobalFeatureSystemNotificationEmailsEnabled(false);
    patchDTO.setGlobalSmtpEnabled(false);
    patchDTO.setGlobalSmtpHost("");
    patchDTO.setGlobalSmtpPort("587");
    patchDTO.setGlobalSmtpSecure(false);
    patchDTO.setGlobalSmtpUsername("");
    patchDTO.setGlobalSmtpPassword("");
    patchDTO.setGlobalSmtpFrom("");
    patchDTO.setGlobalSmtpEmailThemeColor("#0f3b8f");
    var jsonRequest = JsonConverter.convertToJson(patchDTO);
    mockMvc
        .perform(
            patch("/settingsadmin")
                .with(authentication(authentication))
                .header("csrfHeader", "csrfToken")
                .cookie(new Cookie("csrfCookie", "csrfToken"))
                .contentType(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .contentType(javax.ws.rs.core.MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void
      patchApplicationSettings_Should_ReturnForbidden_When_UserNameDoesNotHavePermissionToPatchSettings()
          throws Exception {
    AuthenticationMockBuilder builder = new AuthenticationMockBuilder();

    ApplicationSettingsPatchDTO patchDTO = new ApplicationSettingsPatchDTO();
    patchDTO.setLegalContentChangesBySingleTenantAdminsAllowed(false);
    String jsonRequest = JsonConverter.convertToJson(patchDTO);
    mockMvc
        .perform(
            patch("/settingsadmin")
                .with(authentication(builder.withUserRole(TOPIC_ADMIN.getValue()).build()))
                .header("csrfHeader", "csrfToken")
                .cookie(new Cookie("csrfCookie", "csrfToken"))
                .contentType(javax.ws.rs.core.MediaType.APPLICATION_JSON)
                .content(jsonRequest)
                .contentType(javax.ws.rs.core.MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  private void giveApplicationSettingEntityWithDynamicReleaseToggles() {
    ApplicationSettingsEntity entity = applicationSettingsRepository.findAll().get(0);
    entity.setReleaseToggles("featureToggleTenantCreationEnabled", true);
    applicationSettingsRepository.deleteAll();
    applicationSettingsRepository.save(entity);
  }

  private Authentication givenMockAuthentication(final UserRole authority) {
    final var securityContext = mock(RefreshableKeycloakSecurityContext.class);
    when(securityContext.getTokenString()).thenReturn("tokenString");
    final var token = mock(AccessToken.class, Mockito.RETURNS_DEEP_STUBS);
    when(securityContext.getToken()).thenReturn(token);
    givenOtherClaimsAreDefinedForToken(token);
    final KeycloakAccount mockAccount =
        new SimpleKeycloakAccount(() -> "user", Sets.newHashSet(), securityContext);
    return new KeycloakAuthenticationToken(
        mockAccount, true, Lists.newArrayList((GrantedAuthority) authority::getValue));
  }

  private void givenOtherClaimsAreDefinedForToken(final AccessToken token) {
    final Map<String, Object> claimMap = Maps.newHashMap("username", "test");
    claimMap.put("userId", "some userid");
    when(token.getOtherClaims()).thenReturn(claimMap);
  }
}
