package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.caritas.cob.consultingtypeservice.api.model.TeamSessionsDTONewMessage;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * NotificationsDTOTeamSessions
 */

@JsonTypeName("NotificationsDTO_teamSessions")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class NotificationsDTOTeamSessions {

  @JsonProperty("newMessage")
  private TeamSessionsDTONewMessage newMessage;

  public NotificationsDTOTeamSessions newMessage(TeamSessionsDTONewMessage newMessage) {
    this.newMessage = newMessage;
    return this;
  }

  /**
   * Get newMessage
   * @return newMessage
  */
  @Valid 
  @Schema(name = "newMessage", required = false)
  public TeamSessionsDTONewMessage getNewMessage() {
    return newMessage;
  }

  public void setNewMessage(TeamSessionsDTONewMessage newMessage) {
    this.newMessage = newMessage;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationsDTOTeamSessions notificationsDTOTeamSessions = (NotificationsDTOTeamSessions) o;
    return Objects.equals(this.newMessage, notificationsDTOTeamSessions.newMessage);
  }

  @Override
  public int hashCode() {
    return Objects.hash(newMessage);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificationsDTOTeamSessions {\n");
    sb.append("    newMessage: ").append(toIndentedString(newMessage)).append("\n");
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

