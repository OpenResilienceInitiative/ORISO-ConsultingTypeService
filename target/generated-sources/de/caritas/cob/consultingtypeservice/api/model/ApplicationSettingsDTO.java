package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy;
import de.caritas.cob.consultingtypeservice.api.model.ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled;
import java.util.HashMap;
import java.util.Map;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ApplicationSettingsDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-12T10:17:53.217957196Z[Etc/UTC]")
public class ApplicationSettingsDTO {

  @JsonProperty("multitenancyWithSingleDomainEnabled")
  private ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled multitenancyWithSingleDomainEnabled;

  @JsonProperty("multitenancyEnabled")
  private ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled multitenancyEnabled;

  @JsonProperty("useTenantService")
  private ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled useTenantService;

  @JsonProperty("useConsultingTypesForAgencies")
  private ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled useConsultingTypesForAgencies;

  @JsonProperty("enableWalkthrough")
  private ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled enableWalkthrough;

  @JsonProperty("disableVideoAppointments")
  private ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled disableVideoAppointments;

  @JsonProperty("mainTenantSubdomainForSingleDomainMultitenancy")
  private ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy mainTenantSubdomainForSingleDomainMultitenancy;

  @JsonProperty("useOverviewPage")
  private ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled useOverviewPage;

  @JsonProperty("calcomUrl")
  private ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy calcomUrl;

  @JsonProperty("budibaseAuthClientId")
  private ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy budibaseAuthClientId;

  @JsonProperty("budibaseUrl")
  private ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy budibaseUrl;

  @JsonProperty("calendarAppUrl")
  private ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy calendarAppUrl;

  @JsonProperty("legalContentChangesBySingleTenantAdminsAllowed")
  private ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled legalContentChangesBySingleTenantAdminsAllowed;

  @JsonProperty("documentationEnabled")
  private ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled documentationEnabled;

  @JsonProperty("globalFeatureSystemNotificationEmailsEnabled")
  private ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled globalFeatureSystemNotificationEmailsEnabled;

  @JsonProperty("globalSmtpEnabled")
  private ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled globalSmtpEnabled;

  @JsonProperty("globalSmtpHost")
  private ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpHost;

  @JsonProperty("globalSmtpPort")
  private ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpPort;

  @JsonProperty("globalSmtpSecure")
  private ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled globalSmtpSecure;

  @JsonProperty("globalSmtpUsername")
  private ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpUsername;

  @JsonProperty("globalSmtpPassword")
  private ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpPassword;

  @JsonProperty("globalSmtpFrom")
  private ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpFrom;

  @JsonProperty("globalSmtpEmailThemeColor")
  private ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpEmailThemeColor;

  @JsonProperty("releaseToggles")
  @Valid
  private Map<String, Object> releaseToggles = null;

  public ApplicationSettingsDTO multitenancyWithSingleDomainEnabled(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled multitenancyWithSingleDomainEnabled) {
    this.multitenancyWithSingleDomainEnabled = multitenancyWithSingleDomainEnabled;
    return this;
  }

  /**
   * Get multitenancyWithSingleDomainEnabled
   * @return multitenancyWithSingleDomainEnabled
  */
  @NotNull @Valid 
  @Schema(name = "multitenancyWithSingleDomainEnabled", required = true)
  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled getMultitenancyWithSingleDomainEnabled() {
    return multitenancyWithSingleDomainEnabled;
  }

  public void setMultitenancyWithSingleDomainEnabled(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled multitenancyWithSingleDomainEnabled) {
    this.multitenancyWithSingleDomainEnabled = multitenancyWithSingleDomainEnabled;
  }

  public ApplicationSettingsDTO multitenancyEnabled(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled multitenancyEnabled) {
    this.multitenancyEnabled = multitenancyEnabled;
    return this;
  }

  /**
   * Get multitenancyEnabled
   * @return multitenancyEnabled
  */
  @NotNull @Valid 
  @Schema(name = "multitenancyEnabled", required = true)
  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled getMultitenancyEnabled() {
    return multitenancyEnabled;
  }

  public void setMultitenancyEnabled(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled multitenancyEnabled) {
    this.multitenancyEnabled = multitenancyEnabled;
  }

  public ApplicationSettingsDTO useTenantService(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled useTenantService) {
    this.useTenantService = useTenantService;
    return this;
  }

  /**
   * Get useTenantService
   * @return useTenantService
  */
  @Valid 
  @Schema(name = "useTenantService", required = false)
  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled getUseTenantService() {
    return useTenantService;
  }

  public void setUseTenantService(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled useTenantService) {
    this.useTenantService = useTenantService;
  }

  public ApplicationSettingsDTO useConsultingTypesForAgencies(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled useConsultingTypesForAgencies) {
    this.useConsultingTypesForAgencies = useConsultingTypesForAgencies;
    return this;
  }

  /**
   * Get useConsultingTypesForAgencies
   * @return useConsultingTypesForAgencies
  */
  @Valid 
  @Schema(name = "useConsultingTypesForAgencies", required = false)
  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled getUseConsultingTypesForAgencies() {
    return useConsultingTypesForAgencies;
  }

  public void setUseConsultingTypesForAgencies(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled useConsultingTypesForAgencies) {
    this.useConsultingTypesForAgencies = useConsultingTypesForAgencies;
  }

  public ApplicationSettingsDTO enableWalkthrough(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled enableWalkthrough) {
    this.enableWalkthrough = enableWalkthrough;
    return this;
  }

  /**
   * Get enableWalkthrough
   * @return enableWalkthrough
  */
  @Valid 
  @Schema(name = "enableWalkthrough", required = false)
  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled getEnableWalkthrough() {
    return enableWalkthrough;
  }

  public void setEnableWalkthrough(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled enableWalkthrough) {
    this.enableWalkthrough = enableWalkthrough;
  }

  public ApplicationSettingsDTO disableVideoAppointments(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled disableVideoAppointments) {
    this.disableVideoAppointments = disableVideoAppointments;
    return this;
  }

  /**
   * Get disableVideoAppointments
   * @return disableVideoAppointments
  */
  @Valid 
  @Schema(name = "disableVideoAppointments", required = false)
  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled getDisableVideoAppointments() {
    return disableVideoAppointments;
  }

  public void setDisableVideoAppointments(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled disableVideoAppointments) {
    this.disableVideoAppointments = disableVideoAppointments;
  }

  public ApplicationSettingsDTO mainTenantSubdomainForSingleDomainMultitenancy(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy mainTenantSubdomainForSingleDomainMultitenancy) {
    this.mainTenantSubdomainForSingleDomainMultitenancy = mainTenantSubdomainForSingleDomainMultitenancy;
    return this;
  }

  /**
   * Get mainTenantSubdomainForSingleDomainMultitenancy
   * @return mainTenantSubdomainForSingleDomainMultitenancy
  */
  @Valid 
  @Schema(name = "mainTenantSubdomainForSingleDomainMultitenancy", required = false)
  public ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy getMainTenantSubdomainForSingleDomainMultitenancy() {
    return mainTenantSubdomainForSingleDomainMultitenancy;
  }

  public void setMainTenantSubdomainForSingleDomainMultitenancy(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy mainTenantSubdomainForSingleDomainMultitenancy) {
    this.mainTenantSubdomainForSingleDomainMultitenancy = mainTenantSubdomainForSingleDomainMultitenancy;
  }

  public ApplicationSettingsDTO useOverviewPage(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled useOverviewPage) {
    this.useOverviewPage = useOverviewPage;
    return this;
  }

  /**
   * Get useOverviewPage
   * @return useOverviewPage
  */
  @Valid 
  @Schema(name = "useOverviewPage", required = false)
  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled getUseOverviewPage() {
    return useOverviewPage;
  }

  public void setUseOverviewPage(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled useOverviewPage) {
    this.useOverviewPage = useOverviewPage;
  }

  public ApplicationSettingsDTO calcomUrl(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy calcomUrl) {
    this.calcomUrl = calcomUrl;
    return this;
  }

  /**
   * Get calcomUrl
   * @return calcomUrl
  */
  @Valid 
  @Schema(name = "calcomUrl", required = false)
  public ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy getCalcomUrl() {
    return calcomUrl;
  }

  public void setCalcomUrl(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy calcomUrl) {
    this.calcomUrl = calcomUrl;
  }

  public ApplicationSettingsDTO budibaseAuthClientId(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy budibaseAuthClientId) {
    this.budibaseAuthClientId = budibaseAuthClientId;
    return this;
  }

  /**
   * Get budibaseAuthClientId
   * @return budibaseAuthClientId
  */
  @Valid 
  @Schema(name = "budibaseAuthClientId", required = false)
  public ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy getBudibaseAuthClientId() {
    return budibaseAuthClientId;
  }

  public void setBudibaseAuthClientId(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy budibaseAuthClientId) {
    this.budibaseAuthClientId = budibaseAuthClientId;
  }

  public ApplicationSettingsDTO budibaseUrl(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy budibaseUrl) {
    this.budibaseUrl = budibaseUrl;
    return this;
  }

  /**
   * Get budibaseUrl
   * @return budibaseUrl
  */
  @Valid 
  @Schema(name = "budibaseUrl", required = false)
  public ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy getBudibaseUrl() {
    return budibaseUrl;
  }

  public void setBudibaseUrl(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy budibaseUrl) {
    this.budibaseUrl = budibaseUrl;
  }

  public ApplicationSettingsDTO calendarAppUrl(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy calendarAppUrl) {
    this.calendarAppUrl = calendarAppUrl;
    return this;
  }

  /**
   * Get calendarAppUrl
   * @return calendarAppUrl
  */
  @Valid 
  @Schema(name = "calendarAppUrl", required = false)
  public ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy getCalendarAppUrl() {
    return calendarAppUrl;
  }

  public void setCalendarAppUrl(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy calendarAppUrl) {
    this.calendarAppUrl = calendarAppUrl;
  }

  public ApplicationSettingsDTO legalContentChangesBySingleTenantAdminsAllowed(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled legalContentChangesBySingleTenantAdminsAllowed) {
    this.legalContentChangesBySingleTenantAdminsAllowed = legalContentChangesBySingleTenantAdminsAllowed;
    return this;
  }

  /**
   * Get legalContentChangesBySingleTenantAdminsAllowed
   * @return legalContentChangesBySingleTenantAdminsAllowed
  */
  @Valid 
  @Schema(name = "legalContentChangesBySingleTenantAdminsAllowed", required = false)
  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled getLegalContentChangesBySingleTenantAdminsAllowed() {
    return legalContentChangesBySingleTenantAdminsAllowed;
  }

  public void setLegalContentChangesBySingleTenantAdminsAllowed(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled legalContentChangesBySingleTenantAdminsAllowed) {
    this.legalContentChangesBySingleTenantAdminsAllowed = legalContentChangesBySingleTenantAdminsAllowed;
  }

  public ApplicationSettingsDTO documentationEnabled(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled documentationEnabled) {
    this.documentationEnabled = documentationEnabled;
    return this;
  }

  /**
   * Get documentationEnabled
   * @return documentationEnabled
  */
  @Valid 
  @Schema(name = "documentationEnabled", required = false)
  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled getDocumentationEnabled() {
    return documentationEnabled;
  }

  public void setDocumentationEnabled(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled documentationEnabled) {
    this.documentationEnabled = documentationEnabled;
  }

  public ApplicationSettingsDTO globalFeatureSystemNotificationEmailsEnabled(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled globalFeatureSystemNotificationEmailsEnabled) {
    this.globalFeatureSystemNotificationEmailsEnabled = globalFeatureSystemNotificationEmailsEnabled;
    return this;
  }

  /**
   * Get globalFeatureSystemNotificationEmailsEnabled
   * @return globalFeatureSystemNotificationEmailsEnabled
  */
  @Valid 
  @Schema(name = "globalFeatureSystemNotificationEmailsEnabled", required = false)
  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled getGlobalFeatureSystemNotificationEmailsEnabled() {
    return globalFeatureSystemNotificationEmailsEnabled;
  }

  public void setGlobalFeatureSystemNotificationEmailsEnabled(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled globalFeatureSystemNotificationEmailsEnabled) {
    this.globalFeatureSystemNotificationEmailsEnabled = globalFeatureSystemNotificationEmailsEnabled;
  }

  public ApplicationSettingsDTO globalSmtpEnabled(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled globalSmtpEnabled) {
    this.globalSmtpEnabled = globalSmtpEnabled;
    return this;
  }

  /**
   * Get globalSmtpEnabled
   * @return globalSmtpEnabled
  */
  @Valid 
  @Schema(name = "globalSmtpEnabled", required = false)
  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled getGlobalSmtpEnabled() {
    return globalSmtpEnabled;
  }

  public void setGlobalSmtpEnabled(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled globalSmtpEnabled) {
    this.globalSmtpEnabled = globalSmtpEnabled;
  }

  public ApplicationSettingsDTO globalSmtpHost(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpHost) {
    this.globalSmtpHost = globalSmtpHost;
    return this;
  }

  /**
   * Get globalSmtpHost
   * @return globalSmtpHost
  */
  @Valid 
  @Schema(name = "globalSmtpHost", required = false)
  public ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy getGlobalSmtpHost() {
    return globalSmtpHost;
  }

  public void setGlobalSmtpHost(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpHost) {
    this.globalSmtpHost = globalSmtpHost;
  }

  public ApplicationSettingsDTO globalSmtpPort(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpPort) {
    this.globalSmtpPort = globalSmtpPort;
    return this;
  }

  /**
   * Get globalSmtpPort
   * @return globalSmtpPort
  */
  @Valid 
  @Schema(name = "globalSmtpPort", required = false)
  public ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy getGlobalSmtpPort() {
    return globalSmtpPort;
  }

  public void setGlobalSmtpPort(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpPort) {
    this.globalSmtpPort = globalSmtpPort;
  }

  public ApplicationSettingsDTO globalSmtpSecure(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled globalSmtpSecure) {
    this.globalSmtpSecure = globalSmtpSecure;
    return this;
  }

  /**
   * Get globalSmtpSecure
   * @return globalSmtpSecure
  */
  @Valid 
  @Schema(name = "globalSmtpSecure", required = false)
  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled getGlobalSmtpSecure() {
    return globalSmtpSecure;
  }

  public void setGlobalSmtpSecure(ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled globalSmtpSecure) {
    this.globalSmtpSecure = globalSmtpSecure;
  }

  public ApplicationSettingsDTO globalSmtpUsername(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpUsername) {
    this.globalSmtpUsername = globalSmtpUsername;
    return this;
  }

  /**
   * Get globalSmtpUsername
   * @return globalSmtpUsername
  */
  @Valid 
  @Schema(name = "globalSmtpUsername", required = false)
  public ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy getGlobalSmtpUsername() {
    return globalSmtpUsername;
  }

  public void setGlobalSmtpUsername(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpUsername) {
    this.globalSmtpUsername = globalSmtpUsername;
  }

  public ApplicationSettingsDTO globalSmtpPassword(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpPassword) {
    this.globalSmtpPassword = globalSmtpPassword;
    return this;
  }

  /**
   * Get globalSmtpPassword
   * @return globalSmtpPassword
  */
  @Valid 
  @Schema(name = "globalSmtpPassword", required = false)
  public ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy getGlobalSmtpPassword() {
    return globalSmtpPassword;
  }

  public void setGlobalSmtpPassword(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpPassword) {
    this.globalSmtpPassword = globalSmtpPassword;
  }

  public ApplicationSettingsDTO globalSmtpFrom(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpFrom) {
    this.globalSmtpFrom = globalSmtpFrom;
    return this;
  }

  /**
   * Get globalSmtpFrom
   * @return globalSmtpFrom
  */
  @Valid 
  @Schema(name = "globalSmtpFrom", required = false)
  public ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy getGlobalSmtpFrom() {
    return globalSmtpFrom;
  }

  public void setGlobalSmtpFrom(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpFrom) {
    this.globalSmtpFrom = globalSmtpFrom;
  }

  public ApplicationSettingsDTO globalSmtpEmailThemeColor(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpEmailThemeColor) {
    this.globalSmtpEmailThemeColor = globalSmtpEmailThemeColor;
    return this;
  }

  /**
   * Get globalSmtpEmailThemeColor
   * @return globalSmtpEmailThemeColor
  */
  @Valid 
  @Schema(name = "globalSmtpEmailThemeColor", required = false)
  public ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy getGlobalSmtpEmailThemeColor() {
    return globalSmtpEmailThemeColor;
  }

  public void setGlobalSmtpEmailThemeColor(ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy globalSmtpEmailThemeColor) {
    this.globalSmtpEmailThemeColor = globalSmtpEmailThemeColor;
  }

  public ApplicationSettingsDTO releaseToggles(Map<String, Object> releaseToggles) {
    this.releaseToggles = releaseToggles;
    return this;
  }

  public ApplicationSettingsDTO putReleaseTogglesItem(String key, Object releaseTogglesItem) {
    if (this.releaseToggles == null) {
      this.releaseToggles = new HashMap<>();
    }
    this.releaseToggles.put(key, releaseTogglesItem);
    return this;
  }

  /**
   * Get releaseToggles
   * @return releaseToggles
  */
  
  @Schema(name = "releaseToggles", required = false)
  public Map<String, Object> getReleaseToggles() {
    return releaseToggles;
  }

  public void setReleaseToggles(Map<String, Object> releaseToggles) {
    this.releaseToggles = releaseToggles;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApplicationSettingsDTO applicationSettingsDTO = (ApplicationSettingsDTO) o;
    return Objects.equals(this.multitenancyWithSingleDomainEnabled, applicationSettingsDTO.multitenancyWithSingleDomainEnabled) &&
        Objects.equals(this.multitenancyEnabled, applicationSettingsDTO.multitenancyEnabled) &&
        Objects.equals(this.useTenantService, applicationSettingsDTO.useTenantService) &&
        Objects.equals(this.useConsultingTypesForAgencies, applicationSettingsDTO.useConsultingTypesForAgencies) &&
        Objects.equals(this.enableWalkthrough, applicationSettingsDTO.enableWalkthrough) &&
        Objects.equals(this.disableVideoAppointments, applicationSettingsDTO.disableVideoAppointments) &&
        Objects.equals(this.mainTenantSubdomainForSingleDomainMultitenancy, applicationSettingsDTO.mainTenantSubdomainForSingleDomainMultitenancy) &&
        Objects.equals(this.useOverviewPage, applicationSettingsDTO.useOverviewPage) &&
        Objects.equals(this.calcomUrl, applicationSettingsDTO.calcomUrl) &&
        Objects.equals(this.budibaseAuthClientId, applicationSettingsDTO.budibaseAuthClientId) &&
        Objects.equals(this.budibaseUrl, applicationSettingsDTO.budibaseUrl) &&
        Objects.equals(this.calendarAppUrl, applicationSettingsDTO.calendarAppUrl) &&
        Objects.equals(this.legalContentChangesBySingleTenantAdminsAllowed, applicationSettingsDTO.legalContentChangesBySingleTenantAdminsAllowed) &&
        Objects.equals(this.documentationEnabled, applicationSettingsDTO.documentationEnabled) &&
        Objects.equals(this.globalFeatureSystemNotificationEmailsEnabled, applicationSettingsDTO.globalFeatureSystemNotificationEmailsEnabled) &&
        Objects.equals(this.globalSmtpEnabled, applicationSettingsDTO.globalSmtpEnabled) &&
        Objects.equals(this.globalSmtpHost, applicationSettingsDTO.globalSmtpHost) &&
        Objects.equals(this.globalSmtpPort, applicationSettingsDTO.globalSmtpPort) &&
        Objects.equals(this.globalSmtpSecure, applicationSettingsDTO.globalSmtpSecure) &&
        Objects.equals(this.globalSmtpUsername, applicationSettingsDTO.globalSmtpUsername) &&
        Objects.equals(this.globalSmtpPassword, applicationSettingsDTO.globalSmtpPassword) &&
        Objects.equals(this.globalSmtpFrom, applicationSettingsDTO.globalSmtpFrom) &&
        Objects.equals(this.globalSmtpEmailThemeColor, applicationSettingsDTO.globalSmtpEmailThemeColor) &&
        Objects.equals(this.releaseToggles, applicationSettingsDTO.releaseToggles);
  }

  @Override
  public int hashCode() {
    return Objects.hash(multitenancyWithSingleDomainEnabled, multitenancyEnabled, useTenantService, useConsultingTypesForAgencies, enableWalkthrough, disableVideoAppointments, mainTenantSubdomainForSingleDomainMultitenancy, useOverviewPage, calcomUrl, budibaseAuthClientId, budibaseUrl, calendarAppUrl, legalContentChangesBySingleTenantAdminsAllowed, documentationEnabled, globalFeatureSystemNotificationEmailsEnabled, globalSmtpEnabled, globalSmtpHost, globalSmtpPort, globalSmtpSecure, globalSmtpUsername, globalSmtpPassword, globalSmtpFrom, globalSmtpEmailThemeColor, releaseToggles);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApplicationSettingsDTO {\n");
    sb.append("    multitenancyWithSingleDomainEnabled: ").append(toIndentedString(multitenancyWithSingleDomainEnabled)).append("\n");
    sb.append("    multitenancyEnabled: ").append(toIndentedString(multitenancyEnabled)).append("\n");
    sb.append("    useTenantService: ").append(toIndentedString(useTenantService)).append("\n");
    sb.append("    useConsultingTypesForAgencies: ").append(toIndentedString(useConsultingTypesForAgencies)).append("\n");
    sb.append("    enableWalkthrough: ").append(toIndentedString(enableWalkthrough)).append("\n");
    sb.append("    disableVideoAppointments: ").append(toIndentedString(disableVideoAppointments)).append("\n");
    sb.append("    mainTenantSubdomainForSingleDomainMultitenancy: ").append(toIndentedString(mainTenantSubdomainForSingleDomainMultitenancy)).append("\n");
    sb.append("    useOverviewPage: ").append(toIndentedString(useOverviewPage)).append("\n");
    sb.append("    calcomUrl: ").append(toIndentedString(calcomUrl)).append("\n");
    sb.append("    budibaseAuthClientId: ").append(toIndentedString(budibaseAuthClientId)).append("\n");
    sb.append("    budibaseUrl: ").append(toIndentedString(budibaseUrl)).append("\n");
    sb.append("    calendarAppUrl: ").append(toIndentedString(calendarAppUrl)).append("\n");
    sb.append("    legalContentChangesBySingleTenantAdminsAllowed: ").append(toIndentedString(legalContentChangesBySingleTenantAdminsAllowed)).append("\n");
    sb.append("    documentationEnabled: ").append(toIndentedString(documentationEnabled)).append("\n");
    sb.append("    globalFeatureSystemNotificationEmailsEnabled: ").append(toIndentedString(globalFeatureSystemNotificationEmailsEnabled)).append("\n");
    sb.append("    globalSmtpEnabled: ").append(toIndentedString(globalSmtpEnabled)).append("\n");
    sb.append("    globalSmtpHost: ").append(toIndentedString(globalSmtpHost)).append("\n");
    sb.append("    globalSmtpPort: ").append(toIndentedString(globalSmtpPort)).append("\n");
    sb.append("    globalSmtpSecure: ").append(toIndentedString(globalSmtpSecure)).append("\n");
    sb.append("    globalSmtpUsername: ").append(toIndentedString(globalSmtpUsername)).append("\n");
    sb.append("    globalSmtpPassword: ").append(toIndentedString(globalSmtpPassword)).append("\n");
    sb.append("    globalSmtpFrom: ").append(toIndentedString(globalSmtpFrom)).append("\n");
    sb.append("    globalSmtpEmailThemeColor: ").append(toIndentedString(globalSmtpEmailThemeColor)).append("\n");
    sb.append("    releaseToggles: ").append(toIndentedString(releaseToggles)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

