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
 * ApplicationPermissionSettingsDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-06-09T16:15:48.686845100+07:00[Asia/Jakarta]")
public class ApplicationPermissionSettingsDTO {

  @JsonProperty("featureAnonymousChatEnabled")
  private Boolean featureAnonymousChatEnabled;

  @JsonProperty("featureGroupChatV2Enabled")
  private Boolean featureGroupChatV2Enabled;

  @JsonProperty("featureCallsEnabled")
  private Boolean featureCallsEnabled;

  @JsonProperty("featureSupervisionEnabled")
  private Boolean featureSupervisionEnabled;

  @JsonProperty("featureSupervisionAnonymousChatsEnabled")
  private Boolean featureSupervisionAnonymousChatsEnabled;

  @JsonProperty("featureSupervisionOneOnOneChatsEnabled")
  private Boolean featureSupervisionOneOnOneChatsEnabled;

  @JsonProperty("featureAudioCallsEnabled")
  private Boolean featureAudioCallsEnabled;

  @JsonProperty("featureAudioCallsAnonymousChatsEnabled")
  private Boolean featureAudioCallsAnonymousChatsEnabled;

  @JsonProperty("featureAudioCallsOneOnOneChatsEnabled")
  private Boolean featureAudioCallsOneOnOneChatsEnabled;

  @JsonProperty("featureAudioCallsGroupChatsEnabled")
  private Boolean featureAudioCallsGroupChatsEnabled;

  @JsonProperty("featureAudioCallsSupervisionChatsEnabled")
  private Boolean featureAudioCallsSupervisionChatsEnabled;

  @JsonProperty("featureVideoCallsEnabled")
  private Boolean featureVideoCallsEnabled;

  @JsonProperty("featureVideoCallsAnonymousChatsEnabled")
  private Boolean featureVideoCallsAnonymousChatsEnabled;

  @JsonProperty("featureVideoCallsOneOnOneChatsEnabled")
  private Boolean featureVideoCallsOneOnOneChatsEnabled;

  @JsonProperty("featureVideoCallsGroupChatsEnabled")
  private Boolean featureVideoCallsGroupChatsEnabled;

  @JsonProperty("featureVideoCallsSupervisionChatsEnabled")
  private Boolean featureVideoCallsSupervisionChatsEnabled;

  @JsonProperty("featureThreadsEnabled")
  private Boolean featureThreadsEnabled;

  @JsonProperty("featureThreadsAnonymousChatsEnabled")
  private Boolean featureThreadsAnonymousChatsEnabled;

  @JsonProperty("featureThreadsGroupChatsEnabled")
  private Boolean featureThreadsGroupChatsEnabled;

  @JsonProperty("featureThreadsOneOnOneEnabled")
  private Boolean featureThreadsOneOnOneEnabled;

  @JsonProperty("featureThreadsSupervisionChatsEnabled")
  private Boolean featureThreadsSupervisionChatsEnabled;

  @JsonProperty("featureVoiceMessagesEnabled")
  private Boolean featureVoiceMessagesEnabled;

  @JsonProperty("featureVoiceMessagesAnonymousChatsEnabled")
  private Boolean featureVoiceMessagesAnonymousChatsEnabled;

  @JsonProperty("featureVoiceMessagesOneOnOneChatsEnabled")
  private Boolean featureVoiceMessagesOneOnOneChatsEnabled;

  @JsonProperty("featureVoiceMessagesGroupChatsEnabled")
  private Boolean featureVoiceMessagesGroupChatsEnabled;

  @JsonProperty("featureVoiceMessagesSupervisionChatsEnabled")
  private Boolean featureVoiceMessagesSupervisionChatsEnabled;

  public ApplicationPermissionSettingsDTO featureAnonymousChatEnabled(Boolean featureAnonymousChatEnabled) {
    this.featureAnonymousChatEnabled = featureAnonymousChatEnabled;
    return this;
  }

  /**
   * Get featureAnonymousChatEnabled
   * @return featureAnonymousChatEnabled
  */
  
  @Schema(name = "featureAnonymousChatEnabled", required = false)
  public Boolean getFeatureAnonymousChatEnabled() {
    return featureAnonymousChatEnabled;
  }

  public void setFeatureAnonymousChatEnabled(Boolean featureAnonymousChatEnabled) {
    this.featureAnonymousChatEnabled = featureAnonymousChatEnabled;
  }

  public ApplicationPermissionSettingsDTO featureGroupChatV2Enabled(Boolean featureGroupChatV2Enabled) {
    this.featureGroupChatV2Enabled = featureGroupChatV2Enabled;
    return this;
  }

  /**
   * Get featureGroupChatV2Enabled
   * @return featureGroupChatV2Enabled
  */
  
  @Schema(name = "featureGroupChatV2Enabled", required = false)
  public Boolean getFeatureGroupChatV2Enabled() {
    return featureGroupChatV2Enabled;
  }

  public void setFeatureGroupChatV2Enabled(Boolean featureGroupChatV2Enabled) {
    this.featureGroupChatV2Enabled = featureGroupChatV2Enabled;
  }

  public ApplicationPermissionSettingsDTO featureCallsEnabled(Boolean featureCallsEnabled) {
    this.featureCallsEnabled = featureCallsEnabled;
    return this;
  }

  /**
   * Get featureCallsEnabled
   * @return featureCallsEnabled
  */
  
  @Schema(name = "featureCallsEnabled", required = false)
  public Boolean getFeatureCallsEnabled() {
    return featureCallsEnabled;
  }

  public void setFeatureCallsEnabled(Boolean featureCallsEnabled) {
    this.featureCallsEnabled = featureCallsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureSupervisionEnabled(Boolean featureSupervisionEnabled) {
    this.featureSupervisionEnabled = featureSupervisionEnabled;
    return this;
  }

  /**
   * Get featureSupervisionEnabled
   * @return featureSupervisionEnabled
  */
  
  @Schema(name = "featureSupervisionEnabled", required = false)
  public Boolean getFeatureSupervisionEnabled() {
    return featureSupervisionEnabled;
  }

  public void setFeatureSupervisionEnabled(Boolean featureSupervisionEnabled) {
    this.featureSupervisionEnabled = featureSupervisionEnabled;
  }

  public ApplicationPermissionSettingsDTO featureSupervisionAnonymousChatsEnabled(Boolean featureSupervisionAnonymousChatsEnabled) {
    this.featureSupervisionAnonymousChatsEnabled = featureSupervisionAnonymousChatsEnabled;
    return this;
  }

  /**
   * Get featureSupervisionAnonymousChatsEnabled
   * @return featureSupervisionAnonymousChatsEnabled
  */
  
  @Schema(name = "featureSupervisionAnonymousChatsEnabled", required = false)
  public Boolean getFeatureSupervisionAnonymousChatsEnabled() {
    return featureSupervisionAnonymousChatsEnabled;
  }

  public void setFeatureSupervisionAnonymousChatsEnabled(Boolean featureSupervisionAnonymousChatsEnabled) {
    this.featureSupervisionAnonymousChatsEnabled = featureSupervisionAnonymousChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureSupervisionOneOnOneChatsEnabled(Boolean featureSupervisionOneOnOneChatsEnabled) {
    this.featureSupervisionOneOnOneChatsEnabled = featureSupervisionOneOnOneChatsEnabled;
    return this;
  }

  /**
   * Get featureSupervisionOneOnOneChatsEnabled
   * @return featureSupervisionOneOnOneChatsEnabled
  */
  
  @Schema(name = "featureSupervisionOneOnOneChatsEnabled", required = false)
  public Boolean getFeatureSupervisionOneOnOneChatsEnabled() {
    return featureSupervisionOneOnOneChatsEnabled;
  }

  public void setFeatureSupervisionOneOnOneChatsEnabled(Boolean featureSupervisionOneOnOneChatsEnabled) {
    this.featureSupervisionOneOnOneChatsEnabled = featureSupervisionOneOnOneChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureAudioCallsEnabled(Boolean featureAudioCallsEnabled) {
    this.featureAudioCallsEnabled = featureAudioCallsEnabled;
    return this;
  }

  /**
   * Get featureAudioCallsEnabled
   * @return featureAudioCallsEnabled
  */
  
  @Schema(name = "featureAudioCallsEnabled", required = false)
  public Boolean getFeatureAudioCallsEnabled() {
    return featureAudioCallsEnabled;
  }

  public void setFeatureAudioCallsEnabled(Boolean featureAudioCallsEnabled) {
    this.featureAudioCallsEnabled = featureAudioCallsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureAudioCallsAnonymousChatsEnabled(Boolean featureAudioCallsAnonymousChatsEnabled) {
    this.featureAudioCallsAnonymousChatsEnabled = featureAudioCallsAnonymousChatsEnabled;
    return this;
  }

  /**
   * Get featureAudioCallsAnonymousChatsEnabled
   * @return featureAudioCallsAnonymousChatsEnabled
  */
  
  @Schema(name = "featureAudioCallsAnonymousChatsEnabled", required = false)
  public Boolean getFeatureAudioCallsAnonymousChatsEnabled() {
    return featureAudioCallsAnonymousChatsEnabled;
  }

  public void setFeatureAudioCallsAnonymousChatsEnabled(Boolean featureAudioCallsAnonymousChatsEnabled) {
    this.featureAudioCallsAnonymousChatsEnabled = featureAudioCallsAnonymousChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureAudioCallsOneOnOneChatsEnabled(Boolean featureAudioCallsOneOnOneChatsEnabled) {
    this.featureAudioCallsOneOnOneChatsEnabled = featureAudioCallsOneOnOneChatsEnabled;
    return this;
  }

  /**
   * Get featureAudioCallsOneOnOneChatsEnabled
   * @return featureAudioCallsOneOnOneChatsEnabled
  */
  
  @Schema(name = "featureAudioCallsOneOnOneChatsEnabled", required = false)
  public Boolean getFeatureAudioCallsOneOnOneChatsEnabled() {
    return featureAudioCallsOneOnOneChatsEnabled;
  }

  public void setFeatureAudioCallsOneOnOneChatsEnabled(Boolean featureAudioCallsOneOnOneChatsEnabled) {
    this.featureAudioCallsOneOnOneChatsEnabled = featureAudioCallsOneOnOneChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureAudioCallsGroupChatsEnabled(Boolean featureAudioCallsGroupChatsEnabled) {
    this.featureAudioCallsGroupChatsEnabled = featureAudioCallsGroupChatsEnabled;
    return this;
  }

  /**
   * Get featureAudioCallsGroupChatsEnabled
   * @return featureAudioCallsGroupChatsEnabled
  */
  
  @Schema(name = "featureAudioCallsGroupChatsEnabled", required = false)
  public Boolean getFeatureAudioCallsGroupChatsEnabled() {
    return featureAudioCallsGroupChatsEnabled;
  }

  public void setFeatureAudioCallsGroupChatsEnabled(Boolean featureAudioCallsGroupChatsEnabled) {
    this.featureAudioCallsGroupChatsEnabled = featureAudioCallsGroupChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureAudioCallsSupervisionChatsEnabled(Boolean featureAudioCallsSupervisionChatsEnabled) {
    this.featureAudioCallsSupervisionChatsEnabled = featureAudioCallsSupervisionChatsEnabled;
    return this;
  }

  /**
   * Get featureAudioCallsSupervisionChatsEnabled
   * @return featureAudioCallsSupervisionChatsEnabled
  */
  
  @Schema(name = "featureAudioCallsSupervisionChatsEnabled", required = false)
  public Boolean getFeatureAudioCallsSupervisionChatsEnabled() {
    return featureAudioCallsSupervisionChatsEnabled;
  }

  public void setFeatureAudioCallsSupervisionChatsEnabled(Boolean featureAudioCallsSupervisionChatsEnabled) {
    this.featureAudioCallsSupervisionChatsEnabled = featureAudioCallsSupervisionChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureVideoCallsEnabled(Boolean featureVideoCallsEnabled) {
    this.featureVideoCallsEnabled = featureVideoCallsEnabled;
    return this;
  }

  /**
   * Get featureVideoCallsEnabled
   * @return featureVideoCallsEnabled
  */
  
  @Schema(name = "featureVideoCallsEnabled", required = false)
  public Boolean getFeatureVideoCallsEnabled() {
    return featureVideoCallsEnabled;
  }

  public void setFeatureVideoCallsEnabled(Boolean featureVideoCallsEnabled) {
    this.featureVideoCallsEnabled = featureVideoCallsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureVideoCallsAnonymousChatsEnabled(Boolean featureVideoCallsAnonymousChatsEnabled) {
    this.featureVideoCallsAnonymousChatsEnabled = featureVideoCallsAnonymousChatsEnabled;
    return this;
  }

  /**
   * Get featureVideoCallsAnonymousChatsEnabled
   * @return featureVideoCallsAnonymousChatsEnabled
  */
  
  @Schema(name = "featureVideoCallsAnonymousChatsEnabled", required = false)
  public Boolean getFeatureVideoCallsAnonymousChatsEnabled() {
    return featureVideoCallsAnonymousChatsEnabled;
  }

  public void setFeatureVideoCallsAnonymousChatsEnabled(Boolean featureVideoCallsAnonymousChatsEnabled) {
    this.featureVideoCallsAnonymousChatsEnabled = featureVideoCallsAnonymousChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureVideoCallsOneOnOneChatsEnabled(Boolean featureVideoCallsOneOnOneChatsEnabled) {
    this.featureVideoCallsOneOnOneChatsEnabled = featureVideoCallsOneOnOneChatsEnabled;
    return this;
  }

  /**
   * Get featureVideoCallsOneOnOneChatsEnabled
   * @return featureVideoCallsOneOnOneChatsEnabled
  */
  
  @Schema(name = "featureVideoCallsOneOnOneChatsEnabled", required = false)
  public Boolean getFeatureVideoCallsOneOnOneChatsEnabled() {
    return featureVideoCallsOneOnOneChatsEnabled;
  }

  public void setFeatureVideoCallsOneOnOneChatsEnabled(Boolean featureVideoCallsOneOnOneChatsEnabled) {
    this.featureVideoCallsOneOnOneChatsEnabled = featureVideoCallsOneOnOneChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureVideoCallsGroupChatsEnabled(Boolean featureVideoCallsGroupChatsEnabled) {
    this.featureVideoCallsGroupChatsEnabled = featureVideoCallsGroupChatsEnabled;
    return this;
  }

  /**
   * Get featureVideoCallsGroupChatsEnabled
   * @return featureVideoCallsGroupChatsEnabled
  */
  
  @Schema(name = "featureVideoCallsGroupChatsEnabled", required = false)
  public Boolean getFeatureVideoCallsGroupChatsEnabled() {
    return featureVideoCallsGroupChatsEnabled;
  }

  public void setFeatureVideoCallsGroupChatsEnabled(Boolean featureVideoCallsGroupChatsEnabled) {
    this.featureVideoCallsGroupChatsEnabled = featureVideoCallsGroupChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureVideoCallsSupervisionChatsEnabled(Boolean featureVideoCallsSupervisionChatsEnabled) {
    this.featureVideoCallsSupervisionChatsEnabled = featureVideoCallsSupervisionChatsEnabled;
    return this;
  }

  /**
   * Get featureVideoCallsSupervisionChatsEnabled
   * @return featureVideoCallsSupervisionChatsEnabled
  */
  
  @Schema(name = "featureVideoCallsSupervisionChatsEnabled", required = false)
  public Boolean getFeatureVideoCallsSupervisionChatsEnabled() {
    return featureVideoCallsSupervisionChatsEnabled;
  }

  public void setFeatureVideoCallsSupervisionChatsEnabled(Boolean featureVideoCallsSupervisionChatsEnabled) {
    this.featureVideoCallsSupervisionChatsEnabled = featureVideoCallsSupervisionChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureThreadsEnabled(Boolean featureThreadsEnabled) {
    this.featureThreadsEnabled = featureThreadsEnabled;
    return this;
  }

  /**
   * Get featureThreadsEnabled
   * @return featureThreadsEnabled
  */
  
  @Schema(name = "featureThreadsEnabled", required = false)
  public Boolean getFeatureThreadsEnabled() {
    return featureThreadsEnabled;
  }

  public void setFeatureThreadsEnabled(Boolean featureThreadsEnabled) {
    this.featureThreadsEnabled = featureThreadsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureThreadsAnonymousChatsEnabled(Boolean featureThreadsAnonymousChatsEnabled) {
    this.featureThreadsAnonymousChatsEnabled = featureThreadsAnonymousChatsEnabled;
    return this;
  }

  /**
   * Get featureThreadsAnonymousChatsEnabled
   * @return featureThreadsAnonymousChatsEnabled
  */
  
  @Schema(name = "featureThreadsAnonymousChatsEnabled", required = false)
  public Boolean getFeatureThreadsAnonymousChatsEnabled() {
    return featureThreadsAnonymousChatsEnabled;
  }

  public void setFeatureThreadsAnonymousChatsEnabled(Boolean featureThreadsAnonymousChatsEnabled) {
    this.featureThreadsAnonymousChatsEnabled = featureThreadsAnonymousChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureThreadsGroupChatsEnabled(Boolean featureThreadsGroupChatsEnabled) {
    this.featureThreadsGroupChatsEnabled = featureThreadsGroupChatsEnabled;
    return this;
  }

  /**
   * Get featureThreadsGroupChatsEnabled
   * @return featureThreadsGroupChatsEnabled
  */
  
  @Schema(name = "featureThreadsGroupChatsEnabled", required = false)
  public Boolean getFeatureThreadsGroupChatsEnabled() {
    return featureThreadsGroupChatsEnabled;
  }

  public void setFeatureThreadsGroupChatsEnabled(Boolean featureThreadsGroupChatsEnabled) {
    this.featureThreadsGroupChatsEnabled = featureThreadsGroupChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureThreadsOneOnOneEnabled(Boolean featureThreadsOneOnOneEnabled) {
    this.featureThreadsOneOnOneEnabled = featureThreadsOneOnOneEnabled;
    return this;
  }

  /**
   * Get featureThreadsOneOnOneEnabled
   * @return featureThreadsOneOnOneEnabled
  */
  
  @Schema(name = "featureThreadsOneOnOneEnabled", required = false)
  public Boolean getFeatureThreadsOneOnOneEnabled() {
    return featureThreadsOneOnOneEnabled;
  }

  public void setFeatureThreadsOneOnOneEnabled(Boolean featureThreadsOneOnOneEnabled) {
    this.featureThreadsOneOnOneEnabled = featureThreadsOneOnOneEnabled;
  }

  public ApplicationPermissionSettingsDTO featureThreadsSupervisionChatsEnabled(Boolean featureThreadsSupervisionChatsEnabled) {
    this.featureThreadsSupervisionChatsEnabled = featureThreadsSupervisionChatsEnabled;
    return this;
  }

  /**
   * Get featureThreadsSupervisionChatsEnabled
   * @return featureThreadsSupervisionChatsEnabled
  */
  
  @Schema(name = "featureThreadsSupervisionChatsEnabled", required = false)
  public Boolean getFeatureThreadsSupervisionChatsEnabled() {
    return featureThreadsSupervisionChatsEnabled;
  }

  public void setFeatureThreadsSupervisionChatsEnabled(Boolean featureThreadsSupervisionChatsEnabled) {
    this.featureThreadsSupervisionChatsEnabled = featureThreadsSupervisionChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureVoiceMessagesEnabled(Boolean featureVoiceMessagesEnabled) {
    this.featureVoiceMessagesEnabled = featureVoiceMessagesEnabled;
    return this;
  }

  /**
   * Get featureVoiceMessagesEnabled
   * @return featureVoiceMessagesEnabled
  */
  
  @Schema(name = "featureVoiceMessagesEnabled", required = false)
  public Boolean getFeatureVoiceMessagesEnabled() {
    return featureVoiceMessagesEnabled;
  }

  public void setFeatureVoiceMessagesEnabled(Boolean featureVoiceMessagesEnabled) {
    this.featureVoiceMessagesEnabled = featureVoiceMessagesEnabled;
  }

  public ApplicationPermissionSettingsDTO featureVoiceMessagesAnonymousChatsEnabled(Boolean featureVoiceMessagesAnonymousChatsEnabled) {
    this.featureVoiceMessagesAnonymousChatsEnabled = featureVoiceMessagesAnonymousChatsEnabled;
    return this;
  }

  /**
   * Get featureVoiceMessagesAnonymousChatsEnabled
   * @return featureVoiceMessagesAnonymousChatsEnabled
  */
  
  @Schema(name = "featureVoiceMessagesAnonymousChatsEnabled", required = false)
  public Boolean getFeatureVoiceMessagesAnonymousChatsEnabled() {
    return featureVoiceMessagesAnonymousChatsEnabled;
  }

  public void setFeatureVoiceMessagesAnonymousChatsEnabled(Boolean featureVoiceMessagesAnonymousChatsEnabled) {
    this.featureVoiceMessagesAnonymousChatsEnabled = featureVoiceMessagesAnonymousChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureVoiceMessagesOneOnOneChatsEnabled(Boolean featureVoiceMessagesOneOnOneChatsEnabled) {
    this.featureVoiceMessagesOneOnOneChatsEnabled = featureVoiceMessagesOneOnOneChatsEnabled;
    return this;
  }

  /**
   * Get featureVoiceMessagesOneOnOneChatsEnabled
   * @return featureVoiceMessagesOneOnOneChatsEnabled
  */
  
  @Schema(name = "featureVoiceMessagesOneOnOneChatsEnabled", required = false)
  public Boolean getFeatureVoiceMessagesOneOnOneChatsEnabled() {
    return featureVoiceMessagesOneOnOneChatsEnabled;
  }

  public void setFeatureVoiceMessagesOneOnOneChatsEnabled(Boolean featureVoiceMessagesOneOnOneChatsEnabled) {
    this.featureVoiceMessagesOneOnOneChatsEnabled = featureVoiceMessagesOneOnOneChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureVoiceMessagesGroupChatsEnabled(Boolean featureVoiceMessagesGroupChatsEnabled) {
    this.featureVoiceMessagesGroupChatsEnabled = featureVoiceMessagesGroupChatsEnabled;
    return this;
  }

  /**
   * Get featureVoiceMessagesGroupChatsEnabled
   * @return featureVoiceMessagesGroupChatsEnabled
  */
  
  @Schema(name = "featureVoiceMessagesGroupChatsEnabled", required = false)
  public Boolean getFeatureVoiceMessagesGroupChatsEnabled() {
    return featureVoiceMessagesGroupChatsEnabled;
  }

  public void setFeatureVoiceMessagesGroupChatsEnabled(Boolean featureVoiceMessagesGroupChatsEnabled) {
    this.featureVoiceMessagesGroupChatsEnabled = featureVoiceMessagesGroupChatsEnabled;
  }

  public ApplicationPermissionSettingsDTO featureVoiceMessagesSupervisionChatsEnabled(Boolean featureVoiceMessagesSupervisionChatsEnabled) {
    this.featureVoiceMessagesSupervisionChatsEnabled = featureVoiceMessagesSupervisionChatsEnabled;
    return this;
  }

  /**
   * Get featureVoiceMessagesSupervisionChatsEnabled
   * @return featureVoiceMessagesSupervisionChatsEnabled
  */
  
  @Schema(name = "featureVoiceMessagesSupervisionChatsEnabled", required = false)
  public Boolean getFeatureVoiceMessagesSupervisionChatsEnabled() {
    return featureVoiceMessagesSupervisionChatsEnabled;
  }

  public void setFeatureVoiceMessagesSupervisionChatsEnabled(Boolean featureVoiceMessagesSupervisionChatsEnabled) {
    this.featureVoiceMessagesSupervisionChatsEnabled = featureVoiceMessagesSupervisionChatsEnabled;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApplicationPermissionSettingsDTO applicationPermissionSettingsDTO = (ApplicationPermissionSettingsDTO) o;
    return Objects.equals(this.featureAnonymousChatEnabled, applicationPermissionSettingsDTO.featureAnonymousChatEnabled) &&
        Objects.equals(this.featureGroupChatV2Enabled, applicationPermissionSettingsDTO.featureGroupChatV2Enabled) &&
        Objects.equals(this.featureCallsEnabled, applicationPermissionSettingsDTO.featureCallsEnabled) &&
        Objects.equals(this.featureSupervisionEnabled, applicationPermissionSettingsDTO.featureSupervisionEnabled) &&
        Objects.equals(this.featureSupervisionAnonymousChatsEnabled, applicationPermissionSettingsDTO.featureSupervisionAnonymousChatsEnabled) &&
        Objects.equals(this.featureSupervisionOneOnOneChatsEnabled, applicationPermissionSettingsDTO.featureSupervisionOneOnOneChatsEnabled) &&
        Objects.equals(this.featureAudioCallsEnabled, applicationPermissionSettingsDTO.featureAudioCallsEnabled) &&
        Objects.equals(this.featureAudioCallsAnonymousChatsEnabled, applicationPermissionSettingsDTO.featureAudioCallsAnonymousChatsEnabled) &&
        Objects.equals(this.featureAudioCallsOneOnOneChatsEnabled, applicationPermissionSettingsDTO.featureAudioCallsOneOnOneChatsEnabled) &&
        Objects.equals(this.featureAudioCallsGroupChatsEnabled, applicationPermissionSettingsDTO.featureAudioCallsGroupChatsEnabled) &&
        Objects.equals(this.featureAudioCallsSupervisionChatsEnabled, applicationPermissionSettingsDTO.featureAudioCallsSupervisionChatsEnabled) &&
        Objects.equals(this.featureVideoCallsEnabled, applicationPermissionSettingsDTO.featureVideoCallsEnabled) &&
        Objects.equals(this.featureVideoCallsAnonymousChatsEnabled, applicationPermissionSettingsDTO.featureVideoCallsAnonymousChatsEnabled) &&
        Objects.equals(this.featureVideoCallsOneOnOneChatsEnabled, applicationPermissionSettingsDTO.featureVideoCallsOneOnOneChatsEnabled) &&
        Objects.equals(this.featureVideoCallsGroupChatsEnabled, applicationPermissionSettingsDTO.featureVideoCallsGroupChatsEnabled) &&
        Objects.equals(this.featureVideoCallsSupervisionChatsEnabled, applicationPermissionSettingsDTO.featureVideoCallsSupervisionChatsEnabled) &&
        Objects.equals(this.featureThreadsEnabled, applicationPermissionSettingsDTO.featureThreadsEnabled) &&
        Objects.equals(this.featureThreadsAnonymousChatsEnabled, applicationPermissionSettingsDTO.featureThreadsAnonymousChatsEnabled) &&
        Objects.equals(this.featureThreadsGroupChatsEnabled, applicationPermissionSettingsDTO.featureThreadsGroupChatsEnabled) &&
        Objects.equals(this.featureThreadsOneOnOneEnabled, applicationPermissionSettingsDTO.featureThreadsOneOnOneEnabled) &&
        Objects.equals(this.featureThreadsSupervisionChatsEnabled, applicationPermissionSettingsDTO.featureThreadsSupervisionChatsEnabled) &&
        Objects.equals(this.featureVoiceMessagesEnabled, applicationPermissionSettingsDTO.featureVoiceMessagesEnabled) &&
        Objects.equals(this.featureVoiceMessagesAnonymousChatsEnabled, applicationPermissionSettingsDTO.featureVoiceMessagesAnonymousChatsEnabled) &&
        Objects.equals(this.featureVoiceMessagesOneOnOneChatsEnabled, applicationPermissionSettingsDTO.featureVoiceMessagesOneOnOneChatsEnabled) &&
        Objects.equals(this.featureVoiceMessagesGroupChatsEnabled, applicationPermissionSettingsDTO.featureVoiceMessagesGroupChatsEnabled) &&
        Objects.equals(this.featureVoiceMessagesSupervisionChatsEnabled, applicationPermissionSettingsDTO.featureVoiceMessagesSupervisionChatsEnabled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(featureAnonymousChatEnabled, featureGroupChatV2Enabled, featureCallsEnabled, featureSupervisionEnabled, featureSupervisionAnonymousChatsEnabled, featureSupervisionOneOnOneChatsEnabled, featureAudioCallsEnabled, featureAudioCallsAnonymousChatsEnabled, featureAudioCallsOneOnOneChatsEnabled, featureAudioCallsGroupChatsEnabled, featureAudioCallsSupervisionChatsEnabled, featureVideoCallsEnabled, featureVideoCallsAnonymousChatsEnabled, featureVideoCallsOneOnOneChatsEnabled, featureVideoCallsGroupChatsEnabled, featureVideoCallsSupervisionChatsEnabled, featureThreadsEnabled, featureThreadsAnonymousChatsEnabled, featureThreadsGroupChatsEnabled, featureThreadsOneOnOneEnabled, featureThreadsSupervisionChatsEnabled, featureVoiceMessagesEnabled, featureVoiceMessagesAnonymousChatsEnabled, featureVoiceMessagesOneOnOneChatsEnabled, featureVoiceMessagesGroupChatsEnabled, featureVoiceMessagesSupervisionChatsEnabled);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApplicationPermissionSettingsDTO {\n");
    sb.append("    featureAnonymousChatEnabled: ").append(toIndentedString(featureAnonymousChatEnabled)).append("\n");
    sb.append("    featureGroupChatV2Enabled: ").append(toIndentedString(featureGroupChatV2Enabled)).append("\n");
    sb.append("    featureCallsEnabled: ").append(toIndentedString(featureCallsEnabled)).append("\n");
    sb.append("    featureSupervisionEnabled: ").append(toIndentedString(featureSupervisionEnabled)).append("\n");
    sb.append("    featureSupervisionAnonymousChatsEnabled: ").append(toIndentedString(featureSupervisionAnonymousChatsEnabled)).append("\n");
    sb.append("    featureSupervisionOneOnOneChatsEnabled: ").append(toIndentedString(featureSupervisionOneOnOneChatsEnabled)).append("\n");
    sb.append("    featureAudioCallsEnabled: ").append(toIndentedString(featureAudioCallsEnabled)).append("\n");
    sb.append("    featureAudioCallsAnonymousChatsEnabled: ").append(toIndentedString(featureAudioCallsAnonymousChatsEnabled)).append("\n");
    sb.append("    featureAudioCallsOneOnOneChatsEnabled: ").append(toIndentedString(featureAudioCallsOneOnOneChatsEnabled)).append("\n");
    sb.append("    featureAudioCallsGroupChatsEnabled: ").append(toIndentedString(featureAudioCallsGroupChatsEnabled)).append("\n");
    sb.append("    featureAudioCallsSupervisionChatsEnabled: ").append(toIndentedString(featureAudioCallsSupervisionChatsEnabled)).append("\n");
    sb.append("    featureVideoCallsEnabled: ").append(toIndentedString(featureVideoCallsEnabled)).append("\n");
    sb.append("    featureVideoCallsAnonymousChatsEnabled: ").append(toIndentedString(featureVideoCallsAnonymousChatsEnabled)).append("\n");
    sb.append("    featureVideoCallsOneOnOneChatsEnabled: ").append(toIndentedString(featureVideoCallsOneOnOneChatsEnabled)).append("\n");
    sb.append("    featureVideoCallsGroupChatsEnabled: ").append(toIndentedString(featureVideoCallsGroupChatsEnabled)).append("\n");
    sb.append("    featureVideoCallsSupervisionChatsEnabled: ").append(toIndentedString(featureVideoCallsSupervisionChatsEnabled)).append("\n");
    sb.append("    featureThreadsEnabled: ").append(toIndentedString(featureThreadsEnabled)).append("\n");
    sb.append("    featureThreadsAnonymousChatsEnabled: ").append(toIndentedString(featureThreadsAnonymousChatsEnabled)).append("\n");
    sb.append("    featureThreadsGroupChatsEnabled: ").append(toIndentedString(featureThreadsGroupChatsEnabled)).append("\n");
    sb.append("    featureThreadsOneOnOneEnabled: ").append(toIndentedString(featureThreadsOneOnOneEnabled)).append("\n");
    sb.append("    featureThreadsSupervisionChatsEnabled: ").append(toIndentedString(featureThreadsSupervisionChatsEnabled)).append("\n");
    sb.append("    featureVoiceMessagesEnabled: ").append(toIndentedString(featureVoiceMessagesEnabled)).append("\n");
    sb.append("    featureVoiceMessagesAnonymousChatsEnabled: ").append(toIndentedString(featureVoiceMessagesAnonymousChatsEnabled)).append("\n");
    sb.append("    featureVoiceMessagesOneOnOneChatsEnabled: ").append(toIndentedString(featureVoiceMessagesOneOnOneChatsEnabled)).append("\n");
    sb.append("    featureVoiceMessagesGroupChatsEnabled: ").append(toIndentedString(featureVoiceMessagesGroupChatsEnabled)).append("\n");
    sb.append("    featureVoiceMessagesSupervisionChatsEnabled: ").append(toIndentedString(featureVoiceMessagesSupervisionChatsEnabled)).append("\n");
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

