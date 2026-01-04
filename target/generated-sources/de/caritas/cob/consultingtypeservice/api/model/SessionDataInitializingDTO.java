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
 * SessionDataInitializingDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class SessionDataInitializingDTO {

  @JsonProperty("addictiveDrugs")
  private Boolean addictiveDrugs;

  @JsonProperty("age")
  private Boolean age;

  @JsonProperty("gender")
  private Boolean gender;

  @JsonProperty("relation")
  private Boolean relation;

  @JsonProperty("state")
  private Boolean state;

  public SessionDataInitializingDTO addictiveDrugs(Boolean addictiveDrugs) {
    this.addictiveDrugs = addictiveDrugs;
    return this;
  }

  /**
   * Get addictiveDrugs
   * @return addictiveDrugs
  */
  
  @Schema(name = "addictiveDrugs", example = "true", required = false)
  public Boolean getAddictiveDrugs() {
    return addictiveDrugs;
  }

  public void setAddictiveDrugs(Boolean addictiveDrugs) {
    this.addictiveDrugs = addictiveDrugs;
  }

  public SessionDataInitializingDTO age(Boolean age) {
    this.age = age;
    return this;
  }

  /**
   * Get age
   * @return age
  */
  
  @Schema(name = "age", example = "false", required = false)
  public Boolean getAge() {
    return age;
  }

  public void setAge(Boolean age) {
    this.age = age;
  }

  public SessionDataInitializingDTO gender(Boolean gender) {
    this.gender = gender;
    return this;
  }

  /**
   * Get gender
   * @return gender
  */
  
  @Schema(name = "gender", example = "true", required = false)
  public Boolean getGender() {
    return gender;
  }

  public void setGender(Boolean gender) {
    this.gender = gender;
  }

  public SessionDataInitializingDTO relation(Boolean relation) {
    this.relation = relation;
    return this;
  }

  /**
   * Get relation
   * @return relation
  */
  
  @Schema(name = "relation", example = "false", required = false)
  public Boolean getRelation() {
    return relation;
  }

  public void setRelation(Boolean relation) {
    this.relation = relation;
  }

  public SessionDataInitializingDTO state(Boolean state) {
    this.state = state;
    return this;
  }

  /**
   * Get state
   * @return state
  */
  
  @Schema(name = "state", example = "true", required = false)
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
    SessionDataInitializingDTO sessionDataInitializingDTO = (SessionDataInitializingDTO) o;
    return Objects.equals(this.addictiveDrugs, sessionDataInitializingDTO.addictiveDrugs) &&
        Objects.equals(this.age, sessionDataInitializingDTO.age) &&
        Objects.equals(this.gender, sessionDataInitializingDTO.gender) &&
        Objects.equals(this.relation, sessionDataInitializingDTO.relation) &&
        Objects.equals(this.state, sessionDataInitializingDTO.state);
  }

  @Override
  public int hashCode() {
    return Objects.hash(addictiveDrugs, age, gender, relation, state);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SessionDataInitializingDTO {\n");
    sb.append("    addictiveDrugs: ").append(toIndentedString(addictiveDrugs)).append("\n");
    sb.append("    age: ").append(toIndentedString(age)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    relation: ").append(toIndentedString(relation)).append("\n");
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

