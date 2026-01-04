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
 * WhiteSpotDTO
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-12-25T23:22:19.939702671Z[Etc/UTC]")
public class WhiteSpotDTO {

  @JsonProperty("whiteSpotAgencyAssigned")
  private Boolean whiteSpotAgencyAssigned;

  @JsonProperty("whiteSpotAgencyId")
  private Integer whiteSpotAgencyId;

  public WhiteSpotDTO whiteSpotAgencyAssigned(Boolean whiteSpotAgencyAssigned) {
    this.whiteSpotAgencyAssigned = whiteSpotAgencyAssigned;
    return this;
  }

  /**
   * Get whiteSpotAgencyAssigned
   * @return whiteSpotAgencyAssigned
  */
  
  @Schema(name = "whiteSpotAgencyAssigned", example = "true", required = false)
  public Boolean getWhiteSpotAgencyAssigned() {
    return whiteSpotAgencyAssigned;
  }

  public void setWhiteSpotAgencyAssigned(Boolean whiteSpotAgencyAssigned) {
    this.whiteSpotAgencyAssigned = whiteSpotAgencyAssigned;
  }

  public WhiteSpotDTO whiteSpotAgencyId(Integer whiteSpotAgencyId) {
    this.whiteSpotAgencyId = whiteSpotAgencyId;
    return this;
  }

  /**
   * Get whiteSpotAgencyId
   * @return whiteSpotAgencyId
  */
  
  @Schema(name = "whiteSpotAgencyId", example = "4567", required = false)
  public Integer getWhiteSpotAgencyId() {
    return whiteSpotAgencyId;
  }

  public void setWhiteSpotAgencyId(Integer whiteSpotAgencyId) {
    this.whiteSpotAgencyId = whiteSpotAgencyId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WhiteSpotDTO whiteSpotDTO = (WhiteSpotDTO) o;
    return Objects.equals(this.whiteSpotAgencyAssigned, whiteSpotDTO.whiteSpotAgencyAssigned) &&
        Objects.equals(this.whiteSpotAgencyId, whiteSpotDTO.whiteSpotAgencyId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(whiteSpotAgencyAssigned, whiteSpotAgencyId);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WhiteSpotDTO {\n");
    sb.append("    whiteSpotAgencyAssigned: ").append(toIndentedString(whiteSpotAgencyAssigned)).append("\n");
    sb.append("    whiteSpotAgencyId: ").append(toIndentedString(whiteSpotAgencyId)).append("\n");
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

