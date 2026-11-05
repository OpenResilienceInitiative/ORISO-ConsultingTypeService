package de.caritas.cob.consultingtypeservice.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.caritas.cob.consultingtypeservice.api.auth.AuthorisationService;
import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.ServiceUnavailableException;
import de.caritas.cob.consultingtypeservice.config.apiclient.TenantServiceApiControllerFactory;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.ApiClient;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.TenantControllerApi;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.BooleanPermissionPolicy;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.PermissionPolicyMode;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.TenantPermissionPolicies;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

class AppearanceLockTest {

  private static final long TENANT = 12L;

  private final TenantServiceApiControllerFactory factory =
      mock(TenantServiceApiControllerFactory.class);
  private final AuthorisationService authorisation = mock(AuthorisationService.class);
  private final TenantControllerApi api = mock(TenantControllerApi.class);
  private final ApiClient apiClient = mock(ApiClient.class);
  private final AppearanceLock lock =
      new AppearanceLock(factory, authorisation, new RestTemplateBuilder(), 3000, 8000);

  @BeforeEach
  void setup() {
    when(factory.createControllerApi(any(RestTemplate.class))).thenReturn(api);
    when(api.getApiClient()).thenReturn(apiClient);
    when(authorisation.findAccessToken()).thenReturn(Optional.of("caller-token"));
  }

  private static TenantPermissionPolicies appearance(Boolean value, PermissionPolicyMode mode) {
    return new TenantPermissionPolicies()
        .tenantId(TENANT)
        .putPoliciesItem("appearance", new BooleanPermissionPolicy().value(value).mode(mode));
  }

  @Test
  void isLocked_Should_BeTrue_When_AppearanceIsFalse() {
    when(api.getTenantPermissionPolicies(TENANT))
        .thenReturn(appearance(false, PermissionPolicyMode.ENFORCED));

    assertThat(lock.isLocked(TENANT)).isTrue();
    verify(apiClient).addDefaultHeader("Authorization", "Bearer caller-token");
  }

  @Test
  void isLocked_Should_BeTrue_When_AppearanceIsFalseButOnlySuggested() {
    when(api.getTenantPermissionPolicies(TENANT))
        .thenReturn(appearance(false, PermissionPolicyMode.SUGGESTED));

    assertThat(lock.isLocked(TENANT)).isTrue();
  }

  @Test
  void isLocked_Should_BeFalse_When_AppearanceIsTrue() {
    when(api.getTenantPermissionPolicies(TENANT))
        .thenReturn(appearance(true, PermissionPolicyMode.ENFORCED));

    assertThat(lock.isLocked(TENANT)).isFalse();
  }

  @Test
  void isLocked_Should_Throw_When_TheValueIsMissing() {
    when(api.getTenantPermissionPolicies(TENANT))
        .thenReturn(appearance(null, PermissionPolicyMode.ENFORCED));

    assertThatThrownBy(() -> lock.isLocked(TENANT)).isInstanceOf(ServiceUnavailableException.class);
  }

  @Test
  void isLocked_Should_Throw_When_TheAnswerHasNoPolicies() {
    when(api.getTenantPermissionPolicies(TENANT)).thenReturn(new TenantPermissionPolicies());

    assertThatThrownBy(() -> lock.isLocked(TENANT)).isInstanceOf(ServiceUnavailableException.class);
  }

  @Test
  void isLocked_Should_Throw_When_TheAnswerIsEmpty() {
    when(api.getTenantPermissionPolicies(TENANT)).thenReturn(null);

    assertThatThrownBy(() -> lock.isLocked(TENANT)).isInstanceOf(ServiceUnavailableException.class);
  }

  @Test
  void isLocked_Should_Throw_When_TenantServiceRefuses() {
    when(api.getTenantPermissionPolicies(TENANT))
        .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN));

    assertThatThrownBy(() -> lock.isLocked(TENANT))
        .isInstanceOf(ServiceUnavailableException.class)
        .hasCauseInstanceOf(ResponseStatusException.class);
  }

  @Test
  void isLocked_Should_Throw_When_TenantServiceIsDown() {
    when(api.getTenantPermissionPolicies(TENANT)).thenThrow(new RestClientException("down"));

    assertThatThrownBy(() -> lock.isLocked(TENANT)).isInstanceOf(ServiceUnavailableException.class);
  }

  @Test
  void isLocked_Should_Throw_When_ThereIsNoCallerToken() {
    when(authorisation.findAccessToken()).thenReturn(Optional.empty());

    assertThatThrownBy(() -> lock.isLocked(TENANT)).isInstanceOf(ServiceUnavailableException.class);
  }
}
