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
 * RegistrationMandatoryFieldsDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class RegistrationMandatoryFieldsDTO {

  @JsonProperty("age")
  private Boolean age;

  @JsonProperty("state")
  private Boolean state;

  public RegistrationMandatoryFieldsDTO age(Boolean age) {
    this.age = age;
    return this;
  }

  /**
   * Get age
   * @return age
  */
  
  @Schema(name = "age", example = "true", required = false)
  public Boolean getAge() {
    return age;
  }

  public void setAge(Boolean age) {
    this.age = age;
  }

  public RegistrationMandatoryFieldsDTO state(Boolean state) {
    this.state = state;
    return this;
  }

  /**
   * Get state
   * @return state
  */
  
  @Schema(name = "state", example = "false", required = false)
  public Boolean getState() {
    return state;
  }

  public void setState(Boolean state) {
    this.state = state;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RegistrationMandatoryFieldsDTO registrationMandatoryFieldsDTO = (RegistrationMandatoryFieldsDTO) o;
    return Objects.equals(this.age, registrationMandatoryFieldsDTO.age) &&
        Objects.equals(this.state, registrationMandatoryFieldsDTO.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(age, state);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RegistrationMandatoryFieldsDTO {\n");
    sb.append("    age: ").append(toIndentedString(age)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
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

