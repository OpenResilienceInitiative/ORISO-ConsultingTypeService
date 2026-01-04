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
 * ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy
 */

@JsonTypeName("ApplicationSettingsDTO_mainTenantSubdomainForSingleDomainMultitenancy")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:20.216537826Z[Etc/UTC]")
public class ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy {

  @JsonProperty("value")
  private String value;

  @JsonProperty("readOnly")
  private Boolean readOnly;

  public ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy value(String value) {
    this.value = value;
    return this;
  }

  /**
   * Get value
   * @return value
  */
  @NotNull 
  @Schema(name = "value", example = "true", required = true)
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy readOnly(Boolean readOnly) {
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
    ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy applicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy = (ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy) o;
    return Objects.equals(this.value, applicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy.value) &&
        Objects.equals(this.readOnly, applicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy.readOnly);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, readOnly);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApplicationSettingsDTOMainTenantSubdomainForSingleDomainMultitenancy {\n");
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

