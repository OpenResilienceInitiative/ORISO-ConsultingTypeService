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
 * ApplicationSettingsPatchDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:20.216537826Z[Etc/UTC]")
public class ApplicationSettingsPatchDTO {

  @JsonProperty("legalContentChangesBySingleTenantAdminsAllowed")
  private Boolean legalContentChangesBySingleTenantAdminsAllowed;

  @JsonProperty("mainTenantSubdomainForSingleDomainMultitenancy")
  private String mainTenantSubdomainForSingleDomainMultitenancy;

  public ApplicationSettingsPatchDTO legalContentChangesBySingleTenantAdminsAllowed(Boolean legalContentChangesBySingleTenantAdminsAllowed) {
    this.legalContentChangesBySingleTenantAdminsAllowed = legalContentChangesBySingleTenantAdminsAllowed;
    return this;
  }

  /**
   * Get legalContentChangesBySingleTenantAdminsAllowed
   * @return legalContentChangesBySingleTenantAdminsAllowed
  */
  
  @Schema(name = "legalContentChangesBySingleTenantAdminsAllowed", required = false)
  public Boolean getLegalContentChangesBySingleTenantAdminsAllowed() {
    return legalContentChangesBySingleTenantAdminsAllowed;
  }

  public void setLegalContentChangesBySingleTenantAdminsAllowed(Boolean legalContentChangesBySingleTenantAdminsAllowed) {
    this.legalContentChangesBySingleTenantAdminsAllowed = legalContentChangesBySingleTenantAdminsAllowed;
  }

  public ApplicationSettingsPatchDTO mainTenantSubdomainForSingleDomainMultitenancy(String mainTenantSubdomainForSingleDomainMultitenancy) {
    this.mainTenantSubdomainForSingleDomainMultitenancy = mainTenantSubdomainForSingleDomainMultitenancy;
    return this;
  }

  /**
   * Get mainTenantSubdomainForSingleDomainMultitenancy
   * @return mainTenantSubdomainForSingleDomainMultitenancy
  */
  
  @Schema(name = "mainTenantSubdomainForSingleDomainMultitenancy", required = false)
  public String getMainTenantSubdomainForSingleDomainMultitenancy() {
    return mainTenantSubdomainForSingleDomainMultitenancy;
  }

  public void setMainTenantSubdomainForSingleDomainMultitenancy(String mainTenantSubdomainForSingleDomainMultitenancy) {
    this.mainTenantSubdomainForSingleDomainMultitenancy = mainTenantSubdomainForSingleDomainMultitenancy;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ApplicationSettingsPatchDTO applicationSettingsPatchDTO = (ApplicationSettingsPatchDTO) o;
    return Objects.equals(this.legalContentChangesBySingleTenantAdminsAllowed, applicationSettingsPatchDTO.legalContentChangesBySingleTenantAdminsAllowed) &&
        Objects.equals(this.mainTenantSubdomainForSingleDomainMultitenancy, applicationSettingsPatchDTO.mainTenantSubdomainForSingleDomainMultitenancy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(legalContentChangesBySingleTenantAdminsAllowed, mainTenantSubdomainForSingleDomainMultitenancy);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ApplicationSettingsPatchDTO {\n");
    sb.append("    legalContentChangesBySingleTenantAdminsAllowed: ").append(toIndentedString(legalContentChangesBySingleTenantAdminsAllowed)).append("\n");
    sb.append("    mainTenantSubdomainForSingleDomainMultitenancy: ").append(toIndentedString(mainTenantSubdomainForSingleDomainMultitenancy)).append("\n");
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

