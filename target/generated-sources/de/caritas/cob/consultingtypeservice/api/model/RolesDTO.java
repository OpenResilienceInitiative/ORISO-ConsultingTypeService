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
 * RolesDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class RolesDTO {

  @JsonProperty("consultant")
  private de.caritas.cob.consultingtypeservice.api.consultingtypes.roles.Consultant consultant;

  public RolesDTO consultant(de.caritas.cob.consultingtypeservice.api.consultingtypes.roles.Consultant consultant) {
    this.consultant = consultant;
    return this;
  }

  /**
   * Get consultant
   * @return consultant
  */
  @Valid 
  @Schema(name = "consultant", required = false)
  public de.caritas.cob.consultingtypeservice.api.consultingtypes.roles.Consultant getConsultant() {
    return consultant;
  }

  public void setConsultant(de.caritas.cob.consultingtypeservice.api.consultingtypes.roles.Consultant consultant) {
    this.consultant = consultant;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RolesDTO rolesDTO = (RolesDTO) o;
    return Objects.equals(this.consultant, rolesDTO.consultant);
  }

  @Override
  public int hashCode() {
    return Objects.hash(consultant);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RolesDTO {\n");
    sb.append("    consultant: ").append(toIndentedString(consultant)).append("\n");
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

