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
 * TitlesDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:20.114194580Z[Etc/UTC]")
public class TitlesDTO {

  @JsonProperty("short")
  private String _short;

  @JsonProperty("long")
  private String _long;

  @JsonProperty("welcome")
  private String welcome;

  @JsonProperty("registrationDropdown")
  private String registrationDropdown;

  public TitlesDTO _short(String _short) {
    this._short = _short;
    return this;
  }

  /**
   * Get _short
   * @return _short
  */
  
  @Schema(name = "short", example = "xyz", required = false)
  public String getShort() {
    return _short;
  }

  public void setShort(String _short) {
    this._short = _short;
  }

  public TitlesDTO _long(String _long) {
    this._long = _long;
    return this;
  }

  /**
   * Get _long
   * @return _long
  */
  
  @Schema(name = "long", example = "Beratung xyz in Ort", required = false)
  public String getLong() {
    return _long;
  }

  public void setLong(String _long) {
    this._long = _long;
  }

  public TitlesDTO welcome(String welcome) {
    this.welcome = welcome;
    return this;
  }

  /**
   * Get welcome
   * @return welcome
  */
  
  @Schema(name = "welcome", example = "Herzlich Willkommen zu Beratung xyz", required = false)
  public String getWelcome() {
    return welcome;
  }

  public void setWelcome(String welcome) {
    this.welcome = welcome;
  }

  public TitlesDTO registrationDropdown(String registrationDropdown) {
    this.registrationDropdown = registrationDropdown;
    return this;
  }

  /**
   * Get registrationDropdown
   * @return registrationDropdown
  */
  
  @Schema(name = "registrationDropdown", example = "Beratung xyz", required = false)
  public String getRegistrationDropdown() {
    return registrationDropdown;
  }

  public void setRegistrationDropdown(String registrationDropdown) {
    this.registrationDropdown = registrationDropdown;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TitlesDTO titlesDTO = (TitlesDTO) o;
    return Objects.equals(this._short, titlesDTO._short) &&
        Objects.equals(this._long, titlesDTO._long) &&
        Objects.equals(this.welcome, titlesDTO.welcome) &&
        Objects.equals(this.registrationDropdown, titlesDTO.registrationDropdown);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_short, _long, welcome, registrationDropdown);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TitlesDTO {\n");
    sb.append("    _short: ").append(toIndentedString(_short)).append("\n");
    sb.append("    _long: ").append(toIndentedString(_long)).append("\n");
    sb.append("    welcome: ").append(toIndentedString(welcome)).append("\n");
    sb.append("    registrationDropdown: ").append(toIndentedString(registrationDropdown)).append("\n");
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

