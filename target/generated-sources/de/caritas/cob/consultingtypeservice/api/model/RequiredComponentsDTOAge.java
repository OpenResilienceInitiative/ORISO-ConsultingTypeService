package de.caritas.cob.consultingtypeservice.api.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.caritas.cob.consultingtypeservice.api.model.OptionDTO;
import java.util.ArrayList;
import java.util.List;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * RequiredComponentsDTOAge
 */

@JsonTypeName("RequiredComponentsDTO_age")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.381612101Z[Etc/UTC]")
public class RequiredComponentsDTOAge {

  @JsonProperty("isEnabled")
  private Boolean isEnabled;

  @JsonProperty("options")
  @Valid
  private List<OptionDTO> options = null;

  public RequiredComponentsDTOAge isEnabled(Boolean isEnabled) {
    this.isEnabled = isEnabled;
    return this;
  }

  /**
   * Get isEnabled
   * @return isEnabled
  */
  
  @Schema(name = "isEnabled", required = false)
  public Boolean getIsEnabled() {
    return isEnabled;
  }

  public void setIsEnabled(Boolean isEnabled) {
    this.isEnabled = isEnabled;
  }

  public RequiredComponentsDTOAge options(List<OptionDTO> options) {
    this.options = options;
    return this;
  }

  public RequiredComponentsDTOAge addOptionsItem(OptionDTO optionsItem) {
    if (this.options == null) {
      this.options = new ArrayList<>();
    }
    this.options.add(optionsItem);
    return this;
  }

  /**
   * Get options
   * @return options
  */
  @Valid 
  @Schema(name = "options", required = false)
  public List<OptionDTO> getOptions() {
    return options;
  }

  public void setOptions(List<OptionDTO> options) {
    this.options = options;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RequiredComponentsDTOAge requiredComponentsDTOAge = (RequiredComponentsDTOAge) o;
    return Objects.equals(this.isEnabled, requiredComponentsDTOAge.isEnabled) &&
        Objects.equals(this.options, requiredComponentsDTOAge.options);
  }

  @Override
  public int hashCode() {
    return Objects.hash(isEnabled, options);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RequiredComponentsDTOAge {\n");
    sb.append("    isEnabled: ").append(toIndentedString(isEnabled)).append("\n");
    sb.append("    options: ").append(toIndentedString(options)).append("\n");
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

