package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * TeamSessionsDTONewMessage
 */

@JsonTypeName("TeamSessionsDTO_newMessage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class TeamSessionsDTONewMessage {

  @JsonProperty("allTeamConsultants")
  private Boolean allTeamConsultants;

  public TeamSessionsDTONewMessage allTeamConsultants(Boolean allTeamConsultants) {
    this.allTeamConsultants = allTeamConsultants;
    return this;
  }

  /**
   * Get allTeamConsultants
   * @return allTeamConsultants
  */
  
  @Schema(name = "allTeamConsultants", example = "true", required = false)
  public Boolean getAllTeamConsultants() {
    return allTeamConsultants;
  }

  public void setAllTeamConsultants(Boolean allTeamConsultants) {
    this.allTeamConsultants = allTeamConsultants;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TeamSessionsDTONewMessage teamSessionsDTONewMessage = (TeamSessionsDTONewMessage) o;
    return Objects.equals(this.allTeamConsultants, teamSessionsDTONewMessage.allTeamConsultants);
  }

  @Override
  public int hashCode() {
    return Objects.hash(allTeamConsultants);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TeamSessionsDTONewMessage {\n");
    sb.append("    allTeamConsultants: ").append(toIndentedString(allTeamConsultants)).append("\n");
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

