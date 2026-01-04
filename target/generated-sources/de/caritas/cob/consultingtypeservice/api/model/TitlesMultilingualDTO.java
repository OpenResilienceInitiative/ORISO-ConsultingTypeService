package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.HashMap;
import java.util.Map;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * TitlesMultilingualDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:20.114194580Z[Etc/UTC]")
public class TitlesMultilingualDTO {

  @JsonProperty("short")
  @Valid
  private Map<String, String> _short = null;

  @JsonProperty("long")
  @Valid
  private Map<String, String> _long = null;

  @JsonProperty("welcome")
  private String welcome;

  @JsonProperty("registrationDropdown")
  private String registrationDropdown;

  public TitlesMultilingualDTO _short(Map<String, String> _short) {
    this._short = _short;
    return this;
  }

  public TitlesMultilingualDTO putShortItem(String key, String _shortItem) {
    if (this._short == null) {
      this._short = new HashMap<>();
    }
    this._short.put(key, _shortItem);
    return this;
  }

  /**
   * Get _short
   * @return _short
  */
  
  @Schema(name = "short", required = false)
  public Map<String, String> getShort() {
    return _short;
  }

  public void setShort(Map<String, String> _short) {
    this._short = _short;
  }

  public TitlesMultilingualDTO _long(Map<String, String> _long) {
    this._long = _long;
    return this;
  }

  public TitlesMultilingualDTO putLongItem(String key, String _longItem) {
    if (this._long == null) {
      this._long = new HashMap<>();
    }
    this._long.put(key, _longItem);
    return this;
  }

  /**
   * Get _long
   * @return _long
  */
  
  @Schema(name = "long", required = false)
  public Map<String, String> getLong() {
    return _long;
  }

  public void setLong(Map<String, String> _long) {
    this._long = _long;
  }

  public TitlesMultilingualDTO welcome(String welcome) {
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

  public TitlesMultilingualDTO registrationDropdown(String registrationDropdown) {
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
    TitlesMultilingualDTO titlesMultilingualDTO = (TitlesMultilingualDTO) o;
    return Objects.equals(this._short, titlesMultilingualDTO._short) &&
        Objects.equals(this._long, titlesMultilingualDTO._long) &&
        Objects.equals(this.welcome, titlesMultilingualDTO.welcome) &&
        Objects.equals(this.registrationDropdown, titlesMultilingualDTO.registrationDropdown);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_short, _long, welcome, registrationDropdown);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TitlesMultilingualDTO {\n");
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

