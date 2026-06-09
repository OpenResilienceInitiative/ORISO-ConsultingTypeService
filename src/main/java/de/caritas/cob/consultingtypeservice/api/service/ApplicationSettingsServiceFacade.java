package de.caritas.cob.consultingtypeservice.api.service;

import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsDTO;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsPatchDTO;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalFeatureSystemNotificationEmailsEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpEmailThemeColor;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpFrom;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpHost;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPassword;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPort;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpSecure;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpUsername;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationSettingsServiceFacade {

  private final @NonNull ApplicationSettingsService applicationSettingsService;
  private final @NonNull ApplicationSettingsConverter applicationSettingsConverter;
  private final @NonNull ApplicationPermissionSettingsConverter
      applicationPermissionSettingsConverter;

  public Optional<ApplicationSettingsDTO> getApplicationSettings() {
    var applicationSettings = applicationSettingsService.getApplicationSettings();
    return applicationSettings.isPresent()
        ? Optional.of(applicationSettingsConverter.toDTO(applicationSettings.get()))
        : Optional.empty();
  }

  public void patchApplicationSettings(ApplicationSettingsPatchDTO settingsPatchDTO) {
    var applicationSettings = applicationSettingsService.getApplicationSettings();
    if (applicationSettings.isPresent()) {
      ApplicationSettingsEntity entity = applicationSettings.get();
      convertPatchedValues(settingsPatchDTO, entity);
      applicationSettingsService.saveApplicationSettings(entity);
    }
  }

  private void convertPatchedValues(
      ApplicationSettingsPatchDTO settingsPatchDTO, ApplicationSettingsEntity entity) {
    if (settingsPatchDTO.getLegalContentChangesBySingleTenantAdminsAllowed() != null) {
      entity
          .getLegalContentChangesBySingleTenantAdminsAllowed()
          .setValue(settingsPatchDTO.getLegalContentChangesBySingleTenantAdminsAllowed());
    }
    if (settingsPatchDTO.getMainTenantSubdomainForSingleDomainMultitenancy() != null) {
      entity
          .getMainTenantSubdomainForSingleDomainMultitenancy()
          .setValue(settingsPatchDTO.getMainTenantSubdomainForSingleDomainMultitenancy());
    }
    if (settingsPatchDTO.getGlobalFeatureSystemNotificationEmailsEnabled() != null) {
      if (entity.getGlobalFeatureSystemNotificationEmailsEnabled() == null) {
        entity.setGlobalFeatureSystemNotificationEmailsEnabled(
            new GlobalFeatureSystemNotificationEmailsEnabled()
                .withValue(false)
                .withReadOnly(false));
      }
      entity
          .getGlobalFeatureSystemNotificationEmailsEnabled()
          .setValue(settingsPatchDTO.getGlobalFeatureSystemNotificationEmailsEnabled());
    }
    if (settingsPatchDTO.getGlobalSmtpEnabled() != null) {
      if (entity.getGlobalSmtpEnabled() == null) {
        entity.setGlobalSmtpEnabled(new GlobalSmtpEnabled().withValue(false).withReadOnly(false));
      }
      entity.getGlobalSmtpEnabled().setValue(settingsPatchDTO.getGlobalSmtpEnabled());
    }
    if (settingsPatchDTO.getGlobalSmtpHost() != null) {
      if (entity.getGlobalSmtpHost() == null) {
        entity.setGlobalSmtpHost(new GlobalSmtpHost().withValue("").withReadOnly(false));
      }
      entity.getGlobalSmtpHost().setValue(settingsPatchDTO.getGlobalSmtpHost());
    }
    if (settingsPatchDTO.getGlobalSmtpPort() != null) {
      if (entity.getGlobalSmtpPort() == null) {
        entity.setGlobalSmtpPort(new GlobalSmtpPort().withValue("587").withReadOnly(false));
      }
      entity.getGlobalSmtpPort().setValue(settingsPatchDTO.getGlobalSmtpPort());
    }
    if (settingsPatchDTO.getGlobalSmtpSecure() != null) {
      if (entity.getGlobalSmtpSecure() == null) {
        entity.setGlobalSmtpSecure(new GlobalSmtpSecure().withValue(false).withReadOnly(false));
      }
      entity.getGlobalSmtpSecure().setValue(settingsPatchDTO.getGlobalSmtpSecure());
    }
    if (settingsPatchDTO.getGlobalSmtpUsername() != null) {
      if (entity.getGlobalSmtpUsername() == null) {
        entity.setGlobalSmtpUsername(new GlobalSmtpUsername().withValue("").withReadOnly(false));
      }
      entity.getGlobalSmtpUsername().setValue(settingsPatchDTO.getGlobalSmtpUsername());
    }
    if (settingsPatchDTO.getGlobalSmtpPassword() != null) {
      if (entity.getGlobalSmtpPassword() == null) {
        entity.setGlobalSmtpPassword(new GlobalSmtpPassword().withValue("").withReadOnly(false));
      }
      entity.getGlobalSmtpPassword().setValue(settingsPatchDTO.getGlobalSmtpPassword());
    }
    if (settingsPatchDTO.getGlobalSmtpFrom() != null) {
      if (entity.getGlobalSmtpFrom() == null) {
        entity.setGlobalSmtpFrom(new GlobalSmtpFrom().withValue("").withReadOnly(false));
      }
      entity.getGlobalSmtpFrom().setValue(settingsPatchDTO.getGlobalSmtpFrom());
    }
    if (settingsPatchDTO.getGlobalSmtpEmailThemeColor() != null) {
      if (entity.getGlobalSmtpEmailThemeColor() == null) {
        entity.setGlobalSmtpEmailThemeColor(
            new GlobalSmtpEmailThemeColor().withValue("#0f3b8f").withReadOnly(false));
      }
      entity
          .getGlobalSmtpEmailThemeColor()
          .setValue(settingsPatchDTO.getGlobalSmtpEmailThemeColor());
    }
    if (settingsPatchDTO.getSettings() != null) {
      if (entity.getSettings() == null) {
        entity.setSettings(applicationPermissionSettingsConverter.createDefaultSettings());
      }
      applicationPermissionSettingsConverter.applyPatch(
          entity.getSettings(), settingsPatchDTO.getSettings());
    }
  }
}
