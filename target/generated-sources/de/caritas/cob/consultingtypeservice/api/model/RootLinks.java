package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.caritas.cob.consultingtypeservice.api.model.HalLink;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RootLinks
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class RootLinks {

  @JsonProperty("self")
  private HalLink self;

  @JsonProperty("consultingtypes")
  private HalLink consultingtypes;

  public RootLinks self(HalLink self) {
    this.self = self;
    return this;
  }

  /**
   * Get self
   * @return self
  */
  @NotNull @Valid 
  @Schema(name = "self", required = true)
  public HalLink getSelf() {
    return self;
  }

  public void setSelf(HalLink self) {
    this.self = self;
  }

  public RootLinks consultingtypes(HalLink consultingtypes) {
    this.consultingtypes = consultingtypes;
    return this;
  }

  /**
   * Get consultingtypes
   * @return consultingtypes
  */
  @Valid 
  @Schema(name = "consultingtypes", required = false)
  public HalLink getConsultingtypes() {
    return consultingtypes;
  }

  public void setConsultingtypes(HalLink consultingtypes) {
    this.consultingtypes = consultingtypes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RootLinks rootLinks = (RootLinks) o;
    return Objects.equals(this.self, rootLinks.self) &&
        Objects.equals(this.consultingtypes, rootLinks.consultingtypes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(self, consultingtypes);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RootLinks {\n");
    sb.append("    self: ").append(toIndentedString(self)).append("\n");
    sb.append("    consultingtypes: ").append(toIndentedString(consultingtypes)).append("\n");
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

