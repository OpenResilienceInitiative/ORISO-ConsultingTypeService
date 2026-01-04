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
 * ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled
 */

@JsonTypeName("ApplicationSettingsDTO_multitenancyWithSingleDomainEnabled")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:20.216537826Z[Etc/UTC]")
public class ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled {

  @JsonProperty("value")
  private Boolean value;

  @JsonProperty("readOnly")
  private Boolean readOnly;

  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled value(Boolean value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
  */
  @NotNull 
  @Schema(name = "value", example = "true", required = true)
  public Boolean getValue() {
    return value;
  }

  public void setValue(Boolean value) {
    this.value = value;
  }

  public ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled readOnly(Boolean readOnly) {
    this.readOnly = readOnly;
    return this;
  }

  /**
   * Get readOnly
   * @return readOnly
  */
  @NotNull 
  @Schema(name = "readOnly", example = "false", required = true)
  public Boolean getReadOnly() {
    return readOnly;
  }

  public void setReadOnly(Boolean readOnly) {
    this.readOnly = readOnly;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled applicationSettingsDTOMultitenancyWithSingleDomainEnabled = (ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled) o;
    return Objects.equals(this.value, applicationSettingsDTOMultitenancyWithSingleDomainEnabled.value) &&
        Objects.equals(this.readOnly, applicationSettingsDTOMultitenancyWithSingleDomainEnabled.readOnly);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, readOnly);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApplicationSettingsDTOMultitenancyWithSingleDomainEnabled {\n");
    sb.append("    value: ").append(toIndentedString(value)).append("\n");
    sb.append("    readOnly: ").append(toIndentedString(readOnly)).append("\n");
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

