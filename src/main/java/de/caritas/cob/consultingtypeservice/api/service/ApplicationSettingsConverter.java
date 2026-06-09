package de.caritas.cob.consultingtypeservice.api.service;

import com.google.common.collect.Maps;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsDTO;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalFeatureSystemNotificationEmailsEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpEmailThemeColor;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpFrom;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpHost;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPassword;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPort;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpSecure;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpUsername;
import java.lang.reflect.Field;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@Component
@Slf4j
public class ApplicationSettingsConverter {

  private final ApplicationPermissionSettingsConverter applicationPermissionSettingsConverter;

  public ApplicationSettingsConverter(
      ApplicationPermissionSettingsConverter applicationPermissionSettingsConverter) {
    this.applicationPermissionSettingsConverter = applicationPermissionSettingsConverter;
  }

  public ApplicationSettingsDTO toDTO(ApplicationSettingsEntity applicationSettings) {
    var settingsDTO =
        new ApplicationSettingsDTO()
            .multitenancyWithSingleDomainEnabled(
                toFeatureToggleDTO(applicationSettings.getMultitenancyWithSingleDomainEnabled()))
            .multitenancyEnabled(toFeatureToggleDTO(applicationSettings.getMultitenancyEnabled()))
            .enableWalkthrough(toFeatureToggleDTO(applicationSettings.getEnableWalkthrough()))
            .disableVideoAppointments(
                toFeatureToggleDTO(applicationSettings.getDisableVideoAppointments()))
            .useTenantService(toFeatureToggleDTO(applicationSettings.getUseTenantService()))
            .useConsultingTypesForAgencies(
                toFeatureToggleDTO(applicationSettings.getUseConsultingTypesForAgencies()))
            .mainTenantSubdomainForSingleDomainMultitenancy(
                toSettingDTO(
                    applicationSettings.getMainTenantSubdomainForSingleDomainMultitenancy()))
            .useOverviewPage(toFeatureToggleDTO(applicationSettings.getUseOverviewPage()))
            .calcomUrl(toSettingDTO(applicationSettings.getCalcomUrl()))
            .budibaseAuthClientId(toSettingDTO(applicationSettings.getBudibaseAuthClientId()))
            .budibaseUrl(toSettingDTO(applicationSettings.getBudibaseUrl()))
            .calendarAppUrl(toSettingDTO(applicationSettings.getCalendarAppUrl()))
            .legalContentChangesBySingleTenantAdminsAllowed(
                toFeatureToggleDTO(
                    applicationSettings.getLegalContentChangesBySingleTenantAdminsAllowed()))
            .documentationEnabled(toFeatureToggleDTO(applicationSettings.getDocumentationEnabled()))
            .globalFeatureSystemNotificationEmailsEnabled(
                toFeatureToggleDTO(
                    applicationSettings.getGlobalFeatureSystemNotificationEmailsEnabled() != null
                        ? applicationSettings.getGlobalFeatureSystemNotificationEmailsEnabled()
                        : new GlobalFeatureSystemNotificationEmailsEnabled()
                            .withValue(false)
                            .withReadOnly(false)))
            .globalSmtpEnabled(
                toFeatureToggleDTO(
                    applicationSettings.getGlobalSmtpEnabled() != null
                        ? applicationSettings.getGlobalSmtpEnabled()
                        : new GlobalSmtpEnabled().withValue(false).withReadOnly(false)))
            .globalSmtpHost(
                toSettingDTO(
                    applicationSettings.getGlobalSmtpHost() != null
                        ? applicationSettings.getGlobalSmtpHost()
                        : new GlobalSmtpHost().withValue("").withReadOnly(false)))
            .globalSmtpPort(
                toSettingDTO(
                    applicationSettings.getGlobalSmtpPort() != null
                        ? applicationSettings.getGlobalSmtpPort()
                        : new GlobalSmtpPort().withValue("587").withReadOnly(false)))
            .globalSmtpSecure(
                toFeatureToggleDTO(
                    applicationSettings.getGlobalSmtpSecure() != null
                        ? applicationSettings.getGlobalSmtpSecure()
                        : new GlobalSmtpSecure().withValue(false).withReadOnly(false)))
            .globalSmtpUsername(
                toSettingDTO(
                    applicationSettings.getGlobalSmtpUsername() != null
                        ? applicationSettings.getGlobalSmtpUsername()
                        : new GlobalSmtpUsername().withValue("").withReadOnly(false)))
            .globalSmtpPassword(
                toSettingDTO(
                    applicationSettings.getGlobalSmtpPassword() != null
                        ? applicationSettings.getGlobalSmtpPassword()
                        : new GlobalSmtpPassword().withValue("").withReadOnly(false)))
            .globalSmtpFrom(
                toSettingDTO(
                    applicationSettings.getGlobalSmtpFrom() != null
                        ? applicationSettings.getGlobalSmtpFrom()
                        : new GlobalSmtpFrom().withValue("").withReadOnly(false)))
            .globalSmtpEmailThemeColor(
                toSettingDTO(
                    applicationSettings.getGlobalSmtpEmailThemeColor() != null
                        ? applicationSettings.getGlobalSmtpEmailThemeColor()
                        : new GlobalSmtpEmailThemeColor().withValue("#0f3b8f").withReadOnly(false)))
            .releaseToggles(Maps.newHashMap())
            .settings(
                applicationPermissionSettingsConverter.toDto(applicationSettings.getSettings()));

    settingsDTO.getReleaseToggles().putAll(applicationSettings.getReleaseToggles());
    return settingsDTO;
  }

  private ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy toSettingDTO(
      Object setting) {
    if (setting == null) {
      return null;
    }
    Boolean readOnly = getFieldValue(setting, "readOnly", Boolean.class);
    String value = getFieldValue(setting, "value", String.class);
    return new ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy()
        .readOnly(readOnly)
        .value(value);
  }

  private ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled toFeatureToggleDTO(
      Object setting) {
    if (setting == null) {
      return null;
    }
    Boolean readOnly = getFieldValue(setting, "readOnly", Boolean.class);
    Boolean value = getFieldValue(setting, "value", Boolean.class);
    return new ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled()
        .readOnly(readOnly)
        .value(value);
  }

  private <T> T getFieldValue(Object object, String fieldName, Class<T> fieldType) {
    Field field = ReflectionUtils.findField(object.getClass(), fieldName, fieldType);
    if (field != null) {
      ReflectionUtils.makeAccessible(field);
      return (T) ReflectionUtils.getField(field, object);
    } else {
      log.warn("Field value on object {} not found {} ", object, fieldName);
      return null;
    }
  }
}
