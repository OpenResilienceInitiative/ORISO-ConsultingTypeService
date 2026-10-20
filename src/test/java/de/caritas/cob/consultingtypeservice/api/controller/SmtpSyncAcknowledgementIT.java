package de.caritas.cob.consultingtypeservice.api.controller;

import static de.caritas.cob.consultingtypeservice.api.auth.UserRole.TENANT_ADMIN;
import static de.caritas.cob.consultingtypeservice.api.auth.UserRole.TOPIC_ADMIN;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.caritas.cob.consultingtypeservice.ConsultingTypeServiceApplication;
import de.caritas.cob.consultingtypeservice.api.auth.UserRole;
import de.caritas.cob.consultingtypeservice.api.repository.ApplicationSettingsRepository;
import de.caritas.cob.consultingtypeservice.api.service.SmtpSynchronizationStatus;
import de.caritas.cob.consultingtypeservice.api.tenant.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/** The Keycloak SMTP sync Job reports the exact snapshot revision it wrote (Helm#420). */
@SpringBootTest(classes = ConsultingTypeServiceApplication.class)
@TestPropertySource(properties = "spring.profiles.active=testing")
@TestPropertySource(properties = "feature.multitenancy.with.single.domain.enabled=true")
@TestPropertySource(properties = "settings.smtp.password.encryption.secret=test-only-smtp-secret")
@AutoConfigureMockMvc(addFilters = false)
class SmtpSyncAcknowledgementIT {
  private static final String ACK = "/settingsadmin/smtp-sync-acknowledgement";

  private MockMvc mockMvc;
  @Autowired private WebApplicationContext context;
  @Autowired private ApplicationSettingsRepository repository;

  @BeforeEach
  void setup() {
    TenantContext.clear();
    mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    pendingAt(7);
  }

  private void pendingAt(long revision) {
    var entity = repository.findAll().get(0);
    entity.setSmtpRevision(revision);
    entity.setSmtpPendingRevision(revision);
    entity.setSmtpAppliedRevision(revision - 1);
    entity.setSmtpSyncStatus(SmtpSynchronizationStatus.PENDING);
    repository.save(entity);
  }

  private Authentication as(UserRole role, String tenant) {
    var builder = new AuthenticationMockBuilder().withUserRole(role.getValue());
    return (tenant == null ? builder : builder.withTenantId(tenant)).build();
  }

  private ResultActions acknowledge(Authentication caller, String body) throws Exception {
    var request = post(ACK).contentType(APPLICATION_JSON).content(body);
    return mockMvc.perform(caller == null ? request : request.with(authentication(caller)));
  }

  private ResultActions syncStatus() throws Exception {
    return mockMvc.perform(
        get("/settingsadmin/smtp-sync-status")
            .with(authentication(as(TENANT_ADMIN, "0")))
            .accept(APPLICATION_JSON));
  }

  @Test
  void technicalCallerMarksTheExactWrittenRevisionApplied() throws Exception {
    acknowledge(as(UserRole.TECHNICAL, "0"), "{\"revision\":7,\"status\":\"APPLIED\"}")
        .andExpect(status().isNoContent());
    syncStatus()
        .andExpect(jsonPath("$.status").value("APPLIED"))
        .andExpect(jsonPath("$.revision").value(7))
        .andExpect(jsonPath("$.appliedRevision").value(7));
  }

  @Test
  void disabledSnapshotIsReportedAsDisabled() throws Exception {
    acknowledge(
            as(UserRole.TECHNICAL, "0"), "{\"revision\":7,\"status\":\"DISABLED_OR_INCOMPLETE\"}")
        .andExpect(status().isNoContent());
    syncStatus().andExpect(jsonPath("$.status").value("DISABLED_OR_INCOMPLETE"));
  }

  @Test
  void repeatedAcknowledgementOfTheSameRevisionIsIdempotent() throws Exception {
    var technical = as(UserRole.TECHNICAL, "0");
    acknowledge(technical, "{\"revision\":7,\"status\":\"APPLIED\"}")
        .andExpect(status().isNoContent());
    acknowledge(technical, "{\"revision\":7,\"status\":\"APPLIED\"}")
        .andExpect(status().isNoContent());
    syncStatus().andExpect(jsonPath("$.status").value("APPLIED"));
  }

  @Test
  void olderRevisionNeverMarksANewerPendingSaveSynced() throws Exception {
    pendingAt(8);
    acknowledge(as(UserRole.TECHNICAL, "0"), "{\"revision\":7,\"status\":\"APPLIED\"}")
        .andExpect(status().isConflict());
    syncStatus()
        .andExpect(jsonPath("$.status").value("SMTP_SYNC_PENDING"))
        .andExpect(jsonPath("$.revision").value(8));
  }

  @Test
  void futureRevisionIsRejected() throws Exception {
    acknowledge(as(UserRole.TECHNICAL, "0"), "{\"revision\":9,\"status\":\"APPLIED\"}")
        .andExpect(status().isConflict());
    syncStatus().andExpect(jsonPath("$.status").value("SMTP_SYNC_PENDING"));
  }

  @Test
  void platformAdminCannotFakeASuccessfulSync() throws Exception {
    acknowledge(as(TENANT_ADMIN, "0"), "{\"revision\":7,\"status\":\"APPLIED\"}")
        .andExpect(status().isForbidden());
    acknowledge(as(TOPIC_ADMIN, "0"), "{\"revision\":7,\"status\":\"APPLIED\"}")
        .andExpect(status().isForbidden());
    syncStatus().andExpect(jsonPath("$.status").value("SMTP_SYNC_PENDING"));
  }

  @Test
  void anonymousCallerIsUnauthorized() throws Exception {
    acknowledge(null, "{\"revision\":7,\"status\":\"APPLIED\"}")
        .andExpect(status().isUnauthorized());
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "{\"revision\":0,\"status\":\"APPLIED\"}",
        "{\"revision\":7,\"status\":\"SMTP_SYNC_PENDING\"}",
        "{\"revision\":7}",
        "{\"status\":\"APPLIED\"}"
      })
  void invalidReportIsRejectedWithoutChange(String body) throws Exception {
    acknowledge(as(UserRole.TECHNICAL, "0"), body).andExpect(status().isBadRequest());
    syncStatus().andExpect(jsonPath("$.status").value("SMTP_SYNC_PENDING"));
  }
}
