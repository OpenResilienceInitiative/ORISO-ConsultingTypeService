package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.caritas.cob.consultingtypeservice.api.model.BasicConsultingTypeResponseDTOFurtherInformation;
import de.caritas.cob.consultingtypeservice.api.model.BasicConsultingTypeResponseDTOGroupChat;
import de.caritas.cob.consultingtypeservice.api.model.BasicConsultingTypeResponseDTORegistration;
import de.caritas.cob.consultingtypeservice.api.model.BasicConsultingTypeResponseDTOUrls;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTONotifications;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTOSessionDataInitializing;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTOWelcomeMessage;
import de.caritas.cob.consultingtypeservice.api.model.ConsultingTypeDTOWhiteSpot;
import de.caritas.cob.consultingtypeservice.api.model.RequiredComponentsDTO;
import de.caritas.cob.consultingtypeservice.api.model.RolesDTO;
import de.caritas.cob.consultingtypeservice.api.model.WelcomeScreenDTO;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ConsultingTypeDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.381612101Z[Etc/UTC]")
public class ConsultingTypeDTO {

  @JsonProperty("tenantId")
  private Integer tenantId;

  @JsonProperty("description")
  private String description;

  @JsonProperty("groups")
  @Valid
  private List<String> groups = null;

  @JsonProperty("slug")
  private String slug;

  @JsonProperty("excludeNonMainConsultantsFromTeamSessions")
  private Boolean excludeNonMainConsultantsFromTeamSessions;

  @JsonProperty("isSubsequentRegistrationAllowed")
  private Boolean isSubsequentRegistrationAllowed;

  @JsonProperty("isAnonymousConversationAllowed")
  private Boolean isAnonymousConversationAllowed;

  @JsonProperty("showAskerProfile")
  private Boolean showAskerProfile;

  @JsonProperty("isVideoCallAllowed")
  private Boolean isVideoCallAllowed;

  @JsonProperty("languageFormal")
  private Boolean languageFormal;

  @JsonProperty("consultantBoundedToConsultingType")
  private Boolean consultantBoundedToConsultingType;

  @JsonProperty("sendFurtherStepsMessage")
  private Boolean sendFurtherStepsMessage;

  @JsonProperty("furtherInformation")
  private BasicConsultingTypeResponseDTOFurtherInformation furtherInformation;

  @JsonProperty("urls")
  private BasicConsultingTypeResponseDTOUrls urls;

  @JsonProperty("registration")
  private BasicConsultingTypeResponseDTORegistration registration;

  @JsonProperty("groupChat")
  private BasicConsultingTypeResponseDTOGroupChat groupChat;

  @JsonProperty("whiteSpot")
  private ConsultingTypeDTOWhiteSpot whiteSpot;

  @JsonProperty("welcomeMessage")
  private ConsultingTypeDTOWelcomeMessage welcomeMessage;

  @JsonProperty("sessionDataInitializing")
  private ConsultingTypeDTOSessionDataInitializing sessionDataInitializing;

  @JsonProperty("roles")
  private RolesDTO roles;

  @JsonProperty("notifications")
  private ConsultingTypeDTONotifications notifications;

  @JsonProperty("requiredComponents")
  private RequiredComponentsDTO requiredComponents;

  @JsonProperty("welcomeScreen")
  private WelcomeScreenDTO welcomeScreen;

  public ConsultingTypeDTO tenantId(Integer tenantId) {
    this.tenantId = tenantId;
    return this;
  }

  /**
   * Get tenantId
   * @return tenantId
  */
  
  @Schema(name = "tenantId", example = "100", required = false)
  public Integer getTenantId() {
    return tenantId;
  }

  public void setTenantId(Integer tenantId) {
    this.tenantId = tenantId;
  }

  public ConsultingTypeDTO description(String description) {
    this.description = description;
    return this;
  }

  /**
   * Get description
   * @return description
  */
  
  @Schema(name = "description", example = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr.", required = false)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ConsultingTypeDTO groups(List<String> groups) {
    this.groups = groups;
    return this;
  }

  public ConsultingTypeDTO addGroupsItem(String groupsItem) {
    if (this.groups == null) {
      this.groups = new ArrayList<>();
    }
    this.groups.add(groupsItem);
    return this;
  }

  /**
   * Get groups
   * @return groups
  */
  
  @Schema(name = "groups", required = false)
  public List<String> getGroups() {
    return groups;
  }

  public void setGroups(List<String> groups) {
    this.groups = groups;
  }

  public ConsultingTypeDTO slug(String slug) {
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

  public ConsultingTypeDTO excludeNonMainConsultantsFromTeamSessions(Boolean excludeNonMainConsultantsFromTeamSessions) {
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

  public ConsultingTypeDTO isSubsequentRegistrationAllowed(Boolean isSubsequentRegistrationAllowed) {
    this.isSubsequentRegistrationAllowed = isSubsequentRegistrationAllowed;
    return this;
  }

  /**
   * Get isSubsequentRegistrationAllowed
   * @return isSubsequentRegistrationAllowed
  */
  
  @Schema(name = "isSubsequentRegistrationAllowed", example = "true", required = false)
  public Boolean getIsSubsequentRegistrationAllowed() {
    return isSubsequentRegistrationAllowed;
  }

  public void setIsSubsequentRegistrationAllowed(Boolean isSubsequentRegistrationAllowed) {
    this.isSubsequentRegistrationAllowed = isSubsequentRegistrationAllowed;
  }

  public ConsultingTypeDTO isAnonymousConversationAllowed(Boolean isAnonymousConversationAllowed) {
    this.isAnonymousConversationAllowed = isAnonymousConversationAllowed;
    return this;
  }

  /**
   * Get isAnonymousConversationAllowed
   * @return isAnonymousConversationAllowed
  */
  
  @Schema(name = "isAnonymousConversationAllowed", example = "false", required = false)
  public Boolean getIsAnonymousConversationAllowed() {
    return isAnonymousConversationAllowed;
  }

  public void setIsAnonymousConversationAllowed(Boolean isAnonymousConversationAllowed) {
    this.isAnonymousConversationAllowed = isAnonymousConversationAllowed;
  }

  public ConsultingTypeDTO showAskerProfile(Boolean showAskerProfile) {
    this.showAskerProfile = showAskerProfile;
    return this;
  }

  /**
   * Get showAskerProfile
   * @return showAskerProfile
  */
  
  @Schema(name = "showAskerProfile", example = "true", required = false)
  public Boolean getShowAskerProfile() {
    return showAskerProfile;
  }

  public void setShowAskerProfile(Boolean showAskerProfile) {
    this.showAskerProfile = showAskerProfile;
  }

  public ConsultingTypeDTO isVideoCallAllowed(Boolean isVideoCallAllowed) {
    this.isVideoCallAllowed = isVideoCallAllowed;
    return this;
  }

  /**
   * Get isVideoCallAllowed
   * @return isVideoCallAllowed
  */
  
  @Schema(name = "isVideoCallAllowed", example = "true", required = false)
  public Boolean getIsVideoCallAllowed() {
    return isVideoCallAllowed;
  }

  public void setIsVideoCallAllowed(Boolean isVideoCallAllowed) {
    this.isVideoCallAllowed = isVideoCallAllowed;
  }

  public ConsultingTypeDTO languageFormal(Boolean languageFormal) {
    this.languageFormal = languageFormal;
    return this;
  }

  /**
   * Get languageFormal
   * @return languageFormal
  */
  
  @Schema(name = "languageFormal", example = "true", required = false)
  public Boolean getLanguageFormal() {
    return languageFormal;
  }

  public void setLanguageFormal(Boolean languageFormal) {
    this.languageFormal = languageFormal;
  }

  public ConsultingTypeDTO consultantBoundedToConsultingType(Boolean consultantBoundedToConsultingType) {
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

  public ConsultingTypeDTO sendFurtherStepsMessage(Boolean sendFurtherStepsMessage) {
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

  public ConsultingTypeDTO furtherInformation(BasicConsultingTypeResponseDTOFurtherInformation furtherInformation) {
    this.furtherInformation = furtherInformation;
    return this;
  }

  /**
   * Get furtherInformation
   * @return furtherInformation
  */
  @Valid 
  @Schema(name = "furtherInformation", required = false)
  public BasicConsultingTypeResponseDTOFurtherInformation getFurtherInformation() {
    return furtherInformation;
  }

  public void setFurtherInformation(BasicConsultingTypeResponseDTOFurtherInformation furtherInformation) {
    this.furtherInformation = furtherInformation;
  }

  public ConsultingTypeDTO urls(BasicConsultingTypeResponseDTOUrls urls) {
    this.urls = urls;
    return this;
  }

  /**
   * Get urls
   * @return urls
  */
  @Valid 
  @Schema(name = "urls", required = false)
  public BasicConsultingTypeResponseDTOUrls getUrls() {
    return urls;
  }

  public void setUrls(BasicConsultingTypeResponseDTOUrls urls) {
    this.urls = urls;
  }

  public ConsultingTypeDTO registration(BasicConsultingTypeResponseDTORegistration registration) {
    this.registration = registration;
    return this;
  }

  /**
   * Get registration
   * @return registration
  */
  @Valid 
  @Schema(name = "registration", required = false)
  public BasicConsultingTypeResponseDTORegistration getRegistration() {
    return registration;
  }

  public void setRegistration(BasicConsultingTypeResponseDTORegistration registration) {
    this.registration = registration;
  }

  public ConsultingTypeDTO groupChat(BasicConsultingTypeResponseDTOGroupChat groupChat) {
    this.groupChat = groupChat;
    return this;
  }

  /**
   * Get groupChat
   * @return groupChat
  */
  @Valid 
  @Schema(name = "groupChat", required = false)
  public BasicConsultingTypeResponseDTOGroupChat getGroupChat() {
    return groupChat;
  }

  public void setGroupChat(BasicConsultingTypeResponseDTOGroupChat groupChat) {
    this.groupChat = groupChat;
  }

  public ConsultingTypeDTO whiteSpot(ConsultingTypeDTOWhiteSpot whiteSpot) {
    this.whiteSpot = whiteSpot;
    return this;
  }

  /**
   * Get whiteSpot
   * @return whiteSpot
  */
  @Valid 
  @Schema(name = "whiteSpot", required = false)
  public ConsultingTypeDTOWhiteSpot getWhiteSpot() {
    return whiteSpot;
  }

  public void setWhiteSpot(ConsultingTypeDTOWhiteSpot whiteSpot) {
    this.whiteSpot = whiteSpot;
  }

  public ConsultingTypeDTO welcomeMessage(ConsultingTypeDTOWelcomeMessage welcomeMessage) {
    this.welcomeMessage = welcomeMessage;
    return this;
  }

  /**
   * Get welcomeMessage
   * @return welcomeMessage
  */
  @Valid 
  @Schema(name = "welcomeMessage", required = false)
  public ConsultingTypeDTOWelcomeMessage getWelcomeMessage() {
    return welcomeMessage;
  }

  public void setWelcomeMessage(ConsultingTypeDTOWelcomeMessage welcomeMessage) {
    this.welcomeMessage = welcomeMessage;
  }

  public ConsultingTypeDTO sessionDataInitializing(ConsultingTypeDTOSessionDataInitializing sessionDataInitializing) {
    this.sessionDataInitializing = sessionDataInitializing;
    return this;
  }

  /**
   * Get sessionDataInitializing
   * @return sessionDataInitializing
  */
  @Valid 
  @Schema(name = "sessionDataInitializing", required = false)
  public ConsultingTypeDTOSessionDataInitializing getSessionDataInitializing() {
    return sessionDataInitializing;
  }

  public void setSessionDataInitializing(ConsultingTypeDTOSessionDataInitializing sessionDataInitializing) {
    this.sessionDataInitializing = sessionDataInitializing;
  }

  public ConsultingTypeDTO roles(RolesDTO roles) {
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

  public ConsultingTypeDTO notifications(ConsultingTypeDTONotifications notifications) {
    this.notifications = notifications;
    return this;
  }

  /**
   * Get notifications
   * @return notifications
  */
  @Valid 
  @Schema(name = "notifications", required = false)
  public ConsultingTypeDTONotifications getNotifications() {
    return notifications;
  }

  public void setNotifications(ConsultingTypeDTONotifications notifications) {
    this.notifications = notifications;
  }

  public ConsultingTypeDTO requiredComponents(RequiredComponentsDTO requiredComponents) {
    this.requiredComponents = requiredComponents;
    return this;
  }

  /**
   * Get requiredComponents
   * @return requiredComponents
  */
  @Valid 
  @Schema(name = "requiredComponents", required = false)
  public RequiredComponentsDTO getRequiredComponents() {
    return requiredComponents;
  }

  public void setRequiredComponents(RequiredComponentsDTO requiredComponents) {
    this.requiredComponents = requiredComponents;
  }

  public ConsultingTypeDTO welcomeScreen(WelcomeScreenDTO welcomeScreen) {
    this.welcomeScreen = welcomeScreen;
    return this;
  }

  /**
   * Get welcomeScreen
   * @return welcomeScreen
  */
  @Valid 
  @Schema(name = "welcomeScreen", required = false)
  public WelcomeScreenDTO getWelcomeScreen() {
    return welcomeScreen;
  }

  public void setWelcomeScreen(WelcomeScreenDTO welcomeScreen) {
    this.welcomeScreen = welcomeScreen;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConsultingTypeDTO consultingTypeDTO = (ConsultingTypeDTO) o;
    return Objects.equals(this.tenantId, consultingTypeDTO.tenantId) &&
        Objects.equals(this.description, consultingTypeDTO.description) &&
        Objects.equals(this.groups, consultingTypeDTO.groups) &&
        Objects.equals(this.slug, consultingTypeDTO.slug) &&
        Objects.equals(this.excludeNonMainConsultantsFromTeamSessions, consultingTypeDTO.excludeNonMainConsultantsFromTeamSessions) &&
        Objects.equals(this.isSubsequentRegistrationAllowed, consultingTypeDTO.isSubsequentRegistrationAllowed) &&
        Objects.equals(this.isAnonymousConversationAllowed, consultingTypeDTO.isAnonymousConversationAllowed) &&
        Objects.equals(this.showAskerProfile, consultingTypeDTO.showAskerProfile) &&
        Objects.equals(this.isVideoCallAllowed, consultingTypeDTO.isVideoCallAllowed) &&
        Objects.equals(this.languageFormal, consultingTypeDTO.languageFormal) &&
        Objects.equals(this.consultantBoundedToConsultingType, consultingTypeDTO.consultantBoundedToConsultingType) &&
        Objects.equals(this.sendFurtherStepsMessage, consultingTypeDTO.sendFurtherStepsMessage) &&
        Objects.equals(this.furtherInformation, consultingTypeDTO.furtherInformation) &&
        Objects.equals(this.urls, consultingTypeDTO.urls) &&
        Objects.equals(this.registration, consultingTypeDTO.registration) &&
        Objects.equals(this.groupChat, consultingTypeDTO.groupChat) &&
        Objects.equals(this.whiteSpot, consultingTypeDTO.whiteSpot) &&
        Objects.equals(this.welcomeMessage, consultingTypeDTO.welcomeMessage) &&
        Objects.equals(this.sessionDataInitializing, consultingTypeDTO.sessionDataInitializing) &&
        Objects.equals(this.roles, consultingTypeDTO.roles) &&
        Objects.equals(this.notifications, consultingTypeDTO.notifications) &&
        Objects.equals(this.requiredComponents, consultingTypeDTO.requiredComponents) &&
        Objects.equals(this.welcomeScreen, consultingTypeDTO.welcomeScreen);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tenantId, description, groups, slug, excludeNonMainConsultantsFromTeamSessions, isSubsequentRegistrationAllowed, isAnonymousConversationAllowed, showAskerProfile, isVideoCallAllowed, languageFormal, consultantBoundedToConsultingType, sendFurtherStepsMessage, furtherInformation, urls, registration, groupChat, whiteSpot, welcomeMessage, sessionDataInitializing, roles, notifications, requiredComponents, welcomeScreen);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConsultingTypeDTO {\n");
    sb.append("    tenantId: ").append(toIndentedString(tenantId)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    groups: ").append(toIndentedString(groups)).append("\n");
    sb.append("    slug: ").append(toIndentedString(slug)).append("\n");
    sb.append("    excludeNonMainConsultantsFromTeamSessions: ").append(toIndentedString(excludeNonMainConsultantsFromTeamSessions)).append("\n");
    sb.append("    isSubsequentRegistrationAllowed: ").append(toIndentedString(isSubsequentRegistrationAllowed)).append("\n");
    sb.append("    isAnonymousConversationAllowed: ").append(toIndentedString(isAnonymousConversationAllowed)).append("\n");
    sb.append("    showAskerProfile: ").append(toIndentedString(showAskerProfile)).append("\n");
    sb.append("    isVideoCallAllowed: ").append(toIndentedString(isVideoCallAllowed)).append("\n");
    sb.append("    languageFormal: ").append(toIndentedString(languageFormal)).append("\n");
    sb.append("    consultantBoundedToConsultingType: ").append(toIndentedString(consultantBoundedToConsultingType)).append("\n");
    sb.append("    sendFurtherStepsMessage: ").append(toIndentedString(sendFurtherStepsMessage)).append("\n");
    sb.append("    furtherInformation: ").append(toIndentedString(furtherInformation)).append("\n");
    sb.append("    urls: ").append(toIndentedString(urls)).append("\n");
    sb.append("    registration: ").append(toIndentedString(registration)).append("\n");
    sb.append("    groupChat: ").append(toIndentedString(groupChat)).append("\n");
    sb.append("    whiteSpot: ").append(toIndentedString(whiteSpot)).append("\n");
    sb.append("    welcomeMessage: ").append(toIndentedString(welcomeMessage)).append("\n");
    sb.append("    sessionDataInitializing: ").append(toIndentedString(sessionDataInitializing)).append("\n");
    sb.append("    roles: ").append(toIndentedString(roles)).append("\n");
    sb.append("    notifications: ").append(toIndentedString(notifications)).append("\n");
    sb.append("    requiredComponents: ").append(toIndentedString(requiredComponents)).append("\n");
    sb.append("    welcomeScreen: ").append(toIndentedString(welcomeScreen)).append("\n");
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

