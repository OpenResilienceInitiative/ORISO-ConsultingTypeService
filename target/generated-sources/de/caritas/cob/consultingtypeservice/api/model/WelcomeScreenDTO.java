package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import de.caritas.cob.consultingtypeservice.api.model.AnonymousScreenDTO;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * WelcomeScreenDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.381612101Z[Etc/UTC]")
public class WelcomeScreenDTO {

  @JsonProperty("anonymous")
  private AnonymousScreenDTO anonymous;

  public WelcomeScreenDTO anonymous(AnonymousScreenDTO anonymous) {
    this.anonymous = anonymous;
    return this;
  }

  /**
   * Get anonymous
   * @return anonymous
  */
  @Valid 
  @Schema(name = "anonymous", required = false)
  public AnonymousScreenDTO getAnonymous() {
    return anonymous;
  }

  public void setAnonymous(AnonymousScreenDTO anonymous) {
    this.anonymous = anonymous;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WelcomeScreenDTO welcomeScreenDTO = (WelcomeScreenDTO) o;
    return Objects.equals(this.anonymous, welcomeScreenDTO.anonymous);
  }

  @Override
  public int hashCode() {
    return Objects.hash(anonymous);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WelcomeScreenDTO {\n");
    sb.append("    anonymous: ").append(toIndentedString(anonymous)).append("\n");
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

