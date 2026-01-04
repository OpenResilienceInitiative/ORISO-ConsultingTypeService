package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.caritas.cob.consultingtypeservice.api.model.BasicConsultingTypeResponseDTOFurtherInformation;
import de.caritas.cob.consultingtypeservice.api.model.BasicConsultingTypeResponseDTOGroupChat;
import de.caritas.cob.consultingtypeservice.api.model.BasicConsultingTypeResponseDTORegistration;
import de.caritas.cob.consultingtypeservice.api.model.BasicConsultingTypeResponseDTOUrls;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BasicConsultingTypeResponseDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class BasicConsultingTypeResponseDTO {

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("tenantId")
  private Integer tenantId;

  @JsonProperty("description")
  private String description;

  @JsonProperty("furtherInformation")
  private BasicConsultingTypeResponseDTOFurtherInformation furtherInformation;

  @JsonProperty("urls")
  private BasicConsultingTypeResponseDTOUrls urls;

  @JsonProperty("registration")
  private BasicConsultingTypeResponseDTORegistration registration;

  @JsonProperty("groupChat")
  private BasicConsultingTypeResponseDTOGroupChat groupChat;

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

  public BasicConsultingTypeResponseDTO id(Integer id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @Schema(name = "id", example = "100", required = false)
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public BasicConsultingTypeResponseDTO tenantId(Integer tenantId) {
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

  public BasicConsultingTypeResponseDTO description(String description) {
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

  public BasicConsultingTypeResponseDTO furtherInformation(BasicConsultingTypeResponseDTOFurtherInformation furtherInformation) {
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

  public BasicConsultingTypeResponseDTO urls(BasicConsultingTypeResponseDTOUrls urls) {
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

  public BasicConsultingTypeResponseDTO registration(BasicConsultingTypeResponseDTORegistration registration) {
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

  public BasicConsultingTypeResponseDTO groupChat(BasicConsultingTypeResponseDTOGroupChat groupChat) {
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

  public BasicConsultingTypeResponseDTO isSubsequentRegistrationAllowed(Boolean isSubsequentRegistrationAllowed) {
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

  public BasicConsultingTypeResponseDTO isAnonymousConversationAllowed(Boolean isAnonymousConversationAllowed) {
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

  public BasicConsultingTypeResponseDTO showAskerProfile(Boolean showAskerProfile) {
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

  public BasicConsultingTypeResponseDTO isVideoCallAllowed(Boolean isVideoCallAllowed) {
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

  public BasicConsultingTypeResponseDTO languageFormal(Boolean languageFormal) {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasicConsultingTypeResponseDTO basicConsultingTypeResponseDTO = (BasicConsultingTypeResponseDTO) o;
    return Objects.equals(this.id, basicConsultingTypeResponseDTO.id) &&
        Objects.equals(this.tenantId, basicConsultingTypeResponseDTO.tenantId) &&
        Objects.equals(this.description, basicConsultingTypeResponseDTO.description) &&
        Objects.equals(this.furtherInformation, basicConsultingTypeResponseDTO.furtherInformation) &&
        Objects.equals(this.urls, basicConsultingTypeResponseDTO.urls) &&
        Objects.equals(this.registration, basicConsultingTypeResponseDTO.registration) &&
        Objects.equals(this.groupChat, basicConsultingTypeResponseDTO.groupChat) &&
        Objects.equals(this.isSubsequentRegistrationAllowed, basicConsultingTypeResponseDTO.isSubsequentRegistrationAllowed) &&
        Objects.equals(this.isAnonymousConversationAllowed, basicConsultingTypeResponseDTO.isAnonymousConversationAllowed) &&
        Objects.equals(this.showAskerProfile, basicConsultingTypeResponseDTO.showAskerProfile) &&
        Objects.equals(this.isVideoCallAllowed, basicConsultingTypeResponseDTO.isVideoCallAllowed) &&
        Objects.equals(this.languageFormal, basicConsultingTypeResponseDTO.languageFormal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, tenantId, description, furtherInformation, urls, registration, groupChat, isSubsequentRegistrationAllowed, isAnonymousConversationAllowed, showAskerProfile, isVideoCallAllowed, languageFormal);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasicConsultingTypeResponseDTO {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    tenantId: ").append(toIndentedString(tenantId)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    furtherInformation: ").append(toIndentedString(furtherInformation)).append("\n");
    sb.append("    urls: ").append(toIndentedString(urls)).append("\n");
    sb.append("    registration: ").append(toIndentedString(registration)).append("\n");
    sb.append("    groupChat: ").append(toIndentedString(groupChat)).append("\n");
    sb.append("    isSubsequentRegistrationAllowed: ").append(toIndentedString(isSubsequentRegistrationAllowed)).append("\n");
    sb.append("    isAnonymousConversationAllowed: ").append(toIndentedString(isAnonymousConversationAllowed)).append("\n");
    sb.append("    showAskerProfile: ").append(toIndentedString(showAskerProfile)).append("\n");
    sb.append("    isVideoCallAllowed: ").append(toIndentedString(isVideoCallAllowed)).append("\n");
    sb.append("    languageFormal: ").append(toIndentedString(languageFormal)).append("\n");
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

