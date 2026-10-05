package de.caritas.cob.consultingtypeservice.api.controller;

import de.caritas.cob.consultingtypeservice.api.auth.AuthorisationService;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsDTO;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsPatchDTO;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsSmtpCredentialsDTO;
import de.caritas.cob.consultingtypeservice.api.model.SmtpSynchronizationStatusDTO;
import de.caritas.cob.consultingtypeservice.api.service.ApplicationSettingsServiceFacade;
import de.caritas.cob.consultingtypeservice.generated.api.controller.ApplicationsettingsControllerApi;
import io.swagger.annotations.Api;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;

/** Controller for consulting type API requests. */
@RestController
@RequiredArgsConstructor
@Api(tags = "applicationsettings-controller")
@Slf4j
public class ApplicationSettingsController implements ApplicationsettingsControllerApi {

  private final @NonNull ApplicationSettingsServiceFacade applicationSettingsServiceFacade;
  private final @NonNull AuthorisationService authorisationService;

  @Override
  public Optional<NativeWebRequest> getRequest() {
    return ApplicationsettingsControllerApi.super.getRequest();
  }

  /**
   * Returns application settings
   *
   * @return {@link ResponseEntity} containing application settings
   */
  @Override
  public ResponseEntity<ApplicationSettingsDTO> getApplicationSettings() {
    var settings = applicationSettingsServiceFacade.getApplicationSettings();
    return settings.isPresent()
        ? new ResponseEntity<>(settings.get(), HttpStatus.OK)
        : new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Override
  @PreAuthorize(
      "hasAuthority('AUTHORIZATION_PATCH_APPLICATION_SETTINGS') "
          + "or hasAuthority('ROLE_tenant-admin') "
          + "or hasAuthority('tenant-admin')")
  public ResponseEntity<ApplicationSettingsDTO> patchApplicationSettings(
      ApplicationSettingsPatchDTO settingsPatchDTO) {
    // enableWalkthrough is platform-wide; a tenant admin must not switch it for every tenant.
    if (settingsPatchDTO.getEnableWalkthrough() != null && !authorisationService.isSuperAdmin()) {
      throw new AccessDeniedException(
          "enableWalkthrough can only be changed by the platform admin");
    }
    var saved = applicationSettingsServiceFacade.patchApplicationSettings(settingsPatchDTO);
    return saved.isPresent()
        ? ResponseEntity.ok()
            .cacheControl(CacheControl.noStore())
            .header(
                "X-Smtp-Revision", Long.toString(saved.get().getSynchronization().getRevision()))
            .header("X-Smtp-Sync-Status", saved.get().getSynchronization().getStatus())
            .header("Access-Control-Expose-Headers", "X-Smtp-Revision, X-Smtp-Sync-Status")
            .body(saved.get().getSettings())
        : ResponseEntity.noContent().build();
  }

  @Override
  @PreAuthorize(
      "@authorisationService.isSuperAdmin() "
          + "or hasAuthority('AUTHORIZATION_TECHNICAL_DEFAULT')")
  public ResponseEntity<ApplicationSettingsSmtpCredentialsDTO> getGlobalSmtpCredentials() {
    var snapshot = applicationSettingsServiceFacade.getGlobalSmtpSnapshot();
    return snapshot.isPresent()
        ? ResponseEntity.ok()
            .cacheControl(CacheControl.noStore())
            .header("X-Smtp-Revision", Long.toString(snapshot.get().getRevision()))
            .body(snapshot.get().getCredentials())
        : ResponseEntity.noContent()
            .cacheControl(CacheControl.noStore())
            .header("X-Smtp-Revision", "0")
            .build();
  }

  @Override
  @PreAuthorize("@authorisationService.isSuperAdmin()")
  public ResponseEntity<SmtpSynchronizationStatusDTO> getSmtpSyncStatus() {
    var status = applicationSettingsServiceFacade.getSmtpSynchronizationStatus();
    var dto = new SmtpSynchronizationStatusDTO();
    dto.setRevision(status.getRevision());
    dto.setAppliedRevision(
        org.openapitools.jackson.nullable.JsonNullable.of(status.getAppliedRevision()));
    dto.setStatus(SmtpSynchronizationStatusDTO.StatusEnum.fromValue(status.getStatus()));
    return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(dto);
  }
}
