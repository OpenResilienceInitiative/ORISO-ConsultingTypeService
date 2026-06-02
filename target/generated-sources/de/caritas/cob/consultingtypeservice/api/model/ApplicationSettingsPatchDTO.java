package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ApplicationSettingsPatchDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-03-12T10:17:53.217957196Z[Etc/UTC]")
public class ApplicationSettingsPatchDTO {

  @JsonProperty("legalContentChangesBySingleTenantAdminsAllowed")
  private Boolean legalContentChangesBySingleTenantAdminsAllowed;

  @JsonProperty("mainTenantSubdomainForSingleDomainMultitenancy")
  private String mainTenantSubdomainForSingleDomainMultitenancy;

  @JsonProperty("globalFeatureSystemNotificationEmailsEnabled")
  private Boolean globalFeatureSystemNotificationEmailsEnabled;

  @JsonProperty("globalSmtpEnabled")
  private Boolean globalSmtpEnabled;

  @JsonProperty("globalSmtpHost")
  private String globalSmtpHost;

  @JsonProperty("globalSmtpPort")
  private String globalSmtpPort;

  @JsonProperty("globalSmtpSecure")
  private Boolean globalSmtpSecure;

  @JsonProperty("globalSmtpUsername")
  private String globalSmtpUsername;

  @JsonProperty("globalSmtpPassword")
  private String globalSmtpPassword;

  @JsonProperty("globalSmtpFrom")
  private String globalSmtpFrom;

  @JsonProperty("globalSmtpEmailThemeColor")
  private String globalSmtpEmailThemeColor;

  public ApplicationSettingsPatchDTO legalContentChangesBySingleTenantAdminsAllowed(Boolean legalContentChangesBySingleTenantAdminsAllowed) {
    this.legalContentChangesBySingleTenantAdminsAllowed = legalContentChangesBySingleTenantAdminsAllowed;
    return this;
  }

  /**
   * Get legalContentChangesBySingleTenantAdminsAllowed
   * @return legalContentChangesBySingleTenantAdminsAllowed
  */
  
  @Schema(name = "legalContentChangesBySingleTenantAdminsAllowed", required = false)
  public Boolean getLegalContentChangesBySingleTenantAdminsAllowed() {
    return legalContentChangesBySingleTenantAdminsAllowed;
  }

  public void setLegalContentChangesBySingleTenantAdminsAllowed(Boolean legalContentChangesBySingleTenantAdminsAllowed) {
    this.legalContentChangesBySingleTenantAdminsAllowed = legalContentChangesBySingleTenantAdminsAllowed;
  }

  public ApplicationSettingsPatchDTO mainTenantSubdomainForSingleDomainMultitenancy(String mainTenantSubdomainForSingleDomainMultitenancy) {
    this.mainTenantSubdomainForSingleDomainMultitenancy = mainTenantSubdomainForSingleDomainMultitenancy;
    return this;
  }

  /**
   * Get mainTenantSubdomainForSingleDomainMultitenancy
   * @return mainTenantSubdomainForSingleDomainMultitenancy
  */
  
  @Schema(name = "mainTenantSubdomainForSingleDomainMultitenancy", required = false)
  public String getMainTenantSubdomainForSingleDomainMultitenancy() {
    return mainTenantSubdomainForSingleDomainMultitenancy;
  }

  public void setMainTenantSubdomainForSingleDomainMultitenancy(String mainTenantSubdomainForSingleDomainMultitenancy) {
    this.mainTenantSubdomainForSingleDomainMultitenancy = mainTenantSubdomainForSingleDomainMultitenancy;
  }

  public ApplicationSettingsPatchDTO globalFeatureSystemNotificationEmailsEnabled(Boolean globalFeatureSystemNotificationEmailsEnabled) {
    this.globalFeatureSystemNotificationEmailsEnabled = globalFeatureSystemNotificationEmailsEnabled;
    return this;
  }

  /**
   * Get globalFeatureSystemNotificationEmailsEnabled
   * @return globalFeatureSystemNotificationEmailsEnabled
  */
  
  @Schema(name = "globalFeatureSystemNotificationEmailsEnabled", required = false)
  public Boolean getGlobalFeatureSystemNotificationEmailsEnabled() {
    return globalFeatureSystemNotificationEmailsEnabled;
  }

  public void setGlobalFeatureSystemNotificationEmailsEnabled(Boolean globalFeatureSystemNotificationEmailsEnabled) {
    this.globalFeatureSystemNotificationEmailsEnabled = globalFeatureSystemNotificationEmailsEnabled;
  }

  public ApplicationSettingsPatchDTO globalSmtpEnabled(Boolean globalSmtpEnabled) {
    this.globalSmtpEnabled = globalSmtpEnabled;
    return this;
  }

  /**
   * Get globalSmtpEnabled
   * @return globalSmtpEnabled
  */
  
  @Schema(name = "globalSmtpEnabled", required = false)
  public Boolean getGlobalSmtpEnabled() {
    return globalSmtpEnabled;
  }

  public void setGlobalSmtpEnabled(Boolean globalSmtpEnabled) {
    this.globalSmtpEnabled = globalSmtpEnabled;
  }

  public ApplicationSettingsPatchDTO globalSmtpHost(String globalSmtpHost) {
    this.globalSmtpHost = globalSmtpHost;
    return this;
  }

  /**
   * Get globalSmtpHost
   * @return globalSmtpHost
  */
  
  @Schema(name = "globalSmtpHost", required = false)
  public String getGlobalSmtpHost() {
    return globalSmtpHost;
  }

  public void setGlobalSmtpHost(String globalSmtpHost) {
    this.globalSmtpHost = globalSmtpHost;
  }

  public ApplicationSettingsPatchDTO globalSmtpPort(String globalSmtpPort) {
    this.globalSmtpPort = globalSmtpPort;
    return this;
  }

  /**
   * Get globalSmtpPort
   * @return globalSmtpPort
  */
  
  @Schema(name = "globalSmtpPort", required = false)
  public String getGlobalSmtpPort() {
    return globalSmtpPort;
  }

  public void setGlobalSmtpPort(String globalSmtpPort) {
    this.globalSmtpPort = globalSmtpPort;
  }

  public ApplicationSettingsPatchDTO globalSmtpSecure(Boolean globalSmtpSecure) {
    this.globalSmtpSecure = globalSmtpSecure;
    return this;
  }

  /**
   * Get globalSmtpSecure
   * @return globalSmtpSecure
  */
  
  @Schema(name = "globalSmtpSecure", required = false)
  public Boolean getGlobalSmtpSecure() {
    return globalSmtpSecure;
  }

  public void setGlobalSmtpSecure(Boolean globalSmtpSecure) {
    this.globalSmtpSecure = globalSmtpSecure;
  }

  public ApplicationSettingsPatchDTO globalSmtpUsername(String globalSmtpUsername) {
    this.globalSmtpUsername = globalSmtpUsername;
    return this;
  }

  /**
   * Get globalSmtpUsername
   * @return globalSmtpUsername
  */
  
  @Schema(name = "globalSmtpUsername", required = false)
  public String getGlobalSmtpUsername() {
    return globalSmtpUsername;
  }

  public void setGlobalSmtpUsername(String globalSmtpUsername) {
    this.globalSmtpUsername = globalSmtpUsername;
  }

  public ApplicationSettingsPatchDTO globalSmtpPassword(String globalSmtpPassword) {
    this.globalSmtpPassword = globalSmtpPassword;
    return this;
  }

  /**
   * Get globalSmtpPassword
   * @return globalSmtpPassword
  */
  
  @Schema(name = "globalSmtpPassword", required = false)
  public String getGlobalSmtpPassword() {
    return globalSmtpPassword;
  }

  public void setGlobalSmtpPassword(String globalSmtpPassword) {
    this.globalSmtpPassword = globalSmtpPassword;
  }

  public ApplicationSettingsPatchDTO globalSmtpFrom(String globalSmtpFrom) {
    this.globalSmtpFrom = globalSmtpFrom;
    return this;
  }

  /**
   * Get globalSmtpFrom
   * @return globalSmtpFrom
  */
  
  @Schema(name = "globalSmtpFrom", required = false)
  public String getGlobalSmtpFrom() {
    return globalSmtpFrom;
  }

  public void setGlobalSmtpFrom(String globalSmtpFrom) {
    this.globalSmtpFrom = globalSmtpFrom;
  }

  public ApplicationSettingsPatchDTO globalSmtpEmailThemeColor(String globalSmtpEmailThemeColor) {
    this.globalSmtpEmailThemeColor = globalSmtpEmailThemeColor;
    return this;
  }

  /**
   * Get globalSmtpEmailThemeColor
   * @return globalSmtpEmailThemeColor
  */
  
  @Schema(name = "globalSmtpEmailThemeColor", required = false)
  public String getGlobalSmtpEmailThemeColor() {
    return globalSmtpEmailThemeColor;
  }

  public void setGlobalSmtpEmailThemeColor(String globalSmtpEmailThemeColor) {
    this.globalSmtpEmailThemeColor = globalSmtpEmailThemeColor;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApplicationSettingsPatchDTO applicationSettingsPatchDTO = (ApplicationSettingsPatchDTO) o;
    return Objects.equals(this.legalContentChangesBySingleTenantAdminsAllowed, applicationSettingsPatchDTO.legalContentChangesBySingleTenantAdminsAllowed) &&
        Objects.equals(this.mainTenantSubdomainForSingleDomainMultitenancy, applicationSettingsPatchDTO.mainTenantSubdomainForSingleDomainMultitenancy) &&
        Objects.equals(this.globalFeatureSystemNotificationEmailsEnabled, applicationSettingsPatchDTO.globalFeatureSystemNotificationEmailsEnabled) &&
        Objects.equals(this.globalSmtpEnabled, applicationSettingsPatchDTO.globalSmtpEnabled) &&
        Objects.equals(this.globalSmtpHost, applicationSettingsPatchDTO.globalSmtpHost) &&
        Objects.equals(this.globalSmtpPort, applicationSettingsPatchDTO.globalSmtpPort) &&
        Objects.equals(this.globalSmtpSecure, applicationSettingsPatchDTO.globalSmtpSecure) &&
        Objects.equals(this.globalSmtpUsername, applicationSettingsPatchDTO.globalSmtpUsername) &&
        Objects.equals(this.globalSmtpPassword, applicationSettingsPatchDTO.globalSmtpPassword) &&
        Objects.equals(this.globalSmtpFrom, applicationSettingsPatchDTO.globalSmtpFrom) &&
        Objects.equals(this.globalSmtpEmailThemeColor, applicationSettingsPatchDTO.globalSmtpEmailThemeColor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(legalContentChangesBySingleTenantAdminsAllowed, mainTenantSubdomainForSingleDomainMultitenancy, globalFeatureSystemNotificationEmailsEnabled, globalSmtpEnabled, globalSmtpHost, globalSmtpPort, globalSmtpSecure, globalSmtpUsername, globalSmtpPassword, globalSmtpFrom, globalSmtpEmailThemeColor);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApplicationSettingsPatchDTO {\n");
    sb.append("    legalContentChangesBySingleTenantAdminsAllowed: ").append(toIndentedString(legalContentChangesBySingleTenantAdminsAllowed)).append("\n");
    sb.append("    mainTenantSubdomainForSingleDomainMultitenancy: ").append(toIndentedString(mainTenantSubdomainForSingleDomainMultitenancy)).append("\n");
    sb.append("    globalFeatureSystemNotificationEmailsEnabled: ").append(toIndentedString(globalFeatureSystemNotificationEmailsEnabled)).append("\n");
    sb.append("    globalSmtpEnabled: ").append(toIndentedString(globalSmtpEnabled)).append("\n");
    sb.append("    globalSmtpHost: ").append(toIndentedString(globalSmtpHost)).append("\n");
    sb.append("    globalSmtpPort: ").append(toIndentedString(globalSmtpPort)).append("\n");
    sb.append("    globalSmtpSecure: ").append(toIndentedString(globalSmtpSecure)).append("\n");
    sb.append("    globalSmtpUsername: ").append(toIndentedString(globalSmtpUsername)).append("\n");
    sb.append("    globalSmtpPassword: ").append(toIndentedString(globalSmtpPassword)).append("\n");
    sb.append("    globalSmtpFrom: ").append(toIndentedString(globalSmtpFrom)).append("\n");
    sb.append("    globalSmtpEmailThemeColor: ").append(toIndentedString(globalSmtpEmailThemeColor)).append("\n");
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

