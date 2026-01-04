package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.caritas.cob.consultingtypeservice.api.model.NotificationsDTO;
import de.caritas.cob.consultingtypeservice.api.model.RolesDTO;
import de.caritas.cob.consultingtypeservice.api.model.SessionDataInitializingDTO;
import de.caritas.cob.consultingtypeservice.api.model.WelcomeMessageDTO;
import de.caritas.cob.consultingtypeservice.api.model.WhiteSpotDTO;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ExtendedConsultingTypeResponseDTOAllOf
 */

@JsonTypeName("ExtendedConsultingTypeResponseDTO_allOf")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class ExtendedConsultingTypeResponseDTOAllOf {

  @JsonProperty("slug")
  private String slug;

  @JsonProperty("excludeNonMainConsultantsFromTeamSessions")
  private Boolean excludeNonMainConsultantsFromTeamSessions;

  @JsonProperty("whiteSpot")
  private WhiteSpotDTO whiteSpot;

  @JsonProperty("consultantBoundedToConsultingType")
  private Boolean consultantBoundedToConsultingType;

  @JsonProperty("welcomeMessage")
  private WelcomeMessageDTO welcomeMessage;

  @JsonProperty("sendFurtherStepsMessage")
  private Boolean sendFurtherStepsMessage;

  @JsonProperty("sessionDataInitializing")
  private SessionDataInitializingDTO sessionDataInitializing;

  @JsonProperty("roles")
  private RolesDTO roles;

  @JsonProperty("notifications")
  private NotificationsDTO notifications;

  public ExtendedConsultingTypeResponseDTOAllOf slug(String slug) {
    this.slug = slug;
    return this;
  }

  /**
   * Get slug
   * @return slug
  */
  
  @Schema(name = "slug", example = "part-of-url", required = false)
  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public ExtendedConsultingTypeResponseDTOAllOf excludeNonMainConsultantsFromTeamSessions(Boolean excludeNonMainConsultantsFromTeamSessions) {
    this.excludeNonMainConsultantsFromTeamSessions = excludeNonMainConsultantsFromTeamSessions;
    return this;
  }

  /**
   * Get excludeNonMainConsultantsFromTeamSessions
   * @return excludeNonMainConsultantsFromTeamSessions
  */
  
  @Schema(name = "excludeNonMainConsultantsFromTeamSessions", example = "true", required = false)
  public Boolean getExcludeNonMainConsultantsFromTeamSessions() {
    return excludeNonMainConsultantsFromTeamSessions;
  }

  public void setExcludeNonMainConsultantsFromTeamSessions(Boolean excludeNonMainConsultantsFromTeamSessions) {
    this.excludeNonMainConsultantsFromTeamSessions = excludeNonMainConsultantsFromTeamSessions;
  }

  public ExtendedConsultingTypeResponseDTOAllOf whiteSpot(WhiteSpotDTO whiteSpot) {
    this.whiteSpot = whiteSpot;
    return this;
  }

  /**
   * Get whiteSpot
   * @return whiteSpot
  */
  @Valid 
  @Schema(name = "whiteSpot", required = false)
  public WhiteSpotDTO getWhiteSpot() {
    return whiteSpot;
  }

  public void setWhiteSpot(WhiteSpotDTO whiteSpot) {
    this.whiteSpot = whiteSpot;
  }

  public ExtendedConsultingTypeResponseDTOAllOf consultantBoundedToConsultingType(Boolean consultantBoundedToConsultingType) {
    this.consultantBoundedToConsultingType = consultantBoundedToConsultingType;
    return this;
  }

  /**
   * Get consultantBoundedToConsultingType
   * @return consultantBoundedToConsultingType
  */
  
  @Schema(name = "consultantBoundedToConsultingType", example = "false", required = false)
  public Boolean getConsultantBoundedToConsultingType() {
    return consultantBoundedToConsultingType;
  }

  public void setConsultantBoundedToConsultingType(Boolean consultantBoundedToConsultingType) {
    this.consultantBoundedToConsultingType = consultantBoundedToConsultingType;
  }

  public ExtendedConsultingTypeResponseDTOAllOf welcomeMessage(WelcomeMessageDTO welcomeMessage) {
    this.welcomeMessage = welcomeMessage;
    return this;
  }

  /**
   * Get welcomeMessage
   * @return welcomeMessage
  */
  @Valid 
  @Schema(name = "welcomeMessage", required = false)
  public WelcomeMessageDTO getWelcomeMessage() {
    return welcomeMessage;
  }

  public void setWelcomeMessage(WelcomeMessageDTO welcomeMessage) {
    this.welcomeMessage = welcomeMessage;
  }

  public ExtendedConsultingTypeResponseDTOAllOf sendFurtherStepsMessage(Boolean sendFurtherStepsMessage) {
    this.sendFurtherStepsMessage = sendFurtherStepsMessage;
    return this;
  }

  /**
   * Get sendFurtherStepsMessage
   * @return sendFurtherStepsMessage
  */
  
  @Schema(name = "sendFurtherStepsMessage", example = "true", required = false)
  public Boolean getSendFurtherStepsMessage() {
    return sendFurtherStepsMessage;
  }

  public void setSendFurtherStepsMessage(Boolean sendFurtherStepsMessage) {
    this.sendFurtherStepsMessage = sendFurtherStepsMessage;
  }

  public ExtendedConsultingTypeResponseDTOAllOf sessionDataInitializing(SessionDataInitializingDTO sessionDataInitializing) {
    this.sessionDataInitializing = sessionDataInitializing;
    return this;
  }

  /**
   * Get sessionDataInitializing
   * @return sessionDataInitializing
  */
  @Valid 
  @Schema(name = "sessionDataInitializing", required = false)
  public SessionDataInitializingDTO getSessionDataInitializing() {
    return sessionDataInitializing;
  }

  public void setSessionDataInitializing(SessionDataInitializingDTO sessionDataInitializing) {
    this.sessionDataInitializing = sessionDataInitializing;
  }

  public ExtendedConsultingTypeResponseDTOAllOf roles(RolesDTO roles) {
    this.roles = roles;
    return this;
  }

  /**
   * Get roles
   * @return roles
  */
  @Valid 
  @Schema(name = "roles", required = false)
  public RolesDTO getRoles() {
    return roles;
  }

  public void setRoles(RolesDTO roles) {
    this.roles = roles;
  }

  public ExtendedConsultingTypeResponseDTOAllOf notifications(NotificationsDTO notifications) {
    this.notifications = notifications;
    return this;
  }

  /**
   * Get notifications
   * @return notifications
  */
  @Valid 
  @Schema(name = "notifications", required = false)
  public NotificationsDTO getNotifications() {
    return notifications;
  }

  public void setNotifications(NotificationsDTO notifications) {
    this.notifications = notifications;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExtendedConsultingTypeResponseDTOAllOf extendedConsultingTypeResponseDTOAllOf = (ExtendedConsultingTypeResponseDTOAllOf) o;
    return Objects.equals(this.slug, extendedConsultingTypeResponseDTOAllOf.slug) &&
        Objects.equals(this.excludeNonMainConsultantsFromTeamSessions, extendedConsultingTypeResponseDTOAllOf.excludeNonMainConsultantsFromTeamSessions) &&
        Objects.equals(this.whiteSpot, extendedConsultingTypeResponseDTOAllOf.whiteSpot) &&
        Objects.equals(this.consultantBoundedToConsultingType, extendedConsultingTypeResponseDTOAllOf.consultantBoundedToConsultingType) &&
        Objects.equals(this.welcomeMessage, extendedConsultingTypeResponseDTOAllOf.welcomeMessage) &&
        Objects.equals(this.sendFurtherStepsMessage, extendedConsultingTypeResponseDTOAllOf.sendFurtherStepsMessage) &&
        Objects.equals(this.sessionDataInitializing, extendedConsultingTypeResponseDTOAllOf.sessionDataInitializing) &&
        Objects.equals(this.roles, extendedConsultingTypeResponseDTOAllOf.roles) &&
        Objects.equals(this.notifications, extendedConsultingTypeResponseDTOAllOf.notifications);
  }

  @Override
  public int hashCode() {
    return Objects.hash(slug, excludeNonMainConsultantsFromTeamSessions, whiteSpot, consultantBoundedToConsultingType, welcomeMessage, sendFurtherStepsMessage, sessionDataInitializing, roles, notifications);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ExtendedConsultingTypeResponseDTOAllOf {\n");
    sb.append("    slug: ").append(toIndentedString(slug)).append("\n");
    sb.append("    excludeNonMainConsultantsFromTeamSessions: ").append(toIndentedString(excludeNonMainConsultantsFromTeamSessions)).append("\n");
    sb.append("    whiteSpot: ").append(toIndentedString(whiteSpot)).append("\n");
    sb.append("    consultantBoundedToConsultingType: ").append(toIndentedString(consultantBoundedToConsultingType)).append("\n");
    sb.append("    welcomeMessage: ").append(toIndentedString(welcomeMessage)).append("\n");
    sb.append("    sendFurtherStepsMessage: ").append(toIndentedString(sendFurtherStepsMessage)).append("\n");
    sb.append("    sessionDataInitializing: ").append(toIndentedString(sessionDataInitializing)).append("\n");
    sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
    sb.append("    notifications: ").append(toIndentedString(notifications)).append("\n");
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

