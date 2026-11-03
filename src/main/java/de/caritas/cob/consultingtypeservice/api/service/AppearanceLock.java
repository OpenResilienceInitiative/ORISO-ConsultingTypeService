package de.caritas.cob.consultingtypeservice.api.service;

import de.caritas.cob.consultingtypeservice.api.auth.AuthorisationService;
import de.caritas.cob.consultingtypeservice.api.exception.httpresponses.ServiceUnavailableException;
import de.caritas.cob.consultingtypeservice.config.apiclient.TenantServiceApiControllerFactory;
import de.caritas.cob.consultingtypeservice.tenantservice.generated.web.model.BooleanPermissionPolicy;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Whether the platform admin has taken the editing rights of an organisation's admins away
 * (ORISO-TenantService#345): {@code permissionPolicies.appearance} resolved for the tenant, as the
 * tenant route reads it. Of this service's consulting type fields it locks the form of address.
 *
 * <p>Asked on every call and never cached: a switch the platform admin has just turned off has to
 * hold at once. The question goes to TenantService with the caller's own token, so TenantService
 * decides whether the caller may read that tenant, and the answer needs no service credential here.
 * Only a clear {@code false} locks, as on the tenant route. An answer that cannot be read is not
 * guessed: it ends in a {@link ServiceUnavailableException}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AppearanceLock {

  static final String APPEARANCE = "appearance";

  private final @NonNull TenantServiceApiControllerFactory tenantServiceApiControllerFactory;
  private final @NonNull AuthorisationService authorisationService;

  /**
   * @param tenantId the organisation whose consulting type is changed
   * @return true when its admins may not change what the lock covers
   * @throws ServiceUnavailableException when the policy could not be read
   */
  public boolean isLocked(Long tenantId) {
    String accessToken =
        authorisationService
            .findAccessToken()
            .orElseThrow(
                () -> new ServiceUnavailableException("No caller token to read the lock", null));
    BooleanPermissionPolicy appearance = readAppearancePolicy(tenantId, accessToken);
    if (appearance == null || appearance.getValue() == null) {
      throw new ServiceUnavailableException(
          "TenantService returned no appearance policy for tenant " + tenantId, null);
    }
    return Boolean.FALSE.equals(appearance.getValue());
  }

  private BooleanPermissionPolicy readAppearancePolicy(Long tenantId, String accessToken) {
    try {
      var api = tenantServiceApiControllerFactory.createControllerApi();
      api.getApiClient().addDefaultHeader("Authorization", "Bearer " + accessToken);
      var policies = api.getTenantPermissionPolicies(tenantId);
      return policies == null || policies.getPolicies() == null
          ? null
          : policies.getPolicies().get(APPEARANCE);
    } catch (RuntimeException e) {
      // The message of a client error names URL and status only, never the token.
      throw new ServiceUnavailableException(
          "Could not read the appearance policy of tenant " + tenantId, e);
    }
  }
}
