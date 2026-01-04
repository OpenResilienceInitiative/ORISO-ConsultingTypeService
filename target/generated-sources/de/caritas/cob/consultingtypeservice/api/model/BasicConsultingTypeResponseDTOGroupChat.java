package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
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
 * BasicConsultingTypeResponseDTOGroupChat
 */

@JsonTypeName("BasicConsultingTypeResponseDTO_groupChat")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class BasicConsultingTypeResponseDTOGroupChat {

  @JsonProperty("isGroupChat")
  private Boolean isGroupChat;

  @JsonProperty("groupChatRules")
  @Valid
  private List<String> groupChatRules = null;

  public BasicConsultingTypeResponseDTOGroupChat isGroupChat(Boolean isGroupChat) {
    this.isGroupChat = isGroupChat;
    return this;
  }

  /**
   * Get isGroupChat
   * @return isGroupChat
  */
  
  @Schema(name = "isGroupChat", example = "true", required = false)
  public Boolean getIsGroupChat() {
    return isGroupChat;
  }

  public void setIsGroupChat(Boolean isGroupChat) {
    this.isGroupChat = isGroupChat;
  }

  public BasicConsultingTypeResponseDTOGroupChat groupChatRules(List<String> groupChatRules) {
    this.groupChatRules = groupChatRules;
    return this;
  }

  public BasicConsultingTypeResponseDTOGroupChat addGroupChatRulesItem(String groupChatRulesItem) {
    if (this.groupChatRules == null) {
      this.groupChatRules = new ArrayList<>();
    }
    this.groupChatRules.add(groupChatRulesItem);
    return this;
  }

  /**
   * Get groupChatRules
   * @return groupChatRules
  */
  
  @Schema(name = "groupChatRules", required = false)
  public List<String> getGroupChatRules() {
    return groupChatRules;
  }

  public void setGroupChatRules(List<String> groupChatRules) {
    this.groupChatRules = groupChatRules;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BasicConsultingTypeResponseDTOGroupChat basicConsultingTypeResponseDTOGroupChat = (BasicConsultingTypeResponseDTOGroupChat) o;
    return Objects.equals(this.isGroupChat, basicConsultingTypeResponseDTOGroupChat.isGroupChat) &&
        Objects.equals(this.groupChatRules, basicConsultingTypeResponseDTOGroupChat.groupChatRules);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isGroupChat, groupChatRules);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BasicConsultingTypeResponseDTOGroupChat {\n");
    sb.append("    isGroupChat: ").append(toIndentedString(isGroupChat)).append("\n");
    sb.append("    groupChatRules: ").append(toIndentedString(groupChatRules)).append("\n");
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

