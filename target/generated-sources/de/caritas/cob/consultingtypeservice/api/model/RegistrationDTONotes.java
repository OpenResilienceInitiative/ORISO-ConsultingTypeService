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
 * RegistrationDTONotes
 */

@JsonTypeName("RegistrationDTO_notes")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class RegistrationDTONotes {

  @JsonProperty("agencySelection")
  private String agencySelection;

  @JsonProperty("password")
  private String password;

  public RegistrationDTONotes agencySelection(String agencySelection) {
    this.agencySelection = agencySelection;
    return this;
  }

  /**
   * Get agencySelection
   * @return agencySelection
  */
  
  @Schema(name = "agencySelection", example = "Lorem ipsum", required = false)
  public String getAgencySelection() {
    return agencySelection;
  }

  public void setAgencySelection(String agencySelection) {
    this.agencySelection = agencySelection;
  }

  public RegistrationDTONotes password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Get password
   * @return password
  */
  
  @Schema(name = "password", example = "Lorem impsum", required = false)
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegistrationDTONotes registrationDTONotes = (RegistrationDTONotes) o;
    return Objects.equals(this.agencySelection, registrationDTONotes.agencySelection) &&
        Objects.equals(this.password, registrationDTONotes.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(agencySelection, password);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistrationDTONotes {\n");
    sb.append("    agencySelection: ").append(toIndentedString(agencySelection)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
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

