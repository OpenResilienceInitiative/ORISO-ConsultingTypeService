package de.caritas.cob.consultingtypeservice.api.service;

import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsEntity;
import de.caritas.cob.consultingtypeservice.api.repository.ApplicationSettingsRepository;
import de.caritas.cob.consultingtypeservice.schemas.model.BudibaseAuthClientId;
import de.caritas.cob.consultingtypeservice.schemas.model.BudibaseUrl;
import de.caritas.cob.consultingtypeservice.schemas.model.CalcomUrl;
import de.caritas.cob.consultingtypeservice.schemas.model.CalendarAppUrl;
import de.caritas.cob.consultingtypeservice.schemas.model.DisableVideoAppointments;
import de.caritas.cob.consultingtypeservice.schemas.model.DocumentationEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.EnableWalkthrough;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalFeatureSystemNotificationEmailsEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpEmailThemeColor;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpFrom;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpHost;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPassword;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpPort;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpSecure;
import de.caritas.cob.consultingtypeservice.schemas.model.GlobalSmtpUsername;
import de.caritas.cob.consultingtypeservice.schemas.model.LegalContentChangesBySingleTenantAdminsAllowed;
import de.caritas.cob.consultingtypeservice.schemas.model.MainTenantSubdomainForSingleDomainMultitenancy;
import de.caritas.cob.consultingtypeservice.schemas.model.MultitenancyEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.MultitenancyWithSingleDomainEnabled;
import de.caritas.cob.consultingtypeservice.schemas.model.UseConsultingTypesForAgencies;
import de.caritas.cob.consultingtypeservice.schemas.model.UseOverviewPage;
import de.caritas.cob.consultingtypeservice.schemas.model.UseTenantService;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Loader for the consulting types from the file system. */
@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultApplicationSettingsInitializer {

  @Autowired private ApplicationSettingsRepository applicationSettingsRepository;

  @Autowired private ApplicationPermissionSettingsConverter applicationPermissionSettingsConverter;

  @Value("${multitenancy.enabled}")
  private boolean multitenancy;

  @Value("${feature.multitenancy.with.single.domain.enabled}")
  private boolean multitenancyWithSingleDomainEnabled;

  @Value("${setting.main.tenant.subdomain.for.single.domain.multitenancy}")
  private String mainTenantSubdomainForSingleDomainMultitenancy;

  @PostConstruct
  private void init() {
    if (applicationSettingsRepository.findAll().isEmpty()) {
      log.info("Initializing default application settings in mongo db");
      applicationSettingsRepository.save(createDefaultApplicationSettings());
    }
  }

  private ApplicationSettingsEntity createDefaultApplicationSettings() {
    ApplicationSettingsEntity entity = new ApplicationSettingsEntity();
    entity.setDisableVideoAppointments(
        new DisableVideoAppointments().withValue(true).withReadOnly(false));
    entity.setEnableWalkthrough(new EnableWalkthrough().withValue(false).withReadOnly(false));
    entity.setUseConsultingTypesForAgencies(
        new UseConsultingTypesForAgencies().withValue(false).withReadOnly(false));
    entity.setUseTenantService(
        new UseTenantService()
            .withValue(multitenancyWithSingleDomainEnabled || multitenancy)
            .withReadOnly(false));
    entity.setMultitenancyEnabled(
        new MultitenancyEnabled().withValue(multitenancy).withReadOnly(true));
    entity.setMultitenancyWithSingleDomainEnabled(
        new MultitenancyWithSingleDomainEnabled()
            .withReadOnly(true)
            .withValue(multitenancyWithSingleDomainEnabled));
    entity.setMainTenantSubdomainForSingleDomainMultitenancy(
        new MainTenantSubdomainForSingleDomainMultitenancy()
            .withReadOnly(false)
            .withValue(mainTenantSubdomainForSingleDomainMultitenancy));
    entity.setUseOverviewPage(new UseOverviewPage().withValue(false).withReadOnly(false));
    entity.setCalcomUrl(new CalcomUrl().withValue("calcomUrl").withReadOnly(false));
    entity.setBudibaseAuthClientId(
        new BudibaseAuthClientId().withValue("budibaseAuthClientId").withReadOnly(false));
    entity.setBudibaseUrl(new BudibaseUrl().withValue("budibaseUrl").withReadOnly(false));
    entity.setCalendarAppUrl(new CalendarAppUrl().withValue("calendarAppUrl").withReadOnly(false));
    entity.setLegalContentChangesBySingleTenantAdminsAllowed(
        new LegalContentChangesBySingleTenantAdminsAllowed().withValue(true).withReadOnly(false));
    entity.setDocumentationEnabled(new DocumentationEnabled().withValue(false).withReadOnly(true));
    entity.setGlobalFeatureSystemNotificationEmailsEnabled(
        new GlobalFeatureSystemNotificationEmailsEnabled().withValue(false).withReadOnly(false));
    entity.setGlobalSmtpEnabled(new GlobalSmtpEnabled().withValue(false).withReadOnly(false));
    entity.setGlobalSmtpHost(new GlobalSmtpHost().withValue("").withReadOnly(false));
    entity.setGlobalSmtpPort(new GlobalSmtpPort().withValue("587").withReadOnly(false));
    entity.setGlobalSmtpSecure(new GlobalSmtpSecure().withValue(false).withReadOnly(false));
    entity.setGlobalSmtpUsername(new GlobalSmtpUsername().withValue("").withReadOnly(false));
    entity.setGlobalSmtpPassword(new GlobalSmtpPassword().withValue("").withReadOnly(false));
    entity.setGlobalSmtpFrom(new GlobalSmtpFrom().withValue("").withReadOnly(false));
    entity.setGlobalSmtpEmailThemeColor(
        new GlobalSmtpEmailThemeColor().withValue("#0f3b8f").withReadOnly(false));
    entity.setSettings(applicationPermissionSettingsConverter.createDefaultSettings());
    return entity;
  }
}
