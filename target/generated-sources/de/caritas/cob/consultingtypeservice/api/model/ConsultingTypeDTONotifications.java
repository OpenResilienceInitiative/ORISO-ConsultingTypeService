package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.caritas.cob.consultingtypeservice.api.model.NotificationsDTOTeamSessions;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ConsultingTypeDTONotifications
 */

@JsonTypeName("ConsultingTypeDTO_notifications")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.381612101Z[Etc/UTC]")
public class ConsultingTypeDTONotifications {

  @JsonProperty("teamSessions")
  private NotificationsDTOTeamSessions teamSessions;

  public ConsultingTypeDTONotifications teamSessions(NotificationsDTOTeamSessions teamSessions) {
    this.teamSessions = teamSessions;
    return this;
  }

  /**
   * Get teamSessions
   * @return teamSessions
  */
  @Valid 
  @Schema(name = "teamSessions", required = false)
  public NotificationsDTOTeamSessions getTeamSessions() {
    return teamSessions;
  }

  public void setTeamSessions(NotificationsDTOTeamSessions teamSessions) {
    this.teamSessions = teamSessions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConsultingTypeDTONotifications consultingTypeDTONotifications = (ConsultingTypeDTONotifications) o;
    return Objects.equals(this.teamSessions, consultingTypeDTONotifications.teamSessions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(teamSessions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConsultingTypeDTONotifications {\n");
    sb.append("    teamSessions: ").append(toIndentedString(teamSessions)).append("\n");
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

